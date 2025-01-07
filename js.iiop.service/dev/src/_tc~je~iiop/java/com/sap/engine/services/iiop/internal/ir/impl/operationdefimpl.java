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
public class OperationDefImpl extends ContainedImpl implements OperationDefOperations {
  private IDLType result_def = null;
  private OperationMode mode = null;
  private ParameterDescription[] params = null;
  private ExceptionDef[] exceptions = null;
  private String[] contexts = null;

  public OperationDefImpl(String id, String name, String version, IDLType result_def, OperationMode mode, ParameterDescription[] params, ExceptionDef[] exceptions, String[] contexts, ContainerImpl container, ORB orb, POA poa)  {
    super(id, name, version, container, DefinitionKind.dk_Operation, orb, poa);

    this.result_def = result_def;
    this.params = params;
    this.exceptions = exceptions;
    this.contexts = contexts;

    this.mode(mode);
  }

  public org.omg.CORBA.TypeCode result()   {
    return result_def.type();
  }

  public Description describe()   {
    ExceptionDescription exceptionsDescs[] = new ExceptionDescription[exceptions.length];

    for (int i = 0; i < exceptions.length; i++)
      exceptionsDescs[i] = ExceptionDescriptionHelper.extract(exceptions[i].describe().value);

    Any any = orb.create_any();
    OperationDescriptionHelper.insert(any, new OperationDescription(name, id, defined_in_id(), version, result(), mode, contexts, params, exceptionsDescs));

    return new Description(def_kind(), any);
  }

  public IDLType result_def()   {
    return result_def;
  }

  public void result_def(IDLType newResult_def)   {
    result_def = newResult_def;
  }

  public ParameterDescription[] params()   {
    return params;
  }

  public void params(ParameterDescription[] newParams) {
    params = newParams;
  }

  public OperationMode mode()   {
    return mode;
  }

  public void mode(OperationMode newMode)   {
    if (newMode.equals(OperationMode.OP_ONEWAY) &&
       result().kind().equals(TCKind.tk_void) &&
       exceptions.length == 0) {
      for (int i = 0; i < params.length; i++) {
        if (!params[i].mode.equals(ParameterMode.PARAM_IN))
          throw new BAD_PARAM(31, CompletionStatus.COMPLETED_NO);
      }
    }
    else {
      throw new BAD_PARAM(31, CompletionStatus.COMPLETED_NO);
    }

    mode = newMode;
  }

  public String[] contexts()   {
    return contexts;
  }

  public void contexts(String[] newContexts)   {
    contexts = newContexts;
  }

  public ExceptionDef[] exceptions()   {
    return exceptions;
  }

  public void exceptions(ExceptionDef[] newExceptions)   {
    exceptions = newExceptions;
  }


}
