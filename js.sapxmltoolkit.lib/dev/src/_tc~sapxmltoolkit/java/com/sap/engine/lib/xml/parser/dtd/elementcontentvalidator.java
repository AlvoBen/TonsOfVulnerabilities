package com.sap.engine.lib.xml.parser.dtd;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.Symbols;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * Title:        xml2000
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      May 2001
 */
public final class ElementContentValidator {

  private static final char[] S_ANY = "ANY".toCharArray();
  private static final char[] S_EMPTY = "EMPTY".toCharArray();
  private static final char[] S_PCDATA = "#PCDATA".toCharArray();
  private static final char[] S_BRACKET_ASTERISK = ")*".toCharArray();
  private static final String E_OK = "Ok";
  private static final String E_STROKE_OR_BRACKET = "'|' or ')' expected";
  private static final String E_STROKE_OR_COMMA_OR_BRACKET = "'|' or ',' or ')' expected";
  private static final String E_GROUP_NOT_UNIFORM = "Group should either contain only ','s or only '|'s";
  private static final String E_END = "End of model expected";
  private static final String E_NAME = "Name expected";
  private static final String E_ASTERISK = "'*' expected";
  private static final String E_START = "Content model should be 'ANY', 'EMPTY', or start with '('";
  char[] s = new char[30];
  private int ps;
  private int ls;
  private String errorMessage;

  public boolean check(CharArray ca) {
    ca.trim();
    ls = ca.length();

    if (s.length <= ls) {
      s = new char[ls + 1];
    }

    ca.getChars(s);
    //removeWhitespace();
    s[ls] = '\0';
    errorMessage = E_OK;
    ps = 0;
    skipS();

    if (confirm(S_ANY) || confirm(S_EMPTY)) {
      return confirmEnd();
    }

    if (!confirm('(')) {
      errorMessage = E_START;
      return false;
    }

    skipS();

    if (confirm(S_PCDATA)) {
      skipS();

      if (confirm(')')) {
        confirm('*'); // there might or might not be an '*'
        return confirmEnd();
      }

      while (true) {
        skipS();
        char ch = s[ps];

        if ((ch != '|') && (ch != ')')) {
          errorMessage = E_STROKE_OR_BRACKET;
          return false;
        }

        ps++;

        if (ch == ')') {
          if (!confirm('*')) {
            errorMessage = E_ASTERISK;
            return false;
          }

          return confirmEnd();
        }

        skipS();

        if (!skipName()) {
          return false;
        }
      }
    }

    if (!scanGroupWithoutOpeningBracket()) {
      return false;
    }

    scanQuantifiers();
    return confirmEnd();
  }

  private boolean confirm(char[] t) {
    if (t.length > ls - ps) {
      return false;
    }

    for (int i = 0; i < t.length; i++) {
      if (t[i] != s[ps + i]) {
        return false;
      }
    } 

    ps += t.length;
    return true;
  }

  private boolean confirm(char ch) {
    if (s[ps] != ch) {
      return false;
    }

    ps++;
    return true;
  }

  private boolean skipName() {
    char ch = s[ps];

    if (!Symbols.isInitialNameChar(ch)) {
      errorMessage = E_NAME;
      return false;
    }

    while (true) {
      ps++;
      ch = s[ps];

      if (!Symbols.isNameChar(ch)) {
        return true;
      }
    }
  }

  private void skipS() {
    while (true) {
      if (!Symbols.isWhitespace(s[ps])) {
        break;
      }

      ps++;
    }
  }

  private boolean scanGroupWithoutOpeningBracket() {
    skipS();

    if (!scanGroupOrName()) {
      return false;
    }

    skipS();
    char op = s[ps];
    ps++;

    if (op == ')') {
      scanQuantifiers();
      return true;
    }

    if ((op != '|') && (op != ',')) {
      errorMessage = E_STROKE_OR_COMMA_OR_BRACKET;
      return false;
    }

    while (true) {
      skipS();

      if (!scanGroupOrName()) {
        return false;
      }

      skipS();
      char ch = s[ps];
      ps++;

      if (ch == ')') {
        scanQuantifiers();
        return true;
      }

      if ((ch != '|') && (ch != ',')) {
        errorMessage = E_STROKE_OR_COMMA_OR_BRACKET;
        return false;
      }

      if (ch != op) {
        errorMessage = E_GROUP_NOT_UNIFORM;
        return false;
      }
    }
  }

  private boolean scanGroupOrName() {
    char ch = s[ps];

    if (ch == '(') {
      ps++;

      if (!scanGroupWithoutOpeningBracket()) {
        return false;
      }
    } else {
      if (!skipName()) {
        return false;
      }

      scanQuantifiers();
    }

    return true;
  }

  private void scanQuantifiers() {
    char ch = s[ps];

    while ((ch == '?') || (ch == '*') || (ch == '+')) {
      ps++;
      ch = s[ps];
    }
  }

