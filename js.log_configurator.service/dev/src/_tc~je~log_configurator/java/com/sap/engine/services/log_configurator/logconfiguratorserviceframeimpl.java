package com.sap.engine.services.log_configurator;

/**
 * Title:        Logging
 * Description:  Logging API
 * Copyright:    Copyright (c) 2002
 * Company:      SAP Labs Bulgaria LTD., Sofia, Bulgaria.
 * Url:          Http://www.saplabs.bg
 *               All rights reserved.
 *
 *               This software is the confidential and proprietary information
 *               of SAP AG International ("Confidential Information").
 *               You shall not disclose such  Confidential Information
 *               and shall use it only in accordance with the terms of
 *               the license agreement you entered into with SAP AG.
 */

import java.util.HashSet;

import com.sap.engine.frame.CommunicationServiceContext;
import com.sap.engine.frame.CommunicationServiceFrame;
import com.sap.engine.frame.ServiceContext;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.cluster.ClusterContext;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.event.ContainerEventListenerAdapter;
import com.sap.engine.frame.container.monitor.SystemMonitor;
import com.sap.engine.frame.container.registry.ObjectRegistry;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.ShellInterface;
import com.sap.engine.services.log_configurator.admin.LogConfigurator;
import com.sap.engine.services.log_configurator.admin.shell.ArchiveCommand;
import com.sap.engine.services.log_configurator.admin.shell.LogCreateCommand;
import com.sap.engine.services.log_configurator.admin.shell.LogDumpsCommand;
import com.sap.engine.services.log_configurator.admin.shell.LogEditCommand;
import com.sap.engine.services.log_configurator.admin.shell.LogListCommand;
import com.sap.engine.services.log_configurator.admin.shell.LogRemoveCommand;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Miroslav Petrov
 * @version 6.30
 */
public class LogConfiguratorServiceFrameImpl 
extends ContainerEventListenerAdapter 
implements CommunicationServiceFrame {
  
  protected final Location TRACER;

  private LogInterfaceImpl logInterface = null; // To be removed.
  private ShellInterface shell = null;
  private int commandsId = -1;

  LogConfigurator logConfigurator = null;
  ServiceContext serviceContext = null;
  ClusterContext clusterContext = null;
  ObjectRegistry objectRegistry = null;
  SystemMonitor systemMonitor = null;


  /**
   * Default constructor.
   */
  public LogConfiguratorServiceFrameImpl() {
  	super();
  	this.TRACER = Location.getLocation(this.getClass());
  }


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
  public void start(CommunicationServiceContext serviceContext) throws ServiceException {
  	this.serviceContext = serviceContext;
  	this.clusterContext = serviceContext.getClusterContext();
  	this.objectRegistry = serviceContext.getContainerContext().getObjectRegistry();
  	this.systemMonitor = serviceContext.getContainerContext().getSystemMonitor();
  	init();
  }

  /**
   * This is method for stopping the service. Service has to free all allocated
   * resources and to achieve the state before starting.
   *
   * @exception   ServiceRuntimeException  Thrown if some problem occures while
   * stopping the service.
   *
   */
  public void stop() throws ServiceRuntimeException {
  	if (shell != null) {
  	  shell.unregisterCommands(commandsId);
  	  commandsId = -1;
  	  shell = null;
  	}
  	objectRegistry.unregisterInterfaceProvider("log"); // To be removed.
  	objectRegistry.unregisterInterface(); // To be removed.
  	serviceContext.getServiceState().unregisterManagementInterface();
  	serviceContext.getServiceState().unregisterContainerEventListener();
  	clusterContext.getMessageContext().unregisterListener();
  }


  void init() throws ServiceException {
  	logInterface = new LogInterfaceImpl(); // To be removed.
  	logConfigurator = new LogConfigurator(serviceContext, clusterContext);
  	objectRegistry.registerInterfaceProvider("log", logInterface); // To be removed.
  	objectRegistry.registerInterface(logConfigurator); // To be removed.
  	
    int mask = 
      ContainerEventListener.MASK_INTERFACE_AVAILABLE | 
      ContainerEventListener.MASK_INTERFACE_NOT_AVAILABLE;
  	
    HashSet names = new HashSet(1);
  	names.add("shell");
  	serviceContext.getServiceState().registerContainerEventListener(mask, names, this);
  	serviceContext.getServiceState().registerManagementInterface(logConfigurator);
      
//  	try {
//  	  clusterContext.getMessageContext().registerListener(logConfigurator);
//  	} catch (Throwable t) {
//      // $JL-EXC$
//  	  TRACER.traceThrowableT(
//        Severity.ERROR, "Unable to register listener in the message context", t);
//  	}
  }

  public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
  	try {
  	  if (interfaceName.equals("shell")) {
    		shell = (ShellInterface) interfaceImpl;
    		commandsId = shell.registerCommands(
    		  new Command[] {
      			new LogCreateCommand(logConfigurator),
      			new LogRemoveCommand(logConfigurator),
      			new LogDumpsCommand(logConfigurator),
      			new LogListCommand(logConfigurator),
      			new LogEditCommand(logConfigurator),
      			new ArchiveCommand(logConfigurator)
    		  }
    		);
  	  }
  	} catch (Throwable t) {
      // $JL-EXC$
  	  TRACER.traceThrowableT(Severity.ERROR, t.getMessage(), t);
  	}
  }

  public void interfaceNotAvailable(String interfaceName) {
  	if (interfaceName.equals("shell")) {
  	  shell = null;
  	  commandsId = -1;
  	}
  }
}