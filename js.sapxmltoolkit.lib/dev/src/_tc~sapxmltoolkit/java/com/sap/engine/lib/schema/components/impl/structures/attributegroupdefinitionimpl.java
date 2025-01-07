package com.sap.engine.lib.schema.components.impl.structures;

import org.w3c.dom.*;
import java.util.*;

import com.sap.engine.lib.schema.components.*;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import com.sap.engine.lib.xml.dom.DOM;

/**
 * @author Nick Nickolov
 * @version November 2001
 */
public final class AttributeGroupDefinitionImpl extends RedefineableQualifiedBaseImpl implements AttributeGroupDefinition {

  protected WildcardImpl attributeWildcard;
  protected Vector attribUses;

  public AttributeGroupDefinitionImpl() {
    this(null, null, false, false);
  }

  public AttributeGroupDefinitionImpl(Node associatedNode, SchemaImpl schema, boolean isTopLevel, boolean isRedefined) {
		super(associatedNode, schema, isTopLevel, isRedefined);
		attribUses = new Vector();
  }

  public Wildcard getAttributeWildcard() {
    return(attributeWildcard);
  }

  public int getTypeOfComponent() {
    return(C_ATTRIBUTE_GROUP_DEFINITION);
  }

  public Vector getAttributeUses() {
    return(attribUses);
  }
  
  public void getAttributeUses(Vector attribUsesCollector) {
    Tools.removeFromVectorToVector(this.attribUses, attribUsesCollector);
  }

  public AttributeUse[] getAttributeUsesArray() {
    AttributeUse[] result = new AttributeUse[attribUses.size()];
    attribUses.copyInto(result);
    return(result);
  }

  public boolean match(Base attribGroupDef) {
  	if(!super.match(attribGroupDef)) {
  		return(false);
  	}
  	AttributeGroupDefinitionImpl targetAttribGroupDef = (AttributeGroupDefinitionImpl)attribGroupDef;
  	if(!Tools.compareUnorderedBases(attribUses, targetAttribGroupDef.attribUses)) {
  		return(false);
  	}
  	
 		return(Tools.compareBases(attributeWildcard, targetAttribGroupDef.attributeWildcard)); 	
  }

  public void load() throws SchemaComponentException {
    if(associatedNode != null) {
      id = loadAttribsCollector.getProperty(NODE_ID_NAME);
			boolean[] hasAlreadyIdAttribute = {false};
      NodeList nodeList = associatedNode.getChildNodes();
      for(int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        if(node instanceof Element) {
          String nodeUri = node.getNamespaceURI();
          if(nodeUri != null && nodeUri.equals(SCHEMA_COMPONENTS_NS)) {
            String nodeLocalName = node.getLocalName();
            if(nodeLocalName.equals(NODE_ANY_ATTRIBUTE_NAME)) {
              WildcardImpl wildcard = SchemaStructuresLoader.createWildcard(node, schema);
							SchemaStructuresLoader.loadBase(wildcard);
              if(attributeWildcard != null) {
                attributeWildcard.intersect(wildcard);
              } else {
                attributeWildcard = wildcard;
              }
            } else if(nodeLocalName.equals(NODE_ATTRIBUTE_NAME)) {
              AttributeUseImpl attribUse = SchemaStructuresLoader.createAttributeUse(node, schema);
							SchemaStructuresLoader.loadBase(attribUse);
              addAttributeUse(attribUse, hasAlreadyIdAttribute);
            } else if(nodeLocalName.equals(NODE_ATTRIBUTE_GROUP_NAME)) {
              AttributeGroupDefinitionImpl attribGroup = SchemaStructuresLoader.createAttributeGroupDefinition(node, schema, false, false);
							SchemaStructuresLoader.loadBase(attribGroup);
              for(int j = 0; j < attribGroup.attribUses.size(); j++) {
                AttributeUseImpl attribUse = (AttributeUseImpl)(attribGroup.attribUses.get(j));
                addAttributeUse(attribUse, hasAlreadyIdAttribute);
              }
              initWildcard(attribGroup.attributeWildcard);
            } else if(nodeLocalName.equals(NODE_ANNOTATION_NAME)) {
              annotation = SchemaStructuresLoader.createAnnotation(node, schema);
							SchemaStructuresLoader.loadBase(annotation);
            }
          }
        }
      }
      schema.addComponent(this);
    }
  }

  private void addAttributeUse(AttributeUseImpl attribUse, boolean[] hasAlreadyIdAttribute) throws SchemaComponentException {
    AttributeDeclarationImpl attribDeclr = attribUse.attribDeclr;
    if(attribDeclr.typeDefinition.isDerivedFrom(schema.getTopLevelTypeDefinition(SCHEMA_COMPONENTS_NS, TYPE_ID_NAME), false, false)) {
      if(hasAlreadyIdAttribute[0]) {
        throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of attribute group " + DOM.toXPath(associatedNode) + " is not correct. Two members of the {attribute uses} must not have {attribute declaration}s whose {type definition}s are or are derived from ID.");
      }
			hasAlreadyIdAttribute[0] = true;
    }
    for(int i = 0; i < attribUses.size(); i++) {
      AttributeUseImpl addedAttribUse = (AttributeUseImpl)(attribUses.get(i));
      if(attribDeclr.getQualifiedKey().equals(addedAttribUse.attribDeclr.getQualifiedKey())) {
        throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of attribute group " + DOM.toXPath(associatedNode) + " is not correct. Two members of the {attribute uses} must not have {attribute declaration}s whose {name}s and {target namespace}s are identical.");
      }
    }
    attribUses.add(attribUse);
  }

  private void initWildcard(WildcardImpl wildcard) throws SchemaComponentException {
    if(wildcard != null) {
      if(attributeWildcard == null) {
        attributeWildcard = (WildcardImpl)(wildcard.clone(new Hashtable()));
      } else {
        attributeWildcard.intersect(wildcard);
      }
    }
  }

  protected BaseImpl initializeBase(BaseImpl base, Hashtable clonedCollector) {
    AttributeGroupDefinitionImpl result = (AttributeGroupDefinitionImpl)(super.initializeBase(base, clonedCollector));
    if(attributeWildcard != null) {
      result.attributeWildcard = (WildcardImpl)(attributeWildcard.clone(clonedCollector));
    }
    Tools.cloneVectorWithBases(attribUses, result.attribUses, clonedCollector);
    return(result);
  }
}

