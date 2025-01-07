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
public class StructDefImpl extends ContainedContainerTypedef implements StructDefOperations {

  private StructMember[] members = null;

  public StructDefImpl(String id, String name, String version, StructMember[] members, ContainerImpl container, ORB orb, POA poa)  {
    super(id, name, version, container, DefinitionKind.dk_Struct, orb, poa);
    this.members = members;
  }

  public StructMember[] members()   {
    return members;
  }

  public void members(StructMember[] newMembers)   {
    this.members = newMembers;
  }

  public TypeCode type()   {
    TypeCode typecode = orb.create_struct_tc(id(), name(), members());

    return typecode;
  }
}
