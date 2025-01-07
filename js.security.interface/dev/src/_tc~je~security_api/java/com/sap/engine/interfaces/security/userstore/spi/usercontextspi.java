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

import com.sap.engine.interfaces.security.userstore.listener.UserListener;
import com.sap.engine.interfaces.security.userstore.context.SearchResult;
import com.sap.engine.interfaces.security.userstore.context.SearchFilter;

import javax.security.auth.Subject;
import java.util.Properties;
import java.security.cert.X509Certificate;

/**
 * The User Context Service Provider Interface.
 *
 * @author  Boris Koeberle
 * @version 6.30
 */
public interface UserContextSpi {

  /**
   * List all users' names in an iterator
   *
   * @return  iterator with all user names
   */
  public java.util.Iterator engineListUsers() throws SecurityException;

  /**
   * Search names of all users which match SearchFilter in an enumeration of strings.
   *
   * @return  Iterator of Strings with names of users matching SearchFilter
   */
  public SearchResult engineSearchUsers(SearchFilter filter) throws SecurityException;


  /**
   * Get an instance for an user.
   *
   * @param   userName  the name of the requested user
   *
   * @return  a UserInfo object instance
   */
  public UserInfoSpi engineGetUserInfo(String userName) throws SecurityException;


  /**
   *  Get a user with the given certificate.
   *
   * @param   cert  the search certificate
   *
   * @return  a UserInfo object instance
   *
   * @throws SecurityException  if the user cannot be retrieved
   */
  public UserInfoSpi engineGetUserInfo(X509Certificate cert) throws SecurityException;

  /**
   * Get an instance for an user, requested by logon alias.
   *
   * @param   alias  the alias of the requested user
   *
   * @return  a UserInfo object instance
   */
  public UserInfoSpi engineGetUserInfoByLogonAlias(String alias) throws SecurityException;



  /**
   * Create a new user in the user store.
   *
   * @param   userName   the name of the new user
   */
  public UserInfoSpi engineCreateUser(String userName) throws SecurityException;

  /**
   * Create a new user in the user store with productive password.
   * 
   * @param   userName   name of the new user (if null then generated)
   * @param   password   password (will be set as productive password)
   */
  public UserInfoSpi engineCreateUser(String userName, char[] password) throws SecurityException;
  
  /**
   * Delete the user with the given user name.
   *
   * @param   userName   the name of the user, which shall be deleted
   */
  public void engineDeleteUser(String userName) throws SecurityException;


  /**
   * Get the UserNameFilter.
   *
   * @return  the user name filter, which is used in the user context
   */
  public FilterUsername engineGetFilterUsername() throws SecurityException;


  /**
   * Set the UserNameFilter.
   *
   * @param   filter      the user name filter to be set in the user context
   */
  public void engineSetFilterUsername(FilterUsername filter) throws SecurityException;


  /**
   * Get the PasswordFilter.
   *
   * @return  the password filter, which is used in the user context
   */
  public FilterPassword engineGetFilterPassword() throws SecurityException;


  /**
   * Set the PasswordFilter.
   *
   * @param   filter      the password filter to be set in the user context
   */
  public void engineSetFilterPassword(FilterPassword filter) throws SecurityException;


  /**
   * Ask for support of a certain user property
   *
   * @param   userProperty the user property
   * @param   operation    the operation to be performed on the user property
   *
   * @return  true, if this operation on the user property is supported
   */
  public boolean engineIsUserPropertySupported(int userProperty, int operation) throws SecurityException;


  /**
   * Register a Listener with the UserContext.
   *
   * @param   userListener   the UserListener instance
   * @param   modifier       the events, which shall be listened to
   */
  public void engineRegisterListener(UserListener userListener, int modifier) throws SecurityException;


  /**
   * Unregister a UserListener.
   *
   * @param   userListener   the UserListener instance
   */
  public void engineUnregisterListener(UserListener userListener) throws SecurityException;


  /**
   * Ask for the support of a subject retrieval.
   *
   * @return   true, if subject retrieval is supported
   */
  public boolean engineIsSubjectRetrievalSupported();


  /**
   * Fills the subject with the principals and credentials of the user.
   * Used by run-as-identity aware containers.
   *
   * @param   user     the UserInfo instance
   * @param   subject  the empty Subject instance to hold the result
   *
   * @return  expiration time of the temporary credentials or tickets in the Subject.
   **/
  public long engineFillSubject(UserInfoSpi user, Subject subject) throws SecurityException;

  /**
   *  Removes from subject the associated principals and credentials.
   *
   * @param  subject  the subject which prinsipals and credentials will be removed.
   **/
  public void engineEmptySubject(Subject subject) throws SecurityException;

  /**
   * Change the properties of the UserContext.
   *
   * @param   newProps   new Properties
   */
  public void enginePropertiesChanged(Properties newProps);

  /**
   *  List the names of the root users.
   *
   * @return  iterator with the names of the users , which don't have parent groups.
   */
  public java.util.Iterator engineListRootUsers() throws SecurityException;

  /**
   *
   * @return the number of the allowed invalid login attempts.
   */
  public int engineGetLockAfterInvalidAttempts();

  /**
   *
   *@return a boolean value stating, if this user store is in emergency mode.
   */
  public boolean engineIsInEmergencyMode();

  /**
   *
   * @param   userName  name of the requested user
   *
   * @return a boolean value stating, if the specified user is the emergency user.
   */
  public boolean engineIsEmergencyUser(String userName);

  /**
   *
   * @return the name of the emergency user.
   */
  public String engineGetEmergencyUserName();

  /**
   *
   * @return an empty SearchFilter object.
   */
  public SearchFilter engineGetSearchFilter();

  /**
   * Refresh the specified user's entry in the cache.
   *
   * @param   userName  name of the requested user
   */
  public void engineRefresh(String userName);
  
	/**
   *
   * @return the name of the anonymous user, configured for the corresponding userstore.
   */
  public String engineGetAnonymousUserName();

}

