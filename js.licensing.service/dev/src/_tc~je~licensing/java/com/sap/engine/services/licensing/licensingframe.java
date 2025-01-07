/*
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.licensing;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.engine.admin.model.ManagementModelHelper;
import com.sap.engine.admin.model.ManagementModelManager;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceContext;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.registry.ObjectRegistry;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.ShellInterface;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.licensing.itsam.SAP_ITSAMJ2eeLicensingManagementServiceWrapper;
import com.sap.engine.services.licensing.itsam.SAP_ITSAMJ2eeLicensingManagementService_Impl;
import com.sap.engine.services.licensing.shell.MSHardwareId;
import com.sap.engine.services.licensing.shell.MSSystemId;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Jochen Mueller
 * @version 1.1
 */
public class LicensingFrame implements ApplicationServiceFrame, ContainerEventListener {
  //logging and tracing
  private static final Location location = Location.getLocation(LicensingFrame.class);
  //logging and tracing
  private static final Category category = LoggingHelper.SYS_SERVER;

  //name under which the naming interface is bound to jndi 
  private static final String JNDI_SERVICE_NAME = "naming";

  //the name of the licensing service
  private static final String OWN_NAME = "Licensing service";

  //Stores the service context. Used in shell commands and for unregistering.
  private ServiceContext serviceContext = null;
  
  /** The ObjectRegistry (created in the start-method) */
  private ObjectRegistry _objectRegistry;

  //Stores the shell service interface. Used for unregistering
  private ShellInterface shell = null;

  //Stores the shell commands ID. Used for unregistering
  private int commandsId = -1;

  //Stores the runtime interface of the service
  private LicensingRuntimeInterface runtimeInterface = null;

  //Used to determine the type of the element the service runs on
  private boolean isServer = false;

  //Stores the processor used by the shell commands and runtime interface
  private LicensingProcessor licensingProcessor = null;
  
  /** object name for itsam cim mbean */
  private ObjectName _objNameITSAM;

  /**
   * This method is invoked by the system when a service is started. The
   * service can allocate resources and make connection to other components
   * from the system.
   *
   * @param   serviceContext  This parameter is connection from the service to
   * resources provided by the system.
   * @exception   ServiceException  Thrown if some problem occures while the
   * service initializes resources or establishes connections to other modules
   * from the system.
   */
  public void start(ApplicationServiceContext serviceContext) throws ServiceException {
    this.serviceContext = serviceContext;
    this.licensingProcessor = new LicensingProcessor(serviceContext.getCoreContext());
    // Register the runtime interface
    this.runtimeInterface = new LicensingRuntimeInterfaceImpl(licensingProcessor);
    serviceContext.getContainerContext().getObjectRegistry().registerInterface(runtimeInterface);
    serviceContext.getServiceState().registerManagementInterface(runtimeInterface);
    //  Listener for basicadmin and jmx services is added
    int mask = ContainerEventListener.MASK_INTERFACE_AVAILABLE | ContainerEventListener.MASK_INTERFACE_NOT_AVAILABLE | //shell service
               ContainerEventListener.MASK_SERVICE_STARTED | ContainerEventListener.MASK_BEGIN_SERVICE_STOP; //basicadmin and jmx service

    Set names = new HashSet(3);
    names.add("shell");
    names.add("basicadmin");
    names.add("jmx");
    serviceContext.getServiceState().registerContainerEventListener(mask, names, this);
    //bind to naming, not sure if naming is already up and running 
    bindToNaming(false);
    _objectRegistry = serviceContext.getContainerContext().getObjectRegistry();
  }

  /**
   * This is method for stopping the service. Service has to free all allocated
   * resources and to achieve the state before starting.
   *
   * @exception   com.sap.engine.frame.ServiceRuntimeException  Thrown if some problem occures while
   * stopping the service.
   */
  public void stop() throws ServiceRuntimeException {
    try{
      // Unregister the container listener
      serviceContext.getServiceState().unregisterContainerEventListener();
      // Unregister the shell commands if they are registered
      if (shell != null) {
        shell.unregisterCommands(commandsId);
      }
      // Unregister the runtime interface
      if (runtimeInterface != null) {
        ((ApplicationServiceContext) serviceContext).getContainerContext().getObjectRegistry().unregisterInterface();
        serviceContext.getServiceState().unregisterManagementInterface();
        //unbind from naming
        unbindFromNaming(false);
      }
    }finally {
     //Unregister the CIM Model MBean used by ITSAM.
     removeFromITSAM();
    }
  }

