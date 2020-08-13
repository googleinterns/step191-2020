package com.google.sps.data;

import java.util.HashMap;
import java.util.Map;
import com.google.auto.value.AutoValue;
import com.google.sps.data.Question;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@AutoValue
public abstract class Game {

  public static Builder builder() {
    return new AutoValue_Game.Builder();
  }

  public abstract String title();
  public abstract String creator();
  public abstract String headQuestion();
  public abstract List<Question> questions();

  public Map<String, Object> gameData() {

    Map<String, Object> gameData = new HashMap<>();
        gameData.put("creator", this.creator());
        gameData.put("title", this.title());
        gameData.put("headQuestion", this.headQuestion());
        gameData.put("numberOfQuestions", this.questions().size());
    return gameData;
  }

  public static Game buildWithJson(String gameJsonStr) {
    // The function recieves the JSON and converts to a JsonObject
    String gameJson = gameJsonStr;
    JsonElement gameJsonElem = new JsonParser().parse(gameJson);
    JsonObject gameJsonObj = gameJsonElem.getAsJsonObject();

    // We retrieve the attributes from the game
    String gameTitle = gameJsonObj.get("title").getAsString();
    String gameCreator = gameJsonObj.get("creator").getAsString();

    List<Question> questions = new ArrayList(Arrays.asList()); // Here we will store the questions as objects

    // We get the questions as a JsonArray and then we iterate to add them to the list
    JsonArray questionsJsonArray = (JsonArray) gameJsonObj.get("questions");

    for ( int i = 0; i < questionsJsonArray.size(); i++ ) {
      JsonObject questionJsonObj = (JsonObject) questionsJsonArray.get(i);

      // We retrieve the attributes from the question
      String questionTitle = questionJsonObj.get("title").getAsString();
      boolean isMC = questionJsonObj.get("isMC").getAsBoolean();

      List<Answer> answers = new ArrayList(Arrays.asList()); // Here we will store the answers as objects

      // We get the answers as a JsonArray and then we iterate to add them to the list
      JsonArray answersJsonArray = (JsonArray) questionJsonObj.get("answers");

      for ( int j = 0; j < answersJsonArray.size(); j++ ) {
        JsonObject answerJsonObj = (JsonObject) answersJsonArray.get(j);
      
        // We retrieve the attributes from the answer
        String answerTitle = answerJsonObj.get("title").getAsString();
        boolean isCorrect = answerJsonObj.get("correct").getAsBoolean();

        // We create a new answer object with the attributes that we recovered then add the object to the list
        answers.add(new Answer(answerTitle, isCorrect));
      }
      // We create a new question object with the attributes that we recovered and the answers, then we add the object to the list
      questions.add(new Question(questionTitle, answers, isMC));
    }

    // We return the game object
  return Game.builder().title(gameTitle).creator(gameCreator).questions(questions).headQuestion(questions.get(0).getId()).build(); 
    
  }

  public Builder toBuilder() {
    return new AutoValue_Game.Builder()
      .title(this.title())
      .creator(this.creator())
      .headQuestion(this.headQuestion())
      .questions(this.questions());
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder title(String title);
    public abstract Builder creator(String creator);
    public abstract Builder headQuestion(String headQuestion);
    public abstract Builder questions(List<Question> questions);

    public abstract Game build();
  }
}
