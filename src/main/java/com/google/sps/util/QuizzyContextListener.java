package com.google.sps.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.google.sps.daos.CounterDao;
import com.google.sps.daos.RealtimeDao;

@WebListener("Context Listener")
public class QuizzyContextListener implements ServletContextListener {
  @Override
  public void contextDestroyed(javax.servlet.ServletContextEvent event) {
  }

  @Override
  public void contextInitialized(ServletContextEvent event) {
    // This function is called when the application starts and will safely set a few required
    // context attributes such as the RealtimeDao

    FirebaseDatabase database = initializeRealtimeFirebase();

    CounterDao dao = (CounterDao) event.getServletContext().getAttribute("dao");
    if (dao == null) {
      dao = new RealtimeDao(database);
      event.getServletContext().setAttribute("dao", dao);
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
}
