package com.google.sps.daos;

public interface UserDao {
  
  // Register a user in a gameInstance and return its newly assigned animal alias
  // If user is already registered, just return the animal alias that was already assigned before
  public String joinGameInstance(String idToken, String gameInstanceId);

  public void createIfNotExists(String idToken);

}
