/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.rmic.iiop.util;


import com.sap.engine.rmic.log.RMICLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * @author Ralitsa Bozhkova
 * @version 4.0
 */
public final class RepositoryIDDescriptor {

  public long hashCode;
  public long sUID;
  public static RepositoryIDCacheTable repIDTable = new RepositoryIDCacheTable();
//  private RepositoryIDDescriptor repIDdescriptor;

  public static final RepositoryIDDescriptor get(Class clas) {
    synchronized (repIDTable) {
      RepositoryIDDescriptor repID = (RepositoryIDDescriptor) repIDTable.get(clas);

      if (repID == null) {
        repID = new RepositoryIDDescriptor();
        repID.computeHashCode(clas);
        repID.getSerialVersionUID(clas);
        repIDTable.put(clas, repID);
      }

      RepositoryIDDescriptor repIDprocessed = repID;
      return repIDprocessed;
    }
  }

  /**
   * This method computes the hash code of the class for the
   * RMI hashed format as specified in the CORBA Specification.
   *
   * @param clas - the class for which the RepositoryID is created
   */
  private final void computeHashCode(Class clas) {
    long hash = 0L;
    try {
      ByteArrayOutputStream bs = new ByteArrayOutputStream(512);

      // For classes that do not implement java.io.Serializable, and for interfaces,
      // the hash code is always zero, and the RepositoryID does not
      // contain a serial version UID.
      if (clas.isInterface() || !(java.io.Serializable.class.isAssignableFrom(clas))) {
        hashCode = 0L;
        return;
      } // if

      //For classes that implement java.io.Externalizable, the hash code is
      // always the 64-bit value 1.
      if (java.io.Externalizable.class.isAssignableFrom(clas)) {
        hashCode = 1L;
        return;
      } // if

      // An instance of java.lang.DataOutputStream is used to convert
      // primitive data types to a sequence of bytes.
      MessageDigest digest = MessageDigest.getInstance("SHA");
      DigestOutputStream digestStream = new DigestOutputStream(bs, digest);
      java.io.DataOutputStream stream = new java.io.DataOutputStream(digestStream);
      // The hash code of the superclass, written as a 64-bit long.
      Class parent = clas.getSuperclass();
      RepositoryIDDescriptor descriptor = get(parent);

      if (parent != null && parent != java.lang.Object.class) {
        stream.writeLong(descriptor.hashCode);
      }

      // The value 1 if the class has no writeObject method, or
      // the value 2 if the class has a writeObject method,
      // written as a 32-bit integer.
      try {
        Class[] writeObjectArgs = {java.io.ObjectOutputStream.class};
        Method writeMethod = clas.getDeclaredMethod("writeObject", writeObjectArgs);
        int i = writeMethod.getModifiers();

        if (!Modifier.isPrivate(i) || Modifier.isStatic(i)) {
          writeMethod = null;
        }

        stream.writeInt(writeMethod != null ? 2 : 1);
      } catch (NoSuchMethodException ex) {//$JL-EXC$
        stream.writeInt(1);
      }
      // For each field of the class that is mapped to IDL sorted
      // lexicographically by Java field name, in increasing order:
      Field[] fields = clas.getDeclaredFields();
      Arrays.sort(fields, repIDTable.comparator);

      for (int i = 0; i < fields.length; i++) {
        Field afield = fields[i];
        int j = afield.getModifiers();

        if (!Modifier.isTransient(j) && !Modifier.isStatic(j)) {
          // Java field name, in UTF encoding
          stream.writeUTF(afield.getName());
          // field descriptor, as defined by the Java Virtual Machine
          // Specification, in UTF encoding
          stream.writeUTF(RepositoryID.signature(afield.getType()));
        }
      }// for

      stream.flush();
      // The National Institute of Standards and Technology (NIST)
      // Secure Hash Algorithm (SHA-1) is executed on the stream of
      // bytes produced by DataOutputStream. The hash code is assembled
      // from the first 8 bytes of this array
      byte abyte[] = digest.digest(); // bs.toByteArray());
      int k = Math.min(8, abyte.length);

      for (int i = k; i > 0; i--) {
        hash += (long) (abyte[i] & 0xff) << (i * 8);
      } // for
    } catch (IOException ioexception) {//$JL-EXC$
      hash = -1L;
    } catch (Exception e) {//$JL-EXC$
      RMICLogger.throwing(e);
    }
    hashCode = hash;
  }

  /**
   * This method gets the actual SerialVersionUID of the class
   *
   * @param clas - the class for which the RepositoryID is created
   */
  private final void getSerialVersionUID(Class clas) {
    long id = 0L;
    try {
      ObjectStreamClass osc = ObjectStreamClass.lookup(clas);

      if (osc != null) {
        id = osc.getSerialVersionUID();
      }
    } catch (Exception e) { //$JL-EXC$
      RMICLogger.throwing(e);
      id = -1L;
    }
    sUID = id;
  }

}

