package com.sap.engine.services.iiop.CORBA;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.IOP.ComponentIdHelper;
import com.sap.engine.interfaces.csiv2.*;

public class SimpleProfileHelper {

  public SimpleProfileHelper() {

  }

  public static void insert(Any any, SimpleProfile taggedcomponent) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, taggedcomponent);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static SimpleProfile extract(Any any) {
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
          typecode1 = ORB.init().create_alias_tc(ComponentIdHelper.id(), "ComponentId", typecode1);
          astructmember[0] = new StructMember("tag", typecode1, null);
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_octet);
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          astructmember[1] = new StructMember("component_data", typecode1, null);
          __typeCode = ORB.init().create_struct_tc(id(), "TaggedComponent", astructmember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static SimpleProfile read(InputStream inputstream) {
    int tag = inputstream.read_ulong();
    int i = inputstream.read_long();
    byte[] data = new byte[i];
    inputstream.read_octet_array(data, 0, i);
    SimpleProfile taggedcomponent = new SimpleProfile(tag, data);
    return taggedcomponent;
  }

  public static void write(OutputStream outputstream, SimpleProfileInterface taggedcomponent) {
    outputstream.write_ulong(taggedcomponent.getTag());
    outputstream.write_long(taggedcomponent.getData().length);
    outputstream.write_octet_array(taggedcomponent.getData(), 0, taggedcomponent.getData().length);
  }

  private static String _id = "IDL:omg.org/IOP/TaggedComponent:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;

}

