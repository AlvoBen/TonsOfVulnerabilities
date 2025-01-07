/*
 * Copyright (c) 2000-2009 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.MBeanServer;

import com.sap.bc.proj.jstartup.sadm.ShmAccessPoint;
import com.sap.bc.proj.jstartup.sadm.ShmException;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.event.ClusterEventListener;
import com.sap.engine.frame.cluster.event.ServiceEventListener;
import com.sap.engine.frame.cluster.message.ListenerAlreadyRegisteredException;
import com.sap.engine.frame.cluster.message.MessageAnswer;
import com.sap.engine.frame.cluster.message.MessageContext;
import com.sap.engine.frame.cluster.message.MessageListener;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.ShellInterface;
import com.sap.engine.lib.io.hash.HashUtils;
import com.sap.engine.lib.util.ArrayInt;
import com.sap.engine.lib.util.ArrayObject;
import com.sap.engine.lib.util.ConcurrentHashMapIntObject;
import com.sap.engine.lib.util.ConcurrentHashMapLongObject;
import com.sap.engine.services.httpserver.CommunicationConstants;
import com.sap.engine.services.httpserver.HttpRuntimeInterface;
import com.sap.engine.services.httpserver.ZoneManagementInterface;
import com.sap.engine.services.httpserver.chain.FilterException;
import com.sap.engine.services.httpserver.chain.impl.ChainComposerImpl;
import com.sap.engine.services.httpserver.interfaces.properties.HttpProperties;
import com.sap.engine.services.httpserver.lib.protocol.HeaderValues;
import com.sap.engine.services.httpserver.lib.util.ByteArrayUtils;
import com.sap.engine.services.httpserver.lib.util.EngineVersionUtil;
import com.sap.engine.services.httpserver.server.hosts.Host;
import com.sap.engine.services.httpserver.server.logongroups.LogonGroupsManager;
import com.sap.engine.services.httpserver.server.management.ServerMBeanManager;
import com.sap.engine.services.httpserver.server.properties.HttpPropertiesImpl;
import com.sap.engine.services.httpserver.server.properties.ProxyConfigurationOld;
import com.sap.engine.services.httpserver.server.properties.ProxyMappings;
import com.sap.engine.services.httpserver.server.properties.ReverseProxyMappings;
import com.sap.engine.services.httpserver.server.shellcommands.ApplicationAliasCommand;
import com.sap.engine.services.httpserver.server.shellcommands.HTTPClearCache;
import com.sap.engine.services.httpserver.server.shellcommands.HostCommand;
import com.sap.engine.services.httpserver.server.shellcommands.HttpAliasCommand;
import com.sap.engine.services.httpserver.server.shellcommands.LogonGroupCommand;
import com.sap.engine.services.httpserver.server.zones.ZoneManagementImpl;

/**
 * Startup class for HTTP service.
 *
 * @author Galin Galchev
 * @version 4.0
 */
public class HttpServerFrame implements ApplicationServiceFrame, ClusterEventListener, ServiceEventListener, ContainerEventListener, MessageListener {
  private static final String DEFAULT_ALIAS = "/";

  private HttpPropertiesImpl httpProperties = null;
  private ApplicationServiceContext sc = null;
  private HttpProviderImpl httpProvider = null;
  private HttpMonitoring httpMonitoring = new HttpMonitoring();
  private HttpRuntimeInterface runtimeInterface = null;
  private Date date = new Date();
  private int commandId = -1;
  protected MessageContext message = null;
  private HttpHosts descriptorManager = null;
  private ShellInterface shell = null;
  public static ConcurrentHashMapIntObject ports = new ConcurrentHashMapIntObject();
  public static ProxyMappings plainPort;
  public static ProxyMappings sslPort = null;
  public static ConcurrentHashMapLongObject defaultPorts = new ConcurrentHashMapLongObject();
  //public static ReverseProxyMappings reverseProxyMappings = new ReverseProxyMappings();
  private static boolean monitoringStarted = false;
  private static HttpLock httpLock = null;
  private ZoneManagementInterface zoneManagement;
  private Processor pr = null;
  private ChainComposerImpl composer;

  private LogonGroupsManager logonGroupsManager;

