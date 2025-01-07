package com.sap.engine.lib.schema.validator.xpath;

import java.util.Vector;

import com.sap.engine.lib.schema.validator.ReusableObjectsPool;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-9-14
 * Time: 10:21:47
 * To change this template use Options | File Templates.
 */
public final class XPath {
  
  private Vector xPathSteps;
  private ReusableObjectsPool pool;

  public XPath(ReusableObjectsPool pool) {
    xPathSteps = new Vector();
    this.pool = pool;
  }
  
  public int getXPathStepsCount() {
    return(xPathSteps.size());
  }
  
  public ElementXPathStep getParentElementXPathStep() {
    return(xPathSteps.size() > 1 ? (ElementXPathStep)(xPathSteps.get(xPathSteps.size() - 2)) : null);
  }

  public XPathStep getLastXPathStep() {
    return((XPathStep)(xPathSteps.lastElement()));
  }
  
  public void addXPathStep(XPathStep xPathStep) {
    xPathSteps.add(xPathStep);
  }
  
  public void removeXPathStep() {
    XPathStep xPathStep = (XPathStep)(xPathSteps.remove(xPathSteps.size() - 1));
    if(xPathStep instanceof ElementXPathStep) {
      pool.reuseElementXPathStep(xPathStep);
    } else {
      ElementXPathStep elemXPathstep = (ElementXPathStep)(xPathSteps.lastElement());
      elemXPathstep.removeAttributeXPathStep((AttributeXPathStep)xPathStep);
      pool.reuseAttributeXPathStep(xPathStep);
    }
  }
  
  public Vector getXPathSteps() {
    return(xPathSteps);
  }

  public XPathStep getXPathStep(int index) {
    return((XPathStep)(xPathSteps.get(index)));
  }
}
