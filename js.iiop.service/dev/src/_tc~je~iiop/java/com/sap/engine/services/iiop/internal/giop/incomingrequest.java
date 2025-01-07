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
package com.sap.engine.services.iiop.internal.giop;

import com.sap.engine.services.iiop.CORBA.portable.CORBAOutputStream;
import com.sap.engine.services.iiop.CORBA.util.ExceptionUtility;
import com.sap.engine.services.iiop.CORBA.GIOPMessageConstants;
import com.sap.engine.services.iiop.core.MessageConstants;
import com.sap.engine.services.iiop.internal.TargetHolder;
import com.sap.engine.services.iiop.internal.interceptors.InterceptorsStorage;
import com.sap.engine.services.iiop.internal.interceptors.SlotTable;
import com.sap.engine.services.iiop.internal.interceptors.MinorCodes;
import com.sap.engine.services.iiop.internal.portable.IIOPOutputStream;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.services.iiop.server.IIOPMessageProcessor;
import com.sap.engine.lib.lang.Convert;
import com.sap.engine.system.ThreadWrapper;
import org.omg.CORBA.*;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.IOP.ServiceContext;
import org.omg.PortableInterceptor.*;

import javax.rmi.CORBA.Util;
import java.io.IOException;


/**
 * It is the base class of all incomming request messages.
 *
 * @author Georgy Stanev, Nikolai Neichev
 * @version 4.0
 */
public abstract class IncomingRequest extends IncomingMessage implements ResponseHandler, ServerRequestInfo { //$JL-SER$

  // GIOP REquest Message Header information
  protected int request_id;
  protected boolean response;
  protected String operation;
  protected ServiceContext[] requestContexts;
  protected org.omg.CORBA.portable.ObjectImpl in_target; //$JL-SER$
  protected TargetHolder targetHolder;
  protected ClientReply reply = null;
  protected Any sending_exception;  //$JL-SER$
  protected org.omg.CORBA.Object forward_reference; //$JL-SER$
  protected short reply_status = -666;
  private boolean thrownUnknownException = false;
  private Throwable knownEx;

  private transient SlotTable slotTable = null;

  protected byte[] object_key = null;

  protected IncomingRequest(byte[] binaryData) {
    super(binaryData);
    //this.slotTable = orb.getPICurrent().getThreadSlotTable();
  }

  protected IncomingRequest(byte[] binaryData, int size) {
    super(binaryData, size);
  }

  public void dealReceiveRequestServiceContexts() {
    dealReceiveRequestServiceContexts(InterceptorsStorage.getServerInterceptors());
    dealReceiveRequestServiceContexts(InterceptorsStorage.getServerInterceptors(orb));
  }

