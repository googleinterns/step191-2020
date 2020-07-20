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

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private Firestore db;

  @Override
  public void init() {

    try {
      FirebaseOptions options = new FirebaseOptions.Builder()
    .setCredentials(GoogleCredentials.getApplicationDefault())
    .setDatabaseUrl("https://quizzy-step-2020.firebaseio.com/")
    .build();

      FirebaseApp.initializeApp(options);
      System.out.println("HIIII");

      // [START fs_initialize_project_id]
      FirestoreOptions firestoreOptions =
      FirestoreOptions.getDefaultInstance().toBuilder()
          .setProjectId("quizy-step-2020")
          .setCredentials(GoogleCredentials.getApplicationDefault())
          .build();
      Firestore db = firestoreOptions.getService();
      // [END fs_initialize_project_id]
      this.db = db;

    } catch(IOException ie) {
      ie.printStackTrace();
      System.out.println("FAIL");
    }

    
    
    
    // FirebaseOptions options = new FirebaseOptions.Builder()
    //     .setServiceAccount(config.getServletContext().getResourceAsStream(credential))
    //     .setDatabaseUrl(databaseUrl)
    //     .build();
    // FirebaseApp.initializeApp(options);
    // firebase = FirebaseDatabase.getInstance().getReference();


  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println("<h1>Hello world!</h1>");

    DocumentReference docRef = db.collection("messages").document("alovelace");
    Map<String, Object> data = new HashMap<>();
        data.put("first", "Ada");
        data.put("last", "Lovelace");
        data.put("born", 1815);

    //asynchronously write data
    ApiFuture<WriteResult> result = docRef.set(data);
    // ...
    // result.get() blocks on response
    try {
      System.out.println("Update time : " + result.get().getUpdateTime());
    } catch(Exception e) {
      e.printStackTrace();
    }
    

  }
}
