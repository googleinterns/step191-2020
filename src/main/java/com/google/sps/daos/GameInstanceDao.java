package com.google.sps.daos;
import com.google.sps.data.GameInstance;

public interface GameInstanceDao {
  public GameInstance createNewRoom(String gameId);
  public GameInstance getRoom(String uId);
  public void updateRoom(GameInstance update);
}
