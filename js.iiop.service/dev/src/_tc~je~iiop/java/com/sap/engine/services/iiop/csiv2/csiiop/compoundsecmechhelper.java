package com.sap.engine.services.iiop.csiv2.CSIIOP;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import com.sap.engine.services.iiop.CORBA.SimpleProfileHelper;

public abstract class CompoundSecMechHelper {

  public CompoundSecMechHelper() {

  }

  public static void insert(Any any, CompoundSecMech compoundsecmech) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, compoundsecmech);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static CompoundSecMech extract(Any any) {
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
          typecode1 = ORB.init().get_primitive_tc(TCKind.tk_ushort);
          typecode1 = ORB.init().create_alias_tc(AssociationOptionsHelper.id(), "AssociationOptions", typecode1);
          astructmember[0] = new StructMember("target_requires", typecode1, null);
          typecode1 = com.sap.engine.services.iiop.CORBA.SimpleProfileHelper.type();
          astructmember[1] = new StructMember("transport_mech", typecode1, null);
          typecode1 = AS_ContextSecHelper.type();
          astructmember[2] = new StructMember("as_context_mech", typecode1, null);
          typecode1 = SAS_ContextSecHelper.type();
          astructmember[3] = new StructMember("sas_context_mech", typecode1, null);
          __typeCode = ORB.init().create_struct_tc(id(), "CompoundSecMech", astructmember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static CompoundSecMech read(InputStream inputstream) {
    CompoundSecMech compoundsecmech = new CompoundSecMech();
    compoundsecmech.target_requires = inputstream.read_ushort();
    compoundsecmech.transport_mech = SimpleProfileHelper.read(inputstream);
    compoundsecmech.as_context_mech = AS_ContextSecHelper.read(inputstream);
    compoundsecmech.sas_context_mech = SAS_ContextSecHelper.read(inputstream);
    return compoundsecmech;
  }

  public static void write(OutputStream outputstream, CompoundSecMech compoundsecmech) {
    outputstream.write_ushort(compoundsecmech.target_requires);
    SimpleProfileHelper.write(outputstream, compoundsecmech.transport_mech);
    AS_ContextSecHelper.write(outputstream, compoundsecmech.as_context_mech);
    SAS_ContextSecHelper.write(outputstream, compoundsecmech.sas_context_mech);
  }

  private static String _id = "IDL:omg.org/CSIIOP/CompoundSecMech:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;

}

