package com.sap.engine.lib.schema.components.impl.structures;

import java.util.*;
import org.w3c.dom.*;

import com.sap.engine.lib.schema.components.*;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import com.sap.engine.lib.xml.dom.DOM;

public final class ComplexTypeDefinitionImpl extends TypeDefinitionBaseImpl implements ComplexTypeDefinition {

  protected boolean isDerivationMethodExtension;
  protected boolean isDerivationMethodRestriction;
  protected boolean isProhibitedSubstitutionExtension;
  protected boolean isProhibitedSubstitutionRestriction;
  protected boolean isAbstract;
  protected WildcardImpl attributeWildcard;
  protected Vector attribUses;
  protected SimpleTypeDefinitionImpl contentTypeSimpleTypeDefinition;
  protected ParticleImpl contentTypeContentModel;
  protected boolean isMixed;
  protected Vector annotations;
  protected boolean isPreloaded;
  protected boolean isPreloading;
  
  private boolean hasSimpleContent;
  private boolean hasComplexContent;
  private Vector attribGroups;

  public ComplexTypeDefinitionImpl() {
    this(null, null, false, false);
  }

  public ComplexTypeDefinitionImpl(Node associatedNode, SchemaImpl schema, boolean isTopLevel, boolean isRedefined) {
  	super(associatedNode, schema, isTopLevel, isRedefined);
    attribUses = new Vector();
		annotations = new Vector();
    attribGroups = new Vector();
  }
  
  protected boolean isPreloading() {
    return(isPreloading);
  }
  
  protected void setPreloading(boolean isPreloading) {
    this.isPreloading = isPreloading;
  }
  
  protected boolean isPreloaded() {
    return(isPreloaded);
  }
  
  protected void setPreloaded(boolean isPreloaded) {
    this.isPreloaded = isPreloaded;
  }

  public boolean isDerivationMethodExtension() {
    return(isDerivationMethodExtension);
  }

  public boolean isDerivationMethodRestriction() {
    return(isDerivationMethodRestriction);
  }

  public boolean isProhibitedSubstitutionExtension() {
    return(isProhibitedSubstitutionExtension);
  }

  public boolean isProhibitedSubstitutionRestriction() {
    return(isProhibitedSubstitutionRestriction);
  }

  public boolean isAbstract() {
    return(isAbstract);
  }

  public Wildcard getAttributeWildcard() {
    return(attributeWildcard);
  }

  public Vector getAttributeUses() {
    return(attribUses);
  }
  
  public void getAttributeUses(Vector collector) {
    Tools.removeFromVectorToVector(attribUses, collector);
  }

  public AttributeUse[] getAttributeUsesArray() {
    AttributeUse[] result = new AttributeUse[attribUses.size()];
    attribUses.copyInto(result);
    return(result);
  }

  public SimpleTypeDefinition getContentTypeSimpleTypeDefinition() {
    return(contentTypeSimpleTypeDefinition);
  }

  public Particle getContentTypeContentModel() {
    return(contentTypeContentModel);
  }

  public boolean isContentTypeEmpty() {
    return(contentTypeContentModel == null && contentTypeSimpleTypeDefinition == null);
  }

  public boolean isMixed() {
    return(isMixed);
  }

  public int getTypeOfComponent() {
    return(C_COMPLEX_TYPE_DEFINITION);
  }

  public boolean match(Base complexTypeDef) {
  	if(!super.match(complexTypeDef)) {
  		return(false);
  	}
  	ComplexTypeDefinitionImpl targetComplTypeDef = (ComplexTypeDefinitionImpl)complexTypeDef;
//  	boolean baseTypeDefsCompare = (baseTypeDefinition.isBuiltIn()) ? (targetComplTypeDef.baseTypeDefinition.isBuiltIn() && targetComplTypeDef.baseTypeDefinition.getName().equals(baseTypeDefinition.getName()))
//  																																	: Tools.compareBases(baseTypeDefinition, targetComplTypeDef.baseTypeDefinition);
  	return(isDerivationMethodExtension == targetComplTypeDef.isDerivationMethodExtension &&
		   isFinalExtension == targetComplTypeDef.isFinalExtension &&
  		   isFinalRestriction == targetComplTypeDef.isFinalRestriction &&
  		   isProhibitedSubstitutionExtension == targetComplTypeDef.isProhibitedSubstitutionExtension &&
  		   isProhibitedSubstitutionRestriction == targetComplTypeDef.isProhibitedSubstitutionRestriction &&
  		   isAbstract == targetComplTypeDef.isAbstract &&
  		   isMixed == targetComplTypeDef.isMixed &&
  		   Tools.compareUnorderedBases(attribUses, targetComplTypeDef.attribUses) &&
  		   Tools.compareBases(attributeWildcard, targetComplTypeDef.attributeWildcard) &&
  		   Tools.compareBases(contentTypeContentModel, targetComplTypeDef.contentTypeContentModel) &&
  		   Tools.compareBases(contentTypeSimpleTypeDefinition, targetComplTypeDef.contentTypeSimpleTypeDefinition));
  }

