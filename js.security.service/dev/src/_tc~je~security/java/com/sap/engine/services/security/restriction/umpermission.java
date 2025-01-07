package com.sap.engine.services.security.restriction;

import com.sap.engine.interfaces.security.SecurityResourcePermission;

public class UMPermission extends SecurityResourcePermission {
  public static final String NAME = "security.um";
  
  public static final String RESTRICTION_CREATE_ACCOUNT = "create_account";
  public static final String RESTRICTION_REMOVE_ACCOUNT = "remove_account";
  public static final String RESTRICTION_READ_ATTRIBUTE = "read_attribute";
  public static final String RESTRICTION_WRITE_ATTRIBUTE = "write_attribute";
  public static final String RESTRICTION_CHANGE_CONFIGURATION = "change_configuration";
  public static final String RESTRICTION_GROUP_ACCOUNT = "group_account";
  public static final String RESTRICTION_READ_CREDENTIALS = "view_credentials";
  public static final String RESTRICTION_REMOVE_CREDENTIALS = "remove_credentials";

  public UMPermission(String name, String action, String instance) {
    super(name, action, instance);
  }

  public UMPermission(String name, String actions) {
    super(name, actions);
  }

}
