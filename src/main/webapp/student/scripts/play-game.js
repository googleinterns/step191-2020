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
let gameInstanceId = null; 
const resultObject = document.getElementById("result");
let submited = false;
let isFinished = false;
let isCorrect = null;
let studentId = null;
let currentQuestionTitle = null;
let selectedAnswerTitle = null;

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
  
  gameInstanceId = getGameInstanceIdFromQueryParams();

  if (gameInstanceId == null) {
    gameInstanceId = await getActiveGameInstanceId(user);
  }

  // Register and get animal alias from Firestore, or if exists just retrieve animal alias
  registerStudentInGameInstance(gameInstanceId);

  // Start listening to the GameInstance
  initGameInstanceListener(gameInstanceId);
}

// Register and get animal alias from Firestore, or if exists just retrieve animal alias
function registerStudentInGameInstance(gameInstanceId) {
  firebase.auth().currentUser.getIdToken(/* forceRefresh */ true).then(function(idToken) {
    fetch('/joinGameInstance', {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({idToken: idToken, gameInstanceId: gameInstanceId})
    }).then((response) => {
      response.json().then(animal => {
        // Display the user's alias
        initStudentAlias(animal);
      });
    });
  }).catch(function(error) {
    // Handle error
    console.log("Please log in");
  });
}

// Display the user's alias in the UI
function initStudentAlias(animalAlias) {
  const userIdDivElement = document.getElementById('userId');
  userIdDivElement.innerText = `You are: ${animalAlias}`
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
  document.getElementById("gif").setAttribute('src','')
}

// Inits listener to User's points in Firestore DB
function updatePoints() {
    console.log("Updating points");
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

    if(gameInstanceUpdate.isFinished) {
        finishGame();
        updatePoints();
        displayResultsTable();
        return;
    }

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
          questionTitle: currentQuestionTitle,
          answerId: selectedAnswerId,
          answerTitle: selectedAnswerTitle
        })
      }).then(() => {
        submited = true;
        console.log("Question sent!")
        showAnswers();
        disableAnswers();
      });
  
    }).catch(function(error) {
      // Handle error
    });
  });
}

