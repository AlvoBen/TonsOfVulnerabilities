/*
 * Created on 2004-10-13
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.schema.validator.identity.xpathmatcher;

import java.util.Vector;

import com.sap.engine.lib.schema.components.IdentityConstraintDefinition;
import com.sap.engine.lib.schema.validator.xpath.XPath;
import com.sap.engine.lib.xml.dom.DOM;

/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class IdentityConstraintXPathMatcher {
	
	private FieldXPathMatcher[] fieldMatchers;
	
	public IdentityConstraintXPathMatcher(XPathTokenizer xPathTokenizer, IdentityConstraintDefinition identCostrDefinition) {
		String selectorXPathExpression = identCostrDefinition.getSelector();
		Vector fieldsXPathExpressionsCollector = identCostrDefinition.getFields();
		fieldMatchers = new FieldXPathMatcher[fieldsXPathExpressionsCollector.size()];
		for(int i = 0; i < fieldsXPathExpressionsCollector.size(); i++) {
			String fieldXPathExpression = (String)(fieldsXPathExpressionsCollector.get(i));
			fieldMatchers[i] = new FieldXPathMatcher(xPathTokenizer, selectorXPathExpression, fieldXPathExpression, DOM.getNamespaceMappingsInScope(identCostrDefinition.getAssociatedDOMNode()));
		}
	}
	
	public int getFieldXPathMatchersCount() {
		return(fieldMatchers.length);
	}
	
	public XPathMatchResult match(int contextXPathStepsCount) {
		for(int i = 0; i < fieldMatchers.length; i++) {
      XPathMatchResult matchResult = fieldMatchers[i].matches(contextXPathStepsCount);
      if(matchResult != null) {
        matchResult.setFieldIndex(i);
        return(matchResult);
      }
		}
		return(null);
	} 
}
