package com.sap.engine.lib.xml.signature.encryption;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @deprecated    Use com.sap.engine.lib.xml.signature.Constants or com.sap.engine.lib.xml.signature.Configurator
 * 
 */

public abstract class Constants extends com.sap.engine.lib.xml.signature.Constants {
/*
  public static final String ENCRYPTION_SPEC_NS = "http://www.w3.org/2001/04/xmlenc#";
  public static final String STANDARD_ENC_PREFIX = "xenc:";
  public static final String ELEMENT_ENCRYPTION = "http://www.w3.org/2001/04/xmlenc#Element";
  public static final String CONTENT_ENCRYPTION = "http://www.w3.org/2001/04/xmlenc#Content";
  public static final String KEY_ENCRYPTION = "http://www.w3.org/2001/04/xmlenc#EncryptedKey";
  public static final String ENCRYPTION_PROPERTIES_REFERENCE = "http://www.w3.org/2001/04/xmlenc#EncryptionProperties";
  // ****  BLOCK ENCRYPTION  ****
  // REQUIRED TRIPLEDES
  public static final String ALG_ENC_TRIPLEDES = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
  //REQUIRED AES-128
  public static final String ALG_ENC_AES128 = "http://www.w3.org/2001/04/xmlenc#aes128-cbc";
  //REQUIRED AES-256
  public static final String ALG_ENC_AES256 = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";
  //OPTIONAL AES-192
  public static final String ALG_ENC_AES192 = "http://www.w3.org/2001/04/xmlenc#aes192-cbc";
  // ****  MESSAGE DIGEST  ****
  // Note: SHA1 is inherited from signature constants
  //RECOMMENDED SHA256
  public static final String DIGEST_SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
  //OPTIONAL SHA512
  public static final String DIGEST_SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
  //OPTIONAL RIPEMD-160
  public static final String DIGEST_RIPEMD160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";
  // ****  KEY ENCRYPTION  ****
  //REQUIRED  RSA1.5
  public static final String KEY_ENC_RSA_1_5 = "http://www.w3.org/2001/04/xmlenc#rsa-1_5";
  //REQUIRED RSAO-AEP
  public static final String KEY_ENC_RSA_OAEP = "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p";
     */
  // ****  ALGORITHM NAME MAPPINGS ****
  public static final Hashtable cipherAlgMappings = new Hashtable();


  static {
    cipherAlgMappings.put("DESede", ALG_ENC_TRIPLEDES);
    cipherAlgMappings.put("3DES", ALG_ENC_TRIPLEDES);
    cipherAlgMappings.put("Rijndael", ALG_ENC_AES128);
    cipherAlgMappings.put("Rijndael-256", ALG_ENC_AES256);
    cipherAlgMappings.put("RSA", KEY_ENC_RSA_1_5);
    cipherAlgMappings.put("RSA", KEY_ENC_RSA_OAEP);
  }

  public static final void addCipherAlgorithm(String jceIdentifier, String uri) {
    cipherAlgMappings.put(jceIdentifier, uri);
  }

  public static final Hashtable getAllRegisteredCipherAlgorithms() {
    return cipherAlgMappings;
  }

  public static final String getCipherJCEFromURI(String uri) {
    Enumeration en = cipherAlgMappings.keys();

    while (en.hasMoreElements()) {
      String nextJCE = (String) en.nextElement();
      String nextURI = (String) cipherAlgMappings.get(nextJCE);

      if (nextURI != null && nextURI.equals(uri)) {
        return nextJCE;
      }
    }

    return null;
  }

  public static final String getCipherURIFromJCE(String jceCipher) {
    return (String) cipherAlgMappings.get(jceCipher);
  }

  // **** ALGORIHM IV LENGTH
  public static final Hashtable ivLengths = new Hashtable();

  static {
    ivLengths.put(ALG_ENC_TRIPLEDES, new Integer("8"));
    ivLengths.put(ALG_ENC_AES128, new Integer("16"));
    ivLengths.put(ALG_ENC_AES256, new Integer("32"));
  }

}

