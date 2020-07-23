package com.google.sps.data;

public class User {

  /**
   * A User object with null values
   */
  public static final User null_user = new User();

  private String username;
  private String email;
  private String uid;

  /**
   * Creates the User class with null values
   */
  public User() {
      this.username = "N/A";
      this.email = "N/A";
      this.uid = "N/A";
  }

  /**
   * Creates the User class with null values
   * @param username the username
   * @param email the user email
   * @param uid the user id
   */
  public User(String username, String email, String uid) {
    this.username = username;
    this.email = email;
    this.uid = uid;
  }

  /**
   * Gets the user id
   * @return user id
   */
  public String getUid() {
      return this.uid;
  }

  /**
   * Sets the user id
   * @param uid
   */
  public void setUid(String uid) {
      this.uid = uid;
  }

  /**
   * Gets the username
   * @return username
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * Sets the username
   * @param username
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Gets the user email
   * @return user email
   */
  public String getEmail() {
      return this.email;
  }

  /**
   * Sets the email
   * @param email
   */
  public void setEmail(String email) {
    this.email = email;
  }
}
