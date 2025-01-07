/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.internal.store;

import com.sap.engine.services.iiop.internal.*;
import org.omg.CORBA.DynamicImplementation;
import org.omg.CORBA.portable.InvokeHandler;

import java.util.*;

/**
 * This class represents the output stream cashe, it is a hashtable implementstion.
 *
 * @author Nikolai Neichev, Ivan Atanassov
 * @version 4.0
 */
public class ConnectionTableHash {

    // the raw HashMap
  private HashMap<KeyObject, TargetHolder> cacheStore = new HashMap<KeyObject, TargetHolder>();

    // shared wrapper object
  private KeyObject sharedKeyObject = new KeyObject(null);

  /**
   * Puts an object into the hashtable.
   * @param object The object key.
   */
  public void put(byte[] key, org.omg.CORBA.Object object) throws IllegalArgumentException {
    TargetHolder toPut;
    if (object instanceof InvokeHandler) {
      toPut = new InvokeHandlerHolder((InvokeHandler) object);
    } else if (object instanceof DynamicImplementation) {
      toPut = new DIHolder((DynamicImplementation) object);
    } else if (object instanceof TargetHolder) {
      toPut = (TargetHolder) object;
    } else {
      toPut = new ObjectHolder(object);
    }

    KeyObject keyObject = new KeyObject(key);
    synchronized (sharedKeyObject) {
      TargetHolder previousObj = cacheStore.put(keyObject, toPut);
      if (previousObj != null) {
        cacheStore.put(keyObject, previousObj); //return the previous object
//        Logger.traceWarning("ConnectionTableHash.put(key, org.omg.CORBA.Object)", "ConnectionTableHash put method try to override object " + previousObj + " with object: " + toPut);
        throw new IllegalArgumentException("Try to override existing key");
      }
    }
  }

  /**
   * Get's a object from the hashtable
   * @param key the object key
   * @return the obejct corresponding to the key
   */
  public org.omg.CORBA.Object get(byte[] key) {
    TargetHolder th;
    synchronized (sharedKeyObject) {
      sharedKeyObject.setKey(key);
      th = cacheStore.get(sharedKeyObject);
    }
    if (th != null) {
      return th.getObject();
    } else {
      return null;
    }
  }

  /**
   * Get's a servant from the hashtable
   * @param key the servant key
   * @return the servant corresponding to the key
   */
  public TargetHolder getServant(byte[] key) {
    synchronized (sharedKeyObject) {
      sharedKeyObject.setKey(key);
      return cacheStore.get(sharedKeyObject);
    }
  }

  /**
   * Checks if the object key is already in the hashtable.
   * @param key The object key.
   * @return True - if exists.
   */      //TODO Vancho - not used!
  public boolean contains(byte[] key) {
    synchronized (sharedKeyObject) {
      sharedKeyObject.setKey(key);
      return cacheStore.containsKey(sharedKeyObject);
    }
  }

  /**
   * Deletes an object from the hashtable
   * @param key the object key
   */    //TODO Vancho - not used!
  public void delete(byte[] key) {
    synchronized (sharedKeyObject) {
      sharedKeyObject.setKey(key);
      cacheStore.remove(sharedKeyObject);
    }
  }

  /**
   * Deletes an object from the hashtable
   * @param obj the object to be deleted
   * @return 0 - if the table is empty; 1 - if the object is deleted; -1 - if the object is not found in the table
   */
  public int delete(org.omg.CORBA.Object obj) {

    synchronized (sharedKeyObject) {
      if (cacheStore.isEmpty()) {
        return 0;
      }

      Collection valueColl = cacheStore.values();
      Iterator collIterator = valueColl.iterator();
      while (collIterator.hasNext()) {
        Object nextObj = ((TargetHolder) collIterator.next()).getObject();
        if (nextObj.equals(obj)) {
          collIterator.remove();  //Edinstveno tuk se smjata hash code vednyz
          return 1;
        }
      }
    }
    return -1;
  }

  /**
   * Getter method
   * @return the table size
   */
  public int getSize() {
    return cacheStore.size();
  }

  public Hashtable<KeyObject, TargetHolder> getObjects() {
    return new Hashtable<KeyObject, TargetHolder>(cacheStore);
  }
}
