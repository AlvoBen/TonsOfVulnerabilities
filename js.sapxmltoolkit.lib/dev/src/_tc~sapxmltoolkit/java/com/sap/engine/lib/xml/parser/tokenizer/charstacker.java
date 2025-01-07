/*
 * Copyright (c) 2002 by SAP Labs Sofia AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.lib.xml.parser.tokenizer;

import com.sap.engine.lib.log.LogWriter;

/**
 * 
 *
 * @author Chavdar Baikov (chavdar.baikov@sap.com)
 * @version 6.30
 */
class CharStacker {
  
  private int INITIAL_CHARSIZE = 2048;
  private int INITIAL_INDEXSIZE = 256;
  private char[] charbuffer;
  private int[] intbuffer;
  private int charTop = 0;
  private int intTop = 0;
  
  public CharStacker() {
    charbuffer = new char[INITIAL_CHARSIZE];
    intbuffer = new int[INITIAL_INDEXSIZE];
  }

  /**
   * Resizes up char array.
   */ 
  private char[] resizeCharArray(char[] array, int newSize) {
    char[] result = new char[newSize];
    System.arraycopy(array,0,result,0,array.length);
    return result;
  }
  /**
   * Resizes up int array.
   */ 
  private int[] resizeIntArray(int[] array, int newSize) {
    int[] result = new int[newSize];
    System.arraycopy(array,0,result,0,array.length);
    return result;
  }
  
  public void pushChars(char[] chars) {
    int length = chars.length;
    if (intTop == intbuffer.length) {
      intbuffer = resizeIntArray(intbuffer,intbuffer.length*2);
    }
    if (charTop+length > charbuffer.length) {
      charbuffer = resizeCharArray(charbuffer, charbuffer.length*2 + length);
    }
    intbuffer[intTop] = charTop;
    intTop++;
    System.arraycopy(chars,0,charbuffer,charTop,length);
    charTop += length;    
  }
  
  public char[] getChars() {
    return charbuffer;
  }
  
  public int getBegin() {
    return intbuffer[intTop-1];        
  }
  
  public int getSize() {
    return charTop - intbuffer[intTop-1];    
  }
  
  public void pop() {
    charTop -= (charTop - intbuffer[intTop-1]);
    intTop--;    
  }
  
  public void clear() {
    charTop = 0;
    intTop = 0;
  }
  
  public void writeStatistics() {
    LogWriter.getSystemLogWriter().println(" Charr buffer :"+charbuffer.length); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println(" Int buffer :"+intbuffer.length); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println(" Char top :"+charTop); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println(" Int top :"+intTop); //$JL-SYS_OUT_ERR$
  }
  
}
