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

function loadGameOptions() {
  db.collection("games").get()
  .then(function(querySnapshot) {
      querySnapshot.forEach(function(doc) {
          // doc.data() is never undefined for query doc snapshots
          addGameToList(doc);
      });
  })
  .catch(function(error) {
      console.log("Error getting documents: ", error);
  });
}

function addGameToList(doc) {
  const gameListContainer = document.getElementById('js-game-list');
  
  const newListItem = document.createElement('div');
  const gameObj = doc.data();
  newListItem.innerText = gameObj.title;

  newListItem.addEventListener('click', () => {
    selectGame(doc.id);
  });

  gameListContainer.appendChild(newListItem);  
}

function selectGame(gameId) {
  firebase.auth().currentUser.getIdToken(/* forceRefresh */ true).then(function(idToken) {
    // Send token to your backend via HTTPS
    // ...

    fetch('/newGameInstance', {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({idToken: idToken, gameId: gameId})
    }).then(() => {
      window.location.replace(window.location.origin + "/teacher/controlGameInstance.html");
    });

  }).catch(function(error) {
    // Handle error
  });
  
}

// Load available games in DB
loadGameOptions();
