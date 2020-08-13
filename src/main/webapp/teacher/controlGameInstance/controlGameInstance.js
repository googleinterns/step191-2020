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

let unsubscribeCurrentActiveQuestionInGameInstance = null;

// Is triggered when the User logs in or logs out
function initAuthStateObserver() {
  firebase.auth().onAuthStateChanged(authStateObserver);
}

// Triggers when the auth state change for instance when the user signs-in or signs-out.
function authStateObserver(user) {
  if (user) { // User is signed in!
    // Everything starts working when the User logs in
    loadControlPanel(user);
  } else { // User is signed out!
    console.log("Not logged in");
  }
}

async function loadControlPanel(user) {
  // Get the Game Instance's ID in which the user is participating

  let gameInstanceId = getGameInstanceIdFromQueryParams();
  
  if (gameInstanceId == null) {
    gameInstanceId = await getActiveGameInstanceId(user);
  }
  
  // Get the Active Game Instance Object from DB
  const gameInstance = await queryActiveGameInstance(gameInstanceId);

  // Get the Game the GameInstance is using from DB
  const game = await queryGameDetails(gameInstance.gameId);

  // Add all the GameInstance's Info to UI
  buildActiveGameInstanceUI({ gameInstanceId, gameInstance, game });

  // Add buttons to control the GameInstance state
  initUIControlButtons(gameInstanceId);

  buildQuestionHistory(gameInstanceId);
}

// Probably should do this following the Linked List
function buildQuestionHistory(gameInstanceId) {
  const questionInGameInstanceCollectionRef = db.collection('gameInstance').doc(gameInstanceId).collection('questions');
  questionInGameInstanceCollectionRef.get().then(function(querySnapshot) {
    querySnapshot.forEach(function(doc) {
      // doc.data() is never undefined for query doc snapshots
      const questionId = doc.id
      addQuestionToHistoryUI(doc.data(), questionId);
      console.log(doc.id, " => ", doc.data());

      buildQuestionAnswersHistory(questionId, questionInGameInstanceCollectionRef);
    });
  });
}

function addQuestionToHistoryUI(question, questionId) {
  const questionStatsDivElement = document.getElementById('jsQuestionStats');
  
  const singleQuestionStatDivElement = document.createElement('div');
  singleQuestionStatDivElement.id = 'stats-' + questionId

  const questionTitle = document.createElement('div');
  questionTitle.innerText = 'Question title: ' + question.title;
  singleQuestionStatDivElement.appendChild(questionTitle);

  const numberQuestionAnswersHistoryElement = document.createElement('div');
  numberQuestionAnswersHistoryElement.id = 'stats-' + questionId + '-numberQuestionAnswers';
  numberQuestionAnswersHistoryElement.innerText = 'Number of answers: ' + question.numberAnswered;
  singleQuestionStatDivElement.appendChild(numberQuestionAnswersHistoryElement);

  const numberCorrectQuestionAnswersHistoryElement = document.createElement('div');
  numberCorrectQuestionAnswersHistoryElement.id = 'stats-' + questionId + '-numberCorrectQuestionAnswers';
  numberCorrectQuestionAnswersHistoryElement.innerText = 'Number of correct anwers: ' + question.numberCorrect;
  singleQuestionStatDivElement.appendChild(numberCorrectQuestionAnswersHistoryElement);

  const numberWrongQuestionAnswersHistoryElement = document.createElement('div');
  numberWrongQuestionAnswersHistoryElement.id = 'stats-' + questionId + '-numberWrongQuestionAnswers';
  numberWrongQuestionAnswersHistoryElement.innerText = 'Number of wrong anwers: ' + question.numberWrong;
  singleQuestionStatDivElement.appendChild(numberWrongQuestionAnswersHistoryElement);

  questionStatsDivElement.appendChild(singleQuestionStatDivElement);
}

function buildQuestionAnswersHistory(questionId, questionsCollectionRef) {
  const answersCollectionRef = questionsCollectionRef.doc(questionId).collection('answers');
  answersCollectionRef.get().then(function(querySnapshot) {
    querySnapshot.forEach(function(doc) {
      addQuestionAnswerToHistoryUI(questionId, doc.data());
    });
  });
}

function addQuestionAnswerToHistoryUI(questionId, answer) {
  const questionStatsDivElement = document.getElementById('stats-' + questionId);
  
  const answerInQuestionStatsDivElement = document.createElement('div');
  answerInQuestionStatsDivElement.innerText = answer.title + ' with ' + answer.numberAnswers + ' answers.';

  if (answer.correct) {
    answerInQuestionStatsDivElement.innerText += ' (Correct answer)';
  }

  questionStatsDivElement.appendChild(answerInQuestionStatsDivElement);
}

