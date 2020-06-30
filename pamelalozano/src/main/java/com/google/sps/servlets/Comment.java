
package com.google.sps.servlets;


import java.util.*;  
import com.google.gson.Gson;
import java.io.IOException;

public class Comment {
  
  private String subject;
  private String msg;
  private Date date;

  public Comment(String s, String m, Date d) {
    this.subject = s;
    this.msg = m;
    this.date = d;
  }
  
  public void setSubject(String s) {
      this.subject = s;
  }
  public void setMessage(String m) {
      this.msg = m;
  }
  public void setDate(Date d) {
      this.date = d;
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