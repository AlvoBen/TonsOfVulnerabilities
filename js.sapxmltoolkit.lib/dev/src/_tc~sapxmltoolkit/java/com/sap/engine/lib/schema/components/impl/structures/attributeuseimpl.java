package com.sap.engine.lib.schema.components.impl.structures;

import org.w3c.dom.*;

import com.sap.engine.lib.schema.components.*;
import com.sap.engine.lib.schema.components.impl.ffacets.Value;
import com.sap.engine.lib.schema.components.impl.ffacets.ValueComparator;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import com.sap.engine.lib.xml.dom.DOM;

import java.util.Hashtable;

public final class AttributeUseImpl extends BaseImpl implements AttributeUse {

  protected boolean isRequired;
  protected boolean isProhibited;
  protected AttributeDeclarationImpl attribDeclr;
  protected String valueConstrDefault;
  protected String valueConstrFixed;

  public AttributeUseImpl() {
    this(null, null);
  }

  public AttributeUseImpl(Node associatedNode, SchemaImpl schema) {
		super(associatedNode, schema);
	}
  
  public boolean isRequired() {
    return(isRequired);
  }

  public boolean isProhibited() {
    return(isProhibited);
  }

  public AttributeDeclaration getAttributeDeclaration() {
    return(attribDeclr);
  }

  public String getValueConstraintDefault() {
    return(valueConstrDefault);
  }
  
  public String getValueConstraintFixed() {
    return(valueConstrFixed);
  }
  
  public int getTypeOfComponent() {
    return(C_ATTRIBUTE_USE);
  }
  
  public boolean match(Base attribUse) {
  	if(!super.match(attribUse)) {
  		return(false);
  	}
  	AttributeUseImpl attribUseTarget = (AttributeUseImpl)attribUse;
  	return(isRequired == attribUseTarget.isRequired &&
           isProhibited == attribUseTarget.isProhibited &&
  				 attribDeclr.match(attribUseTarget.attribDeclr) &&
  				 equalValueConstraints(attribUseTarget));
  }
  
  private boolean equalValueConstraints(AttributeUseImpl attribUse) {
  	return(Tools.compareObjects(valueConstrDefault, attribUse.valueConstrDefault) && Tools.compareObjects(valueConstrFixed, attribUse.valueConstrFixed));
  }

  public void load() throws SchemaComponentException {
    valueConstrDefault = loadAttribsCollector.getProperty(NODE_DEFAULT_NAME);
    valueConstrFixed = loadAttribsCollector.getProperty(NODE_FIXED_NAME);
    if(valueConstrDefault != null && valueConstrFixed != null) {
      throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of attribute use " + DOM.toXPath(associatedNode) + " is not correct. 'Default' and 'fixed' must not both be present.");
    }
    String value = loadAttribsCollector.getProperty(NODE_USE_NAME);
    if(value != null) {
      if(value.equals(VALUE_PROHIBITED_NAME)) {
        isProhibited = true;
      } else if(value.equals(VALUE_REQUIRED_NAME)) {
        isRequired = true;
      }
    }
    
    attribDeclr = SchemaStructuresLoader.createAttributeDeclaration(associatedNode, schema, false);
		SchemaStructuresLoader.loadBase(attribDeclr);

    FundamentalFacets ffacets = ((SimpleTypeDefinitionImpl)attribDeclr.typeDefinition).fundamentalFacets;
    if(ffacets != null && valueConstrFixed != null && attribDeclr.fixedValueConstr != null) {
      Value fFacetsFixedValue = ffacets.parse(valueConstrFixed);
      Value attribDeclrFFacetsFixedValue = ffacets.parse(attribDeclr.fixedValueConstr);
      if(ValueComparator.compare(fFacetsFixedValue, attribDeclrFFacetsFixedValue) != COMPARE_RESULT_EQUAL) {
        throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : If the {attribute declaration} has a fixed {value constraint}, then if the attribute use has a {value constraint}, it must also be fixed and its value must match that of the {attribute declaration}'s {value constraint}.");
      }
    }
    
    if(valueConstrDefault != null && (isProhibited || isRequired)) {
      throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of attribute use '" + DOM.toXPath(associatedNode) + "' is not correct. If {default} and {use} are both present, {use} must have the actual value 'optional'.");
    }
  }

  protected BaseImpl initializeBase(BaseImpl base, Hashtable clonedCollector) {
    AttributeUseImpl result = (AttributeUseImpl)(super.initializeBase(new AttributeUseImpl(), clonedCollector));
    result.isRequired = isRequired;
    result.isProhibited = isProhibited;
    result.attribDeclr = (AttributeDeclarationImpl)(attribDeclr.clone(clonedCollector));
    result.valueConstrDefault = valueConstrDefault;
    result.valueConstrFixed = valueConstrFixed;
    return(result);
  }
}

