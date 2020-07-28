package com.google.sps.daos;

import java.util.HashMap;
import java.util.Map;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.sps.data.Game;

public class GameDao {
  
  private Firestore firestoreDb;

  public GameDao(Firestore firestoreDb) {
    this.firestoreDb = firestoreDb;
  }

  public void createGame(Game game) {
    
  }

}
