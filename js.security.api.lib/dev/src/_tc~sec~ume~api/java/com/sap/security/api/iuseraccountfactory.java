package com.sap.security.api;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import java.util.Map;


/**
 *  User account factory will handle authentication and retrieval of user
 *  account information from persistence storage.
 * 
 * <p><b>NOTE</b>:  As this interface�can be extended, this interface can be freely used, 
 * but must not be implemented.
 * 
 *
 *@author     Rajeev Madnawat
 *@created    June 7, 2001
 */
public interface IUserAccountFactory extends IConfigurable {
    /**
     * Creates a new IUserAccount type object. After
     * setting the appropriate data via set-methods, the user account object
          * must be commited to the persistence storage via {@link IPrincipalMaint#commit()}.
     *
     *@param     logonid  logon user id
         * <p> Note: The logonid has to be unique for ALL data stores
     *@param     uniqueIdOfUser - uniqueID of the user that should be assigned to this account
     *@return    Object of type IUserAccount
     *@exception UserAccountAlreadyExistsException if an account with this <code>logonid</code>
     *           already exists
     */
    public IUserAccount newUserAccount(String logonid, String uniqueIdOfUser)
        throws UMException;

    /**
     * Creates a new IUserAccount type object. After
     * setting the appropriate data via set-methods, the user account object
     * must be commited to the persistence storage via {@link IUserFactory#commitUser(IUserMaint, IUserAccount)}.
     * 
     * Note: This object cannot be commited via {@link IPrincipalMaint#commit()}
     *
     *@param     logonid  logon user id
         * <p> Note: The logonid has to be unique for ALL data stores
     *@return    Object of type IUserAccount
     *@exception UserAccountAlreadyExistsException if an account with this <code>logonid</code>
     *           already exists
     */
    public IUserAccount newUserAccount(String logonid)
        throws UMException;

    /**
     *  Gets UserAccount object from the database for a given uid
     *
     *@param     logonid  Description of Parameter
     *@return    UserAccount object
     *@exception UMException Exception will be thrown if there is an
     *           error.
     */
    public IUserAccount getUserAccountByLogonId(String logonid)
        throws UMException;

	/**
	 *  Gets UserAccount object from the database for a given uid
	 *
	 *@param     logonid  Description of Parameter
     *@param populateAttributes The attributes that should be populated
	 *@return    UserAccount object, or null if no user
	 *           found for this logon uid
	 *@exception UMException Exception will be thrown if there is an
	 *           error.
	 */
	public IUserAccount getUserAccountByLogonId(String logonid,
		AttributeList populateAttributes)
		throws UMException;

    /**
     *  Gets UserAccount object from the database for a given uid
     *
     *@param     uniqueId  Description of Parameter
     *@return    UserAccount object, or null if no user
     *           found for this logon uid
     *@exception UMException Exception will be thrown if there is an
     *           error.
     */
    public IUserAccount getUserAccount(String uniqueId)
        throws UMException;

    /**
     * Gets the UserAccount object with the given unique ID and populates
     * the attributes in populateAttributes
     * @param populateAttributes The attributes that should be populated
	 * <p>Note: in case only attribute 
	 * {@link com.sap.security.api.logon.ILoginConstants#LOGON_UID_ALIAS}
	 * is specified the existence of the returned IUserAccount object 
	 * may not be checked which may lead to following RuntimeException 
	 * {@link com.sap.security.api.PrincipalNotAccessibleException}
	 * if other attributes of this IUserAccount object are
	 * accessed. 
     * @exception NoSuchUserAccountException if no userAccount with the
     * given unique ID exists
     */
    public IUserAccount getUserAccount(String uniqueId,
        AttributeList populateAttributes)
        throws com.sap.security.api.UMException;

    /**
     *  Returns UserAccount to whom this certificate belongs. returns null if this
     *  certificate is not mapped to any user.
     *
     *@param     cert X509Certificate certificate
     *@return    UserAccount object
     *@exception CertificateException if there is a problem with the input
     *           certificate
     *@exception UMException  Exception will be throws if there is an
     *           error.
     * @exception NoSuchUserAccountException if no userAccount is mapped to the certificate
     */
    public IUserAccount getUserAccount(X509Certificate cert)
        throws CertificateException, UMException, 
            com.sap.security.api.ticket.TicketException;

    /**
     *  Authenticate the supplied credentials and returns user account object. The
     *  valid keys in the Map could be "j_user" for user id, "j_password" for
     *  password ,"javax.servlet.request.X509Certificate" for certificate. other
     *  values are allowed provided that there is an implementation to understand
     *  those.
     *
     *@param  credentials                        Map containing user credential
     *      parameters
     *@return                                    UserAccount object, or null if no
     *      user is found for supplied credentials
     *@exception  UMException        Exception will be throws if there
     *      is an error.
     *@exception  AuthenticationFailedException  Exception will be throws if
     *      credentials could not be verified.
     *  The message of the exception is the key for the error message.<br>
     *  Possible keys are<ul>
	 *  <li>SecurityPolicy.USER_AUTH_FAILED: general logon failure. Logon id or password wrong, logon id not existent etc.</li>
	 *  <li>SecurityPolicy.CERT_AUTH_FAILED: the client certificate is not mapped to a user.</li>
	 *  <li>SecurityPolicy.PASSWORD_EXPIRED: the user's password has expired.</li>
	 *  <li>SecurityPolicy.SAPSTAR_ACTIVATED: the super user SAP* is activated and therefore no other user can logon.</li>
	 *  </ul>
     *@exception  UserLockedException            Exception will be throws if user
     *      is locked.
     *  The message of the exception is the key for the error message.<br>
     *  Possible keys are<ul>
	 *  <li>SecurityPolicy.ACCOUNT_LOCKED_LOGON: the user account is locked due to logon failures.</li>
	 *  <li>SecurityPolicy.ACCOUNT_LOCKED_ADMIN: the user account is locked by administrator and can't logon.</li>
	 *  </ul>
     *@exception  CertificateException           a problem with certificates.
     */
    public IUserAccount getAuthenticatedUserAccount(Map credentials)
        throws UMException, AuthenticationFailedException, UserLockedException, 
            com.sap.security.api.ticket.TicketException, CertificateException;

