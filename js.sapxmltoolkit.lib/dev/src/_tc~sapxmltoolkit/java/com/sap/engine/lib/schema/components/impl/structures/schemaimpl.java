package com.sap.engine.lib.schema.components.impl.structures;

import java.util.*;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.engine.lib.schema.Constants;
import com.sap.engine.lib.schema.components.*;
import com.sap.engine.lib.schema.components.impl.LoaderImpl;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import com.sap.engine.lib.schema.util.Tools;

public final class SchemaImpl extends BaseImpl implements Schema {

	protected static BuiltInTypesCollector builtInTypesCollector;

	private Vector allComponentsCollector;
	private LoaderImpl loader;
	private boolean elemsFormDefaultIsQualified;
	private boolean attribsFormDefaultIsQualified;
	private String targetNamespace;
	private boolean isFinalExtension;
	private boolean isFinalRestriction;
	private boolean isProhibitedSubstitution;
	private boolean isProhibitedExtension;
	private boolean isProhibitedRestriction;
	private boolean isProhibitedList;
	private boolean isProhibitedUnion;
	private String location;
	private Hashtable idToTopLevelComponentsMapping;
	private Vector importedSchemasCollector;
	private Vector includedSchemasCollector;
	private Element schemaElement;
	private Vector importedNamespacesCollector;
	private String includingNamespace;
  private Vector allComplexTypeDefinitions;
  private Vector referenceableSchemas;

  static {
    if(builtInTypesCollector == null) {
      builtInTypesCollector = new BuiltInTypesCollector();
    }
  }

  public SchemaImpl(SchemaImpl[] schemas) {
  	init();
    augmentWithReferenceableSchemas(schemas);
  }
  
	public SchemaImpl(SchemaImpl[][] schemas) {
		init();
		for(int i = 0; i < schemas.length; i++) {
			augmentWithReferenceableSchemas(schemas[i]);
		}
	}
	
	public void augmentWithReferenceableSchemas(SchemaImpl[] schemas) {
		for(int i = 0; i < schemas.length; i++) {
      augmentWithReferenceableSchema(schemas[i]);
		}
	}
	
	public void augmentWithReferenceableSchema(SchemaImpl schema) {
		try {
			if(schema != this) {
				if(schema.getTargetNamespace().equals(targetNamespace)) {
					includeSchema(schema, false);
				} else {
					importSchema(schema.getTargetNamespace(), schema, false);
				}
			}
		} catch(SchemaComponentException schemaCompExc) {
//    $JL-EXC$		
		}
	}
  
  public SchemaImpl(Element schemaElement, LoaderImpl loader, String location, String includingNamespace) throws SchemaComponentException {
  	init();
  	this.loader = loader;
  	this.location = location;
  	this.schemaElement = schemaElement;
  	this.includingNamespace = includingNamespace;
  	initSchemaInfo();
  }
  
  private void init() {
		initIdToNSQBasesStorageMapping();
    allComponentsCollector = new Vector();
    importedSchemasCollector = new Vector();
		includedSchemasCollector = new Vector();
		importedNamespacesCollector = new Vector();
    allComplexTypeDefinitions = new Vector();
    referenceableSchemas = new Vector();
		targetNamespace = "";
		isLoaded = false;
  }

	private void initIdToNSQBasesStorageMapping() {
		idToTopLevelComponentsMapping = new Hashtable();
		idToTopLevelComponentsMapping.put(TYPE_DEFINITION_ID, new Hashtable());
		idToTopLevelComponentsMapping.put(ELEMENT_DECLARATION_ID, new Hashtable());
		idToTopLevelComponentsMapping.put(ATTRIBUTE_DECLARATION_ID, new Hashtable());
		idToTopLevelComponentsMapping.put(ATTRIBUTE_GROUP_DEFINITION_ID, new Hashtable());
		idToTopLevelComponentsMapping.put(MODEL_GROUP_DEFINITION_ID, new Hashtable());
		idToTopLevelComponentsMapping.put(IDENTITY_CONSTRAINT_DEFINITION_ID, new Hashtable());
		idToTopLevelComponentsMapping.put(NOTATION_DECLARATION_ID, new Hashtable());
	}
  
  private void initSchemaInfo() throws SchemaComponentException {
		NamedNodeMap namedNodeMap = schemaElement.getAttributes();
		for(int i = 0; i < namedNodeMap.getLength(); i++) {
			Node attrib = namedNodeMap.item(i);
			String attribUri = attrib.getNamespaceURI();
			String localName = attrib.getLocalName();
			String attribQName = attrib.getNodeName();
			String attribValue = attrib.getNodeValue();
			if(attribUri == null || attribUri.equals("")) {
				if(localName.equals(NODE_ATTRIB_FORM_DEFAULT_NAME)) {
					attribsFormDefaultIsQualified = attribValue.equals(VALUE_QUALIFIED_NAME);
				} else if(localName.equals(NODE_ELEM_FORM_DEFAULT_NAME)) {
					elemsFormDefaultIsQualified = attribValue.equals(VALUE_QUALIFIED_NAME);
				} else if(localName.equals(NODE_TARGET_NAMESPACE_NAME)) {
					targetNamespace = (attribValue == null) ? "" : attribValue;
				} else if(localName.equals(NODE_BLOCK_DEFAULT_NAME)) {
					if(attribValue.equals(VALUE_ALL_NAME)) {
						isProhibitedSubstitution = true;
						isProhibitedExtension = true;
						isProhibitedRestriction = true;
						isProhibitedList = true;
						isProhibitedUnion = true;
					} else {
						StringTokenizer tokenizer = new StringTokenizer(attribValue);
						while(tokenizer.hasMoreTokens()) {
							attribValue = tokenizer.nextToken();
							if(attribValue.equals(VALUE_SUBSTITUTION_NAME)) {
								isProhibitedSubstitution = true;
							} else if(attribValue.equals(VALUE_EXTENSION_NAME)) {
								isProhibitedExtension = true;
							} else if(attribValue.equals(VALUE_RESTRICTION_NAME)) {
								isProhibitedRestriction = true;
							} else if(attribValue.equals(VALUE_LIST_NAME)) {
								isProhibitedList = true;
							} else if(attribValue.equals(VALUE_UNION_NAME)) {
								isProhibitedUnion = true;
							}
						}
					}
				} else if(localName.equals(NODE_FINAL_DEFAULT_NAME)) {
					if(attribValue.equals(VALUE_ALL_NAME)) {
						isFinalExtension = true;
						isFinalRestriction = true;
					} else {
						StringTokenizer tokenizer = new StringTokenizer(attribValue);
						while(tokenizer.hasMoreTokens()) {
							attribValue = tokenizer.nextToken();
							if(attribValue.equals(VALUE_EXTENSION_NAME)) {
								isFinalExtension = true;
							} else if(attribValue.equals(VALUE_RESTRICTION_NAME)) {
								isFinalRestriction = true;
							}
						}
					}
				}
			}
		}
  }
  
