package com.google.sps.daos;

import java.util.HashMap;
import java.util.Map;
import java.util.List; 
import java.util.logging.Logger;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firestore.v1.StructuredQuery.CollectionSelector;
import java.io.IOException;

import com.google.sps.data.Member;
import com.google.sps.data.Game;
import com.google.sps.data.Question;
import java.util.List;

public class DatabaseGameDao implements GameDao {

  private Firestore firestoreDb;
  private static Logger log = Logger.getLogger(DatabaseGameDao.class.getName());

  public DatabaseGameDao(Firestore firestoreDb) {
    this.firestoreDb = firestoreDb;
  }
  
  @Override
  public boolean createNewGame(Game newGame) {
    DocumentReference newGameRef = firestoreDb.collection("games").document();

    List<Question> questions = newGame.questions();

    newGameRef.set(newGame.gameData());

    try {
      CollectionReference questionRef = newGameRef.collection("questions");
      int i = 0;
      for (Question q : questions) {
        questionRef.document("question" + i).set(q);
        i++;
      }
    } catch (Exception e) {
      System.out.println(e);
    }

    return true;
  }
}
