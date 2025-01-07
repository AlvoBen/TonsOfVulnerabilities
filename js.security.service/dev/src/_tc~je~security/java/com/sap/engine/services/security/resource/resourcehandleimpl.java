package com.sap.engine.services.security.resource;

import java.security.Permission;
import java.util.Vector;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.SecurityRole;
import com.sap.engine.interfaces.security.SecurityRoleContext;
import com.sap.engine.interfaces.security.resource.ResourceAccessControlHandle;
import com.sap.engine.interfaces.security.resource.ResourceHandle;
import com.sap.engine.interfaces.security.resource.ResourceModificationListener;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.exceptions.StorageException;
import com.sap.engine.services.security.exceptions.StorageLockedException;
import com.sap.engine.services.security.login.AuthorizationEntry;
import com.sap.engine.services.security.login.ContextAuthorizationExtension;
import com.sap.engine.services.security.resource.action.ActionManager;
import com.sap.engine.services.security.resource.instance.ACLManager;
import com.sap.engine.services.security.resource.instance.InstanceManager;
import com.sap.engine.services.security.restriction.Restrictions;
import com.sap.engine.services.security.roles.SecurityRoleImpl;
import com.sap.engine.services.security.server.ModificationContextImpl;
import com.sap.engine.services.security.server.SecurityConfigurationPath;
import com.sap.tc.logging.Severity;

public class ResourceHandleImpl implements ResourceHandle, ResourceAccessControlHandle {
  private String path = null;
  private InstanceManager instanceManager = null;
  private ACLManager      aclManager      = null;
  private ActionManager   actionManager   = null;
  private SecurityContext root = null;
  private SecurityRoleContext securityRoleContext = null;

  private String alias = null;
  private String policyConfiguration = null;

  public static String[] listResources(SecurityContext root) {
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration config = mc.getConfiguration(SecurityConfigurationPath.RESOURCES_PATH, false, false);

      return config.getAllSubConfigurationNames();
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "listResources", e);
    } finally {
      mc.forgetModifications();
    }

    return new String[0];
  }

  public static ResourceHandleImpl loadResourceHandle(SecurityContext root, String alias, SecurityRoleContext src, String policyConfiguration) throws ConfigurationException {
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration cont = mc.getConfiguration(SecurityConfigurationPath.RESOURCES_PATH + "/" + alias, false, false);
      if (cont == null) {
        return null;
      }

      return new ResourceHandleImpl(root, cont, alias, src, policyConfiguration);
    } finally {
      mc.forgetModifications();
    }
  }

  public static ResourceHandleImpl createResourceHandle(SecurityContext root, String alias, SecurityRoleContext src, String policyConfiguration) {
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration parent = mc.getConfiguration(SecurityConfigurationPath.RESOURCES_PATH, true, true);
      Configuration resource = parent.createSubConfiguration(alias);

      ResourceHandleImpl handle = new ResourceHandleImpl(root, resource, alias, src, policyConfiguration);
      mc.commitModifications();
      return handle;
    } catch (StorageLockedException sle) {
      mc.rollbackModifications();
      throw sle;
    } catch (SecurityException e) {
      mc.rollbackModifications();
      throw new SecurityException("Resource [" + alias + "] already exists!", e);
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "CREATE_RESOURCE_HANDLE", e);
      mc.rollbackModifications();
      return null;
    }
  }

  private ResourceHandleImpl(SecurityContext root, Configuration container, String alias, SecurityRoleContext src, String policyConfiguration) throws ConfigurationException {
    this.root = root;
    this.securityRoleContext = src;
    this.alias               = alias;
    this.policyConfiguration = policyConfiguration;
    this.path = SecurityConfigurationPath.RESOURCES_PATH + "/" + alias;
    instanceManager = new InstanceManager();
    instanceManager.prepareContainer(container);
    actionManager   = new ActionManager();
    actionManager.prepareContainer(container);
    aclManager      = new ACLManager();
    aclManager.prepareContainer(container);
  }

  protected void destroy() {
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration resource = mc.getConfiguration(path, true, false);
      resource.deleteConfiguration();
      mc.commitModifications();
    } catch (StorageLockedException sle) {
      mc.rollbackModifications();
      throw sle;
    } catch (SecurityException se) {
      mc.rollbackModifications();
      throw new SecurityException("Cannot destroy the resource", se);
    } catch (ConfigurationException ce) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "destroy", ce);
    }
  }
