package com.sap.engine.lib.schema.components.impl.structures;

import java.util.*;
import org.w3c.dom.*;

import com.sap.engine.lib.schema.components.*;
import com.sap.engine.lib.schema.util.*;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import com.sap.engine.lib.xml.dom.DOM;

public final class ElementDeclarationImpl extends InfoItemDeclarationBaseImpl implements ElementDeclaration {

  protected boolean isNillable;
  protected ElementDeclarationImpl substitutionGroupAffiliation;
  protected boolean isAbstract;
  protected Vector identityConstrDefs;
  protected Hashtable substitutableElemDeclrs;
  protected boolean isSubstitutionGroupExclusionExtension;
  protected boolean isSubstitutionGroupExclusionRestriction;
  protected boolean isDisallowedSubstitutionSubstitution;
  protected boolean isDisallowedSubstitutionExtension;
  protected boolean isDisallowedSubstitutionRestriction;

  public ElementDeclarationImpl() {
    this(null, null, false);
  }

  public ElementDeclarationImpl(Node associatedNode, SchemaImpl schema, boolean isTopLevel) {
		super(associatedNode, schema, isTopLevel);
    identityConstrDefs = new Vector();
    substitutableElemDeclrs = new Hashtable();
  }

  public boolean isNillable() {
    return(isNillable);
  }

  public Vector getIdentityConstraintDefinitions() {
    return(identityConstrDefs);
  }
  
  public void getIdentityConstraintDefinitions(Vector collector) {
    Tools.removeFromVectorToVector(identityConstrDefs, collector);
  }

  public IdentityConstraintDefinition[] getIdentityConstraintDefinitionsArray() {
    IdentityConstraintDefinition[] result = new IdentityConstraintDefinition[identityConstrDefs.size()];
    identityConstrDefs.copyInto(result);
    return(result);
  }

  public void getSubstitutableElementDeclarations(Vector collector) {
    Tools.removeFromHashtableToVector(substitutableElemDeclrs, collector);
  }

  public ElementDeclaration[] getSubstitutableElementDeclarationsArray() {
    Vector substitutableElemDeclrsCollectorVector = new Vector();
    getSubstitutableElementDeclarations(substitutableElemDeclrsCollectorVector);
    ElementDeclaration[] result = new ElementDeclaration[substitutableElemDeclrsCollectorVector.size()];
    substitutableElemDeclrsCollectorVector.copyInto(result);
    return(result);
  }

  public ElementDeclaration getSubstitutableElementDeclaration(String targetNamespace, String name) {
    return((ElementDeclaration)(substitutableElemDeclrs.get(Tools.generateKey(targetNamespace, name))));
  }

  public ElementDeclaration getSubstitutionGroupAffiliation() {
    return(substitutionGroupAffiliation);
  }

  public boolean isSubstitutionGroupExclusionExtension() {
    return(isSubstitutionGroupExclusionExtension);
  }

  public boolean isSubstitutionGroupExclusionRestriction() {
    return(isSubstitutionGroupExclusionRestriction);
  }

  public boolean isDisallowedSubstitutionSubstitution() {
    return(isDisallowedSubstitutionSubstitution);
  }

  public boolean isDisallowedSubstitutionExtension() {
    return(isDisallowedSubstitutionExtension);
  }

  public boolean isDisallowedSubstitutionRestriction() {
    return(isDisallowedSubstitutionRestriction);
  }

  public boolean isAbstract() {
    return(isAbstract);
  }

  public int getTypeOfComponent() {
    return(C_ELEMENT_DECLARATION);
  }

  public boolean match(Base elementDeclr) {
  	if(!super.match(elementDeclr)) {
	 		return(false);
  	}
  	ElementDeclarationImpl targetElemDeclr = (ElementDeclarationImpl)elementDeclr;
  	return(isNillable == targetElemDeclr.isNillable &&
  				 isAbstract == targetElemDeclr.isAbstract &&
  				 Tools.compareUnorderedBases(identityConstrDefs, targetElemDeclr.identityConstrDefs) &&
  				 Tools.compareBases(substitutionGroupAffiliation, targetElemDeclr.substitutionGroupAffiliation) &&
  				 isSubstitutionGroupExclusionExtension == targetElemDeclr.isSubstitutionGroupExclusionExtension &&
  		 		 isSubstitutionGroupExclusionRestriction == targetElemDeclr.isSubstitutionGroupExclusionRestriction &&
  		 		 isDisallowedSubstitutionSubstitution == targetElemDeclr.isDisallowedSubstitutionSubstitution &&
  		 		 isDisallowedSubstitutionExtension == targetElemDeclr.isDisallowedSubstitutionExtension &&
  		 		 isDisallowedSubstitutionRestriction == targetElemDeclr.isDisallowedSubstitutionRestriction);
  }

