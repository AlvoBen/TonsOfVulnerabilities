package com.sap.engine.lib.schema.util;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-1-5
 * Time: 12:20:11
 * To change this template use Options | File Templates.
 */
public abstract class Tokenizer {

  protected Vector tokensCollector;
  protected int index;

  public Tokenizer(String target, String[] delimiters) {
    tokensCollector = new Vector();
    index = 0;
    if(delimiters != null) {
      sort(delimiters);
    }
    processTarget(target, delimiters);
  }

  private void sort(String[] delimiters) {
    for(int i = 0; i < delimiters.length; i++) {
      String delimiter = delimiters[i];
      int replacementIndex = 0;
      for(int j = i + 1; j < delimiters.length; j++) {
        String delimiterToCmpWith = delimiters[j];
        if(delimiter.length() < delimiterToCmpWith.length()) {
          delimiter = delimiterToCmpWith;
          replacementIndex = j;
        }
      }
      if(replacementIndex != 0) {
        String hlpDelimiter = delimiters[i];
        delimiters[i] = delimiter;
        delimiters[replacementIndex] = hlpDelimiter;
      }
    }
  }

  protected abstract void processTarget(String target, String[] delimiters);

  public String next() {
    String result = null;
    if(index < tokensCollector.size()) {
      result = (String)(tokensCollector.get(index++));
    }
    return(result);
  }
  
  public String current() {
    if(index > 0 && index < tokensCollector.size()) {
      return((String)(tokensCollector.get(index - 1)));
    }
    return(null);
  }

  public String peek() {
    String result = next();
    if(result != null) {
      index --;
    }
    return(result);
  }

  public int getIndex() {
    return(index);
  }

  public void setIndex(int index) {
    if(index < 0 || index > tokensCollector.size()) {
      throw new IllegalArgumentException("Illegal index : " + index);
    }
    this.index = index;
  }
}
