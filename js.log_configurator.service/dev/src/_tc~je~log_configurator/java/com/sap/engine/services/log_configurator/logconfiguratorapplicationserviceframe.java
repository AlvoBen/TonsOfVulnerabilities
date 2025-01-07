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

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.runtime.RuntimeConfiguration;


/**
 * @author Miroslav Petrov
 * @version 6.30
 */
public class LogConfiguratorApplicationServiceFrame extends LogConfiguratorServiceFrameImpl 
implements ApplicationServiceFrame {//$JL-EXC$
  

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
	this.serviceContext = serviceContext;
	this.clusterContext = serviceContext.getClusterContext();
	this.objectRegistry = serviceContext.getContainerContext().getObjectRegistry();
	this.systemMonitor = serviceContext.getContainerContext().getSystemMonitor();
    
    java.util.Properties propz = serviceContext.getServiceState().getProperties();
    String includeStackTrace = propz.getProperty("IncludeStackTraceForEachRecord");
    if (includeStackTrace.equalsIgnoreCase("true")) {
        com.sap.tc.logging.LoggingManager.getLoggingManager().setIncludeStackTraceForEachRecord(true);
    } else {
        com.sap.tc.logging.LoggingManager.getLoggingManager().setIncludeStackTraceForEachRecord(false);
    }
    String specificLocations = propz.getProperty("SpecificLocations");
    com.sap.tc.logging.LoggingManager.getLoggingManager().setListOfLocationsIncludeStackTrace(specificLocations);
    RuntimeConfiguration runtime = new LoggingRuntimeConfiguration();
    serviceContext.getServiceState().registerRuntimeConfiguration(runtime);

    
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
	super.stop();

    serviceContext.getServiceState().unregisterRuntimeConfiguration();
  }



}
