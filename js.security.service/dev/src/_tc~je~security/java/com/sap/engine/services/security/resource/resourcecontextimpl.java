package com.sap.engine.services.security.resource;

import com.sap.engine.interfaces.security.ResourceContext;
import com.sap.engine.interfaces.security.resource.*;
import com.sap.engine.interfaces.security.SecurityRoleContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.services.security.restriction.Restrictions;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.frame.core.configuration.ConfigurationException;

public class ResourceContextImpl implements ResourceContext {

  private String policyConfiguration = null;
  private SecurityContext       root = null;
  private SecurityRoleContext   sCtx = null;

  public ResourceContextImpl(String policyConfiguration, SecurityContext root) {
    this.policyConfiguration = policyConfiguration;
    this.root = root;
  }

  /**
   *  Tests if the user initiating the call is authorized to use the specified instance
   * of the resource through the given action.
   *  The call returns silently if the caller has permission to use the instance and throws
   * a security exception otherwise.
   *
   * @param  actionId   the identifier of the action as registered with the resource context.
   * @param  resourceId the identifier of the resource as registered with the resource context.
   * @param  instanceId the identifier of the instance as registered with the resource context.
   *
   * @exception SecurityException  thrown if the caller is denied access to the instance.
   */
  public void checkPermission(String actionId, String resourceId, String instanceId) throws SecurityException {
    if (SecurityServerFrame.isEmergencyMode()) {
      return;
    }

    ResourceHandleImpl handle = (ResourceHandleImpl) getResourceHandle(resourceId);
    if (handle == null) {
      throw new SecurityException("Caller not authorized - missing resource handle: " + resourceId);
    }

    handle.checkPermission(actionId, instanceId);
  }

  /**
   *  Creates a new resource with the given alias.
   *
   * @param  alias  the alias of the resource.
   *
   * @exception SecurityException  thrown if the caller is not allowed to create resources.
   */
  public void createResource(String alias) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_RESOURCE_MANAGEMENT, Restrictions.RESTRICTION_ADD_RESOURCE, policyConfiguration);
    ResourceHandleImpl.createResourceHandle(root, alias, getRoleContext(), policyConfiguration);
  }

  /**
   *  Destroys a resource registered with the given alias.
   *
   * @param  alias  the alias of the resource.
   *
   * @exception SecurityException  thrown if the caller is not allowed to destroy this resource.
   */
  public void destroyResource(String alias) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_RESOURCE_MANAGEMENT, Restrictions.RESTRICTION_REMOVE_RESOURCE, policyConfiguration);
    ResourceHandleImpl handle = (ResourceHandleImpl) getResourceHandle(alias);
    if (handle == null) {
      throw new SecurityException("Caller not authorized - missing resource handle: " + alias);
    }
    handle.destroy();
  }

  /**
   *  Returns an instance of <code>java.security.Permission</code> that can be used to
   * test for authorization to access the specified instance through the specified action.
   *
   * @param  actionId   the identifier of the action as registered with the resource context.
   * @param  resourceId the identifier of the resource as registered with the resource context.
   * @param  instanceId the identifier of the instance as registered with the resource context.
   *
   * @return  permission instance.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public java.security.Permission getPermission(String actionId, String resourceId, String instanceId) throws SecurityException {
    return null;
  }

  /**
   *  Returns a handle to the resource with the given name.
   *
   * @param  alias  the name of the resource as registered with the resource context.
   *
   * @return  a handle to the resource.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public ResourceHandle getResourceHandle(String alias) throws SecurityException {
    try {
      return ResourceHandleImpl.loadResourceHandle(root, alias, getRoleContext(), policyConfiguration);
    } catch (ConfigurationException ce) {
      throw new SecurityException("Missing resource handle: " + alias, ce);
    }
  }

  /**
   *  Returns an access control handle to the resource with the given name.
   *
   * @param  alias  the name of the resource as registered with the resource context.
   *
   * @return  a handle to the resource.
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public ResourceAccessControlHandle getResourceAccessControlHandle(String alias) throws SecurityException {
    return (ResourceAccessControlHandle) getResourceHandle(alias);
  }

  /**
   *  Renames the resource.
   *
   * @param  alias     the name of the resource as registered with the resource context.
   * @param  newAlias  the new alias of the resource
   *
   * @exception SecurityException  thrown if the caller is denied.
   */
  public void renameResource(String alias, String newAlias) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_RESOURCE_MANAGEMENT, Restrictions.RESTRICTION_RENAME_RESOURCE, policyConfiguration);
    ResourceHandle handle = getResourceHandle(alias);
    if (handle == null) {
      throw new SecurityException("Caller not authorized - missing resource handle: " + alias);
    }
    handle.renameResource(newAlias);
  }

  /**
   *  Enumerates the aliases of all resources.
   *
   * @return array of resource aliases.
   *
   * @exception   SecurityException thrown if the caller is denied.
   */
  public String[] getResourceAliases() throws SecurityException {
    return ResourceHandleImpl.listResources(root);
  }


  /**
   *  Registers ResourceListener.
   *
   * @param   listener   ResourceListener.
   * @param   modifiers  int
   */
  public void registerListener(ResourceListener listener, int modifiers) {
    throw new SecurityException("Operation is not supported");
  }


  /**
   *  Unregisters ResourceListener.
   *
   * @param   listener  ResourceListener.
   */
  public void unregisterListener(ResourceListener listener) {
    throw new SecurityException("Operation is not supported");
  }

  private SecurityRoleContext getRoleContext() {
    if (sCtx == null) {
      sCtx = root.getPolicyConfigurationContext(policyConfiguration).getAuthorizationContext().getSecurityRoleContext();
    }
    return sCtx;
  }
}