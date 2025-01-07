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

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.interfaces.security.userstore.context.UserInfo;
import com.sap.engine.interfaces.security.userstore.spi.UserInfoSpi;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * The implementation of UserInfoSpi for the Emergency UserStore user.
 *
 * @author  Ekaterina Zheleva
 * @version 6.30
 */
public class EmergencyUserInfoImpl implements UserInfoSpi {

  /**
   * The name of the emergency user. The value is "SAP*".
   */
  public static final String USER_NAME = "SAP*";
  
  private final static Location LOCATION = Location.getLocation(EmergencyUserInfoImpl.class);

  private char[] password = null;
  private java.lang.Integer lockStatus = null;
  private Principal principal = null;
  private Date date = null;
  private long lastFailedLogonDate = 0;
  private long lastSuccessfulLogonDate = 0;
  private int failedLogonCount = 0;
  private int successfulLogonCount = 0;

  private final Vector PARENTS = new Vector(0);
  private final X509Certificate[] NO_CERTIFICATES = new X509Certificate[0];

  EmergencyUserInfoImpl() {
    principal = new com.sap.engine.lib.security.Principal(USER_NAME);
    password = new char[] {'P', 'A', 'S', 'S'};
    lockStatus = new Integer(UserContext.LOCKED_NO);
    date = new Date(Calendar.getInstance().getTime().getTime());
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserInfoSpi#engineGetPrincipal()
   */
  public Principal engineGetPrincipal() {
    return principal;
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserInfoSpi#engineGetName()
   */
  public String engineGetName() {
    return principal.getName();
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserInfoSpi#engineGetParentGroups()
   */
  public Iterator engineGetParentGroups() throws SecurityException {
    return PARENTS.iterator();
  }

  /**
   *  Returns the certificates associated with this user.
   *
   * @return  an array of all certificates associated with the user.
   *
   * @throws SecurityException
   */
  public X509Certificate[] engineGetCertificates() throws SecurityException {
    return NO_CERTIFICATES;
  }

  /**
   * Changes the password of the user to the given one. After this call the user will be forced
   * to change his password on the following authentication.
   *
   * @param   newPassword  the new password of the user
   */
  public void engineSetPassword(char[] newPassword) throws SecurityException {
  }

  /**
   * Changes the password of the user to the given one after verifying that the current password
   * is the given one.
   *
   * @param   oldPassword  the current password of the user
   * @param   newPassword  the new password of the user
   */
  public void engineSetPassword(char[] oldPassword, char[] newPassword) throws SecurityException {
  }

  /**
   *  Changes the certificates associated with this user.
   *
   * @param  certificates  an array of all certificates associated with the user.
   *
   * @throws SecurityException
   */
  public void engineSetCertificates(X509Certificate[] certificates) throws SecurityException {
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserInfoSpi#engineReadUserProperty(int)
   * For a non trivial work of this method the <code>setUserContext()</code> method
   * has to be invoked first, to set the appropriate UserContect implementation.
   * Only in case of connector userstore!
   */
  public Object engineReadUserProperty(int userProperty) throws SecurityException {
    switch (userProperty) {
      case EmergencyUserContextImpl.PROPERTY_PASSWORD: {
        return password;
      }
      case UserContext.PROPERTY_FORCE_TO_CHANGE_PASSWORD: {
        return new Long(-1);
      }
      case UserContext.PROPERTY_LAST_CHANGED_PASSWORD_DATE:  {
        return date;
      }
      case UserContext.PROPERTY_CREATE_DATE:  {
        return date;
      }
      case UserContext.PROPERTY_MODIFY_DATE:  {
        return date;
      }
      case UserContext.PROPERTY_LAST_FAILED_LOGON_DATE:  {
        if (lastFailedLogonDate == 0) {
           return null;
        }
        Date date = new Date(lastFailedLogonDate);
        return date;
      }
      case UserContext.PROPERTY_LAST_SUCCESSFUL_LOGON_DATE:  {
        if (lastSuccessfulLogonDate == 0) {
           return null;
        }
        Date date = new Date(lastSuccessfulLogonDate);
        return date;
      }
      case UserContext.PROPERTY_SUCCESSFUL_LOGON_COUNT:  {
        return new Integer(successfulLogonCount);
      }
      case UserContext.PROPERTY_FAILED_LOGON_COUNT:  {
        return new Integer(failedLogonCount);
      }
      case UserContext.PROPERTY_LOCK_STATUS: {
        return lockStatus;
      }
    }
    return null;
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserInfoSpi#engineWriteUserProperty(int, Object)
   * For a non trivial work of this method the <code>setUserContext()</code> method
   * has to be invoked first, to set the appropriate UserContect implementation.
   * Only in case of connector userstore!
   */
  public void engineWriteUserProperty(int userProperty, Object value) throws SecurityException {
    switch (userProperty) {
      case UserContext.PROPERTY_FAILED_LOGON_COUNT: {
        if (value instanceof Integer) {
          failedLogonCount = ((Integer) value).intValue();
        }
        break;
      }
      case UserContext.PROPERTY_SUCCESSFUL_LOGON_COUNT: {
        if (value instanceof Integer) {
          successfulLogonCount = ((Integer) value).intValue();
        }
        break;
      }
      case UserContext.PROPERTY_LAST_FAILED_LOGON_DATE: {
        if (value instanceof Date) {
          lastFailedLogonDate = ((Date) value).getTime();
        }
        break;
      }
      case UserContext.PROPERTY_LAST_SUCCESSFUL_LOGON_DATE: {
        if (value instanceof Date) {
          lastSuccessfulLogonDate = ((Date) value).getTime();
        }
      }
    }
  }
  
  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserInfoSpi#engineWriteUserProperty(Map)
   */
  public void engineWriteUserProperty(Map properties) throws SecurityException {
	  Iterator keys = properties.keySet().iterator();
	  Object value = null;
	  Object key = null;
	  int userProperty;
	  while (keys.hasNext()) {
		  key = keys.next();
		  userProperty = ((Integer)key).intValue(); 
		  value = properties.get(key);
		  engineWriteUserProperty(userProperty, value);
	  }
  }
  

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserInfoSpi#engineCheckUserProperty(int, Object)
   */
  public boolean engineCheckUserProperty(int userProperty, Object value) throws SecurityException {
    if (userProperty == EmergencyUserContextImpl.PROPERTY_PASSWORD) {
      try {
        return engineCheckPassword((char[]) value);
      } catch (ClassCastException e) {
        LOCATION.traceThrowableT(Severity.INFO, "engineCheckUserProperty", e);
        return false;
      }
    }

    throw new SecurityException("Cannot check user property! Only password property is supported.");
  }

  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserInfoSpi#engineCheckPassword(char[])
   */
  public boolean engineCheckPassword(char[] targetPassword) throws SecurityException {
    return (engineCheckPasswordExtended(targetPassword) == UserInfo.CHECKPWD_OK);
  }
  
  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserInfoSpi#engineCheckPasswordExtended(char[])
   */
  public int engineCheckPasswordExtended(char[] targetPassword) throws SecurityException {
    if ((targetPassword == null) || (targetPassword.length != 4)) {
      // password too short or too long
      return UserInfo.CHECKPWD_WRONGPWD;
    }

    if ((targetPassword[0] != 'P') && (targetPassword[0] != 'p')) {
      // password is not "pass". first letter is not "p"
      return UserInfo.CHECKPWD_WRONGPWD;
    }

    if ((targetPassword[1] != 'A') && (targetPassword[1] != 'a')) {
      // password is not "pass". second letter is not "a"
      return UserInfo.CHECKPWD_WRONGPWD;
    }

    if ((targetPassword[2] != 'S') && (targetPassword[2] != 's')) {
      // password is not "pass". third letter is not "s"
      return UserInfo.CHECKPWD_WRONGPWD;
    }

    if ((targetPassword[3] != 'S') && (targetPassword[3] != 's')) {
      // password is not "pass". fourth letter is not "s"
      return UserInfo.CHECKPWD_WRONGPWD;
    }

    // all letters are the same. password is correct
    return UserInfo.CHECKPWD_OK;
  }
  
  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserInfoSpi#engineSetPasswordDisabled()
   */
  public void engineSetPasswordDisabled() throws SecurityException {
  }
  
  /**
   * @see com.sap.engine.interfaces.security.userstore.spi.UserInfoSpi#engineIsPasswordDisabled()
   */
  public boolean engineIsPasswordDisabled() throws SecurityException {
    return false;
  }

  /**
   * Returns a printable descrition of the user.
   *
   * @return a printable string
   */
  public String toString() {
    String result = "User ID:      " + engineGetName();
    result = result + "\nParent Groups: ";
    Iterator parents = engineGetParentGroups();

    while (parents.hasNext()) {
      result = result + (String) parents.next();
      if (parents.hasNext()) result = result + "\n               ";
    }

    result += "\n";
    return result;
  }

}
