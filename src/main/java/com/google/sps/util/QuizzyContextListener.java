package com.google.sps.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.google.sps.daos.CounterDao;
import com.google.sps.daos.DatabaseDao;
import com.google.sps.daos.DatabaseRoomDao;
import com.google.sps.daos.RoomDao;

@WebListener("Context Listener")
public class QuizzyContextListener implements ServletContextListener {
  @Override
  public void contextDestroyed(javax.servlet.ServletContextEvent event) {
  }

  @Override
  public void contextInitialized(ServletContextEvent event) {
    // This function is called when the application starts and will safely set a few required
    // context attributes such as the RealtimeDao

    FirebaseDatabase realtimeDb = initializeRealtimeFirebase();

    Firestore firestoreDb = initializeFirestore();

    CounterDao dao = (CounterDao) event.getServletContext().getAttribute("dao");
    if (dao == null) {
      dao = new DatabaseDao(realtimeDb, firestoreDb);
      event.getServletContext().setAttribute("dao", dao);
    }

    RoomDao roomDao = (RoomDao) event.getServletContext().getAttribute("roomDao");
    if (roomDao == null) {
      roomDao = new DatabaseRoomDao(realtimeDb);
      event.getServletContext().setAttribute("roomDao", roomDao);
      
    }

  }

  private FirebaseDatabase initializeRealtimeFirebase() {
    FirebaseDatabase database = null;

    try {
      FirebaseOptions options = new FirebaseOptions.Builder()
          .setCredentials(GoogleCredentials.getApplicationDefault())
          .setDatabaseUrl("https://quizzy-step-2020.firebaseio.com/")
          .build();

      // Initialize DB with options
      FirebaseApp.initializeApp(options);

      // Reference to the whole Realtime DB
      database = FirebaseDatabase.getInstance();
    } catch(IOException ie) {
      ie.printStackTrace();
    }
    return database;
  }

  private Firestore initializeFirestore() {
    Firestore firestoreDb = null;

    try {
      // Building options, include projectID, access credentials
      FirestoreOptions firestoreOptions =
      FirestoreOptions.getDefaultInstance().toBuilder()
          .setProjectId("quizzy-step-2020")
          .setCredentials(GoogleCredentials.getApplicationDefault())
          .build();

      firestoreDb = firestoreOptions.getService();
    } catch(IOException ie) {
      ie.printStackTrace();
    }
    return firestoreDb;    
  }

}
