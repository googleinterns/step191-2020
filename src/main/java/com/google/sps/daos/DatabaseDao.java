package com.google.sps.daos;

import java.util.HashMap;
import java.util.Map;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.sps.data.Counter;

public class DatabaseDao implements CounterDao {

  private Firestore firestoreDb;

  public DatabaseDao(Firestore firestoreDb) {
    this.firestoreDb = firestoreDb;
  }

  @Override
  public void updateCounter(Counter counter) {

  }

  @Override
  public void deleteCounter() {
    DocumentReference liveCounterReference = firestoreDb.collection("counterLive").document("counter");
    firestoreDb.runTransaction(transaction -> {
      transaction.update(liveCounterReference, "value", 0);
      return null;
    });
  }

  @Override
  public void increaseCounter() {
    DocumentReference liveCounterReference = firestoreDb.collection("liveCounter").document("counter");

    firestoreDb.runTransaction(transaction -> {
      DocumentSnapshot snapshot = transaction.get(liveCounterReference).get();

      if (snapshot.exists()) {
        long oldCounter = snapshot.getLong("value");
        transaction.update(liveCounterReference, "value", oldCounter + 1);
      } else {
        Map<String, Object> docData = new HashMap<>();
        docData.put("value", 1);
        transaction.set(liveCounterReference, docData);
      }

      return null;
    });
  }

  @Override
  public void storeCounter() {

    DocumentReference liveCounterReference = firestoreDb.collection("liveCounter").document("counter");

    firestoreDb.runTransaction(transaction -> {
      DocumentSnapshot snapshot = transaction.get(liveCounterReference).get();

      if (snapshot.exists()) {
        firestoreDb.collection("counterHistory").add(snapshot.getData());
        transaction.update(liveCounterReference, "value", 0);
      } 

      return null;
    });
  }

}
