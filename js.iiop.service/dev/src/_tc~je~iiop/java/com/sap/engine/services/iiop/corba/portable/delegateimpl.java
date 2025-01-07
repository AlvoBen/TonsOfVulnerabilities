/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.CORBA.portable;

import com.sap.engine.services.iiop.CORBA.*;
import com.sap.engine.services.iiop.PortableServer.RETAINServantHolder;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import org.omg.CORBA.*;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA_2_3.portable.Delegate;

import javax.rmi.CORBA.Tie;

/**
 * An implementation of org.omg.CORBA.portable.Delegate. Every valid CORBA
 * object has exactly one instance of DelegateImpl class. This instance carries
 * the object's IOR (host, port, object_key, ...). When a client stub executes a
 * GIOP Request it uses DelegateImpl to create and send that request and to receive
 * the appropriate GIOP Reply message. When a client stub executes a GIOP Request
 * (LocateRequest or some other GIOP client-side message) for the first time the
 * DelegateImpl instance opens a socket (gets host & port from objects IOR) and
 * keeps that socket open until the object is alive.
 *
 * @author Georgy Stanev
 * @author Vladimir Velinov
 * @version 4.0
 */
public class DelegateImpl extends Delegate {

  protected com.sap.engine.services.iiop.CORBA.ORB orb;
  protected IOR ior = null;
  private boolean isConnected = false;
  protected boolean nowConnected = false;

  protected boolean isFirst = true;
  protected IOR bfwd_ior = null;

  public static final int ALIGN_CONTEXT = 5456208;  //SAP

  public DelegateImpl(IOR ior0) {
    this.orb = (com.sap.engine.services.iiop.CORBA.ORB) ior0.getORB();
    this.ior = ior0;
    this.isConnected = false;
  }

  public void setORB(org.omg.CORBA.ORB orb) {
    this.orb = (com.sap.engine.services.iiop.CORBA.ORB) orb;
  }

  public org.omg.CORBA.ORB getORB() {
    return orb;
  }

  public void setIOR(IOR ior0) {
    ior = ior0;
  }

  public IOR getIOR() {
    return ior;
  }

  public boolean isConnected() {
    return isConnected;
  }

  public void setConnected(boolean b) {
    isConnected = b;
  }

