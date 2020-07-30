package com.google.sps.data;

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
  public abstract List<Question> questions();


  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder title(String title);
    public abstract Builder creator(String creator);
    public abstract Builder questions(List<Question> questions);

    public abstract Game build();
  }
}