/*
 * Created on 2004-3-30
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.encryption.aliases;

import java.security.Key;
import java.util.Enumeration;

import com.sap.engine.lib.xml.signature.SignatureException;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public interface KeyAliasResolver {
  /**
   * Tries to obtain a key with this alias from this key alias resolver
   * @param alias key alias 
   * @return the key with this key alias (may be null if there is no such key)
   * @throws SignatureException if any error occurs
   */
  public Key getKey(String alias) throws SignatureException;
  
  /**
   * Tries to obtain a key with this alias from this key alias resolver
   * @param alias key alias - key name or X509 certificate! 
   * @return the key with this key alias (may be null if there is no such key)
   * @throws SignatureException if any error occurs
   */  
  public Key getKey(Object alias) throws SignatureException;
  
  /**
   * Returns enumeration of all key aliases resovable by this key alias resolver.
   * @return enumeration of all key aliases in this key alias resolver.
   * @throws SignatureException if any error occurs
   */
  public Enumeration getKeyAliases() throws SignatureException;
  
  /**
   * Tries to remove the key with this alias from this key resolver
   * @param alias the alias to be removed
   * @throws SignatureException if any error occurs
   */
  public void removeKey(String alias) throws SignatureException;
  
  /**
   * Tries to remove the key with this alias from this key resolver
   * @param alias the alias to be removed - may be X509 certificate or key name
   * @throws SignatureException if any error occurs
   */
  public void removeKey(Object alias) throws SignatureException;  
  
  /**
   * Binds this key to this alias in this key alias resolver.
   * @param alias alias for this key
   * @param key key to be added
   * @throws SignatureException if any error occurs.
   */
  public void addKey(String alias, Key key) throws SignatureException;
  
  
  /**
   * Binds this key to this alias in this key alias resolver.
   * @param alias alias for this key - may be X509 certificate or string
   * @param key key to be added
   * @throws SignatureException if any error occurs.
   */
  public void addKey(Object alias, Key key) throws SignatureException;
  
  /**
   * Checks if there is a key binded to this alias.
   * @param alias
   * @return if there is a key binded to this alias.
   * @throws SignatureException if any error occurs.
   */
  public boolean contains(String alias) throws SignatureException;
  
  /**
   * Checks if there is a key binded to this alias.
   * @param alias
   * @return if there is a key binded to this alias.
   * @throws SignatureException if any error occurs.
   */
  public boolean contains(Object alias) throws SignatureException;  
  
  /**
   * @return name for this key alias resolver implementation
   */
  public String getName();

}
