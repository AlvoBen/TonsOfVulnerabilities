/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.xsl.xslt;

/**
 *
 * @author Vladislav Velkov   e-mail: vladislav.velkov@sap.com
 * @version 1.0
 *
 *
 * First Edition: 14.07.2001
 *
 */
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.DTM;
import com.sap.engine.lib.xsl.xpath.ETItem;
import com.sap.engine.lib.xsl.xpath.ETLocationStep;
import com.sap.engine.lib.xsl.xpath.ETObject;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XNumber;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

public class XSLNumber extends XSLContentNode {

  public static final byte LEVEL_DEFAULT = 1;
  public static final byte LEVEL_SINGLE = 1;
  public static final byte LEVEL_MULTIPLE = 2;
  public static final byte LEVEL_ANY = 3;
  public static final byte FORMAT_DEFAULT = 1;
  public static final byte FORMAT_NUMERIC = 1;
  public static final byte FORMAT_ROMAN = 2;
  public static final byte FORMAT_ALPHABETIC = 3;
  public static final boolean WITH_CAPITALS = true; // for Roman and alphabetic
  public static final byte LETTER_VALUE_ALPHABETIC = 1;
  public static final byte LETTER_VALUE_TRADITIONAL = 2;
  public static final boolean ALPHANUMERIC = true;
  public static final boolean NON_ALPHANUMERIC = false;
  public static final int ROOT_NODE = 0;
  private int rootelement = -1;
  private String value = null;
  private String lettervalue = null;
  private String lang = null;
  private byte level = LEVEL_DEFAULT;
  private Vector formattokens;
  private ETObject etcount = null;
  private ETObject etfrom = null;
  private char groupingseparator = ' ';
  private int groupingsize = 0;
  private boolean separatorspecified = false;
  private boolean sizespecified = false;
  private boolean countspecified = false;
  private boolean fromspecified = false;
  private ETObject eto = null;

  /**
   * Contains information about the xsl:number format.
   */
  private class Alphanumeric {

    boolean capitals; // for alphabetic and roman formats
    byte type = FORMAT_DEFAULT;
    int minimallength = 1; // for arabic format

  }

  private int determineRootElement(XPathContext xcont) {
    int node = xcont.dtm.firstChild[ROOT_NODE];

    while (xcont.dtm.nodeType[node] != Node.ELEMENT_NODE) {
      node = xcont.dtm.nextSibling[node];
    }

    return node;
  }

  public XSLNumber(XSLStylesheet owner, XSLNode parent) throws XSLException {
    super(owner, parent);
  }

  public XSLNumber(XSLStylesheet owner, XSLNode parent, Node content) throws XSLException {
    super(owner, parent, content);
    boolean formatspecified = false;
    NamedNodeMap attribs = content.getAttributes();

    for (int i = 0; i < attribs.getLength(); i++) {
      Attr att = (Attr) attribs.item(i);
      String attrname = att.getNodeName();

      if (attrname.equals("level")) {
        level = LEVEL_SINGLE;

        if (att.getValue().equals("multiple")) {
          level = LEVEL_MULTIPLE;
        } else if (att.getValue().equals("any")) {
          level = LEVEL_ANY;
        }
      } else if (attrname.equals("count")) {
        countspecified = true;
        etcount = owner.etBuilder.process(new CharArray(att.getValue()));
      } else if (attrname.equals("from")) {
        fromspecified = true;
        etfrom = owner.etBuilder.process(new CharArray(att.getValue()));
      } else if (attrname.equals("value")) {
        value = att.getValue();
        eto = owner.etBuilder.process(new CharArray(value));
      } else if (attrname.equals("format")) {
        formatspecified = true;
        formattokens = new Vector();
        splitToAlphanumericTokens(att.getValue(), formattokens);
      } else if (attrname.equals("lang")) {
        lang = att.getValue();
      } else if (attrname.equals("letter-value")) {
        lettervalue = att.getValue();
      } else if (attrname.equals("grouping-separator")) {
        if (att.getValue().length() > 0) {
          groupingseparator = att.getValue().charAt(0);
          separatorspecified = true;
        }
      } else if (attrname.equals("grouping-size")) {
        try {
          groupingsize = new Integer(att.getValue()).intValue();
          sizespecified = true;
        } catch (NumberFormatException nfe) {
          throw new XSLException("Non-numeric value of 'grouping-size'. ", nfe);
        }
      }
    } 

    //    if (value==null) {
    //      value = "position()";
    //      eto = owner.etBuilder.process(new CharArray(value));
    //    }
    if (!formatspecified) {
      formattokens = new Vector();
      formattokens.add(new Alphanumeric());
      //formattokens.add(".");
    }

    separatorspecified = separatorspecified && sizespecified; // if one of them
    sizespecified = separatorspecified; // is not specified then both are ignored
  }

