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
public class UnionDefImpl extends ContainedContainerTypedef implements UnionDefOperations {

  private IDLType discriminator_type_def = null;
  private UnionMember[] members = null;

  public UnionDefImpl(String id, String name, String version, IDLType discriminator_type, org.omg.CORBA.UnionMember[] members, ContainerImpl container, ORB orb, POA poa)  {
    super(id, name, version, container, DefinitionKind.dk_Union, orb, poa);

    this.members = members;
    this.discriminator_type_def = discriminator_type;
  }

  public org.omg.CORBA.TypeCode discriminator_type()  {
    return discriminator_type_def.type();
  }

  public IDLType discriminator_type_def()  {
    return discriminator_type_def;
  }

  public void discriminator_type_def(IDLType newDiscriminator_type_def)  {
  }

  public org.omg.CORBA.UnionMember[] members()  {
    return members;
  }

  public void members(org.omg.CORBA.UnionMember[] newMembers)   {
    this.members = newMembers;
  }

  public TypeCode type()  {
    return discriminator_type_def.type();
  }
}
