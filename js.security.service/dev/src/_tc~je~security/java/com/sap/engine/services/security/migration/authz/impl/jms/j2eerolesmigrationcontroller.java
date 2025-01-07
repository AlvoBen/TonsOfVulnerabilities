package com.sap.engine.services.security.migration.authz.impl.jms;

import com.sap.engine.services.security.migration.authz.J2EERolesToUMERolesMigrationController;

public class J2EERolesMigrationController extends J2EERolesToUMERolesMigrationController {
  private String instanceName = null;
  private final static String PREFIX = "JMS.";

  public J2EERolesMigrationController(String instanceName, String path) {
    super(path);
    this.instanceName = instanceName;
  }

  public String getPolicyConfiguration() {
    return PREFIX + instanceName;
  }

}
