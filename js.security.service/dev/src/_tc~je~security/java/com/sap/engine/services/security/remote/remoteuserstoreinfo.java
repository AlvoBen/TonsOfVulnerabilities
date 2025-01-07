package com.sap.engine.services.security.remote;

import java.rmi.*;
import java.security.Principal;
import java.security.cert.X509Certificate;

public interface RemoteUserStoreInfo
  extends Remote {

  /**
   * Get the principal object belonging to the user.
   *
   * @return  the name of the user
   */
  public Principal getPrincipal();


  /**
   * Get the name of the user.
   *
   * @return  the name of the user
   */
  public String getName();


  /**
   * Get the groups, the users belongs to.
   *
   * @return  the names of the user's groups
   */
  public RemoteIterator getParentGroups() throws RemoteException;


  /**
   * Get the value of an user property.
   *
   * @param   userProperty the key of the user property
   *
   * @return  the value of the user property
   */
  public Object readUserProperty(int userProperty) throws RemoteException;


  /**
   * Set the value of an user property.
   *
   * @param   userProperty  the key of the user property
   * @param   value         the value of the user property
   */
  public void writeUserProperty(int userProperty, Object value) throws RemoteException;


  /**
   * Set the value of an user property.
   *
   * @param   userProperty   the key of the user property
   * @param   value          the value of the user property
   *
   * @return  true, if the provided value was equal to the saved value
   */
  public boolean checkUserProperty(int userProperty, Object value) throws RemoteException;


  /**
   * Changes the password of the user to the given one after verifying that the current password
   * is the given one.
   *
   * @param   oldPassword  the current password of the user
   * @param   newPassword  the new password of the user
   */
  public void setPassword(char[] oldPassword, char[] newPassword) throws RemoteException;


  /**
   *  Returns the certificates associated with this user.
   *
   * @return  an array of all certificates associated with the user.
   *
   * @throws SecurityException
   */
  public X509Certificate[] getCertificates() throws RemoteException;

  
  /**
   *  Changes the certificates associated with this user.
   *
   * @param  certificates  an array of all certificates associated with the user.
   *
   * @throws SecurityException
   */
  public void setCertificates(X509Certificate[] certificates) throws RemoteException;
  
	/**
   * Disable the password of a user in the user store, so that it can not be 
   * used for authentication purposes any longer.
   */
  public void setPasswordDisabled() throws RemoteException;
 
  /**
   * Return a boolean value indicating, whether the user has a disabled password.
   */
  public boolean isPasswordDisabled() throws RemoteException;
}

