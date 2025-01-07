/**
 * ConnectionFactory.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.connection;

import java.io.Serializable;
import javax.jms.JMSException;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.JMSConstants;
import com.sap.jms.client.Util;
import com.sap.jms.client.connection.Connection.ConnectionType;
import com.sap.jms.client.xa.JMSXAConnection;
import com.sap.jms.client.xa.JMSXAQueueConnection;
import com.sap.jms.client.xa.JMSXATopicConnection;
import com.sap.jms.client.xa.RemoteXAConnection;
import com.sap.jms.client.xa.RemoteXAQueueConnection;
import com.sap.jms.client.xa.RemoteXATopicConnection;
import com.sap.jms.protocol.Packet;
import com.sap.jms.protocol.PacketImpl;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.notification.ConnectionCreateRequest;
import com.sap.jms.protocol.notification.ConnectionCreateResponse;
import com.sap.jms.protocol.notification.ServerExceptionResponse;
import com.sap.jms.util.logging.LogService;
import com.sap.jms.util.logging.LogServiceImpl;

import com.sap.jms.server.remote.JMSRemoteServer;
import com.sap.jms.util.compat.rmi_p4.interfaces.P4RemoteLoadingExt;
import com.sap.jms.util.compat.engine.interfaces.cross.ObjectIdentifier;
import com.sap.jms.util.compat.engine.interfaces.cross.RedirectableExt;

/**  
 * @author Desislav Bantchovski 
 * @version 7.10 
 */

