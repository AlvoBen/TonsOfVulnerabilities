package com.sap.engine.services.security.migration.authz.impl.keystore;

import java.util.Hashtable;

import com.sap.engine.services.security.migration.SecurityMigrationController;
import com.sap.engine.services.security.migration.authz.ResourceMigrationController;

public class KeystoreViewMigrationController implements SecurityMigrationController {
  private String name = null;
  private String path = null;
  
  J2EERolesMigrationController rolesController = null;
  
  ResourceMigrationController entriesController  = null;
  ResourceMigrationController propertyController = null;
  ResourceMigrationController viewController     = null;
  
  public KeystoreViewMigrationController(String name, String path) {
    this.name = name;
    this.path = path;
    
    rolesController = new J2EERolesMigrationController(name, path);
    
    entriesController  = new EntryResourceMigrationController(name, path);
    propertyController = new PropertyResourceMigrationController(name, path);
    viewController     = new ViewResourceMigrationController(name, path);
  } 

  public void migrate() throws Exception {
    rolesController.migrate();
    Hashtable mappings = rolesController.getResultMappings();
  
    entriesController.init(mappings);
    entriesController.migrate();
    
    propertyController.init(mappings);
    propertyController.migrate();
    
    viewController.init(mappings);
    viewController.migrate();
  }


  public boolean needsMigration() {
    return true;
  }

}