  protected void preload() throws SchemaComponentException {
    if(baseTypeDefinition instanceof ComplexTypeDefinitionImpl) {
      SchemaStructuresLoader.preloadComplexType((ComplexTypeDefinitionImpl)baseTypeDefinition);
    }
    loadAttributeUses();
    loadAttributeWildcard();
    loadContentTypeContentModel();
    validateContentModelOfBaseTypeDefinition();
  }
  
  private void validateContentModelOfBaseTypeDefinition() throws SchemaComponentException {
    if(hasSimpleContent && isDerivationMethodRestriction) {
      ComplexTypeDefinitionImpl baseComplTypeDefinition = (ComplexTypeDefinitionImpl)baseTypeDefinition;
      if(baseComplTypeDefinition.isMixed()) {
        ParticleImpl baseTypeContentModel = (ParticleImpl)(baseComplTypeDefinition.getContentTypeContentModel());
        if(baseTypeContentModel != null && !baseTypeContentModel.isEmptiable()) {
          throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of complex type " + DOM.toXPath(associatedNode) + " is not correct. If the <simpleContent> alternative is chosen and the base type defintion is a complex type with mixed content, the content particle of the base type must be ·emptiable·.");
        }
        if(contentTypeSimpleTypeDefinition == null) {
          throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of complex type " + DOM.toXPath(associatedNode) + " is not correct. If the <simpleContent> alternative is chosen and the base type definition is a complex type with mixed content, there must be a <simpleType> among the [children] of <restriction>.");
        }
      } else if(baseComplTypeDefinition.contentTypeSimpleTypeDefinition == null) {
        throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of complex type " + DOM.toXPath(associatedNode) + " is not correct. If the <simpleContent> alternative is chosen and the base type definition is a complex type, the content of the base type must be simple.");
      }
    }
  }
  
  private void loadContentTypeContentModel() throws SchemaComponentException {
    if(hasComplexContent && isDerivationMethodExtension) {
      ParticleImpl baseContentTypeContentModel = ((ComplexTypeDefinitionImpl)baseTypeDefinition).contentTypeContentModel;
      if(baseContentTypeContentModel != null) {
        if(contentTypeContentModel == null) {
          contentTypeContentModel = baseContentTypeContentModel; 
        } else {
          ParticleImpl explicitContentTypeContentModel = contentTypeContentModel;
          contentTypeContentModel = new ParticleImpl(schema);
          contentTypeContentModel.minOccurs = 1;
          contentTypeContentModel.maxOccurs = 1;
          ModelGroupImpl modelGroup = new ModelGroupImpl(schema);
          modelGroup.compositor = ModelGroupImpl.SEQUENCE;
          modelGroup.addParticle(baseContentTypeContentModel);
          modelGroup.addParticle(explicitContentTypeContentModel);
          modelGroup.calculateEffectiveTotalRange();
          contentTypeContentModel.term = modelGroup;
        }
      }
    }
  }
  
  private void loadAttributeUses() throws SchemaComponentException {
    collectAttributeUsesFromAttributeGroups();
    collectAttributeUsesFromTheBase();
  }
  
  private void collectAttributeUsesFromTheBase() throws SchemaComponentException {
    if(baseTypeDefinition instanceof ComplexTypeDefinitionImpl) {
      if(isDerivationMethodRestriction) {
        collectRestrictedAttributeUses(((ComplexTypeDefinitionImpl)baseTypeDefinition).attribUses);
      } else if(isDerivationMethodExtension) {
        collectExtendedAttributeUses(((ComplexTypeDefinitionImpl)baseTypeDefinition).attribUses);
      }
    }
  }
  
  private void collectRestrictedAttributeUses(Vector restrictedAttribUses) throws SchemaComponentException {
    for(int i = 0; i < restrictedAttribUses.size(); i++) {
      AttributeUseImpl restrictedAttribUse = (AttributeUseImpl)(restrictedAttribUses.get(i));
      collectRestrictedAttributeUse(restrictedAttribUse);
    }
  }
  
  private void collectRestrictedAttributeUse(AttributeUseImpl restrictedAttribUse) throws SchemaComponentException {
    String restrictedAttribUseAttriDeclrNs = restrictedAttribUse.attribDeclr.getTargetNamespace();
    String restrictedAttribUseAttriDeclrName = restrictedAttribUse.attribDeclr.getName();
    TypeDefinitionBase idTypeDefinition = schema.getTopLevelTypeDefinition(SCHEMA_COMPONENTS_NS, TYPE_ID_NAME);
    boolean attribUseAttribDeclrTypeIsDrvFromID = restrictedAttribUse.attribDeclr.typeDefinition.isDerivedFrom(idTypeDefinition, false, false);
    boolean collect = true;
    for(int i = 0; i < attribUses.size(); i++) {
      AttributeUseImpl collectedAttribUse = (AttributeUseImpl)(attribUses.get(i));
      if(collectedAttribUse.attribDeclr.getTargetNamespace().equals(restrictedAttribUseAttriDeclrNs) &&
         collectedAttribUse.attribDeclr.getName().equals(restrictedAttribUseAttriDeclrName)) {
        collect = false;
      }
      if(!collect) {
        break;
      }
      validateAttribUsesForMultipleIDDerivation(collectedAttribUse, restrictedAttribUse, attribUseAttribDeclrTypeIsDrvFromID, idTypeDefinition);
    }
    if(collect) {
      attribUses.add(restrictedAttribUse);
    }
  }
  
