package com.sap.engine.services.rmi_p4.server;

import com.sap.engine.interfaces.cross.*;
import com.sap.engine.interfaces.cross.logger.CrossLogger;
import com.sap.engine.frame.core.thread.ClientThreadContext;
import com.sap.engine.services.rmi_p4.ProtocolHeader;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.Message;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.lib.lang.Convert;

import java.io.IOException;

/**
 * Copyright: Copyright (c) 2003
 * Company: SAP AG - Sap Labs Bulgaria
 *
 * @author Ivan Atanassov
 * @version 7.1
 */
public class MSConnection implements Connection {
  private ClusterOrganizer organizer;
  private int serverID;
  private ClientThreadContext tc = null;
  private P4ObjectBroker broker = P4ObjectBroker.init();
  private ConnectionProperties properties;

  public MSConnection(ClusterOrganizer organizer, int serverID) {
    this.organizer = organizer;
    this.serverID = serverID;
    properties = new ConnectionProperties(serverID, MessageProcessor.P4_PROCESSOR, Connection.BIDIRECTIONAL);
  }

   public byte[] getId() {
      return new byte[8];  //TODO
  }

  public long getIdAslong() {
      return 0;  //TODO
  }

  public void sendRequest(byte[] messageBody, int size, CrossCall call) throws IOException {
    int server_id = Convert.byteArrToInt(messageBody, ProtocolHeader.DESTINATION_SERVER_ID); //todo other ID could be used also
    if (P4Logger.getLocation().bePath()) {
      P4Logger.getLocation().pathT("Sending a message to server node " + server_id + " size: " + size);
      if (CrossLogger.getLocation().beDebug()) { //Prevent message body dump
        CrossLogger.getLocation().debugT("\r\n" + Message.toString(messageBody, 0, size));
      }
    }
    organizer.send(server_id, messageBody, 0, size, (broker.getCTC() != null));
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
      return properties;
  }

  public String getUnderlyingProfile() {
    return null;
  }

  public void setUnderlyingProfile(String profile) {
      //TODO
  }

}
