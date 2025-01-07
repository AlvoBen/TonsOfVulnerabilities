/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rfcengine;

import com.sap.engine.interfaces.log.Logger;
import com.sap.engine.interfaces.log.LogInterface;
import com.sap.engine.interfaces.log.LoggerAlreadyExistingException;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.container.event.ContainerEventListenerAdapter;
import com.sap.mw.jco.JCO;
import com.sap.tc.logging.Severity;
import java.util.*;


import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class RFCContainerEventListener extends ContainerEventListenerAdapter {

  /**
   * Logger
   */
  private Logger logger = null;
  /**
   * Log Interface
   */
  private LogInterface log = null;
  
  private ApplicationServiceContext serviceContext = null;
  private RFCRuntimeInterfaceImpl interfaceImpl = null;
  private ObjectName mBeanName = null;
  private MBeanServer mbs = null;
  private int m_maxConnections = 20;
  private int m_maxProcesses = 20;
  private boolean changeProcessesNumber = false;
  private int m_jco_trace = 0;
  private String m_cpic_trace = "0";
  private String m_jrfc_trace = "0";

  /**
   * Constructor
   *
   * @param context Message context to use
   * @param interf runtime interface
   */
  public RFCContainerEventListener(ApplicationServiceContext context,
  									RFCRuntimeInterfaceImpl interf) 
  {
  	 this.serviceContext = context;
  	 this.interfaceImpl = interf;
  }

  public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
    if (interfaceImpl!= null && "log".equals(interfaceName)) {
      log = (LogInterface) interfaceImpl;
      try {
        logger = log.createLogger("rfcengine", null);
      } catch (LoggerAlreadyExistingException exc) {
        logger = exc.getExistingLogger();
      }
    }
  }

  public void interfaceNotAvailable(String interfaceName) {
    if ("log".equals(interfaceName)) {
      log = null;
      logger = null;
    }
  }
  
  public void containerStarted()
  {
  	//  hey, the JMX container started! So register MBeans
  	//System.out.println("RFCContainerEventListener.containerStarted(ApplicationServiceContext serviceContext) begin");
  	String method = "RFCContainerEventListener.containerStarted()";
  	if (mbs != null) return; 
    try {
    	mbs = (MBeanServer)serviceContext.getContainerContext().getObjectRegistry().getServiceInterface("jmx");
    	if (mbs == null) 
    	{
    		//System.out.println("Lookup for JMX service returned null");
    		RFCApplicationFrame.traceError("Lookup for JMX service returned null");
    		LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, new Throwable("Lookup for JMX service returned null"));
    		return;
    	}
    	mBeanName = new ObjectName("RFC:name=JCoRFCProvider");
    	mbs.registerMBean(new RFCEngineManagement(interfaceImpl), mBeanName);
	} catch (Throwable e) {
		//System.out.println("Exception in RFCContainerEventListener.containerStarted(ApplicationServiceContext serviceContext)");
		RFCApplicationFrame.traceError("Unable to get MBeanServer !"+e.toString());
        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
	}
	//System.out.println("RFCContainerEventListener.containerStarted(ApplicationServiceContext serviceContext) end");
  }

  public boolean setServiceProperty(String key, String value) {
  
      String method = "RFCContainerEventListener.setServiceProperty()";
      if (RFCApplicationFrame.isLogged(Severity.INFO))
          RFCApplicationFrame.logInfo(method, "change property "
        		  +key+" to value "+value ,null);
      if (("jrfc.trace").equals(key) && (!this.m_jrfc_trace.equals(value)))
      {
          // memorize own setting and reset only if we changed it
          JCO.setMiddlewareProperty("jrfc.trace",value);
          this.m_jrfc_trace = value;
      }
      else if (("cpic.trace").equals(key) && (!this.m_cpic_trace.equals(value)))
      {
          JCO.setMiddlewareProperty("cpic.trace",value);
          this.m_cpic_trace = value;
      }
      else if (("jco.trace_level").equals(key))
      {
          try {
              int level = Integer.parseInt(value);
              if (this.m_jco_trace != level)
              {
                  RFCRuntimeInterfaceImpl.resetTracePath();
                  JCO.setTraceLevel(level);
                  this.m_jco_trace = level;
              }
              
            } catch (Exception nfe) {
              RFCApplicationFrame.logError(method, "jco.trace_level" ,null);
              LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, nfe);
            }
      }
      else if (("MaxConnections").equals(key))
      {
          int old_maxConnections = this.m_maxConnections;
          try {
        	  m_maxConnections = Integer.parseInt(value);
        	  if (this.m_maxConnections != old_maxConnections)
        	  {
                  this.changeProcessesNumber = true;
                  if (RFCApplicationFrame.isLogged(Severity.INFO))
                      RFCApplicationFrame.logInfo(method, "maxConnection was changed from "
                    		  +old_maxConnections+" to "+m_maxConnections ,null);
        	  }
    
            } catch (Exception nfe) {
            	m_maxConnections = 20;
                RFCApplicationFrame.logError(method, "resetting MaxConnections failed" + nfe.getMessage(),null);
                LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, nfe);
            }
            
      }
      else if (("MaxProcesses").equals(key))
      {
        //  Max Processes parsing
    	  int old_maxProcesses = this.m_maxProcesses;
        try {
        	m_maxProcesses = Integer.parseInt(value);

          if (this.m_maxProcesses != old_maxProcesses)
          {
              this.changeProcessesNumber = true;
              if (RFCApplicationFrame.isLogged(Severity.INFO))
                  RFCApplicationFrame.logInfo(method, "maxProcesses was changed from "
                		  +old_maxProcesses+" to "+m_maxProcesses ,null);
          }
        } catch (Exception nfe) {
        	m_maxProcesses = 20;
            if (RFCApplicationFrame.isLogged(Severity.ERROR))
                RFCApplicationFrame.logError(method, "resetting MaxProcesses failed" ,null);
        }
        
      }
      
      else if (("JarmActiveOnStart").equals(key))
      {

        try {
            RFCApplicationFrame.waitForAppsStart = new Boolean(value).booleanValue();
//          RFCApplicationFrame.logInfo(method, "waitForAppsStart="+ waitForAppsStart,null);
        } catch (Exception nfe) {
            if (RFCApplicationFrame.isLogged(Severity.INFO))
                RFCApplicationFrame.logInfo(method, "resetting WaitForAppsStart failed" ,null);
        }
      }
        
      else if (("JarmActiveOnStart").equals(key))
      {
    
            try {
              RFCJCOServer.useJarm = new Boolean(value).booleanValue();
              //RFCApplicationFrame.logInfo(method, "useJarm="+ RFCJCOServer.useJarm,null);
            } catch (Exception nfe) {
                if (RFCApplicationFrame.isLogged(Severity.INFO))
                    RFCApplicationFrame.logInfo(method, "resetting JarmActiveOnStart failed" ,null);
            }
      }
      // we say, that we tried to change properties
      return true;
  }

  public boolean setServiceProperties(Properties serviceProperties) {
      
      String key = null;
      String value = null;

      Enumeration keys = serviceProperties.keys();
      while (keys.hasMoreElements())
      {
          key = (String)keys.nextElement();
          value = (String)serviceProperties.getProperty(key);
          setServiceProperty(key, value);
          
      }     
      if (changeProcessesNumber && (interfaceImpl != null)) {
          interfaceImpl.newProperties(m_maxProcesses, m_maxConnections);
      }
      return true;
  }

  void destroyLogger() {
    log.destroyLogger("rfcengine");
  }
  
  public void removeMBean()
  {
  	//System.out.println("RFCContainerEventListener.removeMBean() begin");
  	String method = "RFCContainerEventListener.removeMBean(ApplicationServiceContext serviceContext)";
  	
  	try {
  		mbs.unregisterMBean(mBeanName);
  		mbs = null;
	} catch (Throwable e) {
		//System.out.println("Exception in RFCContainerEventListener.removeMBean()");
		RFCApplicationFrame.traceError("Unable to unregister MBean !"+e.toString());
        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
	}
	//System.out.println("RFCContainerEventListener.removeMBean() end");
  	
  }

}

