package com.google.sps.daos;
import com.google.sps.data.Game;

public interface GameDao {
  public abstract boolean createNewGame(Game newGame);
  public abstract void getGame(String uId);
  public abstract void updateGame(Game update);
}
