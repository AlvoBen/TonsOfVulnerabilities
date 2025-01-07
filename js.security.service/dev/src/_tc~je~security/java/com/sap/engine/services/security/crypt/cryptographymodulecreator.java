/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.services.security.crypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 *
 *
 * @author Stephan Zlatarev
 * @version 6.30
 */
public class CryptographyModuleCreator {

  private final static byte[] IV = new byte[8];
  private final static String ALGORITHM = "DES";
  private final static String CIPHER = "DES/CBC/PKCS5Padding";
  private final static String DIGEST = "MD5";

  private String name = null;
  private CryptographyLock lock = null;

  public CryptographyModuleCreator(String name, CryptographyLock lock) {
    this.lock = lock;
    this.name = name;
  }

  /**
   *  Returns a Cipher instance unique for the component. The Cipher is initialized
   * for the specified operation.
   *
   * @param  opmode     the mode to initialize the Cipher with.
   *
   * @return  an instance of javax.crypto.Cipher
   *
   * @see javax.crypto.Cipher
   */
  public Cipher getCipher(int opmode) {
    try {
      if (lock.isLocked()) {
        throw new SecurityException("Crypt service is locked.");
      }

      try {
        byte[] salt = MessageDigest.getInstance(DIGEST).digest(name.getBytes());
        Cipher cipher = Cipher.getInstance(CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, lock.getMasterKey(), new IvParameterSpec(IV));
        byte[] encrypted = cipher.doFinal(salt);
        SecretKeySpec spec = new SecretKeySpec(encrypted, ALGORITHM);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        cipher.init(opmode, factory.generateSecret(spec), new IvParameterSpec(IV));
        return cipher;
      } catch (Exception e) {
        return null;
      }
    } catch (NoClassDefFoundError err) {
      throw new SecurityException("Cipher cannot be instantiated.", err);
    }
  }

  /**
   *  Returns a KeyStore instance for the component. The KeyStore is initialized
   * and ready to be used.
   *
   * @param  component  the component that will use the cipher
   *
   * @return  an instance of java.security.KeyStore
   *
   * @see java.security.KeyStore
   */
  public KeyStore getKeyStore(String component) throws KeyStoreException {
    KeyStore store = null;

    try {
      store = KeyStore.getInstance("EBSDKS");
      store.load(null, null);
    } catch (Exception e) {
      throw new KeyStoreException("Exception occurred on retrieving keystore [" + component + "]", e);
    }
    return store;
  }

  /**
   *  Returns a MessageDigest instance for the component.
   *
   * @return  an instance of java.security.MessageDigest
   *
   * @see java.security.MessageDigest
   */
  public MessageDigest getMessageDigest() {
    try {
      return MessageDigest.getInstance(DIGEST);
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
  }
}