package com.sap.security.api;

/**
 * This interface for a groupfactory provides functionality to get group objects by providing required information.
 * 
 * The group factory provides means to
 * <ol><li>instantiate group objects
 * <li>create new groups (possibly by copying the data of an existing one)
 * <li>delete groups
 * <li>search for groups based on different criteria
 * <li>perform mass commit/rollback operations on a set of groups</ol>
 * 
 * <p><b>NOTE</b>:  As this interface�can be extended, this interface can be freely used, 
 * but must not be implemented.
 * 
 * @author  Alexander Primbs
 * @version $Revision: #1 $ <BR>
 *
 */

public interface IGroupFactory extends IConfigurable
{
    public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/IGroupFactory.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";
	
	/***
	 * Constant used to access build-in group Everyone
	 * @deprecated use {@link #EVERYONE_UNIQUEID} instead
	 ***/	     
    public final static String EVERYONE             = "Everyone";    

	/***
	 * Constant used to access build-in group Everyone with uniqueId
	 ***/	     
	public final static String EVERYONE_UNIQUEID 	= "GRUP.SUPER_GROUPS_DATASOURCE.EVERYONE";

	/***
	 * Constant used to access build-in group Authenticated Users
	 * @deprecated use {@link #AUTHENTICATED_USERS_UNIQUEID} instead
	 ***/	     	
    public final static String AUTHENTICATED_USERS  = "Authenticated Users";

	/***
	 * Constant used to access build-in group Authenticated Users with uniqueId
	 ***/	     
	public final static String AUTHENTICATED_USERS_UNIQUEID = "GRUP.SUPER_GROUPS_DATASOURCE.AUTHENTICATED_USERS";

	/***
	 * Constant used to access build-in group Anonymous Users
	 * @deprecated use {@link #ANONYMOUS_USERS_UNIQUEID} instead
	 ***/	    
    public final static String ANONYMOUS_USERS      = "Anonymous Users";

	/***
	 * Constant used to access build-in group Anonymous Users with uniqueId
	 ***/
	public final static String ANONYMOUS_USERS_UNIQUEID = "GRUP.SUPER_GROUPS_DATASOURCE.Anonymous Users";
	    
   /**
    * Gets the group object with the given unique ID
    * @param uniqueID of group
    * @return IGroup           the group object
    * @exception UMException if no group with the given unique ID
    * exists
    */
    public IGroup getGroup (String uniqueID) throws UMException;

   /**
    * Gets the group object with the given unique ID  and populates 
    * the attributes which are defined in populateAttributes
    * @param uniqueID of group
    * @param populateAttributes {@link com.sap.security.api.AttributeList}
    * @exception UMException if no group with the given unique ID
    * exists
    * @return IGroup           the group object
    */
    public IGroup getGroup (String uniqueID, AttributeList populateAttributes) throws UMException;

   /**
    * Creates a new, initially blank group object.  After
    * setting the appropriate data via set-methods, the group object
    * must be commited to the group store via {@link IPrincipalMaint#commit()}.
    * @param uniqueName of new group
    * Note: This name has to be unique for ALL data stores
    * @exception GroupAlreadyExistsException if group with uniqueName already exists
    * @return IGroup           the group object
    */
    public IGroup newGroup (String uniqueName) throws com.sap.security.api.UMException;

   /**
    * Gets the group object with the given uniqueName
    * <p><b>Note</b>: If you use this method
    * be prepared to get an exception if multiple objects with the same name are found
    * @param uniqueName of group    
    * @exception NoSuchGroupException if no group with the given uniqueName
    * exists
    * @exception UMException if given unique name is not unique
    * @return IGroup           the group object
    */
    public IGroup getGroupByUniqueName (String uniqueName) throws UMException;

   /**
    * Delete a group from the data store
	* Note: deletes also all direct group and role assignments of this group.
    * @param uniqueID of group which should be deleted
    * @exception UMException if the group can't be deleted
    * @exception NoSuchGroupException if the group does not exist
    */
    public void deleteGroup (String uniqueID) throws UMException;

   /**
    * Search for groups in the group store which match the criteria specified in the
    * given <code>filter</code>. In order to get a group search filter use  
    * {@link #getGroupSearchFilter()}.
    * You can define a search filter using methods of class {@link IGroupSearchFilter}.    
    * @param filter defined to search for groups
    * @return ISearchResult result of the search operation {@link ISearchResult}
    */
    public ISearchResult searchGroups(IGroupSearchFilter filter) throws UMException;


    /**
     * Gets the group objects for multiple unique IDs
     * @param uniqueIDs array of uniqueIDs which are used to get an array of IGroup
     * objects.
     * @exception NoSuchGroupException if one or more of the given unique IDs
     * are not assigned to any group
     * @return IGroup[]  an array of group objects
     */
    public IGroup[] getGroups(String[] uniqueIDs)
        throws NoSuchGroupException,
        com.sap.security.api.UMException;

    /**
     * Gets the group objects for multiple unique IDs and 
     * populates the attributes which are defined in populateAttributes
     * @param uniqueIDs array of uniqueIDs which are used to get an array of IGroup
     * objects.
     * @param populateAttributes {@link com.sap.security.api.AttributeList}
     * @exception NoSuchGroupException if one or more of the given unique IDs
     * are not assigned to any group
     * @return IGroup[]  an array of group objects
     */
    public IGroup[] getGroups(String[] uniqueIDs, AttributeList populateAttributes)
        throws NoSuchGroupException, com.sap.security.api.UMException;

