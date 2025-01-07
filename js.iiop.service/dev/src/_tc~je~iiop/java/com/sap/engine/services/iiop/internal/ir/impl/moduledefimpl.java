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
public class ModuleDefImpl extends ContainedContainer implements ModuleDefOperations {
  public ModuleDefImpl(String id, String name, String version, ContainerImpl container, ORB orb, POA poa)  {
    super(id, name, version, container, DefinitionKind.dk_Module, orb, poa);
  }

  public Description describe()   {
    Any any = orb.create_any();
    ModuleDescriptionHelper.insert(any, new ModuleDescription(name(), id(), defined_in_id(), version()));

    return new Description(def_kind(), any);
  }

}
