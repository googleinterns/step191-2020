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

let cursor = "";

async function initialLoads(){
    await loadComments();
    await checkLogin();
}

/*This function displays log in button if there is no user, a set nickname button if there is a user but the
nickname is missing, and a logout button and comment box if the user is logged in and has nickname*/
async function checkLogin() {
        
    //If the user is logged in but doesn't have a nickname     
    document.getElementById("login").classList.toggle("open");
    document.getElementById("login-btn").addEventListener("click", function (event) {
    location.href="/user";
    });

    let userResponse = await fetch('/user');
    let userJson = await userResponse.json();

    //If the fetch fails (that happens when the user doesn't have a nickname)
    //the rest of this code doesn't run

    //The text and button are set as if the nickname is missing in case the fetch fails
    //So if it doesn't fail that means now the /user contains the Auth object

    document.getElementById("login-txt").innerText="Log in to post a comment:";
    document.getElementById("login-btn").innerText="Log in";
    document.getElementById("logout-btn").addEventListener("click", function (event) {
    location.href=userJson.logoutUrl;
    });
    document.getElementById("login-btn").addEventListener("click", function (event) {
    location.href=userJson.loginUrl;
    });
    
    if(userJson.isLoggedIn&&userJson.nickname!=null) {
        document.getElementById("comments-form").classList.toggle("open");
        document.getElementById("logout").classList.toggle("open");
        document.getElementById("login").classList.toggle("hide");
    } else {
        document.getElementById("comments-form").classList.toggle("hide");
        document.getElementById("logout").classList.toggle("hide");
    }
}

/** Loads existing Comments */
async function loadComments() {
  let response = await fetch('/data');
  let responseJson = await response.json();
  let comments = responseJson.comments;
  cursor = responseJson.cursor;

  if(comments.length == 0) {
      document.getElementById("comments-list").innerHTML="No comments";
  } 
  else {
    let commentSection = document.getElementById("comments-list");
    
    //The newest comments at the top
    comments.forEach(comment => {
        console.log(comment);
        let newCard = createCard(comment);
        commentSection.append(newCard);
    });

  };
}

/** Changes comment page */
async function changePage(type){
    let response;
    if(type == 'back') {
        response = await fetch('/data?cursor='+cursor+'&pageDirection=back');
    }
    else if (type == 'next') {
        response = await fetch('/data?cursor='+cursor+'&pageDirection=next');
    }

    let responseJson = await response.json();
    let comments = responseJson.comments;
    cursor = responseJson.cursor;
    
    if(comments.length != 0) {     
        let commentSection = document.getElementById("comments-list");
        //Empty section
        document.getElementById("comments-list").innerText='';

        //Change page num
        let pageNum = document.getElementById("pageNumber").innerText;
        if(type == 'back') {
            if (1 < pageNum ) {
                pageNum=1;
            }
        } 
        else if (type == 'next') {
            pageNum++;
        }
        document.getElementById("pageNumber").innerText= pageNum;

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
  newCard.append(createCardFooter(comment.date, comment.author));
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
function createCardFooter(date, author){
  let footer = document.createElement('footer');
  footer.classList.add('w3-container');
  let dateText = document.createElement('h5');
  dateText.classList.add('comment-date');
  dateText.innerHTML=date;
  let authorText = document.createElement('h5');
  authorText.classList.add('comment-author');
  authorText.innerHTML=author;
  footer.append(dateText);
  footer.append(authorText);
  return footer;
}


