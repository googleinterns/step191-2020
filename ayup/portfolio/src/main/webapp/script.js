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
  var fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');

  while(factContainer.innerHTML == fact) {
    fact = facts[Math.floor(Math.random() * facts.length)]; 
  }
  factContainer.innerHTML = fact;
}

