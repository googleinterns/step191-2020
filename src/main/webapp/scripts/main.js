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

//When vote button is clicked it calls this function
function increaseCounter() {

    //Gets the document from firebase and updates the value to +1
    firebase.firestore().collection('counter').get().then(function(querySnapshot) {

        if(querySnapshot.size == 0){
            //If there is no document it adds the first one with value 1
              return firebase.firestore().collection('counter').add({
                    number: 1 
                }).catch(function(error) {
                    console.error('Error writing new counter to database', error);
                });
        }

        querySnapshot.forEach(function(count) {
                firebase.firestore().collection("counter").doc(count.id).update({
                    number: count.data().number + 1 
                });
        })

    });

}

function loadCounter() {
 // Gets the document
  var queryCounter = firebase.firestore().collection('counter').limit(1);

  //If there is no document it displays nothing
  if(queryCounter == null) { return displayCounter(""); }

   queryCounter.onSnapshot(function(snapshot) {
    //Listens for changes in the document   
    snapshot.docChanges().forEach(function(change) {
        if (change.type === 'removed') {
        displayCounter("");
        } else {
         var counter = change.doc.data();
         displayCounter(counter.number);
        }
    });

  });

}

function displayCounter(number){
    document.getElementById("counter").innerText = number;
}

//Listens to vote button
function onCounterFormSubmit(e) {
  e.preventDefault();
  // Check that the user is signed in.
  if (checkSignedInWithVote()) {
    increaseCounter();
  }
}

// Returns true if user is signed-in. Otherwise false and displays a message.
function checkSignedInWithVote() {
  // Return true if the user is signed in Firebase
  if (isUserSignedIn()) {
    return true;
  }

  // Display a message to the user using a Toast.
  var data = {
    message: 'You must sign-in first',
    timeout: 2000
  };
  signInSnackbarElement.MaterialSnackbar.showSnackbar(data);
  return false;
}

// Shortcuts to DOM Elements.
var counterFormElement = document.getElementById('counter-form');
var submitButtonElement = document.getElementById('submit');

// Saves vote on form submit.
counterFormElement.addEventListener('submit', onCounterFormSubmit);

// We load currently existing votes and listen to new ones.
loadCounter();