  public void collectComponents() throws SchemaComponentException {
		NodeList nodeList = schemaElement.getChildNodes();
		for(int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(node instanceof Element) {
				String nodeUri = node.getNamespaceURI();
				String nodeLocalName = node.getLocalName();
				if(nodeUri != null && nodeUri.equals(SCHEMA_COMPONENTS_NS)) {
					if(nodeLocalName.equals(NODE_IMPORT_NAME)) {
						importSchemas((Element)node);
					} else if(nodeLocalName.equals(NODE_INCLUDE_NAME)) {
						includeSchemas((Element)node);
					} else if(nodeLocalName.equals(NODE_REDEFINE_NAME)) {
            redefineSchemas((Element)node);
					} else if(nodeLocalName.equals(NODE_SIMPLE_TYPE_NAME)) {
            addSimpleTypeDefinition(SchemaStructuresLoader.createSimpleTypeDefinition(node, this, true, false));
					} else if(nodeLocalName.equals(NODE_COMPLEX_TYPE_NAME)) {
            addComplexTypeDefinition(SchemaStructuresLoader.createComplexTypeDefinition(node, this, true, false));
					} else if(nodeLocalName.equals(NODE_GROUP_NAME)) {
            addModelGroupDefinition(SchemaStructuresLoader.createModelGroupDefinition(node, this, true, false));
					} else if(nodeLocalName.equals(NODE_ATTRIBUTE_GROUP_NAME)) {
            addAttributeGroupDefinition(SchemaStructuresLoader.createAttributeGroupDefinition(node, this, true, false));
					} else if(nodeLocalName.equals(NODE_ELEMENT_NAME)) {
						addElementDeclaration(SchemaStructuresLoader.createElementDeclaration(node, this, true));
					} else if(nodeLocalName.equals(NODE_ATTRIBUTE_NAME)) {
						addAttributeDeclaration(SchemaStructuresLoader.createAttributeDeclaration(node, this, true));
					} else if(nodeLocalName.equals(NODE_NOTATION_NAME)) {
						addNotationDeclaration(SchemaStructuresLoader.createNotationDeclaration(node, this));
					}
				}
			}
		}
  }
  
  private void importSchemas(Element importElement) throws SchemaComponentException {
		Attr targetNsAttrib = importElement.getAttributeNode(NODE_IMPORT_NAMESPACE_NAME);
		String importedNamespace = targetNsAttrib == null ? "" : targetNsAttrib.getValue();
		Attr locationAttrib = importElement.getAttributeNode(NODE_SCHEMA_LOCATION_NAME);
		String importedLocation = locationAttrib == null ? null : locationAttrib.getValue();
		if(importedLocation == null) {
			importedNamespacesCollector.add(importedNamespace);
		} else {
			SchemaImpl[] importedSchemas = loader.produceSchemas_Location(location, importedLocation, null, null);
			for(int i = 0; i < importedSchemas.length; i++) {
				importSchema(importedNamespace, importedSchemas[i], false);
			} 
		}
  }
  
  private void importSchema(String importedNamespace, SchemaImpl importedSchema, boolean runtimeLoadingImport) throws SchemaComponentException {
  	String namespace = importedSchema.getTargetNamespace();
  	if(!namespace.equals(importedNamespace) || importedNamespace.equals(targetNamespace)) {
			throw new SchemaComponentException("[location : '" + location + "'] ERROR : Can not import '" + importedSchema.getLocation() + "' schema document. The imported schema document must not have the targetNamespace as the <import>ing schema document, or it's targetNamespace must be with value '" + importedNamespace + "'.");  	
		}
    if(!importedSchemasCollector.contains(importedSchema)) {
      importedSchemasCollector.add(importedSchema);
    }
    if(runtimeLoadingImport) {
      SchemaStructuresLoader.loadBase(importedSchema);
    }
  }
  
  private void includeSchemas(Element includeElement) throws SchemaComponentException {
		Attr locationAttrib = includeElement.getAttributeNode(NODE_SCHEMA_LOCATION_NAME);
		String includedLocation = locationAttrib == null ? null : locationAttrib.getValue();
		if(includedLocation != null) {
			includeSchemas(loader.produceSchemas_Location(location, includedLocation, includingNamespace == null ? targetNamespace : includingNamespace, null), false);
		}
  }
  
  private void redefineSchemas(Element redefineElement) throws SchemaComponentException {
    Attr locationAttrib = redefineElement.getAttributeNode(NODE_SCHEMA_LOCATION_NAME);
    String redefinedLocation = locationAttrib == null ? null : locationAttrib.getValue();
    if(redefinedLocation != null) {
      redefineSchemas(loader.produceSchemas_Location(location, redefinedLocation, includingNamespace == null ? targetNamespace : includingNamespace, null), redefineElement);
    }
  }
  
  private void includeSchemas(SchemaImpl[] includedSchemas, boolean runtimeLoadingInclude) throws SchemaComponentException {
		for(int j = 0; j < includedSchemas.length; j++) {
			includeSchema(includedSchemas[j], runtimeLoadingInclude);
		} 
  }
  
  private void redefineSchemas(SchemaImpl[] redefinedSchemas, Element redefineElement) throws SchemaComponentException {
    for(int j = 0; j < redefinedSchemas.length; j++) {
      redefineSchema(redefinedSchemas[j], redefineElement);
    } 
  }
  
