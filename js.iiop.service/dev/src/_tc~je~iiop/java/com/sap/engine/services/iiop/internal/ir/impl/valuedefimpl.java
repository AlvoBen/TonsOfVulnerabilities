package com.sap.engine.services.iiop.internal.IR.impl;

import org.omg.CORBA.*;
import org.omg.CORBA.ValueDefPackage.*;
import org.omg.CORBA.ContainedPackage.*;
import org.omg.PortableServer.POA;

import java.util.Iterator;
import java.util.ArrayList;

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
public class ValueDefImpl extends ContainedContainer implements ValueDefOperations {
  private boolean is_custom = false;
  private boolean is_abstract = false;
  private byte flags = 0;
  private ValueDef base_value = null;
  private ValueDef[] abstract_base_values = null;
  private boolean has_safe_base = false;
  private InterfaceDef[] supported_interfaces = null;
  private Initializer[] initializers = null;

  public ValueDefImpl(String id, String name, String version, boolean is_custom, boolean is_abstract,
                      byte flags, ValueDef base_value, boolean has_safe_base, ValueDef[] abstract_base_values,
                      InterfaceDef[] supported_interfaces, Initializer[] initializers, ContainerImpl container, ORB orb, POA poa)   {

    super(id, name, version, container, DefinitionKind.dk_Value, orb, poa);

    this.is_custom = is_custom;
    this.is_abstract = is_abstract;
    this.flags = flags;
    this.base_value = base_value;
    this.abstract_base_values = abstract_base_values;
    this.has_safe_base = has_safe_base;
    this.supported_interfaces = supported_interfaces;
    this.initializers = initializers;
  }


  // read/write interface
  public InterfaceDef[] supported_interfaces()   {
    return supported_interfaces;
  }

  public void supported_interfaces(InterfaceDef[] newSupported_interfaces)   {
    for (int i = 0; i < newSupported_interfaces.length; i++) {
      Iterator iter = componentsByName.values().iterator();
      while (iter.hasNext()) {
        String name = (String) iter.next();
        Contained[] res = newSupported_interfaces[i].lookup_name(name, 1, DefinitionKind.dk_all, false);

        if (res.length > 0)
          throw new BAD_PARAM(5, CompletionStatus.COMPLETED_NO);
      }
    }

    this.supported_interfaces = newSupported_interfaces;
  }

  public Initializer[] initializers()   {
    return initializers;
  }

  public void initializers(Initializer[] newInitializers)   {
    this.initializers = newInitializers;
  }

  public ValueDef base_value()   {
    return base_value;
  }

  public void base_value(ValueDef newBase_value)   {
    Iterator iter = componentsByName.values().iterator();
    while (iter.hasNext()) {
      String name = (String) iter.next();
      Contained[] res = newBase_value.lookup_name(name, 1, DefinitionKind.dk_all, false);
      if (res.length > 0)
        throw new BAD_PARAM(5, CompletionStatus.COMPLETED_NO);
    }

    this.base_value = newBase_value;
  }

  public ValueDef[] abstract_base_values()   {
    return abstract_base_values;
  }

  public void abstract_base_values(ValueDef[] newAbstract_base_values)   {
    for (int i = 0; i < newAbstract_base_values.length; i++) {
      Iterator iter = componentsByName.values().iterator();
      while (iter.hasNext()) {
        String name = (String) iter.next();
        Contained[] res = newAbstract_base_values[i].lookup_name(name, 1, DefinitionKind.dk_all, false);

        if (res.length > 0)
          throw new BAD_PARAM(5, CompletionStatus.COMPLETED_NO);
      }
    }

    this.abstract_base_values = newAbstract_base_values;
  }

  public boolean is_abstract()   {
    return is_abstract;
  }

  public void is_abstract(boolean newIs_abstract)   {
    is_abstract = newIs_abstract;
  }

  public boolean is_custom()   {
    return is_custom;
  }

  public void is_custom(boolean newIs_custom)   {
    is_custom = newIs_custom;
  }

  public byte flags()   {
    return flags;
  }

  public void flags(byte newFlags)   {
    flags = newFlags;
  }

  public boolean has_safe_base()   {
    return has_safe_base;
  }

  public void has_safe_base(boolean newHas_safe_base)   {
    has_safe_base = newHas_safe_base;
  }

  // read interface
  public boolean is_a(String value_id)   {
    if (id().equals(value_id) ||
        "IDL:omg.org/CORBA/ValueBase:1.0".equals(value_id) ||
        base_value.is_a(value_id))
      return true;

    for (int i = 0; i < abstract_base_values.length; i++) {
      if (abstract_base_values[i].is_a(value_id))
        return true;
    }

    return false;
  }

