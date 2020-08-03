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
import com.google.sps.data.Game;

public class DatabaseGameDao implements GameDao {

  private Firestore firestoreDb;
  private static Logger log = Logger.getLogger(DatabaseGameDao.class.getName());

  public DatabaseGameDao(Firestore firestoreDb) {
    this.firestoreDb = firestoreDb;
  }
  
  @Override
  public boolean createNewGame(Game newGame) {

    DocumentReference newGameRef = firestoreDb.collection("games").document();

    // //Create Game
    // String userId = newGame.creator(); //User id from auth
    // GameInstance newGameInstance = new GameInstance();
    // newGameInstance.setCreator(userId);
    // newGameInstance.setGameId(gameId);


    // Prepare data to be inserted
    Map<String, Object> gameData = new HashMap<>();
        gameData.put("creator", newGame.creator());
        gameData.put("title", newGame.title());

    //Post to db
    newGameRef.set(gameData);
    

    return true;
  }


  @Override
  public void getGame(String uId) {
    // DocumentReference docRef = firestoreDb.collection("gameInstance").document(uId);
    // ApiFuture<DocumentSnapshot> future = docRef.get();
    // GameInstance gameInstance = new GameInstance();
    // try {
    //     DocumentSnapshot document = future.get();
    //     gameInstance = document.toObject(GameInstance.class);

    //     //Id is not a field in the db document so we add it manually
    //     gameInstance.setId(document.getId());

    // } catch(Exception e) {
    //     System.out.println(e);
    // }
    // return gameInstance;
  }

  @Override
  public void updateGame(Game update) {
    // ApiFuture<WriteResult> writeResult = firestoreDb.collection("gameInstance").document(update.getId()).set(update, SetOptions.merge());
  }

}
