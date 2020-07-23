package com.google.sps.daos;

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

  Counter counter = null;
  
  private DatabaseReference counterDBReference;

  public RealtimeDao(FirebaseDatabase database) {

    counterDBReference = database.getReference("/users-counter/counter");
    
  }

  @Override
  public Counter getCounter() {
    // Query the counter value from the DB
    counterDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        System.out.println("First");
        counter = dataSnapshot.getValue(Counter.class);
      }
    
      @Override
      public void onCancelled(DatabaseError databaseError) {
        // ...
      }
    });
    System.out.println("Second");
    return counter;
  }

  @Override
  public void updateCounter(Counter counter) {
    counterDBReference.setValueAsync(counter);
  }

  @Override
  public void deleteCounter() {
    counterDBReference.setValueAsync(null);
  }

}
