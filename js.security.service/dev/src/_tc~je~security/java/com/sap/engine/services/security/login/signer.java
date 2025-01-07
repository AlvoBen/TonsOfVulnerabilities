package com.sap.engine.services.security.login;

import iaik.asn1.structures.AlgorithmID;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Vector;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.lib.security.Base64;
import com.sap.engine.lib.util.ConcurrentHashMapLongObject;
import com.sap.engine.services.security.Util;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class Signer {
	
	private static final boolean inServer = SystemProperties.getBoolean("server");
  private static final String ALGORITHM = "DSA";
  private static final int KEY_SIZE = 512;
  private static final String EMPTY_SIGNATURE = "";
  
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_P4_TICKET_LOCATION);
  
  private static PrivateKey serverPK = null;
  private static ConcurrentHashMapLongObject map = new ConcurrentHashMapLongObject();
  private static long currentId = -1;
  private static KeyStore keystore = null;
  private static SecureRandom random = null;
  private static Vector signatures = null;

  protected static boolean initialized = false;
  protected static boolean keystoreStarted = false;

  static {
    random = new SecureRandom();
    signatures = new Vector();
  }

  public static void keystoreStarted() {
    keystoreStarted = true;
  }

  public static void keystoreStopped() {
    keystoreStarted = false;
    initialized = false;
  }

  public static String generateAlias() {
    byte[] alias = new byte[20];
    StringBuffer buffer = new StringBuffer("P4_");

    random.nextBytes(alias);

    try {
      buffer.append(new String(Base64.encode(alias)));
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.DEBUG, "P4 communication: Unable to generate Base64 encoded alias for the session.", e);
      for (int i = 0; i < alias.length; i++) {
        buffer.append(Integer.toString(alias[i], 16));
      }
    }
    buffer.append("_SAP");
    return buffer.toString();
  }

  public static String sign(String data) {
    Signature signature = null;

    try {
      if (!keystoreStarted) {
        return EMPTY_SIGNATURE;
      }
      initializeKey();

      signature = getSignature();
      signature.initSign(serverPK, random);
      signature.update(data.getBytes());

      return new String(Base64.encode(signature.sign()));
    } catch (Exception ex) {
      throw new SecurityException("Cannot generate ticket.", ex);
    } finally {
      if (signature != null) {
        signatures.add(signature);
      }
    }
  }

  public static boolean verify(long clusterId, String rawdata, String signed) {
    Signature signature = null;

    try {
      if (!keystoreStarted) {
        return true;
      }
      if (clusterId <= 0) {
        return false;
      }

      initializeKey();

      signature = getSignature();
      PublicKey publicKey = (PublicKey) map.get(clusterId);
      if (publicKey == null) {
        publicKey = getPublicKeyFromKeyStore(clusterId);
      }
      signature.initVerify(publicKey);
      signature.update(rawdata.getBytes());

      byte[] decodedSignature = Base64.decode(signed.getBytes()); 
      boolean verify = signature.verify(decodedSignature);
      return verify;
    } catch (Exception ex) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.DEBUG, "P4 communication: Verification of signature failed!", ex);
      throw new SecurityException("P4 communication: Verification of signature failed!", ex);
    } finally {
      if (signature != null) {
        signatures.add(signature);
      }
    }
  }


/////////////////////////////////////////////////////////////
  private static void initializeKey() {
    if (!initialized && (inServer)) {
      synchronized (Signer.class) {
        if (!initialized) {
          try {
            currentId = com.sap.engine.services.security.SecurityServerFrame.currentParticipant;
            KeyPair pair = generateKeyPair();
            serverPK = pair.getPrivate();
            map.put(currentId, pair.getPublic());
            keystore = KeyStore.getInstance("EBSDKS");
            keystore.load(null, null);
            try {
              keystore.deleteEntry("Server" + currentId);
            } catch (KeyStoreException kse) {
              if (LOCATION.beWarning()) {
                LOCATION.traceThrowableT(Severity.WARNING, kse.getLocalizedMessage(), kse);
              }
            }
            keystore.setKeyEntry("Server" + currentId, pair.getPublic(), null, new Certificate[0]);
            keystoreStarted = true;
            initialized = true;
          } catch (Exception e) {
            keystoreStarted = false;
          }
        }
      }
    }
  }

  private static Signature getSignature() throws GeneralSecurityException {
    Signature result = null;

    if (signatures.size() > 0) {
      try {
        result = (Signature) signatures.remove(0);
      } catch (Exception e) {
        result = null;
      }
    }
    return (result != null) ? result : AlgorithmID.dsa.getSignatureInstance();
  }

  private static KeyPair generateKeyPair() throws GeneralSecurityException {
    KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
    generator.initialize(KEY_SIZE);
    return generator.genKeyPair();
  }

  private static PublicKey getPublicKeyFromKeyStore(long clusterId) throws GeneralSecurityException {
    return (PublicKey) keystore.getKey("Server" + clusterId, null);
  }
}