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

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * User info is the root class for user account representation classes.
 *
 * @author  Boris Koeberle
 * @version 6.30
 */
public interface UserInfoSpi {

  /**
   * Get the principal object belonging to the user.
   *
   * @return  the name of the user
   */
  public Principal engineGetPrincipal();


  /**
   * Get the name of the user.
   *
   * @return  the name of the user
   */
  public String engineGetName();


  /**
   * Get the groups, the users belongs to.
   *
   * @return  the names of the user's groups
   */
  public java.util.Iterator engineGetParentGroups() throws SecurityException;


  /**
   *  Returns the certificates associated with this user.
   *
   * @return  an array of all certificates associated with the user.
   *
   * @throws SecurityException
   */
  public X509Certificate[] engineGetCertificates() throws SecurityException;


  /**
   * Changes the password of the user to the given one. After this call the user will be forced
   * to change his password on the following authentication.
   *
   * @param   newPassword  the new password of the user
   */
  public void engineSetPassword(char[] newPassword) throws SecurityException;


  /**
   * Changes the password of the user to the given one after verifying that the current password
   * is the given one.
   *
   * @param   oldPassword  the current password of the user
   * @param   newPassword  the new password of the user
   */
  public void engineSetPassword(char[] oldPassword, char[] newPassword) throws SecurityException;


  /**
   *  Changes the certificates associated with this user.
   *
   * @param  certificates  an array of all certificates associated with the user.
   *
   * @throws SecurityException
   */
  public void engineSetCertificates(X509Certificate[] certificates) throws SecurityException;


  /**
   * Get the value of an user property.
   *
   * @param   userProperty the key of the user property
   *
   * @return  the value of the user property
   */
  public Object engineReadUserProperty(int userProperty) throws SecurityException;


  /**
   * Set the value of an user property.
   *
   * @param   userProperty  the key of the user property
   * @param   value         the value of the user property
   */
  public void engineWriteUserProperty(int userProperty, Object value) throws SecurityException;


  /**
   * Set the values of a set of user properties.
   *
   * @param   properties  	the map containing the user properties and values 
   * 
   * @see UserInfoSpi#engineCheckUserProperty(int, Object)
   */
  public void engineWriteUserProperty(Map properties) throws SecurityException;

  /**
   * Set the value of an user property.
   *
   * @param   userProperty   the key of the user property
   * @param   value          the value of the user property
   *
   * @return  true, if the provided value was equal to the saved value
   */
  public boolean engineCheckUserProperty(int userProperty, Object value) throws SecurityException;

  /**
   * Checks if the provided password belongs to the user.
   *
   * @param password  the password to be checked.
   *
   * @return  true, if the given password belongs to the user. Othrwise returns false.
   */
  public boolean engineCheckPassword(char[] password) throws SecurityException;
  
  /**
   * Extended method for checking the user's password, 
   * which includes additional checks for the password state.
   *
   * @param password  the password to be checked.
   *
   * @return  int value, indicating the password's state. The state can be: 
   * UserInfo.CHECKPWD_OK, UserInfo.CHECKPWD_WRONGPWD, UserInfo.CHECKPWD_NOPWD, UserInfo.CHECKPWD_PWDLOCKED or UserInfo.CHECKPWD_PWDEXPIRED
   */
  public int engineCheckPasswordExtended(char[] password) throws SecurityException;

	/**
   * Disable the password of a user in the user store, so that it can not be 
   * used for authentication purposes any longer.
   */
  public void engineSetPasswordDisabled() throws SecurityException;
 
  /**
   * Return a boolean value indicating, whether the user has a disabled password.
   */
  public boolean engineIsPasswordDisabled() throws SecurityException;
}

