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
import com.sap.engine.services.iiop.CORBA.util.ExceptionUtility;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import org.omg.CORBA.*;

/**
 * An object that captures the explicit state of a request
 * for the Dynamic Skeleton Interface (DSI).  This class, the
 * cornerstone of the DSI, is analogous to the <code>RequestImpl</code>
 * object in the DII.
 * <P>
 * The ORB is responsible for creating this embodiment of a request,
 * and delivering it to a Dynamic Implementation Routine (DIR).
 * A dynamic servant (a DIR) is created by implementing the
 * <code>DynamicImplementation</code> class,
 * which has a single <code>invoke</code> method.  This method accepts a
 * <code>ServerRequestImpl</code> object.
 *
 * The class <code>ServerRequestImpl</code> defines
 * methods for accessing the
 * method name, the arguments and the context of the request, as
 * well as methods for setting the result of the request either as a
 * return value or an exception. <p>
 *
 * A subtlety with accessing the arguments of the request is that the
 * DIR needs to provide type information about the
 * expected arguments, since there is no compiled information about
 * these. This information is provided through an <code>NVListImpl</code>,
 * which is a list of <code>NamedValueImpl</code> objects.
 * Each <code>NamedValueImpl</code> object
 * contains an <code>AnyImpl</code> object, which in turn
 * has a <code>TypeCodeImpl</code> object representing the type
 * of the argument. <p>
 *
 * Similarly, type information needs to be provided for the response,
 * for either the expected result or for an exception, so the methods
 * <code>result</code> and <code>except</code> take an <code>Any</code>
 * object as a parameter. <p>
 *
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public class ServerRequestImpl extends ServerRequest {

  String _operation;
  NVList _arguments;
  Any _exception;
  Any _result;
  ContextImpl _ctx = null;
  CORBAInputStream _inStream = null;
  boolean _isArgumentSet = false;
  boolean _isResultSet = false;
  boolean _isExceptionSet = false;
  private org.omg.CORBA.ORB orb;

  public ServerRequestImpl(org.omg.CORBA.ORB orb0) {
    orb = orb0;
  }

  public ServerRequestImpl(org.omg.CORBA.ORB orb0, String _operation0) {
    orb = orb0;
    _operation = _operation0;
  }

  public void setInputStream(CORBAInputStream in) {
    _inStream = in;
  }

  public void arguments(NVList list) {
    if (_isArgumentSet || _isExceptionSet) {
      String messageWithId = "ID019048: ServerRequestImpl.arguments(): bad invocation order";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ServerRequestImpl.arguments(NVList)", messageWithId);
      }
      throw new BAD_INV_ORDER(messageWithId);
    }

    if (list == null) {
      String messageWithId = "ID019049: ServerRequestImpl.arguments(): null input parameter";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ServerRequestImpl.arguments(NVList)", messageWithId);
      }
      throw new BAD_PARAM(messageWithId);
    }

    NamedValue nv = null;

    for (int i = 0; i < list.count(); i++) {
      try {
        nv = list.item(i);

        if (nv.flags() != org.omg.CORBA.ARG_OUT.value) {
          nv.value().read_value(_inStream, nv.value().type());
        }
      } catch (Bounds e) {
        continue;
      } catch (Exception e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ServerRequestImpl.arguments(NVList)", LoggerConfigurator.exceptionTrace(e));
        }
        String messageWithId = "ID019050: ServerReqeustImpl.arguments(): error while marshalling argument";
        throw new MARSHAL(messageWithId);
      }
    }

    _arguments = list;
    _isArgumentSet = true;
  }

  public Context ctx() {
    return _ctx;
  }

  public String operation() {
    return _operation;
  }

  public void except(Any any) { // TODO - delete :)
    set_exception(any);
  }

  public void set_exception(Any any) {
    if (any == null) {
      String messageWithId = "ID019051: ServerRequestImpl.set_exception(): null input parameter";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ServerRequestImpl.set_exception(Any)", messageWithId);
      }
      throw new BAD_PARAM(messageWithId);
    }

    if (any.type().kind() != org.omg.CORBA.TCKind.tk_except) {
      String messageWithId = "ID019052: ServerRequestImpl.set_exception(): invalid parameter kind";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ServerRequestImpl.set_exception(Any)", messageWithId);
      }
      throw new BAD_PARAM(messageWithId);
    }

    _exception = any;
    _isExceptionSet = true;
  }

  public void set_result(Any any) {
    if (!_isArgumentSet || _isResultSet || _isExceptionSet) {
      String messageWithId = "ID019053: ServerRequestImpl.set_result(): bad invocation order";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ServerRequestImpl.set_result(Any)", messageWithId);
      }
      throw new BAD_INV_ORDER(messageWithId);
    }

    // must have some check for _ctx
    if (any == null) {
      String messageWithId = "ID019054: ServerRequestImpl.set_result(): null input parameter";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ServerRequestImpl.set_result(Any)", messageWithId);
      }
      throw new BAD_PARAM(messageWithId);
    }

    _result = any;
    _isResultSet = true;
  }

  public byte hasException() { //da se opravi tova za vsichki vidove
    if (_isExceptionSet) {
      try {
        String id = _exception.type().id();

        if (ExceptionUtility.isSystemException(id)) {
          return 2;
        } else {
          return 1;
        }
      } catch (Exception e) {
        return 2;
      }
    } else {
      return 0;
    }
  }

  public Any get_result() {
    if (_result != null) {
      return _result;
    } else {
      return _exception;
    }
  }

  public void writeResult(CORBAOutputStream out) throws Exception {
    if (_isArgumentSet && _isResultSet) { // normal return, after this must marshal in & inout parameters
      _result.write_value(out);
    } else if (_isArgumentSet && !_isResultSet && !_isExceptionSet) {
      try {
        _result = new AnyImpl(orb);
        _result.type(new TypeCodeImpl(orb, org.omg.CORBA.TCKind.tk_void));
        _result.write_value(out);
      } catch (Exception e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ServerRequestImpl.writeResult(CORBAOutputStream)", LoggerConfigurator.exceptionTrace(e));
        }
        throw e;
      }
    } else if (_isExceptionSet) {
      String id = _exception.type().id();

      if (ExceptionUtility.isSystemException(id)) { // this must be SystemException
        CORBAInputStream in = (CORBAInputStream) _exception.create_input_stream();
        out.write_string(in.read_string());
        out.write_long(in.read_long());
        out.write_long(in.unaligned_read_long());
        return;
      } else { // this must be UserDefinedException
        _exception.write_value(out);
        return;
      }
    } else {
      String messageWithId = "ID019055: ServerRequestImpl.writeResult(): bad invocation order";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ServerRequestImpl.writeResult(CORBAOutputStream)", messageWithId);
      }
      throw new BAD_INV_ORDER(messageWithId);
    }

    NamedValue nv = null;

    for (int i = 0; i < _arguments.count(); i++) {
      try {
        nv = _arguments.item(i);

        if (nv.flags() != org.omg.CORBA.ARG_IN.value) {
          nv.value().write_value(out);
        }
      } catch (Bounds e) {
        continue;
      } catch (Exception e) {
        String messageWithId = "ID019056: ServerReqeustImpl.writeResult(): error while marshalling arguments";
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ServerRequestImpl.writeResult(CORBAOutputStream)", LoggerConfigurator.exceptionTrace(e));
        }
        throw new MARSHAL(messageWithId);
      }
    }
  }

}// ServerRequestImpl

