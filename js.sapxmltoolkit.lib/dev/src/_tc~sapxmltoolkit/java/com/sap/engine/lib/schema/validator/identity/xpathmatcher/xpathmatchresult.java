/*
 * Created on 2005-5-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.schema.validator.identity.xpathmatcher;

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class XPathMatchResult {
  
  private int selectorXPathStepIndex;
  private int filedIndex;
  private String fieldXPathExpression;
  
  public void setSelectorXPathStepIndex(int selectorXPathStepIndex) {
    this.selectorXPathStepIndex = selectorXPathStepIndex;
  }
  
  public void setFieldIndex(int filedIndex) {
    this.filedIndex = filedIndex;
  }
  
  protected void setFieldXPathExpression(String fieldXPathExpression) {
    this.fieldXPathExpression = fieldXPathExpression;
  }
  
  public int getSelectorXPathStepIndex() {
    return(selectorXPathStepIndex);
  }
  
  public int getFieldIndex() {
    return(filedIndex);
  }
  
  public String getFieldXPathExpression() {
    return(fieldXPathExpression);
  }
}