  public FullValueDescription describe_value() {
    String name = name();
    String id = id();
    String version = version();
    String defined_in = defined_in_id();
    TypeCode type = type();

    Contained[] ops = contents(DefinitionKind.dk_Operation, true);
    Contained[] attrs = contents(DefinitionKind.dk_Attribute, true);
    Contained[] members = contents(DefinitionKind.dk_ValueMember, true);

    OperationDescription[] operationsDescs = getOperationsDescriptions(ops);
    AttributeDescription[] attributesDescs = getAttributesDescriptions(attrs);
    ValueMember[] vmembers = getValueMembers(members);

    String[] _supported_interfaces = new String[supported_interfaces.length];
    for (int i = 0; i < supported_interfaces.length; i++) {
      _supported_interfaces[i] =  supported_interfaces[i].name();
    }

    String[] _abstract_base_values = new String[abstract_base_values.length];
    for (int i = 0; i < abstract_base_values.length; i++) {
      _abstract_base_values[i] =  abstract_base_values[i].name();
    }

    FullValueDescription fullDesc = new FullValueDescription(name, id, is_abstract, is_custom, flags,
                                                             defined_in, version, operationsDescs,
                                                             attributesDescs, vmembers, initializers,
                                                             _supported_interfaces, _abstract_base_values,
                                                             has_safe_base, base_value.name(), type);

    return fullDesc;
  }

  public Description describe()   {
    Any any = orb.create_any();

    String[] supported_interfaces_ids = new String[supported_interfaces.length];
    for (int i=0;i<supported_interfaces.length;i++) {
      supported_interfaces_ids[i] = supported_interfaces[i].id();
    }

    String[] abstract_base_values_ids = new String[abstract_base_values.length];
    for (int i=0;i<abstract_base_values.length;i++) {
      abstract_base_values_ids[i] = abstract_base_values[i].id();
    }

    ValueDescription desc = new ValueDescription(name(), id(), is_abstract, is_custom,  flags, defined_in_id(), version(), supported_interfaces_ids, abstract_base_values_ids, has_safe_base, base_value.id());
    ValueDescriptionHelper.insert(any, desc);

    return new Description(def_kind(), any);
  }

   public Contained[] contents(org.omg.CORBA.DefinitionKind limit_type, boolean exclude_inherited)   {
    ArrayList list = new ArrayList();

    Iterator iter = componentIDs.iterator();
    while (iter.hasNext()) {
      String id = (String) iter.next();
      Contained contained = ((ContainedImpl)componentsByID.get(id)).getContained();
      if ((limit_type.value() == DefinitionKind._dk_all) ||
         (contained.def_kind().value() == limit_type.value())) {
        list.add(contained);

        if (!exclude_inherited) {
          Contained[] _contained = base_value.contents(limit_type, exclude_inherited);
          for (int i=0;i<_contained.length;i++)
            list.add(_contained[i]);

          for (int i=0;i<abstract_base_values.length;i++) {
            _contained = abstract_base_values[i].contents(limit_type, exclude_inherited);
            for (int j=0;j<_contained.length;j++)
              list.add(_contained[j]);
          }
        }
      }
    }

    return (Contained[])list.toArray(new Contained[list.size()]);
  }

  public TypeCode type()   {
    Contained[] valueMembers = contents(DefinitionKind.dk_ValueMember, true);
    ValueMember[] vm = new ValueMember[valueMembers.length];
    for (int i=0;i<valueMembers.length;i++) {
      vm[i] = ValueMemberHelper.extract(valueMembers[i].describe().value);
    }

    TypeCode typecode = orb.create_value_tc(id(), name(), flags, base_value.type(), vm);

    return typecode;
  }

  // write interface

  public ValueMemberDef create_value_member(String id, String name, String version, IDLType type_def, short access)   {
    checkForPermitedNesting(ValueMemberDef.class);
    checkForExistence(id, name, version);

    ValueMemberDefImpl impl = new ValueMemberDefImpl(id, name, version, type_def, access, this, orb, poa);
    ValueMemberDefPOATie tie = new ValueMemberDefPOATie(impl);
    ValueMemberDef res = null;
    try {
      res = ValueMemberDefHelper.narrow(poa.servant_to_reference(tie));
      impl.setContained(res);

      addContained(impl);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("ValueDefImpl.create_value_member(String, String, String, IDLType, short)", LoggerConfigurator.exceptionTrace(e));
      }
    }

    return res;
  }

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
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("ValueDefImpl.create_attribute(String, String, String, IDLType, AttributeMode)", LoggerConfigurator.exceptionTrace(e));
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
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("ValueDefImpl.createPrimitiveDefs()", LoggerConfigurator.exceptionTrace(e));
      }
    }

    return res;
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

   private ValueMember[] getValueMembers(Contained[] defs)   {
    ValueMember[] desc = new ValueMember[defs.length];

    for (int i = 0; i < defs.length; i++) {
      desc[i] = ValueMemberHelper.extract(defs[i].describe().value);
    }

    return desc;
  }
}
