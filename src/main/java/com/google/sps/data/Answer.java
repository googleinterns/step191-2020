package com.google.sps.data;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Answer {

  public static Builder builder() {
    return new AutoValue_Answer.Builder();
  }

  public abstract String title();
  public abstract boolean correct();


  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder title(String title);

    public abstract Builder correct(boolean correct);

    public abstract Answer build();
  }
}
