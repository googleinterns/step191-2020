package com.google.sps.daos;
import com.google.sps.data.Game;

public interface GameDao {
  public abstract boolean createNewGame(Game newGame);
  public abstract Game getGame(String id);
  public abstract String getQuestionId(String question, String gameId, String currentQuestionId);
}
