package com.sap.engine.lib.schema.components.impl.structures;

import java.util.*;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.engine.lib.schema.components.*;
import com.sap.engine.lib.schema.components.impl.LoaderImpl;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.exception.SchemaComponentException;

//TODO remove comments

public final class SchemaImpl_Namespaces {
//
//public final class SchemaImpl extends BaseImpl implements Schema {
//
//	protected static BuiltInTypesCollector builtInTypesCollector;
//
//	private Vector allComponentsCollector;
//	private LoaderImpl loader;
//	private boolean elemsFormDefaultIsQualified;
//	private boolean attribsFormDefaultIsQualified;
//	private String targetNamespace;
//	private boolean isFinalExtension;
//	private boolean isFinalRestriction;
//	private boolean isProhibitedSubstitution;
//	private boolean isProhibitedExtension;
//	private boolean isProhibitedRestriction;
//	private boolean isProhibitedList;
//	private boolean isProhibitedUnion;
//	private String location;
//	private Hashtable idToTopLevelComponentsMapping;
//	private Hashtable nsToImportedSchemasMapping;
//	private Vector includedSchemasCollector;
//	private Element schemaElement;
//	private Vector importedNamespacesCollector;
//	private String includingNamespace;
//
//  static {
//    if(builtInTypesCollector == null) {
//      builtInTypesCollector = new BuiltInTypesCollector();
//    }
//  }
//
//  public SchemaImpl(SchemaImpl[] schemas) {
//  	init();
//		augmentWithSchemas(schemas);
//  }
//  
//	public SchemaImpl(SchemaImpl[][] schemas) {
//		init();
//		for(int i = 0; i < schemas.length; i++) {
//			augmentWithSchemas(schemas[i]);
//		}
//	}
//	
//	private void augmentWithSchemas(SchemaImpl[] schemas) {
//		for(int i = 0; i < schemas.length; i++) {
//			augmentWithSchema(schemas[i]);
//		}
//	}
//	
//	private void augmentWithSchema(SchemaImpl schema) {
//		try {
//			if(schema != this) {
//				if(schema.getTargetNamespace().equals(targetNamespace)) {
//					includeSchema(schema, false);
//				} else {
//					importSchema(schema.getTargetNamespace(), schema, false);
//				}
//			}
//		} catch(SchemaComponentException schemaCompExc) {
//		}
//	}
//  
//  public SchemaImpl(Element schemaElement, LoaderImpl loader, String location, String includingNamespace) throws SchemaComponentException {
//  	init();
//  	this.loader = loader;
//  	this.location = location;
//  	this.schemaElement = schemaElement;
//  	this.includingNamespace = includingNamespace;
//  	initSchemaInfo();
//  }
//  
//  private void init() {
//		initIdToNSQBasesStorageMapping();
//    allComponentsCollector = new Vector();
//		nsToImportedSchemasMapping = new Hashtable();
//		includedSchemasCollector = new Vector();
//		importedNamespacesCollector = new Vector();
//		targetNamespace = "";
//		isLoaded = false;
//  }
//
//	private void initIdToNSQBasesStorageMapping() {
//		idToTopLevelComponentsMapping = new Hashtable();
//		idToTopLevelComponentsMapping.put(TYPE_DEFINITION_ID, new Hashtable());
//		idToTopLevelComponentsMapping.put(ELEMENT_DECLARATION_ID, new Hashtable());
//		idToTopLevelComponentsMapping.put(ATTRIBUTE_DECLARATION_ID, new Hashtable());
//		idToTopLevelComponentsMapping.put(ATTRIBUTE_GROUP_DEFINITION_ID, new Hashtable());
//		idToTopLevelComponentsMapping.put(MODEL_GROUP_DEFINITION_ID, new Hashtable());
//		idToTopLevelComponentsMapping.put(IDENTITY_CONSTRAINT_DEFINITION_ID, new Hashtable());
//		idToTopLevelComponentsMapping.put(NOTATION_DECLARATION_ID, new Hashtable());
//	}
//  
//  private void initSchemaInfo() throws SchemaComponentException {
//		NamedNodeMap namedNodeMap = schemaElement.getAttributes();
//		for(int i = 0; i < namedNodeMap.getLength(); i++) {
//			Node attrib = namedNodeMap.item(i);
//			String attribUri = attrib.getNamespaceURI();
//			String localName = attrib.getLocalName();
//			String attribQName = attrib.getNodeName();
//			String attribValue = attrib.getNodeValue();
//			if(attribUri == null || attribUri.equals("")) {
//				if(localName.equals(NODE_ATTRIB_FORM_DEFAULT_NAME)) {
//					attribsFormDefaultIsQualified = attribValue.equals(VALUE_QUALIFIED_NAME);
//				} else if(localName.equals(NODE_ELEM_FORM_DEFAULT_NAME)) {
//					elemsFormDefaultIsQualified = attribValue.equals(VALUE_QUALIFIED_NAME);
//				} else if(localName.equals(NODE_TARGET_NAMESPACE_NAME)) {
//					targetNamespace = (attribValue == null) ? "" : attribValue;
//				} else if(localName.equals(NODE_BLOCK_DEFAULT_NAME)) {
//					if(attribValue.equals(VALUE_ALL_NAME)) {
//						isProhibitedSubstitution = true;
//						isProhibitedExtension = true;
//						isProhibitedRestriction = true;
//						isProhibitedList = true;
//						isProhibitedUnion = true;
//					} else {
//						StringTokenizer tokenizer = new StringTokenizer(attribValue);
//						while(tokenizer.hasMoreTokens()) {
//							attribValue = tokenizer.nextToken();
//							if(attribValue.equals(VALUE_SUBSTITUTION_NAME)) {
//								isProhibitedSubstitution = true;
//							} else if(attribValue.equals(VALUE_EXTENSION_NAME)) {
//								isProhibitedExtension = true;
//							} else if(attribValue.equals(VALUE_RESTRICTION_NAME)) {
//								isProhibitedRestriction = true;
//							} else if(attribValue.equals(VALUE_LIST_NAME)) {
//								isProhibitedList = true;
//							} else if(attribValue.equals(VALUE_UNION_NAME)) {
//								isProhibitedUnion = true;
//							}
//						}
//					}
//				} else if(localName.equals(NODE_FINAL_DEFAULT_NAME)) {
//					if(attribValue.equals(VALUE_ALL_NAME)) {
//						isFinalExtension = true;
//						isFinalRestriction = true;
//					} else {
//						StringTokenizer tokenizer = new StringTokenizer(attribValue);
//						while(tokenizer.hasMoreTokens()) {
//							attribValue = tokenizer.nextToken();
//							if(attribValue.equals(VALUE_EXTENSION_NAME)) {
//								isFinalExtension = true;
//							} else if(attribValue.equals(VALUE_RESTRICTION_NAME)) {
//								isFinalRestriction = true;
//							}
//						}
//					}
//				}
//			}
//		}
//  }
//  
//  public void collectComponents() throws SchemaComponentException {
//		NodeList nodeList = schemaElement.getChildNodes();
//		for(int i = 0; i < nodeList.getLength(); i++) {
//			Node node = nodeList.item(i);
//			if(node instanceof Element) {
//				String nodeUri = node.getNamespaceURI();
//				String nodeLocalName = node.getLocalName();
//				if(nodeUri != null && nodeUri.equals(SCHEMA_COMPONENTS_NS)) {
//					if(nodeLocalName.equals(NODE_IMPORT_NAME)) {
//						importSchemas((Element)node);
//					} else if(nodeLocalName.equals(NODE_INCLUDE_NAME)) {
//						includeSchemas((Element)node, false);
//					} else if(nodeLocalName.equals(NODE_REDEFINE_NAME)) {
//						includeSchemas((Element)node, false);
//					} else if(nodeLocalName.equals(NODE_SIMPLE_TYPE_NAME)) {
//						addSimpleTypeDefinition(SchemaStructuresLoader.createSimpleTypeDefinition(node, this, true));
//					} else if(nodeLocalName.equals(NODE_COMPLEX_TYPE_NAME)) {
//						addComplexTypeDefinition(SchemaStructuresLoader.createComplexTypeDefinition(node, this, true));
//					} else if(nodeLocalName.equals(NODE_GROUP_NAME)) {
//						addModelGroupDefinition(SchemaStructuresLoader.createModelGroupDefinition(node, this, true));
//					} else if(nodeLocalName.equals(NODE_ATTRIBUTE_GROUP_NAME)) {
//						addAttributeGroupDefinition(SchemaStructuresLoader.createAttributeGroupDefinition(node, this, true));
//					} else if(nodeLocalName.equals(NODE_ELEMENT_NAME)) {
//						addElementDeclaration(SchemaStructuresLoader.createElementDeclaration(node, this, true));
//					} else if(nodeLocalName.equals(NODE_ATTRIBUTE_NAME)) {
//						addAttributeDeclaration(SchemaStructuresLoader.createAttributeDeclaration(node, this, true));
//					} else if(nodeLocalName.equals(NODE_NOTATION_NAME)) {
//						addNotationDeclaration(SchemaStructuresLoader.createNotationDeclaration(node, this));
//					}
//				}
//			}
//		}
//  }
//  
//  private void importSchemas(Element importElement) throws SchemaComponentException {
//		Attr targetNsAttrib = importElement.getAttributeNode(NODE_IMPORT_NAMESPACE_NAME);
//		String importedNamespace = targetNsAttrib == null ? "" : targetNsAttrib.getValue();
//		Attr locationAttrib = importElement.getAttributeNode(NODE_SCHEMA_LOCATION_NAME);
//		String importedLocation = locationAttrib == null ? null : locationAttrib.getValue();
//		if(importedLocation == null) {
//			importedNamespacesCollector.add(importedNamespace);
//		} else {
//      SchemaImpl[] importedSchemas = loader.produceSchemas_Location(location, importedLocation, null, null);
//			for(int i = 0; i < importedSchemas.length; i++) {
//				importSchema(importedNamespace, importedSchemas[i], false);
//			} 
//		}
//  }
//  
//  private void importSchema(String importedNamespace, SchemaImpl importedSchema, boolean runtimeLoadingImport) throws SchemaComponentException {
//  	String namespace = importedSchema.getTargetNamespace();
//  	if(!namespace.equals(importedNamespace) || importedNamespace.equals(targetNamespace)) {
//			throw new SchemaComponentException("[location : '" + location + "'] ERROR : Can not import '" + importedSchema.getLocation() + "' schema document. The imported schema document must not have the targetNamespace as the <import>ing schema document, or it's targetNamespace must be with value '" + importedNamespace + "'.");  	
//		}
//  	Vector importedSchemasCollector = (Vector)(nsToImportedSchemasMapping.get(namespace));
//  	if(importedSchemasCollector == null) {
//			importedSchemasCollector = new Vector();
//			nsToImportedSchemasMapping.put(namespace, importedSchemasCollector);
//  	}
//		importedSchemasCollector.add(importedSchema);
//    if(runtimeLoadingImport) {
//      SchemaStructuresLoader.loadBase((BaseImpl)importedSchema);
//    }
//  }
//  
//  private void includeSchemas(Element includeElement, boolean runtimeLoadingInclude) throws SchemaComponentException {
//		Attr locationAttrib = includeElement.getAttributeNode(NODE_SCHEMA_LOCATION_NAME);
//		String includedLocation = locationAttrib == null ? null : locationAttrib.getValue();
//		if(includedLocation != null) {
//			includeSchemas(loader.produceSchemas_Location(location, includedLocation, includingNamespace == null ? targetNamespace : includingNamespace, null), runtimeLoadingInclude);
//		}
//  }
//  
//  private void includeSchemas(Schema[] includedSchemas, boolean runtimeLoadingInclude) throws SchemaComponentException {
//		for(int j = 0; j < includedSchemas.length; j++) {
//			includeSchema(includedSchemas[j], runtimeLoadingInclude);
//		} 
//  }
//  
//  private void includeSchema(SchemaImpl includedSchema, boolean runtimeLoadingInclude) throws SchemaComponentException {
//  	if(includedSchema != this && !includedSchemasCollector.contains(includedSchema)) {
//	  	if(!includedSchema.getTargetNamespace().equals("") && !includedSchema.getTargetNamespace().equals(targetNamespace)) {
//	  		throw new SchemaComponentException("[location : '" + location + "'] ERROR : Can not include '" + includedSchema.getLocation() + "' schema document. The included schema documents must have the same targetNamespace as the <include>ing schema document, or no targetNamespace at all.");
//	  	}
//	  	includedSchemasCollector.add(includedSchema);
//      if(runtimeLoadingInclude) {
//        SchemaStructuresLoader.loadBase((BaseImpl)includedSchema);
//      }
//  	}
//  }
//  
//  public Hashtable getImportedSchemas() {
//  	return(nsToImportedSchemasMapping);
//  }
//  
//  public Vector getIncludedSchemas() {
//  	return(includedSchemasCollector);
//  }
//  
//  public LoaderImpl getLoader() {
//  	return(loader);
//  } 
//  
//  public boolean isElemsFormDefaultQualified() {
//  	return(elemsFormDefaultIsQualified);
//  }
//  
//	public boolean isAttribsFormDefaultQualified() {
//  	return(attribsFormDefaultIsQualified);
//  }
//  
//	public String getTargetNamespace() {
//  	return(targetNamespace);
//  }
//  
//	public void setTargetNamespace(String targetNamespace) {
//		setTargetNamespace(targetNamespace, new Vector());
//  }
//  
//  private void setTargetNamespace(String targetNamespace, Vector processedSchemasCollector) {
//  	if(!processedSchemasCollector.contains(this)) {
//			processedSchemasCollector.add(this);
//			this.targetNamespace = targetNamespace;
//			for(int i = 0; i < includedSchemasCollector.size(); i++) {
//        SchemaImpl includedSchema = (SchemaImpl)(includedSchemasCollector.get(i));
//				includedSchema.setTargetNamespace(targetNamespace, processedSchemasCollector);
//			}
//  	}
//  }
//  
//	public boolean isFinalExtension() {
//  	return(isFinalExtension);
//  }
//  
//	public boolean isFinalRestriction() {
//  	return(isFinalRestriction);
//  }
//  
//	public boolean isProhibitedSubstitution() {
//  	return(isProhibitedSubstitution);
//  }
//  
//	public boolean isProhibitedExtension() {
//  	return(isProhibitedExtension);
//  }
//  
//	public boolean isProhibitedRestriction() {
//  	return(isProhibitedRestriction);
//  }
//  
//	public boolean isProhibitedList() {
//  	return(isProhibitedList);
//  }
//  
//	public boolean isProhibitedUnion() {
//  	return(isProhibitedUnion);
//  }
//  
//  public String getLocation() {
//  	return(location);
//  }
//  
//  public void addComponent(BaseImpl base) {
//  	allComponentsCollector.add(base);
//  }
//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////	
//	
//	public void getTopLevelComponents(Vector collector) {
//		Vector processedSchemasCollector = new Vector();
//		processedSchemasCollector.add(this);
//		getAllCurrentNSTopLevelComponents(collector, processedSchemasCollector);
//		getAllImportedNSesTopLevelComponents(collector, processedSchemasCollector);
//	}
//
//	private void getAllCurrentNSTopLevelComponents(Vector collector, Vector processedSchemasCollector) {
//		addAllFromHashtableToVector(collector, (Hashtable)(idToTopLevelComponentsMapping.get(TYPE_DEFINITION_ID)));
//		addAllFromHashtableToVector(collector, (Hashtable)(idToTopLevelComponentsMapping.get(ELEMENT_DECLARATION_ID)));
//		addAllFromHashtableToVector(collector, (Hashtable)(idToTopLevelComponentsMapping.get(ATTRIBUTE_DECLARATION_ID)));
//		addAllFromHashtableToVector(collector, (Hashtable)(idToTopLevelComponentsMapping.get(ATTRIBUTE_GROUP_DEFINITION_ID)));
//		addAllFromHashtableToVector(collector, (Hashtable)(idToTopLevelComponentsMapping.get(MODEL_GROUP_DEFINITION_ID)));
//		addAllFromHashtableToVector(collector, (Hashtable)(idToTopLevelComponentsMapping.get(IDENTITY_CONSTRAINT_DEFINITION_ID)));
//		addAllFromHashtableToVector(collector, (Hashtable)(idToTopLevelComponentsMapping.get(NOTATION_DECLARATION_ID)));
//		for(int i = 0; i < includedSchemasCollector.size(); i++) {
//      SchemaImpl includedSchema = (SchemaImpl)(includedSchemasCollector.get(i));
//			if(!processedSchemasCollector.contains(includedSchema)) {
//				processedSchemasCollector.add(includedSchema);
//				includedSchema.getAllCurrentNSTopLevelComponents(collector, processedSchemasCollector);
//			}
//		}
//	}
//	
//	private void addAllFromHashtableToVector(Vector vctrCollector, Hashtable hashCollector) {
//		Enumeration enum = hashCollector.elements();
//		while(enum.hasMoreElements()) {
//			vctrCollector.add(enum.nextElement());
//		}
//	}
//  
//	private void getAllImportedNSesTopLevelComponents(Vector collector, Vector processedSchemasCollector) {
//		Enumeration enum = nsToImportedSchemasMapping.elements();
//		while(enum.hasMoreElements()) {
//			Vector importedSchemasCollector = (Vector)(enum.nextElement());
//			for(int i = 0; i < importedSchemasCollector.size(); i++) {
//        SchemaImpl importedSchema = (SchemaImpl)(importedSchemasCollector.get(i));
//				if(!processedSchemasCollector.contains(importedSchema)) {
//					processedSchemasCollector.add(importedSchema);
//					importedSchema.getAllCurrentNSTopLevelComponents(collector, processedSchemasCollector);
//				}
//			}
//		}
//	}
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//	public void getTopLevelComponents(Vector collector, String componentsId) {
//		Vector processedSchemasCollector = new Vector();
//		processedSchemasCollector.add(this);
//		getCurrentNSTopLevelComponents(collector, componentsId, processedSchemasCollector);
//		getImportedNSesTopLevelComponents(collector, componentsId, processedSchemasCollector);
//	}
//
//	private void getCurrentNSTopLevelComponents(Vector collector, String componentsId, Vector processedSchemasCollector) {
//		addAllFromHashtableToVector(collector, (Hashtable)(idToTopLevelComponentsMapping.get(componentsId)));
//		for(int i = 0; i < includedSchemasCollector.size(); i++) {
//      SchemaImpl includedSchema = (SchemaImpl)(includedSchemasCollector.get(i));
//			if(!processedSchemasCollector.contains(includedSchema)) {
//				processedSchemasCollector.add(includedSchema);
//				includedSchema.getCurrentNSTopLevelComponents(collector, componentsId, processedSchemasCollector);
//			}
//		}
//	}
//  
//	private void getImportedNSesTopLevelComponents(Vector collector, String componentsId, Vector processedSchemasCollector) {
//		Enumeration enum = nsToImportedSchemasMapping.elements();
//		while(enum.hasMoreElements()) {
//			Vector importedSchemasCollector = (Vector)(enum.nextElement());
//			for(int i = 0; i < importedSchemasCollector.size(); i++) {
//        SchemaImpl importedSchema = (SchemaImpl)(importedSchemasCollector.get(i));
//				if(!processedSchemasCollector.contains(importedSchema)) {
//					processedSchemasCollector.add(importedSchema);
//					importedSchema.getCurrentNSTopLevelComponents(collector, componentsId, processedSchemasCollector);
//				}
//			}
//		}
//  }
//
//  public void getIdentityConstraintDefinitions(Vector collector) {
//		getTopLevelComponents(collector, IDENTITY_CONSTRAINT_DEFINITION_ID);
//  }
//
//  public void getTopLevelTypeDefinitions(Vector collector) {
//		getTopLevelComponents(collector, TYPE_DEFINITION_ID);
//  }
//
//  public void getTopLevelAttributeDeclarations(Vector collector) {
//		getTopLevelComponents(collector, ATTRIBUTE_DECLARATION_ID);
//  }
//
//  public void getTopLevelElementDeclarations(Vector collector) {
//		getTopLevelComponents(collector, ELEMENT_DECLARATION_ID);
//  }
//
//  public void getTopLevelAttributeGroupDefinitions(Vector collector) {
//		getTopLevelComponents(collector, ATTRIBUTE_GROUP_DEFINITION_ID);
//  }
//
//  public void getTopLevelModelGroupDefinitions(Vector collector) {
//		getTopLevelComponents(collector, MODEL_GROUP_DEFINITION_ID);
//  }
//
//  public void getTopLevelNotationDeclarations(Vector collector) {
//		getTopLevelComponents(collector, NOTATION_DECLARATION_ID);
//  }
//
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//  public IdentityConstraintDefinition[] getIdentityConstraintDefinitionsArray() {
//		Vector collector = new Vector();
//		getTopLevelComponents(collector, IDENTITY_CONSTRAINT_DEFINITION_ID);
//		IdentityConstraintDefinition[] components = new IdentityConstraintDefinition[collector.size()];
//		collector.copyInto(components);
//    return(components);
//  }
//
//  public TypeDefinitionBase[] getTopLevelTypeDefinitionsArray() {
//		Vector collector = new Vector();
//		getTopLevelComponents(collector, TYPE_DEFINITION_ID);
//		TypeDefinitionBase[] components = new TypeDefinitionBase[collector.size()];
//		collector.copyInto(components);
//		return(components);
//  }
//
//  public AttributeDeclaration[] getTopLevelAttributeDeclarationsArray() {
//		Vector collector = new Vector();
//		getTopLevelComponents(collector, ATTRIBUTE_DECLARATION_ID);
//		AttributeDeclaration[] components = new AttributeDeclaration[collector.size()];
//		collector.copyInto(components);
//		return(components);
//  }
//
//  public ElementDeclaration[] getTopLevelElementDeclarationsArray() {
//		Vector collector = new Vector();
//		getTopLevelComponents(collector, ELEMENT_DECLARATION_ID);
//		ElementDeclaration[] components = new ElementDeclaration[collector.size()];
//		collector.copyInto(components);
//		return(components);
//  }
//
//  public AttributeGroupDefinition[] getTopLevelAttributeGroupDefinitionsArray() {
//		Vector collector = new Vector();
//		getTopLevelComponents(collector, ATTRIBUTE_GROUP_DEFINITION_ID);
//		AttributeGroupDefinition[] components = new AttributeGroupDefinition[collector.size()];
//		collector.copyInto(components);
//		return(components);
//  }
//
//  public ModelGroupDefinition[] getTopLevelModelGroupDefinitionsArray() {
//		Vector collector = new Vector();
//		getTopLevelComponents(collector, MODEL_GROUP_DEFINITION_ID);
//		ModelGroupDefinition[] components = new ModelGroupDefinition[collector.size()];
//		collector.copyInto(components);
//		return(components);
//  }
//
//  public NotationDeclaration[] getTopLevelNotationDeclarationsArray() {
//		Vector collector = new Vector();
//		getTopLevelComponents(collector, NOTATION_DECLARATION_ID);
//		NotationDeclaration[] components = new NotationDeclaration[collector.size()];
//		collector.copyInto(components);
//		return(components);
//  }
//  
//  public QualifiedBase[] getTopLevelComponentsArray() {
//		Vector collector = new Vector();
//		getTopLevelComponents(collector);
//		QualifiedBase[] components = new QualifiedBase[collector.size()];
//		collector.copyInto(components);
//		return(components);
//  }
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//  public Base[] getAllComponentsAsArray() {
//  	Vector collector = new Vector();
//		getAllComponents(collector);
//    Base[] result = new Base[collector.size()];
//		collector.copyInto(result);
//    return(result);
//  }
//
//  public void getAllComponents(Vector collector) {
//		Vector processedSchemasCollector = new Vector();
//		processedSchemasCollector.add(this);
//		getCurrentNSAllComponents(collector, processedSchemasCollector);
//		getImportedNSesAllComponents(collector, processedSchemasCollector);
//  }
//
//	private void getCurrentNSAllComponents(Vector collector, Vector processedSchemasCollector) {
//		collector.addAll(allComponentsCollector);
//		for(int i = 0; i < includedSchemasCollector.size(); i++) {
//      SchemaImpl includedSchema = (SchemaImpl)(includedSchemasCollector.get(i));
//			if(!processedSchemasCollector.contains(includedSchema)) {
//				processedSchemasCollector.add(includedSchema);
//				includedSchema.getCurrentNSAllComponents(collector, processedSchemasCollector);
//  }
//		}
//  }
//
//	private void getImportedNSesAllComponents(Vector collector, Vector processedSchemasCollector) {
//		Enumeration enum = nsToImportedSchemasMapping.elements();
//		while(enum.hasMoreElements()) {
//			Vector importedSchemasCollector = (Vector)(enum.nextElement());
//			for(int i = 0; i < importedSchemasCollector.size(); i++) {
//        SchemaImpl importedSchema = (SchemaImpl)(importedSchemasCollector.get(i));
//				if(!processedSchemasCollector.contains(importedSchema)) {
//					processedSchemasCollector.add(importedSchema);
//					importedSchema.getCurrentNSAllComponents(collector, processedSchemasCollector);
//    }
//  }
//  }
//  }
//
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//  public int getTypeOfComponent() {
//    return(C_SCHEMA);
//  }
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//  public QualifiedBase getTopLevelComponent(String uri, String name, int type) {
//    switch (type) {
//      case C_ATTRIBUTE_DECLARATION: {
//        return(getTopLevelAttributeDeclaration(uri, name));
//      }
//      case C_ATTRIBUTE_GROUP_DEFINITION: {
//        return(getTopLevelAttributeGroupDefinition(uri, name));
//      }
//      case C_ELEMENT_DECLARATION: {
//        return(getTopLevelElementDeclaration(uri, name));
//      }
//      case C_MODEL_GROUP_DEFINITION: {
//        return(getTopLevelModelGroupDefinition(uri, name));
//      }
//      case C_NOTATION_DECLARATION: {
//        return(getTopLevelNotationDeclaration(uri, name));
//      }
//      case C_TYPE_DEFINITION: {
//      	return(getTopLevelTypeDefinition(uri, name));
//      }
//      case C_COMPLEX_TYPE_DEFINITION: {
//      	return(getTopLevelTypeDefinition(uri, name));
//      }
//      case C_SIMPLE_TYPE_DEFINITION: {
//      	return(getTopLevelTypeDefinition(uri, name));
//      }
//      case C_IDENTITY_CONSTRAINT_DEFINITION: {
//      	return(getIdentityConstraintDefinition(uri, name));
//      }
//      default: {
//        throw new IllegalArgumentException("Type with id " + type + " is unknown.");
//      }
//    }
//  }
//
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//	public QualifiedBase getTopLevelComponent(String uri, String name, String componentsId) {
//		QualifiedBase qBase = null;
//		if(uri.equals(SCHEMA_COMPONENTS_NS) && componentsId.equals(TYPE_DEFINITION_ID)) {
//			qBase = getBuiltInTypeDefinition(name);
//    }
//		if(qBase == null) {
//			qBase = getSpecifiedTopLevelComponent(uri, name, componentsId);
//    }
//		return(qBase);
//  }
//	
//	private QualifiedBase getSpecifiedTopLevelComponent(String uri, String name, String componentsId) {
//		Vector processedSchemasCollector = new Vector();
//		processedSchemasCollector.add(this);
//		if(uri.equals(targetNamespace)) {
//			return(getCurrentNSTopLevelComponent(name, componentsId, processedSchemasCollector));
//    }
//		return(getImportedNSesTopLevelComponent(uri, name, componentsId, processedSchemasCollector));
//  }
//	
//	private QualifiedBase getBuiltInTypeDefinition(String typeName) {
//		return((TypeDefinitionBase)(builtInTypesCollector.get(typeName)));
//  }
//
//	private QualifiedBase getCurrentNSTopLevelComponent(String name, String componentsId, Vector processedSchemasCollector) {
//		Hashtable nameToTopLevelComponentsMapping = (Hashtable)(idToTopLevelComponentsMapping.get(componentsId));
//		QualifiedBase qBase = (QualifiedBase)(nameToTopLevelComponentsMapping.get(name));
//		if(qBase == null) {
//			for(int i = 0; i < includedSchemasCollector.size(); i++) {
//        SchemaImpl includedSchema = (SchemaImpl)(includedSchemasCollector.get(i));
//				if(!processedSchemasCollector.contains(includedSchema)) {
//					processedSchemasCollector.add(includedSchema);
//					qBase = includedSchema.getCurrentNSTopLevelComponent(name, componentsId, processedSchemasCollector);
//					if(qBase != null) {
//					  break;
//					}
//				}
//			}
//		}
//		return(qBase); 
//	}
//  
//	private QualifiedBase getImportedNSesTopLevelComponent(String uri, String name, String componentsId, Vector processedSchemasCollector) {
//		QualifiedBase qBase = null;
//		Vector importedSchemasCollector = (Vector)(nsToImportedSchemasMapping.get(uri));
//		if(importedSchemasCollector != null) {
//			for(int i = 0; i < importedSchemasCollector.size(); i++) {
//        SchemaImpl importedSchema = (SchemaImpl)(importedSchemasCollector.get(i));
//				if(!processedSchemasCollector.contains(importedSchema)) {
//					processedSchemasCollector.add(importedSchema);
//					qBase = importedSchema.getCurrentNSTopLevelComponent(name, componentsId, processedSchemasCollector);;
//					if(qBase != null) {
//					  break;
//          }
//        }
//      }
//    }
//		return(qBase);
//	}
//
//  public IdentityConstraintDefinition getIdentityConstraintDefinition(String uri, String name) {
//    return((IdentityConstraintDefinition)(getTopLevelComponent(uri, name, IDENTITY_CONSTRAINT_DEFINITION_ID)));
//  }
//
//  public TypeDefinitionBase getTopLevelTypeDefinition(String uri, String name) {
//    return((TypeDefinitionBase)(getTopLevelComponent(uri, name, TYPE_DEFINITION_ID)));
//  }
//  
//  public AttributeDeclaration getTopLevelAttributeDeclaration(String uri, String name) {
//		return((AttributeDeclaration)(getTopLevelComponent(uri, name, ATTRIBUTE_DECLARATION_ID)));
//  }
//
//  public ElementDeclaration getTopLevelElementDeclaration(String uri, String name) {
//		return((ElementDeclaration)(getTopLevelComponent(uri, name, ELEMENT_DECLARATION_ID)));
//  }
//
//  public AttributeGroupDefinition getTopLevelAttributeGroupDefinition(String uri, String name) {
//		return((AttributeGroupDefinition)(getTopLevelComponent(uri, name, ATTRIBUTE_GROUP_DEFINITION_ID)));
//  }
//  
//  public ModelGroupDefinition getTopLevelModelGroupDefinition(String uri, String name) {
//		return((ModelGroupDefinition)(getTopLevelComponent(uri, name, MODEL_GROUP_DEFINITION_ID)));
//  }
//  
//  public NotationDeclaration getTopLevelNotationDeclaration(String uri, String name) {
//		return((NotationDeclaration)(getTopLevelComponent(uri, name, NOTATION_DECLARATION_ID)));
//  }
//  
/////////////////////////////////////////////////////////////////////////////////////////////////////////////  
//  
//  private void addAttributeDeclaration(AttributeDeclarationImpl attributeDeclaration) throws SchemaComponentException {
//  	addTopLevelComponent(attributeDeclaration, ATTRIBUTE_DECLARATION_ID);
//  }
//  
//  private void addAttributeGroupDefinition(AttributeGroupDefinitionImpl attributeGroupDefinition) throws SchemaComponentException {
//		addTopLevelComponent(attributeGroupDefinition, ATTRIBUTE_GROUP_DEFINITION_ID);
//  }
//  
//	private void addElementDeclaration(ElementDeclarationImpl elementDeclaration) throws SchemaComponentException {
//		addTopLevelComponent(elementDeclaration, ELEMENT_DECLARATION_ID);
//  }
//  
//	private void addModelGroupDefinition(ModelGroupDefinitionImpl modelGroupDefinition) throws SchemaComponentException {
//		addTopLevelComponent(modelGroupDefinition, MODEL_GROUP_DEFINITION_ID);
//	}
//	
//	private void addNotationDeclaration(NotationDeclarationImpl notationDeclaration) throws SchemaComponentException {
//		addTopLevelComponent(notationDeclaration, NOTATION_DECLARATION_ID);
//	}
//	
//	private void addComplexTypeDefinition(ComplexTypeDefinitionImpl complexTypeDefinition) throws SchemaComponentException {
//		addTopLevelComponent(complexTypeDefinition, TYPE_DEFINITION_ID);
//	}
//	
//	private void addSimpleTypeDefinition(SimpleTypeDefinitionImpl simpleTypeDefinition) throws SchemaComponentException {
//		addTopLevelComponent(simpleTypeDefinition, TYPE_DEFINITION_ID);
//	}
//	
//	protected void addIdentityConstraintDefinition(IdentityConstraintDefinitionImpl identConstrDefinition) throws SchemaComponentException {
//		addTopLevelComponent(identConstrDefinition, IDENTITY_CONSTRAINT_DEFINITION_ID);
//	}
//
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
//	
//  public boolean match(Base schemaImpl) {
//  		return(false);
//  	}
//
//  public void load() throws SchemaComponentException {
//    super.load();
//    importNamespaces();
//    loadImportedSchemas();
//    loadIncludedSchemas();
//		loadTopLevelComponents(TYPE_DEFINITION_ID);
//    loadTopLevelComponents(ELEMENT_DECLARATION_ID);
//		loadTopLevelComponents(ATTRIBUTE_DECLARATION_ID);
//		loadTopLevelComponents(ATTRIBUTE_GROUP_DEFINITION_ID);
//		loadTopLevelComponents(MODEL_GROUP_DEFINITION_ID);
//		loadTopLevelComponents(NOTATION_DECLARATION_ID);
//		loadTopLevelComponents(IDENTITY_CONSTRAINT_DEFINITION_ID);
//  }
//  
//  private void importNamespaces() throws SchemaComponentException {
//  	for(int i = 0; i < importedNamespacesCollector.size(); i++) {
//  		String namespace = (String)(importedNamespacesCollector.get(i));
//  		importNamespace(namespace, false);
//  	}
//  }
//  
//  public void importNamespace(String namespace, boolean runtimeLoadingImport) throws SchemaComponentException {
//    Schema[] schemas = runtimeLoadingImport ? loader.produceSchemas_Namespace_UriResolver(namespace) : loader.produceSchemas_Namespace(namespace);
//    if(schemas != null) {
//  		for(int j = 0; j < schemas.length; j++) {
//  			SchemaImpl importedSchema = schemas[j];
//  			importSchema(namespace, importedSchema, runtimeLoadingImport);
//  		}
//    }
//  } 
//  
//  public void includeNamespace() throws SchemaComponentException {
//  	includeSchemas(loader.produceSchemas_Namespace(targetNamespace), true);
//  }
//  
//  private void loadImportedSchemas() throws SchemaComponentException {
//  	Enumeration enum = nsToImportedSchemasMapping.elements();
//  	while(enum.hasMoreElements()) {
//  		Vector importedSchemasCollector = (Vector)(enum.nextElement());
//  		for(int i = 0; i < importedSchemasCollector.size(); i++) {
//        SchemaImpl importedSchema = (SchemaImpl)(importedSchemasCollector.get(i));
//  			SchemaStructuresLoader.loadBase(importedSchema);
//  		}
//  	}
//  }
//
//  private void loadIncludedSchemas() throws SchemaComponentException {
//  	for(int i = 0; i < includedSchemasCollector.size(); i++) {
//      SchemaImpl includedSchema = (SchemaImpl)(includedSchemasCollector.get(i));
//			SchemaStructuresLoader.loadBase(includedSchema);
//			if(!includedSchema.getTargetNamespace().equals(targetNamespace)) {
//				includedSchema.setTargetNamespace(targetNamespace);
//			}
//  	}
//  }
//  
//  private void loadTopLevelComponents(String componentsId) throws SchemaComponentException {
//  	Hashtable storage = (Hashtable)(idToTopLevelComponentsMapping.get(componentsId));
//  	Enumeration enum = storage.elements();
//  	while(enum.hasMoreElements()) {
//  		BaseImpl qBase = (BaseImpl)(enum.nextElement());
//			SchemaStructuresLoader.loadBase(qBase);
//    }
//  }
//
//  private void addTopLevelComponent(QualifiedBaseImpl qualifiedBase, String componentId) throws SchemaComponentException  {
//  	if(!qualifiedBase.getTargetNamespace().equals(SCHEMA_COMPONENTS_NS) || !componentId.equals(TYPE_DEFINITION_ID) || !builtInTypesCollector.containsKey(qualifiedBase.getName())) {
//			Hashtable storage = (Hashtable)(idToTopLevelComponentsMapping.get(componentId));
//			if(storage.containsKey(qualifiedBase.getName())) {
//				throw new SchemaComponentException("[location : '" + location + "'] ERROR : Top level component " + qualifiedBase.toString() + " is already defined.");
//			}
//			storage.put(qualifiedBase.getName(), qualifiedBase);
//		}
//  }
//
//  protected BaseImpl initializeBase(BaseImpl base, Hashtable clonedCollector) {
//    return(null);
//  }
//
//  public static TypeDefinitionBase getBuiltInTypeDefnition(String typeName) {
//    return((TypeDefinitionBase)(builtInTypesCollector.get(Tools.generateKey(SCHEMA_COMPONENTS_NS, typeName))));
//  }
//
//  public void getBuiltInTypeDefinitions(Vector collector) {
//    Enumeration enum = builtInTypesCollector.elements();
//    while(enum.hasMoreElements()) {
//      collector.add(enum.nextElement());
//    }
//  }
//
//  public SimpleTypeDefinition[] getBuiltInTypeDefinitions() {
//    Vector collector = new Vector();
//    getBuiltInTypeDefinitions(collector);
//    SimpleTypeDefinition[] result = new SimpleTypeDefinition[collector.size()];
//    collector.copyInto(result);
//    return(result);
//  }
}

