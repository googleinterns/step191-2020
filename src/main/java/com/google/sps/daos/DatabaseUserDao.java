package com.google.sps.daos;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
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
  public void verifyUserInFirestore(String idToken) {
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
    DocumentReference userInGameInstanceDocRef = firestoreDb.collection("gameInstance").document(gameInstanceId).collection("students").document(userId);

    ApiFuture<DocumentSnapshot> UserInGameInstanceFuture = userInGameInstanceDocRef.get();
    
    try {
      DocumentSnapshot document = UserInGameInstanceFuture.get();
      if (!document.exists()) {
        registerUserInGameInstance(userInGameInstanceDocRef, userId);
      } 
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // Add the active GameInstance to the User's entry in "Users" collection
    ApiFuture<WriteResult> userActiveGameInstanceFuture = firestoreDb.collection("users").document(userId).update("activeGameInstanceId", gameInstanceId);
    // Block until room is created to continue, since it will be redirected to admin game panel
    try {
      userActiveGameInstanceFuture.get();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

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

    userInGameInstanceDocRef.set(docData);
  }
  
}
