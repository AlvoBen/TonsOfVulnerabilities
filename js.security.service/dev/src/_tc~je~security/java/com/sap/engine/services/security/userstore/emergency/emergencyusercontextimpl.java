/**
 * Copyright:    2002 by SAP AG
 * Company:      SAP AG, http://www.sap.com
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license
 * agreement you entered into with SAP.
 */
package com.sap.engine.services.security.userstore.emergency;

import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.resource.spi.security.PasswordCredential;
import javax.security.auth.Subject;

import com.sap.engine.interfaces.security.userstore.context.SearchFilter;
import com.sap.engine.interfaces.security.userstore.context.SearchResult;
import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.interfaces.security.userstore.listener.UserListener;
import com.sap.engine.interfaces.security.userstore.spi.FilterPassword;
import com.sap.engine.interfaces.security.userstore.spi.FilterUsername;
import com.sap.engine.interfaces.security.userstore.spi.UserContextSpi;
import com.sap.engine.interfaces.security.userstore.spi.UserInfoSpi;
import com.sap.engine.lib.security.Principal;
import com.sap.engine.services.security.userstore.emergency.search.SearchFilterImpl;
import com.sap.engine.services.security.userstore.emergency.search.SearchResultImpl;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Implementation of the UserContextSpi used by the Emergency UserStore.
 *
 * @author  Ekaterina Zheleva
 * @version 6.30
 */
public class EmergencyUserContextImpl implements UserContextSpi {

  final static int PROPERTY_PASSWORD = 9;
  private final static Location LOCATION = Location.getLocation(EmergencyUserContextImpl.class);

  private UserInfoSpi userInfo = null;
  private Vector users = null;

