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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
public class JaccPolicyConfiguration {
  private String policyConfigurationId;
  
  //This array contains the names of J actions
  private String[] roleActions;

  //This array contains the names of E actions
  private String[] excludedActions;
  
  //This array contains the names of U actions
  private String[] uncheckedActions;  
  
  //This set contains the j2ee role names specified for this policy configuration 
  private HashSet j2eeRoleNames = new HashSet();
  
  //specifies if there are extension methods specified in the policy configuration
  private boolean needsMigration = false;

  // This hashmap contains the mapping between urls and http methods (as String)
  // generated from all SAP and SUN web resource permissions assigned to roles.
  // It is used when migrating SAP unchecked resource permissions 
  private HashMap roleAssignedResourceMethods = new HashMap();
  
  // HashMaps that contain IPermissions representing the role assigned permission  
  // Key is the role name and Value is arraylist of permissions assigned to the role 
  private HashMap roleJACCResourcePermissions = new HashMap();
  private HashMap roleJACCUserDataPermissions = new HashMap();
  private HashMap roleSAPResourcePermissions = new HashMap();
  private HashMap roleSAPUserDataPermissions = new HashMap();
  
  //ArrayLists that contain IPermissions representing all unchecked permissions
	private ArrayList uncheckedJACCResourcePermissions = new ArrayList();
	private ArrayList uncheckedJACCUserDataPermissions = new ArrayList();
	private ArrayList uncheckedSAPResourcePermissions = new ArrayList();
	private ArrayList uncheckedSAPUserDataPermissions = new ArrayList();
	
  //ArrayLists that contain IPermissions representing all excluded permissions
	private ArrayList excludedJACCResourcePermissions = new ArrayList();
	private ArrayList excludedJACCUserDataPermissions = new ArrayList();
	private ArrayList excludedSAPResourcePermissions = new ArrayList();
	private ArrayList excludedSAPUserDataPermissions = new ArrayList();
	
	//This hashmap contains all actions that have new set of permissions(because of deletion of permissons)
	//key - action id, value - arraylist with PermissionData objects representing new permissions
  private HashMap actionsWithNewPermissionSet = new HashMap();

	private IActionFactory factory = InternalUMFactory.getActionFactory();
  
  
  public JaccPolicyConfiguration(String policyConfigurationId, String[] roleActions, String[] excludedActions, String[] uncheckedActions) {
    this.policyConfigurationId = policyConfigurationId;
    this.roleActions = roleActions;
    this.excludedActions = excludedActions;
    this.uncheckedActions = uncheckedActions;
  }
  
  public String getPolicyConfigurationId(){
    return policyConfigurationId;
  }
  
  public String[] getRoleActions(){
    return roleActions;
  }
  
  public String[] getExcludedActions(){
    return excludedActions;
  }
  
  public String[] getUncheckedActions(){
    return uncheckedActions;    
  }
  
  public Iterator getUncheckedJACCUserDataPermissions() {
    return uncheckedJACCUserDataPermissions.iterator();
  }

  public Iterator getUncheckedJACCResourcePermissions() {
    return uncheckedJACCResourcePermissions.iterator();
  }
  
  public Iterator getExcludedJACCUserDataPermissions() {
    return excludedJACCUserDataPermissions.iterator();
  }
  
  public Iterator getExcludedJACCResourcePermissions() {
    return excludedJACCResourcePermissions.iterator();
  }

  public HashMap getRoleJACCUserDataPermissions() {
    return roleJACCUserDataPermissions;
  }

  public HashMap getRoleJACCResourcePermissions() {    
    return roleJACCResourcePermissions;
  }

  public ArrayList getUncheckedSAPUserDataPermissions() {
    return uncheckedSAPUserDataPermissions;
  }
  
  public ArrayList getUncheckedSAPResourcePermissions() {
    return uncheckedSAPResourcePermissions;
  }

  public ArrayList getExcludedSAPUserDataPermissions() {
    return excludedSAPUserDataPermissions;
  }

  public ArrayList getExcludedSAPResourcePermissions() {
    return excludedSAPResourcePermissions;
  }
  
  public HashMap getRoleSAPUserDataPermissions() { 
    return roleSAPUserDataPermissions;
  }

  public HashMap getRoleSAPResourcePermissions() {   
    return roleSAPResourcePermissions;
  }
  
  public HashMap getRoleAssignedResourceMethods() {
    return roleAssignedResourceMethods;
  }
  
  public HashMap getActionsWithNewPermissionSet() {
    return actionsWithNewPermissionSet;
  }
  
