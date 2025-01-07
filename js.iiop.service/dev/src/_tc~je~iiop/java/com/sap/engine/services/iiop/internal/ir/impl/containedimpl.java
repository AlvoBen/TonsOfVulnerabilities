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
public abstract class ContainedImpl extends IRObjectImpl implements ContainedOperations
{
  protected String id = null;
  protected String name = null;
  protected String version = null;

  protected String absoluteName = null;

  protected RepositoryImpl repository = null;
  protected ContainerImpl defined_in = null;
  protected Contained _contained = null;

  protected ContainedImpl(String id, String name, String version, ContainerImpl container, DefinitionKind kind, ORB orb, POA poa)  {
    super(kind, orb, poa);

    this.id = id;
    this.name = name;
    this.version = version;

    this.defined_in = container;

    refreshAbsoluteName();
    repository = defined_in.getRepositoryImpl();
  }

  public String id()  {
    return id;
  }

  // read/write interface
  public void id(String id)  {
    this.id = id;
  }

  public String name()  {
    return name;
  }

  public void name(String name)  {
    this.name = name;

    refreshAbsoluteName();
  }

  public String version()  {
    return version;
  }

  public void version(String version)  {
    this.version = version;
  }

  // read interface
  public Container defined_in()  {
    return defined_in.getContainer();
  }

  protected String defined_in_id()  {
    if (defined_in instanceof RepositoryImpl)
      return ((RepositoryImpl) defined_in).getID();
    else if (defined_in instanceof ContainedContainer)
      return ((ContainedContainer) defined_in).id();
    else
      throw new INTERNAL("There is a Container instance which is neither a RepositoryImpl nor ContainedContainer instance");
  }

  public String absolute_name()  {
    return absoluteName;
  }

  public org.omg.CORBA.Repository containing_repository()  {
    return (Repository)repository.getContainer();
  }

  public abstract org.omg.CORBA.ContainedPackage.Description describe()  ;

  // write interface
  public void move(Container new_container, String new_name, String new_version)  {
    throw new INTERNAL("Contained.move(...) is not implemented");
  }

  public void destroy() {
    defined_in.removeContained(this);
  }


  // specific methods for this implementation

  public ContainerImpl getDefinedInImpl()  {
    return defined_in;
  }

  public Contained getContained() {
    return _contained;
  }

  public void setContained(Contained _contained) {
    this._contained = _contained;
  }

  private void refreshAbsoluteName()  {
    if (defined_in instanceof RepositoryImpl)
      this.absoluteName = "::" + name;
    else if (defined_in instanceof ContainedContainer)
      this.absoluteName = ((ContainedContainer) defined_in).absolute_name() + "::" + name;
    else
      throw new INTERNAL("There is a Container instance which is neither a RepositoryImpl nor ContainedContainer instance");
  }
}
