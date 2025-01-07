/*
 * Created on 2004-2-24
 * 
 * @author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature;

import java.security.Provider;
import java.security.Security;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sap.engine.lib.jaxp.DocumentBuilderFactoryImpl;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *  
 */
public class Configurator {
  // may be some synchronization
  static Hashtable providers = new Hashtable(4);

  static String defaultProvider = "IAIK";
  static CryptographicProvider currentProvider = null;

  public static String iniFile = "config.xml";

  static {
    init();
    if (currentProvider == null){
      defaultProvider = "IAIK";
      currentProvider = (CryptographicProvider) providers.get(defaultProvider);
    }
  }

  public static void init() {
    try {
      DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();//.newInstance();
      factory.setValidating(true);
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      /*
      InputStream s = Configurator.class.getResourceAsStream(iniFile);
      StringBuffer sb = new StringBuffer();
      int j = 0;
      while ((j=s.read())!=-1){
        sb.append((char) j);
      }
      LogWriter.getSystemLogWriter().println(sb);
      */
      Document doc = builder.parse(Configurator.class.getResourceAsStream(iniFile));
      Element root = doc.getDocumentElement();
      if ("XMLSecurity".equals(root.getNodeName())){
        defaultProvider = root.getAttribute("defaultProvider");
      }
      NodeList pros = doc.getElementsByTagName("Provider");
      providers.clear();
      for (int i = 0; i < pros.getLength(); i++) {
        CryptographicProvider cp = new CryptographicProvider(pros.item(i));
        providers.put(cp.getJceString(), cp);
      }
      currentProvider = (CryptographicProvider) providers.get(defaultProvider);
    } catch (Throwable ex) {
      ex.printStackTrace();
      new SignatureException("Unable to load configuration", ex);
      initConstants();
    }
  }

  public static void initConstants() {
    CryptographicProvider.initConstants(providers);
  }

  public static void setAlgorithmProvider(String providerName) {
    defaultProvider = providerName;
//    CryptographicPool.ciphers.clear();
//    CryptographicPool.signatures.clear();
//    CryptographicPool.messageDigests.clear();
    SignatureContext.nullCryptographicPool();
    currentProvider = (CryptographicProvider) providers.get(providerName);
  }
  /**
   * Provider has to be registred as securty provider by the caller.
   * No access to addProvider in Security class.
   */
  public static void setAlgorithmProvider(String providerName, Provider provider) {
    setAlgorithmProvider(providerName);
  }

  public static Map getCipherAlgorithms() throws SignatureException {
    if (currentProvider == null) {
      throw new SignatureException("No such provider configured. See config.xml:" + defaultProvider, new Object[] { defaultProvider });
    }
    return currentProvider.getCipherAlgorithms();
  }

  public static Map getSignatureAlgorithms() throws SignatureException {
    if (currentProvider == null) {
      throw new SignatureException("No such provider configured. See config.xml:" + defaultProvider, new Object[] { defaultProvider });
    }
    return currentProvider.getSignatureAlgorithms();
  }

  public static Map getDigestAlgorithms() throws SignatureException {
    if (currentProvider == null) {
      throw new SignatureException("No such provider configured. See config.xml:" + defaultProvider, new Object[] { defaultProvider });
    }
    return currentProvider.getDigestAlgorithms();
  }
  
  public static Map getIVLengths()throws SignatureException {
    if (currentProvider == null) {
      throw new SignatureException("No such provider configured. See config.xml:" + defaultProvider, new Object[] { defaultProvider });
    }
    return currentProvider.getIVLengths();
  }
  
  public static int getIVLength(String algorithmURI)throws SignatureException {
    if (currentProvider == null) {
      throw new SignatureException("No such provider configured. See config.xml:" + defaultProvider, new Object[] { defaultProvider });
    }
    return currentProvider.getIVLength(algorithmURI);
  }

  public static String getDigestURIFromJCE(String jceName) throws SignatureException {
    if (currentProvider == null) {
      throw new SignatureException("No such provider configured. See config.xml:" + defaultProvider, new Object[] { defaultProvider });
    }
    return currentProvider.getDigestURIFromJCE(jceName);
  }

  public static String getSignatureURIFromJCE(String jceName) throws SignatureException {
    if (currentProvider == null) {
      throw new SignatureException("No such provider configured. See config.xml:" + defaultProvider, new Object[] { defaultProvider });
    }
    return currentProvider.getSignatureURIFromJCE(jceName);
  }

  public static String getCipherURIFromJCE(String jceName) throws SignatureException {
    if (currentProvider == null) {
      throw new SignatureException("No such provider configured. See config.xml:" + defaultProvider, new Object[] { defaultProvider });
    }
    return currentProvider.getCipherURIFromJCE(jceName);
  }

  public static String getDigestAlgorithm(String uri) throws SignatureException {
    if (currentProvider == null) {
      throw new SignatureException("No such provider configured. See config.xml:" + defaultProvider, new Object[] { defaultProvider });
    }
    return currentProvider.getDigestAlgorithm(uri);
  }

  public static String getSignatureAlgorithm(String uri) throws SignatureException {
    if (currentProvider == null) {
      throw new SignatureException("No such provider configured. See config.xml:" + defaultProvider, new Object[] { defaultProvider });
    }
    return currentProvider.getSignatureAlgorithm(uri);
  }

  public static String getCipherAlgorithm(String uri) throws SignatureException {
    if (currentProvider == null) {
      throw new SignatureException("No such provider configured. See config.xml:" + defaultProvider, new Object[] { defaultProvider });
    }
    return currentProvider.getCipherAlgorithm(uri);
  }
  
  public static String getProviderName(){
    return defaultProvider;
  }

}