  public HashSet getJ2eeRoleNames() {
    return j2eeRoleNames;
  }    
  
  public boolean needsMigration() {
    return needsMigration;
  }
  
  public void init() {
    fillExcludedPermissions();
    fillUncheckedPermissions();    
    fillRolePermissions();
  }
  
  private void fillUncheckedPermissions() {
    if(uncheckedActions != null) {
      JaccPolicyConfigurationsLog.trace("\nFill unchecked permissions from " + uncheckedActions.length + " unchecked actions");
      
      for(int i = 0; i < uncheckedActions.length; i++) {
        String actionId = uncheckedActions[i];
        try {
          IAction action = factory.getAction(actionId);
          //gets all permission for the current action
          Iterator permissionIterator = action.getPermissions();
 
          boolean permissionsRemoved = false;
          ArrayList permissionsData = new ArrayList();
          
          while (permissionIterator.hasNext()) {
            
            IPermission perm = (IPermission) permissionIterator.next();
            String name = perm.getName();
            String actions = perm.getValue();
            String permClass = perm.getClassName();
            boolean isRemoved = false;
            
            if ((actions != null) && actions.startsWith(JaccPermissionManager.ALL_METHODS_POSITIVE_STRING)) {
              // skip permissions that have +_HTTP_ _METHODS_ _ALL_ actions string
              // as they are not valid
              JaccPolicyConfigurationsLog.trace("OK: skipping " + permClass + "(" + name + ", " + actions + ")");
              continue;
            }	
             
            if (permClass.equals(JaccPermissionManager.WEB_USER_DATA_PERMISSION_CLASSPATH)) {
              //adds only those web user data permissions that are specified with "null" HTTP methods and should be migrated 
              if ((actions == null) 
                  || (actions.equals(JaccPermissionManager.TRANSPORT_GUARANTEE_CONFIDENTIAL)) 
                  || (actions.equals(JaccPermissionManager.TRANSPORT_GUARANTEE_INTEGRAL))) {
                
	              uncheckedJACCUserDataPermissions.add(perm);      

	              //marks this permission for removal
	              isRemoved = true;
	              permissionsRemoved = true;
	              JaccPolicyConfigurationsLog.trace("OK: permission marked for removal from unchecked policy: " + permClass + "(" + name + ", " + actions + ")");
	              
              }
            } else if (permClass.equals(JaccPermissionManager.WEB_RESOURCE_PERMISSION_CLASSPATH) && (actions == null)) {
              //adds only those web resource permissions that are specified with "null" HTTP methods and should be migrated 
              uncheckedJACCResourcePermissions.add(perm);
              
              //marks this permission for removal
              isRemoved = true;
              permissionsRemoved = true;
              JaccPolicyConfigurationsLog.trace("OK: permission marked for removal from unchecked policy: " + permClass + "(" + name + ", " + actions + ")");
 
            } else if (permClass.equals(JaccPermissionManager.SAP_WEB_USER_DATA_PERMISSION_CLASSPATH)) {
              
              uncheckedSAPUserDataPermissions.add(perm);
              
              if(!JaccPermissionManager.extensionStrings.contains(actions)) {
                needsMigration = true;  
              }

              
            } else if (permClass.equals(JaccPermissionManager.SAP_WEB_RESOURCE_PERMISSION_CLASSPATH)) {
                   
              uncheckedSAPResourcePermissions.add(perm);    
              
              if(!JaccPermissionManager.extensionStrings.contains(actions)) {
                needsMigration = true;  
              }
            } 
          
            if(!isRemoved) {
              permissionsData.add(new PermissionData(permClass, name, actions));
            }	          
          }
          
          if (permissionsRemoved) {
            actionsWithNewPermissionSet.put(actionId, permissionsData);
          }
	          
        } catch (Exception e) {
          JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_MESSAGE, e);
          
        }
      }
    }
  }
  
  private void fillExcludedPermissions() {
    if(excludedActions != null) {
      JaccPolicyConfigurationsLog.trace("\nFill excluded permissions from " + excludedActions.length + " excluded actions");

      for(int i = 0; i < excludedActions.length; i++) {
        String actionId = excludedActions[i];
        try {
          IAction action = factory.getAction(actionId);
          Iterator permissionIterator = action.getPermissions();
          
          boolean permissionsRemoved = false;
          ArrayList permissionsData = new ArrayList();
          
          while (permissionIterator.hasNext()) {
            
            IPermission perm = (IPermission) permissionIterator.next();
            String name = perm.getName();
            String actions = perm.getValue();
            String permClass = perm.getClassName();
            
            boolean isRemoved = false;

            
            if ((actions!= null) && actions.startsWith(JaccPermissionManager.ALL_METHODS_POSITIVE_STRING)) {
              //skip permissions that have +_HTTP_ _METHODS_ _ALL_ actions string
              JaccPolicyConfigurationsLog.trace("OK: skipping " + permClass + "(" + name + ", " + actions + ")");
              continue;
            }
             
            if (permClass.equals(JaccPermissionManager.WEB_USER_DATA_PERMISSION_CLASSPATH)) {
              //adds only those web resource permissions that are specified with "null" HTTP methods and should be migrated 
              if ((actions == null) 
                  || (actions.equals(JaccPermissionManager.TRANSPORT_GUARANTEE_CONFIDENTIAL)) 
                  || (actions.equals(JaccPermissionManager.TRANSPORT_GUARANTEE_INTEGRAL))) {
	              
	              excludedJACCUserDataPermissions.add(perm);
	              
	              //marks this permission for removal
	              isRemoved = true;
	              permissionsRemoved = true;
	              JaccPolicyConfigurationsLog.trace("OK: permission marked for removal from excluded policy: " + permClass + "(" + name + ", " + actions + ")");

              }
              
              
            } else if (permClass.equals(JaccPermissionManager.WEB_RESOURCE_PERMISSION_CLASSPATH) && (actions == null)) {
              //adds only those web resource permissions that are specified with "null" HTTP methods and should be migrated 
             
              excludedJACCResourcePermissions.add(perm);
              
              //marks this permission for removal
              isRemoved = true;
              permissionsRemoved = true;
              JaccPolicyConfigurationsLog.trace("OK: permission marked for removal from excluded policy: " + permClass + "(" + name + ", " + actions + ")");


            } else if (permClass.equals(JaccPermissionManager.SAP_WEB_USER_DATA_PERMISSION_CLASSPATH)) {
              
              excludedSAPUserDataPermissions.add(perm);
              
              if(!JaccPermissionManager.extensionStrings.contains(actions)) {
                needsMigration = true;  
              }

            } else if (permClass.equals(JaccPermissionManager.SAP_WEB_RESOURCE_PERMISSION_CLASSPATH)) {
              
              excludedSAPResourcePermissions.add(perm); 
              
              if(!JaccPermissionManager.extensionStrings.contains(actions)) {
                needsMigration = true;  
              }

            } 
            
            if(!isRemoved) {
              permissionsData.add(new PermissionData(permClass, name, actions));
            }	          

          }
          
          if (permissionsRemoved) {
            actionsWithNewPermissionSet.put(actionId, permissionsData);
          }
        
        } catch (Exception e) {
          JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_MESSAGE, e);

        }
      }
    }
  }

  private void fillRolePermissions() {
    if(roleActions != null) {
      JaccPolicyConfigurationsLog.trace("\nFill role permissions from " + roleActions.length + " role actions");

      for(int i = 0; i < roleActions.length; i++) {
        ArrayList roleAssignedJACCResourcePermissions = null;
        ArrayList roleAssignedJACCUserDataPermissions = null;
        ArrayList roleAssignedSAPResourcePermissions = null;
        ArrayList roleAssignedSAPUserDataPermissions = null;
        
        String actionId = roleActions[i];
        try {
          IAction action = factory.getAction(actionId);
          //gets the name of the j2ee role from the action name
          //todo: handle if roleName is null
          String roleName = getRoleName(actionId);
          if (roleName == null) {
            JaccPolicyConfigurationsLog.logWarning("WARNING: role name derived from action " + actionId + " is null");          
            continue;
          }
          
          //adds this role to the store, if it does not exist 
          if (!j2eeRoleNames.contains(roleName)) {
            j2eeRoleNames.add(roleName);

            roleAssignedJACCResourcePermissions = new ArrayList();  
            roleAssignedJACCUserDataPermissions = new ArrayList();
            roleAssignedSAPResourcePermissions = new ArrayList();  
            roleAssignedSAPUserDataPermissions = new ArrayList();
          } else {
            //takes the permissions already assigned to this role
            roleAssignedJACCResourcePermissions = (ArrayList) roleJACCResourcePermissions.get(roleName);
            roleAssignedJACCUserDataPermissions = (ArrayList) roleJACCUserDataPermissions.get(roleName);
            roleAssignedSAPResourcePermissions = (ArrayList) roleSAPResourcePermissions.get(roleName);
            roleAssignedSAPUserDataPermissions = (ArrayList) roleSAPUserDataPermissions.get(roleName);
          }          

          Iterator permissionIterator = action.getPermissions();
          
          boolean permissionsRemoved = false;
          ArrayList permissionsData = new ArrayList();
          
          while (permissionIterator.hasNext()) {
            
            IPermission perm = (IPermission) permissionIterator.next();
            String actions = perm.getValue();
            String permClass = perm.getClassName();
            String name = perm.getName();
            boolean isRemoved = false;
            
            if ((actions!= null) && actions.startsWith(JaccPermissionManager.ALL_METHODS_POSITIVE_STRING)) {
              //skips adding of permissions that have +_HTTP_ _METHODS_ _ALL_ actions string
              JaccPolicyConfigurationsLog.trace("OK: skipping " + permClass + "(" + name + ", " + actions + ")");
        
              continue;
            }
             
            if (permClass.equals(JaccPermissionManager.WEB_USER_DATA_PERMISSION_CLASSPATH)) {
              //adds only those web resource permissions that are specified with "null" HTTP methods and should be migrated 
              if ((actions == null) 
                  || (actions.equals(JaccPermissionManager.TRANSPORT_GUARANTEE_CONFIDENTIAL)) 
                  || (actions.equals(JaccPermissionManager.TRANSPORT_GUARANTEE_INTEGRAL))) {
                
                roleAssignedJACCUserDataPermissions.add(perm);
                
	              //marks this permission for removal 
	              isRemoved = true;
	              permissionsRemoved = true;

	              JaccPolicyConfigurationsLog.trace("OK: permission marked for removal from role " + roleName + ": " + permClass + "(" + name + ", " + actions + ")");
	              
              }
            } else if (permClass.equals(JaccPermissionManager.WEB_RESOURCE_PERMISSION_CLASSPATH)) {
              String newMethods = null;
              
              if (actions == null) {
                //adds only those web resource permissions that are specified with "null" HTTP methods and should be migrated 
 
                roleAssignedJACCResourcePermissions.add(perm);
	              //marks this permission for removal
	              isRemoved = true;
	              permissionsRemoved = true;

	              JaccPolicyConfigurationsLog.trace("OK: permission marked for removal from role " + roleName + ": " + permClass + "(" + name + ", " + actions + ")");
		              
                newMethods = JaccPermissionManager.ALL_STANDARD_METHODS_POSITIVE_STRING;
              } else {                
                newMethods = actions;
              }
 
              //add the methods to the map containing url - methods - roleAssignedResourceMethods

              String currentMethods = (String) roleAssignedResourceMethods.get(name);
              
              if (currentMethods != null) {
                currentMethods = currentMethods + JaccPermissionManager.METHOD_SEPARATOR_STRING + newMethods;
              } else {
                currentMethods = newMethods;
              }       
              
              roleAssignedResourceMethods.put(name, currentMethods);
              
            } else if (permClass.equals(JaccPermissionManager.SAP_WEB_USER_DATA_PERMISSION_CLASSPATH)) {
              
              roleAssignedSAPUserDataPermissions.add(perm);
              
              if(!JaccPermissionManager.extensionStrings.contains(actions)) {
                needsMigration = true;  
              }
              
            } else if (permClass.equals(JaccPermissionManager.SAP_WEB_RESOURCE_PERMISSION_CLASSPATH)) {
              
              roleAssignedSAPResourcePermissions.add(perm);

              if(!JaccPermissionManager.extensionStrings.contains(actions)) {
                needsMigration = true;  
              }	              
            }  
            
            if(!isRemoved) {
              permissionsData.add(new PermissionData(permClass, name, actions));
            }	  
            
          }
          
          if (permissionsRemoved) {
            actionsWithNewPermissionSet.put(actionId, permissionsData);
          }
          
          //writes the permission back into the map
          roleSAPResourcePermissions.put(roleName, roleAssignedSAPResourcePermissions);
          roleSAPUserDataPermissions.put(roleName, roleAssignedSAPUserDataPermissions);
          roleJACCResourcePermissions.put(roleName, roleAssignedJACCResourcePermissions);
          roleJACCUserDataPermissions.put(roleName, roleAssignedJACCUserDataPermissions);
                    
        } catch (Exception e) {
          JaccPolicyConfigurationsLog.logWarning(JaccPolicyConfigurationsLog.UNEXPECTED_EXCEPTION_MESSAGE, e);
        }
      }
    }
    
  }
  
  public void printAllActions() {
	  if (roleActions != null) {
	    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.FOUND_J2EE_ROLE_ACTIONS_MESSAGE + roleActions.length);
	    printActions(roleActions);
	  }
	  if (excludedActions != null) {
	    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.FOUND_EXCLUDED_ACTIONS_MESSAGE + excludedActions.length);
	    printActions(excludedActions);
	  }
	  if (uncheckedActions != null) {
	    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.FOUND_UNCHECKED_ACTIONS_MESSAGE + uncheckedActions.length);
	    printActions(uncheckedActions);
	  }
  }
    
  private void printActions(String[] actions) {
    if (actions == null) {
      JaccPolicyConfigurationsLog.trace("actions is null" );
      return;
    }
    for(int i = 0; i < actions.length; i++) {
      JaccPolicyConfigurationsLog.trace("action " + (i+1) + ": " + actions[i]);
    }
  }
  
  public void printAllPermissions () {

    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.PERMISSIONS_TO_BE_MIGRATED_MESSAGE);
    
    
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.FOUND_EXCLUDED_SAP_RESOURCE_PERMISSIONS_MESSAGE);
    printPermissions(excludedSAPResourcePermissions);
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.FOUND_EXCLUDED_SAP_USERDATA_PERMISSIONS_MESSAGE);
    printPermissions(excludedSAPUserDataPermissions);
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.FOUND_EXCLUDED_JACC_RESOURCE_PERMISSIONS_MESSAGE);
    printPermissions(excludedJACCResourcePermissions);
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.FOUND_EXCLUDED_JACC_USERDATA_PERMISSIONS_MESSAGE);
    printPermissions(excludedJACCUserDataPermissions);
    
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.FOUND_UNCHECKED_SAP_RESOURCE_PERMISSIONS_MESSAGE);
    printPermissions(uncheckedSAPResourcePermissions);
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.FOUND_UNCHECKED_SAP_USERDATA_PERMISSIONS_MESSAGE);
    printPermissions(uncheckedSAPUserDataPermissions);
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.FOUND_UNCHECKED_JACC_RESOURCE_PERMISSIONS_MESSAGE);
    printPermissions(uncheckedJACCResourcePermissions);
    JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.FOUND_UNCHECKED_JACC_USERDATA_PERMISSIONS_MESSAGE);
    printPermissions(uncheckedJACCUserDataPermissions);

    Iterator rolesIterator = j2eeRoleNames.iterator();
    while (rolesIterator.hasNext()) {
      String roleName = (String) rolesIterator.next();
      ArrayList resourceJACCPermissions = (ArrayList) roleJACCResourcePermissions.get(roleName);
      ArrayList userDataJACCPermissions = (ArrayList) roleJACCUserDataPermissions.get(roleName);
      ArrayList resourceSAPPermissions = (ArrayList) roleSAPResourcePermissions.get(roleName);
      ArrayList userDataSAPPermissions = (ArrayList) roleSAPUserDataPermissions.get(roleName);

      JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.FOUND_ROLE_ASSIGNED_SAP_RESOURCE_PERMISSIONS_MESSAGE + roleName);
      printPermissions(resourceSAPPermissions);
      JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.FOUND_ROLE_ASSIGNED_SAP_USERDATA_PERMISSIONS_MESSAGE + roleName);
      printPermissions(userDataSAPPermissions);
      JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.FOUND_ROLE_ASSIGNED_JACC_RESOURCE_PERMISSIONS_MESSAGE + roleName);
      printPermissions(resourceJACCPermissions);
      JaccPolicyConfigurationsLog.trace(JaccPolicyConfigurationsLog.FOUND_ROLE_ASSIGNED_JACC_USERDATA_PERMISSIONS_MESSAGE + roleName);
      printPermissions(userDataJACCPermissions);
    }
  }
    
  private void printPermissions(ArrayList permissions) {
    if (permissions == null) {
      return;
    }
    
    for(int i = 0; i < permissions.size(); i++) {
      IPermission perm = (IPermission) permissions.get(i);

      JaccPolicyConfigurationsLog.trace("classname: " + perm.getClassName() + " name: " + perm.getName() + " value: "  + perm.getValue());
    }
  }
  
  private String getRoleName (String actionName) {
    String roleName = null;
    int idx = actionName.lastIndexOf("$");
    if (idx != -1) {
      roleName = actionName.substring(idx + 1);
    } 
    return roleName;
  }
 
}

