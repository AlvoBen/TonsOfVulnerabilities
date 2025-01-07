/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rmi_p4;

import com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.lib.lang.Convert;
import com.sap.engine.interfaces.cross.Connection;
import com.sap.engine.interfaces.cross.StreamHook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Copyright: Copyright (c) 2003
 * Company: SAP AG - Sap Labs Bulgaria
 * <p/>
 * The stub base used by the broker to initialize the client
 * proxy.THe broker sets the info and repliable fields.
 * They are used by the proxy then new remote call is created
 *
 * @author Georgy Stanev
 * @version 7.0
 */
public abstract class StubImpl implements RemoteRef, Serializable {

  static final long serialVersionUID = -7269201963209714255L;
  public static final String GET_CLASS_DATA = "p4_getClassData";
  public static final String GET_RESOURCE_DATA = "p4_getResourceData";
  /**
   * information for the remote object
   */
  public StubBaseInfo info;
  /**
   * transporter to the implementation
   */
  public Connection repliable;
  public String connectionType;
  public P4ObjectBroker broker = P4ObjectBroker.getBroker();
  public boolean isLocal = false;
  public P4RemoteObject p4remote = null;
  protected ClassLoader clLoader = null;
  private volatile transient long timeout = P4Call.TIMEOUT;

  public StubImpl() {
    this.clLoader = this.getClass().getClassLoader();
  }

  /**
   * returns the StubBaseInfo for this object
   *
   * @return info field of the class
   */
  public StubBaseInfo p4_getInfo() {
    return this.info;
  }

  // implemented from RemoteRef
  public RemoteObjectInfo getObjectInfo() {
    return this.info;
  }

  public RemoteObjectInfo p4_getObjectInfo() {
    return this.getObjectInfo();
  }

  public String p4_getConnectionType() {
    return this.connectionType;
  }

  public void p4_setConnectionType(String _connectionType) {
    this.connectionType = _connectionType;
  }

  public String p4_getUnderlyingProfile() {
     return (repliable != null) ? repliable.getUnderlyingProfile() : null;
  }
  /**
   * This method is overridden by the stubs
   * Returns all operations available for the client.
   * Function is used when proxy fills the remote request
   * header
   *
   * @return array with operations
   */
  public String[] p4_getOperations() {
    return null;
  }

