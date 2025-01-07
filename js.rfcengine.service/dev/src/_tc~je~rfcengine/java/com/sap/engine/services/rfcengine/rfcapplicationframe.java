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
package com.sap.engine.services.rfcengine;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;


import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.ApplicationContainerContext;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.runtime.RuntimeConfiguration;

import com.sap.engine.frame.core.configuration.*;

import com.sap.engine.interfaces.log.Logger;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.mw.jco.JCO;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.engine.services.deploy.DeployListener;
import com.sap.engine.services.deploy.DeployEvent;
import com.sap.engine.services.deploy.DeployCallbackImpl;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.container.ProgressEvent;


/**
 *  RFC Application Service Frame. This service is used to make session
 *  beans calls.
 *
 * @author  Petio Petev, Hristo Iliev, d035676
 * @version 4.2
 */
public class RFCApplicationFrame
	extends RuntimeConfiguration
	implements ApplicationServiceFrame, DeployListener  {

  //static ApplicationServiceContext globalServiceContext ;
  /**
   * Stores the service's runtime interface
   */
  private RFCRuntimeInterfaceImpl interfaceImpl = null;

  /**
   * Service's ContainerContext instance
   */
  protected static ApplicationContainerContext containerContext = null;

  public static Logger logger = null;
  /**
   * JNDIContainerEventListener instance
   */
  private RFCContainerEventListener containerEventListener = null;

  private String method = null;

  ApplicationServiceContext serviceContext = null;

  private InitialContext context = null;

  public static RFCRepositoryInterface local = null;
  
  ConfigurationHandler cfgHandler = null;
  static boolean waitForAppsStart = false;
  DeployService deploy = null;
  DeployCallbackImpl callback = null;
  static boolean isFirstStart = true;
  private static int CONNECTION_CHECK_TIME = 60000; // 60 seconds

  /**
   *  Service start method.
   * @param   serviceContext
   * @exception   ServiceException
   */
  public void start(ApplicationServiceContext serviceContext) throws ServiceException {

    String method = "RFCApplicationFrame.start(ApplicationServiceContext serviceContext)";

    this.serviceContext = serviceContext;
    //this.globalServiceContext =serviceContext;

    // Initialize the logging and tracing
    Location location = Location.getLocation(RFCResourceAccessor.LOCATION_PATH);
    Category category = Category.getCategory(RFCResourceAccessor.CATEGORY);
    new RFCResourceAccessor().init(category, location);
    //logInfo(method,  "enter RFCApplicationFrame.start()", null);

    //  create runtime interface
    try {
      interfaceImpl = new RFCRuntimeInterfaceImpl(serviceContext);

    } catch (RemoteException re) {
      traceError("Unable to prepare the service's interface !");
      LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, re);
      throw new ServiceException(re);
    }

    // Initialize
    RFCRequestHandler rfcRequestHandler = null;
    try {
      containerEventListener = new RFCContainerEventListener(serviceContext, interfaceImpl);
    } catch (Throwable t) {
      t.printStackTrace();
    }
    containerContext = serviceContext.getContainerContext();
    int mask = ContainerEventListener.MASK_INTERFACE_AVAILABLE |
               ContainerEventListener.MASK_INTERFACE_NOT_AVAILABLE |
               ContainerEventListener.MASK_CONTAINER_STARTED;;
    Set names = new HashSet(2);
    names.add("log");
    names.add("jmx");
    
    // set offline properties
    // this call should be after instantiating of interfaceImpl
    containerEventListener.setServiceProperties(serviceContext.getServiceState().getProperties());
    
    // following entry should happen after containerEventListener instanziation
    // register for eventing of changes for online modifiable properties
    serviceContext.getServiceState().registerRuntimeConfiguration(this);
    
    serviceContext.getServiceState().registerContainerEventListener(mask, names, containerEventListener);

    // register J2EE listeners
    try {
      serviceContext.getContainerContext().getObjectRegistry().registerInterface(interfaceImpl);
    } catch (Exception e) {
      traceError("Could not register runtime interface :" + e.toString());
      LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
      throw new ServiceException("Could not register runtime interface. Cause: "+e.toString());
    }
    try {
      serviceContext.getClusterContext().getMessageContext().registerListener(interfaceImpl);
    } catch (Exception e) {
      traceError("Can't register MessageListener :" + e.toString());
      LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
    }
    try {
      serviceContext.getServiceState().registerManagementInterface(interfaceImpl);
    } catch (Exception e) {
      traceError("Can't register ManagementInterface :" + e.toString());
      LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
    }

    try {
      if (context == null)
          context = new InitialContext();
    }
    catch(Exception e){
      traceError("Unable to create InitialContext !");
      LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
      return;
    }

    try {
      try {
          context.lookup("rfcaccessejb");
      } catch(NameNotFoundException ne) {
          context.createSubcontext("rfcaccessejb");
      }
      local = new RFCRepositoryInterfaceImpl();
      context.bind("rfcaccessejb/RFCEngineLocalRepository", local);
    } catch(NamingException e) {
      traceError("Unable to register the service's local interface !");
      LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
      // if binding doesn't work, go further. Only some few components use this bound object
    }
    
    try {
    	ConfigurationHandlerFactory cfgHandlerFactory = serviceContext.getCoreContext().getConfigurationHandlerFactory();
    	ConfigurationHandler cfgHandler = cfgHandlerFactory.getConfigurationHandler();
    	cfgHandler.addConfigurationChangedListener(interfaceImpl, 
    			RFCRuntimeInterfaceImpl.RFCENGINE_CFG_GLOBAL,ConfigurationChangedListener.MODE_ASYNCHRONOUS);
      }
      catch(Exception e){
        traceError("Unable to register  ConfigurationChangedListener !");
        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
      }
    
    // if the rfcengine service was stopped and then started
    // don't wait for event
    // if (!isFirstStart) containerEventListener.containerStarted();

    //logInfo(method, "waitForAppsStart="+ waitForAppsStart+", isFirstStart="+ isFirstStart,null);
    if (!waitForAppsStart || !isFirstStart)
    {
      init();
    }
    else
    {
      try {
        context = new InitialContext();
        deploy = (DeployService) context.lookup("deploy");
        callback = new DeployCallbackImpl();
        callback.addDeployListener((DeployListener)this);
        deploy.registerDeployCallback(callback, null);
      } catch(Exception e) {
        traceError("Unable to register DeployListener !"+e.toString());
        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
      }
    }
    isFirstStart = false; // this is correct place of setting. Don't change it.
    //logInfo(method,  "end RFCApplicationFrame.start()", null);
    
    
  }

  private void init()  throws ServiceException
  {
    String method = "RFCApplicationFrame.init()";

    // initialize interface
    try {
      
        interfaceImpl.addListeners();
        serviceContext.getCoreContext().getThreadSystem().startThread(  new RFCEngineStarter(interfaceImpl) , true );
        //new RFCEngineStarter(interfaceImpl).start();
    } catch (Exception re) {
        RFCApplicationFrame.logError(method, "Couldn't initialize service: "+re.toString(), null);
        traceError("Unable to initialize the service's interface !");
        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, re);
    }
    logInfo(method,  "RFCRuntimeInterface initialized", null);
  }

  /**
   *  Service's stop method.
   */
  public void stop() {

    method = "RFCApplicationFrame.stop()";
    //logInfo(method,  "enter RFCApplicationFrame.stop()", null);

    Enumeration<Bundle> cycle = interfaceImpl.getBundles().elements();

    // Stop all bundles
    try {
    	while (cycle.hasMoreElements()) {
    	      (cycle.nextElement()).stopAll();
    	}
	} catch (Exception e) {
		RFCApplicationFrame.logError(method, "couldn't stop bundles on service stop: "+e.toString(), null);
        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
	}
    

    // remove JCO listeners
    interfaceImpl.removeListeners(false);

    // unbind repositories
    try {
      local = null;
      context.unbind("rfcaccessejb/RFCEngineLocalRepository");
    } catch (NamingException e) {
        traceError("Unable to unregister the service's local interface !");
        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
    }

    // destroy logger
    containerEventListener.destroyLogger();
    
    // unregister all listeners
    containerEventListener.removeMBean(); // unregister MBean
    serviceContext.getServiceState().unregisterRuntimeConfiguration();
    serviceContext.getServiceState().unregisterContainerEventListener();
    serviceContext.getClusterContext().getMessageContext().unregisterListener();
    serviceContext.getServiceState().unregisterManagementInterface();
    serviceContext.getContainerContext().getObjectRegistry().unregisterInterface(); // unregister interfaceImpl
    if (cfgHandler != null)
    	cfgHandler.removeConfigurationChangedListener(interfaceImpl,RFCRuntimeInterfaceImpl.RFCENGINE_CFG_GLOBAL);

    // release all objects hold by this instance
    this.interfaceImpl = null;
    containerContext = null;
    this.context = null;
    this.containerEventListener = null;
    //this.globalServiceContext = null;
    logger = null;
  }
  
  /**
   * Updates service runtime changeable properties. The properties set must be applied or
   * rejected if some of the values is not acceptable
   *
   * @param properties a set of changed service properties
   * @throws ServiceException if there is incorrect value and the hole set is not applied
   */
  public void updateProperties(java.util.Properties properties) throws ServiceException
  {
	  if (RFCApplicationFrame.isLogged(Severity.INFO))
          RFCApplicationFrame.logInfo("updateProperties", "call updateProperties with "+
        		  properties, null);
	  containerEventListener.setServiceProperties(properties);
  }


  /**
   * Returns the service's interface
   *
   * @return   The interface of the service
   */
  public Object getServiceInterface() {
    return interfaceImpl;
  }
  
  public static boolean isLogged(int severity) {
      return RFCResourceAccessor.category != null && RFCResourceAccessor.location.beLogged(severity);
  }

  public static void logError(String message) {
      if (RFCResourceAccessor.location != null) {
          RFCResourceAccessor.location.errorT(message);
      }
  }

  public static void logError(String method, String message, Object[] obj) {
        if (RFCResourceAccessor.location != null) {
            RFCResourceAccessor.location.errorT(method, message, obj);
        }
  }
  
  public static void logWarning(String method, String message, Object[] obj) {
      if (RFCResourceAccessor.location != null) {
          RFCResourceAccessor.location.warningT(method, message, obj);
      }
}

  public static void logInfo(String method, String message, Object[] obj) {
      if (RFCResourceAccessor.location != null) {
          RFCResourceAccessor.location.infoT(method, message, obj);
      }
  }

  public static void traceError(String message) {
      if (RFCResourceAccessor.location != null) {
      	  //$JL-SEVERITY_TEST$ this method is called from the other try/catch blocks 
          RFCResourceAccessor.location.errorT(RFCResourceAccessor.category, message);
      }
  }

  public static void traceError(String method, String message, Object[] obj) {
        if (RFCResourceAccessor.location != null) {
        	//$JL-SEVERITY_TEST$ this method is called from the other try/catch blocks 
        	RFCResourceAccessor.location.errorT(RFCResourceAccessor.category, method, message, obj);
        }
    }

  // for DeployListener interface
  public void processApplicationEvent(DeployEvent event) {
    //process event about operation on application
    if (  event.getActionType() == DeployEvent.INITIAL_START_APPLICATIONS &&
          event.getAction()     == DeployEvent.LOCAL_ACTION_FINISH)
    {
      String method = "processApplicationEvent()";
      if (RFCApplicationFrame.isLogged(Severity.INFO))
          logInfo(method, method +", waiting for application to start ...",null);

      try
      {
          init();
      }//try
      catch (java.lang.Exception e)
      {
        traceError("Unable to initialize RFCApplicationFrame !"+e.toString());
        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
      }//catch
      if (RFCApplicationFrame.isLogged(Severity.INFO))
          logInfo(method, method +", all applications have started" ,null);

      try
      {
          if (deploy != null) deploy.unregisterDeployCallback(callback, null);
      }//try
      catch (java.lang.Exception e)
      {
        traceError("Unable unregister DeployCallback !"+e.toString());
        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
      }//catch
    }
  }
  public void processServiceEvent(DeployEvent event) {
  	
  }
  public void processLibraryEvent(DeployEvent event) {
  }
  public void processInterfaceEvent(DeployEvent event) {
  }
  public void processReferenceEvent(DeployEvent event) {
  }
  public void processStandaloneModuleEvent(DeployEvent event) {
  }
  public void processContainerEvent(ProgressEvent event) {

  }
  public void callbackLost(String serverName) {
  }
  public void serverAdded(String serverName) {
  }
  
  private class RFCEngineStarter implements Runnable
  {

      private RFCRuntimeInterfaceImpl m_ri;
      
      private Object semaphore = new Object();
      
      RFCEngineStarter (RFCRuntimeInterfaceImpl ri)
      {
          m_ri = ri;
      }
      
      public void run()
      {
          if (RFCApplicationFrame.isLogged(Severity.INFO))
          {
              RFCApplicationFrame.logInfo("RFCEngineStarter.run()"," RFCEngineStarter thread "+Thread.currentThread().toString()
                      + " starts the RfcEngine framework", null);
          }
          
          m_ri.init();
          
          // wait to fill list with failed configurations
          synchronized(semaphore) 
		  {
    		  try
    		  {
  		  		semaphore.wait(CONNECTION_CHECK_TIME);
    		  }
    		  catch (InterruptedException e)
    		  {
    			  // should never happen
    			  LoggingHelper.traceThrowable(Severity.DEBUG, RFCResourceAccessor.location, "RFCEngineStarter.run(), interupted on wait: ", e);
    		  }
		  }//synchronized
          
          // check connection validity
          Bundle bundle = null;
          String bundleID = null;
          BundleConfiguration config = null;
          long timeToWait = CONNECTION_CHECK_TIME;
          boolean firstGatewayError = true;
          boolean firstRepositoryError = true;
          
          // If the repository pool connections becomes invalid (e.g. Password 
          // in backend has changed but not updated in the bundle) after the server was 
          // successfully started and is running
          // then we may live even with that bad pool configuration, since the repository
          // may already have needed metadata. The bad repository configuration will
          // be checked on the nexts J2EE Engine restart.
          // Wenn we start the service, the repository has no meta data yet.
          // For any calls we need a valid repository connection, thus 
          // status is stopped as long as either server connection or repository 
          // connection don't work, so, that the user can see over Gui, that
          // bundle doesnt't run and will starts it. He will then get according exception
          // saying that the repository parameters or server parameters are wrong or
          // get a hint that the gateway needed for server or backend needed for repository 
          // pool are not available.
          // If the repository connections are not valid, then on startAll the 
          // bundle will be put back to bundlesFailedOnStart and so we try to start
          // again, until all configurations will work
          
          // run this loop until no configuration with status failedOnStart exist.
          // We make this special loop on start and don't user JCO restart mechanizm,
          // to make sure that repository also has valid connection and we do checks with
          // constant time interval. In JCO case, in werst case, the connection will be
          // established in an hour.
          while (!m_ri.bundlesFailedOnStart.isEmpty())
          {
        	  try
        	  {
        		  timeToWait = System.currentTimeMillis();
        		  // create shallow copy of this hashtable to make sure that it doesn't change
        		  Hashtable<String, Bundle> localBundles = (Hashtable<String, Bundle>)m_ri.bundlesFailedOnStart.clone();
        		  Enumeration<Bundle> bundles = localBundles.elements();
        		  while (bundles.hasMoreElements())
        		  {
        			  bundle = bundles.nextElement();
        			  config = bundle.getConfiguration();
        			  bundleID = config.getProgramId();
        			  try
        			    {
    			        	
    			        	JCO.getNumServerConnections(bundle.getServerProperties()); //throws JCO.Exception
    			        	
    			        	// initialize pool here to catch all repeated exceptions
    			        	bundle.initializePool();
			        		
			        		serviceContext.getCoreContext().getThreadSystem().startThread( 
			        				new RFCRuntimeInterfaceImpl.RFCEngineThreadStarter(m_ri,bundle) , true );
			        		if (RFCApplicationFrame.isLogged(Severity.INFO))
			                    RFCApplicationFrame.logInfo(method, "start bundle "+bundleID+ " after failure on start  on node="+
			                    		m_ri.nodeId, new Object[] {bundleID});
        			    }//try
        			  	catch (RemoteException e) // thrown by repository creation
			            {
			            	// write in log only here
        			  		if (RFCApplicationFrame.isLogged(Severity.INFO))
    			        	{
        			  			RFCApplicationFrame.logError(method, "Failed pepeatedly to create a repository for RFCEngine Bundle "+bundleID, null);
    			                LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
    			        	}
        			  		else if  (firstRepositoryError)
        			  		{
        			  			firstRepositoryError = false;
        			  			LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
        			  		}
			            }
        			    catch (Exception e)
        			    {
    			        	if (RFCApplicationFrame.isLogged(Severity.INFO))
    			        	{
			                    String gateway = "host="+config.getGatewayHost()+", service="+config.getGatewayService();
    			        		RFCApplicationFrame.logInfo(method, "cannot start bundle "+bundleID+ " since the Gateway "+gateway+
    			        				" is now not available, will check Gateway availablility every "+ CONNECTION_CHECK_TIME/1000 +
			                    		" seconds. To accelerate restart make sure that gateway is running and then start bundle "+bundleID+" manually", new Object[] {bundleID});
    			        	}
    			        	else if (firstGatewayError)
    			        	{
    			        		// we log once for error log
    			        		firstGatewayError = false;
    			        		String gateway = "host="+config.getGatewayHost()+", service="+config.getGatewayService();
    			        		RFCApplicationFrame.logError(method, "cannot start bundle "+bundleID+ " since the Gateway "+gateway+
    			        				" is now not available, will check Gateway availablility every "+ CONNECTION_CHECK_TIME/1000 +
			                    		" seconds. To accelerate restart make sure that gateway is running and then start bundle "+bundleID+" manually", new Object[] {bundleID});
    			        	}
        			    }
        			    catch(Error error)
        			    {
        			    	// shouldn't happen, so trace it
        			    	LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, error);
        			    }
        		  }
        	  }
        	  catch(Throwable ex)
			  {
			        RFCApplicationFrame.logError(method, "RFCEngineStarter.run(). Unexpected break: "+ex.getMessage(), new Object[] {bundleID});
			        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, ex);
			  }
	          finally
	          {
	        	  timeToWait = Math.max(CONNECTION_CHECK_TIME + timeToWait - System.currentTimeMillis(),1);
	        	  synchronized(semaphore) 
        		  {
	        		  try
	        		  {
		  		  		semaphore.wait(timeToWait);
	        		  }
	        		  catch (InterruptedException e)
	        		  {
	        			  // should never happen
	        			  LoggingHelper.traceThrowable(Severity.DEBUG, RFCResourceAccessor.location, "RFCEngineStarter.run(), interupted on wait: ", e);
	        		  }
        		  }//synchronized
	          }
	      }//while
      }
  }
}