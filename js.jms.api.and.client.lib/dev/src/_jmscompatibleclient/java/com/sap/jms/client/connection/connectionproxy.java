/*
 * Created on 2004-11-10
 *
 */
package com.sap.jms.client.connection;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Properties;
import javax.jms.JMSException;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.client.session.ThreadPool;
import com.sap.jms.server.ServerComponentAccessor;
import com.sap.jms.server.ServerComponentInterface;
import com.sap.jms.server.remote.JMSRemoteServer;

import com.sap.jms.util.logging.LogService;
import com.sap.jms.util.logging.LogServiceImpl;
import com.sap.tc.logging.PropertiesConfigurator;

/**  
 * @author Desislav Bantchovski
 * @version 7.10
 *  
 * The instances of this class acts as a TransportObjects between the
 * provider and the client, because the class com.sap.jms.client.connection.Connection
 * is not Serializable. We use ConnectionProxy to serialize it anyway and to send it
 * to the client.
 * @see java.io.Serializable
 * private void writeObject(java.io.ObjectOutputStream out) throws IOException;
 * private void readObject(java.io.ObjectInputStream in)    throws IOException, ClassNotFoundException; 
 */

public class ConnectionProxy implements java.io.Serializable {
	public static final long serialVersionUID = -2551260815420919458L;

	private String type;	
	private long connectionID = -1;
	private String serverInstance;
	private JMSRemoteServer server; //$JL-SER$
	private String clientID; 
	private static final int DEFAULT_MAX_POOL_SIZE = 128;
	private static final int DEFAULT_INITIAL_POOL_SIZE = 5;	
	private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;
	private int initialPoolSize = DEFAULT_INITIAL_POOL_SIZE;	
	private transient static ThreadSystem pool;
	private transient LogService logService;
	private static final transient String LOG_COMPONENT = "connection.connectionProxy";	
	private static final transient String LOG_PROPERTIES_FILE = "logging.properties";
	private static final transient String LOG_FILE = "sapjms.log";
	private static transient boolean isLogInitialized = false;

	private boolean supportsOptimization = false;

	public ConnectionProxy(String type, long connectionID, String serverInstance, JMSRemoteServer server, String clientID, boolean supportsOptimization) {
		this.type = type;
		this.connectionID = connectionID;
		this.serverInstance = serverInstance;
		this.server = server;
		this.clientID = clientID;
		this.supportsOptimization = supportsOptimization;
	}

	/**
	 * Creates and initialized RemoteConnection during deserialization.
	 * @return RemoteConnection.
	 * @throws java.io.ObjectStreamException if problem during serialization occures.
	 * @throws JMSException if the connection can not be created.
	 */
	public Object readResolve() throws java.io.ObjectStreamException, JMSException {
		ThreadSystem threadSystem = null;		
		RemoteConnection connection = null;

		logService = LogServiceImpl.getLogService(LogServiceImpl.CLIENT_LOCATION);		

		ServerComponentInterface serverComponentInterface = ServerComponentAccessor.getServerComponentInterface();			

		if (serverComponentInterface != null) {
			threadSystem = serverComponentInterface.getThreadSystem();			
		} else {
			setupLocalLog();
		}
		if (threadSystem == null) {
			threadSystem = getClientThreadPool(initialPoolSize, maxPoolSize);		  
		}								
		logService.debug(LOG_COMPONENT, "ThreadSystem = {0}", new Object[] {threadSystem});				             		

		// TODO EE creates a client side RemoteConnection according to the properties 
		// which were sent to the client by the server copy of this RemoteConnection.  
		// threadSystem is initialised here. 
		connection = instantiate(type, new Object[] { new Long(connectionID), serverInstance, server, threadSystem, clientID, new Boolean(supportsOptimization) } );

		if (connection == null) {       	
			JMSException e = new JMSException("Failed to create connection.");
			logService.exception(LogService.ERROR, LOG_COMPONENT, e);
			throw e;  
		}        
		return connection;
	}

	private synchronized static final ThreadSystem getClientThreadPool(int initialPoolSize, int maxPoolSize) {	
		if (pool == null) { 	
			pool = new ThreadPool(initialPoolSize, maxPoolSize);  	    
		}
		return pool;
	}	

	static Class[] arguments = new Class[] { long.class, String.class, JMSRemoteServer.class, ThreadSystem.class, String.class };
	static Class[] argumentsEx = new Class[] { long.class, String.class, JMSRemoteServer.class, ThreadSystem.class, String.class, boolean.class };

	/**
	 * Creates new instances ot particular connection type. 
	 * @param type the class name of connection to be created.
	 * @param data 
	 * @return new RemoteConnection
	 */
	protected RemoteConnection instantiate(String type, Object[] data) {
		RemoteConnection instance = null;	 	
		try {
			Class definition = Class.forName(type);
			Constructor/*<RemoteConnection>*/ constructor = definition.getConstructor(argumentsEx); // by default all old clients do not support optimization
			instance = (RemoteConnection) constructor.newInstance(data);
		} catch (Exception e) {
			logService.exception(LOG_COMPONENT, e);
		}
		return instance;		
	}

	/**
	 * Setups the local log. 
	 * @see com.sap.jms.client.connection.createConnection(String userName, String password, byte connectionType)
	 */

	private void setupLocalLog() {
		if (!isLogInitialized) {
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
	}
}
