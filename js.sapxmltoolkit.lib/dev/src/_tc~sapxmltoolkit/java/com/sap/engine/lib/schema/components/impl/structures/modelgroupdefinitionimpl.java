package com.sap.engine.lib.schema.components.impl.structures;

import org.w3c.dom.*;

import com.sap.engine.lib.schema.components.Base;
import com.sap.engine.lib.schema.components.ModelGroupDefinition;
import com.sap.engine.lib.schema.components.ModelGroup;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import com.sap.engine.lib.xml.dom.DOM;

import java.util.Hashtable;

public final class ModelGroupDefinitionImpl extends RedefineableQualifiedBaseImpl implements ModelGroupDefinition {

  protected ModelGroupImpl modelGroup;

  public ModelGroupDefinitionImpl() {
    this(null, null, false, false);
  }

  public ModelGroupDefinitionImpl(Node associatedNode, SchemaImpl schema, boolean isTopLevel, boolean isRedefined) {
		super(associatedNode, schema, isTopLevel, isRedefined);
  }

  public ModelGroup getModelGroup() {
    return(modelGroup);
  }

  public int getTypeOfComponent() {
    return(C_MODEL_GROUP_DEFINITION);
  }

  public boolean match(Base modelGroupDef) {
  	if(!super.match(modelGroupDef)) {
  		return(false);
  	}
  	ModelGroupDefinitionImpl targetModelGroupDef = (ModelGroupDefinitionImpl)modelGroupDef;
  	return(Tools.compareBases(modelGroup, targetModelGroupDef.modelGroup));
  }

  public void load() throws SchemaComponentException {
    if(associatedNode != null) {
      id = loadAttribsCollector.getProperty(NODE_ID_NAME);
      NodeList nodeList = associatedNode.getChildNodes();
      for(int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        if(node instanceof Element) {
          String nodeUri = node.getNamespaceURI();
          if(nodeUri != null && nodeUri.equals(SCHEMA_COMPONENTS_NS)) {
            String nodeLocalName = node.getLocalName();
            if(nodeLocalName.equals(NODE_ALL_NAME) || nodeLocalName.equals(NODE_CHOICE_NAME) || nodeLocalName.equals(NODE_SEQUENCE_NAME)) {
              modelGroup = SchemaStructuresLoader.createModelGroup(node, schema);
							SchemaStructuresLoader.loadBase(modelGroup);
            } else if(nodeLocalName.equals(NODE_ANNOTATION_NAME)) {
              annotation = SchemaStructuresLoader.createAnnotation(node, schema);
							SchemaStructuresLoader.loadBase(annotation);
            }
          }
        }
      }
      if(!isAnonymous() && searchForThisModelGroupDefinition(modelGroup)) {
        throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of model group " + DOM.toXPath(associatedNode) + " is not correct. Circular definitions are prohibited.");
      }
      schema.addComponent(this);
    }
  }

  private boolean searchForThisModelGroupDefinition(ModelGroupImpl modelGroup) {
    for(int i = 0; i < modelGroup.particles.size(); i++) {
      ParticleImpl particle = (ParticleImpl)(modelGroup.particles.get(i));
      BaseImpl term = particle.term;
      if(term instanceof ModelGroupDefinitionImpl) {
        ModelGroupDefinitionImpl modelGroupDef = (ModelGroupDefinitionImpl)term;
        if(modelGroupDef == this) {
          return(true);
        }
        return(searchForThisModelGroupDefinition(modelGroupDef.modelGroup));
      }
    }
    return(false);
  }

  protected BaseImpl initializeBase(BaseImpl base, Hashtable clonedCollector) {
    ModelGroupDefinitionImpl result = (ModelGroupDefinitionImpl)(super.initializeBase(base, clonedCollector));
    result.modelGroup = (ModelGroupImpl)(modelGroup.clone(clonedCollector));
    return(result);
  }
}

