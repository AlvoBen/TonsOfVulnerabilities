/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.CORBA.util;

import java.util.Hashtable;

/**
 * This class is used for converting CORBA exceptions from/to IDL format to/from standart
 * java class format - IDL:omg.org/CORBA/BAD_PARAM <---> org.omg.CORBA.BAD_PARAM.
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public class ExceptionUtility {

  static Hashtable idl2exception = new Hashtable();
  static Hashtable exception2idl = new Hashtable();

  static {
    idl2exception.put("IDL:omg.org/CORBA/BAD_CONTEXT:1.0", "org.omg.CORBA.BAD_CONTEXT");
    idl2exception.put("IDL:omg.org/CORBA/BAD_INV_ORDER:1.0", "org.omg.CORBA.BAD_INV_ORDER");
    idl2exception.put("IDL:omg.org/CORBA/BAD_OPERATION:1.0", "org.omg.CORBA.BAD_OPERATION");
    idl2exception.put("IDL:omg.org/CORBA/BAD_PARAM:1.0", "org.omg.CORBA.BAD_PARAM");
    idl2exception.put("IDL:omg.org/CORBA/BAD_TYPECODE:1.0", "org.omg.CORBA.BAD_TYPECODE");
    idl2exception.put("IDL:omg.org/CORBA/COMM_FAILURE:1.0", "org.omg.CORBA.COMM_FAILURE");
    idl2exception.put("IDL:omg.org/CORBA/DATA_CONVERSION:1.0", "org.omg.CORBA.DATA_CONVERSION");
    idl2exception.put("IDL:omg.org/CORBA/IMP_LIMIT:1.0", "org.omg.CORBA.IMP_LIMIT");
    idl2exception.put("IDL:omg.org/CORBA/INTF_REPOS:1.0", "org.omg.CORBA.INTF_REPOS");
    idl2exception.put("IDL:omg.org/CORBA/INTERNAL:1.0", "org.omg.CORBA.INTERNAL");
    idl2exception.put("IDL:omg.org/CORBA/INV_FLAG:1.0", "org.omg.CORBA.INV_FLAG");
    idl2exception.put("IDL:omg.org/CORBA/INV_IDENT:1.0", "org.omg.CORBA.INV_IDENT");
    idl2exception.put("IDL:omg.org/CORBA/INV_OBJREF:1.0", "org.omg.CORBA.INV_OBJREF");
    idl2exception.put("IDL:omg.org/CORBA/MARSHAL:1.0", "org.omg.CORBA.MARSHAL");
    idl2exception.put("IDL:omg.org/CORBA/NO_MEMORY:1.0", "org.omg.CORBA.NO_MEMORY");
    idl2exception.put("IDL:omg.org/CORBA/FREE_MEM:1.0", "org.omg.CORBA.FREE_MEM");
    idl2exception.put("IDL:omg.org/CORBA/NO_IMPLEMENT:1.0", "org.omg.CORBA.NO_IMPLEMENT");
    idl2exception.put("IDL:omg.org/CORBA/NO_PERMISSION:1.0", "org.omg.CORBA.NO_PERMISSION");
    idl2exception.put("IDL:omg.org/CORBA/NO_RESOURCES:1.0", "org.omg.CORBA.NO_RESOURCES");
    idl2exception.put("IDL:omg.org/CORBA/NO_RESPONSE:1.0", "org.omg.CORBA.NO_RESPONSE");
    idl2exception.put("IDL:omg.org/CORBA/OBJ_ADAPTER:1.0", "org.omg.CORBA.OBJ_ADAPTER");
    idl2exception.put("IDL:omg.org/CORBA/INITIALIZE:1.0", "org.omg.CORBA.INITIALIZE");
    idl2exception.put("IDL:omg.org/CORBA/PERSIST_STORE:1.0", "org.omg.CORBA.PERSIST_STORE");
    idl2exception.put("IDL:omg.org/CORBA/TRANSIENT:1.0", "org.omg.CORBA.TRANSIENT");
    idl2exception.put("IDL:omg.org/CORBA/UNKNOWN:1.0", "org.omg.CORBA.UNKNOWN");
    idl2exception.put("IDL:omg.org/CORBA/OBJECT_NOT_EXIST:1.0", "org.omg.CORBA.OBJECT_NOT_EXIST");
    idl2exception.put("IDL:omg.org/CORBA/INVALID_TRANSACTION:1.0", "org.omg.CORBA.INVALID_TRANSACTION");
    idl2exception.put("IDL:omg.org/CORBA/TRANSACTION_REQUIRED:1.0", "org.omg.CORBA.TRANSACTION_REQUIRED");
    idl2exception.put("IDL:omg.org/CORBA/TRANSACTION_ROLLEDBACK:1.0", "org.omg.CORBA.TRANSACTION_ROLLEDBACK");
    idl2exception.put("IDL:omg.org/CORBA/INV_POLICY:1.0", "org.omg.CORBA.INV_POLICY");
    /*
     *
     */
    exception2idl.put("org.omg.CORBA.BAD_CONTEXT", "IDL:omg.org/CORBA/BAD_CONTEXT:1.0");
    exception2idl.put("org.omg.CORBA.BAD_INV_ORDER", "IDL:omg.org/CORBA/BAD_INV_ORDER:1.0");
    exception2idl.put("org.omg.CORBA.BAD_OPERATION", "IDL:omg.org/CORBA/BAD_OPERATION:1.0");
    exception2idl.put("org.omg.CORBA.BAD_PARAM", "IDL:omg.org/CORBA/BAD_PARAM:1.0");
    exception2idl.put("org.omg.CORBA.BAD_TYPECODE", "IDL:omg.org/CORBA/BAD_TYPECODE:1.0");
    exception2idl.put("org.omg.CORBA.COMM_FAILURE", "IDL:omg.org/CORBA/COMM_FAILURE:1.0");
    exception2idl.put("org.omg.CORBA.DATA_CONVERSION", "IDL:omg.org/CORBA/DATA_CONVERSION:1.0");
    exception2idl.put("org.omg.CORBA.IMP_LIMIT", "IDL:omg.org/CORBA/IMP_LIMIT:1.0");
    exception2idl.put("org.omg.CORBA.INTF_REPOS", "IDL:omg.org/CORBA/INTF_REPOS:1.0");
    exception2idl.put("org.omg.CORBA.INTERNAL", "IDL:omg.org/CORBA/INTERNAL:1.0");
    exception2idl.put("org.omg.CORBA.INV_FLAG", "IDL:omg.org/CORBA/INV_FLAG:1.0");
    exception2idl.put("org.omg.CORBA.INV_IDENT", "IDL:omg.org/CORBA/INV_IDENT:1.0");
    exception2idl.put("org.omg.CORBA.INV_OBJREF", "IDL:omg.org/CORBA/INV_OBJREF:1.0");
    exception2idl.put("org.omg.CORBA.MARSHAL", "IDL:omg.org/CORBA/MARSHAL:1.0");
    exception2idl.put("org.omg.CORBA.NO_MEMORY", "IDL:omg.org/CORBA/NO_MEMORY:1.0");
    exception2idl.put("org.omg.CORBA.FREE_MEM", "IDL:omg.org/CORBA/FREE_MEM:1.0");
    exception2idl.put("org.omg.CORBA.NO_IMPLEMENT", "IDL:omg.org/CORBA/NO_IMPLEMENT:1.0");
    exception2idl.put("org.omg.CORBA.NO_PERMISSION", "IDL:omg.org/CORBA/NO_PERMISSION:1.0");
    exception2idl.put("org.omg.CORBA.NO_RESOURCES", "IDL:omg.org/CORBA/NO_RESOURCES:1.0");
    exception2idl.put("org.omg.CORBA.NO_RESPONSE", "IDL:omg.org/CORBA/NO_RESPONSE:1.0");
    exception2idl.put("org.omg.CORBA.OBJ_ADAPTER", "IDL:omg.org/CORBA/OBJ_ADAPTER:1.0");
    exception2idl.put("org.omg.CORBA.INITIALIZE", "IDL:omg.org/CORBA/INITIALIZE:1.0");
    exception2idl.put("org.omg.CORBA.PERSIST_STORE", "IDL:omg.org/CORBA/PERSIST_STORE:1.0");
    exception2idl.put("org.omg.CORBA.TRANSIENT", "IDL:omg.org/CORBA/TRANSIENT:1.0");
    exception2idl.put("org.omg.CORBA.UNKNOWN", "IDL:omg.org/CORBA/UNKNOWN:1.0");
    exception2idl.put("org.omg.CORBA.OBJECT_NOT_EXIST", "IDL:omg.org/CORBA/OBJECT_NOT_EXIST:1.0");
    exception2idl.put("org.omg.CORBA.INVALID_TRANSACTION", "IDL:omg.org/CORBA/INVALID_TRANSACTION:1.0");
    exception2idl.put("org.omg.CORBA.TRANSACTION_REQUIRED", "IDL:omg.org/CORBA/TRANSACTION_REQUIRED:1.0");
    exception2idl.put("org.omg.CORBA.TRANSACTION_ROLLEDBACK", "IDL:omg.org/CORBA/TRANSACTION_ROLLEDBACK:1.0");
    exception2idl.put("org.omg.CORBA.INV_POLICY", "IDL:omg.org/CORBA/INV_POLICY:1.0");
  }

  public static boolean isSystemException(String id) {
    String s = (String) idl2exception.get(id);

    if (s == null) {
      return false;
    }

    return true;
  }

  public static String getClassName(String id) {
    String s = null;
    s = (String) idl2exception.get(id);

    if (s == null) {
      s = "org.omg.CORBA.UNKNOWN";
    }

    return s;
  }

  public static String getIDLName(String ex) {
    String s = null;
    s = (String) exception2idl.get(ex);

    if (s == null) {
      s = "IDL:org.omg/CORBA.UNKNOWN:1.0";
    }

    return s;
  }

}// 

