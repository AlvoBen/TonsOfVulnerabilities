/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security;

import java.io.Serializable;

/**
 *  A security role descriptor and control.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 *
 * @deprecated Security role context is deprecated since NW04 AS Java. Use UME API instead.
 * @see com.sap.engine.interfaces.security.SecurityRoleContext
 */
public interface SecurityRole extends Serializable {
  public static final byte RUN_AS_CREATE_ACCOUNT = 0;
  public static final byte RUN_AS_GET_FROM_MAPPINGS = 1;
  public static final byte RUN_AS_CREATION_FORBIDDEN = 2;

  /**
   *  Returns the name of the role.
   *
   * @return  name of role.
   */
  public String getName();


  /**
   *  Returns the description of this role.
   *
   * @return  the description of this role.
   */
  public String getDescription();


  /**
   *  Returns the groups directly mapped to this security role. No inheritance
   * is taken in consideration.
   *
   * @return  array of user names.
   */
  public String[] getGroups();


  /**
   *  Returns the users directly mapped to this security role. No inheritance
   * is taken in consideration.
   *
   * @return  array of user names.
   */
  public String[] getUsers();


  /**
   *  Tests if the current user is mapped to the security role.
   *
   * @return  true if such a mapping exists.
   */
  public boolean isCallerInRole();


  /**
   *  Maps a group to this security role.
   *
   * @param  groupName  group name.
   */
  public void addGroup(String groupName);


  /**
   *  Maps a user to this security role.
   *
   * @param  userName  user name.
   */
  public void addUser(String userName);


  /**
   *  Invalidates mapping of the group to this role.
   *
   * @param  groupName  group name.
   */
  public void removeGroup(String groupName);


  /**
   *  Invalidates mapping of the user to this role.
   *
   * @param  userName  user name.
   */
  public void removeUser(String userName);


  /**
   *  Changes the description of this role.
   *
   * @param  description  the description of this role.
   */
  public void setDescription(String description);

  /**
   *  Returns the policy configuration and the reference security role, if the security-role refers to some other role.
   *
   */
  public String[] getReference();

  /**
   *
   * @param forceAssociation
   * @return
   */
  public String getRunAsIdentity(boolean forceAssociation);

  /**
   * The method is used to associate a principal name as the run-as identity of the security role.
   * @param principal a valid principal name for the active user store.
   */
  public void setRunAsIdentity(String principal);

  /**
   * The method is used to specify the way the security service generates a dedicated user account for the
   * run-as identity of the security role. If not called the default value is SecurityRole.RUN_AS_CREATE_ACCOUNT.
   * @param type
   */
  public void setRunAsAccountGenerationPolicy(byte type);

}

