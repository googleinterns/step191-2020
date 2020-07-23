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
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/login")
public class LogInServlet extends HttpServlet {

  // Reference to Firestore database
  private Firestore firestoreDb;

  // Reference to Realtime database
  private DatabaseReference realtimeDb;
  private FirebaseAuth auth;
  private String email;
  

  private FirebaseOptions options;
  @Override
  public void init() {

    try {
      // Initialization of reference to Realtime database

      // Building options, include credentials and which DB to use
      this.options = new FirebaseOptions.Builder()
          .setCredentials(GoogleCredentials.getApplicationDefault())
          .setDatabaseUrl("https://quizzy-step-2020.firebaseio.com/")
          .build();

      // Initialize DB with options
      FirebaseApp.initializeApp(this.options);

      // Reference to the whole Realtime DB
      final FirebaseDatabase database = FirebaseDatabase.getInstance();
      this.auth = FirebaseAuth.getInstance(FirebaseApp.getInstance());

      // Reference to the "users" object
      realtimeDb = database.getReference("/users");

      

      // Initialization of Firestore database

      // Building options, include projectID, access credentials
      FirestoreOptions firestoreOptions =
      FirestoreOptions.getDefaultInstance().toBuilder()
          .setProjectId("quizzy-step-2020")
          .setCredentials(GoogleCredentials.getApplicationDefault())
          .build();

      // Get database and store the reference in firestoreDb variable
      firestoreDb = firestoreOptions.getService();
      System.out.println("Should I stay or should I go!?!?!?!?");
    } catch(IOException ie) {
      System.out.println("ERRor!?!?!?!?");
      ie.printStackTrace();
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    String email = "rodrigo.casale.ayup@gmail.com";
    try {

        FirebaseApp.initializeApp(this.options);
        UserRecord userRecord = this.auth.getUserByEmail(email);
        // See the UserRecord reference doc for the contents of userRecord.
        System.out.println("Successfully fetched user data: " + userRecord.getEmail());
        response.getWriter().println("<h1>Welcome <b>" + email + " </b>!!!</h1>");
    } catch (Exception e) {
        System.out.println(e);
        response.getWriter().println("<h1>Error signing in...</h1>");
    }


  }

}