public class RemoteConnectionFactory implements RemoteConnectionFactoryInterface, 
                                                P4RemoteLoadingExt, RedirectableExt, ObjectIdentifier {
  
  static final long serialVersionUID = 3294517766353918551L;
	
  private static final transient String DEFAULT_SERVER_INSTANCE = JMSConstants.DEFAULT_SERVER_INSTANCE;
  private static final transient String LOG_COMPONENT = "connection.RemoteConnectionFactory";
      
  private static transient ThreadSystem threadSystem = null;
  
  private String serverInstance = DEFAULT_SERVER_INSTANCE;
  private String clientID = null;  
  private String factoryName = "";
  
  private JMSRemoteServer server = null; 
  private transient LogService logService;
  private String[] resources = null;
  private String redirectableKey = null;
  private boolean supportsOptimization = false;
  public final static String CrossObjectFactoryName = "JMSRemoteConnectionFactoryCrossObjectFactoryName";  
     
  public RemoteConnectionFactory(JMSRemoteServer server, String serverInstance, 
		  String factroy, String systemID, String hardwareID, String[] resources, 
		  String clientID, boolean supportsOptimization) {
	this.server = server;
	this.resources = resources;
	this.clientID = clientID;
	this.supportsOptimization = supportsOptimization;
	this.serverInstance = serverInstance;
	this.factoryName = factroy;
  }
 
  /**
   * Method setServerInstance. Sets the name of the server instance to which the new
   * connections will be bound.
   * @param serverInstance name of server instance
   */
  public void setServerInstance(String serverInstance) {
	this.serverInstance = serverInstance;
  }

  /**
   * Method getServerInstance. Sets the name of the server instance to which the new
   * connections will be bound.
   * @return name of server instance of this ConnectionFactory object
   */  
  public String getServerInstance() {
	return serverInstance;
  }

  
  /* (non-Javadoc)
   * @see javax.jms.ConnectionFactory#createConnection()
   */
  public javax.jms.Connection createConnection() throws javax.jms.JMSException {
	return createConnection(ConnectionType.GENERIC_CONNECTION);
  }

  /* (non-Javadoc)
   * @see javax.jms.ConnectionFactory#createConnection(String, String)
   */
  public javax.jms.Connection createConnection(String userName, String password) throws javax.jms.JMSException {
	return createConnection(userName, password, ConnectionType.GENERIC_CONNECTION);
  }
    
  /**
   * Method createConnection. Creates a connection.
   * @param connectionType type of the connection (queue, topic, generic)
   * @return Connection the created connection
   * @throws JMSException thrown if internal error occurs
   */
  public javax.jms.Connection createConnection(ConnectionType connectionType) throws javax.jms.JMSException {
	return  createConnection("", "", connectionType);
  }
  
  /**
   * Method createConnection. Creates a connection.
   * @param userName the caller's user name
   * @param password the caller's password
   * @param connectionType type of the connection (queue, topic, generic)
   * @return Connection the created connection
   * @throws JMSException thrown if internal error occurs
   */
  public javax.jms.Connection createConnection(String userName, String password, ConnectionType connectionType) throws javax.jms.JMSException {   
	Packet response = null;    
    logService = LogServiceImpl.getLogService(LogServiceImpl.CLIENT_LOCATION);
	logService.debug(LOG_COMPONENT, "createConnection");
	    
	response = sendConnectionCreateRequest(userName, password);
/*	
	if (response.getPacketType() == PacketTypes.SERVER_EXCEPTION_RESPONSE) {
		JMSException jmse = ((ServerExceptionResponse)response).getException();
		logService.warningLog(LOG_COMPONENT, "Failed to create connection! Reason: {0}", new Object[] {jmse.getMessage()});
		logService.exception(LogService.WARNING, LOG_COMPONENT, jmse);      
		throw jmse;
	}
*/	
	if (response != null) {
		logService.debug(LOG_COMPONENT, "Response.TYPE = {0}", new Object[] {response.getPacketTypeAsString()});			
	}
	RemoteConnection connection = null;
    long connectionId = ((ConnectionCreateResponse) response).getConnectionID(); 

    if (connectionType == ConnectionType.GENERIC_CONNECTION) {
		connection = new RemoteConnection(connectionId, serverInstance, server, threadSystem, clientID,supportsOptimization);
      } else if (connectionType == ConnectionType.QUEUE_CONNECTION) {
  		connection = new RemoteQueueConnection(connectionId, serverInstance, server, threadSystem, clientID,supportsOptimization);
      } else if (connectionType == ConnectionType.TOPIC_CONNECTION) {
  		connection = new RemoteTopicConnection(connectionId, serverInstance, server, threadSystem, clientID,supportsOptimization);
      } else if (connectionType == ConnectionType.XA_GENERIC_CONNECTION) {
		  connection = new RemoteXAConnection(connectionId, serverInstance, server, threadSystem, clientID,supportsOptimization);
      } else if (connectionType == ConnectionType.XA_QUEUE_CONNECTION) {
		  connection = new RemoteXAQueueConnection(connectionId, serverInstance, server, threadSystem, clientID,supportsOptimization);
      } else if (connectionType == ConnectionType.XA_TOPIC_CONNECTION) {
		  connection = new RemoteXATopicConnection(connectionId, serverInstance, server, threadSystem, clientID,supportsOptimization);
      }

	checkProtocolVersion((ConnectionCreateResponse)response,connection);
	
	logService.infoTrace(LOG_COMPONENT, "JMS connection created:\n{0}", connection);
	logService.debug(LOG_COMPONENT, "createConnection result = {0}", new Object[] {(connection != null ? connection.toString() : "null")});
	return connection;
  }
  
  /**
   * Method sendConnectionCreateRequest. Sends a request to the server to create a 
   * connection.
   * @param userName user name
   * @param password password
   * @return Packet the response from the server (ConnectionCreateResponse or ServerExceptionResponse)
   * @throws JMSException thrown if an error occurs 
   */
  private Packet sendConnectionCreateRequest(String userName, String password) throws javax.jms.JMSException {
	PacketImpl response = null; 
    
    try {
  	  Packet request = new ConnectionCreateRequest(serverInstance, userName, password, factoryName);
  	  byte[] answer = server.dispatchRequest(0, request.getBuffer(), request.getOffset(), request.getLength());	
	  if (answer != null) {  	  
		response = (PacketImpl) Util.createPacket(answer, 0, answer.length);
	  }
	} catch (Exception e) {
	  logService.exception(LogService.FATAL, LOG_COMPONENT, e);
	  JMSException jmse = new javax.jms.JMSException("Cannot send connection create request.");
	  jmse.initCause(e);
	  jmse.setLinkedException(e);
	  throw jmse;	    	
	}
	checkForException(response);
	return response;
    
  }
  
  /**
   * Method checkForException. Checks whether the packet is a wrapper of an exception.
   * @param packet the packet to be checked
   * @throws JMSException thrown if the packet contains an exception.
   */
  private void checkForException(PacketImpl packet) throws javax.jms.JMSException {
	if (packet == null) {
	  JMSException jmse = new JMSException("Response from the server cannot be received.");
	  logService.errorTrace(LOG_COMPONENT, "Failed to create connection!");
	  logService.exception(LogService.ERROR, LOG_COMPONENT, jmse);
	  throw jmse;
	} if (packet.getPacketType() == PacketTypes.SERVER_EXCEPTION_RESPONSE) {
	  JMSException jmse = ((ServerExceptionResponse)packet).getException();
	  logService.errorTrace(LOG_COMPONENT, "Failed to create connection!");
	  logService.exception(LogService.ERROR, LOG_COMPONENT, jmse);      
	  throw jmse;
	} else if (packet.getPacketType() != PacketTypes.CONNECTION_CREATE_RESPONSE) {
	  JMSException jmse = new JMSException("Unexpected response received.");
	  logService.errorTrace(LOG_COMPONENT, "Failed to create connection!");
	  logService.exception(LogService.ERROR, LOG_COMPONENT, jmse);
	  throw jmse;
	}    
  }
  
  
  /**
   * A method that checks whether the server has the same protocol version as the one in the client.
   * In case not the connection will be closed (by sending a packet ConnectionCloseRequest)
   * @param connectionCreateResponse - the package returned from the server
   * @param connection - the established connection to the server 
   * @throws JMSException thrown in case the protocol version is incompapatible or due to any other general problem. 
   */
  private void checkProtocolVersion(ConnectionCreateResponse connectionCreateResponse, Connection connection) throws JMSException {
    //this code is executed in the client VM, we can take as version the one from the ConnectionCreateResponse class
    int clientVersion = ConnectionCreateResponse.getClassCompatibilityVersion();
      
    //the one present from the packet.
    int serverVersion = connectionCreateResponse.getProtocolCompatibilityVersion();
    
    if (clientVersion != serverVersion) {
      logService.errorTrace(LOG_COMPONENT, "checkProtocolVersion", "Could not create JMS connection because of incompatible version. Server version is : "+ serverVersion +", client version is : "+ clientVersion);
    }
      
  }
  
  
      
  /**
   * This method returns the URLs toSting() representing the class directory, or jar files 
   * that are actually needed for remote classloading - for backward compatibility with 6.40 
   * @return array of jar file names. 
   */
  
  public String[] getResources() {
  	return resources;
  }
  
  public void setRedirectableKey(String redirectableKey) {
  	this.redirectableKey = redirectableKey;
  }
  
  public ObjectIdentifier p4_objIdentity() {
  	return this;
  }
  
  public String _getFactoryName() {
  	return CrossObjectFactoryName;
  }
  public Serializable _getObjectId() {
  	return redirectableKey;
  }
  public String getTimeCreated() {
	  return redirectableKey.substring(RemoteConnectionFactoryInterface.TIMESTAMP_OFFSET, RemoteConnectionFactoryInterface.TIMESTAMP_LENGTH);
  }

  public boolean supportsOptimization() {
	  return supportsOptimization;
  }

  public String p4_getIdentifier() {
	  return null;
  }
}
