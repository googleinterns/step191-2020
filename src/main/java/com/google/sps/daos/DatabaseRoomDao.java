package com.google.sps.daos;

import java.util.HashMap;
import java.util.Map;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firestore.v1.StructuredQuery.CollectionSelector;

public class DatabaseRoomDao implements RoomDao {

  private DatabaseReference roomsDBReference;
  private Firestore firestoreDb;
  private String actualRoom;
  private String gameId;

  public DatabaseRoomDao(Firestore firestoreDb) {
    this.firestoreDb = firestoreDb;
  }
  
  @Override
  public String createNewRoom(String gameId) {

    this.gameId = gameId;
    DocumentReference newRoomRef = firestoreDb.collection("liveRooms").document();

 
    String userId = "pamelalozano";

    Map<String, Object> data = new HashMap<>();
    data.put("creator", userId);
    data.put("gameId", gameId);
    data.put("isActive", false);

    newRoomRef.set(data);

    //Room document is now created
    this.actualRoom = newRoomRef.getId();

   //Updates game document and adds liveRoom id
   Map<String, Object> update = new HashMap<>();
   update.put("liveRoom", this.actualRoom);
   ApiFuture<WriteResult> writeResult = firestoreDb.collection("games").document(this.gameId).set(update, SetOptions.merge());


    return this.actualRoom;
  }
 
 @Override
  public String getActualRoom() {
      return this.actualRoom;
  }

  @Override
  public void joinRoom(String roomId, String uId) {
    Map<String, Object> data = new HashMap<>();
    data.put("UID", uId);

    firestoreDb.collection("liveRooms").document(roomId).collection("members").add(data);
  }

 @Override
 public void startGame(){
    Map<String, Object> update = new HashMap<>();
    update.put("isActive", true);
    ApiFuture<WriteResult> writeResult = firestoreDb.collection("liveRooms").document(this.actualRoom).set(update, SetOptions.merge());
 }
}
