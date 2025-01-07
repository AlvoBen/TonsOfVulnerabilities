package com.sap.httpclient.utils;

import java.util.StringTokenizer;

public class UtilConvert {

  static final int INTFLAG = 0x000000ff;

  /**
   * Writes int in byte array
   *
   * @param b   buffer
   * @param off offset
   * @param i   int
   */
  public static void writeIntToByteArr(byte[] b, int off, int i) {
    b[off] = (byte) i;
    b[off + 1] = (byte) (i >> 8);
    b[off + 2] = (byte) (i >> 16);
    b[off + 3] = (byte) (i >> 24);
  }

  public static int byteArrToInt(byte[] b, int off) {
    return (b[off] & INTFLAG) | ((b[off + 1] & INTFLAG) << 8) | ((b[off + 2] & INTFLAG) << 16) | ((int) b[off + 3] << 24);
  }

  public static void writeLongToByteArr(byte[] b, int off, long i) {
    b[off] = (byte) i;
    b[off + 1] = (byte) (i >> 8);
    b[off + 2] = (byte) (i >> 16);
    b[off + 3] = (byte) (i >> 24);
    b[off + 4] = (byte) (i >> 32);
    b[off + 5] = (byte) (i >> 40);
    b[off + 6] = (byte) (i >> 48);
    b[off + 7] = (byte) (i >> 56);
  }

  public static long byteArrToLong(byte[] b, int off) {
    int li, ri;
    ri = (b[off] & INTFLAG) | ((b[off + 1] & INTFLAG) << 8) | ((b[off + 2] & INTFLAG) << 16) | ((int) b[off + 3] << 24);
    li = (b[off + 4] & INTFLAG) | ((b[off + 5] & INTFLAG) << 8) | ((b[off + 6] & INTFLAG) << 16) | ((int) b[off + 7] << 24);
    return ((long) li << 32) | (ri & 0xFFFFFFFFL);

  }

  public static String reformatString(String orig, String orig_token, String new_token){
    if(orig == null || orig_token == null || new_token == null){
      return null;
    }
    StringBuilder sb = new StringBuilder();
    StringTokenizer st = new StringTokenizer(orig, orig_token);
    while(st.hasMoreTokens()){
      sb.append(st.nextToken());
      sb.append(new_token);
    }
    return  sb.toString();
  }


}
