package com.sap.engine.services.iiop.internal.IR.impl;

import org.omg.CORBA.*;
import org.omg.PortableServer.POA;

/**
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
public class NativeDefImpl extends TypedefDefImpl implements NativeDefOperations {
  private TypeCode type = null;

  public NativeDefImpl(String id, String name, String version, ContainerImpl container, ORB orb, POA poa)   {
    super(id, name, version, container, DefinitionKind.dk_Native, orb, poa);

    type = orb.create_native_tc(id(), name());
  }

  public TypeCode type()   {
    return type;
  }
}
