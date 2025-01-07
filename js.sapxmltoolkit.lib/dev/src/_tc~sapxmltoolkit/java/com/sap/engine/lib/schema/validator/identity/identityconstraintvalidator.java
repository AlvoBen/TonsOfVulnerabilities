/*
 * Created on 2004-10-14
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.schema.validator.identity;

import org.xml.sax.SAXException;

import com.sap.engine.lib.schema.components.IdentityConstraintDefinition;
import com.sap.engine.lib.schema.components.impl.ffacets.Value;
import com.sap.engine.lib.schema.util.MappingCollectorPool;
import com.sap.engine.lib.schema.validator.SchemaDocHandler;
import com.sap.engine.lib.schema.validator.identity.xpathmatcher.IdentityConstraintXPathMatcher;
import com.sap.engine.lib.schema.validator.identity.xpathmatcher.XPathMatchResult;

/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class IdentityConstraintValidator {
	
	protected SchemaDocHandler schemaDocHandler;
	protected IdentityConstraintDefinition identConstrDefinition;
	protected IdentityConstraintXPathMatcher matcher;
	protected int contextXPathOffset;
	protected MappingCollectorPool selectorRepresentToKeySequenceMapping;
	
	protected IdentityConstraintValidator(SchemaDocHandler schemaDocHandler, IdentityConstraintDefinition identConstrDefinition, IdentityConstraintXPathMatcher matcher) {
		this.schemaDocHandler = schemaDocHandler;
		this.identConstrDefinition = identConstrDefinition;
		this.matcher = matcher;
		selectorRepresentToKeySequenceMapping = new MappingCollectorPool();
	}
	
	protected void setContextXPathOffset(int contextXPathOffset) {
		this.contextXPathOffset = contextXPathOffset;
	}
	
	protected IdentityConstraintDefinition getIdentityConstraintDefinition() {
		return(identConstrDefinition);
	}
	
	protected void registerValue(IdentityConstraintRegResult result, Value ffacetsValue, String value, String represent) throws SAXException {
		XPathMatchResult matchResult = matcher.match(contextXPathOffset);
		if(matchResult != null) {
      int selectorXPathStepIndex = matchResult.getSelectorXPathStepIndex();
      int fieldIndex = matchResult.getFieldIndex();
      String selectorRepresent = schemaDocHandler.getXPath().getXPathStep(selectorXPathStepIndex).getRepresentation();
      KeySequence keySequence = (KeySequence)(selectorRepresentToKeySequenceMapping.get(selectorRepresent));
      if(keySequence != null) {
        if(keySequence.isRegistered(fieldIndex)) {
          schemaDocHandler.collectError("Field '" + matchResult.getFieldXPathExpression() + "' matches more than one value within the scope of it's selector (identity constraint definition '" + identConstrDefinition.getQualifiedKey() + "').");
          result.setRegistrationFail(true);
          return;
        }
      } else {
        keySequence = schemaDocHandler.getReusableObjectsPool().getKeySequence();
        keySequence.setSchemaDocHandler(schemaDocHandler);
        keySequence.setOwner(this);
        keySequence.setKeyCount(matcher.getFieldXPathMatchersCount());
        selectorRepresentToKeySequenceMapping.put(selectorRepresent, keySequence);
      }
      keySequence.registerValue(ffacetsValue, value, represent, fieldIndex);
      result.setValueIsRegistered(true);
    }
	}
	
	protected void clear() {
    while(selectorRepresentToKeySequenceMapping.size() > 0) {
      KeySequence keySequence = (KeySequence)(selectorRepresentToKeySequenceMapping.remove(selectorRepresentToKeySequenceMapping.size() - 1));
      schemaDocHandler.getReusableObjectsPool().reuseKeySequence(keySequence);
    }
	}
	
	public abstract boolean validate() throws SAXException;
}
