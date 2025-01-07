package com.sap.engine.lib.xml.util;

import com.sap.engine.lib.log.LogWriter;

/**
 * @author  Nick Nickolov, nick_nickolov@abv.bg
 * @version 08-Mar-02, 10:41:15
 */
public final class ListSplitter {

  private ListSplitter() {

  }

  public static String[] split(String s) {
    int ls = s.length();
    String[] tokens = new String[(ls + 1) / 2];
    int nTokens = 0;
    int i = 0;
    int j = 0;

    OUTER: while (true) {
      // skip ws
      INNER0: while (true) {
        if (i >= ls) {
          break  OUTER;
        }

        char ch = s.charAt(i);

        if ((ch == ' ') || (ch == '\r') || (ch == '\n') || (ch == '\t')) {
          i++;
          continue  INNER0;
        }

        break  INNER0;
      }

      // read a word
      j = i + 1;

      INNER1: while (true) {
        if (j >= ls) {
          tokens[nTokens] = s.substring(i, j);
          nTokens++;
          break  OUTER;
        }

        char ch = s.charAt(j);

        if (!((ch == ' ') || (ch == '\r') || (ch == '\n') || (ch == '\t'))) {
          j++;
          continue  INNER1;
        }

        tokens[nTokens] = s.substring(i, j);
        nTokens++;
        break  INNER1;
      }

      i = j + 1;
    }

    if (nTokens == tokens.length) {
      return tokens;
    }

    String[] r = new String[nTokens];
    System.arraycopy(tokens, 0, r, 0, nTokens);
    return r;
  }

  public static void main(String[] args) throws Exception {
    String s = "\tSqueeze my\r\n\r\rlemon\n\n \t\ttill \tthe juice \r\rruns down\n my leg    ";
    //String s = "";
    String[] t = split(s);

    for (int i = 0; i < t.length; i++) {
      LogWriter.getSystemLogWriter().println("\"" + t[i] + "\"");
    } 
  }

}

