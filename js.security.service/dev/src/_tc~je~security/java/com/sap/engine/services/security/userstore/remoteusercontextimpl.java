package com.sap.engine.services.security.userstore;

import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.interfaces.security.userstore.context.SearchFilter;
import com.sap.engine.interfaces.security.userstore.spi.FilterPassword;
import com.sap.engine.interfaces.security.userstore.spi.FilterUsername;
import com.sap.engine.services.security.remote.*;
import javax.security.auth.Subject;
import java.rmi.RemoteException;

public class RemoteUserContextImpl extends javax.rmi.PortableRemoteObject implements RemoteUserContext {

  private UserContext context = null;

  public RemoteUserContextImpl(UserContext context) throws RemoteException {
    this.context = context;
  }

  /**
   * List all users' names in an enumeration of strings.
   *
   * @return  Iterator of Strings with user names
   */
  public RemoteIterator listUsers() throws RemoteException {
    return new RemoteIteratorImpl(context.listUsers());
  }

  /**
   * Search names of all users which match SearchFilter in an enumeration of strings.
   *
   * @return  Iterator of Strings with names of users matching SearchFilter
   */
  public RemoteIterator searchUsers(SearchFilter filter) throws RemoteException {
    return new RemoteIteratorImpl(context.searchUsers(filter));
  }

  /**
   * Get an instance for a user.
   *
   * @param   userName  name of the requested user
   *
   * @return  UserInfo object instance
   */
  public RemoteUserStoreInfo getUserInfo(String userName) throws RemoteException {
    return new RemoteUserInfoImpl(context.getUserInfo(userName));
  }

  /**
   * Create a new user in the user store.
   *
   * @param   userName   name of the new user (if null then generated)
   */
  public RemoteUserStoreInfo createUser(String userName) throws RemoteException {
    return new RemoteUserInfoImpl(context.createUser(userName));
  }
  
	/**
   * Create a new user in the user store with productive password.
   *
   * @param   userName   name of the new user (if null then generated)
   * @param   password   password (will be set as productive password)
   */
  public RemoteUserStoreInfo createUser(String userName, char[] password) throws RemoteException {
		return new RemoteUserInfoImpl(context.createUser(userName, password));
  }

  /**
   * Delete the user with the given user name.
   *
   * @param   userName   name of the user, which shall be deleted
   */
  public void deleteUser(String userName) throws RemoteException {
    context.deleteUser(userName);
  }

  /**
   * Get the filter for the user name (logonId).
   *
   * @return  user name filter, which is used in the user context
   */
  public RemoteFilterUsername getFilterUsername() throws RemoteException {
    return new RemoteFilterUsernameImpl(context);
  }

  /**
   * Set the filter for the user name (logonId).
   *
   * @param   filter      user name filter, which shall be used in the user context
   */
  public void setFilterUsername(FilterUsername filter) throws RemoteException {
    context.setFilterUsername(filter);
  }

  /**
   * Get the filter for the password.
   *
   * @return  password filter, which is used in the user context
   */
  public RemoteFilterPassword getFilterPassword() throws RemoteException {
    return new RemoteFilterPasswordImpl(context);
  }

  /**
   * Set the filter for the password.
   *
   * @param   filter      password filter, which shall be used in the user context
   */
  public void setFilterPassword(FilterPassword filter) throws RemoteException {
    context.setFilterPassword(filter);
  }

  /**
   * Ask for support of an operation on a certain user property
   *
   * @param   userProperty requested user property
   * @param   operation    operation on the requested user property
   *
   * @return  true, if the requested operation on the user property is supported
   */
  public boolean isUserPropertySupported(int userProperty, int operation) throws RemoteException {
    return context.isUserPropertySupported(userProperty, operation);
  }

  /**
   *  Tests if the <code>fillSubject</code> method is supported.
   *  One option is to check if the code invoking the method is trusted for the user store
   *  and to test if there is a secure connection set up to the user store.
   *
   * @return  true  if the user store can provide subjects for a user.
   */
  public boolean isSubjectRetrievalSupported() throws RemoteException {
    return context.isSubjectRetrievalSupported();
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
  public long fillSubject(RemoteUserInfo user, Subject subject) throws RemoteException {
    return context.fillSubject(context.getUserInfo(user.getName()), subject);
  }

  /**
   *  List the names of the root users.
   *
   * @return  iterator with the names of the users , which don't have parent groups.
   */
  public RemoteIterator listRootUsers() throws RemoteException {
    return new RemoteIteratorImpl(context.listRootUsers());
  }

  /**
   *
   * @return the number of the allowed invalid login attempts.
   */
  public int getLockAfterInvalidAttempts() {
    return context.getLockAfterInvalidAttempts();
  }
  /**
   *
   *@return an empty SearchFilter object.
   */
  public SearchFilter getSearchFilter() {
    return context.getSearchFilter();
  }
  /**
   *
   * @return a boolean value stating, if this user store is in emergency mode.
   */
  public boolean isInEmergencyMode() {
    return context.isInEmergencyMode();
  }

  /**
   *
   * @param   userName  name of the requested user
   *
   * @return a boolean value stating, if the specified user is the emergency user.
   */
  public boolean isEmergencyUser(String userName) {
    return context.isEmergencyUser(userName);
  }

  /**
   * Refresh the specified user's entry in the cache.
   *
   * @param   userName  name of the requested user
   */
  public void refresh(String userName) {
    context.refresh(userName);
  }
}

