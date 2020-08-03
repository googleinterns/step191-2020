package com.google.sps.data;

public class Answer {

  private String title;
  private boolean correct;

  public Answer () {
  }

  public Answer( String title, boolean correct) {
    this.title = title;
    this.correct = correct;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean isCorrect() {
    return this.correct;
  }

  public void setCorrect(boolean correct) {
    this.correct = correct;
  } 
}