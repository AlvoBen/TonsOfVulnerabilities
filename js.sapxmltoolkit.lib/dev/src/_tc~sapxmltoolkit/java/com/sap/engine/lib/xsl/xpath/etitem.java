package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * Classes that implement this interface represent nodes from the
 * XPath expression tree. All of them have protected constructors
 * and can be obtained through an <tt>ETBuilder</tt>.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version June 2001
 */
public interface ETItem {

  public static final String NTSTR_STAR = "*";
  //public static final String NTSTR_ALLATTR = "@*";
  //public static final String sl = "/";
  //public static final String zsl = "*|/";
  public static final String NTSTR_NODE = "node()";
  public static final String NTSTR_TEXT = "text()";
  public static final String NTSTR_PI = "processing-instruction()";
  public static final String NTSTR_COMMENT = "comment()";
  public static final String AXISSTR_CHILD = "child";
  public static final String AXISSTR_ATTRIBUTE = "attribute";
  public static final String AXISSTR_DOS = "descendant-or-self";
  public static final int NT_UNDEFINED = 0;
  public static final int NT_ALL = 1;
  public static final int NT_ALLNS = 8;
  public static final int NT_ALLATTR = 2;
  public static final int NT_NODE = 3;
  public static final int NT_TEXT = 4;
  public static final int NT_PI = 5;
  public static final int NT_COMMENT = 6;
  public static final int AXIS_CHILD = 1001;
  public static final int AXIS_DOS = 1003;
  public static final int AXIS_ATTRIBUTE = 1002;
  public static final int AXIS_UNDEFINED = 1000;
  public static final int RES_TRUE = 1;
  public static final int RES_FALSE = -1;
  public static final int RES_UNDEFINED = 0;


  String toString();


  void print(int indent);


  XObject evaluate(XPathContext context) throws XPathException;


  boolean match(XPathContext context) throws XPathException;

}

