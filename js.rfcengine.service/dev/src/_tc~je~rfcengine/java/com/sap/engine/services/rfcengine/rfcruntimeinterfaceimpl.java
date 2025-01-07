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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.*;
import javax.rmi.PortableRemoteObject;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.cluster.ClusterContext;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.ClusterException;
import com.sap.engine.frame.cluster.message.MessageAnswer;
import com.sap.engine.frame.cluster.message.MessageListener;
import com.sap.engine.frame.core.configuration.ChangeEvent;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.frame.state.ManagementListener;
import com.sap.engine.interfaces.security.CryptographyContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.mw.jco.JCO;
import com.sap.tc.logging.Severity;

/**
 * RFC Engine Runtime
 *
 * @author Hristo Iliev
 * @version 4.3
 */
public class RFCRuntimeInterfaceImpl extends PortableRemoteObject
  implements RFCRuntimeInterface, MessageListener, ConfigurationChangedListener{

  public static final String RFCENGINE_CFG_GLOBAL =" rfcengine_global" ;
  public static final String RFCENGINE_CFG_LOCAL =" rfcengine_local" ;

  /**
   * Used to determine if called from other cluster element
   */
  private boolean remoteCall = false;
  /**
   * Used for logging purposes
   */
  //  private LogContext logContext = null;

  /**
   * Stores the bundles
   */
  Hashtable<String, Bundle> bundles = new Hashtable<String, Bundle>();
  /**
   * Maximum JCo servers in a bundle
   */
  
  Hashtable<String, Bundle> bundlesFailedOnStart = new Hashtable<String, Bundle>();
  /**
   * Maximum JCo servers in a bundle
   */
  
  int maxProcesses = 20;
  /**
   * Maximum connections in the pool (pool size)
   */
  int maxConnections = 20;
  /**
   * Used for storing bundle's information in the naming
   */
  //private Context ctx = null;
  /**
   * Stores the ClusterContext
   */
  private ClusterContext cluster = null;
  /**
   * Stores the EJB service
   */
  //  private EJBRuntimeImpl ejb = null;
  ApplicationServiceContext serviceContext = null;

  /**
   * Used for encripting / decripting passwords
   */
  private CryptographyContext crypt = null;

  private Hashtable functionTable = new Hashtable();

  ConfigurationHandlerFactory cfgfactory ;

  
  private String method = null;
  String nodeId = null;

  JCOListener m_listener = null;
  
  static final String Version = "7.2.0 (2009-02-18)";
  
  static Set PROPS_SET = getPropsSet();

  /**
   * Constructor
   * @param  srvCtx   Service Context
   * @exception  RemoteException   Thrown if a problem occurs.
   */
  public RFCRuntimeInterfaceImpl(ApplicationServiceContext srvCtx) throws RemoteException {
    super();
    method = "RFCRuntimeInterfaceImpl.RFCRuntimeInterfaceImpl<init>";

    this.serviceContext = srvCtx;
    this.cluster = srvCtx.getClusterContext();

    // Get the cryptography helper
    this.crypt = ((SecurityContext) srvCtx.getContainerContext().getObjectRegistry().getProvidedInterface("security")).getCryptographyContext();
    cfgfactory = srvCtx.getCoreContext().getConfigurationHandlerFactory() ;
    ClusterElement element = cluster.getClusterMonitor().getCurrentParticipant();
    if (element != null)
    	nodeId = (new Integer(element.getClusterId())).toString();

  }

  /**
   * Initializes the bundles
   *
   * @exception  RemoteException   Thrown if a problem occurs.
   */
  void init() {

     ConfigurationHandler handler =null ;
     Configuration rfcconfig = null ;
     method = "RFCRuntimeInterfaceImpl.init()";
     if (RFCJCOServer.useJarm)
      {
        JCO.setProperty("jco.jarm","1");
        if (RFCApplicationFrame.isLogged(Severity.INFO))
            RFCApplicationFrame.logInfo(method, "Jarm activated ", null);
      }
     else if ("1".equals(JCO.getProperty("jco.jarm")))
     	RFCJCOServer.useJarm = true;
     try
     {// try transaction
      handler = cfgfactory.getConfigurationHandler() ;

      // init global Bundles
      try{
        rfcconfig = handler.openConfiguration(RFCENGINE_CFG_GLOBAL,ConfigurationHandler.READ_ACCESS) ;
      }catch(ConfigurationException e){//logical exception
        rfcconfig = handler.createRootConfiguration( RFCENGINE_CFG_GLOBAL ) ;
        //LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
      }

      Map cfgmap = rfcconfig.getAllSubConfigurations() ;
      for(Iterator i =cfgmap.values().iterator();i.hasNext();){
        Configuration next = (Configuration)i.next() ;
        PropertySheet propssheet = next.getPropertySheetInterface() ;
        BundleConfiguration bundlecfg = new BundleConfiguration() ;
        readFromPropertySheet(propssheet, bundlecfg);
        
        if (!bundles.containsKey(bundlecfg.getProgramId()))
        {
	        Bundle bundle = new Bundle() ;
	        bundle.initBundle(bundlecfg, (RFCRuntimeInterface)this);
	        bundles.put(bundlecfg.getProgramId(), bundle);
	        handler.addConfigurationChangedListener(this , RFCENGINE_CFG_GLOBAL + bundlecfg.getProgramId()) ;
	
	        if (bundlecfg.isRunning()) {
	//          this coding is running during service start, so we 
	            // start all bundles in || independent on available thread number
	            // and the status of starting. The user cannot be notified over
	            // gui on service initialization (probably the GUI is not started at all)
	            // so nobody is interested in the status. We surelly write ERRORS in traces
	            serviceContext.getCoreContext().getThreadSystem().startThread( new RFCEngineThreadStarter(this, bundle) , true );
	        }
        }
        else
        {
            RFCApplicationFrame.logError("Duplicate configuration for global JCo RFC Provider bundle "+
            		                     bundlecfg.getProgramId()+" found in the database. The duplicate RFC Listener has not been started.");
        }
      }

      // init local Bundles
      try{
        rfcconfig = handler.openConfiguration(RFCENGINE_CFG_LOCAL,ConfigurationHandler.READ_ACCESS) ;
      }catch(ConfigurationException e){//logical exception
        rfcconfig = handler.createRootConfiguration( RFCENGINE_CFG_LOCAL ) ;
        //LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
      }
      
      try{
        Configuration serverConf =  rfcconfig.getSubConfiguration(nodeId);
        Map maps = serverConf.getAllSubConfigurations() ;
        for(Iterator i =maps.values().iterator();i.hasNext();){
          Configuration next = (Configuration)i.next() ;
          PropertySheet propssheet = next.getPropertySheetInterface() ;
          BundleConfiguration bundlecfg = new BundleConfiguration() ;
          readFromPropertySheet(propssheet, bundlecfg);
          if (!bundles.containsKey(bundlecfg.getProgramId()))
          {
	          Bundle bundle = new Bundle() ;
	          bundle.initBundle(bundlecfg, (RFCRuntimeInterface)this);
	          bundles.put(bundlecfg.getProgramId(), bundle);
	          // as local bundles exist only locally and thus changes are done locally, no
	          // notifications to other nodes are necessary. By change from local to global or reverse,
	          // the notification comes from global
	          //handler.addConfigurationChangedListener(this , RFCENGINE_CFG_LOCAL + nodeId + bundlecfg.getProgramId()) ;
	
	          if (bundlecfg.isRunning()) {
	//            this coding is running during service start, so we 
	              // start all bundles in || independent on available thread number
	              // and the status of starting. The user cannot be notified over
	              // gui on service initialization (probably the GUI is not started at all)
	              // so nobody is interested in the status. We surelly write ERRORS in traces
	              serviceContext.getCoreContext().getThreadSystem().startThread( new RFCEngineThreadStarter(this,bundle) , true );
	          }
          }
	      else
	      {
	          RFCApplicationFrame.logError("Duplicate configuration for local JCo RFC Provider bundle "+
	                                       bundlecfg.getProgramId()+" found in the database. The duplicate RFC Listener has not been started.");
	      }
        }//for
      }catch(ConfigurationException ee){
          //if (RFCApplicationFrame.isLogged(Severity.INFO))
          //    RFCApplicationFrame.logInfo(method, "There are no local bundles regstered for the cluster node ", new String[] {clusterId});
      }

    }catch(ConfigurationException  e ){

		RFCApplicationFrame.traceError("Reading transaction on the database transaction failed"+e.getMessage());
		LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
     }finally{
		try
		{
			handler.closeAllConfigurations();
		}catch(ConfigurationException cfg){
			RFCApplicationFrame.traceError("Failed closing configuration");
		}
	 }
  }

  /**
   * Performs "clean-up"
   *
   * @exception  RemoteException   Thrown if a problem occurs.
   */
  void cleanup() throws RemoteException {

        if (bundles != null) bundles.clear();
      else bundles = new Hashtable();
  }

  /**
   * Starts a bundle
   *
   * @param  prgId   Program Id of the bundle
   * @return  TRUE if the bundle exists and started successfully
   * @exception  RemoteException   Thrown if a problem occurs.
   */
  public synchronized boolean startBundle(String prgId) throws RemoteException {

    method = "RFCRuntimeInterfaceImpl.startBundle(String prgId)";
    // Check if the bundle exists
    if (!bundles.containsKey(prgId)) {
      RFCApplicationFrame.logError(method, "JCo RFC Provider bundle not found "+prgId +" on node="+nodeId+" for starting", null);
      throw new RemoteException("JCo RFC Provider bundle not found "+prgId +" on node="+nodeId+" for starting"); 
    }

    Bundle bndl = bundles.get(prgId);
    BundleConfiguration config = bndl.getConfiguration();

    //setStaticProperties(config);
    
    if (config.isRunning())
    {
    	// if the gui had outdated state, it could show still stopped, althoug the servers are started
    	// so, protokol it and restart
    	RFCApplicationFrame.logError(method, "JCo RFC Provider bundle "+prgId +" was already started on node="+nodeId, null);
      //throw new RemoteException("Bundle was already started "+prgId +" on node="+nodeId); 
    }
    
    try
	{
    	bndl.startAll();
    	if (RFCApplicationFrame.isLogged(Severity.INFO))
            RFCApplicationFrame.logInfo(method, "JCo RFC Provider bundle "+ prgId +" started from gui on node="+nodeId, null);
	}
	catch (RemoteException e)
	{
		RFCApplicationFrame.logError(method, "JCo RFC Provider bundle "+prgId +" couldn't not be started from gui on node="+nodeId, null);
		LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
		throw e;
	}
	// save the new running status only in this method, that is invocated
	// from gui. For all other invocation types of startAll, change the state
	// only in the memory
	finally
	{
		changeRunningStateInConfig(config);
	}
    
  	return true; // for compatibility
  }

  /**
   * Stops a bundle
   *
   * @param  prgId   Program Id of the bundle
   * @return  TRUE if the bundle exists and stopped successfully
   * @exception  RemoteException   Thrown if a problem occurs.
   */
  public synchronized boolean stopBundle(String prgId) throws RemoteException {

    method = "RFCRuntimeInterfaceImpl.stopBundle(String prgId)";
    // Check if the bundle exists
    if (!bundles.containsKey(prgId)) {
        RFCApplicationFrame.logError(method, "JCo RFC Provider bundle not found "+prgId +" on node="+nodeId+" for stopping", null);
        throw new RemoteException("JCo RFC Provider bundle not found "+prgId +" on node="+nodeId+ " for stopping"); 
    }

    Bundle bndl = bundles.get(prgId);
    BundleConfiguration config = bndl.getConfiguration();

    if (!config.isRunning())
    {
      RFCApplicationFrame.logError(method, "JCo RFC Provider bundle was already stopped "+prgId +" on node="+nodeId, null);
      //throw new RemoteException("Bundle is not running "+prgId +" on node="+nodeId); 
    }

    
    try
	{
    	bndl.stopAll();
    	if (RFCApplicationFrame.isLogged(Severity.INFO))
            RFCApplicationFrame.logInfo(method, "JCo RFC Provider bundle "+ prgId +" stopped from gui on node="+nodeId, null);
	}
	catch (RemoteException e)
	{
		RFCApplicationFrame.logError(method, "JCo RFC Provider bundle "+prgId +" couldn't not be stopped from gui on node="+nodeId, null);
		throw e;
	}
	finally
	{
		changeRunningStateInConfig(config);
	}
    
    return true;
  }

  /**
   * Adds a bundle
   *
   * @param  conf   The bundle's configuration settings
   * @return   TRUE if added successfully
   * @exception  RemoteException   Thrown if a problem occurs.
   */
  public synchronized boolean addBundle(BundleConfiguration conf) throws RemoteException {

    method = "RFCRuntimeInterfaceImpl.addBundle(BundleConfiguration conf)";

    Bundle bndl = new Bundle();
    // Initialize the bundle
    bndl.initBundle(conf, (RFCRuntimeInterface)this);
    // Add the bundle
    bundles.put(conf.getProgramId(), bndl);

    ConfigurationHandler handler =null ;
    Configuration rfcconfig = null ;
    try{// try transaction
      handler = cfgfactory.getConfigurationHandler() ;

      // add local bundle
      if (conf.isLocal())
      {
        //check if configuration with such ProgramId exists in global area too
    	try
    	{
	         rfcconfig = handler.openConfiguration(RFCENGINE_CFG_GLOBAL,ConfigurationHandler.READ_ACCESS) ;
	         
	         if (rfcconfig.existsSubConfiguration(conf.getProgramId())) 
	         {
	         	RFCApplicationFrame.traceError("Creating local configuration "+conf.getProgramId()+" failed, as a global configuration with this ProgramId exists");
	             throw new RemoteException("Creating local configuration "+conf.getProgramId()+" failed, as a global configuration with this ProgramId exists");
	         }
    	}
      	catch(ConfigurationException ce)
      	{
      		// $JL-EXC$ , configuration doesn't exists or cannot be opened
      	}
        finally
        {
        	handler.closeAllConfigurations();
        }
        // end check
    	        
    	ClusterElement element = cluster.getClusterMonitor().getCurrentParticipant();
        String  clusterId = (new Integer(element.getClusterId())).toString();
        try{
          rfcconfig = handler.openConfiguration(RFCENGINE_CFG_LOCAL,ConfigurationHandler.WRITE_ACCESS) ;
        } catch(ConfigurationException e){//logical exception
          rfcconfig = handler.createRootConfiguration( RFCENGINE_CFG_LOCAL ) ;
        }
        Configuration  cfg = null;

        if (!rfcconfig.existsSubConfiguration(clusterId)) {
          cfg = rfcconfig.createSubConfiguration(clusterId) ;
        } else {
          cfg = rfcconfig.getSubConfiguration(clusterId) ;
        }

        if (cfg.existsSubConfiguration(conf.getProgramId())) {
          cfg.deleteSubConfigurations(new String[] {conf.getProgramId()});
        }

        Configuration bundleConf = cfg.createSubConfiguration(conf.getProgramId(), Configuration.CONFIG_TYPE_PROPERTYSHEET);
        // as local bundles exist only locally and thus changes are done locally, no
        // notifications to other nodes are necessary. By change from local to global or reverse,
        // the notification comes from global
        //handler.addConfigurationChangedListener(this , RFCENGINE_CFG_LOCAL + nodeId + bundlecfg.getProgramId()) ;

        PropertySheet propssheet = bundleConf.getPropertySheetInterface() ;
        writeInPropertySheet(propssheet, conf);

      }
      else //add global bunlde
      {
        
    	// check if configuration with such ProgramId exists in local area too
    	ClusterElement element = cluster.getClusterMonitor().getCurrentParticipant();
        String  clusterId = (new Integer(element.getClusterId())).toString();
      	try
      	{
           rfcconfig = handler.openConfiguration(RFCENGINE_CFG_LOCAL,ConfigurationHandler.READ_ACCESS) ;
           
           Configuration  cfg = null;

           if (rfcconfig.existsSubConfiguration(clusterId)) {
        	   cfg = rfcconfig.getSubConfiguration(clusterId) ;
        	   if (cfg.existsSubConfiguration(conf.getProgramId())) {
        		   String errorString = "Creating global configuration "+conf.getProgramId()+" on node="+this.nodeId
        		   +" failed, as a local configuration with this ProgramId exists on node="+clusterId;
        		   RFCApplicationFrame.traceError(errorString);
	               throw new RemoteException(errorString);
               }
           	} 
      	}
      	catch(ConfigurationException ce)
      	{
      		// $JL-EXC$ , configuration doesn't exists or cannot be opened
      	}
        finally
        {
          	handler.closeAllConfigurations();
        }
        // end check  
    	  
    	try
    	{
         rfcconfig = handler.openConfiguration(RFCENGINE_CFG_GLOBAL,ConfigurationHandler.WRITE_ACCESS) ;
        }catch(ConfigurationException e){//logical exception
         rfcconfig = handler.createRootConfiguration( RFCENGINE_CFG_GLOBAL ) ;
        }

        if (rfcconfig.existsSubConfiguration(conf.getProgramId())) {
                    rfcconfig.deleteSubConfigurations(new String[] {conf.getProgramId()});
        }

        Configuration cfg = rfcconfig.createSubConfiguration(conf.getProgramId(), Configuration.CONFIG_TYPE_PROPERTYSHEET) ;
        handler.addConfigurationChangedListener(this , RFCENGINE_CFG_GLOBAL + conf.getProgramId()) ;
        PropertySheet prsheet = cfg.getPropertySheetInterface();
        writeInPropertySheet(prsheet, conf);
      }

      handler.commit() ;
      //handler.closeConfiguration(rfcconfig);
      //rfcconfig.close();
    }catch(ConfigurationException  e )
    {
		RFCApplicationFrame.traceError("Commit to the database failed");
		LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
        try{
            handler.rollback() ;
			//handler.closeConfiguration(rfcconfig);
        }catch(ConfigurationException cfg){

          RFCApplicationFrame.traceError("Rollback of the database transaction failed");
          LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, cfg);

            //Very bad - Log  this .
            // transaction failed  and  rollback  failed.
            // configuration could be  dirty !
    	}
    }
    finally
    {
		try
		{
			handler.closeAllConfigurations();
		}catch(ConfigurationException cfg){
			RFCApplicationFrame.traceError("Failed closing configuration"+" on node="+nodeId);
		}
    }
    
    if (conf.isRunning()) 
    {
      try
      {
    	  bndl.startAll();
    	  if (RFCApplicationFrame.isLogged(Severity.INFO))
              RFCApplicationFrame.logInfo(method, "JCo RFC Provider bundle added and started "+conf.getProgramId()+" on node="+nodeId, null);
      }
      catch (RuntimeException e)
      {
    	  conf.setRunningState(false); // we need to set it to false, if the exception from initialize pool is thrown and we need to set the non-running status
    	  RFCApplicationFrame.logError(method, "JCo RFC Provider bundle can not be added "+conf.getProgramId()+" on node="+nodeId, null);
    	  LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
    	  throw e;
      }
    }
    else
    {
    	if (RFCApplicationFrame.isLogged(Severity.INFO))
    		RFCApplicationFrame.logInfo(method, "JCo RFC Provider bundle added without start "+conf.getProgramId()+" on node="+nodeId, null);
    }
    
    return true;
    
  }

  /**
   * Removes a bundle
   *
   * @param  prgId   Program ID of the bundle to be removed
   * @return   TRUE if added successfully
   * @exception  RemoteException   Thrown if a problem occurs.
   */
  public synchronized boolean removeBundle(String prgId) throws RemoteException 
  {
	  method = "RFCRuntimeInterfaceImpl.removeBundle(String prgId)";
	  //RFCApplicationFrame.logInfo(method, "in method removeBundle", null);

	  if (!bundles.containsKey(prgId)) {
		  throw new RemoteException("JCo RFC Provider bundle "+prgId+" not found on removing on node="+nodeId);
	  }

	  Bundle bndl = bundles.get(prgId);
	  BundleConfiguration conf = bndl.getConfiguration();


	  try
	  {
		  bndl.stopAll();
	  }
	  catch(Exception e)
	  {
		  RFCApplicationFrame.logError(method, "JCo RFC Provider bundle "+conf.getProgramId()
				  +" cannot be stopped on removing on node="+nodeId, null);
	  }
	  finally
	  {
		  bundles.remove(prgId);
		  ConfigurationHandler  handler = null;
		    try {
		      handler = cfgfactory.getConfigurationHandler();

		      if (conf.isLocal()) {
		        deleteConfFromLocal(handler, conf);
		      } else {
		        deleteConfFromGlobal(handler, conf);
		      }
		      if (RFCApplicationFrame.isLogged(Severity.INFO))
		            RFCApplicationFrame.logInfo(method, "JCo RFC Provider bundle "+
		            		conf.getProgramId() +" removed on node="+nodeId, null);

		    } 
		    catch (ConfigurationException e) {
			      try{
			        handler.rollback() ;
		            if (RFCApplicationFrame.isLogged(Severity.INFO))
		                RFCApplicationFrame.logInfo(method, "Rollback by removing JCo RFC Provider bundle "+conf.getProgramId(), null);
					//handler.closeConfiguration(conf);
			      }catch(ConfigurationException cfg){
					RFCApplicationFrame.logError(method, "Rollback failed by removing JCo RFC Provider bundle "+conf.getProgramId(), null);
			        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, cfg);
			      }
			      RFCApplicationFrame.logError(method, "Failed to remove JCo RFC Provider bundle "+conf.getProgramId()+ 
			    		  " from database" +" on node="+nodeId, null);
				  throw new RemoteException("Failed to remove JCo RFC Provider bundle "+conf.getProgramId()+ " from database on node="+nodeId);
		   }
		   finally
		   {
			   try
			   {
				   handler.closeAllConfigurations();
			   }catch(ConfigurationException cfg){
				   RFCApplicationFrame.traceError("Failed closing configuration");
			   }
		   }
	  }
	  return true;
  }

  /**
   * Changes the number of processes in a bundle
   * It is only called for running Bundle to change Server number on Live
   *
   * @param  prgId   Program Id of the bundle
   * @return  TRUE if the bundle exists process number could be changed
   * @exception  RemoteException   Thrown if a problem occurs.
   */
  private synchronized void changeNumberOfProcesses(String prgId, int newNumber) throws RemoteException {
    Bundle bndl = bundles.get(prgId);
    //RFCApplicationFrame.logInfo("changeNumberOfProcesses", "method  changeNumberOfProcesses for "+prgId,  new Object[] {prgId});
    if (RFCApplicationFrame.isLogged(Severity.INFO))
        RFCApplicationFrame.logInfo("changeNumberOfProcesses", "Change process number for JCo RFC Provider bundle "+prgId, null);
    if (bndl == null) 
    	throw new RemoteException ("Cannot change number of servers for non-existent JCo RFC Provider bundle "+prgId);

    int oldNumber = bndl.getProcessesNumber();

    if (oldNumber == newNumber) {
      return;
    }

    int max = this.getMaxProcesses();

    if (newNumber <= 0) 
    	throw new RemoteException ("Server number should be a positive number for JCo RFC Provider bundle "+prgId);
    else if (newNumber > max) newNumber = max;
    
    // save this newNumber in the bundle configuration without restarting servers
    BundleConfiguration config = bndl.getConfiguration();
    config.setProcessesNumber(newNumber);
    saveBundleConfiguration(config);

    // and now adjust the number of servers
    if (oldNumber > newNumber)  bndl.remove(oldNumber - newNumber);
    else                        bndl.add(newNumber - oldNumber);
    
    
  }
  
  /**
   * Saves configuration of a bundle without restarting it
   *
   * @param  conf   The new configuration to be set
   * @exception  RemoteException   Thrown if a problem occurs.
   */
  public synchronized void saveBundleConfiguration(BundleConfiguration conf) throws RemoteException 
  {
    method = "RFCRuntimeInterfaceImpl.saveBundleConfiguration(BundleConfiguration config)";
    ConfigurationHandler  handler = null;
    try {
      handler = cfgfactory.getConfigurationHandler();

      if (conf.isLocal()) {
      	setLocalConfiguration(handler, conf);
      } else {
      	setGlobalConfiguration(handler, conf);
      }
      if (RFCApplicationFrame.isLogged(Severity.INFO))
          RFCApplicationFrame.logInfo(method, "JCo RFC Provider bundle "+conf.getProgramId()+" saved", null);

    } 
    catch (ConfigurationException e) {
      try{
        handler.rollback() ;
        if (RFCApplicationFrame.isLogged(Severity.INFO))
            RFCApplicationFrame.logInfo(method, "Rollback by saving JCo RFC Provider bundle "+conf.getProgramId(), null);
		//handler.closeConfiguration(conf);
      }catch(ConfigurationException cfg){
		RFCApplicationFrame.logError(method, "Rollback failed by saving JCo RFC Provider bundle "+conf.getProgramId(), null);
        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, cfg);
      }
      RFCApplicationFrame.logError(method, "JCo RFC Provider bundle "+conf.getProgramId()+"configurations couldn't be saved", null);
      throw new RemoteException ("JCo RFC Provider bundle "+conf.getProgramId()+"configurations couldn't be saved");
   }
   finally
   {
	   try
	   {
		   handler.closeAllConfigurations();
	   }catch(ConfigurationException cfg){
		   RFCApplicationFrame.traceError("Failed closing configuration");
	   }
   }
  }

  /**
   * Changes the configuration of a bundle
   *
   * @param  newConfig   The new configuration to be set
   * @return   TRUE if changed successfully
   * @exception  RemoteException   Thrown if a problem occurs.
   */
  public synchronized boolean changeBundleConfiguration(BundleConfiguration newConfig) throws RemoteException {
    method = "RFCRuntimeInterfaceImpl.changeBundleConfiguration(BundleConfiguration config)";

    String programId = newConfig.getProgramId();
    if (RFCApplicationFrame.isLogged(Severity.INFO))
        RFCApplicationFrame.logInfo(method, "Call  changeBundleConfiguration for JCo RFC Provider bundle "+programId+" on node="+nodeId, null);
    // Check if the bundle exists
    if (!bundles.containsKey(programId)) {
    	throw new RemoteException("Cannot change not existent JCo RFC Provider bundle "+programId);
    }

    Bundle oldBundle = bundles.get(programId);
    
    // If the configuration is the same - exit
    BundleConfiguration oldConfig = oldBundle.getConfiguration();
    try
    {
    	if (oldBundle.getConfiguration().equalsWithoutProcesses(newConfig)) 
    	{
    		if (oldConfig.getProcessesNumber()== newConfig.getProcessesNumber()) 
    		{
	    		if (oldConfig.isRunning() && !newConfig.isRunning())
		  		{
		  			if (RFCApplicationFrame.isLogged(Severity.INFO))
		  	            RFCApplicationFrame.logInfo(method, "In changeBundleConfiguration() stop JCo RFC Provider bundle  "+
		  	            		programId+" on node="+nodeId, null);
		  			oldBundle.stopAll();
		  			changeRunningStateInConfig(newConfig);
		  		}
		    	else if (!oldConfig.isRunning() && newConfig.isRunning())
		    	{
		    		if (RFCApplicationFrame.isLogged(Severity.INFO))
		            RFCApplicationFrame.logInfo(method, "In changeBundleConfiguration() start JCo RFC Provider bundle "+
		            		programId+" on node="+nodeId, null);
		    		oldBundle.startAll();
		    		changeRunningStateInConfig(newConfig);
		    	}
		    	else
		    	{
		    		if (RFCApplicationFrame.isLogged(Severity.INFO))
		                RFCApplicationFrame.logInfo(method, "Configuration of bundle does not need change for JCo RFC Provider bundle "+
		                		programId+" on node="+nodeId, null);
		       
		            setStaticProperties(newConfig);
		    	}
	    		return true;
    		}// end processing same process number
    		//  start processing diff. process number
    		else if (oldConfig.isRunning() && newConfig.isRunning())
        	{
        		if (RFCApplicationFrame.isLogged(Severity.INFO))
      	            RFCApplicationFrame.logInfo(method, "On JCo RFC Provider bundle "+programId+
      	            		" the process number is changed from oldNumber="+
      	            		"="+oldConfig.getProcessesNumber()+", to newNumber="+
      	            		newConfig.getProcessesNumber() +" on node="+nodeId, null);
    	    	changeNumberOfProcesses(programId, newConfig.getProcessesNumber());
    	    	return true;
        	}
        }// equalsWithoutProcesses
    	
    	if (newConfig.isLocal() != oldConfig.isLocal())
    	{
    		removeBundle(programId);
    		addBundle(newConfig);
    	}
    	else
    	{

	  		if (oldBundle.getConfiguration().isRunning())
	  			oldBundle.stopAll();
	  	  	bundles.remove(programId);
	  	  	
	    	Bundle newBundle = new Bundle();
	    	newBundle.initBundle(newConfig, (RFCRuntimeInterface)this);
	        bundles.put(programId, newBundle);
	        
	        saveBundleConfiguration(newConfig);
	        
	        if (newConfig.isRunning())
	        	newBundle.startAll();
    	}
  	  	if (RFCApplicationFrame.isLogged(Severity.INFO))
            RFCApplicationFrame.logInfo(method, "JCo RFC Provider bundle "+ programId+" changed on node="+nodeId, null);
    }
    catch (RemoteException e)
    {
  	  RFCApplicationFrame.logError(method, "Error changing JCo RFC Provider bundle "+programId+" on node="+nodeId, null);
  	  LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
  	  throw new RemoteException("Error changing JCo RFC Provider bundle "+programId+" on node="+nodeId,e);
    }
    
    return true;
  }



  /**
   * Returns a bundle configuration
   *
   * @return  The requested bundle configuration or null if it doesn't exists
   */
  public synchronized BundleConfiguration getConfiguration(String programId) throws RemoteException {
  	method = "RFCRuntimeInterfaceImpl.getConfiguration(String programId)";
  	Bundle bndl = bundles.get(programId);

    if (bndl != null) {

      BundleConfiguration conf = bndl.getConfiguration();

      // update static properties. Move them to getProperty method, which
      // should be called only in initTrace
      conf.setJcoTraceLevel(JCO.getTraceLevel());
      conf.setJarm(RFCJCOServer.useJarm);
      
	  String trace = JCO.getMiddlewareProperty("jrfc.trace");
      if (trace != null)
      {
      	if (trace.equals("1"))
      		conf.setGlobalJrfcTrace(true);
      	else 
      		conf.setGlobalJrfcTrace(false);
      }
      
      trace = JCO.getMiddlewareProperty("cpic.trace");
      if (trace != null)
      {
      	try {
      		int level = Integer.parseInt(trace);
          	conf.setCpicTraceLevel(level);
		} catch (Exception e) {
			// $JL-EXC$ , works as designed
			RFCApplicationFrame.logError(method, "Illegal cpic.trace property "+trace+
            		" detected when setting the configuration for JCo RFC Provider bundle "+programId,  null);
		}
      	
      }

      return conf;
    }

    return null;
  }

  /**
   * Returns all bundle configurations
   *
   * @return   The configurations of all bundles
   */
  public synchronized BundleConfiguration[] getConfigurations() throws RemoteException {

    Enumeration<Bundle> enumer = bundles.elements();
    BundleConfiguration[] result = new BundleConfiguration[bundles.size()];
    int i = 0;

    while (enumer.hasMoreElements()) {
      result[i++] = (enumer.nextElement()).getConfiguration();
    }

    return result;
  }

  /**
   * Returns all bundles
   *
   * @return   All bundles stored in this class
   */
  synchronized Hashtable getBundles() {
    return this.bundles;
  }
  
  /**
   * Returns all bundle names
   *
   * @return   All bundle names
   */
  synchronized String [] getDestinationNames() {
  	Enumeration en = bundles.keys();
  	String [] s = new String[bundles.size()];
  	int i = 0;
  	while (en.hasMoreElements())
  	{
  		s[i++] = (String)en.nextElement();
  	}
  	return s;
  }
  
  /**
   * Returns all bundle names
   *
   * @return   All bundle names
   */
  synchronized Properties getDestinationProperties(String progId) {
  	try {
  		Properties p = getConfiguration(progId).getProperties();
  		if (RFCApplicationFrame.isLogged(Severity.INFO))
    	{
    		RFCApplicationFrame.logInfo(method, "returned Properties "+" on node="+nodeId+" for "+progId+" : "+p, null);
    	}
  		return p;
	} catch (RemoteException e) {
		RFCApplicationFrame.logError("getDestinationSettings", "Can not retrieve destination configuration !", new Object[] {progId});
        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, "getDestinationSettings", e);
	}
  	return null;
  }
  
  public void setDestinationProperties(Properties props)  throws RemoteException
  {
  	String progId = props.getProperty(BundleConfiguration.PROPERTIES[0]);
  	if (progId == null || "".equals(progId)) throw new RemoteException("ProgramId must be defined");
  	
  	if (RFCApplicationFrame.isLogged(Severity.INFO))
	{
        Properties p = (Properties)props.clone();
        if (p.containsKey(BundleConfiguration.PROPERTIES[17]))
        {
            String pass = p.getProperty(BundleConfiguration.PROPERTIES[17]);
        	if (pass != null && !"".equals(pass) 
        			&& !BundleConfiguration.PASSWORD_STARS.equals(pass))
        		//so real password set and sent the first time 
        		p.put(BundleConfiguration.PROPERTIES[17],"***");
        }
		RFCApplicationFrame.logInfo(method, "passed Properties on node="+nodeId+" for "+progId+" : "+p, null);
	}

	if (getConfiguration(progId) == null) 
	{
	    BundleConfiguration newConfig = new BundleConfiguration();
        newConfig.setProgramId(progId);
        addDestination(props, newConfig);
	}
	else
		changeDestination(props, new BundleConfiguration(getConfiguration(progId)));
  }
  
  
  public String getDestinationPropertiesTemplate()
  {
	return BundleConfiguration.REQUIRED_PROPERTIES;
  }
  
  protected void addDestination(Properties props, BundleConfiguration config) throws RemoteException
  {
	parseProperties(props, config);
	addBundle(config);
	
  }
  
  protected void changeDestination(Properties props, BundleConfiguration config) throws RemoteException
  {
  	// first copy existent config properties into config
  	// and then overwrite it with new properties
  	parseProperties(props, config);
  	changeBundleConfiguration(config);
  }

  /**
   * Return the maximum processes in a bundle
   *
   * @return   The maximum processes
   */
  public synchronized int getMaxProcesses() {
    return this.maxProcesses;
  }

  /**
   * Return the maximum connections for a paradigm
   *
   * @return   The maximum processes
   */
  public synchronized int getMaxConnections() {
    return this.maxConnections;
  }

  /**
   * Sets the maximum processes and connections
   *
   * @param  prcs   The maximum processes
   * @param  conn   The maximum connections
   */
  synchronized void newProperties(int prcs, int conn) {
    method = "RFCRuntimeInterfaceImpl.newProperties(int prcs, int conn)";
    this.maxProcesses = prcs;
    this.maxConnections = conn;
    Enumeration<Bundle> configs = bundles.elements();
    String programId = "";

    while (configs.hasMoreElements()) {
      BundleConfiguration cfg = (configs.nextElement()).getConfiguration();
      programId = cfg.getProgramId();

      // Change number of JCo Servers 
      // if the new max number of JCo servers become smaller as 
      // the actual number of JCo Servers, then we need to reduce
      // the number of running servers on all destinations. 
      // Otherwise we don't need to do here any changes
      if (cfg.getProcessesNumber() > maxProcesses) {
    	// thus we need to reduce the process number
    	if (cfg.isRunning())
      	{
      		if (RFCApplicationFrame.isLogged(Severity.INFO))
    	            RFCApplicationFrame.logInfo(method, "JCo RFC Provider bundle "+programId+
    	            		" changes process number by reducing the max allowed number on newProperties(): oldNumber="+
    	            		"="+cfg.getProcessesNumber()+", newNumber="+
    	            		maxProcesses +" on node="+nodeId, null);
      		try
			{
      			changeNumberOfProcesses(programId, maxProcesses);
			}
			catch (Exception e)
			{
				RFCApplicationFrame.logError(method, "Can not change process number for JCo RFC Provider bundle "+
						programId+" on node="+nodeId, new Object[] {programId});
		        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
			}
      	}  
    	else
    		cfg.setProcessesNumber(maxProcesses);
      }
      
      // Change number of repository connections in case if it was
      // changed
      if (cfg.getMaxConnections() != maxConnections) {
    	  BundleConfiguration newConfig = new BundleConfiguration(cfg);
    	  newConfig.setMaxConnections(maxConnections);
    	  try 
    	  {
              changeBundleConfiguration(cfg);
          } 
    	  catch (RemoteException e) 
    	  {
              RFCApplicationFrame.logError(method, "Can not change maxConnections for JCo RFC Provider bundle "+
						programId+" on node="+nodeId, new Object[] {programId});
              LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
          }
      }
    }
  }

  public void receive(int clusterId, int messageId, byte[] message, int offset, int length) {
    method = "RFCRuntimeInterfaceImpl.receive(int clusterId, int messageId, byte[] message, int offset, int length)";

    try {
         cleanup();
         init();
    } catch (RemoteException re) {
        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, re);
            remoteCall = false;
            return;
    }


    remoteCall = false;
  }

  public MessageAnswer receiveWait(int clusterId, int messageId, byte[] message, int offset, int length) {
    return new MessageAnswer(null, 0, 0);
  }

  private synchronized void send(BundleConfiguration cfg, byte msgId) {
    method = "RFCRuntimeInterfaceImpl.send(BundleConfiguration cfg, byte msgId)";

    if (remoteCall) {
      remoteCall = false;
      return;
    }

    ClusterElement[] elements = cluster.getClusterMonitor().getParticipants();

    for (int i = 0; i < elements.length; i++) {
      int id = elements[i].getClusterId();

      if (elements[i].getType() == ClusterElement.SERVER) {
        byte[] arr = null;
        try {
          arr = getByteArray(cfg);
        } catch (Exception e) {
          RFCApplicationFrame.logError(method, "Message could not be sent due to serialization problems !", new Object[] {new Integer(msgId)});
          return;
        }

        try {
          cluster.getMessageContext().send(id, msgId, arr, 0, arr.length);
        } catch (ClusterException e) {
            LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
          // TO DO LOG, HANDLE UNSENT
        }
      }
    }
  }

  /**
   * Serializes an object
   *
   * @param  obj   Object to serialize
   * @return   The serialized object
   */
  private static byte[] getByteArray(Object obj) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    try {
      oos.writeObject(obj);
      oos.close();
    } catch (Exception ex) {
      oos.close();
      throw ex;
    }
    return baos.toByteArray();
  }

  /**
   * Deserializes an object
   *
   * @param  obj   Object's byte array to deserialize
   * @return   The deserialized object
   */
  private static Object getObject(byte[] obj) throws Exception {
    ByteArrayInputStream bais = new ByteArrayInputStream(obj);
    ObjectInputStream ois = new ObjectInputStream(bais);
    Object object = null;
    try {
      object = ois.readObject();
      ois.close();
    } catch (Exception ex) {
      ois.close();
      throw ex;
    }
    return object;
  }

  public void registerManagementListener( ManagementListener managementListener ) {
    /** @todo registerManagmentListener */
  }

  public void configurationChanged(com.sap.engine.frame.core.configuration.ChangeEvent e) {
    method = "RFCRuntimeInterfaceImpl.configurationChanged(com.sap.engine.frame.core.configuration.ChangeEvent e)";
    if (RFCApplicationFrame.isLogged(Severity.INFO))
        RFCApplicationFrame.logInfo(method, "configurationChanged"+" on node="+nodeId, null);

    String programId = null;
    ConfigurationHandler handler = null;
    BundleConfiguration newConfig = new BundleConfiguration();
    Bundle newBundle = null;
    Configuration rootConfig = null ;
    Configuration subConfig = null;
    
    int action = ChangeEvent.ACTION_MODIFIED;
    String path = null;
    int detailedAction = 0;
    String detailedPath = null;
    ChangeEvent ev[] = e.getDetailedChangeEvents();
    ChangeEvent event = null;

    boolean isAdded = false;
    boolean isRemoved = false;
    for (int i=0;i<ev.length;i++)
    {
    	event = (ChangeEvent)ev[i];
    	detailedPath = event.getPath();
    	detailedAction = event.getAction();
    	if (RFCApplicationFrame.isLogged(Severity.INFO))
            RFCApplicationFrame.logInfo(method, "configurationChanged for detailedPath="+
            		detailedPath+" with detailedAction="+detailedAction+
            		" number="+i+" on node="+nodeId, null);
    	if (path == null)
    	{
    		if (detailedPath != null && detailedPath.length()<=RFCENGINE_CFG_GLOBAL.length())
    			continue;
    		else
    			path = detailedPath;
    	}
    	
    	switch(detailedAction){
    		case 0: break;
    		case 1: 
    		{
    			isAdded = true;
    			action = ChangeEvent.ACTION_CREATED;
    			break;
    		}
    		case 2: 
    		{
    			isRemoved = true;
    			action = ChangeEvent.ACTION_DELETED;
    			break;
    		}
    		
    		default: 
    		{
    	        RFCApplicationFrame.logError(method, "configurationChanged, not known event with for detailedPath="+detailedPath+
    	            		" with detailedAction="+detailedAction+
    	            		" on node="+nodeId, null);
    		}
    	}
    }
    if (isAdded && isRemoved)
    	action = ChangeEvent.ACTION_MODIFIED;

    if (RFCApplicationFrame.isLogged(Severity.INFO))
        RFCApplicationFrame.logInfo(method, "configurationChanged for path="+path+
        		" with action="+action+" on node="+nodeId, null);
    
    if (path != null)
    {
    	if (path.startsWith(RFCENGINE_CFG_GLOBAL)) 
    	{
            if (path.length()>18)
            	programId = path.substring(RFCENGINE_CFG_GLOBAL.length()+1);
    	}
        else
        {
        	if (RFCApplicationFrame.isLogged(Severity.ERROR))
                RFCApplicationFrame.logError(method,"invalid path name in configurationChanged path="+path, null);
        	return;
        }
    }	
    else
    {
    	RFCApplicationFrame.logError(method, "invalid path name in configurationChanged path=null", null);
    	return;
    }
    
    synchronized (this)
    {
    
	    if (programId == null || "".equals(programId))
	    {
	    	RFCApplicationFrame.logError(method,"invalid programId="+programId+" in configurationChanged" +
	    			" on node="+nodeId, null);
	    	return;
	    }
	    else if (RFCApplicationFrame.isLogged(Severity.INFO))
	        RFCApplicationFrame.logInfo(method, "configurationChanged for "+programId +" on node="+nodeId, null);
	    
	    if (bundles.containsKey(programId))
	    {
	    	Bundle bundle = bundles.get(programId);
	    	if (bundle.getConfiguration().isLocal())
	    	{
	    		// local bundles are changed within the same node.
	    		// Event to a local bundle can only come in the case,
	    		// when global bundle is changed to local
	    		RFCApplicationFrame.logInfo(method, "Event" +action+" for local Bundle "+programId+
	  	        		" on node="+nodeId +". Ignore it as it is an own event", null);
	    		return;
	    	}
	    }
	    
	    if (ChangeEvent.ACTION_DELETED == action)
	    {
	    	if (!bundles.containsKey(programId)) {
	  	        RFCApplicationFrame.logInfo(method, "Bundle not found "+programId+
	  	        		" in configurationChanged with ACTION_DELETED on node="+nodeId 
	  	        		+". Apperently it is own event", null);
	  	        return;
	    	}
	    	Bundle bundle = bundles.get(programId);
	    	
	    	try
	    	{
	    		bundle.stopAll();
	    	}
	    	catch(Exception ex)
	    	{
	    		RFCApplicationFrame.logError(method, "Couldn't stop JCo RFC Provider bundle "+programId+
	  	        		"in configurationChanged with ACTION_DELETED on node="+nodeId+". cause: "+ex.getMessage(), null);
	    		LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, ex);
	    	}
	    	
	    	bundles.remove(programId);
	    	if (RFCApplicationFrame.isLogged(Severity.INFO))
	            RFCApplicationFrame.logInfo(method, "JCo RFC Provider bundle "+programId+" removed on configurationChanged() on node="+nodeId, null);
	    	return;
	    }
	    
	    //for actions ACTION_MODIFIED and ACTION_CREATED we need to read
	    //the configurations from data base
	    try 
	    {
	          handler = cfgfactory.getConfigurationHandler() ;
	          try{
	            rootConfig = handler.openConfiguration(RFCENGINE_CFG_GLOBAL,ConfigurationHandler.READ_ACCESS) ;
	          } catch(ConfigurationException ex){//logical exception
	            rootConfig = handler.createRootConfiguration( RFCENGINE_CFG_GLOBAL ) ;
	          }
	          subConfig = rootConfig.getSubConfiguration(programId);
	          if (subConfig != null) {
	            PropertySheet propssheet = subConfig.getPropertySheetInterface() ;
	            readFromPropertySheet(propssheet, newConfig);
	          }
	        
	    } 
	    catch (Exception exept) 
	    {
	    	LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, exept);
	    	RFCApplicationFrame.logError(method, "Couldn't read confgiration for JCo RFC Provider bundle "+programId+
		        		"in configurationChanged with ACTION_MODIFIED. cause: "+exept.getMessage(), null);
	    	try
			{
				handler.closeAllConfigurations();
			}catch(ConfigurationException cfg){
				RFCApplicationFrame.traceError("Failed closing configuration");
			}        
			return;
	    }
	        	
	    switch(action){
		    // the database is already updated.
		  	// we just need to load new settings in memory
		    case  ChangeEvent.ACTION_MODIFIED: {
		  		if (!bundles.containsKey(programId)) {
		  	        RFCApplicationFrame.logError(method, "JCo RFC Provider bundle not found "+programId+
		  	        		" in configurationChanged with ACTION_MODIFIED on node="+nodeId, null);
		  	        return;
		  		}
		  		
		  		Bundle oldBundle = bundles.get(programId);
		  		BundleConfiguration oldConfig = oldBundle.getConfiguration();
		  		
		  		if (RFCApplicationFrame.isLogged(Severity.DEBUG))
	  	            RFCApplicationFrame.logInfo(method, "In configurationChanged ACTION_MODIFIED "+
	  	            		programId+" on node="+nodeId+", oldConfig="+oldConfig.toString(), null);
		  		if (RFCApplicationFrame.isLogged(Severity.DEBUG))
	  	            RFCApplicationFrame.logInfo(method, "In configurationChanged ACTION_MODIFIED "+
	  	            		programId+" on node="+nodeId+", newConfig="+newConfig.toString(), null);
	
		  		try
				{
			  	    if (oldConfig.equalsWithoutProcesses(newConfig)) 
			  	    {
			  	    	if (oldConfig.getProcessesNumber()== newConfig.getProcessesNumber())
			  	    	{
			  	    		if (oldConfig.isRunning() && !newConfig.isRunning())
			  	    		{
			  	    			if (RFCApplicationFrame.isLogged(Severity.INFO))
					  	            RFCApplicationFrame.logInfo(method, "In configurationChanged stop bundle "+
					  	            		programId+" on node="+nodeId, null);
			  	    			oldBundle.stopAll();
			  	    		}
				  	    	else if (!oldConfig.isRunning() && newConfig.isRunning())
				  	    	{
				  	    		if (RFCApplicationFrame.isLogged(Severity.INFO))
					  	            RFCApplicationFrame.logInfo(method, "In configurationChanged start bundle "+
					  	            		programId+" on node="+nodeId, null);
				  	    		oldBundle.startAll();
				  	    	}
				  	    	else if	(oldConfig.equals(newConfig))
				  	    		// the configurations are same. So, this is server's own notification
				  	    		// that the server unfortunatelly gets. So we skip it here
				  	    	{
				  	    		if (RFCApplicationFrame.isLogged(Severity.INFO))
					  	            RFCApplicationFrame.logInfo(method, "In configurationChanged own event on ACTION_MODIFIED for "+
					  	            		programId+" on node="+nodeId, null);
				  	    	}
			  	    		return;
			  	    	}
			  	    	else if (oldConfig.isRunning() && newConfig.isRunning())
			        	{
			  	    		if (RFCApplicationFrame.isLogged(Severity.INFO))
				  	            RFCApplicationFrame.logInfo(method, "JCo RFC Provider bundle "+programId+
				  	            		" changes process number on configurationChanged: oldNumber="+
				  	            		"="+oldConfig.getProcessesNumber()+", newNumber="+
				  	            		newConfig.getProcessesNumber() +" on node="+nodeId, null);
			  	    		changeNumberOfProcesses(programId, newConfig.getProcessesNumber());
			  	    		return;
			        	}
			  	    }// equalsWithoutProcesses
			  	}// try
				catch (Exception ex)
				{
					RFCApplicationFrame.logError(method, "Couldn't change running state for JCo RFC Provider bundle "+programId+
		  	        		"in configurationChanged with ACTION_MODIFIED. cause: "+ex.getMessage(), null);
		    		LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, ex);
				}
		  	    
		  	    // we need to remove the old bundle and add a new one
		  	    try
				{
		  	    	if (RFCApplicationFrame.isLogged(Severity.INFO))
		  	            RFCApplicationFrame.logInfo(method, "Stop JCo RFC Provider bundle "+programId+
		  	            		"nn configurationChanged for action ACTION_MODIFIED on node="+nodeId, null);
		  	        if (oldConfig.isRunning())
		  	        	oldBundle.stopAll();
				}
				catch (Exception ex)
				{
					RFCApplicationFrame.logError(method, "Couldn't stop JCo RFC Provider bundle "+programId+
		  	        		"in configurationChanged with ACTION_MODIFIED. cause: "+ex.getMessage(), null);
		    		LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, ex);
				}
		  	    
		    	bundles.remove(programId);
		    	// we don't break and go below
		    }
		    case ChangeEvent.ACTION_CREATED:
		    {
		    	if (bundles.containsKey(programId)) {
		  	        RFCApplicationFrame.logInfo(method, "JCo RFC Provider bundle "+programId+
		  	        		" already exists in configurationChanged with ACTION_CREATED on node="+nodeId 
		  	        		+". Apperently it is own event", null);
		  	        return;
		    	}
		    	newBundle = new Bundle();
		    	newBundle.initBundle(newConfig, (RFCRuntimeInterface)this);
		        // Add the bundle
		    	bundles.put(programId, newBundle);
		    	try
				{
		    		if (newConfig.isRunning())
		    		{
			    		if (RFCApplicationFrame.isLogged(Severity.INFO))
			  	            RFCApplicationFrame.logInfo(method, "Start JCo RFC Provider bundle "+programId+
			  	            		"on configurationChanged for action="+action +" on node="+nodeId, null);
			    		newBundle.startAll();
		    		}
		    		else
		    			if (RFCApplicationFrame.isLogged(Severity.INFO))
			  	            RFCApplicationFrame.logInfo(method, "Add JCo RFC Provider bundle "+programId+
			  	            		" without start on configurationChanged for action="+action +" on node="+nodeId, null);
				}
				catch (Exception ex)
				{
					RFCApplicationFrame.logError(method, "JCo RFC Provider bundle "+programId+
		  	        		" not started on configurationChanged with ACTION_CREATED", null);
				}
		    	
		    	break;
		    }
		    
		    default:
		    	RFCApplicationFrame.logError(method, "Not known ChangeEvent "+e.getAction()+
	  	        		"in configurationChanged for JCo RFC Provider bundle "+programId +" on node="+nodeId, null);
	    }
    }//synchronized
 }

  private void writeInPropertySheet(PropertySheet propssheet, BundleConfiguration conf) throws ConfigurationException {
    
    propssheet.createPropertyEntry(BundleConfiguration.GATEWAY_HOST, conf.getGatewayHost(), "Gateway_host");
    propssheet.createPropertyEntry(BundleConfiguration.GATEWAY_SERVER, conf.getGatewayService(), "Gateway_server");
    propssheet.createPropertyEntry(BundleConfiguration.LOCAL, (new Boolean(conf.isLocal())).toString(), "Is_local");
    
    propssheet.createPropertyEntry(BundleConfiguration.MAX_CONNECTIONS, (new Integer(conf.getMaxConnections())).toString(), "Maximum_connections");
    propssheet.createPropertyEntry(BundleConfiguration.PROCESSES_NUMBER, (new Integer(conf.getProcessesNumber())).toString(), "Process_number");
    propssheet.createPropertyEntry(BundleConfiguration.PROGRAM_ID, conf.getProgramId(), "Program_ID");
    propssheet.createPropertyEntry(BundleConfiguration.RUNNING_STATE, (new Boolean(conf.isRunning())).toString(), "Running_state");
    propssheet.createPropertyEntry(BundleConfiguration.SYSTEM_NUMBER, conf.getSystemNumber() , "System_number");

    propssheet.createPropertyEntry(BundleConfiguration.RFC_TRACE, (new Boolean(conf.getRfcTrace())).toString(), BundleConfiguration.RFC_TRACE);
    
    if (conf.useRepDest)
    {
        propssheet.createPropertyEntry(BundleConfiguration.USE_REP_DEST, (new Boolean(true)).toString(), BundleConfiguration.USE_REP_DEST);
        propssheet.createPropertyEntry(BundleConfiguration.REPOSTORY_DESTINATION, conf.getRepositoryDestination(), BundleConfiguration.REPOSTORY_DESTINATION);
    }
    else
    {
        propssheet.createPropertyEntry(BundleConfiguration.USE_REP_DEST, (new Boolean(false)).toString(), BundleConfiguration.USE_REP_DEST);
        propssheet.createPropertyEntry(BundleConfiguration.LOGON_CLIENT, conf.getLogonClient(), "Logon_client");
        propssheet.createPropertyEntry(BundleConfiguration.LOGON_LANGUAGE, conf.getLogonLanguage(), "Logon_language");
        propssheet.createSecurePropertyEntry(BundleConfiguration.LOGON_PASSWORD, conf.getLogonPassword(), "Logon_password");
        propssheet.createPropertyEntry(BundleConfiguration.LOGON_USER, conf.getLogonUser(), "Logon_user");
        propssheet.createPropertyEntry(BundleConfiguration.APPLICATION_SERVER_HOST, conf.getApplicationServerHost(), "Application_server_host");
    }

    if (conf.getUseSnc()) {
        propssheet.createPropertyEntry(BundleConfiguration.USE_SNC, (new Boolean(true)).toString(), "Use_Snc");
        propssheet.createPropertyEntry(BundleConfiguration.SNC_LIB, conf.getSncLib(), "Snc_Lib");
        propssheet.createPropertyEntry(BundleConfiguration.SNC_NAME, conf.getSncName(), "Snc_Name");
        propssheet.createPropertyEntry(BundleConfiguration.SNC_QOP, (new Integer(conf.getSncQop())).toString() , "Snc_Qop");
        propssheet.createPropertyEntry(BundleConfiguration.SNC_AUTH_PARTNER, conf.getAuthPartner(), "Snc_Auth_Partner");
    }
  }

  private void readFromPropertySheet(PropertySheet propssheet, BundleConfiguration bundlecfg) throws ConfigurationException {
    
    bundlecfg.setGatewayHost( (String)propssheet.getPropertyEntry(BundleConfiguration.GATEWAY_HOST).getValue() );
    bundlecfg.setGatewayServer( (String)propssheet.getPropertyEntry(BundleConfiguration.GATEWAY_SERVER).getValue());

    boolean local = (new Boolean((String)propssheet.getPropertyEntry(BundleConfiguration.LOCAL).getValue())).booleanValue();
    bundlecfg.setLocal( local );
    
    try {
        bundlecfg.useRepDest =  new Boolean((String)propssheet.getPropertyEntry(BundleConfiguration.USE_REP_DEST).getValue()).booleanValue();
  } catch (NameNotFoundException ex) {
         bundlecfg.useRepDest = false;
//      this property didn't exist before, because destination was created on older J2EE release. So create it
  }
    
    if (bundlecfg.useRepDest)
    {
        bundlecfg.setRepositoryDestination( (String)propssheet.getPropertyEntry(BundleConfiguration.REPOSTORY_DESTINATION).getValue());
    }
    else
    {
        bundlecfg.setLogonClient( (String)propssheet.getPropertyEntry(BundleConfiguration.LOGON_CLIENT).getValue());
        bundlecfg.setLogonLanguage( (String)propssheet.getPropertyEntry(BundleConfiguration.LOGON_LANGUAGE).getValue());
        bundlecfg.setLogonPassword( (String)propssheet.getPropertyEntry(BundleConfiguration.LOGON_PASSWORD).getValue());
        bundlecfg.setLogonUser( (String)propssheet.getPropertyEntry(BundleConfiguration.LOGON_USER).getValue());
        bundlecfg.setApplicationServerHost( (String)propssheet.getPropertyEntry(BundleConfiguration.APPLICATION_SERVER_HOST).getValue() );
    }

    // the repository pool size
    int mc= Integer.parseInt( (String)propssheet.getPropertyEntry(BundleConfiguration.MAX_CONNECTIONS).getValue()) ;
    bundlecfg.setMaxConnections( mc );
    
    // the configured number of JCO Servers
    int pn= Integer.parseInt( (String) propssheet.getPropertyEntry(BundleConfiguration.PROCESSES_NUMBER).getValue()) ;
    bundlecfg.setProcessesNumber( pn );

    bundlecfg.setProgramId( (String)propssheet.getPropertyEntry(BundleConfiguration.PROGRAM_ID).getValue());

    boolean rs = (new Boolean((String)propssheet.getPropertyEntry(BundleConfiguration.RUNNING_STATE).getValue())).booleanValue();
    bundlecfg.setRunningState( rs );

    bundlecfg.setSystemNumber( (String)propssheet.getPropertyEntry(BundleConfiguration.SYSTEM_NUMBER).getValue());

    try {
          propssheet.getPropertyEntry(BundleConfiguration.USE_SNC);
          bundlecfg.setUseSnc(true);
          bundlecfg.setSncLib((String)propssheet.getPropertyEntry(BundleConfiguration.SNC_LIB).getValue());
          bundlecfg.setSncName((String)propssheet.getPropertyEntry(BundleConfiguration.SNC_NAME).getValue());
          int qop = Integer.parseInt((String)propssheet.getPropertyEntry(BundleConfiguration.SNC_QOP).getValue());
          bundlecfg.setSncQop(qop);
          bundlecfg.setAuthPartner((String)propssheet.getPropertyEntry(BundleConfiguration.SNC_AUTH_PARTNER).getValue());

    } catch (NameNotFoundException ex) {
        //if (RFCApplicationFrame.isLogged(Severity.INFO)) RFCApplicationFrame.logInfo(method, "SNC properties not found ", null);
    }
    
	try {
			bundlecfg.setRfcTrace((new Boolean((String)propssheet.getPropertyEntry(BundleConfiguration.RFC_TRACE).getValue())).booleanValue());
		} catch (Exception ex) {
            if (RFCApplicationFrame.isLogged(Severity.INFO))
                RFCApplicationFrame.logInfo(method, "RFC Trace property not found: ", null);
			// this property didn't exist before, because destination was created on older J2EE release. So create it
		}
   }

  private void setLocalConfiguration(ConfigurationHandler handler, BundleConfiguration config) throws ConfigurationException {
     Configuration rfcconfig = null;

     try{
       rfcconfig = handler.openConfiguration(RFCENGINE_CFG_LOCAL,ConfigurationHandler.WRITE_ACCESS) ;
     } catch(ConfigurationException e){//logical exception
       rfcconfig = handler.createRootConfiguration( RFCENGINE_CFG_LOCAL ) ;
     }
     Configuration  cfg = null;
     if (rfcconfig.existsSubConfiguration(nodeId)) 
     {
     	cfg = rfcconfig.getSubConfiguration(nodeId) ;
     	if (cfg.existsSubConfiguration(config.getProgramId()))
     		cfg.deleteConfiguration(config.getProgramId());
     }
     else
        cfg = rfcconfig.createSubConfiguration(nodeId) ;
     
     Configuration bundleConf = cfg.createSubConfiguration(config.getProgramId(), Configuration.CONFIG_TYPE_PROPERTYSHEET);
     PropertySheet propssheet = bundleConf.getPropertySheetInterface() ;
     writeInPropertySheet(propssheet, config);
     
     handler.commit();
     handler.closeConfiguration(rfcconfig);
   }

  private void  setGlobalConfiguration(ConfigurationHandler handler, BundleConfiguration config) throws ConfigurationException {
    Configuration rfcconfig = null;
    try {
      rfcconfig = handler.openConfiguration(RFCENGINE_CFG_GLOBAL,ConfigurationHandler.WRITE_ACCESS) ;
    } catch(ConfigurationException e){//logical exception
      rfcconfig = handler.createRootConfiguration( RFCENGINE_CFG_GLOBAL ) ;
    }
    if (rfcconfig.existsSubConfiguration(config.getProgramId()))
    	rfcconfig.deleteConfiguration(config.getProgramId());
    Configuration cfg = rfcconfig.createSubConfiguration(config.getProgramId(), Configuration.CONFIG_TYPE_PROPERTYSHEET) ;
    PropertySheet prsheet = cfg.getPropertySheetInterface() ;
    writeInPropertySheet(prsheet, config);

    handler.commit();
    handler.closeConfiguration(rfcconfig);
  }

  private void deleteConfFromLocal(ConfigurationHandler handler, BundleConfiguration config) throws ConfigurationException {

    Configuration rfcconfig = null;
    try{
      rfcconfig = handler.openConfiguration(RFCENGINE_CFG_LOCAL,ConfigurationHandler.WRITE_ACCESS) ;
    } catch(ConfigurationException e){//logical exception
      rfcconfig = handler.createRootConfiguration( RFCENGINE_CFG_LOCAL ) ;
    }

    Configuration clusterConf = rfcconfig.getSubConfiguration(nodeId);
    if (clusterConf != null) {
        if (clusterConf.existsSubConfiguration(config.getProgramId())) {
        clusterConf.deleteConfiguration(config.getProgramId());
        }
    }
    handler.commit();
  }

  private void deleteConfFromGlobal(ConfigurationHandler handler, BundleConfiguration config) throws ConfigurationException {
    Configuration rfcconfig = null;
    try{
      rfcconfig = handler.openConfiguration(RFCENGINE_CFG_GLOBAL,ConfigurationHandler.WRITE_ACCESS) ;
    }catch(ConfigurationException e){//logical exception
      rfcconfig = handler.createRootConfiguration( RFCENGINE_CFG_GLOBAL ) ;
    }

    if (rfcconfig.existsSubConfiguration(config.getProgramId())) {
        rfcconfig.deleteConfiguration(config.getProgramId());
    }
    handler.commit();
  }

  private void changeRunningStateInConfig(BundleConfiguration config) {

    method = "RFCRuntimeInterfaceImpl.changeRunningStateInConfig(BundleConfiguration config)";

    Configuration rfcconfig = null;
    ConfigurationHandler handler = null;
    try {
      handler =  cfgfactory.getConfigurationHandler();
      if (config.isLocal()) {
        try {
          rfcconfig = handler.openConfiguration(RFCENGINE_CFG_LOCAL,ConfigurationHandler.WRITE_ACCESS) ;
        } catch(ConfigurationException e){//logical exception
          rfcconfig = handler.createRootConfiguration( RFCENGINE_CFG_LOCAL) ;
        }
        Configuration clusterConf =  rfcconfig.getSubConfiguration(nodeId);
        if(clusterConf != null) {
          Configuration programId = clusterConf.getSubConfiguration(config.getProgramId());
          if (programId != null) {
                        PropertySheet propssheet = programId.getPropertySheetInterface() ;
                        propssheet.getPropertyEntry(BundleConfiguration.RUNNING_STATE).setValue((new Boolean(config.isRunning())).toString()) ;
                    }
        }
      } else {
        try {
          rfcconfig = handler.openConfiguration(RFCENGINE_CFG_GLOBAL,ConfigurationHandler.WRITE_ACCESS) ;
        } catch(ConfigurationException e){//logical exception
          rfcconfig = handler.createRootConfiguration( RFCENGINE_CFG_GLOBAL) ;
        }
        Configuration programId =  rfcconfig.getSubConfiguration(config.getProgramId());
        if (programId != null) {
          PropertySheet propssheet = programId.getPropertySheetInterface() ;
          propssheet.getPropertyEntry(BundleConfiguration.RUNNING_STATE).setValue((new Boolean(config.isRunning())).toString()) ;
        }
      }
      handler.commit();
	  //handler.closeAllConfigurations();
      //handler.closeConfiguration(rfcconfig);
      } 
      catch (ConfigurationException e) 
      {
        //RFCApplicationFrame.logError(e.toString());
          LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
      }
	  finally
	  {
		   try
		   {
			   handler.closeAllConfigurations();
		   }catch(ConfigurationException cfg){
			   RFCApplicationFrame.traceError("Failed closing configuration");
	  	   }
	  }
  }

  public void registerFunction(String functionName, String ejbName) {
    functionTable.put(functionName, ejbName);
  }

  public void unregisterFunction(String functionName) {
      functionTable.remove(functionName);
  }

  public  Hashtable getFunctionNamesTable() {
      return functionTable;
  }

  void addListeners(){
    if (m_listener != null) return;
    m_listener = new JCOListener();
    JCO.addServerErrorListener(m_listener);
    JCO.addServerExceptionListener(m_listener);
    JCO.addServerStateChangedListener(m_listener);
    //JCO.addTraceListener(m_listener);
  }

  void removeListeners(boolean check){
    if (m_listener == null) return;
    if (check)
    {
      Enumeration<Bundle> enumer = bundles.elements();
      while (enumer.hasMoreElements())
      {
        Bundle b = enumer.nextElement();
        if (b.getConfiguration().isRunning())
          return; // if any server runs, don't remove listeners
      }
    }

    JCO.removeServerErrorListener(m_listener);
    JCO.removeServerExceptionListener(m_listener);
    JCO.removeServerStateChangedListener(m_listener);
    //JCO.removeTraceListener(m_listener);
    m_listener = null;
  }

  private void setStaticProperties(BundleConfiguration config)
  {
    String programId = config.getProgramId();
  	
  	if (config.getJcoTraceLevel() != JCO.getTraceLevel())
    {
  	  int newLevel = config.getJcoTraceLevel();
  	  if (newLevel > 0)
      {
      	// set trace to 0 to close file writer
  	  	if (JCO.getTraceLevel() != newLevel) JCO.setTraceLevel(0);
  	  	resetTracePath();
      	JCO.setTraceLevel(newLevel);
        if (RFCApplicationFrame.isLogged(Severity.INFO))
            RFCApplicationFrame.logInfo(method, "JCO Trace activated", null);
      }
      else
      {
        JCO.setTraceLevel(0);
        if (RFCApplicationFrame.isLogged(Severity.INFO))
            RFCApplicationFrame.logInfo(method, "JCO Trace deactivated", null);
      }
    }
    
    // set jrfc.trace property only if it is different from the existent
  	boolean on = config.getGlobalJrfcTrace();
  	String trace = JCO.getMiddlewareProperty("jrfc.trace");
    if (trace != null)
    {
    	if (trace.equals("1") && (!on))
    		JCO.setMiddlewareProperty("jrfc.trace","0");
    	else if (trace.equals("0") && on)
    	{
    		resetTracePath();
    		JCO.setMiddlewareProperty("jrfc.trace","1");
            if (RFCApplicationFrame.isLogged(Severity.INFO))
                RFCApplicationFrame.logInfo(method, "Global JRFC Trace activated", null);
    	}
    }
    else if (on) 
    {
    	resetTracePath();
    	JCO.setMiddlewareProperty("jrfc.trace","1");
    	RFCApplicationFrame.logInfo(method, "Global JRFC Trace activated", null);
    }
    
    // set cpic.trace property only if it is different from the existent
    int cpicLevel = config.getCpicTraceLevel();
    trace = JCO.getMiddlewareProperty("cpic.trace");
    if (trace != null)
    {
    	try {
    		int level = Integer.parseInt(trace);
    		if (level != cpicLevel)
    		{
    			JCO.setMiddlewareProperty("cpic.trace",String.valueOf(cpicLevel));
                if (RFCApplicationFrame.isLogged(Severity.INFO))
                    RFCApplicationFrame.logInfo(method, "CPIC Trace changed to "+cpicLevel+"from "+level, null);
    		}
		} catch (Exception e) {
			// $JL-EXC$ , works as designed
			RFCApplicationFrame.logError(method, "Illegal cpic.trace property "+trace+
            		" detected when setting the configuration for JCo RFC Provider bundle "+programId,  null);
		}
    	
    }
    else if (cpicLevel != 0) 
    {
    	JCO.setMiddlewareProperty("cpic.trace",String.valueOf(cpicLevel));
        if (RFCApplicationFrame.isLogged(Severity.INFO))
            RFCApplicationFrame.logInfo(method, "CPIC Trace set to "+cpicLevel, null);
    }
    
//    RFCApplicationFrame.logInfo(method, "trace level = "+JCO.getTraceLevel(), null);
//    RFCApplicationFrame.logInfo(method, "before avaluating jarm config.getJarm()="+config.getJarm(), null);
    if (!RFCJCOServer.useJarm && config.getJarm())
    {
      JCO.setProperty("jco.jarm","1");
      RFCJCOServer.useJarm = true;
      if (RFCApplicationFrame.isLogged(Severity.INFO))
          RFCApplicationFrame.logInfo(method, "Jarm activated ", null);
    }
    else if (RFCJCOServer.useJarm && !config.getJarm())
    {
      JCO.setProperty("jco.jarm","0");
      RFCJCOServer.useJarm = false;
      if (RFCApplicationFrame.isLogged(Severity.INFO))
          RFCApplicationFrame.logInfo(method, "Jarm deactivated ", null);
    }
  }
  
  static void resetTracePath()
  {
  	String path = "."+File.separator;
  	if (!(path.equals(JCO.getProperty("jco.trace_path"))))
  	      	JCO.setTracePath(path);
  }
  
  
  /**
   * This method is only called at add or change action
   */
  protected void parseProperties(Properties props, BundleConfiguration config) throws RemoteException
  {
    try
    {
    	if (props.containsKey(BundleConfiguration.PROPERTIES[3]))
        {
            try 
            {
                    int prc = Integer.parseInt(props.getProperty(BundleConfiguration.PROPERTIES[3]));
                    if (prc < 0) throw new RemoteException(BundleConfiguration.PROPERTIES[3]+
                                " must be a positive number or 0");
                    config.setProcessesNumber(prc);
            } catch (Exception e) {
                throw new RemoteException("Illegal value for property "
                        +BundleConfiguration.PROPERTIES[3]);
            }
        }
        
        if(props.containsKey(BundleConfiguration.PROPERTIES[1]))
        {
            config.setGatewayHost(props.getProperty(BundleConfiguration.PROPERTIES[1]));
        }
        if (props.containsKey(BundleConfiguration.PROPERTIES[2]))
        {
            config.setGatewayServer(props.getProperty(BundleConfiguration.PROPERTIES[2]));
        }
        
        if (props.containsKey(BundleConfiguration.PROPERTIES[9]))
                if ("true".equals(props.getProperty(BundleConfiguration.PROPERTIES[9]))) config.setRunningState(true);
                else config.setRunningState(false);
        
        if (props.containsKey(BundleConfiguration.PROPERTIES[10]))
            if ("true".equals(props.getProperty(BundleConfiguration.PROPERTIES[10]))) config.setLocal(true);
            else config.setLocal(false);
        
        if (props.containsKey(BundleConfiguration.PROPERTIES[11]))
            if ("true".equals(props.getProperty(BundleConfiguration.PROPERTIES[11]))) config.setRfcTrace(true);
            else config.setRfcTrace(false);
        
        if (props.containsKey(BundleConfiguration.PROPERTIES[18]))
        {
            if ("".equals(props.getProperty(BundleConfiguration.PROPERTIES[18])))
                throw new RemoteException("Please set correct value for property "+
                        BundleConfiguration.PROPERTIES[18]);
                
            config.setRepositoryDestination(props.getProperty(BundleConfiguration.PROPERTIES[18]));
            config.useRepDest = true;
            
            if (config.getGatewayHost() == null || 
                    "".equals(config.getGatewayHost()))
                throw new RemoteException("Please configure property "+
                        BundleConfiguration.PROPERTIES[1]);
                
            else if (config.getGatewayService() == null || 
                    "".equals(config.getGatewayService()))
                throw new RemoteException("Please configure property "+
                        BundleConfiguration.PROPERTIES[2]);
        }
        else 
        {
            config.useRepDest = false;
            if (props.containsKey(BundleConfiguration.PROPERTIES[4]))
                    config.setApplicationServerHost(props.getProperty(BundleConfiguration.PROPERTIES[4]));
            
            if (props.containsKey(BundleConfiguration.PROPERTIES[5]))
                    config.setSystemNumber(props.getProperty(BundleConfiguration.PROPERTIES[5]));
            
            if (props.containsKey(BundleConfiguration.PROPERTIES[6]))
                    config.setLogonClient(props.getProperty(BundleConfiguration.PROPERTIES[6]));
            
            if (props.containsKey(BundleConfiguration.PROPERTIES[7]))
                    config.setLogonLanguage(props.getProperty(BundleConfiguration.PROPERTIES[7]));
            
            if (props.containsKey(BundleConfiguration.PROPERTIES[8]))
                    config.setLogonUser(props.getProperty(BundleConfiguration.PROPERTIES[8]));
            
            if (props.containsKey(BundleConfiguration.PROPERTIES[17])
            		// don't save stars
            		&& (!BundleConfiguration.PASSWORD_STARS.equals(props.getProperty(BundleConfiguration.PROPERTIES[17]))))
                config.setLogonPassword(props.getProperty(BundleConfiguration.PROPERTIES[17]));
            
            
            
            if (config.getApplicationServerHost() == null || 
                    "".equals(config.getApplicationServerHost()))
            
                throw new RemoteException("Please configure property "+ 
                        BundleConfiguration.PROPERTIES[4]);
            
            if (config.getSystemNumber() == null || 
                    "".equals(config.getSystemNumber()))
                throw new RemoteException("Please configure property "+
                        BundleConfiguration.PROPERTIES[5]);     
            
            if (config.getLogonClient() == null || 
                    "".equals(config.getLogonClient()))
            {
                throw new RemoteException("Please configure property "+
                        BundleConfiguration.PROPERTIES[6]);
            }
            
            if (config.getLogonUser() == null || 
                    "".equals(config.getLogonUser()))
            {
                throw new RemoteException("Please configure property "+
                        BundleConfiguration.PROPERTIES[8]);
            }
            
            if (config.getLogonPassword() == null || 
                    "".equals(config.getLogonPassword()))
            {
                throw new RemoteException("Please configure property "+
                        BundleConfiguration.PROPERTIES[17]);
            }
            
            // try to fill missing configurations
            if (config.getGatewayHost() == null || 
                    "".equals(config.getGatewayHost()))
                config.setGatewayHost(config.getApplicationServerHost());
            
            if (config.getGatewayService() == null || 
                    "".equals(config.getGatewayService()))
                config.setGatewayServer("sapgw"+config.getSystemNumber());
            
            if (config.getLogonLanguage() == null || 
                    "".equals(config.getLogonLanguage()))
            {
                config.setLogonLanguage("EN");
            }
        } // App. Server logon
        
        // SNC stuff
        if (props.containsKey(BundleConfiguration.PROPERTIES[12])
                && "true".equals(props.getProperty(BundleConfiguration.PROPERTIES[12])))
        {
            config.setUseSnc(true);
            if (props.containsKey(BundleConfiguration.PROPERTIES[13]))
                    config.setSncName(props.getProperty(BundleConfiguration.PROPERTIES[13]));
            if (props.containsKey(BundleConfiguration.PROPERTIES[14]))
                try {
                    int qop = Integer.parseInt(props.getProperty(BundleConfiguration.PROPERTIES[14]));
                    if (qop < 1 || qop > 9) throw new RemoteException(BundleConfiguration.PROPERTIES[14]+
                                " must be a number between 1 and 9");
                    config.setSncQop(qop);
            } catch (Exception e) {
                throw new RemoteException("Illegal value of property "
                        +BundleConfiguration.PROPERTIES[14]);
            }
            if (props.containsKey(BundleConfiguration.PROPERTIES[15]))
                    config.setSncLib(props.getProperty(BundleConfiguration.PROPERTIES[15]));
            
            // this is a required property, so check it
            if (props.containsKey(BundleConfiguration.PROPERTIES[16]))
            {
            	String authPart = props.getProperty(BundleConfiguration.PROPERTIES[16]);
            	if ((authPart == null || 
                        "".equals(authPart)))
                {
                    throw new RemoteException("Please configure property "+
                            BundleConfiguration.PROPERTIES[16]);
                }
                else
                    config.setAuthPartner(authPart);
            }
            else
            	throw new RemoteException("Please define the required property "+  BundleConfiguration.PROPERTIES[16]);                
        }
        else config.setUseSnc(false);
    }
    catch (RemoteException e)
    {
        RFCApplicationFrame.logError("RFCRuntimeInterfaceImpl.parseProperties()", e.getMessage(), null);
        throw e;
    }
  }
    
    static protected Set getPropsSet()
    {
    	PROPS_SET = new HashSet();
    	for (int i = 0; i<BundleConfiguration.PROPERTIES.length;i++) PROPS_SET.add(BundleConfiguration.PROPERTIES[i]);
    	return PROPS_SET;
    }
    
    static class RFCEngineThreadStarter implements Runnable
    {
        
        private Bundle m_bundle;
        private RFCRuntimeInterfaceImpl m_runtime;
        private static String method = "RFCEngineThreadStarter.run()";
        private String programId;
        
        RFCEngineThreadStarter (RFCRuntimeInterfaceImpl runtime, Bundle bundle)
        {
            m_bundle = bundle;
            m_runtime = runtime;
            programId = m_bundle.getConfiguration().getProgramId();
        }
        
        public void run()
        {
            try
            {
                m_bundle.startAll();
                if (RFCApplicationFrame.isLogged(Severity.INFO))
                   RFCApplicationFrame.logInfo(method, "JCo RFC Provider bundle "+m_bundle.getConfiguration().getProgramId()+" started on Engine start ", null);
                
                // remove bundle from automatic restart list
                if (!m_runtime.bundlesFailedOnStart.isEmpty())
                {
                	
                	if (m_runtime.bundlesFailedOnStart.containsKey(programId))
                		m_runtime.bundlesFailedOnStart.remove(programId);
                }
                
            } catch (Exception e)
            {
                // this happens on service start. So we consume all exceptions and trace them
            	if (RFCApplicationFrame.isLogged(Severity.INFO))
            	{
            		RFCApplicationFrame.logInfo(method, "JCo RFC Provider bundle "+m_bundle.getConfiguration().getProgramId()+"couldn't start RfcEngine threads with RFCEngineThreadStarter ", null);
                	LoggingHelper.traceThrowable(Severity.INFO, RFCResourceAccessor.location, method, e);
            	}
                m_runtime.bundlesFailedOnStart.put(programId,m_bundle);
                
            }
            
                
        }
    }
}
