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

// Is triggered when the User logs in or logs out
function initAuthStateObserver() {
  firebase.auth().onAuthStateChanged(authStateObserver);
}

// Triggers when the auth state change for instance when the user signs-in or signs-out.
function authStateObserver(user) {
  if (user) { // User is signed in!
    // Everything starts working when the User logs in
    //getActiveGameInstanceId(user);
    loadControlPanel(user);
  } else { // User is signed out!
    console.log("Not logged in");
  }
}

async function loadControlPanel(user) {
  // Get the Game Instance's ID in which the user is participating
  const gameInstanceId = await getActiveGameInstanceId(user);

  // Get the Active Game Instance Object from DB
  const gameInstance = await queryActiveGameInstance(gameInstanceId);

  // Get the Game the GameInstance is using from DB
  const game = await queryGameDetails(gameInstance.gameId);

  // Add all the GameInstance's Info to UI
  buildActiveGameInstanceUI(gameInstanceId, gameInstance, game);

  // Add buttons to control the GameInstance state
  initUIControlButtons(gameInstanceId);
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
function buildActiveGameInstanceUI(gameInstanceId, gameInstance, game) {
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
    updateCurrentQuestion(gameInstanceUpdate.gameId, gameInstanceUpdate.currentQuestion);

    updateNumberOfMembersUI(gameInstanceUpdate.numberOfMembers);
  });
}

// Updates the panel showing which questions students are seeing
async function updateCurrentQuestion(gameId, currentQuestionId) {
  const currentQuestion = await queryCurrentQuestion(gameId, currentQuestionId);

  const activeQuestionTextElement = document.getElementById('jsActiveQuestionText');
  activeQuestionTextElement.innerText = 'The question is: \"' + currentQuestion.title + '\"';

  const activeQuestionNumberElement = document.getElementById('jsActiveQuestionNumber');
  activeQuestionNumberElement.innerText = "Students are seeing question with ID: " + (currentQuestionId);
}

// Queries and returns the currentQuestion object
function queryCurrentQuestion(gameId, currentQuestionId) {
  return db.collection('games').doc(gameId).collection('questions').doc(currentQuestionId).get().then(function(doc) {
    if (doc.exists) {
      return doc.data()
    }
  });
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
  
  startGameInstanceButtonElement.addEventListener('click', () => {
      fetch('/startGameInstance?action=start&gameInstance='+gameInstanceId);
  });
  nextQuestionButton.addEventListener('click', () => {
      fetch('/startGameInstance?action=next&gameInstance='+gameInstanceId);
  });
  previousQuestionButton.addEventListener('click', () => {
      fetch('/startGameInstance?action=previous&gameInstance='+gameInstanceId);
  });
  endGameInstanceButton.addEventListener('click', () => {
      fetch('/startGameInstance?action=end&gameInstance='+gameInstanceId);
      window.location.href = "/teacher/controlGameInstance.html";
  });
}

initAuthStateObserver();
