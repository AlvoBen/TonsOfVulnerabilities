/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.security.core.server.ume.service.jacc;

import com.sap.engine.lib.logging.LoggingHelper;

import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 *
 *	@author Diana Berberova
 *	@version 7.10
 * 
 */
public class JaccPolicyConfigurationsLog {
  public static final String UNEXPECTED_EXCEPTION_MESSAGE = "\nWarning: Unexpected exception thrown.";
  public static final String UNEXPECTED_EXCEPTION_SET_PERMISSIONS_MESSAGE = "\nWarning: Unexpected exception thrown during setting permissions for action.";
  public static final String UNEXPECTED_EXCEPTION_DELETE_CONFIGURATION_MESSAGE = "\nWarning: Unexpected exception thrown during deletion of policy configuration: ";
  public static final String UNEXPECTED_EXCEPTION_COMMIT_CONFIGURATION_MESSAGE = "\nWarning: Unexpected exception thrown during commit of policy configuration: ";
  public static final String UNEXPECTED_EXCEPTION_MIGRATE_CONFIGURATION_MESSAGE = "\nWarning: Unexpected exception thrown during migrating of policy configuration. Policy configuration is not migrated: ";
  public static final String UNEXPECTED_EXCEPTION_GET_UME_ACTIONS_MESSAGE = "\nWarning: Unexpected exception thrown during collection of UME actions for policy configuration: ";
  public static final String UNEXPECTED_EXCEPTION_MESSAGE_AFTER_COMMIT_OF_ACTIONS = "\nWarning: Unexpected exception during commit of policy configurations, but after commit of actions has been completed. Possible inconsistency.";

  public static final String MIGRATING_ROLE_ASSIGNED_SAP_PERMISSIONS_MESSAGE = "\n\nOk: Migrating role assigned SAP permissions";
  public static final String MIGRATING_UNCHECKED_SAP_PERMISSIONS_MESSAGE = "\n\nOk: Migrating unchecked SAP permissions";
  public static final String MIGRATING_EXCLUDED_SAP_PERMISSIONS_MESSAGE = "\n\nOk: Migrating excluded SAP permissions";
  public static final String MIGRATING_ROLE_ASSIGNED_JACC_PERMISSIONS_MESSAGE = "\n\nOk: Migrating role assigned JACC permissions";
  public static final String MIGRATING_UNCHECKED_JACC_PERMISSIONS_MESSAGE = "\n\nOk: Migrating unchecked JACC permissions";
  public static final String MIGRATING_EXCLUDED_JACC_PERMISSIONS_MESSAGE = "\n\nOk: Migrating excluded JACC permissions";
  
  public static final String NO_MIGRATION_PERFORMED = "\nOk: No migration performed for policy configuration: ";
  public static final String MIGRATION_PERFORMED = "\nOk: Migration performed for policy configuration: ";
  public static final String STANDARD_MIGRATION_PERFORMED = "\nOk: Standard migration performed for policy configuration: ";
  public static final String NARROW_MIGRATION_PERFORMED = "\nOk: Narrow migration performed for policy configuration: ";
  public static final String PERMISSIONS_MIGRATION_PERFORMED = "\nOk: Permissions migarted successfully for policy configuration: ";
 
  public static final String POLICY_CONFIGURATION_REMOVED = "\nOk: Policy configuration is removed:";
  public static final String POLICY_CONFIGURATION_NOT_REMOVED = "\nOk: Policy configuration is not removed:";
  public static final String POLICY_CONFIGURATION_COMMITTED = "\nOk: Policy configuration is committed:"; 
  public static final String ACTION_COMMIT_MESSAGE = "\nOk: Action is committed: ";
  public static final String ACTION_ROLLBACK_MESSAGE = "\nOk: Action is rollbacked: ";
  
