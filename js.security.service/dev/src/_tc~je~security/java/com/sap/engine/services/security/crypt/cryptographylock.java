/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.services.security.crypt;

import com.sap.engine.services.security.exceptions.StorageException;
import com.sap.engine.services.security.server.ModificationContextImpl;
import com.sap.engine.services.security.server.SecurityConfigurationPath;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.frame.core.configuration.Configuration;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.Cipher;
import java.security.MessageDigest;
import java.security.Key;
import java.security.SecureRandom;

/**
 *
 *
 * @author Stephan Zlatarev
 * @version 6.30
 */
public class CryptographyLock {

  private final static byte[] IV = new byte[8];
  private final static String ALGORITHM = "DES";
  private final static String CIPHER = "DES/CBC/PKCS5Padding";
  private final static String CONFIGURATION_ENTRY_VERIFICATION = "verification_string";
  private final static String CONFIGURATION_ENTRY_MASTER = "master_key";
  private final static String DIGEST = "MD5";
  private final static String VERIFY_STRING = "verification_string";

  private boolean isLocked = true;
  private Key masterKey = null;
  private SecurityContext root = null;

  public CryptographyLock(SecurityContext root) {
    this.root = root;
    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
    mc.beginModifications();

    try {
      Configuration configuration = null;

      try {
        configuration = mc.getConfiguration(SecurityConfigurationPath.SECURESTORE_PATH, false, false);
        isLocked = (configuration != null);
      } catch (Exception e) {
        isLocked = false;
      }

      if (!isLocked) {
        mc.getConfiguration(SecurityConfigurationPath.SECURESTORE_PATH, true, true);
      }

      mc.commitModifications();
    } catch (StorageException se) {
      mc.rollbackModifications();
    }
  }

  /**
   *  Tells if the cryptography context is locked. If it is locked no cipher
   * can be retrieved for any component. The context can be unlocked by <code>unlock</code> method.
   *
   * @return  true if the context is locked
   */
  public boolean isLocked() {
    return isLocked;
  }

  public synchronized void lock(char[] password) {
    try {
      if (isLocked) {
        throw new SecurityException("Cannot change lock while crypt is locked.");
      }

      /////
      //IM  check for privileges of the user
      changeLockData(password);
    } catch (Exception e) {
      throw new SecurityException("Exception occured on lock attempt!", e);
    } catch (NoClassDefFoundError err) {
      throw new SecurityException("Exception occured on lock attempt!", err);
    }
  }

  /**
   *  Unlocks the context.
   *
   *  @param  password  the password the context was locked with
   *
   *  @exception  SecurityException thrown if the password is incorrect or an error occurs
   *              when trying to unlock the context.
   */
  public synchronized void unlock(char[] password) throws SecurityException {
    try {
      if (!isLocked) {
        return;
      }

      byte[] verification = null;
      byte[] rawkey = null;
      Configuration container = null;
      ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();
      mc.beginModifications();

      try {
        container = mc.getConfiguration(SecurityConfigurationPath.SECURESTORE_PATH, false, false);
        verification = loadVerificationArray(container);
        rawkey = loadMasterKey(container);
      } finally {
        mc.commitModifications();
      }

      if (rawkey == null) {
        generateMasterKey();
        changeLockData(password);
      } else {
        try {
          getLockData(password, rawkey, verification);
        } catch (Exception e) {
          throw new SecurityException("Password not correct!", e);
        }
      }

      isLocked = false;
    } catch (SecurityException se) {
      throw new SecurityException("Exception occured on unlock attempt!", se);
    } catch (Exception e) {
      throw new SecurityException("Exception occured on unlock attempt!", e);
    } catch (NoClassDefFoundError err) {
      throw new SecurityException("Exception occured on unlock attempt!", err);
    }
  }

  Key getMasterKey() {
    return masterKey;
  }

  private void generateMasterKey() throws Exception {
    SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
    byte[] salt =  random.generateSeed(8);
    SecretKeySpec spec = new SecretKeySpec(salt, ALGORITHM);
    SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
    masterKey = factory.generateSecret(spec);
  }

  private void getLockData(char[] password, byte[] rawkey, byte[] verification) throws Exception {
    byte[] salt = MessageDigest.getInstance(DIGEST).digest(new String(password).getBytes());
    SecretKeySpec spec = new SecretKeySpec(salt, ALGORITHM);
    SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);

    Cipher cipher = Cipher.getInstance(CIPHER);
    cipher.init(Cipher.DECRYPT_MODE, factory.generateSecret(spec), new IvParameterSpec(IV));

    if (verification == null) {
      throw new SecurityException("Internal error. No verification array.");
    }

    if (!VERIFY_STRING.equals(new String(cipher.doFinal(verification)))) {
      throw new SecurityException("Password not correct!");
    }

    cipher = Cipher.getInstance(CIPHER);
    cipher.init(Cipher.DECRYPT_MODE, factory.generateSecret(spec), new IvParameterSpec(IV));
    byte[] decrypted = cipher.doFinal(rawkey);

    spec = new SecretKeySpec(decrypted, ALGORITHM);
    masterKey = factory.generateSecret(spec);
  }

  private void changeLockData(char[] password) throws Exception {
    byte[] salt = MessageDigest.getInstance(DIGEST).digest(new String(password).getBytes());
    SecretKeySpec spec = new SecretKeySpec(salt, ALGORITHM);
    SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
    Key adminKey = factory.generateSecret(spec);

    Cipher cipher = Cipher.getInstance(CIPHER);
    cipher.init(Cipher.ENCRYPT_MODE, adminKey, new IvParameterSpec(IV));
    byte[] encrypted = cipher.doFinal(masterKey.getEncoded());

    cipher = Cipher.getInstance(CIPHER);
    cipher.init(Cipher.ENCRYPT_MODE, adminKey, new IvParameterSpec(IV));
    byte[] verificationArray = cipher.doFinal(VERIFY_STRING.getBytes());


    ModificationContextImpl mc = (ModificationContextImpl) root.getModificationContext();

    try {
      Configuration container = mc.getConfiguration(SecurityConfigurationPath.SECURESTORE_PATH, false, false);
      storeMasterKey(container, encrypted);
      storeVerificationArray(container, verificationArray);
      mc.commitModifications();
    } catch (Exception e) {
      mc.rollbackModifications();
      throw e;
    }
  }

  private byte[] loadMasterKey(Configuration container) {
    try {
      return (byte[]) container.getConfigEntry(CONFIGURATION_ENTRY_MASTER);
    } catch (Exception e) {
      return null;
    }
  }

  private byte[] loadVerificationArray(Configuration container) {
    try {
      return (byte[]) container.getConfigEntry(CONFIGURATION_ENTRY_VERIFICATION);
    } catch (Exception e) {
      return null;
    }
  }

  private void storeMasterKey(Configuration container, byte[] encrypted) throws Exception {
    try {
      container.addConfigEntry(CONFIGURATION_ENTRY_MASTER, encrypted);
    } catch (Exception e) {
      container.modifyConfigEntry(CONFIGURATION_ENTRY_MASTER, encrypted);
    }
  }

  private void storeVerificationArray(Configuration container, byte[] array) throws Exception {
    try {
      container.addConfigEntry(CONFIGURATION_ENTRY_VERIFICATION, array);
    } catch (Exception e) {
      container.modifyConfigEntry(CONFIGURATION_ENTRY_VERIFICATION, array);
    }
  }

}