package com.sap.engine.services.iiop.csiv2.CSIIOP;

import com.sap.engine.services.iiop.csiv2.CSI.*;
import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import com.sap.engine.interfaces.csiv2.*;

public abstract class SAS_ContextSecHelper {

  public SAS_ContextSecHelper() {

  }

  public static void insert(Any any, SAS_ContextSec sas_contextsec) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, sas_contextsec);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static SAS_ContextSec extract(Any any) {
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
          typecode1 = ServiceConfigurationHelper.type();
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          typecode1 = ORB.init().create_alias_tc(ServiceConfigurationListHelper.id(), "ServiceConfigurationList", typecode1);
          astructmember[2] = new StructMember("privilege_authorities", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_octet);
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          typecode1 = ORB.init().create_alias_tc(OIDHelper.id(), "OID", typecode1);
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          typecode1 = ORB.init().create_alias_tc(OIDListHelper.id(), "OIDList", typecode1);
          astructmember[3] = new StructMember("supported_naming_mechanisms", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_ulong);
          typecode1 = ORB.init().create_alias_tc(IdentityTokenTypeHelper.id(), "IdentityTokenType", typecode1);
          astructmember[4] = new StructMember("supported_identity_types", typecode1, null);
          __typeCode = ORB.init().create_struct_tc(id(), "SAS_ContextSec", astructmember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static SAS_ContextSec read(InputStream inputstream) {
    SAS_ContextSec sas_contextsec = new SAS_ContextSec();
    sas_contextsec.target_supports = inputstream.read_ushort();
    sas_contextsec.target_requires = inputstream.read_ushort();
    sas_contextsec.privilege_authorities = ServiceConfigurationListHelper.read(inputstream);
    sas_contextsec.supported_naming_mechanisms = OIDListHelper.read(inputstream);
    sas_contextsec.supported_identity_types = inputstream.read_ulong();
    return sas_contextsec;
  }

  public static void write(OutputStream outputstream, SAS_ContextSec sas_contextsec) {
    outputstream.write_ushort(sas_contextsec.target_supports);
    outputstream.write_ushort(sas_contextsec.target_requires);
    ServiceConfigurationListHelper.write(outputstream, sas_contextsec.privilege_authorities);
    OIDListHelper.write(outputstream, sas_contextsec.supported_naming_mechanisms);
    outputstream.write_ulong(sas_contextsec.supported_identity_types);
  }

  private static String _id = "IDL:omg.org/CSIIOP/SAS_ContextSec:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;

}

