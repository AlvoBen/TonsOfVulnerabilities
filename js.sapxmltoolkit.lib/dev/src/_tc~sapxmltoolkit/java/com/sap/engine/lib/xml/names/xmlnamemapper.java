package com.sap.engine.lib.xml.names;

import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * JAXRPC-compatible name convertor
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      October 2001
 */
public final class XMLNameMapper {

  private XMLNameWordHandlerImpl2 h = new XMLNameWordHandlerImpl2();

  public XMLNameMapper() {

  }

  public static boolean isPunctuation(char ch) {
    return ((ch == '-') || (ch == '.') || (ch == ':') || (ch == '_') || (ch == '\u00b7') // Middle Dot
    // The correct one
    //return ((ch == '-') || (ch == '.') || (ch == ':') || (ch == '\u00b7') // Middle Dot
    || (ch == '\u0387') // Greek Ano Teleia
    || (ch == '\u06dd') // Arabic End Of Ayah
    || (ch == '\u06de') // Arabic Start Of Rub El Hizb
    );
  }

  public static boolean isMark(char ch) {
    return Character.isJavaIdentifierPart(ch) && !Character.isLetter(ch) && !Character.isDigit(ch);
  }

  /**
   * Returns 0 if there is no word break between ch and ch1.
   * Returns 1 if there is a word break, and none of ch and ch1 is a punctuation character.
   * Returns 2 if any of ch and ch1 is a punctuation character.
   */
  public static int isWordBreak(char ch, char ch1, char ch2) {
    // lower | [^lower]
    if (Character.isLowerCase(ch) && !Character.isLowerCase(ch1)) {
      return 1;
    }

    // digit | [^digit]
    // [^digit] digit
    if (Character.isDigit(ch) != Character.isDigit(ch1)) {
      return 1;
    }

    // letter | [^letter]
    // [^letter] letter
    if (Character.isLetter(ch) != Character.isLetter(ch1)) {
      return 1;
    }

    // upper | upper lower
    if (Character.isUpperCase(ch) && Character.isUpperCase(ch1) && Character.isLowerCase(ch2)) {
      return 1;
    }

    if (isPunctuation(ch) || isPunctuation(ch1)) {
      return 2;
    }

    return 0;
  }

  public static void parseWordList(char[] a, int start, int end, XMLNameWordHandler h) {
    int p = start;
    h.start();

    OUTER: while (true) {
      // Skip any punctuation characters
      INNER: while (true) {
        if (p >= end) {
          break  OUTER;
        }

        if (isPunctuation(a[p])) {
          p++;
        } else {
          break  INNER;
        }
      }

      // Read the next word
      int q = p;
      char ch = a[p];

      if (Character.isDigit(ch)) {
        // Read a string consisting only of digits
        p++;

        while ((p < end) && (Character.isDigit(a[p]))) {
          p++;
        }

        h.word(a, q, p);
      } else if (Character.isJavaIdentifierPart(ch)
      /* might be a 'mark', isLetter is not enough */
      ) {
        // Read a string of letters, ending with (lower, ^lower) or (upper, upper, lower)
        INNER: while (true) {
          p++;

          if ((p >= end) || !Character.isJavaIdentifierPart(a[p])) {
            break  INNER; // (letter, ^letter)
          }

          if (Character.isLowerCase(ch) && !Character.isLowerCase(a[p])) {
            break  INNER; // (lower, ^lower)
          }

          if (Character.isUpperCase(ch) && Character.isUpperCase(a[p])) {
            if ((p + 1 < end) && Character.isLowerCase(a[p + 1])) {
              break  INNER; // (upper, upper, lower)
            }
          }

          ch = a[p];
        }

        a[q] = Character.toUpperCase(a[q]);
        h.word(a, q, p);
      } else {
        p++;
      }
    }

    h.end();
  }

  public String toMethodIdentifier(String s) {
    h.setMode(h.MODE_METHOD);
    parseWordList(s, h);
    return h.toString();
  }

  public String toClassIdentifier(String s) {
    h.setMode(h.MODE_CLASS);
    parseWordList(s, h);
    return h.toString();
  }

  public String toConstantIdentifier(String s) {
    h.setMode(h.MODE_CONSTANT);
    parseWordList(s, h);
    return h.toString();
  }

  public String toXMLStyleIdentifier(String s) {
    h.setMode(h.MODE_XML_STYLE);
    parseWordList(s, h);
    return h.toString();
  }

  public void toMethodIdentifier(String s, CharArray ca) {
    h.setMode(h.MODE_METHOD);
    parseWordList(s, h);
    h.loadInto(ca);
  }

  public void toClassIdentifier(String s, CharArray ca) {
    h.setMode(h.MODE_CLASS);
    parseWordList(s, h);
    h.loadInto(ca);
  }

  public void toConstantIdentifier(String s, CharArray ca) {
    h.setMode(h.MODE_CONSTANT);
    parseWordList(s, h);
    h.loadInto(ca);
  }

