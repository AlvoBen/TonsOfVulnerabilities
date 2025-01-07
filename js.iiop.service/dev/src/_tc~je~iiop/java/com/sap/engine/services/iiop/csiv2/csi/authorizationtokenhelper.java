package com.sap.engine.services.iiop.csiv2.CSI;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class AuthorizationTokenHelper {

  public AuthorizationTokenHelper() {

  }

  public static void insert(Any any, AuthorizationElement aauthorizationelement[]) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, aauthorizationelement);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static AuthorizationElement[] extract(Any any) {
    return read(any.create_input_stream());
  }

  public static synchronized TypeCode type() {
    if (__typeCode == null) {
      __typeCode = AuthorizationElementHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      //            __typeCode = ORB.init().create_alias_tc(id(), "AuthorizationToken", __typeCode);
    }

    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static AuthorizationElement[] read(InputStream inputstream) {
    AuthorizationElement aauthorizationelement[] = null;
    int i = inputstream.read_long();
    aauthorizationelement = new AuthorizationElement[i];
    for (int j = 0; j < aauthorizationelement.length; j++) {
      aauthorizationelement[j] = AuthorizationElementHelper.read(inputstream); 
    }
    return aauthorizationelement;
  }

  public static void write(OutputStream outputstream, AuthorizationElement aauthorizationelement[]) {
    outputstream.write_long(aauthorizationelement.length);
    for (int i = 0; i < aauthorizationelement.length; i++) {
      AuthorizationElementHelper.write(outputstream, aauthorizationelement[i]); 
    }
  }

  private static String _id = "IDL:omg.org/CSI/AuthorizationToken:1.0";
  private static TypeCode __typeCode = null;

}

