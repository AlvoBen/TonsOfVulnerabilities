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

/**
 * 
 * @author Chavdar Baikov (chavdar.baikov@sap.com)
 * @version 6.30
 */
public final class StringStacker {

  private int INITIAL_INDEXSIZE = 64;
  private String[] stringArr;
  private int top = 0;

  public StringStacker() {
    stringArr = new String[INITIAL_INDEXSIZE];
  }

  /**
   * Resizes up char array.
   */
  private String[] resizeStringArray(String[] array, int newSize) {
    String[] result = new String[newSize];
    System.arraycopy(array,0,result,0,array.length);
    return result;
  }

  public void push(String string) {
    if (top == stringArr.length) {
      stringArr = resizeStringArray(stringArr,stringArr.length+INITIAL_INDEXSIZE);
    }
    stringArr[top] = string;
    top++;
  }

  public String pop() {
    top--;
    return stringArr[top];
  }

  public String get() {
    return stringArr[top-1];
  }

  public int getSize() {
    return top;
  }


  public void clear() {
    if (stringArr.length != INITIAL_INDEXSIZE){
      stringArr = new String[INITIAL_INDEXSIZE];
    }
    top  = 0;
  }

//  public void writeStatistics() {
//    LogWriter.getSystemLogWriter().println(" Charr buffer :"+charbuffer.length);
//    LogWriter.getSystemLogWriter().println(" Int buffer :"+intbuffer.length);
//    LogWriter.getSystemLogWriter().println(" Char top :"+charTop);
//    LogWriter.getSystemLogWriter().println(" Int top :"+intTop);
//  }

}
