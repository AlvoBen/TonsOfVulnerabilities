/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.iiop.PortableServer;

import com.sap.engine.services.iiop.CORBA.ServerRequestImpl;
import com.sap.engine.services.iiop.CORBA.GIOPMessageConstants;
import com.sap.engine.services.iiop.CORBA.util.ExceptionUtility;
import com.sap.engine.services.iiop.PortableServer.state.StateAction;
import com.sap.engine.services.iiop.internal.TargetHolder;
import com.sap.engine.services.iiop.internal.giop.IncomingRequest;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.services.iiop.client.ConnectionParser;
import org.omg.CORBA.Any;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA_2_3.portable.ObjectImpl;
import org.omg.PortableServer.DynamicImplementation;
import org.omg.PortableServer.Servant;

public abstract class ServantHolder extends ObjectImpl implements TargetHolder {

  private POAImpl poa;
  protected byte[] oid;
  protected String[] ids;


  public ServantHolder(POAImpl poa, byte[] oid, String[] ids) {
    this.oid = oid;
    this.poa = poa;
    this.ids = ids;
  }

  public void invoke(IncomingRequest request) throws Throwable {
    StateAction state = poa.get_state();
    try {
      state.preinvoke(request);
      deliverRequest(request);
    } finally {
      state.postinvoke(request);
    }
  }


  public String[] _ids() {
    return ids;
  }

  void invokeInvokeHandler(InvokeHandler handler, IncomingRequest request) throws Throwable {
    if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beInfo()) {
      LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).infoT("ServantHolder.invokeInvokeHandler(InvokeHandler, IncomingRequest)", "Operation:" + request.operation());
    }

    if (request.operation().equals("_is_a")) {
      try {
        String id = request.read_string();
        //boolean result = ((org.omg.CORBA.Object)handler)._is_a(id);
        boolean result = is_a(id);
        org.omg.CORBA.portable.OutputStream out = request.createReply();
        out.write_boolean(result);
      } catch (Exception ex) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ServantHolder.invokeInvokeHandler(InvokeHandler, IncomingRequest)", LoggerConfigurator.exceptionTrace(ex));
        }
        request.createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName(ex.getClass().getName()), 1, 2);
      }
    } else if (request.operation().equals("_non_existent")) {
      request.createExceptionReply((byte) ConnectionParser.SYSTEM_EXCEPTION, "org.omg.CORBA.NO_IMPLEMENT", 1, 2);
    } else {
      // Set ThreadContextClassLoader
      ClassLoader incomingLoader = Thread.currentThread().getContextClassLoader();
      try {
        Thread.currentThread().setContextClassLoader(handler.getClass().getClassLoader());
        handler._invoke(request.operation(), request, request);
      } finally {
        Thread.currentThread().setContextClassLoader(incomingLoader);
      }
    }

  }

  void invokeDI(DynamicImplementation di, IncomingRequest request) throws Throwable {
    if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beInfo()) {
      LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).infoT("ServantHolder.invokeDI(InvokeHandler, IncomingRequest)", "Operation:" + request.operation());
    }

    if (request.operation().equals("_is_a")) {
      try {
        String id = request.read_string();
        boolean result = di._is_a(id);
        org.omg.CORBA.portable.OutputStream out = request.createReply();
        out.write_boolean(result);
      } catch (Exception ex) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ServantHolder.invokeDI(DynamicImplementation, IncomingRequest)", LoggerConfigurator.exceptionTrace(ex));
        }
        request.createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName(ex.getClass().getName()), 1, 2);
      }
    } else if (request.operation().equals("_non_existent")) {
      try {
        boolean result = di._non_existent();
        org.omg.CORBA.portable.OutputStream out = request.createReply();
        out.write_boolean(result);
      } catch (Exception ex) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ServantHolder.invokeDI(DynamicImplementation, IncomingRequest)", LoggerConfigurator.exceptionTrace(ex));
        }
        request.createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName(ex.getClass().getName()), 1, 2);
      }
    } else {
      // Set ThreadContextClassLoader
      ClassLoader incomingLoader = Thread.currentThread().getContextClassLoader();
      try {
        Thread.currentThread().setContextClassLoader(di.getClass().getClassLoader());
        ServerRequestImpl serverRequest = new ServerRequestImpl(di._orb(), request.operation());
        serverRequest.setInputStream(request);
        di.invoke(serverRequest);
        byte exType = serverRequest.hasException();
        org.omg.CORBA.portable.OutputStream out;
        if (exType != 0) { // exception...
          Any exception = serverRequest.get_result();
          out = request.createExceptionReply(exType, exception.type().id(), 1, 2);
        } else {
          out = request.createReply();
        }
        serverRequest.writeResult((com.sap.engine.services.iiop.CORBA.portable.CORBAOutputStream) out);
      } finally {
        Thread.currentThread().setContextClassLoader(incomingLoader);
      }
    }
  }

  void deliverRequest(IncomingRequest request) throws Throwable {
    Servant servant = locateServant();
    if (servant instanceof DynamicImplementation) {
      invokeDI((DynamicImplementation) servant, request);
    } else {
      invokeInvokeHandler((InvokeHandler) servant, request);
    }
  }

  public Servant locateServant() throws Exception {
    //return poa._RequestProcessingPolicy().locateServant();
    return poa.getPolicyObject().locateServant(oid);
  }

  private boolean is_a(String id) {
    for (int i = 0; i < ids.length; i++) {
      if (ids[i].equals(id)) {
        return true;
      }
    }
    return false;
  }
}
