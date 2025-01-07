/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.implserver;

import java.util.*;

import com.sap.engine.lib.util.ConcurrentArrayObject;

import javax.naming.directory.InvalidSearchFilterException;
import javax.naming.OperationNotSupportedException;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import com.sap.engine.services.jndi.JNDIFrame;

import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;

/**
 * Filter managment
 *
 * @author Petio Petev
 * @version 4.00
 */
public class SearchFilter implements AttrFilter {

  private final static Location LOG_LOCATION = Location.getLocation(SearchFilter.class);

  /**
   * Stores the filter
   */
  String filter;
  /**
   * Stores the position
   */
  int pos;
  /**
   * Stores the root of the filter
   */
  private StringFilter rootFilter;
  /**
   * Debug constant
   */
  protected static final boolean debug = false;
  /**
   * Begin filter constant
   */
  protected static final char BEGIN_FILTER_TOKEN = 40; // '('
  /**
   * End filter constant
   */
  protected static final char END_FILTER_TOKEN = 41; // ')'
  /**
   * And constant
   */
  protected static final char AND_TOKEN = 38; // '&'
  /**
   * Or constant
   */
  protected static final char OR_TOKEN = 124; // '|'
  /**
   * Not constant
   */
  protected static final char NOT_TOKEN = 33; // '!'
  /**
   * Equal constant
   */
  protected static final char EQUAL_TOKEN = 61; // '='
  /**
   * Approx constant
   */
  protected static final char APPROX_TOKEN = 126; // '~'
  /**
   * Less constant
   */
  protected static final char LESS_TOKEN = 60; // '<'
  /**
   * Greater constant
   */
  protected static final char GREATER_TOKEN = 62; // '>'
  /**
   * Extend constant
   */
  protected static final char EXTEND_TOKEN = 58; // ':'
  /**
   * Wildcard constant
   */
  protected static final char WILDCARD_TOKEN = 42; // '*'
  /**
   * And constant
   */
  static final int EQUAL_MATCH = 1;
  /**
   * Approx constant
   */
  static final int APPROX_MATCH = 2;
  /**
   * Greater constant
   */
  static final int GREATER_MATCH = 3;
  /**
   * Less constant
   */
  static final int LESS_MATCH = 4;

  /**
   * Compound Filter class
   */
  final class CompoundFilter implements StringFilter {

    /**
     * Checks the attributes
     *
     * @param attributes Attributes to check
     * @return "true" if the attributes are O.K.
     * @throws javax.naming.NamingException Thrown if a problem occurs.
     */
    public boolean check(Attributes attributes) throws NamingException {
      for (int i = 0; i < subFilters.size(); i++) {
        StringFilter stringfilter = (StringFilter) subFilters.elementAt(i);

        if (stringfilter.check(attributes) != polarity) {
          return polarity ^ true;
        }
      }

      return polarity;
    }

    /**
     * Parses the filter
     *
     * @throws javax.naming.directory.InvalidSearchFilterException Thrown if a problem occurs.
     */
    public void parse() throws javax.naming.directory.InvalidSearchFilterException {
      consumeChar();

      for (; getCurrentChar() != ')'; skipWhiteSpace()) {
        StringFilter stringfilter = createNextFilter();
        subFilters.addElement(stringfilter);
      }
    }

    /**
     * Contains the subfilters
     */
    private ConcurrentArrayObject subFilters = null;
    /**
     * The polarity flag
     */
    private boolean polarity = false;

    CompoundFilter(boolean flag) {
      subFilters = new ConcurrentArrayObject();
      polarity = flag;
    }

  }

  /**
   * Not Filter class
   */
  final class NotFilter implements StringFilter {

    /**
     * Checks the attributes
     *
     * @param attributes Attributes to check
     * @return "true" if the attributes are O.K.
     * @throws NamingException Thrown if a problem occurs.
     */
    public boolean check(Attributes attributes) throws NamingException {
      return filter.check(attributes) ^ true;
    }

    /**
     * Parses the filter
     *
     * @throws javax.naming.directory.InvalidSearchFilterException Thrown if a problem occurs.
     */
    public void parse() throws javax.naming.directory.InvalidSearchFilterException {
      consumeChar();
      filter = createNextFilter();
    }

