// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
    ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('js-greeting-container');
  greetingContainer.innerText = greeting;
}

/**
 * Adds Armandos age to the page.
 */
function calculateArmandoAge() {
  // Calculate age, Armando was born on May 13, 2000.
  const diffMs = new Date() - new Date(2000, 5, 13);
  const ageDt = new Date(diffMs);

  // Add it to the page.
  const ageContainer = document.getElementById('armando-age-container');
  ageContainer.innerText = Math.abs(ageDt.getUTCFullYear() - 1970);

}

// Scripts for the image gallery
var slideIndex = 0;

function initSlides() {
  showSlides(slideIndex);
}

// Next control
function showNextSlide() {
  slideIndex++;
  showSlides(slideIndex);
}

// Previous control
function showPreviousSlide() {
  slideIndex--;
  showSlides(slideIndex);
}

// Thumbnail image controls
function updateCurrentSlide(n) {
  slideIndex = n;
  showSlides(slideIndex);
}

function showSlides(n) {
  var slides = document.getElementsByClassName("mySlides");
  var dots = document.getElementsByClassName("dot");
  slideIndex = n % slides.length;
  if (slideIndex == -1) {
    slideIndex = slides.length - 1;
  }
  for (const slide of slides) {
    slide.style.display = "none";
  }
  for (const dot of dots) {
    dot.classList.remove("active");
  }
  slides[slideIndex].style.display = "block";
  dots[slideIndex].classList.add("active");
}

initSlides();

// List of cars that can be displayed
const cars = [
  {
    name: "McLaren 720S",
    power: "720 hp",
    country: "England",
    funFact: "Just look at it, it is beautiful!",
    path: "images/720s.jpg"
  },
  {
    name: "Porsche 918 Spyder",
    power: "720 hp",
    country: "Germany",
    funFact: "Hybrid power for the win!",
    path: "images/porsche.jpeg"
  },
  {
    name: "Ferrari FXX Evoluzione",
    power: "848 hp",
    country: "Italy",
    funFact: "You should really listen to the sound this one makes",
    path: "images/ferrari.jpg"
  },
  {
    name: "Lamborghini Sesto Elemento",
    power: "562 hp",
    country: "Italy",
    funFact: "The whole thing weights less that a tonne thanks to carbon fiber!",
    path: "images/lambo.jpg"
  },
  {
    name: "Mercedes-AMG G63 6x6",
    power: "536 hp",
    country: "Germany",
    funFact: "Taking offroading to a whole new level.",
    path: "images/mercedes.jpeg"
  },
  {
    name: "Subaru WRX STI",
    power: "536 hp",
    country: "Japan",
    funFact: "Boxster engines rock!",
    path: "images/subaru.jpg"
  },
  {
    name: "Mini JCW",
    power: "306 hp",
    country: "England",
    funFact: "I currently own a Mini :).",
    path: "images/mini.jpeg"
  },
  {
    name: "Volkswagen Sedan",
    power: "Unlimited hp",
    country: "Germany",
    funFact: "Grandpa's car <3, learned to drive in it.",
    path: "images/vw.jpg"
  }
];

/**
 * Build the DOM for a car
 */
function buildCarView(car) {
  const carView = document.createElement('div');
  carView.id = 'js-car-container';

  const image = document.createElement('img');
  image.src = car.path;
  image.classList.add('car-image');

  const name = document.createElement('div');
  name.textContent = `Name: ${car.name}`;

  const power = document.createElement('div');
  power.textContent = `Power: ${car.power}`;

  const country = document.createElement('div');
  country.textContent = `Country: ${car.country}`;

  const funFact = document.createElement('div');
  funFact.textContent = `Why I like this car? ${car.funFact}`;

  carView.appendChild(image);
  carView.appendChild(name);
  carView.appendChild(power);
  carView.appendChild(country);
  carView.appendChild(funFact);

  return carView;
}

/**
 * Adds a random car and its data to the page.
 */
function getRandomCar() {
  // Pick a random car.
  const car = cars[Math.floor(Math.random() * cars.length)];

  const carContainer = document.getElementById('js-car-container');
  const carView = buildCarView(car);
  carContainer.replaceWith(carView);
}

/**
 * Fetch message from Java servlet and add it to the DOM
 */
function getServletComments() {
  let maxComments = verifyNumberCommentsInput();
  if (maxComments != null) {
    fetch('/data?maxComments=' + maxComments).then(response => response.json()).then((comments) => {
      // Get the comments container
      const commentsContainer = document.getElementById('js-comments-container');
      commentsContainer.innerHTML = '';
      
      // Check if the array of comments is empty
      if (!Array.isArray(comments) || !comments.length) {
        const pElement = document.createElement('p');
        pElement.innerText = "Looks like there are no comments yet. Be the first one to comment!"
        commentsContainer.appendChild(pElement);
      } else {
        for (const comment of comments) {
          // Add each comment to the commentsContainer
          commentsContainer.appendChild(createCommentElement(comment));
        }
      }
    });
  }
}

