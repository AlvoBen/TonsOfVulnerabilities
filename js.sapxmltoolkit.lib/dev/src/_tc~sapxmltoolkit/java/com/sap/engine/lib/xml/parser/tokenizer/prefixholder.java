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
 * Simple PrefixHolder class used by tokenizer to store namespaces.
 *
 * @author Chavdar Baikov (chavdar.baikov@sap.com)
 * @version 6.30
 */
public final  class PrefixHolder {

  public String namespace;
  private String[] contents;
  private int[] levels;
  private int top;
  private static final int INITIALSIZE = 32; //256;

//  private static PrefixHolder[] cache = new PrefixHolder[128];
//  public static int ctop = 0;

//  public static synchronized PrefixHolder getPrefixHolder(String namespace) {
//    if (ctop == 0) {
//      return new PrefixHolder(namespace);
//    } else {
//      ctop--;
//      cache[ctop].namespace = namespace;
//      return cache[ctop];
//    }
//  }
//
//  public static synchronized void  returnPrefixHolder(PrefixHolder ph) {
//    if (ctop<cache.length) {
//      ph.init();
//      cache[ctop] = ph;
//      ctop++;
//    }
//  }

  public void init() {
    top = 0;
  }
  public PrefixHolder(String namespace) {
    this.namespace = namespace;
    contents = new String[INITIALSIZE];
    levels = new int[INITIALSIZE];
    top = 0;
  }

  /**
   * Adds namespace mapping to this preffix.
   */
  public void enter(String s) {
    if (top != 0 && contents[top-1].equals(s)) {
      levels[top-1]++;
      return;
    }
    if (top == contents.length) {
      String[] perm1 = new String[contents.length + INITIALSIZE];
      System.arraycopy(contents,0,perm1,0,contents.length);
      contents = perm1;
      int[] perm2 = new int[levels.length + INITIALSIZE];
      System.arraycopy(contents,0,perm2,0,contents.length);
      levels = perm2;
    }
    levels[top] = 1;
    contents[top] = s;
    top++;
  }

  /**
   * Enters one level deep without declaring new namespace.
   */
  public void enter() {
    levels[top-1]++;
  }

  /**
   * Element leave. returns true is namespace goes out of scope. else false.
   */
  public boolean leave() {
    if (top > 0) {
      levels[top-1]--;
      if (levels[top-1]==0) {
        top--;
      }
      if (top==0) {
        return true;
      } else {
        return false;
      }
    } else {
      throw new ArrayIndexOutOfBoundsException("Stack is empty !");
    }
  }

  public int size() {
    return top;
  }

  public void clear() {
    this.top = 0;
  }

  public String top() {
    return contents[top-1];
  }
}
