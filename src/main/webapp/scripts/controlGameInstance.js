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
let game;

// Triggers when the auth state change for instance when the user signs-in or signs-out.
function authStateObserver(user) {
  if (user) { // User is signed in!
    getActiveGameInstanceId(user);
  } else { // User is signed out!
    console.log("Not logged in");
  }
}

function initAuthStateObserver() {
  firebase.auth().onAuthStateChanged(authStateObserver);
}

function getActiveGameInstanceId(user) {
  const uid = user.uid;

  db.collection("users").doc(uid).get().then(function(doc) {
    if (doc.exists) {
      activeGameInstanceId = doc.data().activeGameInstanceId;
      queryActiveGameInstanceDocument(activeGameInstanceId);
    } else {
        // doc.data() will be undefined in this case
      console.log("No such document!");
    }
  }).catch(function(error) {
      console.log("Error getting document:", error);
  });
}

function queryActiveGameInstanceDocument(gameInstanceId) {
  db.collection("liveRooms").doc(gameInstanceId).get().then(function(doc) {
    if (doc.exists) {
      addGameInstanceIdToUI(gameInstanceId);
      buildActiveGameInstanceUI(doc.data());
    } else {
        // doc.data() will be undefined in this case
      console.log("No such document!");
    }
  }).catch(function(error) {
      console.log("Error getting document:", error);
  });
}

function addGameInstanceIdToUI(gameInstanceId) {
  const gameInstanceIdElement = document.getElementById("jsGameInstanceId");
  gameInstanceIdElement.innerText = "This gameInstance's ID: " + gameInstanceId;
}

function buildActiveGameInstanceUI(gameInstance) {
  queryGameDetails(gameInstance.gameId);
}

function queryGameDetails(gameId) {
  db.collection("games").doc(gameId).get().then(function(doc) {
    if (doc.exists) {
      addGameIdToUI(gameId);
      game = doc.data();
      addGameDetailsToUI(game);
    } else {
        // doc.data() will be undefined in this case
      console.log("No such document!");
    }
  }).catch(function(error) {
      console.log("Error getting document:", error);
  });
}

function addGameIdToUI(gameId) {
  const gameIdElement = document.getElementById("jsGameId");
  gameIdElement.innerText = "The game's ID is: " + gameId;
}

function addGameDetailsToUI(game) {
  console.log(game);
  const gameTitleElement = document.getElementById("jsGameTitle");
  gameTitleElement.innerText = "The game's title is: " + game.title;
}

function initActiveQuestionListener() {
  
}

initAuthStateObserver();
