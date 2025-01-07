package com.sap.engine.services.rmi_p4.server;

import com.sap.engine.interfaces.cross.Connection;
import com.sap.engine.interfaces.cross.CrossCall;
import com.sap.engine.interfaces.cross.ConnectionProperties;

import java.io.IOException;

/**
 * Copyright: Copyright (c) 2003
 * Company: SAP AG - Sap Labs Bulgaria
 *
 * @author Ivan Atanassov
 * @version 7.1
 */
public class MSErrorConnection  implements Connection {
  private IOException exception;
  private int serverID;

  public MSErrorConnection(int serverId, IOException exception) {
    this.serverID = serverId;
    this.exception = exception;
  }

   public byte[] getId() {
      return new byte[8];  //TODO
  }

  public long getIdAslong() {
      return 0;  //TODO
  }

  public void sendRequest(byte[] messageBody, int size, CrossCall call) throws IOException {
    IOException ioex =  new IOException("Not initialized connection");
    ioex.initCause(exception);
    throw ioex;
  }

  public void sendReply(byte[] messageBody, int size, byte[] requestId) throws IOException {
     sendRequest(messageBody, size, null);
  }

  public int getPeerId() {
      return 0;  //TODO
  }

  public void close() {
      //TODO
  }

  public void callCompleted(CrossCall call) {
      //TODO
  }

  public void setMetaData(Object metaData) {
      //TODO
  }

  public Object getMetaData() {
      return null;  //TODO
  }

  public boolean isClosed() {
      return false;  //TODO
  }

  public boolean isLocal() {
      return false;  //TODO
  }

  public CrossCall[] getCalls() {
      return new CrossCall[0];  //TODO
  }

  public void addRequestMonitor(Object monitor) // only put ExecutionMonitor here
  {
      //TODO
  }

  public ConnectionProperties getProperties() {
    return null;
  }

  public String getUnderlyingProfile() {
    return null;
  }

  public void setUnderlyingProfile(String profile) {
      //TODO
  }

}