  private void collectAttributeUsesFromAttributeGroups() throws SchemaComponentException {
    for(int i = 0; i < attribGroups.size(); i++) {
      AttributeGroupDefinitionImpl attribGroup = (AttributeGroupDefinitionImpl)(attribGroups.get(i));
      collectAttributeUsesFromAttributeGroup(attribGroup);
    }
  }
  
  private void collectAttributeUsesFromAttributeGroup(AttributeGroupDefinitionImpl attribGroup) throws SchemaComponentException {
    collectExtendedAttributeUses(attribGroup.attribUses);
  }
  
  private void collectExtendedAttributeUses(Vector extendedAttribUses) throws SchemaComponentException {
    for(int i = 0; i < extendedAttribUses.size(); i++) {
      AttributeUseImpl attribUse = (AttributeUseImpl)(extendedAttribUses.get(i));
      collectExtendedAttributeUse(attribUse);
    }
  }
  
  private void collectExtendedAttributeUse(AttributeUseImpl attribUse) throws SchemaComponentException {
    String attribUseAttribDeclrNs = attribUse.attribDeclr.getTargetNamespace();
    String attribUseAttribDeclrName = attribUse.attribDeclr.getName();
    TypeDefinitionBase idTypeDefinition = schema.getTopLevelTypeDefinition(SCHEMA_COMPONENTS_NS, TYPE_ID_NAME);
    boolean attribUseAttribDeclrTypeIsDrvFromID = attribUse.attribDeclr.typeDefinition.isDerivedFrom(idTypeDefinition, false, false);
    for(int i = 0; i < attribUses.size(); i++) {
      AttributeUseImpl collectedAttribUse = (AttributeUseImpl)(attribUses.get(i));
      if(collectedAttribUse.attribDeclr.getTargetNamespace().equals(attribUseAttribDeclrNs) &&
         collectedAttribUse.attribDeclr.getName().equals(attribUseAttribDeclrName)) {
        throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of complex type " + DOM.toXPath(associatedNode) + " is not correct. Two of the attibute uses are associated with attribute declarations with the same namespaces and names (namespase : " + attribUse.attribDeclr.getTargetNamespace() + "; name : " + attribUse.attribDeclr.getName() + ").");
      }
      validateAttribUsesForMultipleIDDerivation(collectedAttribUse, attribUse, attribUseAttribDeclrTypeIsDrvFromID, idTypeDefinition);
    }
    attribUses.add(attribUse);
  }
  
  private void validateAttribUsesForMultipleIDDerivation(AttributeUseImpl collectedAttribUse, AttributeUseImpl newAttribUse, boolean newAttribUseAttribDeclrTypeIsDrvFromID, TypeDefinitionBase idTypeDefinition) throws SchemaComponentException {
    if(collectedAttribUse.attribDeclr.typeDefinition.isDerivedFrom(idTypeDefinition, false, false) &&
       newAttribUseAttribDeclrTypeIsDrvFromID) {
       throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of complex type " + DOM.toXPath(associatedNode) + " is not correct. Two of the attibute uses are associated with attribute declarations which types are derived from " + idTypeDefinition.toString() + ". (" + collectedAttribUse.attribDeclr.toString() + "; " + newAttribUse.attribDeclr.toString() + ").");
     }
  }
  
  private void loadAttributeWildcard() throws SchemaComponentException {
    if(isDerivationMethodRestriction) {
      attributeWildcard = determineCompleteAttributeWildcard();
    } else if(isDerivationMethodExtension) {
      WildcardImpl baseAttribWildcard = determineBaseAttributeWildcard();
      WildcardImpl completeAttribWildcard = determineCompleteAttributeWildcard(); 
      if(baseAttribWildcard == null) {
        attributeWildcard = completeAttribWildcard;
      } else {
        if(completeAttribWildcard == null) {
          attributeWildcard = baseAttribWildcard;
        } else {
          attributeWildcard = baseAttribWildcard;
          attributeWildcard.unite(completeAttribWildcard);
        }
      }
    } else {
      attributeWildcard = determineCompleteAttributeWildcard();
    }
  }
  
  private WildcardImpl determineLocalAttributeWildcard() {
    return(attributeWildcard);
  }
  
