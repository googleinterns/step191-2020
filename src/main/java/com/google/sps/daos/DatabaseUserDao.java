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
        System.out.println("Document data: " + document.getData());
      } 
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
  
}
