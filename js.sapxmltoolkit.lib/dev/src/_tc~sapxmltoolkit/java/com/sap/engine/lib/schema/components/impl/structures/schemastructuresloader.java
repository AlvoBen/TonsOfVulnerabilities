/*
 * Created on 2005-1-18
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.schema.components.impl.structures;

import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.sap.engine.lib.schema.Constants;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.xml.dom.DOM;

/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class SchemaStructuresLoader implements Constants {

	public static AnnotationImpl createAnnotation(Node associatedNode, SchemaImpl schema) throws SchemaComponentException {
		return(new AnnotationImpl(associatedNode, schema));
	}

	public static AttributeDeclarationImpl createAttributeDeclaration(Node associatedNode, SchemaImpl schema, boolean isTopLevel) throws SchemaComponentException {
		AttributeDeclarationImpl attribDeclaration = null;
		if(isTopLevel) {
			attribDeclaration = new AttributeDeclarationImpl(associatedNode, schema, true);
		} else {
			attribDeclaration = (AttributeDeclarationImpl)(getRefferedTopLevelComponent(associatedNode, schema, ATTRIBUTE_DECLARATION_ID));
			if(attribDeclaration == null) {
				attribDeclaration = new AttributeDeclarationImpl(associatedNode, schema, false);
			}
		}
		return(attribDeclaration);
	}

	public static AttributeGroupDefinitionImpl createAttributeGroupDefinition(Node associatedNode, SchemaImpl schema, boolean isTopLevel, boolean isRedefined) throws SchemaComponentException {
		AttributeGroupDefinitionImpl attribGroupDefinition = null;
		if(isTopLevel) {
			attribGroupDefinition = new AttributeGroupDefinitionImpl(associatedNode, schema, true, isRedefined);
		} else {
			attribGroupDefinition = (AttributeGroupDefinitionImpl)(getRefferedTopLevelComponent(associatedNode, schema, ATTRIBUTE_GROUP_DEFINITION_ID));
			if(attribGroupDefinition == null) {
				attribGroupDefinition = new AttributeGroupDefinitionImpl(associatedNode, schema, false, false);
			}
		}
		return(attribGroupDefinition);
	}

	public static AttributeUseImpl createAttributeUse(Node associatedNode, SchemaImpl schema) throws SchemaComponentException {
		return(new AttributeUseImpl(associatedNode, schema));
	}

	public static ComplexTypeDefinitionImpl createComplexTypeDefinition(Node associatedNode, SchemaImpl schema, boolean isTopLevel, boolean isRedefined) throws SchemaComponentException {
		return(new ComplexTypeDefinitionImpl(associatedNode, schema, isTopLevel, isRedefined));
	}

	public static ElementDeclarationImpl createElementDeclaration(Node associatedNode, SchemaImpl schema, boolean isTopLevel) throws SchemaComponentException {
		ElementDeclarationImpl elementDeclaration = null;
		if(isTopLevel) {
			elementDeclaration = new ElementDeclarationImpl(associatedNode, schema, true);
		} else {
			elementDeclaration = (ElementDeclarationImpl)(getRefferedTopLevelComponent(associatedNode, schema, ELEMENT_DECLARATION_ID));
			if(elementDeclaration == null) {
				elementDeclaration = new ElementDeclarationImpl(associatedNode, schema, false);
			}
		}
		return(elementDeclaration);
	}

	public static IdentityConstraintDefinitionImpl createIdentityConstraintDefinition(Node associatedNode, SchemaImpl schema) throws SchemaComponentException {
		return(new IdentityConstraintDefinitionImpl(associatedNode, schema));
	}

	public static FacetImpl createFacet(Node associatedNode, SchemaImpl schema) throws SchemaComponentException {
		return(new FacetImpl(associatedNode, schema));
	}

	public static ModelGroupImpl createModelGroup(Node associatedNode, SchemaImpl schema) throws SchemaComponentException {
		return(new ModelGroupImpl(associatedNode, schema));
	}

	public static ModelGroupDefinitionImpl createModelGroupDefinition(Node associatedNode, SchemaImpl schema, boolean isTopLevel, boolean isRedefined) throws SchemaComponentException {
		ModelGroupDefinitionImpl modelGroupDefinition = null;
		if(isTopLevel) {
			modelGroupDefinition = new ModelGroupDefinitionImpl(associatedNode, schema, true, isRedefined);
		} else {
			modelGroupDefinition = (ModelGroupDefinitionImpl)(getRefferedTopLevelComponent(associatedNode, schema, MODEL_GROUP_DEFINITION_ID));
			if(modelGroupDefinition == null) {
				modelGroupDefinition = new ModelGroupDefinitionImpl(associatedNode, schema, false, false);
			}
		}
		return(modelGroupDefinition);
	}

	public static NotationDeclarationImpl createNotationDeclaration(Node associatedNode, SchemaImpl schema) throws SchemaComponentException {
		return(new NotationDeclarationImpl(associatedNode, schema));
	}

	public static ParticleImpl createParticle(Node associatedNode, SchemaImpl schema) throws SchemaComponentException {
		return(new ParticleImpl(associatedNode, schema));
	}

	public static SimpleTypeDefinitionImpl createSimpleTypeDefinition(Node associatedNode, SchemaImpl schema, boolean isTopLevel, boolean isRedefiend) throws SchemaComponentException {
		return(new SimpleTypeDefinitionImpl(associatedNode, schema, isTopLevel, isRedefiend));
	}

	public static WildcardImpl createWildcard(Node associatedNode, SchemaImpl schema) throws SchemaComponentException {
		return(new WildcardImpl(associatedNode, schema));
	}
	
	private static BaseImpl getRefferedTopLevelComponent(Node associatedNode, SchemaImpl schema, String componentId) throws SchemaComponentException {
    return(schema.getLoader().getBackwardsCompatibilityMode() ? getRefferedTopLevelComponent_BCMIsSet(associatedNode, schema, componentId)
                                                              : getRefferedTopLevelComponent_BCMIsNotSet(associatedNode, schema, componentId));
	}
  
  private static BaseImpl getRefferedTopLevelComponent_BCMIsNotSet(Node associatedNode, SchemaImpl schema, String componentId) throws SchemaComponentException {
		Element associatedElement = (Element)associatedNode;
    NamedNodeMap attribs = associatedElement.getAttributes();
    String referencedName = null;
    boolean hasOtherRepresentAttribs = false;
    for(int i = 0; i < attribs.getLength(); i++) {
      Attr attrib = (Attr)(attribs.item(i));
      if(attrib.getNamespaceURI() == null) {
        String attribName = attrib.getLocalName();
        if(attribName.equals(NODE_REF_NAME)) {
          referencedName = attrib.getValue();
          if(hasOtherRepresentAttribs) {
            break;
          }
        } else if(attribName.equals(NODE_NAME_NAME) ||
                  attribName.equals(NODE_NILLABLE_NAME) ||
                  (attribName.equals(NODE_DEFAULT_NAME) && componentId.equals(ELEMENT_DECLARATION_ID)) ||
                  (attribName.equals(NODE_FIXED_NAME) && componentId.equals(ELEMENT_DECLARATION_ID)) ||
                  attribName.equals(NODE_FORM_NAME) ||
                  attribName.equals(NODE_BLOCK_NAME) ||
                  attribName.equals(NODE_TYPE_NAME)) {
          hasOtherRepresentAttribs = true;
          if(referencedName != null) {
            break;
          }
        }
      }
    }
    if(referencedName != null) {
      if(hasOtherRepresentAttribs) {
        throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of qualified base " + DOM.toXPath(associatedNode) + " is not correct. If 'ref' attribute is present none of 'name', 'nillable', 'default', 'fixed', 'form', 'block' or 'type' should be.");
      }
      return(Tools.getTopLevelComponent(schema, associatedNode, referencedName, componentId));
    }
		return(null);
	}

  private static BaseImpl getRefferedTopLevelComponent_BCMIsSet(Node associatedNode, SchemaImpl schema, String componentId) throws SchemaComponentException {
    Element associatedElement = (Element)associatedNode;
    Attr refAttrib = associatedElement.getAttributeNode(NODE_REF_NAME);
    if(refAttrib != null) {
      return(Tools.getTopLevelComponent(schema, associatedNode, refAttrib.getValue(), componentId));
    }
    return(null);
  }
  
	public static void loadBase(BaseImpl base) throws SchemaComponentException {
		if(!base.isLoaded() && !base.isLoading()) {
			base.setLoading(true);
			base.load();
			base.setLoading(false);
			base.setLoaded(true);
			base.destroy();
		}
	}
  
  public static void preloadComplexType(ComplexTypeDefinitionImpl complexType) throws SchemaComponentException {
    if(!complexType.isPreloaded() && !complexType.isPreloading()) {
      complexType.setPreloading(true);
      complexType.preload();
      complexType.setPreloading(false);
      complexType.setPreloaded(true);
      complexType.destroy();
    }
  }
}
