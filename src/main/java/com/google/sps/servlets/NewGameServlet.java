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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
      // Recieves the inputs from the form
      String gameTitle = request.getParameter("title");
      String question = request.getParameter("question");
      String correctAnswer = request.getParameter("correct-answer");
      String wrongAnswer = request.getParameter("wrong-answer");
      String uid = request.getParameter("uid");
        
      System.out.println(uid);

    // Get a reference to document alovelace
    CollectionReference colRef = firestoreDb.collection("games");

    // Prepare data to be inserted
    Map<String, Object> data = new HashMap<>();
        data.put("creator", uid);

    DocumentReference docRef = colRef.document();
    // Asynchronously write data
    ApiFuture<WriteResult> resultFirebase = docRef.set(data);

    // result.get() blocks on response
    try {
      System.out.println("Update time : " + resultFirebase.get().getUpdateTime());
    } catch(Exception e) {
      e.printStackTrace();
    }
    
      response.sendRedirect("/create-game.html");
  }
}
