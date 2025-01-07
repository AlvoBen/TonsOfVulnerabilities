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
import java.util.*;

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import javax.naming.*;

import com.sap.tc.logging.Severity;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.security.core.server.destinations.api.DestinationException;
import com.sap.security.core.server.destinations.api.DestinationService;
import com.sap.security.core.server.destinations.api.RFCDestination;


/**
 * Stores a Bundle - a set of servers
 * @author  d035676
 * @version 1.0
 */
public class Bundle  {

  /**
   * Stores the paradigms with the same programId
   */
  private RFCJCOServer [] servers = null;
  

  private BundleConfiguration config = null;

  /**
   * Stores the JCO repository
   */
  private IRepository repository = null;

  String bundleID = null;
  private String poolID = null;

  private RFCRuntimeInterfaceImpl runtimeInterface = null;
  private String method = null;
  boolean flag = true; // condition. Update by Exception Listener
  
  /**
   * is true, if an error occurs on automatic restart
   * It means the server tries to restart
   */
  boolean onStartupError = false;
  
  /**
   *  running state of the server. True means that the server was started over startAll
   *  false means tht server was stopped over stopAll  
   */
  boolean isRunning = false;

  /**
   * Creates the bundle
   *
   * @param  conf   Bundle configuration settings
   * @param  runtime   RFCRuntimeInterface to control Bundles
   */
  public void initBundle(BundleConfiguration conf, RFCRuntimeInterface runtime)
  {
    method = "Bundle.initBundle()";
    config = conf;
    runtimeInterface = (RFCRuntimeInterfaceImpl)runtime;
    
    if (RFCApplicationFrame.isLogged(Severity.INFO))
        RFCApplicationFrame.logInfo(method, "Initialize JCo RFC Provider bundle "+conf.getProgramId() +
        		" on node="+runtimeInterface.nodeId, null);

    
    int max = runtimeInterface.getMaxProcesses();
    int num = config.getProcessesNumber();

    bundleID = config.getProgramId();

    // if properties were changed and then we restarted the service with new props
    if (num > max) {
      conf.setProcessesNumber(max);
      num = max;
    }
    int maxCon = runtimeInterface.getMaxConnections();
    if (config.getMaxConnections() > maxCon) {
        conf.setMaxConnections(maxCon);
      }

    // Create servers
    servers = new RFCJCOServer [num];
  }

  private Properties getRepositoryProperties()
  {
    Properties p = new Properties();
    p.setProperty("jco.client.ashost",config.getApplicationServerHost());
    p.setProperty("jco.client.sysnr",config.getSystemNumber());
    p.setProperty("jco.client.client",config.getLogonClient());
    p.setProperty("jco.client.user",config.getLogonUser());
    p.setProperty("jco.client.passwd",config.getLogonPassword());
    if (config.getLogonLanguage() != null && !"".equals(config.getLogonLanguage()))
      p.setProperty("jco.client.lang",config.getLogonLanguage());

    return p;
  }

  Properties getServerProperties()
  {
    Properties p = new Properties();

    p.setProperty("jco.server.gwhost",config.getGatewayHost());
    p.setProperty("jco.server.gwserv",config.getGatewayService());
    p.setProperty("jco.server.progid",config.getProgramId());

    if (config.getUseSnc())
    {
      p.setProperty("jco.server.snc","1");
      p.setProperty("jco.server.snc_qop", String.valueOf(config.getSncQop()));
      p.setProperty("jco.server.snc_lib",config.getSncLib());
      p.setProperty("jco.server.snc_myname", config.getSncName());

    }
    return p;
  }

