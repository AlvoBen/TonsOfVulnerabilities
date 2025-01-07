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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.security.jacc.PolicyConfiguration;
import javax.security.jacc.PolicyConfigurationFactory;
import javax.security.jacc.WebResourcePermission;
import javax.security.jacc.WebUserDataPermission;

import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.role.IAction;
import com.sap.security.core.role.IActionFactory;
import com.sap.security.core.role.IPermission;
import com.sap.security.core.role.PermissionData;

/**
 *
 *	@author Diana Berberova
 *	@version 7.10
 * 
 */
public class JaccPermissionManager {

  private IActionFactory factory;
  
  static HashSet extensionStrings;

  static final String NEGATIVE_STRING = "-";
  static final String POSITIVE_STRING = "+";
  static final String EXCLAMATION_MARK_STRING = "!";
  static final String EMPTY_STRING = "";
  static final String METHOD_SEPARATOR_STRING = ",";
  static final String DUMMY_METHOD_NEGATIVE_STRING = "-_DUMMY_NON_STD_ _NULL_CANNOT_BE_PASSED_";
  static final String DUMMY_METHOD_NEGATIVE_STRING_INTEGRAL = "-_DUMMY_NON_STD_ _NULL_CANNOT_BE_PASSED_:INTEGRAL";
  static final String DUMMY_METHOD_NEGATIVE_STRING_CONFIDENTIAL = "-_DUMMY_NON_STD_ _NULL_CANNOT_BE_PASSED_:CONFIDENTIAL";
  static final String ALL_METHODS_NEGATIVE_STRING = "-_HTTP_ _METHODS_ _ALL_";
  static final String ALL_METHODS_NEGATIVE_STRING_INTEGRAL = "-_HTTP_ _METHODS_ _ALL_:INTEGRAL";
  static final String ALL_METHODS_NEGATIVE_STRING_CONFIDENTIAL = "-_HTTP_ _METHODS_ _ALL_:CONFIDENTIAL";
  static final String ALL_METHODS_POSITIVE_STRING = "+_HTTP_ _METHODS_ _ALL_";
  static final String ALL_METHODS_POSITIVE_STRING_INTEGRAL = "+_HTTP_ _METHODS_ _ALL_:INTEGRAL";
  static final String ALL_METHODS_POSITIVE_STRING_CONFIDENTIAL = "+_HTTP_ _METHODS_ _ALL_:CONFIDENTIAL";
  static final String ALL_STANDARD_METHODS_NEGATIVE_STRING = "!GET,HEAD,POST,PUT,OPTIONS,TRACE,DELETE";
  static final String ALL_STANDARD_METHODS_NEGATIVE_STRING_CONFIDENTIAL = "!GET,HEAD,POST,PUT,OPTIONS,TRACE,DELETE:CONFIDENTIAL";
  static final String ALL_STANDARD_METHODS_NEGATIVE_STRING_INTEGRAL = "!GET,HEAD,POST,PUT,OPTIONS,TRACE,DELETE:INTEGRAL";
  static final String ALL_STANDARD_METHODS_POSITIVE_STRING = "GET,HEAD,POST,PUT,OPTIONS,TRACE,DELETE";
  static final String ALL_STANDARD_METHODS_POSITIVE_STRING_CONFIDENTIAL = "GET,HEAD,POST,PUT,OPTIONS,TRACE,DELETE:CONFIDENTIAL";
  static final String ALL_STANDARD_METHODS_POSITIVE_STRING_INTEGRAL = "GET,HEAD,POST,PUT,OPTIONS,TRACE,DELETE:INTEGRAL";
  static final String EXTERNAL_STRING = "-EXTERNAL";
  static final String TRANSPORT_GUARANTEE_CONFIDENTIAL = ":CONFIDENTIAL";
  static final String TRANSPORT_GUARANTEE_INTEGRAL = ":INTEGRAL";
  
  static final String WEB_RESOURCE_PERMISSION_CLASSPATH = "javax.security.jacc.WebResourcePermission";
  static final String WEB_USER_DATA_PERMISSION_CLASSPATH = "javax.security.jacc.WebUserDataPermission";
  static final String SAP_WEB_RESOURCE_PERMISSION_CLASSPATH = "com.sap.engine.interfaces.security.SAPWebResourcePermission";
  static final String SAP_WEB_USER_DATA_PERMISSION_CLASSPATH = "com.sap.engine.interfaces.security.SAPWebUserDataPermission";

//  static boolean traceOn = true;

  public JaccPermissionManager () {
 
    factory = InternalUMFactory.getActionFactory();
    extensionStrings = new HashSet( Arrays.asList(new String[] {ALL_METHODS_NEGATIVE_STRING,
        ALL_METHODS_POSITIVE_STRING, ALL_METHODS_POSITIVE_STRING_CONFIDENTIAL, 
        ALL_METHODS_NEGATIVE_STRING_CONFIDENTIAL, ALL_METHODS_NEGATIVE_STRING_INTEGRAL,
        ALL_METHODS_POSITIVE_STRING_INTEGRAL, DUMMY_METHOD_NEGATIVE_STRING, 
        DUMMY_METHOD_NEGATIVE_STRING_CONFIDENTIAL, DUMMY_METHOD_NEGATIVE_STRING_INTEGRAL}));
  }
  
  public void migratePolicyConfiguration(String policyConfigurationId) {
    
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.POLICY_CONFIGURATION_MESSAGE + policyConfigurationId);
	
    try {
    
      JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.SEARCH_ACTIONS_FOR_POLICY_CONFIGURATION_MESSAGE + policyConfigurationId);

