/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.userstore.context;

import com.sap.engine.interfaces.security.userstore.spi.GroupContextSpi;
import com.sap.engine.interfaces.security.userstore.spi.GroupInfoSpi;
import com.sap.engine.interfaces.security.userstore.listener.GroupListener;
import com.sap.engine.interfaces.security.userstore.context.SearchResult;
import com.sap.engine.interfaces.security.userstore.context.SearchFilter;
import com.sap.engine.services.security.restriction.Restrictions;

import java.util.Properties;

/**
 *  GroupContext represents a wrapper of UserContextSpi. It calls direcetly
 * the methods of the spi, and is used only for authorization-making decisions.
 *
 * @author Jako Blagoev
 * @version 6.30
 */
public class GroupContext implements com.sap.engine.interfaces.security.userstore.context.GroupContext {

  private GroupContextSpi spi = null;

  public GroupContext(GroupContextSpi spi, Properties props) {
    this.spi = spi;
    this.spi.enginePropertiesChanged(props);
  }

  public void propertiesChanged(Properties newprops) {
    Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_CHANGE_CONFIGURATION);

    this.spi.enginePropertiesChanged(newprops);
  }

  public void registerListener(GroupListener groupListener, int modifier) throws SecurityException {
    // to do
  }

  public void unregisterListener(GroupListener listener) throws SecurityException {
    // to do
  }

  /**
   * List all groups' names in an enumeration of strings.
   *
   * @return  Enumeration of Strings with group names
   */
  public java.util.Iterator listGroups() throws SecurityException {
    return spi.engineListGroups();
  }

  /**
	* Search names of all groups which match SearchFilter in an enumeration of strings.
	*
	* @return  Iterator of Strings with names of groups matching SearchFilter
	*/
  public SearchResult searchGroups(SearchFilter filter) throws SecurityException {
    return spi.engineSearchGroups(filter);
  }

  /**
   * Get an instance for a group.
   *
   * @param   groupName   name of the requested group
   * @return  GroupInfo object instance
   */
  public com.sap.engine.interfaces.security.userstore.context.GroupInfo getGroupInfo(String groupName) throws SecurityException {
    GroupInfoSpi infoSpi = spi.engineGetGroupInfo(groupName);

    if (spi == null) {
      return null;
    }

    return new GroupInfo(infoSpi);
  }

  /**
   * Create a new group in the user store.
   *
   * @param   groupName   name of the new group
   */
  public com.sap.engine.interfaces.security.userstore.context.GroupInfo createGroup(String groupName) throws SecurityException {
    Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_CREATE_ACCOUNT);

    GroupInfoSpi groupSpi = spi.engineCreateGroup(groupName);
    if (spi != null) {
      return new GroupInfo(groupSpi);
    }
    return null;
  }

  /**
   * Delete the group with the given group name.
   *
   * @param   groupName   name of the group, which shall be deleted
   */
  public void deleteGroup(String groupName) throws SecurityException {
    Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_REMOVE_ACCOUNT);

    spi.engineDeleteGroup(groupName);
  }

  /**
   * Add a new child group to a parent group.
   *
   * @param   groupName   name of the child group
   * @param   parentName  name of the parent group
   */
  public void addGroupToParent(String groupName, String parentName) throws SecurityException {
    Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_GROUP_ACCOUNT);

    spi.engineAddGroupToParent(groupName, parentName);
  }

  /**
   * Remove a child group from a parent group.
   *
   * @param   groupName   name of the child group
   *
   * @param  parentName  name of the parent group
   */
  public void removeGroupFromParent(String groupName, String parentName) throws SecurityException {
    Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_GROUP_ACCOUNT);

    spi.engineRemoveGroupFromParent(groupName, parentName);
  }

  /**
   * Get the immediate child groups of a group (not recursive).
   *
   * @param   groupName   name of the parent group
   *
   * @return  Array of strings with child group names
   */
  public java.util.Iterator getChildGroups(String groupName) throws SecurityException {
    return spi.engineGetChildGroups(groupName);
  }

  /**
   * Get the immediate parent groups of a group (not recursive).
   *
   * @param   groupName   name of the parent group
   *
   * @return  Array of strings with parent group names
   */
  public java.util.Iterator getParentGroups(String groupName) throws SecurityException {
    return spi.engineGetParentGroups(groupName);
  }

  /**
   * Add a user to a group.
   *
   * @param   userName    name of the user
   * @param   groupName   name of the group
   */
  public void addUserToGroup(String userName, String groupName) throws SecurityException {
    Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_GROUP_ACCOUNT);

    spi.engineAddUserToGroup(userName, groupName);
  }

  /**
   * Remove a user from a group.
   *
   * @param   userName    name of the user
   * @param   groupName   name of the group
   */
  public void removeUserFromGroup(String userName, String groupName) throws SecurityException {
    Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_GROUP_ACCOUNT);

    spi.engineRemoveUserFromGroup(userName, groupName);
  }

  /**
   * Get the users of a group (not recursive).
   *
   * @param   groupName   name of the group
   *
   * @return  Array of strings with user names
   */
  public java.util.Iterator getUsersOfGroup(String groupName) throws SecurityException {
    return spi.engineGetUsersOfGroup(groupName);
  }

  /**
   * Get the groups of a user (not recursive).
   *
   * @param   userName   name of the user
   *
   * @return  Array of strings with group names
   */
  public java.util.Iterator getGroupsOfUser(String userName) throws SecurityException {
    return spi.engineGetGroupsOfUser(userName);
  }

  /**
   *  List the names of the root groups.
   *
   * @return  iterator with the names of the groups , which don't have parents.
   */
  public java.util.Iterator listRootGroups() {
    return spi.engineListRootGroups();
  }

  /**
   *
   *@return an empty SearchFilter object.
   */
  public SearchFilter getSearchFilter() {
    return spi.engineGetSearchFilter();
  }

  /**
   * Refresh the specified group's entry in the cache.
   *
   * @param   groupName  name of the requested group
   */
  public void refresh(String groupName) {
    spi.engineRefresh(groupName);
  }
}