//////////////////////////////////////////////////////////////////////////
////////////////////////RESOURCE HANDLE///////////////////////////////////
//////////////////////////////////////////////////////////////////////////

  /**
   *  Use this constant when no specific instance or action is targeted.
   */
  public final static String ALL = "ALL";

  public void createInstance(String instance) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_RESOURCE_MANAGEMENT, Restrictions.RESTRICTION_MODIFY_RESOURCE, policyConfiguration);
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration resource = mc.getConfiguration(path, true, false);
      instanceManager.createInstance(instance, resource);
      mc.commitModifications();
    } catch (StorageLockedException sle) {
      mc.rollbackModifications();
      throw sle;
    } catch (SecurityException se) {
      mc.rollbackModifications();
      throw new SecurityException("Cannot create instance [" + instance + "]", se);
    } catch (ConfigurationException e) {
      mc.rollbackModifications();
      throw new StorageException("Cannot create instance [" + instance + "]", e);
    }
  }

  public void createAction(String action) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_RESOURCE_MANAGEMENT, Restrictions.RESTRICTION_MODIFY_RESOURCE, policyConfiguration);
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration resource = mc.getConfiguration(path, true, false);
      actionManager.createAction(action, resource);
      mc.commitModifications();
    } catch (StorageLockedException sle) {
      mc.rollbackModifications();
      throw sle;
    } catch (SecurityException se) {
      mc.rollbackModifications();
      throw new SecurityException("Cannot create action [" + action + "]", se);
    }
  }

  /**
   *  Closes the handle.
   */
  public void free() {
  }

  /**
   *  Returns the alias of the resource.
   *
   * @return  the name of the resource.
   */
  public String getAlias() {
    return alias;
  }

  public void registerListener(ResourceModificationListener listener, int modifiers) throws SecurityException {
    throw new SecurityException("Not supported");
  }

  public void removeInstance(String instance) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_RESOURCE_MANAGEMENT, Restrictions.RESTRICTION_MODIFY_RESOURCE, policyConfiguration);
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    Configuration resource = mc.getConfiguration(path, true, false);
    try {
      instanceManager.removeInstance(instance, resource);
      aclManager.instanceRemoved(instance, resource);
      mc.commitModifications();
    } catch (StorageLockedException sle) {
      mc.rollbackModifications();
      throw sle;
    } catch (SecurityException se) {
      mc.rollbackModifications();
      throw new SecurityException("Cannot remove instance [" + instance + "]", se);
    } catch (ConfigurationException e) {
      mc.rollbackModifications();
      throw new StorageException("Cannot remove instance [" + instance + "]", e);
    }
  }

  public void removeAction(String action) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_RESOURCE_MANAGEMENT, Restrictions.RESTRICTION_MODIFY_RESOURCE, policyConfiguration);
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration resource = mc.getConfiguration(path, true, false);
      actionManager.removeAction(action, resource);
      aclManager.actionRemoved(action, resource);
      mc.commitModifications();
    } catch (StorageLockedException sle) {
      mc.rollbackModifications();
      throw sle;
    } catch (SecurityException se) {
      mc.rollbackModifications();
      throw new SecurityException("Cannot remove action [" + action + "]", se);
    } catch (ConfigurationLockedException e) {
      mc.rollbackModifications();
      throw new StorageLockedException("Cannot remove action [" + action + "]", e);
    } catch (ConfigurationException e) {
      mc.rollbackModifications();
      throw new StorageException("Cannot remove action [" + action + "]", e);
    }
  }

  /**
   *  Renames the resource.
   *
   * @param  newAlias  the new alias of the resource
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public void renameResource(String newAlias) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_RESOURCE_MANAGEMENT, Restrictions.RESTRICTION_MODIFY_RESOURCE, policyConfiguration);
    throw new SecurityException("Not supported");
  }

  /**
   *  Returns a printable string representation of the resource.
   *
   * @return  a printable string.
   */
  public String toString() {
    return "resource : " + alias;
  }

  /**
   *  Unregisters the listener. It will be no longer notified of events.
   *
   * @param  listener  the listener to unregister.
   */
  public void unregisterListener(ResourceModificationListener listener) throws SecurityException {
    throw new SecurityException("Not supported");
  }

  /**
   * Groups instance with the specified parent instance.
   *
   * @param  instanceId       the identifier of the instance as registered with the resource handle.
   * @param  parentInstanceId the identifier of the instance as registered with the resource handle.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public void groupInstance(String instanceId, String parentInstanceId) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_RESOURCE_MANAGEMENT, Restrictions.RESTRICTION_MODIFY_RESOURCE, policyConfiguration);
    ModificationContextImpl mc = (ModificationContextImpl)root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration resource = mc.getConfiguration(path, true, false);
      instanceManager.group(instanceId, parentInstanceId, resource);
      mc.commitModifications();
    } catch (StorageLockedException sle) {
      mc.rollbackModifications();
      throw sle;
    } catch (SecurityException se) {
      mc.rollbackModifications();
      throw new SecurityException("Cannot group instance [" + instanceId + "] to [" + parentInstanceId + "]", se);
    } catch (ConfigurationLockedException e) {
      mc.rollbackModifications();
      throw new StorageLockedException("Cannot group instance [" + instanceId + "] to [" + parentInstanceId + "]", e);
    } catch (ConfigurationException e) {
      mc.rollbackModifications();
      throw new StorageException("Cannot group instance [" + instanceId + "] to [" + parentInstanceId + "]", e);
    }
  }

  /**
   * Ungroups instance from the parent instance.
   *
   * @param  instanceId  the identifier of the resource as registered with the resource context.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public void ungroupInstance(String instanceId) throws SecurityException {
    ModificationContextImpl mc = (ModificationContextImpl)root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration resource = mc.getConfiguration(path, true, false);
      instanceManager.ungroup(instanceId, resource);
      mc.commitModifications();
    } catch (StorageLockedException sle) {
      mc.rollbackModifications();
      throw sle;
    } catch (SecurityException se) {
      mc.rollbackModifications();
      throw new SecurityException("Cannot ungroup instance [" + instanceId + "]", se);
    } catch (ConfigurationException e) {
      mc.rollbackModifications();
      throw new StorageException("Cannot ungroup instance [" + instanceId + "]", e);
    }
  }

  /**
   *  Retrieves the identifier of the parent instance.
   *
   * @param   instanceId  id of the parent.
   *
   * @return int
   */
  public String getParent(String instanceId) {
    ModificationContextImpl mc = (ModificationContextImpl)root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration resource = mc.getConfiguration(path, false, false);
      String parent = instanceManager.getParent(instanceId, resource);
      return parent;
    } catch (StorageLockedException sle) {
      throw sle;
    } catch (SecurityException se) {
      throw new SecurityException("Cannot list the parent instance of instance [" + instanceId + "] the resource", se);
    } catch (ConfigurationException e) {
      throw new StorageException("Cannot list the parent instance of instance [" + instanceId + "] the resource", e);
    } finally {
      mc.forgetModifications();
    }
  }

  /**
   *  Retrieves all children of the instance.
   *
   * @param   instanceId  id of the instance.
   * @return  array of identifiers of all instances.
   */
  public String[] getChildren(String instanceId) {
    ModificationContextImpl mc = (ModificationContextImpl)root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration resource = mc.getConfiguration(path, false, false);
      String[] children = instanceManager.getChildren(instanceId, resource);
      return children;
    } catch (StorageLockedException sle) {
      throw sle;
    } catch (SecurityException se) {
      throw new SecurityException("Cannot list the child instances of instance [" + instanceId + "] the resource", se);
    } catch (ConfigurationException e) {
      throw new StorageException("Cannot list the child instances of instance [" + instanceId + "] the resource", e);
    } finally {
      mc.forgetModifications();
    }
  }

  /**
   *  Retrieves all actions.
   *
   * @return array of action names.
   */
  public String[] getActions() {
    ModificationContextImpl mc = (ModificationContextImpl)root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration resource = mc.getConfiguration(path, false, false);
      String[] actions = actionManager.getActions(resource);
      return actions;
    } catch (StorageLockedException sle) {
      throw sle;
    } catch (SecurityException se) {
      throw new SecurityException("Cannot list the actions of the resource", se);
    } finally {
      mc.forgetModifications();
    }
  }

  /**
   *  Retrieves all instances.
   *
   * @return array of action names.
   */
  public String[] getInstances() {
    ModificationContextImpl mc = (ModificationContextImpl)root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration resource = mc.getConfiguration(path, false, false);
      String[] instances = instanceManager.getInstances(resource);
      return instances;
    } catch (StorageLockedException sle) {
      throw sle;
    } catch (SecurityException se) {
      throw new SecurityException("Cannot list the instances of the resource", se);
    } catch (ConfigurationException e) {
      throw new StorageException("Cannot list the instances of the resource", e);
    } finally {
      mc.forgetModifications();
    }
  }

  /////////////////////////////////////////////////////////////
  //////////////////////ResourceAccessControlHandle////////////
  /////////////////////////////////////////////////////////////

  /**
   *  Tests if the user initiating the call is authorized to use the specified instance
   * of the resource through the given action.
   *  The call returns silently if the caller has permission to use the instance and throws
   * a security exception otherwise.
   *
   * @param  actionId   the identifier of the action as registered with the resource context.
   * @param  instanceId the identifier of the instance as registered with the resource context.
   *
   * @exception SecurityException  thrown if the caller is denied access to the instance.
   */
  public void checkPermission(String actionId, String instanceId) throws SecurityException {
    if (SecurityServerFrame.threadContext.getThreadContext() == null) {
      return;  // this is a system call
    }

    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration resource = mc.getConfiguration(path, false, false);
      AuthorizationEntry authorization = getCurrentAuthorizationContext();
      checkPermission(actionId, instanceId, resource, authorization);
    } catch (ConfigurationLockedException e) {
      throw new StorageLockedException("Cannot check permission", e);
    } catch (ConfigurationException e) {
      throw new StorageException("Cannot check permission", e);
    } finally {
      mc.forgetModifications();
    }
  }


  private void checkPermission(String actionId, String instanceId, Configuration resource, AuthorizationEntry authorization) throws ConfigurationException, SecurityException {
    Vector roles = aclManager.listGrantedSecurityRoles(actionId, instanceId, resource);

    String roleName = null;

    if (roles != null) {
      for (int i = 0; i < roles.size(); i++) {
        roleName = (String) roles.elementAt(i);
        if (authorization.impliesSecurityRole(roleName)) {
          return;
        }
        if (authorization.notImpliesSecurityRole(roleName)) {
          continue;
        }
        if (isCallerInRole(securityRoleContext.getSecurityRole(roleName))) {
          authorization.addImpliedRole(roleName);
          return;
        } else {
          authorization.addNotImpliedRole(roleName);
        }

      }
    }

    roles = aclManager.listDeniedSecurityRoles(actionId, instanceId, resource);

    if (roles != null) {
      for (int i = 0; i < roles.size(); i++) {
        roleName = (String) roles.elementAt(i);
        if (authorization.impliesSecurityRole(roleName)) {
          throw new SecurityException("Caller is not authorized to access the resource instance");
        }
        if (authorization.notImpliesSecurityRole(roleName)) {
          continue;
        }
        if (isCallerInRole(securityRoleContext.getSecurityRole(roleName))) {
          authorization.addImpliedRole(roleName);
          throw new SecurityException("Caller is not authorized to access the resource instance");
        } else {
          authorization.addNotImpliedRole(roleName);
        }
      }
    }

    if (!actionId.equals(ALL)) {
      roles = aclManager.listGrantedSecurityRoles(ALL, instanceId, resource);

      if (roles != null) {
        for (int i = 0; i < roles.size(); i++) {
          roleName = (String) roles.elementAt(i);
          if (authorization.impliesSecurityRole(roleName)) {
            return;
          }
          if (authorization.notImpliesSecurityRole(roleName)) {
            continue;
          }

          if (isCallerInRole(securityRoleContext.getSecurityRole(roleName))) {
            authorization.addImpliedRole(roleName);
            return;
          } else {
            authorization.addNotImpliedRole(roleName);
          }
        }
      }

      roles = aclManager.listDeniedSecurityRoles(ALL, instanceId, resource);

      if (roles != null) {
        for (int i = 0; i < roles.size(); i++) {
          roleName = (String) roles.elementAt(i);
          if (authorization.impliesSecurityRole(roleName)) {
            throw new SecurityException("Caller is not authorized to access the resource instance");
          }
          if (authorization.notImpliesSecurityRole(roleName)) {
            continue;
          }
          if (isCallerInRole(securityRoleContext.getSecurityRole(roleName))) {
            authorization.addImpliedRole(roleName);
            throw new SecurityException("Caller is not authorized to access the resource instance");
          } else {
            authorization.addNotImpliedRole(roleName);
          }
        }
      }
    }

    if (instanceId.equals(ALL)) {
      throw new SecurityException("Caller is not authorized to access the resource instance");
    }

    try {
      String parentId = instanceManager.getParent(instanceId, resource);
      checkPermission(actionId, parentId, resource, authorization);
    } catch (NameNotFoundException e) {
      checkPermission(actionId, ResourceHandle.ALL, resource, authorization);
    } catch (ConfigurationException e) {
      throw new SecurityException("Caller is not authorized to access the resource instance", e);
    }
  }

  public void denySecurityRole(String role, String actionId, String instanceId) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_RESOURCE_MANAGEMENT, Restrictions.RESTRICTION_MODIFY_SECURITY_OF_RESOURCE, policyConfiguration);
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration resource = mc.getConfiguration(path, true, false);
      aclManager.denyPermission(role, actionId, instanceId, resource);
      mc.commitModifications();
    } catch (StorageLockedException sle) {
      mc.rollbackModifications();
      throw sle;
    } catch (SecurityException se) {
      mc.rollbackModifications();
      throw new SecurityException("Cannot deny access of role to resource", se);
    } catch (ConfigurationLockedException e) {
      mc.rollbackModifications();
      throw new StorageLockedException("Cannot deny access of role to resource", e);
    } catch (ConfigurationException e) {
      mc.rollbackModifications();
      throw new StorageException("Cannot deny access of role to resource", e);
    }
  }

  /**
   *  Returns the groups directly given access to the specified instance of the resource
   * through the given action.
   *  No inheritance is taken in consideration.
   *
   * @param  actionId    the identifier of the action as registered with the resource context.
   * @param  instanceId  the identifier of the instance of the resource as registered with the resource context.
   *
   * @return  an array of group names.
   *
   * @exception  SecurityException  thrown if the caller does not have permissions to view restrictions.
   */
  public String[] listGrantedSecurityRoles(String actionId, String instanceId) throws SecurityException {
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration resource = mc.getConfiguration(path, false, false);
      Vector v = aclManager.listGrantedSecurityRoles(actionId, instanceId, resource);
      String[] roles = null;
      if (v != null) {
        roles = new String[v.size()];
        for (int i = 0; i < roles.length; i++) {
          roles[i] = (String) v.elementAt(i);
        }
      } else {
        roles = new String[0];
      }
      return roles;
    } catch (ConfigurationLockedException e) {
      throw new StorageLockedException("Cannot list roles granted access to resource", e);
    } catch (ConfigurationException e) {
      throw new StorageException("Cannot list roles granted access to resource", e);
    } finally {
      mc.forgetModifications();
    }
  }

  /**
   *  Returns the groups directly denied of access to the specified instance of the resource
   * through the given action.
   *  No inheritance is taken in consideration.
   *
   * @param  actionId    the identifier of the action as registered with the resource context.
   * @param  instanceId  the identifier of the instance of the resource as registered with the resource context.
   *
   * @return  an array of group names.
   *
   * @exception  SecurityException  thrown if the caller does not have permissions to view restrictions.
   */
  public String[] listDeniedSecurityRoles(String actionId, String instanceId) throws SecurityException {
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration resource = mc.getConfiguration(path, false, false);
      Vector v = aclManager.listDeniedSecurityRoles(actionId, instanceId, resource);
      String[] roles = null;
      if (v != null) {
        roles = new String[v.size()];
        for (int i = 0; i < roles.length; i++) {
          roles[i] = (String) v.elementAt(i);
        }
      } else {
        roles = new String[0];
      }
      return roles;
    } catch (ConfigurationLockedException e) {
      throw new StorageLockedException("Cannot list roles denied access to resource", e);
    } catch (ConfigurationException e) {
      throw new StorageException("Cannot list roles denied access to resource", e);
    } finally {
      mc.forgetModifications();
    }
  }

  /**
   *  Returns an instance of <code>java.security.Permission</code> that can be used to
   * test for authorization to access the specified instance through the specified action.
   *
   * @param  actionId   the identifier of the action as registered with the resource context.
   * @param  instanceId the identifier of the instance as registered with the resource context.
   *
   * @return  permission instance.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public Permission getPermission(String actionId, String instanceId) throws SecurityException {
    return null;
  }

  public void grantSecurityRole(String role, String actionId, String instanceId) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_RESOURCE_MANAGEMENT, Restrictions.RESTRICTION_MODIFY_SECURITY_OF_RESOURCE, policyConfiguration);
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration resource = mc.getConfiguration(path, true, false);
      aclManager.grantPermission(role, actionId, instanceId, resource);
      mc.commitModifications();
    } catch (StorageLockedException sle) {
      mc.rollbackModifications();
      throw sle;
    } catch (SecurityException se) {
      mc.rollbackModifications();
      throw new SecurityException("Cannot grant security role", se);
    } catch (ConfigurationLockedException e) {
      mc.rollbackModifications();
      throw new StorageLockedException("Cannot grant security role", e);
    } catch (ConfigurationException e) {
      mc.rollbackModifications();
      throw new StorageException("Cannot grant security role", e);
    }
  }

  public void clearSecurityRole(String role, String actionId, String instanceId) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_RESOURCE_MANAGEMENT, Restrictions.RESTRICTION_MODIFY_SECURITY_OF_RESOURCE, policyConfiguration);
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration resource = mc.getConfiguration(path, true, false);
      aclManager.clearPermission(role, actionId, instanceId, resource);
      mc.commitModifications();
    } catch (StorageLockedException sle) {
      mc.rollbackModifications();
      throw sle;
    } catch (SecurityException se) {
      mc.rollbackModifications();
      throw new SecurityException("Cannot clear security role", se);
    } catch (ConfigurationLockedException e) {
      mc.rollbackModifications();
      throw new StorageLockedException("Cannot clear security role", e);
    } catch (ConfigurationException e) {
      mc.rollbackModifications();
      throw new StorageException("Cannot clear security role", e);
    }
  }

  private AuthorizationEntry getCurrentAuthorizationContext() {
    // ILIAK: this method is used from 'checlPermissions' method only - there a check threadContext == null is made!!!!
    return ContextAuthorizationExtension.getEmptyAuthorizationExtension().getAuthorizationEntry(policyConfiguration);
  }

  private boolean isCallerInRole(SecurityRole role) {
    if (role == null) {
      return false;
    }
    if (role instanceof SecurityRoleImpl) {
      SecurityRoleImpl roleImpl = (SecurityRoleImpl) role;
      return roleImpl.isCallerInRolePersistent();
    }

    return role.isCallerInRole();
  }
}