/*
 * Created on 2004-10-15
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.schema.validator.identity.xpathmatcher;

/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class ContextElementMatchingStructure extends ElementMatchingStructure {
	
	protected ContextElementMatchingStructure(NodeMatchingStructure substructure) {
		super();
		this.substructure = substructure;
	}
	
	protected boolean process(XPathTokenizer tokenizer) {
    xPathStepIndex = tokenizer.getIndex() - 1;
		return(substructure == null ? true : substructure.process(tokenizer));
	}

}
