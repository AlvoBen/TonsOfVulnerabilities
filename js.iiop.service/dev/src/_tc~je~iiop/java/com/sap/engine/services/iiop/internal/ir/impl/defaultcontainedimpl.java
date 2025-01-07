package com.sap.engine.services.iiop.internal.IR.impl;

import org.omg.CORBA.DefinitionKind;
import org.omg.CORBA.ORB;
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

/* This class is created to be instantiated in ContainedContainer
 * and provide delegation for common Contained methods
  */

public class DefaultContainedImpl extends ContainedImpl {
  public DefaultContainedImpl(String id, String name, String version, ContainerImpl container, DefinitionKind kind, ORB orb, POA poa)   {
    super(id, name, version, container, kind, orb, poa);
  }

  /* Default dummy implementation of the abstract method
  */
  public org.omg.CORBA.ContainedPackage.Description describe()  {
    return null;
  }

}
