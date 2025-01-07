/*
 * Created on 2004-10-14
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.schema.validator.identity;

import org.xml.sax.SAXException;

import com.sap.engine.lib.schema.components.SimpleTypeDefinition;
import com.sap.engine.lib.schema.components.impl.ffacets.Value;
import com.sap.engine.lib.schema.validator.SchemaDocHandler;

/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class KeySequence {
	
	private KeyWrapper[] keyWrappers;
	private int registeredKeys;
	private SchemaDocHandler schemaDocHandler;
	private IdentityConstraintValidator owner;
	
	public KeySequence() {
		registeredKeys = 0;
	}
  
  protected void setSchemaDocHandler(SchemaDocHandler schemaDocHandler) {
    this.schemaDocHandler = schemaDocHandler;
  }
  
  protected void setKeyCount(int keysCount) {
    keyWrappers = new KeyWrapper[keysCount];
  }
  
  protected void setOwner(IdentityConstraintValidator owner) {
    this.owner = owner;
  }
  
  public KeyWrapper[] getKeyWrappers() {
    return(keyWrappers);
  }
	
  protected boolean isRegistered(int keyIndex) {
    return(keyWrappers[keyIndex] != null);
  }

  protected void registerValue(Value ffacetsValue, String value, String represent, int keyIndex) throws SAXException {
    KeyWrapper keyWrapper = schemaDocHandler.getReusableObjectsPool().getKeyWrapper();
    keyWrapper.setSchemaDocHandler(schemaDocHandler);
    keyWrapper.setFFacetsValue(ffacetsValue);
    keyWrapper.setValue(value);
    keyWrapper.setNodeRepresentation(represent);
    keyWrappers[keyIndex] = keyWrapper;
    registeredKeys++;
  }
  
	protected boolean match(KeySequence keySequence) {
		if(keyWrappers.length != keySequence.keyWrappers.length) {
			return(false);
		}
		for(int i = 0; i < keyWrappers.length; i++) {
			if(!keyWrappers[i].match(keySequence.keyWrappers[i])) {
				return(false);
			}
		}
		return(true);
	}
	
	protected boolean isQualified() {
		return(registeredKeys == keyWrappers.length);
	}
	
	protected String getRepresentation() {
    StringBuffer representBuffer = schemaDocHandler.getReusableObjectsPool().getStringBuffer();
    representBuffer.append("[");
		for(int i = 0; i < keyWrappers.length; i++) {
			KeyWrapper keyWrapper = keyWrappers[i];
			representBuffer.append(keyWrapper == null ? "no selection" : keyWrapper.getRepresentation());
      representBuffer.append(i == keyWrappers.length - 1 ? "]" : "; ");
		}
    String represent = representBuffer.toString();
    schemaDocHandler.getReusableObjectsPool().reuseStringBuffer(representBuffer);
		return(represent);
	}
  
  public void reuse() {
    registeredKeys = 0;
  }
}
