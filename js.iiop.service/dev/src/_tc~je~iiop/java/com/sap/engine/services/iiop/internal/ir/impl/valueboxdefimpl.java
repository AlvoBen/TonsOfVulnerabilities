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
public class ValueBoxDefImpl extends TypedefDefImpl implements ValueBoxDefOperations {
  private IDLType original_type_def = null;

  public ValueBoxDefImpl(String id, String name, String version, IDLType original_type_def, ContainerImpl container,  ORB orb, POA poa)   {
    super(id, name, version, container, DefinitionKind.dk_ValueBox, orb, poa);

    this.original_type_def = original_type_def;
  }

  public IDLType original_type_def()   {
    return original_type_def;
  }

  public void original_type_def(IDLType newOriginal_type_def)  {
    this.original_type_def = newOriginal_type_def;
  }

  public TypeCode type()   {
    return original_type_def.type();
  }
}
