package com.google.sps.daos;

public interface RoomDao {
  public String createNewRoom();

  public void joinRoom(String roomId, String uId);
}