package com.sap.engine.services.security.restriction;

import com.sap.engine.interfaces.security.SecurityResourcePermission;

public class DomainsPermission extends SecurityResourcePermission {
  
  public static final String NAME = "security.domains";
  
  public static final String RESTRICTION_GRANT_PERMISSION = "grant_permission";
  public static final String RESTRICTION_DENY_PERMISSION = "deny_permission";
  
  public DomainsPermission(String name, String action, String instance) {
    super(name, action, instance);
  }

  public DomainsPermission(String name, String actions) {
    super(name, actions);
  }

}
