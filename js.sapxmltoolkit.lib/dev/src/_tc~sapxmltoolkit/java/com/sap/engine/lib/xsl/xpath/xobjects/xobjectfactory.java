package com.sap.engine.lib.xsl.xpath.xobjects;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.DTM;
import com.sap.engine.lib.xsl.xpath.StaticDouble;
import com.sap.engine.lib.xsl.xpath.StaticInteger;
import com.sap.engine.lib.xsl.xslt.pool.ObjectPool;

/**
 *   Only this class should be used to produce instances of <tt>XObject<tt>s.
 * Encapsulates several pools and some frequently used instances.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XObjectFactory {

  private ObjectPool xNodeSetPool = null;
  private ObjectPool xNumberPool = null;
  private ObjectPool xStringPool = null;
  private ObjectPool xJavaPool = null;
  private ObjectPool xNodeSetOverflow = null;
  private ObjectPool xNumberOverflow = null;
  private ObjectPool xStringOverflow = null;
  private ObjectPool xJavaOverflow = null;
  // Constants
  private XBoolean XBOOLEAN_TRUE;
  private XBoolean XBOOLEAN_FALSE;
  private XNumber XNUMBER_MINUS_ONE;
  private static final int XNUMBER_SMALL_LIMIT = 10;
  private XNumber[] XNUMBER_SMALL;
  private XString XSTRING_EMPTY;
  protected CharArray chXNodeSetInst = new CharArray(10);
  protected StaticDouble staticDouble = new StaticDouble();
  protected StaticInteger staticInteger = new StaticInteger();
  
  /**
   * Initializes the inner pools and makes instances of 'constant'
   * objects, i.e. <tt>XObjects</tt> that are likely to be frequently used.
   */
  public XObjectFactory(int initsize) {
    xNodeSetPool = new ObjectPool(XNodeSet.class, initsize, initsize);
    xNumberPool = new ObjectPool(XNumber.class, initsize, initsize);
    xStringPool = new ObjectPool(XString.class, initsize, initsize);
    xJavaPool = new ObjectPool(XJavaObject.class, initsize, initsize);
    xNodeSetOverflow = new ObjectPool(XNodeSet.class, initsize, initsize);
    xNumberOverflow = new ObjectPool(XNumber.class, initsize, initsize);
    xStringOverflow = new ObjectPool(XString.class, initsize, initsize);
    xJavaOverflow = new ObjectPool(XJavaObject.class, initsize, initsize);
    initConstants();
    xNodeSetOverflow.setPos(-1);
    xNumberOverflow.setPos(-1);
    xStringOverflow.setPos(-1);
    xJavaOverflow.setPos(-1);
  }

  private void initConstants() {
    XBOOLEAN_TRUE = new XBoolean();
    XBOOLEAN_TRUE.setParentFact(this);
    XBOOLEAN_TRUE.setClosed(true);
    XBOOLEAN_TRUE.value = true;
    XBOOLEAN_TRUE.isConstant = true;
    //
    XBOOLEAN_FALSE = new XBoolean();
    XBOOLEAN_FALSE.setParentFact(this);
    XBOOLEAN_FALSE.setClosed(true);
    XBOOLEAN_FALSE.value = false;
    XBOOLEAN_FALSE.isConstant = true;
    //
    XNUMBER_MINUS_ONE = new XNumber();
    XNUMBER_MINUS_ONE.setParentFact(this);
    XNUMBER_MINUS_ONE.setClosed(true);
    XNUMBER_MINUS_ONE.value = (double) (-1);
    XNUMBER_MINUS_ONE.isConstant = true;
    //
    XNUMBER_SMALL = new XNumber[XNUMBER_SMALL_LIMIT];

    for (int i = 0; i < XNUMBER_SMALL_LIMIT; i++) {
      XNUMBER_SMALL[i] = new XNumber();
      XNUMBER_SMALL[i].setParentFact(this);
      XNUMBER_SMALL[i].setClosed(true);
      XNUMBER_SMALL[i].value = (double) i;
      XNUMBER_SMALL[i].isConstant = true;
    } 

    //
    XSTRING_EMPTY = new XString();
    XSTRING_EMPTY.setParentFact(this);
    XSTRING_EMPTY.setClosed(true);
    XSTRING_EMPTY.value = CharArray.EMPTY;
    XSTRING_EMPTY.isConstant = true;
  }

  // XNumber pseudo-constructors
  public XNumber getXNumber() {
    return XNUMBER_SMALL[0];
  }

  public XNumber getXNumber(int x) {
    if (x == -1) {
      return XNUMBER_MINUS_ONE;
    }

    if ((x >= 0) && (x < XNUMBER_SMALL_LIMIT)) {
      return XNUMBER_SMALL[x];
    }

    return produceXNumber().reuse((double) x);
  }

  public XNumber getXNumber(double x) {
    if (x == (int) x) {
      return getXNumber((int) x);
    }

    return produceXNumber().reuse(x);
  }

  public XNumber getXNumber(XBoolean xb) {
    return (xb.value) ? XNUMBER_SMALL[1] : XNUMBER_SMALL[0];
  }

  public XNumber getXNumber(XString xs) {
    if (xs.value.length() == 1) {
      char ch = xs.value.charAt(0);

      if (ch >= 0 && ch <= 9) {
        return getXNumber(ch - '0');
      }
    }

    return produceXNumber().reuse(xs);
  }

  public XNumber getXNumber(XNodeSet xns) {
    return produceXNumber().reuse(xns);
  }

  // XString pseudo-constructors
  public XString getXString() {
    return produceXString().reuse();
  }

  public XString getXString(String s) {
    if (s.length() == 0) {
      return XSTRING_EMPTY;
    }

    return produceXString().reuse(s);
  }

  public XString getXString(CharArray ca) {
    if (ca.length() == 0) {
      return XSTRING_EMPTY;
    }

    return produceXString().reuse(ca);
  }

  public XString getXString(CharArray ca, int b, int e) {
    if (b == e) {
      return XSTRING_EMPTY;
    }

    return produceXString().reuse(ca, b, e);
  }

  public XString getXString(XNumber xn) {
    return produceXString().reuse(xn);
  }

  public XString getXString(XBoolean xb) {
    return produceXString().reuse(xb);
  }

  public XString getXString(XNodeSet xns) {
    return produceXString().reuse(xns);
  }
  
  public XString getFullXString(XNodeSet xns) {
    return produceXString().reuseFull(xns);
  }


  public XString getXStringEmpty() {
    return XSTRING_EMPTY;
  }

  // XBoolean pseudo-constructors
  public XBoolean getXBoolean() {
    return XBOOLEAN_FALSE;
  }

  public XBoolean getXBoolean(boolean b) {
    return (b) ? XBOOLEAN_TRUE : XBOOLEAN_FALSE;
  }

  public XBoolean getXBoolean(XNumber xn) {
    return (xn.value == 0.0d || Double.isNaN(xn.value)) ? XBOOLEAN_FALSE : XBOOLEAN_TRUE;
  }

  public XBoolean getXBoolean(XString xs) {
    return (xs.value.length() == 0) ? XBOOLEAN_FALSE : XBOOLEAN_TRUE;
  }

  public XBoolean getXBoolean(XBoolean xb) {
    return xb;
  }

  public XBoolean getXBoolean(XNodeSet xns) {
    return (xns.isEmpty()) ? XBOOLEAN_FALSE : XBOOLEAN_TRUE;
  }

  // XNodeSet pseudo-constructors
  public XNodeSet getXNodeSet() {
    return produceXNodeSet();
  }

  public XNodeSet getXNodeSet(XNodeSet xns) {
    return produceXNodeSet().reuse(xns);
  }

  public XNodeSet getXNodeSet(DTM dtm, int a) {
    return produceXNodeSet().reuse(dtm, a);
  }

  public XNodeSet getXNodeSet(DTM dtm) {
    return produceXNodeSet().reuse(dtm);
  }

  public XJavaObject getXJavaObject(Object o) {
    return produceXJavaObject().reuse(o);
  }

  /**
   * Puts the parameter back into the pool.
   */
  public void releaseXObject(XObject obj) {
    if (!obj.getClosed()) {
      obj.setClosed(true);
      int t = obj.getType();

      if (t == XString.TYPE) {
        xStringOverflow.releaseObject((XString) obj);
      } else if (t == XNumber.TYPE) {
        xNumberOverflow.releaseObject((XNumber) obj);
      } else if (t == XNodeSet.TYPE) {
        xNodeSetOverflow.releaseObject((XNodeSet) obj);
      } else if (t == XJavaObject.TYPE) {
        xJavaOverflow.releaseObject((XJavaObject) obj);
      }
    }
  }

  public void releaseXNodeSet() {
    xNodeSetPool.releaseAllObjects();
  }

  public void releaseOthers() {
    xNumberPool.releaseAllObjects();
    xStringPool.releaseAllObjects();
  }

  public void releaseAllPools() {
    xNodeSetPool.releaseAllObjects();
    xNumberPool.releaseAllObjects();
    xStringPool.releaseAllObjects();
    xJavaPool.releaseAllObjects();
    xNodeSetOverflow.setPos(-1);
    xNumberOverflow.setPos(-1);
    xStringOverflow.setPos(-1);
    xJavaOverflow.setPos(-1);
  }

  private XNumber produceXNumber() {
    if (xNumberOverflow.getPos() > -1) {
      return (XNumber) ((XObject) xNumberOverflow.getObject()).setClosed(false).setParentFact(this);
    }
    return (XNumber) ((XObject) xNumberPool.getObject()).setClosed(false).setParentFact(this);
  }

  private XString produceXString() {
    if (xStringOverflow.getPos() > -1) {
      return (XString) ((XObject) xStringOverflow.getObject()).setClosed(false).setParentFact(this);
    }
    return ((XString) ((XObject) xStringPool.getObject()).setClosed(false).setParentFact(this));
  }

  private XNodeSet produceXNodeSet() {
    if (xNodeSetOverflow.getPos() > -1) {
      return (XNodeSet) ((XNodeSet) xNodeSetOverflow.getObject()).setClosed(false).setParentFact(this);
    }
    return (XNodeSet) ((XObject) xNodeSetPool.getObject()).setClosed(false).setParentFact(this);
  }

  private XJavaObject produceXJavaObject() {
    if (xJavaOverflow.getPos() > -1) {
      return (XJavaObject) ((XJavaObject) xJavaOverflow.getObject()).setClosed(false).setParentFact(this);
    }
    return (XJavaObject) ((XObject) xJavaPool.getObject()).setClosed(false).setParentFact(this);
  }

}

