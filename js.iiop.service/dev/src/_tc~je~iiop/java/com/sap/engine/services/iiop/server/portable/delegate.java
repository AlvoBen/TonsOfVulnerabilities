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
package com.sap.engine.services.iiop.server.portable;
import com.sap.engine.services.iiop.internal.giop.ClientRequest;
import com.sap.engine.services.iiop.internal.portable.IIOPInputStream;
import com.sap.engine.services.iiop.internal.util.IDFactoryItem;
import com.sap.engine.services.iiop.internal.interceptors.MinorCodes;
import com.sap.engine.services.iiop.internal.ClientORB;
import com.sap.engine.services.iiop.internal.ORB;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.services.iiop.server.CommunicationLayerImpl;
import com.sap.engine.services.iiop.server.IIOPMessageProcessor;
import com.sap.engine.services.iiop.CORBA.portable.DelegateImpl;
import com.sap.engine.services.iiop.CORBA.portable.CORBAInputStream;
import com.sap.engine.services.iiop.CORBA.*;
import com.sap.engine.services.iiop.CORBA.util.ExceptionUtility;
import com.sap.engine.interfaces.cross.Connection;
import com.sap.engine.interfaces.cross.ConnectionProperties;
import com.sap.engine.system.ThreadWrapper;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.UnknownException;
import org.omg.IOP.ServiceContext;

import java.io.IOException;

/**
 *  @author Vladimir Velinov
 *  @version 4.0
 */
public abstract class Delegate extends DelegateImpl {

  protected Connection connection = null;
  public CommunicationLayerImpl sender = null;
  private org.omg.CORBA.Object target;

  public Delegate(IOR ior) {
    super(ior);
  }

