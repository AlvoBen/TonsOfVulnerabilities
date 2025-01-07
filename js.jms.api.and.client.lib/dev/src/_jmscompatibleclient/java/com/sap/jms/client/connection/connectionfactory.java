/**
 * ConnectionFactory.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.connection;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.JMSSecurityException;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.lib.util.LinkedList;
import com.sap.engine.lib.util.RandomGenerator;
import com.sap.jms.JMSConstants;
import com.sap.jms.client.connection.Connection.ConnectionType;
import com.sap.jms.client.session.ThreadPool;
import com.sap.jms.client.xa.JMSXAConnection;
import com.sap.jms.client.xa.JMSXAQueueConnection;
import com.sap.jms.client.xa.JMSXATopicConnection;
import com.sap.jms.protocol.PacketImpl;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.notification.ConnectionCreateRequest;
import com.sap.jms.protocol.notification.ConnectionCreateResponse;
import com.sap.jms.protocol.notification.ServerExceptionResponse;
import com.sap.jms.server.ServerComponentAccessor;
import com.sap.jms.server.ServerComponentInterface;
import com.sap.jms.util.logging.LogServiceImpl;
import com.sap.jms.util.logging.LogService;
import com.sap.tc.logging.PropertiesConfigurator;


/**
 * @author Margarit Kirov
 * @version 6.30
 */

/*
 * This class is used to connect to old versions of SAP WebAS (NW04(s))
 * For newer versions see com.sap.jms.client.connection.RemoteConnectionFactory
 */
public class ConnectionFactory implements javax.jms.ConnectionFactory, java.io.Serializable {
  
  static final long serialVersionUID = 3294517766353918551L;
	
//private String path = the relative path where the client should put 
//                      his own preferences if we support this
//                      and perhaps his default security settings
//                      we could use XML to describe user preferences

  private static final transient int DEFAULT_MAX_POOL_SIZE = 128;
  private static final transient int DEFAULT_INITIAL_POOL_SIZE = 5;
  private static final transient String DEFAULT_USER_NAME = "Guest";
  private static final transient String DEFAULT_PASSWORD = "";
  private static final transient String DEFAULT_SERVER_INSTANCE = JMSConstants.DEFAULT_SERVER_INSTANCE;
  private static final transient String PROPERTY_JMS_USER = "jms.user";
  private static final transient String PROPERTY_JMS_PASSWORD = "jms.password";
  private static final transient String LOG_COMPONENT = "connection.connectionFactory";
      
  private static final String LOG_PROPERTIES_FILE = "logging.properties";
  private static final String LOG_FILE = "sapjms.log";
  
  private static transient ThreadSystem threadSystem = null;
  private static boolean isLogInitialized = false;
  
  private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;
  private int initialPoolSize = DEFAULT_INITIAL_POOL_SIZE;
  private LinkedList hosts = new LinkedList();
  private LinkedList ports = new LinkedList();
  private String userName = DEFAULT_USER_NAME;
  private String password = DEFAULT_PASSWORD;  
  private String serverInstance = DEFAULT_SERVER_INSTANCE;
  private String hardwareID;
  private String systemID;
  private String factoryName = "";

  /* describes if password field is bringing the password or the connection factory name */
  private transient boolean isPasswordFieldBringsPassword = false;

  private transient LogService logService;
  private transient int[] dispatcherSequence = null;
     
  /**
   * Constructor ConnectionFactory.
   * @param hosts names of the hosts of the dispatchers
   * @param ports the dispatcher ports listening for JMS requests
   * @param userName user name for authentication purposes
   * @param password password password for authentication purposes
   * @param initialPoolSize property specifying the initial size of the client's thread pool
   * @param maxPoolSize property specifying the maximum size of the client's thread pool
   */
  public ConnectionFactory(String[] hosts, int[] ports, String serverInstance, String userName, String password, int initialPoolSize, int maxPoolSize, String systemID, String hardwareID) {
    this(hosts, ports, userName, password, systemID, hardwareID);
    //administrators will use the next two to change the default settings for the thread pool settings
    this.serverInstance = serverInstance;
    this.initialPoolSize = initialPoolSize;
    this.maxPoolSize = maxPoolSize;
  }
  
