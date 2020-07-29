// Provides more matchers like `toBeOneOf`
require('jest-extended'); 
// module path is relative to 'src/main/webapp/'
const { addRandomGreeting } = require('script.js');

// `describe` blocks can be used to group related tests together.
describe('addRandomGreeting', () => {
  // `test` is a single unit test
  test('inserts a random greeting', () => {
    // Set up our document body
    document.body.innerHTML =
      '<div id="greeting-container"></div>';

    // Call the function under test
    addRandomGreeting();

    // Verify the result using matchers
    const expectedGreetings = [
      'Hello world!',
      '¡Hola Mundo!',
      '你好，世界！',
      'Bonjour le monde!',
    ];
    const actualGreetingText = document.getElementById('greeting-container').innerText;
    expect(actualGreetingText).toBeOneOf(expectedGreetings);
  });
});
