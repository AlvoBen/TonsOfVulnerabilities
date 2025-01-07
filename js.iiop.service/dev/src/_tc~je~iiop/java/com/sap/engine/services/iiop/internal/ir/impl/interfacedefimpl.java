package com.sap.engine.services.iiop.internal.IR.impl;

import org.omg.CORBA.ContainedPackage.*;
import org.omg.CORBA.InterfaceDefPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.POA;

import java.util.ArrayList;
import java.util.Iterator;

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
public class InterfaceDefImpl extends ContainedContainer implements InterfaceDefOperations {
  private InterfaceDef[] base_interfaces = null;
  private boolean is_abstract = false;

  public InterfaceDefImpl(String id, String name, String version, boolean is_abstract, InterfaceDef[] base_interfaces, ContainerImpl container, ORB orb, POA poa)  {
    super(id, name, version, container, DefinitionKind.dk_Interface, orb, poa);

    this.base_interfaces(base_interfaces);
    this.is_abstract = is_abstract;
  }

  // read/write interface
  public InterfaceDef[] base_interfaces()  {
    return base_interfaces;
  }

  public void base_interfaces(InterfaceDef[] newBase_interfaces)   {
    for (int i = 0; i < newBase_interfaces.length; i++) {
      Iterator iter = componentsByName.values().iterator();
      while (iter.hasNext()) {
        String name = (String) iter.next();
        Contained[] res = newBase_interfaces[i].lookup_name(name, 1, DefinitionKind.dk_all, false);

        if (res.length > 0)
          throw new BAD_PARAM(5, CompletionStatus.COMPLETED_NO);
      }
    }

    base_interfaces = newBase_interfaces;
  }

  public boolean is_abstract()  {
    return is_abstract;
  }

  public void is_abstract(boolean newIs_abstract)  {
    is_abstract = newIs_abstract;
  }

  // read interface
  public boolean is_a(String interface_id)   {
    if (id().equals(interface_id) ||
       "IDL:omg.org/CORBA/Object:1.0".equals(interface_id))
      return true;

    // it is possible neverending recursion if there is
    // cyclic inheritance
    for (int i = 0; i < base_interfaces.length; i++) {
      if (base_interfaces[i].is_a(interface_id))
        return true;
    }

    return false;
  }

  public org.omg.CORBA.ContainedPackage.Description describe()   {
    Any any = orb.create_any();
    InterfaceDescriptionHelper.insert(any, new InterfaceDescription(name(), id(), defined_in_id(), version(), getBaseInterfacesIDs()));

    return new Description(def_kind(), any);
  }

  public FullInterfaceDescription describe_interface()   {
    String name = name();
    String id = id();
    String version = version();
    String defined_in = defined_in_id();
    TypeCode type = type();
    String[] baseInterfacesIDs = getBaseInterfacesIDs();

    Contained[] ops = contents(DefinitionKind.dk_Operation, true);
    Contained[] attrs = contents(DefinitionKind.dk_Attribute, true);

    OperationDescription[] operationsDescs = getOperationsDescriptions(ops);
    AttributeDescription[] attributesDescs = getAttributesDescriptions(attrs);

    FullInterfaceDescription fullDesc = new FullInterfaceDescription(name, id, defined_in, version, is_abstract, operationsDescs, attributesDescs, baseInterfacesIDs, type);

    return fullDesc;
  }

  public TypeCode type()  {
    TypeCode typecode = orb.create_interface_tc(id(), name());

    return typecode;
  }

  public org.omg.CORBA.Contained[] contents(org.omg.CORBA.DefinitionKind limit_type, boolean exclude_inherited)  {
    ArrayList list = new ArrayList();

    Iterator iter = componentIDs.iterator();
    while (iter.hasNext()) {
      String id = (String) iter.next();
      ContainedImpl contained = (ContainedImpl) componentsByID.get(id);
      if ((limit_type.value() == DefinitionKind._dk_all) ||
         (contained.def_kind().value() == limit_type.value())) {
        list.add(contained.getContained());
      }
    }

    if (!exclude_inherited) {
      for (int i = 0; i < base_interfaces.length; i++) {
        Contained[] _contained = base_interfaces[i].contents(limit_type, exclude_inherited);
        for (int j = 0; j < _contained.length; j++) {
          list.add(_contained[j]);
        }
      }
    }

    return (Contained[]) list.toArray(new Contained[0]);
  }


  public org.omg.CORBA.Contained[] lookup_name(String search_name, int levels_to_search, org.omg.CORBA.DefinitionKind limit_type, boolean exclude_inherited)   {
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

    if (!exclude_inherited) {
      for (int i = 0; i < base_interfaces.length; i++) {
         Contained[] _contained = base_interfaces[i].lookup_name(search_name, levels_to_search, limit_type, exclude_inherited);
         for (int j = 0; j < _contained.length; j++) {
           list.add(_contained[j]);
         }
      }
    }

    return (Contained[]) list.toArray(new Contained[list.size()]);
  }

  // write interface
  public AttributeDef create_attribute(String id, String name, String version, IDLType type, AttributeMode mode)   {
    checkForPermitedNesting(AttributeDef.class);
    checkForExistence(id, name, version);

    AttributeDefImpl impl = new AttributeDefImpl(id, name, version, type, mode, this, orb, poa);
    AttributeDefPOATie tie = new AttributeDefPOATie(impl);
    AttributeDef res = null;
    try {
      res = AttributeDefHelper.narrow(poa.servant_to_reference(tie));
      impl.setContained(res);
      // impl.setContainer(res);

      addContained(impl);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("InterfaceDefImpl.create_attribute(String, String, String, IDLType, AttributeMode)", LoggerConfigurator.exceptionTrace(e));
      }      
    }

    return res;
  }

  public OperationDef create_operation(String id, String name, String version, IDLType result, OperationMode mode, ParameterDescription[] params, ExceptionDef[] exceptions, String[] contexts)   {
    checkForPermitedNesting(OperationDef.class);
    checkForExistence(id, name, version);

    OperationDefImpl impl = new OperationDefImpl(id, name, version, result, mode, params, exceptions, contexts, this, orb, poa);
    OperationDefPOATie tie = new OperationDefPOATie(impl);
    OperationDef res = null;
    try {
      res = OperationDefHelper.narrow(poa.servant_to_reference(tie));
      impl.setContained(res);

      addContained(impl);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("InterfaceDefImpl.create_operation(String, String, String, IDLType, OperationMode, ParameterDescription[], ExceptionDef[], String[])", LoggerConfigurator.exceptionTrace(e));
      }
    }

    return res;
  }


  // util methods

  private String[] getBaseInterfacesIDs()   {
    String[] names = new String[base_interfaces.length];

    for (int i = 0; i < base_interfaces.length; i++) {
      names[i] = base_interfaces[i].id();
    }

    return names;
  }

  private AttributeDescription[] getAttributesDescriptions(Contained[] defs)  {
    AttributeDescription[] desc = new AttributeDescription[defs.length];

    for (int i = 0; i < defs.length; i++) {
      desc[i] = AttributeDescriptionHelper.extract(defs[i].describe().value);
    }

    return desc;
  }

  private OperationDescription[] getOperationsDescriptions(Contained[] defs)   {
    OperationDescription[] desc = new OperationDescription[defs.length];

    for (int i = 0; i < defs.length; i++) {
      desc[i] = OperationDescriptionHelper.extract(defs[i].describe().value);
    }

    return desc;
  }

}
