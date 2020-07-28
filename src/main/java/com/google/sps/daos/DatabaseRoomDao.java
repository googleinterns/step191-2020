package com.google.sps.daos;

import java.util.HashMap;
import java.util.Map;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firestore.v1.StructuredQuery.CollectionSelector;

public class DatabaseRoomDao implements RoomDao {

  private FirebaseAuth firebaseAuth;
  private DatabaseReference roomsDBReference;
  private Firestore firestoreDb;

  public DatabaseRoomDao(Firestore firestoreDb, FirebaseAuth firebaseAuth) {
    this.firestoreDb = firestoreDb;
    this.firebaseAuth = firebaseAuth;
  }

  @Override
  public String createNewRoom(String idToken, String gameId) {
    DocumentReference newRoomRef = firestoreDb.collection("liveRooms").document();

    FirebaseToken decodedToken = null;
    String uid = null;

    try {
      decodedToken = firebaseAuth.verifyIdToken(idToken);
      uid = decodedToken.getUid();
    } catch (FirebaseAuthException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    Map<String, Object> data = new HashMap<>();
    data.put("creator", uid);
    data.put("gameId", gameId);
    data.put("timestamp", new java.util.Date());
    
    newRoomRef.set(data);

    return newRoomRef.getId();
  }

  @Override
  public void joinRoom(String roomId, String uId) {
    Map<String, Object> data = new HashMap<>();
    data.put("UID", uId);

    firestoreDb.collection("liveRooms").document(roomId).collection("members").add(data);
  }

}