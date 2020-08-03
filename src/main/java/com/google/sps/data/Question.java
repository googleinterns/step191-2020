package com.google.sps.data;

import com.google.sps.data.Answer;
import java.util.List;

public class Question {

  private String title;
  List<Answer> answers;

  public Question () {
  }

  public Question(String title, List<Answer> answers) {
    this.title = title;
    this.answers = answers;
  }


  public String getTitle() {
    return this.title;  
  }

  public void setTitle(String title) {
    this.title = title;  
  }

  public List<Answer> getAnswers() {
    return this.answers;  
  }

  public void setAnswers(List<Answer> answer) {
    this.answers = answers;
  }

  public void addAnswer(Answer answer) {
    this.answers.add(answer);
  }

}