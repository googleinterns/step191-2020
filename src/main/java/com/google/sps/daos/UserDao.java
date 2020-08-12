package com.google.sps.daos;

public interface UserDao {
  public void joinGameInstance(String idToken, String gameInstanceId);
  public void createIfNotExists(String idToken);

}
