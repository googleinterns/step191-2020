package com.google.sps.daos;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.sps.data.Counter;

public class DatabaseDao implements CounterDao {

  private DatabaseReference counterDBReference;
  private CollectionReference collectionReference;

  public DatabaseDao(FirebaseDatabase realtimeDb, Firestore firestoreDb) {
    counterDBReference = realtimeDb.getReference("/users-counter/counter/value");
    collectionReference = firestoreDb.collection("counter");
  }

  @Override
  public void updateCounter(Counter counter) {
    counterDBReference.setValueAsync(counter.getValue());
  }

  @Override
  public void deleteCounter() {
    counterDBReference.setValueAsync(null);
  }

  @Override
  public void increaseCounter() {
    counterDBReference.runTransaction(new Transaction.Handler() {
      @Override
      public Transaction.Result doTransaction(MutableData mutableData) {
        Integer currentValue = mutableData.getValue(Integer.class);
        if (currentValue == null) {
          mutableData.setValue(1);
        } else {
          mutableData.setValue(currentValue + 1);
        }
    
        return Transaction.success(mutableData);
      }
    
      @Override
      public void onComplete(
          DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
        System.out.println("Transaction completed");
      }
    });
  }

  @Override
  public void storeCounter() {
    counterDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        // ...
        Map<String, Object> data = new HashMap<>();
        data.put("value", dataSnapshot.getValue(Integer.class));
        collectionReference.add(data);
      }
    
      @Override
      public void onCancelled(DatabaseError databaseError) {
        // ...
      }
    });
  }

}