  public static final String POLICY_CONFIGURATION_WITHOUT_JACC_PERMISSIONS_MESSAGE = "\nOk: Policy Configuration is not migrated as there are no jacc permissions with non standard http methods: ";
  public static final String POLICY_CONFIGURATION_NOT_JACC_CONFIGURATION_MESSAGE = "\nOk: Policy Configuration is not jacc policy configuration: ";
  public static final String SEARCH_ACTIONS_FOR_POLICY_CONFIGURATION_MESSAGE = "\n\n\nOk: Search actions for policy configuration: ";
  public static final String MIGRATION_INTERRUPTED_MESSAGE = "\nWarning: Migration interrupted for policy configuration: ";
  public static final String POLICY_CONFIGURATION_MESSAGE= "Ok: Policy configuration: ";
  
  public static final String FOUND_EXCLUDED_SAP_RESOURCE_PERMISSIONS_MESSAGE = "\nOk: Found excluded SAP Resource permissions ";
  public static final String FOUND_UNCHECKED_SAP_RESOURCE_PERMISSIONS_MESSAGE = "\nOk: Found unchecked SAP Resource permissions ";
  public static final String FOUND_ROLE_ASSIGNED_SAP_RESOURCE_PERMISSIONS_MESSAGE = "\nOk: Found role assigned SAP Resource permissions for role: ";
  public static final String FOUND_EXCLUDED_JACC_RESOURCE_PERMISSIONS_MESSAGE = "\nOk: Found excluded JACC Resource permissions ";
  public static final String FOUND_UNCHECKED_JACC_RESOURCE_PERMISSIONS_MESSAGE = "\nOk: Found unchecked JACC Resource permissions ";
  public static final String FOUND_ROLE_ASSIGNED_JACC_RESOURCE_PERMISSIONS_MESSAGE = "\nOk: Found role assigned JACC Resource permissions for role: ";

  public static final String FOUND_EXCLUDED_SAP_USERDATA_PERMISSIONS_MESSAGE = "\nOk: Found excluded SAP UserData permissions ";
  public static final String FOUND_UNCHECKED_SAP_USERDATA_PERMISSIONS_MESSAGE = "\nOk: Found unchecked SAP UserData permissions ";
  public static final String FOUND_ROLE_ASSIGNED_SAP_USERDATA_PERMISSIONS_MESSAGE = "\nOk: Found role assigned SAP UserData permissions for role: ";
  public static final String FOUND_EXCLUDED_JACC_USERDATA_PERMISSIONS_MESSAGE = "\nOk: Found excluded JACC UserData permissions ";
  public static final String FOUND_UNCHECKED_JACC_USERDATA_PERMISSIONS_MESSAGE = "\nOk: Found unchecked JACC UserData permissions ";
  public static final String FOUND_ROLE_ASSIGNED_JACC_USERDATA_PERMISSIONS_MESSAGE = "\nOk: Found role assigned JACC UserData permissions for role: ";
  
  public static final String FOUND_J2EE_ROLE_ACTIONS_MESSAGE = "\nOk: Found j2ee role actions: ";
  public static final String FOUND_UNCHECKED_ACTIONS_MESSAGE = "\nOk: Found unchecked actions: ";
  public static final String FOUND_EXCLUDED_ACTIONS_MESSAGE = "\nOk: Found excluded actions: ";
  public static final String PERMISSIONS_TO_BE_MIGRATED_MESSAGE = "\nOk: Permissions that will be migrated \n";
  
  private static Location location = Location.getLocation(JaccPermissionManager.class);
  private static Category category = Category.getCategory(LoggingHelper.SYS_SECURITY, "JACC");

  public static void logWarning(String message, Throwable e) {
    if (category.beWarning()) {
      category.logThrowableT(Severity.WARNING, location, message, e);
    }
  }
  
  public static void logWarning(String message) {
    if (category.beWarning()) {
      category.logT(Severity.WARNING, location, message);
    }
  }
  
  public static void logInfo(String message) {
    if (category.beInfo()) {
      category.logT(Severity.INFO, location, message);
    }
  }
  
  public static void trace(String message) {
    if (location.beDebug()) {
      location.debugT(message);
    }    
  }
  
  public static void traceThrowable(Throwable t) {
    if (location.beDebug()) {
      location.traceThrowableT(Severity.DEBUG, t.getLocalizedMessage(), t);      
    }
  }
  
  public static void traceThrowable(String message, Throwable t) {
    if (location.beDebug()) {
     location.traceThrowableT(Severity.DEBUG, message, t);
    } 
  }

}
