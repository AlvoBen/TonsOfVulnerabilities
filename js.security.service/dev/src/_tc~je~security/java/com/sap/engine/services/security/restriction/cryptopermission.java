package com.sap.engine.services.security.restriction;

import com.sap.engine.interfaces.security.SecurityResourcePermission;

public class CryptoPermission extends SecurityResourcePermission {
  public static final String NAME = "security.crypto";
  
  public static final String RESTRICTION_CHANGE_PROVIDERS = "change_providers";
  
  public CryptoPermission(String name, String action, String instance) {
    super(name, action, instance);
  }

  public CryptoPermission(String name, String actions) {
    super(name, actions);
  }

}
