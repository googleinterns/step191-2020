package com.google.sps.daos;

public interface UserDao {
  public String joinGameInstance(String idToken, String gameInstanceId);
  public void createIfNotExists(String idToken);

}