  public void toXMLStyleIdentifier(String s, CharArray ca) {
    h.setMode(h.MODE_XML_STYLE);
    parseWordList(s, h);
    h.loadInto(ca);
  }

  // Static version
  public static String toMethodIdentifierStatic(String s) {
    XMLNameWordHandlerImpl2 h = new XMLNameWordHandlerImpl2();
    h.setMode(h.MODE_METHOD);
    parseWordList(s, h);
    return h.toString();
  }

  public static String toClassIdentifierStatic(String s) {
    XMLNameWordHandlerImpl2 h = new XMLNameWordHandlerImpl2();
    h.setMode(h.MODE_CLASS);
    parseWordList(s, h);
    return h.toString();
  }

  public static String toConstantIdentifierStatic(String s) {
    XMLNameWordHandlerImpl2 h = new XMLNameWordHandlerImpl2();
    h.setMode(h.MODE_CONSTANT);
    parseWordList(s, h);
    return h.toString();
  }

  public static String toXMLStyleIdentifierStatic(String s) {
    XMLNameWordHandlerImpl2 h = new XMLNameWordHandlerImpl2();
    h.setMode(h.MODE_XML_STYLE);
    parseWordList(s, h);
    return h.toString();
  }

  public static void toMethodIdentifierStatic(String s, CharArray ca) {
    XMLNameWordHandlerImpl2 h = new XMLNameWordHandlerImpl2();
    h.setMode(h.MODE_METHOD);
    parseWordList(s, h);
    h.loadInto(ca);
  }

  public static void toClassIdentifierStatic(String s, CharArray ca) {
    XMLNameWordHandlerImpl2 h = new XMLNameWordHandlerImpl2();
    h.setMode(h.MODE_CLASS);
    parseWordList(s, h);
    h.loadInto(ca);
  }

  public static void toConstantIdentifierStatic(String s, CharArray ca) {
    XMLNameWordHandlerImpl2 h = new XMLNameWordHandlerImpl2();
    h.setMode(h.MODE_CONSTANT);
    parseWordList(s, h);
    h.loadInto(ca);
  }

  public static void toXMLStyleIdentifierStatic(String s, CharArray ca) {
    XMLNameWordHandlerImpl2 h = new XMLNameWordHandlerImpl2();
    h.setMode(h.MODE_XML_STYLE);
    parseWordList(s, h);
    h.loadInto(ca);
  }

  public static void parseWordList(String s, XMLNameWordHandler h) {
    parseWordList(s.toCharArray(), 0, s.length(), h);
  }

//  public static void main(String[] args) {
//    String x = null;
//    x="mixedCaseName";
//    LogWriter.getSystemLogWriter().print(x);
//    LogWriter.getSystemLogWriter().print(" ");
//    LogWriter.getSystemLogWriter().print(toClassIdentifierStatic(x));
//    LogWriter.getSystemLogWriter().print(" ");
//    LogWriter.getSystemLogWriter().print(toMethodIdentifierStatic(x));
//    LogWriter.getSystemLogWriter().println();
//    x="name-with-dashes";
//    LogWriter.getSystemLogWriter().print(x);
//    LogWriter.getSystemLogWriter().print(" ");
//    LogWriter.getSystemLogWriter().print(toClassIdentifierStatic(x));
//    LogWriter.getSystemLogWriter().print(" ");
//    LogWriter.getSystemLogWriter().print(toMethodIdentifierStatic(x));
//    LogWriter.getSystemLogWriter().println();
//    x="name_with_underscore";
//    LogWriter.getSystemLogWriter().print(x);
//    LogWriter.getSystemLogWriter().print(" ");
//    LogWriter.getSystemLogWriter().print(toClassIdentifierStatic(x));
//    LogWriter.getSystemLogWriter().print(" ");
//    LogWriter.getSystemLogWriter().print(toMethodIdentifierStatic(x));
//    LogWriter.getSystemLogWriter().println();
//    x="other_punct�chars";
//    LogWriter.getSystemLogWriter().print(x);
//    LogWriter.getSystemLogWriter().print(" ");
//    LogWriter.getSystemLogWriter().print(toClassIdentifierStatic(x));
//    LogWriter.getSystemLogWriter().print(" ");
//    LogWriter.getSystemLogWriter().print(toMethodIdentifierStatic(x));
//    LogWriter.getSystemLogWriter().println();
//    x="Answer42";
//    LogWriter.getSystemLogWriter().print(x);
//    LogWriter.getSystemLogWriter().print(" ");
//    LogWriter.getSystemLogWriter().print(toClassIdentifierStatic(x));
//    LogWriter.getSystemLogWriter().print(" ");
//    LogWriter.getSystemLogWriter().print(toMethodIdentifierStatic(x));
//    LogWriter.getSystemLogWriter().println();
//  }
}

