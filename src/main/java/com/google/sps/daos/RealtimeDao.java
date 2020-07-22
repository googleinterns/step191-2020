package com.google.sps.daos;

import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.sps.data.Counter;
import java.io.IOException;

public class RealtimeDao implements CounterDao {
  
  Counter counter;

  private DatabaseReference realtimeDb;

  public RealtimeDao() {

    try {
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

    } catch(IOException ie) {
      ie.printStackTrace();
    }
    
  }

  @Override
  public Counter getCounter() {
    return counter;
  }

  @Override
  public void updateCounter(Counter counter) {
    realtimeDb.setValueAsync(counter);
  }

  @Override
  public void deleteCounter() {
    realtimeDb.setValueAsync(null);
  }

}
