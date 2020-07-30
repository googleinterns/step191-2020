package com.google.sps.data;

import com.google.sps.data.Answer;
import java.util.List;

public class Question {

  private String title; // The title of the question
  List<Answer> answers; // A collection of answers

  public Question () {
  }

  /**
   * Creates a new question
   * @param title defines the title or the text that the question will have
   * @param answers its a list of answers
   */
  public Question(String title, List<Answer> answers) {
    this.title = title;
    this.answers = answers;
  }

  /**
   * @return the question title
   */
  public String getTitle() {
    return this.title;  
  }

  /**
   * Sets the title
   * @param title defines the title or the text that the question will have
   */
  public void setTitle(String title) {
    this.title = title;  
  }

  /**
   * @return the answers
   */
  public List<Answer> getAnswers() {
    return this.answers;  
  }


  /**
   * Sets the answers
   * @param answers its a list of answers
   */
  public void setAnswers(List<Answer> answer) {
    this.answers = answers;
  }


  /**
   * Adds one answer
   * @param answer the asnwer that will be added
   */
  public void addAnswer(Answer answer) {
    this.answers.add(answer);
  }

}
