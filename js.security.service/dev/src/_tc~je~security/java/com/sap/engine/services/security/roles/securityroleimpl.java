package com.sap.engine.services.security.roles;

import java.util.HashSet;
import java.util.Iterator;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.interfaces.security.SecurityRole;
import com.sap.engine.interfaces.security.userstore.UserStore;
import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.userstore.context.GroupContext;
import com.sap.engine.interfaces.security.userstore.context.GroupInfo;
import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.interfaces.security.userstore.context.UserInfo;
import com.sap.engine.lib.lang.SWMRG;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.login.AuthorizationEntry;
import com.sap.engine.services.security.login.ContextAuthorizationExtension;
import com.sap.tc.logging.Severity;

public class SecurityRoleImpl implements SecurityRole, java.io.Serializable, Comparable {

  static final long serialVersionUID = 9150184898356412447L;

  private String name = null;
  private String description = null;
  private HashSet users = null;
  private HashSet groups = null;
  private String runAsIdentity = null;

  private transient SWMRG guard = new SWMRG();
  private transient SecurityRoleContextImpl context = null;

  public SecurityRoleImpl(String name) {
    this.name = name;
    this.description = "";
    this.users = new HashSet();
    this.groups = new HashSet();
  }

  public SecurityRoleImpl(String name, String[] users, String[] groups) {
    this(name);
    setUsers(users);
    setGroups(groups);
  }

  public SecurityRoleImpl(String name, String[] users, String[] groups, String runAsIdentity) {
    this(name);
    setUsers(users);
    setGroups(groups);
    this.runAsIdentity = runAsIdentity;
  }

  /**
   *  Returns the name of the role.
   *
   * @return  name of role.
   */
  public String getName() {
    return this.name;
  }

  /**
   *  Returns the description of this role.
   *
   * @return  the description of this role.
   */
  public String getDescription() {
    return this.description;
  }

  /**
   *  Returns the groups directly mapped to this security role. No inheritance
   * is taken in consideration.
   *
   * @return  array of user names.
   */
  public String[] getGroups() {
    String[] result = new String[groups.size()];
    Iterator iterator = groups.iterator();

    for (int i = 0; i < result.length; i++) {
      result[i] = (String) iterator.next();
    }

    return result;
  }

  /**
   *  Returns the users directly mapped to this security role. No inheritance
   * is taken in consideration.
   *
   * @return  array of user names.
   */
  public String[] getUsers() {
    String[] result = new String[users.size()];
    Iterator iterator = users.iterator();

    for (int i = 0; i < result.length; i++) {
      result[i] = (String) iterator.next();
    }

    return result;
  }

  /**
   *  Tests if the current user is mapped to the security role.
   *
   * @return  true if such a mapping exists.
   */
  public boolean isCallerInRole() {
    AuthorizationEntry authorization = getCurrentAuthorizationContext();
    if (authorization == null) {
      return true;
    }

    if (authorization.impliesSecurityRole(name)) {
      return true;
    } else if (authorization.notImpliesSecurityRole(name)) {
      return false;
    }

    boolean inRole = isCallerInRolePersistent();
    if (inRole) {
      authorization.addImpliedRole(name);
    } else {
      authorization.addNotImpliedRole(name);
    }
    return inRole;
  }

  public boolean isCallerInRolePersistent() {
    context.refreshSecurityRole(this);
    String caller = context.getCallerName();

    guard.startRead();
    try {
      if (users.contains(caller)) {
        return true;
      }
      UserInfo info = getUserContext().getUserInfo(caller);
      if (info == null) {
        return false;
      }
      java.util.Iterator parents = info.getParentGroups();
      
			HashSet checkedParents = new HashSet();
      while(parents.hasNext()) {
				if (checkGroup((String) parents.next(), checkedParents)) {
          return true;
        }
      }

      return false;
    } finally {
      guard.endRead();
    }
  }

	private boolean checkGroup(String parentGroup, HashSet checkedParents) {
    try {
      if (groups.contains(parentGroup)) {
        return true;
      }

      GroupInfo info = getGroupContext().getGroupInfo(parentGroup);
      if (info == null) {
        return false;
      }
      java.util.Iterator parents = info.getParentGroups();
      
			String nextParent = null;
      while(parents.hasNext()) {
				nextParent = (String) parents.next();
				if (checkedParents.contains(nextParent)) {
					continue;
				} else {
					checkedParents.add(nextParent);
					if (checkGroup(nextParent, checkedParents)) {
						return true;
					}
				}
      }
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "checkGroup", e);
    }

