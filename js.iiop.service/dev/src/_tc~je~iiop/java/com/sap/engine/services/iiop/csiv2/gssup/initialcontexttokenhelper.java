package com.sap.engine.services.iiop.csiv2.GSSUP;

import com.sap.engine.services.iiop.csiv2.CSI.GSS_NT_ExportedNameHelper;
import com.sap.engine.services.iiop.csiv2.CSI.UTF8StringHelper;
import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class InitialContextTokenHelper {

  public InitialContextTokenHelper() {

  }

  public static void insert(Any any, InitialContextToken initialcontexttoken) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, initialcontexttoken);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static InitialContextToken extract(Any any) {
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
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_octet);
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          typecode1 = ORB.init().create_alias_tc(UTF8StringHelper.id(), "UTF8String", typecode1);
          astructmember[0] = new StructMember("username", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_octet);
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          typecode1 = ORB.init().create_alias_tc(UTF8StringHelper.id(), "UTF8String", typecode1);
          astructmember[1] = new StructMember("password", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_octet);
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          typecode1 = ORB.init().create_alias_tc(GSS_NT_ExportedNameHelper.id(), "GSS_NT_ExportedName", typecode1);
          astructmember[2] = new StructMember("target_name", typecode1, null);
          __typeCode = ORB.init().create_struct_tc(id(), "InitialContextToken", astructmember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static InitialContextToken read(InputStream inputstream) {
    InitialContextToken initialcontexttoken = new InitialContextToken();
    initialcontexttoken.username = UTF8StringHelper.read(inputstream);
    initialcontexttoken.password = UTF8StringHelper.read(inputstream);
    initialcontexttoken.target_name = GSS_NT_ExportedNameHelper.read(inputstream);
    return initialcontexttoken;
  }

  public static void write(OutputStream outputstream, InitialContextToken initialcontexttoken) {
    UTF8StringHelper.write(outputstream, initialcontexttoken.username);
    UTF8StringHelper.write(outputstream, initialcontexttoken.password);
    GSS_NT_ExportedNameHelper.write(outputstream, initialcontexttoken.target_name);
  }

  private static String _id = "IDL:omg.org/GSSUP/InitialContextToken:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;

}

