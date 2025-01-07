package com.sap.engine.services.iiop.internal.IR.impl;

import org.omg.CORBA.*;
import org.omg.CORBA.ContainedPackage.Description;
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
public abstract class TypedefDefImpl extends ContainedImpl implements TypedefDefOperations {
  public TypedefDefImpl(String id, String name, String version, ContainerImpl container, DefinitionKind kind, ORB orb, POA poa)   {
    super(id, name, version, container, kind, orb, poa);
  }

  public Description describe()  {
    Any any = orb.create_any();
    TypeDescriptionHelper.insert(any, new TypeDescription(name(), id(), defined_in_id(), version(), type()));

    return new Description(def_kind(), any);
  }

  public abstract TypeCode type()  ;
}