  /**
   * Converts a String to Alphanumeric taking in mind the value of "format"
   * attribute.
   * @param st - the String to be converted.
   */
  private Alphanumeric stringToAlphanumeric(String st) {
    Alphanumeric an = new Alphanumeric();
    char ch = st.charAt(st.length() - 1);

    if (ch == 'a') {
      an.capitals = !WITH_CAPITALS;
      an.type = FORMAT_ALPHABETIC;
    } else if (ch == 'A') {
      an.capitals = WITH_CAPITALS;
      an.type = FORMAT_ALPHABETIC;
    } else if (ch == 'i') {
      an.capitals = !WITH_CAPITALS;
      an.type = FORMAT_ROMAN;
    } else if (ch == 'I') {
      an.capitals = WITH_CAPITALS;
      an.type = FORMAT_ROMAN;
    } else {
      an.type = FORMAT_NUMERIC;
      an.minimallength = st.length();
    }

    return an;
  }

  /**
   *  This method splits the 'format" attribute value to tokens. Each of them
   *  contains only alphanumeric or only non-alphanumeric characters and adds
   *  them to Vector tokens. The first ones are converted to Alphanumeric
   *  before that.
   */
  private void splitToAlphanumericTokens(String fortokenizing, Vector tokens) {
    if ((fortokenizing == null) || ((fortokenizing != null) && (fortokenizing.equals("")))) {
      Alphanumeric an = new Alphanumeric();
      an.type = FORMAT_DEFAULT;
      tokens.add(an);
    }

    boolean iscurrentalphanumeric = Character.isLetterOrDigit(fortokenizing.charAt(0));
    boolean islastalphanumeric = iscurrentalphanumeric;
    int substrstarts = 0;
    int substrends = 0;

    for (int i = 1; i < fortokenizing.length(); i++) {
      iscurrentalphanumeric = Character.isLetterOrDigit(fortokenizing.charAt(i));

      if (iscurrentalphanumeric != islastalphanumeric) {
        substrstarts = substrends;
        substrends = i;

        if (islastalphanumeric) {
          tokens.add(stringToAlphanumeric(fortokenizing.substring(substrstarts, substrends)));
        } else {
          tokens.add(fortokenizing.substring(substrstarts, substrends));
        }
      }

      islastalphanumeric = iscurrentalphanumeric;
    } 

    substrstarts = substrends;
    substrends = fortokenizing.length();

    if (islastalphanumeric) {
      tokens.add(stringToAlphanumeric(fortokenizing.substring(substrstarts, substrends)));
    } else {
      tokens.add(fortokenizing.substring(substrstarts, substrends));

      if (tokens.size() == 1) {
        tokens.add(0, new Alphanumeric());
      }
    }
  }

  /**
   * This method
   *
   * @param   xcont
   * @param   node
   * @exception   XSLException
   * @exception   XPathException
   */
  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    //xpcontext = xcont;
    if (rootelement == -1) {
      rootelement = determineRootElement(xcont);
    }

    XSLOutputProcessor oprocessor = owner.getOutputProcessor();
//    String characteroutput = null;

    if (!countspecified) {
      etcount = owner.etBuilder.process(xcont.dtm.name[node].toString());
    }

    if (!fromspecified) {
      QName x = new QName();
      x.localname = xcont.dtm.name[rootelement].localname;
      x.prefix = xcont.dtm.name[rootelement].prefix;
      x.uri = xcont.dtm.name[rootelement].uri;
      ETLocationStep e = new ETLocationStep(ETItem.AXISSTR_CHILD, x);
      etfrom = new ETObject(xcont.dtm.name[rootelement].toString(), e);
      //etfrom = owner.etBuilder.process(xcont.dtm.name[rootelement].toString());
    }

