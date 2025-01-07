package com.sap.engine.services.iiop.csiv2.CSIIOP;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import com.sap.engine.interfaces.csiv2.*;

public abstract class ServiceConfigurationHelper {

  public ServiceConfigurationHelper() {

  }

  public static void insert(Any any, ServiceConfiguration serviceconfiguration) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, serviceconfiguration);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static ServiceConfiguration extract(Any any) {
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
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_ulong);
          typecode1 = ORB.init().create_alias_tc(ServiceConfigurationSyntaxHelper.id(), "ServiceConfigurationSyntax", typecode1);
          astructmember[0] = new StructMember("syntax", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_octet);
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          typecode1 = ORB.init().create_alias_tc(ServiceSpecificNameHelper.id(), "ServiceSpecificName", typecode1);
          astructmember[1] = new StructMember("name", typecode1, null);
          __typeCode = ORB.init().create_struct_tc(id(), "ServiceConfiguration", astructmember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static ServiceConfiguration read(InputStream inputstream) {
    ServiceConfiguration serviceconfiguration = new ServiceConfiguration();
    serviceconfiguration.syntax = inputstream.read_ulong();
    serviceconfiguration.name = ServiceSpecificNameHelper.read(inputstream);
    return serviceconfiguration;
  }

  public static void write(OutputStream outputstream, ServiceConfiguration serviceconfiguration) {
    outputstream.write_ulong(serviceconfiguration.syntax);
    ServiceSpecificNameHelper.write(outputstream, serviceconfiguration.name);
  }

  private static String _id = "IDL:omg.org/CSIIOP/ServiceConfiguration:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;

}

