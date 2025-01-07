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
public class AttributeDefImpl extends ContainedImpl implements AttributeDefOperations {
  private IDLType type_def = null;
  private AttributeMode mode = null;

  public AttributeDefImpl(String id, String name, String version, IDLType type_def, AttributeMode mode, ContainerImpl container, ORB orb, POA poa)  {
    super(id, name, version, container, DefinitionKind.dk_Attribute, orb, poa);

    this.type_def = type_def;
    this.mode = mode;
  }

  public org.omg.CORBA.TypeCode type()  {
    return type_def.type();
  }

  public Description describe()  {
    Any any = orb.create_any();
    AttributeDescriptionHelper.insert(any, new AttributeDescription(name, id, defined_in_id(), version, type(), mode));

    return new Description(def_kind(), any);
  }

  public AttributeMode mode() {
    return mode;
  }

  public void mode(AttributeMode newMode)  {
    mode = newMode;
  }

  public IDLType type_def()  {
    return type_def;
  }

  public void type_def(IDLType type_def)  {
    this.type_def = type_def;
  }

  public void destroy() {
  }
}
