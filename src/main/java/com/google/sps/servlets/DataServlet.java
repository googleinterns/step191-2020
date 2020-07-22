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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.sps.daos.CounterDao;
import com.google.sps.data.Counter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/data")
public class DataServlet extends HttpServlet {

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
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println("<h1>Writting to the DBs</h1>");

    // Writing to Realtime DB
    CounterDao counterDao = (CounterDao) this.getServletContext().getAttribute("dao");

    Counter counter = counterDao.getCounter();
    response.getWriter().println("<p>The counter right now is: " + counter.getValue() + "</p>");
    response.getWriter().println("<p>Reloading the page will increase it by one</p>");
    
    Counter updatedCounter = new Counter(counter.getValue() + 1);
    counterDao.updateCounter(updatedCounter);
    


    // Writing to Firestore

    // Get a reference to document alovelace
    DocumentReference docRef = firestoreDb.collection("messages").document("alovelace");

    // Prepare data to be inserted
    Map<String, Object> data = new HashMap<>();
        data.put("first", "Ada");
        data.put("last", "Lovelace");
        data.put("born", 1815);

    // Asynchronously write data
    ApiFuture<WriteResult> resultFirebase = docRef.set(data);

    // result.get() blocks on response
    try {
      System.out.println("Update time : " + resultFirebase.get().getUpdateTime());
    } catch(Exception e) {
      e.printStackTrace();
    }
    
  }

}
