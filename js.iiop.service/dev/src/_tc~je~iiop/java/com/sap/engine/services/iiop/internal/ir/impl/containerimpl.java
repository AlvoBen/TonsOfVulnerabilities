package com.sap.engine.services.iiop.internal.IR.impl;

import org.omg.CORBA.*;
import org.omg.PortableServer.POA;

import java.util.*;

import com.sap.engine.services.iiop.logging.LoggerConfigurator;

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
public abstract class ContainerImpl extends IRObjectImpl implements ContainerOperations {
  protected Map componentsByName = null;
  protected Map componentsByID = null;
  protected List componentIDs = null;

  private RepositoryImpl repository = null;
  private Container _container = null;

  protected ContainerImpl(DefinitionKind kind, ORB orb, POA poa) {
    super(kind, orb, poa);

    componentsByName = new Hashtable();
    componentsByID = new Hashtable();
    componentIDs = new Vector();

    setRepositoryImpl();
  }

  public org.omg.CORBA.Contained lookup(String search_name) {
    int i = search_name.indexOf("::");
    if (i == 0)
      return repository.lookup(search_name.substring(2));
    else if (i == -1)
      return ((ContainedImpl)componentsByName.get(search_name)).getContained();
    else {
      String s1 = search_name.substring(0, i);
      String s2 = search_name.substring(i + 2);
      Contained contained = lookup(s1);
      if (contained == null)
        return null;
      if (!(contained instanceof ContainerImpl))
        return null;
      else
        return ((ContainerImpl)contained).lookup(s2);
    }
  }

  public org.omg.CORBA.Contained[] contents(org.omg.CORBA.DefinitionKind limit_type, boolean exclude_inherited) {
    ArrayList list = new ArrayList();

    Iterator iter = componentIDs.iterator();
    while (iter.hasNext()) {
      String id = (String) iter.next();
      Contained contained = ((ContainedImpl)componentsByID.get(id)).getContained();
      if ((limit_type.value() == DefinitionKind._dk_all) ||
         (contained.def_kind().value() == limit_type.value())) {
        list.add(contained);
      }
    }

    return (Contained[]) list.toArray(new Contained[0]);
  }


  public org.omg.CORBA.Contained[] lookup_name(String search_name, int levels_to_search, org.omg.CORBA.DefinitionKind limit_type, boolean exclude_inherited)  {
    ArrayList list = new ArrayList();

    Iterator iter = componentsByName.values().iterator();
    while (iter.hasNext()) {
      ContainedOperations containedOp = (ContainedOperations)iter.next();
      if (((limit_type.value() == DefinitionKind._dk_all) || (containedOp.def_kind().value() == limit_type.value())) &&
          (containedOp.name().equals(search_name))) {

        list.add(((ContainedImpl)containedOp).getContained());
      }

      if (levels_to_search != 0 && (containedOp instanceof ContainerImpl)) {
        Contained[] _contained = ((ContainerImpl)containedOp).lookup_name(search_name, levels_to_search-1, limit_type, exclude_inherited);

        for (int i = 0; i < _contained.length; i++) {
          list.add(_contained[i]);
        }
      }
    }

    return (Contained[]) list.toArray(new Contained[list.size()]);
  }

  public org.omg.CORBA.ContainerPackage.Description[] describe_contents(org.omg.CORBA.DefinitionKind limit_type, boolean exclude_inherited, int max_returned_objs)  {
    Contained[] contained = contents(limit_type,exclude_inherited);
    int N = contained.length;

    if ((max_returned_objs > -1) && (max_returned_objs < N)) {
      N = max_returned_objs;
    }

    org.omg.CORBA.ContainerPackage.Description[] descs = new org.omg.CORBA.ContainerPackage.Description[N];
    for (int i = 0; i < N; i++) {
      Any any = orb.create_any();
      org.omg.CORBA.ContainedPackage.DescriptionHelper.insert(any, contained[i].describe());

      descs[i] = new org.omg.CORBA.ContainerPackage.Description(contained[i], contained[i].def_kind(), any);
    }

    return descs;
  }


  // write interface
  public ModuleDef create_module(String id, String name, String version)  {
    checkForPermitedNesting(ModuleDef.class);
    checkForExistence(id, name, version);


    ModuleDefImpl impl = new ModuleDefImpl(id, name, version, this, orb, poa);
    ModuleDefPOATie tie = new ModuleDefPOATie(impl);
    ModuleDef res = null;
    try {
      res = ModuleDefHelper.narrow(poa.servant_to_reference(tie));
      impl.setContained(res);
      impl.setContainer(res);

      addContained(impl);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("ContainerImpl.create_module(String, String, String)", LoggerConfigurator.exceptionTrace(e));
      }
    }


