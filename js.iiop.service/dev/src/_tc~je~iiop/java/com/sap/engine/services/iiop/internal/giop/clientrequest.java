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

import com.sap.engine.services.iiop.CORBA.util.ExceptionUtility;
import com.sap.engine.services.iiop.CORBA.IOR;
import com.sap.engine.services.iiop.CORBA.Profile;
import com.sap.engine.services.iiop.CORBA.CodeSetChooser;
import com.sap.engine.services.iiop.CORBA.portable.DelegateImpl;
import com.sap.engine.services.iiop.CORBA.portable.CORBAOutputStream;
import com.sap.engine.services.iiop.internal.interceptors.InterceptorsStorage;
import com.sap.engine.services.iiop.internal.interceptors.MinorCodes;
import com.sap.engine.services.iiop.internal.interceptors.SlotTable;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.interfaces.csiv2.SimpleProfileInterface;
import org.omg.CORBA.*;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TaggedComponent;
import org.omg.PortableInterceptor.*;

import java.lang.reflect.Method;
import java.io.IOException;
import java.util.Vector;

/**
 * This is the base class for all client request messages.
 * It is also used for implementation of
 * org.omg.PortableInterceptor.ClientRequestInfo
 *
 * @author Georgy Stanev, Ivan Atanassov
 * @version 4.0
 */
public abstract class ClientRequest extends OutgoingMessage implements ClientRequestInfo {
  //$JL-SER$
  protected int request_id;
  protected String operation;
  protected boolean response;
  public IncomingReply reply;
  public ServiceContext[] requestContexts;
  //ClientRequestInfo attributes
  protected Any received_exception;
  protected String received_exception_id;
  protected org.omg.CORBA.Object forward_reference;
  protected short reply_status = -666;

  private SlotTable slotTable = null;

  protected ClientRequest(com.sap.engine.services.iiop.CORBA.ORB orb, String op, int id, boolean response, CodeSetChooser codeSetChooser) {
    super(orb, codeSetChooser);
    this.operation = op;
    this.request_id = id;
    this.response = response;

    goToMessageHeaderPosition();

    slotTable = orb.getPICurrent().getThreadSlotTable();

    if (slotTable != null) {
      slotTable = slotTable.copy();
    }
  }

  public int getRequestId() {
    return request_id;
  }

  public void setRequestId(int request_id) {
    this.request_id = request_id;
  }

  protected abstract void writeGIOPHeader();

  public abstract void writeMessageHeader();

  // Must refresh interceptorContxts array size,
  // because there is one or more contexts replaced
  //private boolean refresh;

  public void dealSendRequest() {
    dealSendRequest(InterceptorsStorage.getClientInterceptors());
    dealSendRequest(InterceptorsStorage.getClientInterceptors(orb));
  }

