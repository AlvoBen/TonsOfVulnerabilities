package com.sap.engine.services.security.restriction;

import java.security.Permission;

import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.interfaces.security.SecurityResourcePermission;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.security.api.IUser;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.tc.logging.Severity;

public class Restrictions {

  public static final int USER_MANAGEMENT = 0;
  public static final int CRYPTOGRAPHY_PROVIDERS = 1;
  public static final int COMPONENT_SECURITY_ROLES = 2;
  public static final int COMPONENT_RESOURCE_MANAGEMENT = 3;
  public static final int COMPONENT_PROTECTION_DOMAINS = 4;
  public static final int COMPONENT_AUTHENTICATION = 5;

  public static final int RESTRICTION_CREATE_ACCOUNT = 0;
  public static final int RESTRICTION_REMOVE_ACCOUNT = 1;
  public static final int RESTRICTION_READ_ATTRIBUTE = 2;
  public static final int RESTRICTION_WRITE_ATTRIBUTE = 3;
  public static final int RESTRICTION_CHANGE_CONFIGURATION = 4;
  public static final int RESTRICTION_GROUP_ACCOUNT = 5;
  public static final int RESTRICTION_READ_CREDENTIALS = 6;
  public static final int RESTRICTION_REMOVE_CREDENTIALS = 7;

  public static final int RESTRICTION_CHANGE_PROVIDERS = 0;

  public static final int RESTRICTION_ADD_SECURITY_ROLE = 0;
  public static final int RESTRICTION_MODIFY_SECURITY_ROLE = 1;
  public static final int RESTRICTION_REMOVE_SECURITY_ROLE = 2;

  public static final int RESTRICTION_ADD_RESOURCE = 0;
  public static final int RESTRICTION_MODIFY_RESOURCE = 1;
  public static final int RESTRICTION_MODIFY_SECURITY_OF_RESOURCE = 2;
  public static final int RESTRICTION_REMOVE_RESOURCE = 3;
  public static final int RESTRICTION_RENAME_RESOURCE = 4;

  public static final int RESTRICTION_GRANT_PERMISSION = 0;
  public static final int RESTRICTION_DENY_PERMISSION = 1;

  public static final int RESTRICTION_SET_AUTHENTICATION_USER_STORE = 0;
  public static final int RESTRICTION_SET_LOGIN_MODULES = 1;
  public static final int RESTRICTION_SET_PROPERTY = 2;
  public static final int RESTRICTION_UPDATE = 3;
  public static final int RESTRICTION_SET_HELPER = 4;

  public static final void checkPermission(int component, int restriction) throws SecurityException {
    checkPermission(component, restriction, SecurityResourcePermission.INSTANCE_ALL);
  }

  public static final void checkPermission(int component, int restriction, String instance) throws SecurityException {
    if (SecurityServerFrame.threadContext.getThreadContext() == null) {
      return;  // this is a system call
    }

    try {
      SecurityContextObject sco = (SecurityContextObject) SecurityServerFrame.threadContext.getThreadContext().getContextObject(SecurityContextObject.NAME);
      String userName = sco.getSession().getPrincipal().getName();
      IUser iuser = UMFactory.getUserFactory().getUserByUniqueName(userName);
      Permission perm = getPermission(component, restriction, instance);
      
      if (perm == null) {
         return;
      }
      
      if (!iuser.hasPermission("security", perm)) {
        throw new SecurityException("User does not have permission for the security operation!");
      } 
    } catch (UMException e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "checkPermission", e);
    }
  }

  public static final void checkSystemPermission() {
    if (SecurityServerFrame.threadContext.getThreadContext() != null) {
      throw new SecurityException("Operation is not allowed in application threads!");
    }
  } 
  
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////PRIVATE METHODS///////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  
  private static Permission getPermission(int component, int restriction, String instance) {
    switch (component) {
      case USER_MANAGEMENT: {
      
        return new UMPermission(UMPermission.NAME, getUMAction(restriction), instance);
      } 
      case CRYPTOGRAPHY_PROVIDERS: {
      
        return new CryptoPermission(CryptoPermission.NAME, getCryptoAction(restriction), instance);  
      }
      case COMPONENT_PROTECTION_DOMAINS: {
        
        return new DomainsPermission(DomainsPermission.NAME, getDomainsAction(restriction), instance);
      }
      case COMPONENT_AUTHENTICATION: {
        
        return new AuthPermission(AuthPermission.NAME, getAuthAction(restriction), instance);
      }
      default: {
        return null;
      }
    }
  }
  
  private static String getAuthAction(int restriction) {
    switch (restriction) {
      case RESTRICTION_SET_AUTHENTICATION_USER_STORE : {
        return AuthPermission.RESTRICTION_SET_AUTHENTICATION_USER_STORE;
      }
      case RESTRICTION_SET_LOGIN_MODULES : {
        return AuthPermission.RESTRICTION_SET_LOGIN_MODULES;
      }
      case RESTRICTION_SET_PROPERTY : {
        return AuthPermission.RESTRICTION_SET_PROPERTY;
      }
      case RESTRICTION_UPDATE : {
        return AuthPermission.RESTRICTION_UPDATE;
      }
      case RESTRICTION_SET_HELPER : {
        return AuthPermission.RESTRICTION_SET_HELPER;
      }
      default : {
        return AuthPermission.ACTION_ALL;
      }
    }
  }
  
  private static String getDomainsAction(int restriction) {
    switch (restriction) {
      case RESTRICTION_GRANT_PERMISSION : {
        return DomainsPermission.RESTRICTION_GRANT_PERMISSION;
      }
      case RESTRICTION_DENY_PERMISSION : {
        return DomainsPermission.RESTRICTION_DENY_PERMISSION;
      }     
      default : {
        return DomainsPermission.ACTION_ALL;
      }
    }
  }  
  
  private static String getCryptoAction(int restriction) {
    switch (restriction) {
      case RESTRICTION_CHANGE_PROVIDERS : {
        return CryptoPermission.RESTRICTION_CHANGE_PROVIDERS;
      } 
      default : {
        return CryptoPermission.ACTION_ALL;
      }
    }
  }
  
  private static String getUMAction(int restriction) {
    switch (restriction) {
      case RESTRICTION_CREATE_ACCOUNT : {
        return UMPermission.RESTRICTION_CREATE_ACCOUNT;
      }
      case RESTRICTION_REMOVE_ACCOUNT : {
        return UMPermission.RESTRICTION_REMOVE_ACCOUNT;
      }
      case RESTRICTION_READ_ATTRIBUTE : {
        return UMPermission.RESTRICTION_READ_ATTRIBUTE;
      }
      case RESTRICTION_WRITE_ATTRIBUTE : {
        return UMPermission.RESTRICTION_WRITE_ATTRIBUTE;
      }
      case RESTRICTION_CHANGE_CONFIGURATION : {
        return UMPermission.RESTRICTION_CHANGE_CONFIGURATION;
      }
      case RESTRICTION_GROUP_ACCOUNT : {
        return UMPermission.RESTRICTION_GROUP_ACCOUNT;
      }
      case RESTRICTION_READ_CREDENTIALS : {
        return UMPermission.RESTRICTION_READ_CREDENTIALS;
      }
      case RESTRICTION_REMOVE_CREDENTIALS : {
        return UMPermission.RESTRICTION_REMOVE_CREDENTIALS;
      }     
      default : {
        return UMPermission.ACTION_ALL;
      }
    }
  }
}