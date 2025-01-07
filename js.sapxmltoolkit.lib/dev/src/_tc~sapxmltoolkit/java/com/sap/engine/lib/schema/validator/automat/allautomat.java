/*
 * Created on 2004-10-26
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.schema.validator.automat;

import com.sap.engine.lib.schema.components.ModelGroup;
import com.sap.engine.lib.schema.validator.ReusableObjectsPool;

import java.util.Vector;

public final class AllAutomat extends GroupAutomat {

  private Vector privateParticleAutomats;
  private ModelGroup modelGroupAll;

  protected AllAutomat(ModelGroup modelGroup) {
  	super(modelGroup);
  	modelGroupAll = modelGroup;
    privateParticleAutomats = new Vector(particleAutomats);
  }

  public Switch switchState(String uri, String name) {
  	for(int i = 0; i < privateParticleAutomats.size(); i++) {
      ParticleAutomat particleAutomat = (ParticleAutomat)(privateParticleAutomats.get(i));
      Switch switchResult = particleAutomat.switchState(uri, name);
      if(switchResult != null) {
        isUntouched = false;
        particleAutomat.reset();
        privateParticleAutomats.removeElementAt(i);
    	switchResult.scopeModelGroupAll = modelGroupAll;
    	return(switchResult);
      }
  	}
  	return(null);
  }

  protected void reset() {
    super.reset();
    privateParticleAutomats.clear();
    privateParticleAutomats.addAll(particleAutomats);
  }

  protected boolean isSatisfied() {
    for(int i = 0; i < privateParticleAutomats.size(); i++) {
      ParticleAutomat particleAutomat = (ParticleAutomat)(privateParticleAutomats.get(i));
      if(!particleAutomat.isSatisfied()) {
        return(false);
      }
    }
    return(true);
  }
  
  protected boolean isDrained() {
    return(privateParticleAutomats.size() == 0);
  }

  protected AutomatState createState() {
    return(new AllAutomatState());
  }
  
  protected void initState(AutomatState state) {
    super.initState(state);
    AllAutomatState allAutomatState = (AllAutomatState)state;
    allAutomatState.privateParticleAutomats = new Vector(privateParticleAutomats);
  }
  
  protected void setState(AutomatState state) {
    super.setState(state);
    AllAutomatState allAutomatState = (AllAutomatState)state;
    privateParticleAutomats.clear();
    privateParticleAutomats.addAll(allAutomatState.privateParticleAutomats);
  }
  
  protected void initToStringBuffer_GroupCompositor(StringBuffer toStringBuffer) {
    toStringBuffer.append("All");
  }
  
  protected void initExpectedBuffer(StringBuffer expectedBuffer) {
    for(int i = 0; i < privateParticleAutomats.size(); i++) {
      ParticleAutomat particleAutomat = (ParticleAutomat)(privateParticleAutomats.get(i));
      particleAutomat.initExpectedBuffer(expectedBuffer);
    }
  }
  
  protected void initExpectedBuffer_Partialy(StringBuffer expectedBuffer, int automatIndex) {
    initExpectedBuffer(expectedBuffer);
  }
}
