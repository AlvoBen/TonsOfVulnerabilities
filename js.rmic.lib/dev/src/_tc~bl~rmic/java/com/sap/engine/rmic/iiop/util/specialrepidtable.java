package com.sap.engine.rmic.iiop.util;

import java.io.ObjectStreamClass;

public class SpecialRepIDTable extends RepositoryIDCacheTable {
  static final long serialVersionUID = 7087422784312730484L;

  private static SpecialRepIDTable table = new SpecialRepIDTable();

  public static String getID(Class cls) {
    return (String) table.get(cls);
  }

  static {
    String classDescValueHash = ":" + Long.toHexString(ObjectStreamClass.lookup(javax.rmi.CORBA.ClassDesc.class).getSerialVersionUID());
    String classDescValueRepID = "RMI:javax.rmi.CORBA.ClassDesc" + classDescValueHash;
    table.put(java.lang.Class.class, classDescValueRepID);
    table.put(java.lang.String.class, "IDL:omg.org/CORBA/WStringValue:1.0");
    table.put(java.rmi.Remote.class, "");
    table.put(org.omg.CORBA.Object.class, "IDL:omg.org/CORBA/Object:1.0");
  }


}

