package com.google.sps.daos;

public interface RoomDao {
  public String createNewRoom(String idToken, String gameId);

  public void joinRoom(String roomId, String uId);
}