package com.google.sps.data;

import com.google.sps.data.Question;
import java.util.List;

public class Game {

  private String creator;
  private String title;
  List<Question> questions;

  public Game () {
  }
  /**
   * Constructs a new game
   * @param creator the id of the creator
   * @param title the game title
   * @param questions a list of questions
   */
  public Game(String creator, String title, List<Question> questions) {
    this.title = title;  
    this.creator = creator;  
    this.questions = questions;
  }

  /**
   * @return the Game's title
   */
  public String getTitle() {
    return this.title;  
  }
  /**
   * Sets the game title
   * @param title 
   */
  public void setTitle(String title) {
    this.title = title;  
  }
  
  /**
   * @return the game's creator
   */
  public String getCreator() {
    return this.title;  
  }

  /**
   * Sets the game's creator
   * @param creator 
   */
  public void setCreator(String creator) {
    this.creator = creator;  
  }

  
  /**
   * @return the game's questions
   */
  public List<Question> getQuestions() {
    return this.questions;
  }

  /**
   * Sets the game's questions
   * @param questions 
   */
  public void setQuestions(List<Question> questions) {
    this.questions = questions;
  }

  /**
   * Adds a question
   * @param question
   */
  public void addQuestion(Question question) {
    this.questions.add(question);
  }
}
