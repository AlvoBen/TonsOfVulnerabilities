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
public class StringDefImpl extends IRObjectImpl implements StringDefOperations {
  private int bound = 0;

  public StringDefImpl(int bound, ORB orb, POA poa)    {
    super(DefinitionKind.dk_String, orb, poa);

    this.bound = bound;
  }

  public int bound()   {
    return bound;
  }

  public void bound(int newBound)   {
    bound = newBound;
  }

  public TypeCode type()   {
    return orb.create_string_tc(bound);
  }

  public void destroy() {
  }

}
