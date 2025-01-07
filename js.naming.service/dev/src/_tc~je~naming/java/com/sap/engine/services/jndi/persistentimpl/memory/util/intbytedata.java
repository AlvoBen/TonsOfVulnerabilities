package com.sap.engine.services.jndi.persistentimpl.memory.util;

public class IntByteData {
  
  private int toConvert = 0;
  private byte[] intInBytes = null;
  private byte intInBytesLen = 0;
  
  public IntByteData() {
  }

  public IntByteData(int toConvert) {
    this.toConvert = toConvert;
  }
  
  public IntByteData process() {
    
    intInBytes = intToByteArr(toConvert);
    intInBytesLen = (byte) intInBytes.length;
    return this;
    
  }
  
  public byte[] getIntInBytes() {
    return intInBytes;
  }

  public byte getIntInBytesLen() {
    return intInBytesLen;
  }

  public static byte[] intToByteArr(int i) {
    byte[] y = new byte[4];
    y[0] = (byte) i;
    y[1] = (byte) (i >> 8);
    y[2] = (byte) (i >> 16);
    y[3] = (byte) (i >> 24);
    return y;
  }

  public void setToConvert(int toConvert) {
    this.toConvert = toConvert;
  }
  
}
