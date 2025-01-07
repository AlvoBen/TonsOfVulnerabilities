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
 * Class that holds namespace prefixes. 
 * @author Chavdar Baikov (chavdar.baikov@sap.com)
 * @version 6.30
 */
public final class PHSet {

  private final int INITIAL_SIZE = 64;
  private PrefixHolder[] content;
  public int currentSize;

  public PHSet() {
    content = new PrefixHolder[INITIAL_SIZE];
    currentSize = 0;
  }

  private void resizePH(int newSize) {
    PrefixHolder[] result = new PrefixHolder[newSize];
    System.arraycopy(content,0,result,0,content.length);
    this.content = result;
  }

  public void add(PrefixHolder ph) {
    if (currentSize == content.length) {
      resizePH(content.length+INITIAL_SIZE);
    }
    content[currentSize] = ph;
    currentSize++;
  }

  public PrefixHolder get(int index) {
    return content[index];
  }

  public int remove(int index) {
    if (index<currentSize) {
      content[index] = content[currentSize-1];
      content[currentSize-1] = null;
      currentSize--;
      return index--;
    } else {
      return index;
    }
  }

  public void setSize(int size) {
    this.currentSize = size;
  }

  public PrefixHolder[] getContent() {
    return content;
  }

  public int size() {
    return currentSize;
  }

  public void clear() {
    content = new PrefixHolder[INITIAL_SIZE];
    currentSize = 0;
  }
}
