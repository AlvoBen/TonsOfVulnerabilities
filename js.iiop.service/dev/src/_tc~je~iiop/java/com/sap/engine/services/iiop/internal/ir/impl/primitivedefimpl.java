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
public class PrimitiveDefImpl extends IRObjectImpl implements PrimitiveDefOperations {
  private PrimitiveKind kind = null;

  public PrimitiveDefImpl(PrimitiveKind kind, ORB orb, POA poa)   {
    super(DefinitionKind.dk_Primitive, orb, poa);

    this.kind = kind;
  }

  public org.omg.CORBA.PrimitiveKind kind()   {
    return kind;
  }

  public void destroy() {
    kind = null;
  }

  public TypeCode type()    {
    return orb.get_primitive_tc(TCKind.from_int(kind.value()));
  }
}
