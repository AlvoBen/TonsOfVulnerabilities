/**
 * Copyright:    2002 by SAP AG
 * Company:      SAP AG, http://www.sap.com
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license
 * agreement you entered into with SAP.
 */
package com.sap.engine.services.security.userstore.context;

import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;

import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.interfaces.security.userstore.spi.UserInfoSpi;
import com.sap.engine.services.security.restriction.Restrictions;

public final class UserInfo implements com.sap.engine.interfaces.security.userstore.context.UserInfo {

  private UserInfoSpi spi = null;

  private transient String username = null;
  private transient Principal principal = null;

  public UserInfo(UserInfoSpi spi) {
    this.spi = spi;
  }

  /**
   * Get the principal object belonging to the user.
   *
   * @return  the name of the user
   */
  public Principal getPrincipal() {
    if (principal == null) {
      principal = spi.engineGetPrincipal();
    }

    return principal;
  }

  /**
   * Get the name of the user.
   *
   * @return  the name of the user
   */
  public String getName() {
    if (username == null) {
      username = spi.engineGetName();
    }

    return username;
  }

  /**
   *  Returns the certificates associated with this user.
   *
   * @return  an array of all certificates associated with the user.
   *
   * @throws SecurityException
   */
  public X509Certificate[] getCertificates() throws SecurityException {
    return spi.engineGetCertificates();
  }

  /**
   * Changes the password of the user to the given one. After this call the user will be forced
   * to change his password on the following authentication.
   *
   * @param   newPassword  the new password of the user
   */
  public void setPassword(char[] newPassword) throws SecurityException {
    if (!isCallerTheSameUser()) {
      Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_WRITE_ATTRIBUTE);
    }

