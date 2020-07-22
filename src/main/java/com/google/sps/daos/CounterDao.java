package com.google.sps.daos;

import com.google.sps.data.Counter;

public interface CounterDao {
  public Counter getCounter();

  public void updateCounter(Counter counter);

  public void deleteCounter();
}
