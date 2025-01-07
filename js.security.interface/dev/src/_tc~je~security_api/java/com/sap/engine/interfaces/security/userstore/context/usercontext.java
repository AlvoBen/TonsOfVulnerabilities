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

import com.sap.engine.interfaces.security.userstore.spi.FilterUsername;
import com.sap.engine.interfaces.security.userstore.spi.FilterPassword;
import com.sap.engine.interfaces.security.userstore.listener.UserListener;
import javax.security.auth.Subject;

import java.util.Properties;
import java.security.cert.X509Certificate;

/**
 * UserContext represents a wrapper of UserContextSpi. It calls directly
 * the methods of the spi and is used only for authorization decisions.
 *
 * @author  d031387
 * @version 6.40
 *
 */
public interface UserContext {

  /**
   * Constants for user attributes in search filters
   */
  public final static String ATTRIBUTE_USERNAME = "user.name";
  public final static String ATTRIBUTE_PARENT_GROUP = "user.parentgroup";
  public final static String ATTRIBUTE_LOCKED = "user.locked";

  /**
   * Other constants.
   */
  public final static int PROPERTY_CREATE_DATE = 1; //java.util.Date
  public final static int PROPERTY_MODIFY_DATE = 2; //java.util.Date
  public final static int PROPERTY_FAILED_LOGON_COUNT = 3; //int
  public final static int PROPERTY_LAST_FAILED_LOGON_DATE = 4; //java.util.Date
  public final static int PROPERTY_SUCCESSFUL_LOGON_COUNT = 5; //int
  public final static int PROPERTY_LAST_SUCCESSFUL_LOGON_DATE = 6; //java.util.Date
  public final static int PROPERTY_VALID_FROM_DATE = 7; //java.util.Date
  public final static int PROPERTY_VALID_TO_DATE = 8; //java.util.Date

  public final static int PROPERTY_LAST_CHANGED_PASSWORD_DATE = 10; //java.util.Date
  public final static int PROPERTY_FORCE_TO_CHANGE_PASSWORD = 11; //long
  final public static int PROPERTY_LOCK_STATUS = 12; //int

  final public static int LOCKED_NO = 0;//iser is not LOCKED
  final public static int LOCKED_AUTO = 1;//user is automatically LOCKED
  final public static int LOCKED_BY_ADMIN = 2;//user is LOCKED by administrator

  public final static int OPERATION_READ = 1;
  public final static int OPERATION_WRITE = 2;
  public final static int OPERATION_CHECK = 3;


  public void propertiesChanged(Properties newprops);

  public void registerListener(UserListener userListener, int modifier) throws SecurityException;

  public void unregisterListener(UserListener listener) throws SecurityException;

  /**
   * List all users' names in an enumeration of strings.
   *
   * @return  Iterator of Strings with user names
   * @deprecated Use {@link UserContext#searchUsers(SearchFilter filter) instead
   */
  public java.util.Iterator listUsers() throws SecurityException;

  /**
   * Search names of all users which match SearchFilter in an enumeration of strings.
   *
   * @return  Iterator of Strings with names of users matching SearchFilter
   */
  public SearchResult searchUsers(SearchFilter filter) throws SecurityException;

  /**
   * Get an instance for a user.
   *
   * @param   userName  name of the requested user
   *
   * @return  UserInfo object instance
   */
  public UserInfo getUserInfo(String userName) throws SecurityException;

  /**
   *  Get a user with the given certificate.
   *
   * @param   cert  the search certificate
   *
   * @return  user  info instance
   *
   * @throws SecurityException  if the user cannot be retrieved
   */
  public UserInfo getUserInfo(X509Certificate cert) throws SecurityException;

  /**
   * Get an instance for an user, requested by logon alias.
   *
   * @param   alias  the alias of the requested user
   *
   * @return  a UserInfo object instance
   */
  public UserInfo getUserInfoByLogonAlias(String alias) throws SecurityException;

  /**
   * Create a new user in the user store.
   *
   * @param   userName   name of the new user (if null then generated)
   */
  public UserInfo createUser(String userName) throws SecurityException;
  
	/**
	 * Create a new user in the user store with productive password.
	 *
	 * @param   userName   name of the new user (if null then generated)
	 * @param   password   password (will be set as productive password)
	 */
  public UserInfo createUser(String userName, char[] password) throws SecurityException;

  /**
   * Delete the user with the given user name.
   *
   * @param   userName   name of the user, which shall be deleted
   */
  public void deleteUser(String userName) throws SecurityException;

  /**
   * Get the filter for the user name (logonId).
   *
   * @return  user name filter, which is used in the user context
   */
  public FilterUsername getFilterUsername() throws SecurityException;

  /**
   * Set the filter for the user name (logonId).
   *
   * @param   filter      user name filter, which shall be used in the user context
   */
  public void setFilterUsername(FilterUsername filter) throws SecurityException;

  /**
   * Get the filter for the password.
   *
   * @return  password filter, which is used in the user context
   */
  public FilterPassword getFilterPassword() throws SecurityException;

  /**
   * Set the filter for the password.
   *
   * @param   filter      password filter, which shall be used in the user context
   */
  public void setFilterPassword(FilterPassword filter) throws SecurityException;

  /**
   * Ask for support of an operation on a certain user property
   *
   * @param   userProperty requested user property
   * @param   operation    operation on the requested user property
   *
   * @return  true, if the requested operation on the user property is supported
   */
  public boolean isUserPropertySupported(int userProperty, int operation) throws SecurityException;

  /**
   *  Tests if the <code>fillSubject</code> method is supported.
   *  One option is to check if the code invoking the method is trusted for the user store
   *  and to test if there is a secure connection set up to the user store.
   *
   * @return  true  if the user store can provide subjects for a user.
   */
  public boolean isSubjectRetrievalSupported();

  /**
   *  Fills the subject with the principals and credentials of the user.
   * Used by run-as-identity aware containers.
   *
   * @param  user  the user info object
   * @param  subject  an empty Subject instance to hold the result
   *
   * @return  expiration time of the temporary credentials or tickets in the Subject.
   **/
  public long fillSubject(UserInfo user, Subject subject) throws SecurityException;

  /**
   *  Removes from subject the associated principals and credentials.
   *
   * @param  subject  the subject which prinsipals and credentials will be removed.
   **/
  public void emptySubject(Subject subject) throws SecurityException;

  /**
   *  List the names of the root users.
   *
   * @return  iterator with the names of the users , which don't have parent groups.
   */
  public java.util.Iterator listRootUsers() throws SecurityException;

  /**
   *
   * @return the number of the allowed invalid login attempts.
   */
  public int getLockAfterInvalidAttempts();

  /**
   *
   * @return a boolean value stating, if this user store is in emergency mode.
   */
  public boolean isInEmergencyMode();

  /**
   *
   * @param   userName  name of the requested user
   *
   * @return a boolean value stating, if the specified user is the emergency user.
   */
  public boolean isEmergencyUser(String userName);

  /**
   *
   * @return the name of the emergency user.
   */
  public String getEmergencyUserName();

  /**
   *
   * @return an empty SearchFilter object.
   */
  public SearchFilter getSearchFilter();

  /**
   * Refresh the specified user's entry in the cache.
   *
   * @param   userName  name of the requested user
   */
  public void refresh(String userName);
  
	/**
   *
   * @return the name of the anonymous user, configured for the corresponding userstore.
   */
  public String getAnonymousUserName();

}