    spi.engineSetPassword(newPassword);
  }

  /**
   * Changes the password of the user to the given one after verifying that the current password
   * is the given one.
   *
   * @param   oldPassword  the current password of the user
   * @param   newPassword  the new password of the user
   */
  public void setPassword(char[] oldPassword, char[] newPassword) throws SecurityException {
    try {
      AccessController.checkPermission(UserContext.ALL_PERMISSION);
    } catch (AccessControlException se) {
      if (!isCallerTheSameUser()) {
        Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_WRITE_ATTRIBUTE);
      }
    }

    spi.engineSetPassword(oldPassword, newPassword);
  }

  /**
   *  Changes the certificates associated with this user.
   *
   * @param  certificates  an array of all certificates associated with the user.
   *
   * @throws SecurityException
   */
  public void setCertificates(X509Certificate[] certificates) throws SecurityException {
    Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_WRITE_ATTRIBUTE);
    spi.engineSetCertificates(certificates);
  }

  /**
   * Get the groups, the users belongs to.
   *
   * @return  the names of the user's groups
   */
  public java.util.Iterator getParentGroups() throws SecurityException {
    return spi.engineGetParentGroups();
  }

  /**
   * Get the value of an user property.
   *
   * @param   userProperty the key of the user property
   *
   * @return  the value of the user property
   */
  public Object readUserProperty(int userProperty) throws SecurityException {
    if ((userProperty == 9) && !isCallerTheSameUser()) {//check if userProperty is password
      try {
        AccessController.checkPermission(UserContext.ALL_PERMISSION);
      } catch (AccessControlException se) {
        Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_READ_ATTRIBUTE);
      }
    }

    return spi.engineReadUserProperty(userProperty);
  }

  /**
   * Set the value of an user property.
   *
   * @param   userProperty  the key of the user property
   * @param   value         the value of the user property
   */
  public void writeUserProperty(int userProperty, Object value) throws SecurityException {
      checkPermissions(userProperty);
      if (userProperty == 9) {
        try {
          spi.engineSetPassword((char[]) value);
        } catch (SecurityException e) {
          throw e;
        } catch (Exception e) {
          throw new SecurityException("Password must be of type char[].");
        }
      } else {
        spi.engineWriteUserProperty(userProperty, value);
      }
    }
    
    public void writeUserProperty(Map properties) throws SecurityException {
  	  Iterator keys = properties.keySet().iterator();
  	  Object value = null;
  	  Object key = null;
  	  int userProperty;
  	  while (keys.hasNext()) {
  		  key = keys.next();
  		  userProperty = ((Integer)key).intValue();
  		  checkPermissions(userProperty);
  	  }
  	  spi.engineWriteUserProperty(properties);
    }
    
    private void checkPermissions(int userProperty) throws SecurityException {
  	  if (userProperty == 9) {
  	    if (!isCallerTheSameUser()) {
  	      Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_WRITE_ATTRIBUTE);
  	    }
  	  } else if ((userProperty == UserContext.PROPERTY_FAILED_LOGON_COUNT) || (userProperty == UserContext.PROPERTY_LAST_FAILED_LOGON_DATE) || (userProperty == UserContext.PROPERTY_LAST_SUCCESSFUL_LOGON_DATE) || (userProperty == UserContext.PROPERTY_SUCCESSFUL_LOGON_COUNT)) {
  	    try {
  	      AccessController.checkPermission(UserContext.ALL_PERMISSION);
  	    } catch (AccessControlException se) {
  	      Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_WRITE_ATTRIBUTE);
  	    }
  	  } else if (userProperty == UserContext.PROPERTY_LOCK_STATUS) {
  	    if (!isCallerTheSameUser()) {
  	      Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_WRITE_ATTRIBUTE);
  	    } else {
  	      throw new SecurityException("User cannot change his own lock status.");
  	    }
  	  } else {
  	    Restrictions.checkPermission(Restrictions.USER_MANAGEMENT, Restrictions.RESTRICTION_WRITE_ATTRIBUTE);
  	  }
    
    }
    
  /**
   * Set the value of an user property.
   *
   * @param   userProperty   the key of the user property
   * @param   value          the value of the user property
   *
   * @return  true, if the provided value was equal to the saved value
   */
  public boolean checkUserProperty(int userProperty, Object value) throws SecurityException {
    return spi.engineCheckUserProperty(userProperty, value);
  }

  /**
   * Checks if the provided password belongs to the user.
   *
   * @param password  the password to be checked.
   *
   * @return  true, if the given password belongs to the user. Otherwise returns false.
   */
  public boolean checkPassword(char[] password) throws SecurityException {
    return spi.engineCheckPassword(password);
  }
  
  /**
   * Extended method for checking the user's password state.
   *
   * @param password  the password to be checked.
   *
   * @return  int value, indicating the password's state. The state can be: 
   *          CHECKPWD_OK, CHECKPWD_WRONGPWD, CHECKPWD_NOPWD, CHECKPWD_PWDLOCKED or CHECKPWD_PWDEXPIRED
   */
  public int checkPasswordExtended(char[] password) throws SecurityException {
    return spi.engineCheckPasswordExtended(password);
  }
  
  /**
   * Disable the password of a user in the user store, so that it can not be 
   * used for authentication purposes any longer.
   */
  public void setPasswordDisabled() throws SecurityException {
  	spi.engineSetPasswordDisabled();
  }
 
  /**
   * Return a boolean value indicating, whether the user has a disabled password.
   */
  public boolean isPasswordDisabled() throws SecurityException {
    return spi.engineIsPasswordDisabled();
  }

  protected UserInfoSpi getSpi() {
    return spi;
  }

  public String toString() {
    return spi.toString();
  }

  private SecurityContextObject getCurrentSecurityContext() {
    ThreadContext threadContext = com.sap.engine.services.security.SecurityServerFrame.threadContext.getThreadContext();

    if (threadContext != null) {
      return (SecurityContextObject) threadContext.getContextObject("security");
    } else {
      return null;
    }
  }

  private boolean isCallerTheSameUser() {
    SecurityContextObject threadContext = getCurrentSecurityContext();
    if (threadContext != null) {
      return threadContext.getSession().getPrincipal().getName().equals(spi.engineGetPrincipal().getName());
    }
    return false;
  }
}