  private void includeSchema(SchemaImpl includedSchema, boolean runtimeLoadingInclude) throws SchemaComponentException {
  	if(includedSchema != this && !includedSchemasCollector.contains(includedSchema)) {
	  	if(!includedSchema.getTargetNamespace().equals("") && !includedSchema.getTargetNamespace().equals(targetNamespace)) {
	  		throw new SchemaComponentException("[location : '" + location + "'] ERROR : Can not include '" + includedSchema.getLocation() + "' schema document. The included schema documents must have the same targetNamespace as the <include>ing schema document, or no targetNamespace at all.");
	  	}
      if(!includedSchemasCollector.contains(includedSchema)) {
        includedSchemasCollector.add(includedSchema);
      }
      if(runtimeLoadingInclude) {
        SchemaStructuresLoader.loadBase(includedSchema);
      }
  	}
  }
  
  private void redefineSchema(SchemaImpl redefinedSchema, Element redefineElement) throws SchemaComponentException {
    includeSchema(redefinedSchema, false);
    collectRedefinedComponents(redefineElement, redefinedSchema);
  }
  
  private void collectRedefinedComponents(Element redefineElement, SchemaImpl redefinedSchema) throws SchemaComponentException {
    NodeList nodeList = redefineElement.getChildNodes();
    for(int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if(node instanceof Element) {
        String nodeUri = node.getNamespaceURI();
        String nodeLocalName = node.getLocalName();
        if(nodeUri != null && nodeUri.equals(SCHEMA_COMPONENTS_NS)) {
          if(nodeLocalName.equals(NODE_SIMPLE_TYPE_NAME)) {
            addRedefinedSimpleTypeDefinition(SchemaStructuresLoader.createSimpleTypeDefinition(node, this, true, true), redefinedSchema);
          } else if(nodeLocalName.equals(NODE_COMPLEX_TYPE_NAME)) {
            addRedefinedComplexTypeDefinition(SchemaStructuresLoader.createComplexTypeDefinition(node, this, true, true), redefinedSchema);
          } else if(nodeLocalName.equals(NODE_GROUP_NAME)) {
            addRedefinedModelGroupDefinition(SchemaStructuresLoader.createModelGroupDefinition(node, this, true, true), redefinedSchema);
          } else if(nodeLocalName.equals(NODE_ATTRIBUTE_GROUP_NAME)) {
            addRedefinedAttributeGroupDefinition(SchemaStructuresLoader.createAttributeGroupDefinition(node, this, true, true), redefinedSchema);
          }
        }
      }
    }
  }
  
  private void addRedefinedSimpleTypeDefinition(SimpleTypeDefinitionImpl simpleTypeDefinition, SchemaImpl redefinedSchema) throws SchemaComponentException {
    processTopLevelComponentForRedefinition(simpleTypeDefinition, TYPE_DEFINITION_ID, redefinedSchema);
    addSimpleTypeDefinition(simpleTypeDefinition);
  }
  
  private void addRedefinedComplexTypeDefinition(ComplexTypeDefinitionImpl complexTypeDefinition, SchemaImpl redefinedSchema) throws SchemaComponentException {
    processTopLevelComponentForRedefinition(complexTypeDefinition, TYPE_DEFINITION_ID, redefinedSchema);
    addComplexTypeDefinition(complexTypeDefinition);
  }
  
  private void addRedefinedModelGroupDefinition(ModelGroupDefinitionImpl modelGroupDefinition, SchemaImpl redefinedSchema) throws SchemaComponentException {
    processTopLevelComponentForRedefinition(modelGroupDefinition, MODEL_GROUP_DEFINITION_ID, redefinedSchema);
    addModelGroupDefinition(modelGroupDefinition);
  }
  
  private void addRedefinedAttributeGroupDefinition(AttributeGroupDefinitionImpl attributeGroupDefinition, SchemaImpl redefinedSchema) throws SchemaComponentException {
    processTopLevelComponentForRedefinition(attributeGroupDefinition, ATTRIBUTE_GROUP_DEFINITION_ID, redefinedSchema);
    addAttributeGroupDefinition(attributeGroupDefinition);
  }
  
  private void processTopLevelComponentForRedefinition(QualifiedBaseImpl qualifiedBase, String componentId, SchemaImpl redefinedSchema) throws SchemaComponentException {
    String redefinedSchemaNs = redefinedSchema.getTargetNamespace();
    
    String qualifiedBaseNamespace = qualifiedBase.getTargetNamespace();
    String qualifiedBaseName = qualifiedBase.getName();
    
    QualifiedBaseImpl redefinedQualifiedBase = (QualifiedBaseImpl)(redefinedSchema.getTopLevelComponent(redefinedSchemaNs, qualifiedBaseName, componentId));
    
    SchemaImpl redefinedQBaseOwnerSchema = (SchemaImpl)(redefinedQualifiedBase.getOwnerSchema());
    redefinedQBaseOwnerSchema.augmentWithReferenceableSchema(this);
    
    String newName = generateRandomName(qualifiedBaseName);
    
    redefinedQBaseOwnerSchema.removeTopLevelComponent(qualifiedBaseName, componentId);
    redefinedQualifiedBase.setName(newName);
    redefinedQBaseOwnerSchema.addTopLevelComponent(redefinedQualifiedBase, componentId);
    
    if(componentId.equals(TYPE_DEFINITION_ID)) {
      changeReferenceAttributeValue_TypeDefinition((Element)(qualifiedBase.getAssociatedDOMNode()), newName, qualifiedBaseNamespace, qualifiedBaseName);
    } else if(componentId.equals(MODEL_GROUP_DEFINITION_ID)) {
      changeReferenceAttributeValue_GroupDefinition((Element)(qualifiedBase.getAssociatedDOMNode()), NODE_GROUP_NAME, newName, qualifiedBaseNamespace, qualifiedBaseName);
    } else {
      changeReferenceAttributeValue_GroupDefinition((Element)(qualifiedBase.getAssociatedDOMNode()), NODE_ATTRIBUTE_GROUP_NAME, newName, qualifiedBaseNamespace, qualifiedBaseName);
    }
  }
  
