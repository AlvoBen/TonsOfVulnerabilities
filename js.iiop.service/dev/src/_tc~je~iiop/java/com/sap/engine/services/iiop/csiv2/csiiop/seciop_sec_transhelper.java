package com.sap.engine.services.iiop.csiv2.CSIIOP;

import com.sap.engine.services.iiop.csiv2.CSI.GSS_NT_ExportedNameHelper;
import com.sap.engine.services.iiop.csiv2.CSI.OIDHelper;
import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class SECIOP_SEC_TRANSHelper {

  public SECIOP_SEC_TRANSHelper() {

  }

  public static void insert(Any any, SECIOP_SEC_TRANS seciop_sec_trans) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, seciop_sec_trans);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static SECIOP_SEC_TRANS extract(Any any) {
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
          StructMember astructmember[] = new StructMember[5];
          TypeCode typecode1 = null;
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_ushort);
          typecode1 = ORB.init().create_alias_tc(AssociationOptionsHelper.id(), "AssociationOptions", typecode1);
          astructmember[0] = new StructMember("target_supports", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_ushort);
          typecode1 = ORB.init().create_alias_tc(AssociationOptionsHelper.id(), "AssociationOptions", typecode1);
          astructmember[1] = new StructMember("target_requires", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_octet);
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          typecode1 = ORB.init().create_alias_tc(OIDHelper.id(), "OID", typecode1);
          astructmember[2] = new StructMember("mech_oid", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_octet);
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          typecode1 = ORB.init().create_alias_tc(GSS_NT_ExportedNameHelper.id(), "GSS_NT_ExportedName", typecode1);
          astructmember[3] = new StructMember("target_name", typecode1, null);
          typecode1 = TransportAddressHelper.type();
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          typecode1 = ORB.init().create_alias_tc(TransportAddressListHelper.id(), "TransportAddressList", typecode1);
          astructmember[4] = new StructMember("addresses", typecode1, null);
          __typeCode = ORB.init().create_struct_tc(id(), "SECIOP_SEC_TRANS", astructmember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static SECIOP_SEC_TRANS read(InputStream inputstream) {
    SECIOP_SEC_TRANS seciop_sec_trans = new SECIOP_SEC_TRANS();
    seciop_sec_trans.target_supports = inputstream.read_ushort();
    seciop_sec_trans.target_requires = inputstream.read_ushort();
    seciop_sec_trans.mech_oid = OIDHelper.read(inputstream);
    seciop_sec_trans.target_name = GSS_NT_ExportedNameHelper.read(inputstream);
    seciop_sec_trans.addresses = TransportAddressListHelper.read(inputstream);
    return seciop_sec_trans;
  }

  public static void write(OutputStream outputstream, SECIOP_SEC_TRANS seciop_sec_trans) {
    outputstream.write_ushort(seciop_sec_trans.target_supports);
    outputstream.write_ushort(seciop_sec_trans.target_requires);
    OIDHelper.write(outputstream, seciop_sec_trans.mech_oid);
    GSS_NT_ExportedNameHelper.write(outputstream, seciop_sec_trans.target_name);
    TransportAddressListHelper.write(outputstream, seciop_sec_trans.addresses);
  }

  private static String _id = "IDL:omg.org/CSIIOP/SECIOP_SEC_TRANS:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;

}

