package com.sap.engine.lib.schema.components.impl.structures;

import java.util.*;
import org.w3c.dom.*;

import com.sap.engine.lib.schema.components.ModelGroup;
import com.sap.engine.lib.schema.components.Particle;
import com.sap.engine.lib.schema.components.Base;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.exception.SchemaComponentException;

public final class ModelGroupImpl extends BaseImpl implements ModelGroup {

  protected static final int ALL = 1;
  protected static final int CHOICE = 2;
  protected static final int SEQUENCE = 3;
  protected int compositor;
  protected Vector particles;
  protected int minimumEffectiveTotalRange;
  protected int maximumEffectiveTotalRange;
  protected boolean unboundedMaximumEffectiveTotalRange;
  
  private int productOfParticleMinOccurs;
  private int sumOfMinimumEffectiveTotalRanges;
  private int minimumOfMinimumEffectiveTotalRanges;
  
  private int productOfParticleMaxOccurs;
  private int sumOfMaximumEffectiveTotalRanges;
  private int maximumOfMaximumEffectiveTotalRanges;

  public boolean isUnboundedMaximumEffectiveTotalRange() {
    return(unboundedMaximumEffectiveTotalRange);
  }
  
  public int getMinimumEffectiveTotalRange() {
    return(minimumEffectiveTotalRange);
  }
  
  public int getMaximumEffectiveTotalRange() {
    return(maximumEffectiveTotalRange);
  }
  
  public ModelGroupImpl() {
    this(null);
  }

  public ModelGroupImpl(SchemaImpl schema) {
    this(null, schema);
  }

  public ModelGroupImpl(Node associatedNode, SchemaImpl schema) {
  	super(associatedNode, schema);
		particles = new Vector();
    productOfParticleMinOccurs = 1;
    sumOfMinimumEffectiveTotalRanges = 0;
    minimumOfMinimumEffectiveTotalRanges = -1;
    productOfParticleMaxOccurs = 1;
    sumOfMaximumEffectiveTotalRanges = 0;
    maximumOfMaximumEffectiveTotalRanges = -1;
  }

  public boolean isCompositorAll() {
    return (compositor == ALL);
  }

  public boolean isCompositorChoice() {
    return(compositor == CHOICE);
  }

  public boolean isCompositorSequence() {
    return(compositor == SEQUENCE);
  }

  public Vector getParticles() {
    return(particles);
  }
  
  public void getParticles(Vector collector) {
    Tools.removeFromVectorToVector(particles, collector);
  }

  public Particle[] getParticlesArray() {
    Particle[] result = new Particle[particles.size()];
    particles.copyInto(result);
    return(result);
  }

  public int getTypeOfComponent() {
    return(C_MODEL_GROUP);
  }

  public boolean match(Base modelGroup) {
  	if(!super.match(modelGroup)) {
  		return(false);
  	}
  	ModelGroupImpl targetModelGroup = (ModelGroupImpl)modelGroup;
  	if(compositor != targetModelGroup.compositor) {
  		return(false);
  	} 

  	if(compositor == SEQUENCE) {
  		return(Tools.compareOrderedBases(particles, targetModelGroup.particles));
  	} 
		return(Tools.compareUnorderedBases(particles, targetModelGroup.particles));
  }

  public void load() throws SchemaComponentException {
    if(associatedNode != null) {
      
      id = loadAttribsCollector.getProperty(NODE_ID_NAME);
      String associatedNodeLocalName = associatedNode.getLocalName();
      if(associatedNodeLocalName.equals(NODE_ALL_NAME)) {
        compositor = ALL;
      } else if(associatedNodeLocalName.equals(NODE_CHOICE_NAME)) {
        compositor = CHOICE;
      } else if(associatedNodeLocalName.equals(NODE_SEQUENCE_NAME)) {
        compositor = SEQUENCE;
      }
      NodeList nodeList = associatedNode.getChildNodes();
      for(int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        if(node instanceof Element) {
          String nodeUri = node.getNamespaceURI();
          if(nodeUri != null && nodeUri.equals(SCHEMA_COMPONENTS_NS)) {
            String nodeLocalName = node.getLocalName();
            if(nodeLocalName.equals(NODE_ELEMENT_NAME) || nodeLocalName.equals(NODE_CHOICE_NAME) || nodeLocalName.equals(NODE_SEQUENCE_NAME) || nodeLocalName.equals(NODE_GROUP_NAME) || nodeLocalName.equals(NODE_ANY_NAME)) {
              ParticleImpl particle = SchemaStructuresLoader.createParticle(node, schema);
							SchemaStructuresLoader.loadBase(particle);
							addParticle(particle);  
            } else if(nodeLocalName.equals(NODE_ANNOTATION_NAME)) {
              annotation = SchemaStructuresLoader.createAnnotation(node, schema);
							SchemaStructuresLoader.loadBase(annotation);
            }
          }
        }
      }
      calculateEffectiveTotalRange();
    }
  }
  
  protected void calculateEffectiveTotalRange() {
    if(compositor == CHOICE) {
      calculateEffectiveTotalRange_Choice();
    } else {
      calculateEffectiveTotalRange_SequenceAndAll();
    }
  }
  
  private void calculateEffectiveTotalRange_Choice() {
    minimumEffectiveTotalRange = productOfParticleMinOccurs * minimumOfMinimumEffectiveTotalRanges;
    if(!unboundedMaximumEffectiveTotalRange) {
      maximumEffectiveTotalRange = productOfParticleMaxOccurs * maximumOfMaximumEffectiveTotalRanges;
    }
  }
  
