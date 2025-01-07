/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security;

import java.security.KeyStore;
import java.security.MessageDigest;
import javax.crypto.Cipher;

/**
 *  Context of the J2EE Engine or a deployed instance of a component that
 * gives access to the cryptography modules configured for it.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 *
 * @deprecated Cryptography context is deprecated since NW04 AS Java. Use Secure Store service or
 *      the encryption of Configuration Manager API instead.
 * @see com.sap.engine.interfaces.security.SecurityContext
 */
public interface CryptographyContext {

  /**
   *  Returns an initialized instance of a cipher.
   *
   * @param  opmode  the operation mode of the cipher, e.g. ENCRYPT_MODE.
   *
   * @return  an instance of a cipher
   *
   * @see  javax.crypto.Cipher
   */
  public Cipher getCipher(int opmode);


  /**
   *  Returns an initialized instance of a keystore
   *
   * @param  instance  optional parameter ( may be null ).
   *
   * @return  an instance of a key store.
   */
  public KeyStore getKeyStore(String instance);


  /**
   *  Returns an initialized instance of a digest algorithm.
   *
   * @return  an instance of a digest algorithm
   */
  public MessageDigest getMessageDigest();


  /**
   *  Changes the lock password for the encryption of data.
   * Note that this password is cluster-wide.
   *
   * @param  password  lock password.
   */
  public void lock(char[] password);


  /**
   *  Provides the lock password for alreadyencrypted data.
   *
   * @param  password  lock password.
   */
  public void unlock(char[] password);

}

