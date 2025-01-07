package com.sap.engine.lib.xsl.xpath.xobjects;

import java.util.Stack;

import com.sap.engine.lib.xsl.xpath.ETItem;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xslt.XSLApplyTemplates;
import com.sap.engine.lib.xsl.xslt.XSLCallTemplate;
import com.sap.engine.lib.xsl.xslt.XSLNode;
import com.sap.engine.lib.xsl.xslt.XSLTemplate;

/**
 *   Extenders from this abstract class represent the four basic XPath types,
 * as described in the XPath specification. Instances of <tt>XObject</tt>s
 * should only be obtained from a <tt>XObjectFactory</tt>. All
 * <tt>XObjects</tt> except <tt>XNodeSet</tt> have a <tt>getValue</tt>
 * method to return a respective java equivalent.
 *   To avoid unnecessary usage of the 'instanceof' operator every <tt>XObject</tt>
 * has a <tt>getType()</tt> method and a static <tt>TYPE</tt> field.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public abstract class XObject implements ETItem {

  protected boolean isClosed = false;
  protected boolean isConstant = false;
  protected XObjectFactory factory = null;

 // private XSLNode parentDeclared;
  private boolean isTopVariableValue = false; 

  /**
   * <p>
   * Avoids the <tt>instanceof</tt> operator, as type-checking is frequently
   * used for such objects.
   * </p>
   * <p>
   * Every class extending from <tt>XObject</tt> has a <tt>.TYPE</tt>
   * field, which is the return.
   * </p>
   */
  public abstract int getType();

  /**
   * Conversion method. Should be overriden.
   */
  public XNumber toXNumber() throws XPathException {
    throw new XPathException("Illegal conversion");
  }

  /**
   * Conversion method. Should be overriden.
   */
  public XString toXString() throws XPathException {
    throw new XPathException("Illegal conversion");
  }

  /**
   * Conversion method. Should be overriden.
   */
  public XBoolean toXBoolean() throws XPathException {
    throw new XPathException("Illegal conversion");
  }

  /**
   * Conversion method. Should be overriden.
   */
  public XNodeSet toXNodeSet() throws XPathException {
    throw new XPathException("Illegal conversion");
  }

  public XJavaObject toXJavaObject() throws XPathException {
    throw new XPathException("Illegal conversion");
  }

  protected final boolean getClosed() {
    return isClosed;
  }

  /**
   * A <tt>XObject</tt> is 'closed' whenever it is in a pool,
   * and can be reused.
   */
  public final XObject setClosed(boolean v) {
    isClosed = v;
    return this;
  }

  public final void close() throws XPathException {
    this.factory.releaseXObject(this);
  }

  /**
   * Returns the <tt>XObjectFactory</tt> to which this <tt>XObject</tt>
   * belongs.
   */
  public final XObjectFactory getParentFact() {
    return factory;
  }

  public final XObject setParentFact(XObjectFactory factory) {
    this.factory = factory;
    reset();
    return this;
  }

  //  
  //  public final XObject copy() throws XPathException {
  //    if (this instanceof XString) {
  //      return toXString();
  //    } else if (this instanceof XNumber) {
  //      return toXNumber();
  //    } else if (this instanceof XBoolean) {
  //      return toXBoolean();
  //    } else {
  //      return this;
  //    }
  //  }

  public XSLNode getParentDeclared() {
    if (parents.isEmpty()) {
      return null;
    }
    return (XSLNode)parents.peek();
    //return parentDeclared;
  }

  private Stack parents = new Stack();
  public void setParentDeclared(XSLNode parent) {
    parents.push(parent);
    //parentDeclared = parent;
  }
  
  public boolean isConstant() {
    return isConstant;
  }
  
  public void setTopVariableValue(boolean isTopVariableValue) {
    this.isTopVariableValue = isTopVariableValue;
  }

  public boolean isTopVariableValue() {
    return isTopVariableValue;
  }
  
  public void closeAndRelease(XSLNode closingParent) throws XPathException {
    if (isConstant || isTopVariableValue) {
      return;
    }
    if (parents.isEmpty()) {
      return;
    }
    XSLNode parent = (XSLNode)parents.peek();
    if (parent instanceof XSLApplyTemplates || parent instanceof XSLCallTemplate) {
      if (closingParent != parent) {
        return; //Values will be reset after XSLApply/CallTemplate is processed. Otherwise, errors might occur due to template recursion
      }
    } else {
      XSLTemplate templateDeclared = parent.getTemplate(); 
      if (templateDeclared == null || templateDeclared != closingParent) { //In 6.40 variables are maintained on parent node level but not on template level. The method there should be changed accordingly
        return;
      }
    }
    
    parents.pop();
    if (parents.isEmpty()) {
    setClosed(false);
    close();
  }
  }
  
  public XObject setConstant() {
    setClosed(true);
    isConstant = true;
    return this;
  }
  
  public void reset() {
    parents.clear();
    //parentDeclared = null;
    isTopVariableValue = false;
  }
}