  /**
   * Creates new remote call on the specified by index
   * method.
   *
   * @param opnum index of the method
   * @return created call
   * @throws IOException
   */
  public Call p4_newCall(int opnum) throws Exception {
    if (repliable == null) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("StubImpl.newCall(int)", "Operation with operation number: " + opnum + " was called on disconnected stub: " + this);
      }
      throw new P4ConnectionException("Stub is not connected");
    }
    return Call.newCall(this, opnum);
  }

  public Call p4_newCall(String opname) throws Exception {
    if (repliable == null) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("StubImpl.newCall(String)", "Operation: " + opname + " was called on disconnected stub: " + this);
      }
      throw new P4ConnectionException("Stub is not connected");
    }
    return Call.newCall(this, opname);
  }


  /**
   * Creates new remote call on the specified by index method.
   *
   * @param opnum index of the method
   * @return created call
   * @throws IOException
   */
  public Call p4_newCall(int opnum, boolean isOptimized) throws Exception {
    if (repliable == null) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("StubImpl.newCall(int, isOptimized)", "Operation with operation number: " + opnum + " was called on disconnected stub: " + this);
      }
      throw new P4ConnectionException("Stub is not connected");
    }
    return Call.newCall(this, opnum, isOptimized);
  }

  public Call p4_newCall(String opname, boolean isOptimized) throws Exception {
    if (repliable == null) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("StubImpl.newCall(String, isOptimized)", "Operation with opname: " + opname + " was called on disconnected stub: " + this);
      }
      throw new P4ConnectionException("Stub is not connected");
    }
    return Call.newCall(this, opname, isOptimized);
  }

  /**
   * Invokes a call witch was created before that
   * with void newCall(int).
   *
   * @param call the call to invoke
   * @throws IOException
   */
  public void p4_invoke(Call call) throws Exception {
    try {
      if (isLocal) {
        call.sendLocalRequest();
      } else if(call.isOptimizedModel()){
        call.sendSimpleRequest();
      } else {
        call.sendRequest();
      }
    } catch (P4IOException ioe) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("StubImpl.p4_invoke(Call)",P4Logger.exceptionTrace(ioe));
      }

      if (ioe.getMessage().endsWith("Ilegal client ID.")) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("StubImpl.p4_invoke(Call)", "Illegal client ID:" + call.stub.p4_getInfo().client_id);
        }
        Call.removeCall(call.call_id);
        throw (P4ConnectionException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_ConnectionException, P4ConnectionException.Ilegal_client_ID, ioe);
      }
      try {
        if (info.checkConnectionType(connectionType) < 2) {
          if (P4Logger.getLocation().beInfo()) {
            P4Logger.getLocation().infoT("StubImpl.p4_invoke(Call)", "The Stub could not establish connection");
          }
          Call.removeCall(call.call_id);
          throw P4ObjectBroker.init().getException(P4ObjectBroker.P4_ConnectionException, P4ConnectionException.Stub_couldnot_Establish_connection, null);
        }

        P4ObjectBroker.init().makeConnection(connectionType, this);
        if (isLocal) { //Keep status if connection was optimized
          call.sendLocalRequest();
        } else if(call.isOptimizedModel()){
          call.sendSimpleRequest();
        } else {
          call.sendRequest();
        }
      } catch (Exception ex) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("StubImpl.p4_invoke(Call)",P4Logger.exceptionTrace(ex));
        }
        Call.removeCall(call.call_id);
        throw (P4ConnectionException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_ConnectionException, P4ConnectionException.Ilegal_client_ID, ex);
      }
    } catch (Exception ex) {
      Call.removeCall(call.call_id);
      throw ex;
    }
  }

  /**
   * Removes a call.It is assumed that call was created and
   * invoked before that.
   *
   * @param call the call to remove
   */
  public void p4_done(Call call) {
    if (call != null) {
      Call.removeCall(call.call_id);
    }
  }

  /**
   * method used by the broker to set an object for the proxy
   * for transport level arrangement
   *
   * @param repliable the object used for requesting and
   */
  public void p4_setConnection(Connection repliable) {
    this.repliable = repliable;
    if (P4Logger.getLocation().bePath()) {
      P4Logger.getLocation().pathT("StubImpl.p4_setConnection(Connection)", this + " repliable set to: " + repliable);
    }
    if (!info.connected) {
      try {
        byte[] inf = p4_makeInformMessage((byte) 0);
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("StubImpl.p4_setConnection(Connection)", this + " Sending INFORM message");
        }
        Convert.writeIntToByteArr(inf, 6, info.server_id);
        Convert.writeIntToByteArr(inf, 10, broker.id);
        Convert.writeIntToByteArr(inf, 2, inf.length);
        ProtocolHeader.writeHeader(inf, 0, inf.length, info.server_id);
        repliable.sendRequest(inf, inf.length, null);        
        info.connected = true;
      } catch (Exception ioex) {
        if (P4Logger.getLocation().beInfo()) {
          P4Logger.getLocation().infoT("StubImpl.p4_setConnection(Connection)", "Can't send inform message. " + ioex.getMessage());
        }
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("StubImpl.p4_setConnection(Connection))", P4Logger.exceptionTrace(ioex));
        }
      }
    }
  }

  public ClassLoader p4_getClassLoader() {
    return clLoader;
  }

  public void p4_setClassLoader(ClassLoader clLoader) {
    if (clLoader != null) {
      this.clLoader = clLoader;
    }
  }

  /**
   * method used by the broker to set the remote object
   * information
   *
   * @param info the information
   */
  public void p4_setInfo(StubBaseInfo info) {
    this.info = info;
  }

  protected byte[] p4_makeInformMessage(byte finalize) {
    //messageSize   = long + byte + byte[] + byte
    //--------------call_id--type----key----finalize-----
    int keyLength = info.key.length;
    byte[] informMessage = new byte[ProtocolHeader.HEADER_SIZE + 10 + keyLength];
    //set call_id
    informMessage[ProtocolHeader.HEADER_SIZE + 0] = -2;
    informMessage[ProtocolHeader.HEADER_SIZE + 1] = 0;
    informMessage[ProtocolHeader.HEADER_SIZE + 2] = 0;
    informMessage[ProtocolHeader.HEADER_SIZE + 3] = 0;
    informMessage[ProtocolHeader.HEADER_SIZE + 4] = 0;
    informMessage[ProtocolHeader.HEADER_SIZE + 5] = 0;
    informMessage[ProtocolHeader.HEADER_SIZE + 6] = 0;
    informMessage[ProtocolHeader.HEADER_SIZE + 7] = 0;
    //set message type
    informMessage[ProtocolHeader.HEADER_SIZE + 8] = Message.INFORM_MESSAGE;
    informMessage[ProtocolHeader.HEADER_SIZE + 9] = finalize;
    System.arraycopy(info.key, 0, informMessage, ProtocolHeader.HEADER_SIZE + 10, keyLength);
    return informMessage;
  }

  public void finalize() { //$JL-FINALIZE$
    if (info.connected && !isLocal) {
      byte[] inf = p4_makeInformMessage((byte) 1);
      if (P4Logger.getLocation().bePath()) {
        P4Logger.getLocation().pathT("StubImpl.p4_finalize()", "Finalyzing " + this +": sending INFORM message");
      }
      ProtocolHeader.writeHeader(inf, 0, inf.length, info.server_id);
      broker.getFinalizeInformer().setWork(repliable, inf, this.toString());
    }
  }

  public Object p4_replicate(Object obj) {
    return p4_initializeStreams(obj);
  }

  protected Object p4_initializeStreams(Object obj) {
    try {
      ByteArrayOutput bao = new ByteArrayOutput(0);
      ReplicateOutputStream out = new ReplicateOutputStream(bao);
      out.writeObject(obj);
      ReplicateInputStream in = new ReplicateInputStream(new ByteArrayInput(bao.getBuffer()), out);
      in.setClassLoader(this.getClass().getClassLoader());
      return in.readObject();
    } catch (ClassNotFoundException cnfe){
      if (P4Logger.getLocation().beWarning()) {
        P4Logger.trace(P4Logger.WARNING, "StubImpl.p4_initializeStreams(Object)", "Exception during replicating parameters or returning value for method of local stub. Check context class loader: \r\n{0}", "ASJ.rmip4.rt2028", new Object []{P4Logger.exceptionTrace(cnfe)});
      }
      throw new P4RuntimeException("Cannot replicate parameters or return value of local stub", cnfe);
    } catch (Exception e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("StubImpl.p4_initializeStreams(Object)", "Exception during replicate parameters or return value for method of local stub \r\n" + P4Logger.exceptionTrace(e));
      }
      throw (P4RuntimeException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, P4BaseRuntimeException.Couldnt_initialize_stream, e, new Object[]{e.toString()});
    }
  }

  protected Object p4_replicateParameters(Object obj) {
    try {
      ByteArrayOutput bao = new ByteArrayOutput(0);
      ReplicateOutputStream out = new ReplicateOutputStream(bao);
      out.writeObject(obj);
      ReplicateInputStream in = new ReplicateInputStream(new ByteArrayInput(bao.getBuffer()), out);
      in.setClassLoader(this.p4remote.getDelegate().getClass().getClassLoader());
      if (p4remote.getDelegate() instanceof StreamHook) {
        in.setHook((StreamHook) p4remote.getDelegate());
      }
      return in.readObject();
    } catch (Exception e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("StubImpl.p4_replicateParameters(Object)", P4Logger.exceptionTrace(e));
      }
      throw (P4RuntimeException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, P4BaseRuntimeException.Couldnt_initialize_stream, e, new Object[]{e.toString()});
    }

  }

  protected Object p4_replicateWithStreams(ReplicateInputStream inn, ReplicateOutputStream outt, Object obj) {
    try {
      outt.writeObject(obj);
      outt.flush();
      inn.setClassLoader(this.p4remote.getDelegate().getClass().getClassLoader());
      inn.setArrayBuffer(outt.getBuffer());
      return inn.readObject();
    } catch (Exception e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("StubImpl.p4_replicateWithStreams(ReplicateInputStream, ReplicateOutputStream, Object)", P4Logger.exceptionTrace(e));
      }
      throw (P4RuntimeException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, P4BaseRuntimeException.Couldnt_initialize_stream, e, new Object[]{e.toString()});
    }
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof StubImpl)) {
      return false;
    }

    StubImpl stub = (StubImpl) obj;

    if (isLocal && stub.isLocal) {
      return p4remote.getDelegate().equals(stub.p4remote.getDelegate());
    } else if (!isLocal && !stub.isLocal) {
      if (!repliable.equals(stub.repliable)) {
        return false;
      } else if (java.util.Arrays.equals(info.connectionProfiles, stub.info.connectionProfiles)) {
        return java.util.Arrays.equals(info.key, stub.info.key);
      }
    }
    return false;
  }

  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    if (isLocal) {
      info.connectionProfiles = P4ObjectBroker.init().getConnectionProfiles();
    }

    out.writeObject(connectionType);
    out.writeObject(info);
  }

  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    P4ObjectBroker.init();

    connectionType = (String) in.readObject();

    if (connectionType == null) {
      connectionType = P4ObjectBroker.transportType;
    }
    timeout = P4Call.TIMEOUT;
    info = (StubBaseInfo) in.readObject();
    info.connected = false;
    this.clLoader = this.getClass().getClassLoader(); // set the classloader of the stub
    Class stubClass = (this.getClass().getInterfaces())[0];
    P4ObjectBroker.init().narrow(this, stubClass, connectionType);
  }

  public Object p4_invokeReflect(Object o, String name, Object[] params, Class[] p) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    Method m = o.getClass().getMethod(name, p);
    return m.invoke(o, params);
  }

  public Object p4_invokeReflect(Object o, String name, Object[] params) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    Class[] p = new Class[0];
    Method m = o.getClass().getMethod(name, p);
    return m.invoke(o, params);
  }

  public ReplicateInputStream p4_getReplicateInput(ReplicateOutputStream outt) {
    try {
      return new ReplicateInputStream(new ByteArrayInput(outt.getBuffer()), outt);
    } catch (IOException ioe) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("StubImpl.p4_getReplicateInput(ReplicateOutputStream)", P4Logger.exceptionTrace(ioe));
      }
      throw (P4RuntimeException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, P4BaseRuntimeException.Couldnt_initialize_stream, ioe, new Object[]{ioe.toString()});
    }
  }

  public ReplicateOutputStream p4_getReplicateOutput() {
    try {
      return new ReplicateOutputStream(new ByteArrayOutput(0));
    } catch (IOException ioe) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("StubImpl.p4_getReplicateOutput()", P4Logger.exceptionTrace(ioe));
      }
      throw (P4RuntimeException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, P4BaseRuntimeException.Couldnt_initialize_stream, ioe, new Object[]{ioe.toString()});
    }
  }

  public void p4_setNewServerInfo(byte[] oKey, int cluster_id) {
    info.key = oKey;
    info.server_id = cluster_id;
  }

  public byte[] p4_getResourceData(String resourceName) throws FileNotFoundException, ClassNotFoundException {
    return p4_getData(resourceName, GET_RESOURCE_DATA);
  }

  public byte[] p4_getClassData(String className) throws FileNotFoundException, ClassNotFoundException {
    return p4_getData(className, GET_CLASS_DATA);
  }

  public byte[] p4_getData(String name, String operation) throws  FileNotFoundException, ClassNotFoundException {
    try {
      if(P4Logger.getLocation().beDebug()){
        P4Logger.getLocation().debugT("StubImpl.p4_getData(String, String)", "Request: " + name + " with operation: " + operation);
      }
      Call call = p4_newCall(operation);
      P4ObjectOutput out = call.getOutputStream();
      out.writeObject(name);
      p4_invoke(call);
      P4ObjectInput in = call.getResultStream();
      byte[] data;
      byte[] allData = null;
      do {
        data = new byte[in.available()];
        int offset = 0;
        int readed = 0;
        while (offset < data.length) {
          readed = in.read(data, offset, data.length - offset);
          if (readed == -1) {
            byte[] result = new byte[offset];
            System.arraycopy(data, 0, result, 0, offset);
            return result;
          }
          offset = data.length;
        }
        byte[] old = null;
        if (allData == null) {
          allData = data;
        } else {
          old = allData;
          allData = new byte[old.length + data.length];
          System.arraycopy(old, 0, allData, 0, old.length);
          System.arraycopy(data, 0, allData, old.length, data.length);
        }
      } while (in.available() > 0);
        if(P4Logger.getLocation().beDebug()){
          P4Logger.getLocation().debugT("StubImpl.p4_getData(String, String)", "Read data with size: " + allData.length);
        }
       data = allData;
       return data;
    } catch (ClassNotFoundException cnf) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("StubImpl.p4_getData(String, String)", P4Logger.exceptionTrace(cnf));
      }
      throw cnf;
    } catch (FileNotFoundException fnf) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("StubImpl.p4_getData(String, String)", P4Logger.exceptionTrace(fnf));
      }
      throw fnf;
    } catch (Exception e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("StubImpl.p4_getData(String, String)", P4Logger.exceptionTrace(e));
      }
      throw new ClassNotFoundException(name, e);
    }
  }

  public void setServerLoaderName(String name){
    this.info.setServer_classLoaderName(name);
  }

  public String getServerLoderName(){
    return this.info.getServer_classLoaderName();
  }

  public void setCallTimeout(long timeout) {
      if (timeout < 0) {
        throw new IllegalArgumentException("Call timeout cannot be negative");
      } else {
        this.timeout = timeout;
      }

  }

  public long getCallTimeout() {
      return timeout;
  }
}

