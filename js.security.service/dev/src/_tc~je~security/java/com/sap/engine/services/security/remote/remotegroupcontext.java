package com.sap.engine.services.security.remote;

import com.sap.engine.interfaces.security.userstore.context.SearchFilter;

import java.rmi.*;

public interface RemoteGroupContext
  extends Remote {

  /**
   * List all groups' names in an enumeration of strings.
   *
   * @return  Enumeration of Strings with group names
   */
  public RemoteIterator listGroups() throws RemoteException;

  /**
	* Search names of all groups which match SearchFilter in an enumeration of strings.
	*
	* @return  Iterator of Strings with names of groups matching SearchFilter
	*/
  public RemoteIterator searchGroups(SearchFilter filter) throws RemoteException;

  /**
   * Get an instance for a group.
   *
   * @param   groupName   name of the requested group
   * @return  GroupInfo object instance
   */
  public RemoteGroupInfo getGroupInfo(String groupName) throws RemoteException;


  /**
   * Create a new group in the user store.
   *
   * @param   groupName   name of the new group
   */
  public void createGroup(String groupName) throws RemoteException;


  /**
   * Delete the group with the given group name.
   *
   * @param   groupName   name of the group, which shall be deleted
   */
  public void deleteGroup(String groupName) throws RemoteException;


  /**
   * Add a new child group to a parent group.
   *
   * @param   groupName   name of the child group
   * @param   parentName  name of the parent group
   */
  public void addGroupToParent(String groupName, String parentName) throws RemoteException;


  /**
   * Remove a child group from a parent group.
   *
   * @param   groupName   name of the child group
   *
   * @param  parentName  name of the parent group
   */
  public void removeGroupFromParent(String groupName, String parentName) throws RemoteException;


  /**
   * Get the immediate child groups of a group (not recursive).
   *
   * @param   groupName   name of the parent group
   *
   * @return  Array of strings with child group names
   */
  public RemoteIterator getChildGroups(String groupName) throws RemoteException;


  /**
   * Get the immediate parent groups of a group (not recursive).
   *
   * @param   groupName   name of the parent group
   *
   * @return  Array of strings with parent group names
   */
  public RemoteIterator getParentGroups(String groupName) throws RemoteException;


  /**
   * Add a user to a group.
   *
   * @param   userName    name of the user
   * @param   groupName   name of the group
   */
  public void addUserToGroup(String userName, String groupName) throws RemoteException;


  /**
   * Remove a user from a group.
   *
   * @param   userName    name of the user
   * @param   groupName   name of the group
   */
  public void removeUserFromGroup(String userName, String groupName) throws RemoteException;


  /**
   * Get the users of a group (not recursive).
   *
   * @param   groupName   name of the group
   *
   * @return  Array of strings with user names
   */
  public RemoteIterator getUsersOfGroup(String groupName) throws RemoteException;


  /**
   * Get the groups of a user (not recursive).
   *
   * @param   userName   name of the user
   *
   * @return  Array of strings with group names
   */
  public RemoteIterator getGroupsOfUser(String userName) throws RemoteException;

  /**
   *  List the names of the root groups.
   *
   * @return  iterator with the names of the groups , which don't have parents.
   */
  public RemoteIterator listRootGroups() throws RemoteException;

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