  public void load() throws SchemaComponentException {
    if(associatedNode != null) {
      String typeDefAttribValue = loadAttribsCollector.getProperty(NODE_TYPE_NAME);
      if(typeDefAttribValue != null) {
        typeDefinition = (TypeDefinitionBaseImpl)(Tools.getTopLevelComponent(schema, associatedNode, typeDefAttribValue, TYPE_DEFINITION_ID));
				SchemaStructuresLoader.loadBase(typeDefinition);
      }
      defaultValueConstr = loadAttribsCollector.getProperty(NODE_DEFAULT_NAME);
      fixedValueConstr = loadAttribsCollector.getProperty(NODE_FIXED_NAME);
      id = loadAttribsCollector.getProperty(NODE_ID_NAME);
      String nillableAttribValue = loadAttribsCollector.getProperty(NODE_NILLABLE_NAME);
      if(nillableAttribValue != null) {
        isNillable = nillableAttribValue.equals(VALUE_TRUE_NAME);
        if(isNillable && fixedValueConstr != null) {
          throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Defintion of element declaration " + DOM.toXPath(associatedNode) + " is not correct. If the element is declared to be nillable, there must not be fixed {value constraint}.");
        }
      }
      String abstractAttribValue = loadAttribsCollector.getProperty(NODE_ABSTRACT_NAME);
      if(abstractAttribValue != null) {
        isAbstract = abstractAttribValue.equals(VALUE_TRUE_NAME);
      }
      String blockAttribValue = loadAttribsCollector.getProperty(NODE_BLOCK_NAME);
      if(blockAttribValue != null) {
        if(blockAttribValue.equals(VALUE_ALL_NAME)) {
            isDisallowedSubstitutionSubstitution = true;
            isDisallowedSubstitutionExtension = true;
            isDisallowedSubstitutionRestriction = true;
        } else {
          StringTokenizer tokenizer = new StringTokenizer(blockAttribValue);
          while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if(token.equals(VALUE_SUBSTITUTION_NAME)) {
              isDisallowedSubstitutionSubstitution = true;
            } else if(token.equals(VALUE_EXTENSION_NAME)) {
              isDisallowedSubstitutionExtension = true;
            } else if(token.equals(VALUE_RESTRICTION_NAME)) {
              isDisallowedSubstitutionRestriction = true;
            }
          }
        }
      } else {
        isDisallowedSubstitutionSubstitution = schema.isProhibitedSubstitution();
        isDisallowedSubstitutionRestriction = schema.isProhibitedRestriction();
        isDisallowedSubstitutionExtension = schema.isProhibitedExtension();
      }
      String finalAttribValue = loadAttribsCollector.getProperty(NODE_FINAL_NAME);
      if(finalAttribValue != null) {
        if(finalAttribValue.equals(VALUE_ALL_NAME)) {
          isSubstitutionGroupExclusionExtension = true;
          isSubstitutionGroupExclusionRestriction = true;
        } else {
          StringTokenizer tokenizer = new StringTokenizer(finalAttribValue);
          while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if(token.equals(VALUE_EXTENSION_NAME)) {
              isSubstitutionGroupExclusionExtension = true;
            } else if(token.equals(VALUE_RESTRICTION_NAME)) {
              isSubstitutionGroupExclusionRestriction = true;
            }
          }
        }
      } else {
        isSubstitutionGroupExclusionExtension = schema.isFinalExtension();
        isSubstitutionGroupExclusionRestriction = schema.isFinalRestriction();
      }
      NodeList nodeList = associatedNode.getChildNodes();
      for(int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        if(node instanceof Element) {
          String nodeUri = node.getNamespaceURI();
          if(nodeUri != null && nodeUri.equals(SCHEMA_COMPONENTS_NS)) {
            String nodeLocalName = node.getLocalName();
            if(nodeLocalName.equals(NODE_SIMPLE_TYPE_NAME)) {
              if(typeDefinition != null) {
                throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of element declaration " + DOM.toXPath(associatedNode) + " is not correct. 'Type', <simpleType> and <complexType> are exclusive.");
              }
              typeDefinition = SchemaStructuresLoader.createSimpleTypeDefinition(node, schema, false, false);
							SchemaStructuresLoader.loadBase(typeDefinition);
            } else if(nodeLocalName.equals(NODE_COMPLEX_TYPE_NAME)) {
              if(typeDefinition != null) {
                throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of element declaration " + DOM.toXPath(associatedNode) + " is not correct. 'Type', <simpleType> and <complexType> are exclusive.");
              }
              typeDefinition = SchemaStructuresLoader.createComplexTypeDefinition(node, schema, false, false);
							SchemaStructuresLoader.loadBase(typeDefinition);
            } else if(nodeLocalName.equals(NODE_KEY_NAME) || nodeLocalName.equals(NODE_KEYREF_NAME) || nodeLocalName.equals(NODE_UNIQUE_NAME)) {
              IdentityConstraintDefinitionImpl identConstrDef = new IdentityConstraintDefinitionImpl(node, schema);
              identConstrDef.owner = this;
              identityConstrDefs.add(identConstrDef);
							schema.addIdentityConstraintDefinition(identConstrDef);
            } else if(nodeLocalName.equals(NODE_ANNOTATION_NAME)) {
              annotation = SchemaStructuresLoader.createAnnotation(node, schema);
							SchemaStructuresLoader.loadBase(annotation);
            }
          }
        }
      }
      String substGroupAffilAttribValue = loadAttribsCollector.getProperty(NODE_SUBSTITUTION_GROUP_NAME);
      if(substGroupAffilAttribValue != null) {
        substitutionGroupAffiliation = (ElementDeclarationImpl)(Tools.getTopLevelComponent(schema, associatedNode, substGroupAffilAttribValue, ELEMENT_DECLARATION_ID));
				SchemaStructuresLoader.loadBase(substitutionGroupAffiliation);
				if(typeDefinition != null) {
          boolean disallowedRestriction = substitutionGroupAffiliation.isSubstitutionGroupExclusionRestriction;
          boolean disallowedExtension = substitutionGroupAffiliation.isSubstitutionGroupExclusionExtension;
          if(substitutionGroupAffiliation.typeDefinition instanceof ComplexTypeDefinitionImpl) {
            disallowedRestriction = disallowedRestriction || ((ComplexTypeDefinitionImpl)substitutionGroupAffiliation.typeDefinition).isFinalRestriction;
            disallowedExtension = disallowedExtension || ((ComplexTypeDefinitionImpl)substitutionGroupAffiliation.typeDefinition).isFinalExtension;
          }
          if(!typeDefinition.isDerivedFrom(substitutionGroupAffiliation.typeDefinition, disallowedRestriction, disallowedExtension)) {
            throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Defintion of element declaration " + DOM.toXPath(associatedNode) + " is not correct. If there is an {substitution group affiliation}, the {type definition} of the element declaration must be validly derived from the {type definition} of the {substitution group affiliation}.");
          }
				} else {
					typeDefinition = substitutionGroupAffiliation.typeDefinition; 
				}
        substitutionGroupAffiliation.substitutableElemDeclrs.put(getQualifiedKey(), this);
      }
			if(typeDefinition == null) {
				typeDefinition = (TypeDefinitionBaseImpl)(schema.getTopLevelTypeDefinition(SCHEMA_COMPONENTS_NS, TYPE_ANY_TYPE_NAME));
      }
      processValueConstraint();
      schema.addComponent(this);
    }
  }

  protected BaseImpl initializeBase(BaseImpl base, Hashtable clonedCollector) {
    ElementDeclarationImpl result = (ElementDeclarationImpl)(super.initializeBase(base, clonedCollector));
    result.isNillable = isNillable;
    if(substitutionGroupAffiliation != null) {
      result.substitutionGroupAffiliation = (ElementDeclarationImpl)(substitutionGroupAffiliation.clone(clonedCollector));
    }
    result.isAbstract = isAbstract;
    Tools.cloneVectorWithBases(identityConstrDefs, result.identityConstrDefs, clonedCollector);
    Tools.cloneHashtableWithBases(substitutableElemDeclrs, result.substitutableElemDeclrs, clonedCollector);
    result.isSubstitutionGroupExclusionExtension = isSubstitutionGroupExclusionExtension;
    result.isSubstitutionGroupExclusionRestriction = isSubstitutionGroupExclusionRestriction;
    result.isDisallowedSubstitutionSubstitution = isDisallowedSubstitutionSubstitution;
    result.isDisallowedSubstitutionExtension = isDisallowedSubstitutionExtension;
    result.isDisallowedSubstitutionRestriction = isDisallowedSubstitutionRestriction;
    return(result);
  }
}

