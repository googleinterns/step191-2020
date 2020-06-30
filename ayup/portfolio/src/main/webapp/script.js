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
 * Adds a random fact to the page.
 */
function addRandomFact() {
  const facts = [
    "🎮 I develop Nintendo GameBoy Advance ROMs in my spare time",
    "📹 I was a YouTube star back in the days <a href='https://youtube.com/thefredo1000'>Check out my channel</a>",
    "🤓 I am a huge Star Wars fan",
    "🎵 I love Weezer, they are my favorite band",
    "🎸 I can play the bass (Still a rookie)",
    "🐕 I have three dogs!"];
  
  // Pick a random fact.
  var newFact = facts[Math.floor(Math.random() * facts.length)];

  // Get the current fact
  const factContainer = document.getElementById('js-fact-container');
  const currentFact = factContainer.innerHTML;

  // While the current fact it's the same as the new fact we try with another fact
  while(currentFact == newFact) {
    newFact = facts[Math.floor(Math.random() * facts.length)]; 
  }
  factContainer.innerHTML = newFact;
}


async function getRandomFactUsingAsyncAwait() {
  const response = await fetch('/data');
  const fact = await response.text();
  document.getElementById('js-fact-container').innerHTML = fact;
}

function createParaElement(text) {
  const liElement = document.createElement('p');
  liElement.innerHTML = text;
  return liElement;
}
function getServerFacts() {
  fetch('/data').then(response => response.json()).then((serverFacts) => {
    // stats is an object, not a string, so we have to
    // reference its fields to create HTML content

    const statsListElement = document.getElementById('js-fact-container');
    statsListElement.innerHTML = '';
    statsListElement.appendChild( createParaElement('Comment 1: ' + serverFacts.comment0));
    statsListElement.appendChild( createParaElement('Comment 2: ' + serverFacts.comment1));
    statsListElement.appendChild( createParaElement('Comment 3: ' + serverFacts.comment2));
    statsListElement.appendChild( createParaElement('Comment 4: ' + serverFacts.comment3));
  });
}
