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
public class ArrayDefImpl extends IRObjectImpl implements ArrayDefOperations {
  private int length = 0;
  private IDLType element_type = null;

  public ArrayDefImpl(int length, IDLType element_type, ORB orb, POA poa)  {
    super(DefinitionKind.dk_Sequence, orb, poa);

    this.length = length;
    this.element_type = element_type;
  }

  public int length()  {
    return length;
  }

  public void length(int newLength)  {
    length = newLength;
  }

  public org.omg.CORBA.TypeCode element_type()  {
    return element_type.type();
  }

  public IDLType element_type_def() {
    return element_type;
  }

  public void element_type_def(IDLType newElement_type_def)  {
    element_type = newElement_type_def;
  }

  public TypeCode type()  {
    return orb.create_array_tc(length, element_type());
  }

  public void destroy() {
    element_type = null;
  }
}
