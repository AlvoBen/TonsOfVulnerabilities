package com.sap.engine.services.iiop.internal;

import com.sap.engine.services.iiop.CORBA.util.ExceptionUtility;
import com.sap.engine.services.iiop.CORBA.GIOPMessageConstants;
import com.sap.engine.services.iiop.internal.giop.IncomingRequest;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;

import org.omg.CORBA.Object;
import org.omg.CORBA.portable.InvokeHandler;



public class InvokeHandlerHolder implements TargetHolder {
 InvokeHandler in_target;

  public InvokeHandlerHolder(InvokeHandler handler) {
    this.in_target = handler;
  }

  public void invoke(IncomingRequest request) throws Throwable {
    if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beInfo()) {
      LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).infoT("InvokeHandlerHolder.invoke(IncomingRequest)", "Operation:"+request.operation());
    }

    if (request.operation().equals("_is_a")) {
      try {
        String id = request.read_string();
        boolean result = ((org.omg.CORBA.Object)in_target)._is_a(id);
        org.omg.CORBA.portable.OutputStream out = request.createReply();
        out.write_boolean(result);
      } catch (Exception ex) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("InvokeHandlerHolder.invoke(IncomingRequest)", LoggerConfigurator.exceptionTrace(ex));
        }
        request.createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName(ex.getClass().getName()), 1, 2);
      }
    } else if (request.operation().equals("_non_existent")) {
      try {
        boolean result = ((org.omg.CORBA.Object) in_target)._non_existent();
        org.omg.CORBA.portable.OutputStream out = request.createReply();
        out.write_boolean(result);
      } catch (Exception ex) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("InvokeHandlerHolder.invoke(IncomingRequest)", LoggerConfigurator.exceptionTrace(ex));
        }
        request.createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName(ex.getClass().getName()), 1, 2);
      }
    } else {
      // Set ThreadContextClassLoader
      ClassLoader incomingLoader = Thread.currentThread().getContextClassLoader();
      try {
        Thread.currentThread().setContextClassLoader(in_target.getClass().getClassLoader());
        in_target._invoke(request.operation(), request, request);
      } finally {
        Thread.currentThread().setContextClassLoader(incomingLoader);
      }
    }
  }

  public Object getObject() {
    return (Object) in_target;
  }

  public int hashCode() {
    return in_target.hashCode();
  }

  public boolean equals(java.lang.Object obj) {
    if (obj instanceof org.omg.CORBA.Object) {
      return in_target.equals((org.omg.CORBA.Object) obj);
    } else {
      return super.equals(obj);
    }
  }

  public String toString() {
    return "InvokeHandler Holder. Wrapped object:" + in_target.toString();
  }
}
