package com.sap.engine.lib.schema.validator.automat;

import com.sap.engine.lib.schema.components.ModelGroup;
import com.sap.engine.lib.schema.components.Particle;

import java.util.Vector;

public abstract class GroupAutomat extends Automat {

  protected Vector particleAutomats;
  protected int switchingAutomatIndex;

  protected GroupAutomat(ModelGroup modelGroup) {
    super();
    initParicleAutomats(modelGroup);
  }
  
  private void initParicleAutomats(ModelGroup modelGroup) {
    particleAutomats = new Vector();
    Vector particles = modelGroup.getParticles();
    for(int i = 0; i < particles.size(); i++) {
      Particle particle = (Particle)(particles.get(i));
      ParticleAutomat particleAutomat = new ParticleAutomat(particle);   
      particleAutomats.add(particleAutomat);
    }
  }

  protected AutomatState createState() {
    return(new GroupAutomatState());
  }
  
  protected void initState(AutomatState state) {
    super.initState(state);
    GroupAutomatState groupAutomatState = (GroupAutomatState)state;
    groupAutomatState.switchingAutomatIndex = switchingAutomatIndex;
    ParticleAutomat switchingAutomat = determineSwitchingAutomat();
    groupAutomatState.switchingAutomatState = switchingAutomat == null ? null : switchingAutomat.getState(); 
  }

  protected void setState(AutomatState state) {
    super.setState(state);
    GroupAutomatState groupAutomatState = (GroupAutomatState)state;
    switchingAutomatIndex = groupAutomatState.switchingAutomatIndex;
    ParticleAutomat switchingAutomat = determineSwitchingAutomat();
    if(switchingAutomat != null) {
      switchingAutomat.setState(groupAutomatState.switchingAutomatState);
    }
  }

  protected void reset() {
    if(!isUntouched) {
      super.reset();
      ParticleAutomat switchingAutomat = determineSwitchingAutomat();
      if(switchingAutomat != null) {
        switchingAutomat.reset();
      }
      switchingAutomatIndex = 0;
    }
  }

  protected ParticleAutomat determineSwitchingAutomat() {
    if(particleAutomats.size() != 0) {
      ParticleAutomat switchingAutomat = (ParticleAutomat)(particleAutomats.get(switchingAutomatIndex));
      return(switchingAutomat);
    }
    return(null);
  }
  
  protected void initToStringBuffer(StringBuffer toStringBuffer, String offset) {
    toStringBuffer.append(offset);
    initToStringBuffer_GroupCompositor(toStringBuffer);
    initToStringBuffer_Automats(toStringBuffer, offset + TO_STRING_OFFSET);
  }
  
  protected void initToStringBuffer_Automats(StringBuffer toStringBuffer, String offset) {
    for(int i = 0; i < particleAutomats.size(); i++) {
      toStringBuffer.append("\n");
      ParticleAutomat automat = (ParticleAutomat)(particleAutomats.get(i));
      automat.initToStringBuffer(toStringBuffer, offset);
    }
  }
  
  protected abstract void initToStringBuffer_GroupCompositor(StringBuffer toStringBuffer);
  
  protected abstract void initExpectedBuffer_Partialy(StringBuffer expectedBuffer, int automatIndex);
}
