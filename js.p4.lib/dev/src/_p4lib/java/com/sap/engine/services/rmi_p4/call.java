package com.sap.engine.services.rmi_p4;

import com.sap.engine.frame.core.thread.ClientThreadContext;
import com.sap.engine.frame.core.thread.ContextObject;
import com.sap.engine.frame.core.thread.ContextObjectNameIterator;
import com.sap.engine.frame.core.thread.Transferable;
import com.sap.engine.frame.core.thread.TransferableExt;
import com.sap.engine.lib.lang.Convert;
import com.sap.engine.services.rmi_p4.dsr.DSRP4Instr;
import com.sap.engine.services.rmi_p4.dsr.DSRP4RequestContextImpl;
import com.sap.engine.services.rmi_p4.exception.*;
import com.sap.engine.services.rmi_p4.exception.NoSuchMethodException;
import com.sap.engine.system.ThreadWrapper;
import com.sap.engine.interfaces.cross.Connection;
import com.sap.tc.logging.Location;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * @author Georgy Stanev
 * @version 7.10
 */
public class Call extends P4Call {

  protected MarshalOutputStream out;
  private MarshalInputStream in;

  protected DataOptOutputStream dout;
  private DataOptInputStream din;
  protected ByteArrayOutput bout;

  private byte[] reply;
  protected String opname;
  private int offset;
  public StubImpl stub;
  private byte[] serializedContextObjects = null;

  private boolean isOptimizedModel = false;
  
  // used for optimization in calculate size of transferable context objects
  //because size() is slow operation especially for security context objects.
  private HashMap<String, Integer> contObjects = new HashMap<String, Integer>(5);

  // use for local Requests
  public Call(StubImpl stub, int opnum) throws IOException {
    this.stub = stub;
    this.repliable = stub.repliable;
    this.opname = stub.p4_getOperations()[opnum];
    offset = 2 + 2 * opname.length();
    out = new MarshalOutputStream(new ByteArrayOutput(offset));
  }

  // backwards compatibility
  public Call(StubBase stub, int opnum) throws IOException {
    this.stub = stub;
    this.repliable = stub.repliable;
    this.opname = stub.getOperations()[opnum].getOperation();
    offset = 2 + 2 * opname.length();
    out = new MarshalOutputStream(new ByteArrayOutput(offset));
  }

  public Call(StubImpl stub, String opname) throws IOException {
    this.stub = stub;
    this.repliable = stub.repliable;
    this.opname = opname;
    offset = 2 + 2 * opname.length();
    out = new MarshalOutputStream(new ByteArrayOutput(offset));

  }

  public Call(StubImpl stub, String opname, long call_id, long timeout) throws IOException {
    this(stub, opname, call_id, timeout, false);
  }

  public Call(StubImpl stub, int opnum, long call_id, long timeout) throws IOException {
    this(stub, stub.p4_getOperations()[opnum], call_id, timeout, false);
  }

  public Call(StubImpl stub, String opname, long call_id, long timeout, boolean isOptimized) throws IOException {
    this.timeout = timeout;
    this.stub = stub;
    this.repliable = stub.repliable;
    this.opname = opname;
    this.call_id = call_id;
    this.isOptimizedModel = isOptimized;
    int redirectableSize = 0;

    if (stub.info.isRedirectable) {
      if ((stub.info.factoryName != null) && (stub.info.objIdentity != null)) {
        redirectableSize = 2 * stub.info.factoryName.length() + getObjSize(stub.info.objIdentity) + 4;
      } else {
        redirectableSize = 2 * stub.info.redirIdent.length() + 1;
      }
    }
    if (P4ObjectBroker.getBroker().coLoadFilter.get() != null) {//Skip context objects for initial checks of context objects.
      serializedContextObjects = new byte[0];
    } else {
      serializedContextObjects = serializeCOs(stub.p4_getInfo().ownerId);
    }
    /*         8(callID) 1(msg type) 4(ThreadContextSize) 1(redirType) 2(opNameSize)    */
    offset += ProtocolHeader.HEADER_SIZE + 8 + 1 + 4 + serializedContextObjects.length + 1 + redirectableSize + 2 + 2 * opname.length() + stub.p4_getInfo().key.length;

    if (isOptimized) {
      dout = new DataOptOutputStream(new ByteArrayOutput(offset));
    } else {
      out = new MarshalOutputStream(new ByteArrayOutput(offset));
    }
    reply = null;
  }

