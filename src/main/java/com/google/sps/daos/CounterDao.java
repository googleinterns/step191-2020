package com.google.sps.daos;

import com.google.sps.data.Counter;

public interface CounterDao {
  public void increaseCounter();

  public void updateCounter(Counter counter);

  public void deleteCounter();

  public void storeCounter();
}
