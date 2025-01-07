package com.sap.engine.services.iiop.csiv2.CSIIOP;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import com.sap.engine.interfaces.csiv2.*;

public abstract class ServiceConfigurationListHelper {

  public ServiceConfigurationListHelper() {

  }

  public static void insert(Any any, ServiceConfiguration aserviceconfiguration[]) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, aserviceconfiguration);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static ServiceConfiguration[] extract(Any any) {
    return read(any.create_input_stream());
  }

  public static synchronized TypeCode type() {
    if (__typeCode == null) {
      __typeCode = ServiceConfigurationHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ServiceConfigurationList", __typeCode);
    }

    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static ServiceConfiguration[] read(InputStream inputstream) {
    ServiceConfiguration aserviceconfiguration[] = null;
    int i = inputstream.read_long();
    aserviceconfiguration = new ServiceConfiguration[i];
    for (int j = 0; j < aserviceconfiguration.length; j++) {
      aserviceconfiguration[j] = ServiceConfigurationHelper.read(inputstream); 
    }
    return aserviceconfiguration;
  }

  public static void write(OutputStream outputstream, ServiceConfiguration aserviceconfiguration[]) {
    outputstream.write_long(aserviceconfiguration.length);
    for (int i = 0; i < aserviceconfiguration.length; i++) {
      ServiceConfigurationHelper.write(outputstream, aserviceconfiguration[i]); 
    }
  }

  private static String _id = "IDL:omg.org/CSIIOP/ServiceConfigurationList:1.0";
  private static TypeCode __typeCode = null;

}

