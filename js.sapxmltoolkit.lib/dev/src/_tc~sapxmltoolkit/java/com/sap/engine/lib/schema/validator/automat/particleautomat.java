package com.sap.engine.lib.schema.validator.automat;

import com.sap.engine.lib.schema.components.*;

public final class ParticleAutomat extends Automat {

  private int minOccurs;
  private int maxOccurs;
  private boolean isUnbounded;
  private Automat automat;
  private int occurrenceCounter;
  private boolean incremetOccurrenceCounter;

  protected ParticleAutomat(Particle particle) {
    super();
    incremetOccurrenceCounter = true;
    occurrenceCounter = 0;
    minOccurs = particle.getMinOccurs();
    maxOccurs = particle.getMaxOccurs();
    isUnbounded = particle.isMaxOccursUnbounded();
    initAutomat(particle);
  }
  
  private void initAutomat(Particle particle) {
    Base term = particle.getTerm();
    if(term instanceof ElementDeclaration) {
      automat = new ElementAutomat((ElementDeclaration)term);
    } else if(term instanceof Wildcard) {
      automat = new WildcardAutomat((Wildcard)term);
    } else if(term instanceof ModelGroup) {
      automat = determineGroupAutomat((ModelGroup)term);
    } else {
      automat = determineGroupAutomat((ModelGroupDefinition)term);
    }
  }

  private GroupAutomat determineGroupAutomat(ModelGroup modelGroup) {
    if(modelGroup.isCompositorAll()) {
      return(new AllAutomat(modelGroup));
    }
    if(modelGroup.isCompositorChoice()) {
      return(new ChoiceAutomat(modelGroup));
    }
    return(new SequenceAutomat(modelGroup));
  }
  
  private GroupAutomat determineGroupAutomat(ModelGroupDefinition modelGroupDef) {
    ModelGroup modelGroup = modelGroupDef.getModelGroup();
    return(determineGroupAutomat(modelGroup));
  }

  protected Switch switchState(String uri, String name) {
    if(isDrained()) {
      return(null);
    }
    return((automat instanceof BaseAutomat) ? switchState_BaseAutomat(uri, name) : switchState_GroupAutomat(uri, name));
  }

  private Switch switchState_BaseAutomat(String uri, String name) {
    Switch switchResult = automat.switchState(uri, name);
    if(switchResult != null) {
      isUntouched = false;
      occurrenceCounter++;
    }
    return(switchResult);
  }
  
  private Switch switchState_GroupAutomat(String uri, String name) {
    Switch switchResult = automat.switchState(uri, name);
    boolean automatIsSatisfied = automat.isSatisfied(); 
    if(!isOccurrenceDrained() && automatIsSatisfied && (switchResult == null || switchResult.base instanceof Wildcard)) {
      AutomatState presentAutomatState = getState();
      automat.reset();
      Switch newSwitchResult = automat.switchState(uri, name);
      if((switchResult == null && newSwitchResult == null) || 
         (switchResult != null && switchResult.base instanceof Wildcard && (newSwitchResult == null || newSwitchResult.base instanceof Wildcard))) {
        setState(presentAutomatState);
      } else {
        automatIsSatisfied = automat.isSatisfied();
        switchResult = newSwitchResult;
      }
    }
    if(switchResult != null) {
      isUntouched = false;
      if(incremetOccurrenceCounter && automatIsSatisfied) {
        occurrenceCounter++;
        incremetOccurrenceCounter = false;
      }
    }
    return(switchResult);
  }
  
  protected boolean isSatisfied() {
    boolean automatIsUntouched = automat.isUntouched();
    boolean automatIsSatisfied = automat.isSatisfied();
    return((automatIsUntouched && automatIsSatisfied) ||
           (minOccurs == 0 && isUntouched) ||
           (occurrenceCounter >= minOccurs && automatIsSatisfied));
  }
  
