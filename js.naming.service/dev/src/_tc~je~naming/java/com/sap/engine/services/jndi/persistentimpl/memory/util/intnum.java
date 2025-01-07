package com.sap.engine.services.jndi.persistentimpl.memory.util;

public class IntNum {
  
  private int num = 0;
  
  public IntNum(int i) {
    num = i;
  }

  public void inc() {
    num++;
  }
  
  public void dec() {
    num--;
  }
  
  public int getNum() {
    return num;
  }
  
}
