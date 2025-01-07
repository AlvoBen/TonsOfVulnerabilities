package com.sap.engine.services.iiop.internal.IR.impl;

import java.util.HashMap;
import java.util.ArrayList;
import org.omg.CORBA.*;



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
public class Nesting {
  private static HashMap nestingHirarchy = new HashMap();

  private static ArrayList repositoryChildList = new ArrayList();
  private static ArrayList moduleChildList = new ArrayList();
  private static ArrayList interfaceChildList = new ArrayList();
  private static ArrayList valueChildList = new ArrayList();
  private static ArrayList structUnionExceptionChildList = new ArrayList();

  static {
    repositoryChildList.add(ConstantDef.class);
    repositoryChildList.add(TypedefDef.class);
    repositoryChildList.add(ExceptionDef.class);
    repositoryChildList.add(InterfaceDef.class);
    repositoryChildList.add(ValueDef.class);
    repositoryChildList.add(ValueBoxDef.class);
    repositoryChildList.add(ModuleDef.class);

    nestingHirarchy.put(Repository.class, repositoryChildList);

    // The contained classes in moduleChildList is the same like repositoryChildList,
    // but I used different instance for future specification differences

    moduleChildList.add(ConstantDef.class);
    moduleChildList.add(TypedefDef.class);
    moduleChildList.add(ExceptionDef.class);
    moduleChildList.add(InterfaceDef.class);
    moduleChildList.add(ValueDef.class);
    moduleChildList.add(ValueBoxDef.class);
    moduleChildList.add(ModuleDef.class);

    nestingHirarchy.put(ModuleDef.class, moduleChildList);

    interfaceChildList.add(ConstantDef.class);
    interfaceChildList.add(TypedefDef.class);
    interfaceChildList.add(ExceptionDef.class);
    interfaceChildList.add(AttributeDef.class);
    interfaceChildList.add(OperationDef.class);

    nestingHirarchy.put(InterfaceDef.class, interfaceChildList);

    valueChildList.add(ConstantDef.class);
    valueChildList.add(TypedefDef.class);
    valueChildList.add(ExceptionDef.class);
    valueChildList.add(AttributeDef.class);
    valueChildList.add(OperationDef.class);
    valueChildList.add(ValueMemberDef.class);

    nestingHirarchy.put(ValueDef.class, valueChildList);

    structUnionExceptionChildList.add(StructDef.class);
    structUnionExceptionChildList.add(UnionDef.class);
    structUnionExceptionChildList.add(EnumDef.class);

    nestingHirarchy.put(StructDef.class, structUnionExceptionChildList);
    nestingHirarchy.put(UnionDef.class, structUnionExceptionChildList);
    nestingHirarchy.put(ExceptionDef.class, structUnionExceptionChildList);

  }

  public static boolean checkForPermitedNesting(Class parent, Class child) {
    ArrayList list = (ArrayList) nestingHirarchy.get(parent);

    if ((list == null) ||
       (!list.contains(child)))
      return false;

    return true;
  }
}
