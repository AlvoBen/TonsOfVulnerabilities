package com.sap.engine.lib.schema.validator.automat;

import com.sap.engine.lib.schema.components.ModelGroup;
import com.sap.engine.lib.schema.components.Wildcard;

public final class ChoiceAutomat extends GroupAutomat {

  protected ChoiceAutomat(ModelGroup modelGroup) {
    super(modelGroup);
  }

  public Switch switchState(String uri, String name) {
    Switch switchResult = null;
    if(isUntouched) {
      AutomatState switchToWildcardState = null;
      Switch wildcradSwitchResult = null;
      for(int i = 0; i < particleAutomats.size(); i++) {
        ParticleAutomat particleAutomat = (ParticleAutomat)(particleAutomats.get(i));
        switchResult = particleAutomat.switchState(uri, name);
        if(switchResult != null) {
          switchingAutomatIndex = i;
          if(switchResult.base instanceof Wildcard) {
            if(switchToWildcardState == null) {
              switchToWildcardState = getState();
              wildcradSwitchResult = switchResult;
              particleAutomat.reset();
            }
          } else {
            switchToWildcardState = null;
            break;
          }
        }
      }
      if(switchToWildcardState != null) {
        setState(switchToWildcardState);
        switchResult = wildcradSwitchResult;
      }
    } else {
      ParticleAutomat switchingAutomat = determineSwitchingAutomat();
      switchResult = switchingAutomat.switchState(uri, name); 
    }
    if(switchResult != null) {
      isUntouched = false;
    }
    return(switchResult);
  }

  protected boolean isSatisfied() {
    if(particleAutomats.size() == 0) {
      return(true);
    }
    if(isUntouched) {
      for(int i = 0; i < particleAutomats.size(); i++) {
        ParticleAutomat particleAutomat = (ParticleAutomat)(particleAutomats.get(i));
        if(particleAutomat.isSatisfied()) {
          return(true);
        }
      }
      return(false);
    }
    ParticleAutomat switchingAutomat = determineSwitchingAutomat();
    return(switchingAutomat.isSatisfied());
  }
  
  protected boolean isDrained() {
    if(particleAutomats.size() == 0) {
      return(true);
    }
    if(isUntouched) {
      return(false);
    }
    ParticleAutomat switchingautomat = determineSwitchingAutomat();
    return(switchingautomat.isDrained());
  }
  
  protected void initToStringBuffer_GroupCompositor(StringBuffer toStringBuffer) {
    toStringBuffer.append("Choice");
  }
  
  protected void initExpectedBuffer(StringBuffer expectedBuffer) {
    if(isUntouched) {
      initExpectedBuffer_Untouched(expectedBuffer);
    } else {
      ParticleAutomat switchingAutomat = determineSwitchingAutomat();
      switchingAutomat.initExpectedBuffer(expectedBuffer);
    }
  }

  protected void initExpectedBuffer_Untouched(StringBuffer expectedBuffer) {
    for(int i = 0; i < particleAutomats.size(); i++) {
      ParticleAutomat particleAutomat = (ParticleAutomat)(particleAutomats.get(i));
      particleAutomat.initExpectedBuffer(expectedBuffer);
    }
  }
  
  protected void initExpectedBuffer_Partialy(StringBuffer expectedBuffer, int automatIndex) {
    initExpectedBuffer_Untouched(expectedBuffer);
  }
}