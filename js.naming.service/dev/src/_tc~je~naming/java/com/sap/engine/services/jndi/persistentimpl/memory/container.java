/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.persistentimpl.memory;

import java.util.Enumeration;

import com.sap.engine.lib.util.ConcurrentArrayObject;
import com.sap.engine.lib.util.ConcurrentHashMapObjectObject;
import com.sap.engine.services.jndi.persistent.JNDIHandle;
import com.sap.engine.services.jndi.persistent.JNDIHandleEnumeration;
import com.sap.engine.services.jndi.persistent.exceptions720.JNDIException;

/**
 * Container for storing data
 *
 * @author Panayot Dobrikov, Elitsa Pancheva
 * @version 4.00
 */
public class Container implements java.io.Serializable {
  /**
   * serial version UID
   */
  static final long serialVersionUID = -9058533831829538267L;
  /**
   * Container Name
   */
  protected String cName;
  

  /**
   * Data in the container
   */
  private byte[] data;
  /**
   * Objects in the container
   */

  ConcurrentHashMapObjectObject objsByName = new ConcurrentHashMapObjectObject();

  /**
   * Constructor
   *
   * @param name Name of the container
   * @param cName Container ID
   */
  public Container(String cName) {
    this.cName = cName;
  }

  public byte[] getData() {
    return data;
  }

  public synchronized void setData(byte[] data) {
    this.data = data;
  }

  // util *********************************
  /**
   * Returns object from container by name
   *
   * @param name Name of the object
   * @return Handle to the object requested
   */
  JNDIHandle getJNDIHandle(String name) {
    Obj o = (Obj) objsByName.get(name);

    if (o != null) {
      return new JNDIHandleImpl(cName, o.getName());
    } else {
      return null;
    }
  }

  /**
   * Returns object from container by name
   *
   * @param name Name of the object
   * @return The object requested
   */
  Obj getObject(String name) {
    return (Obj) objsByName.get(name);
  }

  /**
   * Returns object from container by handle
   *
   * @param jh Handle to the container
   * @return Requested object
   */
  Obj getObject(JNDIHandle jh) {
    return getObject(jh.getObjectName());
  }

  /**
   * Returns position of object from container by object's ID
   *
   * @param oid Object ID
   * @return Position in the container
   */
  //  int getObjectPosition(long oid) {
  //    Obj o = null;
  //
  //    for (int i = 0; i < obs.size(); i++) {
  //      o = (Obj) obs.elementAt(i);
  //
  //      if (o.oid == oid) {
  //        return i;
  //      }
  //    }
  //
  //    return -1;
  //  }
  /**
   * Returns position of object from container by handle
   *
   * @param jh Handle to the object
   * @return Position in the container
   */
  //  int getObjectPosition(JNDIHandle jh) {
  //    return getObjectPosition(jh.getObjectID());
  //  }
  public String getContainerName() {
    return cName;
  }

  /**
   * Sets the ID of the container
   *
   * @param cName container's Id to be set
   */
  public synchronized void setName(String cName) {
    this.cName = cName;
  }

  /**
   * Appends data
   *
   * @param name Name to use
   * @param data Data to use
   * @param oName Object ID
   * @throws JNDIException 
   */
  public synchronized void append(String name, byte[] data, short type, int clusterID) throws JNDIException {
    if (!objsByName.containsKey(name)) {
      objsByName.put(name, new Obj(name, data, type, clusterID));
    } else {
      JNDIException je = new JNDIException("Object with name " + name + " is already bound in this context.");
      je.setExceptionType(JNDIException.NAME_ALREADY_BOUND);
      throw je;
    }
  }

  /**
   * Modifies data
   *
   * @param object Handle to the object
   * @param data Data to use
   */
  public synchronized void modify(JNDIHandle object, byte data[]) {
    Obj o = (Obj) objsByName.remove(object.getObjectName());

    if (o != null) {
      o.data = data;
      objsByName.put(o.name, o);
    }
  }

  /**
   * Renames data
   *
   * @param object Handle to the object
   * @param newName Name to use
   */
  public synchronized void rename(JNDIHandle object, String newName) {
    Obj o = (Obj) objsByName.remove(object.getObjectName());

    if (o != null) {
      String oldName = o.name;
      o.name = newName;
      objsByName.put(newName, o);
    }
  }

  /**
   * Deletes an object
   *
   * @param handle Handle to the object
   */
  public synchronized void deleteObject(JNDIHandle handle) {
    objsByName.remove(handle.getObjectName());
  }

  /**
   * Reads an object
   *
   * @param object Handle to the object
   * @return Data read
   */
  public byte[] readObject(JNDIHandle object) {
    Obj o = getObject(object);
    if (o != null) {
      return o.data;
    } else {
      return null;
    }
  }

  /**
   * Creates a link
   *
   * @param handleObject Handle to the object
   * @param linkid Link ID
   */
  public synchronized void createLink(JNDIHandle handleObject, String linkid) {
    Obj o = (Obj) objsByName.remove(handleObject.getObjectName());

    if (o != null) {
      o.linkid = linkid;
      objsByName.put(o.name, o);
    }
  }

  /**
   * Gets a linked container
   *
   * @param handle Handle to the object
   * @return Handle to the container linked
   */
  public JNDIHandle getLinkedContainer(JNDIHandle handle) {
    Obj o = getObject(handle);

    if (o != null && o.linkid != null) {
      return new JNDIHandleImpl(o.linkid, null);
    } else {
      return null;
    }
  }

  /**
   * Removes a link
   *
   * @param handle Handle to the object
   */
  public synchronized void removeLink(JNDIHandle handle) {
    Obj o = (Obj) objsByName.remove(handle.getObjectName());
    if (o != null) {
      o.linkid = null;
      objsByName.put(o.name, o);
    }
  }

  /**
   * Gets the name of an object
   *
   * @param handleObject Handle to the object
   * @return Name of the object
   */
  public String getNameOf(JNDIHandle handleObject) {
    Obj o = getObject(handleObject);
    if (o != null) {
      return o.getName();
    } else {
      return null;
    }
  }

  /**
   * Gets the clusterID of an object
   *
   * @param handleObject Handle to the object
   * @return clusterID of the object
   */
  public int getClIdOf(JNDIHandle handleObject) {
    Obj o = getObject(handleObject);
    if (o != null) {
      return o.getClusterID();
    } else {
      return 0;
    }
  }

  public short getTypeOf(JNDIHandle handleObject) {
    Obj o = getObject(handleObject);
    if (o != null) {
      return o.getType();
    } else {
      return -1;
    }
  }

  public synchronized void setTypeOf(JNDIHandle object, short type) {
    Obj o = (Obj) objsByName.remove(object.getObjectName());

    if (o != null) {
      o.type = type;
      objsByName.put(o.name, o);
    }
  }

  /**
   * Gets all object using name of the container
   *
   * @return Enumeration of handles to the objects
   */
  public synchronized JNDIHandleEnumeration getAllByName() {
    ConcurrentArrayObject oids = new ConcurrentArrayObject();

    Enumeration keys = objsByName.keys();
    while (keys.hasMoreElements()) {
      oids.add(keys.nextElement());
    }

    return new JNDIHandleEnumerationImpl(oids, this.cName);
  }

  /**
   * Prints the data
   */
  protected void printAll() {
    Object[] objects = objsByName.getAllValues();

    for (int i = 0; i < objects.length; i++) {
      Obj o = (Obj) objects[i];
      System.out.print(o.name + " "); //$JL-SYS_OUT_ERR$
    }
  }

}
