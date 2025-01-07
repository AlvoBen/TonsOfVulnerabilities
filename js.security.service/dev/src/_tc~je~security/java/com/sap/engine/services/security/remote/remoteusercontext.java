package com.sap.engine.services.security.remote;

import java.rmi.*;
import javax.security.auth.*;
import com.sap.engine.interfaces.security.userstore.spi.FilterPassword;
import com.sap.engine.interfaces.security.userstore.spi.FilterUsername;
import com.sap.engine.interfaces.security.userstore.context.SearchFilter;

public interface RemoteUserContext
  extends Remote {

  /**
   * List all users' names in an enumeration of strings.
   *
   * @return  Iterator of Strings with user names
   */
  public RemoteIterator listUsers() throws RemoteException;

  /**
   * Search names of all users which match SearchFilter in an enumeration of strings.
   *
   * @return  Iterator of Strings with names of users matching SearchFilter
   */
  public RemoteIterator searchUsers(SearchFilter filter) throws RemoteException;

  /**
   * Get an instance for a user.
   *
   * @param   userName  name of the requested user
   *
   * @return  UserInfo object instance
   */
  public RemoteUserStoreInfo getUserInfo(String userName) throws RemoteException;


  /**
   * Create a new user in the user store.
   *
   * @param   userName   name of the new user (if null then generated)
   */
  public RemoteUserStoreInfo createUser(String userName) throws RemoteException;

	/**
   * Create a new user in the user store with productive password.
   *
   * @param   userName   name of the new user (if null then generated)
   * @param   password   password (will be set as productive password)
   */
  public RemoteUserStoreInfo createUser(String userName, char[] password) throws RemoteException;

  /**
   * Delete the user with the given user name.
   *
   * @param   userName   name of the user, which shall be deleted
   */
  public void deleteUser(String userName) throws RemoteException;


  /**
   * Get the filter for the user name (logonId).
   *
   * @return  user name filter, which is used in the user context
   */
  public RemoteFilterUsername getFilterUsername() throws RemoteException;


  /**
   * Set the filter for the user name (logonId).
   *
   * @param   filter      user name filter, which shall be used in the user context
   */
  public void setFilterUsername(FilterUsername filter) throws RemoteException;


  /**
   * Get the filter for the password.
   *
   * @return  password filter, which is used in the user context
   */
  public RemoteFilterPassword getFilterPassword() throws RemoteException;


  /**
   * Set the filter for the password.
   *
   * @param   filter      password filter, which shall be used in the user context
   */
  public void setFilterPassword(FilterPassword filter) throws RemoteException;


  /**
   * Ask for support of an operation on a certain user property
   *
   * @param   userProperty requested user property
   * @param   operation    operation on the requested user property
   *
   * @return  true, if the requested operation on the user property is supported
   */
  public boolean isUserPropertySupported(int userProperty, int operation) throws RemoteException;


  /**
   *  Tests if the <code>fillSubject</code> method is supported.
   *  One option is to check if the code invoking the method is trusted for the user store
   *  and to test if there is a secure connection set up to the user store.
   *
   * @return  true  if the user store can provide subjects for a user.
   */
  public boolean isSubjectRetrievalSupported() throws RemoteException;


  /**
   *  Fills the subject with the principals and credentials of the user.
   * Used by run-as-identity aware containers.
   *
   * @param  user  the user info object
   * @param  subject  an empty Subject instance to hold the result
   *
   * @return  expiration time of the temporary credentials or tickets in the Subject.
   **/
  public long fillSubject(RemoteUserInfo user, Subject subject) throws RemoteException;

  /**
   *  List the names of the root users.
   *
   * @return  iterator with the names of the users , which don't have parent groups.
   */
  public RemoteIterator listRootUsers() throws RemoteException;

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
   *@return an empty SearchFilter object.
   */
  public SearchFilter getSearchFilter();

  /**
   * Refresh the specified user's entry in the cache.
   *
   * @param   userName  name of the requested user
   */
  public void refresh(String userName);
}

