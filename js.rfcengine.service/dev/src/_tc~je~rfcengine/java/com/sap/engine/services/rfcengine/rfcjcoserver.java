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

import java.util.Properties;

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sap.mw.jco.util.Jarm;
import com.sap.tc.logging.Severity;


/**
 * Class used to handle requests from SAP R/3
 *
 * @author  d035676
 * @version 1.0
 */
public class RFCJCOServer extends JCO.Server {
  //$JL-CLONE$
  
  //private static final int MaxStartupDelay = 3600; 
  
  /**
   * The authorization partner used for SNC
   */
  private String authParner;

  /**
   * Handler to use
   */
  private RFCRequestHandler handler = null;
  RFCRuntimeInterfaceImpl runtimeInterface = null;

  // variables needed only for Throughput handling
  private static JCO.Throughput throughput = null;
  private static final String RFCENGINE_THROUGHPUT = "rfcengine.throughput";
  private static JCOThroughputManagement tm = null;

  /* Monitors with Jarm */
  private Jarm.Monitor rfcengine_jarm_monitor;
  static boolean useJarm = false;
  protected String beanName = null;
  
  Exception anyerr = null ;
  Object scheduler = new Object() ;
  Bundle m_bundle;

  static
  {
    try
    {
        if (System.getProperty(RFCENGINE_THROUGHPUT) != null)
        {
          throughput=new JCO.Throughput();
          tm = new JCOThroughputManagement(throughput);
        }
    }//try
    catch (java.lang.Exception ex)
    {
        throw new RuntimeException("RFCENGINE: Environment property RFCENGINE_THROUGHPUT was set wrong");
    }//catch
  }

  /**
   * Creates JCO.Server using properties
   *
   * @param   prop   Properties for Server Connection"
   */
  public RFCJCOServer(Properties prop, IRepository repository, RFCRuntimeInterface runtime_interface, Bundle bundle) {
    super(prop, repository);
    runtimeInterface = (RFCRuntimeInterfaceImpl)runtime_interface;
    m_bundle = bundle;
    setProperty("jco.server.min_startup_delay","30");
    handler = new RFCDefaultRequestHandler(runtimeInterface, this);

    if (throughput != null)
    {
      this.setThroughput(throughput);
    }
    
  }
/*
  public void run()
  {
      //----------------------------------------------------------
      //  Update the state
      //----------------------------------------------------------
      setState(JCO.STATE_STARTED);
      isRunning = true;
	  int startup_delay = 0;
      if (RFCApplicationFrame.isLogged(Severity.INFO))
          RFCApplicationFrame.logInfo("RFCJCOServer.run()", " start JCO thread "+Thread.currentThread().toString(), null);
	
      //----------------------------------------------------------
      //  Loop until the thread is being interrupted or stopped.
      //----------------------------------------------------------
      while (isRunning) {

          //--------------------------------------------------
          // Wait for the specified time before trying to
          // restart again.
          //--------------------------------------------------
          if (startup_delay > 0) {
              synchronized(semaphore) {
                  try {
                	  semaphore.wait(startup_delay);
                  }
                  catch (InterruptedException ex) {
                  	// $JL-EXC$
                  }//try
              }//synchronized
          }//if
          // if was stopped in startAll don't listen anymore
          if (m_bundle.isRunning)
          {
	          listen();
	          synchronized(semaphore) {
		          if (onStartupError) 
		          {
		          	if (startup_delay == 0) startup_delay=1000;
		          	if (startup_delay < MaxStartupDelay)startup_delay *= 2;
		      	  } 
		          else startup_delay = 0;
	          }
          }
      }//while
	  setState(JCO.STATE_STOPPED);
      if (RFCApplicationFrame.isLogged(Severity.INFO))
          RFCApplicationFrame.logInfo("RFCJCOServer.run()", " stop JCO thread "+Thread.currentThread().toString(), null);
  }
*/
  /*
  public void stop()
  {
      //----------------------------------------------------------
      //  Update the state
      //----------------------------------------------------------
	  super.stop();
	  synchronized (semaphore) {
		  semaphore.notifyAll();
      }
  }
  */