  private void changeReferenceAttributeValue_TypeDefinition(Element scanedElement, String newName, String refNamespace, String refName) throws SchemaComponentException {
    if(scanedElement.getNamespaceURI().equals(SCHEMA_COMPONENTS_NS) && (scanedElement.getLocalName().equals(NODE_RESTRICTION_NAME) || scanedElement.getLocalName().equals(NODE_EXTENSION_NAME))) {
      Attr refAttrib = scanedElement.getAttributeNode(NODE_BASE_NAME);
      if(changeRefAttributeValue(refAttrib, newName, refNamespace, refName)) {
        return;
      }
    }
    NodeList nodeList = scanedElement.getChildNodes();
    for(int i = 0; i < nodeList.getLength(); i++) {
      Node childNode = nodeList.item(i);
      if(childNode.getNodeType() == Node.ELEMENT_NODE) {
        changeReferenceAttributeValue_TypeDefinition((Element)childNode, newName, refNamespace, refName);
      }
    }
  }
  
  private void changeReferenceAttributeValue_GroupDefinition(Element scanedElement, String groupElementName, String newName, String refNamespace, String refName) throws SchemaComponentException {
    if(scanedElement.getNamespaceURI().equals(SCHEMA_COMPONENTS_NS) && scanedElement.getLocalName().equals(groupElementName)) {
      Attr refAttrib = scanedElement.getAttributeNode(NODE_REF_NAME);
      if(changeRefAttributeValue(refAttrib, newName, refNamespace, refName)) {
        return;
      }
    }
    NodeList nodeList = scanedElement.getChildNodes();
    for(int i = 0; i < nodeList.getLength(); i++) {
      Node childNode = nodeList.item(i);
      if(childNode.getNodeType() == Node.ELEMENT_NODE) {
        changeReferenceAttributeValue_GroupDefinition((Element)childNode, groupElementName, newName, refNamespace, refName);
      }
    }
  }
  
  private boolean changeRefAttributeValue(Attr refAttrib, String newName, String refNamespace, String refName) throws SchemaComponentException {
    if(refAttrib != null) {
      String refAttribValue = refAttrib.getValue();
      String[] namespaceAndName = Tools.getNamespaceAndName(refAttribValue, this, refAttrib.getOwnerElement());
      String namespace = namespaceAndName[0];
      String name = namespaceAndName[1];
      if(refNamespace.equals(namespace) && refName.equals(name)) {
        changeRefAttributeValue(refAttrib, newName);
        return(true);
      }
    }
    return(false);
  }
  
  private void changeRefAttributeValue(Attr refAttrib, String newName) {
    String value = refAttrib.getValue();
    int indexFromNameDelIndex = value.indexOf(":");
    refAttrib.setValue(indexFromNameDelIndex > 0 ? value.substring(0, indexFromNameDelIndex + 1) + newName : newName);
  }
  
  private void removeTopLevelComponent(String quilifiedBaseName, String componentId) {
    Hashtable storage = (Hashtable)(idToTopLevelComponentsMapping.get(componentId));
    storage.remove(quilifiedBaseName);
  }
  
  private String generateRandomName(String realName) {
    return(realName + System.currentTimeMillis());
  }
  
  public Vector getImportedSchemas() {
  	return(importedSchemasCollector);
  }
  
  public Vector getIncludedSchemas() {
  	return(includedSchemasCollector);
  }
  
  public LoaderImpl getLoader() {
  	return(loader);
  } 
  
  public boolean isElemsFormDefaultQualified() {
  	return(elemsFormDefaultIsQualified);
  }
  
	public boolean isAttribsFormDefaultQualified() {
  	return(attribsFormDefaultIsQualified);
  }
  
	public String getTargetNamespace() {
  	return(targetNamespace);
  }
  
	public void setTargetNamespace(String targetNamespace) {
		setTargetNamespace(targetNamespace, new Vector());
  }
  
  private void setTargetNamespace(String targetNamespace, Vector processedSchemasCollector) {
  	if(!processedSchemasCollector.contains(this)) {
			processedSchemasCollector.add(this);
			this.targetNamespace = targetNamespace;
			for(int i = 0; i < includedSchemasCollector.size(); i++) {
				SchemaImpl includedSchema = (SchemaImpl)(includedSchemasCollector.get(i));
				includedSchema.setTargetNamespace(targetNamespace, processedSchemasCollector);
			}
  	}
  }
  
	public boolean isFinalExtension() {
  	return(isFinalExtension);
  }
  
	public boolean isFinalRestriction() {
  	return(isFinalRestriction);
  }
  
	public boolean isProhibitedSubstitution() {
  	return(isProhibitedSubstitution);
  }
  
	public boolean isProhibitedExtension() {
  	return(isProhibitedExtension);
  }
  
	public boolean isProhibitedRestriction() {
  	return(isProhibitedRestriction);
  }
  
	public boolean isProhibitedList() {
  	return(isProhibitedList);
  }
  
	public boolean isProhibitedUnion() {
  	return(isProhibitedUnion);
  }
  
  public String getLocation() {
  	return(location);
  }
  
  public void addComponent(BaseImpl base) {
  	allComponentsCollector.add(base);
  }

////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
  // GET TOP LEVEL COMPONENTS IMPLEMENTATION
  
	public void getTopLevelComponents(Vector collector) {
		referenceableSchemas.add(this);
    getTopLevelComponents(collector, referenceableSchemas);
    referenceableSchemas.clear();
	}
  
  private void getTopLevelComponents(Vector collector, Vector processedSchemasCollector) {
    getOwnTopLevelComponents(collector);
    getReferenceableTopLevelComponents(collector, processedSchemasCollector, importedSchemasCollector);
    getReferenceableTopLevelComponents(collector, processedSchemasCollector, includedSchemasCollector);
  }
  