  /**
   * Constructs the user context.
   */
  public EmergencyUserContextImpl() {
    userInfo = new EmergencyUserInfoImpl();
    users = new Vector(1);
    users.addElement(userInfo.engineGetName());
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#enginePropertiesChanged(Properties)
   */
  public void enginePropertiesChanged(Properties newProps) {
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineListUsers()
   */
  public Iterator engineListUsers() throws SecurityException {
    return users.iterator();
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineSearchUsers(com.sap.engine.interfaces.security.userstore.context.SearchFilter filter)
   */
  public SearchResult engineSearchUsers(SearchFilter filter) throws SecurityException {
    return new SearchResultImpl(this);
  }

  /**
   *  List the names of the root users.
   *
   * @return  iterator with the names of the users , which don't have parent groups.
   */
  public java.util.Iterator engineListRootUsers() {
    return engineListUsers();
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineGetUserInfo(String)
   */
  public UserInfoSpi engineGetUserInfo(String userName) throws SecurityException {
    return userInfo;
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineGetUserInfo(X509Certificate)
   */
  public UserInfoSpi engineGetUserInfo(X509Certificate cert) throws SecurityException {
    return userInfo;
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineGetUserInfoByLogonAlias(String)
   */
  public UserInfoSpi engineGetUserInfoByLogonAlias(String alias) throws SecurityException {
    return userInfo;
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineCreateUser(String)
   */
  public UserInfoSpi engineCreateUser(String userName) throws SecurityException {
    throw new SecurityException("Emergency User Store does not allow the operation");
  }
  
	/**
	 * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineCreateUser(String, char[])
	 */
  public UserInfoSpi engineCreateUser(String userName, char[] password) throws SecurityException {
    throw new SecurityException("Emergency User Store does not allow the operation");
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineDeleteUser(String)
   */
  public void engineDeleteUser(String userName) throws SecurityException {
    throw new SecurityException("Emergency User Store does not allow the operation");
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineGetFilterUsername()
   */
  public FilterUsername engineGetFilterUsername() throws SecurityException {
    throw new SecurityException("Emergency User Store does not allow the operation");
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineSetFilterUsername(FilterUsername)
   */
  public void engineSetFilterUsername(FilterUsername filterUsername) throws SecurityException {
    throw new SecurityException("Emergency User Store does not allow the operation");
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineGetFilterPassword()
   */
  public FilterPassword engineGetFilterPassword() throws SecurityException {
    throw new SecurityException("Emergency User Store does not allow the operation");
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineSetFilterPassword(FilterPassword)
   */
  public void engineSetFilterPassword(FilterPassword filterPassword) throws SecurityException {
    throw new SecurityException("Emergency User Store does not allow the operation");
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineIsUserPropertySupported(int, int)
   */
  public boolean engineIsUserPropertySupported(int userProperty, int operation) throws SecurityException {
    switch (userProperty) {
      case UserContext.PROPERTY_CREATE_DATE: {
        if (operation == UserContext.OPERATION_READ || operation == UserContext.OPERATION_CHECK ) {
          return true;
        }
        return false;
      }
      case UserContext.PROPERTY_MODIFY_DATE: {
        if (operation == UserContext.OPERATION_READ || operation == UserContext.OPERATION_CHECK) {
          return true;
        }
        return false;
      }
      case UserContext.PROPERTY_FAILED_LOGON_COUNT: {
        if (operation == UserContext.OPERATION_READ || operation == UserContext.OPERATION_CHECK || operation == UserContext.OPERATION_WRITE) {
          return true;
        }
        return false;
      }
      case UserContext.PROPERTY_LAST_FAILED_LOGON_DATE: {
        if (operation == UserContext.OPERATION_READ || operation == UserContext.OPERATION_CHECK || operation == UserContext.OPERATION_WRITE) {
          return true;
        }
        return false;
      }
      case UserContext.PROPERTY_SUCCESSFUL_LOGON_COUNT: {
        if (operation == UserContext.OPERATION_READ || operation == UserContext.OPERATION_CHECK || operation == UserContext.OPERATION_WRITE) {
          return true;
        }
        return false;
      }
      case UserContext.PROPERTY_LAST_SUCCESSFUL_LOGON_DATE: {
        if (operation == UserContext.OPERATION_READ || operation == UserContext.OPERATION_CHECK || operation == UserContext.OPERATION_WRITE) {
          return true;
        }
        return false;
      }
      case UserContext.PROPERTY_VALID_FROM_DATE: {
        return false;
      }
      case UserContext.PROPERTY_VALID_TO_DATE: {
        return false;
      }
      case PROPERTY_PASSWORD: {
        if (operation == UserContext.OPERATION_CHECK || operation == UserContext.OPERATION_READ) {
          return true;
        }
        return false;
      }
      case UserContext.PROPERTY_LAST_CHANGED_PASSWORD_DATE: {
        if (operation == UserContext.OPERATION_READ || operation == UserContext.OPERATION_CHECK) {
          return true;
        }
        return false;
      }
      case UserContext.PROPERTY_FORCE_TO_CHANGE_PASSWORD: {
        return false;
      }
      case UserContext.PROPERTY_LOCK_STATUS: {
        if (operation == UserContext.OPERATION_READ) {
          return true;
        }
        return false;
      }
    }
    return false;
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineRegisterListener(UserListener, int)
   */
  public void engineRegisterListener(UserListener userListener, int modifier) throws SecurityException {
    //throw new BaseSecurityException(BaseSecurityException.NOT_SUPPORTED);
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineUnregisterListener(UserListener)
   */
  public void engineUnregisterListener(UserListener userlistener) throws SecurityException {
    //throw new BaseSecurityException(BaseSecurityException.NOT_SUPPORTED1);
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineIsSubjectRetrievalSupported()
   */
  public boolean engineIsSubjectRetrievalSupported() {
    return true;
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineFillSubject(UserInfoSpi, Subject)
   */
  public long engineFillSubject(UserInfoSpi user, Subject subject) throws SecurityException {
    char[] password = (char[]) userInfo.engineReadUserProperty(PROPERTY_PASSWORD);
    PasswordCredential credential = new PasswordCredential(userInfo.engineGetName(), password);
    subject.getPrivateCredentials().add(credential);

    subject.getPrincipals().add(userInfo.engineGetPrincipal());
    return Long.MAX_VALUE;
  }

  /**
   *  Removes from subject the associated principals and credentials.
   *
   * @param  subject  the subject which prinsipals and credentials will be removed.
   **/
  public void engineEmptySubject(Subject subject) throws SecurityException {
    try {
      Iterator principals = subject.getPrincipals(Principal.class).iterator();

      while (principals.hasNext()) {
        principals.next();
        principals.remove();
      }
    } catch (Exception e) {
      // ignore
      LOCATION.traceThrowableT(Severity.DEBUG, "engineEmptySubject [Subject: {0}", new Object[]{subject}, e);
    }

    try {
      Iterator privateCredentials = subject.getPrivateCredentials(PasswordCredential.class).iterator();

      while (privateCredentials.hasNext())  {
        privateCredentials.next();
        privateCredentials.remove();
      }
    } catch (Exception e) {
      // ignore
      LOCATION.traceThrowableT(Severity.DEBUG, "engineEmptySubject [Subject: {0}", new Object[]{subject}, e);
    }
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineGetLockAfterInvalidAttempts()
   */
  public int engineGetLockAfterInvalidAttempts() {
    return -1;
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineIsInEmergencyMode()
   */
  public boolean engineIsInEmergencyMode() {
    return true;
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineIsEmergencyUser(String userName)
   */
  public boolean engineIsEmergencyUser(String userName) {
    return userName.equals(userInfo.engineGetName());
  }

  /**
   *@see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineGetEmergencyUserName()
   */
  public String engineGetEmergencyUserName() {
    return userInfo.engineGetName();
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineGetSearchFilter()
   */
  public SearchFilter engineGetSearchFilter() {
    return new SearchFilterImpl();
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineRefresh(String userName)
   */
  public void engineRefresh(String userName) {
    //no cache of user entries supported
  }
  
	/**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserContextSpi#engineGetAnonymousUserName()
   */
  public String engineGetAnonymousUserName() {
    return userInfo.engineGetName();
  }
}
