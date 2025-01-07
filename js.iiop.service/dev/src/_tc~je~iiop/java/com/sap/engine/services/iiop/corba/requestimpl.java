/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.CORBA;


import com.sap.engine.services.iiop.CORBA.portable.CORBAInputStream;
import com.sap.engine.services.iiop.CORBA.portable.CORBAOutputStream;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import org.omg.CORBA.*;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.ObjectImpl;

/**
 * An object containing the information necessary for
 * invoking a method.  This class is
 * the cornerstone of the ORB Dynamic
 * Invocation Interface (DII), which allows dynamic creation and
 * invocation of requests.
 * A server cannot tell the difference between a client
 * invocation using a client stub and a request using the DII.
 * <P>
 * A <code>RequestImpl</code> object consists of:
 * <UL>
 * <LI>the name of the operation to be invoked
 * <LI>an <code>NVList</code> containing arguments for the operation.<BR>
 * Each item in the list is a <code>NamedValue</code> object, which has three
 * parts:
 *  <OL>
 *    <LI>the name of the argument
 *    <LI>the value of the argument (as an <code>Any</code> object)
 *    <LI>the argument mode flag indicating whether the argument is
 *        for input, output, or both
 *  </OL>
 * </UL>
 * <P>
 * <code>RequestImpl</code> objects may also contain additional information,
 * depending on how an operation was defined in the original IDL
 * interface definition.  For example, where appropriate, they may contain
 * a <code>NamedValue</code> object to hold the return value or exception,
 * a context, a list of possible exceptions, and a list of
 * context strings that need to be resolved.
 * <P>
 * New <code>RequestImpl</code> objects are created using one of the
 * <code>create_request</code> methods in the <code>DelegateImpl</code> class.
 * In other words, a <code>create_request</code> method is performed on the
 * object which is to be invoked.
 *
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public class RequestImpl extends Request {

  private boolean response_expected = true; // default
  private String _operation = null;
  private NVList _arguments = null;
  private Context _context = null;
  private TypeCode _return_tc = null;
  private NamedValue _result = null;
  private Environment _environment = null;
  private ContextList _contextList = null;
  private ExceptionList _exceptions = new ExceptionListImpl();
  private org.omg.CORBA.Object _target = null;
  private org.omg.CORBA.ORB orb;
  private int status = GIOPMessageConstants.NO_EXCEPTION;
//  private static int requestID = 0x05;
  //?????????
//  private java.io.InputStream inStream;
//  private java.io.OutputStream outStream;
  //?????????
//  private int connection = 0;

//  public void setInputStream(java.io.InputStream in) {
//    inStream = in;
//  }

//  public void setOutputStream(java.io.OutputStream out) {
//    outStream = out;
//  }

//  public void setIOStreams(java.io.InputStream in, java.io.OutputStream out) {
//    inStream = in;
//    outStream = out;
//  }

  public int getStatus() {
    return status;
  }

  public RequestImpl(org.omg.CORBA.ORB orb0) {
    orb = orb0;
    _environment = new EnvironmentImpl();
  }

  public RequestImpl(org.omg.CORBA.ORB orb0, org.omg.CORBA.Object _target0, String _operation0) {
    orb = orb0;
    _target = _target0;

    if (_operation0 == null || _operation0.equals("")) {
      _operation = "get";
    } else {
      _operation = _operation0;
    }

    _environment = new EnvironmentImpl();
    _arguments = new NVListImpl(orb);
    _context = new ContextImpl(orb);
    _exceptions = new ExceptionListImpl();
    _contextList = new ContextListImpl();
  }

  public RequestImpl(org.omg.CORBA.ORB orb0, org.omg.CORBA.Object _target0, Context _context0, String _operation0, NVList _arguments0, NamedValue _result0, ExceptionList _exceptions0, ContextList _contextList0) {
    this(orb0);
    _target = _target0;

    if (_context0 == null) {
      _context = new ContextImpl(orb);
    } else {
      _context = _context0;
    }

    if (_operation0 == null || _operation0.equals("")) {
      _operation = "get";
    } else {
      _operation = _operation0;
    }

    if (_arguments0 == null) {
      _arguments = new NVListImpl(orb);
    } else {
      _arguments = _arguments0;
    }

    _result = _result0;

    if (_exceptions0 == null) {
      _exceptions = new ExceptionListImpl();
    } else {
      _exceptions = _exceptions0;
    }

    if (_contextList0 == null) {
      _contextList = new ContextListImpl();
    } else {
      _contextList = _contextList0;
    }
  }

  public Any add_in_arg() {
    return _arguments.add(ARG_IN.value).value();
  }

  public Any add_inout_arg() {
    return _arguments.add(ARG_INOUT.value).value();
  }

  public Any add_named_in_arg(String name) {
    return _arguments.add_item(name, ARG_IN.value).value();
  }

  public Any add_named_inout_arg(String name) {
    return _arguments.add_item(name, ARG_INOUT.value).value();
  }

  public Any add_named_out_arg(String name) {
    return _arguments.add_item(name, ARG_OUT.value).value();
  }

  public Any add_out_arg() {
    return _arguments.add(ARG_OUT.value).value();
  }

  public NVList arguments() {
    return _arguments;
  }

  public ContextList contexts() {
    return _contextList;
  }

  public Context ctx() {
    return _context;
  }

  public void ctx(Context ctx) {
    _context = ctx;
  }

  public Environment env() {
    return _environment;
  }

  public ExceptionList exceptions() {
    return _exceptions;
  }

  public void get_response() throws WrongTransaction {
    throw new WrongTransaction("ID019045: get_response() is not implemented");
  }

  public void invoke() throws org.omg.CORBA.SystemException {
    CORBAOutputStream out = (com.sap.engine.services.iiop.CORBA.portable.CORBAOutputStream) ((ObjectImpl) _target)._request(_operation, response_expected);
    write(out);
    try {
      CORBAInputStream in = (com.sap.engine.services.iiop.CORBA.portable.CORBAInputStream) ((ObjectImpl) _target)._invoke(out);

      if (_return_tc != null) {
        if (_result == null) {
          AnyImpl _any = new AnyImpl(orb);
          _any.type(_return_tc);
          _result = new NamedValueImpl(orb, "", _any, ARG_OUT.value);
        }

        _result.value().read_value(in, _return_tc);
      }

      try {
        for (int i = 0; i < _arguments.count(); i++) {
          NamedValue nv = _arguments.item(i);

          if (nv.flags() != ARG_IN.value) {
            nv.value().read_value(in, nv.value().type());
          }
        }
      } catch (org.omg.CORBA.Bounds ex) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("RequestImpl.invoke()", LoggerConfigurator.exceptionTrace(ex));
        }
      } catch (Exception e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("RequestImpl.invoke()", LoggerConfigurator.exceptionTrace(e));
        }
      }
    } catch (ApplicationException aex) {
      CORBAInputStream in = (CORBAInputStream) aex.getInputStream();
      int i = in.getPos();
      String id = in.read_string();
      in.reset(i);
      try {
        for (int k = 0; k < _exceptions.count(); k++) {
          TypeCode typecode = _exceptions.item(k);

          if (typecode.id().equals(id)) {
            Any any = orb.create_any();
            any.read_value(in, typecode);
            _environment.exception(new UnknownUserException(any));
            return;
          }
        }
      } catch (Exception bex) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("RequestImpl.invoke()", LoggerConfigurator.exceptionTrace(bex));
        }
      }
      UNKNOWN unknown = new UNKNOWN(0x53550001, CompletionStatus.COMPLETED_MAYBE);
      throw unknown;
    } catch (Exception appEx) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("RequestImpl.invoke()", LoggerConfigurator.exceptionTrace(appEx));
      }
      UNKNOWN unknown = new UNKNOWN(0x53550001, CompletionStatus.COMPLETED_MAYBE);
      _environment.exception(appEx);
      throw unknown;
    }
  }

  public String operation() {
    return _operation;
  }

  public boolean poll_response() {
    throw new org.omg.CORBA.NO_IMPLEMENT("ID019046 poll_response() is not implemented");
  }

  public NamedValue result() {
    return _result;
  }

  public Any return_value() {
    return _result.value();
  }

  public void send_deferred() {
    throw new org.omg.CORBA.NO_IMPLEMENT("ID019047 send_deferred() is not implemented");
  }

  public void send_oneway() {
    response_expected = true;
  }

  public void set_return_type(TypeCode tc) {
    _return_tc = tc;
  }

  public org.omg.CORBA.Object target() {
    return _target;
  }

  private void write(CORBAOutputStream out) {
    try {
      for (int i = 0; i < _arguments.count(); i++) {
        NamedValue nv = _arguments.item(i);

        if (nv.flags() != ARG_OUT.value) { // nv.flags() e ili ARG_IN ili ARG_INOUT
          nv.value().write_value(out);
        }
      }
    } catch (org.omg.CORBA.Bounds ex) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("RequestImpl.write(CORBAOutputStream)", LoggerConfigurator.exceptionTrace(ex));
      }
    }
  }

  //  private byte[] toByteArray() {
  //    IOR ior = ((DelegateImpl) ((ObjectImpl) _target)._get_delegate()).getIOR();
  //    RequestMessage m = new RequestMessage(orb, null, ++requestID, response_expected, ior.getProfile().getObjectKey(), _operation);
  //    CORBAOutputStream out = null; //m.getRequestHeader();
  //    try {
  //      for (int i = 0; i < _arguments.count(); i++) {
  //        NamedValue nv = _arguments.item(i);
  //
  //        if (nv.flags() != ARG_OUT.value) { // nv.flags() e ili ARG_IN ili ARG_INOUT
  //          nv.value().write_value(out);
  //        }
  //      }
  //    } catch (org.omg.CORBA.Bounds ex) {
  //
  //    }
  //    return new byte[0];
  //  }

}// RequestImpl