  // backwards compatibility
  public Call(StubBase stub, int opnum, long call_id, long timeout) throws IOException {
    this(stub, (stub.getOperations()[opnum]).getOperation(), call_id, timeout, false);
  }

  public P4ObjectOutput getOutputStream() {
    return out;
  }

  public DataOptOutputStream getDataOutputStream() {
    return dout;
  }

  public boolean isOptimizedModel() {
    return this.isOptimizedModel;
  }

  public void setOptimizedModel(boolean isOptim) {
    this.isOptimizedModel = isOptim;
  }

  public DataOptInputStream getDataResultStream() throws IOException, Exception {
    try {
      if (stub.isLocal) {
        din = new DataOptInputStream(new ByteArrayInputStream(reply));
      } else {
        synchronized (this) {
          try {
            ThreadWrapper.pushSubtask("RMI call to " + P4ObjectBroker.getRemoteHost(this) + opname, ThreadWrapper.TS_WAITING_ON_IO);
            while ((replyMessage == null) && !exception) {
              try {
                wait(stub.getCallTimeout());
                if (replyMessage == null && reply == null) {
                  if (ex != null) {
                    if (P4Logger.getLocation().beDebug()) {
                      P4Logger.getLocation().debugT("Call.getResultStream()", "Call ID: " + call_id + " opname: " + opname + " <> " + P4Logger.exceptionTrace(ex));
                    }
                    throw ex;
                  }
                  if (P4Logger.getLocation().beDebug()) {
                    P4Logger.getLocation().debugT("Call.getResultStream()", "Call ID: " + call_id + " opname: " + opname + ". Request timeout, replyMessage did not arrive");
                  }
                  informDSR(new byte[0], DSRP4RequestContextImpl.INCOMING, DSRP4RequestContextImpl.TIMEOUT);
                  throw (P4IOException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_IOException, P4BaseIOException.ReplyMessage_Didnot_arrived, null);
                }
              } catch (InterruptedException iex) {
                if (P4Logger.getLocation().beDebug()) {
                  P4Logger.getLocation().debugT("Call.getResultStream()", "Call ID: " + call_id + " : opname: " + opname + ". InterruptedException while waiting Message" + " <>" + P4Logger.exceptionTrace(iex));
                }
                throw (P4IOException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Interrupted_While_Wait_Message, iex);
              }
            }
          }finally {
            ThreadWrapper.popSubtask();
          }
        }

        if (replyMessage != null) {
          replyMessage.setInfo(stub.p4_getInfo());
          din = new DataOptInputStream(replyMessage.getByteArrayInputStream());
          din.setUnderlyingProfile(stub.repliable.getUnderlyingProfile());
          ContextObject co = replyMessage.p4co;
          if (co != null) {
            byte[] temp = new byte[((Transferable) co).size()];
            ((Transferable) co).store(temp, 0);
            int cluster_id = Convert.byteArrToInt(temp, temp.length - 4);
            byte[] oKey = new byte[temp.length - 4];
            System.arraycopy(temp, 0, oKey, 0, temp.length - 4);
            stub.p4_setNewServerInfo(oKey, cluster_id);
          }
        } else {
          if (reply != null) {
            din = new DataOptInputStream(new ByteArrayInputStream(reply));
          } else {
            if (ex != null) {
              if (P4Logger.getLocation().beDebug()) {
                P4Logger.getLocation().debugT("Call.getResultStream()", "Call ID: " + call_id + " opname: " + opname + " <> " + P4Logger.exceptionTrace(ex));
              }
              throw ex;
            } else {
              if (P4Logger.getLocation().beDebug()) {
                P4Logger.getLocation().debugT("Call.getResultStream()", "Call ID: " + call_id + " opname: " + opname + ". Reply Message did not arrive");
              }
              informDSR(new byte[0], DSRP4RequestContextImpl.INCOMING, DSRP4RequestContextImpl.TIMEOUT);
              throw (P4IOException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_IOException, P4BaseIOException.ReplyMessage_Didnot_arrived, null);
            }
          }
        }
      }
      din.setConnectionType(stub.p4_getConnectionType());
      din.setClassLoader(stub.p4_getClassLoader());

      if (exception) {//todo think about it!
        if (replyMessage != null) {
          in = new MarshalInputStream(replyMessage.getByteArrayInputStream());
        } else if (reply != null) {
          in = new MarshalInputStream(new ByteArrayInputStream(reply));
        }
        in.setConnectionType(stub.p4_getConnectionType());
        in.setClassLoader(stub.p4_getClassLoader());
        Exception ex = null;
        try {
          ex = (Exception) in.readObject();
        } catch (Exception exe) {
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("Call.getResultStream()", "Call ID: " + call_id + " : opname:" + opname + ". Exception is thrown in execution process. Exception occurs when try to read original Exception <> " + P4Logger.exceptionTrace(exe));
          }
          throw (MarshalException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_MarshalException, P4BaseMarshalException.Exception_in_Execution_Process, exe);
        }
        throw ex;
      }
      din.setRemoteBrokerId(this.stub.info.ownerId);
      informDSR(replyMessage.request, DSRP4RequestContextImpl.INCOMING, DSRP4RequestContextImpl.REPLY);
      return din;
    } finally {
      removeCall(call_id);
    }
  }

  public P4ObjectInput getResultStream() throws IOException, Exception {
    try {
      if (stub.isLocal) {
        in = new MarshalInputStream(new ByteArrayInputStream(reply));
      } else {
        synchronized (this) {
          try {
            ThreadWrapper.pushSubtask("RMI call to " + P4ObjectBroker.getRemoteHost(this) + opname, ThreadWrapper.TS_WAITING_ON_IO);
            while ((replyMessage == null) && !exception) {
              try {
                wait(stub.getCallTimeout());
                if (replyMessage == null && reply == null) {
                  if (ex != null) {
                    if (P4Logger.getLocation().beDebug()) {
                      P4Logger.getLocation().debugT("Call.getResultStream()", "Call ID: " + call_id + " opname: " + opname + " <> " + P4Logger.exceptionTrace(ex));
                    }
                    throw ex;
                  }
                  if (P4Logger.getLocation().beDebug()) {
                    P4Logger.getLocation().debugT("Call.getResultStream()", "Call ID: " + call_id + " opname: " + opname + ". Request timeout, replyMessage did not arrive");
                  }
                  informDSR(new byte[0], DSRP4RequestContextImpl.INCOMING, DSRP4RequestContextImpl.TIMEOUT);
                  throw (P4IOException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_IOException, P4BaseIOException.ReplyMessage_Didnot_arrived, null);
                }
              } catch (InterruptedException iex) {
                if (P4Logger.getLocation().beDebug()) {
                  P4Logger.getLocation().debugT("Call.getResultStream()", "Call ID: " + call_id + " opname: " + opname + ". InterruptedException while waiting Message <> " + P4Logger.exceptionTrace(iex));
                }
                throw (P4IOException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Interrupted_While_Wait_Message, iex);
              }
            }
          }finally {
            ThreadWrapper.popSubtask();
          }
        }

        if (replyMessage != null) {
          replyMessage.setInfo(stub.p4_getInfo());
          in = new MarshalInputStream(replyMessage.getByteArrayInputStream());
          in.setUnderlyingProfile(stub.repliable.getUnderlyingProfile());
          ContextObject co = replyMessage.p4co;
          if (co != null) {
            byte[] temp = new byte[((Transferable) co).size()];
            ((Transferable) co).store(temp, 0);
            int cluster_id = Convert.byteArrToInt(temp, temp.length - 4);
            byte[] oKey = new byte[temp.length - 4];
            System.arraycopy(temp, 0, oKey, 0, temp.length - 4);
            stub.p4_setNewServerInfo(oKey, cluster_id);
          }
        } else {
          if (reply != null) {
            in = new MarshalInputStream(new ByteArrayInputStream(reply));
          } else {
            if (ex != null) {
              if (P4Logger.getLocation().beDebug()) {
                P4Logger.getLocation().debugT("Call.getResultStream()", "Call ID: " + call_id + " opname: " + opname + " <> " + P4Logger.exceptionTrace(ex));
              }
              throw ex;
            } else {
              if (P4Logger.getLocation().beDebug()) {
                P4Logger.getLocation().debugT("Call.getResultStream()", "Call ID: " + call_id + " opname: " + opname + ". Reply Message did not arrive");
              }
              informDSR(new byte[0], DSRP4RequestContextImpl.INCOMING, DSRP4RequestContextImpl.TIMEOUT);
              throw (P4IOException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_IOException, P4BaseIOException.ReplyMessage_Didnot_arrived, null);
            }
          }
        }
      }
      in.setConnectionType(stub.p4_getConnectionType());
      in.setClassLoader(stub.p4_getClassLoader());

      if (exception) {
        Exception ex = null;
        try {
          ex = (Exception) in.readObject();
        } catch (Exception exe) {
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("Call.getResultStream()", "Call ID: " + call_id + ": opname: " + opname + ". Exception is thrown in execution process. Exception occur when try to read original Exception <> " + P4Logger.exceptionTrace(exe));
          }
          throw (MarshalException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_MarshalException, P4BaseMarshalException.Exception_in_Execution_Process, exe);
        }
        if ((ex instanceof NoSuchMethodException) || (ex.getClass().equals(Exception.class) && ex.getMessage().startsWith("Incorrect Operation :"))) {
          throw new NoSuchOperationException("Remote skeleton does not support operation " + opname, ex);
        } else {
//          if ( java.lang.RuntimeException.class.isAssignableFrom(ex.getClass()) ) {
//            Exception e = ex.getClass().newInstance();
//            e.initCause(ex);
//            throw e;
//          }
//          ex.setStackTrace(new Exception(e).getStackTrace());
          throw ex;
        }
      }
      in.setRemoteBrokerId(this.stub.info.ownerId);
      informDSR(replyMessage.request, DSRP4RequestContextImpl.INCOMING, DSRP4RequestContextImpl.REPLY);
      return in;
    } finally {
      removeCall(call_id);
    }
  }

  public StubImpl getStub() {
    return stub;
  }

  public static Call newCall(StubImpl stubImpl, String opname) throws IOException {
    Call call = null;
    if (stubImpl.isLocal) {
      if (stubImpl.repliable == null) {
        stubImpl.p4_setConnection(new LocalDispatch(stubImpl.broker.getObject(stubImpl.info.key)));
      }
      call = new Call(stubImpl, opname);
    } else {
      call = new Call(stubImpl, opname, getNewId(), 0);
      calls.put(call.call_id, call);
    }
    return call;
  }

  // backwards compatibility
  public static Call newCall(StubBase stubBase, String opname) throws IOException {
    Call call = null;
    try {
      if (stubBase.isLocal) {
        if (stubBase.repliable == null) {
          stubBase.setConnection(new LocalDispatch(stubBase.broker.getObject(stubBase.info.key)));
        }
        call = new Call(stubBase, opname);
      } else {
        call = new Call(stubBase, opname, getNewId(), 0);
        calls.put(call.call_id, call);
      }
    } catch (Exception e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("Call.newCall(StubBase, String)", P4Logger.exceptionTrace(e));
      }
      throw new IOException(e.getMessage());
    }
    return call;
  }

  public static Call newCall(StubImpl stubImpl, int opnum) throws IOException {
    Call call;
    if (stubImpl.isLocal) {
      if (stubImpl.repliable == null) {
        stubImpl.p4_setConnection(new LocalDispatch(stubImpl.broker.getObject(stubImpl.info.key)));
      }
      call = new Call(stubImpl, opnum);
    } else {
      call = new Call(stubImpl, opnum, getNewId(), 0);
      calls.put(call.call_id, call);
    }
    return call;
  }

  public static Call newCall(StubImpl stubImpl, String opname, boolean isOptimized) throws IOException {
    Call call;
    if (stubImpl.isLocal) {
      if (stubImpl.repliable == null) {
        stubImpl.p4_setConnection(new LocalDispatch(stubImpl.broker.getObject(stubImpl.info.key)));
      }
      call = new Call(stubImpl, opname);
    } else {
      call = new Call(stubImpl, opname, getNewId(), 0, isOptimized);
      calls.put(call.call_id, call);
    }
    return call;
  }


  public static Call newCall(StubImpl stubImpl, int opnum, boolean isOptimized) throws IOException {
    Call call;
    if (stubImpl.isLocal) {
      if (stubImpl.repliable == null) {
        stubImpl.p4_setConnection(new LocalDispatch(stubImpl.broker.getObject(stubImpl.info.key)));
      }
      call = new Call(stubImpl, opnum);
    } else {
      call = new Call(stubImpl, stubImpl.p4_getOperations()[opnum], getNewId(), 0, isOptimized);
      calls.put(call.call_id, call);
    }
    return call;
  }

  // backwards compatibility
  public static Call newCall(StubBase stubBase, int opnum) throws IOException {
    Call call;
    if (stubBase.isLocal) {
      if (stubBase.repliable == null) {
        stubBase.setConnection(new LocalDispatch(stubBase.broker.getObject(stubBase.info.key)));
      }
      call = new Call(stubBase, opnum);
    } else {
      call = new Call(stubBase, opnum, getNewId(), 0);
      calls.put(call.call_id, call);
    }
    return call;
  }

  private int calculateSize(int ownerId){
    ClientThreadContext ctc = P4ObjectBroker.getBroker().getCTC();
    //Optimization for JMS Scenario, when they use system threads - no context objects
    if (ctc == null){
      return 0;
    }
    int size = 0;
    contObjects.clear();
    try {
      ContextObjectNameIterator coIt = ctc.getTransferableContextObjectNames();
      while (coIt.hasNext()) {
        String co_name = coIt.nextName();
        /* skip forbidden context objects */
        if (forbidden(repliable, co_name)){
          continue;
        }
        ContextObject co = ctc.getContextObject(co_name);
        int currentCoLength = 0;

        if ((P4ObjectBroker.getBroker().brokerId != ownerId) && (co instanceof TransferableExt)) {//getting size
          currentCoLength = ((TransferableExt) co).size(ownerId);
        } else {
          currentCoLength = ((Transferable) co).size();
        }
        contObjects.put(co_name, currentCoLength);
        //Skip these context objects with size zero
        if (currentCoLength > 0){
          size += 2 + co_name.length() + 2 + currentCoLength;
        }
      }  //while

      size += 2;
    } catch (Throwable ex){
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("Call.calculateSize(int brokerId)", P4Logger.exceptionTrace(ex));
      }
      return 0;
    }
    return size;

  }

  private byte[] serializeCOs(int ownerId){
    int length = calculateSize(ownerId);
    if (length == 0) {
      return new byte[0];
    }

    byte[] collectedCOs = new byte[length];
    ClientThreadContext ctc = P4ObjectBroker.getBroker().getCTC();
    int current_position = 2;
    int contextObjectCount = 0;

    try{
      ContextObjectNameIterator coIt = ctc.getTransferableContextObjectNames();

      /* context objects' count iteration */
      while (coIt.hasNext()) {
        String co_name = coIt.nextName();
        /* skip forbidden context objects */
        if (forbidden(repliable, co_name)) {
          continue;
        }

        int currentCoLength = 0;
        ContextObject co = ctc.getContextObject(co_name);
        /* getting size */
        currentCoLength = contObjects.get(co_name);
        //if ((P4ObjectBroker.getBroker().brokerId != ownerId) && (co instanceof TransferableExt)) {
        //  currentCoLength = ((TransferableExt) co).size(ownerId);
        //} else {
        //  currentCoLength = ((Transferable) co).size();
        //}
        
        //Skip these context objects with size zero
        if (currentCoLength > 0) {
          writeIntToShort(collectedCOs, current_position, co_name.length());    //write coNameLength 2 bytes
          current_position += 2;
  
          writeString(collectedCOs, current_position, co_name);                 //write coName
          current_position += co_name.length();
  
          writeIntToShort(collectedCOs, current_position, currentCoLength);     //write coLegnth
          current_position += 2;
  
          /* storing co in the request */
          if ((P4ObjectBroker.getBroker().brokerId != ownerId) && (co instanceof TransferableExt)) {
            /* store security co by connection - it is in this client side */
            ((TransferableExt) co).store(ownerId, collectedCOs, current_position);
          } else {
            ((Transferable) co).store(collectedCOs, current_position);
          }
          current_position += currentCoLength;
  
          contextObjectCount++;  //count just added co 
        }
      }
      writeIntToShort(collectedCOs, 0, contextObjectCount); //Write the count of COs

    } catch (Throwable ex) {
      if (P4Logger.getLocation().beWarning() || P4Logger.getSecLocation().beWarning()) {
        Location loc;
        if (P4Logger.getSecLocation().beWarning()) {
          loc = P4Logger.getSecLocation();
        } else {
          loc = P4Logger.getLocation();
        }
        P4Logger.trace(P4Logger.WARNING, loc, "Call.serializeCOs(int brokerId)", "Failed to serialize context objects. RMI-P4 message will be sent with no context objects; RMI-P4 client will not work properly Exception: {0}", "ASJ.rmip4.rt2000", new Object []{P4Logger.exceptionTrace(ex)});
      }
      return new byte[0];
    }
    return collectedCOs;
  }

  /* (non java documentation)
   * This method checks if context object is in forbidden list for this connection.
   * NOTE: Server should not send unknown context object to another server. 
   * But for server-client connection, client will not be able to check COs.
   * 
   * @param repliable The connection that we search for 
   * @param co_name The name of context object that we check
   * @return true if the context object is forbidden; 
   *         false if the context object is allowed
   */
  private boolean forbidden(Connection repliable, String co_name) {
    P4ObjectBroker broker = P4ObjectBroker.getBroker();
    //If remote side has no class for such CO - return true;
    if (broker != null && broker.getForbiden(repliable) != null && broker.getForbiden(repliable).contains(co_name)) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("Call.forbidden()", "Context object: " + co_name +  " was filtered for connection " + repliable);
      }
      return true;
    }
    //Otherwise return false;
    return false;
  }

  public void sendSimpleRequest() throws Exception {
    int server_id = stub.p4_getInfo().server_id;
    int client_id = stub.p4_getInfo().client_id;
    int operationLength = opname.length();
    int opnameLength = 2 * operationLength;
    byte[] _key = stub.p4_getInfo().key;
    if (P4Logger.getLocation().beInfo()) {
      if (stub.broker.isServerBroker()) {
        P4Logger.getLocation().infoT("Call.sendSimpleRequest()", stub.getClass() + "@" + Integer.toHexString(System.identityHashCode(stub)) + ": Calling remote method: " + opname + " call id: " + call_id + " client_id: " + client_id + " server id: " + server_id);
      } else {
        P4Logger.getLocation().infoT("Call.sendSimpleRequest()", stub.getClass() + "@" + Integer.toHexString(System.identityHashCode(stub)) + ": Calling remote method: " + opname + " call id: " + call_id + " connection: " + stub.repliable + " server id: " + server_id);
      }
    }

    try {
      dout.flush();
    } catch (IOException ioex) {
      if (stub != null) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("Call.sendSimpleRequest()", "Call ID: " + call_id + ": opname: " + opname + " <> " + P4Logger.exceptionTrace(ioex));
        }
      }
    }

    byte[] request = dout.getBuffer();
    int size = dout.getSize();
    int nextPos = ProtocolHeader.END_PROTOCOL;
    Convert.writeLongToByteArr(request, ProtocolHeader.CALL_ID, call_id);
    request[ProtocolHeader.MESSAGE_TYPE] = 0;
    Convert.writeIntToByteArr(request, ProtocolHeader.THREAD_CONTEXT_SIZE, serializedContextObjects.length);
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("Serialized context objects size is "+ serializedContextObjects.length + " (0x" + Integer.toHexString(serializedContextObjects.length) + ")");
    }
    if (serializedContextObjects.length != 0) {      //write context objects to in the request
      System.arraycopy(serializedContextObjects, 0, request, nextPos, serializedContextObjects.length);
      nextPos += serializedContextObjects.length;
    }

    if (stub.info.isRedirectable) {
      if (stub.info.factoryName != null && stub.info.objIdentity != null) {

        request[nextPos++] = 2;//extended redirectable
        request[nextPos++] = (byte) stub.info.factoryName.length();//factory name
        request[nextPos++] = (byte) (stub.info.factoryName.length() >> 8);
        Convert.writeUStringToByteArr(request, nextPos, stub.info.factoryName);
        nextPos += 2 * stub.info.factoryName.length();

        byte[] serialized = objToBytes(stub.info.objIdentity);
        request[nextPos++] = (byte) serialized.length;
        request[nextPos++] = (byte) (serialized.length >> 8);
        System.arraycopy(serialized, 0, request, nextPos, serialized.length);
        nextPos += serialized.length;

      } else {
        request[nextPos++] = 1;
        request[nextPos++] = (byte) stub.info.redirIdent.length(); // name length
        Convert.writeUStringToByteArr(request, nextPos, stub.info.redirIdent);
        nextPos += 2 * stub.info.redirIdent.length();
      }
    } else {
      request[nextPos++] = 0;
    }

    request[nextPos++] = (byte) operationLength; // name length
    request[nextPos++] = (byte) (operationLength >> 8);
    Convert.writeUStringToByteArr(request, nextPos, opname);
    nextPos += opnameLength;
    System.arraycopy(_key, 0, request, nextPos, _key.length);
    nextPos += _key.length;
    ProtocolHeader.writeHeader(request, 0, size, server_id);
    //logged, after optimized stream was got via method getDataResultStream
    informDSR(request, DSRP4RequestContextImpl.OUTGOING, DSRP4RequestContextImpl.REQUEST);
    repliable.sendRequest(request, size, this);
  }

  public void sendRequest() throws IOException {
    int server_id = stub.p4_getInfo().server_id;
    int client_id = stub.p4_getInfo().client_id;
    int operationLength = opname.length();
    int opnameLength = 2 * operationLength;
    byte[] _key = stub.p4_getInfo().key;
    if (P4Logger.getLocation().beInfo()) {
      if (stub.broker.isServerBroker()) {
        P4Logger.getLocation().infoT("Call.sendRequest()", stub.getClass() + "@" + Integer.toHexString(System.identityHashCode(stub)) + ": Calling remote method: " + opname + " call id: " + call_id + " client_id: " + client_id + " server id: " + server_id);
      } else {
        P4Logger.getLocation().infoT("Call.sendRequest()", stub.getClass() + "@" + Integer.toHexString(System.identityHashCode(stub)) + ": Calling remote method: " + opname + " call id: " + call_id + " connection: " + stub.repliable + " server id: " + server_id);
      }
    }
    try {
      out.flush();
    } catch (IOException ioex) {
      if (stub != null) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("Call.sendRequest()", "Call ID: " + call_id + ": opname: " + opname + " <> " + P4Logger.exceptionTrace(ioex));
        }
      }
    }

    byte[] request = out.getBuffer();
    int size = out.getSize();
    int nextPos = ProtocolHeader.END_PROTOCOL;
    Convert.writeLongToByteArr(request, ProtocolHeader.CALL_ID, call_id);
    request[ProtocolHeader.MESSAGE_TYPE] = 0;
    Convert.writeIntToByteArr(request, ProtocolHeader.THREAD_CONTEXT_SIZE, serializedContextObjects.length);
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("Call.sendRequest()", "Serialized context objects size is "+ serializedContextObjects.length + " (0x" + Integer.toHexString(serializedContextObjects.length) + ")");
    }
    if (serializedContextObjects.length != 0) {      //write context objects to in the request
      System.arraycopy(serializedContextObjects, 0, request, nextPos, serializedContextObjects.length);
      nextPos += serializedContextObjects.length;
    }

    if (stub.info.isRedirectable) {
      if (stub.info.factoryName != null && stub.info.objIdentity != null) {

        request[nextPos++] = 2;//extended redirectable
        request[nextPos++] = (byte) stub.info.factoryName.length();//factory name
        request[nextPos++] = (byte) (stub.info.factoryName.length() >> 8);
        Convert.writeUStringToByteArr(request, nextPos, stub.info.factoryName);
        nextPos += 2 * stub.info.factoryName.length();

        byte[] serialized = objToBytes(stub.info.objIdentity);
        request[nextPos++] = (byte) serialized.length;
        request[nextPos++] = (byte) (serialized.length >> 8);
        System.arraycopy(serialized, 0, request, nextPos, serialized.length);
        nextPos += serialized.length;

      } else {
        request[nextPos++] = 1;
        request[nextPos++] = (byte) stub.info.redirIdent.length(); // name length
        Convert.writeUStringToByteArr(request, nextPos, stub.info.redirIdent);
        nextPos += 2 * stub.info.redirIdent.length();
      }
    } else {
      request[nextPos++] = 0;
    }

    request[nextPos++] = (byte) operationLength; // name length
    request[nextPos++] = (byte) (operationLength >> 8);
    Convert.writeUStringToByteArr(request, nextPos, opname);
    nextPos += opnameLength;
    System.arraycopy(_key, 0, request, nextPos, _key.length);
    nextPos += _key.length;
    nextPos += _key.length;
    ProtocolHeader.writeHeader(request, 0, size, server_id);
    informDSR(request, DSRP4RequestContextImpl.OUTGOING, DSRP4RequestContextImpl.REQUEST); //11 - outgoing, 0 - request
    repliable.sendRequest(request, size, this);
  }

  private byte[] objToBytes(Serializable obj) {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    try {
      MarshalOutputStream out = new MarshalOutputStream(bout);
      out.writeObject(obj);
      return bout.toByteArray();
    } catch (IOException e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("Call.objToBytes()", "Cannot serialize the object: " + obj);
        P4Logger.getLocation().debugT(P4Logger.exceptionTrace(e));
      }
    }
    return null;
  }

  private int getObjSize(Serializable obj) {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    try {
      MarshalOutputStream out = new MarshalOutputStream(bout);
      out.writeObject(obj);
      return bout.toByteArray().length;
    } catch (IOException e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("Call.getObjSize()", "Cannot serialize the object: " + obj);
        P4Logger.getLocation().debugT(P4Logger.exceptionTrace(e));
      }
    }
    return 0;
  }

  public void sendLocalRequest() throws IOException {
    try {
      out.flush();
    } catch (IOException ioex) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("Call.sendLocalRequest()", "Call ID: " + call_id + " opname:" + opname + " <> " + P4Logger.exceptionTrace(ioex));
      }
    }
    byte[] request = out.getBuffer();
    int operationLength = opname.length();
    request[0] = (byte) (operationLength); // name length
    request[1] = (byte) ((operationLength >> 8));
    Convert.writeUStringToByteArr(request, 2, opname);
    informDSR(request, DSRP4RequestContextImpl.OUTGOING, DSRP4RequestContextImpl.REQUEST);
    stub.repliable.sendRequest(request, out.getSize(), this);
  }

  public void releaseInputStream() {  
  }

  public void setReply(byte[] reply) {
    this.reply = reply;
    synchronized (this) {
      notify();
    }
    informDSR(reply, DSRP4RequestContextImpl.INCOMING, DSRP4RequestContextImpl.REPLY);//10 - Incoming, 1 - reply
  }
  
  /**
   * This method informs DSR for end of incoming call in case of enabled monitoring via DSR. 
   * @param reply This is the received message as byte array.
   */
  public void informDSR(byte[] reply, int direction, int type){
    //In case of Exception is invoked method in parent P4Call.setException(Message)
    if (P4ObjectBroker.isDSRMonitorable()) {
      DSRP4RequestContextImpl requestContext = new DSRP4RequestContextImpl();
      requestContext.setServerID(stub.p4_getInfo().server_id);
      requestContext.setClientID(stub.p4_getInfo().client_id);
      requestContext.setStubClass(stub.getClass().getName());
      requestContext.setOperation(opname);
      requestContext.setRedirectable(stub.info.isRedirectable);
      requestContext.setConnectionID(repliable.getId());
      requestContext.setBytes(reply.length);
      requestContext.setCallID(call_id);
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
      } else {
        //Reply, Error-reply, Timeout
        DSRP4Instr.requestEnd(requestContext);
      }
      if (P4ObjectBroker.isEnabledAccounting() && P4ObjectBroker.getBroker() != null && P4ObjectBroker.getBroker().isServerBroker()) {
        P4ObjectBroker.getBroker().endMeasure("P4/DSR_Monitoring");
      }
    }
  }

  public void writeIntToShort(byte[] buf, int off, int d) {
    com.sap.engine.lib.lang.Convert.writeShortToByteArr(buf, off, (short) d);
  }

  public void writeString(byte[] buf, int off, String str) {
    com.sap.engine.lib.lang.Convert.writeAStringToByteArr(buf, off, str);
  }

  public boolean isFrom(Connection repliable) {
    return repliable.equals(this.stub.repliable);
  }

  public boolean isToConnection(int connectionId) {
    return stub.info.client_id == connectionId;
  }

  public boolean isToClusterElement(int elementId) {
    return stub.broker.brokerId == stub.info.ownerId && Math.abs(stub.info.local_id) == elementId;
  }

  public String toString() {
    StringBuffer callString = new StringBuffer("Call ID: ");
    callString.append(call_id);
    callString.append(" request for operation: "); 
    callString.append(opname);
   if (repliable != null) {
     if (repliable instanceof ClientConnection) {
       callString.append(" to ").append(((ClientConnection)repliable).connectionId);
     } else if (stub.getObjectInfo().getOwnerId() == P4ObjectBroker.getBroker().brokerId) {     //TODO NPE possible if broker is closed
       callString.append(" to server node ").append(stub.getObjectInfo().server_id);
     } else {
       callString.append(" to " + repliable.getUnderlyingProfile());
     }
   }
   return callString.toString();
  }
  
  /**
   * Return the server id from the stub - it can be from other cluster, 
   * if the stub has repliable object with connection properties. 
   */
  public int getDestServerId(){
    return stub.getObjectInfo().getServer_id();
  }
}

