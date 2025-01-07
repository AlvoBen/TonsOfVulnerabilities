package com.sap.engine.services.security.roles;

import java.util.HashSet;
import java.util.Iterator;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.interfaces.security.SecurityRole;
import com.sap.engine.interfaces.security.SecurityRoleContext;
import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.userstore.context.GroupContext;
import com.sap.engine.interfaces.security.userstore.context.GroupInfo;
import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.interfaces.security.userstore.context.UserInfo;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.restriction.Restrictions;
import com.sap.engine.services.security.server.ConfigurationLock;
import com.sap.engine.services.security.server.ModificationContextImpl;
import com.sap.engine.services.security.server.SecurityConfigurationPath;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class SecurityRoleContextImpl implements SecurityRoleContext {
  
  private static final Location TRACER = Location.getLocation(SecurityRoleContextImpl.class); 
  
  private static final byte CREATE_RUN_AS_IDENTITY_CONFIG = 0;
  private static final byte GENERATE_RUN_AS_IDENTITY = 1;
  private static final byte ADD_RUN_AS_IDENTITY_TO_ROLE = 2;

  private static final String RUN_AS_LOCK_NAME = "_security_run_as";
  private byte run_as_state;

  protected String      policyConfiguration = null;
  private   String      path = null;
  private   com.sap.engine.interfaces.security.SecurityContext root = null;

  public SecurityRoleContextImpl(com.sap.engine.interfaces.security.SecurityContext root, String config, String userStore) {
    this.root = root;
    this.policyConfiguration = config;
    this.path = SecurityConfigurationPath.ROLES_PATH + "/" + userStore;
  }

  /**
   *  Adds a security role for the application.
   *
   * @param  securityRole  the name of the security role.
   *
   * @return  the new security role
   */
  public SecurityRole addSecurityRole(String securityRole) {
    Restrictions.checkPermission(Restrictions.COMPONENT_SECURITY_ROLES, Restrictions.RESTRICTION_ADD_SECURITY_ROLE, policyConfiguration);
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration configuration = mc.getConfiguration(path, true, true);

      if (SecurityRoleSerializator.existsSecurityRole(configuration, securityRole)) {
        throw new SecurityException("Security role with alias " + securityRole + " already exists.");
      }

      SecurityRoleImpl role = new SecurityRoleImpl(securityRole);
      role.setContext(this);

      SecurityRoleSerializator.storeSecurityRole(configuration, role);
      mc.commitModifications();
      return role;
    } catch (SecurityException se) {
      mc.rollbackModifications();
      throw new SecurityException("Cannot add security role.", se);
    }
  }

  /**
   *  Adds a security role for the application.
   *
   * @param  roleName             the name of the new security role.
   * @param  policyConfiguration  the policy configuration that contain the security role.
   * @param  securityRole         the name of the security role in the given policy configuration.
   *
   * @return  the new security role
   */
  public SecurityRole addSecurityRoleReference(String roleName, String policyConfiguration, String securityRole) {
    Restrictions.checkPermission(Restrictions.COMPONENT_SECURITY_ROLES, Restrictions.RESTRICTION_ADD_SECURITY_ROLE, policyConfiguration);
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      SecurityContext policy = root.getPolicyConfigurationContext(policyConfiguration);

      if (policy == null) {
        throw new SecurityException("Cannot add reference to security role in non-existent policy configuration [" + policyConfiguration + "].");
      }

      SecurityRole reference = policy.getAuthorizationContext().getSecurityRoleContext().getSecurityRole(securityRole);

      if (reference == null) {
        throw new SecurityException("Cannot add reference to non-existent security role [" + securityRole + "].");
      }
      Configuration configuration = mc.getConfiguration(path, true, true);

      if (SecurityRoleSerializator.existsSecurityRole(configuration, roleName)) {
        throw new SecurityException("Security role with alias [" + roleName + "] already exists.");
      }

      SecurityRoleSerializator.storeSecurityRole(configuration, new SecurityRoleReference(roleName, policyConfiguration, reference));

      mc.commitModifications();

      return new SecurityRoleReference(roleName, policyConfiguration, reference);
    } catch (SecurityException se) {
      mc.rollbackModifications();
      throw new SecurityException("Cannot add security role reference.", se);
    }
  }


  /**
   *  Lists all registered security roles of this component.
   *
   * @return  list of the registered security roles.
   */
  public SecurityRole[] listSecurityRoles() {
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      int compact = 0;
      Configuration configuration = mc.getConfiguration(path, false, false);
      if (configuration == null) {
        return new SecurityRole[0];
      }
      String[] names = configuration.getAllSubConfigurationNames();
      SecurityRole[] all = new SecurityRole[names.length];

      for (int i = 0; i < names.length; i++) {
        all[i] = SecurityRoleSerializator.loadSecurityRole(root, configuration, names[i]);

        if (all[i] == null) {
          compact++;
        } else if (all[i] instanceof SecurityRoleImpl) {
          ((SecurityRoleImpl) all[i]).setContext(this);
        }
      }

      if (compact > 0) {
        SecurityRole[] temp = new SecurityRole[all.length - compact];

        for (int i = 0, j = 0; i < temp.length; i++) {
					if (all[j] != null) {
						temp[i] = all[j];
					} else {
						i--;
					}
					j++;
        }

        all = temp;
      }

      return all;
    } catch (Exception se) {
      throw new SecurityException("Cannot list security roles.", se);
    } finally {
      mc.forgetModifications();
    }
  }

  /**
   *  Lists all registered security roles for the specified group.
   *
   * @param  groupName  group name
   *
   * @return  list of the registered security roles.
   */
  public SecurityRole[] listSecurityRolesOfGroup(String groupName) {
    throw new UnsupportedOperationException("Method was deprecated and removed in a previous version!");
  }

  /**
   *  Lists all registered security roles for the specified user.
   *
   * @param  userName  user name
   *
   * @return  list of the registered security roles.
   */
  public SecurityRole[] listSecurityRolesOfUser(String userName) {
    throw new UnsupportedOperationException("Method was deprecated and removed in a previous version!");
  }

  /**
   *  Removes a security role for the component.
   *
   * @param  securityRole  the name of the security role.
   */
  public void removeSecurityRole(String securityRole) {
    Restrictions.checkPermission(Restrictions.COMPONENT_SECURITY_ROLES, Restrictions.RESTRICTION_REMOVE_SECURITY_ROLE, policyConfiguration);
    ModificationContextImpl mc = (ModificationContextImpl)root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration configuration = mc.getConfiguration(path, true, false);

      if (!SecurityRoleSerializator.existsSecurityRole(configuration, securityRole)) {
        throw new SecurityException("Security role with alias [" + securityRole + "] does not exist.");
      }

      SecurityRoleSerializator.removeSecurityRole(configuration, securityRole);
      mc.commitModifications();
    } catch (SecurityException se) {
      mc.rollbackModifications();
      throw new SecurityException("Cannot remove security role.", se);
    }
  }

  public SecurityRole getSecurityRole(String securityRole) {
    if (SecurityServerFrame.isEmergencyMode()) {
      ModificationContextImpl mc = (ModificationContextImpl)root.getModificationContext();
      mc.beginModifications();

      try {
        Configuration configuration = mc.getConfiguration(path, false, false);

        if (!SecurityRoleSerializator.existsSecurityRole(configuration, securityRole)) {
          return null;
        }      
      } catch (Exception e) {
        return null;
      } finally {
        mc.rollbackModifications();
      }
      String emergencyUserName = root.getUserStoreContext().getActiveUserStore().getUserContext().getEmergencyUserName();
      SecurityRole emergencyRole = new SecurityRoleImpl(securityRole, new String[] {emergencyUserName}, new String[0]);
      ((SecurityRoleImpl) emergencyRole).setContext(this);
      return emergencyRole;
    }
    return readSecurityRole(securityRole);
  }

  /**
   *  This method reads the definition of security role from configuration manager.
   * It avoids the case of emergency user store.
   *
   * @param securityRole  the name of the security role to be read.
   *
   * @return  an instance of a security role
   */
  SecurityRole readSecurityRole(String securityRole) {
    ModificationContextImpl mc = (ModificationContextImpl)root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration configuration = mc.getConfiguration(path, false, false);

      if (!SecurityRoleSerializator.existsSecurityRole(configuration, securityRole)) {
        return null;
      }

      SecurityRole role = SecurityRoleSerializator.loadSecurityRole(root, configuration, securityRole);

      if (role instanceof SecurityRoleImpl) {
        ((SecurityRoleImpl) role).setContext(this);
      } else if (role instanceof SecurityRoleReference) {
        ((SecurityRoleReference) role).setContext(this);
      }

      return role;
    } catch (SecurityException se) {
      throw new SecurityException("Cannot get security role.", se);
    } finally {
      mc.forgetModifications();
    }
  }

  void refreshSecurityRole(SecurityRole role) {
      ModificationContextImpl mc = (ModificationContextImpl)root.getModificationContext();
      mc.beginModifications();

      try {
        Configuration configuration = mc.getConfiguration(path, false, false);

        if (!SecurityRoleSerializator.existsSecurityRole(configuration, role.getName())) {
          return;
        }

        SecurityRoleSerializator.refreshSecurityRole(root, configuration, role);

        if (role instanceof SecurityRoleImpl) {
          ((SecurityRoleImpl) role).setContext(this);
        }
      } catch (SecurityException se) {
        throw new SecurityException("Cannot get security role.", se);
      } finally {
        mc.forgetModifications();
      }
  }

  protected void modifySecurityRole(SecurityRole role) {
    Restrictions.checkPermission(Restrictions.COMPONENT_SECURITY_ROLES, Restrictions.RESTRICTION_MODIFY_SECURITY_ROLE, policyConfiguration);
    if (role instanceof SecurityRoleReference) {
      throw new SecurityException("Cannot modify referenced security role.");
    }
    ModificationContextImpl mc = (ModificationContextImpl)root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration configuration = mc.getConfiguration(path, true, false);
      if (!SecurityRoleSerializator.existsSecurityRole(configuration, role.getName())) {
        throw new SecurityException("Security role with alias [" + role.getName() + "] does not exist.");
      }
      SecurityRoleSerializator.storeSecurityRole(configuration, role);
      mc.commitModifications();
    } catch (SecurityException se) {
      mc.rollbackModifications();
      throw new SecurityException("Cannot modify security role.", se);
    }
  }

  protected void modifySecurityRole(SecurityRole role, Configuration configuration) {
    Restrictions.checkPermission(Restrictions.COMPONENT_SECURITY_ROLES, Restrictions.RESTRICTION_REMOVE_SECURITY_ROLE, policyConfiguration);
    modifySecurityRoleInternal(role, configuration);
  }

 protected void modifySecurityRoleInternal(SecurityRole role, Configuration configuration) {
    if (role instanceof SecurityRoleReference) {
      throw new SecurityException("Cannot modify referenced security role.");
    }
    if (!SecurityRoleSerializator.existsSecurityRole(configuration, role.getName())) {
      throw new SecurityException("Security role with alias [" + role.getName() + "] does not exist.");
    }
    SecurityRoleSerializator.storeSecurityRole(configuration, role);
  }

