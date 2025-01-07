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
package com.sap.engine.services.jndi.persistent;


/**
 * JNDI's Handle base class
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public interface JNDIHandle extends java.io.Serializable {

  static final long serialVersionUID = -5063935607322793140L;

  /**
   * Returns container's id
   *
   * @return Container's id
   */
  public String getContainerName();

  /**
   * Returns object's id
   *
   * @return Object's id
   */
  public String getObjectName();

  /**
   * Returns a JNDIHandle deserialized from byte array
   *
   * @param data Byte array to be deserialized
   * @return The deserialized array
   * @throws NamingException If problems encountered
   */
  public JNDIHandle read(byte[] data) throws javax.naming.NamingException;


  /**
   * Returns a serialized form from a handle
   *
   * @param j Handle to serialize
   * @return The serialized handle
   * @throws NamingException If problems encountered
   */
  public byte[] write(JNDIHandle j) throws javax.naming.NamingException;

  /**
   *  Calculates a hash code based on id-s
   *
   * @return Hashcode
   */
//  public int hashCode();

}