/**
 * Verify the input number from selection of number of comments to display
 * Minimum number is 1 comment, and maximum is 10 comments
 * If number is not valid it returns null
 */
function verifyNumberCommentsInput() {
  let maxComments = null;
  let maxCommentsContainer = document.getElementById("comment-number-input");
  maxCommentsContainer.reportValidity();
  let maxCommentsInput = maxCommentsContainer.value;
  if (maxCommentsInput.length != 0 && maxCommentsInput < 11 && maxCommentsInput > 0) {
    maxComments = maxCommentsInput;
  }
  return maxComments;
}

/**
 * Function that handles when the user has voted a comment, then refreshes 
 * the comments section
 * @param {*} comment the comment which is being voted 
 * @param {*} choice true for upvote, false for downvote
 */
function voteComment(comment, choice) {
  fetch('/vote-comment', {
    method: 'POST',
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({commentId: comment.id, commentChoice: choice})
  }).then(() => {
    getServletComments();
  });
}

function deleteComment(comment) {
  fetch('/delete-comment', {
    method: 'POST',
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({commentId: comment.id})
  }).then(() => {
    getServletComments();
  });
}

/**
 * Takes the comment object and builds the element in the DOM
 * @param {*} comment the comment object to be displayed
 */
function createCommentElement(comment) {
  const commentElementTemplateClone = document.querySelector('#commentElementTemplate').content.cloneNode(true);

  const commentView = commentElementTemplateClone.querySelector('div');
  const commentDivs = commentView.querySelectorAll('div');
  
  const username = commentDivs[0];
  username.innerText = `Posted by: ${comment.username}`;

  const timestamp = commentDivs[1];
  const date = new Date(comment.timestamp);
  const options = {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: 'numeric'
  };
  timestamp.innerText = `On: ${new Intl.DateTimeFormat('en', options).format(date)}`;

  const popularity = commentDivs[2];
  popularity.innerText = `Popularity: ${comment.upvotes - comment.downvotes}`;

  const body = commentDivs[3];
  body.innerText = comment.body;

  const commentVotes = commentView.querySelectorAll('img');

  const upvote = commentVotes[0];
  upvote.addEventListener('click', () => {
    voteComment(comment, true);
  });

  const downvote = commentVotes[1];
  downvote.addEventListener('click', () => {
    voteComment(comment, false);
  });

  const deleteCommentButton = commentView.querySelector('button');
  deleteCommentButton.addEventListener('click', () => {
    handleCommentSubmit;
    deleteComment(comment);
  });

  return commentElementTemplateClone;
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

/**
 * Deletes all comments in DS and then refreshes comments section
 */
function deleteAllComments() {
  fetch('/delete-data', {method: 'POST'}).then(() => {
    getServletComments();
  });
}

function onDOMLoad() {
  buildWriteCommentsBox();
  getServletComments();
}

async function checkLoggedIn() {
  const response = await fetch('/login-status');
  const loginInfo = await response.json();
  return loginInfo;
}

async function buildWriteCommentsBox() {
  const writeCommentsBox = document.getElementById('js-write-comment-box');
  const loginInfo = await checkLoggedIn();
  if (loginInfo.isLoggedIn) { 
    writeCommentsBox.appendChild(buildWriteCommentBoxLoggedIn(loginInfo));
  } else {
    writeCommentsBox.appendChild(buildWriteCommentBoxLoggedOut(loginInfo));
  }
}

function buildWriteCommentBoxLoggedIn(loginInfo) {
  const commentFormTemplateClone = document.querySelector('#commentInputBox').content.cloneNode(true);

  commentForm = commentFormTemplateClone.querySelector('form');
  commentForm.addEventListener('submit', () => {
    handleCommentSubmit;
    submitComment(commentForm);
  });

  const logoutAElement = commentForm.querySelector('a');
  logoutAElement.href = loginInfo.url;

  return commentFormTemplateClone;
}

function handleCommentSubmit(event) {
  event.preventDefault();
}

function buildWriteCommentBoxLoggedOut(loginInfo) {
  const divElement = document.createElement('div');
  
  const pElement = document.createElement('p');
  pElement.innerText = "You should log in";

  const loginAElement = document.createElement('a');
  loginAElement.href = loginInfo.url;
  loginAElement.innerText = "Log in by clicking here";

  divElement.appendChild(pElement);
  divElement.appendChild(loginAElement);

  return divElement;
}

function submitComment(commentForm) {
  commentBody = commentForm.elements['comments-body-input'].value;
  console.log(commentBody);
  fetch('/data', {
    method: 'POST',
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({commentBody: commentBody})
  }).then(() => {
    getServletComments();
    commentForm.reset();
  });
}
