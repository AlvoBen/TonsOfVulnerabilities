/*
 * ...Copyright...
 */
package com.sap.engine.services.security.server;

import java.util.HashSet;
import java.util.Iterator;
import javax.security.auth.Subject;

import com.sap.engine.interfaces.security.AuthorizationContext;
import com.sap.engine.interfaces.security.ProtectionDomainContext;
import com.sap.engine.interfaces.security.ResourceContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.SecurityRole;
import com.sap.engine.interfaces.security.SecurityRoleContext;
import com.sap.engine.interfaces.security.userstore.context.GroupContext;
import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.interfaces.security.userstore.context.UserInfo;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.login.PrivilegedRunnable;
import com.sap.engine.services.security.resource.ResourceContextImpl;
import com.sap.engine.services.security.roles.SecurityRoleContextImpl;
import com.sap.tc.logging.Severity;

/**
 *  Context of the J2EE Engine or a deployed instance of a component that
 * gives access to the access controls to security sensitive resources.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public class AuthorizationContextImpl implements AuthorizationContext {

  private String configuration;
  private ProtectionDomainContext domains = null;
  private ResourceContext resource = null;
  private SecurityRoleContext roles = null;
  private SecurityContext root = null;

  protected AuthorizationContextImpl(String configuration, SecurityContext root) {
    this.configuration = configuration;
    this.root = root;

    domains = new ProtectionDomainContextImpl(configuration);
    resource = new ResourceContextImpl(configuration, root);

    try {
      update();
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "Cannot update roles on active user store change. Check user store configuration.", e);
    }
  }

  /**
   *  Returns the code-based security controls.
   *
   * @return protection domains management context.
   */
  public ProtectionDomainContext getProtectionDomainContext() {
    return domains;
  }

  /**
   *  Returns the user-based security controls.
   *
   * @return security resource management context.
   */
  public ResourceContext getSecurityResourceContext() {
    return resource;
  }

  /**
   *  Returns the security roles context for the deployed instance of a
   * component or J2EE Engine.
   *
   * @return security roles management context.
   */
  public SecurityRoleContext getSecurityRoleContext() {
    return roles;
  }

  /**
   *  Returns the security roles context for the deployed instance of a
   * component or J2EE Engine.
   *
   * @return security roles management context.
   */
  public SecurityRoleContext getSecurityRoleContext(String userStore) {
    return new SecurityRoleContextImpl(root, configuration, userStore);
  }

  public void doAsPrivileged(Runnable runnable) throws SecurityException {
    if (SecurityServerFrame.threadContext.getThreadContext() != null) {
      throw new SecurityException("Action not allowed in application thread.");
    }
    SecurityRole role = root.getPolicyConfigurationContext(SecurityContextImpl.J2EE_ENGINE_CONFIGURATION).getAuthorizationContext().getSecurityRoleContext().getSecurityRole(roles.ROLE_ADMINISTRATORS);
    UserInfo info = getUserFromRole(role);
    Subject  admin_subject = new Subject();
    root.getUserStoreContext().getActiveUserStore().getUserContext().fillSubject(info, admin_subject);
    PrivilegedRunnable privilegedRunnable = new PrivilegedRunnable(runnable, admin_subject);
    SecurityServerFrame.threadContext.startThread(privilegedRunnable, false);
  }

  /**
   *  Update on active user store change
   */
  public void update() {
    roles = getSecurityRoleContext(root.getUserStoreContext().getActiveUserStore().getConfiguration().getName());
  }

	private UserInfo getUserFromRole(SecurityRole role) {
		UserContext userContxt = root.getUserStoreContext().getActiveUserStore().getUserContext();
		if (SecurityServerFrame.isEmergencyMode()) {
			String emergencyUserName = userContxt.getEmergencyUserName();
			return userContxt.getUserInfo(emergencyUserName);
		}

		if (role.getUsers().length != 0) {
			String userName = role.getUsers()[0];
			return userContxt.getUserInfo(userName);
		}

		String[] groups = role.getGroups();
		if (groups.length == 0) {
			throw new SecurityException("Security Role Administrators is not configured with any users within it.");
		}
		GroupContext groupContext = root.getUserStoreContext().getActiveUserStore().getGroupContext();
		HashSet checkedGroups = new HashSet();
		for (int i = 0; i < groups.length; i++) {
			String userName = getUserFromGroup(groups[i], groupContext, checkedGroups);
			if (userName != null) {
				return userContxt.getUserInfo(userName);
			}
		}
    throw new SecurityException("Security Role Administrators is not configured with any users within it.");
	}

	private String getUserFromGroup(String group, GroupContext groupContext, HashSet checkedGroups) {
		Iterator users = groupContext.getUsersOfGroup(group);
		if (users.hasNext()) {
			return (String) users.next();
		}
		Iterator groups = groupContext.getChildGroups(group);
		String nextGroup = null;
		while (groups.hasNext()) {
			nextGroup = (String) groups.next();
			if (checkedGroups.contains(nextGroup)) {
				continue;
			} else {
				checkedGroups.add(nextGroup);
				String user = getUserFromGroup(nextGroup, groupContext, checkedGroups);
				if (user != null) {
					return user;
				}
			}
		}
		return null;
	}

}

