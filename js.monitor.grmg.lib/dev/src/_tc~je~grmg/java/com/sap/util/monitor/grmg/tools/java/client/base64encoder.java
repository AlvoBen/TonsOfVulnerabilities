package com.sap.util.monitor.grmg.tools.java.client;

class BASE64Encoder {
  final static String encodingChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

  public String encode(String source) {
    char[] sourceBytes = getPaddedBytes(source);
    int numGroups = (sourceBytes.length + 2) / 3;
    char[] targetBytes = new char[4];
    char[] target = new char[4 * numGroups];
 
    for (int group = 0; group < numGroups; group++) {
      convert3To4(sourceBytes, group * 3, targetBytes);
      for (int i = 0; i < targetBytes.length; i++) {
        target[i + 4 * group] = encodingChar.charAt(targetBytes[i]);
      }
    }
 
    int numPadBytes = sourceBytes.length - source.length();
 
    for (int i = target.length - numPadBytes; i < target.length; i++) {
      target[i] = '=';
    }
    
    return new String(target);
  }

  private char[] getPaddedBytes(String source) {
    char[] converted = source.toCharArray();
    int requiredLength = 3 * ((converted.length + 2) / 3);
    char[] result = new char[requiredLength];
    
    System.arraycopy(converted, 0, result, 0, converted.length);
 
    return result;
  }
  
  private void convert3To4(char[] source, int sourceIndex, char[] target) {
    target[0] = (char) (source[sourceIndex] >>> 2);
    target[1] = (char) (((source[sourceIndex] & 0x03) << 4) | (source[sourceIndex + 1] >>> 4));
    target[2] = (char) (((source[sourceIndex + 1] & 0x0f) << 2) | (source[sourceIndex + 2] >>> 6));
    target[3] = (char) (source[sourceIndex + 2] & 0x3f);
  }
}
