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
public abstract class ElementMatchingStructure extends NodeMatchingStructure {
	
	protected NodeMatchingStructure substructure;
	protected int xPathStepIndex;
	
	protected ElementMatchingStructure() {
		super();
	}
	
	protected void addSubStructure(NodeMatchingStructure substructure) {
		if(this.substructure == null) {
			this.substructure = substructure;
		} else if(this.substructure instanceof ElementMatchingStructure) {
			((ElementMatchingStructure)this.substructure).addSubStructure(substructure);
		}
	}
	
	protected int getXPathStepIndex(NodeMatchingStructure substructure) {
		if(this.substructure != null) {
			if(this.substructure == substructure) {
				return(xPathStepIndex);
			}
			if(this.substructure instanceof ElementMatchingStructure) {
				return(((ElementMatchingStructure)this.substructure).getXPathStepIndex(substructure));
			}
		}
		return(-1);
	}
}