    if (value != null) {
      //    if (!value.equals("position()")) {
      //      System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGg");
      XObject xo = owner.getXPathProcessor().process(eto, xcont, varContext);
      XNumber xn = xo.toXNumber();
      printSingleOrAny(oprocessor, (int) Math.round(xn.getValue()));
    } else {
      if (level == LEVEL_ANY) {
        int num = calculateAny(etcount, etfrom, xcont);
        printSingleOrAny(oprocessor, num);
      } else if (level == LEVEL_SINGLE) {
        int num = calculateSingle(etcount, etfrom, xcont);
        printSingleOrAny(oprocessor, num);
      } else if (level == LEVEL_MULTIPLE) {
        int list[] = calculateMultiple(etcount, etfrom, xcont);
        int j = 0;

        if (formattokens.elementAt(0) instanceof String) {
          j++;
          oprocessor.characters(new CharArray((String) formattokens.elementAt(0)), true);
        }

        boolean hasendstring;
        int lastalphanumeric;

        if (formattokens.elementAt(formattokens.size() - 1) instanceof String) {
          hasendstring = true;
          lastalphanumeric = formattokens.size() - 2;
        } else {
          hasendstring = false;
          lastalphanumeric = formattokens.size() - 1;
        }

        for (int i = 0; i < list.length; i++) {
          Alphanumeric an;

          if (j < formattokens.size() - 1) {
            an = (Alphanumeric) formattokens.elementAt(j);
          } else {
            an = (Alphanumeric) formattokens.elementAt(lastalphanumeric);
          }

          printAlphanumeric(an, list[i], oprocessor);
          j++;

          if (hasendstring) {
            if (i == list.length - 1) {
              oprocessor.characters(new CharArray((String) formattokens.elementAt(formattokens.size() - 1)), true);
            } else {
              if (j >= formattokens.size() - 1) {
                if (formattokens.size() < 4) {
                  // 4 == Alphanumeric - separator - Alphanumeric - separator
                  // 3 == separator - Alphanumeric - separator
                  // and there is no separator which is not in the beginning
                  // or in the end of the sequance. Then using default.
                  oprocessor.characters(new CharArray("."), false);
                } else {
                  // else use the last which is nor in the beginning neither in
                  //the end
                  oprocessor.characters(new CharArray((String) formattokens.elementAt(formattokens.size() - 3)), true);
                }
              } else {
                oprocessor.characters(new CharArray((String) formattokens.elementAt(j)), true);
              }
            }
          } else {
            if (i < list.length - 1) {
              if (j >= formattokens.size() - 1) {
                if (formattokens.size() < 3) {
                  oprocessor.characters(new CharArray("."), false);
                } else {
                  oprocessor.characters(new CharArray((String) formattokens.elementAt(formattokens.size() - 2)), true);
                }
              } else {
                oprocessor.characters(new CharArray((String) formattokens.elementAt(j)), true);
              }
            }
          }

          j++;
        } 
      }
    }

//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  /**
   *
   *
   * @param   ind
   */
  public void print(String ind) {

  }

  /**
   * This method determines how many numbers the list contains in case of
   * attribute level="multiple" and then calculates the exact values
   * that they must have.
   *
   */
  private int[] calculateMultiple(ETObject matchETO, ETObject fromETO, XPathContext basecontext) throws XPathException {
    int listlength = howManyItems(matchETO, fromETO, basecontext);
    int tempnode = basecontext.node;
    int[] list = new int[listlength];
    XPathContext xcont = new XPathContext();
    xcont.reuse(basecontext);

    for (int i = 0; i < listlength; i++) { //determines which nodes are in the list
      while (!matchETO.et.match(xcont)) {
        tempnode = xcont.dtm.parent[tempnode];
        xcont.node = tempnode;
      }

      list[listlength - i - 1] = tempnode;
      tempnode = basecontext.dtm.parent[tempnode];
      xcont.node = tempnode;
    } 

    for (int i = 0; i < listlength; i++) { //converts the node number in its sequence number
      int nodenumber = 0;
      tempnode = list[i];

      while (tempnode != -1) {
        if (basecontext.dtm.nodeType[tempnode] == DTM.ELEMENT_NODE) {
          xcont.reuse(basecontext);
          xcont.node = tempnode;

          if (matchETO.et.match(xcont)) {
            nodenumber++;
          }
        }

        tempnode = xcont.dtm.previousSibling[tempnode];
        xcont.node = tempnode;
      }

      list[i] = nodenumber;
    } 

    return list;
  }

