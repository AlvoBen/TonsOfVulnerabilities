package com.sap.security.api;

/**
 * This interface provides methods to access, create, search and delete IRole objects.
 * 
 * <p><b>NOTE</b>:  As this interface�can be extended, this interface can be freely used, 
 * but must not be implemented.
 * 
 */

public interface IRoleFactory extends IConfigurable
{
  public final static String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/IRoleFactory.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

   /**
    * Gets the role object with the given unique ID
    * @param uniqueID String representing the uniqueID of a role object.
    * @exception NoSuchRoleException if no role with the given unique ID
    * @return IRole           the role object
    * exists
    */
    public IRole getRole (String uniqueID) throws UMException;

   /**
    * Gets the role object with the given unique ID and populates the attributes in populateAttributes
    * @param uniqueID String representing the uniqueID of a role object.
    * @param populateAttributes {@link com.sap.security.api.AttributeList}
    * @exception NoSuchRoleException if no role with the given unique ID
    * exists
    * @return IRole           the role object
    */
    public IRole getRole (String uniqueID, AttributeList populateAttributes) throws UMException;

   /**
    * Delete a role from the used store
    * @param uniqueID String representing the uniqueID of a role object.
    * @exception UMException if the role can't be deleted
    * @exception NoSuchRoleException if the role does not exist
    */
    public void deleteRole (String uniqueID) throws UMException;

   /**
    * Search for roles in the role store and role account store which match the criteria specified in the<p>
    * given <code>filter</code>. In order to get a role search filter use  
    * {@link #getRoleSearchFilter()}.
    * You can define a search filter using methods of class {@link IRoleSearchFilter}.    
	* If you are using roles stored in the PCD, the behaviour not as expected. When searching with the
    * uniquename and the equals operator for a role stored in PCD, only the first role 
    * which is found is returned. If you want to get all roles with a similar unique name you have 
    * to use the like operator in the role search filter.
    * This PCD-Role specific behaviour is caused by the fact that the UME unique name is 
    * mapped to the pcd name of the role which is for example:
    * pcd:portal_content/myFolder/myPrefix.MyRole
    * However searching is only possible with the last part of the pcd name like "myPrefix.MyRole".
    * In order to get all roles named for example "myPrefix.MyRole" you have to use the like 
    * operator with search string: "myPrefix.MyRole*" 
    * @param filter defined to search for roles
    * @return ISearchResult result of the search operation {@link ISearchResult}
    */
    public ISearchResult searchRoles(IRoleSearchFilter filter) throws UMException;

    /**
     *  Returns an IRoleSearchFilter object to be used to specify query attributes
     * <p>
     * IRoleSearchFilter contains attributes which can be queried
     *
     *@return     IRoleSearchFilter container for values to be used as a search
     * filter
     *@exception  UMException
     *@exception  FeatureNotAvailableException
    */
    public IRoleSearchFilter getRoleSearchFilter() throws UMException;

   /**
    * Creates a new, initially blank role object.  After
    * setting the appropriate data via set-methods, the role object
    * must be commited to the role store via {@link IRole#commit()}.
    *
    * @param uniqueName of new IRole object.
    * Note: This uniqueName has to be unique for ALL data stores
    * @exception RoleAlreadyExistsException if role with uniqueName already exists
    * @return IRole a role object which can be modified
    */
    public IRole newRole (String uniqueName) throws com.sap.security.api.UMException;

   /**
    * Gets the role object with the given uniqueName
    * <p><b>Note</b>: If you use this method
    * be prepared to get an exception if multiple objects with the same name are found 
    * @param uniqueName of IRole object    
    * @exception NoSuchRoleException if no role with the given uniqueName
    * exists
    * @exception UMException if given unique name is not unique
    * @return IRole           the role object
    */
    public IRole getRoleByUniqueName (String uniqueName) throws UMException;

    /**
     * Gets the role objects for multiple unique IDs
     * @param uniqueIDs array of uniqueIDs which are used to get an array of IRole
     * objects.
     * @exception NoSuchRoleException if one or more of the given unique IDs
     * are not assigned to any role
     * @exception NoSuchPCDRoleException if the role does not exist
     * @return IRole[] an array of role objects
     */
    public IRole[] getRoles(String[] uniqueIDs)
        throws com.sap.security.api.UMException;

    /**
     * Gets the role objects for multiple unique IDs and populates the attributes
     * defined in populateAttributes
     * 
     * @param uniqueIDs array of uniqueIDs which are used to get an array of IRole
     * objects.
     * @param populateAttributes {@link com.sap.security.api.AttributeList}
     * @exception NoSuchRoleException if one or more of the given unique IDs
     * are not assigned to any role
     * @exception NoSuchPCDRoleException if the role does not exist
     * @return IRole[] an array of role objects
     */
    public IRole[] getRoles(String[] uniqueIDs, AttributeList populateAttributes)
        throws com.sap.security.api.UMException;

