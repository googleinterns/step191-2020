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
  username.innerText = `Posted by: ${comment.nickname}`;

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

/**
 * Function that loads necessary info onLoad()
 */
function onDOMLoad() {
  createMap();
  buildWriteCommentsBox();
  getServletComments();
}

/**
 * Function that checks if the s
 */
async function checkLoggedIn() {
  const response = await fetch('/login-status');
  const loginInfo = await response.json();
  return loginInfo;
}

/**
 * Function that starts the building of a comment box depending on if the user is logged in or not
 */
async function buildWriteCommentsBox() {
  const writeCommentsBox = document.getElementById('js-write-comment-box');
  writeCommentsBox.innerHTML = '';
  const loginInfo = await checkLoggedIn();
  if (loginInfo.isLoggedIn) { 
    writeCommentsBox.appendChild(buildWriteCommentBoxLoggedIn(loginInfo));
  } else {
    writeCommentsBox.appendChild(buildWriteCommentBoxLoggedOut(loginInfo));
  }
}

/**
 * Function that builds the two forms that are displayed to the user if he is logged in
 * @param {*} loginInfo the login info about the user
 */
function buildWriteCommentBoxLoggedIn(loginInfo) {
  const commentFormTemplateClone = document.querySelector('#commentInputBox').content.cloneNode(true);

  const commentForm = commentFormTemplateClone.querySelectorAll('form')[0];
  commentForm.id = "commentForm";
  buildWriteCommentInputBox(loginInfo, commentForm); 

  const nicknameForm = commentFormTemplateClone.querySelectorAll('form')[1];
  nicknameForm.id = 'nicknameForm';
  buildNicknameInputBox(loginInfo, nicknameForm);

  return commentFormTemplateClone;
}

/**
 * Function that builds the section where the user writes his comment
 * @param {*} loginInfo The login info about the user
 * @param {*} commentForm The element that conains the elements in which the user will type and submit his comment
 */
function buildWriteCommentInputBox(loginInfo, commentForm) {
  commentForm.addEventListener('submit', (event) => {
    event.preventDefault();
    submitComment(commentForm, loginInfo);
  });

  const logoutAElement = commentForm.querySelector('a');
  logoutAElement.href = loginInfo.url;
}

/**
 * Function that builds the section where the user sets his nickname
 * @param {*} loginInfo The login info about the user
 * @param {*} nicknameForm The element that conains the elements in which the user will type and submit his nickname
 */
function buildNicknameInputBox(loginInfo, nicknameForm) {
  const nicknamePElement = nicknameForm.querySelector('p');
  nicknamePElement.innerText = `You are posting as: ${loginInfo.nickname}`;

  nicknameForm.addEventListener('submit', (event) => {
    event.preventDefault();
    updateNickname(nicknameForm, nicknamePElement);
  });
}

/**
 * Function that handles when the user wants to change his nickname
 * @param {*} nicknameForm The nickname form that contains the data the user wants to submit
 */
function updateNickname(nicknameForm) {
  const nickname = nicknameForm.elements['comments-nickname-input'].value;
  const nicknamePElement = nicknameForm.querySelector('p');
  if (nickname != "") {
    fetch('/login-status', {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({nickname: nickname})
    }).then(() => {
      nicknamePElement.innerText = `You are posting as: ${nickname}`;
      nicknameForm.reset();
    });
  } 
}


/**
 * Function that builds the comments box telling the user to log in
 * @param {*} loginInfo The login info that carries the link for the user to log in.
 */
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

/**
 * Function that posts the comment only if the user has a nickname
 * @param {*} commentForm The form that contains the comment body the user just wrote
 * @param {*} loginInfo The login info of the user
 */
