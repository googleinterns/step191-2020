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
let selectedAnswerId = "";
let currentQuestionActive = false;
let gameInstanceId = getGameInstanceIdFromQueryParams();
const resultObject = document.getElementById("result");
let submited = false;
let isCorrect = null;
let studentId = null;

// Is triggered when the User logs in or logs out
function initAuthStateObserver() {
  firebase.auth().onAuthStateChanged(authStateObserver);
}

// Triggers when the auth state change for instance when the user signs-in or signs-out.
function authStateObserver(user) {
  if (user) { // User is signed in!
    // Everything starts working when the User logs in
    studentId = user;
    loadGamePanel(user)
  } else { // User is signed out!
    console.log("Not logged in");
  }
}

// Load all the information 
async function loadGamePanel(user) {
  // Get the Game Instance's ID in which the user is participating
  
  if (gameInstanceId == null) {
    gameInstanceId = await getActiveGameInstanceId(user);
  }

  // Start listening to the GameInstance
  initGameInstanceListener(gameInstanceId);

}

// Gets the gameInstanceId from the query string if there is
// If not, it returns null
function getGameInstanceIdFromQueryParams() {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get('gameInstanceId');
}

function resetDOM() {
  selectedAnswerId = "";
  submited = false;
  document.getElementById("result").innerText = '';
  if(isCorrect != null) {
  resultObject.classList.toggle(isCorrect.toString());
  }
  isCorrect = null;
}

// Inits listener to User's points in Firestore DB
function updatePoints() {
  db.collection("gameInstance").doc(gameInstanceId).collection("students").doc(studentId.uid).get().then(function(doc) {
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
      initSubmitButton(gameInstanceId, gameInstanceUpdate.gameId);
    } 
    if (gameInstanceUpdate.isActive && (gameInstanceUpdate.currentQuestion != currentQuestionId || gameInstanceUpdate.currentQuestionActive != currentQuestionActive)) {
      // The question displayed must be changed
      updateCurrentQuestion(gameInstanceUpdate.gameId, gameInstanceUpdate.currentQuestion, gameInstanceUpdate.currentQuestionActive);
    }
    
  });
}

// Add event listener to the submit button
function initSubmitButton(gameInstanceId, gameId) {
  const submitButtonElement = document.getElementById('submitButton');
  submitButtonElement.addEventListener('click', () => {
    submited = true;
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
          gameInstanceId: gameInstanceId, 
          gameId: gameId,
          questionId: currentQuestionId,
          answerId: selectedAnswerId
        })
      }).then(() => {
        console.log("Question sent!")
        showAnswers();
      });
  
    }).catch(function(error) {
      // Handle error
    });
  });
}

// Update the question that has changed
async function updateCurrentQuestion(gameId, questionId, isCurrentQuestionActive) {
  currentQuestionId = questionId;
  currentQuestionActive = isCurrentQuestionActive

  let currentQuestionDocRef = db.collection('games').doc(gameId).collection('questions').doc(questionId);
  const currentQuestion = await queryCurrentQuestion(currentQuestionDocRef);

  // Add the question title to the UI
  createQuestionObject(currentQuestion.title);

  //Reset Selection
  resetDOM();

  if(!currentQuestionActive){
      document.getElementById("jsIsActive").innerText = "Isn't active";
      if(!submited){
        document.getElementById("submitButton").click();
      } else {
          showAnswers();
      }
      return;
  }

  document.getElementById("jsIsActive").innerText = 'Is active';



  // Get answers to the question
  createAnswersObject(currentQuestionDocRef);
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

function createAnswersObject(currentQuestionDocRef) {
    var quiz = document.getElementById("quiz");
    quiz.innerHTML = "";
    var multipleDiv = document.createElement("div");
    multipleDiv.classList.add("multiple-choice");
    var index = 1;

    currentQuestionDocRef.collection('answers').get()
    .then(function(querySnapshot) {
        querySnapshot.forEach(function(doc) {

        createAnswer(quiz, multipleDiv, doc, index);

        if(index%2 == 0) {
        quiz.appendChild(multipleDiv);  
        multipleDiv = document.createElement("div");
        multipleDiv.classList.add("multiple-choice");
        }
        
        index++;
        });
    })
    .catch(function(error) {
        console.log("Error retrieving answers");
    });
}

function createAnswer(quiz, multipleDiv, answerTitle, i){
      const boxDiv = document.createElement("div");
      boxDiv.classList.add("demo-card-square");
      boxDiv.classList.add("mdl-card");
      boxDiv.classList.add("mdl-shadow--2dp");
      const titleDiv = document.createElement("div");
      titleDiv.classList.add("mdl-card__title");
      titleDiv.classList.add("mdl-card--expand")
      titleDiv.setAttribute("id", "card-"+(i-1));
      const title = document.createElement("h2");
      title.classList.add("mdl-card__title-text");
      title.innerText = answerTitle;
      titleDiv.appendChild(title);
      boxDiv.appendChild(titleDiv);
      multipleDiv.appendChild(boxDiv);
}



function createAnswer(quiz, multipleDiv, doc, i){
      const boxDiv = document.createElement("div");
      boxDiv.classList.add("demo-card-square");
      boxDiv.classList.add("mdl-card");
      boxDiv.classList.add("mdl-shadow--2dp");
      const titleDiv = document.createElement("div");
      titleDiv.setAttribute("id", doc.id);
      boxDiv.addEventListener('click', ()=>{
          if(doc.id != selectedAnswerId && selectedAnswerId != "" && selectedAnswerId != null){
            document.getElementById(selectedAnswerId).classList.toggle("selected");
          }
          selectedAnswerId = doc.id;
          titleDiv.classList.toggle("selected");
      })
      titleDiv.classList.add("mdl-card__title");
      titleDiv.classList.add("mdl-card--expand");
      titleDiv.classList.add("card-"+(i-1));
      const title = document.createElement("h2");
      title.classList.add("mdl-card__title-text");
      title.innerText = doc.data().title;
      titleDiv.appendChild(title);
      boxDiv.appendChild(titleDiv);
      multipleDiv.appendChild(boxDiv);
}

function showAnswers(){
      document.getElementById("quiz").innerHTML = '';
      if(currentQuestionActive){
        document.getElementById("result").innerText = 'Wait for question to end...';
      } else {
        firebase.auth().currentUser.getIdToken(/* forceRefresh */ true).then(async function(idToken) {
        const infoJson = await fetch('/answer?gameInstance='+gameInstanceId+'&student='+idToken);
        const info = await infoJson.json(); 
        isCorrect = info.correct;
        resultObject.classList.toggle(isCorrect.toString());
        if(isCorrect) {
            resultObject.innerText = 'Correct!';
        } else {
            resultObject.innerText = 'Incorrect :(';
        }

        updatePoints();
        });
      }
}


initAuthStateObserver();
