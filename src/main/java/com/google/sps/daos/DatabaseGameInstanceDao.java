package com.google.sps.daos;

import com.google.api.core.ApiFuture;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.DocumentSnapshot;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import com.google.sps.data.GameInstance;

public class DatabaseGameInstanceDao implements GameInstanceDao {

  private FirebaseAuth firebaseAuth;
  private Firestore firestoreDb;

  public DatabaseGameInstanceDao(Firestore firestoreDb, FirebaseAuth firebaseAuth) {
    this.firestoreDb = firestoreDb;
    this.firebaseAuth = firebaseAuth;
  }
  
  @Override
  public void createNewGameInstance(String idToken, String gameId) {
    DocumentReference newGameInstanceRef = firestoreDb.collection("gameInstance").document();

    FirebaseToken decodedToken = null;
    String userId = null;

    try {
      decodedToken = firebaseAuth.verifyIdToken(idToken);
      userId = decodedToken.getUid();
    } catch (FirebaseAuthException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    GameInstance newGameInstance = new GameInstance();
    newGameInstance.setCreator(userId);
    newGameInstance.setGameId(gameId);

    //Post to db
    newGameInstanceRef.set(newGameInstance);
  }


  @Override
  public GameInstance getGameInstance(String gameInstanceId) {
    DocumentReference docRef = firestoreDb.collection("gameInstance").document(gameInstanceId);
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
    firestoreDb.collection("gameInstance").document(update.getId()).set(update, SetOptions.merge());
  }

}
