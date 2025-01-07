package com.sap.engine.services.rmi_p4;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.thread.ClientThreadContext;
import com.sap.engine.frame.core.thread.ContextObject;
import com.sap.engine.frame.core.thread.ContextObjectNameIterator;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.frame.core.thread.Transferable;
import com.sap.engine.frame.core.load.LoadContext;
import com.sap.engine.lib.lang.Convert;
import com.sap.engine.services.rmi_p4.monitor.PingCall;
import com.sap.engine.services.rmi_p4.dsr.DSRP4Instr;
import com.sap.engine.services.rmi_p4.dsr.DSRP4RequestContextImpl;
import com.sap.engine.services.rmi_p4.exception.*;
import com.sap.engine.system.ThreadWrapper;
import com.sap.engine.interfaces.cross.Connection;
import com.sap.engine.interfaces.cross.StreamHook;
import com.sap.engine.session.exec.SessionExecContext;
import com.sap.tc.logging.Location;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.Operation;
import java.util.Enumeration;
import java.net.URL;

/**
 * Dispatcher for the incoming requests.
 * implements Runnable in order to be run
 *
 * @author Georgy Stanev
 * @version 7.10
 */
public class DispatchImpl implements Runnable, Dispatch {

  static long temp;
  static int counter = 0;
  protected boolean running = true;
  static int bla0 = 0;
  protected static ClassLoader dataLoader = null;
  private boolean is_call_request_ok = true;
  private boolean firstRun = true;

  private DataOptOutputStream dout = null;
  private DataOptInputStream din = null;
  /**
   * the incoming message
   */
  public Message message;
  /**
   * the repliable from witch the message comes
   */
  public Connection repliable;
  /**
   * the requested in the message object
   */
  public P4RemoteObject object; // the impl
  /**
   * the info of the requested object
   */
  public P4RemoteObjectInfo info;
  /**
   * the skeleton proxy of the requested object
   */
  public Skeleton skel; // the skel
  /**
   * outgoing stream
   */
  public MarshalOutputStream out;
  /**
   * incoming stream
   */
  public MarshalInputStream in;
  /**
   * the object broker
   */
  public P4ObjectBroker broker;
  public DispatchPool pool;
  public ThreadContext tC = null;
  public P4ContextObject p4co = null;
  public byte[] p4co_data;
  int tc_size = 0;

  public synchronized void setData(Message message, P4ObjectBroker broker, Connection repliable) {
    this.message = message;
    this.repliable = repliable;
    this.broker = broker;
    this.notifyAll();
  }

  protected DispatchImpl(DispatchPool _pool) {
    this.pool = _pool;
  }

  /**
   * Creates new DispatchImpl object for the specified message
   *
   * @param message   the request for dispatching
   * @param broker    the object broker
   * @param repliable the connection
   */
  public DispatchImpl(Message message, P4ObjectBroker broker, Connection repliable) {
    this.message = message;
    this.repliable = repliable;
    this.broker = broker;
  }

  public DispatchImpl(P4ObjectBroker broker, Connection repliable) {
    this.repliable = repliable;
    this.broker = broker;
  }

  public void setPool(DispatchPool _pool) {
    this.pool = _pool;
  }