// Update the question that has changed
async function updateCurrentQuestion(gameId, questionId, isCurrentQuestionActive) {
  
  if(currentQuestionId != questionId) {
    resetDOM();
    currentQuestionId = questionId;
  }
  currentQuestionActive = isCurrentQuestionActive

  let currentQuestionDocRef = db.collection('games').doc(gameId).collection('questions').doc(questionId);
  const currentQuestion = await queryCurrentQuestion(currentQuestionDocRef);

  // Add the question title to the UI
  createQuestionObject(currentQuestion.title);

  if(!currentQuestionActive){
      if(!submited){
        document.getElementById('submitButton').disabled = false;
        document.getElementById("submitButton").click();
        document.getElementById('submitButton').disabled = true;
        document.getElementById("quiz").innerHTML='';
      } else {
        showAnswers();
        disableAnswers();
      }
      return;
  }

    document.getElementById('submitButton').disabled = false;
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
  currentQuestionTitle = title;
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

const handlers = [];
function createAnswer(quiz, multipleDiv, doc, i){
      const boxDiv = document.createElement("div");
      boxDiv.classList.add("demo-card-square");
      boxDiv.classList.add("mdl-card");
      boxDiv.classList.add("mdl-shadow--2dp");
      const titleDiv = document.createElement("div");
      titleDiv.setAttribute("id", doc.id);
      boxDiv.addEventListener('click', handlers[i-1] = () => {
          if(doc.id != selectedAnswerId && selectedAnswerId != "" && selectedAnswerId != null){
            document.getElementById(selectedAnswerId).classList.toggle("selected");
          }
          selectedAnswerId = doc.id;
          titleDiv.classList.toggle("selected");
          selectedAnswerTitle = doc.data().title;
          console.log(selectedAnswerTitle);
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

async function showAnswers(){
      if(currentQuestionActive){
        document.getElementById("result").innerText = 'Wait for question to end...';
      } else {
        var tag;
        firebase.auth().currentUser.getIdToken(/* forceRefresh */ true).then(async function(idToken) {
        const infoJson = await fetch('/answer?gameInstance='+gameInstanceId+'&student='+idToken);
        const info = await infoJson.json(); 
        isCorrect = info.correct;
        resultObject.classList.toggle(isCorrect.toString());
        if(isCorrect) {
            tag = "celebrate";
            resultObject.innerText = 'Correct!';
        } else {
            tag = "disappointment";
            resultObject.innerText = 'Incorrect :(';
        }
        getGif(tag);
        updatePoints();
        });
      }
}

async function getGif(tag){
    const data = await fetch('https://api.giphy.com/v1/gifs/random?api_key=rwk4YOsjvWroRr7p4QYFtjVwtSsMtwk4&tag='+tag+'&rating=g');
    console.log('https://api.giphy.com/v1/gifs/random?api_key=rwk4YOsjvWroRr7p4QYFtjVwtSsMtwk4&tag='+tag+'&rating=g');
    const json = await data.json();
    console.log(json);
    document.getElementById("gif").setAttribute('src', json.data.fixed_height_downsampled_url);
}

function disableAnswers(){
    document.getElementById('submitButton').disabled = true;
    let cards = document.querySelectorAll('.demo-card-square');
    var j = 0;
    cards.forEach((card) => {
        card.removeEventListener('click', handlers[j]);
        card.childNodes[0].classList.add("disabled");
        j++;
    });
}

function finishGame() {
    document.getElementById('results').innerHTML='';
    const questionElement = document.getElementById("question");
    const quizElement = document.getElementById("quiz");
    const readyHeadingElement = document.getElementById("getReady");
    const gameElement = document.getElementById("gameSection");
    if(gameElement.classList.length < 2) {
      gameElement.classList.toggle("active");
    }
    if(1 < readyHeadingElement.classList.length) {
    readyHeadingElement.classList.toggle("ready");
    }
    readyHeadingElement.innerText='Game Over';
    questionElement.innerHTML = '';
    quizElement.innerHTML = '';
    document.getElementById('submitButton').disabled = true;
}

function displayResultsTable() {
    const resultsTable = document.getElementById("resultsTable");
    const table = document.createElement('table');
    createTable(table);
    createHeaders(table);
    const tbody = document.createElement('tbody');
    db.collection("gameInstance").doc(gameInstanceId).collection("students").get().then(function(querySnapshot) {
        querySnapshot.forEach(function(doc) {
            createElementTable(tbody, doc.data().alias, doc.data().points);
            console.log(doc.id, " => ", doc.data().points);
        });
    });
    table.appendChild(tbody);
    resultsTable.appendChild(table);
}

function createTable(table) {
    table.classList.add("mdl-data-table")
    table.classList.add("mdl-js-data-table")
    table.classList.add("mdl-data-table--selectable")
    table.classList.add("mdl-shadow--2dp")
}

function createHeaders(table) {
    const thead = document.createElement('thead');
    const tr = document.createElement('tr');
    const th = document.createElement('th')
    th.classList.add('mdl-data-table__cell--non-numeric');
    th.innerText = "Student Id";
    tr.appendChild(th);
    const scoreth = document.createElement('th');
    scoreth.innerText='Score';
    tr.appendChild(scoreth);
    thead.appendChild(tr);
    table.appendChild(thead);
}

function createElementTable(tbody, id, score) {
    const tr = document.createElement('tr');
    const td = document.createElement('td');
    td.classList.add('mdl-data-table__cell--non-numeric');
    td.innerText = id;
    tr.appendChild(td);
    const scoretd = document.createElement('td');
    scoretd.innerText = score; 
    tr.appendChild(scoretd);
    tbody.append(tr);
}

initAuthStateObserver();