  /**
   * Binds the licensing runtime interface to jndi if the naming service is started.
   * 
   * @see  com.sap.engine.frame.container.event.ContainerEventListener#serviceStarted(String, Object)
   */
  public void serviceStarted(String serviceName, Object serviceInterface) {
    String METHOD = "serviceStarted(serviceName, serviceInterface)";
    location.pathT(METHOD, "serviceName={0}", new Object[] { serviceName });
    
    if (serviceName.equals(JNDI_SERVICE_NAME)) {
      bindToNaming(true);
    } else if (serviceName.equals("basicadmin")){ 
      addToITSAM();
    }
  }

  /** 
   * @see  com.sap.engine.frame.container.event.ContainerEventListener#interfaceAvailable(String, Object)
   */
  public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
    // Register the shell commands
    if (interfaceName.equals("shell")) {
      shell = (ShellInterface) interfaceImpl;
      Command cmds[] = { new MSHardwareId(licensingProcessor), new MSSystemId(licensingProcessor)};
      commandsId = shell.registerCommands(cmds);
    }
  }

  /** 
   * @see  com.sap.engine.frame.container.event.ContainerEventListener#serviceNotStarted(String)
   */
  public void serviceNotStarted(String serviceName) {
  }

  /** 
   * @see  com.sap.engine.frame.container.event.ContainerEventListener#beginServiceStop(String)
   */
  public void beginServiceStop(String serviceName) {
    String METHOD = "beginServiceStop(serviceName)";
    location.pathT(METHOD, "serviceName={0}", new Object[] { serviceName });
    if (serviceName.equals("jmx")){ 
      removeFromITSAM(); 
    }
  }
  
  /** 
   * @see  com.sap.engine.frame.container.event.ContainerEventListener#serviceStopped(String)
   */
  public void serviceStopped(String serviceName) {
  }

  /** 
   * @see  com.sap.engine.frame.container.event.ContainerEventListener#containerStarted()
   */
  public void containerStarted() {
  }

  /** 
   * @see  com.sap.engine.frame.container.event.ContainerEventListener#beginContainerStop()
   */
  public void beginContainerStop() {
  }

  /** 
   * @see  com.sap.engine.frame.container.event.ContainerEventListener#interfaceNotAvailable(String)
   */
  public void interfaceNotAvailable(String interfaceName) {
  }

  /** 
   * @see  com.sap.engine.frame.container.event.ContainerEventListener#markForShutdown(long)
   */
  public void markForShutdown(long time) {
  }

  /** 
   * @see  com.sap.engine.frame.container.event.ContainerEventListener#setServiceProperty(String, String)
   */
  public boolean setServiceProperty(String key, String value) {
    return false;
  }

  /** 
   * @see  com.sap.engine.frame.container.event.ContainerEventListener#setServiceProperties(Properties)
   */
  public boolean setServiceProperties(Properties serviceProperties) {
    return false;
  }

  /**
   * Binds the runtime interface to jndi.
   * @param expectNamingAvailable indicates if the naming service is running or not
   */
  private void bindToNaming(boolean expectNamingAvailable) {
    String METHOD = "bindToNaming(expectNamingAvailable)";
    try {
      Context jndi = getInitialContext(expectNamingAvailable);
      if (jndi == null)
        return;
      jndi.rebind(LicensingRuntimeInterface.JNDI_NAME, runtimeInterface);
    } catch (Exception e) {
      category.errorT(location, METHOD, "{0} could not be bound in JNDI", new Object[] { OWN_NAME });
    }
  }

  /**
   * Unbinds the runtime interface from jndi.
   * @param expectNamingAvailable indicates if the naming service is running or not
   */
  private void unbindFromNaming(boolean expectNamingAvailable) {
    String METHOD = "unbindFromNaming(expectNamingAvailable)";
    try {
      Context jndi = getInitialContext(expectNamingAvailable);
      if (jndi == null)
        return;
      jndi.unbind(LicensingRuntimeInterface.JNDI_NAME);
    } catch (NamingException e) {
      category.errorT(location, METHOD, "{0} could not be unbound from JNDI", new Object[] { OWN_NAME });
    }
  }

  /**
   * Returns the initial context of the engine that can be used to do 
   * lookups from the jndi.
   * @param expectNamingAvailable indicates if the naming service is running or not
   * @throws NamingException will be thrown if there is a problem with getting the naming and expectNamingAvailable is <code>true</code>
   * @return InitialContext the jndi context of the engine
   */
  private InitialContext getInitialContext(boolean expectNamingAvailable) throws NamingException {
    String METHOD = "getInitialContext(expectNamingAvailable)";
    location.pathT(METHOD, "expectNamingAvailable={0}", new Object[] { new Boolean(expectNamingAvailable)});
    try {
      InitialContext jndi = new InitialContext();
      location.pathT(METHOD, "success");
      return jndi;
    } catch (NamingException e) {
      location.pathT(METHOD, "exception={0}", new Object[] { e });
      if (expectNamingAvailable)
        throw e;
      else
        return null;
    }
  }
  
  // Register CIM Model MBean (SAP_ITSAMJ2eeLicensingManagementServiceWrapper), 
  // used by ITSAM. True for Rio or latest engine's versions.
  // All related ITSAM classes are generated automatically by java code generator plug-in.
  // All methods declared in this MBean has the same signature as methods declared in LicensingRuntimeInterface.
  // We are passing previously created LicensingRuntimeInterface as a parameter.
  // Each method of this MBean calls corresponding method from the runtime interface,
  private void addToITSAM(){ 
     String METHOD = "addToITSAM()";
     try {
       location.pathT(METHOD, "Begin");
       MBeanServer mbs = (MBeanServer) new InitialContext().lookup("jmx");
       ObjectName objName = getObjectNameForITSAM();
       
       if(!mbs.isRegistered(objName)){
           // Create an instance by using runtime interface as a parameter
           SAP_ITSAMJ2eeLicensingManagementService_Impl mbeanImpl = new SAP_ITSAMJ2eeLicensingManagementService_Impl(runtimeInterface);
           
           // Now we register the ITSAM MBean, that resides on each node.           
           mbs.registerMBean(new SAP_ITSAMJ2eeLicensingManagementServiceWrapper(mbeanImpl), objName);
            
           location.pathT(METHOD, "Registered in MBean Server");
       } else{
           location.pathT(METHOD, "Already registered in MBean Server");
       }
     }catch (Exception ex){
       LoggingHelper.logThrowable(Severity.ERROR, category, location, METHOD, ex);
     }finally{
       location.pathT(METHOD, "End");
     }
    }
    
  //Unregister MBean
  private void removeFromITSAM(){
    String METHOD = "removeFromITSAM()";
    location.pathT(METHOD, "Begin");
    try{
      MBeanServer mbs = (MBeanServer) new InitialContext().lookup("jmx");
      mbs.unregisterMBean(getObjectNameForITSAM());
      location.pathT(METHOD, "Unregistered from MBean Server");
    }catch(Exception ex){
      LoggingHelper.logThrowable(Severity.ERROR, category, location, METHOD, ex);
    }finally{
      location.pathT(METHOD, "End");
    }
  }
  
  private ObjectName getObjectNameForITSAM() throws Exception{   
    if(_objNameITSAM==null){
       
      String simClass = "SAP_ITSAMJ2eeLicensingManagementService";
      String simParentClass = "SAP_ITSAMJ2eeCluster";
      
      String clusterNameKey = simParentClass + ".Name";
      String clusterCreationClassKey = simParentClass + ".CreationClassName";
      
      ManagementModelManager mmm = (ManagementModelManager)_objectRegistry.getServiceInterface("basicadmin");
      ObjectName onCluster = mmm.getManagementModelHelper().getJ2EEServer();
      
      String clusterNameValue = onCluster.getKeyProperty(clusterNameKey);
      String clusterCreationClassValue = onCluster.getKeyProperty(clusterCreationClassKey);
           
      String pattern = ":cimclass=" + simClass + "," + 
          "version=1.0," + 
          "type=" + simParentClass + "." + simClass + "," + 
          clusterNameKey + "=" + clusterNameValue + "," + 
          clusterCreationClassKey + "=" + clusterCreationClassValue + "," + 
          simClass + ".ElementName=" + simClass;
       
      _objNameITSAM = new ObjectName(pattern);
    }
    return _objNameITSAM;
  }

}
