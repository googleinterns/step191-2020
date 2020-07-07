
package com.google.sps.servlets;


import java.util.*;  
import com.google.gson.Gson;
import java.io.IOException;

public class Auth {
  
  private boolean isLoggedIn;
  private boolean isAdmin;
  private String logoutUrl;
  private String loginUrl;
  private String activeUser;

  public Auth(boolean isLoggedIn) {
    this.isLoggedIn = isLoggedIn;
    this.isAdmin = false;
    this.logoutUrl = null;
    this.loginUrl = null;
    this.activeUser = null;
  }
  
  public void setLoginUrl(String loginUrl) {
      this.loginUrl = loginUrl;
      this.logoutUrl = null;
      this.activeUser = null;
  }
  public void setLogoutUrl(String logoutUrl) {
      this.logoutUrl = logoutUrl;
      this.loginUrl = null;
  }
  public void setAdmin(boolean isAdmin) {
      this.isAdmin = isAdmin;
  }
  public void setIsLoggedIn(boolean isLoggedIn) {
      this.isLoggedIn = isLoggedIn;
  }
  public void setActiveUser(String activeUser) {
      this.activeUser = activeUser;
  }
  public String getLoginUrl() {
      return this.loginUrl;
  }
  public String getLogoutUrl() {
      return this.logoutUrl;
  }
  public String getUserEmail() {
      return this.activeUser;
  }
  public Boolean isAuthenticated() {
      return this.isLoggedIn;
  }
  public Boolean isAdministrator() {
      return this.isAdmin;
  }
  public String toJson() {
    Gson gson = new Gson();
    String json = gson.toJson(this);
    return json;
  }
};
