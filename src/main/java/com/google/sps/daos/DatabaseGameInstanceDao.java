package com.google.sps.daos;

import com.google.api.core.ApiFuture;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.DocumentSnapshot;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.sps.data.Game;
import com.google.sps.data.GameInstance;

public class DatabaseGameInstanceDao implements GameInstanceDao {

  private FirebaseAuth firebaseAuth;
  private Firestore firestoreDb;

  public DatabaseGameInstanceDao(Firestore firestoreDb, FirebaseAuth firebaseAuth) {
    this.firestoreDb = firestoreDb;
    this.firebaseAuth = firebaseAuth;
  }

  @Override
  public String createNewGameInstance(String idToken, String gameId) {
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
    newGameInstance.setNumberOfMembers(0);
    newGameInstance.setId(newGameInstanceRef.getId());

    // Post to db
    newGameInstanceRef.set(newGameInstance);

    // Update the User's entry with game he just started
    firestoreDb.collection("users").document(userId).update("activeGameInstanceId", newGameInstanceRef.getId());

    return newGameInstanceRef.getId();
  }

  private void initQuestionsInGameInstance() {
    // first we need to query the game questions

    // Need to get the game that is being used in the gameInstance
    // Game game = theGame

    // Then add the subcollection for the gameInstance with all the questions to be answered

  }

  @Override
  public GameInstance getGameInstance(String gameInstanceId) {
    DocumentReference docRef = firestoreDb.collection("gameInstance").document(gameInstanceId);
    ApiFuture<DocumentSnapshot> future = docRef.get();
    GameInstance gameInstance = new GameInstance();
    try {
      DocumentSnapshot document = future.get();
      gameInstance = document.toObject(GameInstance.class);

      // Id is not a field in the db document so we add it manually
      gameInstance.setId(document.getId());

    } catch (Exception e) {
      System.out.println(e);
    }
    return gameInstance;
  }

  @Override
  public void updateGameInstance(GameInstance update) {
    firestoreDb.collection("gameInstance").document(update.getId()).set(update, SetOptions.merge());
  }

  @Override
  public boolean getAnswer(String gameInstance, String student) {
    // To get current question
    GameInstance actualInstance = this.getGameInstance(gameInstance);
    DocumentReference gameInstanceDocRef = firestoreDb.collection("gameInstance").document(gameInstance);
    DocumentReference answerInStudentDocRef = gameInstanceDocRef.collection("students").document(student)
        .collection("questions").document(actualInstance.getCurrentQuestion());
    ApiFuture<DocumentSnapshot> questionAnswerFuture = answerInStudentDocRef.get();
    try {
      DocumentSnapshot document = questionAnswerFuture.get();
      return Boolean.parseBoolean(document.get("correct").toString());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return false;
  }

}
