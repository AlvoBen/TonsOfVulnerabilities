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
public abstract class ContainedContainer extends ContainerImpl implements ContainedOperations {
  protected ContainedImpl contained = null;

  protected ContainedContainer(String id, String name, String version, ContainerImpl container, DefinitionKind kind, ORB orb, POA poa) {
    super(kind, orb, poa);
    contained = new DefaultContainedImpl(id, name, version, container, kind, orb, poa);
  }


  public String id()  {
    return contained.id();
  }

  // read/write interface
  public void id(String id)  {
    contained.id(id);
  }

  public String name()  {
    return contained.name();
  }

  public void name(String name)  {
    contained.name(name);
  }

  public String version()  {
    return contained.version();
  }

  public void version(String version)  {
    contained.version(version);
  }

  // read interface
  public Container defined_in()  {
    return contained.defined_in();
  }

  public String absolute_name()  {
    return contained.absolute_name();
  }

  public org.omg.CORBA.Repository containing_repository()  {
    return contained.containing_repository();
  }

   public Contained getContained() {
    return contained.getContained();
  }

  public void setContained(Contained _contained) {
    contained.setContained(_contained);
  }

  public abstract org.omg.CORBA.ContainedPackage.Description describe()  ;

  // write interface
  public void move(Container new_container, String new_name, String new_version)   {
    contained.move(new_container, new_name, new_version);
  }

  public ContainerImpl getDefinedInImpl()  {
    return contained.defined_in;
  }

  public void destroy()   {
    super.destroy();
    contained.destroy();
  }

  // extension
  protected String defined_in_id()   {
    return contained.defined_in_id();
  }
}
