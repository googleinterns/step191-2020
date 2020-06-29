
package com.google.sps.servlets;


import java.util.*;  
import com.google.gson.Gson;
import java.io.IOException;

public class Comment {
  
  private String subject;
  private String msg;

  public Comment(String s, String m) {
    this.subject=s;
    this.msg=m;
  }
  
  public void setSubject(String s){
      this.subject=s;
  }
  public void setMessage(String m){
      this.msg=m;
  }
  public String getSubject(){
      return this.subject;
  }
  public String getMessage(){
      return this.msg;
  }
};