    return false;
  }
  /**
   *  Maps a group to this security role.
   *
   * @param  groupName  group name.
   */
  public void addGroup(String groupName) {
    if (!groups.contains(groupName)) {
      groups.add(groupName);

      if (context != null) {
        context.modifySecurityRole(this);
      }
    }
  }

  /**
   *  Maps a user to this security role.
   *
   * @param  userName  user name.
   */
  public void addUser(String userName) {
    if (!users.contains(userName)) {
      users.add(userName);

      if (context != null) {
        context.modifySecurityRole(this);
      }
    }
  }

  /**
   *  Maps a user to this security role.
   *
   * @param  userName  user name.
   */
  protected void addUser(String userName, Configuration configuration) {
    if (!users.contains(userName)) {
      users.add(userName);

      if (context != null) {
        context.modifySecurityRole(this, configuration);
      }
    }
  }

  void addUserInternal(String userName, Configuration configuration) {
    if (!users.contains(userName)) {
      users.add(userName);

      if (context != null) {
        context.modifySecurityRoleInternal(this, configuration);
      }
    }
  }

  /**
   *  Invalidates mapping of the group to this role.
   *
   * @param  groupName  group name.
   */
  public void removeGroup(String groupName) {
    groups.remove(groupName);

    if (context != null) {
      context.modifySecurityRole(this);
    }
  }

  /**
   *  Returns the policy configuration and the reference security role, if the security-role refers to some other role.
   *
   */
  public String[] getReference() {
    return new String[0];
  }
  /**
   *  Invalidates mapping of the user to this role.
   *
   * @param  userName  user name.
   */
  public void removeUser(String userName) {
    users.remove(userName);

    if (context != null) {
      context.modifySecurityRole(this);
    }
  }

  /**
   *  Changes the description of this role.
   *
   * @param  description  the description of this role.
   */
  public void setDescription(String description) {
    this.description = description;

      if (context != null) {
        context.modifySecurityRole(this);
      }
  }

  public String toString() {
    return name;
  }

  /**
   *
   * @param forceAssociation
   * @return
   */
  public String getRunAsIdentity(boolean forceAssociation) {
    if (context != null) {
      runAsIdentity = context.getRunAsIdentity(this, forceAssociation);
    }
    return runAsIdentity;
  }

  /**
   * The method is used to associate a principal name as the run-as identity of the security role.
   * @param principal a valid principal name for the active user store.
   */
  public void setRunAsIdentity(String principal) {
    this.runAsIdentity = principal;
    if (context != null) {
      context.setRunAsIdentity(this, principal);
    }
  }

  /**
   * @see com.sap.engine.interfaces.security.SecurityRole#setRunAsAccountGenerationPolicy(byte type)
   */
  public void setRunAsAccountGenerationPolicy(byte type) {
    if (context != null) {
      context.setRunAsAccountGenerationPolicy(this, type);
    }
  }

  protected void setContext(SecurityRoleContextImpl context) {
    this.context = context;
  }

  protected void setUsers(String[] users) {
    guard.startWrite();
    try {
      this.users.clear();
      for (int i = 0; i < users.length; i++) {
        this.users.add(users[i]);
      }
    } finally {
      guard.endWrite();
    }
  }

  protected void setGroups(String[] groups) {
    guard.startWrite();
    try {
      this.groups.clear();
      for (int i = 0; i < groups.length; i++) {
        this.groups.add(groups[i]);
      }
    } finally {
      guard.endWrite();
    }
  }

  private UserContext getUserContext() {
    UserStoreFactory factory = context.getUserStoreFactory();
    UserStore active = factory.getActiveUserStore();
    return active.getUserContext();
  }

  private GroupContext getGroupContext() {
    UserStoreFactory factory = context.getUserStoreFactory();
    UserStore active = factory.getActiveUserStore();
    return active.getGroupContext();
  }

  public int compareTo(Object o) {
    return 0;
  }

  private AuthorizationEntry getCurrentAuthorizationContext() {
    ThreadContext tc = SecurityServerFrame.threadContext.getThreadContext();
    if (tc == null) {
      return null;
    }
    com.sap.engine.services.security.login.SecurityContext securityContext = (com.sap.engine.services.security.login.SecurityContext) tc.getContextObject("security");
    if (securityContext == null) {
      return null;
    }
    return ContextAuthorizationExtension.getEmptyAuthorizationExtension().getAuthorizationEntry(context.policyConfiguration);
  }
}

