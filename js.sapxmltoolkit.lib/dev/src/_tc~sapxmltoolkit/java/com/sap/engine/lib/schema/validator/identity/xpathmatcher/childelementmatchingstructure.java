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
public final class ChildElementMatchingStructure extends ElementMatchingStructure {

	private String elementUri;
	private String elementName;

	protected ChildElementMatchingStructure(String elementUri, String elementName, NodeMatchingStructure substructure) {
		super();
		this.elementUri = elementUri;
		this.elementName = elementName;
		this.substructure = substructure;
	}

	public boolean process(XPathTokenizer tokenizer) {
    xPathStepIndex = tokenizer.getIndex();
		XPathStep stepObj = tokenizer.next();
		return(stepObj != null && !stepObj.isAttribute() && (elementUri == null || elementUri.equals(stepObj.getUriStr())) && (elementName == null || elementName.equals(stepObj.getLocalNameStr())) && (substructure == null ? true : substructure.process(tokenizer)));
	}
}
