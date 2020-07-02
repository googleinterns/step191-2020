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

/**
 * Fetch the facts from the server
 */
async function getRandomFactUsingAsyncAwait() {
  const response = await fetch('/data');
  const fact = await response.text();

  // New fact container to replace the older one
  const newFactContainer = document.createElement("span");
  newFactContainer.id = "js-fact-container";
  newFactContainer.innerHTML = fact;

  // Replacing the current fact container with a new one
  var a = document.getElementById('js-fact-container');
  a.parentNode.replaceChild(newFactContainer, a);
}

/** Fetches comments from the server and adds them to the DOM. */
function loadComments() {
  fetch('/list-comments').then(response => response.json()).then((comments) => {
    const commentListElement = document.getElementById('comment-list');

    // Deletes all of the current commentss
    while (commentListElement.firstChild) {
      commentListElement.removeChild(commentListElement.lastChild);
    }
    comments.forEach((comment) => {
      commentListElement.appendChild(createCommentElement(comment));
    })
  });
}

/** Creates an element that represents a title, a description, a username and its delete button. */
function createCommentElement(comment) {
  const commentElement = document.createElement('li');
  commentElement.className = 'comment';

  const titleElement = document.createElement('span');
  titleElement.innerText = comment.title;
  titleElement.className = "font-weight-bold";

  const descriptionElement = document.createElement('span');
  descriptionElement.innerText = comment.description;

  const usernameElement = document.createElement('span');
  usernameElement.innerText = "By: " + comment.username;

  const deleteButtonElement = createDeleteButton()
  deleteButtonElement.addEventListener('click', () => {
    deleteComment(comment.id);

    // Loads the comments again
    loadComments()
  });

  commentElement.appendChild(titleElement);
  commentElement.appendChild(document.createElement("br"));
  commentElement.appendChild(descriptionElement);
  commentElement.appendChild(document.createElement("br"));
  commentElement.appendChild(usernameElement);
  commentElement.appendChild(document.createElement("br"));
  commentElement.appendChild(deleteButtonElement);
  commentElement.appendChild(document.createElement("hr"));
  return commentElement;
}

/** Tells the server to delete the comment. */
function deleteComment(commentId) {
  const params = new URLSearchParams();
  params.append('id', commentId);
  fetch('/delete-comment', {method: 'POST', body: params});
}

function createDeleteButton() {
  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.className = "btn btn-danger";
  return deleteButtonElement;
}