function submitComment(commentForm, loginInfo) {
  commentBody = commentForm.elements['comments-body-input'].value;
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



let map;
let markers = [];
let infowindow;

const MEXICO_CITY = {
  lat: 19.433,
  lng: -99.133
};

const MONTERREY = {
  lat: 25.686,
  lng: -100.317
};

const PUEBLA = {
  lat: 19.043,
  lng: -98.199
};

const GUANAJUATO = {
  lat: 21.019,
  lng: -101.258
}

const RIVIERA_MAYA = {
  lat: 20.581,
  lng: -87.12
}

const MEXICO_BOUNDS = {
  north: 33.990,
  south: 13.652,
  west: -119.495,
  east: -85.515
};

const mapOpt = {
  center: MEXICO_CITY, 
  zoom: 5,
  mapTypeControlOptions: {
    mapTypeIds: ['roadmap', 'satellite', 'hybrid', 'dark_mode']
  },
  mapTypeId: 'hybrid',
  restriction: {
    latLngBounds: MEXICO_BOUNDS,
    strictBounds: false
  }
};

function createMap() {
  /** Creates a map and adds it to the page. */
  // Dark mode styling
  const darkModeMapType = new google.maps.StyledMapType(
    [
      {
        "elementType": "geometry",
        "stylers": [
          {
            "color": "#242f3e"
          }
        ]
      },
      {
        "elementType": "labels.text.fill",
        "stylers": [
          {
            "color": "#746855"
          }
        ]
      },
      {
        "elementType": "labels.text.stroke",
        "stylers": [
          {
            "color": "#242f3e"
          }
        ]
      },
      {
        "featureType": "administrative.locality",
        "elementType": "labels.text.fill",
        "stylers": [
          {
            "color": "#d59563"
          }
        ]
      },
      {
        "featureType": "poi",
        "elementType": "labels.text.fill",
        "stylers": [
          {
            "color": "#d59563"
          }
        ]
      },
      {
        "featureType": "poi.park",
        "elementType": "geometry",
        "stylers": [
          {
            "color": "#263c3f"
          }
        ]
      },
      {
        "featureType": "poi.park",
        "elementType": "labels.text.fill",
        "stylers": [
          {
            "color": "#6b9a76"
          }
        ]
      },
      {
        "featureType": "road",
        "elementType": "geometry",
        "stylers": [
          {
            "color": "#38414e"
          }
        ]
      },
      {
        "featureType": "road",
        "elementType": "geometry.stroke",
        "stylers": [
          {
            "color": "#212a37"
          }
        ]
      },
      {
        "featureType": "road",
        "elementType": "labels.text.fill",
        "stylers": [
          {
            "color": "#9ca5b3"
          }
        ]
      },
      {
        "featureType": "road.highway",
        "elementType": "geometry",
        "stylers": [
          {
            "color": "#746855"
          }
        ]
      },
      {
        "featureType": "road.highway",
        "elementType": "geometry.stroke",
        "stylers": [
          {
            "color": "#1f2835"
          }
        ]
      },
      {
        "featureType": "road.highway",
        "elementType": "labels.text.fill",
        "stylers": [
          {
            "color": "#f3d19c"
          }
        ]
      },
      {
        "featureType": "transit",
        "elementType": "geometry",
        "stylers": [
          {
            "color": "#2f3948"
          }
        ]
      },
      {
        "featureType": "transit.station",
        "elementType": "labels.text.fill",
        "stylers": [
          {
            "color": "#d59563"
          }
        ]
      },
      {
        "featureType": "water",
        "elementType": "geometry",
        "stylers": [
          {
            "color": "#17263c"
          }
        ]
      },
      {
        "featureType": "water",
        "elementType": "labels.text.fill",
        "stylers": [
          {
            "color": "#515c6d"
          }
        ]
      },
      {
        "featureType": "water",
        "elementType": "labels.text.stroke",
        "stylers": [
          {
            "color": "#17263c"
          }
        ]
      }
    ], {name: 'Dark Mode'}
  );


  map = new google.maps.Map(document.getElementById('map'), mapOpt);

  map.mapTypes.set('dark_mode', darkModeMapType);

  infowindow = new google.maps.InfoWindow({
    content: ''
  });

  // Add markers
  addMarker(MONTERREY, "Monterrey", 'images/norteno-hat.png', new google.maps.Size(50, 38), 'My hometown!');
  addMarker(MEXICO_CITY, "Mexico City", 'images/angel.png', new google.maps.Size(30, 90), 'It\'s not called \"The City of Palaces\" for nothing!');
  addMarker(PUEBLA, "Puebla", 'images/church.png', new google.maps.Size(40, 40), 'An architectural and historical odyssey awaits you at Puebla\'s churches!');
  addMarker(GUANAJUATO, "Guanajuato", 'images/mummy.png', new google.maps.Size(20, 60), 'A truly quaint town where you will experience the Mexican folklore at it\'s finest!');
  addMarker(RIVIERA_MAYA, "Riviera Maya", 'images/pyramid.png', new google.maps.Size(50, 40), 'A paradise on earth,<br> with many Mayan<br> archeological sites<br> to explore!');
  
}

// Adds a marker to the map and push to the array.
function addMarker(location, markerTitle, image, size, description) {
  let marker = new google.maps.Marker({
    position: location,
    map: map,
    title: markerTitle,
    icon: {
      // (width, height)
      size: size,
      scaledSize: size,
      url: image 
    }
  });

  marker.addListener('click', function() {
    infowindow.setContent(description);
    infowindow.open(map, marker);
  });
  markers.push(marker);
}
