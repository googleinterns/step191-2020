package com.google.sps.daos;

import java.util.HashMap;
import java.util.Map;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firestore.v1.StructuredQuery.CollectionSelector;

public class DatabaseRoomDao implements RoomDao {

  private DatabaseReference roomsDBReference;
  private Firestore firestoreDb;

  public DatabaseRoomDao(Firestore firestoreDb) {
    this.firestoreDb = firestoreDb;
  }
  
  @Override
  public String createNewRoom() {
    DocumentReference newRoomRef = firestoreDb.collection("liveRooms").document();

    Map<String, Object> data = new HashMap<>();
    data.put("Creator", "Armando");
    
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