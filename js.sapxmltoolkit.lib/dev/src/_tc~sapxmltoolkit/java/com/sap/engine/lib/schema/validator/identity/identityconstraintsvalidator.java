/*
 * Created on 2004-10-14
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.schema.validator.identity;

import java.util.Hashtable;
import java.util.Vector;

import org.xml.sax.SAXException;

import com.sap.engine.lib.schema.components.IdentityConstraintDefinition;
import com.sap.engine.lib.schema.components.SimpleTypeDefinition;
import com.sap.engine.lib.schema.components.impl.ffacets.Value;
import com.sap.engine.lib.schema.validator.SchemaDocHandler;
import com.sap.engine.lib.schema.validator.identity.xpathmatcher.IdentityConstraintXPathMatcher;
import com.sap.engine.lib.schema.validator.identity.xpathmatcher.XPathTokenizer;
import com.sap.engine.lib.schema.validator.xpath.XPath;

/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class IdentityConstraintsValidator {
	
	private SchemaDocHandler schemaDocHandler;
	private Vector registeredValidators;
  private Hashtable qKeyToUnregisteredValidatorsMapping;
  private XPathTokenizer xPathTokenizer;
	
	public IdentityConstraintsValidator(SchemaDocHandler schemaDocHandler) {
		this.schemaDocHandler = schemaDocHandler;
		registeredValidators = new Vector();
    qKeyToUnregisteredValidatorsMapping = new Hashtable();
    xPathTokenizer = new XPathTokenizer(schemaDocHandler.getXPath());
	}
  
  public void registerValidators(Vector identityConstrDefinitions, int contextXPathStepsCount) {
    Vector keyrefIdentConstrValidators = new Vector();
    for(int i = 0; i < identityConstrDefinitions.size(); i++) {
      IdentityConstraintDefinition identConstrDefinition = (IdentityConstraintDefinition)(identityConstrDefinitions.get(i));
      IdentityConstraintValidator validator = (IdentityConstraintValidator)(qKeyToUnregisteredValidatorsMapping.get(identConstrDefinition.getQualifiedKey()));
      if(validator == null) {
        if(identConstrDefinition.isIdentityConstraintCategoryKey()) {
          validator = new KeyIdentityConstraintValidator(schemaDocHandler, identConstrDefinition, schemaDocHandler.getReusableObjectsPool().getIdentityConstraintXPathMatcher(identConstrDefinition, xPathTokenizer));
        } else if(identConstrDefinition.isIdentityConstraintCategoryUnique()) {
          validator = new UniqueIdentityConstraintValidator(schemaDocHandler, identConstrDefinition, schemaDocHandler.getReusableObjectsPool().getIdentityConstraintXPathMatcher(identConstrDefinition, xPathTokenizer));
        } else {
          validator = new KeyrefIdentityConstraintValidator(schemaDocHandler, identConstrDefinition, schemaDocHandler.getReusableObjectsPool().getIdentityConstraintXPathMatcher(identConstrDefinition, xPathTokenizer));
          keyrefIdentConstrValidators.add(validator);
        }
      } else {
        qKeyToUnregisteredValidatorsMapping.remove(identConstrDefinition.getQualifiedKey());
      }
      validator.setContextXPathOffset(contextXPathStepsCount);
      registeredValidators.add(validator);
    }
    for(int i = 0; i < keyrefIdentConstrValidators.size(); i++) {
      KeyrefIdentityConstraintValidator keyrefIdentConstrvalidator = (KeyrefIdentityConstraintValidator)(keyrefIdentConstrValidators.get(i));
      IdentityConstraintDefinition refKeyIdentConstrDefinition = keyrefIdentConstrvalidator.getIdentityConstraintDefinition().getReferencedKey();
      for(int j = registeredValidators.size() - 1; j >= 0; j--) {
        IdentityConstraintValidator registeredIdentConstrValidator = (IdentityConstraintValidator)(registeredValidators.get(j));
        if(registeredIdentConstrValidator.getIdentityConstraintDefinition().getQualifiedKey().equals(refKeyIdentConstrDefinition.getQualifiedKey())) {
          keyrefIdentConstrvalidator.setReferedIdentityConstraintValidator(registeredIdentConstrValidator);
          break;
        }
      }
    }
  }
  
	public boolean validate(Vector identityConstrDefinitions) throws SAXException {
    boolean result = true;
    for(int i = 0; i < identityConstrDefinitions.size(); i++) {
      IdentityConstraintValidator validator = (IdentityConstraintValidator)(registeredValidators.get(registeredValidators.size() - 1 - i));
      result = result && validator.validate();
    }
    return(result);
	}
  
  public void unregisterValidators(Vector identityConstrDefinitions) {
    for(int i = 0; i < identityConstrDefinitions.size(); i++) {
      unregisterLastValidator();
    }
  }
	
  private void unregisterLastValidator() {
    IdentityConstraintValidator validator = (IdentityConstraintValidator)(registeredValidators.remove(registeredValidators.size() - 1));
    validator.clear();
    qKeyToUnregisteredValidatorsMapping.put(validator.getIdentityConstraintDefinition().getQualifiedKey(), validator);
  }
  
	public IdentityConstraintRegResult registerValue(Value ffacetsValue, String value, String represent) throws SAXException {
    IdentityConstraintRegResult result = schemaDocHandler.getReusableObjectsPool().getIdentityConstraintRegResult();
    for(int i = 0; i < registeredValidators.size(); i++) {
      IdentityConstraintValidator validator = (IdentityConstraintValidator)(registeredValidators.get(i));
      validator.registerValue(result, ffacetsValue, value, represent);
    }
    return(result);
	}
}