  private WildcardImpl determineCompleteAttributeWildcard() throws SchemaComponentException {
    WildcardImpl localAttribWildcard = determineLocalAttributeWildcard();
    WildcardImpl completeAttribWildcard = localAttribWildcard;
    for(int i = 0; i < attribGroups.size(); i++) {
      AttributeGroupDefinitionImpl attribGroupDef = (AttributeGroupDefinitionImpl)(attribGroups.get(i));
      WildcardImpl attribGroupDefWildcard = (WildcardImpl)(attribGroupDef.getAttributeWildcard());
      if(attribGroupDefWildcard != null) {
        if(completeAttribWildcard == null) {
          completeAttribWildcard = (WildcardImpl)(attribGroupDefWildcard.clone(new Hashtable()));
        } else {
          completeAttribWildcard.intersect(attribGroupDefWildcard);
        }
      }
    }
    return(completeAttribWildcard);
  }
  
  private WildcardImpl determineBaseAttributeWildcard() {
    if(baseTypeDefinition instanceof ComplexTypeDefinitionImpl) {
      WildcardImpl baseAttribWildcard = (WildcardImpl)(((ComplexTypeDefinitionImpl)baseTypeDefinition).getAttributeWildcard());
      if(baseAttribWildcard != null) {
        return((WildcardImpl)(baseAttribWildcard.clone(new Hashtable())));
      }
    }
    return(null);
  }
  
  private WildcardImpl createWildcard(Node node) throws SchemaComponentException {
    WildcardImpl wildcard = SchemaStructuresLoader.createWildcard(node, schema);
    SchemaStructuresLoader.loadBase(wildcard);
    return(wildcard);
  }

  private AttributeUseImpl createAttributeUse(Node node) throws SchemaComponentException {
    AttributeUseImpl attribUse = SchemaStructuresLoader.createAttributeUse(node, schema);
    SchemaStructuresLoader.loadBase(attribUse);
    return(attribUse);
  }
  
  private AttributeGroupDefinitionImpl createAttributeGroupDefinition(Node node) throws SchemaComponentException {
    AttributeGroupDefinitionImpl attribGroup = SchemaStructuresLoader.createAttributeGroupDefinition(node, schema, false, false);
    SchemaStructuresLoader.loadBase(attribGroup);
    return(attribGroup);
  }
  
  private SimpleTypeDefinitionImpl createSimpleTypeDefinition(Node node) throws SchemaComponentException {
    SimpleTypeDefinitionImpl simpleTypeDef = SchemaStructuresLoader.createSimpleTypeDefinition(node, schema, false, false);
    SchemaStructuresLoader.loadBase(simpleTypeDef);
    return(simpleTypeDef);
  }
  
  private AnnotationImpl createAnnotation(Node node) throws SchemaComponentException {
    AnnotationImpl annotation = SchemaStructuresLoader.createAnnotation(node, schema);
    SchemaStructuresLoader.loadBase(annotation);
    return(annotation);
  }
  
  private ParticleImpl createParticle(Node node) throws SchemaComponentException {
    ParticleImpl particle = SchemaStructuresLoader.createParticle(node, schema);
    SchemaStructuresLoader.loadBase(particle);
    return(particle);
  }
  
  private FacetImpl createFacet(Node node) throws SchemaComponentException {
    FacetImpl facet = SchemaStructuresLoader.createFacet(node, schema);
    SchemaStructuresLoader.loadBase(facet);
    return(facet);
  }

