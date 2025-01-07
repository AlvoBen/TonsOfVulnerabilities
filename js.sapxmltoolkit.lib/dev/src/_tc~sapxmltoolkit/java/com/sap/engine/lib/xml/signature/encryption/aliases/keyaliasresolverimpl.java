/*
 * Created on 2004-3-30
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.encryption.aliases;

import java.security.Key;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import com.sap.engine.lib.xml.signature.SignatureException;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public class KeyAliasResolverImpl implements KeyAliasResolver {
  
  private static final String NAME = "Key Alias Resolver Implementation v0.1";
  
  protected Vector keyAliasResolvers = new Vector();
  
  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#getKey(java.lang.String)
   */
  public Key getKey(String alias) throws SignatureException {
    Iterator it = keyAliasResolvers.iterator();
    while (it.hasNext()){
      KeyAliasResolver temp = (KeyAliasResolver) it.next();
      try {
        if (temp.contains(alias)){
          return temp.getKey(alias);
        }
      } catch (Exception ex){
        // $JL-EXC$
        //nothing!!! - error then try next
      }
    }
    return null;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#getKeyAliases()
   */
  public Enumeration getKeyAliases() throws SignatureException {
    // TODO: Not implemented - combination of enumerations
    return null;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#removeKey(java.lang.String)
   */
  public void removeKey(String alias) throws SignatureException {
    Iterator it = keyAliasResolvers.iterator();
    while (it.hasNext()){
      KeyAliasResolver temp = (KeyAliasResolver) it.next();
      try {
        if (temp.contains(alias)){
          temp.removeKey(alias);
          // deletes only the first one!!!
          return;
        }
      } catch (Exception ex){
        // $JL-EXC$
        //nothing!!! - error then try next
      }
    }

  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#addKey(java.lang.String, java.security.Key)
   */
  public void addKey(String alias, Key key) throws SignatureException {
     throw new SignatureException("Use inner resolvers for this operation");
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#contains(java.lang.String)
   */
  public boolean contains(String alias) throws SignatureException {
    Iterator it = keyAliasResolvers.iterator();
    while (it.hasNext()){
      KeyAliasResolver temp = (KeyAliasResolver) it.next();
      try {
        if (temp.contains(alias)){
          return true;
        }
      } catch (Exception ex){
        // $JL-EXC$
        //nothing!!! - error then try next
      }
    }
    return false;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.signature.encryption.aliases.KeyAliasResolver#getName()
   */
  public String getName() {
    return NAME;
  }
  
  public void addKeyAliasResolver(KeyAliasResolver kar){
    keyAliasResolvers.addElement(kar);
  }
  
  public void insertKeyAliasResolverAt(KeyAliasResolver kar, int pos){
    keyAliasResolvers.insertElementAt(kar,pos);
  }
  
  public void removeKeyAliasResolver(int pos){
    keyAliasResolvers.removeElementAt(pos);
  }
  
  public int countKeyAliasResolvers(){
    return keyAliasResolvers.size();
  }
  
  public Vector getKeyAliasResolverVector(){
    return keyAliasResolvers;
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
