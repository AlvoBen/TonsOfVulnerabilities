package com.sap.engine.services.security.userstore;

import com.sap.engine.interfaces.security.userstore.context.UserInfo;
import com.sap.engine.services.security.remote.*;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.rmi.RemoteException;

public class RemoteUserInfoImpl extends javax.rmi.PortableRemoteObject implements RemoteUserStoreInfo {

  private UserInfo info = null;

  public RemoteUserInfoImpl(UserInfo info) throws RemoteException {
    this.info = info;
  }

  /**
   * Get the principal object belonging to the user.
   *
   * @return  the name of the user
   */
  public Principal getPrincipal() {
    return info.getPrincipal();
  }

  /**
   * Get the name of the user.
   *
   * @return  the name of the user
   */
  public String getName() {
    return info.getName();
  }

  /**
   * Get the groups, the users belongs to.
   *
   * @return  the names of the user's groups
   */
  public RemoteIterator getParentGroups() throws RemoteException {
    return new RemoteIteratorImpl(info.getParentGroups());
  }

  /**
   * Get the value of an user property.
   *
   * @param   userProperty the key of the user property
   *
   * @return  the value of the user property
   */
  public Object readUserProperty(int userProperty) throws RemoteException {
    return info.readUserProperty(userProperty);
  }

  /**
   * Set the value of an user property.
   *
   * @param   userProperty  the key of the user property
   * @param   value         the value of the user property
   */
  public void writeUserProperty(int userProperty, Object value) throws RemoteException {
    if (userProperty == 9) {
      try {
        info.setPassword((char[]) value);
      } catch (ClassCastException cce) {
        throw new SecurityException("Password must be of type char[].");
      }
    }
    info.writeUserProperty(userProperty, value);
  }

  /**
   * Set the value of an user property.
   *
   * @param   userProperty   the key of the user property
   * @param   value          the value of the user property
   *
   * @return  true, if the provided value was equal to the saved value
   */
  public boolean checkUserProperty(int userProperty, Object value) throws RemoteException {
    if (userProperty == 9) {
      try {
        return info.checkPassword((char[]) value);
      } catch (ClassCastException cce) {
        throw new SecurityException("Password must be of type char[].");
      }
    }
    return info.checkUserProperty(userProperty, value);
  }

  /**
   * Changes the password of the user to the given one after verifying that the current password
   * is the given one.
   *
   * @param   oldPassword  the current password of the user
   * @param   newPassword  the new password of the user
   */
  public void setPassword(char[] oldPassword, char[] newPassword) throws RemoteException {
    info.setPassword(oldPassword, newPassword);
  }

  /**
   *  Returns the certificates associated with this user.
   *
   * @return  an array of all certificates associated with the user.
   *
   * @throws SecurityException
   */
  public X509Certificate[] getCertificates() throws RemoteException {
    return info.getCertificates();
  }

  /**
   *  Changes the certificates associated with this user.
   *
   * @param  certificates  an array of all certificates associated with the user.
   *
   * @throws SecurityException
   */
  public void setCertificates(X509Certificate[] certificates) throws RemoteException {
    info.setCertificates(certificates);
  }
  
	/**
   * Disable the password of a user in the user store, so that it can not be 
   * used for authentication purposes any longer.
   */
  public void setPasswordDisabled() throws RemoteException {
  	info.setPasswordDisabled();
  }
 
  /**
   * Return a boolean value indicating, whether the user has a disabled password.
   */
  public boolean isPasswordDisabled() throws RemoteException {
  	return info.isPasswordDisabled();
  }

}

