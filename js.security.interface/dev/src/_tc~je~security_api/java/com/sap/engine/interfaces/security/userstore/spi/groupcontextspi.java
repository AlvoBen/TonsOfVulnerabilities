/**
 * Copyright:    2002 by SAP AG
 * Company:      SAP AG, http://www.sap.com
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license
 * agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.security.userstore.spi;

import com.sap.engine.interfaces.security.userstore.listener.GroupListener;
import com.sap.engine.interfaces.security.userstore.context.SearchResult;
import com.sap.engine.interfaces.security.userstore.context.SearchFilter;

/**
 * The Group Context Service Provider Interface.
 *
 * Groups are not necessarily arranged in a tree, they can also be arranged in
 * a directed graph (one group can have more than one parent) or totally flat.
 * The GroupContextSpi interface does not make any implications, how the
 * implementation handles the hierarchy. If a restriction of the implementation
 * is broken, an exception is thrown.
 *
 * @author  Boris Koeberle
 * @version 6.30
 */
public interface GroupContextSpi {

  /**
   * List all groups' names in an iterator.
   *
   * @return  Enumeration of Strings with group names
   */
  public java.util.Iterator engineListGroups() throws SecurityException;

  /**
	* Search names of all groups which match SearchFilter in an enumeration of strings.
	*
	* @return  Iterator of Strings with names of groups matching SearchFilter
	*/
  public SearchResult engineSearchGroups(SearchFilter filter) throws SecurityException;

  /**
   * Get an instance for a group.
   *
   * @param   groupName   the name of the requested group
   *
   * @return  the GroupInfo instance
   */
  public GroupInfoSpi engineGetGroupInfo(String groupName) throws SecurityException;


  /**
   * Create a new group in the user store.
   *
   * @param   groupName   the name of the new group
   */
  public GroupInfoSpi engineCreateGroup(String groupName) throws SecurityException;


  /**
   * Delete the group with the given group name.
   *
   * @param   groupName   the name of the group to be deleted
   */
  public void engineDeleteGroup(String groupName) throws SecurityException;


  /**
   * Add a new child group to a parent group.
   *
   * @param   groupName   the name of the child group
   * @param   parentName  the name of the parent group
   */
  public void engineAddGroupToParent(String groupName, String parentName) throws SecurityException;


  /**
   * Remove a child group from a parent group.
   *
   * @param   groupName   the name of the child group
   * @param   parentName  the name of the parent group
   */
  public void engineRemoveGroupFromParent(String groupName, String parentName) throws SecurityException;


  /**
   * Get the immediate child groups of a group (not recursive).
   *
   * @param   groupName   the name of the group
   *
   * @return  iterator with the names of the child groups of the group
   */
  public java.util.Iterator engineGetChildGroups(String groupName) throws SecurityException;


  /**
   * Get the immediate parent groups of a group (not recursive).
   *
   * @param   groupName   the name of the parent group
   *
   * @return  iterator with the names of the parent groups of the group
   */
  public java.util.Iterator engineGetParentGroups(String groupName) throws SecurityException;


  /**
   * Add a user to a group.
   *
   * @param   userName    the name of the user
   * @param   groupName   the name of the group
   */
  public void engineAddUserToGroup(String userName, String groupName) throws SecurityException;


  /**
   * Remove a user from a group.
   *
   * @param   userName    the name of the user
   * @param   groupName   the name of the group
   */
  public void engineRemoveUserFromGroup(String userName, String groupName) throws SecurityException;


  /**
   * Get the users of a group (not recursive).
   *
   * @param   groupName   the name of the group
   *
   * @return  iterator with the names of the users of the group
   */
  public java.util.Iterator engineGetUsersOfGroup(String groupName) throws SecurityException;


  /**
   * Get the groups of a user (not recursive).
   *
   * @param   userName   the name of the user
   *
   * @return  iterator with the names of the groups of the user
   */
  public java.util.Iterator engineGetGroupsOfUser(String userName) throws SecurityException;


  /**
   * Register a GroupListener with the GroupContext.
   *
   * @param   groupListener  the GroupListener instance
   * @param   modifier       the events, which shall be listened to
   */
  public void registerListener(GroupListener groupListener, int modifier) throws SecurityException;

  /**
   * Unregister a GroupListener.
   *
   * @param   groupListener   the GroupListener instance
   */
  public void unregisterListener(GroupListener groupListener) throws SecurityException;


  /**
   * Change the properties of the UserContext.
   *
   * @param   newProps   new Properties
   */
  public void enginePropertiesChanged(java.util.Properties newProps);


	/**
	 *  List the names of the root groups.
	 *
	 * @return  iterator with the names of the groups , which don't have parents.
	 */
  public java.util.Iterator engineListRootGroups() throws SecurityException;

  /**
   *
   * @return an empty SearchFilter object.
   */
  public SearchFilter engineGetSearchFilter();

  /**
   * Refresh the specified group's entry in the cache.
   *
   * @param   groupName  name of the requested group
   */
  public void engineRefresh(String groupName);
}