  /**
   * Connection with SAP System
   */
  synchronized void initializePool()  throws RemoteException
  {
    method = "Bundle.initialize()";

    if (repository != null && RFCApplicationFrame.isLogged(Severity.INFO)) 
    {
        RFCApplicationFrame.logInfo(method, "Repository already exists for JCo RFC Provider bundle "+bundleID,null);
        //throw new RemoteException("repository exists already for "+bundleID);
    }
    
    if (BundleConfiguration.OWN_REPOSITORY.equals(config.getRepositoryDestination()))
    {
        if (RFCApplicationFrame.local == null) // could happen, if rfcengine wasn't started propertly
        {
            throw new RemoteException("JCo RFC Provider bundle "+bundleID
                    +" is using own repository but RFCRepositoryInterface is not initialized ");
        }
        repository = RFCApplicationFrame.local.getRepository(bundleID);
        if (repository == null)
        {
            throw new RemoteException("No valid own repository found for JCo RFC Provider bundle "+bundleID);
        }
        if (RFCApplicationFrame.isLogged(Severity.INFO))
            RFCApplicationFrame.logInfo(method, " JCo RFC Provider bundle "+bundleID+" uses static repository ", null);
        
    }
    
    else 
    {
          String destName = null;

          Properties p;
          if (config.useRepDest)
          {
              try
              {
                  Context ctx = new InitialContext();
                  DestinationService dstService = (DestinationService)
                      ctx.lookup(DestinationService.JNDI_KEY);
                  if (dstService == null)
                  {
                      throw new RemoteException("Destination Service where rfc destinations are stored is not available");
                  }
                  destName = config.getRepositoryDestination();
                  RFCDestination dst =
                      (RFCDestination) dstService.getDestination("RFC", destName);

                  p = dst.getJCoProperties();
              }
              catch (Exception de)
              {
            	  if (RFCApplicationFrame.isLogged(Severity.INFO))
            	  {
            		  RFCApplicationFrame.logError(method, "Couldn't get destination "+ destName +
                          " for RFCEngine Bundle "+bundleID, null);
                  	LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, de);
            	  }
                  throw new RemoteException("Couldn't get destination "+ destName +
                          " for JCo RFC Provider bundle "+bundleID,de);
              }
          }
          else
          {
              p = getRepositoryProperties();
          }
          
          JCO.Client client = JCO.createClient(p);
          poolID = config.getProgramId()+client.getHashKey(false);   
          JCO.Pool pool = JCO.getClientPoolManager().getPool(poolID);
          
          if (pool != null)
          {
        	  try 
        	  {
        		  if (RFCApplicationFrame.isLogged(Severity.INFO))
        			  RFCApplicationFrame.logInfo(method, "JCo client pool "+poolID+" already exists", null);
        		  JCO.removeClientPool(poolID);
        	
          	  } catch (Exception e) 
          	  {
      		  	if (RFCApplicationFrame.isLogged(Severity.INFO))
          		{
          		  RFCApplicationFrame.logError(method, "Failed to remove JCo client pool for JCo RFC Provider bundle "+bundleID, null);
          		  LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
          		  // it is an internal exception, that we don't pass to gui
          		  //throw new RemoteException("Failed to remove client pool for Bundle "+bundleID,e);
          		}
          	  }
          }
          try
          {
        	  JCO.addClientPool(poolID, config.getMaxConnections(), p);      
        	  client = JCO.getClient(poolID);
                // on getClient a ping() is called
                if (client == null) 
                {
                	if (RFCApplicationFrame.isLogged(Severity.INFO))
                		RFCApplicationFrame.logError(method, "Repository contains invalid client for JCo RFC Provider bundle "+bundleID, new Object[] {bundleID});
                	throw new RemoteException("JCo client pool contains invalid client for JCo RFC Provider bundle "+bundleID);
                }
          }//try
          catch (JCO.Exception e)
          {
                //RFCApplicationFrame.logError(method, "Failed using Repository of RFCEngine Bundle "+bundleID, new Object[] {bundleID});
                //LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
                throw new RemoteException("Failed creating JCo client pool for JCo RFC Provider bundle "+bundleID,e);

          }
          finally
          {
                if (client != null) JCO.releaseClient(client);
          }
          
          
          repository = JCO.createRepository(poolID, poolID);
          
          if (repository == null)
          {
        	  if (RFCApplicationFrame.isLogged(Severity.INFO))
        		  RFCApplicationFrame.logError("Jco repository could not be created for JCo RFC Provider bundle "+bundleID);
        	  throw new RemoteException("Jco repository could not be created for JCo RFC Provider bundle "+bundleID);
          }
      }//else
      if (RFCApplicationFrame.isLogged(Severity.INFO))
    	  RFCApplicationFrame.logInfo(method, "Initialized repository for JCo RFC Provider bundle "+bundleID +
        		" on node="+runtimeInterface.nodeId, new Object[] {bundleID});
  }

  /**
   * Starts all Servers of Actual bundle
   */
  public synchronized void startAll() throws RemoteException {
    method = "Bundle.startAll()";
    
    // If servers were unsuccessful started on initial start (so are now in
    // initial restart routine) and then they are
    // additionally started manually from GUI, we interrupt restart routine first
    // and try to start servers in this thread. So, we provide correct info in 
    // real time and avoid that servers are started twice.
    // Also, the gui may show old status (if not refreshed) or it is really stopped
    // status. We handle these cases in same manner and stop servers first, to
    // make sure that they are not started twice.
	if (isRunning)
	{
		if (!onStartupError) // not in JCo startup routine 
			return; // it was already started and is running, so nothing to do
			
		for (int i = 0; i < servers.length; i++)
        {
			try {
	    		if (servers[i]!=null)
	    		{
	    			((RFCJCOServer)servers[i]).stop();
	    		}
	        
			} catch (Exception ex) {
				//$JL-EXC$
			}
        }
		isRunning = false; // since we stopped servers
		config.setRunningState(false);
	}

    Properties p = getServerProperties();
    try
    {
    	JCO.getNumServerConnections(p);
    }
    catch (JCO.Exception e)
    {
    	RFCApplicationFrame.logError(method, "Failed to start JCo RFC Provider bundle "
        		+ " on node="+runtimeInterface.nodeId, null);
        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
    	throw new RemoteException(e.getMessage(),e);
    }
    
    try
    {
    	initializePool();
    }
    catch (RemoteException e)
    {
    	RFCApplicationFrame.logError(method, "Failed to create a repository for JCo RFC Provider bundle "+bundleID, null);
        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
        throw e;
    }
    catch (Throwable e)
    {
    	RFCApplicationFrame.logError(method, "Failed to create a repository for JCo RFC Provider bundle "+bundleID, null);
        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
        throw new RemoteException("Failed to create a repository for JCo RFC Provider bundle "+bundleID,e);
    }
    
    try
    {
    	// create and start new Server instances
    	for (int i = 0; i < servers.length; i++)
    	{
	      servers[i] = new RFCJCOServer(p, repository, runtimeInterface, this);
	      if (config.getUseSnc()) servers[i].setAuthorizationPartner(config.getAuthPartner());
	      if (config.getRfcTrace()) servers[i].setTrace(true);
	      servers[i].start();
      
    	}//for
    	config.setRunningState(true);
    	isRunning = true;
    	if (RFCApplicationFrame.isLogged(Severity.INFO))
	      RFCApplicationFrame.logInfo(method, "JCo RFC Provider bundle "+bundleID+" started "+servers.length+" servers" +
	  		" on node="+runtimeInterface.nodeId, null);
  
    }//try
    
    catch (Throwable e)
    {
        RFCApplicationFrame.logError(method, "Failed to start JCo RFC Provider bundle "
        		+ " on node="+runtimeInterface.nodeId, null);
        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
        
        for (int i = 0; i < servers.length; i++)
        {
			try {
	    		if (servers[i]!=null)
	    			((RFCJCOServer)servers[i]).stop();
	        
			} catch (Exception ex) {
				//$JL-EXC$
			}
        }
        repository = null;
        config.setRunningState(false);
        try {
    		if (poolID != null) 
    			JCO.removeClientPool(poolID);
    	} catch (Exception ex) {
    		//$JL-EXC$
    	  RFCApplicationFrame.logError(method, "Failed to remove client pool for JCo RFC Provider bundle "+bundleID +
          		" on node="+runtimeInterface.nodeId, null);
    	  LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, ex);
    	}
        
        throw new RemoteException("Failed to start JCo RFC Provider bundle "+bundleID+": cause: "+e.getMessage(),e);
    }
  }

  /**
   *  Stops all Servers
   */
  public synchronized void stopAll() throws RemoteException {
    method = "Bundle.stopAll()";
    repository = null;
    try {
      for (int i = 0; i < servers.length; i++) {
        if (servers[i]!=null) servers[i].stop();
      }
      config.setRunningState(false);
      onStartupError = false;
      isRunning = false;
      if (RFCApplicationFrame.isLogged(Severity.INFO))
          RFCApplicationFrame.logInfo(method, "JCo RFC Provider bundle "+bundleID+" stopped "+servers.length+" servers" +
          		" on node="+runtimeInterface.nodeId, null);
    } catch (Throwable e) {
      RFCApplicationFrame.logError(method, "Failed to stop JCo RFC Provider bundle "
    		  +bundleID+" on node="+this.runtimeInterface.nodeId, null);
      LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
      throw new RemoteException("Failed to stop JCo RFC Provider bundle "+bundleID,e);
    }
    finally
    {
    	try {
    		if (poolID != null) 
    			JCO.removeClientPool(poolID);
    	} catch (Exception e) {
    	  RFCApplicationFrame.logError(method, "Failed to remove JCo client pool for JCo RFC Provider bundle "
    			  +bundleID+" on node="+runtimeInterface.nodeId, null);
    	  LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
    	  // it is an internal exception, that we don't pass to gui
    	}
    }	
  }

  /**
   * Returns the number of paradigms in the current bundle
   *
   * @return   Number of processes in the bundle
   */
  public int getProcessesNumber() {
    return servers.length;
  }

  /**
   * Returns the bundle's configuration settings
   *
   * @return   The bundle's settings
   */
  public BundleConfiguration getConfiguration() {
    return this.config;
  }

  public synchronized void add(int addNumber) throws RemoteException {

    method = "Bundle.add()";
    int oldSize = servers.length;
    int newSize = oldSize + addNumber;

    RFCJCOServer [] newServers = new RFCJCOServer[newSize];
    for (int i=0;i<oldSize;i++) newServers[i] = servers[i];

    Properties p = ((RFCJCOServer)servers[0]).getProperties();
    try
    {
      // create and start new Server instances
      for (int i = oldSize; i < newSize; i++)
      {
        newServers[i] = new RFCJCOServer(p, repository, runtimeInterface, this);
        if (config.getUseSnc()) newServers[i].setAuthorizationPartner(config.getAuthPartner());
        if (config.getRfcTrace()) newServers[i].setTrace(true);
        newServers[i].start();
      }
    }
    catch (Throwable e)
    {
        RFCApplicationFrame.logError(method, "Failed to start JCo RFC Provider bundle "+bundleID, null);
        LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
        throw new RemoteException ("On increasing server number failed to start JCo RFC Provider bundle "+bundleID,e);
    }

    servers = newServers;
    if (RFCApplicationFrame.isLogged(Severity.INFO))
        RFCApplicationFrame.logInfo(method, "added "+addNumber+" servers "+bundleID, null);
  }

  /**
   * Removes a process from the bundle
   *
   * @param  number   number of the process to add to the bundle.
   * @return   TRUE if the process if removed successfully
   */
  public synchronized void remove(int number) throws RemoteException {
    method = "Bundle.remove()";

    int oldSize = servers.length;
    int newSize = oldSize - number;

    RFCJCOServer [] newServers = new RFCJCOServer[newSize];
    for (int i=0;i<newSize;i++) newServers[i] = servers[i];

    try {
      for (int i = newSize; i < oldSize; i++) {
        servers[i].stop();
      }
    } catch (Throwable e) {
      RFCApplicationFrame.logError(method, "Failed to stop JCo RFC Provider bundle "+bundleID, null);
      LoggingHelper.traceThrowable(Severity.ERROR, RFCResourceAccessor.location, method, e);
      throw new RemoteException ("On reducing server number failed to stop JCo RFC Provider bundle "+bundleID,e);
    }
    servers = newServers;

    if (RFCApplicationFrame.isLogged(Severity.INFO))
        RFCApplicationFrame.logInfo(method,  "removed "+number+" servers", new Object[] {bundleID});
  }
}

