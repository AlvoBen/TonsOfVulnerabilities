/*
 * Created on 2004-10-15
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.schema.validator.identity.xpathmatcher;

import java.util.Vector;

import com.sap.engine.lib.schema.validator.xpath.XPathStep;

/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class DescendentElementMatchingStructure extends ElementMatchingStructure {
	
	private String elementUri;
	private String elementName;

	protected DescendentElementMatchingStructure(String elementUri, String elementName, NodeMatchingStructure substructure) {
		super();
		this.elementUri = elementUri;
		this.elementName = elementName;
		this.substructure = substructure;
	}

	protected boolean process(XPathTokenizer tokenizer) {
		XPathStep stepObj = null;
		int beforeProcessingIndex = tokenizer.getIndex();
		for(int i = tokenizer.getTokensCount() - 1; i >= beforeProcessingIndex; i--) {
			tokenizer.setIndex(i);
			stepObj = tokenizer.next();
			if(!stepObj.isAttribute()) {
        if((elementUri == null || elementUri.equals(stepObj.getUriStr())) && 
           (elementName == null || elementName.equals(stepObj.getLocalNameStr())) &&
           (substructure != null && substructure.process(tokenizer))) {
          xPathStepIndex = i;
          return(true);
        }
			}
		}
		return(false);
	}
}
