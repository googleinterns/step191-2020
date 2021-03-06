package com.google.sps.data;

import java.util.List; 
import java.util.ArrayList; 

public class GameInstance {

  private String id;
  private boolean isActive;
  private boolean isFinished;
  private String creator;
  private String gameId;
  private String currentQuestion;
  private boolean currentQuestionActive;
  private int numberOfMembers;
  private List<Member> members;

  public GameInstance() {
    this.members = new ArrayList<Member>();
    this.isActive = false;
    this.isFinished = false;
  }

  public GameInstance(String id) {
    this.id = id;
    this.members = new ArrayList<Member>();
    this.isActive = false;
    this.isFinished = false;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }
  
  public boolean getIsActive() {
    return this.isActive;
  }

  public void setIsActive(boolean isActive) {
    this.isActive = isActive;
  }
  
  public int getNumberOfMembers() {
    return this.numberOfMembers;
  }

  public void setNumberOfMembers(int numberOfMembers) {
    this.numberOfMembers = numberOfMembers;
  }
  
  public String getCreator() {
    return this.creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getGameId() {
    return this.gameId;
  }

  public void setGameId(String gameId) {
    this.gameId = gameId;
  }

  public String getCurrentQuestion() {
    return this.currentQuestion;
  }

  public void setCurrentQuestion(String currentQuestion) {
    this.currentQuestion = currentQuestion;
  }

  public boolean isCurrentQuestionActive() {
    return this.currentQuestionActive;
  }

  public void setCurrentQuestionActive(boolean currentQuestionActive) {
    this.currentQuestionActive = currentQuestionActive;
  }

  public Member getMember(int index) {
    return this.members.get(index);
  }
  
  public List<Member> getMembers() {
    return this.members;
  }

  public List<Member> addMember(Member member) {
    this.members.add(member);
    return this.members;
  }

  public boolean getIsFinished() {
    return this.isFinished;
  }

  public void setIsFinished(boolean isFinished) {
    this.isFinished = isFinished;
  }

  
}
