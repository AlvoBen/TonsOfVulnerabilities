package com.sap.engine.services.iiop.csiv2.CSIIOP;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class CompoundSecMechListHelper {

  public CompoundSecMechListHelper() {

  }

  public static void insert(Any any, CompoundSecMechList compoundsecmechlist) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, compoundsecmechlist);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static CompoundSecMechList extract(Any any) {
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
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_boolean);
          astructmember[0] = new StructMember("stateful", typecode1, null);
          typecode1 = CompoundSecMechHelper.type();
          typecode1 = ORB.init().create_sequence_tc(0, typecode1);
          typecode1 = ORB.init().create_alias_tc(CompoundSecMechanismsHelper.id(), "CompoundSecMechanisms", typecode1);
          astructmember[1] = new StructMember("mechanism_list", typecode1, null);
          __typeCode = ORB.init().create_struct_tc(id(), "CompoundSecMechList", astructmember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static CompoundSecMechList read(InputStream inputstream) {
    CompoundSecMechList compoundsecmechlist = new CompoundSecMechList();
    compoundsecmechlist.stateful = inputstream.read_boolean();
    compoundsecmechlist.mechanism_list = CompoundSecMechanismsHelper.read(inputstream);
    return compoundsecmechlist;
  }

  public static void write(OutputStream outputstream, CompoundSecMechList compoundsecmechlist) {
    outputstream.write_boolean(compoundsecmechlist.stateful);
    CompoundSecMechanismsHelper.write(outputstream, compoundsecmechlist.mechanism_list);
  }

  private static String _id = "IDL:omg.org/CSIIOP/CompoundSecMechList:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;

}

