package com.sap.engine.services.iiop.csiv2.CSI;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ContextIdHelper {

  public ContextIdHelper() {

  }

  public static void insert(Any any, long l) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, l);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static long extract(Any any) {
    return read(any.create_input_stream());
  }

  public static synchronized TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_ulonglong);
      //            __typeCode = ORB.init().create_alias_tc(id(), "ContextId", __typeCode);
    }

    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static long read(InputStream inputstream) {
    long l = 0L;
    l = inputstream.read_ulonglong();
    return l;
  }

  public static void write(OutputStream outputstream, long l) {
    outputstream.write_ulonglong(l);
  }

  private static String _id = "IDL:omg.org/CSI/ContextId:1.0";
  private static TypeCode __typeCode = null;

}

