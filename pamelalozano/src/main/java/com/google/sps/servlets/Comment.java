package com.google.sps.servlets;

import java.lang.String;  
import java.util.Date;  
import com.google.gson.Gson;
import java.io.IOException;

public class Comment {
  
  private String subject;
  private String msg;
  private String author;
  private Date date;

  public Comment(String subject, String message, String author, Date date) {
    this.subject = subject;
    this.msg = message;
    this.date = date;
    this.author = author;
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
  public void setAuthor(String author) {
      this.author = author;
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
  public String getAuthor() {
      return this.author;
  }
  public String toJson() {
    Gson gson = new Gson();
    String json = gson.toJson(this);
    return json;
  }
}
