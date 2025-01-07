package com.sap.engine.services.iiop.csiv2.CSIIOP;

import com.sap.engine.services.iiop.csiv2.CSI.GSS_NT_ExportedNameHelper;
import com.sap.engine.services.iiop.csiv2.CSI.OIDHelper;
import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import com.sap.engine.interfaces.csiv2.*;

public abstract class AS_ContextSecHelper {

  public AS_ContextSecHelper() {

  }

  public static void insert(Any any, AS_ContextSec as_contextsec) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, as_contextsec);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static AS_ContextSec extract(Any any) {
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
          StructMember astructmember[] = new StructMember[4];
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
          astructmember[2] = new StructMember("client_authentication_mech", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_octet);
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          typecode1 = ORB.init().create_alias_tc(GSS_NT_ExportedNameHelper.id(), "GSS_NT_ExportedName", typecode1);
          astructmember[3] = new StructMember("target_name", typecode1, null);
          __typeCode = ORB.init().create_struct_tc(id(), "AS_ContextSec", astructmember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static AS_ContextSec read(InputStream inputstream) {
    AS_ContextSec as_contextsec = new AS_ContextSec();
    as_contextsec.target_supports = inputstream.read_ushort();
    as_contextsec.target_requires = inputstream.read_ushort();
    as_contextsec.client_authentication_mech = OIDHelper.read(inputstream);
    as_contextsec.target_name = GSS_NT_ExportedNameHelper.read(inputstream);
    return as_contextsec;
  }

  public static void write(OutputStream outputstream, AS_ContextSec as_contextsec) {
    outputstream.write_ushort(as_contextsec.target_supports);
    outputstream.write_ushort(as_contextsec.target_requires);
    OIDHelper.write(outputstream, as_contextsec.client_authentication_mech);
    GSS_NT_ExportedNameHelper.write(outputstream, as_contextsec.target_name);
  }

  private static String _id = "IDL:omg.org/CSIIOP/AS_ContextSec:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;

}

