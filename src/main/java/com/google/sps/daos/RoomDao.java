package com.google.sps.daos;

public interface RoomDao {
  public String createNewRoom(String gameId);
  public String getActualRoom();
  public void startGame();
  public void joinRoom(String roomId, String uId);
}