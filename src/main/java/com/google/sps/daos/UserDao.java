package com.google.sps.daos;

public interface UserDao {
  public void verifyUserInFirestore(String idToken);
}
