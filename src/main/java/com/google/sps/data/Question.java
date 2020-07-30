package com.google.sps.data;

import com.google.auto.value.AutoValue;
import com.google.sps.data.Answer;
import java.util.List;

@AutoValue
public abstract class Question {

  public static Builder builder() {
    return new AutoValue_Question.Builder();
  }

  public abstract String title();
  public abstract List<Answer> answers();


  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder title(String title);

    public abstract Builder answers(List<Answer> answers);

    public abstract Question build();
  }
}