// Gets the gameInstanceId from the query string if there is
// If not, it returns null
function getGameInstanceIdFromQueryParams() {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get('gameInstanceId');
}

// Queries the "Users" collection of the DB to get the activeGameInstanceId the User is participating in
// Returns the gameInstanceId string
function getActiveGameInstanceId(user) {
  console.log('Got gameInstanceId from Firestore');
  const uid = user.uid;

  // Query the User's document in "Users" collection
  return db.collection("users").doc(uid).get().then(function(doc) {
    if (doc.exists) {
      // Get the activeGameInstance's ID in which the User is participating
      return doc.data().activeGameInstanceId;
    } else {
      throw 'User not registered in a GameInstance'
        // doc.data() will be undefined in this case
      console.log("No such document!");
    }
  }).catch(function(error) {
      if (error == 'User not registered in a GameInstance') {
        // Do stuff because user is not registered
        console.log('Go register in a game first!');
      }
      console.log("Error getting document:", error);
  });
}

// Queries and returns the gameInstance object from the "GameInstance" collections of DB
function queryActiveGameInstance(gameInstanceId) {
  return db.collection("gameInstance").doc(gameInstanceId).get().then(function(doc) {
    if (doc.exists) {
      return doc.data();
    } else {
        // doc.data() will be undefined in this case
      console.log("No such document!");
    }
  }).catch(function(error) {
      console.log("Error getting document:", error);
  });
}

// Queries and returns the Game object from the "Games" collection of the DB
function queryGameDetails(gameId) {
  return db.collection("games").doc(gameId).get().then(function(doc) {
    if (doc.exists) {
      return doc.data();
    } else {
        // doc.data() will be undefined in this case
      console.log("No such document!");
    }
  }).catch(function(error) {
      console.log("Error getting document:", error);
  });
}

// Build the UI elements with all the info of the GameInstance
function buildActiveGameInstanceUI({ gameInstanceId, gameInstance, game} = {}) {
  // Add the GameInstance's ID to UI
  addGameInstanceIdToUI(gameInstanceId);

  // Add the Game details to UI
  addGameDetailsToUI(gameInstance.gameId, game);

  // This will listen to when anything in the GameInstance changes
  initGameInstanceListener(gameInstanceId);
}

// Adds the GameInstance's ID to the UI
function addGameInstanceIdToUI(gameInstanceId) {
  const gameInstanceIdElement = document.getElementById("jsGameInstanceId");
  gameInstanceIdElement.innerText = "This gameInstance's ID is: " + gameInstanceId;
}

// Adds the Game's details to the UI
function addGameDetailsToUI(gameId, game) {
  // Adds the Game's ID to the UI
  addGameIdToUI(gameId);

  // Add the Game Title
  const gameTitleElement = document.getElementById("jsGameTitle");
  gameTitleElement.innerText = "The game's title is: " + game.title;

  // Add the Number of Questions
  const numberOfQuestionsOfGameElement = document.getElementById('jsNumberOfQuestionsOfGame');
  numberOfQuestionsOfGameElement.innerText = 'This game has a total of ' + game.numberOfQuestions + ' questions.';
}

// Adds the Game's ID to the UI
function addGameIdToUI(gameId) {
  const gameIdElement = document.getElementById("jsGameId");
  gameIdElement.innerText = "The game's ID is: " + gameId;
}

// This listens to any change in the GameInstance Doc in Firestore DB
function initGameInstanceListener(gameInstanceId) {
  db.collection('gameInstance').doc(gameInstanceId).onSnapshot(function(doc) {
    // If this is triggered it's because the GameInstance's activeQuestion changed
    // OR someone joined
    const gameInstanceUpdate = doc.data();

    // TODO: this should be initiated once the game is started, not before...

    updateCurrentQuestion({ gameId: gameInstanceUpdate.gameId, currentQuestionId: gameInstanceUpdate.currentQuestion, isCurrentQuestionActive: gameInstanceUpdate.currentQuestionActive });

    // The listener to the question stats is initiated
    initQuestionStatsListener({ gameInstanceId, currentQuestionId: gameInstanceUpdate.currentQuestion });

    updateNumberOfMembersUI(gameInstanceUpdate.numberOfMembers);

  });
}

// Updates the panel showing which questions students are seeing

