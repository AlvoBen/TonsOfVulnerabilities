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

import com.sap.engine.services.jndi.persistent.JNDIHandle;
import com.sap.engine.services.jndi.persistent.Serializator;

/**
 * Implements JNDI Handle
 *
 * @author Petio Petev, Panayot Dobrikov
 * @version 4.00
 */
public class JNDIHandleImpl implements JNDIHandle, java.io.Serializable {

  String cName;
  String oName;
  static final long serialVersionUID = 4523052135409476957L;

  /**
   * Constructor
   *
   * @param cName Container ID
   * @param oName Object ID
   */
  public JNDIHandleImpl(String cName, String oName) {
    this.cName = cName;
    this.oName = oName;
  }

  /**
   * Gets the container ID
   *
   * @return The container ID
   */
  public String getContainerName() {
    return cName;
  }

  /**
   * Gets the object ID
   *
   * @return The object ID
   */
  public String getObjectName() {
    return oName;
  }

  /**
   * Extracts handle from byte array
   *
   * @param data Data to work with
   * @return The handle requested
   * @throws NamingException Thrown if a problem occures
   */
  public JNDIHandle read(byte[] data) throws javax.naming.NamingException {
    return (JNDIHandle) Serializator.toObject(data);
  }

  /**
   * Writes a handle to byte array
   *
   * @param j Handle to use
   * @return Byte array
   * @throws NamingException Thrown if a problem occures
   */
  public byte[] write(JNDIHandle j) throws javax.naming.NamingException {
    return Serializator.toByteArray(j);
  }

  /**
   * Gets the hash code
   *
   * @return Hash code requested
   */
    public int hashCode() {
      return super.hashCode();
    }
  /**
   * Check relation between object and handle
   *
   * @param obj Obejct to work with
   * @return "true" if equal
   */
  public boolean equals(Object obj) {
    return ((((JNDIHandle) obj).getContainerName().equals(cName)) & (((JNDIHandle) obj).getObjectName().equals(oName)));
  }

  public String toString() {
    return "JNDIHandle[CNAME = " + cName + "][ONAME = " + oName + "]";
  }

}
