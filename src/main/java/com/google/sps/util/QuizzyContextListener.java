package com.google.sps.util;

import com.google.auth.oauth2.GoogleCredentials;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.google.sps.daos.DatabaseGameInstanceDao;
import com.google.sps.daos.GameInstanceDao;
import com.google.sps.daos.DatabaseGameDao;
import com.google.sps.daos.GameDao;

@WebListener("Context Listener")
public class QuizzyContextListener implements ServletContextListener {
  @Override
  public void contextDestroyed(javax.servlet.ServletContextEvent event) {
  }

  @Override
  public void contextInitialized(ServletContextEvent event) {
    // This function is called when the application starts and will safely set a few required
    // context attributes such as the RealtimeDao

    FirebaseApp firebase = initializeFirebase();
    Firestore firestoreDb = initializeFirestore();

    event.getServletContext().setAttribute("firestoreDb", firestoreDb);
    
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(firebase);

    GameInstanceDao gameInstanceDao = (GameInstanceDao) event.getServletContext().getAttribute("gameInstanceDao");
    if (gameInstanceDao == null) {
      gameInstanceDao = new DatabaseGameInstanceDao(firestoreDb, firebaseAuth);
      event.getServletContext().setAttribute("gameInstanceDao", gameInstanceDao);
    }

    GameDao gameDao = (DatabaseGameDao) event.getServletContext().getAttribute("gameDao");
    if(gameDao == null) {
      gameDao = new DatabaseGameDao(firestoreDb);
      event.getServletContext().setAttribute("gameDao", gameDao);
    }
  }

  private FirebaseApp initializeFirebase() {
    FirebaseApp firebase = null;
    FirebaseOptions options;
    try {
      options = new FirebaseOptions.Builder()
      .setProjectId("quizzy-step-2020")
      .setCredentials(GoogleCredentials.getApplicationDefault())
      .build();
      firebase = FirebaseApp.initializeApp(options);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return firebase;
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
