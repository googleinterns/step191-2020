package com.google.sps.daos;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.sps.data.Counter;

public class RealtimeDao implements CounterDao {

  private DatabaseReference counterDBReference;

  public RealtimeDao(FirebaseDatabase database) {
    counterDBReference = database.getReference("/users-counter/counter/value");
  }

  @Override
  public void updateCounter(Counter counter) {
    counterDBReference.setValueAsync(counter);
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
  
}
