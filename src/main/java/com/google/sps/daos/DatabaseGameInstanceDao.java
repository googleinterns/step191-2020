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

import com.google.sps.data.Member;
import com.google.sps.data.GameInstance;

public class DatabaseGameInstanceDao implements GameInstanceDao {

  private Firestore firestoreDb;
  private static Logger log = Logger.getLogger(DatabaseGameInstanceDao.class.getName());

  public DatabaseGameInstanceDao(Firestore firestoreDb) {
    this.firestoreDb = firestoreDb;
  }
  
  @Override
  public GameInstance createNewGameInstance(String gameId) {

    DocumentReference newRoomRef = firestoreDb.collection("gameInstance").document();

    //Create Room
    String userId = "pamelalozano"; //User id from auth
    GameInstance newGameInstance = new GameInstance();
    newGameInstance.setCreator(userId);
    newGameInstance.setGameId(gameId);

    //Post to db
    newRoomRef.set(newGameInstance);
    
    //Get id
    newGameInstance.setId(newRoomRef.getId());

    return newGameInstance;
  }


  @Override
  public GameInstance getGameInstance(String uId) {
          System.out.println(uId);
    DocumentReference docRef = firestoreDb.collection("gameInstance").document(uId);
    ApiFuture<DocumentSnapshot> future = docRef.get();
    GameInstance gameInstance = new GameInstance();
    try {
        DocumentSnapshot document = future.get();
        gameInstance = document.toObject(GameInstance.class);

        //Id is not a field in the db document so we add it manually
        gameInstance.setId(document.getId());

    } catch(Exception e) {
        System.out.println(e);
    }
    return gameInstance;
  }

  @Override
  public void updateGameInstance(GameInstance update) {
    ApiFuture<WriteResult> writeResult = firestoreDb.collection("gameInstance").document(update.getId()).set(update, SetOptions.merge());
  }

}
