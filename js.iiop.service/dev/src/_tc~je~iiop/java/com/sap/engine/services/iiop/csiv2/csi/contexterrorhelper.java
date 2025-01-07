package com.sap.engine.services.iiop.csiv2.CSI;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ContextErrorHelper {

  public ContextErrorHelper() {

  }

  public static void insert(Any any, ContextError contexterror) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, contexterror);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static ContextError extract(Any any) {
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
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_ulonglong);
          //                    typecode1 = ORB.init().create_alias_tc(ContextIdHelper.id(), "ContextId", typecode1);
          astructmember[0] = new StructMember("client_context_id", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_long);
          astructmember[1] = new StructMember("major_status", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_long);
          astructmember[2] = new StructMember("minor_status", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_octet);
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          //                    typecode1 = ORB.init().create_alias_tc(GSSTokenHelper.id(), "GSSToken", typecode1);
          astructmember[3] = new StructMember("error_token", typecode1, null);
          __typeCode = ORB.init().create_struct_tc(id(), "ContextError", astructmember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static ContextError read(InputStream inputstream) {
    ContextError contexterror = new ContextError();
    contexterror.client_context_id = inputstream.read_ulonglong();
    contexterror.major_status = inputstream.read_long();
    contexterror.minor_status = inputstream.read_long();
    contexterror.error_token = GSSTokenHelper.read(inputstream);
    return contexterror;
  }

  public static void write(OutputStream outputstream, ContextError contexterror) {
    outputstream.write_ulonglong(contexterror.client_context_id);
    outputstream.write_long(contexterror.major_status);
    outputstream.write_long(contexterror.minor_status);
    GSSTokenHelper.write(outputstream, contexterror.error_token);
  }

  private static String _id = "IDL:omg.org/CSI/ContextError:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;

}

