package com.sap.engine.services.security.migration;

public interface SecurityMigrationController {
  
  public void migrate() throws Exception;

  public boolean needsMigration();
}