  private boolean confirmEnd() {
    skipS();

    if (s[ps] != '\0') {
      errorMessage = E_END;
      return false;
    }

    return true;
  }

  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append(errorMessage).append(" at (zero-based) position ").append(ps).append(" in content model '").append(s, 0, ls).append("'");
    try {
      b.append(" -> ").append(s, 0, ps).append(" << ").append(s[ps]).append(" >> ").append(s, ps + 1, ls - ps - 1);
    } catch (Exception e) {
      //$JL-EXC$
      b.append("[EXCEPTION]");
    }
    return b.toString();
  }

//  public static void testFromKeyboard() throws Exception {
//    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//    ElementContentValidator v = new ElementContentValidator();
//
//    while (true) {
//      LogWriter.getSystemLogWriter().print(" Model : "); //$JL-SYS_OUT_ERR$
//      String s = in.readLine();
//      boolean r = v.check(new CharArray(s));
//      LogWriter.getSystemLogWriter().println(" Result : " + v); //$JL-SYS_OUT_ERR$
//      LogWriter.getSystemLogWriter().println(" Result : " + r); //$JL-SYS_OUT_ERR$
//    }
//  }

  public static void testAutomatic() throws Exception {
    CharArray[] right = {new CharArray("(a)"), new CharArray("(a,b,c)"), new CharArray("(a|b|c)"), new CharArray("(a|(b,c))"), new CharArray("((a|b),c)"), new CharArray("(a+)"), new CharArray("(a)+"), new CharArray("(a?+*)*?+"), new CharArray("(a?,(a|b),c)"), new CharArray("(a|b|c|(d|e)|f)"), new CharArray("(a*,b,c?)"), new CharArray("(a?,a)"), new CharArray("(((((a)))))"), new CharArray("((a))"), new CharArray("((a)+)"), };
    CharArray[] wrong = {new CharArray("(a"), new CharArray("a)"), new CharArray("a*"), new CharArray("()"), new CharArray("(a|b,c)"), new CharArray("(?a)"), };
    ElementContentValidator v = new ElementContentValidator();

    for (int i = 0; i < right.length; i++) {
      if (v.check(right[i])) {
        LogWriter.getSystemLogWriter().print("OK '"); //$JL-SYS_OUT_ERR$
        LogWriter.getSystemLogWriter().print(right[i].toString()); //$JL-SYS_OUT_ERR$
        LogWriter.getSystemLogWriter().print("'  ->  RECOGNIZED"); //$JL-SYS_OUT_ERR$
      } else {
        LogWriter.getSystemLogWriter().print("ERROR '"); //$JL-SYS_OUT_ERR$
        LogWriter.getSystemLogWriter().print(right[i].toString()); //$JL-SYS_OUT_ERR$
        LogWriter.getSystemLogWriter().print("'  ->  "); //$JL-SYS_OUT_ERR$
        LogWriter.getSystemLogWriter().print(v.toString()); //$JL-SYS_OUT_ERR$
      }

      LogWriter.getSystemLogWriter().println("\n"); //$JL-SYS_OUT_ERR$
    } 

    for (int i = 0; i < wrong.length; i++) {
      if (v.check(wrong[i])) {
        LogWriter.getSystemLogWriter().print("ERROR '"); //$JL-SYS_OUT_ERR$
        LogWriter.getSystemLogWriter().print(wrong[i].toString()); //$JL-SYS_OUT_ERR$
        LogWriter.getSystemLogWriter().print("'  ->  RECOGNIZED"); //$JL-SYS_OUT_ERR$
      } else {
        LogWriter.getSystemLogWriter().print("OK '"); //$JL-SYS_OUT_ERR$
        LogWriter.getSystemLogWriter().print(wrong[i].toString()); //$JL-SYS_OUT_ERR$
        LogWriter.getSystemLogWriter().print("'  ->  "); //$JL-SYS_OUT_ERR$
        LogWriter.getSystemLogWriter().print(v.toString()); //$JL-SYS_OUT_ERR$
      }

      LogWriter.getSystemLogWriter().println("\n"); //$JL-SYS_OUT_ERR$
    } 
  }

//  public static void main(String[] args) throws Exception {
//    //testFromKeyboard();
//    testAutomatic();
//  }

}

/*
 [45]  elementdecl ::=  '<!ELEMENT' S Name S contentspec S? '>'
 [46]  contentspec ::=  'EMPTY' | 'ANY' | Mixed | children
 [47]  children ::=  (choice | seq) ('?' | '*' | '+')?
 [48]  cp ::=  (Name | choice | seq) ('?' | '*' | '+')?
 [49]  choice ::=  '(' S? cp ( S? '|' S? cp )* S? ')' [  VC: Proper Group/PE Nesting ]
 [50]  seq ::=  '(' S? cp ( S? ',' S? cp )* S? ')' [  VC: Proper Group/PE Nesting ]
 [51]  Mixed ::=  '(' S? '#PCDATA' (S? '|' S? Name)* S? ')*'
 */