  public org.omg.CORBA.Object get_interface_def(org.omg.CORBA.Object self) {
    //
    String messageWithId = "ID019044: Get interface definition is not implemented";
    if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
      LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("DelegateImpl.get_interface_def(org.omg.CORBA.Object)", messageWithId);
    }
    throw new org.omg.CORBA.NO_IMPLEMENT(messageWithId);
  }

  public org.omg.CORBA.Object duplicate(org.omg.CORBA.Object obj) {
    return obj;
  }

  public void release(org.omg.CORBA.Object obj) {
    orb.disconnect(obj);
  }

  public boolean is_a(org.omg.CORBA.Object obj, String repository_id) {
    String[] ids = ((org.omg.CORBA.portable.ObjectImpl) obj)._ids();

    if (repository_id.equals(ior.getTypeID())) {
      return true;
    }

    for (String anId : ids) {
      if (repository_id.equals(anId)) {
        return true;
      }
    }

    return false;
    //should be smth else here. At present - false;
    //    Profile p = ior.getProfile();
    //    try {
    //      //      if (!isSocketOpen) {
    //      //        initConnection(p);
    //      //      }
    //      NVListImpl _list = new NVListImpl(orb);
    //      AnyImpl _any = new AnyImpl(orb);
    //      _any.insert_string(repository_id);
    //      _list.add_value("id", _any, org.omg.CORBA.ARG_IN.value);
    //      NamedValueImpl _result = new NamedValueImpl(orb, org.omg.CORBA.ARG_OUT.value);
    //      RequestImpl request = new RequestImpl(orb, obj, null, "_is_a", _list, _result, null, null);
    //      request.set_return_type(orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean));
    //      request.invoke();
    //      int status = request.getStatus();
    //
    //      if (status == GIOPMessageConstants.SYSTEM_EXCEPTION || status == GIOPMessageConstants.USER_EXCEPTION) {
    //        return false;
    //      }
    //
    //      return _result.value().extract_boolean();
    //    } catch (Exception e) {
    //      return false;
    //    }
  }

  public boolean non_existent(org.omg.CORBA.Object obj) {
    try {
      NamedValueImpl _result = new NamedValueImpl(orb, org.omg.CORBA.ARG_OUT.value);
      RequestImpl request = new RequestImpl(orb, obj, null, "_non_existent", null, _result, null, null);
      request.set_return_type(orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean));
      try {
        request.invoke();
      } catch (Exception e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("DelegateImpl.non_existent(org.omg.CORBA.Object)", LoggerConfigurator.exceptionTrace(e));
        }
        return true;
      }
      int status = request.getStatus();

      if (status == GIOPMessageConstants.SYSTEM_EXCEPTION || status == GIOPMessageConstants.USER_EXCEPTION) {
        return true;
      }

      return _result.value().extract_boolean();
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("DelegateImpl.non_existent(org.omg.CORBA.Object)", LoggerConfigurator.exceptionTrace(e));
      }
      return true;
    }
  }

  public boolean is_local(org.omg.CORBA.Object obj) {
    DelegateImpl d = (DelegateImpl) ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate();
    Profile prof = d.getIOR().getProfile();
    byte[] key = prof.getObjectKey_ForSend();
    return orb.is_local(key);
  }

  public boolean is_equivalent(org.omg.CORBA.Object obj, org.omg.CORBA.Object other) {
    if (other == null) {
      return false;
    }

    org.omg.CORBA.portable.ObjectImpl oi = (org.omg.CORBA.portable.ObjectImpl) other;
    Delegate delegate = (Delegate) oi._get_delegate();
    if (!(delegate instanceof com.sap.engine.services.iiop.CORBA.portable.DelegateImpl)) {
      return false;
    }

    if (delegate == this) {
      return true;
    }

    return ior.isEquivalent(((DelegateImpl) delegate).getIOR());
  }

  public int hash(org.omg.CORBA.Object obj, int max) {
    int h = hashCode();
    return (h > max) ? 0 : h;
  }

  protected synchronized void initConnection(Profile p) throws Exception {
    while (nowConnected) {
      this.wait(200);
    }
  }

  public Request request(org.omg.CORBA.Object obj, String operation) {
    return new RequestImpl(orb, obj, operation);
  } // request(...

  public Request create_request(org.omg.CORBA.Object obj, Context ctx, String operation, NVList arg_list, NamedValue result) {
    return create_request(obj, ctx, operation, arg_list, result, null, null);
  }

  public Request create_request(org.omg.CORBA.Object obj, Context ctx, String operation, NVList arg_list, NamedValue result, ExceptionList exclist, ContextList ctxlist) {
    RequestImpl request = new RequestImpl(orb, obj, ctx, operation, arg_list, result, exclist, ctxlist);

    while (nowConnected) {
      try {
        synchronized (this) {
          this.wait(500);
        }
      } catch (InterruptedException ex) {
        continue;
      }
    }

    Profile p = ior.getProfile();
    try {
      initConnection(p);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("DelegateImpl.create_request(org.omg.CORBA.Object, Context, String, NVList, NamedValue, ExceptionList, ContextList)", "Could not set connection to " + p.getHost() + ":" + p.getPort() + LoggerConfigurator.exceptionTrace(e));
      }
      throw new RuntimeException("Could not set connection to " + p.getHost() + ":" + p.getPort());
    }
    return request;
  }

  public org.omg.CORBA.ORB orb(org.omg.CORBA.Object obj) {
    return orb;
  }

  public ServantObject servant_preinvoke(org.omg.CORBA.Object self, String operation, Class expectedType) {
    byte[] objKey = ior.getProfile().getObjectKey();
    Object impl = orb.getObject(objKey);
    ServantObject serObj = new ServantObject();

    if (impl instanceof javax.rmi.CORBA.Tie) {
      serObj.servant = ((javax.rmi.CORBA.Tie) impl).getTarget();
    } else if (impl instanceof RETAINServantHolder) {
      try {
        RETAINServantHolder retainSH = (RETAINServantHolder) impl;
        Tie tie = (Tie) retainSH.locateServant();
        serObj.servant = tie.getTarget();
      } catch (Exception e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beDebug()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).debugT("DelegateImpl.servant_preinvoke(org.omg.CORBA.Object, String, Class)", LoggerConfigurator.exceptionTrace(e));
        }
      }
    } else {
      serObj.servant = impl;
    }

    return serObj;
  }

  public void servant_postinvoke(org.omg.CORBA.Object self, ServantObject servant) {
  }
}

