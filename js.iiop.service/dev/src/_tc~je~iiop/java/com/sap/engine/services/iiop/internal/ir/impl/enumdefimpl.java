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
public class EnumDefImpl extends TypedefDefImpl implements EnumDefOperations {
  private String[] members = null;

  public EnumDefImpl(String id, String name, String version, String[] members, ContainerImpl container, ORB orb, POA poa)  {
    super(id, name, version, container, DefinitionKind.dk_Struct, orb, poa);
    this.members = members;
  }

  public String[] members()  {
    return members;
  }

  public void members(String[] newMembers)  {
    this.members = newMembers;
  }

  public TypeCode type()  {
    TypeCode typecode = orb.create_enum_tc(id(), name(), members());

    return typecode;
  }
}
