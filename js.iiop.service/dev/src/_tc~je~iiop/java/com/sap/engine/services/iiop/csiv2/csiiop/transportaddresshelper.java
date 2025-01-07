package com.sap.engine.services.iiop.csiv2.CSIIOP;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class TransportAddressHelper {

  public TransportAddressHelper() {

  }

  public static void insert(Any any, TransportAddress transportaddress) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, transportaddress);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static TransportAddress extract(Any any) {
    return read(any.create_input_stream());
  }

  public static synchronized TypeCode type() {
    if (__typeCode == null) {
      synchronized (org.omg.CORBA.TypeCode.class) {
        if (__typeCode == null) {
          if (__active) {
            TypeCode typecode = ORB.init().create_recursive_tc(_id);
            return typecode;
          }

          __active = true;
          StructMember astructmember[] = new StructMember[2];
          TypeCode typecode1 = null;
          typecode1 = ORB.init().create_string_tc(0);
          astructmember[0] = new StructMember("host_name", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_ushort);
          astructmember[1] = new StructMember("port", typecode1, null);
          __typeCode = ORB.init().create_struct_tc(id(), "TransportAddress", astructmember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static TransportAddress read(InputStream inputstream) {
    TransportAddress transportaddress = new TransportAddress();
    transportaddress.host_name = inputstream.read_string();
    transportaddress.port = (inputstream.read_ushort() << 0) & 0x0000FFFF;
    return transportaddress;
  }

  public static void write(OutputStream outputstream, TransportAddress transportaddress) {
    outputstream.write_string(transportaddress.host_name);
    outputstream.write_ushort((short) (transportaddress.port & 0x0000FFFF));
  }

  private static String _id = "IDL:omg.org/CSIIOP/TransportAddress:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;

}

