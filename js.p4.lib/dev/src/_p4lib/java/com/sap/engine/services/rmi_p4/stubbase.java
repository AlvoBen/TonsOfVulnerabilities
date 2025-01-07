package com.sap.engine.services.rmi_p4;

import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.interfaces.cross.Connection;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.server.Operation;

/**
 * The stub base used by the broker to initialize the client
 * proxy.THe broker sets the info and repliable fields.
 * They are used by the proxy then new remote call is created
 *
 * @author Ivo Simeonov, Georgy Stanev
 * @version 7.0
 */
public abstract class StubBase extends StubImpl {

  static final long serialVersionUID = -7269201963209714255L;

  public StubBase() {

  }

  /**
   * returns the StubBaseInfo for this object
   *
   * @return info field of the class
   */
  public StubBaseInfo getInfo() {
    return super.p4_getInfo();
  }

  public RemoteObjectInfo getObjectInfo() {
    return super.getObjectInfo();
  }

  public String getConnectionType() {
    return super.p4_getConnectionType();
  }

  public void setConnectionType(String _connectionType) {
    super.p4_setConnectionType(_connectionType);
  }

  /**
   * Returns all operations available for the client.
   * Function is used when proxy fills the remote request
   * header
   *
   * @return interface operations represented
   *         by string names
   */
  public abstract Operation[] getOperations();

  /**
   * Creates new remote call on the specified by index
   * method.
   *
   * @param opnum index of the method
   * @return created call
   * @throws IOException
   */
  public Call newCall(int opnum) throws Exception {
    if (repliable == null) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("StubBase.newCall(int)", "Operation with opnum: " + opnum + " was called on disconnected stub: " + this);
      }
      throw new P4ConnectionException("Stub is not connected");
    }
    return Call.newCall(this, opnum);
  }

  public Call newCall(String opname) throws Exception {
    if (repliable == null) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("StubBase.newCall(String)", "Operation: " + opname + " was called on disconnected stub: " + this);
      }
      throw new P4ConnectionException("Stub is not connected");
    }
    return Call.newCall(this, opname);
  }

  /**
   * Invokes a call witch was created before that
   * with void newCall(int).
   *
   * @param call the call to invoke
   * @throws IOException
   */
  public void invoke(Call call) throws Exception {
    super.p4_invoke(call);
  }

  /**
   * Removes a call.It is assumed that call was created and
   * invoked before that.
   *
   * @param call the call to remove
   */
  public void done(Call call) {
    super.p4_done(call);
  }

  /**
   * method used by the broker to set an object for the proxy
   * for transport level arrangement
   *
   * @param repliable the object used for requesting and
   */
  public void setConnection(Connection repliable) {
    super.p4_setConnection(repliable);
  }

  public ClassLoader getClassLoader() {
    return super.p4_getClassLoader();
  }

  public void setClassLoader(ClassLoader clLoader) {
    super.p4_setClassLoader(clLoader);
  }

  /**
   * method used by the broker to set the remote object
   * information
   *
   * @param info the information
   */
  public void setInfo(StubBaseInfo info) {
    super.p4_setInfo(info);
  }

  public Object replicate(Object obj) {
    return super.p4_replicate(obj);
  }

  protected Object replicateParameters(Object obj) {
    return super.p4_replicateParameters(obj);
  }

  protected Object replicateWithStreams(ReplicateInputStream inn, ReplicateOutputStream outt, Object obj) {
    return super.p4_replicateWithStreams(inn, outt, obj);
  }

  public Object invokeReflect(Object o, String name, Object[] params, Class[] p) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return super.p4_invokeReflect(o, name, params, p);
  }

  public Object invokeReflect(Object o, String name, Object[] params) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return super.p4_invokeReflect(o, name, params);
  }

  public ReplicateInputStream getReplicateInput(ReplicateOutputStream outt) {
    return super.p4_getReplicateInput(outt);
  }

  public ReplicateOutputStream getReplicateOutput() {
    return super.p4_getReplicateOutput();
  }

  public void setNewServerInfo(byte[] oKey, int cluster_id) {
    super.p4_setNewServerInfo(oKey, cluster_id);
  }

}