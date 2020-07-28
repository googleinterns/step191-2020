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

package com.google.sps.servlets;


import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.sps.daos.CounterDao;
import com.google.sps.data.Game;
import com.google.sps.data.Question;
import com.google.sps.data.Answer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/new-game")
public class NewGameServlet extends HttpServlet {

  // Reference to Firestore database
  private Firestore firestoreDb;

  @Override
  public void init() {

    try {

      // Initialization of Firestore database

      // Building options, include projectID, access credentials
      FirestoreOptions firestoreOptions =
      FirestoreOptions.getDefaultInstance().toBuilder()
          .setProjectId("quizzy-step-2020")
          .setCredentials(GoogleCredentials.getApplicationDefault())
          .build();

      // Get database and store the reference in firestoreDb variable
      firestoreDb = firestoreOptions.getService();

    } catch(IOException ie) {
      ie.printStackTrace();
    }

  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws IOException {
      
        

    Answer correct = new Answer(request.getParameter("correct-answer"), true);
    Answer wrong = new Answer(request.getParameter("wrong-answer"), false);
    
    List<Answer> answers = Arrays.asList(correct, wrong);

    Question question = new Question(request.getParameter("question"), answers);

    List<Question> questions = Arrays.asList(question);

    Game currentGame = new Game(request.getParameter("uid"), request.getParameter("title"), questions);
    
    System.out.println(currentGame.getQuestions().get(0).getAnswers().get(0).getTitle());
    // // Get a reference to document alovelace
    // CollectionReference gamesCol = firestoreDb.collection("games");

    // // Prepare data to be inserted
    // Map<String, Object> gameData = new HashMap<>();
    //     gameData.put("creator", uid);
    //     gameData.put("title", gameTitle);

    // DocumentReference gameDoc = gamesCol.document();
    // gameDoc.set(gameData);

    // // Prepare data to be inserted
    // Map<String, Object> questionData = new HashMap<>();
    //     questionData.put("title", question);

    // CollectionReference questionCol = gameDoc.collection("questions");
    // DocumentReference questionDoc = questionCol.document();

    // questionDoc.set(questionData);


    // CollectionReference answerCol = questionDoc.collection("answers");
    // DocumentReference correctAns = answerCol.document();
    // DocumentReference wrongAns = answerCol.document();


    // Map<String, Object> correctData = new HashMap<>();
    //     correctData.put("title", correctAnswer);
    //     correctData.put("correct", true);
    
    // correctAns.set(correctData);

    // Map<String, Object> wrongData = new HashMap<>();
    //     wrongData.put("title", wrongAnswer);
    //     correctData.put("correct", false);
        
    // wrongAns.set(correctData);
    
    
    response.sendRedirect("/create-game.html");
  }
}