    /**
     * Contains the filter
     */
    private StringFilter filter = null;

    NotFilter() {

    }

  }

  /**
   * Not Filter class
   */
  final class AtomicFilter implements StringFilter {

    /**
     * Checks the attributes
     *
     * @param attributes Attributes to check
     * @return "true" if the attributes are O.K.
     */
    public boolean check(Attributes attributes) {
      NamingEnumeration namingenumeration = null;
      try {
        Attribute attribute = attributes.get(attrID);

        if (attribute == null) {
          return false;
        }

        namingenumeration = attribute.getAll();
      } catch (NamingException ex) {
        LOG_LOCATION.traceThrowableT(Severity.PATH, "", ex);
        return false;
      }

      while (namingenumeration.hasMoreElements()) {
        String s = namingenumeration.nextElement().toString();

        switch (matchType) {
          default: {
            break;
          }
          case EQUAL_MATCH:
          case APPROX_MATCH: {
            if (substringMatch(value, s)) {
              return true;
            }

            break;
          }
          case GREATER_MATCH: {
            if (s.compareTo(value) >= 0) {
              return true;
            }

            break;
          }
          case LESS_MATCH: {
            if (s.compareTo(value) <= 0) {
              return true;
            }

            break;
          }
        }
      }

      return false;
    }

    /**
     * Parses the filter
     *
     * @throws javax.naming.directory.InvalidSearchFilterException Thrown if a problem occurs.
     */
    public void parse() throws javax.naming.directory.InvalidSearchFilterException {
      skipWhiteSpace();
      try {
        int i = relIndexOf(END_FILTER_TOKEN);
        int j = relIndexOf(EQUAL_TOKEN);
        char c = relCharAt(j - 1);

        switch (c) {
          case APPROX_TOKEN: {
            /* '~' */
            matchType = 2;
            attrID = relSubstring(0, j - 1);
            value = relSubstring(j + 1, i);
            break;
          }
          case GREATER_TOKEN: {
            /* '>' */
            matchType = 3;
            attrID = relSubstring(0, j - 1);
            value = relSubstring(j + 1, i);
            break;
          }
          case LESS_TOKEN: {
            /* '<' */
            matchType = 4;
            attrID = relSubstring(0, j - 1);
            value = relSubstring(j + 1, i);
            break;
          }
          case EXTEND_TOKEN: {
            /* ':' */
            throw new OperationNotSupportedException("Extensible match not supported in search operations.");
          }
          default: {
            matchType = 1;
            attrID = relSubstring(0, j);
            value = relSubstring(j + 1, i);
            break;
          }
        }

        attrID = attrID.trim();
        value = value.trim();
        consumeChars(i);
      } catch (Exception exception) {
        LOG_LOCATION.traceThrowableT(Severity.PATH, "", exception);
        InvalidSearchFilterException isfe = new InvalidSearchFilterException("Unable to parse character " + new Integer(pos) + " in " + filter + ".");
        throw isfe;
      }
    }

    /**
     * Determines if substring matches
     *
     * @param s String to scan
     * @param s1 String to scan
     * @return "true" if a match is found
     */
    private boolean substringMatch(String s, String s1) {
      if (s.equals(new Character('*').toString())) {
        return true;
      }

      if (s.indexOf(WILDCARD_TOKEN) == -1) {
        return s.equalsIgnoreCase(s1);
      }

      int i = 0;
      StringTokenizer stringtokenizer = new StringTokenizer(s, "*", false);

      if (s.charAt(0) != '*' && !s1.toString().toLowerCase().startsWith(stringtokenizer.nextToken().toLowerCase())) {
        return false;
      }

      while (stringtokenizer.hasMoreTokens()) {
        String s2 = stringtokenizer.nextToken();
        i = s1.toLowerCase().indexOf(s2.toLowerCase(), i);

        if (i == -1) {
          return false;
        }

        i += s2.length();
      }

      return s.charAt(s.length() - 1) == '*' || i == s1.length();
    }

