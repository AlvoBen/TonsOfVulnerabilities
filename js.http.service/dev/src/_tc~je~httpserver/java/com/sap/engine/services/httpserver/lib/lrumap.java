package com.sap.engine.services.httpserver.lib;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LRUMap<K, V> extends LinkedHashMap<K, V> {
  private static final long serialVersionUID = 4372455368577337965L;
  private int mMaxCapacity;

  public LRUMap(int initCapacity) {
    super(initCapacity, 1.0f, true);
    mMaxCapacity = 50 * initCapacity;
  }//end of constructor

  @Override
  protected boolean removeEldestEntry(Map.Entry eldest) {
    return size() > mMaxCapacity;
  }//end of removeEldestEntry(Map.Entry eldest)
  
}//end of class