async function updateCurrentQuestion({ gameId, currentQuestionId, isCurrentQuestionActive } = {}) {
  const currentQuestion = await queryCurrentQuestion({ gameId, currentQuestionId });

  const activeQuestionTextElement = document.getElementById('jsActiveQuestionText');
  activeQuestionTextElement.innerText = 'The question is: \"' + currentQuestion.title + '\"';

  const canStudentsAnswerElement = document.getElementById("jsCanStudentsAnswer");
  console.log(isCurrentQuestionActive);
  if (isCurrentQuestionActive){
    canStudentsAnswerElement.innerText = "STUDENTS CAN ANSWER NOW";
  } else {
    canStudentsAnswerElement.innerText = "Students can't answer yet";      
  };

  const activeQuestionNumberElement = document.getElementById('jsActiveQuestionNumber');
  activeQuestionNumberElement.innerText = "Students are seeing question with ID: " + (currentQuestionId);
}


function updateNumberOfMembersUI(numberOfMembers) {
  const numberOfMembersElement = document.getElementById("jsNumberOfStudents");
  numberOfMembersElement.innerText = "There are " + numberOfMembers + " students registered in your room.";
}


// Queries and returns the currentQuestion object
function queryCurrentQuestion({ gameId, currentQuestionId }) {
  return db.collection('games').doc(gameId).collection('questions').doc(currentQuestionId).get().then(function(doc) {
    if (doc.exists) {
      return doc.data()
    }
  });
}

function initQuestionStatsListener({ gameInstanceId, currentQuestionId } = {}) {
  if (unsubscribeCurrentActiveQuestionInGameInstance != null) {
    // This is to stop listening to live changes to the previous question which is not active anymore
    unsubscribeCurrentActiveQuestionInGameInstance();
  }
  unsubscribeCurrentActiveQuestionInGameInstance = db.collection('gameInstance').doc(gameInstanceId).collection('questions').doc(currentQuestionId).onSnapshot(function (doc) {
    updateCurrentQuestionStats(doc.data());
    console.log(doc.data());
  });
}

function updateCurrentQuestionStats(updatedQuestionStats) {

  // Update the question in the active panel
  const numberCurrentQuestionAnswersElement = document.getElementById('jsNumberCurrentQuestionAnswers');
  numberCurrentQuestionAnswersElement.innerText = 'This question has been answered by ' + updatedQuestionStats.numberAnswered + ' students.';

  const numberCorrectCurrentQuestionAnswersElement = document.getElementById('jsNumberCorrectCurrentQuestionAnswers');
  numberCorrectCurrentQuestionAnswersElement.innerText = 'Number of answers correct: ' + updatedQuestionStats.numberCorrect

  const numberWrongCurrentQuestionAnswersElement = document.getElementById('jsNumberWrongCurrentQuestionAnswers');
  numberWrongCurrentQuestionAnswersElement.innerText = 'Number of answers wrong: ' + updatedQuestionStats.numberWrong

  // Update the question in the history panel

}

function updateNumberOfMembersUI(numberOfMembers) {
  const numberOfMembersElement = document.getElementById("jsNumberOfStudents");
  numberOfMembersElement.innerText = "There are " + numberOfMembers + " students registered in your room.";
}

// Inits the control buttons for the teacher to control the game
function initUIControlButtons(gameInstanceId) {
  const startGameInstanceButtonElement = document.getElementById('startGameInstanceButton');
  const nextQuestionButton = document.getElementById("nextQuestionButton");
  const previousQuestionButton = document.getElementById("previousQuestionButton");
  const endGameInstanceButton = document.getElementById("endGameInstanceButton");
  const startQuestionButton = document.getElementById("startQuestionButton");
  const endQuestionButton = document.getElementById("endQuestionButton");
  
  startGameInstanceButtonElement.addEventListener('click', () => {
      fetch('/startGameInstance?gameInstance='+gameInstanceId, { method: 'POST' });
  });
  nextQuestionButton.addEventListener('click', () => {
      fetch('/nextQuestion?gameInstance='+gameInstanceId, { method: 'POST' });
  });
  previousQuestionButton.addEventListener('click', () => {
      fetch('/previousQuestion?gameInstance='+gameInstanceId, { method: 'POST' });
  });
  endGameInstanceButton.addEventListener('click', () => {
      fetch('/endGameInstance?gameInstance='+gameInstanceId, { method: 'POST' }).then(()=>{
          window.location.href = "/teacher/controlGameInstance/controlGameInstance.html";
      });
  });
  startQuestionButton.addEventListener('click', () => {
      fetch('/controlQuestion?gameInstance='+gameInstanceId+'&action=start', { method: 'POST' });
  });
  endQuestionButton.addEventListener('click', () => {
      fetch('/controlQuestion?gameInstance='+gameInstanceId+'&action=end', { method: 'POST' });
  });
}

initAuthStateObserver();