  protected boolean isDrained() {
    return(maxOccurs == 0 || (isOccurrenceDrained() && automat.isDrained()));
  }
  
  private boolean isOccurrenceDrained() {
    return(occurrenceCounter >= maxOccurs && !isUnbounded);
  }
  
  protected void reset() {
    super.reset();
    automat.reset();
    incremetOccurrenceCounter = true;
    occurrenceCounter = 0;
  }

  protected AutomatState createState() {
    return(new ParticleAutomatState());
  }
  
  protected void initState(AutomatState state) {
    super.initState(state);
    ParticleAutomatState particleAutomatState = (ParticleAutomatState)state;
    particleAutomatState.occurrenceCounter = occurrenceCounter;
    particleAutomatState.incremetOccurrenceCounter = incremetOccurrenceCounter;
    particleAutomatState.automatState = automat.getState();
  }

  protected void setState(AutomatState state) {
    super.setState(state);
    ParticleAutomatState particleAutomatState = (ParticleAutomatState)state;
    occurrenceCounter = particleAutomatState.occurrenceCounter;
    incremetOccurrenceCounter = particleAutomatState.incremetOccurrenceCounter;
    automat.setState(particleAutomatState.automatState);
  }
  
  protected void initToStringBuffer(StringBuffer toStringBuffer, String offset) {
    toStringBuffer.append(offset);
    toStringBuffer.append("Particle ");
    initToStringBuffer_Occurs(toStringBuffer, "minOccurs=", minOccurs);
    toStringBuffer.append(" ");
    initToStringBuffer_Occurs(toStringBuffer, "maxOccurs=", maxOccurs);
    toStringBuffer.append(" ");
    initToStringBuffer_IsUnbounded(toStringBuffer);
    toStringBuffer.append("\n");
    initToStringBuffer_Automat(toStringBuffer, offset + TO_STRING_OFFSET);
  }
  
  protected void initToStringBuffer_Occurs(StringBuffer toStringBuffer, String occursId, int occursValue) {
    toStringBuffer.append(occursId);
    toStringBuffer.append(occursValue);
  }
  
  protected void initToStringBuffer_IsUnbounded(StringBuffer toStringBuffer) {
    toStringBuffer.append("isUnbounded=");
    toStringBuffer.append(isUnbounded);
  }
  
  protected void initToStringBuffer_Automat(StringBuffer toStringBuffer, String offset) {
    automat.initToStringBuffer(toStringBuffer, offset);
  }
  
  protected void initExpectedBuffer(StringBuffer expectedBuffer) {
    if(!isDrained()) {
      if(automat instanceof BaseAutomat) {
        automat.initExpectedBuffer(expectedBuffer);
      } else {
        initExpectedBuffer_GroupAutomat(expectedBuffer);
      }
    }
  }
  
  private void initExpectedBuffer_GroupAutomat(StringBuffer expectedBuffer) {
    if(automat.isDrained()) {
      initExpectedBuffer_GroupAutomat_ResetAutomat(expectedBuffer, false);
    } else {
      automat.initExpectedBuffer(expectedBuffer);
      if(!automat.isUntouched() && automat.isSatisfied()) {
        initExpectedBuffer_GroupAutomat_ResetAutomat(expectedBuffer, true);
      }
    }
  }
  
  private void initExpectedBuffer_GroupAutomat_ResetAutomat(StringBuffer expectedBuffer, boolean processGroupToTheCurrentAutomatIndex) {
    if(!isOccurrenceDrained()) {
      GroupAutomatState automatState = (GroupAutomatState)(automat.getState());
      automat.reset();
      if(processGroupToTheCurrentAutomatIndex) {
        int currentAutomatIndex = automatState.switchingAutomatIndex;
        ((GroupAutomat)automat).initExpectedBuffer_Partialy(expectedBuffer, currentAutomatIndex);
      } else {
        automat.initExpectedBuffer(expectedBuffer);
      }
      automat.setState(automatState);
    }
  }
}