  /**
   * Constructor ConnectionFactory.
   * @param hosts names of the hosts of the dispatchers
   * @param ports the dispatcher ports listening for JMS requests
   * @param userName user name for authentication purposes
   * @param password password for authentication purposes
   */
  public ConnectionFactory(String[] hosts, int[] ports, String userName, String password, String systemID, String hardwareID) {
    this(hosts, ports, systemID, hardwareID);
    //administrators will use the next two to change the default settings for the user name and password for the connections
    this.userName = userName; 
    this.password = password;
  }

  /**
   * Constructor ConnectionFactory.
   * @param hosts names of the hosts of the dispatchers
   * @param ports the dispatcher ports listening for JMS requests
   * @param userName user name for authentication purposes
   * @param password password password for authentication purposes
   */
  public ConnectionFactory(String[] hosts, int[] ports, String serverInstance, String userName, String password, String systemID, String hardwareID) {
    this(hosts, ports, userName, password, systemID, hardwareID);
    this.serverInstance = serverInstance;
  }

  /**
   * Constructor ConnectionFactory.
   * @param hosts names of the hosts of the dispatchers
   * @param ports the dispatcher ports listening for JMS requests
   */
  public ConnectionFactory(String[] hosts, int[] ports, String systemID, String hardwareID) {
    if (hosts.length != ports.length) {
      throw new IllegalArgumentException("The number of hosts does not match the number of ports.");
    }
    
    this.hardwareID = hardwareID;
    this.systemID = systemID;
    
    for (int i = 0; i < hosts.length; i++) {
      this.hosts.add(hosts[i]);
      this.ports.add(new Integer(ports[i]));
    }
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

  /**
   * Method setDefaultCredentials. Sets the userName and password which will
   * be used with the createConnection() method.
   * @param userName user name
   * @param password password
   */   
  public void setDefaultCredentials(String userName, String password) {
    this.userName = userName;
    this.password = password;
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
    String systemPropUser = null;
    String systemPropPassword = null;
    
    try {
       systemPropUser = System.getProperty(PROPERTY_JMS_USER);
       systemPropPassword = System.getProperty(PROPERTY_JMS_PASSWORD);    
    } catch (Exception e) { //$JL- This situation is normal and should not be logged or traced$
      return  createConnection(userName, password, connectionType);
    }

    if (systemPropUser != null && systemPropPassword != null) {
      userName = systemPropUser;
      password = systemPropPassword;
    }
    
    return  createConnection(userName, password, connectionType);
  }
  
  /**
   * Method createConnection. Creates a connection.
   * @param userName the caller's user name
   * @param password the caller's password
   * @param connectionType type of the connection (queue, topic, generic)
   * @return Connection the created connection
   * @throws JMSException thrown if internal error occurs
   */
  public javax.jms.Connection createConnection(String username, String passwd, ConnectionType connectionType) throws javax.jms.JMSException {   

    logService = LogServiceImpl.getLogService(LogServiceImpl.CLIENT_LOCATION);
    ServerComponentInterface serverComponentInterface = ServerComponentAccessor.getServerComponentInterface();    

    if (serverComponentInterface == null && !isLogInitialized) {
        File logProperties = new File(System.getProperty("user.home") + "/" + LOG_PROPERTIES_FILE);
          
        if (logProperties.exists()) {
          PropertiesConfigurator logConfigurator = new PropertiesConfigurator(logProperties);
          logConfigurator.configure();
        } else {
          String logFile = System.getProperty("user.home") + "/" + LOG_FILE;
          Properties properties = new Properties();
          properties.put("com.sap.jms.severity", "ERROR");
          properties.put("log[Trace]", "FileLog");
          properties.put("log[Trace].pattern", logFile);
          properties.put("com.sap.jms.logs", "+log[Trace]");
          PropertiesConfigurator logConfigurator = new PropertiesConfigurator(properties);
          logConfigurator.configure();
        }
        
        isLogInitialized = true;
    }

    //  this check should be after the init of the logging
    if (isPasswordFieldBringsPassword && (null == username || "".equals(username))) {
        JMSException jmse= new JMSException("The username cannot be an empty string or null. There is no such user!");
        logService.exception(LogService.ERROR, LOG_COMPONENT, jmse);
        throw jmse;
    }  
      
    Object[] responseAndAdapter = makeConnection(username, passwd);

    ConnectionCreateResponse response = (ConnectionCreateResponse) responseAndAdapter[0];
    NetworkAdapter adapter = (NetworkAdapter) responseAndAdapter[1];
    Connection connection = null;
    
    if (connectionType == ConnectionType.GENERIC_CONNECTION) {
        connection = new Connection(response.getConnectionID(), serverInstance, adapter, threadSystem);
      } else if (connectionType == ConnectionType.QUEUE_CONNECTION) {
        connection = new QueueConnection(response.getConnectionID(), serverInstance, adapter, threadSystem);
      } else if (connectionType == ConnectionType.TOPIC_CONNECTION) {
        connection = new TopicConnection(response.getConnectionID(), serverInstance, adapter, threadSystem);
      } else if (connectionType == ConnectionType.XA_GENERIC_CONNECTION) {
    	  connection = new JMSXAConnection(response.getConnectionID(), serverInstance, adapter, threadSystem);
      } else if (connectionType == ConnectionType.XA_QUEUE_CONNECTION) {
    	  connection = new JMSXAQueueConnection(response.getConnectionID(), serverInstance, adapter, threadSystem);
      } else if (connectionType == ConnectionType.XA_TOPIC_CONNECTION) {
    	  connection = new JMSXATopicConnection(response.getConnectionID(), serverInstance, adapter, threadSystem);
      }
    
     checkProtocolVersion((ConnectionCreateResponse)response,connection);

     logService.infoTrace(LOG_COMPONENT, "JMS connection created:\n{0}", connection);
    
    return connection;
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
    int clientVersion = connectionCreateResponse.getClassCompatibilityVersion();
      
    //the one present from the packet.
    int serverVersion = connectionCreateResponse.getProtocolCompatibilityVersion();
    
    if (clientVersion != serverVersion) {
      logService.errorTrace(LOG_COMPONENT, "checkProtocolVersion", 
    		  "Could not create JMS connection because of incompatible version. Server version is : "+serverVersion+" , client version is : "+clientVersion);
      
      try {
          connection.close();
      } catch (JMSException jmsException) {
          logService.exception(LOG_COMPONENT, jmsException);
      }

      JMSException jmsException = new JMSException("Incompatible protocol version. (Server version = "+ serverVersion +", Client version = " + clientVersion+"). Please upgrade to the same patch level.");
      logService.exception(LOG_COMPONENT,  jmsException);
      throw jmsException;
    }
  }
  /**
   * Method sendConnectionCreateRequest. Sends a request to the server to create a 
   * connection.
   * @param userName user name
   * @param password password
   * @param socket the socket through which the request will be sent
   * @return ConnectionCreateResponse the response from the server
   * @throws JMSException thrown if an error occurs 
   */
  private ConnectionCreateResponse sendConnectionCreateRequest(String userName, String password, NetworkAdapter adapter) throws javax.jms.JMSException {
    logService.path(LOG_COMPONENT ,"Enter sendConnectionCreateRequest()");
    PacketImpl response = null; 
    
    try {
      response = (PacketImpl)adapter.sendAndWait(new ConnectionCreateRequest(serverInstance, userName, password, factoryName));
    } catch (java.io.IOException ioe) {
      logService.exception(LogService.FATAL, LOG_COMPONENT, ioe);
      JMSException jmse = new javax.jms.JMSException("Cannot send connection create request.");
      jmse.initCause(ioe);
      jmse.setLinkedException(ioe);
      logService.path(LOG_COMPONENT ,"Exit sendConnectionCreateRequest()");
      throw jmse;
    }
    
    checkForException(response);
    logService.path(LOG_COMPONENT ,"Exit sendConnectionCreateRequest()");
    return (ConnectionCreateResponse) response;
    
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
      logService.exception(LogService.FATAL, LOG_COMPONENT, jmse);
      throw jmse;
    }    
  }
  
