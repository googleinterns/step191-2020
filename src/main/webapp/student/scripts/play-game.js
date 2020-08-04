
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
  const radioForm = document.getElementById("answerOptions");
  document.getElementById("answerOptions").innerHTML = "";
  console.log(radioForm)
  for (var i = 0; i < answers.length; i++) {
    var input = document.createElement("input");
    input.setAttribute("type", "radio");
    input.setAttribute("name", "answer");
    input.setAttribute("value", answers[i].title);
    if (answers[i].correct) {
      input.id = "correctAnswer"
    }
    radioForm.appendChild(input);
    radioForm.append(answers[i].title);
    radioForm.appendChild(document.createElement("br"));
  }

  const answerFeedbackElement = document.getElementById("answerFeedback");
  answerFeedbackElement.innerHTML = '';
  answerFeedbackElement.className = '';


  const answerFormElement = document.getElementById('answerForm');
  answerFormElement.addEventListener('submit', (event) => {
    event.preventDefault();
    const correctRadioInput = document.getElementById("correctAnswer");
    answerFeedbackElement.className = '';
    var titleElement = document.createElement("h3")
    if (correctRadioInput.checked) {
      answerFeedbackElement.innerText = "That is correct!"
      answerFeedbackElement.classList.add('rightAnswer')
      titleElement.className = "mdl-dialog__title mdl-color-text--green-900";
      titleElement.innerText = "Your answer is correct!"
    } else {
      answerFeedbackElement.innerText = "Are you sure that's the correct answer?"
      answerFeedbackElement.classList.add('wrongAnswer')
      titleElement.className = "mdl-dialog__title mdl-color-text--red-900";
      titleElement.innerText = "Your answer is wrong :("
    }
    document.getElementById("dialog-title").appendChild(titleElement)
    dialog.showModal();
  });
}

function setSubmitButton() {

}

checkIfActive();


  (function() {
    'use strict';
    var dialog = document.querySelector('#dialog');
    if (! dialog.showModal) {
      dialogPolyfill.registerDialog(dialog);
    }
    dialog.querySelector('button:not([disabled])')
    .addEventListener('click', function() {
      dialog.close();
    });
  }());