package com.google.sps.data;

public class User {
    
  public static final User null_user = new User();

  private String username;
  private String email;
  private String uid;

  public User() {
      this.username = "N/A";
      this.email = "N/A";
      this.uid = "N/A";
  }

  public User(String username, String email, String uid) {
    this.username = username;
    this.email = email;
    this.uid = uid;
  }
  public String getUid() {
      return this.uid;
  }

  public void setUid(String uid) {
      this.uid = uid;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
      return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
