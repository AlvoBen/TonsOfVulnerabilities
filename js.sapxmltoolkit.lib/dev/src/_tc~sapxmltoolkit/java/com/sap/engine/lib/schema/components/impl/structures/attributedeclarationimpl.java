package com.sap.engine.lib.schema.components.impl.structures;

import org.w3c.dom.*;

import com.sap.engine.lib.schema.components.AttributeDeclaration;
import com.sap.engine.lib.schema.components.Base;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.xml.dom.DOM;

import java.util.Hashtable;


public final class AttributeDeclarationImpl extends InfoItemDeclarationBaseImpl implements AttributeDeclaration {

  public AttributeDeclarationImpl() {
    this(null, null, false);
  }

  public AttributeDeclarationImpl(Node associatedNode, SchemaImpl schema, boolean isTopLevel) {
  	super(associatedNode, schema, isTopLevel);
  }

  public int getTypeOfComponent() {
    return(C_ATTRIBUTE_DECLARATION);
  }

  public boolean match(Base attribDeclr) {
		return(super.match(attribDeclr));
  }

  public void load() throws SchemaComponentException {
    if(associatedNode != null) {
      defaultValueConstr = loadAttribsCollector.getProperty(NODE_DEFAULT_NAME);
      fixedValueConstr = loadAttribsCollector.getProperty(NODE_FIXED_NAME);
      id = loadAttribsCollector.getProperty(NODE_ID_NAME);
      String typeDefAttribValue = loadAttribsCollector.getProperty(NODE_TYPE_NAME);
      if(typeDefAttribValue != null) {
        typeDefinition = (TypeDefinitionBaseImpl)(Tools.getTopLevelComponent(schema, associatedNode, typeDefAttribValue, TYPE_DEFINITION_ID));
        SchemaStructuresLoader.loadBase(typeDefinition);
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
                throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of attribute declaration " + DOM.toXPath(associatedNode) + " is not correct. 'type' and <simpleType> are exclusive.");
              }
              typeDefinition = SchemaStructuresLoader.createSimpleTypeDefinition(node, schema, false, false);
              SchemaStructuresLoader.loadBase(typeDefinition);
            } else if(nodeLocalName.equals(NODE_ANNOTATION_NAME)) {
              annotation = SchemaStructuresLoader.createAnnotation(node, schema);
              SchemaStructuresLoader.loadBase(annotation);
            }
          }
        }
      }
      if(name.equals("xmlns")) {
        throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of attribute declaration '" + DOM.toXPath(associatedNode) + "' is not correct. The {name} of an attribute declaration must not match 'xmlns'.");
      }
      if(getTargetNamespace().equals(SCHEMA_INSTANCE_COMPONENTS_NS)) {
        throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of attribute declaration '" + DOM.toXPath(associatedNode) + "' is not correct. The {target namespace} of an attribute declaration, whether local or top-level, must not match http://www.w3.org/2001/XMLSchema-instance.");
      }
      if(typeDefinition == null) {
        typeDefinition = (TypeDefinitionBaseImpl)(schema.getTopLevelTypeDefinition(SCHEMA_COMPONENTS_NS, TYPE_ANY_SIMPLE_TYPE_NAME));
      }
      processValueConstraint();
      schema.addComponent(this);
    }
  }

  protected BaseImpl initializeBase(BaseImpl base, Hashtable typesCollector) {
    AttributeDeclarationImpl result = (AttributeDeclarationImpl)(super.initializeBase(base, typesCollector));
    return(result);
  }
}