  private void dealReceiveRequestServiceContexts(Interceptor[] interceptors) {
    int index = 0;
    try {
      for (index = 0; index < interceptors.length; index++) {
        ((ServerRequestInterceptor) interceptors[index]).receive_request_service_contexts(this);
      }
    } catch (SystemException sex) {
      reply_status = SYSTEM_EXCEPTION.value;
      sending_exception = systemExceptionToAny(sex);

      while (--index >= 0) {
        try {
          ((ServerRequestInterceptor) interceptors[index]).send_exception(this);
        } catch (Exception _ex) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncommingRequest.dealReceiveRequestServiceContexts(Interceptor[])", "send_exception failed : " + LoggerConfigurator.exceptionTrace(_ex));
          }
        }
      }
    } catch (ForwardRequest fex) {
      reply_status = LOCATION_FORWARD.value;
      forward_reference = fex.forward;

      while (--index >= 0) {
        try {
          ((ServerRequestInterceptor) interceptors[index]).send_other(this);
        } catch (Exception _ex) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncommingRequest.dealReceiveRequestServiceContexts(Interceptor[])", "send_other failed : " + LoggerConfigurator.exceptionTrace(_ex));
          }
        }
      }
    }
  }

  public void dealReceiveRequest() {
    dealReceiveRequest(InterceptorsStorage.getServerInterceptors());
    dealReceiveRequest(InterceptorsStorage.getServerInterceptors(orb));
  }

  public void dealReceiveRequest(Interceptor[] interceptors) {
    int index = 0;
    try {
      for (index = 0; index < interceptors.length; index++) {
        ((ServerRequestInterceptor) interceptors[index]).receive_request(this);
      }
    } catch (SystemException sex) {
      reply_status = SYSTEM_EXCEPTION.value;
      sending_exception = systemExceptionToAny(sex);

      for (index++; index < interceptors.length; index++) {
        try {
          ((ServerRequestInterceptor) interceptors[index]).send_exception(this);
        } catch (Exception _ex) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncommingRequest.dealReceiveRequest(Interceptor[])", "send_exception failed : " + LoggerConfigurator.exceptionTrace(_ex));
          }
        }
      }
    } catch (ForwardRequest fex) {
      reply_status = LOCATION_FORWARD.value;
      forward_reference = fex.forward;

      for (index++; index < interceptors.length; index++) {
        try {
          ((ServerRequestInterceptor) interceptors[index]).send_other(this);
        } catch (Exception _ex) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncommingRequest.dealReceiveRequest(Interceptor[])", "send_other failed : " + LoggerConfigurator.exceptionTrace(_ex));
          }
        }
      }
    }
  }

  public void dealSendReply() {
    dealSendReply(InterceptorsStorage.getServerInterceptors());
    dealSendReply(InterceptorsStorage.getServerInterceptors(orb));
  }

  private void dealSendReply(Interceptor[] interceptors) {
    int index = 0;
    try {
      for (index = interceptors.length - 1; index >= 0; index--) {
        ((ServerRequestInterceptor) interceptors[index]).send_reply(this);
      }
    } catch (SystemException ex) {
      ex.completed = CompletionStatus.COMPLETED_YES;
      reply_status = SYSTEM_EXCEPTION.value;
      sending_exception = systemExceptionToAny(ex);

      for (index--; index >= 0; index--) {
        try {
          ((ServerRequestInterceptor) interceptors[index]).send_exception(this);
        } catch (Exception _ex) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncommingRequest.dealSendReply(Interceptor[])", "send_exception failed : " + LoggerConfigurator.exceptionTrace(_ex));
          }
        }
      }
    }
  }

  public void dealSendException() {
    dealSendException(InterceptorsStorage.getServerInterceptors());
    dealSendException(InterceptorsStorage.getServerInterceptors(orb));
  }

  private void dealSendException(Interceptor[] interceptors) {
    int index = 0;
    try {
      for (index = interceptors.length - 1; index >= 0; index--) {
        try {
          ((ServerRequestInterceptor) interceptors[index]).send_exception(this);
        } catch (SystemException ex) {
          ex.completed = CompletionStatus.COMPLETED_YES;
          reply_status = SYSTEM_EXCEPTION.value;
          sending_exception = systemExceptionToAny(ex);
        }
      }
    } catch (ForwardRequest fex) {
      reply_status = LOCATION_FORWARD.value;
      forward_reference = fex.forward;

      for (index--; index >= 0; index--) {
        try {
          ((ServerRequestInterceptor) interceptors[index]).send_other(this);
        } catch (Exception ex) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncommingRequest.dealSendException(Interceptor[])", "send_other failed : " + LoggerConfigurator.exceptionTrace(ex));
          }
        }
      }
    }
  }

  public void dealSendOther() {
    dealSendOther(InterceptorsStorage.getServerInterceptors());
    dealSendOther(InterceptorsStorage.getServerInterceptors(orb));
  }

  private void dealSendOther(Interceptor[] interceptors) {
    int index = 0;
    try {
      for (index = interceptors.length - 1; index >= 0; index--) {
        try {
          ((ServerRequestInterceptor) interceptors[index]).send_other(this);
        } catch (ForwardRequest fex) {
          reply_status = LOCATION_FORWARD.value;
          forward_reference = fex.forward;
        }
      }
    } catch (SystemException ex) {
      reply_status = SYSTEM_EXCEPTION.value;
      sending_exception = systemExceptionToAny(ex);

      for (index--; index >= 0; index--) {
        try {
          ((ServerRequestInterceptor) interceptors[index]).send_exception(this);
        } catch (Exception _ex) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncommingRequest.dealSendOther(Interceptor[])", "send_exception failed : " + LoggerConfigurator.exceptionTrace(_ex));
          }
        }
      }
    }
  }

  public void process_initial() {
    readMessageHeader();
    if (targetHolder != null && slotTable == null) {
      ObjectImpl obj = (ObjectImpl) targetHolder.getObject();
      orb = obj._orb();
      this.slotTable = ((com.sap.engine.services.iiop.CORBA.ORB) orb).getPICurrent().getThreadSlotTable();
    }

    dealReceiveRequestServiceContexts();
    dealReceiveRequest();
    if (targetHolder == null) {
      createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName("org.omg.CORBA.OBJECT_NOT_EXIST"), 1, 2);
      reply.flushData();
      return;
    }

    try {
      targetHolder.invoke(this);
    } catch (UnknownException ex ) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncommingRequest.process()", "Unknown exception in invoke : " + LoggerConfigurator.exceptionTrace(ex));
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncommingRequest.process()", "Original exception in invoke : " + LoggerConfigurator.exceptionTrace(ex.originalEx));
      }
      knownEx = ex.originalEx;

      if (knownEx instanceof javax.transaction.TransactionRequiredException) {
        createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName("org.omg.CORBA.TRANSACTION_REQUIRED"), 1, 2);
      } else if (ex.originalEx instanceof java.rmi.NoSuchObjectException) {
        createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName("org.omg.CORBA.OBJECT_NOT_EXIST"), 1, 2);
      } else {
        thrownUnknownException = true;
        createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName("org.omg.CORBA.UNKNOWN"), 1, 2);
      }
    } catch (Throwable e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncommingRequest.process()", "Throwable in invoke : " + LoggerConfigurator.exceptionTrace(e));
      }
      int minorCode = 1;
      if (SystemException.class.isAssignableFrom(e.getClass())) {
        minorCode = ((SystemException) e).minor;
      }
      createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName(e.getClass().getName()), minorCode, 2);
    }
    reply.flushData();
  }


  public void process() {
    busyThreads++;
    ThreadWrapper.pushSubtask("processing request", ThreadWrapper.TS_PROCESSING);
    try {
      ConnectionMetaData metaData = (ConnectionMetaData) connection.getMetaData();

      if (metaData.isFirst()) {
        setToSendCodeBase(true);
        metaData.notFirst();
      }

      if (fragmented()) {
        metaData.storeFragment(request_id(), this);
        return;
      }

      if (getDefault_codebase() == null) {
        setDefault_codebase(metaData.getDefaultCodebase());
      }

      readMessageHeader();
      if (targetHolder != null && slotTable == null) {
        ObjectImpl obj = (ObjectImpl) targetHolder.getObject();
        orb = obj._orb();
        this.slotTable = ((com.sap.engine.services.iiop.CORBA.ORB) orb).getPICurrent().getThreadSlotTable();
      }

      dealReceiveRequestServiceContexts();
      dealReceiveRequest();

      if (targetHolder == null) {
        createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName("org.omg.CORBA.OBJECT_NOT_EXIST"), 1, 2);
      } else {
        try {
          ThreadWrapper.setSubTaskName(this.operation());
          if (noResources) {
            createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName("org.omg.CORBA.NO_RESOURCES"), 1, 2);
          } else {
            targetHolder.invoke(this);
          }
        } catch (UnknownException ex ) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncommingRequest.process()", "Unknown exception in invoke : " + LoggerConfigurator.exceptionTrace(ex));
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncommingRequest.process()", "Original exception in invoke : " + LoggerConfigurator.exceptionTrace(ex.originalEx));
          }
          knownEx = ex.originalEx;

          if (knownEx instanceof javax.transaction.TransactionRequiredException) {
            createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName("org.omg.CORBA.TRANSACTION_REQUIRED"), 1, 2);
          } else if (ex.originalEx instanceof java.rmi.NoSuchObjectException) {
            createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName("org.omg.CORBA.OBJECT_NOT_EXIST"), 1, 2);
          } else {
            thrownUnknownException = true;
            createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName("org.omg.CORBA.UNKNOWN"), 1, 2);
          }
        } catch (Throwable e) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncommingRequest.process()", "Throwable in invoke : " + LoggerConfigurator.exceptionTrace(e));
          }
          int minorCode = 1;
          if (SystemException.class.isAssignableFrom(e.getClass())) {
            minorCode = ((SystemException) e).minor;
          }
          createExceptionReply((byte) GIOPMessageConstants.SYSTEM_EXCEPTION, ExceptionUtility.getIDLName(e.getClass().getName()), minorCode, 2);
        }
      }

      if (response_expected()) {
        reply.flushData();
        byte[] reqId = new byte[8];
        Convert.writeLongToByteArr(reqId, 0, request_id);
        try {
          byte[] toSend = reply.toByteArray();
          ThreadWrapper.setSubTaskName("sending reply");
          connection.sendReply(toSend, toSend.length, reqId);
        } catch (IOException e) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("Communication failure : " + LoggerConfigurator.exceptionTrace(e));
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("connection.sendReply(byte[], int, byte[])", LoggerConfigurator.exceptionTrace(e));
          }
        }
      }
      metaData.setDefaultCodebase(getDefault_codebase());

      IIOPMessageProcessor.fcaConnector.releaseBuffer(this.getData());
    } finally {
      ThreadWrapper.popSubtask();
      busyThreads--;
    }
  }

  protected ClientReply createServerReply() {
    reply = createLocalServerReply();
    dealSendReply();
    return reply;
  }

  protected abstract ClientReply createLocalServerReply();

  /**
   * Creates server message reply.
   *
   * @return     An output stream with loaded reply.
   */
  public org.omg.CORBA.portable.OutputStream createReply() {
    reply = createServerReply();
    if (toSendCodeBase) {
      try {
        org.omg.CORBA.portable.ObjectImpl rtime = (org.omg.CORBA.portable.ObjectImpl) Util.createValueHandler().getRunTimeCodeBase();
        if (rtime != null) {
          ORB _orb = ORB.init(new String[0], null);
          _orb.connect(rtime);
          org.omg.CORBA.Object cb_obj = orb.string_to_object(_orb.object_to_string(rtime));
          CORBAOutputStream out = new CORBAOutputStream(orb);
          out.set_minor_version(minorVersion);
          out.set_encapsulation();
          out.setEndian(false);
          out.write_boolean(out.getEndian());
          out.write_Object(cb_obj);
          add_reply_service_context(new ServiceContext(MessageConstants.CODEBASE_SENDING_CONTEXT_RUN_TIME, out.toByteArray()), false);
        }
      } catch (Exception e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncomingRequest.createReply()", "Can't send a codebase context, caused by: " + LoggerConfigurator.exceptionTrace(e));
        }
      }
      toSendCodeBase = false;
    }
    reply.goToMessageHeaderPosition();
    reply.writeMessageHeader();
    return reply;
  }

  /**
   * Creates USER exception reply.
   *
   * @return     An output stream with loaded exception.
   */
  public org.omg.CORBA.portable.OutputStream createExceptionReply() {
    //    Thread.dumpStack();
    reply = createServerReply();
    reply.goToMessageHeaderPosition();
    reply.writeExceptionMessageHeader((byte) GIOPMessageConstants.USER_EXCEPTION); // USER_EXCEPTION
    return reply;
  }

  private void addUEIContext() {
    ServiceContext[] sc = reply.replyContexts;
    IIOPOutputStream cos = new IIOPOutputStream(orb);
    cos.set_encapsulation();
    cos.set_minor_version(minorVersion);
    cos.write_boolean(cos.getEndian());
    cos.write_value(knownEx);
    byte[] exData = cos.toByteArray();
    ServiceContext uexiSC = new ServiceContext(9, exData);

    if (sc == null) {
      sc = new ServiceContext[1];
      sc[0] = uexiSC;
    } else {
      ServiceContext[] temp = new ServiceContext[sc.length + 1];
      System.arraycopy(sc, 0, temp, 0, sc.length);
      temp[sc.length] = uexiSC;
      sc = temp;
    }

    reply.replyContexts = sc;
  }

  /**
   * Creates exception reply, from the specified type.
   *
   * @param   type  Error type;
   * @return     An output stream with loaded exception.
   */
  public org.omg.CORBA.portable.OutputStream createExceptionReply(short type, String id, int minor_code, int completion_status) {
    reply = createServerReply();

    if (thrownUnknownException) {
      addUEIContext();
      thrownUnknownException = false;
    }

    reply.goToMessageHeaderPosition();
    reply.writeExceptionMessageHeader((byte) type);

    if (type == 2) { // only SYSTEM exception
      reply.write_string(id);
      reply.write_long(minor_code);
      reply.write_long(completion_status);
    }

    return reply;
  }

  public OutgoingMessage getServerReply() {
    return reply;
  }

  public org.omg.IOP.ServiceContext get_request_service_context(int id) {
    if (requestContexts == null) {
      requestContexts = new ServiceContext[0];
    }

    for (ServiceContext requestContext : requestContexts) {
      if (requestContext.context_id == id) {
        return requestContext;
      }
    }

    throw new BAD_PARAM("Not found ServiceContext with id=" + id, MinorCodes.INVALID_SERVICE_CONTEXT_ID, CompletionStatus.COMPLETED_MAYBE);
  }

  public org.omg.IOP.ServiceContext get_reply_service_context(int id) {
    if (reply.replyContexts == null) {
      reply.replyContexts = new ServiceContext[0];
    }

    for (ServiceContext replyContext : reply.replyContexts) {
      if (replyContext.context_id == id) {
        return replyContext;
      }
    }

    throw new BAD_PARAM("Not found ServiceContext with id=" + id, MinorCodes.INVALID_SERVICE_CONTEXT_ID, CompletionStatus.COMPLETED_MAYBE);
  }

  public void add_reply_service_context(ServiceContext ctx, boolean replace) {
    boolean replaced = false;

    if (reply.replyContexts == null) {
      reply.replyContexts = new ServiceContext[0];
    }

    if (replace) {
      for (int i = 0; i < reply.replyContexts.length; i++) {
        if (ctx.context_id == reply.replyContexts[i].context_id) {
          reply.replyContexts[i] = ctx;
          replaced = true;
          break;
        }
      }
    }

    if (!replaced) {
      int count = reply.replyContexts.length;
      ServiceContext[] temp = new ServiceContext[count + 1];
      System.arraycopy(reply.replyContexts, 0, temp, 0, count);
      temp[count] = ctx;
      reply.replyContexts = temp;
    }
  }

  public int request_id() {
    return request_id;
  }

  public String operation() {
    return operation;
  }

  public boolean response_expected() {
    return response;
  }

  public org.omg.CORBA.Object target() {
    return targetHolder == null ? null : targetHolder.getObject();
  }

  public org.omg.CORBA.Object effective_target() {
    return targetHolder == null ? null : targetHolder.getObject();
  }

  public void set_slot(int i, Any any) throws InvalidSlot {
    slotTable.set_slot(i, any);
  }

  public Any get_slot(int i) throws InvalidSlot {
    return slotTable.get_slot(i);
  }

  public String server_id() {
    throw new NO_IMPLEMENT(); //TODO
  }

  public String[] adapter_name() {
    throw new NO_IMPLEMENT(); //TODO
  }

  public String orb_id() {
    throw new NO_IMPLEMENT(); //TODO
  }

  public byte[] object_id() {
    return object_key;
  }

  private Any systemExceptionToAny(SystemException received_exception) {
    Any any = orb.create_any();
    OutputStream out = any.create_output_stream();
    ORB orb = out.orb();
    String name = received_exception.getClass().getName();
    out.write_string(ExceptionUtility.getIDLName(received_exception.getClass().getName()));
    out.write_long(received_exception.minor);
    out.write_long(received_exception.completed.value());
    StructMember[] members = new StructMember[3];
    members[0] = new StructMember("id", orb.create_string_tc(0), null);
    members[1] = new StructMember("minor", orb.get_primitive_tc(TCKind.tk_long), null);
    members[2] = new StructMember("completed", orb.get_primitive_tc(TCKind.tk_long), null);
    any.read_value(out.create_input_stream(), orb.create_exception_tc(ExceptionUtility.getIDLName(received_exception.getClass().getName()), name, members));
    return any;
  }

}

