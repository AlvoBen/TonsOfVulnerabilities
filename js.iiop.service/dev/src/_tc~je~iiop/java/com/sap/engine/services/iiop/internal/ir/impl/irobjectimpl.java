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
public abstract class IRObjectImpl extends LocalObject implements IRObjectOperations {
  protected DefinitionKind kind = null;
  ORB orb = null;
  POA poa = null;


  public IRObjectImpl(DefinitionKind kind, ORB orb, POA poa)  {
    super();

    this.kind = kind;
    this.orb = orb;
    this.poa = poa;
  }

  public org.omg.CORBA.DefinitionKind def_kind()  {
    return kind;
  }

  // write interface

  public abstract void destroy();

  // specific methods for this implementation
}
