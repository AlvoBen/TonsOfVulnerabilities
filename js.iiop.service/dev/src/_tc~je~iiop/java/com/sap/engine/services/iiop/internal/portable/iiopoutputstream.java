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
package com.sap.engine.services.iiop.internal.portable;

import com.sap.engine.services.iiop.CORBA.portable.CORBAOutputStream;
import com.sap.engine.services.iiop.internal.portable.cache.StreamCache;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.ValueBase;

import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author Georgy Stanev, Nikolai Neichev
 * @author  Vladimir Velinov
 * @version 4.0
 */
public class IIOPOutputStream extends CORBAOutputStream {

  public static final int INDIRECTION_TAG = 0xFFFFFFFF;
  public static final int NULL_TAG = 0x0;
  public static final String HELPER = "Helper";
  public static final String WRITE = "write";
  public static final String IDL_WSTRING = "IDL:omg.org/CORBA/WStringValue:1.0";

  /** Holds objects that are written to the stream */
  public StreamCache objectCache = new StreamCache();
  private ValueHandler vh = null;
  private boolean chunk = false;

  public IIOPOutputStream(org.omg.CORBA.ORB orb0) {
    super(orb0);
  }

  public org.omg.CORBA.portable.InputStream create_input_stream() {
    return new IIOPInputStream(orb, data, littleEndian);
  }

  public void write_value(java.io.Serializable value, Class clz) {
    write_value(value);
  }

  public void write_value(java.io.Serializable value) {
    if (value == null) {
      write_long(NULL_TAG);
      return;
    }

    int ofs;
    if ((ofs = objectCache.get(value)) > -1) { //object found, write a reference
      write_long(INDIRECTION_TAG);
      //refer to a preceeding location in GIOP message, all offsets are negative
      //As an example, this means that an offset of negative four (-4) is illegal,
      //because it is self-indirecting to its indirection marker.
      write_long(ofs - getPos());
      return;
    }
    boolean flag = chunk;
    if (inBlock) {
      endBlock();
    }

    if (vh == null) {
      vh = Util.createValueHandler();
    }
    if ((value instanceof IDLEntity) && !(value instanceof ValueBase) && !(value instanceof org.omg.CORBA.Object)) {
      Class valueClass = value.getClass();
      chunk = true;
      writeValueTag(true, null);
      objectCache.put(value, getPos() - 4);
      writeRepositoryId(vh.getRMIRepositoryID(valueClass));
      startBlock();
      endTag--;
      try {
        ClassLoader classloader = (valueClass != null) ? valueClass.getClassLoader() : null;
        Class class2 = Class.forName(valueClass.getName() + HELPER, true, classloader);
        Class aclass[] = {org.omg.CORBA.portable.OutputStream.class, valueClass};
        Method method = class2.getDeclaredMethod(WRITE, aclass);
        java.lang.Object aobj[] = {this, value};
        method.invoke(null, aobj);
      } catch (Exception e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IIOPOutputStream.write_value(Serializable)", LoggerConfigurator.exceptionTrace(e));
        }
        throw new MARSHAL(e.getMessage());
      }
      endBlock();
      writeEndTag(true);
    } else if (value instanceof String) {
      if (chunk) {
        writeValueTag(true, null);
        objectCache.put(value, getPos() - 4); //new object, put it into the cache
        writeRepositoryId(IDL_WSTRING);
        startBlock();
        endTag--;
        write_wstring((String) value);
        endBlock();
        writeEndTag(true);
      } else {
        writeValueTag(false, null);
        objectCache.put(value, getPos() - 4); //new object, put it into the cache
        writeRepositoryId(IDL_WSTRING);
        endTag--;
        write_wstring((String) value);
        writeEndTag(false);
      }
    } else if (value instanceof Class) {
      writeValueClass(vh.getRMIRepositoryID((Class) value), (Class) value);
    } else {
      Serializable old = value;
      value = vh.writeReplace(value);
      if (value != old) {
        if (value == null) {
          write_long(NULL_TAG);
          return;
        }
        if ((ofs = objectCache.get(value)) > -1) { //object found, write a reference
          write_long(INDIRECTION_TAG);
          //refer to a preceeding location in GIOP message, all offsets are negative
          //As an example, this means that an offset of negative four (-4) is illegal,
          //because it is self-indirecting to its indirection marker.
          write_long(ofs - getPos());
          return;
        }
      }
      if (chunk || vh.isCustomMarshaled(value.getClass())) {
        chunk = true;
        writeValueTag(true, null);
        objectCache.put(value, getPos() - 4); //new object, put it into the cache
        writeRepositoryId(vh.getRMIRepositoryID(value.getClass()));
        endTag--;
        startBlock();
        vh.writeValue(this, value);
        endBlock();
        writeEndTag(true);
      } else {
        writeValueTag(false, null);
        objectCache.put(value, getPos() - 4); //new object, put it into the cache
        writeRepositoryId(vh.getRMIRepositoryID(value.getClass()));
        vh.writeValue(this, value);
        endTag--;
        writeEndTag(false);
      }
    }
    chunk = flag;
    if (chunk) {
      startBlock();
    }
  }

  public void write_abstract_interface(java.lang.Object obj) {
    boolean flag = false;
    org.omg.CORBA.Object obj1 = null;
    if (obj != null && (obj instanceof org.omg.CORBA.Object)) {
      obj1 = (org.omg.CORBA.Object) obj;
      flag = true;
    }
    write_boolean(flag);
    if (flag) {
      write_Object(obj1);
    } else {
      try {
        write_value((Serializable) obj);
      } catch (ClassCastException ccex) {
        if (obj instanceof Serializable) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IIOPOutputStream.write_value(Serializable)", LoggerConfigurator.exceptionTrace(ccex));
          }
          throw ccex;
        }
      }
    }
  }

  public void writeValueClass(String repId, Class clazz) {
    if (chunk) {
      writeValueTag(true, null);
      if (repId != null) {
        writeRepositoryId(vh.getRMIRepositoryID(javax.rmi.CORBA.ClassDesc.class));
        startBlock();
        endTag--;
        write_value(javax.rmi.CORBA.Util.getCodebase(clazz));
        write_value(repId);
      } else {
        //        javax.rmi.CORBA.ClassDesc classDesc = new javax.rmi.CORBA.ClassDesc();
        //        write_string(vh.getRMIRepositoryID(javax.rmi.CORBA.ClassDesc.class));
      }
      endBlock(); //--------
      writeEndTag(true);
    } else {
      writeValueTag(false, null);
      if (repId != null) {
        writeRepositoryId(vh.getRMIRepositoryID(javax.rmi.CORBA.ClassDesc.class));
        endTag--;
        write_value(javax.rmi.CORBA.Util.getCodebase(clazz));
        write_value(repId);
      } else {
        //        javax.rmi.CORBA.ClassDesc classDesc = new javax.rmi.CORBA.ClassDesc();
        //        writeRepositoryId(vh.getRMIRepositoryID(javax.rmi.CORBA.ClassDesc.class));
      }
      writeEndTag(false);
    }
  }

}

