package com.sap.engine.services.iiop.internal.IR.impl;

import org.omg.CORBA.ContainedPackage.Description;
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
public class ExceptionDefImpl extends ContainedContainer implements ExceptionDefOperations {
  private StructMember[] members = null;

  public ExceptionDefImpl(String id, String name, String version, StructMember[] members, ContainerImpl container, ORB orb, POA poa)   {
    super(id, name, version, container, DefinitionKind.dk_Struct, orb, poa);
    this.members = members;
  }

  public org.omg.CORBA.TypeCode type()   {
    TypeCode typecode = orb.create_exception_tc(id(), name(), members());

    return typecode;
  }

  public StructMember[] members()   {
    return members;
  }

  public void members(org.omg.CORBA.StructMember[] newMembers)  {
    this.members = newMembers;
  }

  public org.omg.CORBA.ContainedPackage.Description describe()  {
    Any any = orb.create_any();
    ExceptionDescriptionHelper.insert(any, new ExceptionDescription(name(), id(), defined_in_id(), version(), type()));

    return new Description(def_kind(), any);
  }
}
