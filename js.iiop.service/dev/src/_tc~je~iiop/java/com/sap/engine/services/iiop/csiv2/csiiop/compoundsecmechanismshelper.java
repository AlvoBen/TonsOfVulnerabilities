﻿package com.sap.engine.services.iiop.csiv2.CSIIOP;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class CompoundSecMechanismsHelper {

  public CompoundSecMechanismsHelper() {

  }

  public static void insert(Any any, CompoundSecMech acompoundsecmech[]) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, acompoundsecmech);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static CompoundSecMech[] extract(Any any) {
    return read(any.create_input_stream());
  }

  public static synchronized TypeCode type() {
    if (__typeCode == null) {
      __typeCode = CompoundSecMechHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "CompoundSecMechanisms", __typeCode);
    }

    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static CompoundSecMech[] read(InputStream inputstream) {
    CompoundSecMech acompoundsecmech[] = null;
    int i = inputstream.read_long();
    acompoundsecmech = new CompoundSecMech[i];
    for (int j = 0; j < acompoundsecmech.length; j++) {
      acompoundsecmech[j] = CompoundSecMechHelper.read(inputstream); 
    }
    return acompoundsecmech;
  }

  public static void write(OutputStream outputstream, CompoundSecMech acompoundsecmech[]) {
    outputstream.write_long(acompoundsecmech.length);
    for (int i = 0; i < acompoundsecmech.length; i++) {
      CompoundSecMechHelper.write(outputstream, acompoundsecmech[i]); 
    }
  }

  private static String _id = "IDL:omg.org/CSIIOP/CompoundSecMechanisms:1.0";
  private static TypeCode __typeCode = null;

}

