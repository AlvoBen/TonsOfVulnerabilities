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
public final class AttributeMatchingStructure extends NodeMatchingStructure {

	private String attributeUri;
	private String attributeName;

	protected AttributeMatchingStructure(String attributeUri, String attributeName) {
		super();
		this.attributeUri = attributeUri;
		this.attributeName = attributeName;
	}

	protected boolean process(XPathTokenizer tokenizer) {
		XPathStep stepObj = tokenizer.next();
		return(stepObj != null && stepObj.isAttribute() && (attributeUri == null || attributeUri.equals(stepObj.getUriStr())) && (attributeName == null || attributeName.equals(stepObj.getLocalNameStr())));
	}
}
