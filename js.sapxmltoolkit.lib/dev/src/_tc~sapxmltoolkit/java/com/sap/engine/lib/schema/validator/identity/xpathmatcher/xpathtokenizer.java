package com.sap.engine.lib.schema.validator.identity.xpathmatcher;

import com.sap.engine.lib.schema.validator.xpath.XPathStep;
import com.sap.engine.lib.schema.validator.xpath.XPath;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-2-13
 * Time: 11:41:06
 * To change this template use Options | File Templates.
 */
public final class XPathTokenizer {

  private Vector xPathStepsCollector;
  private int index;
  
  public XPathTokenizer(XPath xPath) {
    xPathStepsCollector = xPath.getXPathSteps();
  }
  
  protected void init(int offset) {
    index = offset;
  }
  
  protected XPathStep next() {
    if(index > xPathStepsCollector.size() - 1) {
      return(null);
    }
    return((XPathStep)(xPathStepsCollector.get(index++)));
  }

  protected XPathStep peek() {
    if(index > xPathStepsCollector.size() - 1) {
      return(null);
    }
    return((XPathStep)(xPathStepsCollector.get(index)));
  }

  protected int getIndex() {
    return(index);
  }

  protected void setIndex(int index) {
    this.index = index;
  }
  
  protected int getTokensCount() {
  	return(xPathStepsCollector.size());
  }
}
