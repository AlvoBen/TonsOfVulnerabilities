/*
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.client.portable;

import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA_2_3.portable.*;
import org.omg.IOP.ServiceContext;
import com.sap.engine.services.iiop.CORBA.*;
import com.sap.engine.services.iiop.CORBA.portable.*;
import org.omg.CORBA.portable.UnknownException;
import com.sap.engine.services.iiop.internal.portable.IIOPInputStream;
import com.sap.engine.services.iiop.internal.util.IDFactoryItem;
import com.sap.engine.services.iiop.CORBA.util.ExceptionUtility;
import com.sap.engine.services.iiop.client.CommunicationLayerImpl;
import com.sap.engine.services.iiop.client.portable.weak.WeakStorrage;

import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;

import com.sap.engine.services.iiop.internal.giop.ClientRequest;
import com.sap.engine.services.iiop.internal.interceptors.MinorCodes;
import com.sap.engine.services.iiop.internal.ClientORB;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
/**
 *  @author Ivan Atanassov
 *  @version 4.0
 */
public abstract class Delegate extends DelegateImpl {

  protected String host;
  protected int port;
  public CommunicationLayerImpl sender = null;
  private static String RESOURCE = "IDL:omg.org/CosTransactions/Resource:1.0";
  private static WeakStorrage connections = new WeakStorrage();
  private org.omg.CORBA.Object target;

  public Delegate(IOR ior) {
    super(ior);
  }

