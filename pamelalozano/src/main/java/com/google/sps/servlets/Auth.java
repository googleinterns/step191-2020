package com.google.sps.servlets;

import java.lang.String;  
import java.lang.Boolean;  
import java.util.Date;  
import com.google.gson.Gson;
import java.io.IOException;

public class Auth {
  private final boolean isLoggedIn;
  private final boolean isAdmin;
  private final String logoutUrl;
  private final String loginUrl;
  private final String activeUser;
  private final String nickname;
 
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
  public String getNickname() {
      return this.nickname;
  }

  private Auth(Builder builder) {
    isLoggedIn = builder.isLoggedIn;
    isAdmin = builder.isAdmin;
    activeUser = builder.activeUser;
    loginUrl = builder.loginUrl;
    logoutUrl = builder.logoutUrl;
    nickname = builder.nickname;
  }

  public static class Builder {
    private boolean isLoggedIn=false;
    private boolean isAdmin=false;
    private String logoutUrl;
    private String loginUrl;
    private String activeUser;
    private String nickname;

    public Builder setLoginUrl(String loginUrl) {
      this.loginUrl = loginUrl;
      return this;
    }
    public Builder setLogoutUrl(String logoutUrl) {
      this.logoutUrl = logoutUrl;
      return this;
    }
    public Builder setAdmin(boolean isAdmin) {
      this.isAdmin = isAdmin;
      return this;
    }
    public Builder setIsLoggedIn(boolean isLoggedIn) {
      this.isLoggedIn = isLoggedIn;
      return this;
    }
    public Builder setActiveUser(String activeUser) {
      this.activeUser = activeUser;
      return this;
    }
    public Builder setNickname(String nickname) {
      this.nickname = nickname;
      return this;
    }
    public Auth build() {
      return new Auth(this);
    }
  }
  public String toJson() {
    Gson gson = new Gson();
    String json = gson.toJson(this);
    return json;
  }
}
