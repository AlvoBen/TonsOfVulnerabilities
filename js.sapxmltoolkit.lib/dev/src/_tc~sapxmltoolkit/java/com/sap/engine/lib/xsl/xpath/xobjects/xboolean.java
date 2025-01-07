package com.sap.engine.lib.xsl.xpath.xobjects;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.ETItem;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.Symbols;

/**
 *   Represents XPath's <strong>boolean</strong> type.
 *   Wraps a java primitive boolean, accessible through the <tt>getValue()</tt>
 * method.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XBoolean extends XObject implements ETItem, XFunction {

  /**
   * The <tt>TYPE</tt> field for this <tt>XObject</tt>.
   * @see XObject#getType()
   */
  public static final int TYPE = 3;
  protected boolean value;

  public XBoolean() {

  }

  /**
   * @see XObject#getType()
   */
  public int getType() {
    return TYPE;
  }

  /**
   * Returns the primitive <tt>boolean</tt> represented by this <b>boolean</b>.
   */
  public boolean getValue() {
    return value;
  }

  public XNumber toXNumber() throws XPathException {
    return factory.getXNumber(this);
  }

  public XString toXString() throws XPathException {
    return factory.getXString(this);
  }

  public XBoolean toXBoolean() throws XPathException {
    return this;
  }

  /**
   * From interface XPathExpression.
   */
  public XObject evaluate(XPathContext context) {
    return this;
  }

  /**
   * From interface XPathExpression.
   */
  public void print(int indent) {
    Symbols.printSpace(indent);
    LogWriter.getSystemLogWriter().println("XBoolean(" + value + ")");
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    return a[0].toXBoolean();
  }

  public String getFunctionName() {
    return "boolean";
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 1);
  }

  public String toString() {
    return value ? "true" : "false";
  }

  public boolean match(XPathContext c) throws XPathException {
    throw new XPathException(this.getClass() + " cannot be matched.");
  }

}