    return res;
  }


  public ConstantDef create_constant(String id, String name, String version, IDLType type, org.omg.CORBA.Any value)  {
    checkForPermitedNesting(ConstantDef.class);
    checkForExistence(id, name, version);

    ConstantDefImpl impl = new ConstantDefImpl(id, name, version, type, value, this, orb, poa);
    ConstantDefPOATie tie = new ConstantDefPOATie(impl);
    ConstantDef res = null;
    try {
      res = ConstantDefHelper.narrow(poa.servant_to_reference(tie));
      impl.setContained(res);

      addContained(impl);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("ContainerImpl.create_constant(String, String, String, IDLType, Any)", LoggerConfigurator.exceptionTrace(e));
      }
    }

    return res;
  }

  public StructDef create_struct(String id, String name, String version, org.omg.CORBA.StructMember[] members)  {
    checkForPermitedNesting(StructDef.class);
    checkForExistence(id, name, version);

    StructDefImpl impl =  new StructDefImpl(id, name, version, members, this, orb, poa);
    StructDefPOATie tie = new StructDefPOATie(impl);
    StructDef res = null;
    try {
      res = StructDefHelper.narrow(poa.servant_to_reference(tie));
      impl.setContained(res);
      impl.setContainer(res);

      addContained(impl);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("ContainerImpl.create_struct(String, String, String, StructMember[])", LoggerConfigurator.exceptionTrace(e));
      }
    }

    return res;
  }


  public UnionDef create_union(String id, String name, String version, IDLType discriminator_type, org.omg.CORBA.UnionMember[] members)  {
    checkForPermitedNesting(UnionDef.class);
    checkForExistence(id, name, version);

    UnionDefImpl impl = new UnionDefImpl(id, name, version, discriminator_type, members, this, orb, poa);
    UnionDefPOATie tie = new UnionDefPOATie(impl);
    UnionDef res = null;
    try {
      res = UnionDefHelper.narrow(poa.servant_to_reference(tie));
      impl.setContained(res);
      impl.setContainer(res);

      addContained(impl);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("ContainerImpl.create_union(String, String, String, IDLType, UnionMember[])", LoggerConfigurator.exceptionTrace(e));
      }
    }

    return res;
  }


  public EnumDef create_enum(String id, String name, String version, String[] members)  {
    checkForPermitedNesting(EnumDef.class);
    checkForExistence(id, name, version);

    EnumDefImpl impl = new EnumDefImpl(id, name, version, members, this, orb, poa);
    EnumDefPOATie tie = new EnumDefPOATie(impl);
    EnumDef res = null;
    try {
      res = EnumDefHelper.narrow(poa.servant_to_reference(tie));
      impl.setContained(res);

      addContained(impl);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("ContainerImpl.create_enum(String, String, String, String[])", LoggerConfigurator.exceptionTrace(e));
      }
    }

    return res;
  }


  public AliasDef create_alias(String id, String name, String version, IDLType original_type)  {
    // AliasDef instances have the same nesting policy as the TypedefDef instances
    checkForPermitedNesting(TypedefDef.class);
    checkForExistence(id, name, version);

    AliasDefImpl impl = new AliasDefImpl(id, name, version, original_type, this, orb, poa);
    AliasDefPOATie tie = new AliasDefPOATie(impl);
    AliasDef res = null;
    try {
      res = AliasDefHelper.narrow(poa.servant_to_reference(tie));
      impl.setContained(res);

      addContained(impl);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("ContainerImpl.create_alias(String, String, String, IDLType)", LoggerConfigurator.exceptionTrace(e));
      }
    }

    return res;
  }


  public ExceptionDef create_exception(String id, String name, String version, org.omg.CORBA.StructMember[] members)  {
    checkForPermitedNesting(ExceptionDef.class);
    checkForExistence(id, name, version);

    ExceptionDefImpl impl = new ExceptionDefImpl(id, name, version, members, this, orb, poa);
    ExceptionDefPOATie tie = new ExceptionDefPOATie(impl);
    ExceptionDef res = null;
    try {
      res = ExceptionDefHelper.narrow(poa.servant_to_reference(tie));
      impl.setContained(res);
      impl.setContainer(res);

      addContained(impl);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("ContainerImpl.create_exception(String, String, String, StructMember[])", LoggerConfigurator.exceptionTrace(e));
      }
    }

    return res;
  }


  public InterfaceDef create_interface(String id, String name, String version, boolean is_abstract, InterfaceDef[] base_interfaces)  {
    checkForPermitedNesting(InterfaceDef.class);
    checkForExistence(id, name, version);

    InterfaceDefImpl impl = new InterfaceDefImpl(id, name, version, is_abstract, base_interfaces, this, orb, poa);
    InterfaceDefPOATie tie = new InterfaceDefPOATie(impl);
    InterfaceDef res = null;
    try {
      res = InterfaceDefHelper.narrow(poa.servant_to_reference(tie));
      impl.setContained(res);
      impl.setContainer(res);

      addContained(impl);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("ContainerImpl.create_interface(String, String, String, boolean, InterfaceDef[])", LoggerConfigurator.exceptionTrace(e));
      }
    }

    return res;
  }

  public ValueBoxDef create_value_box(String id, String name, String version, IDLType original_type_def) {
    checkForPermitedNesting(TypedefDef.class);
    checkForExistence(id, name, version);

    ValueBoxDefImpl impl = new ValueBoxDefImpl(id, name, version, original_type_def, this, orb, poa);
    ValueBoxDefPOATie tie = new ValueBoxDefPOATie(impl);
    ValueBoxDef res = null;
    try {
      res = ValueBoxDefHelper.narrow(poa.servant_to_reference(tie));
      impl.setContained(res);

      addContained(impl);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("ContainerImpl.create_value_box(String, String, String, IDLType)", LoggerConfigurator.exceptionTrace(e));
      }
    }

    return res;
  }


  public NativeDef create_native(String id, String name, String version)  {
    checkForPermitedNesting(TypedefDef.class);
    checkForExistence(id, name, version);

    NativeDefImpl impl =  new NativeDefImpl(id, name, version, this, orb, poa);
    NativeDefPOATie tie = new NativeDefPOATie(impl);
    NativeDef res = null;
    try {
      res = NativeDefHelper.narrow(poa.servant_to_reference(tie));
      impl.setContained(res);

      addContained(impl);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("ContainerImpl.create_native(String, String, String)", LoggerConfigurator.exceptionTrace(e));
      }
    }

    return res;
  }

  public ValueDef create_value(String id, String name, String version, boolean is_custom, boolean is_abstract, byte flags, ValueDef base_value, boolean has_safe_base, ValueDef[] abstract_base_values, InterfaceDef[] supported_interfaces, Initializer[] initializers)  {
    checkForPermitedNesting(ValueDef.class);
    checkForExistence(id, name, version);

    ValueDefImpl impl = new ValueDefImpl(id, name, version, is_custom, is_abstract, flags,
                                         base_value, has_safe_base, abstract_base_values,
                                         supported_interfaces, initializers, this, orb, poa);
    ValueDefPOATie tie = new ValueDefPOATie(impl);
    ValueDef res = null;
    try {
      res = ValueDefHelper.narrow(poa.servant_to_reference(tie));
      impl.setContained(res);
      impl.setContainer(res);

      addContained(impl);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("ContainerImpl.create_value(String, String, String, boolean, boolean, byte, ValueDef, boolean, ValueDeff[], InterfaceDef[], Initializer[])", LoggerConfigurator.exceptionTrace(e));
      }
    }

    return res;
  }



  // specific methods for this implementation

  public Container getContainer() {
    return _container;
  }

  public void setContainer(Container container) {
    this._container = container;
  }

  public void destroy()  {
    Iterator iter = componentsByName.values().iterator();
    while (iter.hasNext()) {
      ContainedImpl contained = (ContainedImpl) iter.next();
      contained.destroy();
    }

    componentsByName = null;
    componentsByID = null;
    componentIDs = null;
  }

  void removeContained(ContainedOperations contained)  {
    componentsByName.remove(contained.name());
    componentsByID.remove(contained.id());
    componentIDs.remove(contained.id());

    repository.getAllIDs().remove(contained.id());
  }

  void addContained(ContainedOperations contained)  {
    componentsByName.put(contained.name(), contained);
    componentsByID.put(contained.id(), contained);
    componentIDs.add(contained.id());

    repository.getAllIDs().add(contained.id());
  }

  void checkForPermitedNesting(Class child)  {
    if (!Nesting.checkForPermitedNesting(this.getClass(), child))
      throw new org.omg.CORBA.BAD_PARAM(4, CompletionStatus.COMPLETED_NO);
  }

  void checkForExistence(String id, String name, String version)  {
    if (componentsByName.get(name) != null) {
      throw new org.omg.CORBA.BAD_PARAM(3, CompletionStatus.COMPLETED_NO);
    }

    if (repository.getAllIDs().contains(id)) {
      throw new org.omg.CORBA.BAD_PARAM(2, CompletionStatus.COMPLETED_NO);
    }
    // version is not supported in this implementation of IR (yet)
  }


  // specific methods for this implementation
  private void setRepositoryImpl() {
    if (this instanceof RepositoryImpl) {
      repository = (RepositoryImpl)this;
    }
    else {
      repository = ((ContainedContainer)this).getDefinedInImpl().getRepositoryImpl();
    }
  }

  RepositoryImpl getRepositoryImpl()   {
    return repository;
  }
}
