package com.sap.jms.client.rmi;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import java.rmi.Remote;
import javax.jms.JMSException;

import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.P4RuntimeException;
import com.sap.jms.client.connection.Connection;
import com.sap.jms.client.connection.ClientFacade;
import com.sap.jms.client.connection.ConnectionFactoryInterface;
import com.sap.jms.client.connection.ConnectionData;
import com.sap.jms.client.connection.ServerFacade;
import com.sap.jms.client.connection.LocalClientFacade;
import com.sap.jms.client.connection.QueueConnection;
import com.sap.jms.client.connection.TopicConnection;
import com.sap.jms.client.connection.JMSXAConnection;
import com.sap.jms.client.connection.JMSXAQueueConnection;
import com.sap.jms.client.connection.JMSXATopicConnection;
import com.sap.jms.client.connection.Connection.ConnectionType;

import com.sap.jms.protocol.PacketImpl;
import com.sap.jms.server.ServerComponentAccessor;
import com.sap.jms.server.ServerComponentInterface;
import com.sap.jms.util.StandaloneTaskManager;
import com.sap.jms.util.Logging;
import com.sap.jms.util.TaskManager;
import com.sap.tc.logging.PropertiesConfigurator;
import com.sap.tc.logging.Severity;

/**
 * @author Desislav Bantchovski
 * @version 7.30 
 */

public class RMIConnectionFactory implements javax.jms.ConnectionFactory, Serializable {
	
	private static final transient String PROPERTY_JMS_USERNAME = "jms.user";
	private static final transient String PROPERTY_JMS_PASSWORD = "jms.password";
	
	private RMIConnectionFactoryInterface connectionFactoryInterface = null;
	private String vpName = null;	
	private String clientId = null;
	private boolean supportsOptimization = false;
	String connectionFactoryName = null;	
	String redirectableKey = null;
	
	private static final int DEFAULT_MAX_POOL_SIZE = 128;
	private static final int DEFAULT_INITIAL_POOL_SIZE = 5;		
	private static final String LOG_PROPERTIES_FILE = "logging.properties";
	private static final String LOG_FILE = "sapjms.log";
	
	private transient static TaskManager standaloneTaskManager;
	private static ServerComponentInterface serverComponentInterface = null;
	
	static {
		serverComponentInterface = ServerComponentAccessor.getServerComponentInterface();
		if (serverComponentInterface == null) {
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
		}
	}
	
	public static TaskManager getStandaloneTaskManager() {
		if (standaloneTaskManager == null) {
			synchronized (RMIConnectionFactory.class) {
				if (standaloneTaskManager == null) {
					Map taskManagerProperties = new HashMap();
					standaloneTaskManager = new StandaloneTaskManager(taskManagerProperties);
					standaloneTaskManager.start();					
				}
			}        
		}		
		return standaloneTaskManager;
	}
	
