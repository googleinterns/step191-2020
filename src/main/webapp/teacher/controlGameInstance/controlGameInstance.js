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

  // Add all the GameInstance's Info to UI
  buildActiveGameInstanceUI(gameInstance, gameInstanceId);


  // Add buttons to control the GameInstance state
  initUIButtons(gameInstanceId);

  console.log(gameInstanceId);
  console.log(gameInstance);
}

// Queries the "Users" collection of the DB to get the activeGameInstanceId the User is participating in
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

// Gets the gameInstance entity from the "GameInstance" collections of DB
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
    updateGameInfo(gameInstanceUpdate.currentQuestion, gameInstanceUpdate.gameId);
    console.log(gameInstanceUpdate);
  });
}

async function updateGameInfo(currentQuestion, gameId) {
    const games = db.collection('games').doc(gameId);
    games.collection('questions').doc(currentQuestion).get().then(function(doc) {
        if (doc.exists) {
            const activeQuestionTextElement = document.getElementById('jsActiveQuestionText');
            activeQuestionTextElement.innerText = 'The question is: \"' + doc.data().title + '\"';
        }
    })
    const activeQuestionNumberElement = document.getElementById('jsActiveQuestionNumber');
    activeQuestionNumberElement.innerText = "Students are seeing question #" + (currentQuestion);

}

function initUIButtons(gameInstanceId) {
  const startGameInstanceButtonElement = document.getElementById('startGameInstanceButton');
  const nextQuestionButton = document.getElementById("nextQuestionButton");
  const previousQuestionButton = document.getElementById("previousQuestionButton");
  const endGameInstanceButton = document.getElementById("endGameInstanceButton");
  
  startGameInstanceButtonElement.addEventListener('click', () => {
      fetch('/startGameInstance?action=start&gameInstance='+activeGameInstanceId);
  });
  nextQuestionButton.addEventListener('click', () => {
      fetch('/startGameInstance?action=next&gameInstance='+activeGameInstanceId);
  });
  previousQuestionButton.addEventListener('click', () => {
      fetch('/startGameInstance?action=previous&gameInstance='+activeGameInstanceId);
  });
  endGameInstanceButton.addEventListener('click', () => {
      fetch('/startGameInstance?action=end&gameInstance='+activeGameInstanceId);
      window.location.href = "/teacher/controlGameInstance.html";
  });
}

initAuthStateObserver();
