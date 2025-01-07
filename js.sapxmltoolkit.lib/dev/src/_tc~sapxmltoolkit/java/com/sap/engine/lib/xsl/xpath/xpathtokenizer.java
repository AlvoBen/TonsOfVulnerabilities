package com.sap.engine.lib.xsl.xpath;

import java.util.*;

/**
 * <p>
 *   Does some preprocessing of the XPath queries.
 * </p>
 * <p>
 *   This class is responsible for "tokenizing" the xpath expression, which
 * means splitting it into string tokens, whose concatenation is the
 * initial expression. A token corresponds to the smallest sequence of
 * characters that carries a separate idea, e.g. 'aaa', '::', 'sum', ']', ...
 * </p>
 * <p>
 *   Adds a "" at the last position of the result.
 * </p>
 * <p>
 *   You should create an instance of this class before using it. A single
 * instance is sufficient for tokenizing many strings.
 * </p>
 *
 * @see ETBuilder
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version June 2001
 */
public final class XPathTokenizer {

  private static final char[][] TWO_CHARACTER_TOKENS = {{'!', '='}, {'<', '='}, {'>', '='}, };
  private String expression;
  private char[] e = new char[100];
  private int index;
  private static int[] h1 = new int[255];

  static {
    java.util.Arrays.fill(h1, -1);
    h1['('] = T.OPENING_BRACKET;
    h1[')'] = T.CLOSING_BRACKET;
    h1['['] = T.OPENING_SQUARE_BRACKET;
    h1[']'] = T.CLOSING_SQUARE_BRACKET;
    h1['.'] = T.DOT;
    h1['@'] = T.AT;
    h1[','] = T.COMMA;
    h1['|'] = T.UNSPECIFIED;
    h1['+'] = T.UNSPECIFIED;
    h1['-'] = T.UNSPECIFIED;
    h1['='] = T.UNSPECIFIED;
    h1['<'] = T.UNSPECIFIED;
    h1['>'] = T.UNSPECIFIED;
    h1['*'] = T.UNSPECIFIED;
    h1['$'] = T.DOLLAR;
  }

  private int h1get(char ch) {
    if ((ch < 0) || (ch >= h1.length)) {
      return -1;
    }

    return h1[ch];
    /*
     Integer r = ((Integer) h1.get(new Character(ch)));
     return (r == null) ? -1 : r.intValue();
     */
  }

  public void process(String s, Vector rs, IntVector rt, IntVector pos) throws XPathException {
    tokenize(s, rs, rt, pos);
  }

  private void tokenize(String expressionString, Vector vs, IntVector vt, IntVector pos) throws XPathException {
    expression = expressionString;
    int l = expressionString.length();

    if (e.length < l + 1) {
      e = new char[l + 10];
    }

    expressionString.getChars(0, l, e, 0);
    e[l] = ' ';
    index = 0;
    vs.clear(); // Tokens of the expression
    vt.clear(); // Their types
    pos.clear(); // Contains starting positions of tokens in the initial query

    while (index < l) {
      char ch = e[index];
      char ch1 = e[index + 1];
      int oldIndex = index;

      if (Symbols.isWhitespace(ch)) {
        index++;
        continue;
      }

      pos.add(index);

      if (Symbols.isInitialCharForQName(ch)) {
        skipQName();
        vs.addElement(expression.substring(oldIndex, index));
        vt.add(T.QNAME);
        continue;
      }

      if ((ch == '.') && (ch1 == '.')) {
        vs.addElement("..");
        vt.add(T.DOUBLE_DOT);
        index += 2;
        continue;
      }

      if ((ch == ':') && (ch1 == ':')) {
        vs.addElement("::");
        vt.add(T.DOUBLE_COLON);
        index += 2;
        continue;
      }

      if ((ch == '/') && (ch1 == '/')) {
        vs.addElement("//");
        vt.add(T.DOUBLE_SLASH);
        index += 2;
        continue;
      }

      if (isTwoCharacterToken(ch, ch1)) {
        vs.addElement("" + ch + ch1);
        vt.add(T.UNSPECIFIED);
        index += 2;
        continue;
      }

      if (ch == '/') {
        vs.addElement("/");
        vt.add(T.SLASH);
        index++;
        continue;
      }

      if (ch == '.') {
        if (Symbols.isDigit(ch1)) {
          index += 2;
          skipDigits();
          vs.addElement(expression.substring(oldIndex, index));
          vt.add(T.NUMBER);
          continue;
        }
      }

      if (Symbols.isDigit(ch)) {
        skipDigits();

        if (e[index] == '.') {
          if (Symbols.isDigit(expression.charAt(index + 1))) {
            index += 2;
            skipDigits();
          }
        }

        vs.addElement(expression.substring(oldIndex, index));
        vt.add(T.NUMBER);
        continue;
      }

      if ((ch == '"') || (ch == '\'')) {
        int p = expression.indexOf(ch, index + 1);

        if (p == -1) {
          throw new XPathException("Could not process XPath query: " + expressionString + ". Reason: Literal started at position " + index + " in the query has not been closed");
        }

        vs.addElement(expression.substring(index, p + 1));
        vt.add(T.LITERAL);
        index = p + 1;
        continue;
      }

      int a = h1get(ch);

      if (a != -1) {
        vs.addElement(String.valueOf(ch));
        vt.add(a);
        index++;
        continue;
      }

      throw new XPathException("Character not allowed in XPath, '" + ch + "'");
    }

    vs.addElement("");
    vt.add(T.END);
    pos.add(l);
  }

  private void skipDigits() {
    while (true) {
      char ch = e[index];

      if (!Symbols.isDigit(ch)) {
        break;
      }

      index++;
    }
  }

  private void skipQName() {
    skipNCName();

    if (e[index] == ':') {
      char ch1 = e[index + 1];

      if (Symbols.isInitialCharForQName(ch1)) {
        index++;
        skipNCName();
      } else if (ch1 == '*') {
        index += 2; // We assume <NCName>:* is a correct <QName>
      }
    }
  }

  private void skipNCName() {
    while (true) {
      char ch = e[index];

      if (!Symbols.isCharForQName(ch)) {
        return;
      }

      index++;
    }
  }

  private boolean isTwoCharacterToken(char a, char b) {
    for (int i = 0; i < TWO_CHARACTER_TOKENS.length; i++) {
      if ((TWO_CHARACTER_TOKENS[i][0] == a) && (TWO_CHARACTER_TOKENS[i][1] == b)) {
        return true;
      }
    } 

    return false;
  }

}

