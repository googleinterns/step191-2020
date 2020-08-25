firebase.auth().onAuthStateChanged(function(user) {
  if (user) {
    // The parameter fot the user id on
    document.getElementById("user-id").value = getUserId();
  }
});


var dialog = document.querySelector('#modal-example');
var exitDialog = document.querySelector('#modal-exit');

function closeClickHandler() {
    dialog.close();
};
function showClickHandler() {
    dialog.showModal();
};
  
function closeExitClickHandler() {
  exitDialog.close();
  location.replace("..")
};
function showExitClickHandler() {
  exitDialog.showModal();
};
// Hides all of the questions except for the 'n' question 
function hideAllExcept(n) {
  // Get the size of the number of questions (it's -2 because you remove the title form and we start from 0)
  size = document.getElementById("createGameForm").children.length - 2;

  // It goes throught each question form and selector, then it decides if the current index
  // it's the same as 'n' it'll show it
  for(i = -1; i <  size; i++) {
    var currentQuestion = document.getElementById("question" + (i));
    var currentSelector = document.getElementById("jsSelectQ" + (i));
    if(n == i) {
      currentQuestion.removeAttribute("hidden");
      currentSelector.className = "mdl-navigation__link mdl-navigation__link--current";
    } else { 
      currentQuestion.setAttribute("hidden", true);
      currentSelector.className = "mdl-navigation__link";
    }
  }
}

// TODO: Find a better way to tell this function what type of question it is
function addNewQuestion(isMC) {
  // Sets the header if idMC (is multiple choice) is true or not
  var header = isMC ? "Multiple Choice" : "True or False";

  let formElement = document.getElementById("createGameForm");

  // Get the size of the number of questions (it's -2 because you remove the title form and we start from 0)
  let num = formElement.children.length -2;

  // Append to the form element a new question that it's created with this function is called
  formElement.appendChild(questionElement(num, header, isMC));

  let navElem = document.getElementById("drawerNav")
  // The new question link it's added before the new question button
  navElem.insertBefore(navLinkElement(num, header), navElem.lastElementChild);

  // This is a function that MDL requires each time a new input it's added
  componentHandler.upgradeElements(document.getElementsByClassName("mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect"));
  componentHandler.upgradeElements(document.getElementsByClassName("mdl-textfield mdl-js-textfield mdl-textfield--floating-label"));
  
  // Closes the dialog
  dialog.close();
}
// This creates a navigation link for the drawer
function navLinkElement(num, header) {
  let navLinkElem = document.createElement("a");
  navLinkElem.className = "mdl-navigation__link"
  navLinkElem.id = "jsSelectQ" + num;
  navLinkElem.setAttribute('onclick',"hideAllExcept(" + num + ")");
  navLinkElem.innerHTML = "<b>" + (num+1) + " </b> " + header;
  return navLinkElem;
}

function questionElement(num, header, isMC) {
  // The main element is the parent node for the entire question form
  var mainElement = document.createElement("main");
  mainElement.id = "question" + num;
  mainElement.className = "mdl-layout__content questionSection";
  mainElement.setAttribute("hidden", true);

  // Another parent
  var contentElement = document.createElement("div");
  contentElement.className = "page-content";

  // These two lines append childs from the elements created from the following functions: titleGrid(), multipleChoiceGrid(), trueOrFalseGrid()
  // This function creates a grid element that holds the title of the following question with the header of the question
  contentElement.appendChild(titleGrid(header));
  // Depending if isMC is true or false, we create a grid with the respective function
  contentElement.appendChild(isMC ? multipleChoiceGrid(num) : trueOrFalseGrid(num));
  
  mainElement.appendChild(contentElement);
  return mainElement;
}

function titleGrid(title) {
  const titleHtml = '<div class="mdl-grid">' +
    '  <div class="mdl-cell mdl-cell--3-col"></div>' +
      '<div class="mdl-cell mdl-cell--6-col">' +
      '  <h3>' + title + '</h3>' + 
        '<div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">' +
          '<input class="mdl-textfield__input" type="text" name="questionTitle"> <label class="mdl-textfield__label" >Question Title</label>' +
        '</div>' +
      '</div>' +
      '<div class="mdl-cell mdl-cell--3-col"></div>' +
    '</div>'
  const titleElem = document.createElement("div");
  titleElem.innerHTML = titleHtml;
  return titleElem;
}

