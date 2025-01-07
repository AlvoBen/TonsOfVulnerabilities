package com.sap.engine.lib.schema.components.impl.structures;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.util.*;

import com.sap.engine.lib.schema.components.*;
import com.sap.engine.lib.schema.components.impl.ffacets.Value;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.validator.SimpleTypeValidator;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import com.sap.engine.lib.xml.dom.DOM;

public abstract class InfoItemDeclarationBaseImpl extends QualifiedBaseImpl implements InfoItemDeclarationBase {

  protected TypeDefinitionBaseImpl typeDefinition;
  protected ComplexTypeDefinitionImpl scope;
  protected String defaultValueConstr;
  protected String fixedValueConstr;

  public InfoItemDeclarationBaseImpl() {
    this(null, null, false);
  }

  public InfoItemDeclarationBaseImpl(Node associatedNode, SchemaImpl schema, boolean isTopLevel) {
		super(associatedNode, schema, isTopLevel);
    if(!isTopLevel) {
      String form = loadAttribsCollector.getProperty(NODE_FORM_NAME);
      if(form != null) {
        isQualified = form.equals(VALUE_QUALIFIED_NAME);
      } else if(getTypeOfComponent() == C_ELEMENT_DECLARATION) {
        isQualified = schema.isElemsFormDefaultQualified();
      } else if(getTypeOfComponent() == C_ATTRIBUTE_DECLARATION) {
        isQualified = schema.isAttribsFormDefaultQualified();
      } else {
        isQualified = false;
      }
    }
  }

  public final TypeDefinitionBase getTypeDefinition() {
    return(typeDefinition);
  }

  public final ComplexTypeDefinition getScope() {
    return(scope);
  }

  public final String getValueConstraintDefault() {
    return(defaultValueConstr);
  }
  
  public final String getValueConstraintFixed() {
    return(fixedValueConstr);
  }
  
	public boolean match(Base infoItemDeclarationBase) {
		if(!super.match(infoItemDeclarationBase)) {
			return(false);
		}
		InfoItemDeclarationBaseImpl targetInfoItemDeclrBase = (InfoItemDeclarationBaseImpl)infoItemDeclarationBase;

		return(Tools.compareBases(typeDefinition, targetInfoItemDeclrBase.typeDefinition) &&
					 Tools.compareObjects(defaultValueConstr, targetInfoItemDeclrBase.defaultValueConstr) &&
					 Tools.compareObjects(fixedValueConstr, targetInfoItemDeclrBase.fixedValueConstr));
	}

  protected void processValueConstraint() throws SchemaComponentException {
    if(defaultValueConstr != null && fixedValueConstr != null) {
      throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of info item declaration base " + DOM.toXPath(associatedNode) + " is not correct. 'Default' and 'fixed' must not both be present.");
    }
    if(typeDefinition.isDerivedFrom(schema.getTopLevelTypeDefinition(SchemaImpl.SCHEMA_COMPONENTS_NS, TYPE_ID_NAME), false, false) && (defaultValueConstr != null || fixedValueConstr != null)) {
      throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of info item declaration base '" + DOM.toXPath(associatedNode) + "' is not correct. If the {type definition} is or is derived from ID then there must not be a {value constraint}.");
    }

    SimpleTypeDefinition simpleTypeDef = typeDefinition instanceof SimpleTypeDefinition ? (SimpleTypeDefinition)typeDefinition : ((ComplexTypeDefinition)typeDefinition).getContentTypeSimpleTypeDefinition();
    if(simpleTypeDef != null) {
      FundamentalFacets fFacets = simpleTypeDef.getFundamentalFacets();
      String valueConstr = null;
      Value fFactesConstrValue = null;
      if(fixedValueConstr != null) {
        valueConstr = fixedValueConstr;
        if(fFacets != null) {
          fFactesConstrValue = fFacets.parse(fixedValueConstr); 
        }
      } else if(defaultValueConstr != null) {
        valueConstr = defaultValueConstr;
        if(fFacets != null) {
          fFactesConstrValue = fFacets.parse(defaultValueConstr); 
        }
      }
      try {
        if(valueConstr != null && !SimpleTypeValidator.validateSimpleTypeDefinition(simpleTypeDef, null, fFactesConstrValue, valueConstr, null, null)) {
          throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of info item declaration base " + DOM.toXPath(associatedNode) + " is not correct. If {value constraint} is present, it's value must be valid with respect to the corresponding simple content.");
        }
      } catch(SAXException saxExc) {
//      $JL-EXC$
      }
    }
  }

  protected BaseImpl initializeBase(BaseImpl base, Hashtable clonedCollector) {
    InfoItemDeclarationBaseImpl result = (InfoItemDeclarationBaseImpl)(super.initializeBase(base, clonedCollector));
    result.typeDefinition = typeDefinition.isBuiltIn ? typeDefinition : (TypeDefinitionBaseImpl)(typeDefinition.clone(clonedCollector));
    result.defaultValueConstr = defaultValueConstr;
    result.fixedValueConstr = fixedValueConstr;
    return(result);
  }
}

