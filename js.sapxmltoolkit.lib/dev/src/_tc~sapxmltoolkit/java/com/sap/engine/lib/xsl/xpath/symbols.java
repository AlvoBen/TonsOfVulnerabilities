package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.log.LogWriter;

/**
 *   Contains methods for checking if a character matches a
 * certain production from the specification.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version June 2001
 *
 * @deprecated <tt>com.sap.engine.lib.xml.parser.helpers.Symbols</tt> should be used instead
 */
public final class Symbols {

  public static boolean isDigit(char ch) {
    return ((ch >= '0') && (ch <= '9'));
  }

  public static boolean isInitialCharForQName(char ch) {
    return (((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z')) || (ch == '_'));
  }

  public static boolean isCharForQName(char ch) {
    return (((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z')) || (ch == '_') || (ch == '-') || (ch == '.') || ((ch >= '0') && (ch <= '9')));
  }

  public static boolean isQName(String s) {
    if (!isInitialCharForQName(s.charAt(0))) {
      return false;
    }

    for (int i = 1; i < s.length(); i++) {
      if (!isCharForQName(s.charAt(i))) {
        return false;
      }
    } 

    return true;
  }

  public static boolean isWhitespace(char ch) {
    return ((ch == (char) 32) || (ch == (char) 13) || (ch == (char) 10) || (ch == (char) 9));
  }

  /**
   *   Prints to the console a sequence of 2*n spaces.
   */
  public static void printSpace(int n) {
    for (int i = 0; i < n; i++) {
      LogWriter.getSystemLogWriter().print("  ");
    } 
  }

  public static String trimWhitespace(String s) {
    if (s == null) {
      return null;
    }

    if (s.equals("")) {
      return s;
    }

    int l = s.length();
    int i;
    int j;
    for (i = 0; (i < l) && isWhitespace(s.charAt(i)); i++) {
      ; 
    }

    if (i < l) {
      for (j = l - 1; (j >= 0) && isWhitespace(s.charAt(j)); j--) {
        ; 
      }
      return s.substring(i, j + 1);
    } else {
      return "";
    }
  }

}

