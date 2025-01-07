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

import com.sap.engine.interfaces.security.userstore.spi.UserContextSpi;
import com.sap.engine.interfaces.security.userstore.spi.FilterUsername;
import com.sap.engine.interfaces.security.userstore.spi.FilterPassword;
import com.sap.engine.interfaces.security.userstore.spi.UserInfoSpi;
import com.sap.engine.interfaces.security.userstore.listener.UserListener;
import com.sap.engine.interfaces.security.userstore.context.SearchFilter;
import com.sap.engine.interfaces.security.userstore.context.SearchResult;
import com.sap.engine.services.security.restriction.Restrictions;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

import java.security.AccessController;
import java.security.AllPermission;
import java.security.AccessControlException;
import java.security.cert.X509Certificate;
import javax.security.auth.Subject;
import java.util.Properties;

/**
 *  UserContext represents a wrapper of UserContextSpi. It calls direcetly
 * the methods of the spi, and is used only for authorization-making decisions.
 *
 * @author Jako Blagoev
 * @version 6.30
 */
public class UserContext implements com.sap.engine.interfaces.security.userstore.context.UserContext {
 //$JL-SER$
   //previous line explaination:
   //JLin Serialization Test disabled, because class is manually serialized.
   //See SecurityContext.load and SecurityContext.store

  private static final Location LOCATION = Location.getLocation(UserContext.class);

  public static final AllPermission ALL_PERMISSION = new AllPermission();

  private UserContextSpi spi = null;