  public void load() throws SchemaComponentException {
    if(associatedNode != null) {
      id = loadAttribsCollector.getProperty(NODE_ID_NAME);
      String abstractAttribValue = loadAttribsCollector.getProperty(NODE_ABSTRACT_NAME);
      if(abstractAttribValue != null) {
        isAbstract = abstractAttribValue.equals(VALUE_TRUE_NAME);
      }
      String blockAttribValue = loadAttribsCollector.getProperty(NODE_BLOCK_NAME);
      if(blockAttribValue != null) {
        if(blockAttribValue.equals(VALUE_ALL_NAME)) {
          isProhibitedSubstitutionExtension = true;
          isProhibitedSubstitutionRestriction = true;
        } else {
          StringTokenizer tokenizer = new StringTokenizer(blockAttribValue);
          while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if(token.equals(VALUE_EXTENSION_NAME)) {
              isProhibitedSubstitutionExtension = true;
            } else if(token.equals(VALUE_RESTRICTION_NAME)) {
              isProhibitedSubstitutionRestriction = true;
            }
          }
        }
      } else {
        isProhibitedSubstitutionExtension = schema.isProhibitedExtension();
        isProhibitedSubstitutionRestriction = schema.isProhibitedRestriction();
      }
      String finalAttribValue = loadAttribsCollector.getProperty(NODE_FINAL_NAME);
      if(finalAttribValue != null) {
        if(finalAttribValue.equals(VALUE_ALL_NAME)) {
          isFinalExtension = true;
          isFinalRestriction = true;
        } else {
          StringTokenizer tokenizer = new StringTokenizer(finalAttribValue);
          while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if(token.equals(VALUE_EXTENSION_NAME)) {
              isFinalExtension = true;
            } else if(token.equals(VALUE_RESTRICTION_NAME)) {
              isFinalRestriction = true;
            }
          }
        }
      } else {
        isFinalExtension = schema.isFinalExtension();
        isFinalRestriction = schema.isFinalRestriction();
      }
      String mixedAttribValue = loadAttribsCollector.getProperty(NODE_MIXED_NAME);
      if(mixedAttribValue != null) {
        isMixed = mixedAttribValue.equals(VALUE_TRUE_NAME);
      }
      NodeList nodeList = associatedNode.getChildNodes();
      for(int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        if(node instanceof Element) {
          String nodeUri = node.getNamespaceURI();
          if(nodeUri != null && nodeUri.equals(SCHEMA_COMPONENTS_NS)) {
            String nodeLocalName = node.getLocalName();
            if(nodeLocalName.equals(NODE_GROUP_NAME) || nodeLocalName.equals(NODE_ALL_NAME) || nodeLocalName.equals(NODE_CHOICE_NAME) || nodeLocalName.equals(NODE_SEQUENCE_NAME)) {
              baseTypeDefinition = (TypeDefinitionBaseImpl)(schema.getTopLevelTypeDefinition(SCHEMA_COMPONENTS_NS, TYPE_ANY_TYPE_NAME));
              contentTypeContentModel = createParticle(node);
            } else if(nodeLocalName.equals(NODE_ANY_ATTRIBUTE_NAME)) {
              attributeWildcard = createWildcard(node);
            } else if(nodeLocalName.equals(NODE_SIMPLE_CONTENT_NAME)) {
              loadSimpleContent(node);
            } else if(nodeLocalName.equals(NODE_COMPLEX_CONTENT_NAME)) {
              loadComplexContent(node);
            } else if(nodeLocalName.equals(NODE_ATTRIBUTE_NAME)) {
              collectExtendedAttributeUse(createAttributeUse(node));
            } else if(nodeLocalName.equals(NODE_ATTRIBUTE_GROUP_NAME)) {
              attribGroups.add(createAttributeGroupDefinition(node));
            } else if(nodeLocalName.equals(NODE_ANNOTATION_NAME)) {
              annotation = createAnnotation(node);
            }
          }
        }
      }

