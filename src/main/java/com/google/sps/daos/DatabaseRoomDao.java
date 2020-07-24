package com.google.sps.daos;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseRoomDao implements RoomDao {

  private DatabaseReference roomsDBReference;

  public DatabaseRoomDao(FirebaseDatabase realtimeDb) {
    roomsDBReference = realtimeDb.getReference("/liveRooms");
  }
  
  @Override
  public String createNewRoom() {
    Map<String, Object> data = new HashMap<>();
    data.put("Creator", "Armando");
    roomsDBReference.setValueAsync(data);

    DatabaseReference newRoomRef = roomsDBReference.push();
    newRoomRef.setValueAsync(data);
    System.out.println("Done");
    return newRoomRef.getKey();
  }

}