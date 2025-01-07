package com.sap.engine.interfaces.security;

public interface JACCContext {
  
  public JACCUpdateContext getUpdateContext();
  
  public JACCUndeployContext getUndeployContext();
  
  public JACCMigrationContext getMigrationContext();
}
