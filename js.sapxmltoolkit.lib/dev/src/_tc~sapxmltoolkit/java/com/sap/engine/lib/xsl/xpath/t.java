package com.sap.engine.lib.xsl.xpath;

import java.util.*;

/**
 *   Contains constants used for identifying the types of
 * various objects.
 *   Cannot be instantiated.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version June 2001
 */
public final class T {

  static final int UNSPECIFIED = 0;
  // Basic
  static final int NUMBER = 1;
  static final int STRING = 2;
  static final int BOOLEAN = 3;
  static final int NODESET = 4;
  // A sequence of characters matching the QName production
  static final int QNAME = 5;
  // A sequence of characters, starting and ending
  // with either "'" or '"'
  static final int LITERAL = 6;
  // Parts of the expression tree
  static final int VARIABLE = 7;
  //static final int PATH                   = 8;
  //static final int STEP                   = 9;
  static final int UNARY_OPERATOR = 10;
  static final int BINARY_OPERATOR = 11;
  static final int FUNCTION = 12;
  // Special symbols that are not operators
  static final int OPENING_BRACKET = 13;
  static final int CLOSING_BRACKET = 14;
  static final int OPENING_SQUARE_BRACKET = 15;
  static final int CLOSING_SQUARE_BRACKET = 16;
  static final int COMMA = 17;
  static final int DOUBLE_COLON = 18;
  static final int DOLLAR = 19;
  // Symbols that must be expanded as abbreviations
  static final int AT = 20;
  static final int DOT = 21;
  static final int DOUBLE_DOT = 22;
  static final int DOUBLE_SLASH = 23;
  // Slashes
  static final int SLASH = 24;
  static final int UNARY_SLASH = 25;
  static final int BINARY_SLASH = 26;
  static final int END = 100;
  static Hashtable h = new Hashtable();

  static {
    h.put(new Integer(UNSPECIFIED), "UNSPECIFIED");
    h.put(new Integer(NUMBER), "NUMBER");
    h.put(new Integer(STRING), "STRING");
    h.put(new Integer(BOOLEAN), "BOOLEAN");
    h.put(new Integer(NODESET), "NODESET");
    h.put(new Integer(QNAME), "QNAME");
    h.put(new Integer(LITERAL), "LITERAL");
    h.put(new Integer(VARIABLE), "VARIABLE");
    h.put(new Integer(UNARY_OPERATOR), "UNARY_OPERATOR");
    h.put(new Integer(BINARY_OPERATOR), "BINARY_OPERATOR");
    h.put(new Integer(FUNCTION), "FUNCTION");
    h.put(new Integer(OPENING_BRACKET), "OPENING_BRACKET");
    h.put(new Integer(CLOSING_BRACKET), "CLOSING_BRACKET");
    h.put(new Integer(CLOSING_SQUARE_BRACKET), "CLOSING_SQUARE_BRACKET");
    h.put(new Integer(COMMA), "COMMA");
    h.put(new Integer(DOUBLE_COLON), "DOUBLE_COLON");
    h.put(new Integer(DOLLAR), "DOLLAR");
    h.put(new Integer(AT), "AT");
    h.put(new Integer(DOT), "DOT");
    h.put(new Integer(DOUBLE_DOT), "DOUBLE_DOT");
    h.put(new Integer(DOUBLE_SLASH), "DOUBLE_SLASH");
    h.put(new Integer(SLASH), "SLASH");
    h.put(new Integer(UNARY_SLASH), "UNARY_SLASH");
    h.put(new Integer(BINARY_SLASH), "BINARY_SLASH");
  }

  private T() {

  }

  static String stringForType(int typeId) {
    return (String) h.get(new Integer(typeId));
  }

}