  public boolean is_a(org.omg.CORBA.Object obj, String repository_id) {
  	if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beInfo()) {
  	  LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).infoT("Delegate.is_a(org.omg.CORBA.Object, String)", "is_a operation for repoId/Object " + repository_id + "/" + obj);
  	}

    if (repository_id.equals(ior.getTypeID())) {
      return true;
    }
    if (repository_id.equals("IDL:omg.org/CosTransactions/Resource:1.0")) {
      return true;
    }

    //should be smth else here. At present - false;
    try {
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
  	  if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beWarning()) {
  	    LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).warningT("Delegate.is_a(org.omg.CORBA.Object, String)", "is_a operation for repoId/Object " + repository_id + "/" + obj + " causes exception." + LoggerConfigurator.exceptionTrace(e));
  	  }
      return false;
    }

  }

  public void setConnection(Connection  connection) {
    this.connection = connection;
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
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beInfo()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).infoT("Delegate.request(org.omg.CORBA.Object, String, boolean)", " Object/String/boolean : "+obj+"/"+s+"/"+flag);
      }
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
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("Delegate.request(org.omg.CORBA.Object, String, boolean)", "Request error : " + LoggerConfigurator.exceptionTrace(t));
      }
      return null;
    }
  }

  public org.omg.CORBA.portable.InputStream invoke(org.omg.CORBA.Object obj, org.omg.CORBA.portable.OutputStream outputstream) throws ApplicationException, RemarshalException {
    if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beInfo()) {
      LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).infoT("Delegate.invoke(org.omg.CORBA.Object, org.omg.CORBA.portable.OutputStream)", " Obj:"+obj);
    }

    ClientRequest out = (ClientRequest) outputstream;
    //get the previously stored ID
    int id = out.getRequestId();
    IDFactoryItem item = getFactoryItem(id);
    out.flushData();

    byte[] toSend = out.toByteArray();
    synchronized (item) {
      try {
        if (connection != null) {
          ConnectionProperties cp = connection.getProperties();
          if (cp != null) {
            ThreadWrapper.pushSubtask("IIOP call to " + cp.getRemoteAddress() + ":" + cp.getRemotePort() + " " + out.operation(), ThreadWrapper.TS_WAITING_ON_IO);
          } else {
            ThreadWrapper.pushSubtask("IIOP call to local node " + out.operation(), ThreadWrapper.TS_WAITING_ON_IO);
          }
          try {
            connection.sendRequest(toSend, toSend.length, item);
            if (out.response_expected()) {
              item.wait();
            }
          } finally {
            ThreadWrapper.popSubtask();
          }
        } else {
          throw new IOException("Delegate is not connected");
        }
      } catch (IOException ioe) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("Delegate.invoke(org.omg.CORBA.Object, org.omg.CORBA.portable.OutputStream)", LoggerConfigurator.exceptionTrace(ioe));
        }
      } catch (InterruptedException ex) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("Delegate.invoke(org.omg.CORBA.Object, org.omg.CORBA.portable.OutputStream)", "Synchronization error!" + LoggerConfigurator.exceptionTrace(ex));
        }
      }
      connection.callCompleted(item);
    }

    if (out.response_expected()) {
      out.reply = item.getMessage();
      if (out.reply == null) {
        disposeRequestId(id);
        throw new org.omg.CORBA.COMM_FAILURE(1398079490, CompletionStatus.COMPLETED_NO);
      }
      out.reply.setORB(orb);
      int status = out.reply.getStatus();
      CompletionStatus completionStatus = CompletionStatus.COMPLETED_YES;
      switch (status) {
        case GIOPMessageConstants.NO_EXCEPTION: {
          disposeRequestId(id);
          out.dealReceiveReply();
          return out.reply;
        }
        case GIOPMessageConstants.USER_EXCEPTION: {
          disposeRequestId(id);
          int i = out.reply.getPos();
          String exceptionId = out.reply.read_string();
          out.reply.reset(i);
          //        out.dealReceiveReply(); //Vancho VREMENNO predi o
          ApplicationException aex = new ApplicationException(exceptionId, out.reply);
          out.dealReceiveException(aex);
          throw aex;
        }
        case GIOPMessageConstants.SYSTEM_EXCEPTION: {
          disposeRequestId(id);
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
            throw new org.omg.CORBA.INTERNAL("ID019162: Bad completion status: " + completion, 0, CompletionStatus.COMPLETED_MAYBE);
          }

          SystemException systemexception;
          //UnknownException
          ServiceContext context =  null;
          try {
            context = out.get_reply_service_context(9);
          } catch(BAD_PARAM bp_ex) {
            if (bp_ex.minor != MinorCodes.INVALID_SERVICE_CONTEXT_ID) {
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
              if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beWarning()) {
                LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).warningT("Delegate.invoke(org.omg.CORBA.Object, org.omg.CORBA.portable.OutputStream)", "Bad system exception. " + LoggerConfigurator.exceptionTrace(ex));
              }
              throw new org.omg.CORBA.INTERNAL("ID019163: Bad SystemException: " + exClassName, 0, CompletionStatus.COMPLETED_MAYBE);
            }
          }
          out.dealReceiveException(systemexception);
        }
        case GIOPMessageConstants.LOCATION_FORWARD: {
          return forwardRequest((ORB) orb, obj, out);
        }
        default: {
          disposeRequestId(id);
          throw new org.omg.CORBA.UNKNOWN("ID019164: Bad reply status: " + status + " >>> " + this);
        }
      }
    } else {
      disposeRequestId(id);
      return null;
    }
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
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beWarning()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).warningT("Delegate.invoke(org.omg.CORBA.Object, org.omg.CORBA.portable.OutputStream)", "Create request failed. " + LoggerConfigurator.exceptionTrace(t));
      }
      return null;
    }
  }

//  private boolean checkIsLocalRequest(String host, int port) {
//    String checkUrl = "@" + host + ":" + port;
//    String[] urls = sender.getURL();
//    for (int i = 0; i < urls.length; i++) {
//      if (checkUrl.equals(urls[i])) {
//        return true;
//      }
//    }
//    return false;
//  }

//  private class LocalRequest implements Runnable {
//
//    CommunicationLayerImpl sender;
//    byte[] toSend;
//
//    public LocalRequest(CommunicationLayerImpl sender, byte[] toSend) {
//      this.sender = sender;
//      this.toSend = toSend;
//    }
//
//    public void run() {
//      try {
//        CrossMessage localMessage = CommunicationLayerImpl.broker.getMessageProcessor().getMessage(toSend, toSend.length, connection);
//        localMessage.process();
//      } catch (IOException ioe) {
//         if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
//          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("LocalRequest.run()", "LocalRequest failed : " + LoggerConfigurator.exceptionTrace(ioe));
//        }
//      }
//    }
//  }


  public void releaseReply(org.omg.CORBA.Object obj, org.omg.CORBA.portable.InputStream inputstream) {
    if (inputstream != null) {
      IIOPMessageProcessor.fcaConnector.releaseBuffer(((CORBAInputStream) inputstream).getData());
    }
  }

  public abstract ClientRequest createRequestLocal(String operation, int id, boolean response);

  public abstract org.omg.CORBA.portable.InputStream forwardRequest(com.sap.engine.services.iiop.internal.ORB orb0, org.omg.CORBA.Object obj, ClientRequest out) throws ApplicationException, RemarshalException;

}