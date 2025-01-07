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
public final class KeyIdentityConstraintValidator extends IdentityConstraintValidator {

	protected KeyIdentityConstraintValidator(SchemaDocHandler schemaDocHandler, IdentityConstraintDefinition identConstrDefinition, IdentityConstraintXPathMatcher matcher) {
		super(schemaDocHandler, identConstrDefinition, matcher);
	}
	
	public boolean validate() throws SAXException {
    Vector keySequences  = selectorRepresentToKeySequenceMapping.values();
		for(int i = 0; i < keySequences.size(); i++) {
			KeySequence keySequence1 = (KeySequence)(keySequences.get(i));
			if(keySequence1.isQualified()) {
				for(int j = i + 1; j < keySequences.size(); j++) {
					KeySequence keySequence2 = (KeySequence)(keySequences.get(j));
					if(keySequence2.isQualified()) {
						if(keySequence1.match(keySequence2)) {
							schemaDocHandler.collectError("Node sequence " + keySequence1.getRepresentation() + " is identical to the node sequence " + keySequence2.getRepresentation() + ". (identity constraint definition '" + identConstrDefinition.getQualifiedKey() + "').");
							return(false);
						}
					} else {
						schemaDocHandler.collectError("Node sequence " + keySequence2.getRepresentation() + " has to be qualified. All of the {fields}, with the node selected by the {selector} as a context node, should select exactly one node (identity constraint definition '" + identConstrDefinition.getQualifiedKey() + "').");
						return(false);
					}
				}
			} else {
				schemaDocHandler.collectError("Node sequence " + keySequence1.getRepresentation() + " has to be qualified. All of the {fields}, with the node selected by the {selector} as a context node, should select exactly one node (identity constraint definition '" + identConstrDefinition.getQualifiedKey() + "').");
				return(false);
			}
		}
		return(true);
	}
}
