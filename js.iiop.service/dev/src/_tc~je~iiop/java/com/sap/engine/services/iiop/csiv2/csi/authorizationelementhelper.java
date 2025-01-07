package com.sap.engine.services.iiop.csiv2.CSI;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class AuthorizationElementHelper {

  public AuthorizationElementHelper() {

  }

  public static void insert(Any any, AuthorizationElement authorizationelement) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, authorizationelement);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static AuthorizationElement extract(Any any) {
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
          //                    typecode1 = ORB.init().create_alias_tc(AuthorizationElementTypeHelper.id(), "AuthorizationElementType", typecode1);
          astructmember[0] = new StructMember("the_type", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_octet);
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          //                    typecode1 = ORB.init().create_alias_tc(AuthorizationElementContentsHelper.id(), "AuthorizationElementContents", typecode1);
          astructmember[1] = new StructMember("the_element", typecode1, null);
          __typeCode = ORB.init().create_struct_tc(id(), "AuthorizationElement", astructmember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static AuthorizationElement read(InputStream inputstream) {
    AuthorizationElement authorizationelement = new AuthorizationElement();
    authorizationelement.the_type = inputstream.read_ulong();
    authorizationelement.the_element = AuthorizationElementContentsHelper.read(inputstream);
    return authorizationelement;
  }

  public static void write(OutputStream outputstream, AuthorizationElement authorizationelement) {
    outputstream.write_ulong(authorizationelement.the_type);
    AuthorizationElementContentsHelper.write(outputstream, authorizationelement.the_element);
  }

  private static String _id = "IDL:omg.org/CSI/AuthorizationElement:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;

}

