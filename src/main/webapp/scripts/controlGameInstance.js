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
let activeGameInstanceId;

// Triggers when the auth state change for instance when the user signs-in or signs-out.
function authStateObserver(user) {
  if (user) { // User is signed in!
    // Everything starts working when the User logs in
    getActiveGameInstanceId(user);
  } else { // User is signed out!
    console.log("Not logged in");
  }
}

// Is triggered when the User logs in or logs out
function initAuthStateObserver() {
  firebase.auth().onAuthStateChanged(authStateObserver);
}

// Queries the "Users" collection of the DB to get the activeGameInstanceId the User is participating in
function getActiveGameInstanceId(user) {
  const uid = user.uid;

  // Query the User's document in "Users" collection
  db.collection("users").doc(uid).get().then(function(doc) {
    if (doc.exists) {
      // Get the activeGameInstance's ID in which the User is participating
      activeGameInstanceId = doc.data().activeGameInstanceId;
      // Query the info of that GameInstance from the "GameInstance" collections
      queryActiveGameInstanceDocument(activeGameInstanceId);
    } else {
        // doc.data() will be undefined in this case
      console.log("No such document!");
    }
  }).catch(function(error) {
      console.log("Error getting document:", error);
  });
}

// Gets the gameInstance entity from DB
function queryActiveGameInstanceDocument(gameInstanceId) {
  db.collection("gameInstance").doc(gameInstanceId).get().then(function(doc) {
    if (doc.exists) {
      // The GameInstance exists, so now populate all info about it

      // Add all the GameInstance's Info to UI
      buildActiveGameInstanceUI(doc.data(), gameInstanceId);
      
      // Add buttons to control the GameInstance state
      //initUIButtons(gameInstanceId);
    } else {
        // doc.data() will be undefined in this case
      console.log("No such document!");
    }
  }).catch(function(error) {
      console.log("Error getting document:", error);
  });
}

// Build the UI elements with all the info of the GameInstance
function buildActiveGameInstanceUI(gameInstance, gameInstanceId) {
  // Add the GameInstance's ID to UI
  addGameInstanceIdToUI(gameInstanceId);

  // Get the game object and add it to the UI
  queryGameDetails(gameInstance.gameId);

  // This will listen to when anything in the GameInstance changes
  initGameInstanceListener();
}

// Adds the GameInstance's ID to the UI
function addGameInstanceIdToUI(gameInstanceId) {
  const gameInstanceIdElement = document.getElementById("jsGameInstanceId");
  gameInstanceIdElement.innerText = "This gameInstance's ID is: " + gameInstanceId;
}

// Queries the Game instance from the DB and then adds it to the UI
function queryGameDetails(gameId) {
  db.collection("games").doc(gameId).get().then(function(doc) {
    if (doc.exists) {
      const game = doc.data();
      addGameDetailsToUI(game, gameId);
    } else {
        // doc.data() will be undefined in this case
      console.log("No such document!");
    }
  }).catch(function(error) {
      console.log("Error getting document:", error);
  });
}

// Adds the Game's details to the UI
function addGameDetailsToUI(game, gameId) {
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

function initGameInstanceListener() {
  db.collection('gameInstance').doc(activeGameInstanceId).onSnapshot(function(doc) {
    // If this is triggered it's because the GameInstance's activeQuestion changed
    const gameInstanceUpdate = doc.data();
    updateGameInfo(gameInstanceUpdate.activeQuestionId);
    console.log(gameInstanceUpdate);
  });
}

function updateGameInfo(activeQuestionNumber) {
  const activeQuestionNumberElement = document.getElementById('jsActiveQuestionNumber');
  activeQuestionNumberElement.innerText = "Students are seeing question #" + (activeQuestionNumber + 1);
  
  const activeQuestionTextElement = document.getElementById('jsActiveQuestionText');
  activeQuestionTextElement.innerText = 'The question is: \"' + game.questions[activeQuestionNumber].question + '\"'
}

function initUIButtons(gameInstanceId) {
  const startGameInstanceButtonElement = document.getElementById('startGameInstanceButton');
  startGameInstanceButtonElement.addEventListener('click', () => {
    console.log("Hi");
    // fetch('/newGameInstance', {
    //   method: 'POST',
    //   headers: {
    //     'Accept': 'application/json',
    //     'Content-Type': 'application/json'
    //   },
    //   body: JSON.stringify({idToken: idToken, gameId: gameId})
  });
}

initAuthStateObserver();
