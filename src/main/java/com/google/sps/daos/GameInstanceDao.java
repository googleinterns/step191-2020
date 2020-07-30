package com.google.sps.daos;
import com.google.sps.data.GameInstance;

public interface GameInstanceDao {
  public GameInstance createNewGameInstance(String gameId);
  public GameInstance getGameInstance(String uId);
  public void updateGameInstance(GameInstance update);
}
