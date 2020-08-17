package com.google.sps.daos;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

public class DatabaseUserDao implements UserDao {

  private FirebaseAuth firebaseAuth;
  private Firestore firestoreDb;

  public DatabaseUserDao(Firestore firestoreDb, FirebaseAuth firebaseAuth) {
    this.firestoreDb = firestoreDb;
    this.firebaseAuth = firebaseAuth;
  }

  @Override

  public void createIfNotExists(String idToken) {

    FirebaseToken decodedToken = null;
    String userId = null;

    try {
      decodedToken = firebaseAuth.verifyIdToken(idToken);
      userId = decodedToken.getUid();
    } catch (FirebaseAuthException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    DocumentReference userDocRef = firestoreDb.collection("users").document(userId);

    ApiFuture<DocumentSnapshot> future = userDocRef.get();
    
    try {
      DocumentSnapshot document = future.get();
      if (!document.exists()) {
        initUser(userId);
      } 
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }

  @Override
  public void joinGameInstance(String idToken, String gameInstanceId) {
    FirebaseToken decodedToken = null;
    String userId = null;

    try {
      decodedToken = firebaseAuth.verifyIdToken(idToken);
      userId = decodedToken.getUid();
    } catch (FirebaseAuthException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // Add to GameInstance's members list
    DocumentReference gameInstanceDocRef = firestoreDb.collection("gameInstance").document(gameInstanceId);

    DocumentReference userInGameInstanceDocRef = gameInstanceDocRef.collection("students").document(userId);

    ApiFuture<DocumentSnapshot> UserInGameInstanceFuture = userInGameInstanceDocRef.get();
    
    try {
      DocumentSnapshot document = UserInGameInstanceFuture.get();
      if (!document.exists()) {
        registerUserInGameInstance(userInGameInstanceDocRef, userId);
        addOneToMembersCounter(gameInstanceDocRef);
      } 
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // Add the active GameInstance to the User's entry in "Users" collection
    firestoreDb.collection("users").document(userId).update("activeGameInstanceId", gameInstanceId);

  }


  private void initUser(String userId) {
    Map<String, Object> docData = new HashMap<>();
    docData.put("activeGameInstanceId", "");
    docData.put("userId", userId);
    
    firestoreDb.collection("users").document(userId).set(docData);
  }


  private void registerUserInGameInstance(DocumentReference userInGameInstanceDocRef, String userId) {
    Map<String, Object> docData = new HashMap<>();
    docData.put("points", 0);
    docData.put("numberAnswered", 0);
    docData.put("numberCorrect", 0);
    docData.put("numberWrong", 0);

    userInGameInstanceDocRef.set(docData);
  }

  private void addOneToMembersCounter(DocumentReference gameInstanceDocRef) {
    firestoreDb.runTransaction(transaction -> {
      // retrieve document and increment population field
      DocumentSnapshot snapshot = transaction.get(gameInstanceDocRef).get();
      long oldNumberOfMembers = snapshot.getLong("numberOfMembers");
      transaction.update(gameInstanceDocRef, "numberOfMembers", oldNumberOfMembers + 1);
      return null;
    });
  }

  
}
