/*
 * Created on 2005-4-14 @author Alexander Alexandrov,
 * e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.crypto;


import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sap.engine.lib.jaxp.DocumentBuilderFactoryImpl;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.crypto.impl.*;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 */
public class ReusableConfigurator {
  static Class[] EMPTY = new Class[0];

  static String provider = "IAIK";

  public static void addClass(String className, String uri) throws Exception {
    Class clas = Class.forName(className);
    Reusable.newInstanceUris.put(uri, clas.getMethod("newInstance", EMPTY));
    Reusable.releaseMethods.put(uri, clas.getMethod("release", EMPTY));
  }

  public static String defaultConfigFile = System.getProperty("com.sap.xml.security.algorithms", "algorithms.xml");

  public static void readConfiguration() {
    try {
      Reusable.newInstanceUris.clear();
      Reusable.releaseMethods.clear();
      DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();//.newInstance();
      factory.setValidating(true);
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc;
      if (new File(defaultConfigFile).exists()){
        doc = builder.parse(new FileInputStream(defaultConfigFile));
      } else {
        doc = builder.parse(ReusableConfigurator.class.getResourceAsStream(defaultConfigFile));
      }
      Element root = doc.getDocumentElement();
      provider = root.getAttribute("Provider");
      ReusablePool.MAX_SIZE = Integer.parseInt(root.getAttribute("MaxPoolSize"));
      NodeList algos =  doc.getElementsByTagName("Algorithm");
      for (int i=0;i<algos.getLength();i++){
        Element algorithm = (Element) algos.item(i);
        addClass(algorithm.getAttribute("Class"), algorithm.getAttribute("Uri"));
      }
    } catch (Throwable ex) {
      ex.printStackTrace();
      new SignatureException("Unable to load configuration", ex);
      initConstants();
    }

  }

  /**
   * 
   */
  private static void initConstants() {
    try {
      addClass(ReusableAESCipher.class.getName(), ReusableAESCipher.AES_128_URI);
      addClass(ReusableAESCipher.class.getName(), ReusableAESCipher.AES_192_URI);
      addClass(ReusableAESCipher.class.getName(), ReusableAESCipher.AES_256_URI);
      addClass(ReusableAESSecretKeyFactory.class.getName(), ReusableAESSecretKeyFactory.AES_KEYGEN128_URI);
      addClass(ReusableAESSecretKeyFactory.class.getName(), ReusableAESSecretKeyFactory.AES_KEYGEN192_URI);
      addClass(ReusableAESSecretKeyFactory.class.getName(), ReusableAESSecretKeyFactory.AES_KEYGEN256_URI);
      addClass(ReusableDESedeCipher.class.getName(), ReusableDESedeCipher.DES_EDE_URI);
      addClass(ReusableDESedeSecretKeyFactory.class.getName(), ReusableDESedeSecretKeyFactory.DESEDE_KEYGEN_URI);
      addClass(ReusableDSASignature.class.getName(), ReusableDSASignature.DSA_SHA1_URI);
      addClass(ReusableHMACSignature.class.getName(), ReusableHMACSignature.HMAC_SHA1_URI);
      addClass(ReusableRSASignature.class.getName(), ReusableRSASignature.RSA_SHA1_URI);
      addClass(ReusableSecureRandom.class.getName(), ReusableSecureRandom.SHA1_PRNG_URI);
      addClass(ReusableRSAOAEPCipher.class.getName(), ReusableRSAOAEPCipher.RSA_OAEP);
      addClass(ReusableRSA15Cipher.class.getName(), ReusableRSA15Cipher.RSA_1_5);
      addClass(ReusableMD5Digest.class.getName(), ReusableMD5Digest.MD5_DIGEST);
      addClass(ReusableSHA1Digest.class.getName(), ReusableSHA1Digest.SHA1_DIGEST);
      addClass(ReusableSHA256Digest.class.getName(), ReusableSHA256Digest.SHA256_DIGEST);
      addClass(ReusableSHA512Digest.class.getName(), ReusableSHA512Digest.SHA512_DIGEST);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static String getProvider() {
    return provider;
  }
  public static String getDefaultConfigFile() {
    return defaultConfigFile;
  }
  public static void setDefaultConfigFile(String defaultConfigFile) {
    ReusableConfigurator.defaultConfigFile = defaultConfigFile;
    readConfiguration();
  }
}
