package com.sap.engine.services.iiop.csiv2.CSIIOP;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServiceConfigurationSyntaxHelper {

  public ServiceConfigurationSyntaxHelper() {

  }

  public static void insert(Any any, int i) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, i);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static int extract(Any any) {
    return read(any.create_input_stream());
  }

  public static synchronized TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_ulong);
      __typeCode = ORB.init().create_alias_tc(id(), "ServiceConfigurationSyntax", __typeCode);
    }

    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static int read(InputStream inputstream) {
    int i = 0;
    i = inputstream.read_ulong();
    return i;
  }

  public static void write(OutputStream outputstream, int i) {
    outputstream.write_ulong(i);
  }

  private static String _id = "IDL:omg.org/CSIIOP/ServiceConfigurationSyntax:1.0";
  private static TypeCode __typeCode = null;

}