function multipleChoiceGrid(num) {
  var answersStr = '<input name="isMC" value=true hidden>';
  for (var i = 0; i < 4; i += 2) {
    answersStr += `
    <!-- The first line -->
    <div class="mdl-grid">
      <div class="mdl-cell mdl-cell--1-col"></div>
      <div class="mdl-cell mdl-cell--5-col">
        <!-- First answer -->
        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
          <input class="mdl-textfield__input" type="text" id="game-correct-answer" name="answer` + i + `"> <label class="mdl-textfield__label" > Answer ` + (i+1) + `</label>
        </div>
  
        <!-- Check if it's correct -->
        <label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="question` + num + `checkbox` + i + `">
          <input type="checkbox" id="question` + num + `checkbox` + i + `" class="mdl-checkbox__input" name="correct` + i + `">
          <span class="mdl-checkbox__label">Correct</span>
        </label>
      </div>
      <div class="mdl-cell mdl-cell--5-col">
        <!-- Second answer -->
        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
          <input class="mdl-textfield__input" type="text" id="game-correct-answer" name="answer` + (i+1) + `"> <label class="mdl-textfield__label" > Answer ` + (i+2) + `</label>
        </div>
  
        <!-- Check if it's correct -->
        <label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="question` + num + `checkbox` + (i+1) + `">
          <input type="checkbox" id="question` + num + `checkbox` + (i+1) + `" class="mdl-checkbox__input" name="correct` + (i+1) + `">
          <span class="mdl-checkbox__label">Correct</span>
        </label>
      </div>
      <div class="mdl-cell mdl-cell--1-col"></div>
    </div>
    `
  }
  const elem = document.createElement("div");
  elem.innerHTML = answersStr;
  return elem;
}

function trueOrFalseGrid(num) {
  const answersHtml = `
  <!-- The first line -->
  <input name="isMC" value=false hidden>
  <div class="mdl-grid">
    <div class="mdl-cell mdl-cell--1-col"></div>
    <div class="mdl-cell mdl-cell--5-col">
      <!-- First answer -->
      <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
        <input class="mdl-textfield__input" type="text" id="game-correct-answer" name="answer0"> <label class="mdl-textfield__label" > True </label>
      </div>
      <input hidden type="checkbox" id="question` + num + `checkbox0" class="mdl-checkbox__input" name="correct0" checked>

    </div>

    <div class="mdl-cell mdl-cell--5-col">
      <!-- Second answer -->
      <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
        <input class="mdl-textfield__input" type="text" id="game-correct-answer" name="answer1"> <label class="mdl-textfield__label" > False</label>
      </div>
      <input hidden type="checkbox" id="question` + num + `checkbox1" class="mdl-checkbox__input" name="correct1">

    </div>
    <div class="mdl-cell mdl-cell--1-col"></div>
    <input class="mdl-textfield__input" type="text" id="game-correct-answer" name="answer2" hidden>
    <input hidden name="correct2">
    <input class="mdl-textfield__input" type="text" id="game-correct-answer" name="answer3" hidden> 
    <input hidden name="correct3">
  </div>

  `
  const elem = document.createElement("div");
  elem.innerHTML = answersHtml;
  return elem;
}

function sendQuestions() {
  showExitClickHandler()
  let title = document.getElementsByName("gameTitle")[0].value;
  const questionTitles = document.getElementsByName("questionTitle");
  const questionType = document.getElementsByName("isMC");
  const answer0 = document.getElementsByName("answer0");
  const answer1 = document.getElementsByName("answer1");
  const answer2 = document.getElementsByName("answer2");
  const answer3 = document.getElementsByName("answer3");

  const correct0 = document.getElementsByName("correct0");
  const correct1 = document.getElementsByName("correct1");
  const correct2 = document.getElementsByName("correct2");
  const correct3 = document.getElementsByName("correct3");
  var questions = [];

  for (var i = 0; i < questionTitles.length; i++) {
    if(questionType[i].value == "true") {
      answers = [
        {
          correct : correct0[i].checked,
          title : answer0[i].value
        },
        {
          correct : correct1[i].checked,
          title : answer1[i].value
        },
        {
          correct : correct2[i].checked,
          title : answer2[i].value
        },
        {
          correct : correct3[i].checked,
          title : answer3[i].value
        }
      ]
    } else {
      answers = [
        {
          correct : true,
          title : answer0[i].value
        },
        {
          correct : false,
          title : answer1[i].value
        }
      ]
    }
    var question = {
      title : questionTitles[i].value,
      isMC : (questionType[i].value === "true"),
      answers : answers
    };
    questions.push(question);
  }
  gameJSON = {
    title : title,
    questions : questions,
    creator : getUserId()
  }
  var postParams = new URLSearchParams();
  postParams.append("game", JSON.stringify(gameJSON));
  fetch("/newGame", {method: "POST", body: postParams});
}
