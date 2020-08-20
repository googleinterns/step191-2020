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

// Variables to identify the change when receiving an update of the game Instance 
let numberOfMembersInGameInstance = null;
let currentQuestionId = null;

// These variables will help in detaching listeners to documents we no longer need to listen to in Firestore
let unsubscribeCurrentActiveQuestionInGameInstance = null;
let unsubscribeCurrentActiveQuestionAnswersInGameInstance = null;

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

// Build the Teacher Control panel UI
async function loadControlPanel(user) {
  // Get the Game Instance's ID in which the user is participating
  // First check if it has been provided in the query params
  let gameInstanceId = getGameInstanceIdFromQueryParams();
  
  // If not, then retrieve it from Firestore
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

  // Add the questions' history to the UI
  buildQuestionHistory(gameInstanceId);
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

    // If currentQuestionId is different from what we have in memory it means it has changed and we must update UI
    if (currentQuestionId != gameInstanceUpdate.currentQuestion) {
      updateCurrentQuestion({ gameId: gameInstanceUpdate.gameId, currentQuestionId: gameInstanceUpdate.currentQuestion, isCurrentQuestionActive: gameInstanceUpdate.currentQuestionActive });

      // The listener to the question stats is initiated
      initQuestionStatsListener({ gameInstanceId, currentQuestionId: gameInstanceUpdate.currentQuestion });

      // The listener to the question answers is initiated
      initQuestionAnswerStatsListener({ gameInstanceId, currentQuestionId: gameInstanceUpdate.currentQuestion });
    }

    // If numberOfMembersInGameInstance is different from what we have in memory it means it has changed and we must update UI
    if (numberOfMembersInGameInstance != gameInstanceUpdate.numberOfMembers) {
      // Show the updated number of members in UI
      updateNumberOfMembersUI(gameInstanceUpdate.numberOfMembers);
    }
    
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

// Queries and returns the currentQuestion object
function queryCurrentQuestion({ gameId, currentQuestionId }) {
  return db.collection('games').doc(gameId).collection('questions').doc(currentQuestionId).get().then(function(doc) {
    if (doc.exists) {
      return doc.data()
    }
  });
}

// Init the listener to a change in the current question's answers
function initQuestionAnswerStatsListener({ gameInstanceId, currentQuestionId } = {}) {
  if (unsubscribeCurrentActiveQuestionAnswersInGameInstance) {
    // This helps remove unnecesary listeners to an answers' collection
    unsubscribeCurrentActiveQuestionAnswersInGameInstance();
  }
  unsubscribeCurrentActiveQuestionAnswersInGameInstance = db.collection('gameInstance').doc(gameInstanceId).collection('questions').doc(currentQuestionId).collection('answers')
  .onSnapshot(function(querySnapshot) {
    // Clean the answers' div
    clearCurrentQuestionAnswersDiv();

    querySnapshot.forEach(function(doc) {
      // Update the current question's answers in the "Active" top panel
      updateCurrentQuestionAnswerStats(doc.data(), doc.id);

      // Update the current question's answers in the history section
      updateCurrentQuestionAnswerStatsHistory(doc.data(), doc.id, currentQuestionId);
    });
  });
}

// Clear the current question's answers in UI
function clearCurrentQuestionAnswersDiv() {
  const currentQuestionAnswersDivElement = document.getElementById('jsCurrentQuestionAnswers');
  currentQuestionAnswersDivElement.innerText = '';
}

// Update the current question's answer's stats in the main panel
function updateCurrentQuestionAnswerStats(updatedAnswer) {
  const currentQuestionAnswersDivElement = document.getElementById('jsCurrentQuestionAnswers');
  
  const answerElement = document.createElement('div');
  answerElement.innerText = updatedAnswer.title + ' with ' + updatedAnswer.numberAnswers + ' answers.';
  
  if (updatedAnswer.correct) {
    answerElement.innerText += ' (Correct answer)';
  }

  currentQuestionAnswersDivElement.appendChild(answerElement);
}

// Update a question's stats in the history section
function updateCurrentQuestionAnswerStatsHistory({ updatedAnswer, answerId, questionId } = {}) {
  const answerInQuestionStatsDivElement = document.getElementById('stats-' + questionId + '-' + answerId);
  if (answerInQuestionStatsDivElement == null) {
    // This may be executed before the node is created the first time
    return;
  }

  // Update question stats
  answerInQuestionStatsDivElement.innerText = updatedAnswer.title + ' with ' + updatedAnswer.numberAnswers + ' answers.';

  if (updatedAnswer.correct) {
    answerInQuestionStatsDivElement.innerText += ' (Correct answer)';
  } else {
    answerInQuestionStatsDivElement.innerText += ' (Wrong answer)';
  }
}

// Update the number of students in game in UI
function updateNumberOfMembersUI(numberOfMembers) {
  const numberOfMembersElement = document.getElementById("jsNumberOfStudents");
  numberOfMembersElement.innerText = "There are " + numberOfMembers + " students registered in your room.";
}

// Listen to the current question's stats for any change
function initQuestionStatsListener({ gameInstanceId, currentQuestionId } = {}) {
  if (unsubscribeCurrentActiveQuestionInGameInstance) {
    // This is to stop listening to live changes to the previous question which is not active anymore
    unsubscribeCurrentActiveQuestionInGameInstance();
  }
  unsubscribeCurrentActiveQuestionInGameInstance = db.collection('gameInstance').doc(gameInstanceId).collection('questions').doc(currentQuestionId).onSnapshot(function (doc) {
    updateQuestionStatsHelper({ updatedQuestionStats: doc.data(), currentQuestionId });
  });
}

// When there is an update in a question's stats the update must be reflected on the active panel and the question's history section
function updateQuestionStatsHelper({ updatedQuestionStats, currentQuestionId } = {}) {
  // Update the current question's stats in the "Active" main panel of UI
  updateCurrentQuestionStats(updatedQuestionStats);

  // Update the current question's stats in history section of UI
  updateCurrentQuestionStatsInHistory({ updatedQuestionStats, currentQuestionId });
}

// Update the current question's stats in the "Active" main panel of UI
function updateCurrentQuestionStats(updatedQuestionStats) {

  // Update the question in the active panel
  const numberCurrentQuestionAnswersElement = document.getElementById('jsNumberCurrentQuestionAnswers');
  numberCurrentQuestionAnswersElement.innerText = 'This question has been answered by ' + updatedQuestionStats.numberAnswered + ' students.';

  const numberCorrectCurrentQuestionAnswersElement = document.getElementById('jsNumberCorrectCurrentQuestionAnswers');
  numberCorrectCurrentQuestionAnswersElement.innerText = 'Number of answers correct: ' + updatedQuestionStats.numberCorrect

  const numberWrongCurrentQuestionAnswersElement = document.getElementById('jsNumberWrongCurrentQuestionAnswers');
  numberWrongCurrentQuestionAnswersElement.innerText = 'Number of answers wrong: ' + updatedQuestionStats.numberWrong

}

// Update the current question's stats in history section of UI
function updateCurrentQuestionStatsInHistory({ updatedQuestionStats, currentQuestionId } = {}) {
  const numberQuestionAnswersHistoryElement = document.getElementById('stats-' + currentQuestionId + '-numberQuestionAnswers');
  if (numberQuestionAnswersHistoryElement == null) {
    // This may be executed before the node is created the first time
    return;
  }

  numberQuestionAnswersHistoryElement.innerText = 'Number of answers: ' + updatedQuestionStats.numberAnswered;

  const numberCorrectQuestionAnswersHistoryElement = document.getElementById('stats-' + currentQuestionId + '-numberCorrectQuestionAnswers');
  numberCorrectQuestionAnswersHistoryElement.innerText = 'Number of correct anwers: ' + updatedQuestionStats.numberCorrect;

  const numberWrongQuestionAnswersHistoryElement = document.getElementById('stats-' + currentQuestionId + '-numberWrongQuestionAnswers');
  numberWrongQuestionAnswersHistoryElement.innerText = 'Number of wrong anwers: ' + updatedQuestionStats.numberWrong;
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

// Get all questions in the "questions" collection in the gameInstance
function buildQuestionHistory(gameInstanceId) {
  const questionInGameInstanceCollectionRef = db.collection('gameInstance').doc(gameInstanceId).collection('questions');
  questionInGameInstanceCollectionRef.get().then(function(querySnapshot) {
    querySnapshot.forEach(function(doc) {
      // doc.data() is never undefined for query doc snapshots
      const questionId = doc.id

      // Add the question to the UI
      addQuestionToHistoryUI({ question: doc.data(), questionId });

      // Add the question's answers to the UI
      buildQuestionAnswersHistory({ questionId, questionsCollectionRef: questionInGameInstanceCollectionRef });
    });
  });
}

// Add a question to the Question History section of the UI 
function addQuestionToHistoryUI({ question, questionId } = {}) {
  const questionStatsDivElement = document.getElementById('jsQuestionStats');
  
  // The div for this new question element
  const singleQuestionStatDivElement = document.createElement('div');
  singleQuestionStatDivElement.id = 'stats-' + questionId
  singleQuestionStatDivElement.classList.add('questionInHistory');

  const questionTitle = document.createElement('div');
  questionTitle.innerText = 'Question title: ' + question.title;
  singleQuestionStatDivElement.appendChild(questionTitle);

  // How many students have answered the question
  const numberQuestionAnswersHistoryElement = document.createElement('div');
  numberQuestionAnswersHistoryElement.id = 'stats-' + questionId + '-numberQuestionAnswers';
  numberQuestionAnswersHistoryElement.innerText = 'Number of answers: ' + question.numberAnswered;
  singleQuestionStatDivElement.appendChild(numberQuestionAnswersHistoryElement);

  // How many students have answered correclty the question
  const numberCorrectQuestionAnswersHistoryElement = document.createElement('div');
  numberCorrectQuestionAnswersHistoryElement.id = 'stats-' + questionId + '-numberCorrectQuestionAnswers';
  numberCorrectQuestionAnswersHistoryElement.innerText = 'Number of correct anwers: ' + question.numberCorrect;
  singleQuestionStatDivElement.appendChild(numberCorrectQuestionAnswersHistoryElement);

  // How many students have answered incorreclty the question
  const numberWrongQuestionAnswersHistoryElement = document.createElement('div');
  numberWrongQuestionAnswersHistoryElement.id = 'stats-' + questionId + '-numberWrongQuestionAnswers';
  numberWrongQuestionAnswersHistoryElement.innerText = 'Number of wrong anwers: ' + question.numberWrong;
  singleQuestionStatDivElement.appendChild(numberWrongQuestionAnswersHistoryElement);

  // Add the question to the questions history section
  questionStatsDivElement.appendChild(singleQuestionStatDivElement);
}

// Add a question's answers to its history stats
function buildQuestionAnswersHistory({ questionId, questionsCollectionRef } = {}) {
  // Get the reference to the question's answers
  const answersCollectionRef = questionsCollectionRef.doc(questionId).collection('answers');
  answersCollectionRef.get().then(function(querySnapshot) {
    querySnapshot.forEach(function(doc) {
      // Add the answer and its stats to the history section in UI
      addQuestionAnswerToHistoryUI({ questionId, answerId: doc.id, answer: doc.data() });
    });
  });
}

// Add an answer's stats to the history section of the UI
function addQuestionAnswerToHistoryUI({ questionId, answerId, answer } = {}) {
  // The div in which the answer will be inserted
  const questionStatsDivElement = document.getElementById('stats-' + questionId);

  // Build the answer element
  const answerInQuestionStatsDivElement = document.createElement('div');
  answerInQuestionStatsDivElement.id = 'stats-' + questionId + '-' + answerId;
  answerInQuestionStatsDivElement.innerText = answer.title + ' with ' + answer.numberAnswers + ' answers.';

  if (answer.correct) {
    answerInQuestionStatsDivElement.innerText += ' (Correct answer)';
  }

  // Add the answer to its component in the DOM
  questionStatsDivElement.appendChild(answerInQuestionStatsDivElement);
}

initAuthStateObserver();
