package com.sap.security.api;

/**
 * The user factory provides means to
 * <ol>
 *   <li>instantiate user objects,</li>
 *   <li>create new users (possibly by copying the data of an existing one),</li>
 *   <li>delete users,</li>
 *   <li>search for users based on different criteria and</li>
 *   <li>perform mass commit/rollback operations on a set of users.</li>
 * </ol>
 * 
 * <p><b>NOTE</b>:  As this interface�can be extended, this interface can be freely used, 
 * but must not be implemented.
 * 
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 * @version 1.0
 */
public  interface IUserFactory
        extends IConfigurable {

    public final static String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/IUserFactory.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

/**
 * Gets the unique IDs of all users in the user store.<p> Note this operation
 * might be very time consuming because all uniqueIDs of the complete user population
 * are returned.
 * @return {@link ISearchResult}
 */
    public ISearchResult getUniqueIDs () throws com.sap.security.api.UMException;


/**
 * Gets the user object with the given unique ID
 * @param uniqueID String representing the uniqueID of a user object.
 * @exception NoSuchUserException if no user with the given unique ID
 * exists
 */
    public IUser getUser (String uniqueID) throws com.sap.security.api.UMException;
    
/**
 * Gets the user object with the given unique ID and populates 
 * the attributes specified in populateAttributes
 * @param uniqueID String representing the uniqueID of a role object.
 * @param populateAttributes The attributes that should be populated.
 * <p>Note: in case only attribute 
 * {@link com.sap.security.api.IPrincipal#UNIQUE_NAME} is specified
 * the existence of the returned IUser object may not be checked which
 * may lead to following RuntimeException 
 * {@link com.sap.security.api.PrincipalNotAccessibleException}
 * if other attributes of this IUser object are accessed.
 * @exception NoSuchUserException if no user with the given unique ID
 * exists
 */
    public IUser getUser (String uniqueID, AttributeList populateAttributes)
        throws com.sap.security.api.UMException;


/**
 * Gets the user object with the given uniqueName
 * <p><b>Note</b>: If you use this method
 * be prepared to get an exception if multiple objects with the same name are found
 * @param uniqueName of IUser object    
 * @exception NoSuchUserException if no user with the given unique name
 * exists
 * @exception UMException if given unique name is not unique
 */
    public IUser getUserByUniqueName (String uniqueName) throws com.sap.security.api.UMException;

	/**
	 * Gets the user object with the given uniqueName
     * <p><b>Note</b>: If you use this method
     * be prepared to get an exception if multiple objects with the same name are found
	 * @param uniqueName of IUser object    
	 * @param attributeList The attributes that should be populated
	 * @exception NoSuchUserException if no user with the given unique name
	 * exists
     * @exception UMException if given unique name is not unique 
	 */
		public IUser getUserByUniqueName (String uniqueName,
			AttributeList attributeList) 
			throws com.sap.security.api.UMException;

/**
 * Gets the user objects for multiple unique IDs
 * @param uniqueIDs array of uniqueIDs which are used to get an array of IUser
 * objects.
 * @exception NoSuchUserException if one or more of the given unique IDs
 * are not assigned to any user
 * @return IUser[] array of IUser objects
 */
    public IUser[] getUsers (String[] uniqueIDs)
        throws com.sap.security.api.UMException;

/**
 * Gets the user objects for multiple unique IDs and 
 * populates the attributes specified in populateAttributes
 * @param uniqueIDs array of uniqueIDs which are used to get an array of IUser
 * objects.
 * @param populateAttributes The attributes that should be populated
 * {@link com.sap.security.api.AttributeList}
 * @exception NoSuchUserException if one or more of the given unique IDs
 * are not assigned to any user
 * @return IUser[] array of IUser objects
 */
    public IUser[] getUsers (String[] uniqueIDs, AttributeList populateAttributes)
        throws com.sap.security.api.UMException;

/**
 * Creates a new, initially blank user object with the given uniqueName. After
 * setting the appropriate data via IUserMaint's set-methods, the user object
 * must be commited to the user store via {@link IPrincipalMaint#commit()}.
 * @param uniqueName of new IRole object.
 * Note: This name has to be unique for ALL data stores
 * @exception UserAlreadyExistsException if a user with the given uniqueName already
 * exists; depending on the implementation, checking for already existing users
 * may also be deferred to when {@link IPrincipalMaint#commit()} is called.
 * @exception InvalidIDException if <code>uniqueName</code> doesn't meet the user
 * store's requirements (e.g. its too long)
 * @exception UserAlreadyExistsException if user with uniqueName already exists
 * @return IUserMaint a user object which can be modified
 */
    public IUserMaint newUser (String uniqueName)
        throws com.sap.security.api.UMException;

/**
 * Creates a new user with the given uniqueName. The existing user
 * <code>copyFrom</code> will be used as a template, i.e. some (but not
 * necessarily all) attributes will be copied to the new user object.
 * @param uniqueName of new IRole object.
 * Note: This name has to be unique for ALL data stores
 * @param copyFrom instance of an IUser object which should be copied.
 * @exception UserAlreadyExistsException if a user with the given uniqueName already
 * exists; depending on the implementation, checking for already existing users
 * may also be deferred to when {@link IPrincipalMaint#commit()} is called.
 * @exception InvalidIDException if <code>uniqueName</code> doesn't meet the user
 * store's requirements (e.g. its too long)
 * @exception UserAlreadyExistsException if user with uniqueName already exists
 * @return IUserMaint a user object which can be modified
 */
    public IUserMaint newUser (String uniqueName, IUser copyFrom)
        throws com.sap.security.api.UMException;

/**
 * Delete a user from the user store
 * Note: deletes also the accounts which are assigned to this user
 *       as well as the direct group and role assignments of this user.
 * @param uniqueID String representing the uniqueID of a user object.
 * @exception UMException if the user can't be deleted
 * @exception NoSuchUserException if the user does not exist
 */
    public void deleteUser (String uniqueID)
        throws UMException;

/**
 * Search for users in the user store which match the criteria specified in the
 * given <code>filter</code>. In order to get a user search filter use 
 * {@link #getUserSearchFilter()}.
 * You can define a search filter using {@link IUserSearchFilter}.
 * @return ISearchResult result of the search operation {@link ISearchResult}
 */
    public ISearchResult searchUsers (IUserSearchFilter filter) throws UMException;

/**
 * Search for users in the user store and user account store which match the criteria specified in the<p>
 * given <code>ufilter</code> and <code>uafilter</code> and combine the result.
 * In order to get a user search filter use {@link #getUserSearchFilter()} and 
 * {@link IUserAccountFactory#getUserAccountSearchFilter()}.
 * @return ISearchResult result of the search operation {@link ISearchResult}
 */
    public ISearchResult searchUsers (IUserSearchFilter ufilter, IUserAccountSearchFilter uafilter)
        throws UMException;

    
    /**
     *  Returns a User object who matches the given personid.
     *
     *@param  personid             PersonID of the user
     *@return IUser           the user object
     *@exception UMException  in case of an error
     *@exception NoSuchUserException  if the user does not exist
     */
    public IUser getUserByPersonID(String personid) throws UMException;

    /**
     *  Returns a User object who matches the given personid.
     *
     *@param  personid             PersonID of the user
     *@param  attributeList The attributes that should be populated
     *@return IUser           the user object
     *@exception UMException  in case of an error
     *@exception NoSuchUserException  if the user does not exist
     */
    public IUser getUserByPersonID(String personid,
    			AttributeList attributeList) throws UMException;
    
/**
 *  Returns a User object for an existing user.
 *
 *@param  logonid             Logon id of the user
 *@return IUser           the user object
 *@exception UMException  in case of an error
 *@exception NoSuchUserException  if the user does not exist
 */
    public IUser getUserByLogonID( String logonid ) throws UMException;

	/**
	 *  Returns a User object for an existing user.
	 *
	 *@param  logonid             Logon id of the user
	 *@param  attributeList The attributes that should be populated
	 *@return IUser           the user object
	 *@exception UMException  in case of an error
	 *@exception NoSuchUserException  if the user does not exist
	 */
	public IUser getUserByLogonID( String logonid, 
		AttributeList attributeList) 
		throws UMException;
	
	/**
	 *  Returns a User object for an existing user.
	 *  
	 *  Note: Use this method carefully, because the user objects returned by this
	 *  method are not read from a cache, which may cause performance problems.
	 *
	 *@param  logonalias             Logon alias of the user
	 *@param  attrlist               The attribute list or null.
	 *@return IUser           the user object
	 *@exception UMException  in case of an error
	 *@exception NoSuchUserException  if the user does not exist
	 *@exception DuplicateKeyException if multiple users have the same logonalias
	 */
	 public IUser getUserByLogonAlias( String logonalias, AttributeList attrlist ) throws UMException;
	

/**
 * Creates a new, initially blank user objects. Given uniqueNames are set to the users. After
 * setting the appropriate data via IUserMaint's set-methods, each user object
 * must be saved and commited to the user store via {@link IPrincipalMaint#commit()}. For batch
 * save and commit use {@link IPrincipalMaint#save()} 
 * and {@link IPrincipalMaint#commit()}
 * @param  uniqueNames   Array of uniqueNames
 * Note: This name has to be unique for ALL data stores
 * @exception UserAlreadyExistsException if user with uniqueName already exists
 * @return IUserMaint[]  array of user objects which can be modified
 */
    public IUserMaint[] newUsers(String[] uniqueNames) throws com.sap.security.api.UMException;

/**
 * Get a user object which can be modified. This method returns an object
 * which implements
 * <code>IUserMaint</code> interface which contains the corresponding
 * set-methods. After
 * setting the appropriate data via IUserMaint's set-methods, each user object
 * must be saved and commited to the user store via {@link IPrincipalMaint#commit()}. 
 * @param uniqueId of an IUser object
 * @return IUserMaint[]  array of user objects which can be modified
*/
    public IUserMaint getMutableUser(String uniqueId) throws UMException;

/**
 *  Returns an IUserSearchFilter object to be used to specify query attributes
 *@return     IUserSearchFilter container for values to be used as a search
 * filter
 *@exception  UMException
 *@exception  FeatureNotAvailableException
 */
    public IUserSearchFilter getUserSearchFilter() throws UMException;

// -----------------------------
// Register/UnRegister observers ---------------------------------------------
// -----------------------------
/**
* registerListener allows to subscribe to a predefined eventName
* {@link UserListener}
* The caller has to provide a receiver object which implements UserListener
* @param userListener object which implements interface UserListener
* @param modifier constant defined in {@link UserListener}
*/
    public void registerListener( UserListener userListener, int modifier);

    /**
    * registerListener allows to subscribe to a predefined eventName
    * {@link UserListener}
    * The caller has to provide a receiver object which implements UserListener
    * @param userListener object which implements interface UserListener
    * @param modifier constant defined in {@link UserListener}
     * @param notifyAfterPhysicalCommitCompleted Allows callers when set to false, to get a notification before the physical transaction is completed in order to include their actions into the same physical transaction.
    */
        public void registerListener( UserListener userListener, int modifier, boolean notifyAfterPhysicalCommitCompleted);
    
/***
* unregisterListener unsubscribes a receiver from a previously subscribed event.
* @param userListener object which implements interface UserListener   
*/
    public void unregisterListener( UserListener userListener);

	/***
	* removes the user object which belongs to the account which has the specified logonid 
	* from the factory's cache. Calling this method also removes the user account which has 
	* the specified logonid from the UserAccountFactory's cache. 
	* 
	* Note: Use this method carefully, because calling it too often may cause performance problems
	* 
	* @param logonid the logonid of the user account
	* @throws UMException if a error occurs
	*/
	public void invalidateCacheEntryByLogonId(String logonid) throws UMException;

	/***
	* removes the user object which has the specified unique name 
	* from the factory's cache
	* 
	* Note: Use this method carefully, because calling it too often may cause performance problems
	*  
	* 	* @param uniqueName the uniqueName of the user 
	* @throws UMException if an error occurs
	*/
	public void invalidateCacheEntryByUniqueName(String uniqueName) throws UMException;
	
	/***
	* removes the user object which has the specified uniqueid from the factory's cache
	* 
	* Note: Use this method carefully, because calling it too often may cause performance problems
	* 
	* @param uniqueid the uniqueid of the user 
	* @throws UMException if an error occurs
	*/
	public void invalidateCacheEntry(String uniqueid) throws UMException;
	
	/**
	 * Creates the user and the account object within one transaction
	 * 
	 * Note: Both objects have to be new or exist already. Mixing 
	 *       creation and update is not possible
	 * 
	 * @param user the user object
	 * @param account the user account object
	 * @exception UMException if an error occurs
	 */
	public void commitUser(IUserMaint user, IUserAccount account) throws UMException;

	/**
	 * Returns whether a user can be created with the active UME configuration.
	 * 
	 * @return <code>true</code> if an IUser can be created, otherwise <code>false</code>.
	 * @throws UMException if an error occurs
	 */
	public boolean isUserCreationPossible() throws UMException;
	
}