    /**
     *  Return UserAccount object based on the credentials supplied. Credentials will
     *  not be authenticated.
     *
     *
     *@param  credentials                  user credentials
     *@return                              UserAccount or null if the supplied user credentials do not match with any user.
     *@exception  UMException  Description of Exception
     */
    public IUserAccount getUserAccount(Map credentials)
        throws UMException, CertificateException;

    /**
     *  Delete this user account from persistence storage
     *
     *@param uniqueId           user account
     *@exception  UMException  UMException is thrown
     *            is delete operation is failed for some reason
     * @exception NoSuchUserAccountException if the user account does not exist
     */
    public void deleteUserAccount(String uniqueId) throws UMException;

    /**
          * Search for user accounts in the persistence storage which match the criteria
          * specified in the
         * given search filter. In order to get a user account search filter use
         * {@link #getUserAccountSearchFilter()}.
         * You can define a search filter using methods of class {@link IUserAccountSearchFilter}.
         * @param filter defined to search for user accounts
         * @return ISearchResult result of the search operation {@link ISearchResult}
     */
    public ISearchResult search(IUserAccountSearchFilter filter)
        throws UMException;

    /**
     *  Returns all user accounts of the given user
     *
     *@param  uniqueIdOfUser                         The user
     *@return                              Array of user accounts
     *@exception  UMException  Description of Exception
     */
    public IUserAccount[] getUserAccounts(String uniqueIdOfUser)
        throws UMException;

	/**
	 *  Returns all user accounts of the given user
	 *
	 *@param  uniqueIdOfUser                         The user
	 *@param  attributeList 			   The attributes that should be populated
	 *@return                              Array of user accounts
	 *@exception  UMException  Description of Exception
	 */
	public IUserAccount[] getUserAccounts(String uniqueIdOfUser,
		AttributeList attributeList)
		throws UMException;

    /**
       * Gets an IUserAccount objects for a uniqueId of a user account.
       * @param  uniqueId - uniqueId of the user account
       * @exception NoSuchPrincipalException if the unique ID does not exist
       * @return a mutable IUserAccount object which can be modified.
       */
    public IUserAccount getMutableUserAccount(String uniqueId)
        throws UMException;

    /**
      *  Returns an IUserAccountSearchFilter object to be used to specify query attributes
      * <p>
      * IUserAccountSearchFilter only contains attributes which will be queried
      *
      *@return     IUserAccountSearchFilter
      *@exception  UMException
      *@exception  FeatureNotAvailableException
      */
    public IUserAccountSearchFilter getUserAccountSearchFilter()
        throws UMException;

    // -----------------------------
    // Register/UnRegister observers ---------------------------------------------
    // -----------------------------

    /**
    * registerListener allows to subscribe to a predefined eventName
    * {@link UserAccountListener}
    * The caller has to provide a receiver object which implements UserAccountListener
    * @param userAccountListener object which implements interface UserAccountListener
    * @param modifier constant defined in {@link UserAccountListener}
    */
    public void registerListener(UserAccountListener userAccountListener,
        int modifier);

    /**
     * registerListener allows to subscribe to a predefined eventName
     * {@link UserAccountListener}
     * The caller has to provide a receiver object which implements UserAccountListener
     * @param userAccountListener object which implements interface UserAccountListener
     * @param modifier constant defined in {@link UserAccountListener}
     * @param notifyAfterPhysicalCommitCompleted Allows callers when set to false, to get a notification before the physical transaction is completed in order to include their actions into the same physical transaction.
     */
     public void registerListener(UserAccountListener userAccountListener,
         int modifier, boolean notifyAfterPhysicalCommitCompleted);
    
    /***
    * unregisterListener unsubscribes a receiver from a previously subscribed event.
    * @param userAccountListener object which implements interface UserAccountListener
    */
    public void unregisterListener(UserAccountListener userAccountListener);
    
	/***
	* removes the user account object which has the specified logonid from the factory's cache
	* 
	* Note: Use this method carefully, because calling it too often may cause performance problems
	* 
	* @param logonid the logonid of the user account
	* @throws UMException if a error occurs
	*/
    public void invalidateCacheEntryByLogonId(String logonid) throws UMException;

	/***
	* removes the user account object which has the specified uniqueid from the factory's cache
	* 
	* Note: Use this method carefully, because calling it too often may cause performance problems
	* 
	* @param uniqueid the uniqueid of the user account
	* @throws UMException if a error occurs
	*/
	public void invalidateCacheEntry(String uniqueid) throws UMException;
}
