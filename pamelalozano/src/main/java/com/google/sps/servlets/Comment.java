
package com.google.sps.servlets;


import java.util.*;  
import com.google.gson.Gson;
import java.io.IOException;

public class Comment {
  
  private String subject;
  private String msg;
  private Date date;

  public Comment(String subject, String message, Date date) {
    this.subject = subject;
    this.msg = message;
    this.date = date;
  }
  
  public void setSubject(String subject) {
      this.subject = subject;
  }
  public void setMessage(String message) {
      this.msg = message;
  }
  public void setDate(Date date) {
      this.date = date;
  }
  public String getSubject() {
      return this.subject;
  }
  public String getMessage() {
      return this.msg;
  }
  public Date getDate() {
      return this.date;
  }
};
