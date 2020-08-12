package com.google.sps.data;

import java.util.HashMap;
import java.util.Map;
import com.google.auto.value.AutoValue;
import com.google.sps.data.Question;
import java.util.List;

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