      if(baseTypeDefinition != null) {
        if(isDerivationMethodExtension) {
          if(baseTypeDefinition.isFinalExtension) {
            throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of complex type " + DOM.toXPath(associatedNode) + " is not correct. If the {derivation method} is extension The {final} of the base type definition must not contain extension.");
          }
        } else if(isDerivationMethodRestriction) {
          if(baseTypeDefinition.isFinalRestriction) {
            throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of complex type " + DOM.toXPath(associatedNode) + " is not correct. If the {derivation method} is restriction The {final} of the base type definition must not contain restriction.");
          }
        }
      } else {
        baseTypeDefinition = (TypeDefinitionBaseImpl)(schema.getTopLevelTypeDefinition(SCHEMA_COMPONENTS_NS, TYPE_ANY_TYPE_NAME));
      }
			schema.addComponent(this);
      schema.addComplexTypeDefinition_All(this);
    }
  }

  private void loadSimpleContent(Node node) throws SchemaComponentException {
    hasSimpleContent = true;
    NodeList childNodes = node.getChildNodes();
    for(int j = 0; j < childNodes.getLength(); j++) {
      Node childNode = childNodes.item(j);
      if(childNode.getNodeType() == Node.ELEMENT_NODE) {
        String uri = childNode.getNamespaceURI();
        String elemName = childNode.getLocalName();
        if(uri.equals(SchemaImpl.SCHEMA_COMPONENTS_NS)) {
          if(elemName.equals(NODE_ANNOTATION_NAME)) {
            annotations.add(createAnnotation(childNode));
          } else if(elemName.equals(NODE_RESTRICTION_NAME)) {
            loadSimpleContentRestriction(childNode);
          } else if(elemName.equals(NODE_EXTENSION_NAME)) {
            loadSimpleContentExtension(childNode);
          }
        }
      }
    }
  }
  
  private void loadSimpleContentRestriction(Node node) throws SchemaComponentException {
  	NamedNodeMap attribsMap = node.getAttributes();
  	for(int i = 0; i < attribsMap.getLength(); i++) {
      Node attrib = attribsMap.item(i);
      String attribName = attrib.getLocalName();
      String uri = attrib.getNamespaceURI();
      String value = attrib.getNodeValue();
      if(uri == null || uri.equals("")) {
        if(attribName.equals(NODE_BASE_NAME)) {
          baseTypeDefinition = (TypeDefinitionBaseImpl)(Tools.getTopLevelComponent(schema, node, value, TYPE_DEFINITION_ID));
          validateForCircularDefinitions();
					SchemaStructuresLoader.loadBase(baseTypeDefinition);
          if(!(baseTypeDefinition instanceof ComplexTypeDefinitionImpl)) {
            throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of complex type " + DOM.toXPath(associatedNode) + " is not correct. If the <simpleContent> alternative is chosen, the base type definition must be a complex type.");
          }
          ComplexTypeDefinitionImpl complBaseTypeDef = (ComplexTypeDefinitionImpl)baseTypeDefinition;
          if(complBaseTypeDef.contentTypeSimpleTypeDefinition != null) {
            contentTypeSimpleTypeDefinition = (SimpleTypeDefinitionImpl)(complBaseTypeDef.contentTypeSimpleTypeDefinition.clone(new Hashtable()));
          }
        }
      }
   	}

  	NodeList childNodes = node.getChildNodes();
  	boolean removeEnumerationFacets = true;
		boolean removePatternFacets = true;
  	for(int i = 0; i < childNodes.getLength(); i++) {
  	Node childNode = childNodes.item(i);
  		if(childNode.getNodeType() == Node.ELEMENT_NODE) {
  			String uri = childNode.getNamespaceURI();
  			String elemName = childNode.getLocalName();
  			if(uri.equals(SchemaImpl.SCHEMA_COMPONENTS_NS)) {
  				if(elemName.equals(NODE_ANNOTATION_NAME)) {
  				} else if(elemName.equals(NODE_SIMPLE_TYPE_NAME)) {
            contentTypeSimpleTypeDefinition = createSimpleTypeDefinition(childNode);
          } else if(contentTypeSimpleTypeDefinition != null &&
                    (elemName.equals(FACET_MIN_EXCLUSIVE_NAME) || elemName.equals(FACET_MIN_INCLUSIVE_NAME) ||
										elemName.equals(FACET_MAX_EXCLUSIVE_NAME) || elemName.equals(FACET_MIN_INCLUSIVE_NAME) ||
										elemName.equals(FACET_TOTAL_DIGITS_NAME) || elemName.equals(FACET_FRACTION_DIGITS_NAME) ||
										elemName.equals(FACET_LENGTH_NAME) || elemName.equals(FACET_MIN_LENGTH_NAME) ||
										elemName.equals(FACET_MAX_LENGTH_NAME) || elemName.equals(FACET_ENUMERATION_NAME) ||
										elemName.equals(FACET_WHITE_SPACE_NAME) || elemName.equals(FACET_PATTERN_NAME))) {
            FacetImpl restrictedFacet = createFacet(childNode);
            contentTypeSimpleTypeDefinition.addRestrictedFacet(restrictedFacet, removeEnumerationFacets, removePatternFacets);
						removeEnumerationFacets = !elemName.equals(FACET_ENUMERATION_NAME);
						removePatternFacets = !elemName.equals(FACET_PATTERN_NAME);
          } else if(elemName.equals(NODE_ATTRIBUTE_NAME)) {
            collectExtendedAttributeUse(createAttributeUse(childNode));
          } else if(elemName.equals(NODE_ATTRIBUTE_GROUP_NAME)) {
            attribGroups.add(createAttributeGroupDefinition(childNode));
          } else if(elemName.equals(NODE_ANY_ATTRIBUTE_NAME)) {
            attributeWildcard = createWildcard(childNode);
          }
        }
      }
  	}
    isDerivationMethodRestriction = true;
  }

  private void loadSimpleContentExtension(Node node) throws SchemaComponentException {
    NamedNodeMap attribsMap = node.getAttributes();
    for(int i = 0; i < attribsMap.getLength(); i++) {
      Node attrib = attribsMap.item(i);
      String attribName = attrib.getLocalName();
      String uri = attrib.getNamespaceURI();
      String value = attrib.getNodeValue();
      if(uri == null || uri.equals("")) {
        if(attribName.equals(NODE_BASE_NAME)) {
          baseTypeDefinition = (TypeDefinitionBaseImpl)(Tools.getTopLevelComponent(schema, node, value, TYPE_DEFINITION_ID));
          validateForCircularDefinitions();
		  SchemaStructuresLoader.loadBase(baseTypeDefinition);
          if(baseTypeDefinition instanceof ComplexTypeDefinitionImpl) {
            ComplexTypeDefinitionImpl complBaseTypeDef = (ComplexTypeDefinitionImpl)baseTypeDefinition;
            if(complBaseTypeDef.contentTypeSimpleTypeDefinition == null) {
              throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of complex type " + DOM.toXPath(associatedNode) + " is not correct. If the <simpleContent> and <extension> alternatives are chosen, and the base type definition is a complex type, the content model of base type must be simple.");
            }
            contentTypeSimpleTypeDefinition = complBaseTypeDef.contentTypeSimpleTypeDefinition;
          } else {
            contentTypeSimpleTypeDefinition = (SimpleTypeDefinitionImpl)baseTypeDefinition;
          }
        }
      }
    }

  	NodeList childNodes = node.getChildNodes();
  	for(int i = 0; i < childNodes.getLength(); i++) {
  		Node childNode = childNodes.item(i);
  		if(childNode.getNodeType() == Node.ELEMENT_NODE) {
  			String uri = childNode.getNamespaceURI();
  			String elemName = childNode.getLocalName();
  			if(uri.equals(SchemaImpl.SCHEMA_COMPONENTS_NS)) {
  				if(elemName.equals(NODE_ANNOTATION_NAME)) {
          } else if(elemName.equals(NODE_ATTRIBUTE_NAME)) {
            collectExtendedAttributeUse(createAttributeUse(childNode));
          } else if(elemName.equals(NODE_ATTRIBUTE_GROUP_NAME)) {
            attribGroups.add(createAttributeGroupDefinition(childNode));
          } else if(elemName.equals(NODE_ANY_ATTRIBUTE_NAME)) {
            attributeWildcard = createWildcard(childNode);
          }
        }
      }
    }

    isDerivationMethodExtension = true;
  }

  private void loadComplexContent(Node node) throws SchemaComponentException {
    hasComplexContent = true;
    NamedNodeMap namedNodeMap = node.getAttributes();
    for(int i = 0; i < namedNodeMap.getLength(); i++) {
      Node attrib = namedNodeMap.item(i);
      String uri = attrib.getNamespaceURI();
      if(uri == null || uri.equals("")) {
        String attribName = attrib.getLocalName();
        String value = attrib.getNodeValue();
        if(attribName.equals(NODE_MIXED_NAME)) {
          if(value.equals(VALUE_TRUE_NAME)) {
            isMixed = true;
          } else if(value.equals(VALUE_FALSE_NAME)) {
            isMixed = false;
          }
        }
      }
    }

    NodeList nodeList = node.getChildNodes();
    for(int i = 0; i < nodeList.getLength(); i++) {
      Node childNode = nodeList.item(i);
      if(childNode.getNodeType() == Node.ELEMENT_NODE) {
        String uri = childNode.getNamespaceURI();
        if(uri.equals(SchemaImpl.SCHEMA_COMPONENTS_NS)) {
          String elemName = childNode.getLocalName();
          if(elemName.equals(NODE_RESTRICTION_NAME)) {
            loadComplexContentRestriction(childNode);
          } else if(elemName.equals(NODE_EXTENSION_NAME)) {
            loadComplexContentExtension(childNode);
          }
        }
      }
    }
  }

  private void loadComplexContentRestriction(Node node) throws SchemaComponentException {
    NamedNodeMap namedNodeMap = node.getAttributes();
    for(int i = 0; i < namedNodeMap.getLength(); i++) {
      Node attrib = namedNodeMap.item(i);
      String uri = attrib.getNamespaceURI();
      if(uri == null || uri.equals("")) {
        String attribName = attrib.getLocalName();
        String value = attrib.getNodeValue();
        if(attribName.equals(NODE_BASE_NAME)) {
          baseTypeDefinition = (TypeDefinitionBaseImpl)(Tools.getTopLevelComponent(schema, node, value, TYPE_DEFINITION_ID));
          validateForCircularDefinitions();
					SchemaStructuresLoader.loadBase(baseTypeDefinition);
          if(!(baseTypeDefinition instanceof ComplexTypeDefinitionImpl)) {
            throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of complex type " + DOM.toXPath(associatedNode) + " is not correct. If the <complexContent> alternative is chosen, the base type definition must be a complex type.");
          }
        }
      }
    }

    NodeList nodeList = node.getChildNodes();
    for(int i = 0; i < nodeList.getLength(); i++) {
      Node childNode = nodeList.item(i);
      if(childNode.getNodeType() == Node.ELEMENT_NODE) {
        String uri = childNode.getNamespaceURI();
        if(uri.equals(SchemaImpl.SCHEMA_COMPONENTS_NS)) {
          String elemName = childNode.getLocalName();
          if(elemName.equals(NODE_ATTRIBUTE_NAME)) {
            collectExtendedAttributeUse(createAttributeUse(childNode));
          } else if(elemName.equals(NODE_ATTRIBUTE_GROUP_NAME)) {
            attribGroups.add(createAttributeGroupDefinition(childNode));
          } else if(elemName.equals(NODE_ANY_ATTRIBUTE_NAME)) {
            attributeWildcard = createWildcard(childNode);
          } else if(elemName.equals(NODE_GROUP_NAME) || elemName.equals(NODE_ALL_NAME) || elemName.equals(NODE_CHOICE_NAME) || elemName.equals(NODE_SEQUENCE_NAME)) {
            contentTypeContentModel = createParticle(childNode);
          }
        }
      }
    }
    isDerivationMethodRestriction = true;
  }

  private void loadComplexContentExtension(Node node) throws SchemaComponentException {
    NamedNodeMap namedNodeMap = node.getAttributes();
    for(int i = 0; i < namedNodeMap.getLength(); i++) {
      Node attrib = namedNodeMap.item(i);
      String uri = attrib.getNamespaceURI();
      if(uri == null || uri.equals("")) {
        String attribName = attrib.getLocalName();
        String value = attrib.getNodeValue();
        if(attribName.equals(NODE_BASE_NAME)) {
          baseTypeDefinition = (TypeDefinitionBaseImpl)(Tools.getTopLevelComponent(schema, node, value, TYPE_DEFINITION_ID));
          validateForCircularDefinitions();
					SchemaStructuresLoader.loadBase(baseTypeDefinition);
          if(!(baseTypeDefinition instanceof ComplexTypeDefinitionImpl)) {
            throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of complex type " + DOM.toXPath(associatedNode) + " is not correct. If the <complexContent> alternative is chosen, the base type definition must be a complex type.");
          }
        }
      }
    }

    NodeList nodeList = node.getChildNodes();
    for(int i = 0; i < nodeList.getLength(); i++) {
      Node childNode = nodeList.item(i);
      if(childNode.getNodeType() == Node.ELEMENT_NODE) {
        String uri = childNode.getNamespaceURI();
        if(uri.equals(SchemaImpl.SCHEMA_COMPONENTS_NS)) {
          String elemName = childNode.getLocalName();
          if(elemName.equals(NODE_ATTRIBUTE_NAME)) {
            collectExtendedAttributeUse(createAttributeUse(childNode));
          } else if(elemName.equals(NODE_ATTRIBUTE_GROUP_NAME)) {
            attribGroups.add(createAttributeGroupDefinition(childNode));
          } else if(elemName.equals(NODE_ANY_ATTRIBUTE_NAME)) {
            attributeWildcard = createWildcard(childNode);
          } else if(elemName.equals(NODE_GROUP_NAME) || elemName.equals(NODE_ALL_NAME) || elemName.equals(NODE_CHOICE_NAME) || elemName.equals(NODE_SEQUENCE_NAME)) {
            contentTypeContentModel = createParticle(childNode);
          }
        }
      }
    }
    isDerivationMethodExtension = true;
  }

  private void validateForCircularDefinitions() throws SchemaComponentException {
    if(baseTypeDefinition.isLoading() && baseTypeDefinition.isDerivedFrom(this, false, false)) {
      throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of complex type " + DOM.toXPath(associatedNode) + " is not correct. Circular definitions detected.");
    }
  }

  public boolean isDerivedFrom(TypeDefinitionBase typeDefinition, boolean disallowedRestriction, boolean disallowedExtension) {
    TypeDefinitionBaseImpl typeDefBase = (TypeDefinitionBaseImpl)typeDefinition;
    if(typeDefBase == null) {
      return(false);
    }
    if(typeDefBase.isUrType && typeDefBase.name.equals(TYPE_ANY_TYPE_NAME)) {
      return(true);
    }
    if(isUrType && name.equals(TYPE_ANY_TYPE_NAME)) {
      return(false);
    }
    if((isDerivationMethodExtension && disallowedExtension) || (isDerivationMethodRestriction && disallowedRestriction)) {
      return(false);
    }
    if(this == typeDefBase) {
      return(true);
    }

    if(!baseTypeDefinition.isUrType) {
      return(baseTypeDefinition.isDerivedFrom(typeDefinition, disallowedRestriction, disallowedExtension));
    }
    return(false);
  }

  protected BaseImpl initializeBase(BaseImpl base, Hashtable clonedCollector) {
    ComplexTypeDefinitionImpl result = (ComplexTypeDefinitionImpl)(super.initializeBase(base, clonedCollector));
    result.isDerivationMethodExtension = isDerivationMethodExtension;
    result.isDerivationMethodRestriction = isDerivationMethodRestriction;
    result.isProhibitedSubstitutionExtension = isProhibitedSubstitutionExtension;
    result.isProhibitedSubstitutionRestriction = isProhibitedSubstitutionRestriction;
    result.isAbstract = isAbstract;
    if(attributeWildcard != null) {
      result.attributeWildcard = (WildcardImpl)(attributeWildcard.clone(clonedCollector));
    }
    Tools.cloneVectorWithBases(attribUses, result.attribUses, clonedCollector);
    if(contentTypeSimpleTypeDefinition != null) {
      result.contentTypeSimpleTypeDefinition = (SimpleTypeDefinitionImpl)(contentTypeSimpleTypeDefinition.clone(clonedCollector));
    }
    if(contentTypeContentModel != null) {
      result.contentTypeContentModel = (ParticleImpl)(contentTypeContentModel.clone(clonedCollector));
    }
    result.isMixed = isMixed;
    Tools.cloneVectorWithBases(annotations, result.annotations, clonedCollector);
    return(result);
  }
}

