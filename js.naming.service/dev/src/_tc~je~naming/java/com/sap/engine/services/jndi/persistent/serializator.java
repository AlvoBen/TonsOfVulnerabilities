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

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.engine.services.jndi.persistent.exceptions720.JNDIException;

import java.io.*;

/**
 * Serializator
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public class Serializator {

	private final static Location LOG_LOCATION = Location.getLocation(Serializator.class);

  /**
   * Convert Object to byte[]
   *
   * @param obj Object to serialize
   * @return Serialized object
   * @throws JNDIException If problems occured
   */
  public static byte[] toByteArray(Object obj) throws javax.naming.NamingException {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(obj);
      oos.flush();
      byte[] arr = baos.toByteArray();
      oos.close();
      baos.close();
      return arr;
    } catch (Exception e) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      throw new JNDIException("Error during object serialization.", e);
    }
  }

  /**
   * Convert byte[] to Object
   *
   * @param byteArr The byte array to deserialize
   * @return Deserialized byte array
   * @throws JNDIException If problems occured
   */
  public static Object toObject(byte byteArr[]) throws javax.naming.NamingException {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(byteArr);
      ObjectInputStream ois = new ObjectInputStream(bais);
      Object obj = ois.readObject();
      ois.close();
      bais.close();
      return obj;
    } catch (Exception e) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      throw new JNDIException("Error during object serialization.", e);
    }
  }

  /**
   * Convert byte[] to Object
   *
   * @param byteArr The byte array to deserialize
   * @return Deserialized byte array
   * @throws JNDIException If problems occured
   */
  public static Object toObject(byte byteArr[], int offset, int length, boolean remote) throws javax.naming.NamingException {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(byteArr, offset, length);
      ObjectInputStream ois = new ObjectInputStream(bais);
      Object obj = ois.readObject();
      ois.close();
      bais.close();
      return obj;
    } catch (Exception e) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      throw new JNDIException("Error during object serialization.", e);
    }
  }

}

