package com.sap.engine.core.service630.container;

import com.sap.engine.core.Names;
import com.sap.engine.frame.container.monitor.ServiceMonitor;
import com.sap.engine.frame.container.monitor.Reference;
import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.runtime.RuntimeConfiguration;
import com.sap.engine.frame.state.ManagementInterface;
import com.sap.engine.frame.*;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;
import com.sap.tc.logging.Category;

import java.util.*;

/**
 * Implements ServiceMonitor.
 *
 * @see com.sap.engine.frame.container.monitor.ServiceMonitor
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class ServiceWrapper extends ComponentWrapper implements ServiceMonitor {

  private String applicationFrameClassName;
  private String runtimeControlClassName;

  private ApplicationServiceFrame frameObject;

  private byte internalStatus;
  private boolean implicit;

  private ManagementInterface managementInterface;
  private RuntimeConfiguration runtimeConfiguration;

  //interface names to be provided
  private Set<String> providedInterfaces;

  //use for initial start and shutdown synchronisaton
  private int waitCount = -1;

  //use to store current properties
  private NestedProperties currentProperties;

  private static final Location location = Location.getLocation(ServiceWrapper.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  ServiceWrapper(MemoryContainer memoryContainer, Properties props) {
    super(memoryContainer, props);
    internalStatus = INTERNAL_STATUS_STOPPED;
    implicit = false;
  }

  /////////////////////////////////////// SERVICE MONITOR //////////////////////////////////////////////////////////////

  public String getApplicationFrameClassName() {
    if (location.beDebug()) {
      location.debugT("Method getApplicationFrameClassName() for service " + componentName + " returns: " + applicationFrameClassName);
    }
    return applicationFrameClassName;
  }

  public String getRuntimeControlClassName() {
    if (location.beDebug()) {
      location.debugT("Method getRuntimeControlClassName() for service " + componentName + " returns: " + runtimeControlClassName);
    }
    return runtimeControlClassName;
  }

  public boolean isCore() {
    return memoryContainer.getCoreServiceSet().contains(this);
  }

  public byte getStartupState() {
    if (currentStatus == STATUS_ACTIVE) {
      return STARTUP_STATE_STARTED;
    } else {
      return STARTUP_STATE_STOPPED;
    }
  }

  public void start() throws ServiceException {
    memoryContainer.startServiceRuntime(this);
    memoryContainer.getOperationDistributor().sendStartService(componentName);
  }

  public void stop() throws ServiceException {
    // Here we use a little complex throwable handling
    // in order to ensure ServiceStop broadcast even in case of
    // ServiceStop failure on this node.

    Throwable throwableDuringStop = null;
    try {
      memoryContainer.stopServiceRuntime(this);
    } catch (Throwable t) {
      //$JL-EXC$ -- it is okay to catch Throwable
      if (t instanceof OutOfMemoryError || t instanceof ThreadDeath) {
        //$JL-EXC$ -- it is okay to rethrow these Errors
        throw (Error) t;
      }

      // Hold on the throwable, we have to notify
      // the other instance nodes before rethrowing it
      throwableDuringStop = t;
    }

    Throwable throwableDuringBroadcast = null;
    try {
      memoryContainer.getOperationDistributor().sendStopService(componentName);
    } catch (Throwable t) {
      //$JL-EXC$ -- it is okay to catch Throwable
      if (t instanceof OutOfMemoryError || t instanceof ThreadDeath) {
        //$JL-EXC$ -- it is okay to rethrow these Errors
        throw (Error) t;
      }

      // We have to check if there is exception already from stop call above
      throwableDuringBroadcast = t;
    }

    // We cannot throw two exceptions, so pick the first one
    Throwable toBeRethrown = throwableDuringStop == null ? throwableDuringBroadcast : throwableDuringStop;
    if (toBeRethrown != null) {
      // There was error
      if (throwableDuringStop != null && throwableDuringBroadcast != null) {
        SimpleLogger.log(Severity.ERROR, Category.SYS_SERVER, location, "ASJ.krn_srv.000070",
            "Could not broadcast stop of service [{0}] in the instance. Service may still be running on other server processes in the instance", componentName);
      }

      // Cast and rethrow `toBeRethrown' because we cannot throw directly a Throwable
      if (toBeRethrown instanceof Error) {
        //$JL-EXC$ -- it is okay to rethrow Error
        throw (Error)toBeRethrown;
      } else if (toBeRethrown instanceof RuntimeException) {
        //$JL-EXC$ -- it is okay to rethrow RuntimeException
        throw (RuntimeException)toBeRethrown;
      } else if (toBeRethrown instanceof ServiceException) {
        throw (ServiceException)toBeRethrown;
      } else {
        // Unexpected Exception, has to be fixed if happens
        // Maybe someone added another exception to stopServiceRuntime()
        // or sendStopService() signatures, next to `... throws ServiceException'?
        throw new ServiceException(location, toBeRethrown);
      }
    }
  }

  public byte getStartupMode() {
    if (isDisabled()) {
      return ServiceMonitor.DISABLED;
    } else if (memoryContainer.getStartServiceSet().contains(this)) {
      return ServiceMonitor.ALWAYS_START;
    } else {
      return ServiceMonitor.MANUAL_START;
    }
  }

  public Properties getProperties() {
    if (currentProperties == null) {
      initProperties();
    }
    if (location.beDebug()) {
      location.debugT("Method getProperties() for service " + componentName + " returns: " + currentProperties);
    }
    return (Properties) currentProperties.clone();
  }

  public String getProperty(String key) {
    if (currentProperties == null) {
      initProperties();
    }
    String result = currentProperties.getProperty(key);
    if (location.beDebug()) {
      location.debugT("Method getProperty(" + key + ") for service " + componentName + " returns: " + result);
    }
    return result;
  }

  void initProperties() {
    try {
      currentProperties = memoryContainer.getPersistentContainer().getComponentProperties(componentName, false, false);
    } catch (ServiceException e) {
      location.traceThrowableT(Severity.INFO, "initProperties() for service " + componentName, e);
      throw new ServiceRuntimeException(location, e);
    }
    if (location.beDebug()) {
      location.debugT("Method initProperties() for service " + componentName + " initializes : " + currentProperties);
    }
  }

  NestedProperties getCurrentProperties() {
    return currentProperties;
  }

  public ManagementInterface getManagementInterface() {
    if (location.beDebug()) {
      location.debugT("Method getManagementInterface() for service " + componentName + " returns: " + managementInterface);
    }
    return managementInterface;
  }

  /**
   * @see com.sap.engine.frame.container.monitor.ServiceMonitor#getRuntimeConfiguration
   */
  public RuntimeConfiguration getRuntimeConfiguration() {
    if (location.beDebug()) {
      location.debugT("Method getRuntimeConfiguration() for service " + componentName + " returns: " + runtimeConfiguration);
    }
    return runtimeConfiguration;
  }

  ///////////////////////////////////////////// INTERNAL METHODS ///////////////////////////////////////////////////////

  /**
   * Update runtime configuration. If runtime is null it's equal to unregistered operation.
   *
   * @param runtimeConfiguration
   */
  public void setRuntimeConfiguration (RuntimeConfiguration runtimeConfiguration) {
    if (location.beDebug()) {
      location.debugT("Method setRuntimeConfiguration(" + runtimeConfiguration + ") for service " + componentName + " is invoked");
    }
    if (runtimeConfiguration != null) {
      //register
      if (this.runtimeConfiguration == null) {
        if (currentProperties == null) {
          initProperties();
        }
        this.runtimeConfiguration = new RuntimeConfigurationWrapper(runtimeConfiguration, this);
      } else {
        throw new ServiceRuntimeException(location, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
                ResourceUtils.getKey(ResourceUtils.RUNTIME_CONFIGURATION_ALREADY_REGISTERED), new Object[] {componentName}));
      }
    } else {
      //unregister
      this.runtimeConfiguration = null;
    }
  }

  public void setManagementInterface(ManagementInterface mi) {
    if (location.beDebug()) {
      location.debugT("Method setManagementInterface(" + mi + ") for service " + componentName + " is invoked");
    }
    if (managementInterface != null) {
      if (location.beWarning()) {
        SimpleLogger.trace(Severity.WARNING, location, getDcName(),
                           getCSNComponent(), "ASJ.krn_srv.000067",
                           "Management interface for service [{0}] is already registered and will be overwritten with [{1}]",
                           null, componentName, mi.toString());
      }
    }
    this.managementInterface = mi;
    ContainerEvent event = new ContainerEvent();
    event.isAdmin = true;
    event.method = ContainerEventListener.MASK_MANAGEMENT_INTERFACE_REGISTERED;
    event.name = componentName;
    event.managementInterface = mi;
    memoryContainer.getServiceContainer().getContainerEventRegistry().addContainerEvent(event);
  }

  public void removeManagementInterface() {
    if (location.beDebug()) {
      location.debugT("Method removeManagementInterface() for service " + componentName + " is invoked");
    }
    if (managementInterface != null) {
      ContainerEvent event = new ContainerEvent();
      event.isAdmin = true;
      event.isBefore = true;
      event.method = ContainerEventListener.MASK_MANAGEMENT_INTERFACE_UNREGISTERED;
      event.name = componentName;
      memoryContainer.getServiceContainer().getContainerEventRegistry().addContainerEvent(event);
      managementInterface = null;
    } else {
      if (location.beWarning()) {
        SimpleLogger.trace(Severity.WARNING, location, getDcName(),
                           getCSNComponent(), "ASJ.krn_srv.000068",
                           "Management interface for service [{0}] is not registered and cannot be removed",
                           null, componentName);
      }
    }
  }

  String getType() {
    return "service";
  }

  byte getByteType() {
    return TYPE_SERVICE;
  }

  public byte getInternalStatus() {
    return internalStatus;
  }

  protected void parseProperties(Properties props) {
    applicationFrameClassName = props.getProperty("application-frame");
    runtimeControlClassName = props.getProperty("runtime-editor");
    initProviders(props);
  }

  void setClassLoader(ClassLoader loader) {
    super.setClassLoader(loader);
    if (loader == null) {
      //release frame object when unloading
      frameObject = null;
    }
  }

  private void initProviders(Properties props) {
    String property = props.getProperty("provided-interfaces");
    if (property != null) {
      providedInterfaces = new HashSet<String>();
      int count = Integer.parseInt(props.getProperty("provided-interfaces"));
      for (int i = 0; i < count; i++) {
        String name = props.getProperty("interface_name_" + i);
        String providerName = props.getProperty("interface_provider-name_" + i);
        ReferenceImpl reference = new ReferenceImpl(memoryContainer, name, providerName, Reference.REFER_INTERFACE, Reference.TYPE_SOFT);
        referenceSet.add(reference);
        String iName = reference.getName();
        providedInterfaces.add(iName);
      }
    }
  }

  Set<String> getProvidedInterfaces() {
    return providedInterfaces;
  }

  ApplicationServiceFrame getFrameClass() {
    return frameObject;
  }

  void setFrameClass(ApplicationServiceFrame frameObject) {
    this.frameObject = frameObject;
  }

  void setInternalStatus(byte internalStatus) {
    this.internalStatus = internalStatus;
    implicit = false;
  }

  boolean isImplicit() {
    return implicit;
  }

  void setImplicit() {
    this.implicit = true;
  }

  //estimate whether direct start is possible
  boolean isDirectStartPossible() {
    boolean result = true;
    for (ReferenceImpl reference : referenceSet) {
      if (reference.getType() == Reference.TYPE_HARD) {
        if (reference.getReferentType() == Reference.REFER_SERVICE) {
          ServiceWrapper service = (ServiceWrapper) reference.getReferencedComponent();
          if (service.getStatus() != ComponentMonitor.STATUS_ACTIVE) {
            result = false;
            break;
          }
        } else if (reference.getReferentType() == Reference.REFER_INTERFACE) {
          InterfaceWrapper interfaceWrapper = (InterfaceWrapper) reference.getReferencedComponent();
          ServiceWrapper provider = interfaceWrapper.getProvider();
          if (provider == null || provider.getStatus() != ComponentMonitor.STATUS_ACTIVE) {
            //interface provider can be <null> or not active
            result = false;
            break;
          }
        }
      }
    }
    return result;
  }

  //use for initial start & shutdown synchronization
  synchronized int getWaitCount() {
    return waitCount;
  }

  //initialize the wait count value
  synchronized void calculateWaitCount(boolean isReverse) {
    if (isReverse) {
      waitCount = getReverseHardReferencesCount();
    } else {
      waitCount = getHardReferencesCount();
    }
  }

  //decrease wait count with 1 and return the value
  synchronized int decreaseWaitCount() {
    return --waitCount;
  }

  //in case of predecessor start error the wait count is set negative to prevent eventual start
  synchronized void setNegativeWaitCount() {
    waitCount = -1;
  }

  //calculate hard refs - used only on initial startup
  private int getHardReferencesCount() {
    //calculate direct refs ( S --h--> S' and S --h--> I')
    int result = 0;
    for (ReferenceImpl ref : referenceSet) {
      if (ref.getType() == Reference.TYPE_HARD) {
        byte type = ref.getReferentType();
        if (type == Reference.REFER_SERVICE) {
          result++;
        } else if (type == Reference.REFER_INTERFACE) {
          //because service is in startup set its hard references to interface must be resolved against interface provider
          result++;
        }
      }
    }
    return result;
  }

  //calculate reverse hard refs - used only on shutdown
  private int getReverseHardReferencesCount() {
    //calculate direct reverse refs and indirect reverse refs across interface(S' --h--> S and S'' --h--> I--p--S)
    int result = 0;
    for (ReferenceImpl ref : reverseReferenceSet) {
      if (ref.getType() == Reference.TYPE_HARD) {
        if (ref.getReferentType() == Reference.REFER_SERVICE && ref.getReferencedComponent().getStatus() == STATUS_ACTIVE) {
          result++;
        } //count only active servises
      }
    }
    if (providedInterfaces != null) {
      for (String iName : providedInterfaces) {
        InterfaceWrapper iw = memoryContainer.getInterfaces().get(iName); //can not be null because it is active!
        for (ReferenceImpl ref : iw.getReverseReferenceSet()) {
          if (ref.getType() == Reference.TYPE_HARD) {
            if (ref.getReferentType() == Reference.REFER_SERVICE && ref.getReferencedComponent().getStatus() == STATUS_ACTIVE) {
              result++;
            } //count only active servises
          }
        }
      }
    }
    return result;
  }

  ///////////////////////////////////// DEPRECATED /////////////////////////////////////////////////////////////////////

  public void setStartupMode(byte startupMode) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("setStartupMode(" + startupMode + ")");
    }
  }

  public String getCommunicationFrameClassName() {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("getCommunicationFrameClassName()");
    }
    return null;
  }

  public Set getGlobalSecuredPropertiesKeys() {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("getGlobalSecuredPropertiesKeys()");
    }
    try {
      return memoryContainer.getPersistentContainer().getSecuredPropertiesKeys(componentName, true, false);
    } catch (ServiceException e) {
      throw new ServiceRuntimeException(location, e);
    }
  }

  public Set getLocalSecuredPropertiesKeys() {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("getLocalSecuredPropertiesKeys()");
    }
    try {
      return memoryContainer.getPersistentContainer().getSecuredPropertiesKeys(componentName, false, false);
    } catch (ServiceException e) {
      throw new ServiceRuntimeException(location, e);
    }
  }

  public Properties getGlobalDefaultProperties() {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("getGlobalDefaultProperties()");
    }
    try {
      return memoryContainer.getPersistentContainer().getComponentProperties(componentName, true, true, false);
    } catch (ServiceException e) {
      throw new ServiceRuntimeException(location, e);
    }
  }

  public Properties getGlobalCustomProperties() {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("getGlobalCustomProperties()");
    }
    try {
      return memoryContainer.getPersistentContainer().getComponentProperties(componentName, true, false, false);
    } catch (ServiceException e) {
      throw new ServiceRuntimeException(location, e);
    }
  }

  public Properties getLocalDefaultProperties() {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("getLocalDefaultProperties()");
    }
    try {
      return memoryContainer.getPersistentContainer().getComponentProperties(componentName, false, true, false);
    } catch (ServiceException e) {
      throw new ServiceRuntimeException(location, e);
    }
  }

  public Properties getLocalCustomProperties() {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("getLocalCustomProperties()");
    }
    try {
      return memoryContainer.getPersistentContainer().getComponentProperties(componentName, false, false, false);
    } catch (ServiceException e) {
      throw new ServiceRuntimeException(location, e);
    }
  }

  public boolean setProperties(Properties properties) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("setProperties(" +  properties + ")");
    }
    boolean result = false;
    ContainerEventListenerWrapper listener = memoryContainer.getServiceContainer().getContainerEventRegistry().containerEvents.get(componentName);
    if (listener != null) {
      Properties merge = getProperties();
      for (Object keyObj : properties.keySet()) {
        String key = (String) keyObj;
        merge.setProperty(key, properties.getProperty(key));
      }
      result = listener.setServiceProperties(merge);
    }
    memoryContainer.getPersistentContainer().storeComponentProps(componentName, properties, false, false, null, false);
    return result;
  }

  public synchronized boolean setProperty(String key, String value) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("setProperty(" +  key + ", " + value + ")");
    }
    boolean result = false;
    ContainerEventListenerWrapper listener = memoryContainer.getServiceContainer().getContainerEventRegistry().containerEvents.get(componentName);
    if (listener != null) {
      result = listener.setServiceProperty(key, value);
    }
    Properties tmp = new Properties();
    tmp.setProperty(key, value);
    memoryContainer.getPersistentContainer().storeComponentProps(componentName, tmp, false, false, null, false);
    return result;
  }

  public boolean notifyPropertiesChange(Properties properties) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("notifyPropertiesChange(" +  properties + ")");
    }
    boolean result = false;
    ContainerEventListenerWrapper listener = memoryContainer.getServiceContainer().getContainerEventRegistry().containerEvents.get(componentName);
    if (listener != null) {
      Properties merge = getProperties();
      for (Object keyObj : properties.keySet()) {
        String key = (String) keyObj;
        merge.setProperty(key, properties.getProperty(key));
      }
      result = listener.setServiceProperties(merge);
    }
    return result;
  }

  public void removeProperty(String key) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("removeProperty(" +  key + ")");
    }
    memoryContainer.getPersistentContainer().removeComponentProperties(componentName, new String[] {key}, false, false);
  }

  public void removeProperties(String[] keys) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("removeProperties(" +  keys + ")");
    }
    memoryContainer.getPersistentContainer().removeComponentProperties(componentName, keys, false, false);
  }

  public void removeGlobalProperties(String[] keys) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("removeGlobalProperties(" +  keys + ")");
    }
    memoryContainer.getPersistentContainer().removeComponentProperties(componentName, keys, true, false);
  }

  public void removeLocalProperties(String[] keys) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("removeLocalProperties(" +  keys + ")");
    }
    memoryContainer.getPersistentContainer().removeComponentProperties(componentName, keys, false, false);
  }

  public void setGlobalDefaultProperties(Properties properties) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("setGlobalDefaultProperties("+ properties + ")");
    }
    memoryContainer.getPersistentContainer().storeComponentProps(componentName, properties, true, true, null, false);
  }

  public void setGlobalCustomProperties(Properties properties) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("setGlobalCustomProperties("+ properties + ")");
    }
    memoryContainer.getPersistentContainer().storeComponentProps(componentName, properties, true, false, null, false);
  }

  public void setLocalDefaultProperties(Properties properties) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("setLocalDefaultProperties("+ properties + ")");
    }
    memoryContainer.getPersistentContainer().storeComponentProps(componentName, properties, false, true, null, false);
  }

  public void setLocalCustomProperties(Properties properties) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("setLocalCustomProperties("+ properties + ")");
    }
    memoryContainer.getPersistentContainer().storeComponentProps(componentName, properties, false, false, null, false);
  }

  public void setGlobalDefaultProperties(Properties properties, Set secured) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("setGlobalDefaultProperties("+ properties + ", " + secured + ")");
    }
    memoryContainer.getPersistentContainer().storeComponentProps(componentName, properties, true, true, secured, false);
  }

  public void setGlobalCustomProperties(Properties properties, Set secured) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("setGlobalCustomProperties("+ properties + ", " + secured + ")");
    }
    memoryContainer.getPersistentContainer().storeComponentProps(componentName, properties, true, false, secured, false);
  }

  public void setLocalDefaultProperties(Properties properties, Set secured) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("setLocalDefaultProperties("+ properties + ", " + secured + ")");
    }
    memoryContainer.getPersistentContainer().storeComponentProps(componentName, properties, false, true, secured, false);
  }

  public void setLocalCustomProperties(Properties properties, Set secured) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("setLocalCustomProperties(" + properties + ", " + secured + ")");
    }
    memoryContainer.getPersistentContainer().storeComponentProps(componentName, properties, false, false, secured, false);
  }

  public void restoreGlobalProperties() throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("restoreGlobalProperties()");
    }
    memoryContainer.getPersistentContainer().restoreComponentProperties(componentName, true, null, false);
  }

  public void restoreGlobalProperty(String key) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("restoreGlobalProperty(" + key + ")");
    }
    memoryContainer.getPersistentContainer().restoreComponentProperties(componentName, true, new String[] {key}, false);
  }

  public Set[] restoreGlobalProperties(String[] keys) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("restoreGlobalProperties(" + keys + ")");
    }
    return memoryContainer.getPersistentContainer().restoreComponentProperties(componentName, true, keys, false);
  }

  public Set restoreLocalProperties() throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("restoreLocalProperties()");
    }
    return memoryContainer.getPersistentContainer().restoreComponentProperties(componentName, false, null, false)[0];
  }

  public void restoreLocalProperty(String key) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("restoreLocalProperty(" + key + ")");
    }
    memoryContainer.getPersistentContainer().restoreComponentProperties(componentName, false, new String[] {key}, false);
  }

  public void restoreLocalProperties(String[] keys) throws ServiceException {
    if (location.beDebug()) {
      logDeprecatedDegugInfo("restoreLocalProperties(" + keys + ")");
    }
    memoryContainer.getPersistentContainer().restoreComponentProperties(componentName, false, keys, false);
  }

  private void logDeprecatedDegugInfo(String info) {
    location.debugT("Deprecated method " + info + " for service " + componentName + " is invoked");
  }

}