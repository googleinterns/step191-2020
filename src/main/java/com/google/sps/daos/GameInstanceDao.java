package com.google.sps.daos;
import com.google.sps.data.GameInstance;

public interface GameInstanceDao {
  public void createNewGameInstance(String idToken, String gameId);
  public GameInstance getGameInstance(String gameInstanceId);
  public void updateGameInstance(GameInstance update);
}
