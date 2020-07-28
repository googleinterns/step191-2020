package com.google.sps.data;

public class Room {

  private int id;
  private boolean isActive;
  private String creator;
  private String gameId;

  public Room() {
  }

  public Room(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
  
  public boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(boolean isActive) {
    this.isActive = isActive;
  }
  
  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getGameId() {
    return gameId;
  }

  public void setGameId(String gameId) {
    this.gameId = gameId;
  }
  
}