  /**
   * Handles a request using the registered handler
   *
   * @param  function   Function to handle
   */
  public void handleRequest(JCO.Function function) throws Exception{

      String method = "RFCJCOServer.handleRequest()";
      String ticket = this.getAttributes().getSSOTicket();
      
      anyerr = null;
      
      J2EEApplicationRunnable r = new J2EEApplicationRunnable( function) ;

      synchronized( scheduler )
      {
          try{
              if (RFCApplicationFrame.isLogged(Severity.INFO)) {
                  RFCApplicationFrame.logInfo(method, "pass request from thread "+
                      Thread.currentThread()+" to runnable "+ r.toString(), null);
              }
              handler.setTicket(ticket);
              if (ticket != null)
                  ((RFCRuntimeInterfaceImpl)runtimeInterface).serviceContext.getCoreContext().getThreadSystem().startCleanThread( r , false, false);
              else
            	  ((RFCRuntimeInterfaceImpl)runtimeInterface).serviceContext.getCoreContext().getThreadSystem().startThread( r , false, false );
              
              scheduler.wait() ; //ms 
          }catch(InterruptedException ie){
              LoggingHelper.traceThrowable(Severity.DEBUG, RFCResourceAccessor.location, "RFCJCOServer.handleRequest(JCO.Function function)", ie);
            // continue 
          }//catch
      }//synchronized 
      if (anyerr != null)
          throw anyerr;
  }

  /**
   * Checks whether the call is authorized
   *
   * @param  function_name   Name of the ABAP function
   * @param  authorization_mode Authorization mode
   * @param  authorization_partner Authorization partner string
   * @param  authorization_key  Authorization partner represented in ASN.1 format
   */
  protected boolean checkAuthorization(String function_name, int authorization_mode,
        String authorization_partner, byte[] authorization_key)	{

      if (RFCApplicationFrame.isLogged(Severity.INFO))
      {
        RFCApplicationFrame.logInfo("checkAuthorization", "Function name:    ", new String [] {function_name});
        RFCApplicationFrame.logInfo("checkAuthorization", "Authorization Parner:   ", new String [] {authorization_partner});
        RFCApplicationFrame.logInfo("checkAuthorization", "Authorization Parner set by user:   ", new String [] {authParner});
      }
      if (authParner != null) {
        return authorization_partner.equals(authParner);
      }
      return false;
    }

  public void setAuthorizationPartner(String partner) {
    this.authParner = partner;
  }
  /**
   *  Notify about begin of call processing
   *  Function different then in JCO
   *  This method is called, as soon as RFC Engine know the Bean Name
   */
  protected void beginCall(String name)
  {
    if(useJarm) {
      rfcengine_jarm_monitor = Jarm.getRequestMonitor(null, name);
      if(rfcengine_jarm_monitor != null) {
        rfcengine_jarm_monitor.startComponent(name);
      }
    }
  } // beginCall

  /**
   * Overriden JCO Function, that's why super
   * This method is calld, when JCO knows the data length
   */
  protected void endCall() {
    if(useJarm && rfcengine_jarm_monitor != null) {
      JCO.Attributes attributes = getAttributes();
      rfcengine_jarm_monitor.setUser(attributes != null ? attributes.getUser() : (String) null);
      rfcengine_jarm_monitor.endComponent();
      rfcengine_jarm_monitor.endRequest();
    }
    super.endCall();
  } // endCall
  
  class  J2EEApplicationRunnable implements Runnable
  {
      JCO.Function function ;     
      
      J2EEApplicationRunnable(JCO.Function function0)
      {
          function = function0 ;
      }  
    
      public void  run()
      {
          try
          {
              if (RFCApplicationFrame.isLogged(Severity.INFO))
              {
                  RFCApplicationFrame.logInfo("J2EEApplicationRunnable.run()"," J2EEApplicationRunnable thread "+Thread.currentThread().toString()
                          + " started and processes now function "+function.getName(), null);
                  
              }
              if (throughput != null && function.getName().equals("JTEST_GET_THROUGHPUT"))      tm.handleGetThroughput(function);
              else                                                                              handler.handleRequest(function);        
          }
          catch(Exception e)
          {
              anyerr = e;
          }
          finally
          {
              synchronized( scheduler )
              { 
                   scheduler.notify() ;
              }
              if (RFCApplicationFrame.isLogged(Severity.INFO))
              {
                  RFCApplicationFrame.logInfo("J2EEApplicationRunnable.run()", " leave J2EEApplicationRunnable thread "+Thread.currentThread().toString()
                          + " and processing function "+function.getName(), null);
              }
          }
      }//run
  }// class J2EEApplicationRunnable
}

