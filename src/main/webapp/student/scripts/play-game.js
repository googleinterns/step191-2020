/**
 * Copyright 2018 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

const db = firebase.firestore();
let active = false;
let currentQuestionId = null;
let selectedAnswerId = null;

// Is triggered when the User logs in or logs out
function initAuthStateObserver() {
  firebase.auth().onAuthStateChanged(authStateObserver);
}

// Triggers when the auth state change for instance when the user signs-in or signs-out.
function authStateObserver(user) {
  if (user) { // User is signed in!
    // Everything starts working when the User logs in
    loadGamePanel(user)
  } else { // User is signed out!
    console.log("Not logged in");
  }
}

// Load all the information 
async function loadGamePanel(user) {
  // Get the Game Instance's ID in which the user is participating
  const gameInstanceId = await getActiveGameInstanceId(user);

  // Start listening to the GameInstance
  initGameInstanceListener(gameInstanceId);

  // Listen to points in GameInstance
  initPointsListener(gameInstanceId, user);
}

// Inits listener to User's points in Firestore DB
function initPointsListener(gameInstanceId, user) {
  db.collection("gameInstance").doc(gameInstanceId).collection("students").doc(user.uid)
  .onSnapshot(function(doc) {
    const studentInGameInstaneUpdate = doc.data();
    updatePointsInUI(studentInGameInstaneUpdate.points);
  });
}

// Updates the User's points in UI
function updatePointsInUI(points) {
  const pointsElement = document.getElementById('jsPoints');
  pointsElement.innerText = 'Your points: ' + points;
}

// Queries the "Users" collection of the DB to get the activeGameInstanceId the User is participating in
// Returns the gameInstanceId string
function getActiveGameInstanceId(user) {
  const uid = user.uid;

  // Query the User's document in "Users" collection
  return db.collection("users").doc(uid).get().then(function(doc) {
    if (doc.exists) {
      // Get the activeGameInstance's ID in which the User is participating
      return doc.data().activeGameInstanceId;
    } else {
        // doc.data() will be undefined in this case
      console.log("No such document!");
    }
  }).catch(function(error) {
      console.log("Error getting document:", error);
  });
}

// Listen to the GameInstance in Firestore DB and update when it changes
function initGameInstanceListener(gameInstanceId) {
  db.collection('gameInstance').doc(gameInstanceId).onSnapshot(function(doc) {
    const gameInstanceUpdate = doc.data();
    if (!active && gameInstanceUpdate.isActive) {
      const readyHeadingElement = document.getElementById("getReady");
      const gameElement = document.getElementById("gameSection");
      readyHeadingElement.classList.toggle("ready");
      gameElement.classList.toggle("active");
      active = true;
      
      // Init submit button
      initSubmitButton(gameInstanceId);
    } 
    if (gameInstanceUpdate.isActive && (gameInstanceUpdate.currentQuestion != currentQuestionId)) {
      // The question displayed must be changed
      updateCurrentQuestion(gameInstanceUpdate.gameId, gameInstanceUpdate.currentQuestion);
    }
    
  });
}

// Add event listener to the submit button
function initSubmitButton(gameInstanceId) {
  const submitButtonElement = document.getElementById('submitButton');
  submitButtonElement.addEventListener('click', () => {

    firebase.auth().currentUser.getIdToken(/* forceRefresh */ true).then(function(idToken) {
      // Send token to your backend via HTTPS
      // ...
  
      fetch('/answer', {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          idToken: idToken,
          gameInstnceId: gameInstanceId, 
          questionId: currentQuestionId,
          answerId: selectedAnswerId
        })
      }).then(() => {
        console.log("Question sent!")
      });
  
    }).catch(function(error) {
      // Handle error
    });

  });
}

// Update the question that has changed
async function updateCurrentQuestion(gameId, questionId) {
  currentQuestionId = questionId;

  let currentQuestionDocRef = db.collection('games').doc(gameId).collection('questions').doc(questionId);

  const currentQuestion = await queryCurrentQuestion(currentQuestionDocRef);

  // Add the question title to the UI
  createQuestionObject(currentQuestion.title);

  // Get answers to the question
  addAnswers(currentQuestionDocRef);
}

// Query and return the current question from the Game it belongs to
function queryCurrentQuestion(currentQuestionDocRef) {
  return currentQuestionDocRef.get().then(function(doc) {
    if (doc.exists) {
      return doc.data();
    }
  });
}

// Add the question title to the UI
function createQuestionObject(title) {
  document.getElementById("question").innerText = title;
}

// Get and add answers that correspond to the questionId
function addAnswers(currentQuestionDocRef) {

  document.getElementById("answerOptions").innerHTML = "";

  currentQuestionDocRef.collection('answers')
  .get()
  .then(function(querySnapshot) {
    querySnapshot.forEach(function(doc) {
      addAnswerToUI(doc);
    });
  })
  .catch(function(error) {
    console.log("Error retrieving answers");
  });
}

function addAnswerToUI(answerDoc) {
  const radioForm = document.getElementById("answerOptions");
  
  const inputElement = document.createElement("input");
  inputElement.setAttribute("type", "radio");
  inputElement.setAttribute("name", "answer");
  inputElement.setAttribute("id", answerDoc.id);
  inputElement.setAttribute("value", answerDoc.id);

  inputElement.addEventListener('click', () => {
    selectedAnswerId = answerDoc.id;
  });

  const labelElement = document.createElement("label");
  labelElement.setAttribute("for", answerDoc.id);
  labelElement.innerText = answerDoc.data().title;

  radioForm.appendChild(inputElement);
  radioForm.appendChild(labelElement);
  radioForm.appendChild(document.createElement("br"));
}

initAuthStateObserver();