  /**
   * Used by calculateMultiple to determine how many numbers contains xsl:number in case
   * of attribute level="multiple"
   *
   */
  private int howManyItems(ETObject matchETO, ETObject fromETO, XPathContext basecontext) throws XPathException {
    int count = 0;
    int fromnode = basecontext.node;
    XPathContext xcont = new XPathContext();
    xcont.reuse(basecontext);

    while (!fromETO.et.match(xcont) && fromnode != xcont.dtm.parent[rootelement]) {
      if (matchETO.et.match(xcont)) {
        count++;
      }

      fromnode = xcont.dtm.parent[fromnode];
      xcont.node = fromnode;
    }

    return count;
  }

  /**
   * Calculates the value of xsl:number in case when the "level" attribute has
   * value "single".
   */
  private int calculateSingle(ETObject matchETO, ETObject fromETO, XPathContext basecontext) throws XPathException {
    int fromnode = basecontext.node;
    XPathContext xcont = new XPathContext();
    xcont.reuse(basecontext);

    while (!matchETO.et.match(xcont) && fromnode != xcont.dtm.parent[rootelement]) {
      fromnode = xcont.dtm.parent[fromnode];
      xcont.node = fromnode;
    }

    if (fromnode == xcont.dtm.parent[rootelement]) {
      return -1;
    }

    int countnode = xcont.node;

    while (!fromETO.et.match(xcont) && (countnode != fromnode)) {
      countnode = xcont.dtm.parent[countnode];
      xcont.node = countnode;
    }

    //    if (countnode == fromnode) {
    //      return -1;
    //    }
    int nodecount = 1;

    while (xcont.dtm.previousSibling[countnode] != -1) {
      countnode = xcont.dtm.previousSibling[countnode];
      xcont.node = countnode;

      if ((xcont.dtm.nodeType[countnode] == DTM.ELEMENT_NODE) && matchETO.et.match(xcont)) {
        //(matchesPattern(dtm.name[countnode].toString(),countpattern))) {
        nodecount++;
      }
    }

    return nodecount;
  }

  /**
   * Calculates the value of xsl:number in case when the "level" attrivute has
   * value "any".
   */
  private int calculateAny(ETObject matchETO, ETObject fromETO, XPathContext basecontext) throws XPathException {
    int nodecount = 0;
    XPathContext xcont = new XPathContext();
    xcont.reuse(basecontext);
    int node = xcont.node;

    for (int i = node; i >= rootelement; i--) {
      if (xcont.dtm.nodeType[i] == DTM.ELEMENT_NODE) {
        xcont.node = i;

        if (fromETO.et.match(xcont)) {
          return nodecount;
        }

        if (matchETO.et.match(xcont)) {
          nodecount++;
        }
      }
    } 

    return nodecount;
  }

