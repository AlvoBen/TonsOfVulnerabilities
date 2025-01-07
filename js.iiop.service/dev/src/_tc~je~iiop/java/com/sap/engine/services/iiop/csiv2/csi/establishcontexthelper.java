package com.sap.engine.services.iiop.csiv2.CSI;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class EstablishContextHelper {

  public EstablishContextHelper() {

  }

  public static void insert(Any any, EstablishContext establishcontext) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, establishcontext);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static EstablishContext extract(Any any) {
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
          typecode1 = AuthorizationElementHelper.type();
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          //                    typecode1 = ORB.init().create_alias_tc(AuthorizationTokenHelper.id(), "AuthorizationToken", typecode1);
          astructmember[1] = new StructMember("authorization_token", typecode1, null);
          typecode1 = IdentityTokenHelper.type();
          astructmember[2] = new StructMember("identity_token", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_octet);
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          //                    typecode1 = ORB.init().create_alias_tc(GSSTokenHelper.id(), "GSSToken", typecode1);
          astructmember[3] = new StructMember("client_authentication_token", typecode1, null);
          __typeCode = ORB.init().create_struct_tc(id(), "EstablishContext", astructmember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static EstablishContext read(InputStream inputstream) {
    EstablishContext establishcontext = new EstablishContext();
    establishcontext.client_context_id = inputstream.read_ulonglong();
    establishcontext.authorization_token = AuthorizationTokenHelper.read(inputstream);
    establishcontext.identity_token = IdentityTokenHelper.read(inputstream);
    establishcontext.client_authentication_token = GSSTokenHelper.read(inputstream);
    return establishcontext;
  }

  public static void write(OutputStream outputstream, EstablishContext establishcontext) {
    outputstream.write_ulonglong(establishcontext.client_context_id);
    AuthorizationTokenHelper.write(outputstream, establishcontext.authorization_token);
    IdentityTokenHelper.write(outputstream, establishcontext.identity_token);
    GSSTokenHelper.write(outputstream, establishcontext.client_authentication_token);
  }

  private static String _id = "IDL:omg.org/CSI/EstablishContext:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;

}

