/*
 * Created on 2004-10-15
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.schema.validator.identity.xpathmatcher;

import com.sap.engine.lib.schema.validator.xpath.XPathStep;

/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class DescendentAttributeMatchingStructure extends NodeMatchingStructure {

	private String attributeUri;
	private String attributeName;

	protected DescendentAttributeMatchingStructure(String attributeUri, String attributeName) {
		super();
		this.attributeUri = attributeUri;
		this.attributeName = attributeName;
	}
	
	protected boolean process(XPathTokenizer tokenizer) {
		tokenizer.setIndex(tokenizer.getTokensCount() - 1);
		XPathStep xPathStep = tokenizer.next();
		return(xPathStep.isAttribute() && (attributeUri == null || attributeUri.equals(xPathStep.getUriStr())) && (attributeName == null || attributeName.equals(xPathStep.getLocalNameStr())));
	}
}