	private void getOwnTopLevelComponents(Vector collector) {
		addAllFromHashtableToVector(collector, (Hashtable)(idToTopLevelComponentsMapping.get(TYPE_DEFINITION_ID)));
		addAllFromHashtableToVector(collector, (Hashtable)(idToTopLevelComponentsMapping.get(ELEMENT_DECLARATION_ID)));
		addAllFromHashtableToVector(collector, (Hashtable)(idToTopLevelComponentsMapping.get(ATTRIBUTE_DECLARATION_ID)));
		addAllFromHashtableToVector(collector, (Hashtable)(idToTopLevelComponentsMapping.get(ATTRIBUTE_GROUP_DEFINITION_ID)));
		addAllFromHashtableToVector(collector, (Hashtable)(idToTopLevelComponentsMapping.get(MODEL_GROUP_DEFINITION_ID)));
		addAllFromHashtableToVector(collector, (Hashtable)(idToTopLevelComponentsMapping.get(IDENTITY_CONSTRAINT_DEFINITION_ID)));
		addAllFromHashtableToVector(collector, (Hashtable)(idToTopLevelComponentsMapping.get(NOTATION_DECLARATION_ID)));
	}
	
	private void addAllFromHashtableToVector(Vector vctrCollector, Hashtable hashCollector) {
		Enumeration enum1 = hashCollector.elements();
		while(enum1.hasMoreElements()) {
			vctrCollector.add(enum1.nextElement());
		}
	}
  
  private void getReferenceableTopLevelComponents(Vector collector, Vector processedSchemasCollector, Vector referenceableSchemasCollector) {
    for(int i = 0; i < referenceableSchemasCollector.size(); i++) {
      SchemaImpl referenceableSchema = (SchemaImpl)(referenceableSchemasCollector.get(i));
      getReferenceableTopLevelComponents(collector, processedSchemasCollector, referenceableSchema);
    }
  }
  
  private void getReferenceableTopLevelComponents(Vector collector, Vector processedSchemasCollector, SchemaImpl referenceableSchema) {
      if(!processedSchemasCollector.contains(referenceableSchema)) {
        processedSchemasCollector.add(referenceableSchema);
        referenceableSchema.getTopLevelComponents(collector, processedSchemasCollector);
      }
    }
  
//////////////////////////////////////////////////////////////////////////////////////////////////////////

  //GET SPECIFIED TOP LEVEL COMPONENTES
  
	public void getTopLevelComponents(Vector collector, String componentsId) {
    referenceableSchemas.add(this);
    getTopLevelComponents(collector, componentsId, referenceableSchemas);
    referenceableSchemas.clear();
	}
  
  private void getTopLevelComponents(Vector collector, String componentsId, Vector processedSchemasCollector) {
    getOwnTopLevelComponents(collector, componentsId);
    getReferenceableTopLevelComponents(collector, componentsId, processedSchemasCollector, importedSchemasCollector);
    getReferenceableTopLevelComponents(collector, componentsId, processedSchemasCollector, includedSchemasCollector);
  }

	private void getOwnTopLevelComponents(Vector collector, String componentsId) {
		addAllFromHashtableToVector(collector, (Hashtable)(idToTopLevelComponentsMapping.get(componentsId)));
	}
  
  private void getReferenceableTopLevelComponents(Vector collector, String componentsId, Vector processedSchemasCollector, Vector referenceableSchemasCollector) {
    for(int i = 0; i < referenceableSchemasCollector.size(); i++) {
      SchemaImpl referenceableSchema = (SchemaImpl)(referenceableSchemasCollector.get(i));
      getReferenceableTopLevelComponents(collector, componentsId, processedSchemasCollector, referenceableSchema);
    }
  }
  
  private void getReferenceableTopLevelComponents(Vector collector, String componentsId, Vector processedSchemasCollector, SchemaImpl referenceableSchema) {
      if(!processedSchemasCollector.contains(referenceableSchema)) {
        processedSchemasCollector.add(referenceableSchema);
        referenceableSchema.getTopLevelComponents(collector, componentsId, processedSchemasCollector);
      }
    }
  
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  

  public void getIdentityConstraintDefinitions(Vector collector) {
		getTopLevelComponents(collector, IDENTITY_CONSTRAINT_DEFINITION_ID);
  }

  public void getTopLevelTypeDefinitions(Vector collector) {
		getTopLevelComponents(collector, TYPE_DEFINITION_ID);
  }

  public void getTopLevelAttributeDeclarations(Vector collector) {
		getTopLevelComponents(collector, ATTRIBUTE_DECLARATION_ID);
  }

  public void getTopLevelElementDeclarations(Vector collector) {
		getTopLevelComponents(collector, ELEMENT_DECLARATION_ID);
  }

  public void getTopLevelAttributeGroupDefinitions(Vector collector) {
		getTopLevelComponents(collector, ATTRIBUTE_GROUP_DEFINITION_ID);
  }

  public void getTopLevelModelGroupDefinitions(Vector collector) {
		getTopLevelComponents(collector, MODEL_GROUP_DEFINITION_ID);
  }

  public void getTopLevelNotationDeclarations(Vector collector) {
		getTopLevelComponents(collector, NOTATION_DECLARATION_ID);
  }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public IdentityConstraintDefinition[] getIdentityConstraintDefinitionsArray() {
		Vector collector = new Vector();
		getTopLevelComponents(collector, IDENTITY_CONSTRAINT_DEFINITION_ID);
		IdentityConstraintDefinition[] components = new IdentityConstraintDefinition[collector.size()];
		collector.copyInto(components);
    return(components);
  }

  public TypeDefinitionBase[] getTopLevelTypeDefinitionsArray() {
		Vector collector = new Vector();
		getTopLevelComponents(collector, TYPE_DEFINITION_ID);
		TypeDefinitionBase[] components = new TypeDefinitionBase[collector.size()];
		collector.copyInto(components);
		return(components);
  }

  public AttributeDeclaration[] getTopLevelAttributeDeclarationsArray() {
		Vector collector = new Vector();
		getTopLevelComponents(collector, ATTRIBUTE_DECLARATION_ID);
		AttributeDeclaration[] components = new AttributeDeclaration[collector.size()];
		collector.copyInto(components);
		return(components);
  }

  public ElementDeclaration[] getTopLevelElementDeclarationsArray() {
		Vector collector = new Vector();
		getTopLevelComponents(collector, ELEMENT_DECLARATION_ID);
		ElementDeclaration[] components = new ElementDeclaration[collector.size()];
		collector.copyInto(components);
		return(components);
  }

