package com.sap.engine.services.iiop.csiv2.CSIIOP;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class TransportAddressListHelper {

  public TransportAddressListHelper() {

  }

  public static void insert(Any any, TransportAddress atransportaddress[]) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, atransportaddress);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static TransportAddress[] extract(Any any) {
    return read(any.create_input_stream());
  }

  public static synchronized TypeCode type() {
    if (__typeCode == null) {
      __typeCode = TransportAddressHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "TransportAddressList", __typeCode);
    }

    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static TransportAddress[] read(InputStream inputstream) {
    TransportAddress atransportaddress[] = null;
    int i = inputstream.read_long();
    atransportaddress = new TransportAddress[i];
    for (int j = 0; j < atransportaddress.length; j++) {
      atransportaddress[j] = TransportAddressHelper.read(inputstream); 
    }
    return atransportaddress;
  }

  public static void write(OutputStream outputstream, TransportAddress atransportaddress[]) {
    outputstream.write_long(atransportaddress.length);
    for (int i = 0; i < atransportaddress.length; i++) {
      TransportAddressHelper.write(outputstream, atransportaddress[i]); 
    }
  }

  private static String _id = "IDL:omg.org/CSIIOP/TransportAddressList:1.0";
  private static TypeCode __typeCode = null;

}

