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
import com.google.sps.data.Room;

public class DatabaseRoomDao implements RoomDao {

  private DatabaseReference roomsDBReference;
  private Firestore firestoreDb;
  private String actualRoom;
  private static Logger log = Logger.getLogger(DatabaseRoomDao.class.getName());

  public DatabaseRoomDao(Firestore firestoreDb) {
    this.firestoreDb = firestoreDb;
  }
  
  @Override
  public Room createNewRoom(String gameId) {

    DocumentReference newRoomRef = firestoreDb.collection("liveRooms").document();

    //Create Room
    String userId = "pamelalozano"; //User id from auth
    Room newRoom = new Room();
    newRoom.setCreator(userId);
    newRoom.setGameId(gameId);

    //Post to db
    newRoomRef.set(newRoom);
    
    //Get id
    newRoom.setId(newRoomRef.getId());

    return newRoom;
  }


  @Override
  public Room getRoom(String uId) {
    DocumentReference docRef = firestoreDb.collection("liveRooms").document(uId);
    ApiFuture<DocumentSnapshot> future = docRef.get();
    Room room = new Room();
    try {
        DocumentSnapshot document = future.get();
        room = document.toObject(Room.class);
    } catch(Exception e) {
        System.out.println(e);
    }
    return room;
  }

  @Override
  public void updateRoom(Room update) {
    ApiFuture<WriteResult> writeResult = firestoreDb.collection("liveRooms").document(update.getId()).set(update, SetOptions.merge());
  }

}
