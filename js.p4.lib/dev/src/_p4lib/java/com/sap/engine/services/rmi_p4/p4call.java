package com.sap.engine.services.rmi_p4;

import com.sap.engine.lib.util.ConcurrentHashMapLongObject;
import com.sap.engine.interfaces.cross.CrossCall;
import com.sap.engine.interfaces.cross.Connection;
import com.sap.engine.services.rmi_p4.dsr.DSRP4Instr;
import com.sap.engine.services.rmi_p4.dsr.DSRP4RequestContextImpl;
import com.sap.engine.services.rmi_p4.exception.P4Logger;


/**
 * Author: Asen Petrov
 * Date: 2006-1-9
 * Time: 16:19:11
 */
public abstract class P4Call implements CrossCall {
  static long id = 0;
  private static long idHeader = 1;
  public static long TIMEOUT = 0;
  protected static ConcurrentHashMapLongObject calls = new ConcurrentHashMapLongObject(101);
  protected long timeout;
  protected long call_id;
  protected Message replyMessage = null;
  protected Exception ex;
  public boolean exception = false;
  public Connection repliable;


  public static P4Call getCall(long call_id) {
    P4Call call = (P4Call) calls.get(call_id);
    if (call != null) {
      calls.remove(call_id);
    }
    return call;
  }

  public static P4Call getCallReference(long call_id) {
    return (P4Call) calls.get(call_id);
  }
    
  public synchronized static long getNewId() {
    P4ObjectBroker broker = P4ObjectBroker.getBroker(); // to avoid nullpointes on the client when the broker is closed
    if (broker != null && broker.isServerBroker()) {
      if (idHeader == 1) { //not initialized
        idHeader = broker.getId() & 255;
        idHeader = idHeader << 56;
      }
      id++;
      if (id == 16777215) { // -1 >>> 8 :wrap when the 7th byte is filled
        id = 0;
      }
      return idHeader | id;
    } else {
      return id++;
    }
  }

   public long getCall_id() {
    return call_id;
  }

  public static Object[] getAllCalls() {
    return calls.getAllValues();
  }

  public static void removeCall(long call_id) {
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("P4Call.removeCall(long)", "Removing call " + call_id);
    }
    if (calls.containsKey(call_id)) {
      calls.remove(call_id);
    } else {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4Call.removeCall(long)", "Call id " + call_id + " was already removed");
      }
    }
  }

  public static boolean callsEmpty() {
    return calls.isEmpty();
  }

  /**
   * This method is invoked, when exception is received instead of reply to current Call.
   * @param replyMsg The reply message with received Exception. 
   */
  public synchronized void setException(Message replyMsg){
    this.exception = true;
    this.replyMessage = replyMsg;
    notify();
    informDSR(replyMsg.request, DSRP4RequestContextImpl.INCOMING, DSRP4RequestContextImpl.ERRORREPLY);// 10 - "incoming", 2 - "errorreply");
  }

  /**
   * Overridden in class Call. Here it does not do anything.
   * @param request byte array of reply or error-reply
   * @param destination If the message is incoming (server) or outgoing (client)
   * @see DSRP4RequestContextImpl#INCOMING
   * @see DSRP4RequestContextImpl#OUTGOING
   * @param type The type of the message. If it is request, reply or error-reply.
   * @see DSRP4RequestContextImpl#REQUEST
   * @see DSRP4RequestContextImpl#REPLY
   * @see DSRP4RequestContextImpl#ERRORREPLY 
   */
  public void informDSR(byte[] request, int destination, int type) {
    //Do nothing for non "Call" calls. Overridden in Call.
  }

  public synchronized void setException(boolean ex) {
    exception = ex;
  }

  public synchronized void setException(Exception ex) {
    this.ex = ex;
    exception = true;
    notify();
  }

  public synchronized void setReply(Message reply) {
    this.replyMessage = reply;
    notify();
  }

  public void remove() {
    if (calls.containsKey(call_id)) {
      calls.remove(call_id);
    }
  }
// used by the client implementation
  public abstract boolean isFrom(Connection repliable);
  //this must not be used on 7.10 servers as connections are managed in the cross service.
  public abstract boolean isToConnection(int connectionId);
  //used by the server implementation 
  public abstract boolean isToClusterElement(int clusterElement);
  //used by server implementation of Broker to unify getting of destination server id
  public abstract int getDestServerId();

  public void fail(){
    synchronized(this){
       Exception exe = P4ObjectBroker.init().getException(P4ObjectBroker.P4_ConnectionException, com.sap.engine.services.rmi_p4.P4ConnectionException.ConnectionLost, null);
       setException(exe);
       notifyAll();
    }
  }
 // public abstract P4ObjectInput getResultStream() throws IOException, Exception;
}