  /**
   * This method is specified by the service and is invoked by the service manager after loading
   * all services.
   *
   * @param   sc  The ServiceContext given to the service to operate with. As the this interface is for server-only services, the ServiceContext given is a ApplicationServiceContext with its specifics.
   * @exception   ServiceException  Thrown if some problem occurs while the service initializes and starts.
   */
  public void start(ApplicationServiceContext sc) throws ServiceException {
    this.sc = sc;
    message = sc.getClusterContext().getMessageContext();
    ServiceContext.init(sc.getCoreContext().getThreadSystem());
    ServiceContext.getServiceContext().setApplicationServiceContext(sc);
    ServiceContext.getServiceContext().setHttpMoniroting(httpMonitoring);
    Log.init();
    HttpAccessLog.init(date);
    SmdAccessLog.init();

    httpProperties = new HttpPropertiesImpl();
    httpProperties.setZoneSeparator();
    httpProperties.setDisableURLSessionTracking();
    httpProperties.setProperties(sc.getServiceState().getProperties(), true);
    ServiceContext.getServiceContext().setHttpProperties(httpProperties);
    sc.getServiceState().registerRuntimeConfiguration(httpProperties);

    httpLock = new HttpLock(sc);
    descriptorManager = new HttpHosts();
    descriptorManager.init(EngineVersionUtil.getEngineVersion(sc.getCoreContext().getCoreMonitor()), date, httpProperties, sc);
    ServiceContext.getServiceContext().setHttpHosts(descriptorManager);
    httpProvider = new HttpProviderImpl(descriptorManager, httpProperties, this, sc.getClusterContext().getClusterMonitor());
    ServiceContext.getServiceContext().setHttpProvider(httpProvider);
    // Chain API implementation
    try {
      composer = new ChainComposerImpl();
    } catch (FilterException fe) {
      Log.logError("ASJ.http.000181", "Fail to construct ChainComposer for HTTP Provider", fe, null, null, null);
      throw new ServiceException(fe);
    }
    composer.register(null);
    pr = new Processor(httpMonitoring, composer);
    pr.runServer(); //GALKY
    /* TODO: Check if this registration is required any more
    sc.getClusterContext().getApplicationSessionContext().registerProcessor(pr); */
    for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
      try {
        descriptorManager.readAllHostsFromConfiguration();
        break;
      } catch (ConfigurationException e) {
        try {
          Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
        } catch (InterruptedException ie) {
          Log.logError("ASJ.http.000182",
            "A thread interrupted while waiting for cluster lock for HTTP Provider service for configuration access.", ie, null, null, null);
        }
        if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
          throw new ServiceException(e);
        }
      }
    }
    for (int count = 0; count < HttpLock.READ_LOCK_ITER_COUNT; count++) {
      try {
        synchronizeUploadedFiles();
        break;
      } catch (InconsistentReadException e) {
        try {
          Thread.sleep(HttpLock.READ_LOCK_WAIT_TIMEOUT);
        } catch (InterruptedException ie) {
          Log.logError("ASJ.http.000183",
            "A thread interrupted while waiting for cluster lock for HTTP Provider service for configuration access.", ie, null, null, null);
        }
        if (count == HttpLock.READ_LOCK_ITER_COUNT - 1) {
          throw new ServiceException(e);
        }
      }
    }
    try {
      sc.getClusterContext().getMessageContext().registerListener(this);
    } catch (ListenerAlreadyRegisteredException e) {
      Log.logError("ASJ.http.000184", "Cannot register the HTTP Provider service as message listener.", e, null, null, null);
      throw new ServiceException(e);
    }
    try {
      sc.getServiceState().registerClusterEventListener(this);
    } catch (ListenerAlreadyRegisteredException e) {
      Log.logError("ASJ.http.000185", "Cannot register the HTTP Provider service as cluster listener.", e, null, null, null);
      throw new ServiceException(e);
    }
    ClusterElement[] clElements = sc.getClusterContext().getClusterMonitor().getParticipants();
    for (int i = 0; i < clElements.length; i++) {
      if ((clElements[i].getType() == ClusterElement.ICM) && (clElements[i].getGroupId() == getGroupID())) {
        ServiceContext.setIcmClusterId(clElements[i].getClusterId());
      }
    }
    sc.getContainerContext().getObjectRegistry().registerInterface(httpProvider);
    // can be at the end of this method but the logonGroupsManager is needed to init the zoneManagementInterface
    logonGroupsManager = new LogonGroupsManager(sc.getCoreContext().getConfigurationHandlerFactory(), httpProperties.getZoneSeparator());
    httpProperties.setLogonGroupsManager(logonGroupsManager);
    ServiceContext.getServiceContext().setLogonGroupsManager(logonGroupsManager);

    sc.getServiceState().registerManagementInterface(getHttpRuntimeInterface());
    try {
      sc.getServiceState().registerServiceEventListener(this);
    } catch (ListenerAlreadyRegisteredException e) {
      Log.logError("ASJ.http.000186", "Cannot register the HTTP service as service listener.", e, null, null, null);
      throw new ServiceException(e);
    }
    int mask = ContainerEventListener.MASK_INTERFACE_AVAILABLE |
               ContainerEventListener.MASK_CONTAINER_STARTED |
               ContainerEventListener.MASK_SERVICE_STARTED |
               ContainerEventListener.MASK_SERVICE_STOPPED |
               ContainerEventListener.MASK_SERVICE_NOT_STARTED;
    Set names = new HashSet(4);
    names.add("servlet_jsp");
    names.add("shell");
    names.add("monitor");
    names.add("jmx");
    sc.getServiceState().registerContainerEventListener(mask, names, this);
    if (sc.getContainerContext().getSystemMonitor().getService("monitor") != null
        && sc.getContainerContext().getSystemMonitor().getService("monitor").getStatus() == ComponentMonitor.STATUS_ACTIVE) {
      serviceStarted("monitor", null);
    }
  }

  public HttpProperties getHttpProperties() {
    return httpProperties;
  }

  public Date getDate() {
    return date;
  }

  public static HttpLock getHttpLock() {
    return httpLock;
  }

  public int[] getServers(int group) {
    ArrayInt servers = new ArrayInt();
    ClusterElement[] clusterElements = sc.getClusterContext().getClusterMonitor().getParticipants();
    for (int i = 0; i < clusterElements.length; i++) {
      if (clusterElements[i] != null && clusterElements[i].getGroupId() == group) {
        servers.add(clusterElements[i].getClusterId());
      }
    }
    return servers.toArray();
  }

  public int getGroup(int server) {
    ClusterElement clusterElement = sc.getClusterContext().getClusterMonitor().getParticipant(server);
    if (clusterElement == null) {
      return -1;
    }
    return clusterElement.getGroupId();
  }

  protected void clearRemoteCache() {
    if (sc.getClusterContext().getClusterMonitor().getCurrentParticipant().getState() == ClusterElement.DEBUGGING) {
      return;
    }
    try {
      byte[][] aliases = HeaderValues.getAllETags();

      for (int i = 0; i < aliases.length; i++) {
        byte[] aliasb = new byte[aliases[i].length];
        System.arraycopy(aliases[i], 0, aliasb, 0, aliasb.length);
        byte[] msg = encode(HeaderValues.ETAG_INQMY + new String(aliasb));
        if (LOCATION_HTTP.beDebug()) {
          LOCATION_HTTP.debugT("HttpServerFrame.clearRemoteCache(" + new String(msg) + ")");
        }
        message.send(-1, ClusterElement.ICM, CommunicationConstants.MESSAGE_ETAG_REMOVE, msg, 0, msg.length);
      }
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (ThreadDeath e) {
      throw e;
    } catch (Throwable e) {
      Log.logError("ASJ.http.000187",
        "Cannot clear cached responses for static file requests from the ICM HTTP cache. " +
        "Possible reason: cluster communication error.", e, null, null, null);
    }
  }

  protected void clearRemoteCache(String alias) {
    if (sc.getClusterContext().getClusterMonitor().getCurrentParticipant().getState() == ClusterElement.DEBUGGING) {
      return;
    }
    try {
      byte[] aliasb = HeaderValues.getSapIscEtag(alias);

      if (aliasb != null) {
        byte[] msg = encode(new String(aliasb));
        if (LOCATION_HTTP.beDebug()) {
          LOCATION_HTTP.debugT("HttpServerFrame.clearRemoteCache(" + new String(msg) + ")");
        }
        message.send(-1, ClusterElement.ICM, CommunicationConstants.MESSAGE_ETAG_REMOVE, msg, 0, msg.length);
      }
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (ThreadDeath e) {
      throw e;
    } catch (Throwable e) {
      Log.logError("ASJ.http.000188",
        "Cannot clear the objects cached for the AS Java from the ICM HTTP cache. " +
        "Possible reason: cluster communication error.", e, null, null, null);
    }

    // Clears default application alias
    if (!DEFAULT_ALIAS.equals(alias)) {
      clearRemoteCache(DEFAULT_ALIAS);
    }
  }

  /**
   * Encode message to UTF8 format with 4 bytes header
   *
   * @param message  message in Unicode string format
   * @return byte array contains encoded message
   */
  public byte[] encode(String message) {
    int length = 0;

    for (int i = 0; i < message.length(); i++) {
      if (message.charAt(i) == 0) {
        length += 2;
      } else if (message.charAt(i) < '\u0080') {
        length += 1;
      } else if (message.charAt(i) < '\u0800') {
        length += 2;
      } else {
        length += 3;
      }
    }

    byte[] result = new byte[length + 4];
    // encode header
    result[3] = (byte) length;
    length >>= 8;
    result[2] = (byte) length;
    length >>= 8;
    result[1] = (byte) length;
    length >>= 8;
    result[0] = (byte) length;
    // encode UTF8
    int offset = 4;

    for (int i = 0; i < message.length(); i++) {
      if (message.charAt(i) == 0) {
        result[offset++] = (byte) 0xC0;
        result[offset++] = (byte) 0x80;
      } else if (message.charAt(i) < '\u0080') {
        result[offset++] = (byte) message.charAt(i);
      } else if (message.charAt(i) < '\u0800') {
        result[offset++] = (byte) (0xC0 + (message.charAt(i) >> 6));
        result[offset++] = (byte) (0x80 + (message.charAt(i) & 0x3F)); // 00xxxxxx
      } else {
        result[offset++] = (byte) (0xE0 + (message.charAt(i) >> 12));
        result[offset++] = (byte) (0x80 + ((message.charAt(i) >> 6) & 0x3F)); // 00xxxxxx
        result[offset++] = (byte) (0x80 + (message.charAt(i) & 0x3F)); // 00xxxxxx
      }
    }

    return result;
  }

  /**
   * Adds HTTP's shell commands
   *
   * @param   shell  OwnContext to add commands to
   */
  private void setCommands(ShellInterface shell) {
    this.shell = shell;
    Command cmds[] = {new HTTPClearCache(httpProvider),
                      new HostCommand(getHttpRuntimeInterface()),
                      new HttpAliasCommand(getHttpRuntimeInterface()),
                      new ApplicationAliasCommand(sc, getHttpRuntimeInterface()),
                      new LogonGroupCommand(getLogonGroupsManager())};
    commandId = shell.registerCommands(cmds);
  }

  // ServiceEventListener
  /**
   * This method is invoked by the <code>MSConnection</code> in which this
   * listener is registered, when the monitored service on the specified
   * cluster node is started.
   *
   * @param newElement the node on which the monitored service is started.
   */
  public void serviceStarted(ClusterElement newElement) {

  }


  /**
   * This method is invoked by the <code>MSConnection</code> in which this
   * listener is registered, when the monitored service on the specified
   * cluster node is stopped.
   *
   * @param oldElement the node on which the monitored service is stopped.
   */
  public void serviceStopped(ClusterElement oldElement) {
    // Nothing to do here
  }

  protected HttpRuntimeInterface getHttpRuntimeInterface() {
    if (runtimeInterface == null) {
      try {
        zoneManagement = new ZoneManagementImpl(httpProvider, logonGroupsManager);
        runtimeInterface = new HttpRuntimeInterfaceImpl(this, descriptorManager, httpMonitoring, httpProperties, zoneManagement);
      } catch (RemoteException _) {
        Log.logError("ASJ.http.000189",
          "Cannot load the remote interfaces of the HTTP Provider service. " +
          "The features they provide will not be accessible.", _, null, null, null);
      }
    }

    return runtimeInterface;
  }

  public LogonGroupsManager getLogonGroupsManager() {
    if (logonGroupsManager == null) {
      logonGroupsManager = new LogonGroupsManager(sc.getCoreContext().getConfigurationHandlerFactory(), httpProperties.getZoneSeparator());
    }
    return logonGroupsManager;
  }

  public int getServerID() {
    return sc.getClusterContext().getClusterMonitor().getCurrentParticipant().getClusterId();
  }

  public int getGroupID() {
    return sc.getClusterContext().getClusterMonitor().getCurrentParticipant().getGroupId();
  }

  /**
   *  Invokes the stop process of a service frame. The service frame has to free all resources and
   *  if possible to achieve the state of the server prior service's work, unless a "forced" stop
   *  is required.
   *
   */
  public void stop() {
    clearRemoteCache();
    if (pr != null) { pr.stopServer(); }
    // Unregisters chain composer
    composer.unregister();
    sc.getContainerContext().getObjectRegistry().unregisterInterface();
    sc.getServiceState().unregisterContainerEventListener();
    sc.getClusterContext().getMessageContext().unregisterListener();
    //    sc.getOwnContext().unregisterRuntimeMonitor();
    shell.unregisterCommands(commandId);
    sc.getServiceState().unregisterServiceEventListener();
    sc.getServiceState().unregisterManagementInterface();
    sc.getServiceState().unregisterClusterEventListener();
    sc.getServiceState().unregisterRuntimeConfiguration();
    //    if (snapShotProviderId != -1) {
    //      sc.getContainerContext().getLocalMonitor().unregisterSnapShotProvider(snapShotProviderId);
    //    }
  }

  public MessageAnswer receiveWait(int clusterId, int msgType, byte[] msg, int off, int length) {
    switch (msgType) {
      case CommunicationConstants.MESSAGE_REMOVE_SESSION: {
        httpProvider.removeSession(new String(msg, off, length));
        return new MessageAnswer();
      }
      default: {
    	String message = new String(msg, off, length);
        Log.logError("ASJ.http.000190",
          "Unexpected message received. Message: [{0}], Message Type: [{1}], Received from Cluster Element: [{2}].",
          new Object[]{message, msgType, clusterId}, null, null, null);
      }
    }
    return null;
  }

  public void receive(int clusterId, int msgType, byte[] msg, int off, int length) {
	String message = new String(msg, off, length);
    Log.logError( "ASJ.http.000191",
      "Unexpected message received. Message: [{0}], Message Type: [{1}], Received from Cluster Element: [{2}].",
      new Object[]{message, msgType, clusterId}, null, null, null);
  }

  public void containerStarted() {
  }

  public void beginContainerStop() {
  }

  public void serviceStarted(String serviceName, Object serviceInterface) {
    if ("monitor".equalsIgnoreCase(serviceName)) {
      monitoringStarted = true;
    }else if (serviceName.equals("jmx")){
    	Object jmxi = sc.getContainerContext().getObjectRegistry().getServiceInterface("jmx");
    	if (jmxi instanceof MBeanServer){
    		ServerMBeanManager mngr = ServerMBeanManager.initManager((MBeanServer)jmxi);
    		mngr.startAll(sc,getHttpRuntimeInterface(), getLogonGroupsManager(), pr.resourceManager.getMonitor());
    	}
    }
  }

  public void serviceNotStarted(String serviceName) {
    // Nothing to do here
  }

  public void beginServiceStop(String serviceName) {

  }

  public void serviceStopped(String serviceName) {
    if ("monitor".equalsIgnoreCase(serviceName)) {
      monitoringStarted = false;
    }
  }

  public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
    if (interfaceName.equals("shell")) {
      setCommands((ShellInterface) interfaceImpl);
    }
  }

  public void interfaceNotAvailable(String interfaceName) {

  }

  public void markForShutdown(long timeout) {
  }

  /**
   *
   * @deprecated - use runtime configuration: HttpPropertiesImpl.updateProperties()
   */
  public boolean setServiceProperty(String key, String value) {
  	Log.logWarning("ASJ.http.000192",
  	  "An attempt to change service properties via deprecated method. Please, use runtime configuration.", null, null, null);
    boolean needRestart = httpProperties.setProperty(key, value, false);
    // return false - to restart the server
    return !needRestart;
  }

  /**
   *
   * @deprecated - use runtime configuration: HttpPropertiesImpl.updateProperties()
   */
  public boolean setServiceProperties(Properties serviceProperties) {
  	Log.logWarning("ASJ.http.000077",
  	  "An attempt to change service properties via deprecated method. Please, use runtime configuration", null, null, null);
    boolean needRestart = httpProperties.setProperties(serviceProperties, false);
    // return false - to restart the server
    return !needRestart;
  }

  private synchronized void synchronizeUploadedFiles() throws InconsistentReadException {
    try {
      ConfigurationHandlerFactory factory = sc.getCoreContext().getConfigurationHandlerFactory();
      if (factory == null) {
        Log.logFatal("ASJ.http.000225", "Cannot access database. Cannot synchronize uploaded HTTP files.", null, null, null);
        return;
      }
      Configuration configHost = null;
      ConfigurationHandler handler = factory.getConfigurationHandler();
      Configuration configApps = null;
      try {
        configApps = handler.openConfiguration(Constants.HTTP_ALIASES, ConfigurationHandler.READ_ACCESS);
      } catch (NameNotFoundException e) {
        //ok - there is no uploaded files
        return;
      } catch (Exception e) {
        Log.logError("ASJ.http.000193",
          "Cannot read configuration [{0}] from database. Cannot synchronize uploaded HTTP files.",
          new Object[]{Constants.HTTP_ALIASES}, e, null, null, null);
        return;
      }
      Host[] descriptors = descriptorManager.getAllHosts();
      for (int j = 0; j < descriptors.length; j++) {
        try {
          configHost = configApps.getSubConfiguration(descriptors[j].getHostName());
        } catch (InconsistentReadException e) {
          throw e;
        } catch (Exception e) {
          Log.logError("ASJ.http.000194",
            "Cannot read configuration [{0}/{1}] from database. Cannot synchronize uploaded HTTP files.",
            new Object[]{Constants.HTTP_ALIASES, descriptors[j].getHostName()}, e, null, null, null);
          continue;
        }
        // all aliases
        String[] aliases =configHost.getAllSubConfigurationNames();
        if (aliases == null || aliases.length == 0) {
          continue;
        }
        Configuration configAlias = null;
        for(int i = 0; i < aliases.length; i++) {
          Map fileEntries = null;
          configAlias = configHost.getSubConfiguration(aliases[i]);
          // files
          fileEntries = configAlias.getAllFileEntries();
          if (fileEntries.isEmpty()) {
            continue;
          }

          String rootDir = descriptors[j].getHostProperties().getAliasValue(aliases[i]);
          if (rootDir == null) {
            continue;
          }
          Set keySet = fileEntries.keySet();
          Iterator iter = keySet.iterator();

          while (iter.hasNext()) {
            String nextFilename = (String)iter.next();
            // Ako crc na file i crc na file-a ot DB sa razlichni togava loadvame ot bazata...
            String file = (String)configAlias.getConfigEntry(nextFilename.substring(1));
            File hddFile = new File(rootDir + file);
            if (hddFile.exists()) {
              byte[] crcHDDFile = HashUtils.generateFileHash(hddFile);// throws IOException
              byte[] crcDBFile = (byte[])configAlias.getConfigEntry(nextFilename.replace('#', '$'));
              if (ByteArrayUtils.equalsBytes(crcHDDFile, crcDBFile)) {
                continue;
              }
            }
            (new File(hddFile.getParent())).mkdirs();
            FileOutputStream fileout = new FileOutputStream(hddFile);
            try {
              InputStream in = configAlias.getFile(nextFilename);
              byte[] buf = new byte[1024];
              int received = 0;
              while ((received=in.read(buf)) != -1) {
                fileout.write(buf, 0, received);
              }
            } finally {
              try {
                fileout.close();
              } catch (IOException io) {
                Log.logError("ASJ.http.000195", "Cannot close file [{0}].", new Object[]{hddFile}, io, null, null, null);
              }
            }
          }
        }
      }
    } catch (InconsistentReadException e) {
      throw e;
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (ThreadDeath e) {
      throw e;
    } catch (Throwable e) {
      Log.logError("ASJ.http.000196",
        "Cannot synchronize uploaded HTTP files. Possible reason: database read failed or file system write failed.", e, null, null, null);
    }
  }

  public static boolean isMonitoringStarted() {
    return monitoringStarted;
  }

  public static ProxyMappings getProxyMappings(int port) {
    return (ProxyMappings)ports.get(port);
  }

  /**
   * Returns the first port mappings with "http" scheme if any
   * including the default ones
   *
   * @return
   * the first <code>ProxyMappings</code> with "http" scheme if any
   * including the default ones, otherwise <code>null</code>
   */
  public static ProxyMappings getPlainPortMappings() {
    if (plainPort != null) { return plainPort; }

    try {
      ShmAccessPoint[] accessPoints =
        ShmAccessPoint.getAllAccessPoints(ShmAccessPoint.PID_HTTP);
      if (accessPoints.length > 0) {
        return new ProxyMappings(accessPoints[0].getAddress().getHostName(),
          accessPoints[0].getPort(), "http", false);
      }
    } catch (ShmException e) {
      Log.logWarning("ASJ.http.000078", "Could not define default proxy mappings.", e, null, null, null);
    }

    return null;
  }

  public static ProxyMappings getSslProxyMappings() {
    if (sslPort != null) { return sslPort; }

    try {
      ShmAccessPoint[] accessPoints =
        ShmAccessPoint.getAllAccessPoints(ShmAccessPoint.PID_HTTPS);
      if (accessPoints.length > 0) {
        return new ProxyMappings(accessPoints[0].getAddress().getHostName(),
          accessPoints[0].getPort(), "https", false);
      }
    } catch (ShmException e) {
      Log.logError("ASJ.http.000197", "Could not define default proxy mappings.", e, null, null, null);
    }

    // This is a workaround when there isn't any https access point.
    // The returned proxy mapping has the host of the http access point,
    // the default https port 443 and https scheme
    try {
      ShmAccessPoint[] accessPoints =
        ShmAccessPoint.getAllAccessPoints(ShmAccessPoint.PID_HTTP);
      if (accessPoints.length > 0) {
        return new ProxyMappings(accessPoints[0].getAddress().getHostName(),
          443, "https", false);
      }
    } catch (ShmException e) {
      Log.logError("ASJ.http.000198", "Could not define default proxy mappings.", e, null, null, null);
    }

    return null;
  } 

  /**
   * Gets default proxy mappings. They contain the scheme, host and port of the
   * dispatcher.
   *
   * ID of dispatcher
   * @param port
   * Port of the incoming request
   * @return Default <code>ProxyMappings</code> for the
   * <code>dispatcherId</code> and <code>port</code> if any, otherwise
   * <code>null</code>
   */
  public static ProxyConfigurationOld getDefaultReverseProxyMappings(int port) {
    ShmAccessPoint[] accessPoints = null;
    try {
      // Retrieves all http access points
      accessPoints = ShmAccessPoint.getAllAccessPoints(ShmAccessPoint.PID_HTTP);
      if (accessPoints != null) {
        for (int i = 0; i < accessPoints.length; i++) {
          ShmAccessPoint accessPoint = accessPoints[i];
          if (port == accessPoint.getPort()) {
            return new ProxyConfigurationOld(accessPoint.getAddress()
                .getHostName(), port, "http", false);
          }
        }
      }
    } catch (ShmException e) {
      Log.logError("ASJ.http.000407", "Could not create default proxy mappings for plain http.", e, null, null, null);
    }
    
    try {
      // Retrieves all https access points
      accessPoints =
        ShmAccessPoint.getAllAccessPoints(ShmAccessPoint.PID_HTTPS);
      if (accessPoints != null) {
        for (int i = 0; i < accessPoints.length; i++) {
          ShmAccessPoint accessPoint = accessPoints[i];
          if (port == accessPoint.getPort()) {
            return new ProxyConfigurationOld(accessPoint.getAddress().getHostName(),
              port, "https", false);
          }
        }
      }
    } catch (ShmException e) {
      Log.logError("ASJ.http.000410", "Could not create default proxy mappings for https.", e, null, null, null);
    }

    return null;
  }


  /**
   * Parses proxy mappings string set as a property
   */
  public static void setProxyMappings(String proxyMappings) {
    String s;
    int i, j, port;
    boolean plainFound = false, sslFound = false;
    ConcurrentHashMapIntObject p = new ConcurrentHashMapIntObject();
    ProxyConfigurationOld dp = null;
    sslPort = null;
    String hostValue, portValue, schemeValue, overrideValue;

    String errorPrefix = "Could not parse property ProxyMappings: ";
    String errorSuffix = " Please correct the ProxyMapping property.";

    j = 0;
    ArrayObject errors = new ArrayObject();
    while (j < proxyMappings.length()
        && (i = proxyMappings.indexOf("=", j)) > -1) {
      String portStr = proxyMappings.substring(j, i).trim();
      try {
        port = Integer.parseInt(portStr);
      } catch (NumberFormatException e) {
        port = -1;
        Log.logError("ASJ.http.000200",
          "Could not parse property ProxyMappings: The specified port [{0}] is not a number. " +
          "The mapping for this port will be ignored.Please correct the ProxyMapping property.",
          new Object[]{portStr}, null, null, null);
      }

      parsing_port: if (port != -1) {
        i++;
        if ((i = proxyMappings.indexOf("(", i)) > -1
            && (j = proxyMappings.indexOf(')', i)) > -1) {
          boolean isDefault = false;
          s = proxyMappings.substring(i + 1, j);

          String errorPrefix2 = errorPrefix + "The specified mapping for port "
              + port + ": \"" + s + "\" is not correct. The error is: ";

          if (!isProxyMappingsSyntaxCorrect(s, errorPrefix2, errorSuffix)) {
            Log.logError("ASJ.http.000201",
              "{0} Incorrect syntax. The mapping for this port will be ignored. {1}",
              new Object[]{errorPrefix2, errorSuffix}, null, null, null);
            break parsing_port;
          }

          hostValue = getValue(s, "Host");
          if (hostValue == null || hostValue.length() == 0) {
            errors.add(errorPrefix2
                + "\"Host\" is null or empty string. The mapping for this port will be ignored."
                + errorSuffix);
            //break parsing_port;
          }

          int proxyPort = -1;
          portValue = getValue(s, "Port");
          if (portValue == null || portValue.length() == 0) {
            errors.add(errorPrefix2
                + "\"Port\" is null or empty string. The mapping for this port will be ignored."
                + errorSuffix);
            //break parsing_port;
          } else {
            try {
              proxyPort = Integer.parseInt(portValue);
            } catch (NumberFormatException e1) {
              Log.logError("ASJ.http.000202", "{0} \"Port\" value=={1}" +
                  " is not a valid integer. The mapping for this port will be ignored. {2}",
                  new Object[]{errorPrefix2, portValue, errorSuffix}, null, null, null);
              break parsing_port;
            }
            if (proxyPort < 0 || proxyPort > 65535) {
              Log.logError("ASJ.http.000203", "{0} \"Port\" value=={1}" +
                  " is not a valid TCP port (0-65535). The mapping for this port will be ignored. {2}",
                  new Object[]{errorPrefix2, portValue, errorSuffix}, null, null, null);
              break parsing_port;
            }
          }

          schemeValue = getValue(s, "Scheme");
          if (schemeValue == null || !(schemeValue.equalsIgnoreCase("http")
              || schemeValue.equalsIgnoreCase("https"))) {
            Log.logError("ASJ.http.000408", "Error: {0} Unknown \"Scheme\"==\"" +
                "{1} \", assuming default schema \"http\".",
                new Object[]{errorPrefix2, schemeValue}, null, null, null);
            
						schemeValue = "http";
          }

          // TODO: Remove defaults because they comes from different place
          boolean proxyOverride = false;
          overrideValue = getValue(s, "Override");
          if ("default".equals(overrideValue)) {
            isDefault = true;
          } else if ("true".equalsIgnoreCase(overrideValue)) {
            proxyOverride = true;
          } else if (!"false".equalsIgnoreCase(overrideValue)) {
            Log.logError("ASJ.http.000409", "Error: {0} Unknown \"Override\"==\"" +
                "{1}\", assuming default: \"Override:false\".", new Object[]{errorPrefix2, overrideValue}, null, null, null);
          }

          if (hostValue != null && proxyPort != -1) {
            // OK. All values are set (scheme and override has default values).
          } else if (proxyOverride) {
            // OK. Scheme is set and override is true
          } else {
            for (int e = 0; e < errors.size(); e++) {
              String strError = (String) errors.get(e);
              Log.logError("ASJ.http.000204", "{0}", new Object[]{strError}, null, null, null);
            }
            errors.clear();
            break parsing_port;
          }

          dp = new ProxyConfigurationOld(hostValue, proxyPort, schemeValue,
              proxyOverride);

          // TODO: Remove defaults because they comes from different place
          if (!isDefault) {
            p.put(port, dp);
          } else {
            defaultPorts.put(port, dp);
          }

          if (LOCATION_HTTP.bePath()) {
						LOCATION_HTTP.pathT("HttpServerFrame.setProxyMappings(): Parsed ProxyMappings property for port " + port +
								": Host==\"" + dp.getHost() + "\", Port==" + dp.getPort() + ", Scheme==\"" +
								dp.getScheme() + "\", Override==" + dp.isOverride() + ".");
					}

					if (!plainFound && "http".equals(dp.getScheme())) {
					  ReverseProxyMappings.plainPortOld = dp;
            plainFound = true;
          }

          if (!sslFound && "https".equals(dp.getScheme())) {
            ReverseProxyMappings.sslPortOld = dp;
            sslFound = true;
          }
        }
      }

      j = proxyMappings.indexOf(',', j + 1);
      if (j == -1) {
        break;
      }
      j++;
    }

    if (p.isEmpty()) {
      if (LOCATION_HTTP.bePath()) {
				LOCATION_HTTP.pathT("HttpServerFrame.setProxyMappings(): No ProxyMappings property.");
			}
    }

    //synchronized (ports) { ports = p; }
    ReverseProxyMappings.oldConfigurationsPorts = p;
  }

  private static String getValue(String data, String key) {
    int i = 0, j = 0;

    while ((i = data.indexOf(key + ":", j)) > -1) {
      if (i == 0 || Character.isWhitespace(data.charAt(i - 1)) || data.charAt(i - 1) == ',') {
        break;
      }
      j += key.length() + 1;
    }
    if (i > -1) {
      i += key.length() + 1;
      if ((j = data.indexOf(',', i)) == -1) {
        j = data.length();
      }
      return data.substring(i, j).trim();
    }
    return null;
  }

  /**
   * Check for key:value structure and check if key is known.
   */
  private static boolean isProxyMappingsSyntaxCorrect(String s, String errorPrefix, String errorSuffix) {
    int sLen = s.length();
    if (sLen < 13) { // "Host:?,Port:?".length() == 13
      return false;
    }

    char first = s.charAt(0);
    char last = s.charAt(sLen - 1);

    if (first == ':' || last == ':' || first == ',' || last == ',') {
      Log.logError("ASJ.http.000205",
        "{0} Separator symbol (':' or ',') at string edge. " +
        "The mapping for this port will be ignored. {1}", new Object[]{errorPrefix, errorSuffix}, null, null, null);
      return false;
    }

    boolean foundHost = false;
    boolean foundPort = false;
    boolean foundScheme = false;
    boolean foundOverride = false;

    // Find each key-value pair
    int right;
    for (int left = 0; left < sLen; left = right + 1) {
      right = s.indexOf(',', left);

      if (right == -1) {
        // Take up to end-of-string
        right = sLen;
      }

      String keyValuePair = s.substring(left, right);

      int keyValueSplit = keyValuePair.indexOf(':');
      if (keyValueSplit == -1) {
        Log.logError("ASJ.http.000206",
          "{0} Not a key-value token==\"{1}\", token ignored. {2}",
          new Object[]{errorPrefix, keyValuePair, errorSuffix}, null, null, null);
        continue;
      }

      // Now check the key
      String key = keyValuePair.substring(0, keyValueSplit).trim();

      if (key.equals("Host")) {
        if (foundHost) {
          Log.logError("ASJ.http.000207",
            "{0} Duplicate key \"Host\" found, the first value is used. {1}",
            new Object[]{errorPrefix, errorSuffix}, null, null, null);
          continue;
        }
        foundHost = true;
      } else if (key.equals("Port")) {
        if (foundPort) {
          Log.logError("ASJ.http.000208",
            "{0} Duplicate key \"Port\" found, the first value is used. {1}",
            new Object[]{errorPrefix, errorSuffix}, null, null, null);
          continue;
        }
        foundPort = true;
      } else if (key.equals("Scheme")) {
        if (foundScheme) {
          Log.logError("ASJ.http.000209",
            "{0} Duplicate key \"Scheme\" found, the first value is used. {1}",
            new Object[]{errorPrefix, errorSuffix}, null, null, null);
          continue;
        }
        foundScheme = true;
      } else if (key.equals("Override")) {
        if (foundOverride) {
          Log.logError("ASJ.http.000210",
            "{0} Duplicate key \"Override\" found, the first value is used. {1}",
            new Object[]{errorPrefix, errorSuffix}, null, null, null);
          continue;
        }
        foundOverride = true;
      } else {
        Log.logError("ASJ.http.000211",
          "{0} Unknown key==\"{1}\", key is ignored. {2}",
          new Object[]{errorPrefix, key, errorSuffix}, null, null, null);
        continue;
      }

      if (keyValueSplit == keyValuePair.length() - 1) {
        Log.logError("ASJ.http.000212",
          "{0} Missing value for \"{1}\", error ignored. {2}", new Object[]{errorPrefix, key, errorSuffix}, null, null, null);
        continue;
      }

      String value = keyValuePair.substring(keyValueSplit + 1).trim();

      if (value.length() == 0) {
        Log.logError("ASJ.http.000213",
          "{0} Missing value for \"{1}\", error ignored. {2}", new Object[]{errorPrefix, key, errorSuffix}, null, null, null);
        continue;
      }
    }

    return true;
  }

  public void elementJoin(ClusterElement clusterElement) {
    if ((clusterElement.getType() == ClusterElement.ICM) && (clusterElement.getGroupId() == getGroupID())) {
      ServiceContext.setIcmClusterId(clusterElement.getClusterId());
    }
  }

  public void elementLoss(ClusterElement clusterElement) {
    if ((clusterElement.getType() == ClusterElement.ICM) && (clusterElement.getGroupId() == getGroupID())) {
      ServiceContext.setIcmClusterId(-1);
    }
  }

  public void elementStateChanged(ClusterElement clusterElement, byte b) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

}
