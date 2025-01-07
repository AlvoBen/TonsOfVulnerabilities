package com.sap.engine.lib.schema.validator.automat;

public abstract class BaseAutomat extends Automat {

  protected Switch switchResult;
  
  protected BaseAutomat() {
    super();
    switchResult = new Switch();
  }

  protected boolean isSatisfied() {
    return(!isUntouched);
  }
  
  protected boolean isDrained() {
    return(isSatisfied());
  }
}