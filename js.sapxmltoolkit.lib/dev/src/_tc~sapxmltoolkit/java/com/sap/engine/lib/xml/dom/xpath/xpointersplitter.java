package com.sap.engine.lib.xml.dom.xpath;

import java.util.Vector;

import com.sap.engine.lib.log.LogWriter;

/**
 * @author  Nick Nickolov, nick_nickolov@abv.bg
 * @version Feb 5, 2002, 9:06:21 PM
 */
public final class XPointerSplitter {

  private XPointerSplitter() {

  }

  /**
   *      (+s, -schemes, -expressions)
   *
   * Splits the string into scheme-expr pairs
   * and fills them in the Vectors passed as arguments.
   */
  public static final void split(String s, Vector schemes, Vector expressions) throws XPathException {
    schemes.clear();
    expressions.clear();

    while (true) {
      s = stripLeftWhitespace(s);

      if (s.length() == 0) {
        break;
      }

      int p = s.indexOf('(');
      int q = s.indexOf(')');

      if (p == -1) {
        if (q != -1) {
          // ')' occurs but '(' does not
          throw new XPathException(XPathException.INVALID_EXPRESSION_ERR, "Brackets not balanced in an XPointer expression, " + "')' occurs but '(' does not. " + "expression='" + s + "'");
        }

        // It's a bare name
        schemes.add("(bare)");
        expressions.add(stripRightWhitespace(s));
        return;
      }

      if (q < p) {
        // ')' before '('
        throw new XPathException(XPathException.INVALID_EXPRESSION_ERR, "Brackets not balanced in an XPointer expression, " + "')' before '('. " + "expression='" + s + "'");
      }

      int matchingBracket = findMatchingBracket(s, p); // might throw if unbalanced
      schemes.add(s.substring(0, p));
      expressions.add(s.substring(p + 1, matchingBracket));
      s = s.substring(matchingBracket + 1);
    }
  }

  private static final String stripLeftWhitespace(String s) {
    int p = 0;
    int ls = s.length();

    while (p < ls) {
      char ch = s.charAt(p);

      if ((ch != ' ') && (ch != '\t') && (ch != '\r') && (ch != '\n')) {
        break;
      }

      p++;
    }

    return (p == 0) ? s : s.substring(p);
  }

  private static final String stripRightWhitespace(String s) {
    int ls = s.length();
    int p = ls - 1;

    while (p >= 0) {
      char ch = s.charAt(p);

      if ((ch != ' ') && (ch != '\t') && (ch != '\r') && (ch != '\n')) {
        break;
      }

      p--;
    }

    return (p == ls - 1) ? s : s.substring(0, p + 1);
  }

  private static final int findMatchingBracket(String s, int indexOfBracket) throws XPathException {
    int level = 1;
    int i = indexOfBracket + 1;
    int ls = s.length();

    while (i < ls) {
      char ch = s.charAt(i);

      if (ch == '(') {
        level++;
      } else if (ch == ')') {
        level--;

        if (level == 0) {
          return i;
        }
      }

      i++;
    }

    throw new XPathException(XPathException.INVALID_EXPRESSION_ERR, "Brackets not balanced in an XPointer expression.");
  }

  public static void main(String[] args) throws Exception {
    Vector vSchemes = new Vector();
    Vector vExpressions = new Vector();
    split("xmlns(xs='schema') xpointer(lksd/1/2/3/4)", vSchemes, vExpressions);
    LogWriter.getSystemLogWriter().println(vSchemes.toString());
    LogWriter.getSystemLogWriter().println(vExpressions.toString());
  }

}