//////////////////////////////////////////////////////////////////////////////////


  protected static String getCallerName() {
    ThreadContext threadContext = SecurityServerFrame.threadContext.getThreadContext();
    if (threadContext == null) {
      return null;
    }
    SecurityContextObject sc = (SecurityContextObject) threadContext.getContextObject("security");
    if (sc == null) {
      return null;
    }
    java.security.Principal pr = sc.getSession().getPrincipal();
    return (pr == null?null:pr.getName());
  }

  protected UserStoreFactory getUserStoreFactory() {
    return root.getUserStoreContext();
  }

  protected void setRunAsIdentity(SecurityRole role, String principal) {
    Restrictions.checkPermission(Restrictions.COMPONENT_SECURITY_ROLES, Restrictions.RESTRICTION_MODIFY_SECURITY_ROLE, policyConfiguration);

//    if (role instanceof SecurityRoleReference) {
//      throw new BaseSecurityException(BaseSecurityException.CANNOT_MODIFY_ROLE);
//    }

    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration configuration = mc.getConfiguration(path, true, false);

      if (!SecurityRoleSerializator.existsSecurityRole(configuration, role.getName())) {
        throw new SecurityException("Security role with alias [" + role.getName() + "] does not exist.");
      }
      SecurityRoleSerializator.setRunAsIdentityToSecurityRole(configuration, role, principal);
      mc.commitModifications();
    } catch (SecurityException se) {
      mc.rollbackModifications();
      throw new SecurityException("Cannot set run_as identity for security role [" + role.getName() + "].", se);
    }
  }

  protected void setRunAsAccountGenerationPolicy(SecurityRole role, byte type) {
    Restrictions.checkPermission(Restrictions.COMPONENT_SECURITY_ROLES, Restrictions.RESTRICTION_MODIFY_SECURITY_ROLE, policyConfiguration);
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration configuration = mc.getConfiguration(path, true, false);

      if (!SecurityRoleSerializator.existsSecurityRole(configuration, role.getName())) {
        throw new SecurityException("Security role with alias [" + role.getName() + "] does not exist.");
      }
      if (type < SecurityRole.RUN_AS_CREATE_ACCOUNT || type > SecurityRole.RUN_AS_CREATION_FORBIDDEN) {
        type = SecurityRole.RUN_AS_CREATION_FORBIDDEN;
      }
      SecurityRoleSerializator.setRunAsAccountGenerationPolicy(configuration, role, type);

      mc.commitModifications();
    } catch (SecurityException se) {
      mc.rollbackModifications();
      throw new SecurityException("Cannot set run_as account generation policy for security role [" + role.getName() + "].", se);
    }
  }

  protected String getRunAsIdentity(SecurityRole role, boolean forceAssociation) {
    String runAsIdentity = null;
    run_as_state = -1;
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();
    Configuration configuration = null;
    try {
      if (TRACER.beDebug()) {
        TRACER.logT(Severity.DEBUG, "Check if there is RunAsIdentity in the data base");
      }
      
      configuration = mc.getConfiguration(path, false, false);
      
      if (TRACER.beDebug()) {
        TRACER.logT(Severity.DEBUG, "Open Configuration {0}", new Object[] {configuration.getPath()});
      }
      
      if (!SecurityRoleSerializator.existsSecurityRole(configuration, role.getName())) {
        if (TRACER.beDebug()) {
          TRACER.logT(Severity.DEBUG, "Security Role {0} does not exist in Configuration {1}. Throwing exception", new Object[] {role.getName(), configuration.getPath()});
        }
        
        throw new SecurityException("Role [" + role.getName() + "] does not exist.");
      }
      
      configuration = configuration.getSubConfiguration(role.getName());
      
      if (TRACER.beDebug()) {
        TRACER.logT(Severity.DEBUG, "Open Sub-Configuration {0}", new Object[] {configuration.getPath()});
      }
      
      if (configuration.existsConfigEntry(SecurityRoleSerializator.RUN_AS_IDENTITY)) {
        if (TRACER.beDebug()) {
          TRACER.logT(Severity.DEBUG, "Configuration Entry {0} exists in Configuration {1}", new Object[] {SecurityRoleSerializator.RUN_AS_IDENTITY, configuration.getPath()});
        }
        
        if (!forceAssociation) {
          return (String) configuration.getConfigEntry(SecurityRoleSerializator.RUN_AS_IDENTITY);
        } else {
          String principal = (String) configuration.getConfigEntry(SecurityRoleSerializator.RUN_AS_IDENTITY);
          
          if (TRACER.beDebug()) {
            TRACER.logT(Severity.DEBUG, "RunAsIdentity {0} found for Role {1}. Check if it is valid", new Object[] {principal, role});
          }
          
          if (isValidRunAsPrincipal(principal, role)) {
            if (TRACER.beDebug()) {
              TRACER.logT(Severity.DEBUG, "{0} is valid RunAsIdentity for Role {1}", new Object[] {principal, role});
            }
            
            return principal;
          } else {
            if (TRACER.beDebug()) {
              TRACER.logT(Severity.DEBUG, "{0} is not valid RunAsIdentity for Role {1}", new Object[] {principal, role});
            }
            
            run_as_state = GENERATE_RUN_AS_IDENTITY;
          }
        }
      } else {
        if (TRACER.beDebug()) {
          TRACER.logT(Severity.DEBUG, "Configuration Entry {0} does not exist in Configuration {1}", new Object[] {SecurityRoleSerializator.RUN_AS_IDENTITY, configuration.getPath()});
        }
        
        if (!forceAssociation) {
          return null;
        } else {
          run_as_state = CREATE_RUN_AS_IDENTITY_CONFIG;
        }
      }
    } catch (Exception e) {
      if (TRACER.beDebug()) {
        TRACER.traceThrowableT(Severity.DEBUG, "Exception during searching for run-as identity in the data base:", e);
      }
    } finally {
      mc.forgetModifications();
    }
    if (run_as_state > -1) {
      if (TRACER.beDebug()) {
        TRACER.logT(Severity.DEBUG, "Generate new RunAsIdentity for Role {0}", new Object[] {role});
      }
      
      ConfigurationLock locker = null;
      try {
        locker = new ConfigurationLock();
        
        if (TRACER.beDebug()) {
          TRACER.logT(Severity.DEBUG, "Lock Configuration {0}", new Object[] {policyConfiguration});
        }
        
        locker.lock(policyConfiguration, RUN_AS_LOCK_NAME);
        configuration = mc.getConfiguration(path, true, false);
        
        if (TRACER.beDebug()) {
          TRACER.logT(Severity.DEBUG, "Open Configuration {0}", new Object[] {configuration.getPath()});
        }
        
        Configuration roleConfiguration = configuration.getSubConfiguration(role.getName());
        
        if (TRACER.beDebug()) {
          TRACER.logT(Severity.DEBUG, "Open Sub-Configuration {0}", new Object[] {roleConfiguration.getPath()});
        }
        
        byte state = run_as_state;
        runAsIdentity = generateValidRunAsPrincipal(roleConfiguration, role);
        
        if (runAsIdentity != null) {
          if (TRACER.beDebug()) {
            TRACER.logT(Severity.DEBUG, "Generated valid RunAsIdentity {0}", new Object[] {runAsIdentity});
          }
          
          if (state == GENERATE_RUN_AS_IDENTITY) {
            roleConfiguration.modifyConfigEntry(SecurityRoleSerializator.RUN_AS_IDENTITY, runAsIdentity);
          } else if (state == CREATE_RUN_AS_IDENTITY_CONFIG) {
            if (roleConfiguration.existsConfigEntry(SecurityRoleSerializator.RUN_AS_IDENTITY)) {
              //On another cluster node the run_as identity is generated in the meantime.
              //That can happen on simultaneous starting of an application on several cluster nodes.
              runAsIdentity = (String) roleConfiguration.getConfigEntry(SecurityRoleSerializator.RUN_AS_IDENTITY);
              
              if (TRACER.beDebug()) {
                TRACER.logT(Severity.DEBUG, "RunAsIdentity {0} created in the meantime by another server node - roll back this one", new Object[] {runAsIdentity});
              }
              
              mc.rollbackModifications();
              return runAsIdentity;
            }
            
            if (TRACER.beDebug()) {
              TRACER.logT(Severity.DEBUG, "Adding this RunAsIdentity to the data base");
            }
            
            roleConfiguration.addConfigEntry(SecurityRoleSerializator.RUN_AS_IDENTITY, runAsIdentity);
          }
          
          if (run_as_state == ADD_RUN_AS_IDENTITY_TO_ROLE) {
            ((SecurityRoleImpl) role).addUserInternal(runAsIdentity, configuration);
          }
          
          if (TRACER.beDebug()) {
            TRACER.logT(Severity.DEBUG, "Commit RunAsIdentity modifications");
          }
          
          mc.commitModifications();
        } else {
          if (TRACER.beDebug()) {
            TRACER.logT(Severity.DEBUG, "Generated RunAsIdentity is null - roll back the modifications");
          }
          
          mc.rollbackModifications();
        }
      } catch (Exception se) {
        mc.rollbackModifications();
        throw new SecurityException("Cannot retrieve run-as identity for security role: " + role.getName());
      } finally {
        if (locker != null) {
          if (TRACER.beDebug()) {
            TRACER.logT(Severity.DEBUG, "Release locked configuration {0}", new Object[] {policyConfiguration});
          }
          
          locker.releaseLock(policyConfiguration, RUN_AS_LOCK_NAME);
        }
      }
    }

    return runAsIdentity;
  }

  private boolean isValidRunAsPrincipal(String principal, SecurityRole role) {
    UserInfo info = null;
    try {
      info = root.getUserStoreContext().getActiveUserStore().getUserContext().getUserInfo(principal);
      
      if (info == null) {
        if (TRACER.beDebug()) {
          TRACER.logT(Severity.DEBUG, "User info for user {0} is null - such user does not exist", new Object[] {principal});
        }
        
        return false;
      }
      
      if (TRACER.beDebug()) {
        TRACER.logT(Severity.DEBUG, "Found user info for user {0}", new Object[] {principal});
      }
    } catch (SecurityException e) {
      if (TRACER.beDebug()) {
        TRACER.traceThrowableT(Severity.DEBUG, "Can not get user info for user {0} - such user does not exist", new Object[] {principal}, e);
      }
      
      return false;
    }
    return isUserFromRole(principal, role);
  }

  private String generateValidRunAsPrincipal(Configuration roleConfiguration, SecurityRole role) {
    byte runAsGenerationPolicy;
    try {
      if (roleConfiguration.existsConfigEntry(SecurityRoleSerializator.RUN_AS_GENERATION_POLICY)) {
        runAsGenerationPolicy = ((Byte) roleConfiguration.getConfigEntry(SecurityRoleSerializator.RUN_AS_GENERATION_POLICY)).byteValue();
      } else {
        runAsGenerationPolicy = SecurityRole.RUN_AS_CREATION_FORBIDDEN;
      }
    } catch (Exception _) {
      runAsGenerationPolicy = SecurityRole.RUN_AS_CREATION_FORBIDDEN;
    }
    String principal = null;
    if (runAsGenerationPolicy == SecurityRole.RUN_AS_CREATION_FORBIDDEN) {
      principal = getUserFromRole(role);
    } else {
      if (runAsGenerationPolicy == SecurityRole.RUN_AS_GET_FROM_MAPPINGS) {
        principal = getUserFromRole(role);
        if (principal != null) {
          return principal;
        }
      }
      try {
        if (role instanceof SecurityRoleReference) {
          throw new SecurityException("Cannot modify referenced security role.");
        }
        UserContext context = root.getUserStoreContext().getActiveUserStore().getUserContext();
        principal = context.getFilterUsername().generateUsername();
        UserInfo userInfo = ((com.sap.engine.services.security.userstore.context.UserContext) context).createUserInternal(principal);
        principal = userInfo.getName();//ABAP persistence is not case sensitive
        run_as_state = ADD_RUN_AS_IDENTITY_TO_ROLE;
      } catch (Exception e) {
        throw new SecurityException("Cannot generate run_as identity for the security role [" + role.getName() + "].", e);
      }
    }
    return principal;
  }

  private String getUserFromRole(SecurityRole role) {
    if (SecurityServerFrame.isEmergencyMode()) {
      return root.getUserStoreContext().getActiveUserStore().getUserContext().getEmergencyUserName();
    }
    
    UserContext userContext = root.getUserStoreContext().getActiveUserStore().getUserContext();
    String[] users = role.getUsers();
    for (int i = 0; i < users.length; i++) {
      try {
        return userContext.getUserInfo(users[i]).getName();
      } catch (Exception _) {
        //$JL-EXC$
        // fake user mapping, trying the next
      }
    }

		String[] groups = role.getGroups();
		if (groups.length > 0) {
			GroupContext ctx = root.getUserStoreContext().getActiveUserStore().getGroupContext();
			HashSet checkedGroups = new HashSet();
			for (int i = 0; i < groups.length; i++) {
				String userName = getUserFromGroup(groups[i], ctx, checkedGroups);
				if (userName != null) {
					return userName;
				}
			}
		}
		return null;
  }

  private String getUserFromGroup(String group, GroupContext ctx, HashSet checkedGroups) {
    UserContext userContext = root.getUserStoreContext().getActiveUserStore().getUserContext();
		Iterator users = ctx.getUsersOfGroup(group);
    while (users.hasNext()) {
      try {
        return userContext.getUserInfo((String) users.next()).getName();
      } catch (Exception _) {
        //$JL-EXC$
        // fake user mapping, trying the next
      }
    }
    
		Iterator groups = ctx.getChildGroups(group);
		String nextGroup = null;
		while (groups.hasNext()) {
			nextGroup = (String) groups.next();
			if (checkedGroups.contains(nextGroup)) {
				continue;
			} else {
				checkedGroups.add(nextGroup);
				String user = getUserFromGroup(nextGroup, ctx, checkedGroups);
				if (user != null) {
					return user;
				}
			}
		}
		return null;
	}

  private boolean isUserFromRole(String userName, SecurityRole role) {
    if (TRACER.beDebug()) {
      TRACER.logT(Severity.DEBUG, "Check if User {0} is from Role {1}", new Object[] {userName, role});
    }
    
    if (TRACER.beDebug()) {
      TRACER.logT(Severity.DEBUG, "Getting users of Role {0}", new Object[] {role});
    }
    
    String[] mappedUsers = role.getUsers();
    
    if (TRACER.beDebug()) {
      TRACER.logT(Severity.DEBUG, "Iterate over the users of Role {0}", new Object[] {role});
    }
    
    for (int i = 0; i < mappedUsers.length; i++) {
      if (mappedUsers[i].equals(userName)) {
        if (TRACER.beDebug()) {
          TRACER.logT(Severity.DEBUG, "User {0} is from Role {1}", new Object[] {mappedUsers[i], role});
        }
        
        return true;
      }
    }
    
    if (TRACER.beDebug()) {
      TRACER.logT(Severity.DEBUG, "Getting groups of Role {0}", new Object[] {role});
    }
    
    String[] groups = role.getGroups();
    
    if (TRACER.beDebug()) {
      TRACER.logT(Severity.DEBUG, "Role {0} has groups {1}", new Object[] {role, groups});
    }
    
    if (groups.length > 0) {
      GroupContext groupContext = root.getUserStoreContext().getActiveUserStore().getGroupContext();
      UserContext userContext = root.getUserStoreContext().getActiveUserStore().getUserContext();
      
      if (TRACER.beDebug()) {
        TRACER.logT(Severity.DEBUG, "Iterate over the groups of Role {0}", new Object[] {role});
      }
      
      for (int i = 0; i < groups.length; i++) {
        if (isUserFromGroup(userName, groups[i], userContext, groupContext)) {
          return true;
        }
      }
    }
    
    return false;
  }
  
  private boolean isUserFromGroup(String user, String parentGroup, UserContext userContext, GroupContext groupContext) {
    if (TRACER.beDebug()) {
      TRACER.logT(Severity.DEBUG, "Check if User {0} is from Group {1}", new Object[] {user, parentGroup});
    }
    
    boolean res = false;
    UserInfo userInfo = userContext.getUserInfo(user);
    
    if (TRACER.beDebug()) {
      TRACER.logT(Severity.DEBUG, "Iterate over the parent groups of User {0}", new Object[] {user});
    }
    
    for (Iterator iter = userInfo.getParentGroups(); iter.hasNext();) {
      String nextGroup = (String) iter.next();
      
      if (TRACER.beDebug()) {
        TRACER.logT(Severity.DEBUG, "Parent Group {0} found for User {1}", new Object[] {nextGroup, user});
      }
      
      if (TRACER.beDebug()) {
        TRACER.logT(Severity.DEBUG, "Check if Group {0} is parent of Group {1}", new Object[] {parentGroup, nextGroup});
      }
      
      if (isParentGroup(parentGroup, nextGroup, groupContext)) {
        if (TRACER.beDebug()) {
          TRACER.logT(Severity.DEBUG, "Group {0} is parent of Group {1}", new Object[] {parentGroup, nextGroup});
        }
        
        res = true;
        break;
      }
    }
    
    if (TRACER.beDebug()) {
      if (res) {
        TRACER.logT(Severity.DEBUG, "User {0} is from Group {1})", new Object[] {user, parentGroup});
      } else {
        TRACER.logT(Severity.DEBUG, "User {0} is not from Group {1})", new Object[] {user, parentGroup});
      }
    }
    
    return res;
  }
  
  private boolean isParentGroup(String parentGroup, String group, GroupContext groupContext) {
    boolean res = false;
    
    if(parentGroup.equalsIgnoreCase(group)) {
      if (TRACER.beDebug()) {
        TRACER.logT(Severity.DEBUG, "Group {0} equals Group {1}", new Object[] {parentGroup, group});
      }
      
      res = true;
    } else {
      if (TRACER.beDebug()) {
        TRACER.logT(Severity.DEBUG, "Iterate over the parent groups of Group {0}", new Object[] {group});
      }
      
      GroupInfo groupInfo = groupContext.getGroupInfo(group);
      
      for (Iterator iter = groupInfo.getParentGroups(); iter.hasNext();) {
        String nextGroup = (String) iter.next();
        
        if (TRACER.beDebug()) {
          TRACER.logT(Severity.DEBUG, "Check if Group {0} is parent of Group {1}", new Object[] {parentGroup, nextGroup});
        }
        
        if (isParentGroup(parentGroup, nextGroup, groupContext)) {
          res = true;
          break;
        }
      }
    }
    
    return res;
  }
  
}
