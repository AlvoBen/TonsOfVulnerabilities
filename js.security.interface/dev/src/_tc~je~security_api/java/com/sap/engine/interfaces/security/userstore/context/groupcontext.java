/**
 * Copyright:    2002 by SAP AG
 * Company:      SAP AG, http://www.sap.com
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license
 * agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.security.userstore.context;

import com.sap.engine.interfaces.security.userstore.listener.GroupListener;
import java.util.Properties;

/**
 * GroupContext represents a wrapper of GroupContextSpi. It calls directly
 * the methods of the spi and is used only for authorization decisions.
 *
 * @author  d031387
 * @version 6.40
 *
 */
public interface GroupContext {

  /**
   * Constants for group attributes in search filters
   */
  public final static String ATTRIBUTE_GROUPNAME = "group.name";
  public final static String ATTRIBUTE_PARENT_GROUP = "group.parentgroup";
  public final static String ATTRIBUTE_CHILD_GROUP = "group.childgroup";
  public final static String ATTRIBUTE_CHILD_USER = "group.childuser";

  public void propertiesChanged(Properties newprops);

  public void registerListener(GroupListener groupListener, int modifier) throws SecurityException;

  public void unregisterListener(GroupListener listener) throws SecurityException;

  /**
   * List all groups' names in an enumeration of strings.
   *
   * @return  Enumeration of Strings with group names
   * @deprecated Use {@link GroupContext#searchGroups(SearchFilter filter) instead
   */
  public java.util.Iterator listGroups() throws SecurityException;

  /**
	* Search names of all groups which match SearchFilter in an enumeration of strings.
	*
	* @return  Iterator of Strings with names of groups matching SearchFilter
	*/
  public SearchResult searchGroups(SearchFilter filter) throws SecurityException;

  /**
   * Get an instance for a group.
   *
   * @param   groupName   name of the requested group
   * @return  GroupInfo object instance
   */
  public GroupInfo getGroupInfo(String groupName) throws SecurityException;

  /**
   * Create a new group in the user store.
   *
   * @param   groupName   name of the new group
   */
  public GroupInfo createGroup(String groupName) throws SecurityException;

  /**
   * Delete the group with the given group name.
   *
   * @param   groupName   name of the group, which shall be deleted
   */
  public void deleteGroup(String groupName) throws SecurityException;

  /**
   * Add a new child group to a parent group.
   *
   * @param   groupName   name of the child group
   * @param   parentName  name of the parent group
   */
  public void addGroupToParent(String groupName, String parentName) throws SecurityException;

  /**
   * Remove a child group from a parent group.
   *
   * @param   groupName   name of the child group
   *
   * @param  parentName  name of the parent group
   */
  public void removeGroupFromParent(String groupName, String parentName) throws SecurityException;

  /**
   * Get the immediate child groups of a group (not recursive).
   *
   * @param   groupName   name of the parent group
   *
   * @return  Array of strings with child group names
   */
  public java.util.Iterator getChildGroups(String groupName) throws SecurityException;

  /**
   * Get the immediate parent groups of a group (not recursive).
   *
   * @param   groupName   name of the parent group
   *
   * @return  Array of strings with parent group names
   */
  public java.util.Iterator getParentGroups(String groupName) throws SecurityException;

  /**
   * Add a user to a group.
   *
   * @param   userName    name of the user
   * @param   groupName   name of the group
   */
  public void addUserToGroup(String userName, String groupName) throws SecurityException;

  /**
   * Remove a user from a group.
   *
   * @param   userName    name of the user
   * @param   groupName   name of the group
   */
  public void removeUserFromGroup(String userName, String groupName) throws SecurityException;

  /**
   * Get the users of a group (not recursive).
   *
   * @param   groupName   name of the group
   *
   * @return  Array of strings with user names
   */
  public java.util.Iterator getUsersOfGroup(String groupName) throws SecurityException;

  /**
   * Get the groups of a user (not recursive).
   *
   * @param   userName   name of the user
   *
   * @return  Array of strings with group names
   */
  public java.util.Iterator getGroupsOfUser(String userName) throws SecurityException;

  /**
   *  List the names of the root groups.
   *
   * @return  iterator with the names of the groups , which don't have parents.
   */
  public java.util.Iterator listRootGroups() throws SecurityException;

  /**
   *
   * @return an empty SearchFilter object.
   */
  public SearchFilter getSearchFilter();

  /**
   * Refresh the specified group's entry in the cache.
   *
   * @param   groupName  name of the requested group
   */
  public void refresh(String groupName);

}