      String[] props = com.sap.security.core.role.imp.ActionFactory.splitContextID(policyConfigurationId);
      
      String providerName = props[0];
      String applicationName = props[1];
      String moduleName = props[2];
      
      
      if ((providerName == null) || (applicationName == null) || (moduleName == null)) {
        JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.POLICY_CONFIGURATION_NOT_JACC_CONFIGURATION_MESSAGE + policyConfigurationId);
      } else {
        String[] roleActions = null;
        String[] excludedActions = null;
        String[] uncheckedActions = null;
        String[] roleActionsExternal = null;
        String[] excludedActionsExternal = null;
        String[] uncheckedActionsExternal = null;
        
        String moduleNameExternal = moduleName + EXTERNAL_STRING;
        
        try {
          
	        roleActionsExternal = factory.searchActions(providerName, applicationName, moduleNameExternal, IAction.TYPE_J2EE_ROLE, null);
	        excludedActionsExternal = factory.searchActions(providerName, applicationName, moduleNameExternal, IAction.TYPE_EXCLUDED, null);
	        uncheckedActionsExternal = factory.searchActions(providerName, applicationName, moduleNameExternal, IAction.TYPE_UNCHECKED, null);

	        int externalActionsCount = roleActionsExternal.length + excludedActionsExternal.length + uncheckedActionsExternal.length;
	        
	        try {
		        if (externalActionsCount > 0) {
		          
							roleActions = factory.searchActions(providerName, applicationName, moduleName, IAction.TYPE_J2EE_ROLE, null);
			        excludedActions = factory.searchActions(providerName, applicationName, moduleName, IAction.TYPE_EXCLUDED, null);
			        uncheckedActions = factory.searchActions(providerName, applicationName, moduleName, IAction.TYPE_UNCHECKED, null);
			        
		          String[] roleActionsAll = new String[roleActions.length + roleActionsExternal.length];
		          String[] excludedActionsAll = new String[excludedActions.length + excludedActionsExternal.length];
		          String[] uncheckedActionsAll = new String[uncheckedActions.length + uncheckedActionsExternal.length];
		          
		          System.arraycopy(roleActions, 0, roleActionsAll, 0, roleActions.length);
		          System.arraycopy(roleActionsExternal, 0, roleActionsAll, roleActions.length, roleActionsExternal.length);
		          System.arraycopy(excludedActions, 0, excludedActionsAll, 0, excludedActions.length);
		          System.arraycopy(excludedActionsExternal, 0, excludedActionsAll, excludedActions.length, excludedActionsExternal.length);
		          System.arraycopy(uncheckedActions, 0, uncheckedActionsAll, 0, uncheckedActions.length);
		          System.arraycopy(uncheckedActionsExternal, 0, uncheckedActionsAll, uncheckedActions.length, uncheckedActionsExternal.length);
	
		          
		          JaccPolicyConfiguration jaccConfiguration = 
		            new JaccPolicyConfiguration(policyConfigurationId, roleActionsAll, excludedActionsAll, uncheckedActionsAll);
	
		          jaccConfiguration.init();
		          migrateJaccConfiguration(jaccConfiguration);
		        } else {
		          JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.POLICY_CONFIGURATION_WITHOUT_JACC_PERMISSIONS_MESSAGE + policyConfigurationId);
		        }
	        } catch (Exception e) {
	          JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.MIGRATION_INTERRUPTED_MESSAGE + policyConfigurationId);
	        }
	        
        } catch (Exception e) {
          JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.MIGRATION_INTERRUPTED_MESSAGE + policyConfigurationId);
          JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_GET_UME_ACTIONS_MESSAGE + policyConfigurationId, e);
         
        }

        
      }	
    } catch (Exception e) {
      JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_MESSAGE, e);
    }   
	     
	}

  private void migrateJaccConfiguration(JaccPolicyConfiguration jaccConfiguration) {
  
    PolicyConfiguration policyConfiguration = null;
    String configurationId = jaccConfiguration.getPolicyConfigurationId();
    String additionalConfigurationId = configurationId + EXTERNAL_STRING;
  
    if(!jaccConfiguration.needsMigration()) {
      try {
	      //opens the policy configuration for edit
        policyConfiguration = PolicyConfigurationFactory.getPolicyConfigurationFactory().getPolicyConfiguration(configurationId, false);
        
//      migrates diff 
        addNeededExcludedPermissions(jaccConfiguration, policyConfiguration);
        addNeededUncheckedPermissions(jaccConfiguration, policyConfiguration);
        addNeededRoleAssignedPermissions(jaccConfiguration, policyConfiguration);
 
		    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.PERMISSIONS_MIGRATION_PERFORMED + configurationId);

        try {
			    //commit changes in policy configuration 
		      policyConfiguration.commit();
		      JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.POLICY_CONFIGURATION_COMMITTED + configurationId);
	
		      try {
		        //delete additional policy configuration
		        PolicyConfiguration additionalPolicyConfiguration = PolicyConfigurationFactory.getPolicyConfigurationFactory().getPolicyConfiguration(additionalConfigurationId, true);
		        additionalPolicyConfiguration.delete();
		
		        JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.POLICY_CONFIGURATION_REMOVED + additionalConfigurationId);

		        JaccPolicyConfigurationsLog.logInfo(JaccPolicyConfigurationsLog.MIGRATION_PERFORMED + configurationId);
				    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.NARROW_MIGRATION_PERFORMED + configurationId);
			      
	        } catch (Exception e) {

	          JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_DELETE_CONFIGURATION_MESSAGE + additionalConfigurationId, e);
	          
	        }
	        
        } catch (Exception e) {
          
          JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_COMMIT_CONFIGURATION_MESSAGE + configurationId, e);
          JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.POLICY_CONFIGURATION_NOT_REMOVED + additionalConfigurationId, e);

        }
        
        
      } catch (Exception e) {
        
        JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_MIGRATE_CONFIGURATION_MESSAGE + configurationId, e);
      }
        

    } else {
     
       boolean actionsMigrated = false;
      
	    try {
	      //remove the jacc permissions from actions
	      HashMap actionsToPermissions = jaccConfiguration.getActionsWithNewPermissionSet();
	      if (actionsToPermissions.size() > 0) {
	        migrateActionsWithNewPermissionSet(actionsToPermissions);
	        actionsMigrated = true;
	      }
	      
	      
	      //opens the policy configuration for edit
	      policyConfiguration = PolicyConfigurationFactory.getPolicyConfigurationFactory().getPolicyConfiguration(configurationId, false);
	      
	      migrateExcludedJACCPermissions(jaccConfiguration, policyConfiguration);
		    
	      migrateUncheckedJACCPermissions(jaccConfiguration, policyConfiguration);
		    
	      migrateRoleAssignedJACCPermissions(jaccConfiguration, policyConfiguration);
		    
	      migrateExcludedSAPPermissions(jaccConfiguration, policyConfiguration);

		    migrateUncheckedSAPPermissions(jaccConfiguration, policyConfiguration);

		    migrateRoleAssignedSAPPermissions(jaccConfiguration, policyConfiguration);
		    
		    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.PERMISSIONS_MIGRATION_PERFORMED + configurationId);

		    try {
			    //commit changes in policy configuration 
		      policyConfiguration.commit();
		      JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.POLICY_CONFIGURATION_COMMITTED + configurationId);
	
			    try {
				    //remove non standard policy configuration
			      PolicyConfiguration additionalPolicyConfiguration = PolicyConfigurationFactory.getPolicyConfigurationFactory().getPolicyConfiguration(additionalConfigurationId, true);
				    additionalPolicyConfiguration.delete();
				    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.POLICY_CONFIGURATION_REMOVED + additionalConfigurationId);

				    JaccPolicyConfigurationsLog.logInfo(JaccPolicyConfigurationsLog.MIGRATION_PERFORMED + configurationId);
				    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.STANDARD_MIGRATION_PERFORMED + configurationId);
			    } catch (Exception e) {
			      
			      JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_DELETE_CONFIGURATION_MESSAGE + additionalConfigurationId, e);

			    }
		    } catch (Exception e) {

		      JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_COMMIT_CONFIGURATION_MESSAGE + configurationId, e);
		      JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.POLICY_CONFIGURATION_NOT_REMOVED + additionalConfigurationId, e);
		    
		    }
	    } catch (Exception e) {
	      JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_MIGRATE_CONFIGURATION_MESSAGE + configurationId, e);
	      if(actionsMigrated) {
	        JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_MESSAGE_AFTER_COMMIT_OF_ACTIONS);
	      }
	    }    
    }
  }
  
  private void addNeededExcludedPermissions(JaccPolicyConfiguration jaccConfiguration, PolicyConfiguration policyConfiguration) {
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.MIGRATING_EXCLUDED_SAP_PERMISSIONS_MESSAGE);

    try {
      ArrayList excludedSapUserDataPermissions = jaccConfiguration.getExcludedSAPUserDataPermissions();
      Iterator userDataPermissionsIterator = excludedSapUserDataPermissions.iterator();
	    
      while (userDataPermissionsIterator.hasNext()) {
        IPermission perm = (IPermission) userDataPermissionsIterator.next();
        String actions = perm.getValue();
        String url = perm.getName();
        String newActions = getNeededActionsFromSapPermissionActions(actions);
        
        JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from excluded policy : SAPWebUserDataPermission(" + url + ", " + actions + ")");
	      
        if (newActions == null) {
          JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is not valid: " + perm.getClassName() + "(" + url + "," + actions + ")");
        } else {
	     
          policyConfiguration.addToExcludedPolicy(new WebUserDataPermission(url, newActions));
          JaccPolicyConfigurationsLog.trace("Permission is added to excluded policy : WebUserDataPermission(" + url + ", " + newActions + ")");
        }	    
      }    
	    
      
	    // migrates the excluded SAPWebResourcePermision objects 
	  	
      ArrayList excludedSapResourcePermissions = jaccConfiguration.getExcludedSAPResourcePermissions();
	  	Iterator resourcePermissionsIterator = excludedSapResourcePermissions.iterator();
	    
	  	while (resourcePermissionsIterator.hasNext()) {
        IPermission perm = (IPermission) resourcePermissionsIterator.next();
        String actions = perm.getValue();
        String url = perm.getName();
        String newActions = getNeededActionsFromSapPermissionActions(actions);
        
        JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from excluded policy : SAPWebResourcePermission(" + url + ", " + actions + ")");
	      
        if (newActions == null) {
          JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is not valid: " + perm.getClassName() + "(" + url + "," + actions + ")");
        } else {
	     
          policyConfiguration.addToExcludedPolicy(new WebResourcePermission(url, newActions));
          JaccPolicyConfigurationsLog.trace("Permission is added to excluded policy : WebResourcePermission(" + url + ", " + newActions + ")");
        }
      }
	    
    } catch (Exception e) {
      JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_MESSAGE, e);
 
    }    
  }
   
  private void addNeededUncheckedPermissions(JaccPolicyConfiguration jaccConfiguration, PolicyConfiguration policyConfiguration) {
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.MIGRATING_UNCHECKED_SAP_PERMISSIONS_MESSAGE);
    
    try {
	    // migrates the unchecked SAPWebUserDataPermision objects 

      ArrayList uncheckedSapUserDataPermissions = jaccConfiguration.getUncheckedSAPUserDataPermissions();
      Iterator userDataPermissionsIterator = uncheckedSapUserDataPermissions.iterator();
      while (userDataPermissionsIterator.hasNext()) {
        IPermission perm = (IPermission) userDataPermissionsIterator.next();
        String actions = perm.getValue();
        String url = perm.getName();
        String newActions = getNeededActionsFromSapPermissionActions(actions);
        
        JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from unchecked policy : SAPWebUserDataPermission(" + url + ", " + actions + ")");
	      
        policyConfiguration.addToUncheckedPolicy(new WebUserDataPermission(url, newActions));
        JaccPolicyConfigurationsLog.trace("Permission is added to unchecked policy : WebUserDataPermission(" + url + ", " + newActions + ")");
 		     
      }    

	    // migrates the excluded SAPWebResourcePermision objects 

	    HashMap roleAssignedResourceMethods = jaccConfiguration.getRoleAssignedResourceMethods();

	    ArrayList uncheckedSapResourcePermissions = jaccConfiguration.getUncheckedSAPResourcePermissions();
	    
	    Iterator permissionsIterator = uncheckedSapResourcePermissions.iterator();
	    while (permissionsIterator.hasNext()) {
        IPermission perm = (IPermission) permissionsIterator.next();
        String url = perm.getName();
        String actions = perm.getValue();
        String newActions = getNeededActionsFromSapPermissionActions(actions);
        
        JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from unchecked policy : SAPWebResourcePermission(" + url + ", " + actions + ")");
	   	        
        policyConfiguration.addToUncheckedPolicy(new WebResourcePermission(url, newActions));
        JaccPolicyConfigurationsLog.trace("Permission is added to unchecked policy : WebResourcePermission(" + url + ", " + newActions + ")");
	      
	    }
	    
    } catch (Exception e) {
      JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_MESSAGE, e);
   
    }    
  }

  private void addNeededRoleAssignedPermissions(JaccPolicyConfiguration jaccConfiguration, PolicyConfiguration policyConfiguration) {
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.MIGRATING_ROLE_ASSIGNED_SAP_PERMISSIONS_MESSAGE);
    
    try {
	    // migrates the excluded SAPWebUserDataPermision objects 

      HashMap roleAssignedSapUserDataPermissions = jaccConfiguration.getRoleSAPUserDataPermissions();
      Iterator roleAssignedSAPUserDataKeys = roleAssignedSapUserDataPermissions.keySet().iterator();

      while (roleAssignedSAPUserDataKeys.hasNext()) {
	      String roleKey = (String)roleAssignedSAPUserDataKeys.next();
	      ArrayList roleAssignedUserDataPermissions = (ArrayList) roleAssignedSapUserDataPermissions.get(roleKey);
	      Iterator permissionsIterator = roleAssignedUserDataPermissions.iterator();
	      while (permissionsIterator.hasNext()) {
	        IPermission perm = (IPermission) permissionsIterator.next();
	        String actions = perm.getValue();
	        String url = perm.getName();
	        String newActions = getNeededActionsFromSapPermissionActions(actions);
	        
	        JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from role " + roleKey +  ": SAPWebUserDataPermission(" + url + ", " + actions + ")");
		      
	        if (newActions == null) {
	          JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is not valid:  " + perm.getClassName() + "(" + url + "," + actions + ")");

	        } else {		     

	          policyConfiguration.addToRole(roleKey, new WebUserDataPermission(url, newActions));
	          JaccPolicyConfigurationsLog.trace("Permission is added to role " + roleKey +  ": WebUserDataPermission(" + url + ", " + newActions + ")");

	        }
	      }
	    }    
	    
	    
      // migrates the excluded SAPWebResourcePermision objects 
     
      HashMap roleAssignedSapResourcePermissions = jaccConfiguration.getRoleSAPResourcePermissions();
      Iterator roleAssignedSAPResourceKeys = roleAssignedSapResourcePermissions.keySet().iterator();
      while (roleAssignedSAPResourceKeys.hasNext()) {
	      String roleKey = (String)roleAssignedSAPResourceKeys.next();
	      ArrayList roleAssignedResourcePermissions = (ArrayList) roleAssignedSapResourcePermissions.get(roleKey);
	      Iterator permissionsIterator = roleAssignedResourcePermissions.iterator();
	      while (permissionsIterator.hasNext()) {
	        IPermission perm = (IPermission) permissionsIterator.next();
	        String actions = perm.getValue();
	        String url = perm.getName();
	        String newActions = getNeededActionsFromSapPermissionActions(actions);
	        
	        JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from role " + roleKey +  ": SAPWebResourcePermission(" + url + ", " + actions + ")");
	
	        
	        if (actions == null) {
	          JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is not valid: " + perm.getClassName() + "(" + url + "," + actions + ")");
	        } else {
		        
	          policyConfiguration.addToRole(roleKey, new WebResourcePermission(url, newActions));
	          JaccPolicyConfigurationsLog.trace("Permission is added to role " + roleKey +  ": WebResourcePermission(" + url + ", " + newActions + ")");

	        }
	      }
	    }
	    
    } catch (Exception e) {
      JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_MESSAGE, e);
    }    
  }

  
  
  private void migrateExcludedJACCPermissions(JaccPolicyConfiguration jaccConfiguration, PolicyConfiguration policyConfiguration) {
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.MIGRATING_EXCLUDED_JACC_PERMISSIONS_MESSAGE);
        
    try {
	    Iterator excludedUserDataPermissions = jaccConfiguration.getExcludedJACCUserDataPermissions();
	   
	    while(excludedUserDataPermissions.hasNext()) {
	      IPermission perm = (IPermission)excludedUserDataPermissions.next();
	      String url = perm.getName();
	      String actions = perm.getValue();
	      String newActions = getActionsFromJaccPermissionActions(actions);

	      JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from excluded policy : WebUserDataPermission(" + url + ", " + actions + ")");
	      
	      if (newActions == null) {
	        JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is not valid: " + perm.getClassName() + "(" + url + "," + actions + ")");
	      } else {
		      
		      policyConfiguration.addToExcludedPolicy(new WebUserDataPermission(url, newActions));
	        JaccPolicyConfigurationsLog.trace("Permission is added to excluded policy : WebUserDataPermission(" + url + ", " + newActions + ")");

	      }
	    }
	    

	    Iterator excludedResourcePermissions = jaccConfiguration.getExcludedJACCResourcePermissions();

	    while(excludedResourcePermissions.hasNext()) {
	      IPermission perm = (IPermission)excludedResourcePermissions.next();
	      String url = perm.getName();
	      String actions = perm.getValue();
	      String newActions = getActionsFromJaccPermissionActions(actions);
	      
	      JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from excluded policy : WebResourcePermission(" + url + ", " + actions + ")");
	      
	      if (newActions == null) {
	        JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is not valid: " + perm.getClassName() + "(" + url + "," + actions + ")");
	      } else {

		      policyConfiguration.addToExcludedPolicy(new WebResourcePermission(url, newActions));
		      JaccPolicyConfigurationsLog.trace("Permission is added to excluded policy : WebResourcePermission(" + url + ", " + ALL_STANDARD_METHODS_POSITIVE_STRING + ")");
		      
	      }
	    }
    } catch (Exception e) {
      JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_MESSAGE, e);
    }    
  }
  
  private void migrateUncheckedJACCPermissions(JaccPolicyConfiguration jaccConfiguration, PolicyConfiguration policyConfiguration) {
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.MIGRATING_UNCHECKED_JACC_PERMISSIONS_MESSAGE);

    try {
	    Iterator uncheckedUserDataPermissions = jaccConfiguration.getUncheckedJACCUserDataPermissions();
	   
	    while(uncheckedUserDataPermissions.hasNext()) {
	      IPermission perm = (IPermission)uncheckedUserDataPermissions.next();
	      String url = perm.getName();
	      String actions = perm.getValue();
	      String newActions = getActionsFromJaccPermissionActions(actions);
	      
	      JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from unchecked policy : WebUserDataPermission(" + url + ", " + actions + ")");

	      
	      if (newActions == null) {
	        JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is not valid: " + perm.getClassName() + "(" + url + "," + actions + ")");
	      } else {
		      
	     		policyConfiguration.addToUncheckedPolicy(new WebUserDataPermission(url, newActions));
	     		JaccPolicyConfigurationsLog.trace("Permission is added to unchecked policy : WebUserDataPermission(" + url + ", " + newActions + ")");

		    }
	    }
	    

	    Iterator uncheckedResourcePermissions = jaccConfiguration.getUncheckedJACCResourcePermissions();

	    while(uncheckedResourcePermissions.hasNext()) {
	      IPermission perm = (IPermission)uncheckedResourcePermissions.next();
	      String url = perm.getName();
	      String actions = perm.getValue();
	      String newActions = getActionsFromJaccPermissionActions(actions);
		      
	      JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from unchecked policy : WebResourcePermission(" + url + ", " + actions + ")");


	      if (newActions == null) {
	        JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is not valid: " + perm.getClassName() + "(" + url + "," + actions + ")");
	      } else {

	        policyConfiguration.addToUncheckedPolicy(new WebResourcePermission(url, newActions));
	        JaccPolicyConfigurationsLog.trace("Permission is added to unchecked policy : WebResourcePermission(" + url + ", " + ALL_STANDARD_METHODS_POSITIVE_STRING + ")");
	      }
	    }
    } catch (Exception e) {
      JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_MESSAGE, e);
            
    }    
  }
  
  private void migrateRoleAssignedJACCPermissions(JaccPolicyConfiguration jaccConfiguration, PolicyConfiguration policyConfiguration) {
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.MIGRATING_ROLE_ASSIGNED_JACC_PERMISSIONS_MESSAGE);
    
    try {
      HashMap userDataPermissions = jaccConfiguration.getRoleJACCUserDataPermissions();
      Iterator userDataPermissionRoles = userDataPermissions.keySet().iterator();
     
      while (userDataPermissionRoles.hasNext()) {
        String roleName = (String) userDataPermissionRoles.next();
        Iterator roleUserDataPermissions = ((ArrayList) userDataPermissions.get(roleName)).iterator();
       
        while(roleUserDataPermissions.hasNext()) {
  	      IPermission perm = (IPermission)roleUserDataPermissions.next();
  	      String url = perm.getName();
  	      String actions = perm.getValue();
  	      String newActions = getActionsFromJaccPermissionActions(actions);
  	      
  	      JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from role " + roleName + " : WebUserDataPermission(" + url + ", " + actions + ")");
   	      
  	      
  	      if (newActions == null) {
  	        JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is not valid: " + perm.getClassName() + "(" + url + "," + actions + ")");
   	      } else {  		      
  	 	      policyConfiguration.addToRole(roleName, new WebUserDataPermission(url, newActions));
  	 	      JaccPolicyConfigurationsLog.trace("Permission is added to role: " + roleName + " : WebUserDataPermission(" + url + ", " + newActions + ")");
   	      }
   	    }
      }
      
        
      HashMap resourcePermissions = jaccConfiguration.getRoleJACCResourcePermissions();
      Iterator resourcePermissionRoles = resourcePermissions.keySet().iterator();
      
      while (resourcePermissionRoles.hasNext()) {
        String roleName = (String) resourcePermissionRoles.next();
        Iterator roleResourcePermissions = ((ArrayList) resourcePermissions.get(roleName)).iterator();
       
        while(roleResourcePermissions.hasNext()) {
		      IPermission perm = (IPermission)roleResourcePermissions.next();
		      String url = perm.getName();
		      String actions = perm.getValue();
  	      String newActions = getActionsFromJaccPermissionActions(actions);
		      
  	      JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from role " + roleName + " : WebResourcePermission(" + url + ", " + actions + ")");
	
  	      if (newActions == null) {
  	        JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is not valid: " + perm.getClassName() + "(" + url + "," + actions + ")");
  	      } else {

  	        policyConfiguration.addToRole(roleName, new WebResourcePermission(url, newActions));
  	        JaccPolicyConfigurationsLog.trace("Permission is added to role: " + roleName + " : WebResourcePermission(" + url + ", " + ALL_STANDARD_METHODS_POSITIVE_STRING + ")");
 	        }
	      }
      }    
	    
    } catch (Exception e) {
      JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_MESSAGE, e);
      
    }    
  }
  
  private void migrateExcludedSAPPermissions(JaccPolicyConfiguration jaccConfiguration, PolicyConfiguration policyConfiguration) {
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.MIGRATING_EXCLUDED_SAP_PERMISSIONS_MESSAGE);
    
    try {
      ArrayList excludedSapUserDataPermissions = jaccConfiguration.getExcludedSAPUserDataPermissions();
      Iterator userDataPermissionsIterator = excludedSapUserDataPermissions.iterator();
	    
      while (userDataPermissionsIterator.hasNext()) {
        IPermission perm = (IPermission) userDataPermissionsIterator.next();
        String actions = perm.getValue();
        String url = perm.getName();
        String newActions = getActionsFromSapPermissionActions(actions);
        
        JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from excluded policy : SAPWebUserDataPermission(" + url + ", " + actions + ")");
	      
        if (newActions == null) {
          JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is not valid: " + perm.getClassName() + "(" + url + "," + actions + ")");
        } else {
	     
           policyConfiguration.addToExcludedPolicy(new WebUserDataPermission(url, newActions));
           JaccPolicyConfigurationsLog.trace("Permission is added to excluded policy : WebUserDataPermission(" + url + ", " + newActions + ")");
 		      
        }	    
      }    
	    
      
	    // migrates the excluded SAPWebResourcePermision objects 
	  	
      ArrayList excludedSapResourcePermissions = jaccConfiguration.getExcludedSAPResourcePermissions();
	  	Iterator resourcePermissionsIterator = excludedSapResourcePermissions.iterator();
	    
	  	while (resourcePermissionsIterator.hasNext()) {
        IPermission perm = (IPermission) resourcePermissionsIterator.next();
        String actions = perm.getValue();
        String url = perm.getName();
        String newActions = getActionsFromSapPermissionActions(actions);
        
        JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from excluded policy : SAPWebResourcePermission(" + url + ", " + actions + ")");
	      
        if (newActions == null) {
          JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is not valid: " + perm.getClassName() + "(" + url + "," + actions + ")");
        } else {
	     
          policyConfiguration.addToExcludedPolicy(new WebResourcePermission(url, newActions));
          JaccPolicyConfigurationsLog.trace("Permission is added to excluded policy : WebResourcePermission(" + url + ", " + newActions + ")");
 		      
       }
      }
	    
    } catch (Exception e) {
      JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_MESSAGE, e);
 
    }    
  }

  private void migrateUncheckedSAPPermissions(JaccPolicyConfiguration jaccConfiguration, PolicyConfiguration policyConfiguration) {
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.MIGRATING_UNCHECKED_SAP_PERMISSIONS_MESSAGE);
    
    try {
	    // migrates the unchecked SAPWebUserDataPermision objects 

      ArrayList uncheckedSapUserDataPermissions = jaccConfiguration.getUncheckedSAPUserDataPermissions();
      Iterator userDataPermissionsIterator = uncheckedSapUserDataPermissions.iterator();
	      while (userDataPermissionsIterator.hasNext()) {
	        IPermission perm = (IPermission) userDataPermissionsIterator.next();
	        String actions = perm.getValue();
	        String url = perm.getName();
	        String newActions = null;
	        
	        JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from unchecked policy : SAPWebUserDataPermission(" + url + ", " + actions + ")");
		      
	        if (actions == null) {
	          JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is null: " + perm.getClassName() + "(" + url + "," + actions + ")");
	        } else {
	          if (actions.startsWith(POSITIVE_STRING)){
		          
	            newActions = actions.substring(1);
		        
		        } else if (actions.equals(ALL_METHODS_NEGATIVE_STRING) || actions.equals(DUMMY_METHOD_NEGATIVE_STRING)) {

		          newActions = ALL_STANDARD_METHODS_NEGATIVE_STRING;
	
		        } else if (actions.equals(ALL_METHODS_NEGATIVE_STRING_INTEGRAL) || actions.equals(DUMMY_METHOD_NEGATIVE_STRING_INTEGRAL)) {

		          newActions = ALL_STANDARD_METHODS_NEGATIVE_STRING_INTEGRAL;

		        } else if (actions.equals(ALL_METHODS_NEGATIVE_STRING_CONFIDENTIAL) || actions.equals(DUMMY_METHOD_NEGATIVE_STRING_CONFIDENTIAL)) {

		          newActions = ALL_STANDARD_METHODS_NEGATIVE_STRING_CONFIDENTIAL;

		        }	else if (actions.startsWith(NEGATIVE_STRING)) {
		          
		          String parsedMethods = null;
		          
		          if (actions.startsWith(ALL_METHODS_NEGATIVE_STRING)) {

		            parsedMethods = actions.substring(ALL_METHODS_NEGATIVE_STRING.length() + 1);
			          
			        } else {
		          
			          parsedMethods = actions.substring(1);
			        }
		          
		          newActions = EXCLAMATION_MARK_STRING + parsedMethods;
	            
		        } else {

		          JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is not valid: " + perm.getClassName() + "(" + url + "," + actions + ")");
		          continue;
		        }
	          
	          policyConfiguration.addToUncheckedPolicy(new WebUserDataPermission(url, newActions));
	          JaccPolicyConfigurationsLog.trace("Permission is added to unchecked policy : WebUserDataPermission(" + url + ", " + newActions + ")");
			      
	        }
	      }
	    

	      // migrates the excluded SAPWebResourcePermision objects 

	    HashMap roleAssignedResourceMethods = jaccConfiguration.getRoleAssignedResourceMethods();

	    ArrayList uncheckedSapResourcePermissions = jaccConfiguration.getUncheckedSAPResourcePermissions();
	    
	    Iterator permissionsIterator = uncheckedSapResourcePermissions.iterator();
	    while (permissionsIterator.hasNext()) {
        IPermission perm = (IPermission) permissionsIterator.next();
        String url = perm.getName();
        String actions = perm.getValue();
        String newActions = null;
        
        JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from unchecked policy : SAPWebResourcePermission(" + url + ", " + actions + ")");
	      
        if (actions == null) {
          JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is null: " + perm.getClassName() + "(" + url + "," + actions + ")");
        } else {
	        if (actions.startsWith(POSITIVE_STRING)){

	          newActions = actions.substring(1);
	        
	        } else if (actions.equals(ALL_METHODS_NEGATIVE_STRING) || actions.equals(DUMMY_METHOD_NEGATIVE_STRING)) {

	          newActions = ALL_STANDARD_METHODS_NEGATIVE_STRING;

	        } else if (actions.startsWith(NEGATIVE_STRING)) {
	          
	          String parsedMethods = null;
	          
	          if (actions.startsWith(ALL_METHODS_NEGATIVE_STRING)) {

	            parsedMethods = actions.substring(ALL_METHODS_NEGATIVE_STRING.length() + 1);
		          
		        } else {
	          
		          parsedMethods = actions.substring(1);
		        }
	          
	          
	          String methods = (String) roleAssignedResourceMethods.get(url);
		          if ((methods != EMPTY_STRING) && (methods != null)) {
	            newActions = EXCLAMATION_MARK_STRING + methods + METHOD_SEPARATOR_STRING + parsedMethods;
	          } else {
	            newActions = EXCLAMATION_MARK_STRING + parsedMethods;
	          }
	        
	        } else {

	          JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is not valid: " + perm.getClassName() + "(" + url + "," + actions + ")");
	          continue;
	        }
	        
	        policyConfiguration.addToUncheckedPolicy(new WebResourcePermission(url, newActions));
	        JaccPolicyConfigurationsLog.trace("Permission is added to unchecked policy : WebResourcePermission(" + url + ", " + newActions + ")");
		      
        }
      }
	    
    } catch (Exception e) {
      JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_MESSAGE, e);
   
    }    
  }

  private void migrateRoleAssignedSAPPermissions(JaccPolicyConfiguration jaccConfiguration, PolicyConfiguration policyConfiguration) {
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.MIGRATING_ROLE_ASSIGNED_SAP_PERMISSIONS_MESSAGE);
    
    try {
	    // migrates the excluded SAPWebUserDataPermision objects 

      HashMap roleAssignedSapUserDataPermissions = jaccConfiguration.getRoleSAPUserDataPermissions();
      Iterator roleAssignedSAPUserDataKeys = roleAssignedSapUserDataPermissions.keySet().iterator();

      while (roleAssignedSAPUserDataKeys.hasNext()) {
	      String roleKey = (String)roleAssignedSAPUserDataKeys.next();
	      ArrayList roleAssignedUserDataPermissions = (ArrayList) roleAssignedSapUserDataPermissions.get(roleKey);
	      Iterator permissionsIterator = roleAssignedUserDataPermissions.iterator();
	      while (permissionsIterator.hasNext()) {
	        IPermission perm = (IPermission) permissionsIterator.next();
	        String actions = perm.getValue();
	        String url = perm.getName();
	        String newActions = getActionsFromSapPermissionActions(actions);
	        
	        JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from role " + roleKey +  ": SAPWebUserDataPermission(" + url + ", " + actions + ")");
		      
	        if (newActions == null) {
	          JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is not valid:  " + perm.getClassName() + "(" + url + "," + actions + ")");

	        } else {		     
	
	          policyConfiguration.addToRole(roleKey, new WebUserDataPermission(url, newActions));
	          JaccPolicyConfigurationsLog.trace("Permission is added to role " + roleKey +  ": WebUserDataPermission(" + url + ", " + newActions + ")");
			      
	        }
	      }
	    }    
	    
	    
      // migrates the excluded SAPWebResourcePermision objects 
     
      HashMap roleAssignedSapResourcePermissions = jaccConfiguration.getRoleSAPResourcePermissions();
      Iterator roleAssignedSAPResourceKeys = roleAssignedSapResourcePermissions.keySet().iterator();
      while (roleAssignedSAPResourceKeys.hasNext()) {
	      String roleKey = (String)roleAssignedSAPResourceKeys.next();
	      ArrayList roleAssignedResourcePermissions = (ArrayList) roleAssignedSapResourcePermissions.get(roleKey);
	      Iterator permissionsIterator = roleAssignedResourcePermissions.iterator();
	      while (permissionsIterator.hasNext()) {
	        IPermission perm = (IPermission) permissionsIterator.next();
	        String actions = perm.getValue();
	        String url = perm.getName();
	        String newActions = getActionsFromSapPermissionActions(actions);
	        
	        JaccPolicyConfigurationsLog.trace("\nPermission to be migrated from role " + roleKey +  ": SAPWebResourcePermission(" + url + ", " + actions + ")");
	
	        
	        if (actions == null) {
	          JaccPolicyConfigurationsLog.logWarning("WARNING: value of permission is not valid: " + perm.getClassName() + "(" + url + "," + actions + ")");
	        } else {
		        
	          policyConfiguration.addToRole(roleKey, new WebResourcePermission(url, newActions));
	          JaccPolicyConfigurationsLog.trace("Permission is added to role " + roleKey +  ": WebResourcePermission(" + url + ", " + newActions + ")");

	        }
	      }
	    }
	    
    } catch (Exception e) {
      JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_MESSAGE, e);
    }    
  }

  private void migrateActionsWithNewPermissionSet (HashMap actionsToPermissions) {
    
    Iterator actionIds = actionsToPermissions.keySet().iterator();
    while (actionIds.hasNext()) {
      try {
	      String actionId = (String) actionIds.next();
	      IAction action = factory.getAction(actionId);

	      ArrayList permissionsData = (ArrayList) actionsToPermissions.get(actionId);
		    Iterator newPermissions = permissionsData.iterator();
	      
		    try {
				    action.resetPermissions();
				
				    while(newPermissions.hasNext()) {
				      PermissionData permData = (PermissionData) newPermissions.next();
				      action.addPermission(permData);
				    }
				    
				    action.save();
				    action.commit();
				    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.ACTION_COMMIT_MESSAGE + action.getUniqueID());
				  } catch (Exception e) {
				    //rollback of changes in action
				    action.rollback();
				    JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.ACTION_ROLLBACK_MESSAGE + action.getUniqueID());
				    
				    JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_SET_PERMISSIONS_MESSAGE, e);
				    
			
			   } 
      } catch (Exception e) {
        JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_MESSAGE, e);
      }
    }
  }

  private String getActionsFromJaccPermissionActions(String oldActions) {
    String newActions = null;
    
    if (oldActions == null) {
      
      newActions = ALL_STANDARD_METHODS_POSITIVE_STRING;
      
    } else if (oldActions.equals(TRANSPORT_GUARANTEE_CONFIDENTIAL)) {
      
      newActions = ALL_STANDARD_METHODS_POSITIVE_STRING_CONFIDENTIAL;
   
    } else if (oldActions.equals(TRANSPORT_GUARANTEE_INTEGRAL)) {
      
      newActions = ALL_STANDARD_METHODS_POSITIVE_STRING_INTEGRAL;
    }
    
    return newActions;
  }
  
  private String getActionsFromSapPermissionActions(String oldActions) {
    String newActions = null;
    
    if (oldActions != null) {
    
	    if (oldActions.startsWith(POSITIVE_STRING)){
	      
	      newActions = oldActions.substring(1);
	     		        
	    } else if (oldActions.equals(ALL_METHODS_NEGATIVE_STRING)) {
	      
	      newActions = ALL_STANDARD_METHODS_NEGATIVE_STRING;
	
	    } else if (oldActions.equals(ALL_METHODS_NEGATIVE_STRING_INTEGRAL)) {
	
	      newActions = ALL_STANDARD_METHODS_NEGATIVE_STRING_INTEGRAL;
	
	    } else if (oldActions.equals(ALL_METHODS_NEGATIVE_STRING_CONFIDENTIAL)) {
	
	      newActions = ALL_STANDARD_METHODS_NEGATIVE_STRING_CONFIDENTIAL;
	
	    }
    }
    
    return newActions;
  }
  
  private String getNeededActionsFromSapPermissionActions(String oldActions) {
    String newActions = null;
    
    if (oldActions != null) {
    
	    if (oldActions.equals(ALL_METHODS_NEGATIVE_STRING) || oldActions.equals(DUMMY_METHOD_NEGATIVE_STRING)) {

        newActions = ALL_STANDARD_METHODS_NEGATIVE_STRING;

      } else if (oldActions.equals(ALL_METHODS_NEGATIVE_STRING_INTEGRAL) || oldActions.equals(DUMMY_METHOD_NEGATIVE_STRING_INTEGRAL)) {

        newActions = ALL_STANDARD_METHODS_NEGATIVE_STRING_INTEGRAL;

      } else if (oldActions.equals(ALL_METHODS_NEGATIVE_STRING_CONFIDENTIAL) || oldActions.equals(DUMMY_METHOD_NEGATIVE_STRING_CONFIDENTIAL)) {

        newActions = ALL_STANDARD_METHODS_NEGATIVE_STRING_CONFIDENTIAL;

      }	
    }
    
    return newActions;
  }
}
