package com.sap.engine.interfaces.security;

import com.sap.engine.frame.core.configuration.Configuration;

public interface JACCMigrationContext {
  
  public void migratePolicyConfiguration(Configuration config) throws SecurityException;

  public void migratePolicyConfiguration(String applicationName, String moduleName) throws SecurityException;
}
