package com.sap.engine.lib.xsl.xpath.xobjects;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.ETItem;
import com.sap.engine.lib.xsl.xpath.StaticInteger;
import com.sap.engine.lib.xsl.xpath.Symbols;
import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;

/**
 *   Represents XPath's <strong>string</strong> type.
 *   Wraps a <tt>com.sap.engine.lib.xml.parser.helpers.CharArray</tt>,
 * accessible through the <tt>getValue()</tt> method.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XString extends XObject implements ETItem, XFunction {

  /**
   * The <tt>TYPE</tt> field for this <tt>XObject</tt>.
   * @see XObject#getType()
   */
  public static final int TYPE = 2;
  protected CharArray value = new CharArray(20);

  public XString() {

  }

  /**
   * @see XObject#getType()
   */
  public int getType() {
    return TYPE;
  }

  /**
   * Returns the sequence of characters represented by this <b>string</b> as a
   * <tt>CharArray</tt>.
   */
  public CharArray getValue() {
    return value;
  }

  protected XString reuse() {
    value.clear();
    return this;
  }

  protected XString reuse(String v) {
    value.set(v);
    return this;
  }

  protected XString reuse(CharArray v, int b, int e) {
    value.set(v, b, e);
    return this;
  }

  protected XString reuse(CharArray v) {
    value.set(v);
    return this;
  }

  protected XString reuse(XNumber xn) {
    if (Double.isNaN(xn.value)) {
      value.set("NaN");
      return this;
    }

    if ((xn.value == 0.0) || (xn.value == -0.0)) {
      value.set("0");
      return this;
    }

    if (xn.value == Double.POSITIVE_INFINITY) {
      value.set("Infinity");
      return this;
    }

    if (xn.value == Double.NEGATIVE_INFINITY) {
      value.set("-Infinity");
      return this;
    }

    if (xn.value == (int) xn.value) {
      // No trailing '.0' for integers
      factory.staticInteger.intToCharArray((int) xn.value, value);
    } else {
      factory.staticDouble.doubleToString(xn.value, value);
    }

    return this;
  }

  protected XString reuse(XBoolean xb) {
    value.set(xb.value ? "true" : "false");
    return this;
  }

  protected XString reuse(XNodeSet xns) {
//    CharArray v = xns.stringValue();
//
//    if (v == null) {
//      v = CharArray.EMPTY;
//    }
//
//    value.set(v);
    xns.stringValue(value);
    return this;
  }
  
  protected XString reuseFull(XNodeSet xns) {
    CharArray v = xns.fullStringValue();

    if (v == null) {
      v = CharArray.EMPTY;
    }

    value.set(v);
    return this;
  
  }

  public XNumber toXNumber() throws XPathException {
    return factory.getXNumber(this);
  }

  public XString toXString() throws XPathException {
    return this;
    //return factory.getXString(value);
  }

  public XBoolean toXBoolean() throws XPathException {
    return factory.getXBoolean(this);
  }

  public XObject evaluate(XPathContext context) {
    return this;
  }

  public void print(int indent) {
    Symbols.printSpace(indent);
    LogWriter.getSystemLogWriter().println("XString(" + value + ")");
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    if (a.length == 0) {
      return context.getXFactCurrent().getXString(context.dtm.getStringValue(context.node));
    } else {
      return a[0].toXString();
    }
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length <= 1);
  }

  public String getFunctionName() {
    return "string";
  }

  public String toString() {
    return value.getString();
  }

  public boolean match(XPathContext c) throws XPathException {
    throw new XPathException(this.getClass() + " cannot be matched.");
  }

  public boolean equals(Object o) {
    if (! (o instanceof XString)) {
      return false;
    }
    
    XString x = (XString) o;
    return x.value.equals(value);
  }

  public int hashCode(){
    throw new UnsupportedOperationException("Not implemented for usage in hash-based collections");
  }
  

}