  private void calculateEffectiveTotalRange_SequenceAndAll() {
    minimumEffectiveTotalRange = productOfParticleMinOccurs * sumOfMinimumEffectiveTotalRanges;
    if(!unboundedMaximumEffectiveTotalRange) {
      maximumEffectiveTotalRange = productOfParticleMaxOccurs * sumOfMaximumEffectiveTotalRanges;
    }
  }
  
  protected void addParticle(ParticleImpl particle) {
    particles.add(particle);
    
    if(compositor == CHOICE) {
      calculateComponentsForEffectiveTotalRange_Choice(particle);
    } else {
      calculateComponentsForEffectiveTotalRange_SequenceAndAll(particle);
    }
  }
  
  private void calculateComponentsForEffectiveTotalRange_Choice(ParticleImpl particle) {
    calculateComponentsForMinimumEffectiveTotalRange_Choice(particle);
    calculateComponentsForMaximumEffectiveTotalRange_Choice(particle);
  }
  
  private void calculateComponentsForEffectiveTotalRange_SequenceAndAll(ParticleImpl particle) {
    calculateComponentsForMinimumEffectiveTotalRange_SequenceAndAll(particle);
    calculateComponentsForMaximumEffectiveTotalRange_SequenceAndAll(particle);
  }
  
  private void calculateComponentsForMinimumEffectiveTotalRange_Choice(ParticleImpl particle) {
    productOfParticleMinOccurs *= particle.minOccurs;
    int minEffectiveTotalRange = determineMinEffectiveTotalRange(particle);
    if(minimumOfMinimumEffectiveTotalRanges > minEffectiveTotalRange) {
      minimumOfMinimumEffectiveTotalRanges = minEffectiveTotalRange;
    }
  }
  
  private int determineMinEffectiveTotalRange(ParticleImpl particle) {
    if(particle.term instanceof ModelGroupDefinitionImpl) {
      return(((ModelGroupDefinitionImpl)particle.term).modelGroup.getMinimumEffectiveTotalRange());
    } else if(particle.term instanceof ModelGroupImpl) {
      return(((ModelGroupImpl)particle.term).getMinimumEffectiveTotalRange());
    } 
    return(particle.minOccurs);
  }
  
  private int determineMaxEffectiveTotalRange(ParticleImpl particle) {
    if(particle.term instanceof ModelGroupDefinitionImpl) {
      return(((ModelGroupDefinitionImpl)particle.term).modelGroup.getMaximumEffectiveTotalRange());
    } else if(particle.term instanceof ModelGroupImpl) {
      return(((ModelGroupImpl)particle.term).getMaximumEffectiveTotalRange());
    } 
    return(particle.maxOccurs);
  }

  private boolean isUnboundedMaximumEffectiveTotalRange(ParticleImpl particle) {
    if(particle.term instanceof ModelGroupDefinitionImpl) {
      return(((ModelGroupDefinitionImpl)particle.term).modelGroup.isUnboundedMaximumEffectiveTotalRange());
    } else if(particle.term instanceof ModelGroupImpl) {
      return(((ModelGroupImpl)particle.term).isUnboundedMaximumEffectiveTotalRange());
    } 
    return(particle.isMaxOccursUnbounded());
  }
  
  private void calculateComponentsForMaximumEffectiveTotalRange_Choice(ParticleImpl particle) {
    if(isUnboundedMaximumEffectiveTotalRange(particle)) {
      unboundedMaximumEffectiveTotalRange = true;
    }
    if(!unboundedMaximumEffectiveTotalRange) {
      productOfParticleMaxOccurs *= particle.maxOccurs;
      int maxEffectiveTotalRange = determineMaxEffectiveTotalRange(particle);
      if(maximumOfMaximumEffectiveTotalRanges < maxEffectiveTotalRange) {
        maximumOfMaximumEffectiveTotalRanges = maxEffectiveTotalRange;
      }
    }
  }
  
  private void calculateComponentsForMinimumEffectiveTotalRange_SequenceAndAll(ParticleImpl particle) {
    productOfParticleMinOccurs *= particle.minOccurs;
    sumOfMinimumEffectiveTotalRanges += determineMinEffectiveTotalRange(particle);
  }
  
  private void calculateComponentsForMaximumEffectiveTotalRange_SequenceAndAll(ParticleImpl particle) {
    if(isUnboundedMaximumEffectiveTotalRange(particle)) {
      unboundedMaximumEffectiveTotalRange = true;
    }
    if(!unboundedMaximumEffectiveTotalRange) {
      productOfParticleMaxOccurs *= particle.maxOccurs;
      sumOfMaximumEffectiveTotalRanges += determineMaxEffectiveTotalRange(particle); 
    }
  }
  
  protected BaseImpl initializeBase(BaseImpl base, Hashtable clonedCollector) {
    ModelGroupImpl result = (ModelGroupImpl)(super.initializeBase(base, clonedCollector));
    result.compositor = compositor;
    result.minimumEffectiveTotalRange = minimumEffectiveTotalRange;
    result.maximumEffectiveTotalRange = maximumEffectiveTotalRange;
    result.unboundedMaximumEffectiveTotalRange = unboundedMaximumEffectiveTotalRange;
    Tools.cloneVectorWithBases(particles, result.particles, clonedCollector);
    return(result);
  }
  
  
}

