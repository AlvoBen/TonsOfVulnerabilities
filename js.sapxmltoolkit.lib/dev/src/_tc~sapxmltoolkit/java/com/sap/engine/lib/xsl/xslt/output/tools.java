package com.sap.engine.lib.xsl.xslt.output;

import com.sap.engine.lib.xml.Symbols;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import java.util.*;

/**
 * Contains static methods, which are not appropriate for
 * encapsulation in any other class. This class cannot be
 * instantiated.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version 1.0
 */
final class Tools {

  static final String YES = "yes";
  static final String NO = "no";

  private Tools() {

  }

  static boolean isYes(String s) {
    return ((s != null) && (s.trim().equalsIgnoreCase(YES)));
  }

  //
  //  static boolean isYes(CharArray s) {
  //    return ((s != null) && (s.trim().equalsIgnoreCase(YES)));
  //  }
  static boolean isNo(String s) {
    return ((s != null) && (s.trim().equalsIgnoreCase(NO)));
  }

  static String yn(boolean b) {
    return b ? YES : NO;
  }

  static boolean notEmpty(String s) {
    return ((s != null) && !s.trim().equals(""));
  }

  static boolean notEmpty(CharArray s) {
    return ((s != null) && !s.isWhitespace());
  }

  /**
   * Clears the Hashset and adds the NMTOKENS of s to it.
   */
  static void parseNMTOKENS(String s, HashSet set) {
    if (s == null) {
      s = "";
    }

    set.clear();
    int i = 0;
    int j = 0;
    int l = s.length();

    while (i < l) {
      while ((i < l) && Symbols.isWhitespace(s.charAt(i))) {
        i++;
      }

      if (i >= l) {
        break;
      }

      j = i;

      while ((j < l) && !Symbols.isWhitespace(s.charAt(j))) {
        j++;
      }

      if (i <= j) {
        set.add(s.substring(i, j));
      }

      i = j;
    }
  }

  static boolean isWhitespace(char ch) {
    // Same as com.sap.engine.lib.xml.parser.helpers.Symbols.isWhitespace(char ch)
    return ((ch == 0x0020) || (ch == 0x000D) || (ch == 0x000A) || (ch == 0x0009));
  }

  static boolean isWhitespace(String s) {
    int l = s.length();

    for (int i = 0; i < l; i++) {
      if (!isWhitespace(s.charAt(i))) {
        return false;
      }
    } 

    return true;
  }

  static boolean isWhitespace(char[] ch, int start, int length) {
    int end = start + length;

    for (int i = start; i < end; i++) {
      if (!isWhitespace(ch[i])) {
        return false;
      }
    } 

    return true;
  }

}

