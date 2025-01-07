package com.sap.engine.lib.schema.components.impl.structures;

import org.w3c.dom.*;

import com.sap.engine.lib.schema.components.ModelGroup;
import com.sap.engine.lib.schema.components.Particle;
import com.sap.engine.lib.schema.components.Base;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import com.sap.engine.lib.xml.dom.DOM;

import java.util.Hashtable;

public final class ParticleImpl extends BaseImpl implements Particle {

  protected int minOccurs;
  protected int maxOccurs;
  protected BaseImpl term;
  protected boolean isEmptiable;

  public ParticleImpl() {
    this(null);
  }

  public ParticleImpl(SchemaImpl schema) {
    this(null, schema);
  }

	public ParticleImpl(Node associatedNode, SchemaImpl schema) {
		super(associatedNode, schema);
	}
	
  public int getMinOccurs() {
    return(minOccurs);
  }

  public boolean isMaxOccursUnbounded() {
    return(maxOccurs == Integer.MAX_VALUE);
  }

  public int getMaxOccurs() {
    return(maxOccurs);
  }

  public Base getTerm() {
    return(term);
  }

  public int getTypeOfComponent() {
    return(C_PARTICLE);
  }

  public boolean match(Base particle) {
  	if(!super.match(particle)) {
  		return(false);
  	}
  	ParticleImpl targetParticle = (ParticleImpl)particle; 
  	return(minOccurs == targetParticle.minOccurs &&
  				 maxOccurs == targetParticle.maxOccurs &&
  				 term.match(targetParticle.term));
  }
  
  public boolean isEmptiable() {
    return(isEmptiable);
  }

  public void load() throws SchemaComponentException {
    if(associatedNode != null) {
      id = loadAttribsCollector.getProperty(NODE_ID_NAME);
      String maxOccursAttribValue = loadAttribsCollector.getProperty(NODE_MAX_OCCURS_NAME);
      if(maxOccursAttribValue != null) {
        maxOccurs = (maxOccursAttribValue.equals(VALUE_UNBOUNDED_NAME)) ? Integer.MAX_VALUE : processNumber(maxOccursAttribValue);
        if(maxOccurs < 0) {
          throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of particle " + DOM.toXPath(associatedNode) + " is not correct. {max occurs} must be greater than or equal to 0.");
        }
      } else  {
        maxOccurs = 1;
      }
      String minOccursAttribValue = loadAttribsCollector.getProperty(NODE_MIN_OCCURS_NAME);
      if(minOccursAttribValue != null) {
        minOccurs = processNumber(minOccursAttribValue);
        if(minOccurs < 0) {
          throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of particle " + DOM.toXPath(associatedNode) + " is not correct. {min occurs} must have a non negative value.");
        }
      } else  {
        minOccurs = 1;
      }
      if(minOccurs > maxOccurs) {
        throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of particle " + DOM.toXPath(associatedNode) + " is not correct. {min occurs} must not be greater than {max occurs}.");
      }

      String localName = associatedNode.getLocalName();
      if(localName.equals(NODE_ELEMENT_NAME)) {
        term = SchemaStructuresLoader.createElementDeclaration(associatedNode, schema, false);
      } else if(localName.equals(NODE_ANY_NAME) || localName.equals(NODE_ANY_ATTRIBUTE_NAME)) {
        term = SchemaStructuresLoader.createWildcard(associatedNode, schema);
      } else if(localName.equals(NODE_ALL_NAME) || localName.equals(NODE_SEQUENCE_NAME) || localName.equals(NODE_CHOICE_NAME)) {
        term = SchemaStructuresLoader.createModelGroup(associatedNode, schema);
      } else if(localName.equals(NODE_GROUP_NAME)) {
        term = SchemaStructuresLoader.createModelGroupDefinition(associatedNode, schema, false, false);
      }
			SchemaStructuresLoader.loadBase(term);
      loadIsEmptiable();
    }
  }
  
  private void loadIsEmptiable() {
    isEmptiable = minOccurs == 0 || 
                  (term instanceof ModelGroup && ((ModelGroup)term).getMinimumEffectiveTotalRange() == 0) ||
                  (term instanceof ModelGroupDefinitionImpl && ((ModelGroupDefinitionImpl)term).getModelGroup().getMinimumEffectiveTotalRange() == 0);
  }

  private int processNumber(String value) throws SchemaComponentException {
    int result = -1;
    try {
      result = Integer.parseInt(value);
    } catch(NumberFormatException numberFormExc) {
      throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of particle " + DOM.toXPath(associatedNode) + " is not correct.A number format error occurs while processing {minOccurs} or {maxOccurs} value.", numberFormExc);
    }
    return(result);
  }

  protected BaseImpl initializeBase(BaseImpl base, Hashtable clonedCollector) {
    ParticleImpl result = (ParticleImpl)(super.initializeBase(base, clonedCollector));
    result.minOccurs = minOccurs;
    result.maxOccurs = maxOccurs;
    result.isEmptiable = isEmptiable;
    result.term = term.clone(clonedCollector);
    return(result);
  }
}
