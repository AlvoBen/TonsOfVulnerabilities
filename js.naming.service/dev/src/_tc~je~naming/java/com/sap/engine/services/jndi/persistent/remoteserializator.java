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

import java.io.*;

import javax.naming.NamingException;

import com.sap.engine.services.jndi.implclient.ClientContext;
import com.sap.engine.services.jndi.persistent.exceptions720.JNDIException;
import com.sap.engine.lib.util.ConcurrentHashMapObjectObject;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Remote serializator
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public class RemoteSerializator {

	private final static Location LOG_LOCATION = Location.getLocation(RemoteSerializator.class);

  /**
   * LROs' hashtable
   */
  public static ConcurrentHashMapObjectObject LROTable = new ConcurrentHashMapObjectObject();
  /**
   * Stores the serializator factory
   */
  public static SerializatorFactory serializatorFactory = null;

  /**
   * Convert Object to byte[]
   *
   * @param obj Object to serialize
   * @return Serialized object
   * @throws JNDIException If problems occured
   */
  public static byte[] toByteArray(Object obj, ClientContext cc) throws JNDIException {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      CPOOutputStream oos = new CPOOutputStream(baos, cc);
      //      CPOOutputStream oos = serializatorFactory.getNewCPOOutputStream(baos);
      oos.writeObject(obj);
      oos.flush();
      byte[] arr = baos.toByteArray();
      oos.close();
      baos.close();
      return arr;
    } catch (Exception e) {
      JNDIException je = new JNDIException("Error during object serialization.", e);
      throw je;
    }
  }

  /**
   * Convert byte[] to Object
   *
   * @param byteArr The byte array to deserialize
   * @return Deserialized byte array
   */
  public static Object toObject(byte byteArr[], int offset, int length, ClientContext cc) throws Exception {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(byteArr, offset, length);
      //      CPOInputStream ois = new CPOInputStream(bais);
      CPOInputStream ois = serializatorFactory.getNewCPOInputStream(bais, cc);
      Object obj = ois.readObject();
      ois.close();
      bais.close();

      return obj;

    } catch (Exception e) {
      // chek if the client is running server side or remote
      boolean returnUnsatisfiedRef = false;
      try {
        returnUnsatisfiedRef = (cc.getEnvironment().get("server") != null);
      } catch (NamingException ne) {
        returnUnsatisfiedRef = false;
        LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      }

      // if the client is a remote one there is no good reason to return UnsatisfiedReference it is almost impossible to narrow it properly
      if (returnUnsatisfiedRef) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.traceThrowableT(Severity.INFO, "UnsatisfiedReference will be returned as a result of the lookup operation. Reason: exception in the deserialization process.", e);
        }

        ByteArrayOutputStream ostr = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(ostr));

        return new UnsatisfiedReferenceImpl(getObjectArray(byteArr, offset, length), ostr.toString(), e.toString(), e);
      }
      throw e;
    } catch (NoClassDefFoundError er) {
      // chek if the client is running server side or remote
      boolean returnUnsatisfiedRef = false;
      try {
        returnUnsatisfiedRef = (cc.getEnvironment().get("server") != null);
      } catch (NamingException ne) {
        returnUnsatisfiedRef = false;
                LOG_LOCATION.traceThrowableT(Severity.PATH, "", ne);
      }

      // if the client is a remote one there is no good reason to return UnsatisfiedReference it is almost impossible to narrow it properly
      if (returnUnsatisfiedRef) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.traceThrowableT(Severity.INFO, "UnsatisfiedReference will be returned as a result of the lookup operation. Reason: NoClassDefFoundError in the deserialization process.", er);
        }

        ByteArrayOutputStream ostr = new ByteArrayOutputStream();
        er.printStackTrace(new PrintStream(ostr));

        return new UnsatisfiedReferenceImpl(getObjectArray(byteArr, offset, length), ostr.toString(), er.toString(), er);
      }
      throw er;
    }
  }

  private static byte[] getObjectArray(byte[] sourceArr, int offset, int length) {
    byte[] temp = new byte[length];
    System.arraycopy(sourceArr, offset, temp, 0, length);
    return temp;
  }

}

