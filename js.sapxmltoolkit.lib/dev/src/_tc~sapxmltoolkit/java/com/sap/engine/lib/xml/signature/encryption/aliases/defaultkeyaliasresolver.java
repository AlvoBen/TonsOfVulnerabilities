/*
 * Created on 2004-3-30
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.encryption.aliases;

import java.security.Key;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Hashtable;

import com.sap.engine.lib.xml.signature.SignatureException;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public class DefaultKeyAliasResolver implements KeyAliasResolver {
  private static final String NAME ="Default Key Resolver v0.1"; 
  protected Hashtable keys = new Hashtable();
  public Key getKey(String alias) throws SignatureException {
    return (Key) keys.get(alias);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#getKeyAliases()
   */
  public Enumeration getKeyAliases() throws SignatureException {
    return keys.keys();
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#removeKey(java.lang.String)
   */
  public void removeKey(String alias) throws SignatureException {
    keys.remove(alias);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#addKey(java.lang.String, java.security.Key)
   */
  public void addKey(String alias, Key key) throws SignatureException {
    keys.put(alias, key);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#contains(java.lang.String)
   */
  public boolean contains(String alias) throws SignatureException {
    return keys.containsKey(alias);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#getName()
   */
  public String getName() {
    return NAME;
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

  //TODO: read initial keys from XML file!
  
}
