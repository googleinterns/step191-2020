package com.google.sps.data;

import com.google.sps.data.Question;
import java.util.List;

public class Game {

  private String creator;
  private String title;
  List<Question> questions;

  public Game () {
  }

  public Game(String creator, String title, List<Question> questions) {
    this.title = title;  
    this.creator = creator;  
    this.questions = questions;
  }

  public String getTitle() {
    return this.title;  
  }

  public void setTitle(String title) {
    this.title = title;  
  }

  public String getCreator() {
    return this.title;  
  }

  public void setCreator(String creator) {
    this.creator = creator;  
  }

  public List<Question> getQuestions() {
    return this.questions;
  }

  public void setQuestions(List<Question> questions) {
    this.questions = questions;
  }

  public void addQuestion(Question question) {
    this.questions.add(question);
  }
}