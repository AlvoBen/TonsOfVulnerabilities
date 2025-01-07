package com.sap.engine.services.iiop.csiv2.CSIIOP;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class TLS_SEC_TRANSHelper {

  public TLS_SEC_TRANSHelper() {

  }

  public static void insert(Any any, TLS_SEC_TRANS tls_sec_trans) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, tls_sec_trans);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static TLS_SEC_TRANS extract(Any any) {
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
          StructMember astructmember[] = new StructMember[3];
          TypeCode typecode1 = null;
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_ushort);
          typecode1 = ORB.init().create_alias_tc(AssociationOptionsHelper.id(), "AssociationOptions", typecode1);
          astructmember[0] = new StructMember("target_supports", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_ushort);
          typecode1 = ORB.init().create_alias_tc(AssociationOptionsHelper.id(), "AssociationOptions", typecode1);
          astructmember[1] = new StructMember("target_requires", typecode1, null);
          typecode1 = TransportAddressHelper.type();
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          typecode1 = ORB.init().create_alias_tc(TransportAddressListHelper.id(), "TransportAddressList", typecode1);
          astructmember[2] = new StructMember("addresses", typecode1, null);
          __typeCode = ORB.init().create_struct_tc(id(), "TLS_SEC_TRANS", astructmember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static TLS_SEC_TRANS read(InputStream inputstream) {
    TLS_SEC_TRANS tls_sec_trans = new TLS_SEC_TRANS();
    tls_sec_trans.target_supports = inputstream.read_ushort();
    tls_sec_trans.target_requires = inputstream.read_ushort();
    tls_sec_trans.addresses = TransportAddressListHelper.read(inputstream);
    return tls_sec_trans;
  }

  public static void write(OutputStream outputstream, TLS_SEC_TRANS tls_sec_trans) {
    outputstream.write_ushort(tls_sec_trans.target_supports);
    outputstream.write_ushort(tls_sec_trans.target_requires);
    TransportAddressListHelper.write(outputstream, tls_sec_trans.addresses);
  }

  private static String _id = "IDL:omg.org/CSIIOP/TLS_SEC_TRANS:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;

}