  private void randomizeDispatcherSequence(int size) { 
    if (dispatcherSequence == null) {
      dispatcherSequence = new int[size];      
      LinkedList available = new LinkedList(size);
      RandomGenerator generator = new RandomGenerator();
      
      for (int i = 0; i < size; i++) {
        available.add(new Integer(i));
      }
      
      for (int i = 0; i < size; i++) {
        dispatcherSequence[i] = ((Integer)available.remove(generator.nextInt() % available.size())).intValue();
      }
    }
  }

  private String getHostPortList() {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < dispatcherSequence.length; i++) {
          sb.append(hosts.get(i));
          sb.append(':');
          sb.append(ports.get(i));
          if (i != (dispatcherSequence.length - 1)) {
              sb.append(", ");
          }
    }
    
    return sb.toString();
  }
  
  private synchronized static final ThreadSystem getClientThreadPool(int initialPoolSize, int maxPoolSize) {
    if (threadSystem == null) {
      threadSystem = new ThreadPool(initialPoolSize, maxPoolSize);
    }
    
    return threadSystem;
  }
  
  protected void setIsPasswordFieldBringsPassword(boolean flag) {
      this.isPasswordFieldBringsPassword = flag;
  }
  
  /**
   * Tries to create a new Connection.
   * In most of the cases when it is on the server virtual machine (deployed client) a ServerAdapter will be used
   * If it is on the client virtual machine or on the server virtual machine, but the server node has no
   * running JMS service will use all available dispatchers until get a proper ConnectionCreateResponse 
   * 
   * @return Object[] - an array of returned ConnectionCreateResponse and used NetworkAdapter
   * @throws JMSException 
   */
  private Object[] makeConnection(String username, String passwd) throws JMSException {
  	
	  Object[] responseAndAdapter = null;
	  ServerComponentInterface serverComponentInterface = ServerComponentAccessor.getServerComponentInterface();

	  /*
	   * As this class is used only to connect to NW04(s) this check most likely will result to false
	   * i.e. it will use makeConnectionOnClientVM method 
	   */
	  
	  if (serverComponentInterface != null && hardwareID != null && hardwareID.equals(serverComponentInterface.getHardwareID()) && systemID != null && systemID.equals(serverComponentInterface.getSystemID())) {
		  // we are on the server node
      
		  try {
			  responseAndAdapter = makeConnectionOnServerVM(username, passwd);
		  } catch (JMSException jmsx) {
		          logService.warningTrace(LOG_COMPONENT, 
		                  "Could not create a JMS Connection on this server node. Will try to create one on another server node via dispatchers.");
		          logService.exception(LogService.WARNING, LOG_COMPONENT, jmsx);
			  responseAndAdapter = makeConnectionOnClientVM(username, passwd);
		  }
      
	  } else {//the session container is not on this JVM
    	
		  responseAndAdapter = makeConnectionOnClientVM(username, passwd);
		  
	  }

	  return responseAndAdapter;
  }

  /**
   * Tries to create a JMS connection on server virtual machine
   * 
   * @see makeConnection(String, String)
   */
  private Object[] makeConnectionOnServerVM(String username, String passwd) throws JMSException {

	  NetworkAdapter adapter = null;
	  ConnectionCreateResponse response = null;

	  ServerComponentInterface serverComponentInterface = ServerComponentAccessor.getServerComponentInterface();
	  
	  try {
	      try {
	        Class serverClientAdapter = Class.forName("com.sap.jms.server.ServerClientAdapter");
	        Constructor constructor = serverClientAdapter.getConstructor(new Class[] {Object.class});
	        adapter = (NetworkAdapter) constructor.newInstance(new Object[] {null});
	      } catch (ClassNotFoundException cnfe) {
	        JMSException jmse= new JMSException("Failed to create connection.");
	        logService.exception(LogService.FATAL, LOG_COMPONENT, cnfe);
	        throw jmse;
	      }

	  } catch (Exception x) {
	      logService.exception(LogService.ERROR, LOG_COMPONENT, x);
		  JMSException jmse= new JMSException("Failed to create connection.");
		  throw jmse;
	  }

	  adapter.setThreadSystem(serverComponentInterface.getThreadSystem());
	  threadSystem = serverComponentInterface.getThreadSystem();

	  response = sendConnectionCreateRequest(username, passwd, adapter);

	  return new Object[] {response, adapter};
  }

  /**
   * Tries to create a JMS connection on client virtual machine (or on the server virtual machine when 
   * could not create one on current server node) via dispatchers 
   * 
   * @see makeConnection(String, String)
   */
  private Object[] makeConnectionOnClientVM(String username, String passwd) throws JMSException {

	  NetworkAdapter adapter = null;
	  ConnectionCreateResponse response = null;

	  ServerComponentInterface serverComponentInterface = ServerComponentAccessor.getServerComponentInterface();
	  
	  randomizeDispatcherSequence(hosts.size());

	  /*
	   * try with all dispatchers - first try to connect the dispacther and if there are no exceptions
	   * try to send the first packet to the server node
	   * if the response is good -> return it, otherwise try with the next dispatcher
	   */
	  for (int i = 0; i < dispatcherSequence.length; i++) {
		  try {
			  adapter = new SocketWrapper((String)hosts.get(i), ((Integer) ports.get(i)).intValue());

			  if (serverComponentInterface != null && serverComponentInterface.getThreadSystem() != null) {
				  threadSystem = serverComponentInterface.getThreadSystem();
			  } else {
				  threadSystem = ConnectionFactory.getClientThreadPool(initialPoolSize, maxPoolSize);
			  }

			  adapter.setThreadSystem(threadSystem);
			  threadSystem.startThread(adapter, false);

			  logService.debug(LOG_COMPONENT, "Adapter that will be used = {0}", new Object[]{adapter});    
			  
			  response = sendConnectionCreateRequest(username, passwd, adapter);

			      logService.debug(LOG_COMPONENT, 
			          "Dispatcher with address {0}:{1} is used to create connection {2}",
			          new Object[] {
			          	(String) hosts.get(i),
			          	((Integer) ports.get(i)),
			          	response
			          }
			      );
          
			  break; // don't try anymore, we got a good ConnectionCreateResponse

		  } catch (IOException iox) {
			  // the dispatcher is not available, so try with next one
			  logService.debug(LOG_COMPONENT,
					  "Could not connect to dispatcher with IP address = {0} and port = {1}",
					  new Object[] {
					  (String) hosts.get(i),
					  ((Integer) ports.get(i))
			  }

			  );
			  logService.exception(LogService.DEBUG, "com.sap.jms.client.connection.ConnectionFactory.makeConnectionOnClientVM()", iox);
			  continue;
		  } catch (JMSSecurityException jmsSecEx) {
			  // this exception is being thrown when the passed username and password are not valid
			  throw jmsSecEx;
		  } catch (JMSException jmsx) {
			  // probably there is no JMS service running on the server nodes behind this dispatcher
			  logService.debug(LOG_COMPONENT,
					  "Could not create JMS connection using dispatcher with IP address = {0} and port = {1}",
					  new Object[] {
					  (String) hosts.get(i),
					  ((Integer) ports.get(i))
			  }
			  );
			  logService.exception(LogService.DEBUG, LOG_COMPONENT, jmsx);
			  continue;
		  }
	  }

	  if (response == null) {
		  // all attempts failed
		  String triedHosts = getHostPortList();
		  JMSException jmse= new JMSException("The following hosts" + triedHosts +" cannot be accessed:");
		  logService.errorTrace(LOG_COMPONENT, "The following hosts (hostname:port) could not be reached: {0} - Please check host names and ports.", new Object[] {triedHosts});
		  logService.exception(LogService.ERROR, LOG_COMPONENT, jmse);
		  throw jmse;
	  }

	  return new Object[] {response, adapter};
  }

}