  protected void throwException(Exception ex) {
    is_call_request_ok = false;
    byte[] reply = null;
    int size = 0;
    if (P4Logger.getLocation().bePath()) {
      if (broker.isServerBroker()) {
        P4Logger.getLocation().pathT("DispatchImpl.throwException(Exception)", "P4 Call execution: Exception in execute operation :<" + message.opName + "> : " + ex.getMessage() + " called from: " + message.client_id + " received from server id: " + message.sender_id + " implementation on server id : " + message.clusterEl_id);
      } else {
        P4Logger.getLocation().pathT("DispatchImpl.throwException(Exception)", "P4 Call execution: Exception in execute operation :<" + message.opName + "> : " + ex.getMessage() + " called from: " + repliable + " received from server id: " + message.sender_id + " implementation on server id : " + message.clusterEl_id);
      }
    }
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("DispatchImpl.throwException(Exception)", P4Logger.exceptionTrace(ex));
    }
    try {
      out = (MarshalOutputStream) getNewOutputStream();
      try {
        out.writeObject(ex);
      } catch (Exception th) {
        if (P4Logger.getLocation().beError()) {
          P4Logger.trace(P4Logger.ERROR, "DispatchImpl.throwException(Exception)", "Exception occurred during execution of remote method. Exception was not sent to the client, because serialization and sending of orignal exception failed. A new exception will be sent to the client with original message and class name included in it. Remote address: {0}. Thrown exception: {1}", "ASJ.rmip4.rt2002", new Object []{remoteAddressAndPortToString(repliable), P4Logger.exceptionTrace(th)});
        }
        out = (MarshalOutputStream) getNewOutputStream();
        out.writeObject(new Exception("Communication Error: The original exception cannot be sent to the client: " + th.getClass().getName() + ": " + th.getMessage() + ". Original Exception is: <" + ex.getClass().getName() + ": " + ex.getMessage() + ">, occurred during call of operation: " + message.opName + " on cluster id: " + message.own_id + ". Check server traces for details"));
      }
      out.close();
      reply = out.getBuffer();
      size = out.getSize();
    } catch (IOException ioex) {
      if (P4Logger.getLocation().beError()) {
        P4Logger.trace(P4Logger.ERROR, "DispatchImpl.throwException(Exception)", "Exception occurred during execution of remote method. Failed to create output stream for serializing original exception, or failed to serialize and send wrapped exception. An empty error message will be sent to the client. Remote address: {0}. Thrown exception: {1}", "ASJ.rmip4.rt2003", new Object []{remoteAddressAndPortToString(repliable), P4Logger.exceptionTrace(ioex)});
      }
      reply = new byte[ProtocolHeader.END_PROTOCOL];
      size = reply.length;
    }
    System.arraycopy(message.call_id, 0, reply, ProtocolHeader.CALL_ID, 8);
    reply[ProtocolHeader.MESSAGE_TYPE] = 2;
    ProtocolHeader.writeHeader(reply, 0, size, message.clusterEl_id);
    informDSR(DSRP4RequestContextImpl.OUTGOING, DSRP4RequestContextImpl.ERRORREPLY); // 11-"outgoing", 2-"errorreply"
    try {
      repliable.sendReply(reply, size, message.call_id);
    } catch (IOException io) {
      if (P4Logger.getLocation().beInfo()) {
        P4Logger.getLocation().debugT("DispatchImpl.throwException(Exception)", "Failed to send reply. Probably the client is already disconected");
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("DispatchImpl.throwException(Exception)", P4Logger.exceptionTrace(io));
        }
      }
    }
  }
  
  /**
   * This method informs DSR for end of incoming call in case of enabled monitoring via DSR. 
   * @param reply This is the received message as byte array.
   */
  public void informDSR(int direction, int type){
    //In case of Exception is invoked method in parent P4Call.setException(Message)
    if (P4ObjectBroker.isDSRMonitorable()) {
      DSRP4RequestContextImpl requestContext = new DSRP4RequestContextImpl();
      if (message.info != null && message.info.getStubs() != null){
        String[] stubs = message.info.getStubs();
        StringBuilder implementors = new StringBuilder("");
        for (int i=0; i<stubs.length; i++){
          implementors.append(stubs[i]);
          if (i<stubs.length-1) { //Separate with comma interfaces, if they are more than one 
            implementors.append(", ");
          }
        }
        requestContext.setStubClass(implementors.toString());
      }
      if (requestContext.getStubClasses() == null && object != null && object.info.skeleton.getImplemntsObjects() != null) {
        String[] skels = object.info.skeleton.getImplemntsObjects();
        StringBuilder implementors = new StringBuilder("");
        for (int i=0; i<skels.length; i++){
          implementors.append(skels[i]);
          if (i<skels.length-1) { //Separate with comma interfaces, if they are more than one 
            implementors.append(", ");
          }
        }
        requestContext.setStubClass(implementors.toString());
      }
      //Retrieve exported object (com.sap.engine.services.rmi_p4.ContextObjectClassReceiver) for method getClassData().
      if (requestContext.getStubClasses() == null && object == null && broker.id == message.own_id) {
        P4RemoteObject dclSkeleton = broker.getObject(message.objectKey);
        if (dclSkeleton != null) {
          String[] skels = dclSkeleton.info.skeleton.getImplemntsObjects();
          StringBuilder implementors = new StringBuilder("");
          for (int i=0; i<skels.length; i++){
            implementors.append(skels[i]);
            if (i<skels.length-1) { //Separate with comma interfaces, if they are more than one 
              implementors.append(", ");
            }
          }
          requestContext.setStubClass(implementors.toString());
        }
      }
      requestContext.setServerID(broker.id); 
      requestContext.setClientID(message.sender_id);
      requestContext.setOperation(message.opName);
      requestContext.setRedirectable(message.isRedir);
      requestContext.setConnectionID(repliable.getId());
      requestContext.setCallID(message.getCallId());
      requestContext.setBytes(message.getSize());
      requestContext.setDirection(direction);
      requestContext.setType(type);
      if (repliable.getProperties() != null) {
        requestContext.setHost(repliable.getProperties().getRemoteAddress());
        requestContext.setPort(repliable.getProperties().getRemotePort());
        requestContext.setConnectionType(repliable.getProperties().getTransportQueue());
      }
      if (P4ObjectBroker.isEnabledAccounting() && P4ObjectBroker.getBroker() != null && P4ObjectBroker.getBroker().isServerBroker()) {
        P4ObjectBroker.getBroker().beginMeasure("P4/DSR_Monitoring", DSRP4Instr.getRegistered().getClass());
      }
      if (type == DSRP4RequestContextImpl.REQUEST){
        DSRP4Instr.requestStart(requestContext);
      }else {
        DSRP4Instr.requestEnd(requestContext);
      }
      if (P4ObjectBroker.isEnabledAccounting() && P4ObjectBroker.getBroker() != null && P4ObjectBroker.getBroker().isServerBroker()) {
        broker.endMeasure("P4/DSR_Monitoring");
      }
    }
  }

  /**
   * Used for redirectable messages to determinate from given string 
   * the name of registered factory for this remote object.
   * @param str The original string from getIdentifier() method in the current implementation of Redirectable interface  
   * @return The name of the factory according to received String.
   */  
  protected String getFactoryPart(String str) {
    int ind = str.indexOf(':');
    if (ind == -1) {
      return str;
    } else {
      return str.substring(0, ind);
    }
  }

  /**
   * Used for redirectable messages to determinate from given string 
   * the identification string for remote object that we want to build from registered factory.
   * @param str The original string from getIdentifier() method in the current implementation of Redirectable interface  
   * @return Object ID according to received String.
   */  
  protected String getObjectPart(String str) {
    int ind = str.indexOf(':');
    if (ind == -1) {
      return "";
    } else {
      return str.substring(ind + 1);
    }
  }

  public void _run() {

    _runInternal();
  }

  public void _runInternal() {
    boolean popSubTask = false;
    try {
      P4Call call = null;

      switch (message.type) {
        case Message.CALL_REQUEST: {
          boolean isOptmizedParams = false;
          boolean isOptmizedResult = false;
          try {
            ByteArrayInputStream bin = message.getByteArrayInputStream();
            if (message.opName.startsWith(P4ObjectBroker.BOTH_OPTIMIZED) || message.opName.startsWith(P4ObjectBroker.RESULT_OPTIMIZED)) {
              isOptmizedResult = true;
            }
            if (message.opName.startsWith(P4ObjectBroker.BOTH_OPTIMIZED) || message.opName.startsWith(P4ObjectBroker.PARAM_OPTIMIZED)) {
              isOptmizedParams = true;
              din = new DataOptInputStream(bin);
              din.setConnectionType(P4ObjectBroker.transportType);
            } else {
              in = new MarshalInputStream(bin, true);
              in.setConnectionType(P4ObjectBroker.transportType);
            }
          } catch (RuntimeException rte) {
            throwException(rte);
            return;
          } catch (IOException ioex) {
            throwException(ioex);
            return;
          }
          String op = message.opName;
          ThreadWrapper.pushSubtask(op, ThreadWrapper.TS_PROCESSING);
          popSubTask = true;
          if (op.equals(StubImpl.GET_CLASS_DATA)) {// get class from the server
            if (broker.isServerBroker()) {
              try {
                //Client environment will download followinf classes for DSR: 
                //com.sap.engine.services.dsr.server.DSRTransferable, com.sap.jdsr.writer.DsrIPassport, com.sap.engine.services.dsr.DSRHandler;  
                informDSR(DSRP4RequestContextImpl.INCOMING, DSRP4RequestContextImpl.REQUEST);
                getClassData((String) in.readObject());//class name
              } catch (Exception ex) {
                throwException(ex);
                return;
              }
            } else {
              throwException(new P4RuntimeException("Operation not supported")); // not supported on clients or between server nodes
              return;

            }
          } else if (op.equals(StubImpl.GET_RESOURCE_DATA)) {// get resource from the server
            if (broker.isServerBroker()) {
              try {
                getResourceData((String) in.readObject());
              } catch (Exception ex) {
                throwException(ex);
                return;
              }
            } else {
              throwException(new P4RuntimeException("Operation not supported")); // not supported on clients or between server nodes
              return;

            }
          } else {
            if (broker.id == message.own_id) {
              try {
                object = broker.getObject(message.objectKey);
                if (object == null) {
                  if (message.isRedir) {
                    checkDB();
                    if (object == null) {
                      if (P4Logger.getLocation().beInfo()) {
                        P4Logger.getLocation().infoT("DispatchImpl._runInternal()", "Invoking remote method: " + op + " redirected to another server.");
                      }
                      return;     //message is redirected to another server
                    }
                    p4co_data = new byte[message.objectKey.length + 4];
                    System.arraycopy(object.getInfo().key, 0, p4co_data, 0, message.objectKey.length);
                    Convert.writeIntToByteArr(p4co_data, message.objectKey.length, message.own_id);
                    p4co = new P4ContextObject();
                    p4co.load(p4co_data, 0);
                  } else {
                    byte[] timeStamp = P4ObjectBroker.init().getTimeStamp();
                    if (!(timeStamp[0] == message.objectKey[8] && timeStamp[1] == message.objectKey[9] && timeStamp[2] == message.objectKey[10] && timeStamp[3] == message.objectKey[11])) {
                      throwException(new java.rmi.NoSuchObjectException("Object timestamp does not match. Probably the server node was restarted"));
                    } else {
                      throwException(new java.rmi.NoSuchObjectException("The requested object was not found and the message is not redirectable"));
                    }
                    return;
                  }
                }
              } catch (java.rmi.NoSuchObjectException e) {
                throwException(e);
                return;
              } catch (Exception ce) {
                throwException(ce);
                return;
              }
            } else {
              try {
                if (message.isRedir) {
                  checkDB();
                  if (object == null) {
                    if (P4Logger.getLocation().beInfo()) {
                      P4Logger.getLocation().infoT("DispatchImpl._runInternal()", "Invoking remote method: " + op + " redirected to another server");
                    }
                    return;  //message is redirected to another server
                  }
                  p4co_data = new byte[message.objectKey.length + 4];
                  System.arraycopy(object.getInfo().key, 0, p4co_data, 0, message.objectKey.length);
                  Convert.writeIntToByteArr(p4co_data, message.objectKey.length, broker.id);
                  p4co = new P4ContextObject();
                  p4co.load(p4co_data, 0);
                } else {
                  //Logging will cause hang in ATS when Redirectable is tested, because of ATS remote logger
                  throwException(new java.rmi.ServerException("The server process, that the client is connected with, is not working. The object is not redirectable"));
                  return;
                }
              } catch (java.rmi.NoSuchObjectException e) {
                throwException(e);
                return;
              } catch (Exception ce) {
                throwException(ce);
                return;
              }
            }
            info = object.getInfo();
            skel = info.skeleton;
            Class dcClass = skel.getClass();
            ClassLoader cl = skel.getClass().getClassLoader();
            if (skel instanceof P4DynamicSkeleton) {
              cl = object.getDelegate().getClass().getClassLoader();
              dcClass = object.getDelegate().getClass();
            }
            if (isOptmizedParams) {
              din.setClassLoader(cl);
            } else {
              in.setClassLoader(cl);
              if (object.getDelegate() instanceof StreamHook) {
                in.setHook((StreamHook) object.getDelegate());
              }
            }

            Operation[] operations = skel.getOperations();
            int opnum = 0;
            boolean flag = false;
            while (opnum < operations.length) {
              if (op.equals(operations[opnum].getOperation())) {
                flag = true;
                break;
              } else {
                opnum++;
              }
            }
            if (flag) {
              informDSR(DSRP4RequestContextImpl.INCOMING, DSRP4RequestContextImpl.REQUEST); //10 - "incoming", 0 - "request"
              if (broker.isServerBroker()){
                SessionExecContext execContext = SessionExecContext.getExecutionContext();
                if (execContext != null ) {
                  execContext.setDetails(repliable.getProperties());
                }
              }
              ClassLoader old_cl = Thread.currentThread().getContextClassLoader();
              if (P4Logger.getLocation().beInfo()) {
                if (broker.isServerBroker()) {
                  P4Logger.getLocation().infoT("DispatchImpl._runInternal()", "Invoking remote method: " + op + " skeleton: " + skel + " from connection id: " + message.client_id + " received from server id: " + message.sender_id + " implementation on server id : " + message.clusterEl_id);
                } else {
                  P4Logger.getLocation().infoT("DispatchImpl._runInternal()", "Invoking remote method: " + op + " skeleton: " + skel + " from connection: " + repliable.toString() + " received from server id: " + message.sender_id + " implementation on server id : " + message.clusterEl_id);
                }
              }
              try {
                Thread.currentThread().setContextClassLoader(cl);
              } catch (java.security.AccessControlException ace) {
                old_cl = null;
                P4Logger.trace(P4Logger.ERROR, "DispatchImpl._runInternal()", "Setting context class loader failed. Exception: {0}", "ASJ.rmip4.rt2004", new Object []{P4Logger.exceptionTrace(ace)});
              }
              broker.beginMeasure("P4/Delegate_Control_To_Implementation", dcClass);
              try {
                skel.dispatch((Remote) object, this, opnum);
              } catch (Exception _) {
                P4Logger.trace(P4Logger.PATH, "DispatchImpl._runInternal()", "Implementation of remote object has thrown an exception. Exception: {0}, remote object: {1}, skeleton: {2}, operation number: {3}, operation name matched: {4}, caller remote address: {5}", "ASJ.rmip4.rt2001", new Object []{_.getMessage(), object, skel, opnum, op.equals(operations[opnum].getOperation()), remoteAddressAndPortToString(repliable)}, cl, null);
                old_cl = null;
                throwException(_);
                return;
              } catch (OutOfMemoryError oomm) {
                //$JL-EXC$
                throwException(new RemoteException("OutOfMemory occurred while executing method: " + op + " to skel: " + skel, oomm));
                throw oomm;
              } catch (ThreadDeath td) {
                //$JL-EXC$
                throwException(new RemoteException("ThreadDeath occurred while executing method: " + op + " to skel: " + skel, td));
                throw td;
              } catch (Error thr) { //$JL-EXC$ Some error can be thrown it must be sent to the client and rethrown here to exit
                if (P4Logger.getLocation().beError()) {
                  P4Logger.trace(P4Logger.ERROR, "DispatchImpl._runInternal()", "Implementation of remote object has thrown an error. ERROR: {0}, remote object: {1}, skeleton: {2}, operation number = {3}, operation name matched: {4}, caller remote address: {5}", "ASJ.rmip4.rt2005", new Object []{thr.getMessage(), object, skel, opnum, op.equals(operations[opnum].getOperation()), remoteAddressAndPortToString(repliable)}, cl, null);
                }
                throwException(new RemoteException("ERROR: ", thr));
                throw thr;
              } finally {
                if (broker.isServerBroker()){
                  SessionExecContext execContext = SessionExecContext.getExecutionContext();
                  if (execContext != null ) {
                    execContext.setDetails(null);
                  }
                  broker.endMeasure("P4/Delegate_Control_To_Implementation");
                }

                if (old_cl != null) {
                  Thread.currentThread().setContextClassLoader(old_cl);
                }
              }
            } else {
              if (P4Logger.getLocation().beWarning()) {
                P4Logger.trace(P4Logger.WARNING, "DispatchImpl.runInternal()",  "No such method: {0} in remote object: {1} for skeleton: {2}. Check version of remote object in client and server side. Ask owner of remote object for known incompatibilities", "ASJ.rmip4.rt2039", new Object[]{op, object, skel}, object.getClass().getClassLoader(), null);
              }
              if (P4Logger.getLocation().beDebug()) {
                P4Logger.getLocation().debugT("DispatchImpl.runInternal()", "No such method: " + op + " in remote object: " + object + " for skeleton: " + skel + " Remote Object Info: \r\n" + info);
              } 
              throwException(new com.sap.engine.services.rmi_p4.exception.NoSuchOperationException("Incorrect Operation: " + op + " in skeleton " + skel + ". Reason: incompatible version of stub - method available in stub, but does not exist in skeleton side"));
              return;
            }
          }
          
          if (isOptmizedResult && dout != null) {
            dout.flush();
            sendOptReply(dout);
          } else {
            out.flush();
            sendReply(out);
          }
          
          try {
            if (isOptmizedResult && dout != null) {
              dout.close();
            }else {
              if (out != null) {
                out.close();
              }
            }
          } catch (IOException ioex) {
            if (P4Logger.getLocation().beDebug()) {
              P4Logger.getLocation().debugT("DispatchImpl._runInternal()", P4Logger.exceptionTrace(ioex));
            }
          }
          break;
        }
        case Message.DISPATCH_REPLY: {
          call = P4Call.getCall(message.getCallId());
          if (call != null) {
            message.inCall = true;
            repliable.callCompleted(call);
            call.setReply(message);
          }
          break;
        }
        case Message.DISPATCH_ERROR_REPLY: {
          call = P4Call.getCall(message.getCallId());
          if (call != null) {
            message.inCall = true;
            repliable.callCompleted(call);
            call.setException(message);
          }
          break;
        }
        case Message.SPECIAL_ERROR_MESSAGE: {
          P4Call callTemp = P4Call.getCall(message.getCallId());
          if (callTemp != null) {
            message.inCall = true;
            repliable.callCompleted(callTemp);
            ByteArrayInputStream bis = message.getByteArrayInputStream();
            byte[] errorCodeByteArr = new byte[4];
            bis.read(errorCodeByteArr, 0, 4);
            if (P4Logger.getLocation().beDebug()) {
              P4Logger.getLocation().debugT("DispatchImpl._runInternal()", "Special Error Message from the dispatcher. There are problems with the servers in the cluster. Error message: " + message.getSpecialErrorMessage(Convert.byteArrToInt(errorCodeByteArr, 0)));
            }
            callTemp.setException(broker.getException(P4ObjectBroker.P4_ConnectionException, "Special Error Message from the dispatcher. There are problems with the servers in the cluster. Error message: " + message.getSpecialErrorMessage(Convert.byteArrToInt(errorCodeByteArr, 0)), null));
          }
          break;
        }
        case Message.PING_CONNECTION_MESSAGE_SERVER: {
          PingCall callTemp = PingCall.getCall(message.getCallId());
          if (callTemp != null) {
            message.inCall = true;
            repliable.callCompleted(callTemp);
            callTemp.set(message);
          }
          break;
        }
        case Message.RESOLVE_INITIAL_REFERENCE: {
          String name = null;
          try {
            name = Convert.byteArrToUString(message.getUnmarshaledRequest());
            ThreadWrapper.pushSubtask("resolve initial reference request-" + name, ThreadWrapper.TS_PROCESSING);
            popSubTask = true;
            byte[] reference = P4ObjectBroker.init().getInitialObject(name);
            int size = reference.length + 23; //protocol header + 9
            if (message.request.length < size) {
              message.request = new byte[size];
              System.arraycopy(message.call_id, 0, message.request, ProtocolHeader.CALL_ID, 8);
            }
            message.request[ProtocolHeader.MESSAGE_TYPE] = Message.RESOLVE_INITIAL_REFERENCE_REPLY;
            System.arraycopy(reference, 0, message.request, ProtocolHeader.THREAD_CONTEXT_SIZE, reference.length);
            ProtocolHeader.writeHeader(message.request, 0, size, message.sender_id);
            repliable.sendReply(message.request, size, message.call_id);
          } catch (RemoteException rex) {
            throwException(rex);
            return;
          } catch (Exception ex) {
            if (P4Logger.getLocation().beDebug()) {
              P4Logger.getLocation().debugT("DispatchImpl._runInternal()", P4Logger.exceptionTrace(ex));
            }
            throwException(new RemoteException("Could not get initial object with name " + name + ". Caused by: " + ex.toString()));
            return;
          }

          break;
        }
        case Message.RESOLVE_INITIAL_REFERENCE_REPLY: {
          P4Call inCall = P4Call.getCall(message.getCallId());
          if (inCall != null) {
            message.inCall = true;
            repliable.callCompleted(inCall); 
            inCall.setReply(message);
          }
          break;
        }
        case Message.INFORM_MESSAGE: {
          broker.inform(repliable, message);
          break;
        }
        case Message.GET_CONTEXT_OBJECT: {
          String name_CO = Convert.byteArrToAString(message.request, (ProtocolHeader.HEADER_SIZE + 9), (message.request.length - ProtocolHeader.HEADER_SIZE - 9));
          Object contextObj = tC.getContextObject(tC.getContextObjectId(name_CO));
          byte[] tmp = contextObj.getClass().getName().getBytes();
          int size = tmp.length + 23;
          if (message.request.length < size) {
            message.request = new byte[size];
            System.arraycopy(message.call_id, 0, message.request, ProtocolHeader.CALL_ID, 8);
          }
          message.request[ProtocolHeader.MESSAGE_TYPE] = Message.GET_CONTEXT_OBJECT_REPLY;
          System.arraycopy(tmp, 0, message.request, ProtocolHeader.THREAD_CONTEXT_SIZE, tmp.length);
          ProtocolHeader.writeHeader(message.request, 0, size, message.sender_id);
          try {
            repliable.sendReply(message.request, size, message.call_id);
          } catch (Exception ex) {
            if (P4Logger.getLocation().beDebug()) {
              P4Logger.getLocation().debugT("DispatchImpl._runInternal()", P4Logger.exceptionTrace(ex));
            }
          }

          break;
        }
        case Message.GET_CONTEXT_OBJECT_REPLY: {
          Message.obj.setInfo(message.request);
          Message.obj.notify();
          message.request = null;
          break;
        }
        default: {
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("DispatchImpl._runInternal()", "Incorrect message type : " + message.type);
          }
        }
      }
    } catch (Exception e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("DispatchImpl._runInternal()", P4Logger.exceptionTrace(e));
      }
    } finally {
      if (popSubTask) {
        ThreadWrapper.popSubtask();
      }
    }
  }

  private void sendOptReply(DataOptOutputStream out) {
    sendReply(out.getBuffer(), out.getSize());
  }

  private void sendReply(MarshalOutputStream out) {
    sendReply(out.getBuffer(), out.getSize());
  }

  private void sendReply(byte[] reply, int size) {
    System.arraycopy(message.call_id, 0, reply, ProtocolHeader.CALL_ID, 8);
    reply[ProtocolHeader.MESSAGE_TYPE] = 1;
    ProtocolHeader.writeHeader(reply, 0, size, message.clusterEl_id);
    try {
      repliable.sendReply(reply, size, message.call_id);
    } catch (Exception io) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("DispatchImpl.sendReply(byte[], int)", P4Logger.exceptionTrace(io));
      }
      if (P4Logger.getLocation().beError()) {
        P4Logger.trace(P4Logger.ERROR, "DispatchImpl.sendReply(byte[], int)", "Sending reply failed. Client may hang if connection is not already closed. Exception: {0}", "ASJ.rmip4.rt2006", new Object []{remoteAddressAndPortToString(repliable), io.getMessage()});
      }
    }
    this.informDSR(DSRP4RequestContextImpl.OUTGOING, DSRP4RequestContextImpl.REPLY); //11 - "outgoing", 1 - "reply"
  }


  /**
   * main thread method
   */
  public void run() {
    boolean isClean = true;
    while (isClean) {
      synchronized (this) {
        if (firstRun) {
          firstRun = false;
        } else {
          if (!pool.returnInPool(this) || !running) {
            return;
          }
        }
        while (message == null) {
          try {
            this.wait();
          } catch (InterruptedException interrupted) { //$JL-EXC$ that is usual event
            P4Logger.getLocation().traceThrowableT(P4ExceptionConstants.SEVERITY_WARNING, "DispatchImpl.run:", interrupted);
            return;
          }
          if (!running) {
            return;
          }
        }
      }
      try {
        _run();
      } catch (Throwable e) {
        P4Logger.getLocation().traceThrowableT(P4ExceptionConstants.SEVERITY_WARNING, "DispatchImpl.run:", e);
      } finally {
        this.message = null;
        this.repliable = null;
        this.broker = null;
        this.object = null;
        this.info = null;
        this.skel = null;
        this.in = null;
        this.out = null;
        this.din = null;
        this.dout = null;
        P4ObjectBroker p4 = P4ObjectBroker.getBroker();
        ClientThreadContext ctc = p4.getCTC();
        ContextObjectNameIterator coIt = ctc.getContextObjectNames();
        /*  sets null in all context objects which are return in the pool */
        while (coIt.hasNext()) {
          String name = coIt.nextName();
          ContextObject co = ctc.getContextObject(name);
          if (co != null) {
            try {
              co.empty();
            } catch (Throwable t) {
              if (P4Logger.getLocation().beDebug()) {
                P4Logger.getLocation().debugT("DispatchImpl.run()", t.getMessage());
              }
              isClean = false;
            }
          }
        }
      }
    }
  }

  /**
   * Used in normal replies. 
   * @return stream for output
   */
  public P4ObjectOutput getOutputStream() {
    tc_size = broker.getTCSize();
    if (message.isRedir && (p4co_data != null)) {
      if (tc_size == 0) {
        tc_size += p4co.size() + P4ContextObject.NAME.length() + 6; // plus two bytes for the COs count that are otherwise calculated in tc_size
      } else {
        tc_size += p4co.size() + P4ContextObject.NAME.length() + 4;
      }
    }
    try {
      out = new MarshalOutputStream(new ByteArrayOutput(ProtocolHeader.END_PROTOCOL + tc_size));
      Convert.writeIntToByteArr(out.getBuffer(), ProtocolHeader.THREAD_CONTEXT_SIZE, tc_size);
      if (tc_size > 0) {
        storeContextObjects(out.getBuffer(), ProtocolHeader.END_PROTOCOL, message.isRedir);
      }
    } catch (IOException ioex) {
      throw new P4RuntimeException("Failed to create output stream: " + ioex.getMessage(), ioex);
    }
    return out;
  }

  public P4ObjectOutput getNewOutputStream() throws IOException {
    try {
      tc_size = broker.getTCSize();
      if (message.isRedir && (p4co_data != null)) {
        if (tc_size == 0) {
          tc_size += p4co.size() + P4ContextObject.NAME.length() + 6; // plus two bytes for the COs count that are otherwise calculated in tc_size
        } else {
          tc_size += p4co.size() + P4ContextObject.NAME.length() + 4;
        }
      }
      out = new MarshalOutputStream(new ByteArrayOutput(ProtocolHeader.END_PROTOCOL + tc_size));
      Convert.writeIntToByteArr(out.getBuffer(), ProtocolHeader.THREAD_CONTEXT_SIZE, tc_size);
      if (tc_size > 0) {
        storeContextObjects(out.getBuffer(), ProtocolHeader.END_PROTOCOL, message.isRedir);
      }
    } catch (RuntimeException e) {
      tc_size = 0;
      if (P4ObjectBroker.getBroker() != null) {
        if (P4Logger.getLocation().beError()) {
          P4Logger.trace(P4Logger.ERROR, "DispatchImpl.getNewOutputStream()", "Failed to create output stream, or serialization of transferable context objects failed. The error message will be sent without context objects. \r\nException: {0}", "ASJ.rmip4.rt2007", new Object []{P4Logger.exceptionTrace(e)});
        }
      } else {
        if (P4Logger.getLocation().bePath()) {
          P4Logger.trace(P4Logger.PATH, "DispatchImpl.getNewOutputStream()", "Failed to get transferable context objects. Error-reply message will be sent without context objects. P4 service is stopping, while P4 message is in process \r\nException: " + P4Logger.exceptionTrace(e));
        }
      }
      out = new MarshalOutputStream(new ByteArrayOutput(ProtocolHeader.END_PROTOCOL));
    }

    return out;
  }

  /**
   * @return input stream created from the request
   */
  public P4ObjectInput getInputStream() {
    return in;
  }


  public DataOptInputStream getDataInputStream() {
    return din;
  }


  public DataOptOutputStream getDataOutputStream() {
    tc_size = broker.getTCSize();
    if (message.isRedir && (p4co_data != null)) {
      if (tc_size == 0) {
        tc_size += p4co.size() + P4ContextObject.NAME.length() + 6; // plus two bytes for the COs count that are otherwise calculated in tc_size
      } else {
        tc_size += p4co.size() + P4ContextObject.NAME.length() + 4;
      }
    }
    dout = new DataOptOutputStream(new ByteArrayOutput(ProtocolHeader.END_PROTOCOL + tc_size));
    Convert.writeIntToByteArr(dout.getBuffer(), ProtocolHeader.THREAD_CONTEXT_SIZE, tc_size);
    if (tc_size > 0) {
      storeContextObjects(dout.getBuffer(), ProtocolHeader.END_PROTOCOL, message.isRedir);
    }
    return dout;

  }

  /**
   * closes created input stream
   */
  public void releaseInputStream() {
    try {
      if (in != null) {
        in.close();
      }

      if (din != null) {
        din.close();
      }
    } catch (IOException ioex) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("DispatchImpl.releaseInputStream()", P4Logger.exceptionTrace(ioex));
      }
    }
  }

  public void writeIntToShort(byte[] buf, int off, int d) {
    com.sap.engine.lib.lang.Convert.writeShortToByteArr(buf, off, (short) d);
  }

  public void writeString(byte[] buf, int off, String str) {
    com.sap.engine.lib.lang.Convert.writeAStringToByteArr(buf, off, str);
  }

  /*
   * Invoked from getOutputStream, getNewOutputStream and getDataOutputStream.
   * Directly fill context objects into given byte-array for the reply 
   * 
   * @param reply The reply as byte array
   * @param nextPos position, that we begin to fill from
   * @param isRedir If the object was redirected
   */
  public void storeContextObjects(byte[] reply, int nextPos, boolean isRedir) { 
    int coCounts = 0;
    int byteArrLen = 0;
    P4ObjectBroker p4 = P4ObjectBroker.getBroker();
    ClientThreadContext ctc;
    try {
      ctc = p4.getCTC();
    }catch (NullPointerException npe){
      //$JL-EXC$
      //P4 service is already stopped so P4 ObjectBroker is already null
      throw new P4RuntimeException("Request was canceled because the P4 service is shutting down, or is not started yet");
    }

    //We do not have to count context objects in advance, we can just keep 
    //the position in byte-array for this record, and skip it for now...
    int coCountPosition = nextPos;
    nextPos = nextPos + 2;
    
    //write P4 context object for redirectable messages
    if (isRedir && (p4co_data != null)) {
      coCounts++; //Count P4 context object for redirected message as context object
      writeIntToShort(reply, nextPos, P4ContextObject.NAME.length());
      nextPos = nextPos + 2;
      writeString(reply, nextPos, P4ContextObject.NAME);
      nextPos = nextPos + P4ContextObject.NAME.length();
      writeIntToShort(reply, nextPos, p4co_data.length);
      nextPos = nextPos + 2;
      p4co.store(reply, nextPos);
      nextPos = nextPos + p4co_data.length;
    }
    try {
      //Write other context objects to the reply 
      if (ctc != null) {
        ContextObjectNameIterator coI = ctc.getTransferableContextObjectNames();
        String name = null;
        while (coI.hasNext()) {
          name = coI.nextName();
          ContextObject co = ctc.getContextObject(name);
          if (co == null) { //unknown context object - skip it;
            continue;
          }
          byteArrLen = ((Transferable) co).size();
          //Skip zero length context objects in reply
          if (byteArrLen > 0) {
            coCounts++; //Count added to reply context objects
            writeIntToShort(reply, nextPos, name.length());
            nextPos = nextPos + 2;
            writeString(reply, nextPos, name);
            nextPos = nextPos + name.length();
            writeIntToShort(reply, nextPos, byteArrLen);
            nextPos = nextPos + 2;
            ((Transferable) co).store(reply, nextPos);
            nextPos = nextPos + byteArrLen;
          }
        }
      }
    } catch (RuntimeException generalEx){
      if (P4Logger.getLocation().beError() || P4Logger.getSecLocation().beError()) {
        Location loc;
        if (P4Logger.getSecLocation().beWarning()) {
          loc = P4Logger.getSecLocation();
        } else {
          loc = P4Logger.getLocation();
        }
        P4Logger.trace(P4Logger.ERROR, loc, "DispatchImpl.getNewOutputStream(byte[], int)", "Serialization of transferable context objects failed. The message will be sent with some (or all) of the context objects missing \r\nException: {0}", "ASJ.rmip4.rt2036", new Object []{P4Logger.exceptionTrace(generalEx)});
      }
      throw generalEx;
    } catch (OutOfMemoryError oom) {
      throw oom;
    } catch (ThreadDeath td) {
      throw td;
    }catch (Error linkage) {
      if (P4Logger.getLocation().beError() || P4Logger.getSecLocation().beError()) {
        Location loc;
        if (P4Logger.getSecLocation().beWarning()) {
          loc = P4Logger.getSecLocation();
        } else {
          loc = P4Logger.getLocation();
        }
        P4Logger.trace(P4Logger.ERROR, loc, "DispatchImpl.getNewOutputStream(byte[], int)", "Serialization of transferable context objects failed. Context objects will not be added to the message; it will be sent with some (or all) of the context objects missing. \r\nError: {0}", "ASJ.rmip4.rt2037", new Object []{P4Logger.exceptionTrace(linkage)});
      }
      throw linkage;
    } 
    
    //Now write the number of written context objects into reply byte-array
    writeIntToShort(reply, coCountPosition, coCounts);
  }

  public void checkDB() throws java.rmi.NoSuchObjectException, ConfigurationException {
    //Logging may cause hang in ATS when Redirectable is tested
  }

  public void setMessage(Message m) {
    message = m;
  }

  public boolean isCallOK() {
    return is_call_request_ok;
  }

  // methods for remote class loader system

  private ClassLoader getClLoader(LoadContext lc, String classname, String loadername) {
    Class cls = null;
    ClassLoader definigLoader = null;
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("DispatchImpl.getClLoader(LoadContext, String, String): classname: " + classname + " loadername: " + loadername);
    }
    if (loadername != null && loadername.length() > 0) {
      ClassLoader cloader = lc.getClassLoader(loadername);
      try {
        cloader.loadClass(classname);
        definigLoader = cloader;
        return cloader;
      } catch (ClassNotFoundException e) {//  $JL-EXC$
      }
    }
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("DispatchImpl.getClLoader(LoadContext, String, String): trying to load class : " + classname + " with the other available loaders");
    }
    Enumeration enumeration = lc.listLoaders();
    while (enumeration.hasMoreElements()) {
      String name = (String) enumeration.nextElement();
      ClassLoader loader = lc.getClassLoader(name);
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("DispatchImpl.getClLoader(LoadContext, String, String): loader name: " + name + " loader: " + loader);
      }
      if (loader != null) {
        try {
          cls = Class.forName(classname, false, loader);
          definigLoader = cls.getClassLoader();
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("DispatchImpl.getClLoader(LoadContext, String, String): class : " + cls + " definingLoader: " + definigLoader);
          }
          if (definigLoader == null && cls != null) {
            /* maybe the class is loaded by VM's loader so each loader can do the work :-) */
            if (P4Logger.getLocation().beDebug()) {
              String msg = "P4Service: Class<" + classname + "> maybe is loaded by the VM's loader.";
              P4Logger.getLocation().debugT(msg);
            }
            return loader;
          }
          break;
        } catch (ClassNotFoundException e) {// $JL-EXC$ try with next
        } catch(NoClassDefFoundError e){// $JL-EXC$ try with next
        }
      }
    }
    return definigLoader;
  }

  public void getResourceData(String name) throws IOException, FileNotFoundException {
    P4ObjectOutput out = getOutputStream();
    LoadContext lc = P4ObjectBroker.init().getServiceContext().getCoreContext().getLoadContext();
    String resName = name.substring(0, name.indexOf(P4ObjectBroker.P4_DELIMITER));
    String serverLoaderName = name.substring((name.indexOf(P4ObjectBroker.P4_DELIMITER) + P4ObjectBroker.P4_DELIMITER.length()));
    InputStream in = null;

    if (P4ObjectBroker.init().getClass().getName().equals(P4ObjectBroker.SERVER_BROKER_CLASS)) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("DispatchImpl.getDesourceData trying to load resource : " + name + " with loader : " + serverLoaderName);
      }
      in = getRes(serverLoaderName, lc, resName);
      if (in == null) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("DispatchImpl.getDesourceData: getRes failed; trying to load resource : " + name + " with loader : " + this.getClass().getClassLoader());
        }
        in = this.getClass().getClassLoader().getResourceAsStream(resName);
      }
    } else {
      in = this.getClass().getClassLoader().getResourceAsStream(resName);
    }
    if (in == null) {
      throw new FileNotFoundException("Resource " + resName + " cannot be found in server process");
    }
    readWriteData(in);
  }

  private InputStream getRes(String resLoader, LoadContext lc, String resource) {
    URL res = null;
    ClassLoader cll = lc.getClassLoader(resLoader);
    if (cll != null) {
      res = cll.getResource(resource);
      if (res != null) {
        return cll.getResourceAsStream(resource);
      }
    }
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("DispatchImpl.getRes: cannot load resource with " + resLoader + "; trying to load resource : " + resource + " from the other loaders");
    }
    Enumeration loaders = lc.listLoaders();
    while (loaders.hasMoreElements()) {
      String nameLoader = (String) loaders.nextElement();
      ClassLoader loader = lc.getClassLoader(nameLoader);
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("DispatchImpl.getRes: nameLoader: " + nameLoader + " loader: " + loader);
      }
      if (loader != null) {
        res = loader.getResource(resource);
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("DispatchImpl.getRes: res : " + res);
        }
        if (res != null) {
          return loader.getResourceAsStream(resource);
        }
      }
    }
    return null;
  }

  public void getClassData(String name) throws IOException, ClassNotFoundException {
    InputStream resourceStream = null;
    String class_name = name.substring(0, name.indexOf(P4ObjectBroker.P4_DELIMITER));  // read class name
    String serverLoaderName = name.substring(name.indexOf(P4ObjectBroker.P4_DELIMITER) + P4ObjectBroker.P4_DELIMITER.length()); // read class loader name

    if (P4Logger.getLocation().beDebug()) {
      String msg = "P4Service: search for Class<" + class_name + ">";
      P4Logger.getLocation().debugT(msg);
    }

    P4ObjectOutput out = getOutputStream();
    LoadContext lc = P4ObjectBroker.init().getServiceContext().getCoreContext().getLoadContext();
    if (P4ObjectBroker.init().getClass().getName().equals(P4ObjectBroker.SERVER_BROKER_CLASS)) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("DispatchImpl.getClassData(String)", "Trying to find a classloader for class " + name);
      }
      ClassLoader cl = getClLoader(lc, class_name, serverLoaderName);
      //ClassLoader cl = getClLoader(lc, class_name);
      if (cl != null) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("DispatchImpl.getClassData(String)", "Found loader: " + cl + " trying to get the class as stream");
        }
        resourceStream = cl.getResourceAsStream(class_name.replace('.', '/') + ".class");
      } else {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("DispatchImpl.getClassData(String)", "Class <" + class_name + "> cannot be found in server process. No suitable classloader found");
        }
        throw new ClassNotFoundException("Class " + class_name + " cannot be found in server process");
      }
    } else {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("DispatchImpl.getClassData(String)", "Trying to get resource stream from current classloader");
      }
      resourceStream = this.getClass().getClassLoader().getResourceAsStream(class_name.replace('.', '/') + ".class");
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("DispatchImpl.getClassData(String)", "Resource stream is " + resourceStream);
      }
    }

    readWriteData(resourceStream);
    if (resourceStream == null) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("DispatchImpl.getClassData(String)", "Resource stream was null trying to get resource stream from " + this.getClass().getClassLoader());
      }
      resourceStream = this.getClass().getClassLoader().getResourceAsStream(class_name.replace('.', '/') + ".class");
      readWriteData(resourceStream);
      if (resourceStream == null) {

        if (P4Logger.getLocation().beDebug()) {
          String msg = "P4Service: Class <" + class_name + "> was not found in server process";
          P4Logger.getLocation().debugT(msg);
        }
        throw new ClassNotFoundException(class_name);
      }
    }

    if (P4Logger.getLocation().beDebug()) {
      String msg = "P4Service: Class <" + class_name + "> was found";
      P4Logger.getLocation().debugT(msg);
    }

  }

  private void readWriteData(InputStream resourceStream) throws IOException {
    byte[] data = null;
    if (resourceStream != null) {
      do {
        data = new byte[resourceStream.available()];
        int offset = 0;
        int readed = 0;
        while (offset < data.length) {
          readed = resourceStream.read(data, offset, data.length - offset);
          if (readed == -1) {
            out.write(data, 0, offset);
            return;
          }
          offset += readed;
        }
        out.write(data);
      } while (resourceStream.available() > 0);
    }
  }
  
  public static String remoteAddressAndPortToString(Connection repliable) {
    if (repliable == null || repliable.getProperties() == null){
      return "N/A";
    }
    return repliable.getProperties().getRemoteAddress() + ":" + repliable.getProperties().getRemotePort();
  }
}