  private void dealSendRequest(Interceptor[] interceptors) {
    int index = 0;
    try {
      for (index = 0; index < interceptors.length; index++) {
        ((ClientRequestInterceptor) interceptors[index]).send_request(this);
      }
    } catch (SystemException sex) {
      sex.completed = CompletionStatus.COMPLETED_NO;
      reply_status = SYSTEM_EXCEPTION.value;
      received_exception = exceptionToAny(sex);
      received_exception_id = ExceptionUtility.getIDLName(received_exception.getClass().getName());

      while (--index >= 0) {
        try {
          ((ClientRequestInterceptor) interceptors[index]).receive_exception(this);
        } catch (Exception _ex) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ClientRequest.dealSendRequest(Interceptor[])", "receive_exception failed: " + LoggerConfigurator.exceptionTrace(_ex));
          }
        }
      }
    } catch (ForwardRequest fex) {
      reply_status = LOCATION_FORWARD.value;
      forward_reference = fex.forward;

      while (--index >= 0) {
        try {
          ((ClientRequestInterceptor) interceptors[index]).receive_other(this);
        } catch (Exception _ex) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ClientRequest.dealSendRequest(Interceptor[])", "receive_other failed: " + LoggerConfigurator.exceptionTrace(_ex));
          }
        }
      }
    }
  }

  public void dealReceiveReply() {
    dealReceiveReply(InterceptorsStorage.getClientInterceptors());
    dealReceiveReply(InterceptorsStorage.getClientInterceptors(orb));
  }

  private void dealReceiveReply(Interceptor[] interceptors) {
    int index = 0;
    try {
      for (index = interceptors.length - 1; index >= 0; index--) {
        ((ClientRequestInterceptor) interceptors[index]).receive_reply(this);
      }
    } catch (SystemException sex) {
      sex.completed = CompletionStatus.COMPLETED_YES;
      reply_status = SYSTEM_EXCEPTION.value;
      received_exception = exceptionToAny(sex);
      received_exception_id = ExceptionUtility.getIDLName(received_exception.getClass().getName());

      for (index--; index >= 0; index--) {
        try {
          ((ClientRequestInterceptor) interceptors[index]).receive_exception(this);
        } catch (Exception _ex) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ClientRequest.dealReceiveReply(Interceptor[])", "receive_exception failed: " + LoggerConfigurator.exceptionTrace(_ex));
          }
        }
      }
    }
  }

  public void dealReceiveException(Exception ex) throws SystemException {
    SystemException exception = null;

    if (ex instanceof SystemException) {
      exception = (SystemException) ex;
      received_exception = exceptionToAny(exception);
      received_exception_id = ExceptionUtility.getIDLName(received_exception.getClass().getName());
    } else if( ex instanceof ApplicationException ) {
      received_exception = exceptionToAny(ex);
      received_exception_id = ((ApplicationException) ex).getId();
    }
    dealReceiveException(InterceptorsStorage.getClientInterceptors(), exception);
    dealReceiveException(InterceptorsStorage.getClientInterceptors(orb), exception);
  }

  private void dealReceiveException(Interceptor[] interceptors, SystemException exception) throws SystemException {
    int index = 0;
    try {
      for (index = interceptors.length - 1; index >= 0; index--) {
        try {
          ((ClientRequestInterceptor) interceptors[index]).receive_exception(this);
        } catch (SystemException sex) {
          exception = sex;
          sex.completed = CompletionStatus.COMPLETED_YES;
          reply_status = SYSTEM_EXCEPTION.value;
          received_exception = exceptionToAny(sex);
          received_exception_id = ExceptionUtility.getIDLName(received_exception.getClass().getName());
        }
      }
    } catch (ForwardRequest fex) {
      reply_status = LOCATION_FORWARD.value;
      forward_reference = fex.forward;

      for (index--; index >= 0; index--) {
        try {
          ((ClientRequestInterceptor) interceptors[index]).receive_other(this);
        } catch (Exception _ex) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ClientRequest.dealReceiveException(Interceptor[], SystemException)", "receive_other failed: " + LoggerConfigurator.exceptionTrace(_ex));
          }
        }
      }
    }

    if (exception != null) {
      throw exception;
    }
  }

  public void dealReceiveOther() {
    dealReceiveOther(InterceptorsStorage.getClientInterceptors());
    dealReceiveOther(InterceptorsStorage.getClientInterceptors(orb));
  }

  private void dealReceiveOther(Interceptor[] interceptors) {
    int index = 0;
    try {
      for (index = interceptors.length - 1; index >= 0; index--) {
        try {
          ((ClientRequestInterceptor) interceptors[index]).receive_other(this);
        } catch (ForwardRequest fex) {
          reply_status = LOCATION_FORWARD.value;
          forward_reference = fex.forward;
        }
      }
    } catch (SystemException sex) {
      reply_status = SYSTEM_EXCEPTION.value;
      received_exception = exceptionToAny(sex);
      received_exception_id = ExceptionUtility.getIDLName(received_exception.getClass().getName());

      for (index--; index >= 0; index--) {
        try {
          ((ClientRequestInterceptor) interceptors[index]).receive_exception(this);
        } catch (Exception ex) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ClientRequest.dealReceiveOther(Interceptor[])", "receive_exception failed: " + LoggerConfigurator.exceptionTrace(ex));
          }
        }
      }
    }
  }

  /**
   * This is listener method for adding org.omg.IOP.ServiceContexts
   *
   * @param   ctx  The added org.omg.IOP.ServiceContext
   * @param   replace  If exists, replace.
   */
  public void add_request_service_context(org.omg.IOP.ServiceContext ctx, boolean replace) {
    boolean replaced = false;

    if (requestContexts == null) {
      requestContexts = new ServiceContext[0];
    }

    for (int i = 0; i < requestContexts.length; i++) {
      if (ctx.context_id == requestContexts[i].context_id) {
        if (replace) {
          requestContexts[i] = ctx;
          replaced = true;
        }
      }
    }

    if (!replaced) {
      ServiceContext[] temp = new ServiceContext[requestContexts.length + 1];
      System.arraycopy(requestContexts, 0, temp, 0, requestContexts.length);
      temp[requestContexts.length] = ctx;
      requestContexts = temp;
    }
  }

  public org.omg.IOP.ServiceContext get_request_service_context(int id) {
    if (requestContexts != null) {
      for (ServiceContext aRequestContext : requestContexts) {
        if (aRequestContext.context_id == id) {
          return aRequestContext;
        }
      }
    }

    throw new BAD_PARAM("Not found ServiceContext with id=" + id, MinorCodes.INVALID_SERVICE_CONTEXT_ID, CompletionStatus.COMPLETED_MAYBE);
  }

  public org.omg.IOP.ServiceContext get_reply_service_context(int id) {
    if ((reply != null) && (reply.replyContexts != null)) {
      for (ServiceContext aReplyContext : reply.replyContexts) {
        if (aReplyContext.context_id == id) {
          return aReplyContext;
        }
      }
    }

    throw new BAD_PARAM("Not found ServiceContext with id=" + id, MinorCodes.INVALID_SERVICE_CONTEXT_ID, CompletionStatus.COMPLETED_MAYBE);
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

  public short reply_status() {
    return reply_status;
  }

  public Object target() {
    return target;
  }

  public org.omg.IOP.TaggedComponent get_effective_component(int id) {
    return get_effective_components(id)[0];
  }

  public Policy get_request_policy(int id) {
    return null;
  }

  public TaggedComponent[] get_effective_components(int id) {
    Vector<TaggedComponent> componenet_store = new Vector<TaggedComponent>();
    DelegateImpl delegate =  (DelegateImpl) target._get_delegate();
    IOR ior = delegate.getIOR();
    Profile effective_profile = ior.getProfile();

    SimpleProfileInterface[] profile_componenets = effective_profile.getComponents();
    for (SimpleProfileInterface aComponent : profile_componenets) {
      if (aComponent.getTag() == id) {
        componenet_store.add(new TaggedComponent(aComponent.getTag(), aComponent.getData()));
      }
    }

    if (componenet_store.size() == 0) {
      throw new BAD_PARAM("Not found TaggedComponents with id=" + id, MinorCodes.INVALID_COMPONENT_ID, CompletionStatus.COMPLETED_MAYBE);
    } else {
      return componenet_store.toArray(new TaggedComponent[0]);
    }
  }

  public org.omg.IOP.TaggedProfile effective_profile() {
    DelegateImpl delegate =  (DelegateImpl) target._get_delegate();
    IOR ior = delegate.getIOR();
    Profile profile = ior.getProfile();
    CORBAOutputStream os = new CORBAOutputStream(orb, 256);
    profile.write_to_stream(os);
    byte[] profile_data = os.toByteArray();
    return new org.omg.IOP.TaggedProfile(profile.getTAG(), profile_data);
  }

  public Object effective_target() {
    return target;
  }

  public Any received_exception() {
    return received_exception;
  }

  public String received_exception_id() {
    return received_exception_id;
  }

  public Object forward_reference() {
    if (reply_status == SYSTEM_EXCEPTION.value) {
      return forward_reference;
    } else {
      return null; //vancho exception
    }
  }

  public Any get_slot(int i) throws InvalidSlot {
    if (slotTable == null) {
      throw new InvalidSlot("Invalid slot ID: " + i);
    }

    return slotTable.get_slot(i);
  }

  private Any exceptionToAny(Exception exception) {
    Any any = orb.create_any();
    return exceptionToAny(exception, any);
  }

  private Any exceptionToAny(Exception exception, Any any) {
    if (exception instanceof SystemException) {
      OutputStream out = any.create_output_stream();
      ORB orb = out.orb();
      String name = exception.getClass().getName();
      out.write_string(ExceptionUtility.getIDLName(exception.getClass().getName()));
      out.write_long(((SystemException) exception).minor);
      out.write_long(((SystemException) exception).completed.value());
      StructMember[] members = new StructMember[3];
      members[0] = new StructMember("id", orb.create_string_tc(0), null);
      members[1] = new StructMember("minor", orb.get_primitive_tc(TCKind.tk_long), null);
      members[2] = new StructMember("completed", orb.get_primitive_tc(TCKind.tk_long), null);
      any.read_value(out.create_input_stream(), orb.create_exception_tc(ExceptionUtility.getIDLName(exception.getClass().getName()), name, members));
    } else if (exception instanceof ApplicationException) {
        ApplicationException appException = (ApplicationException) exception;
        try {
          // Extract the UserException from the ApplicationException.
          // Look up class name from repository id:
          String className = getRepIdClassName(((ApplicationException) exception).getId());

          // Find the read method on the helper class:
          String helperClassName = className + "Helper";
          Class helperClass = Class.forName(helperClassName);
          Class[] readParams = new Class[1];
          readParams[0] = org.omg.CORBA.portable.InputStream.class;
          Method readMethod = helperClass.getMethod("read", readParams);

          // Invoke the read method, passing in the input stream to
          // retrieve the user exception.  Mark and reset the stream
          // as to not disturb it.
          InputStream ueInputStream = appException.getInputStream();
          ueInputStream.mark( 0 );
          UserException userException = null;
          try {
            java.lang.Object[] readArguments = new java.lang.Object[1];
            readArguments[0] = ueInputStream;
            userException = (UserException) readMethod.invoke(null, readArguments);
          } finally {
            try {
              ueInputStream.reset();
            } catch( IOException e ) {
              if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
                LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("Error in getting exception. Reset of the stream failed on Application Exception : " + e);
                LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ClientRequest.exceptionToAny(Exception, Any)", LoggerConfigurator.exceptionTrace(e));
              }
            }
          }

        any = exceptionToAny(userException, any);
      } catch (Exception un_ex) {
        any = exceptionToAny(new UNKNOWN("Could not insert UserException into Any", MinorCodes.UNKNOWN_USER_EXCEPTION, CompletionStatus.COMPLETED_MAYBE ), any);
      }
    } else if (exception instanceof UserException) {
      UserException userException = (UserException) exception;
      try {
        // Insert this UserException into the provided Any using the
        // helper class.
        if( userException != null ) {
          Class exceptionClass = userException.getClass();
          String className = exceptionClass.getName();
          String helperClassName = className + "Helper";
          Class helperClass = Class.forName(helperClassName);

          // Find insert( Any, class ) method
          Class[] insertMethodParams = new Class[2];
          insertMethodParams[0] = org.omg.CORBA.Any.class;
          insertMethodParams[1] = exceptionClass;
          Method insertMethod = helperClass.getMethod("insert", insertMethodParams);

          // Call helper.insert( result, userException ):
          java.lang.Object[] insertMethodArguments = new java.lang.Object[2];
          insertMethodArguments[0] = any;
          insertMethodArguments[1] = userException;
          insertMethod.invoke( null, insertMethodArguments );
        }
      } catch (Exception ex) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("ClientRequest.exceptionToAny(Exception, Any)", LoggerConfigurator.exceptionTrace(ex));
        }
        throw new UNKNOWN( "Could not insert UserException into Any", MinorCodes.UNKNOWN_USER_EXCEPTION, CompletionStatus.COMPLETED_MAYBE );
      }
    }

    return any;
  }

  private String getRepIdClassName(String repositoryId) {
    if ((repositoryId.length() == 0)) {
      return "";
    } else if (repositoryId.equals("IDL:omg.org/CORBA/WStringValue:1.0")) {
      return "java.lang.String";
    } else if (repositoryId.startsWith("RMI:")) {
      return repositoryId.substring(4, repositoryId.indexOf(':', 4));
    } else if (repositoryId.startsWith("IDL:")) {
      String typeString = repositoryId.substring(4, repositoryId.indexOf(':', 4));

      if (typeString.startsWith("omg.org/")) {
        return "org.omg." + typeString.substring("omg.org/".length()).replace('/','.');
      } else {
        return typeString.replace('/','.');
      }
    }

    return null;
  }

}

