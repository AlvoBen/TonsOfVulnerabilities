package com.sap.engine.services.security.migration.authz.impl.keystore;

import com.sap.engine.services.security.migration.authz.J2EERolesToUMERolesMigrationController;

public class J2EERolesMigrationController extends J2EERolesToUMERolesMigrationController {
  private String keystore = null;
  private final static String PREFIX = "keystore-view.";

  public J2EERolesMigrationController(String keystore, String path) {
    super(path);
    this.keystore = keystore;
  }

  public String getPolicyConfiguration() {
    return PREFIX + keystore;
  }
}
