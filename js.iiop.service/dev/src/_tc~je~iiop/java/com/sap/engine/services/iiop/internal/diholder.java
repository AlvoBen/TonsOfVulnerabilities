/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.iiop.internal;

import com.sap.engine.services.iiop.CORBA.ServerRequestImpl;
import com.sap.engine.services.iiop.CORBA.GIOPMessageConstants;
import com.sap.engine.services.iiop.CORBA.util.ExceptionUtility;
import com.sap.engine.services.iiop.internal.giop.IncomingRequest;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import org.omg.CORBA.Any;
import org.omg.CORBA.DynamicImplementation;
import org.omg.CORBA.Object;

/*
 * @author Georgi Stanev
 * @version 6.30
 */
public class DIHolder implements TargetHolder {

  DynamicImplementation in_target;

  public DIHolder(DynamicImplementation in_target) {
    this.in_target = in_target;
  }

  public void invoke(IncomingRequest request) throws Throwable {
    if (request.operation().equals("_is_a")) {
      try {
        String id = request.read_string();
        boolean result = in_target._is_a(id);
        org.omg.CORBA.portable.OutputStream out = request.createReply();
        out.write_boolean(result);
      } catch (Exception ex) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("DIHolder.invoke(IncomingRequest)", LoggerConfigurator.exceptionTrace(ex));
        }
        request.createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName(ex.getClass().getName()), 1, 2);
      }
    } else if (request.operation().equals("_non_existent")) {
      try {
        boolean result = in_target._non_existent();
        org.omg.CORBA.portable.OutputStream out = request.createReply();
        out.write_boolean(result);
      } catch (Exception ex) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("DIHolder.invoke(IncomingRequest)", LoggerConfigurator.exceptionTrace(ex));
        }
        request.createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName(ex.getClass().getName()), 1, 2);
      }
    } else {
      // Set ThreadContextClassLoader
      ClassLoader incomingLoader = Thread.currentThread().getContextClassLoader();
      try {
        Thread.currentThread().setContextClassLoader(in_target.getClass().getClassLoader());
        ServerRequestImpl serverRequest = new ServerRequestImpl(in_target._orb(), request.operation());
        serverRequest.setInputStream(request);
        in_target.invoke(serverRequest);
        byte exType = serverRequest.hasException();
        org.omg.CORBA.portable.OutputStream out;
        if (exType != 0) { // exception...
          Any exception = serverRequest.get_result();
          out = request.createExceptionReply(exType, exception.type().id() , 1, 2);
        } else {
          out = request.createReply();
        }
        serverRequest.writeResult((com.sap.engine.services.iiop.CORBA.portable.CORBAOutputStream)out);
      } finally {
        Thread.currentThread().setContextClassLoader(incomingLoader);
      }
    }
  }

  public Object getObject() {
    return in_target;
  }

  public int hashCode() {
    return in_target.hashCode();
  }

  public boolean equals(java.lang.Object obj) {
    if (obj instanceof org.omg.CORBA.Object) {
      return in_target.equals(obj);
    } else {
      return super.equals(obj);
    }

  }

  public String toString() {
    return "DynamicInvocation Holder. Wrapped object: " + in_target.toString();
  }
}
