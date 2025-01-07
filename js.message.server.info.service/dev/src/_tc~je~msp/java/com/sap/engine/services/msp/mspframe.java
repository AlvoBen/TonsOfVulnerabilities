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
package com.sap.engine.services.msp;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;

import com.sap.engine.admin.model.ManagementModelManager;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceContext;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.event.ContainerEventListenerAdapter;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.ShellInterface;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.msp.itsam.SAP_ITSAMMessageServer;
import com.sap.engine.services.msp.itsam.SAP_ITSAMMessageServerWrapper;
import com.sap.engine.services.msp.itsam.SAP_ITSAMMessageServer_Impl;
import com.sap.engine.services.msp.shell.MSClientStatistic;
import com.sap.engine.services.msp.shell.MSHardwareIdCommand;
import com.sap.engine.services.msp.shell.MSInfoCommand;
import com.sap.engine.services.msp.shell.MSLCR;
import com.sap.engine.services.msp.shell.MSParams;
import com.sap.engine.services.msp.shell.MSServiceInfo;
import com.sap.engine.services.msp.shell.MSStatistic;
import com.sap.engine.services.msp.shell.MSSystemId;
import com.sap.engine.services.msp.shell.MSTraceLevelCommand;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Dispatcher frame for the service
 */
public class MSPFrame extends ContainerEventListenerAdapter implements ApplicationServiceFrame {

	private static final String JMX_SERVICE = "jmx";
	private static final String BASICADMIN_SERVICE = "basicadmin";
	private static final String SHELL_INTERFACE = "shell";
	private static final String LOG_INTERFACE = "log";
	private static final String UNABLE_TO_REGISTER_ITSAM_MODEL_PROVIDER_MBEAN = 
		"Unable to register model provider mbean for ITSAM Manager";
	private static final String ITSAM_MODEL_PROVIDER_MBEAN_ALREADY_REGISTERED = 
		"The model provider mbean for ITSAM Manager is already registered";
	private static final String ERROR_UNREGISTERING_ITSAM_MODEL_PROVIDER_MBEAN = 
		"Error occured when attempting to unregister the model provider mbean for ITSAM Manager";
  /**
   * Stores the service context. Used in shell commands and for unregistering.
   */
  private ServiceContext serviceContext = null;

  /**
   * Stores the shell service interface. Used for unregistering
   */
  private ShellInterface shell = null;

  /**
   * Stores the shell commands ID. Used for unregistering
   */
  private int commandsId = -1;

  /**
   * Stores the runtime interface
   */
  private MSPRuntimeInterface runtimeInterface = null;
  
  /** object name for itsam cim mbean */
  private ObjectName _objNameITSAM;

  /**
   * Used for logging
   */
  private static final Location location = Location.getLocation(MSPFrame.class);
  private static final Category category = Category.SYS_SERVER;

  /**
   * Stores the processor used by the shell commands and runtime interface
   */
  private MSPProcessor mspProcessor = null;

  /**
   * This method is invoked by the system when a service is started. In it
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
    // Initialize
    this.serviceContext = serviceContext;
    this.mspProcessor = new MSPProcessor(serviceContext.getClusterContext().getClusterMonitor().getMessageServerBridge());
    // Register the runtime interface
    try {
      this.runtimeInterface = new MSPRuntimeInterfaceImpl(mspProcessor);
      serviceContext.getContainerContext().getObjectRegistry().registerInterface(runtimeInterface);
      serviceContext.getServiceState().registerManagementInterface(runtimeInterface);
    } catch (RemoteException re) {
      category.warningT(location, "Can not register the runtime interface !");
      LoggingHelper.traceThrowable(Severity.WARNING, location, "start(ApplicationServiceContext)", re);
    }
    int mask = 	ContainerEventListener.MASK_INTERFACE_AVAILABLE | 
								ContainerEventListener.MASK_INTERFACE_NOT_AVAILABLE |
								ContainerEventListener.MASK_SERVICE_STARTED | 
								ContainerEventListener.MASK_BEGIN_SERVICE_STOP;
    Set names = new HashSet(3);
    names.add(SHELL_INTERFACE);
    names.add(LOG_INTERFACE);
    names.add(BASICADMIN_SERVICE);
    names.add(JMX_SERVICE);
    serviceContext.getServiceState().registerContainerEventListener(mask, names, this);
    location.pathT("Service started !");
  }

  /**
   * This is method for stopping the service. Service has to free all allocated
   * resources and to achieve the state before starting.
   *
   * @exception   com.sap.engine.frame.ServiceRuntimeException  Thrown if some problem occures while
   * stopping the service.
   *
   */
  public void stop() throws ServiceRuntimeException {
  	unregisterITSAMModelProviderMBean();
    // Unregister the container listener
    serviceContext.getServiceState().unregisterContainerEventListener();
    // Unregister the shell commands if they are registered
    if (shell != null) {
      shell.unregisterCommands(commandsId);
    }
    // Unregister the runtime interface
    if (runtimeInterface != null) {
      ((ApplicationServiceContext)serviceContext).getContainerContext().getObjectRegistry().unregisterInterface();
      serviceContext.getServiceState().unregisterManagementInterface();
    }
    location.pathT("Service stopped !");
  }

