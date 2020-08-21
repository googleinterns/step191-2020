package com.google.sps.data;

public class Answer {

  private String title;
  private boolean correct;
  private String id;

  public Answer () {
  }

  public Answer( String title, boolean correct) {
    this.title = title;
    this.correct = correct;
  }

  public Answer( String title, boolean correct, String id) {
    this.title = title;
    this.correct = correct;
    this.id = id;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getId() {
    return this.id;  
  }

  public void setId(String id) {
    this.id = id;  
  }

  public boolean isCorrect() {
    return this.correct;
  }

  public void setCorrect(boolean correct) {
    this.correct = correct;
  } 

  public String toString(){//overriding the toString() method  
    return "{title=" + this.title + " id=" + this.id + ", correct="+ this.correct + "}";  
  }  
}