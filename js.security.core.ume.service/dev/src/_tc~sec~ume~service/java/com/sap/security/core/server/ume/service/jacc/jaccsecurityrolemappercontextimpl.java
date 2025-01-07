/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.security.core.server.ume.service.jacc;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.interfaces.security.JACCSecurityRoleMappingContext;
import com.sap.engine.interfaces.security.SecurityRoleContext;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.security.api.IRole;
import com.sap.security.api.IRoleSearchFilter;
import com.sap.security.api.ISearchAttribute;
import com.sap.security.api.ISearchResult;
import com.sap.security.api.NoSuchRoleException;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.imp.RoleFactory;
import com.sap.security.core.imp.RoleSearchFilter;
import com.sap.security.core.role.ActionException;
import com.sap.security.core.role.IAction;
import com.sap.security.core.role.NoSuchActionException;
import com.sap.security.core.role.PermissionData;
import com.sap.security.core.role.imp.PermissionRoles;
import com.sap.security.core.role.persistence.PersistenceException;
import com.sap.security.core.role.persistence.PersistenceLayer;
import com.sap.security.core.role.persistence.PrincipalMappingPersistenceManager;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class JACCSecurityRoleMapperContextImpl implements JACCSecurityRoleMappingContext {

	private static Location myLoc = Location.getLocation(JACCSecurityRoleMapperContextImpl.class);
	private static Category myCat = Category.getCategory(LoggingHelper.SYS_SECURITY, "JACC");

	/**
	 * Method for mapping the jacc security role to the UME role.
	 * This method must be used from the containers.
	 * 
	 * 
	 * @param jaccSecurityRole  the name of the JACC Security Role
	 * @param jaccPolicyConfiguration  the name of the JACC Policy Configuration 
	 * @param umeRole  the name of the target UME Role which will be mapped
	 * @throws SecurityException  thrown if invalid parameter is passed
	 */
	public void addUMERoleToJACCRole(String jaccSecurityRole, String jaccPolicyConfiguration, String umeRole) throws SecurityException {
    int status = -1;
    try {
      status = PersistenceLayer.getPolicyConfigurationStatus(jaccPolicyConfiguration);
    } catch (Exception e) {
      throw new SecurityException("Unable to determine which persistence maintains context ID '" + jaccPolicyConfiguration + "'!", e);
    }
    
    if (status == PersistenceLayer.STATUS_JPL_PERSISTENCE) {
      PrincipalMappingPersistenceManager manager = PrincipalMappingPersistenceManager.getManager();
      try {
        //TODO: update the docs about name convertion
        String umeRoleName = convert640to700SecurityRoleConvetion(umeRole);
        
        try {
          //TODO: create the ume role if it does not exist
          IRole role = null;
          try {
            role = UMFactory.getRoleFactory().getRoleByUniqueName(umeRoleName);
          } catch (NoSuchRoleException exc) {
            
            // role doesn't exist -> create it
             role = UMFactory.getRoleFactory().newRole(umeRoleName);
             role.save();
             role.commit();
          }
        } catch (UMException e) {
          throw new SecurityException("Unable to create ume role '" +  umeRoleName + "'!", e);
        }
        
        
        manager.assignAppRoleToServerRole(jaccPolicyConfiguration, jaccSecurityRole, umeRoleName);
        manager.commit();
        
      } catch (PersistenceException pe) {
        throw new SecurityException("Unable to commit changes for policy configuration with context ID '" + jaccPolicyConfiguration + "'!", pe);
      } 
      
    } else {

      //TODO: remove the check when web container remove the creation of EXTERNAL policy configurations 
      if (!jaccPolicyConfiguration.endsWith("-EXTERNAL")) {
        IAction action = null;
    				
    		try {
    			action = InternalUMFactory.getActionFactory().getAction(jaccPolicyConfiguration, IAction.TYPE_J2EE_ROLE, jaccSecurityRole);
    		} catch (NoSuchActionException e) {
    			try {
    				action = InternalUMFactory.getActionFactory().newAction(jaccPolicyConfiguration, IAction.TYPE_J2EE_ROLE, jaccSecurityRole);
    			} catch (ActionException ae) {
    			  throw new SecurityException(ae.getMessage(), ae);
    			}
    		} catch (ActionException e) {
    			throw new SecurityException(e.getMessage(), e);
    		}
      
        addActionToUMERole(action, umeRole);
      }
    }
	}
	
  public void addUMERoleToServiceRole(String serviceRole, String serviceName, String umeRole) throws SecurityException {
    IAction action = null;
        
    try {
      action = InternalUMFactory.getActionFactory().getAction("sap.com/" + serviceName, IAction.TYPE_UME_ACTION, serviceRole);
    } catch (NoSuchActionException e) {
      throw new SecurityException(e.getMessage(), e);
    } catch (ActionException e) {
      throw new SecurityException(e.getMessage(), e);
    }
    
    addActionToUMERole(action, umeRole);
  }

  public void addUMERoleToServiceRole(String serviceRole, String serviceName, String umeRole, String permissionClass, String permissionName, String permissionValue) throws SecurityException {
    IAction action = null;
        
    try {
      action = InternalUMFactory.getActionFactory().getAction("sap.com/" + serviceName, IAction.TYPE_UME_ACTION, serviceRole);
    } catch (NoSuchActionException e) {
      try {
        action = InternalUMFactory.getActionFactory().newAction("sap.com/" + serviceName, IAction.TYPE_UME_ACTION, serviceRole);
        PermissionData data = new PermissionData(permissionClass, permissionName, permissionValue);
        action.addPermission(data);
        action.save();
        action.commit();
      } catch (ActionException ae) {
        throw new SecurityException(ae.getMessage(), ae);
      } catch (UMException ume) {        
        throw new SecurityException(ume.getMessage(), ume);
      }
    } catch (ActionException e) {
      throw new SecurityException(e.getMessage(), e);
    }
    
    addActionToUMERole(action, umeRole);  
  }
  
  private void addActionToUMERole(IAction action, String umeRole) {
    IRole role = null;

    String umeRole700Style = convert640to700SecurityRoleConvetion(umeRole);    
    try {
      role = UMFactory.getRoleFactory().getRoleByUniqueName(umeRole700Style);
      role = UMFactory.getRoleFactory().getMutableRole(role.getUniqueID());
    } catch (NoSuchRoleException nsre) {
      try {
        role = UMFactory.getRoleFactory().newRole(umeRole);
      } catch (UMException e) {
        throw new SecurityException(e.getMessage(), e);
      }
    } catch (UMException ex) {
      throw new SecurityException(ex.getMessage(), ex);
    }

      
    try {
      PermissionRoles.addAction(role, action);
      role.save();
      role.commit();
    } catch (UMException ex) {
      role.rollback();
      throw new SecurityException(ex.getMessage(), ex);      
    }    
  }
  
  /**
	 * This method will be used only for the migration from 6.40 versions of the j2ee engine.
	 * This implementation must create a new ume role, and map it to the jacc security role.
	 * 
	 * 
	 * @param jaccSecurityRole  the name of the JACC Security Role
	 * @param jaccPolicyConfiguration  the name of the JACC Policy Configuration 
	 * @param users  list of the users that are mapped to the J2EE Role, before the migration.
	 * @param groups  list of the groups that are mapped in the J2EE Role, before the migration.
	 * @throws SecurityException  thrown if invalid parameter is passed
	 */
	public String addUsersAndGroupsToJACCRole(String jaccSecurityRole, String jaccPolicyConfiguration, String[] users, String[] groups) throws SecurityException {
		IRole role = null;
		final String methodName = "addUsersAndGroupsToJACCRole(...)";
		
		String newRoleName = convert640to700SecurityRoleConvetion(jaccPolicyConfiguration != null ? jaccPolicyConfiguration + "." + jaccSecurityRole : jaccSecurityRole);
		try {
			try {
				role = UMFactory.getRoleFactory().getRoleByUniqueName(newRoleName);
				role = UMFactory.getRoleFactory().getMutableRole(role.getUniqueID());
				if (myLoc.beInfo())
					myLoc.infoT(methodName, "Modifying role: " + newRoleName);
			} catch (NoSuchRoleException nsre) {
				role = UMFactory.getRoleFactory().newRole(newRoleName);
				if (myLoc.beInfo())
					myLoc.infoT(methodName, "Creating role: " + newRoleName);
			}			  
		  
			if (users != null && users.length != 0) {
				for (int i = 0; i < users.length; i++) {
					try {
						String uniqueId = UMFactory.getUserFactory().getUserByUniqueName(users[i]).getUniqueID();
						// add user to role if role is new or user isn't member of role
						if ((role.getUniqueID() == null) || (!role.isUserMember(uniqueId, false)))
							role.addUserMember(uniqueId);
					} catch (Exception e) {
						LoggingHelper.traceThrowable(Severity.ERROR, myLoc, "hardcoded location 1-public String addUsersAndGroupsToJACCRole(...)", e);
					}
				}
			} 
		  
			if (groups != null && groups.length != 0) {
				for (int i = 0; i < groups.length; i++) {
					try {
						String uniqueId = UMFactory.getGroupFactory().getGroupByUniqueName(groups[i]).getUniqueID();
						// add group to role if role is new or group isn't member of role
						if ((role.getUniqueID() == null) || (!role.isGroupMember(uniqueId, false)))
							role.addGroupMember(uniqueId);
					} catch (Exception e) {
						LoggingHelper.traceThrowable(Severity.ERROR, myLoc, "hardcoded location 2-public String addUsersAndGroupsToJACCRole(...)", e);
					}
				} 
			} 		  
		  
			role.save();
			role.commit();	
			return newRoleName;	  
		} catch (UMException ex) {
			if (role != null) {
				role.rollback();
			}
			LoggingHelper.traceThrowable(Severity.ERROR, myLoc, methodName + ": role couldn't be committed! " + role, ex);
			throw new SecurityException(ex.getMessage(), ex);
		}
	}
	
	private String convert640to700SecurityRoleConvetion(String role640Style) {
	  if (role640Style.equalsIgnoreCase(SecurityRoleContext.ROLE_ADMINISTRATORS)) {
	    return JACCSecurityRoleMappingContext.UME_ADMINSTRATOR_SECURITY_ROLE;
	  }
	  
	  if (role640Style.equalsIgnoreCase(SecurityRoleContext.ROLE_GUESTS)) {
		  return JACCSecurityRoleMappingContext.UME_GUEST_SECURITY_ROLE;
	  }
	  
	  if (role640Style.equalsIgnoreCase(SecurityRoleContext.ROLE_ALL)) {
		  return JACCSecurityRoleMappingContext.UME_EVERYONE_SECURITY_ROLE;
	  }
	  return role640Style;
	}


	public void removeUMERole(String roleName) throws SecurityException {
    try {
		  String uniqueId = UMFactory.getRoleFactory().getRoleByUniqueName(roleName).getUniqueID();
		  UMFactory.getRoleFactory().deleteRole(uniqueId);
      PrincipalMappingPersistenceManager.getManager().removeServerRoleAssignments(roleName);
    } catch (Exception ex) {
      throw new SecurityException(ex.getMessage(), ex);
    }
	}


	public String getRunAsIdentity(String jaccSecurityRole, String policyConfiguration) throws SecurityException {
    IAction action = null;
    
    try {
      action = InternalUMFactory.getActionFactory().getAction(policyConfiguration, IAction.TYPE_J2EE_ROLE, jaccSecurityRole);
      return action.getRunAsIdentity();
    } catch (NoSuchActionException e) {
      throw new SecurityException(e.getMessage());
    } catch (ActionException e) {
      throw new SecurityException(e.getMessage());
    }   
	} 

  public void setRunAsIdentity(String runAsIdentity, String jaccSecurityRole, String policyConfiguration) throws SecurityException {
    IAction action = null;
    try {
      action = InternalUMFactory.getActionFactory().getAction(policyConfiguration, IAction.TYPE_J2EE_ROLE, jaccSecurityRole);
      action.setRunAsIdentity(runAsIdentity);
      action.save();
      action.commit();
    } catch (NoSuchActionException e) {
      if (action != null) {
        action.rollback();        
      }
      throw new SecurityException(e.getMessage());
    } catch (ActionException e) {
      if (action != null) {
        action.rollback();        
      }
      throw new SecurityException(e.getMessage());
    } catch (UMException ume) {
      if (action != null) {
        action.rollback();        
      }
      throw new SecurityException(ume.getMessage());
    }
  }
  
  //TODO: update design docs with change - added check for persistence
  public void setRunAsAccountGenerationPolicy(byte type, String jaccSecurityRole, String policyConfiguration) {
    IAction action = null;
    try {
      action = InternalUMFactory.getActionFactory().getAction(policyConfiguration, IAction.TYPE_J2EE_ROLE, jaccSecurityRole);
      action.setRunAsAccountGenerationPolicy(type);
      action.save();
      action.commit();
    } catch (NoSuchActionException e) {
      if (action != null) {
        action.rollback();        
      }
      throw new SecurityException(e.getMessage(), e);
    } catch (ActionException e) {
      if (action != null) {
        action.rollback();        
      }
      throw new SecurityException(e.getMessage(), e);
    } catch (UMException ume) {
      if (action != null) {
        action.rollback();        
      }
      throw new SecurityException(ume.getMessage(), ume);
    }
  }
  
  /**
   * This method will be used only for the application roles migration from 6.40 versions of the j2ee engine.
   * This implementation must create a new ume role, and map it to the jacc security role.
   * 
   * 
   * @param jaccSecurityRole  the name of the JACC Security Role
   * @param jaccPolicyConfiguration  the name of the JACC Policy Configuration 
   * @param users  list of the users that are mapped to the J2EE Role, before the migration.
   * @param groups  list of the groups that are mapped in the J2EE Role, before the migration.
   * @throws SecurityException  thrown if invalid parameter is passed
   */
  String migrateUsersAndGroupsToJACCRoleMappings(String jaccSecurityRole, String jaccPolicyConfiguration, String[] users, String[] groups) throws SecurityException {
    IRole role = null;
    final String methodName = "migrateUsersAndGroupsToJACCRoleMappings(...)";

    String newRoleName = convert640to700SecurityRoleConvetion(jaccPolicyConfiguration != null ? jaccPolicyConfiguration + "." + jaccSecurityRole : jaccSecurityRole);
    try {
      try {
        role = UMFactory.getRoleFactory().getRoleByUniqueName(newRoleName);
        role = UMFactory.getRoleFactory().getMutableRole(role.getUniqueID());
        if (myLoc.beInfo())
          myLoc.infoT(methodName, "Modifying role: " + newRoleName);
      } catch (NoSuchRoleException nsre) {

        if ((users != null && users.length > 0) || (groups != null && groups.length > 0)) {
          // UME role is created only there users or groups assigned to it
          role = UMFactory.getRoleFactory().newRole(newRoleName);
          if (myLoc.beInfo())
            myLoc.infoT(methodName, "Creating role: " + newRoleName);
        }
      }

      if (role != null) {
        if (users != null && users.length != 0) {
          for (int i = 0; i < users.length; i++) {
            try {
              String uniqueId = UMFactory.getUserFactory().getUserByUniqueName(users[i]).getUniqueID();
              // add user to role if role is new or user isn't member of role
              if ((role.getUniqueID() == null) || (!role.isUserMember(uniqueId, false)))
                role.addUserMember(uniqueId);
            } catch (Exception e) {
              LoggingHelper.traceThrowable(Severity.ERROR, myLoc, "hardcoded location 1-public String migrateUsersAndGroupsToJACCRoleMappings(...)", e);
            }
          }
        }

        if (groups != null && groups.length != 0) {
          for (int i = 0; i < groups.length; i++) {
            try {
              String uniqueId = UMFactory.getGroupFactory().getGroupByUniqueName(groups[i]).getUniqueID();
              // add group to role if role is new or group isn't member of role
              if ((role.getUniqueID() == null) || (!role.isGroupMember(uniqueId, false)))
                role.addGroupMember(uniqueId);
            } catch (Exception e) {
              LoggingHelper.traceThrowable(Severity.ERROR, myLoc, "hardcoded location 2-public String migrateUsersAndGroupsToJACCRoleMappings(...)", e);
            }
          }
        }

        role.save();
        role.commit();
        return newRoleName;
      } else {
        return null;
      }
    } catch (UMException ex) {
      if (role != null) {
        role.rollback();
      }
      LoggingHelper.traceThrowable(Severity.ERROR, myLoc, methodName + ": role couldn't be committed! " + role, ex);
      throw new SecurityException(ex.getMessage(), ex);
    }
  }
  
  
  /**
   * This method will be used only for the migration from 6.40 versions of the
   * j2ee engine. The method creates mapping from the jacc security role to the
   * UME role and deletes previous mappings
   * 
   * @param jaccSecurityRole the name of the JACC Security Role
   * @param jaccPolicyConfiguration the name of the JACC Policy Configuration
   * @param umeRole the name of the target UME Role which will be mapped
   * @throws SecurityException thrown if invalid parameter is passed
   */
  void migrateUMERoleToJACCRoleMappings(String jaccSecurityRole, String jaccPolicyConfiguration, String umeRole) throws SecurityException {
    // TODO: remove the check when web container remove the creation of EXTERNAL
    // policy configurations
    if (!jaccPolicyConfiguration.endsWith("-EXTERNAL")) {
      IAction action = null;

      try {
        action = InternalUMFactory.getActionFactory().getAction(jaccPolicyConfiguration, IAction.TYPE_J2EE_ROLE, jaccSecurityRole);

        // If action exists all mappings to UME roles should be deleted
        deleteActionToUMERoleMappings(action.getUniqueID());
      } catch (NoSuchActionException e) {
        //action should not be created if it does not exist
      } catch (ActionException e) {
        if ((e.getMessage() != null) && (e.getMessage().startsWith("Unable to construct JACC action"))) {
          //do nothing - this is hack that will be removed in EhP2
          //When ActionFactory.getAction method is called if  there is no such action in the persistence then 
          //ActionException is thrown with message "Unable to construct JACC action".
          //In Ehp2 this method will be changed to throw NoSuchActionException and this case will be handled by the
          //previous catch {} block.
        } else {
          throw new SecurityException(e.getMessage(), e);
        }
      }

      // Add mapping only if action exist and umeRole exists or has been created
      if ((action != null) && (umeRole != null)) {
        addActionToUMERole(action, umeRole);
      }
    }
  }
 
  private void deleteActionToUMERoleMappings(String actionId) {
    RoleFactory roleFactory = (RoleFactory) UMFactory.getRoleFactory();
    try {
      IRoleSearchFilter searchFilter = new RoleSearchFilter();
      searchFilter.setSearchAttribute(PermissionRoles.ROLE_NAMESPACE, PermissionRoles.ACTIONS, actionId, ISearchAttribute.LIKE_OPERATOR, false);
      ISearchResult roleIter = roleFactory.searchRoles(searchFilter);
      while (roleIter.hasNext()) {
        String roleID = (String) roleIter.next();
        IRole role = roleFactory.getMutableRole(roleID);

        try {
          role.removeAttributeValue(PermissionRoles.ROLE_NAMESPACE, PermissionRoles.ACTIONS, actionId);

          role.save();
          role.commit();
        } catch (UMException ex) {
          role.rollback();
          throw new SecurityException(ex.getMessage(), ex);
        }
      }
    } catch (UMException e) {
      throw new SecurityException(e.getMessage(), e);
    }
  }
}
