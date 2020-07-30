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
