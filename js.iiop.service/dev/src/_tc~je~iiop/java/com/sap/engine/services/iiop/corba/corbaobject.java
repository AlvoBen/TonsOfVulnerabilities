/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.CORBA;


import org.omg.CORBA.portable.Delegate;

/**
 * Implementation of org.omg.CORBA.portable.ObjectImpl - the base
 * class of all CORBA objects. It's used only for creating a basic
 * CORBA object without any functionality. When this object is
 * created it must be narrowed to it's proper type.
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public class CORBAObject extends org.omg.CORBA_2_3.portable.ObjectImpl {

  IOR ior;
  org.omg.CORBA.ORB orb;
//  static String[] ids = {"IDL:com.sap.CORBA/Object:1.0", "IDL:omg.org/CosNaming/NamingContextExt:1.0", "IDL:omg.org/CosNaming/NamingContext:1.0"};
  static String OBJECT = "IDL:com.sap.CORBA/Object:1.0";
  static String NAMING = "IDL:omg.org/CosNaming/NamingContext:1.0";
  static String NAMINGEXT = "IDL:omg.org/CosNaming/NamingContextExt:1.0";
  String[] ids = {OBJECT, OBJECT, NAMING, NAMINGEXT};
                        // "" - empty string for ior's type id

  public CORBAObject() {
    super();
  }

  public String[] _ids() {
    return ids;
  }

  public CORBAObject(IOR ior0) {
    super();
    ior = ior0;
    ids[0] = ior.getTypeID();
    orb = ior.getORB();
    Delegate d = ((com.sap.engine.services.iiop.CORBA.ORB) orb).getDelegate(ior);
    _set_delegate(d);
  }

  public IOR getIOR() {
    return ior;
  }

  public org.omg.CORBA.ORB getORB() {
    return orb;
  }

}

