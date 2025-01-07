/*
 * Created on 2005-6-7
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.schema.validator.identity;

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IdentityConstraintRegResult {
  
  private boolean registraitionFail;
  private boolean valueIsRegistered;
  
  public IdentityConstraintRegResult() {
    init();
  }
  
  private void init() {
    registraitionFail = false;
    valueIsRegistered = false;
  }
  
  protected void setRegistrationFail(boolean registraitionFail) {
    this.registraitionFail = registraitionFail;
  }
  
  protected void setValueIsRegistered(boolean valueIsRegistered) {
    this.valueIsRegistered = valueIsRegistered;
  }
  
  public boolean registraitionFail() {
    return(registraitionFail);
  }
  
  public boolean valueIsRegistered() {
    return(valueIsRegistered);
  }
  
  public void reuse() {
    init();
  }
}
