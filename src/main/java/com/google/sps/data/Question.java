package com.google.sps.data;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Question {

  private String title;
  private boolean isMC;
  List<Answer> answers;
  private String id;
  private String prevId;
  private String nextId;

  public Question () {
  }

  public Question(String title, List<Answer> answers, boolean isMC) {
    this.title = title;
    this.answers = answers;
    this.isMC = isMC;
    this.id = "null";
    this.prevId = "null";
    this.nextId = "null";
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


  public String getPrevId() {
    return this.prevId;  
  }

  public void setPrevId(String prevId) {
    this.prevId = prevId;  
  }

  public String getNextId() {
    return this.nextId;  
  }

  public void setNextId(String nextId) {
    this.nextId = nextId;  
  }

  public List<Answer> getAnswers() {
    return this.answers;  
  }

  public void setAnswers(List<Answer> answers) {
    this.answers = answers;
  }

  public void addAnswer(Answer answer) {
    this.answers.add(answer);
  }

  public boolean getIsMC() {
    return this.isMC;
  }
    
  public void setIsMC(boolean isMC) {
    this.isMC = isMC;
  }

  public Map<String, Object> questionData() {
    Map<String, Object> questionData = new HashMap<>();
    questionData.put("title", this.getTitle());
    questionData.put("isMC", this.getIsMC());
    questionData.put("previousQuestion", (this.getPrevId() == "null" ? null : this.getPrevId()));
    questionData.put("nextQuestion", (this.getNextId() == "null" ? null : this.getNextId()));
    return questionData;
  }

  public String toString(){//overriding the toString() method  
    return "{title=" + title +", answers="+ answers.toString() + " id=" + this.id + " prevId=" + this.prevId + " nextId=" + this.nextId + " }";  
  }  
}
