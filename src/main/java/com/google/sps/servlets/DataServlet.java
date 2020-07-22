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

  // Reference to Realtime database
  private DatabaseReference realtimeDb;

  // Counter that will be updated 
  Counter counter = new Counter(0);

  @Override
  public void init() {

    try {
      // Initialization of reference to Realtime database

      // Building options, include credentials and which DB to use
      FirebaseOptions options = new FirebaseOptions.Builder()
          .setCredentials(GoogleCredentials.getApplicationDefault())
          .setDatabaseUrl("https://quizzy-step-2020.firebaseio.com/")
          .build();

      // Initialize DB with options
      FirebaseApp.initializeApp(options);

      // Reference to the whole Realtime DB
      final FirebaseDatabase database = FirebaseDatabase.getInstance();
      
      // Reference to the "counter" object
      realtimeDb = database.getReference("/users-counter/counter");

      // Add a listener to the counter, executed when counter is changed
      realtimeDb.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          counter = dataSnapshot.getValue(Counter.class);
          System.out.println("Listener executed " + counter.value);
        }
      
        @Override
        public void onCancelled(DatabaseError databaseError) {
          System.out.println("The read failed: " + databaseError.getCode());
        }
      });


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

    // Creating a tempCounter just to show that counter is not updated locally, but in the DB
    Counter tempCounter = new Counter(counter.value + 1);
    response.getWriter().println(tempCounter.value);

    // Set in DB with one of the following two options:

    // Update counter asynchronously in Realtime DB with no callback
    //realtimeDb.setValueAsync(tempCounter);

    // Set value and attach a callback for when it is ready, may not work because of AppEngine thread management?
    realtimeDb.setValue(tempCounter, new DatabaseReference.CompletionListener() {
      @Override
      public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError != null) {
          System.out.println("Data could not be saved " + databaseError.getMessage());
        } else {
          System.out.println("Data saved successfully. " + counter.value);
        }
      }
    }
    );


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
