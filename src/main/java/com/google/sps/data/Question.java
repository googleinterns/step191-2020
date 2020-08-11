package com.google.sps.data;

import com.google.sps.data.Answer;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Question {

  private String title;
  private boolean isMP;
  List<Answer> answers;
  private String id;
  private String prevId;
  private String nextId;

  public Question () {
  }

  public Question(String title, List<Answer> answers, boolean isMP) {
    this.title = title;
    this.answers = answers;
    this.isMP = isMP;
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

  public void setAnswers(List<Answer> answer) {
    this.answers = answers;
  }

  public void addAnswer(Answer answer) {
    this.answers.add(answer);
  }

  public boolean getIsMP() {
    return this.isMP;
  }
    
  public void setIsMP(boolean isMP) {
    this.isMP = isMP;
  }

  public Map<String, Object> questionData() {
    Map<String, Object> questionData = new HashMap<>();
    questionData.put("title", this.getTitle());
    questionData.put("isMp", this.getIsMP());
    questionData.put("previousQuestion", this.getPrevId());
    questionData.put("nextQuestion", this.getNextId());
    return questionData;
  }

  public String toString(){//overriding the toString() method  
    return "{title=" + title +", answers="+ answers.toString() + " id=" + this.id + " prevId=" + this.prevId + " nextId=" + this.nextId + " }";  
  }  
}
