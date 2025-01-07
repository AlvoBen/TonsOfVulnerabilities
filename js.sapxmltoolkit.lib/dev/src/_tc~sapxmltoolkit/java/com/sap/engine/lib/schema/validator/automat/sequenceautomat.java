package com.sap.engine.lib.schema.validator.automat;

import com.sap.engine.lib.schema.components.ModelGroup;
import com.sap.engine.lib.schema.components.Wildcard;

public final class SequenceAutomat extends GroupAutomat {

  protected SequenceAutomat(ModelGroup modelGroup) {
    super(modelGroup);
    switchingAutomatIndex = 0;
  }

  protected Switch switchState(String uri, String name) {
    if(particleAutomats.size() == 0) {
      return(null);
    }
    ParticleAutomat switchingAutomat = determineSwitchingAutomat();
    Switch switchResult = switchingAutomat.switchState(uri, name);
    boolean switchingAutomatIsSatisfied = switchingAutomat.isSatisfied();
    if(switchingAutomatIsSatisfied && switchingAutomatIndex < particleAutomats.size() - 1 && (switchResult == null || switchResult.base instanceof Wildcard)) {
      AutomatState presentState = getState();
      switchingAutomat.reset();
      switchingAutomatIndex++;
      Switch newSwitchResult = switchState(uri, name);
      if((switchResult == null && newSwitchResult == null) || 
         (switchResult != null && switchResult.base instanceof Wildcard && (newSwitchResult == null || newSwitchResult.base instanceof Wildcard))) {
        setState(presentState);
      } else {
        switchResult = newSwitchResult;
      }
    }
    if(switchResult != null) {
      isUntouched = false;
    }
    return(switchResult);
  }
  
  protected boolean isSatisfied() {
    for(int i = switchingAutomatIndex; i < particleAutomats.size(); i++) {
      ParticleAutomat particleAutomat = (ParticleAutomat)(particleAutomats.get(i));
      if(!particleAutomat.isSatisfied()) {
        return(false);
      }
    }
    return(true);
  }
  
  protected boolean isDrained() {
    if(particleAutomats.size() == 0) {
      return(true);
    }
    return(switchingAutomatIndex == particleAutomats.size() - 1 && determineSwitchingAutomat().isDrained());
  }
  
  protected void initToStringBuffer_GroupCompositor(StringBuffer toStringBuffer) {
    toStringBuffer.append("Sequence");
  }
  
  protected void initExpectedBuffer(StringBuffer expectedBuffer) {
    initExpectedBuffer(expectedBuffer, particleAutomats.size());
  }
  
  protected void initExpectedBuffer(StringBuffer expectedBuffer, int limitation) {
    for(int i = switchingAutomatIndex; i < limitation; i++) {
      ParticleAutomat particleAutomat = (ParticleAutomat)(particleAutomats.get(i));
      particleAutomat.initExpectedBuffer(expectedBuffer);
      if(!particleAutomat.isSatisfied()) {
        break;
      }
    }
  }
  
  protected void initExpectedBuffer_Partialy(StringBuffer expectedBuffer, int automatIndex) {
    initExpectedBuffer(expectedBuffer, automatIndex);
  }
}