package com.sap.engine.services.iiop.csiv2.CSIIOP;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class AssociationOptionsHelper {

  public AssociationOptionsHelper() {

  }

  public static void insert(Any any, short word0) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, word0);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static short extract(Any any) {
    return read(any.create_input_stream());
  }

  public static synchronized TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_ushort);
      __typeCode = ORB.init().create_alias_tc(id(), "AssociationOptions", __typeCode);
    }

    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static short read(InputStream inputstream) {
    short word0 = 0;
    word0 = inputstream.read_ushort();
    return word0;
  }

  public static void write(OutputStream outputstream, short word0) {
    outputstream.write_ushort(word0);
  }

  private static String _id = "IDL:omg.org/CSIIOP/AssociationOptions:1.0";
  private static TypeCode __typeCode = null;

}

