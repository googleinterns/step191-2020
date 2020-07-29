package com.google.sps.daos;
import com.google.sps.data.Room;

public interface RoomDao {
  public Room createNewRoom(String gameId);
  public Room getRoom(String uId);
  public void updateRoom(Room update);
}
