package com.sap.engine.lib.xsl.xpath.xobjects;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xsl.xpath.*;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 *   Represents XPath's <strong>number</strong> type.
 *   Wraps a java primitive double, accessible through the <tt>getValue()</tt>
 * method.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version July 2001
 */
public final class XNumber extends XObject implements ETItem, XFunction {

  /**
   * The <tt>TYPE</tt> field for this <tt>XObject</tt>.
   * @see XObject#getType()
   */
  public static final int TYPE = 1;
  protected double value;

  public XNumber() {

  }

  /**
   * @see XObject#getType()
   */
  public int getType() {
    return TYPE;
  }

  /**
   * Returns the primitive <tt>double</tt> represented by this <b>number</b>.
   */
  public double getValue() {
    return value;
  }

  protected XNumber reuse() {
    value = Double.NaN;
    return this;
  }

  protected XNumber reuse(double v) {
    value = v;
    return this;
  }

  protected XNumber reuse(XNumber xn) {
    value = xn.value;
    return this;
  }

  private void initWithCharArray(CharArray ca) {
    if (ca == null) {
      value = Double.NaN;
    } else {
      try {
        value = factory.staticDouble.stringToDouble(ca);
      } catch (NumberFormatException e) {
        //$JL-EXC$
        value = Double.NaN;
      }
    }
  }

  private void initWithXString(XString xs) {
    initWithCharArray(xs.value);
  }

  public XNumber reuse(XString xs) {
    initWithXString(xs);
    return this;
  }

  public XNumber reuse(XBoolean xb) {
    value = xb.value ? 1.0 : 0.0;
    return this;
  }

  public XNumber reuse(XNodeSet xns) {
    initWithCharArray(xns.stringValue());
    return this;
  }

  public XNumber toXNumber() {
    return this;
    //return factory.getXNumber(value);
  }

  public XString toXString() {
    return factory.getXString(this);
  }

  public XBoolean toXBoolean() {
    return factory.getXBoolean(this);
  }

  public XObject evaluate(XPathContext context) {
    return this;
  }

  public void print(int indent) {
    Symbols.printSpace(indent);
    LogWriter.getSystemLogWriter().println("XNumber(" + value + ")"); //$JL-SYS_OUT_ERR$
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    if (a.length == 0) {
      XObjectFactory f = context.getXFactCurrent();
      return f.getXNumber(f.getXString(context.dtm.getStringValue(context.node)));
    }

    return a[0].toXNumber();
  }

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length <= 1);
  }

  public String getFunctionName() {
    return "number";
  }

  public boolean match(XPathContext c) throws XPathException {
    throw new XPathException(this.getClass() + " cannot be matched.");
  }

  public String toString() {
    return Double.toString(value);
  }

}