  public UserContext(UserContextSpi spi, Properties props) {
    this.spi = spi;
    try {
      this.spi.enginePropertiesChanged(props);
    } catch (java.lang.SecurityException se) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000150", "Cannot instantiate UserContext.", se);
      throw se;
    }
  }

  public void propertiesChanged(Properties newprops) {
    Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_CHANGE_CONFIGURATION);

    spi.enginePropertiesChanged(newprops);
  }

  public void registerListener(UserListener userListener, int modifier) throws SecurityException {
    // to do
  }

  public void unregisterListener(UserListener listener) throws SecurityException {
    // to do
  }

  /**
   * List all users' names in an enumeration of strings.
   *
   * @return  Iterator of Strings with user names
   */
  public java.util.Iterator listUsers() throws SecurityException {
    return spi.engineListUsers();
  }

  /**
   * Search names of all users which match SearchFilter in an enumeration of strings.
   *
   * @return  Iterator of Strings with names of users matching SearchFilter
   */
  public SearchResult searchUsers(SearchFilter filter) throws SecurityException {
    return spi.engineSearchUsers(filter);
  }

  /**
   * Get an instance for a user.
   *
   * @param   userName  name of the requested user
   *
   * @return  UserInfo object instance
   */
  public com.sap.engine.interfaces.security.userstore.context.UserInfo getUserInfo(String userName) throws SecurityException {
    UserInfoSpi userSpi = spi.engineGetUserInfo(userName);

    if (userSpi == null) {
      throw new SecurityException(" Unable to get user " + userName);
    }

    return new UserInfo(userSpi);
  }

  /**
   *  Get a user with the given certificate.
   *
   * @param   cert  the search certificate
   *
   * @return  user  info instance
   *
   * @throws SecurityException  if the user cannot be retrieved
   */
  public com.sap.engine.interfaces.security.userstore.context.UserInfo getUserInfo(X509Certificate cert) throws SecurityException {
    return new UserInfo(spi.engineGetUserInfo(cert));
  }

  /**
   * Get an instance for an user, requested by logon alias.
   *
   * @param   alias  the alias of the requested user
   *
   * @return  a UserInfo object instance
   */
  public com.sap.engine.interfaces.security.userstore.context.UserInfo getUserInfoByLogonAlias(String alias) throws SecurityException {
    UserInfoSpi userSpi = spi.engineGetUserInfoByLogonAlias(alias);
  
    if (userSpi == null) {
      throw new SecurityException(" Unable to get user with alias " + alias);
    }
  
    return new UserInfo(userSpi);
  }

  /**
   * Create a new user in the user store.
   *
   * @param   userName   name of the new user (if null then generated)
   */
  public com.sap.engine.interfaces.security.userstore.context.UserInfo createUser(String userName) throws SecurityException {
    Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_CREATE_ACCOUNT);

    FilterUsername filter = spi.engineGetFilterUsername();
    if (filter != null && !filter.filterUsername(userName)) {
      throw new SecurityException("Name " + userName + " is not allowed for current configuration");
    }
    return new UserInfo(spi.engineCreateUser(userName));
  }
  
  /**
   * Create a new user in the user store with productive password.
   *
   * @param   userName   name of the new user (if null then generated)
   * @param   password   password (will be set as productive password)
   */
  public com.sap.engine.interfaces.security.userstore.context.UserInfo createUser(String userName, char[] password) throws SecurityException {
    Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_CREATE_ACCOUNT);

    FilterUsername filter = spi.engineGetFilterUsername();
    if (filter != null && !filter.filterUsername(userName)) {
      throw new SecurityException("Name " + userName + " is not allowed for current configuration");
    }
    return new UserInfo(spi.engineCreateUser(userName, password));
  }

  /**
   * Create a new user in the user store. This method is for internal use only!
   *
   * @param   userName   name of the new user (if null then generated)
   */
  public com.sap.engine.interfaces.security.userstore.context.UserInfo createUserInternal(String userName) throws SecurityException {
    //FilterUsername filter = spi.engineGetFilterUsername();
    //if (filter != null && !filter.filterUsername(userName)) {
    //  throw new SecurityException("Name " + userName + " is not allowed for current configuration");
    //}
    return new UserInfo(spi.engineCreateUser(userName));
  }

  /**
   * Delete the user with the given user name.
   *
   * @param   userName   name of the user, which shall be deleted
   */
  public void deleteUser(String userName) throws SecurityException {
    Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_REMOVE_ACCOUNT);

    spi.engineDeleteUser(userName);
  }

  /**
   * Get the filter for the user name (logonId).
   *
   * @return  user name filter, which is used in the user context
   */
  public FilterUsername getFilterUsername() throws SecurityException {
    return spi.engineGetFilterUsername();
  }

  /**
   * Set the filter for the user name (logonId).
   *
   * @param   filter      user name filter, which shall be used in the user context
   */
  public void setFilterUsername(FilterUsername filter) throws SecurityException {
    Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_CHANGE_CONFIGURATION);

    spi.engineSetFilterUsername(filter);
  }

  /**
   * Get the filter for the password.
   *
   * @return  password filter, which is used in the user context
   */
  public FilterPassword getFilterPassword() throws SecurityException {
    return spi.engineGetFilterPassword();
  }

  /**
   * Set the filter for the password.
   *
   * @param   filter      password filter, which shall be used in the user context
   */
  public void setFilterPassword(FilterPassword filter) throws SecurityException {
    Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_CHANGE_CONFIGURATION);

    spi.engineSetFilterPassword(filter);
  }

  /**
   * Ask for support of an operation on a certain user property
   *
   * @param   userProperty requested user property
   * @param   operation    operation on the requested user property
   *
   * @return  true, if the requested operation on the user property is supported
   */
  public boolean isUserPropertySupported(int userProperty, int operation) throws SecurityException {
    return spi.engineIsUserPropertySupported(userProperty, operation);
  }

  /**
   *  Tests if the <code>fillSubject</code> method is supported.
   *  One option is to check if the code invoking the method is trusted for the user store
   *  and to test if there is a secure connection set up to the user store.
   *
   * @return  true  if the user store can provide subjects for a user.
   */
  public boolean isSubjectRetrievalSupported() {
    return spi.engineIsSubjectRetrievalSupported();
  }

  /**
   *  Fills the subject with the principals and credentials of the user.
   * Used by run-as-identity aware containers.
   *
   * @param  user  the user info object
   * @param  subject  an empty Subject instance to hold the result
   *
   * @return  expiration time of the temporary credentials or tickets in the Subject.
   **/
  public long fillSubject(com.sap.engine.interfaces.security.userstore.context.UserInfo user, Subject subject) throws SecurityException {
    try {
      AccessController.checkPermission(ALL_PERMISSION);
    } catch (AccessControlException se) {
      Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_READ_CREDENTIALS);
    }

    return spi.engineFillSubject(((UserInfo)user).getSpi(), subject);
  }

  /**
   *  Removes from subject the associated principals and credentials.
   *
   * @param  subject  the subject which prinsipals and credentials will be removed.
   **/
  public void emptySubject(Subject subject) throws SecurityException {
    try {
      AccessController.checkPermission(ALL_PERMISSION);
    } catch (AccessControlException se) {
      Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_REMOVE_CREDENTIALS);
    }

    spi.engineEmptySubject(subject);
  }

  /**
   *  List the names of the root users.
   *
   * @return  iterator with the names of the users , which don't have parent groups.
   */
  public java.util.Iterator listRootUsers() {
    return spi.engineListRootUsers();
  }

  /**
   *
   * @return the number of the allowed invalid login attempts.
   */
  public int getLockAfterInvalidAttempts() {
    return spi.engineGetLockAfterInvalidAttempts();
  }

  /**
   *
   *@return a boolean value stating, if this user store is in emergency mode.
   */
  public boolean isInEmergencyMode() {
    return spi.engineIsInEmergencyMode();
  }

  /**
   *
   * @param   userName  name of the requested user
   *
   * @return a boolean value stating, if the specified user is the emergency user.
   */
  public boolean isEmergencyUser(String userName) {
    return spi.engineIsEmergencyUser(userName);
  }

  /**
   *
   * @return the name of the emergency user.
   */
  public String getEmergencyUserName() {
    return spi.engineGetEmergencyUserName();
  }

  /**
   *
   *@return an empty SearchFilter object.
   */
  public SearchFilter getSearchFilter() {
    return spi.engineGetSearchFilter();
  }

  /**
   * Refresh the specified user's entry in the cache.
   *
   * @param   userName  name of the requested user
   */
  public void refresh(String userName) {
    spi.engineRefresh(userName);
  }

  /**
   *
   * @return the name of the anonymous user, configured for the corresponding userstore.
   */
  public String getAnonymousUserName() {
    return spi.engineGetAnonymousUserName();
  }

}

