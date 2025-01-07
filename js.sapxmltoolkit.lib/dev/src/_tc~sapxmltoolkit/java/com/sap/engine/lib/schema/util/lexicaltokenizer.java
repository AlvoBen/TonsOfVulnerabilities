package com.sap.engine.lib.schema.util;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2003-12-19
 * Time: 12:22:34
 * To change this template use Options | File Templates.
 */
public final class LexicalTokenizer extends Tokenizer {

  public LexicalTokenizer(String target, String[] delimiters) {
    super(target, delimiters);
  }

  protected void processTarget(String target, String[] delimiters) {
    if(target.equals("")) {
      tokensCollector.add("");
    } else {
      StringBuffer buffer = new StringBuffer();
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
          int bufferLength = buffer.length();
          if(bufferLength != 0) {
            tokensCollector.add(buffer.toString());
            buffer.delete(0, bufferLength);
          }
          tokensCollector.add(delimiter);
        } else {
          buffer.append(target.charAt(i));
          i++;
        }
      }
      if(buffer.length() != 0) {
        tokensCollector.add(buffer.toString());
      }
    }
  }
}
