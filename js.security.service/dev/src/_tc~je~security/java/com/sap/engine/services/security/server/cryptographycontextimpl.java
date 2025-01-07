/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.services.security.server;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;

import javax.crypto.Cipher;

import com.sap.engine.interfaces.security.CryptographyContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.services.security.crypt.CryptographyLock;
import com.sap.engine.services.security.crypt.CryptographyModuleCreator;

/**
 *  Context of the J2EE Engine or a deployed instance of a component that
 * gives access to the cryptography modules configured for it.
 *  
 * @author  Stephan Zlatarev
 * @version 6.30
 * @deprecated This functionality must not be used anymore.
 */
public class CryptographyContextImpl implements CryptographyContext {

  private static CryptographyLock lock;
  private CryptographyModuleCreator creator;

  protected CryptographyContextImpl(String configuration, SecurityContext root) {

    if (lock == null) {
      lock = new CryptographyLock(root);
    }
    this.creator = new CryptographyModuleCreator(configuration, lock);

    if ((root instanceof SecurityContextImpl) && isLocked()) {
      String filename = ((SecurityContextImpl) root).getEnvironment().getServiceState().getProperty("crypt.file");

      if (filename != null) {
        unlockUsingFile(filename);
      }
    }
  }

  public boolean isLocked() {
    return lock.isLocked();
  }

  /**
   *  Returns an initialized instance of a cipher.
   *
   * @param  opmode  the operation mode of the cipher, e.g. ENCRYPT_MODE.
   *
   * @return  an instance of a cipher
   *
   * @see  javax.crypto.Cipher
   */
  public Cipher getCipher(int opmode) {
    return creator.getCipher(opmode);
  }

  /**
   *  Returns an initialized instance of a keystore
   *
   * @param  instance  optional parameter ( mat be null ).
   *
   * @return  an instance of a key store.
   */
  public KeyStore getKeyStore(String instance) {
    try {
      return creator.getKeyStore(instance);
    } catch (KeyStoreException e) {
      throw new SecurityException(e);
    }
  }

  /**
   *  Returns an initialized instance of a digest algorithm.
   *
   * @return  an instance of a digest algorithm
   */
  public MessageDigest getMessageDigest() {
    return creator.getMessageDigest();
  }

  /**
   *  Changes the lock password for the encryption of data.
   * Note that this password is cluster-wide.
   *
   * @param  password  lock password.
   * @deprecated this method is not supported anymore
   */
  public void lock(char[] password) {
    lock.lock(password);
  }

  /**
   *  Provides the lock password for alreadyencrypted data.
   *
   * @param  password  lock password.
   */
  public void unlock(char[] password) {
    throw new IllegalStateException("This method is not supported anymore!");
  }

  private void unlockUsingFile(String filename) {
    throw new IllegalStateException("This method is not supported anymore!");
  }

}