  public AttributeGroupDefinition[] getTopLevelAttributeGroupDefinitionsArray() {
		Vector collector = new Vector();
		getTopLevelComponents(collector, ATTRIBUTE_GROUP_DEFINITION_ID);
		AttributeGroupDefinition[] components = new AttributeGroupDefinition[collector.size()];
		collector.copyInto(components);
		return(components);
  }

  public ModelGroupDefinition[] getTopLevelModelGroupDefinitionsArray() {
		Vector collector = new Vector();
		getTopLevelComponents(collector, MODEL_GROUP_DEFINITION_ID);
		ModelGroupDefinition[] components = new ModelGroupDefinition[collector.size()];
		collector.copyInto(components);
		return(components);
  }

  public NotationDeclaration[] getTopLevelNotationDeclarationsArray() {
		Vector collector = new Vector();
		getTopLevelComponents(collector, NOTATION_DECLARATION_ID);
		NotationDeclaration[] components = new NotationDeclaration[collector.size()];
		collector.copyInto(components);
		return(components);
  }
  
  public QualifiedBase[] getTopLevelComponentsArray() {
		Vector collector = new Vector();
		getTopLevelComponents(collector);
		QualifiedBase[] components = new QualifiedBase[collector.size()];
		collector.copyInto(components);
		return(components);
  }

//////////////////////////////////////////////////////////////////////////////////////////////////////////

  public Base[] getAllComponentsAsArray() {
  	Vector collector = new Vector();
		getAllComponents(collector);
    Base[] result = new Base[collector.size()];
		collector.copyInto(result);
    return(result);
  }

///////////////////////////////////////////////////////////////////////////////////////////////////
  
  //GET ALL COMPONENTS IMPLEMENTATION
  
  public void getAllComponents(Vector collector) {
    referenceableSchemas.add(this);
    getAllComponents(collector, referenceableSchemas);
    referenceableSchemas.clear();
  }

  private void getAllComponents(Vector collector, Vector processedSchemasCollector) {
    getAllOwnComponents(collector);
    getAllReferenceableComponents(collector, processedSchemasCollector, importedSchemasCollector);
    getAllReferenceableComponents(collector, processedSchemasCollector, includedSchemasCollector);
  }

  private void getAllOwnComponents(Vector collector) {
    collector.addAll(allComponentsCollector);
  }
  
  private void getAllReferenceableComponents(Vector collector, Vector processedSchemasCollector, Vector referenceableSchemasCollector) {
    for(int i = 0; i < referenceableSchemasCollector.size(); i++) {
      SchemaImpl referenceableSchema = (SchemaImpl)(referenceableSchemasCollector.get(i));
      getAllReferenceableComponents(collector, processedSchemasCollector, referenceableSchema);
    }
  }
  
