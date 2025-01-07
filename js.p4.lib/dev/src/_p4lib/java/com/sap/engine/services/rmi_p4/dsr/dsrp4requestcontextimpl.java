/**
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rmi_p4.dsr;

import com.sap.engine.lib.lang.Convert;

/**
 * This class will contain all needed information about the message.
 * It could be extended afterwards with more specific details, if they are needed.
 * This way we will have versions and the new version will give more details, but will be 
 * completely compatible with the old version. 
 * 
 * @author Tsvetko Trendafilov (I041949)
 */
public class DSRP4RequestContextImpl {
  
  /**
   * Constants for types of message.
   */
  public static final int REQUEST = 0;
  public static final int REPLY = 1;
  public static final int ERRORREPLY = 2;
  public static final int TIMEOUT = 3;
  
  public static final int INCOMING = 10;
  public static final int OUTGOING = 11;
  
  public static final int JMS_OFF = 20;
  public static final int JMS_ON = 21;
  public static final int JMS_ONLY = 22;
  
  
  //Target Host as IP address
  private String host;
  //Target P4 port
  private int port;
  //None, SSL, HTTPS, SAPRouter - the connection type
  private String connectionType;
  /**
   * For requests it is our server ID.
   * For replies it is the ???
   */
  int server_id;
  /**
   * For requests it is current client's id. ID is: <br> 
   * -1         for all stand-alone clients;  
   * Server ID  of current P4 client server for cluster-cluster communication.
   */ 
  int client_id;
  /**
   * The ID of current call which sends request or receives reply.
   */
  long call_id; 
  //The name of the class of the stub, which method is invoked
  String stubClass;
  //Currently invoked method. May be some special internal methods
  String operation;
  //bytes transferred in current message
  private int bytes;
  //If the call is for redirectable object or not.
  private boolean redirectable;
  //The connection ID
  private long connectionID;
  //the type of the message "request", "reply" or "errorreply"
  /**
   * 0 - request;
   * 1 - reply;
   * 2 - error reply.
   */
  private int type;
  /** 
   * The direction of current request or reply. If it comes to the current server (incoming - server) or 
   * is initiated for another partner (outgoing - client).
   * 
   * 10 - "incoming" for incoming requests and incoming replies for already sent requests.
   * 11 - "outgoing" for outgoing requests and outgoing replies for received incoming requests;  
   */
  private int direction;
  
  //Used for debugging purpose to check if requestStrat and requestEnd are correctly invoked or disordered.
  public String invokedDSRMethod = "";
  
  public int getBytes(){
    return bytes;
  }
  
  public String getHost(){
    return host;
  }
  
  public int getPort(){
    return port;
  }
  
  public String getConnectionType(){
    return connectionType;
  }
  
  public String getConnectionBanner(){
    return connectionType + ":" + host + ":" + port;
  }
  
  public String getStubClasses(){
    return stubClass;
  }
  
  public String getOperation(){
    return operation;
  }
  
  public String getClientID(){
    return Integer.toString(client_id);
  }
  
  public String getServerID(){
    return Integer.toString(server_id);
  }
  
  /**
   * The ID of connection. It is long number, which is returned as String.
   * @return Connection ID
   */
  public String getConnectionID(){
    return Long.toString(connectionID);
  }
  
  /**
   * 0 - request;
   * 1 - reply;
   * 2 - error reply.
   */
  public int getType(){
    return type;
  }
  
  /** 
   * The direction of current request or reply. If it comes to the current server (incoming - server) or 
   * is initiated for another partner (outgoing - client).
   * 
   * 10 - "incoming" for incoming requests and incoming replies for already sent requests.
   * 11 - "outgoing" for outgoing requests and outgoing replies for received incoming requests;  
   */
  public int getDirection(){
    return direction;
  }
  
  /**
   * Returns if the P4 message is redirecteble or not. It it can be redirected to a healthy server node, 
   * if the target server node goes down or cannot be redirected to another server node.
   * @return true if the message is redirectable or
   *         false otherwise.
   */
  public boolean getRedirecteblr(){
    return redirectable;
  }
  
  public long getCallID(){
    return this.call_id;
  }
  
  
  public void setHost(String host){
    this.host = host;
  }
  
  public void setBytes(int bytes){
    this.bytes = bytes;
  }
  
  public void setPort(int port){
    this.port = port;
  }
  
  /**
   * Set the connection type:
   *  - None for default (plain) connection, 
   *  - ssl  for P4 connection over SSL socket, 
   *  - ni   for P4 connection over socket via SAP Router, 
   *  - https for HTTP tunneling over secure connection.  
   * @param type The string for connection type.
   */
  public void setConnectionType(String type){
    if (type == null || type.equals("")) {
      this.connectionType = "None";
    }else {
      this.connectionType = type;
    }
  }
  
  public void setStubClass(String stub){
    this.stubClass = stub;
  }
  
  public void setClientID(int id){
    this.client_id = id;
  }
  
  public void setServerID(int id){
    this.server_id = id;
  }
  
  public void setOperation(String method){
    this.operation = method; 
  }
  
  public void setRedirectable(boolean redirectable){
    this.redirectable = redirectable;
  }

  public void setConnectionID(byte[] id) {
    connectionID = Convert.byteArrToLong(id, 0);
  }

  /**
   * Set the type of the message.
   * @param messageType - 0 - request for requests; 
   *                      1 - reply for replies;
   *                      2 - error reply - process as reply;
   *                      3 - timeout - no reply received for request.
   */
  public void setType(int messageType) {
    this.type = messageType;
  }

  /**
   * Shows the direction of current communication.
   * @param direction 10 - "incoming" for incoming requests and incoming replies for already sent requests. 
   *                  11 - "outgoing" for outgoing requests and outgoing replies for received incoming requests;
   */
  public void setDirection(int direction) {
    this.direction = direction;
  }
  
  public void setCallID(long id){
    this.call_id = id;
  }
  
  public String toString(){
    StringBuilder readableResult = new StringBuilder();
    readableResult.append(super.toString()).append("\r\n");
    readableResult.append("Connection: ").append(getConnectionBanner()).append("\r\n");
    readableResult.append("Message:    ");
    if (direction == INCOMING) {
      readableResult.append("incoming ");
    } else {
      readableResult.append("outgoing ");
    }
    if (type == REQUEST) {
      readableResult.append("request");
    } else {
      if (type == REPLY) {
        readableResult.append("reply");
      } else {
        readableResult.append("error-reply");
      }
    }
    readableResult.append("\r\n");
    
    readableResult.append("Transferred:   ").append(bytes).append("\r\n");
    readableResult.append("Connection ID: ").append(connectionID).append("\r\n");
    readableResult.append("Call ID:   ").append(call_id).append("\r\n");
    readableResult.append("Client ID: ").append(client_id).append("\r\n");
    readableResult.append("Server ID: ").append(server_id).append("\r\n");
    readableResult.append("Operation: ").append(operation).append("\r\n");
    readableResult.append("Stub/Skel: ").append(stubClass).append("\r\n");
    readableResult.append("redirectable:  ").append(redirectable).append("\r\n");
    readableResult.append("Method:    ").append(invokedDSRMethod).append("\r\n");
    return readableResult.toString();
  }
}