  /**
   * Converts an integer from 1 to 3999 into String representing a Roman number.
   * If there is a zero value as input then "0" is returned.
   *
   */
  private String intToRoman(int i, boolean capitals) {
    String st = "";
    if (i == 0) {
      return "0";
    }

    if (i < 4000) { //MMMCMXCIX = 3999
      if (capitals) {
        while (i >= 1000) {
          st = st + "M";
          i -= 1000;
        }

        if (i >= 900) {
          st += "CM";
          i -= 900;
        }

        if (i >= 500) {
          st += "D";
          i -= 500;
        }

        if (i >= 400) {
          st += "CD";
          i -= 400;
        }

        while (i >= 100) {
          st = st + "C";
          i -= 100;
        }

        if (i >= 90) {
          st += "XC";
          i -= 90;
        }

        if (i >= 50) {
          st += "L";
          i -= 50;
        }

        if (i >= 40) {
          st += "XL";
          i -= 40;
        }

        while (i >= 10) {
          st = st + "X";
          i -= 10;
        }

        if (i == 9) {
          st = st + "IX";
          i = 0;
        }

        if (i >= 5) {
          st = st + "V";
          i -= 5;
        }

        if (i == 4) {
          st = st + "IV";
          i = 0;
        }

        while (i >= 1) {
          st = st + "I";
          i -= 1;
        }
      } else { //if not capitals
        while (i >= 1000) {
          st = st + "m";
          i -= 1000;
        }

        if (i >= 900) {
          st += "cm";
          i -= 900;
        }

        if (i >= 500) {
          st += "d";
          i -= 500;
        }

        if (i >= 400) {
          st += "cd";
          i -= 400;
        }

        while (i >= 100) {
          st = st + "c";
          i -= 100;
        }

        if (i >= 90) {
          st += "xc";
          i -= 90;
        }

        if (i >= 50) {
          st += "l";
          i -= 50;
        }

        if (i >= 40) {
          st += "xl";
          i -= 40;
        }

        while (i >= 10) {
          st = st + "x";
          i -= 10;
        }

        if (i == 9) {
          st = st + "ix";
          i = 0;
        }

        if (i >= 5) {
          st = st + "v";
          i -= 5;
        }

        if (i == 4) {
          st = st + "iv";
          i = 0;
        }

        while (i >= 1) {
          st = st + "i";
          i -= 1;
        }
      }
    }

    return st;
  }

  /**
   * Converts an integer value to a letter representation in the following
   * sequence:
   * 0,a,b,c,...,z,aa,ab,ac,..,az,ba,...,zz,aaa...
   */
  private String intToLetter(int i, boolean capitals) {
    String st;

    if (i == 0) {
      st = "0";
    } else {
      st = "";
      i -= 1; // a -> 0, z -> 25 for easier transformation
      char ref = 0;

      if (capitals) {
        ref = 'A';
      } else {
        ref = 'a';
      }

      if (i == 0) {
        return ref + "";
      }

      while (i > 0) {
        int j = i % 26;
        st = (char) (ref + j) + st;
        i /= 26;
      }
    }

    return st;
  }

  /**
   * Sends the calculated value of xsl:numbere in case when level="single or "any"
   */
  private void printSingleOrAny(XSLOutputProcessor oprocessor, int num) throws XSLOutputException {
    if (num >= 0) {
      int i = 0;

      if (formattokens.elementAt(0) instanceof String) {
        i++;
        oprocessor.characters(new CharArray((String) formattokens.elementAt(0)), true);
      }

      Alphanumeric an = (Alphanumeric) formattokens.elementAt(i);
      printAlphanumeric(an, num, oprocessor);
      i++;

      if (formattokens.size() > i) {
        if (formattokens.elementAt(formattokens.size() - 1) instanceof String) {
          oprocessor.characters(new CharArray((String) formattokens.elementAt(formattokens.size() - 1)), true);
        }
      }
    }
  }

  /**
   * Sends to output a number taking in mind its format (roman, alphabetic,
   * arabic, leading zerroes).
   */
  private void printAlphanumeric(Alphanumeric an, int num, XSLOutputProcessor oprocessor) throws XSLOutputException {
    if (an.type == FORMAT_ROMAN) {
      oprocessor.characters(new CharArray(intToRoman(num, an.capitals)), true);
    } else if (an.type == FORMAT_ALPHABETIC) {
      oprocessor.characters(new CharArray(intToLetter(num, an.capitals)), true);
    } else {
      String number = new Integer(num).toString();

      if (number.length() < an.minimallength) {
        CharArray zero = new CharArray("0");

        for (int j = 1; j <= an.minimallength - number.length(); j++) {
          oprocessor.characters(zero, true);
        } 
      }

      oprocessor.characters(new CharArray(number), true);
    }
  }

  public final static String[] REQPAR = {};
  public final static String[] OPTPAR = {"level", "count", "from", "value", "format", "lang", "letter-value", "grouping-separator", "grouping-size"};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

}

