package com.sap.engine.services.iiop.internal.IR.impl;

import org.omg.CORBA.ContainedPackage.*;
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
public class ValueMemberDefImpl extends ContainedImpl implements ValueMemberDefOperations {
  private IDLType type_def = null;
  private short access = PRIVATE_MEMBER.value;

  public ValueMemberDefImpl(String id, String name, String version, IDLType type_def, short access, ContainerImpl container, ORB orb, POA poa)  {
    super(id, name, version, container, DefinitionKind.dk_ValueMember, orb, poa);

    this.type_def = type_def;
    this.access = access;
  }

  public org.omg.CORBA.TypeCode type()   {
    return type_def.type();
  }

  public IDLType type_def()   {
    return type_def;
  }

  public void type_def(IDLType newType_def)   {
    this.type_def = newType_def;
  }

  public short access()   {
    return access;
  }

  public void access(short newAccess)   {
    if (newAccess != PRIVATE_MEMBER.value &&
        newAccess != PUBLIC_MEMBER.value)
      throw new BAD_PARAM();

    access = newAccess;
  }

  public org.omg.CORBA.ContainedPackage.Description describe()   {
    Any any = orb.create_any();
    ValueMemberHelper.insert(any, new ValueMember(name, id, defined_in_id(), version, type(), type_def, access));

    return new Description(def_kind(), any);
  }
}