    /**
     * ID of the attribute
     */
    private String attrID;
    /**
     * Value of the attribute
     */
    private String value;
    /**
     * Match type
     */
    private int matchType;

    AtomicFilter() {

    }

  }

  /**
   * Constructor
   *
   * @param s Filter to use
   * @throws javax.naming.directory.InvalidSearchFilterException Thrown if a problem occurs.
   */
  public SearchFilter(String s) throws javax.naming.directory.InvalidSearchFilterException {
    filter = s;
    pos = 0;
    normalizeFilter();
    rootFilter = createNextFilter();
  }

  /**
   * Checks the attributes
   *
   * @param attributes Attributes to check
   * @return "true" if the attributes are O.K.
   * @throws NamingException Thrown if a problem occurs.
   */
  public boolean check(Attributes attributes) throws NamingException {
    if (attributes == null) {
      return false;
    } else {
      return rootFilter.check(attributes);
    }
  }

  /**
   * Skips one char
   */
  protected void consumeChar() {
    pos++;
  }

  /**
   * Skips several chars
   *
   * @param i Chars to skip
   */
  protected void consumeChars(int i) {
    pos += i;
  }

  /**
   * Creates a new filter
   *
   * @return The requester filter
   * @throws javax.naming.directory.InvalidSearchFilterException Thrown if a problem occurs.
   */
  protected StringFilter createNextFilter() throws javax.naming.directory.InvalidSearchFilterException {
    skipWhiteSpace();
    Object obj;
    try {
      if (getCurrentChar() != '(') {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("Expected '(' at position " + pos + ".");
        }
        throw new InvalidSearchFilterException("Expected '(' at position " + new Integer(pos) + ".");
      }

      consumeChar();
      skipWhiteSpace();

      switch (getCurrentChar()) {
        case AND_TOKEN: {
          /* '&' */
          obj = new CompoundFilter(true);
          ((StringFilter) obj).parse();
          break;
        }
        case OR_TOKEN: {
          /* '|' */
          obj = new CompoundFilter(false);
          ((StringFilter) obj).parse();
          break;
        }
        case NOT_TOKEN: {
          /* '!' */
          obj = new NotFilter();
          ((StringFilter) obj).parse();
          break;
        }
        default: {
          obj = new AtomicFilter();
          ((StringFilter) obj).parse();
          break;
        }
      }

      skipWhiteSpace();

      if (getCurrentChar() != ')') {
        throw new InvalidSearchFilterException("Expected ')' at position " + new Integer(pos) + ".");
      }

      consumeChar();
    } catch (javax.naming.directory.InvalidSearchFilterException e) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      InvalidSearchFilterException isfe = new InvalidSearchFilterException("Exception during processing search filter " + filter + ".");
      isfe.setRootCause(e);
      throw isfe;
    } catch (Exception exception) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", exception);
      InvalidSearchFilterException isfe = new InvalidSearchFilterException("Unable to parse character " + new Integer(pos) + " in " + filter + ".");
      isfe.setRootCause(exception);
      throw isfe;
    }
    return ((StringFilter) obj);
  }

  /**
   * Finds normal (not ESC) chars
   *
   * @param c Char to find
   * @param s String to find in
   * @param i Starting index
   * @return Position of the chars
   */
  public static int findUnescaped(char c, String s, int i) {
    int k;

    for (int j = s.length(); i < j; i = k + 1) {
      k = s.indexOf(c, i);

      if (k == i || k == -1 || s.charAt(k - 1) != '\\') {
        return k;
      }
    }

    return -1;
  }

  /**
   * Performs a formatting
   *
   * @param s Source to be formatted
   * @param aobj Objects to use
   * @throws NamingException Thrown if a problem occurs.
   */
  public static String format(String s, Object aobj[]) throws NamingException {
    int j = 0;
    int k = 0;
    StringBuffer stringbuffer = new StringBuffer(s.length());

    while ((j = findUnescaped('{', s, k)) >= 0) {
      int l = j + 1;
      int i1 = s.indexOf('}', l);

      if (i1 < 0) {
        throw new InvalidSearchFilterException("Expected a closing symbol for the '{' in " + s + ".");
      }

      int i;
      try {
        i = Integer.parseInt(s.substring(l, i1));
      } catch (NumberFormatException ex) {
        InvalidSearchFilterException isfe = new InvalidSearchFilterException("Integer expected between '{','}': " + s);
        isfe.setRootCause(ex);
        throw isfe;
      }

      if (i >= aobj.length) {
        throw new InvalidSearchFilterException("Number " + new Integer(i) + " exceeds the number of arguments in the list.");
      }

      stringbuffer.append(s.substring(k, j)).append(getEncodedStringRep(aobj[i]));
      k = i1 + 1;
    }

    if (k < s.length()) {
      stringbuffer.append(s.substring(k));
    }

    return stringbuffer.toString();
  }

  /**
   * Gets the current char
   *
   * @return Current char
   */
  protected char getCurrentChar() {
    return filter.charAt(pos);
  }

  /**
   * Gets encoded string
   *
   * @param obj Object to use
   * @return Encoded string
   */
  private static String getEncodedStringRep(Object obj) {
    if (obj == null) {
      return null;
    } else if (obj instanceof byte[]) {
      byte abyte0[] = (byte[]) obj;
      StringBuffer stringbuffer = new StringBuffer(abyte0.length * 3);

      for (int j = 0; j < abyte0.length; j++) {
        stringbuffer.append('\\');
        hexDigit(stringbuffer, abyte0[j]);
      }

      return stringbuffer.toString();
    }

    String s;

    if (!(obj instanceof String)) {
      s = obj.toString();
    } else {
      s = (String) obj;
    }

    int i = s.length();
    StringBuffer stringbuffer1 = new StringBuffer(i);

    for (int k = 0; k < i; k++) {
      char c;

      switch (c = s.charAt(k)) {
        case WILDCARD_TOKEN: {
          /* '*' */
          stringbuffer1.append("\\2a");
          break;
        }
        case BEGIN_FILTER_TOKEN: {
          /* '(' */
          stringbuffer1.append("\\28");
          break;
        }
        case END_FILTER_TOKEN: {
          /* ')' */
          stringbuffer1.append("\\29");
          break;
        }
        case 92: {
          /* '\\' */
          stringbuffer1.append("\\5c");
          break;
        }
        case 0: {
          /* '\0' */
          stringbuffer1.append("\\00");
          break;
        }
        default: {
          stringbuffer1.append(c);
          break;
        }
      }
    }

    return stringbuffer1.toString();
  }

  /**
   * Gets a hex digit
   *
   * @param stringbuffer String representation
   * @param byte0 Hex byte
   */
  private static void hexDigit(StringBuffer stringbuffer, byte byte0) {
    char c = (char) (byte0 >> 4 & 0xf);

    if (c > '\t') {
      c = (char) ((c - 10) + 65);
    } else {
      c += '0';
    }

    stringbuffer.append(c);
    c = (char) (byte0 & 0xf);

    if (c > '\t') {
      c = (char) ((c - 10) + 65);
    } else {
      c += '0';
    }

    stringbuffer.append(c);
  }

  /**
   * Normalizes the filter
   */
  protected void normalizeFilter() {
    skipWhiteSpace();

    if (getCurrentChar() != '(') {
      filter = '(' + filter + ')';
    }
  }

  /**
   * Gets a char at relative position
   *
   * @param i Relative position
   * @return Relative result
   */
  protected char relCharAt(int i) {
    return filter.charAt(pos + i);
  }

  /**
   * Determines relative index
   *
   * @param i Relative position
   * @return Relative result
   */
  protected int relIndexOf(int i) {
    return filter.indexOf(i, pos) - pos;
  }

  /**
   * Gets a substring between relative positions
   *
   * @param i Relative position
   * @return Requested string
   */
  protected String relSubstring(int i, int j) {
    return filter.substring(i + pos, j + pos);
  }

  /**
   * Skips whitespaces
   */
  private void skipWhiteSpace() {
    for (; Character.isWhitespace(getCurrentChar()); consumeChar()) {
      ;
    }
  }

}

