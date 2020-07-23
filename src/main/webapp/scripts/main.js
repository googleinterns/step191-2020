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

// Triggers when the auth state change for instance when the user signs-in or signs-out.
function authStateObserver(user) {
  if (user) { // User is signed in!
    // Get the signed-in user's profile pic and name.
    var profilePicUrl = getProfilePicUrl();
    var userName = getUserName();

    // Set the user's profile pic and name.
    userPicElement.style.backgroundImage = 'url(' + addSizeToGoogleProfilePic(profilePicUrl) + ')';
    userNameElement.textContent = userName;

    // Show user's profile and sign-out button.
    userNameElement.removeAttribute('hidden');
    userPicElement.removeAttribute('hidden');
    signOutButtonElement.removeAttribute('hidden');

    // Hide sign-in button.
    signInButtonElement.setAttribute('hidden', 'true');

    var postParams = new URLSearchParams();
    postParams.append('username', firebase.auth().currentUser.displayName);
    postParams.append('email', firebase.auth().currentUser.email);
    postParams.append('uid', firebase.auth().currentUser.uid);
    fetch("/login", {method: "POST", body: postParams})

  } else { // User is signed out!
    // Hide user's profile and sign-out button.
    userNameElement.setAttribute('hidden', 'true');
    userPicElement.setAttribute('hidden', 'true');
    signOutButtonElement.setAttribute('hidden', 'true');

    // Show sign-in button.
    signInButtonElement.removeAttribute('hidden');
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


// Adds a size to Google Profile pics URLs.
function addSizeToGoogleProfilePic(url) {
  if (url.indexOf('googleusercontent.com') !== -1 && url.indexOf('?') === -1) {
    return url + '?sz=150';
  }
  return url;
}

// A loading image URL.
var LOADING_IMAGE_URL = 'https://www.google.com/images/spin-32.gif?a';



// Checks that the Firebase SDK has been correctly setup and configured.
function checkSetup() {
  if (!window.firebase || !(firebase.app instanceof Function) || !firebase.app().options) {
    window.alert('You have not configured and imported the Firebase SDK. ' +
        'Make sure you go through the codelab setup instructions and make ' +
        'sure you are running the codelab using `firebase serve`');
  }
}

// Checks that Firebase has been imported.
checkSetup();

// Shortcuts to DOM Elements.
var counterFormElement = document.getElementById('counter-form');
var submitButtonElement = document.getElementById('submit');
var userPicElement = document.getElementById('user-pic');
var userNameElement = document.getElementById('user-name');
var signInButtonElement = document.getElementById('sign-in');
var signOutButtonElement = document.getElementById('sign-out');
var signInSnackbarElement = document.getElementById('must-signin-snackbar');

// Saves vote on form submit.
counterFormElement.addEventListener('submit', onCounterFormSubmit);
signOutButtonElement.addEventListener('click', signOut);
signInButtonElement.addEventListener('click', signIn);

// initialize Firebase
initFirebaseAuth();


// We load currently existing votes and listen to new ones.
loadCounter();
