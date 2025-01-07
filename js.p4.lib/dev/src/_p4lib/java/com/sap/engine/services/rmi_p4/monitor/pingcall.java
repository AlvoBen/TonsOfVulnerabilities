/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.services.rmi_p4.monitor;

import com.sap.engine.lib.lang.Convert;
import com.sap.engine.services.rmi_p4.ClientConnection;
import com.sap.engine.services.rmi_p4.Message;
import com.sap.engine.services.rmi_p4.P4IOException;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.exception.P4BaseIOException;
import com.sap.engine.interfaces.cross.CrossCall;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author Nikolai Neychev
 * @version 7.0
 */
public class PingCall implements CrossCall {

  private static Hashtable calls = new Hashtable();
  private static long callId = 0;
  private long id;
  private byte[] result;
  private ClientConnection con;
  private int dispId;
  private int clientId;
  private static int pingTimeout = 10000;

  PingCall(long id) {
    this.id = id;
  }

  public int getDispId() {
    return dispId;
  }

  public void setDispId(int dispId) {
    this.dispId = dispId;
  }

  public int getClientId() {
    return clientId;
  }

  public void setClientId(int clientId) {
    this.clientId = clientId;
  }

  public synchronized byte[] getResult(ClientConnection con) throws InterruptedException, P4IOException {
    this.con = con;

    if (result == null) {
      do {
        this.wait(pingTimeout);
      } while (false);
    }

    if (result == null) {
      throw (P4IOException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Connection_lost, null);
    }
    return result;
  }

  public synchronized void set(Message result) {
    this.result = result.request;
    this.notify();
  }

  public void writeId(byte[] where, int off) {
    Convert.writeLongToByteArr(where, off, id);
  }

  public void remove() {
    Long ID = new Long(id);
    calls.remove(ID);
  }

  protected static void clear() {
    calls.clear();
    callId = 0;
  }

  protected static void freeConnection(ClientConnection con) {
//    InitialCall call = null;
//
//    for (Enumeration enumeration = calls.elements(); enumeration.hasMoreElements();) {
//      call = (InitialCall) enumeration.nextElement();
//      if ((call.con != null) && call.con.equals(con)) {
//        synchronized (call) {
//          call.notify();
//        }
//        call.remove();
//      }
//    }
  }

  public static synchronized PingCall getPingCall() {
    PingCall call = new PingCall(callId);
    calls.put(new Long(callId++), call);
    return call;
  }

  public static PingCall getCall(long id) {
    Long ID = new Long(id);
    PingCall call = (PingCall) calls.get(ID);
    calls.remove(ID);
    return call;
  }

  public static Enumeration getAllCalls() {
    return calls.elements();
  }

   public void fail(){
    synchronized(this){
      notifyAll();
    }
  }
}



