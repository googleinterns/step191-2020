package com.google.sps.data;

import java.util.List; 
import java.util.ArrayList; 

public class GameInstance {

  private String id;
  private boolean isActive;
  private String creator;
  private String gameId;
  private List<Member> members;

  public GameInstance() {
    this.members = new ArrayList<Member>();
    this.isActive = false;
  }

  public GameInstance(String id) {
    this.id = id;
    this.members = new ArrayList<Member>();
    this.isActive = false;
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
  
}
