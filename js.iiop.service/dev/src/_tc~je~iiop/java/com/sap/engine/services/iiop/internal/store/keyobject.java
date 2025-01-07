package com.sap.engine.services.iiop.internal.store;

import java.util.Arrays;

public class KeyObject {

  private byte[] key;

  public KeyObject(byte[] key) {
    this.key = key;
  }

  public byte[] getKey() {
    return key;
  }

  public void setKey(byte[] key) {
    this.key = key;
  }

  public int hashCode() {
    int hashcode = 0;
    for (int i = 0; i < key.length; i++) {
      hashcode += key[i]*(i+1);
    }
    return Math.abs(hashcode);
  }

  public boolean equals(Object kObj) {
    if (kObj instanceof KeyObject) {
      return Arrays.equals(key, ((KeyObject) kObj).getKey());
    } else {
      return false;
    }
  }

}
