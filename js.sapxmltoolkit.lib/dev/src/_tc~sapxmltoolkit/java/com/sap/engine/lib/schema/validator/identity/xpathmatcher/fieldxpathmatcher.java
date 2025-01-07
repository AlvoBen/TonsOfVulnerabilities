package com.sap.engine.lib.schema.validator.identity.xpathmatcher;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-2-13
 * Time: 13:09:51
 * To change this template use Options | File Templates.
 */
public final class FieldXPathMatcher {

  private ElementMatchingStructure selectorMatchingStructure;
  private NodeMatchingStructure fieldMatchingStructure;
  private XPathTokenizer xPathTokenizer;
  private String fieldXPathExpression;
  
  public FieldXPathMatcher(XPathTokenizer xPathTokenizer, String selectorXPathExpresion, String fieldXPathExpression, Hashtable prefixesMapping) {
    selectorMatchingStructure = (ElementMatchingStructure)(NodeMatchingStructureFactory.create(selectorXPathExpresion, prefixesMapping));
    fieldMatchingStructure = NodeMatchingStructureFactory.create(fieldXPathExpression, prefixesMapping);
    selectorMatchingStructure.addSubStructure(fieldMatchingStructure);
    this.fieldXPathExpression = fieldXPathExpression;
    this.xPathTokenizer = xPathTokenizer; 
  }

  public XPathMatchResult matches(int offset) {
    xPathTokenizer.init(offset);
    if(selectorMatchingStructure.process(xPathTokenizer) && xPathTokenizer.peek() == null) {
      XPathMatchResult matchResult = new XPathMatchResult();
      matchResult.setFieldXPathExpression(fieldXPathExpression);
      matchResult.setSelectorXPathStepIndex(selectorMatchingStructure.getXPathStepIndex(fieldMatchingStructure));
      return(matchResult);
    }
    return(null);
  }
}