  private void getAllReferenceableComponents(Vector collector, Vector processedSchemasCollector, SchemaImpl referenceableSchema) {
      if(!processedSchemasCollector.contains(referenceableSchema)) {
        processedSchemasCollector.add(referenceableSchema);
        referenceableSchema.getAllComponents(collector, processedSchemasCollector);
      }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////

  public int getTypeOfComponent() {
    return(C_SCHEMA);
  }

//////////////////////////////////////////////////////////////////////////////////////////////////////////

  public QualifiedBase getTopLevelComponent(String uri, String name, int type) {
    switch (type) {
      case C_ATTRIBUTE_DECLARATION: {
        return(getTopLevelAttributeDeclaration(uri, name));
      }
      case C_ATTRIBUTE_GROUP_DEFINITION: {
        return(getTopLevelAttributeGroupDefinition(uri, name));
      }
      case C_ELEMENT_DECLARATION: {
        return(getTopLevelElementDeclaration(uri, name));
      }
      case C_MODEL_GROUP_DEFINITION: {
        return(getTopLevelModelGroupDefinition(uri, name));
      }
      case C_NOTATION_DECLARATION: {
        return(getTopLevelNotationDeclaration(uri, name));
      }
      case C_TYPE_DEFINITION: {
      	return(getTopLevelTypeDefinition(uri, name));
      }
      case C_COMPLEX_TYPE_DEFINITION: {
      	return(getTopLevelTypeDefinition(uri, name));
      }
      case C_SIMPLE_TYPE_DEFINITION: {
      	return(getTopLevelTypeDefinition(uri, name));
      }
      case C_IDENTITY_CONSTRAINT_DEFINITION: {
      	return(getIdentityConstraintDefinition(uri, name));
      }
      default: {
        throw new IllegalArgumentException("Type with id " + type + " is unknown.");
      }
    }
  }

////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  //GET TOP LEVEL COMPONENT IMPLEMENTATION
  
	public QualifiedBase getTopLevelComponent(String uri, String name, String componentsId) {
		QualifiedBase qBase = null;
		if(uri.equals(SCHEMA_COMPONENTS_NS) && componentsId.equals(TYPE_DEFINITION_ID)) {
			qBase = getBuiltInTypeDefinition(name);
    }
		if(qBase == null) {
			qBase = getSpecifiedTopLevelComponent(uri, name, componentsId);
    }
		return(qBase);
  }
	
	private QualifiedBase getSpecifiedTopLevelComponent(String uri, String name, String componentsId) {
    referenceableSchemas.add(this);
    QualifiedBase qBase = getSpecifiedTopLevelComponent(uri, name, componentsId, referenceableSchemas);
    referenceableSchemas.clear();
    return(qBase);
  }
  
  private QualifiedBase getSpecifiedTopLevelComponent(String uri, String name, String componentsId, Vector processedSchemasCollector) {
    QualifiedBase qBase = null;
    if(uri.equals(targetNamespace)) {
      qBase = getOwnTopLevelComponent(name, componentsId);
    }
    if(qBase == null) {
      qBase = getReferenceableTopLevelComponent(uri, name, componentsId, processedSchemasCollector, includedSchemasCollector);
      if(qBase == null) {
        qBase = getReferenceableTopLevelComponent(uri, name, componentsId, processedSchemasCollector, importedSchemasCollector);
      }
    }
    return(qBase);
  }
	
	private QualifiedBase getBuiltInTypeDefinition(String typeName) {
		return((TypeDefinitionBase)(builtInTypesCollector.get(typeName)));
  }

	private QualifiedBase getOwnTopLevelComponent(String name, String componentsId) {
		Hashtable nameToTopLevelComponentsMapping = (Hashtable)(idToTopLevelComponentsMapping.get(componentsId));
		return((QualifiedBase)(nameToTopLevelComponentsMapping.get(name)));
	}
  
  private QualifiedBase getReferenceableTopLevelComponent(String uri, String name, String componentsId, Vector processedSchemasCollector, Vector referenceableSchemasCollector) {
    for(int i = 0; i < referenceableSchemasCollector.size(); i++) {
      SchemaImpl referenceableSchema = (SchemaImpl)(referenceableSchemasCollector.get(i));
      if(!processedSchemasCollector.contains(referenceableSchema)) {
        processedSchemasCollector.add(referenceableSchema);
        QualifiedBase qBase = referenceableSchema.getSpecifiedTopLevelComponent(uri, name, componentsId, processedSchemasCollector);
        if(qBase != null) {
          return(qBase);
        }
      }
    }
    return(null);
  }
  
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public IdentityConstraintDefinition getIdentityConstraintDefinition(String uri, String name) {
    return((IdentityConstraintDefinition)(getTopLevelComponent(uri, name, IDENTITY_CONSTRAINT_DEFINITION_ID)));
  }

  public TypeDefinitionBase getTopLevelTypeDefinition(String uri, String name) {
    return((TypeDefinitionBase)(getTopLevelComponent(uri, name, TYPE_DEFINITION_ID)));
  }
  
  public AttributeDeclaration getTopLevelAttributeDeclaration(String uri, String name) {
		return((AttributeDeclaration)(getTopLevelComponent(uri, name, ATTRIBUTE_DECLARATION_ID)));
  }

  public ElementDeclaration getTopLevelElementDeclaration(String uri, String name) {
		return((ElementDeclaration)(getTopLevelComponent(uri, name, ELEMENT_DECLARATION_ID)));
  }

  public AttributeGroupDefinition getTopLevelAttributeGroupDefinition(String uri, String name) {
		return((AttributeGroupDefinition)(getTopLevelComponent(uri, name, ATTRIBUTE_GROUP_DEFINITION_ID)));
  }
  
  public ModelGroupDefinition getTopLevelModelGroupDefinition(String uri, String name) {
		return((ModelGroupDefinition)(getTopLevelComponent(uri, name, MODEL_GROUP_DEFINITION_ID)));
  }
  
  public NotationDeclaration getTopLevelNotationDeclaration(String uri, String name) {
		return((NotationDeclaration)(getTopLevelComponent(uri, name, NOTATION_DECLARATION_ID)));
  }
  
///////////////////////////////////////////////////////////////////////////////////////////////////////////  
  
  private void addAttributeDeclaration(AttributeDeclarationImpl attributeDeclaration) throws SchemaComponentException {
  	addTopLevelComponent(attributeDeclaration, ATTRIBUTE_DECLARATION_ID);
  }
  
  private void addAttributeGroupDefinition(AttributeGroupDefinitionImpl attributeGroupDefinition) throws SchemaComponentException {
		addTopLevelComponent(attributeGroupDefinition, ATTRIBUTE_GROUP_DEFINITION_ID);
  }
  
	private void addElementDeclaration(ElementDeclarationImpl elementDeclaration) throws SchemaComponentException {
		addTopLevelComponent(elementDeclaration, ELEMENT_DECLARATION_ID);
  }
  
	private void addModelGroupDefinition(ModelGroupDefinitionImpl modelGroupDefinition) throws SchemaComponentException {
		addTopLevelComponent(modelGroupDefinition, MODEL_GROUP_DEFINITION_ID);
	}
	
	private void addNotationDeclaration(NotationDeclarationImpl notationDeclaration) throws SchemaComponentException {
		addTopLevelComponent(notationDeclaration, NOTATION_DECLARATION_ID);
	}
	
	private void addComplexTypeDefinition(ComplexTypeDefinitionImpl complexTypeDefinition) throws SchemaComponentException {
		addTopLevelComponent(complexTypeDefinition, TYPE_DEFINITION_ID);
	}
	
	private void addSimpleTypeDefinition(SimpleTypeDefinitionImpl simpleTypeDefinition) throws SchemaComponentException {
		addTopLevelComponent(simpleTypeDefinition, TYPE_DEFINITION_ID);
	}
	
	protected void addIdentityConstraintDefinition(IdentityConstraintDefinitionImpl identConstrDefinition) throws SchemaComponentException {
		addTopLevelComponent(identConstrDefinition, IDENTITY_CONSTRAINT_DEFINITION_ID);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
  public boolean match(Base schemaImpl) {
  		return(false);
  	}

  public void load() throws SchemaComponentException {
    super.load();
    importNamespaces();
    loadImportedSchemas();
    loadIncludedSchemas();
		loadTopLevelComponents(TYPE_DEFINITION_ID);
    loadTopLevelComponents(ELEMENT_DECLARATION_ID);
		loadTopLevelComponents(ATTRIBUTE_DECLARATION_ID);
		loadTopLevelComponents(ATTRIBUTE_GROUP_DEFINITION_ID);
		loadTopLevelComponents(MODEL_GROUP_DEFINITION_ID);
		loadTopLevelComponents(NOTATION_DECLARATION_ID);
		loadTopLevelComponents(IDENTITY_CONSTRAINT_DEFINITION_ID);
    preloadComplexTypeDefinitions();
  }
  
  private void preloadComplexTypeDefinitions() throws SchemaComponentException {
    for(int i = 0; i < allComplexTypeDefinitions.size(); i++) {
      ComplexTypeDefinitionImpl complTypeDef = (ComplexTypeDefinitionImpl)(allComplexTypeDefinitions.get(i));
      SchemaStructuresLoader.preloadComplexType(complTypeDef);
    }
  }
  
  private void importNamespaces() throws SchemaComponentException {
  	for(int i = 0; i < importedNamespacesCollector.size(); i++) {
  		String namespace = (String)(importedNamespacesCollector.get(i));
  		importNamespace(namespace, false);
  	}
  }
  
  public void importNamespace(String namespace, boolean runtimeLoadingImport) throws SchemaComponentException {
		SchemaImpl[] schemas = runtimeLoadingImport ? loader.produceSchemas_Namespace_UriResolver(namespace) : loader.produceSchemas_Namespace(namespace);
    if(schemas != null) {
  		for(int j = 0; j < schemas.length; j++) {
  			SchemaImpl importedSchema = schemas[j];
  			importSchema(namespace, importedSchema, runtimeLoadingImport);
  		}
    }
  } 
  
  public void includeNamespace() throws SchemaComponentException {
  	includeSchemas(loader.produceSchemas_Namespace(targetNamespace), true);
  }
  
  private void loadImportedSchemas() throws SchemaComponentException {
		for(int i = 0; i < importedSchemasCollector.size(); i++) {
			SchemaImpl importedSchema = (SchemaImpl)(importedSchemasCollector.get(i));
      loadImportedSchema(importedSchema);
		}
  }
  
  private void loadImportedSchema(SchemaImpl importedSchema) throws SchemaComponentException {
			SchemaStructuresLoader.loadBase(importedSchema);
		}

  private void loadIncludedSchemas() throws SchemaComponentException {
  	for(int i = 0; i < includedSchemasCollector.size(); i++) {
  		SchemaImpl includedSchema = (SchemaImpl)(includedSchemasCollector.get(i));
      loadIncludedSchema(includedSchema);
  	}
  }
  
  private void loadIncludedSchema(SchemaImpl includedSchema) throws SchemaComponentException {
			SchemaStructuresLoader.loadBase(includedSchema);
			if(!includedSchema.getTargetNamespace().equals(targetNamespace)) {
				includedSchema.setTargetNamespace(targetNamespace);
			}
  	}
  
  private void loadTopLevelComponents(String componentsId) throws SchemaComponentException {
  	Hashtable storage = (Hashtable)(idToTopLevelComponentsMapping.get(componentsId));
  	Enumeration enum1 = storage.elements();
  	while(enum1.hasMoreElements()) {
      loadTopLevelComponent(componentsId, (QualifiedBaseImpl)(enum1.nextElement()));
    }
  }
  
  private void loadTopLevelComponent(String componentId, QualifiedBaseImpl topLevelComponent) throws SchemaComponentException {
    SchemaStructuresLoader.loadBase(topLevelComponent);
    validateForMultipleTopLevelComponents(topLevelComponent, componentId, importedSchemasCollector);
    validateForMultipleTopLevelComponents(topLevelComponent, componentId, includedSchemasCollector);
  }
  
  private void validateForMultipleTopLevelComponents(QualifiedBaseImpl ownTopLevelComponent, String componentId, Vector referenceableSchemas) throws SchemaComponentException {
    for(int i = 0; i < referenceableSchemas.size(); i++) {
      SchemaImpl referenceableSchema = (SchemaImpl)(referenceableSchemas.get(i));
      validateForMultipleTopLevelComponents(ownTopLevelComponent, componentId, referenceableSchema);
    }
  }
  
  private void validateForMultipleTopLevelComponents(QualifiedBaseImpl ownTopLevelComponent, String componentId, SchemaImpl referenceableSchema) throws SchemaComponentException {
    QualifiedBaseImpl referenceableTopLevelComponent = (QualifiedBaseImpl)(referenceableSchema.getTopLevelComponent(ownTopLevelComponent.getTargetNamespace(), ownTopLevelComponent.getName(), componentId));
    validateForMultipleTopLevelComponents(ownTopLevelComponent, referenceableTopLevelComponent);
  }
  
  private void validateForMultipleTopLevelComponents(QualifiedBaseImpl ownTopLevelComponent, QualifiedBaseImpl referenceableTopLevelComponent) throws SchemaComponentException {
    if(referenceableTopLevelComponent != null && ownTopLevelComponent != referenceableTopLevelComponent) {
      throw new SchemaComponentException("[location : '" + location + "'] ERROR : Top level component " + ownTopLevelComponent.toString() + " is already defined by the schema '" + referenceableTopLevelComponent.getOwnerSchema().getLocation() + "'.");
    }
  }

  private void addTopLevelComponent(QualifiedBaseImpl qualifiedBase, String componentId) throws SchemaComponentException  {
  	if(!qualifiedBase.getTargetNamespace().equals(SCHEMA_COMPONENTS_NS) || !componentId.equals(TYPE_DEFINITION_ID) || !builtInTypesCollector.containsKey(qualifiedBase.getName())) {
			Hashtable storage = (Hashtable)(idToTopLevelComponentsMapping.get(componentId));
			if(!(qualifiedBase instanceof RedefineableQualifiedBaseImpl && ((RedefineableQualifiedBaseImpl)qualifiedBase).isRedefined()) && storage.containsKey(qualifiedBase.getName())) {
				throw new SchemaComponentException("[location : '" + location + "'] ERROR : Top level component " + qualifiedBase.toString() + " is already defined.");
			}
			storage.put(qualifiedBase.getName(), qualifiedBase);
		}
  }

  protected BaseImpl initializeBase(BaseImpl base, Hashtable clonedCollector) {
    return(null);
  }

  public static TypeDefinitionBase getBuiltInTypeDefnition(String typeName) {
    return((TypeDefinitionBase)(builtInTypesCollector.get(typeName)));
  }

  public void getBuiltInTypeDefinitions(Vector collector) {
    Enumeration enum1 = builtInTypesCollector.elements();
    while(enum1.hasMoreElements()) {
      collector.add(enum1.nextElement());
    }
  }

  public TypeDefinitionBase[] getBuiltInTypeDefinitions() {
    Vector collector = new Vector();
    getBuiltInTypeDefinitions(collector);
    TypeDefinitionBase[] result = new TypeDefinitionBase[collector.size()];
    collector.copyInto(result);
    return(result);
  }
  
  protected void addComplexTypeDefinition_All(ComplexTypeDefinition complTypeDef) {
    allComplexTypeDefinitions.add(complTypeDef);
  }
  
  protected Vector getAllComplexTypeDefinition() {
    return(allComplexTypeDefinitions);
  }
}

