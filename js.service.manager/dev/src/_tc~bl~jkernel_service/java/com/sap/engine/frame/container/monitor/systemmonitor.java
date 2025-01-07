package com.sap.engine.frame.container.monitor;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.ServiceException;

import java.io.InputStream;
import java.io.IOException;
import java.util.Map;

/**
 * General interface for service container monitoring and configuration
 *
 * @author Dimitar Kostadinov
 */
public interface SystemMonitor {

  /**
   * @deprecated
   */
  public final static byte COMMUNICATION_CONTAINER = ClusterElement.DISPATCHER;

  /**
   * @deprecated
   */
  public final static byte APPLICATION_CONTAINER = ClusterElement.SERVER;

  /**
   * The container is starting
   */
  public static final byte STATE_STARTING = 1;

  /**
   * The container is started
   */
  public static final byte STATE_STARTED = 2;

  /**
   * The container is stopping
   */
  public static final byte STATE_STOPPING = 3;

  /**
   * The container is stopped
   */
  public static final byte STATE_STOPPED = 4;

  /**
   * Returns the current container state. The state can be STATE_STARTING, STATE_STARTED, STATE_STOPPING and STATE_STOPPED
   *
   * @return current state
   */
  public byte getContainerState();

  /**
   * Returns all service monitors
   *
   * @see com.sap.engine.frame.container.monitor.ServiceMonitor
   * @return all service monitors
   */
  public ServiceMonitor[] getServices();

  /**
   * Returns all library monitors
   *
   * @see com.sap.engine.frame.container.monitor.LibraryMonitor
   * @return all library monitors
   */
  public LibraryMonitor[] getLibraries();

  /**
   * Returns all interface monitors
   *
   * @see com.sap.engine.frame.container.monitor.InterfaceMonitor
   * @return all interface monitors
   */
  public InterfaceMonitor[] getInterfaces();

  /**
   * Returns service monitor by given name
   *
   * @see com.sap.engine.frame.container.monitor.ServiceMonitor
   * @param name - the service name
   * @return service monitor
   */
  public ServiceMonitor getService(String name);

  /**
   * Returns library monitor by given name
   *
   * @see com.sap.engine.frame.container.monitor.LibraryMonitor
   * @param name - the library name
   * @return library monitor
   */
  public LibraryMonitor getLibrary(String name);

  /**
   * Returns interface monitor by given name
   *
   * @see com.sap.engine.frame.container.monitor.InterfaceMonitor
   * @param name - the interface name
   * @return interface monitor
   */
  public InterfaceMonitor getInterface(String name);

  /**
   * Returns components that contains in their descriptors container specified file name
   *
   * @param fileName the name of the file
   * @return components that contains in descriptors container requested file
   * @throws ServiceException if an error occurs
   */
  public ComponentMonitor[] getComponentDescriptorsContainingFile(String fileName) throws ServiceException;

  /**
   * Returns services that contains in their descriptors container specified file name
   *
   * @param fileName the name of the file
   * @return services that contains in descriptors container requested file
   * @throws ServiceException if an error occurs
   */
  public ServiceMonitor[] getServiceDescriptorsContainingFile(String fileName) throws ServiceException;

  /**
   * Returns libraries that contains in their descriptors container specified file name
   *
   * @param fileName the name of the file
   * @return libraryes that contains in descriptors container requested file
   * @throws ServiceException if an error occurs
   */
  public LibraryMonitor[] getLibraryDescriptorsContainingFile(String fileName) throws ServiceException;

  /**
   * Returns interfaces that contains in their descriptors container specified file name
   *
   * @param fileName the name of the file
   * @return interfaces that contains in descriptors container requested file
   * @throws ServiceException if an error occurs
   */
  public InterfaceMonitor[] getInterfaceDescriptorsContainingFile(String fileName) throws ServiceException;

  /**
   * @deprecated - the container type can be only server (APPLICATION_CONTAINER) since despatcher is droped
   */
  public byte getContainerType();

  /**
   * @deprecated - use configuration library
   */
  public String[] getConfigurationFilesList() throws ConfigurationException;

  /**
   * @deprecated - use configuration library
   */
  public InputStream getConfigurationFile(String fileName) throws ConfigurationException, IOException;

  /**
   * @deprecated empty <code>Map</code> array is returned
   *
   * map[0] manager_name --> default properties; map[1] manager_name --> custom properties; map[2] manager_name --> secured keys
   * map[3] service_name --> default properties; map[4] service_name --> custom properties; map[5] service_name --> secured keys
   */
  public Map[] getGlobalDispatcherProperties() throws ConfigurationException, IOException;

  /**
   * @deprecated - use configuration library
   *
   * map[0] manager_name --> default properties; map[1] manager_name --> custom properties; map[2] manager_name --> secured keys
   * map[3] service_name --> default properties; map[4] service_name --> custom properties; map[5] service_name --> secured keys
   */
  public Map[] getGlobalServerProperties() throws ConfigurationException, IOException;

}

