package com.sap.engine.services.iiop.csiv2.CSI;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class CompleteEstablishContextHelper {

  public CompleteEstablishContextHelper() {

  }

  public static void insert(Any any, CompleteEstablishContext completeestablishcontext) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, completeestablishcontext);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static CompleteEstablishContext extract(Any any) {
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
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_ulonglong);
          //                    typecode1 = ORB.init().create_alias_tc(ContextIdHelper.id(), "ContextId", typecode1);
          astructmember[0] = new StructMember("client_context_id", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_boolean);
          astructmember[1] = new StructMember("context_stateful", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_octet);
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          //                    typecode1 = ORB.init().create_alias_tc(GSSTokenHelper.id(), "GSSToken", typecode1);
          astructmember[2] = new StructMember("final_context_token", typecode1, null);
          __typeCode = ORB.init().create_struct_tc(id(), "CompleteEstablishContext", astructmember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static CompleteEstablishContext read(InputStream inputstream) {
    CompleteEstablishContext completeestablishcontext = new CompleteEstablishContext();
    completeestablishcontext.client_context_id = inputstream.read_ulonglong();
    completeestablishcontext.context_stateful = inputstream.read_boolean();
    completeestablishcontext.final_context_token = GSSTokenHelper.read(inputstream);
    return completeestablishcontext;
  }

  public static void write(OutputStream outputstream, CompleteEstablishContext completeestablishcontext) {
    outputstream.write_ulonglong(completeestablishcontext.client_context_id);
    outputstream.write_boolean(completeestablishcontext.context_stateful);
    GSSTokenHelper.write(outputstream, completeestablishcontext.final_context_token);
  }

  private static String _id = "IDL:omg.org/CSI/CompleteEstablishContext:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;

}

