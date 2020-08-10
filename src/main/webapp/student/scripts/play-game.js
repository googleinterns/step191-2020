
'use strict';

const db = firebase.firestore();

const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const roomId = urlParams.get('room');
var active = false;

function checkIfActive() {
  db.collection('gameInstance').doc(roomId).onSnapshot(function (doc) {
    if (doc.data().isActive && !active) {
      var readyHeading = document.getElementById("getReady");
      var game = document.getElementById("gameSection");
      readyHeading.classList.toggle("ready");
      game.classList.toggle("active");
      active = true;
      listenGameInstance();
    }
  })
}

function listenGameInstance() {
  db.collection('gameInstance').doc(roomId).onSnapshot(function (doc) {
    getQuestion(doc.data().gameId, doc.data().currentQuestion);
  })
}

function getQuestion(gameId, questionId) {
  const games = db.collection('games').doc(gameId);
  games.collection('questions').doc(questionId).get().then(function (doc) {
    if (doc.exists) {
      createQuestionObject(doc.data().title);
      createAnswersObject(doc.data().answers);
      setSubmitButton(doc.data().answers);
    }
  })
}

function createQuestionObject(title) {
  document.getElementById("question").innerText = title;
}

function createAnswersObject(answers) {
    var quiz = document.getElementById("quiz");
    quiz.innerHTML = "";
    var multipleDiv = document.createElement("div");
    multipleDiv.classList.add("multiple-choice");
  for (var i = 1; i <= answers.length; i++) {
      createAnswer(quiz, multipleDiv, answers[i-1].title, i);
      if(i%2 == 0) {
        quiz.appendChild(multipleDiv);  
        multipleDiv = document.createElement("div");
        multipleDiv.classList.add("multiple-choice");
      }
  }

  const answerFeedbackElement = document.getElementById("answerFeedback");
  answerFeedbackElement.innerHTML = '';
  answerFeedbackElement.className = '';


  const answerFormElement = document.getElementById('answerForm');
  answerFormElement.addEventListener('submit', (event) => {
    event.preventDefault();
    const correctRadioInput = document.getElementById("correctAnswer");
    answerFeedbackElement.className = '';
    if (correctRadioInput.checked) {
      answerFeedbackElement.innerText = "That is correct!"
      answerFeedbackElement.classList.add('rightAnswer')
    } else {
      answerFeedbackElement.innerText = "Are you sure that's the correct answer?"
      answerFeedbackElement.classList.add('wrongAnswer')
    }
  });
}

function createAnswer(quiz, multipleDiv, answerTitle, i){
      const boxDiv = document.createElement("div");
      boxDiv.classList.add("demo-card-square");
      boxDiv.classList.add("mdl-card");
      boxDiv.classList.add("mdl-shadow--2dp");
      const titleDiv = document.createElement("div");
      titleDiv.classList.add("mdl-card__title");
      titleDiv.classList.add("mdl-card--expand")
      titleDiv.setAttribute("id", "card-"+(i-1));
      const title = document.createElement("h2");
      title.classList.add("mdl-card__title-text");
      title.innerText = answerTitle;
      titleDiv.appendChild(title);
      boxDiv.appendChild(titleDiv);
      multipleDiv.appendChild(boxDiv);
}

function setSubmitButton() {

}

checkIfActive();


