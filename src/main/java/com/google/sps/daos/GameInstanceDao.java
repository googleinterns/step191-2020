package com.google.sps.daos;
import com.google.sps.data.Game;
import com.google.sps.data.GameInstance;

public interface GameInstanceDao {
  public String createNewGameInstance(String idToken, String gameId, Game game);
  public GameInstance getGameInstance(String gameInstanceId);
  public void updateGameInstance(GameInstance update);
  public boolean getAnswer(String gameInstanceId, String student);
}