	public RMIConnectionFactory(RMIConnectionFactoryInterface connectionFactoryInterface, String vpName, String clientId, 
			boolean supportsOptimization, String connectionFactoryName, String redirectableKey) {
		this.connectionFactoryInterface = connectionFactoryInterface;
		this.vpName = vpName; 
		this.clientId = clientId; 
		this.supportsOptimization = supportsOptimization;
		this.connectionFactoryName = connectionFactoryName;	
		this.redirectableKey = redirectableKey;
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
	public javax.jms.Connection createConnection(String username, String password) throws javax.jms.JMSException {
		return createConnection(username, password, ConnectionType.GENERIC_CONNECTION);
	}
	
	public boolean supportsOptimization() {
		return supportsOptimization;
	}
	
	public boolean isLocal() {
		// two alternative implementations 	
		boolean result = getLocalConnectionFactoryInterface(vpName) != null; // && isLocal(connectionFactoryInterface);
		return result;
	}
	
	public static ConnectionFactoryInterface getLocalConnectionFactoryInterface(String vpName) {
		ConnectionFactoryInterface connectionFactoryInterface = null;
		if (serverComponentInterface != null) {
			connectionFactoryInterface = serverComponentInterface.getLocalConnectionFactoryInterface(vpName);
		}
		return connectionFactoryInterface;
	}
	
	public boolean isLocal(Remote stub) {
		boolean result = false;		
		try {
			result = P4ObjectBroker.init().isP4Stub(stub) && P4ObjectBroker.init().isLocal(stub);
		} catch(Exception e) {
			Logging.exception(this, e);
		}
		if (Logging.isWritable(this, Severity.DEBUG)) {		
			Logging.log(this, Severity.DEBUG, "isLocal = " + result);
		}
		return result;
	}
	
	public String getRedirectebleKey() {
		return redirectableKey;
	}	
	
	protected javax.jms.Connection createConnection(ConnectionType type) throws javax.jms.JMSException {
		String username = System.getProperty(PROPERTY_JMS_USERNAME);
		String password = System.getProperty(PROPERTY_JMS_PASSWORD);
		return createConnection(username, password, type);
	}	

	protected javax.jms.Connection createConnection(String username, String password, ConnectionType type) throws javax.jms.JMSException {
		ConnectionData connectionInfo;
		ClientFacade clientFacade;
		boolean local = false;
		
		try {
			byte[] certificate = null;
			local = isLocal(); 
			if (local) {
				clientFacade = new LocalClientFacade();
				connectionInfo = getLocalConnectionFactoryInterface(vpName).connectionCreate(username, password, type, vpName, certificate, connectionFactoryName, clientFacade, local);				
			} else {
				clientFacade = new RMIClientFacadeImpl();
				connectionInfo = connectionFactoryInterface.connectionCreate(username, password, type, vpName, certificate, connectionFactoryName, clientFacade, local);				
			}
		} catch (P4RuntimeException e) {
			Logging.exception(this, e);
			JMSException x = new JMSException("Could not allocate JMS connection due to RMI error.");
			x.setLinkedException(e);
			throw x;
		}
		if (connectionInfo == null) {
			throw new JMSException("Could not allocate JMS connection.");		
		}
		checkProtocolVersion(connectionInfo.getServerVersion());
		TaskManager taskManager = serverComponentInterface != null ? serverComponentInterface.getTaskManager() : getStandaloneTaskManager(); 
		Connection connection = createConnection(connectionInfo, type, clientFacade, taskManager);
		if (local) {
			((LocalClientFacade) clientFacade).setClientFacade(connection);			
		} else {
			((RMIClientFacadeImpl) clientFacade).setClientFacade(connection);			
		}
		return connection;
	}
	
	private Connection createConnection(ConnectionData connectionInfo, ConnectionType type, ClientFacade clientFacade, TaskManager taskManager) {
		Connection connection = null;
		
		long connectionId = connectionInfo.getConnectionId();
		ServerFacade serverFacade = connectionInfo.getServerFacade();
		
		switch (type) {
			case GENERIC_CONNECTION: {				
				connection = new Connection(connectionId, serverFacade, clientFacade, vpName, clientId, taskManager, supportsOptimization);
				break;
			}
	
			case QUEUE_CONNECTION: {
				connection = new QueueConnection(connectionId, serverFacade, clientFacade, vpName, clientId, taskManager, supportsOptimization);
				break;
			}
	
			case TOPIC_CONNECTION: {
				connection = new TopicConnection(connectionId, serverFacade, clientFacade, vpName, clientId, taskManager, supportsOptimization);
				break;
			}
	
			case XA_GENERIC_CONNECTION: {
				connection = new JMSXAConnection(connectionId, serverFacade, clientFacade, vpName, clientId, taskManager, supportsOptimization);
				break;
			}
	
			case XA_QUEUE_CONNECTION: {
				connection = new JMSXAQueueConnection(connectionId, serverFacade, clientFacade, vpName, clientId, taskManager, supportsOptimization);
				break;
			}
	
			case XA_TOPIC_CONNECTION: {
				connection = new JMSXATopicConnection(connectionId, serverFacade, clientFacade, vpName, clientId, taskManager, supportsOptimization);
				break;
			}
		}	
		return connection;
	}
	
	private void checkProtocolVersion(int serverVersion) {
		//this code is executed in the client VM, we can take as version the one from the ConnectionCreateResponse class
		int clientVersion = PacketImpl.getClassCompatibilityVersion();

		if (clientVersion != serverVersion) {
			if (Logging.isWritable(this, Severity.ERROR)) {
				Logging.log(this, Severity.ERROR, "Could not create JMS connection because of incompatible version. Server version is :",serverVersion,", client version is : ",clientVersion);
			}
		}
	}		
}
