package com.sap.engine.services.security.roles;

import com.sap.engine.interfaces.security.SecurityRole;

public class SecurityRoleReference implements SecurityRole, Comparable {


  static final long serialVersionUID = 9150184898356412450L;

  private String name = null;
  private String referencePolicy = null;
  private SecurityRole reference = null;
  private String runAsIdentity = null;

  private transient SecurityRoleContextImpl context = null;

  public SecurityRoleReference(String name, String referencePolicy, SecurityRole reference) {
    this.name = name;
    this.reference = reference;
    this.referencePolicy = referencePolicy;
  }

  public SecurityRoleReference(String name, String referencePolicy, SecurityRole reference, String runAsIdentity) {
    this.name = name;
    this.reference = reference;
    this.referencePolicy = referencePolicy;
    this.runAsIdentity = runAsIdentity;
  }

  /**
   *  Returns the name of the role.
   *
   * @return  name of role.
   */
  public String getName() {
    return name;
  }


  /**
   *  Returns the description of this role.
   *
   * @return  the description of this role.
   */
  public String getDescription() {
    return reference.getDescription();
  }


  /**
   *  Returns the groups directly mapped to this security role. No inheritance
   * is taken in consideration.
   *
   * @return  array of user names.
   */
  public String[] getGroups() {
    return reference.getGroups();
  }


  /**
   *  Returns the reference information.
   *
   * @return  array of policy configuration name and security role name.
   */
  public String[] getReference() {
    return new String[] { referencePolicy, reference.getName() };
  }

  protected void setReference(String name, SecurityRole reference) {
    this.name = name;
    this.reference = reference;
  }

  /**
   *  Returns the users directly mapped to this security role. No inheritance
   * is taken in consideration.
   *
   * @return  array of user names.
   */
  public String[] getUsers() {
    return reference.getUsers();
  }


  /**
   *  Tests if the current user is mapped to the security role.
   *
   * @return  true if such a mapping exists.
   */
  public boolean isCallerInRole() {
    context.refreshSecurityRole(this);
    return reference.isCallerInRole();
  }


  /**
   *  Maps a group to this security role.
   *
   * @param  groupName  group name.
   */
  public void addGroup(String groupName) {
    throw new IllegalStateException("Cannot change referenced security role.");
  }


  /**
   *  Maps a user to this security role.
   *
   * @param  userName  user name.
   */
  public void addUser(String userName) {
    throw new IllegalStateException("Cannot change referenced security role.");
  }


  /**
   *  Invalidates mapping of the group to this role.
   *
   * @param  groupName  group name.
   */
  public void removeGroup(String groupName) {
    throw new IllegalStateException("Cannot change referenced security role.");
  }


  /**
   *  Invalidates mapping of the user to this role.
   *
   * @param  userName  user name.
   */
  public void removeUser(String userName) {
    throw new IllegalStateException("Cannot change referenced security role.");
  }


  /**
   *  Changes the description of this role.
   *
   * @param  description  the description of this role.
   */
  public void setDescription(String description) {
    throw new IllegalStateException("Cannot change referenced security role.");
  }

  public String toString() {
    return name + " [" + referencePolicy + ":" + reference.getName() + "]";
  }

  public int compareTo(Object o) {
    return 0;
  }

  protected void setContext(SecurityRoleContextImpl context) {
    this.context = context;
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

  public void setRunAsAccountGenerationPolicy(byte type) {
    if (context != null) {
      context.setRunAsAccountGenerationPolicy(this, type);
    }
  }
}