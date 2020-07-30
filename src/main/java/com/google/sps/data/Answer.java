package com.google.sps.data;

public class Answer {

  private String title; // The title of the answer
  private boolean correct; // If the answer is correct or not

  public Answer () {
  }

  /**
   * Creates a new answer
   * @param title defines the title or the text that the answer will have
   * @param correct it will define if the answer is correct
   */
  public Answer( String title, boolean correct) {
    this.title = title;
    this.correct = correct;
  }

  /**
   * @return the answer title
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Sets the title
   * @param title defines the title or the text that the answer will have
   */
  public void setTitle(String title) {
    this.title = title;
  }
  
  /**
   * @return if the answer is correctl
   */
  public boolean isCorrect() {
    return this.correct;
  }

  /**
   * Sets if it's correct or not
   * @param correct it will define if the answer is correct
   */
  public void setCorrect(boolean correct) {
    this.correct = correct;
  } 
}
