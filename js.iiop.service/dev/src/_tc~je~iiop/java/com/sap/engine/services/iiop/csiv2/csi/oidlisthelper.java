﻿package com.sap.engine.services.iiop.csiv2.CSI;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class OIDListHelper {

  public OIDListHelper() {

  }

  public static void insert(Any any, byte abyte0[][]) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, abyte0);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static byte[][] extract(Any any) {
    return read(any.create_input_stream());
  }

  public static synchronized TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_octet);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      //            __typeCode = ORB.init().create_alias_tc(OIDHelper.id(), "OID", __typeCode);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      //            __typeCode = ORB.init().create_alias_tc(id(), "OIDList", __typeCode);
    }

    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static byte[][] read(InputStream inputstream) {
    byte abyte0[][] = null;
    int i = inputstream.read_long();
    abyte0 = new byte[i][];
    for (int j = 0; j < abyte0.length; j++) {
      abyte0[j] = OIDHelper.read(inputstream); 
    }
    return abyte0;
  }

  public static void write(OutputStream outputstream, byte abyte0[][]) {
    outputstream.write_long(abyte0.length);
    for (int i = 0; i < abyte0.length; i++) {
      OIDHelper.write(outputstream, abyte0[i]); 
    }
  }

  private static String _id = "IDL:omg.org/CSI/OIDList:1.0";
  private static TypeCode __typeCode = null;

}

