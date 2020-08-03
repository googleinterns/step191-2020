package com.google.sps.data;

import com.google.sps.data.Question;
import java.util.List;

public class Game {

  private String creator;
  private String title;
  private int numberOfQuestions;

  public Game () {
  }

  public Game(String creator, String title, int numberOfQuestions) {
    this.title = title;  
    this.creator = creator;  
    this.numberOfQuestions = numberOfQuestions;
  }

  public String getTitle() {
    return this.title;  
  }

  public void setTitle(String title) {
    this.title = title;  
  }

  public String getCreator() {
    return this.creator;  
  }

  public void setCreator(String creator) {
    this.creator = creator;  
  }

  public int getNumberOfQuestions() {
    return this.numberOfQuestions;
  }

  public void setNumberOfQuestions(int numberOfQuestions) {
    this.numberOfQuestions = numberOfQuestions;
  }

}