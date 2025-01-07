/*
 * Created on 2004-2-24
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public class CryptographicProvider {

  String name = null;
  String jceString = null;
  /**
   * 
   */
  public CryptographicProvider(Node provider) {
    super();
    NamedNodeMap nnm =  provider.getAttributes();
    Node uri = nnm.getNamedItem("name");
    Node jce = nnm.getNamedItem("jceString");
    
    name = uri!=null?uri.getNodeValue():null;
    jceString = jce!=null?jce.getNodeValue():null;
    NodeList childs = provider.getChildNodes();
    for(int i=0;i<childs.getLength();i++){
      Node n = childs.item(i);
      String nameValue = n.getNodeName();
      if ("CipherAlgorithms".equals(nameValue)){
        init(n,cipherAlgorithms, cipherAlgorithmsReversed);
      } else if ("MessageDigestAlgorithms".equals(nameValue)){
        init(n,digestAlgorithms, digestAlgorithmsReversed);
      } else if ("SignatureAlgorithms".equals(nameValue)){
        init(n,signatureAlgorithms, signatureAlgorithmsReversed);
      }  
    }
  }
  
  /**
   * @param n
   */
  private void init(Node n, Hashtable mapping, Hashtable reversedMapping) {
    NodeList childs = n.getChildNodes();
    for(int i=0;i<childs.getLength();i++){
      Node child = childs.item(i);
      String nodeName = child.getNodeName();
      if ("Algorithm".equals(nodeName)){
        NamedNodeMap nnm =  child.getAttributes();
        nnm.getNamedItem("name");
        Node uri = nnm.getNamedItem("uri");
        Node jce = nnm.getNamedItem("jce");
        Node ivLen = nnm.getNamedItem("ivlen");
        if ((uri!=null)&&(jce!=null)){
          String uriValue = uri.getNodeValue();
          String jceValue = jce.getNodeValue();
          mapping.put(uriValue, jceValue);
          reversedMapping.put(jceValue, uriValue);
          if (ivLen!=null){
            String iv = ivLen.getNodeValue();
            try {
            ivLengths.put(uriValue, new Integer(iv));
            } catch (NumberFormatException nfe){
              new SignatureException("Number format exception",new Object[]{n.getOwnerDocument(),uriValue, iv}, nfe );
            }
          }
        }

      } 
    }    
    
  }

  protected CryptographicProvider(){
    super();
  }
  
  static void initConstants(Hashtable providers){
    providers.clear();
    
    CryptographicProvider IAIK = new CryptographicProvider();
    IAIK.name = "IAIK";
    IAIK.jceString = "IAIK";
    
    IAIK.digestAlgorithms.put( Constants.DIGEST_MD5, "MD5");
    IAIK.digestAlgorithms.put( Constants.DIGEST_SHA1, "SHA-1");
    
    IAIK.digestAlgorithmsReversed.put( "MD5", Constants.DIGEST_MD5);
    IAIK.digestAlgorithmsReversed.put( "SHA-1", Constants.DIGEST_SHA1);    
    
    IAIK.signatureAlgorithms.put(Constants.SIGN_DSA, "SHA-1/DSA");
    IAIK.signatureAlgorithms.put(Constants.SIGN_RSA, "SHA-1/RSA");
    
    IAIK.signatureAlgorithmsReversed.put( "SHA-1/DSA", Constants.SIGN_DSA);
    IAIK.signatureAlgorithmsReversed.put( "SHA-1/RSA", Constants.SIGN_RSA);    

    IAIK.cipherAlgorithms.put(Constants.ALG_ENC_TRIPLEDES, "DESede");
    IAIK.cipherAlgorithms.put(Constants.ALG_ENC_AES128, "AES");
    IAIK.cipherAlgorithms.put(Constants.ALG_ENC_AES192, "AES-192");
    IAIK.cipherAlgorithms.put(Constants.ALG_ENC_AES256, "AES-256");
    IAIK.cipherAlgorithms.put(Constants.KEY_ENC_RSA_1_5, "RSA");
    IAIK.cipherAlgorithms.put(Constants.KEY_ENC_RSA_OAEP, "RSA");

    IAIK.cipherAlgorithmsReversed.put( "DESede", Constants.ALG_ENC_TRIPLEDES);
    IAIK.cipherAlgorithmsReversed.put("AES",Constants.ALG_ENC_AES128 );
    IAIK.cipherAlgorithmsReversed.put("AES-192",Constants.ALG_ENC_AES192 );
    IAIK.cipherAlgorithmsReversed.put("AES-256",Constants.ALG_ENC_AES256 );
    IAIK.cipherAlgorithmsReversed.put("RSA",Constants.KEY_ENC_RSA_1_5 );
    
    IAIK.ivLengths.put(Constants.ALG_ENC_TRIPLEDES, new Integer("8"));
    IAIK.ivLengths.put(Constants.ALG_ENC_AES128, new Integer("16"));
    IAIK.ivLengths.put(Constants.ALG_ENC_AES192, new Integer("16"));
    IAIK.ivLengths.put(Constants.ALG_ENC_AES256, new Integer("16"));
    
    CryptographicProvider BC = new CryptographicProvider();
    BC.name = "BouncyCastle";
    BC.jceString = "BC";
    
    BC.digestAlgorithms.put( Constants.DIGEST_MD5, "MD5");
    BC.digestAlgorithms.put( Constants.DIGEST_SHA1, "SHA1");

    BC.digestAlgorithmsReversed.put( "MD5", Constants.DIGEST_MD5);
    BC.digestAlgorithmsReversed.put( "SHA1", Constants.DIGEST_SHA1);    
    
    BC.signatureAlgorithms.put(Constants.SIGN_DSA, "DSA");
    BC.signatureAlgorithms.put(Constants.SIGN_RSA, "SHA1WithRSAEncryption");

    BC.signatureAlgorithmsReversed.put( "DSA", Constants.SIGN_DSA);
    BC.signatureAlgorithmsReversed.put( "SHA1WithRSAEncryption", Constants.SIGN_RSA);

    BC.cipherAlgorithms.put(Constants.ALG_ENC_TRIPLEDES, "DESEDE");
    BC.cipherAlgorithms.put(Constants.ALG_ENC_AES128, "AES");
    BC.cipherAlgorithms.put(Constants.ALG_ENC_AES192, "AES-192");
    BC.cipherAlgorithms.put(Constants.ALG_ENC_AES256, "AES-256");
    BC.cipherAlgorithms.put(Constants.KEY_ENC_RSA_1_5, "RSA");
    BC.cipherAlgorithms.put(Constants.KEY_ENC_RSA_OAEP, "RSA");

    BC.cipherAlgorithmsReversed.put( "DESEDE", Constants.ALG_ENC_TRIPLEDES);
    BC.cipherAlgorithmsReversed.put("AES",Constants.ALG_ENC_AES128 );
    BC.cipherAlgorithmsReversed.put("AES-192",Constants.ALG_ENC_AES192 );
    BC.cipherAlgorithmsReversed.put("AES-256",Constants.ALG_ENC_AES256 );
    BC.cipherAlgorithmsReversed.put("RSA",Constants.KEY_ENC_RSA_1_5 );
    
    BC.ivLengths.put(Constants.ALG_ENC_TRIPLEDES, new Integer("8"));
    BC.ivLengths.put(Constants.ALG_ENC_AES128, new Integer("16"));
    BC.ivLengths.put(Constants.ALG_ENC_AES192, new Integer("16"));
    BC.ivLengths.put(Constants.ALG_ENC_AES256, new Integer("16"));    
    
    providers.put(BC.getJceString(), BC);
    providers.put(IAIK.getJceString(), IAIK);
  }


  Hashtable signatureAlgorithms = new Hashtable(4);
  Hashtable signatureAlgorithmsReversed = new Hashtable(4);
  
  Hashtable cipherAlgorithms = new Hashtable(8);
  Hashtable cipherAlgorithmsReversed = new Hashtable(8);
  
  Hashtable digestAlgorithms = new Hashtable(4);
  Hashtable digestAlgorithmsReversed = new Hashtable(4);
  
  Hashtable ivLengths = new Hashtable(4);
 
  public Map getCipherAlgorithms(){
    return Collections.unmodifiableMap(cipherAlgorithms); 
  }
  
  public Map getSignatureAlgorithms(){
    return Collections.unmodifiableMap(signatureAlgorithms); 
  }
  
  public Map getDigestAlgorithms() {
    return Collections.unmodifiableMap(digestAlgorithms);
  }
  
  public Map getIVLengths(){
    return Collections.unmodifiableMap(ivLengths);
  }
  
  public int getIVLength(String algorithmURI){
    Integer i = (Integer) ivLengths.get(algorithmURI);
    if (i==null) {
      return 0;
    } else {
      return i.intValue();
    }
  }
  
  public String getDigestURIFromJCE(String jceName) {
    return (String) digestAlgorithmsReversed.get(jceName);
  }

  public String getSignatureURIFromJCE(String jceName) {
    return (String) signatureAlgorithmsReversed.get(jceName);
  }

  public String getCipherURIFromJCE(String jceName) {
    return (String) cipherAlgorithmsReversed.get(jceName);
  }

  public String getDigestAlgorithm(String uri) {
    return (String) digestAlgorithms.get(uri);
  }

  public String getSignatureAlgorithm(String uri) {
    return (String) signatureAlgorithms.get(uri);
  }

  public String getCipherAlgorithm(String uri) {
    return (String) cipherAlgorithms.get(uri);
  }  
  
  
  /**
   * @return Returns the jceString.
   */
  public String getJceString() {
    return jceString;
  }

  /**
   * @return Returns the name.
   */
  public String getName() {
    return name;
  }

}
