package com.sap.engine.services.jndi.persistentimpl.memory.util;


public class StringByteData {

  private String toConvert;
  private byte[] stringBytes = null;
  private int stringBytesLen = 0;
  private byte[] strBytesLenInBytes = null;
  private byte strBytesLenInBytesLen = 0;

  public StringByteData() {
    this.toConvert = null;
  }

  public StringByteData(String toConvert) {
    this.toConvert = toConvert;
  }

  public StringByteData process() {
    if (toConvert != null) {
      stringBytes = toConvert.getBytes();
      stringBytesLen = stringBytes.length;
      strBytesLenInBytes = intToByteArr(stringBytesLen);
      strBytesLenInBytesLen = (byte) strBytesLenInBytes.length;
    } else {
      strBytesLenInBytes = intToByteArr(0);
      strBytesLenInBytesLen = (byte) strBytesLenInBytes.length;
    }
    return this;
  }

  public byte[] getStringBytes() {
    return stringBytes;
  }

  public int getStringBytesLen() {
    return stringBytesLen;
  }

  public byte[] getStrBytesLenInBytes() {
    return strBytesLenInBytes;
  }

  public byte getStrBytesLenInBytesLen() {
    return strBytesLenInBytesLen;
  }
  
  public void setToConvert(String toConvert) {
    this.toConvert = toConvert;
  }
  
  public static byte[] intToByteArr(int i) {
    byte[] y = new byte[4];
    y[0] = (byte) i;
    y[1] = (byte) (i >> 8);
    y[2] = (byte) (i >> 16);
    y[3] = (byte) (i >> 24);
    return y;
  }



}
