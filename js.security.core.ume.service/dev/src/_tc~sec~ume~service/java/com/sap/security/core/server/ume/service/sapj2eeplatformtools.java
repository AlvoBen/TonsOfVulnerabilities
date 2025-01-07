package com.sap.security.core.server.ume.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;
import java.util.Iterator;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.sql.DataSource;

import com.sap.engine.frame.ServiceContext;
import com.sap.engine.frame.cluster.ClusterContext;
import com.sap.engine.frame.cluster.ClusterException;
import com.sap.engine.frame.cluster.message.ListenerAlreadyRegisteredException;
import com.sap.engine.frame.cluster.message.MessageAnswer;
import com.sap.engine.frame.cluster.message.MessageContext;
import com.sap.engine.frame.cluster.message.MessageListener;
import com.sap.engine.frame.cluster.monitor.ClusterMonitor;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.connector.ResourceObjectFactory;
import com.sap.security.api.UMException;
import com.sap.security.api.umap.IUserMapping;
import com.sap.security.api.util.IUMParameters;
import com.sap.security.core.persistence.datasource.InitializationException;
import com.sap.security.core.tools.PerformanceTracer;
import com.sap.security.core.umap.imp.UserMapping;
import com.sap.security.core.util.IClusterMessageListener;
import com.sap.security.core.util.IPlatformTools;
import com.sap.security.core.util.config.IUMConfigFull;
import com.sap.security.core.util.config.UMConfigurationException;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class SAPJ2EEPlatformTools implements IPlatformTools {

    private static final String M_GET_DB_CONNECTION_POOL        = "getDBConnectionPool(String, boolean)";
    private static final String M_HAS_FULL_CRYPTO               = "hasFullCryptoVersion()";
    private static final String M_INIT_USER_MAPPING             = "initializeUserMapping()";
    private static final String M_PREPARE_KEYSTORE_API_LISTENER = "prepareKeystoreApiListener()";

    private static final String JNDI_DBPOOL_PREFIX     = "jdbc/notx/";
    private static final String KEYSTORE_API_INTERFACE = "keystore_api";
    
    private static final Location _loc = Location.getLocation(SAPJ2EEPlatformTools.class);
    private static final Category _cat = Category.getCategory(Category.SYS_SECURITY, "Usermanagement");
    private static PerformanceTracer mPerfTracer = PerformanceTracer.getInstance();

    
    private ServiceContext        _serviceContext      = null;
    private UMEServiceFrame       _umeServiceFrame     = null;
    private KeyStoreEventListener _keystoreApiListener = null;
    private SAPJ2EEConfiguration  _configuration       = null;
	private ClusterContext 		  _cctx       		   = null;
	private MessageContext 		  _mctx       		   = null;
	private Collection            _registeredListeners =  null;
	private int me;
    
    // Private default constructor
    private SAPJ2EEPlatformTools() { /* Must not be used. */ }
    
    public SAPJ2EEPlatformTools(UMEServiceFrame umeServiceFrame) throws UMConfigurationException {
    	final String mn = "SAPJ2EEPlatformTools.<init>";
    	_umeServiceFrame = umeServiceFrame;
        _serviceContext = umeServiceFrame.getServiceContext();
        _configuration = new SAPJ2EEConfiguration(_serviceContext);

        /* init cluster communication */
        _registeredListeners = new LinkedList();
        _cctx = umeServiceFrame.getServiceContext().getClusterContext();
		ClusterMonitor cm = _cctx.getClusterMonitor();
		me = cm.getCurrentParticipant().getClusterId();
		if (_loc.beInfo())
		{
			_loc.infoT(mn, "Instance ID: " + me);        
		}
        _mctx = _cctx.getMessageContext();
		try 
		{
	        _mctx.registerListener(new Listener());
		} 
		catch (ListenerAlreadyRegisteredException ae) 
		{
			LoggingHelper.traceThrowable(Severity.ERROR, _loc, mn, ae);
		}
    }

    /**
     * Clean up all used resources.
     */
    public void stop() {
    	_configuration.stop();
    	_mctx.unregisterListener();
    }

    public int getPlatformType() {
        return PLATFORM_TYPE_J2EE;
    }

    public String getSID() {
        return _serviceContext.getCoreContext().getCoreMonitor()
            .getManagerRuntimeProperty("ClusterManager", "___MS_PROP_GET_SYSTEM_ID");
    }

    public boolean supportsClientConcept() {
        return false;
    }

    public boolean supportsAnonymousUsers() {
        return true;
    }

    public String getClient() throws UMException {
        return DEFAULT_CLIENT;
    }
    
    public boolean orgRestrictionsEnabled()
    {
    	//check A1S profile parameter
    	return false;
    }

    public boolean isA1SSystem()
    {
    	return DATASOURCE_CONFIGURATION_A1S
				.equals(_configuration
						.getStringStatic(IUMParameters.UME_PERSISTENCE_DATA__SOURCE__CONFIGURATION));
    }

    public String[] getClients() throws UMException {
        return new String[] { DEFAULT_CLIENT };
    }

    public boolean hasNamingService() {
        return true;
    }

    public DataSource getDBConnectionPool(String datasource, boolean jtaTransactionSupported) throws UMException {
        try {
            Hashtable parms = new Hashtable();

            // Check datasource string for backward compatibility
            if (datasource.startsWith(JNDI_DBPOOL_PREFIX)) {
                datasource = datasource.substring(JNDI_DBPOOL_PREFIX.length());
            }

            ResourceObjectFactory rFactory = new ResourceObjectFactory();

            Reference notxref = new Reference(
                "com.sap.engine.services.dbpool.cci.ConnectionFactory",
                "com.sap.engine.services.connector.ResourceObjectFactory",
                "service:connector"
            );
            notxref.add(new StringRefAddr("res-type"     , "javax.sql.DataSource"));
            // used by PM, not by the application - "Application" for optimization
            notxref.add(new StringRefAddr("res-auth"     , "Application"));
            notxref.add(new StringRefAddr("sharing-scope", "Shareable"  ));
            notxref.add(new StringRefAddr("res-name"     , datasource   ));
            notxref.add(new StringRefAddr("tx-support"   , "true"       ));

            DataSource dbDatasource = (DataSource) rFactory.getObjectInstance(notxref, null, null, parms);

            if(dbDatasource == null) {
                _cat.errorT(_loc, "Cannot access system database. Check availability of the system database and the configuration of the system connection pool.");
                String msg = MessageFormat.format(
                    "Cannot get database connection pool ''{0}''.",
                    new Object[] { datasource }
                );
                _loc.errorT(M_GET_DB_CONNECTION_POOL, msg);
                throw new InitializationException(msg);
            }
            else {
                if(_loc.beInfo()) {
                    String msg = MessageFormat.format(
                        "Datasource ''{0}'' found.", new Object[] { datasource }
                    );
                    _loc.infoT(M_GET_DB_CONNECTION_POOL, msg);
                }
                return dbDatasource;
            }
        }
        catch(Exception e) { //$JL-EXC$
            _cat.errorT(_loc, "Cannot access system database. Check availability of the system database and the configuration of the system connection pool. Detailed information is available in the trace file.");
            String msg = MessageFormat.format(
                "Error while trying to get database connection pool ''{0}''.",
                new Object[] { datasource }
            );
            _loc.traceThrowableT(Severity.ERROR, M_GET_DB_CONNECTION_POOL, msg, e);
            throw new InitializationException(e, msg);
        }
    }

    public void initializeTicket() {
        // TODO Remove this method after it has been removed from the interface.
        // Reason: BufferingTicket is initialized by security service now.
    }

    public void initializeUserMapping() {
        _loc.infoT(M_INIT_USER_MAPPING, "Initialization of UME user mapping component.");

        // Do the whole stuff only if we need a user mapping key,
        // i.e. if IAIK crypto lib is available
        if(hasFullCryptoVersion()) {
            _loc.infoT(M_INIT_USER_MAPPING, "Strong cryptography is available on this " +
                "system. Registering for notification when keystore service is available " +
                "to read the main cryptographic key for user mapping encryption.");

            // We can't retrieve the user mapping master key from the AS Java keystore
            // service when UME Service starts up because the keystore service has not been
            // initialized yet. So we register to get notified when the service is ready
            // and retrieve the key then.
            prepareKeystoreApiListener();
            _keystoreApiListener.enableUserMappingKeyHandling();

            // UserMapping.initialize(...) is called in the event listener
            // after having retrieved / migrated / generated a master key.
        }
        else {
            _loc.infoT(M_INIT_USER_MAPPING, "Skipping user mapping key retrieval / migration / " +
                "generation because strong cryptography is not available on this system.");

            UserMapping.initialize((SecretKey) null);
        }

        _loc.exiting();
    }

    private boolean hasFullCryptoVersion() {
        String umapCryptoAlgo = System.getProperties().getProperty(IUserMapping.UMAP_ENCRYPTION_ALGO_ALIAS, "DESede");

        try {
            Cipher.getInstance(umapCryptoAlgo);
            return true;
        }
        catch(GeneralSecurityException e) {
            String msg = "Full version of JCE provider is not available.";
            _loc.traceThrowableT(Severity.DEBUG, M_HAS_FULL_CRYPTO, msg, e);
            return false;
        }
    }

    private synchronized void prepareKeystoreApiListener() {
        // This needs to be done only once although the listener is used to initialize
        // both the user mapping master key and the TicketKeystore
        if(_keystoreApiListener != null) return;

        // Register only for "keystore service is available" event
        int eventMask = ContainerEventListener.MASK_INTERFACE_AVAILABLE;

        Set interfaceNames = new HashSet();
        interfaceNames.add(KEYSTORE_API_INTERFACE);

        // Create the listener and remember it because we need to tell it whether
        // it should handle the user mapping master key, the TicketKeystore or both.
        _keystoreApiListener =
            new KeyStoreEventListener(_configuration, _serviceContext.getCoreContext().getLockingContext());

        // Register the listener
        _umeServiceFrame.addEventListener(eventMask, interfaceNames, _keystoreApiListener);

        if(_loc.beInfo()) {
            String msg = MessageFormat.format(
                "Registered for notification as soon as keystore service (interface " +
                "''{0}'') is available. The listener will then initialize the user " +
                "mapping and SAP logon ticket functionalities of UME as required.",
                new Object[] { KEYSTORE_API_INTERFACE }
            );
            _loc.infoT(M_PREPARE_KEYSTORE_API_LISTENER, msg);
        }
    }

    /* (non-Javadoc)
     * @see com.sap.security.core.util.IPlatformTools#getConfiguration()
     */
    public IUMConfigFull getConfiguration() {
        return _configuration;
    }
    
	private byte[] map(Serializable message) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(message);
			oos.close();
			return baos.toByteArray();
		} catch (IOException io) {
			String mn = "IPlatformTools.map(Serializable message)";
			_cat.warningT(_loc, "Cannot serialize object " + message);
			LoggingHelper.traceThrowable(Severity.ERROR, _loc, mn, io);
			return null;
		}
	}
	
	private Serializable map(byte[] data, int offset, int length) {
		try {
			ByteArrayInputStream bais =
				new ByteArrayInputStream(data, offset, length);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Serializable message = (Serializable) ois.readObject();
			ois.close();
			return message;
		} catch (Exception e) {
			String mn = "IPlatformTools.map(byte[] data, int offset, int length)";
			_cat.warningT(_loc, "Cannot deserialize object");
			LoggingHelper.traceThrowable(Severity.ERROR, _loc, mn, e);
			return null;
		}
	}
	

    /**
     * Serializes the serializable object and sends it to all cluster nodes.
     * @param message The serializable object that represents the message.
     */
    public void sendClusterMessage(Serializable message)
    {
    	final String mn = "sendClusterMessage(Serializable message)";
    	byte[] data = map(message);
		if (data != null)
		{
			try {
				_mctx.send(-1, (byte) - 1, 0, data, 0, data.length);
	            if (mPerfTracer.isActive()) mPerfTracer.log("cluster message sent on node {0}: message={1}",new Object[]{new Integer(me),message});
			} catch (ClusterException ce) {
				LoggingHelper.traceThrowable(Severity.ERROR, _loc, mn, ce);
			}
		}    	
    }
    
    /**
     * Registers an implementation of IClusterMessageListener which receives messages
     * that are send by other cluster nodes.
     * @param listener The listener that will be called when a message arrives.
     */
    public void registerClusterMessageListener(IClusterMessageListener listener)
    {
    	_registeredListeners.add(listener);
    }
    
    /**
     * Deregisters an implementation of IClusterMessageListener.
     * @param listener The listener that will be deregistered.
     */    
    public void deregisterClusterMessageListener(IClusterMessageListener listener)
    {
    	_registeredListeners.remove(listener);
    }
	
    
	private class Listener implements MessageListener {

		/* (non-Javadoc)
		 * @see com.sap.engine.frame.cluster.message.MessageListener#receive(int, int, byte[], int, int)
		 */
		public void receive(
			int clusterId,
			int messageType,
			byte[] body,
			int offset,
			int length) {
			if (clusterId != me) 
			{
				Serializable message = map(body, offset, length);
				if (mPerfTracer.isActive()) mPerfTracer.log("cluster message received on node {0}: clusterid={1}, messageType={2}, message={3}",new Object[]{new Integer(me),new Integer(clusterId), new Integer(messageType),message});
				if (_registeredListeners != null)
				{
					for (Iterator iter=_registeredListeners.iterator(); iter.hasNext();)
					{
						IClusterMessageListener cml = (IClusterMessageListener)iter.next();
						long startTimeStamp = System.currentTimeMillis();
						cml.receiveMessage(message);
						long endTimeStamp = System.currentTimeMillis();
	    	            if (mPerfTracer.isActive()) mPerfTracer.log("({0}ms) receiveMessage called with message \"{1}\" on IClusterMessageListener \"{2}\".",new Object[]{new Long(endTimeStamp-startTimeStamp),message,cml});
					}
				}
			}
		}

		/* (non-Javadoc)
		 * @see com.sap.engine.frame.cluster.message.MessageListener#receiveWait(int, int, byte[], int, int)
		 */
		public MessageAnswer receiveWait(
			int clusterId,
			int messageType,
			byte[] body,
			int offset,
			int length)
			throws Exception {
			receive(clusterId, messageType, body, offset, length);
			return new MessageAnswer();
		}

	}
    
    

}
