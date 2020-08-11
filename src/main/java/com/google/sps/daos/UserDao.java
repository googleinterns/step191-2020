package com.google.sps.daos;

public interface UserDao {
  public void createIfNotExists(String idToken);
}
