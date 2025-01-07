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

public class ConstantDefImpl extends ContainedImpl implements ConstantDefOperations {
  private IDLType type_def = null;
  private Any value = null;

  public ConstantDefImpl(String id, String name, String version, IDLType type_def, org.omg.CORBA.Any value, ContainerImpl container, ORB orb, POA poa)   {
    super(id, name, version, container, DefinitionKind.dk_Constant, orb, poa);

    this.type_def = type_def;
    this.value = value;
  }


  public org.omg.CORBA.TypeCode type()   {
    return type_def.type();
  }

  public IDLType type_def()  {
    return type_def;
  }

  public void type_def(IDLType newType_def)  {
    this.type_def = newType_def;
  }

  public org.omg.CORBA.Any value()  {
    return value;
  }

  public void value(org.omg.CORBA.Any newValue)  {
    if (!newValue.type().equals(type_def.type()))
      throw new BAD_PARAM("The any value does not match to initial type for this ConstantDef instance");

    this.value = newValue;
  }

  public Description describe()  {
    Any any = orb.create_any();
    ConstantDescriptionHelper.insert(any, new ConstantDescription(name(), id(), defined_in_id(), version(), type(), value()));

    return new Description(def_kind(), any);
  }
}
