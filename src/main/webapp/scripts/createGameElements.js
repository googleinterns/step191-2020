
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
  module.exports = { trueOrFalseGrid, multipleChoiceGrid, titleGrid, questionElement }
  