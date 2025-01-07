/*
 * Created on 2004-10-14
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.schema.validator.identity;

import java.util.Vector;

import org.xml.sax.SAXException;

import com.sap.engine.lib.schema.components.IdentityConstraintDefinition;
import com.sap.engine.lib.schema.validator.SchemaDocHandler;
import com.sap.engine.lib.schema.validator.identity.xpathmatcher.IdentityConstraintXPathMatcher;

/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class KeyrefIdentityConstraintValidator extends IdentityConstraintValidator {
	
	private IdentityConstraintValidator referedIdentityConstraintValidator;
	
	protected KeyrefIdentityConstraintValidator(SchemaDocHandler schemaDocHandler, IdentityConstraintDefinition identConstrDefinition, IdentityConstraintXPathMatcher matcher) {
		super(schemaDocHandler, identConstrDefinition, matcher);
	}
	
	protected void setReferedIdentityConstraintValidator(IdentityConstraintValidator referedIdentityConstraintValidator) {
		this.referedIdentityConstraintValidator = referedIdentityConstraintValidator;
	}
  
	public boolean validate() throws SAXException {
		boolean result = true; 
    Vector keySequences = selectorRepresentToKeySequenceMapping.values();
		for(int i = 0; i < keySequences.size(); i++) {
			KeySequence keySequence1 = (KeySequence)(keySequences.get(i));
			if(keySequence1.isQualified()) {
				boolean hasDublicate = false;
				if(referedIdentityConstraintValidator != null) {
				  for(int j = 0; j < referedIdentityConstraintValidator.selectorRepresentToKeySequenceMapping.values().size(); j++) {
						KeySequence keySequence2 = (KeySequence)(referedIdentityConstraintValidator.selectorRepresentToKeySequenceMapping.values().get(j));
						if(keySequence2.isQualified()) {
							if(keySequence1.match(keySequence2)) {
								hasDublicate = true;
								break;
							}
						}
					}
				}
				if(!hasDublicate) {
					result = false;
					schemaDocHandler.collectError("Node sequence " + keySequence1.getRepresentation() + " does not have a duplicate among the selected node sequences from the refered identity constraint definition (identity constraint definition '" + identConstrDefinition.getQualifiedKey() + "').");
				}
			}
		}
		return(result);
	}
//	
//	public void clear() {
//		super.clear();
//		referedIdentityConstraintValidator = null;
//	}
}
