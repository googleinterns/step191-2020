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

// Listen to clicks on "Go" button
function initGoButton() {
  const goButtonElement = document.getElementById('goButton');
  goButtonElement.addEventListener('click', () => {
    checkGoButton();
  });
}

// User has clicked the "Go!" button
async function checkGoButton() {
  const gameInstanceId = document.getElementById("gameInstanceId").value;

  const gameInstanceExists = await verifyGameInstanceExists(gameInstanceId);
  if (gameInstanceExists) {
    joinGameInstance(gameInstanceId);
  } else {
    showGameInstanceDoesntExist();
  }
}

// Check if GameInstanceId is valid
function verifyGameInstanceExists(gameInstanceId) {
  return db.collection("gameInstance").doc(gameInstanceId).get().then(function (doc) {
    if (!doc.exists) {
      return false;
    } else {
      return true;
    }
  });
}

// Send the request to join the Game Instance
function joinGameInstance(gameInstanceId) {
  firebase.auth().currentUser.getIdToken(/* forceRefresh */ true).then(function(idToken) {
    fetch('/joinGameInstance', {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({idToken: idToken, gameInstanceId: gameInstanceId})
    }).then(() => {
      window.location.href = "/student/play-game.html?gameInstanceId=" + gameInstanceId;
    });
  }).catch(function(error) {
    // Handle error
    console.log("Please log in");
  });
}

// Display a message saying that GameInstanceId is not valid
function showGameInstanceDoesntExist() {
  const resultGameInstanceElement = document.getElementById("resultGameInstance");
  resultGameInstanceElement.classList.toggle("notActive");
  setTimeout(function () {
    resultGameInstanceElement.classList.toggle("notActive");
  }, 5000);
}

// TODO: We should check if the user is signed in or not before allowing to click
initGoButton()
