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

  // Your web app's Firebase configuration
var firebaseConfig = {
    apiKey: "AIzaSyCoNJFqs76_GbI1i1T7hhPmyxqv1Oc2hU4",
    authDomain: "quizzy-step-2020.firebaseapp.com",
    databaseURL: "https://quizzy-step-2020.firebaseio.com",
    projectId: "quizzy-step-2020",
    storageBucket: "quizzy-step-2020.appspot.com",
    messagingSenderId: "1029940211712",
    appId: "1:1029940211712:web:8e31fe1fcb8423d4a728e5",
    measurementId: "G-M62T74PK0V"
};
  
// Initialize Firebase
firebase.initializeApp(firebaseConfig);

var db = firebase.firestore();

function signIn() {
  // Sign into Firebase using popup auth & Google as the identity provider.
  var provider = new firebase.auth.GoogleAuthProvider();
  firebase.auth().signInWithPopup(provider);
}

function signOut() {
  // Sign out of Firebase.
  firebase.auth().signOut();
}

// Initiate Firebase Auth.
function initFirebaseAuth() {
  // Listen to auth state changes.
  firebase.auth().onAuthStateChanged(authStateObserver);
}

// Returns the signed-in user's profile pic URL.
function getProfilePicUrl() {
  return firebase.auth().currentUser.photoURL || '/images/profile_placeholder.png';
}

// Returns the signed-in user's display name.
function getUserName() {
  return firebase.auth().currentUser.displayName;
}

// Returns true if a user is signed-in.
function isUserSignedIn() {
  return !!firebase.auth().currentUser;
}

>>>>>>> origin/master
=======
>>>>>>> 8e0f9a973e55c66f48e1f831d438f32c76a3e23d
//When vote button is clicked it calls this function
function increaseCounter() {
  fetch('/increase', {method: 'POST'});
}

function loadCounter() {
  db.collection("liveCounter").doc("counter").onSnapshot(function(doc) {
        displayCounter(doc.data().value);
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
