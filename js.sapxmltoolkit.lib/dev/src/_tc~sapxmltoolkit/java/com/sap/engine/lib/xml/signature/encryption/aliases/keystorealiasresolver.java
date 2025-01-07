/*
 * Created on 2004-3-30
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.encryption.aliases;

import java.security.Key;
import java.security.KeyStore;
import java.util.Enumeration;

import java.security.cert.Certificate;

import com.sap.engine.lib.xml.signature.SignatureException;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public class KeyStoreAliasResolver implements KeyAliasResolver {
  private final static String name = "Key Store Alias Resolver v0.1";
  private KeyStore keyStore = null;
  private transient char[] password = null;
  private Certificate[] cert = null;
  
  public KeyStoreAliasResolver(KeyStore keystore, char[] password){
    this.password = password;
    keyStore = keystore;
  }
  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#getKey(java.lang.String)
   */
  public Key getKey(String alias) throws SignatureException {
    try {
      return keyStore.getKey(alias, password);
    } catch (Exception ex){
//TODO: remove password from logging      
      throw new SignatureException("Unable to get key:"+alias+" from keystore with this password",new Object[]{keyStore, alias, password}, ex);
    }
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#getKeyAliases()
   */
  public Enumeration getKeyAliases() throws SignatureException {
    try {
//TODO: certificates not to be used!!!!      
      return keyStore.aliases();
    } catch (Exception ex){
      throw new SignatureException("Unable to get aliases",new Object[]{keyStore}, ex);
    }
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#removeKey(java.lang.String)
   */
  public void removeKey(String alias) throws SignatureException {
    try{
      keyStore.deleteEntry(alias);
    } catch (Exception ex){
      throw new SignatureException("Unable to delete:"+alias,new Object[]{keyStore,alias}, ex);
    }
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#addKey(java.lang.String, java.security.Key)
   */
  public void addKey(String alias, Key key) throws SignatureException {
    try{
      keyStore.setKeyEntry(alias, key, password, cert);
    } catch (Exception ex){
      throw new SignatureException("Unable to delete:"+alias,new Object[]{keyStore,alias}, ex);
    }

  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#getName()
   */
  public String getName() {
    return name;
  }
  
  public void setCertificates(Certificate[] certificates){
    this.cert = certificates;
  }
  
  public void setKeyStore(KeyStore ks){
    this.keyStore = ks;
  }
  
  public Certificate[] getCertificates(){
    return cert;
  }
  
  public KeyStore getKeyStore(){
    return keyStore;
  }
  
  public void setPassword(char[] password){
    this.password = password;
  }
  
  public boolean contains(String alias) throws SignatureException{
    try {
      return keyStore.containsAlias(alias)&&keyStore.isKeyEntry(alias);
    } catch (Exception ex){
      throw new SignatureException("Unable to check:"+alias,new Object[]{keyStore,alias}, ex);
    }
  }
  
  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#getKey(java.lang.Object)
   */
  public Key getKey(Object alias) throws SignatureException {
      String name;
      if (alias instanceof String){
        name = (String) alias;
      } else {
        //TODO: not sure!!!
        name = alias.toString();
      }
      return getKey(name);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#removeKey(java.lang.Object)
   */
  public void removeKey(Object alias) throws SignatureException {
    String name;
    if (alias instanceof String){
      name = (String) alias;
    } else {
      //TODO: not sure!!!
      name = alias.toString();
    }
    removeKey(name);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#addKey(java.lang.Object, java.security.Key)
   */
  public void addKey(Object alias, Key key) throws SignatureException {
    String name;
    if (alias instanceof String){
      name = (String) alias;
    } else {
      //TODO: not sure!!!
      name = alias.toString();
    }
    addKey(name, key);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#contains(java.lang.Object)
   */
  public boolean contains(Object alias) throws SignatureException {
    String name;
    if (alias instanceof String){
      name = (String) alias;
    } else {
      //TODO: not sure!!!
      name = alias.toString();
    }
    
    return contains(name);
  }  

}
