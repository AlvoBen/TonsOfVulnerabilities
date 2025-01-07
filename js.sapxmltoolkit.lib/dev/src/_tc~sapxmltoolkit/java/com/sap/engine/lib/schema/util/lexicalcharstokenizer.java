package com.sap.engine.lib.schema.util;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-1-5
 * Time: 12:12:11
 * To change this template use Options | File Templates.
 */
public final class LexicalCharsTokenizer extends Tokenizer {
  
  private static final int CHARS_STORAGE_INCREMENT_LENGTH = 10;
  private static final int CHARS_STORAGE_INIT_LENGTH = 30;
  
  private char[] charsStorage;
  private int storedCharsCount;
  private Vector stringsStorage;
  
  public LexicalCharsTokenizer(String target, String[] delimiters) {
    super(target, delimiters);
  }

  protected void processTarget(String target, String[] delimiters) {
    charsStorage = new char[CHARS_STORAGE_INIT_LENGTH];
    storedCharsCount = 0;
    stringsStorage = new Vector();
    if(delimiters != null) {
      processTargetWithDelimiters(target, delimiters);
    } else {
      processTargetWithoutDelimiters(target);
    }
  }

  private void processTargetWithDelimiters(String target, String[] delimiters) {
    int i = 0;
    while(i < target.length()) {
      String delimiter = null;
      for(int j = 0; j < delimiters.length; j++) {
        if(target.startsWith(delimiters[j], i)) {
          delimiter = delimiters[j];
          break;
        }
      }
      if(delimiter != null) {
        i += delimiter.length();
        tokensCollector.add(delimiter);
      } else {
        tokensCollector.add(determineString(target.charAt(i)));
        i++;
      }
    }
  }

  private void processTargetWithoutDelimiters(String target) {
    for(int i = 0; i < target.length(); i++) {
      tokensCollector.add(determineString(target.charAt(i)));
    }
  }
  
  private String determineString(char ch) {
    String result = null;
    for(int i = 0; i < storedCharsCount; i++) {
      char storedCh = charsStorage[i];
      if(ch == storedCh) {
        return((String)(stringsStorage.get(i)));
      }
    }
    return(storeCharAndString(ch));
  }
  
  private String storeCharAndString(char ch) {
    if(storedCharsCount == charsStorage.length) {
      incrementCharsStorage();
    }
    String result = String.valueOf(ch);
    charsStorage[storedCharsCount++] = ch;
    stringsStorage.add(result);
    return(result);
  }
  
  private void incrementCharsStorage() {
    char[] hlpArray = charsStorage;
    charsStorage = new char[charsStorage.length + CHARS_STORAGE_INCREMENT_LENGTH];
    System.arraycopy(hlpArray, 0, charsStorage, 0, hlpArray.length);
  }
}
