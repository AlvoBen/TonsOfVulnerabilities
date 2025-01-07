package com.sap.engine.lib.schema.components.impl.structures;

import org.w3c.dom.*;

import com.sap.engine.lib.schema.components.Base;
import com.sap.engine.lib.schema.components.TypeDefinitionBase;

import com.sap.engine.lib.schema.util.Tools;

import java.util.Hashtable;

/**
 * @author Nick Nickolov
 * @version November 2001
 */
public abstract class TypeDefinitionBaseImpl extends RedefineableQualifiedBaseImpl implements TypeDefinitionBase, Cloneable {
	
	protected TypeDefinitionBaseImpl baseTypeDefinition;
  protected boolean isFinalRestriction;
  protected boolean isFinalExtension;
  protected boolean isUrType;

  public TypeDefinitionBaseImpl() {
    this(null, null, false, false);
  }

  public TypeDefinitionBaseImpl(Node associatedNode, SchemaImpl schema, boolean isTopLevel, boolean isRedefined) {
		super(associatedNode, schema, isTopLevel, isRedefined);
  }

  public boolean match(Base typeDefBase) {
  	if(!super.match(typeDefBase)) {
  		return(false);
  	}
  	TypeDefinitionBaseImpl targetTypeDefBase = (TypeDefinitionBaseImpl)typeDefBase;
  	if(baseTypeDefinition == null || targetTypeDefBase.baseTypeDefinition == null) {
  		return(baseTypeDefinition == null && targetTypeDefBase.baseTypeDefinition == null);
  	}
  	
  	return((baseTypeDefinition.isBuiltIn()) ? targetTypeDefBase.baseTypeDefinition.isBuiltIn()
    																				 : Tools.compareBases(baseTypeDefinition, targetTypeDefBase.baseTypeDefinition));
  }
  
  public TypeDefinitionBase getBaseTypeDefinition() {
  	return(baseTypeDefinition);
  }

  public abstract boolean isDerivedFrom(TypeDefinitionBase baseTypeDef, boolean disallowedRestriction, boolean disallowedExtension);

  protected BaseImpl initializeBase(BaseImpl base, Hashtable clonedCollector) {
    TypeDefinitionBaseImpl result = (TypeDefinitionBaseImpl)(super.initializeBase(base, clonedCollector));
    clonedCollector.put(this, result);
    if(baseTypeDefinition != null) {
      result.baseTypeDefinition = baseTypeDefinition.isBuiltIn ? baseTypeDefinition : (TypeDefinitionBaseImpl)(baseTypeDefinition.clone(clonedCollector));
    }
    result.isFinalRestriction = isFinalRestriction;
    result.isFinalExtension = isFinalExtension;
    result.isUrType = isUrType;
    return(result);
  }

  public boolean isFinalExtension() {
    return(isFinalExtension);
  }

  public boolean isFinalRestriction() {
    return(isFinalRestriction);
  }
  
  public String getTargetNamespace() {
  	return(isBuiltIn ? SCHEMA_COMPONENTS_NS  : super.getTargetNamespace());
  }
}