  public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
    // Register the shell commands
    if (interfaceName.equals(SHELL_INTERFACE)) {
      shell = (ShellInterface)interfaceImpl;
      Command cmds[] = {new MSHardwareIdCommand(mspProcessor), new MSSystemId(mspProcessor),
                        new MSInfoCommand(mspProcessor), new MSParams(mspProcessor),
                        new MSStatistic(mspProcessor), new MSClientStatistic(mspProcessor),
                        new MSServiceInfo(mspProcessor), new MSTraceLevelCommand(mspProcessor),
                        new MSLCR(mspProcessor)};
      commandsId = shell.registerCommands(cmds);
    }
  }

  public void interfaceNotAvailable(String interfaceName) {
  }

  public void serviceStarted(String serviceName, Object serviceInterface) {
  	String method = "serviceStarted";
  	location.entering(method);
    if (serviceName.equals(BASICADMIN_SERVICE)) {
    	registerITSAMModelProviderMBean();
    }
    location.exiting(method);
  }
  
  public void beginServiceStop(String serviceName) {
  	String method = "beginServiceStop";
  	location.entering(method);
    if (serviceName.equals(JMX_SERVICE)) {
    	unregisterITSAMModelProviderMBean();
    }
    location.exiting(method);
  }
  public boolean setServiceProperty(String key, String value) {
    return false;
  }

  public boolean setServiceProperties(Properties serviceProperties) {
    return false;
  }
  
  private void registerITSAMModelProviderMBean() {
  	String method = "registerITSAMModelProviderMBean";
  	location.entering(method);
  	if (runtimeInterface!=null) {
	    try {
	    	MBeanServer mbs = (MBeanServer) new InitialContext().lookup(JMX_SERVICE);
	    	
	    	ObjectName objectName = getObjectNameForITSAM();
	    		    	
	    	if (!mbs.isRegistered(objectName)) {
	    		SAP_ITSAMMessageServer_Impl impl = new SAP_ITSAMMessageServer_Impl(runtimeInterface);
	    		SAP_ITSAMMessageServerWrapper wrapper = new SAP_ITSAMMessageServerWrapper(impl);
	    		mbs.registerMBean(wrapper, objectName);
	    	}	
	    	else{
	    		location.debugT(ITSAM_MODEL_PROVIDER_MBEAN_ALREADY_REGISTERED);
	    	}
	    } 
	    catch (Exception e) {
	    	location.traceThrowableT(Severity.DEBUG, UNABLE_TO_REGISTER_ITSAM_MODEL_PROVIDER_MBEAN, e);
	    }    
  	}
    location.exiting(method);
  }
  
  private void unregisterITSAMModelProviderMBean() {  	
  	String method = "unregisterITSAMModelProviderMBean";
  	location.entering(method);
    try {
    	MBeanServer mbs = (MBeanServer) new InitialContext().lookup(JMX_SERVICE);
    	
    	ObjectName objectName = getObjectNameForITSAM();
    	    	   	
    	if (mbs.isRegistered(objectName)) {
    		mbs.unregisterMBean(objectName);
    	}
    } 
    catch (Exception e) {
    	location.traceThrowableT(Severity.DEBUG, ERROR_UNREGISTERING_ITSAM_MODEL_PROVIDER_MBEAN, e);
    }    
    location.exiting(method);
  }  
  
  //Create Object name.
  private ObjectName getObjectNameForITSAM() throws Exception{  	
  	if(_objNameITSAM==null){
  		
	  	String simClass = "SAP_ITSAMMessageServer"; 
	  	String simParentClass = "SAP_ITSAMJ2eeCluster";
	  	
	  	String clusterNameKey = simParentClass + ".Name";
	  	String clusterCreationClassKey = simParentClass + ".CreationClassName";
	  	
	  	ManagementModelManager mmm = 
	  		(ManagementModelManager)((ApplicationServiceContext)serviceContext).
	  		getContainerContext().getObjectRegistry().getServiceInterface(BASICADMIN_SERVICE);
	  	
	  	ObjectName onCluster = mmm.getManagementModelHelper().getSAP_ITSAMJ2eeClusterObjectName();
	  	
	  	String clusterNameValue = onCluster.getKeyProperty(clusterNameKey);
	  	String clusterCreationClassValue = onCluster.getKeyProperty(clusterCreationClassKey);
	  		  	
	  	String pattern = ":cimclass=" + simClass + "," + 
	  			"version=1.0," + 
	  			"type=" + simParentClass + "." + simClass + "," + 
	  			clusterNameKey + "=" + clusterNameValue + "," + 
	  			clusterCreationClassKey + "=" + clusterCreationClassValue + "," + 
	  			simClass + ".ElementName=" + simClass + "," + 
	  			"name=" + SAP_ITSAMMessageServer.MBEAN_NAME;
	  	
	  	_objNameITSAM = new ObjectName(pattern);
  	}
  	return _objNameITSAM;
  }
}
