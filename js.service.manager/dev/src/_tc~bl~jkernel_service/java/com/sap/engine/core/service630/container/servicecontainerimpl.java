package com.sap.engine.core.service630.container;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Manager;
import com.sap.engine.core.Names;
import com.sap.engine.core.cluster.ClusterManager;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.engine.core.service630.ServiceContainer;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.frame.container.monitor.InterfaceMonitor;
import com.sap.engine.frame.container.monitor.LibraryMonitor;
import com.sap.engine.frame.container.monitor.ServiceMonitor;
import com.sap.engine.frame.container.monitor.SystemMonitor;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.monitor.CoreMonitor;
import com.sap.engine.frame.core.reflect.ReflectContext;
import com.sap.engine.frame.state.ManagementInterface;
import com.sap.engine.lib.config.api.component.ComponentHandler;
import com.sap.engine.lib.config.api.exceptions.ClusterConfigurationException;
import com.sap.engine.system.SystemEnvironment;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;
import com.sap.tools.memory.trace.AllocationStatisticRecord;
import com.sap.tools.memory.trace.AllocationStatisticRegistry;

/**
 * This class represents service manager. It provides runtime control and monitoring of the service container.
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class ServiceContainerImpl implements ServiceContainer, SystemMonitor, ReflectContext {

  //work folder
  public final static String WORK_FOLDER = "." + File.separatorChar + "temp";
  //bin folders
  public static String INTERFACE_BIN_DIR;
  public static String LIBRARY_BIN_DIR;
  public static String SERVICE_BIN_DIR;

  static {
    String root = getCannonicDir(".." + File.separatorChar + "bin");
    INTERFACE_BIN_DIR = root + File.separatorChar + "interfaces";
    LIBRARY_BIN_DIR = root + File.separatorChar + "ext";
    SERVICE_BIN_DIR = root + File.separatorChar + "services";
  }

  private static String getCannonicDir(String dir) {
    File dirFile = new File(dir);
    String result;
    try {
      result = dirFile.getCanonicalPath();
    } catch (IOException e) {
      result = dirFile.getAbsolutePath();
    }
    return result;
  }

  //time startstics registry
  private static final HashMap<String, long[]> timeStatisticsRegistry = new HashMap<String, long[]>();

  //logging
  private static final Category CATEGORY = Category.SYS_SERVER;
  private static final Location LOCATION = Location.getLocation(ServiceContainerImpl.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);
  private static final Location timeStatistics = Location.getLocation("com.sap.engine.core.service630.container.TimeStatistics", Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);
  private static final Location memoryStatistics = Location.getLocation("com.sap.engine.core.service630.container.MemoryStatistics", Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  //service manager properties
  private Properties properties;

  //service container management interface
  private ManagementInterface managementInterface = null;

  //cluster manager
  private ClusterManager clusterManager;

  //wrap classload context to replace component names if needed
  private LoadContextWrapper loadContextWrapper;

  //object registry singleton
  private ContainerObjectRegistry containerObjectRegistry;

  //event registry singleton
  private ContainerEventRegistry containerEventRegistry;

  //operation container singleton
  private MemoryContainer memoryContainer;

  //service container runtime state
  private byte containerState = SystemMonitor.STATE_STOPPED;

  //instance type (j2ee or jms)
  private String instanceType;

  //////////////////////////////////////////// MANAGER /////////////////////////////////////////////////////////////////

  /** @see com.sap.engine.core.Manager#init(java.util.Properties) */
  public boolean init(Properties properties) {
    //set system property for naming!
    SystemProperties.setProperty("server", "true");
    containerState = SystemMonitor.STATE_STARTING;
    this.properties = properties;
    getManagers();
    clusterManager = ((ClusterManager) Framework.getManager(Names.CLUSTER_MANAGER));
    containerObjectRegistry = new ContainerObjectRegistry(this);
    containerEventRegistry = new ContainerEventRegistry(this);
    memoryContainer = new MemoryContainer(this);
    loadContextWrapper = new LoadContextWrapper(memoryContainer);
    //read the instance type
    try {
      instanceType = memoryContainer.getPersistentContainer().getInstanceLevel().getInstanceType();
    } catch (ClusterConfigurationException e) {
      SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
          "ASJ.krn_srv.000057",
          "Cannot read instance type. J2EE Engine cannot be started");
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
          "Cannot read instance type. J2EE Engine cannot be started", e);
      instanceType = CoreMonitor.INSTANCE_J2EE;
    }
    //start services
    return memoryContainer.start();
  }

  /** @see com.sap.engine.core.Manager#loadAdditional() */
  public void loadAdditional() {
    //wait to process all events
    ContainerEvent waitEvent = new ContainerEvent();
    waitEvent.isBefore = true;
    waitEvent.method = ContainerEvent.CONTAINER_SYNCHRONIZATION;
    containerEventRegistry.addContainerEvent(waitEvent);
    //fire container started
    containerState = SystemMonitor.STATE_STARTED;
    ContainerEvent event = new ContainerEvent();
    event.method = ContainerEventListener.MASK_CONTAINER_STARTED;
    containerEventRegistry.addContainerEvent(event);
    LOCATION.pathT(ResourceUtils.getString(ResourceUtils.SERVICE_MANAGER_STARTED));
  }

  /** @see com.sap.engine.core.Manager#shutDown(java.util.Properties) */
  public void shutDown(Properties properties) {
    containerState = SystemMonitor.STATE_STOPPING;
    clusterManager.startShutdown();
    ContainerEvent event = new ContainerEvent();
    event.method = ContainerEventListener.MASK_BEGIN_CONTAINER_STOP;
    event.isBefore = true;
    containerEventRegistry.addContainerEvent(event);
    memoryContainer.stop();
    //wait to process all events
    ContainerEvent waitEvent = new ContainerEvent();
    waitEvent.isBefore = true;
    waitEvent.method = ContainerEvent.CONTAINER_SYNCHRONIZATION;
    containerEventRegistry.addContainerEvent(waitEvent);
    containerState = SystemMonitor.STATE_STOPPED;
    printTimeStatistics();
    printMemoryStatistics();
    LOCATION.pathT(ResourceUtils.getString(ResourceUtils.SERVICE_MANAGER_STOPPED));
  }

  /** @see com.sap.engine.core.Manager#getCurrentProperties() */
  public Properties getCurrentProperties() {
    return properties;
  }

  /** @see com.sap.engine.core.Manager#getCurrentProperty(String) */
  public String getCurrentProperty(String key) {
    return properties.getProperty(key);
  }

  /** @see com.sap.engine.core.Manager#setProperty(String, String) */
  public boolean setProperty(String key, String value) throws IllegalArgumentException {
    properties.setProperty(key, value);
    return false;
  }

  /** @see com.sap.engine.core.Manager#setProperties(java.util.Properties) */
  public boolean setProperties(Properties properties) throws IllegalArgumentException {
    boolean flag = true;
    // Extract the properties
    Enumeration keys = properties.keys();
    Enumeration elements = properties.elements();
    // Set the properties one by one
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      String value = (String) elements.nextElement();
      // Set the next property
      if (!setProperty(key, value)) {
        flag = false;
      }
    }
    return flag;
  }

  /** @see com.sap.engine.core.Manager#updateProperties(Properties) */
  public void updateProperties(Properties properties) {
    //todo impl
  }

  /** @see com.sap.engine.core.Manager#getDebugInfo(int) */
  public String getDebugInfo(int flag) {
    return null;
  }

  /** @see com.sap.engine.core.Manager#getStatus() */
  public byte getStatus() {
    return 0;
  }

  /** @see com.sap.engine.core.Manager#getManagementInterface() */
  public synchronized ManagementInterface getManagementInterface() {
    if (managementInterface == null) {
      managementInterface = new ServiceManagementImpl(this);
    }
    return managementInterface;
  }

  private void getManagers() {
    boolean flag = (checkForManagerExistence(Names.CLASSLOADER_MANAGER)
            || checkForManagerExistence(Names.POOL_MANAGER)
            || checkForManagerExistence(Names.LOCKING_MANAGER)
            || checkForManagerExistence(Names.DATABASE_MANAGER)
            || checkForManagerExistence(Names.CONFIGURATION_MANAGER)
            || checkForManagerExistence(Names.LICENSING_MANAGER)
            || checkForManagerExistence(Names.CACHE_MANAGER)
            || checkForManagerExistence(Names.THREAD_MANAGER)
            || checkForManagerExistence(Names.APPLICATION_THREAD_MANAGER)
            || checkForManagerExistence(Names.CLUSTER_MANAGER));
    if (!flag) {
      Framework.criticalShutdown(MemoryContainer.CANT_GET_MANAGERS_EXIT_CODE, ResourceUtils.getString(ResourceUtils.CANT_GET_MANAGERS));
    }
  }

  private boolean checkForManagerExistence(String name) {
    Manager manager = Framework.getManager(name);
    if (manager == null) {
      SimpleLogger.log(Severity.FATAL, CATEGORY, LOCATION, "ASJ.krn_srv.000058", "Cannot get Manager: [{0}]", name);
      return false;
    }
    return true;
  }

  //////////////////////////////////////////// SYSTEM MONITOR //////////////////////////////////////////////////////////

  /** @see com.sap.engine.frame.container.monitor.SystemMonitor#getContainerType() */
  public byte getContainerType() {
    return SystemMonitor.APPLICATION_CONTAINER;
  }

  /** @see com.sap.engine.frame.container.monitor.SystemMonitor#getContainerState() */
  public byte getContainerState() {
    return containerState;
  }

  /** @see com.sap.engine.frame.container.monitor.SystemMonitor#getServices() */
  public ServiceMonitor[] getServices() {
    Hashtable<String, ServiceWrapper> services = memoryContainer.getServices();
    synchronized (services) {
      ServiceMonitor[] result = new ServiceMonitor[services.size()];
      services.values().toArray(result);
      return result;
    }
  }

  /** @see com.sap.engine.frame.container.monitor.SystemMonitor#getLibraries() */
  public LibraryMonitor[] getLibraries() {
    Hashtable<String, LibraryWrapper> libraries = memoryContainer.getLibraries();
    synchronized (libraries) {
      LibraryMonitor[] result = new LibraryMonitor[libraries.size()];
      libraries.values().toArray(result);
      return result;
    }
  }

  /** @see com.sap.engine.frame.container.monitor.SystemMonitor#getInterfaces() */
  public InterfaceMonitor[] getInterfaces() {
    Hashtable<String, InterfaceWrapper> interfaces = memoryContainer.getInterfaces();
    synchronized (interfaces) {
      InterfaceMonitor[] result = new InterfaceMonitor[interfaces.size()];
      interfaces.values().toArray(result);
      return result;
    }
  }

  /** @see com.sap.engine.frame.container.monitor.SystemMonitor#getService(String) */
  public ServiceMonitor getService(String name) {
    return memoryContainer.getServices().get(ComponentWrapper.convertComponentName(name));
  }

  /** @see com.sap.engine.frame.container.monitor.SystemMonitor#getLibrary(String) */
  public LibraryMonitor getLibrary(String name) {
    return memoryContainer.getLibraries().get(ComponentWrapper.convertComponentName(name));
  }

  /** @see com.sap.engine.frame.container.monitor.SystemMonitor#getInterface(String) */
  public InterfaceMonitor getInterface(String name) {
    name = InterfaceWrapper.transformINameApiToIName(name);
    return memoryContainer.getInterfaces().get(ComponentWrapper.convertComponentName(name));
  }

  /** @see com.sap.engine.frame.container.monitor.SystemMonitor#getComponentDescriptorsContainingFile(String) */
  public ComponentMonitor[] getComponentDescriptorsContainingFile(String fileName) throws ServiceException {
    Set<ComponentMonitor> components = new HashSet<ComponentMonitor>();
    String[] names = searchFiles(fileName, ComponentHandler.TYPE_INTERFACE);
    for (int i = 0; i < names.length; i++) {
      names[i] = InterfaceWrapper.transformINameApiToIName(names[i]);
    }
    initComponents(components, names, memoryContainer.getInterfaces());
    initComponents(components, searchFiles(fileName, ComponentHandler.TYPE_LIBRARY), memoryContainer.getLibraries());
    initComponents(components, searchFiles(fileName, ComponentHandler.TYPE_SERVICE), memoryContainer.getServices());
    ComponentMonitor[] result = new ComponentMonitor[components.size()];
    components.toArray(result);
    return result;
  }

  private void initComponents(Set<ComponentMonitor> set, String[] names, Hashtable<String, ? extends ComponentWrapper> components) {
    for (String name : names) {
      if (components.containsKey(name)) {
        set.add(components.get(name));
      }
    }
  }

  /** @see com.sap.engine.frame.container.monitor.SystemMonitor#getInterfaceDescriptorsContainingFile(String) */
  public InterfaceMonitor[] getInterfaceDescriptorsContainingFile(String fileName) throws ServiceException {
    Set<InterfaceMonitor> interfaces = new HashSet<InterfaceMonitor>();
    String[] names = searchFiles(fileName, ComponentHandler.TYPE_INTERFACE);
    for (int i = 0; i < names.length; i++) {
      names[i] = InterfaceWrapper.transformINameApiToIName(names[i]);
    }
    for (String name : names) {
      if (memoryContainer.getInterfaces().containsKey(name)) {
        interfaces.add(memoryContainer.getInterfaces().get(name));
      }
    }
    InterfaceMonitor[] result = new InterfaceMonitor[interfaces.size()];
    interfaces.toArray(result);
    return result;
  }

  /** @see com.sap.engine.frame.container.monitor.SystemMonitor#getLibraryDescriptorsContainingFile(String) */
  public LibraryMonitor[] getLibraryDescriptorsContainingFile(String fileName) throws ServiceException {
    Set<LibraryMonitor> libraries = new HashSet<LibraryMonitor>();
    String[] names = searchFiles(fileName, ComponentHandler.TYPE_LIBRARY);
    for (String name : names) {
      if (memoryContainer.getLibraries().containsKey(name)) {
        libraries.add(memoryContainer.getLibraries().get(name));
      }
    }
    LibraryMonitor[] result = new LibraryMonitor[libraries.size()];
    libraries.toArray(result);
    return result;
  }

  /** @see com.sap.engine.frame.container.monitor.SystemMonitor#getServiceDescriptorsContainingFile(String) */
  public ServiceMonitor[] getServiceDescriptorsContainingFile(String fileName) throws ServiceException {
    Set<ServiceMonitor> services = new HashSet<ServiceMonitor>();
    String[] names = searchFiles(fileName, ComponentHandler.TYPE_SERVICE);
    for (String name : names) {
      if (memoryContainer.getServices().containsKey(name)) {
        services.add(memoryContainer.getServices().get(name));
      }
    }
    ServiceMonitor[] result = new ServiceMonitor[services.size()];
    services.toArray(result);
    return result;
  }

  /**
   * @see com.sap.engine.frame.container.monitor.SystemMonitor#getConfigurationFilesList()
   * @deprecated - use configuration library
   */
  public String[] getConfigurationFilesList() throws ConfigurationException {
    return new String[0];
  }

  /**
   * @see com.sap.engine.frame.container.monitor.SystemMonitor#getConfigurationFile(String)
   * @deprecated - use configuration library
   */
  public InputStream getConfigurationFile(String fileName) throws ConfigurationException, IOException {
    return new ByteArrayInputStream(new byte[0]);
  }

  /**
   * @see com.sap.engine.frame.container.monitor.SystemMonitor#getGlobalDispatcherProperties()
   * @deprecated
   *
   * map[0] manager_name --> default properties; map[1] manager_name --> custom properties; map[2] manager_name --> secured keys
   * map[3] service_name --> default properties; map[4] service_name --> custom properties; map[5] service_name --> secured keys
   */
  public Map[] getGlobalDispatcherProperties() throws ConfigurationException, IOException {
    return new Map[] {new HashMap(), new HashMap(), new HashMap(), new HashMap(), new HashMap(), new HashMap()};
  }

  /**
   * @see com.sap.engine.frame.container.monitor.SystemMonitor#getGlobalServerProperties()
   * @deprecated
   *
   * map[0] manager_name --> default properties; map[1] manager_name --> custom properties; map[2] manager_name --> secured keys
   * map[3] service_name --> default properties; map[4] service_name --> custom properties; map[5] service_name --> secured keys
   */
  public Map[] getGlobalServerProperties() throws ConfigurationException {
    return memoryContainer.getPersistentContainer().getGlobalProperties();
  }

  //returns component names
  private String[] searchFiles(String fileName, byte type) throws ServiceException {
    try {
      return memoryContainer.getPersistentContainer().getInstanceLevel().getComponentAccess().searchFileEntries(fileName, type);
    } catch (ClusterConfigurationException e) {
      throw new ServiceException(LOCATION, e);
    }
  }

  //////////////////////////////////////////// REFLECT /////////////////////////////////////////////////////////////////

  /** @see com.sap.engine.frame.core.reflect.ReflectContext#getCoreComponent(String) */
  public Object getCoreComponent(String name) {
    return Framework.getManager(name);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public MemoryContainer getMemoryContainer() {
    return memoryContainer;
  }

  public ContainerObjectRegistry getContainerObjectRegistry() {
    return containerObjectRegistry;
  }

  public ContainerEventRegistry getContainerEventRegistry() {
    return containerEventRegistry;
  }

  public LoadContextWrapper getLoadContext() {
    return loadContextWrapper;
  }

  public String getInstanceType() {
    return instanceType;
  }

  private void printMemoryStatistics() {
    if (memoryStatistics.bePath() && AllocationStatisticRegistry.isEnabled()) {
      Map<String, AllocationStatisticRecord> result = AllocationStatisticRegistry.getAllocationStatistic("Service .+", true, true, true);
      String[] keyArray = result.keySet().toArray(new String[result.size()]);
      Arrays.sort(keyArray);
      StringBuilder logBuffer = new StringBuilder((result.size() * 2) * 80);
      logBuffer.append("Name;Allocated Objects;Allocated Memory;Hold Objects;Hold Memory\r\n");
      String currentService = null;
      long[] total = new long[4];
      for (String key : keyArray) {
        int index = key.indexOf(' ');
        String serviceName = key.substring(index + 1, key.indexOf(' ', index + 1));
        boolean isStop = key.endsWith("stop");
        if (!serviceName.equals(currentService)) {
          logTotals(currentService, logBuffer, total);
          currentService = serviceName;
          total[0] = 0; total[1] = 0; total[2] = 0; total[3] = 0;
        }
        logBuffer.append(key);
        logBuffer.append(';');
        AllocationStatisticRecord record = result.get(key);
        logBuffer.append(record.getAllocatedObjects());
        logBuffer.append(';');
        logBuffer.append(record.getAllocatedBytes());
        logBuffer.append(';');
        logBuffer.append(record.getHoldObjects());
        logBuffer.append(';');
        logBuffer.append(record.getHoldBytes());
        logBuffer.append("\r\n");
        if (!isStop) {
          total[0] += record.getAllocatedObjects();
          total[1] +=record.getAllocatedBytes();
          total[2] += record.getHoldObjects();
          total[3] += record.getHoldBytes();
        }
      }
      logTotals(currentService, logBuffer, total);
      SystemEnvironment.STD_OUT.println(AllocationStatisticRegistry.generateTextReport(result));
      memoryStatistics.pathT(logBuffer.toString());
    }
  }

  private void logTotals(String serviceName, StringBuilder logBuffer, long[] total) {
    if (serviceName != null) {
      logBuffer.append("Service ");
      logBuffer.append(serviceName);
      logBuffer.append(" total");
      logBuffer.append(';');
      logBuffer.append(total[0]);
      logBuffer.append(';');
      logBuffer.append(total[1]);
      logBuffer.append(';');
      logBuffer.append(total[2]);
      logBuffer.append(';');
      logBuffer.append(total[3]);
      logBuffer.append("\r\n");
    }
  }

  private void printTimeStatistics() {
    if (timeStatistics.bePath()) {
      int maxDigit = 6;
      int maxLenght = 0;
      String name = "Name";
      boolean isMemoryStatisticsActive = memoryStatistics.bePath() && AllocationStatisticRegistry.isEnabled();
      boolean isConsecutiveStartDisabled = !SystemProperties.getBoolean("consecutive");
      if (isMemoryStatisticsActive && isConsecutiveStartDisabled) {
        name += " (Memory statistics is active and Consecutive start not switched on - time statistics might not be accurate.)";
      } else if (isMemoryStatisticsActive) {
        name += " (Memory statistics is active - time statistics might not be accurate.)";
      } else if (isConsecutiveStartDisabled) {
        name += " (Consecutive start not switched on - time statistics might not be accurate.)";
      }
      for (String key : timeStatisticsRegistry.keySet()) {
        if (key.length() > maxLenght) maxLenght = key.length();
      }
      if (maxLenght < name.length()) maxLenght = name.length();
      StringBuilder outBuffer = new StringBuilder((timeStatisticsRegistry.size() + 5) * (maxLenght + 2 * maxDigit + 20));
      StringBuilder logBuffer = new StringBuilder((timeStatisticsRegistry.size() + 1) * (maxLenght / 2 + 20));
      fillChar(outBuffer, maxLenght +  2 * maxDigit + 18, '-');
      outBuffer.append("\r\n");
      outBuffer.append("| ");
      outBuffer.append(name);
      fillChar(outBuffer, maxLenght - name.length(), ' ');
      outBuffer.append(" | Time");
      fillChar(outBuffer, maxDigit + 4 - " Time".length(), ' ');
      outBuffer.append("| CPU");
      fillChar(outBuffer, maxDigit + 8 - " CPU".length(), ' ');
      outBuffer.append("|\r\n");
      fillChar(outBuffer, maxLenght +  2 * maxDigit + 18, '-');
      outBuffer.append("\r\n");
      logBuffer.append(name);
      logBuffer.append(";Time;CPU\r\n");
      String[] keyArray = timeStatisticsRegistry.keySet().toArray(new String[timeStatisticsRegistry.size()]);
      Arrays.sort(keyArray);
      for (String key : keyArray) {
        outBuffer.append("| ");
        outBuffer.append(key);
        fillChar(outBuffer, maxLenght - key.length(), ' ');
        outBuffer.append(" |");
        logBuffer.append(key);
        logBuffer.append(';');
        long[] val = timeStatisticsRegistry.get(key);
        String t = Long.toString(val[0]);
        fillChar(outBuffer, maxDigit - t.length(), ' ');
        outBuffer.append(t);
        outBuffer.append(" ms |");
        logBuffer.append(t);
        logBuffer.append(';');
        String c = Long.toString(val[1]);
        fillChar(outBuffer, maxDigit - c.length(), ' ');
        outBuffer.append(c);
        outBuffer.append(" CPU ms |\r\n");
        logBuffer.append(c);
        logBuffer.append("\r\n");
      }
      fillChar(outBuffer, maxLenght +  2 * maxDigit + 18, '-');
      outBuffer.append("\r\n");
      SystemEnvironment.STD_OUT.println(outBuffer.toString());
      timeStatistics.pathT(logBuffer.toString());
    }
  }

  private void fillChar(StringBuilder buffer, int count, char ch) {
    for (int i = 0; i < count; i++) {
      buffer.append(ch);
    }
  }

  /**
   * Add or update time statistics entry
   *
   * @param key - time key
   * @param times - times array - [time, CPU time]
   */
  static void setTimeStatistic(String key, long[] times) {
    synchronized (timeStatisticsRegistry) {
      if (timeStatisticsRegistry.containsKey(key)) {
        long[] val = timeStatisticsRegistry.get(key);
        val[0] += times[0];
        val[1] += times[1];
      } else {
        timeStatisticsRegistry.put(key, times);
      }
    }
  }

}