    /**
     * Gets a modifiable IRole objects for a unique ID
     * @param uniqueID of an IRole object
     * @exception NoSuchRoleException if the unique ID does not exist
     * @exception NoSuchPCDRoleException if the role does not exist
     * @return a mutable Role object which can be modified.
     */
    public IRole getMutableRole(String uniqueID)
        throws com.sap.security.api.UMException;


   /**
    *  Returns the maximum role description length, which is implementation
    *  depending.
    *
    * @return  maximum supported role description length
    * @deprecated the maximum length is defined in 
    * {@link IPrincipalMetaData#setAttribute (String, String, String[])}
    */
     public int getMaxRoleDescriptionLength();

  // -----------------------------
  // Register/UnRegister observers ---------------------------------------------
  // -----------------------------
  /**
   * registerListener allows to subscribe to a predefined eventName
   * {@link RoleListener}
   * The caller has to provide a receiver object which implements RoleListener
   * @param roleListener object which implements interface RoleListener
   * @param modifier constant defined in {@link RoleListener}
   */
    public void registerListener( RoleListener roleListener, int modifier);

    /**
     * registerListener allows to subscribe to a predefined eventName
     * {@link RoleListener}
     * The caller has to provide a receiver object which implements RoleListener
     * @param roleListener object which implements interface RoleListener
     * @param modifier constant defined in {@link RoleListener}
     * @param notifyAfterPhysicalCommitCompleted Allows callers when set to false, to get a notification before the physical transaction is completed in order to include their actions into the same physical transaction.
     */
      public void registerListener( RoleListener roleListener, int modifier, boolean notifyAfterPhysicalCommitCompleted);
    
  /***
   * unregisterListener unsubscribes a receiver from a previously subscribed event.
   * @param roleListener object which implements interface RoleListener   
   */
    public void unregisterListener( RoleListener roleListener);

    /**
     * Returns users who are assigned to role identified by uniqueIdOfRole
     * @param uniqueIdOfRole representing the ID of an IRole
     * @param recursive If this parameter is set to <code>false</code>
     * all direct users are returned
     * if recursive is <code>true</code> all users which are assigned via groups to this role
     * are returned
     * @return String[] of uniqueIdOfUsers
     * @exception NoSuchPCDRoleException if the role does not exist
     */
    public String[] getUsersOfRole(String uniqueIdOfRole, boolean recursive);

    /**
     * Returns groups which are assigned to role identified by uniqueIdOfRole
     * @param uniqueIdOfRole representing the ID of an IRole
     * @param recursive If this parameter is set to <code>false</code>
     * all direct groups are returned
     * if recursive is <code>true</code> all groups which are assigned via 
     * other groups to this role are returned.
     * @return String[] of uniqueIdOfGroups
     */
    public String[] getGroupsOfRole(String uniqueIdOfRole, boolean recursive);

    /**
     * Returns roles which are assigned to user identified by uniqueIdOfUser
     * @param uniqueIdOfUser representing the ID of an IUser
     * @param recursive If this parameter is set to <code>false</code>
     * all directly assigned roles are returned
     * if recursive is <code>true</code> all roles which are assigned via 
     * other groups to this role are returned.
     * @return String[] of uniqueIdOfRoles
     */
    public String[] getRolesOfUser(String uniqueIdOfUser, boolean recursive);

    /**
     * Returns roles which are assigned to a group identified by uniqueIdOfGroup
     * @param uniqueIdOfGroup representing the ID of an IGroup
     * @param recursive If this parameter is set to <code>false</code>
     * all directly assigned roles are returned
     * if recursive is <code>true</code> all roles which are assigned via 
     * other groups to this role are returned.
     * @return String[] of uniqueIdOfRoles
     */
    public String[] getRolesOfGroup(String uniqueIdOfGroup, boolean recursive);

    /**
     * Adds the specified user to the specified role and implicitly does a
     * commit.
     *
     * @param uniqueIdOfUser - the id of the user who will be added to the role
     * @param uniqueIdOfRole - the id of the role to which the user will be added
     */
    public void addUserToRole(String uniqueIdOfUser, String uniqueIdOfRole) throws UMException;

    /**
     * Adds the specified group to the specified role and implicitly does a
     * commit.
     *
     * @param uniqueIdOfGroup - the id of the group which will be added to the role
     * @param uniqueIdOfRole - the id of the role to which the group will be added
     */
    public void addGroupToRole(String uniqueIdOfGroup, String uniqueIdOfRole) throws UMException;

    /**
     * Remove the specified user from the specified role and implicitly does a
     * commit.
     *
     * @param uniqueIdOfUser - the id of the user which will be removed from the role
     * @param uniqueIdOfRole - the id of the role from which the user will be removed
     */
    public void removeUserFromRole(String uniqueIdOfUser, String uniqueIdOfRole) throws UMException;

    /**
     * Remove the specified group from the specified role and implicitly does a
     * commit.
     *
     * @param uniqueIdOfGroup - the id of the group which will be removed from the role
     * @param uniqueIdOfRole - the id of the role from which the group will be removed
     */
    public void removeGroupFromRole(String uniqueIdOfGroup, String uniqueIdOfRole) throws UMException;

}