  public boolean is_a(org.omg.CORBA.Object obj, String repository_id) {

    if (repository_id.equals(ior.getTypeID())) {
      return true;
    }


    if (repository_id.equals(RESOURCE)) {
      return true;
    }

    //should be smth else here. At present - false;
    try {
//      Profile p = ior.getProfile();
//      if (!isSocketOpen) {
//        initConnection(p);
//      }
      NVListImpl _list = new NVListImpl(orb);
      AnyImpl _any = new AnyImpl(orb);
      _any.insert_string(repository_id);
      _list.add_value("id", _any, org.omg.CORBA.ARG_IN.value);
      NamedValueImpl _result = new NamedValueImpl(orb, org.omg.CORBA.ARG_OUT.value);
      RequestImpl request = new RequestImpl(orb, obj, null, "_is_a", _list, _result, null, null);
      request.set_return_type(orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean));
      request.invoke();
      int status = request.getStatus();
      if (status == GIOPMessageConstants.SYSTEM_EXCEPTION || status == GIOPMessageConstants.USER_EXCEPTION) {
        return false;
      }

      return _result.value().extract_boolean();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public void setHostPort(String host, int port) {
    this.host = host;
    this.port = port;
    connections.put(new WeakReference(this, WeakQueueThread.refQueue));
  }

  public static void closeConnection(String host, int port) {
    WeakReference[] weaks = connections.toArray();
    for (int i = 0; i < weaks.length; i++) {
      Delegate delegate = (Delegate) weaks[i].get();
      if (delegate == null) {
        connections.remove(weaks[i]);
      } else if (delegate.host.equals(host) && delegate.port == port) {
        delegate.host = "-";
        delegate.port = -1;
        connections.remove(weaks[i]);
      }
    }
    WeakQueueThread.end = true;
  }

  private int getNewRequestId() {
    return ClientORB.getIDFactory().requestID();
  }

  private IDFactoryItem getFactoryItem(int id) {
    return ClientORB.getIDFactory().get(id);
  }

  private void disposeRequestId(int id) {
    ClientORB.getIDFactory().disposeID(id);
  }


  public org.omg.CORBA.portable.OutputStream request(org.omg.CORBA.Object obj, String s, boolean flag) {
    try {
      target = obj;

      int reqID;
      if (flag) {
        reqID = getNewRequestId();
      } else {
        reqID = -1;
      }
      ClientRequest request = createRequest(s, reqID, flag);
      return request;
    } catch (Throwable t) {
      t.printStackTrace();
      return null;
    }
  }

  private int request(ClientRequest out, IDFactoryItem item) throws ApplicationException {
    if (host.equals("-")) {
      throw new org.omg.CORBA.COMM_FAILURE(1398079490, CompletionStatus.COMPLETED_NO);
    }
    synchronized (item) {
      out.flushData();
      try {
        sender.send(item, out.toByteArray_forSend(), 0, out.byteArray_forSend_length(), host, port);
        if (out.response_expected()) {
          item.wait();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    if (out.response_expected()) {
      out.reply = item.getMessage();
      if (out.reply == null) {
        throw new org.omg.CORBA.COMM_FAILURE(1398079490, CompletionStatus.COMPLETED_NO);
      }
      out.reply.setORB(this.orb);
      return out.reply.getStatus();
    } else {
      return GIOPMessageConstants.NO_EXCEPTION;
    }
  }

  public org.omg.CORBA.portable.InputStream invoke(org.omg.CORBA.Object obj, org.omg.CORBA.portable.OutputStream outputstream) throws ApplicationException, RemarshalException {
    ClientRequest out = (ClientRequest) outputstream;
    int id = out.getRequestId();
    IDFactoryItem item = getFactoryItem(id);
    int status = request(out, item);

    if (out.response_expected()) {
      CompletionStatus completionStatus = CompletionStatus.COMPLETED_YES;
      switch (status) {
        case GIOPMessageConstants.NO_EXCEPTION: {
          out.dealReceiveReply();
          disposeRequestId(id);
          return out.reply;
        }
        case GIOPMessageConstants.USER_EXCEPTION: {
          int i = out.reply.getPos();
          String exceptionId = out.reply.read_string();
          out.reply.reset(i);
          //        out.dealReceiveReply(); //Vancho VREMENNO predi o
          ApplicationException aex = new ApplicationException(exceptionId, out.reply);
          out.dealReceiveException(aex);
          disposeRequestId(id);
          throw aex;
        }
        case GIOPMessageConstants.SYSTEM_EXCEPTION: {
          String repId = out.reply.read_string();
          String exClassName = ExceptionUtility.getClassName(repId);
          int minorCode = out.reply.read_long();
          int completion = out.reply.unaligned_read_long();
          if (completion == 0) {
            completionStatus = CompletionStatus.COMPLETED_YES;
          } else if (completion == 1) {
            completionStatus = CompletionStatus.COMPLETED_NO;
          } else if (completion == 2) {
            completionStatus = CompletionStatus.COMPLETED_MAYBE;
          } else {
            disposeRequestId(id);
            throw new org.omg.CORBA.INTERNAL("ID019162: Bad completion status: " + completion, 0, CompletionStatus.COMPLETED_MAYBE);
          }
          SystemException systemexception;
          //UnknownException
          ServiceContext context = null;
          try {
            context = out.get_reply_service_context(9);
          } catch(BAD_PARAM bp_ex) {
            if (bp_ex.minor != MinorCodes.INVALID_SERVICE_CONTEXT_ID) {
              disposeRequestId(id);
              throw bp_ex;
            }
          }

          if (context != null) {
            IIOPInputStream istream = new IIOPInputStream(ior.getORB(), context.context_data);
            istream.set_encapsulation();
            istream.setEndian(istream.read_boolean());
            Throwable trException = (Throwable) istream.read_value();
            systemexception = new UnknownException(trException);
          } else {
            try {
              systemexception = (SystemException) Class.forName(exClassName).newInstance();
              systemexception.minor = minorCode;
              systemexception.completed = completionStatus;
            } catch (Exception ex) {
              ex.printStackTrace();
              disposeRequestId(id);
              throw new org.omg.CORBA.INTERNAL("ID019163: Bad SystemException: " + exClassName, 0, CompletionStatus.COMPLETED_MAYBE);
            }
          }
          out.dealReceiveException(systemexception);
          break;
        }
        case GIOPMessageConstants.LOCATION_FORWARD: {
          return forwardRequest((ClientORB) orb, obj, out);
        }
        default: {
          disposeRequestId(id);
          throw new org.omg.CORBA.UNKNOWN("ID019164: Bad reply status: " + status + " >>> " + this);
        }
      }
    }
    disposeRequestId(id);
    return null;
  }

  public ClientRequest createRequest(String operation, int id, boolean response) {
    try {
      ClientRequest clRequest = createRequestLocal(operation, id, response);
      clRequest.setTarget(target);
      clRequest.dealSendRequest();
      if (isFirst) {
        orb.initializeRuntimeCodebase(clRequest);
        isFirst = false;
      }
      clRequest.writeMessageHeader();
      return clRequest;
    } catch (Throwable t) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("Delegate.createRequest(String, int, boolean)", "Create request failed. " + LoggerConfigurator.exceptionTrace(t));
      }
      return null;
    }
  }

  public void releaseReply(org.omg.CORBA.Object obj, org.omg.CORBA.portable.InputStream inputstream) {
  }

  public abstract ClientRequest createRequestLocal(String operation, int id, boolean response);

  public abstract org.omg.CORBA.portable.InputStream forwardRequest(com.sap.engine.services.iiop.internal.ClientORB orb0, org.omg.CORBA.Object obj, ClientRequest out) throws ApplicationException, RemarshalException;

  static {
    Thread thread = new Thread(new WeakQueueThread());
    thread.setDaemon(true);
    thread.start();
  }

  public static class WeakQueueThread implements Runnable {

    public static boolean end = false;

    public static ReferenceQueue refQueue = new ReferenceQueue();

    public void run() {
      while (!end) {
        try {
          WeakReference ref = (WeakReference) refQueue.remove();
          if (ref != null) connections.remove(ref);
        } catch (InterruptedException ie) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("Delegate$WeakQueueThread.run()", LoggerConfigurator.exceptionTrace(ie));
          }
        }
      }
    }

  }

}

