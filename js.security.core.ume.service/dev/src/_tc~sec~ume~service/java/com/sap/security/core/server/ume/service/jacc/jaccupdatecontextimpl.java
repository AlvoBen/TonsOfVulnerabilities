package com.sap.security.core.server.ume.service.jacc;

import com.sap.engine.interfaces.security.JACCSecurityRoleMappingContext;
import com.sap.engine.interfaces.security.JACCUpdateContext;
import com.sap.engine.interfaces.security.SecurityRoleContext;
import com.sap.security.api.IRole;
import com.sap.security.api.IRoleFactory;
import com.sap.security.api.IRoleSearchFilter;
import com.sap.security.api.ISearchAttribute;
import com.sap.security.api.ISearchResult;
import com.sap.security.api.NoSuchRoleException;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.imp.RoleSearchFilter;
import com.sap.security.core.role.ActionException;
import com.sap.security.core.role.IAction;
import com.sap.security.core.role.IActionFactory;
import com.sap.security.core.role.NoSuchActionException;
import com.sap.security.core.role.imp.Action;
import com.sap.security.core.role.imp.PermissionRoles;
import com.sap.security.core.role.persistence.PersistenceException;
import com.sap.security.core.role.persistence.PersistenceLayer;
import com.sap.security.core.role.persistence.PrincipalMappingPersistenceManager;

import java.util.Set;
import java.util.HashSet;


public class JACCUpdateContextImpl implements JACCUpdateContext {
  private String policyConfiguration = null;
  private JaccPermissionManager permissionManager = new JaccPermissionManager();
  
  public JACCUpdateContextImpl(String policyConfiguration) {
    this.policyConfiguration = policyConfiguration;
  }

  public void jaccRoleAdded(String securityRole, String[] umeRoles) throws SecurityException {
    int status = -1;
    try {
      status = PersistenceLayer.getPolicyConfigurationStatus(policyConfiguration);
    } catch (Exception e) {
      throw new SecurityException("Unable to determine which persistence maintains context ID '" + policyConfiguration + "'!", e);
    }
    
    if (status == PersistenceLayer.STATUS_JPL_PERSISTENCE) {
      PrincipalMappingPersistenceManager manager = PrincipalMappingPersistenceManager.getManager();

      try {
        
        for (int i = 0; i < umeRoles.length; i++) {
          String umeRoleName = convert640to700SecurityRoleConvetion(umeRoles[i]);
          
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

          manager.assignAppRoleToServerRole(policyConfiguration, securityRole, umeRoleName);
        }
        manager.commit();
        
      } catch (PersistenceException pe) {
        throw new SecurityException("Unable to commit changes for policy configuration with context ID '" + policyConfiguration + "'!", pe);
      }
      
    } else {
    
      IAction action = null;
          
      try {
        action = InternalUMFactory.getActionFactory().newAction(policyConfiguration, IAction.TYPE_J2EE_ROLE, securityRole);
      } catch (ActionException ae) {
        try {
          action = InternalUMFactory.getActionFactory().getAction(policyConfiguration, IAction.TYPE_J2EE_ROLE, securityRole);
        } catch (NoSuchActionException ne) {
          throw new SecurityException(ne.getMessage(), ne);
        } catch (ActionException aex) {
          throw new SecurityException(aex.getMessage(), aex);
        }
      } 
      
      for (int i = 0; i < umeRoles.length; i++) {
        IRole role = null;
  
        String umeRole700Style = convert640to700SecurityRoleConvetion(umeRoles[i]);      
        try {
          role = UMFactory.getRoleFactory().getRoleByUniqueName(umeRole700Style);
          role = UMFactory.getRoleFactory().getMutableRole(role.getUniqueID());
        } catch (NoSuchRoleException nsre) {
          try {
            role = UMFactory.getRoleFactory().newRole(umeRole700Style);
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
    }
  }
  
  public void jaccRoleRemoved(String securityRole) throws SecurityException {
    
    int status = -1;
    try {
      status = PersistenceLayer.getPolicyConfigurationStatus(policyConfiguration);
    } catch (Exception e) {
      throw new SecurityException("Unable to determine which persistence maintains context ID '" + policyConfiguration + "'!", e);
    }
    
    if (status == PersistenceLayer.STATUS_JPL_PERSISTENCE) {
      PrincipalMappingPersistenceManager manager = PrincipalMappingPersistenceManager.getManager();
      try {
        manager.removeAppRoleAssignments(policyConfiguration, securityRole);
        manager.commit();
      } catch (PersistenceException pe) {
        throw new SecurityException("Unable to commit changes for policy configuration with context ID '" + policyConfiguration + "'!", pe);
      }
      
    } else {
      
      // remove all assignments for the given Action (of type J2EE role) of this policy configuration
      IRoleFactory rf = UMFactory.getRoleFactory();
      String actionID = IActionFactory.ACTION_TYPE + "." + IActionFactory.DS_NAME + 
        ".un:" + IAction.TYPE_J2EE_ROLE + Action.SEPARATOR + Action.escapeString(policyConfiguration) +
        Action.SEPARATOR + Action.escapeString(securityRole);
      
      IRoleSearchFilter sf = new RoleSearchFilter();
      sf.setSearchAttribute(PermissionRoles.ROLE_NAMESPACE, PermissionRoles.ACTIONS, actionID, ISearchAttribute.EQUALS_OPERATOR, false);
      try {
        ISearchResult sr = rf.searchRoles(sf);
        while (sr.hasNext()) {
          IRole role = rf.getMutableRole((String) sr.next());
          String[] actions = role.getAttribute(PermissionRoles.ROLE_NAMESPACE, PermissionRoles.ACTIONS);
          int nrOfActions = (actions != null) ? actions.length : 0;
     
          // check for the actionID 
          for (int i=0; i < nrOfActions; i++) {
            if (actions[i].equals(actionID)) {
              role.removeAttributeValue(PermissionRoles.ROLE_NAMESPACE, PermissionRoles.ACTIONS, actions[i]);
              break;
            }
          }
          role.save();
          role.commit();
        }
      } catch (UMException exc) {
        throw new SecurityException(exc.getMessage(), exc);
      }
    }
  }
  
  public void jaccRoleMappingsChanged(String securityRole, String[] newUmeRoles) throws SecurityException {
    jaccRoleRemoved(securityRole);
    jaccRoleAdded(securityRole, newUmeRoles);
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
  
  /**
   * Retrieve the PolicyConfigurations of the modules with a specified application type, registered within this application
   */
  public Set getApplicationModulesNames(String applicationName) throws SecurityException {
  	//todo
  	return new HashSet();
  }
  
  /**
   * Retrieve the names of the j2ee roles currently deployed for a specified module within this application
   */
  public Set getApplicationModuleRolesNames(String applicationName, String moduleName) throws SecurityException {
  	//todo
  	return new HashSet();
  }
  
  /**
   * Retrieve the names of the UME roles currently mapped to a j2ee role deployed for a specified module within this application
   */
  public Set getApplicationModuleMappedServerRoles(String applicationName, String moduleName, String j2eeRoleName) throws SecurityException {
    //todo
  	return new HashSet();
  }
  
  public void migrateJaccPolicyConfiguration() {
    permissionManager.migratePolicyConfiguration(policyConfiguration);
  }
}
