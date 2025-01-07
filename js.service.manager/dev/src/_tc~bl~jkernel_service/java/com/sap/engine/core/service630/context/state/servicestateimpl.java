package com.sap.engine.core.service630.context.state;

import com.sap.engine.frame.state.ServiceState;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.cluster.event.ClusterEventListener;
import com.sap.engine.frame.cluster.event.ServiceEventListener;
import com.sap.engine.frame.cluster.message.ListenerAlreadyRegisteredException;
import com.sap.engine.frame.state.ManagementInterface;
import com.sap.engine.frame.state.PersistentContainer;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.runtime.RuntimeConfiguration;
import com.sap.engine.core.service630.container.ServiceContainerImpl;
import com.sap.engine.core.service630.container.ServiceWrapper;
import com.sap.engine.core.service630.container.PersistentHelperImpl;
import com.sap.engine.core.cluster.ClusterManager;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;

import java.util.Properties;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.io.File;

/**
 * This class implements ServiceState.
 *
 * @see com.sap.engine.frame.state.ServiceState
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class ServiceStateImpl implements ServiceState {

  private ServiceContainerImpl container;
  private ServiceWrapper wrapper;

  private String workingDirectory;
  private String persistentDirectory;

  private PersistentHelperImpl persistentHelper;
  private ClusterManager clusterManager;

  public ServiceStateImpl(ServiceContainerImpl container, ServiceWrapper wrapper) {
    this.container = container;
    this.wrapper = wrapper;
    this.clusterManager = (ClusterManager) Framework.getManager(Names.CLUSTER_MANAGER);
  }

  public PersistentContainer getPersistentContainer() {
    if (persistentHelper == null) {
      persistentHelper = new PersistentHelperImpl(wrapper.getComponentName(), container);
    }
    return persistentHelper;
  }

  public String getPersistentDirectoryName() {
    if (persistentDirectory == null) {
      File dirFile = new File(ServiceContainerImpl.WORK_FOLDER, wrapper.getComponentName() + File.separator);
      dirFile.mkdirs();
      persistentDirectory = dirFile.getPath();
    }
    return persistentDirectory;
  }

  public String getWorkingDirectoryName() {
    if (workingDirectory == null) {
      File dirFile = new File(ServiceContainerImpl.WORK_FOLDER, wrapper.getComponentName() + File.separator);
      dirFile.mkdirs();
      workingDirectory = dirFile.getPath();
    }
    return workingDirectory;
  }

  public void stopMe() throws ServiceException {
    container.getMemoryContainer().stopServiceRuntime(wrapper);
  }

  public String getServiceName() {
    return wrapper.getComponentName();
  }

  public Properties getProperties() {
    return wrapper.getProperties();
  }

  public String getProperty(String key) {
    return wrapper.getProperty(key);
  }

  public String getProperty(String key, String defaultValue) {
    String result = wrapper.getProperty(key);
    return (result != null) ? result : defaultValue;
  }

  public void registerManagementInterface(ManagementInterface managementInterface) throws ServiceException {
    if (managementInterface == null) {
      throw new NullPointerException();
    }
    wrapper.setManagementInterface(managementInterface);
  }

  public void unregisterManagementInterface() {
    wrapper.removeManagementInterface();
  }

  public void registerContainerEventListener(ContainerEventListener listener) {
    container.getContainerEventRegistry().registerContainerEventListener(wrapper, listener, 0xffffffff, null);
  }

  public void registerContainerEventListener(int mask, Set names, ContainerEventListener listener) {
    container.getContainerEventRegistry().registerContainerEventListener(wrapper, listener, mask, names);
  }

  public void unregisterContainerEventListener() {
    container.getContainerEventRegistry().unregisterContainerEventListener(wrapper);
  }

  public void registerClusterEventListener(ClusterEventListener listener) throws ListenerAlreadyRegisteredException {
    clusterManager.registerClusterListener(listener, wrapper.getComponentName());
  }

  public void unregisterClusterEventListener() {
    clusterManager.unregisterClusterListener(wrapper.getComponentName());
  }

  public void registerServiceEventListener(ServiceEventListener listener) throws ListenerAlreadyRegisteredException {
    clusterManager.registerServiceListener(wrapper.getComponentName(), listener);
  }

  public void unregisterServiceEventListener() {
    clusterManager.unregisterServiceListener(wrapper.getComponentName());
  }

  public void registerRuntimeConfiguration(RuntimeConfiguration runtime) {
    if (runtime == null) {
      throw new NullPointerException();
    }
    wrapper.setRuntimeConfiguration(runtime);
  }

  public void unregisterRuntimeConfiguration() {
    wrapper.setRuntimeConfiguration(null);
  }

}