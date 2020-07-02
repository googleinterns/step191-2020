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

/** Toggles the project's section */
document.getElementById("projects").addEventListener("click", function (event) {
        location.href="#projects";
        event.target.closest('.project').classList.toggle("open");
});

/** Loads existing Comments */
async function loadComments() {
  let response = await fetch('/data');
  let comments = await response.json();
  if(comments.length == 0) {
      document.getElementById("comments-section").innerHTML="No comments";
  } 
  else {
    let commentSection = document.getElementById("comments-section");
    
    //The newest comments at the top
    comments.forEach(comment => {
        let newCard = createCard(comment);
        commentSection.append(newCard);
    });

  };
}

/** Creates a card element containing the comment. */
function createCard(comment) {
  let newCard = document.createElement('div');
  newCard.classList.add('w3-card-4');
  newCard.append(createCardHeader(comment.subject));
  newCard.append(createCardComment(comment.msg));
  newCard.append(createCardFooter(comment.date));
  return newCard;
}

/** Creates the comment's header. */
function createCardHeader(text){
  let header = document.createElement('header');
  header.classList.add('w3-container');
  let headerText = document.createElement('h3');
  headerText.innerHTML=text;
  header.append(headerText);
  return header;
}

/** Creates the comment's text. */
function createCardComment(text){
  let comment = document.createElement('div');
  comment.classList.add('w3-container');
  let commentText = document.createElement('p');
  commentText.innerHTML=text;
  comment.append(commentText);
  return comment;
}

/** Creates the comment's footer with date. */
function createCardFooter(text){
  let footer = document.createElement('footer');
  footer.classList.add('w3-container');
  let footerText = document.createElement('h5');
  footerText.innerHTML=text;
  footer.append(footerText);
  return footer;
}


