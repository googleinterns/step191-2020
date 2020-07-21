package com.google.sps.servlets;


import java.util.*;  
import com.google.gson.Gson;
import java.io.IOException;

public class CommentsPage {
  
  private ArrayList<Comment> comments;
  private String cursor;

  public CommentsPage(ArrayList<Comment> comments, String cursor) {
    this.cursor = cursor;
    this.comments = comments;
  }

  public String toJson() {
    Gson gson = new Gson();
    String json = gson.toJson(this);
    return json;
  }

}
  