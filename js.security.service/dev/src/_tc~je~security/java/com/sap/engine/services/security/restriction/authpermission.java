package com.sap.engine.services.security.restriction;

import com.sap.engine.interfaces.security.SecurityResourcePermission;

public class AuthPermission extends SecurityResourcePermission {
  public static final String NAME = "security.auth";
  
  public static final String RESTRICTION_SET_AUTHENTICATION_USER_STORE = "set_authentication_user_store";
  public static final String RESTRICTION_SET_LOGIN_MODULES = "set_login_modules";
  public static final String RESTRICTION_SET_PROPERTY = "set_authentication_property";
  public static final String RESTRICTION_UPDATE = "update_authentication";
  public static final String RESTRICTION_SET_HELPER = "set_login_module_helper";
  
  public AuthPermission(String name, String action, String instance) {
    super(name, action, instance);
  }
  
  public AuthPermission(String name, String actions) {
    super(name, actions);
  }

}