    /**
     * Gets the group object identified by uniqueID 
     * which can be modified
     * @param uniqueID of group object
     * @exception NoSuchGroupException if the unique ID does not exist
     * @return a mutable Group object which can be modified.
     * @exception NoSuchGroupException if group with given uniqueID
     * does not exist.
     */
    public IGroup getMutableGroup(String uniqueID)
        throws NoSuchGroupException, com.sap.security.api.UMException;

  // -----------------------------
  // Register/UnRegister observers ---------------------------------------------
  // -----------------------------
  /**
   * registerListener allows to subscribe to a predefined eventName
   * {@link GroupListener}
   * The caller has to provide a receiver object which implements GroupListener
   * @param groupListener object which implements interface GroupListener
   * @param modifier constant defined in {@link GroupListener}
   */
    public void registerListener( GroupListener groupListener, int modifier);

    /**
     * registerListener allows to subscribe to a predefined eventName
     * {@link GroupListener}
     * The caller has to provide a receiver object which implements GroupListener
     * @param groupListener object which implements interface GroupListener
     * @param modifier constant defined in {@link GroupListener}
     * @param notifyAfterPhysicalCommitCompleted Allows callers when set to false, to get a notification before the physical transaction is completed in order to include their actions into the same physical transaction.
     */
     public void registerListener( GroupListener groupListener, int modifier, boolean notifyAfterPhysicalCommitCompleted);
    
  /***
   * unregisterListener unsubscribes a receiver from a previously subscribed event.
   * @param groupListener object which implements interface GroupListener   
   */
    public void unregisterListener( GroupListener groupListener);

   /**
     * Gets the list of parent groups of group which is identified by
     * uniqueIdOfGroup
     * @param uniqueIdOfGroup which should be used
     * @param recursive if recursive set to true recursive parent groups are checked
     *  including parents, grandparents, ...
     * @return String[] of uniqueIds of parent group
     * @exception NoSuchGroupException if group with given uniqueIdOfGroup
     * does not exist.
     */
    public String[] getParentGroups(String uniqueIdOfGroup, boolean recursive)
        throws com.sap.security.api.UMException;

    /**
     * Returns principals of type group belonging to this groupId
     * This method does a recursive search if the second parameter
     * @param uniqueIdOfGroup given uniqueId of group object     
     * @param recursive if recursive is set to true all children, grandchildren, ...
     * will be returned. If false only children of uniqueIdOfGroup are returned.
     * @return String[] of uniqueIds of child groups
     * @exception NoSuchGroupException if one or more of the given unique IDs
     */
    public String[] getChildGroups(String uniqueIdOfGroup, boolean recursive)
        throws com.sap.security.api.UMException;

    /**
     * assign group with uniqueIdOfGroup to parent group with uniqueIdOfParentGroup.
     * Implicitly a commit is done if you call this method.
     *
     * @param	uniqueIdOfGroup       id of group
     * @param	uniqueIdOfParentGroup id of the parent group
     * @exception UMException if group cannot be added to parent
     */
    public void addGroupToParent(String uniqueIdOfGroup, String uniqueIdOfParentGroup) throws UMException;

    /**
     * assign user with uniqueIdOfUser to group with uniqueIdOfGroup. Implicitly
     * a commit is done if you call this method.
     *
     * @param	uniqueIdOfUser       id of user
     * @param	uniqueIdOfGroup id of the group
     * @exception	UMException if user cannot be added to group
     */
    public void addUserToGroup(String uniqueIdOfUser, String uniqueIdOfGroup) throws UMException;

    /**
     * unassign group with uniqueIdOfGroup from parent group with uniqueIdOfParentGroup. Implicitly
     * a commit is done if you call this method.
     *
     * @param	uniqueIdOfGroup       id of group
     * @param	uniqueIdOfParentGroup id of the parent group
     * @exception	UMException if group cannot be removed from parent group
     */
    public void removeGroupFromParent(String uniqueIdOfGroup, String uniqueIdOfParentGroup) throws UMException;

    /**
     * unassign user with uniqueIdOfUser from group with uniqueIdOfGroup. Implicitly
     * a commit is done if you call this method.
     *
     * @param	uniqueIdOfUser       id of user
     * @param	uniqueIdOfGroup      id of the group
     * @exception	UMException if user cannot be removed from group
     */
    public void removeUserFromGroup(String uniqueIdOfUser, String uniqueIdOfGroup) throws UMException;


    /**
     *  Returns an IGroupSearchFilter object to be used to specify query attributes
     * <p>
     * IGroupSearchFilter only contains attributes which will be queried
     *
     * @return     IGroupSearchFilter container for values to be used as a search
     * filter
     * @exception  UMException if filter cannot be provided
     */
    public IGroupSearchFilter getGroupSearchFilter() throws UMException;

	/***
	* removes the group object which has the specified unique name from the factory's cache
	* 
	* Note: Use this method carefully, because calling it too often may cause performance problems
	* 
	* @param uniqueName the uniqueName of the group
	* @throws UMException if a error occurs
	*/
	public void invalidateCacheEntryByUniqueName(String uniqueName) throws UMException;
	
	/***
	* removes the group object which has the specified uniqueid from the factory's cache
	* 
	* Note: Use this method carefully, because calling it too often may cause performance problems
	* 
	* @param uniqueid the uniqueid of the group
	* @throws UMException if a error occurs
	*/
	public void invalidateCacheEntry(String uniqueid) throws UMException;
	

}
