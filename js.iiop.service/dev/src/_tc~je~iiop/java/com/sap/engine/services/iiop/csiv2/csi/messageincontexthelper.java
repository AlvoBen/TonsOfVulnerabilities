package com.sap.engine.services.iiop.csiv2.CSI;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class MessageInContextHelper {

  public MessageInContextHelper() {

  }

  public static void insert(Any any, MessageInContext messageincontext) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, messageincontext);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static MessageInContext extract(Any any) {
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
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_ulonglong);
          //                    typecode1 = ORB.init().create_alias_tc(ContextIdHelper.id(), "ContextId", typecode1);
          astructmember[0] = new StructMember("client_context_id", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_boolean);
          astructmember[1] = new StructMember("discard_context", typecode1, null);
          __typeCode = ORB.init().create_struct_tc(id(), "MessageInContext", astructmember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static MessageInContext read(InputStream inputstream) {
    MessageInContext messageincontext = new MessageInContext();
    messageincontext.client_context_id = inputstream.read_ulonglong();
    messageincontext.discard_context = inputstream.read_boolean();
    return messageincontext;
  }

  public static void write(OutputStream outputstream, MessageInContext messageincontext) {
    outputstream.write_ulonglong(messageincontext.client_context_id);
    outputstream.write_boolean(messageincontext.discard_context);
  }

  private static String _id = "IDL:omg.org/CSI/MessageInContext:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;

}

