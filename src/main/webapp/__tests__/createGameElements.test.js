// Provides more matchers like `toBeOneOf`
require('jest-extended'); 
// module path is relative to 'src/main/webapp/'
const { trueOrFalseGrid, multipleChoiceGrid, titleGrid, questionElement } = require('scripts/createGameElements.js');

// `describe` blocks can be used to group related tests together.
describe('createGameGrids', () => {
  // `test` is a single unit test
  test('is the true or false grid appearing', () => {
    // Set up our document body
    var num = 0;
    document.body.innerHTML = "<div id='trueOrFalseDiv'>" + `
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
  
    ` + "<div>";

    // Call the function under test
    const expectedTrueOrFalseDiv = trueOrFalseGrid(num);
    expect(document.getElementById('trueOrFalseDiv').innerHTML).toContain(expectedTrueOrFalseDiv.innerHTML);
  });

  test("There's a title grid appearing", () => {
    var title = "Hello this is a test!!!";

    document.body.innerHTML =  "<div id='titleGrid'>" + '<div class="mdl-grid">' +
    '  <div class="mdl-cell mdl-cell--3-col"></div>' +
      '<div class="mdl-cell mdl-cell--6-col">' +
      '  <h3>' + title + '</h3>' + 
        '<div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">' +
          '<input class="mdl-textfield__input" type="text" name="questionTitle"> <label class="mdl-textfield__label" >Question Title</label>' +
        '</div>' +
      '</div>' +
      '<div class="mdl-cell mdl-cell--3-col"></div>' +
    '</div>' + '</div>';

    const expectedTitleDiv = titleGrid(title);
    expect(document.getElementById('titleGrid').innerHTML).toContain(expectedTitleDiv.innerHTML);
  });

  test("Every two question one is multiple choice and the other is ToF", () => {
    var title = "Test Question ";
    var n = 100;
    for(var i = 0; i < n; i++) {
        document.body.appendChild( questionElement(i, (title+i), (i%2)) );
    }

    const questionTitles = document.getElementsByName("isMC");
    
    for(var i = 0; i < n; i++) {    
        expect(questionTitles[i].value === 'true').toEqual((i%2 == 1));
    }
  });
});
