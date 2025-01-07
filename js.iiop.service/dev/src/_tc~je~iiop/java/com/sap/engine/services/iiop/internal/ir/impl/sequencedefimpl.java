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
public class SequenceDefImpl extends IRObjectImpl implements SequenceDefOperations {
  private int bound = 0;
  private IDLType element_type = null;

  public SequenceDefImpl(int bound, IDLType element_type, ORB orb, POA poa)  {
    super(DefinitionKind.dk_Sequence, orb, poa);

    this.bound = bound;
    this.element_type = element_type;
  }

  public int bound()   {
    return bound;
  }

  public void bound(int newBound)   {
    bound = newBound;
  }

  public org.omg.CORBA.TypeCode element_type()   {
    return element_type.type();
  }

  public IDLType element_type_def()   {
    return element_type;
  }

  public void element_type_def(IDLType newElement_type_def)   {
    element_type = newElement_type_def;
  }

  public TypeCode type()   {
    return orb.create_sequence_tc(bound, element_type());
  }

  public void destroy() {
    element_type = null;
  }
}
