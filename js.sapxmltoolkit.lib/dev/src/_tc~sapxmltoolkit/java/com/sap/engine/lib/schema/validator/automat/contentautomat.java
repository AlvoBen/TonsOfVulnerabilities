package com.sap.engine.lib.schema.validator.automat;

import com.sap.engine.lib.schema.components.ComplexTypeDefinition;
import com.sap.engine.lib.schema.components.Particle;

public final class ContentAutomat {

  private ParticleAutomat partcileAutomat;

  public ContentAutomat(ComplexTypeDefinition complTypeDefinition) {
    Particle contentParticle = complTypeDefinition.getContentTypeContentModel();
    if(contentParticle != null) {
      partcileAutomat = new ParticleAutomat(contentParticle);
    }
  }

  public Switch switchState(String uri, String name) {
    return(partcileAutomat == null ? null : partcileAutomat.switchState(uri, name));
  }

  public boolean isUntouched() {
    return(partcileAutomat == null || partcileAutomat.isUntouched());
  }

  public boolean isSatisfied() {
    return(partcileAutomat == null || partcileAutomat.isSatisfied());
  }

  public void reset() {
    if(partcileAutomat != null) {
      partcileAutomat.reset();
    }
  }

  public String toString() {
  	return(partcileAutomat == null ? "empty" : partcileAutomat.toString());
  }
  
  public String getExpected() {
    if(partcileAutomat != null) {
      StringBuffer expectedBuffer = new StringBuffer();
      initExpectedBuffer(expectedBuffer);
      return(expectedBuffer.toString());
    }
    return("");
  }
  
  private void initExpectedBuffer(StringBuffer expectedBuffer) {
    expectedBuffer.append("[");
    partcileAutomat.initExpectedBuffer(expectedBuffer);
    expectedBuffer.append("]");
  }
}