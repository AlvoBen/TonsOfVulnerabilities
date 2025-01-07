/*
 * Copyright (c) 2000-2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server;

import static com.sap.engine.services.httpserver.server.Log.CATEGORY_HTTP;
import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP;
import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP_REQUEST;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

import com.sap.bc.proj.jstartup.fca.FCAConnection;
import com.sap.bc.proj.jstartup.fca.FCAException;
import com.sap.bc.proj.jstartup.fca.FCAProperties;
import com.sap.bc.proj.jstartup.fca.FCAServer;
import com.sap.bc.proj.jstartup.sadm.ShmWebSession;
import com.sap.engine.boot.SystemProperties;
import com.sap.engine.frame.client.ClientException;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.lib.lang.ObjectPool;
import com.sap.engine.lib.util.HashMapObjectLong;
import com.sap.engine.services.httpserver.chain.ChainComposer;
import com.sap.engine.services.httpserver.chain.impl.HTTPRequestImpl;
import com.sap.engine.services.httpserver.chain.impl.HTTPResponseImpl;
import com.sap.engine.services.httpserver.chain.impl.HostChainImpl;
import com.sap.engine.services.httpserver.interfaces.ErrorData;
import com.sap.engine.services.httpserver.interfaces.SupportabilityData;
import com.sap.engine.services.httpserver.lib.ResponseCodes;
import com.sap.engine.services.httpserver.lib.Responses;
import com.sap.engine.services.httpserver.lib.util.MaskUtils;
import com.sap.engine.services.httpserver.server.preservation.ReleaseManager;
import com.sap.engine.services.httpserver.server.rcm.RequestProcessorThreadFactory;
import com.sap.engine.services.httpserver.server.rcm.RequestProcessorThread;
import com.sap.engine.services.httpserver.server.rcm.ThrResourceManager;
import com.sap.engine.system.ThreadWrapper;
import com.sap.tc.logging.Severity;


public class Processor implements RequestProcessorThreadFactory {
  private ObjectPool clientsPool = null;
  private HttpMonitoring monitoring = null;

  /**
   * Holds the server side queue of the FCA
   */
  private FCAServer fcaServer = null;

  /**
   * Flag that processor is stopping
   */
  private boolean stopping = false;

  /**
   * Counter that help in FCAServer recreation
   */
  private int waitingFCAThreads = 0;

  /**
   * Thread synchronization object for FCAServer recreation
   */
  private Object recreateFCAServerLock = new Object();

  /**
   *
   */
  private ChainComposer composer;

  /**
   * The number of pre-configured request processing (worker) threads
   */
  private int numberOfThreads = 10;

  /**
   * The number of currently used request processing (worker) threads
   */
  private AtomicInteger clientsRunning = new AtomicInteger(0);

  /**
   * The value of last used connectionId.
   */
  private AtomicInteger connectionId = new AtomicInteger(0);

  ThrResourceManager resourceManager;
  ReleaseManager releaseManager;

  public Processor(HttpMonitoring httpMonitoring, ChainComposer composer) {
    this.monitoring = httpMonitoring;
    this.clientsPool = new ObjectPool(ServiceContext.getServiceContext()
      .getHttpProperties().getMinPoolSize(), ServiceContext.getServiceContext()
      .getHttpProperties().getMaxPoolSize(), ServiceContext.getServiceContext()
      .getHttpProperties().getDecreaseCapacityPoolSize(), Client.class);
    this.composer = composer;
    releaseManager = new ReleaseManager(clientsPool);

  }

  public void runServer() {
    numberOfThreads = ServiceContext.getServiceContext().getHttpProperties()
      .getFCAServerThreadCount();
    if (numberOfThreads <= 0) { return; }
    monitoring.setThreadsInPool(numberOfThreads);
    try {
      FCAProperties.init(4);
      fcaServer = new FCAServer(SystemProperties.getProperty("SAPSYSTEMNAME")
        + "_" + SystemProperties.getProperty("SAPSYSTEM") + "_"
        + ServiceContext.getServiceContext().getApplicationServiceContext()
        .getClusterContext().getClusterMonitor().getCurrentParticipant()
        .getClusterId() + "_HTTP");
    } catch (Exception e) {
      Log.logFatal("ASJ.http.000226", "Could not initialize FCA server.", e, null, null, null);
      return;
    }

    ThreadSystem threadSystem = ServiceContext.getServiceContext().getApplicationServiceContext()
        .getCoreContext().getThreadSystem();
    resourceManager = new ThrResourceManager(threadSystem, this, numberOfThreads);
    //Replaced from ThrResourceManager
//    for (int i = 0; i < numberOfThreads; i++) {
//      FCAProcessorThread fcaThread = new FCAProcessorThread();
//      threadSystem.executeInDedicatedThread(fcaThread, "HTTP Worker [" + i + "]");
//    }
  } 

  public void stopServer() {
    if (fcaServer == null) {
      return;
    }

    stopping = true;
    try {
      fcaServer.close();
    } catch (FCAException e) {
      Log.logWarning("ASJ.http.000101", "Error while closing FCAServer.", e, null, null, null);
    } finally {
      fcaServer = null;
    }
  }

  /**
   * Closes the current FCAServer instance and creates a new one without
   * closing other running threads.
   *
   * <p/>The first thread closes the old erroneous FCAServer instance then
   * together with other threads waits for the last thread to create a new
   * FCAServer instance and leave them work </p>
   *
   * TODO: Make this method private
   */
  public void recreateFCAServer() {
    synchronized (recreateFCAServerLock) {
      if (waitingFCAThreads == 0) {
        // First thread closes FCAServer instance
        try {
          fcaServer.close();
        } catch (FCAException e) {
          Log.logWarning("ASJ.http.000079", "Error while closing FCAServer.", e, null, null, null);
        }
      } else if (waitingFCAThreads == numberOfThreads - 1) {
        // Last thread tries 5 times to create new FCAServer instance
        for (int i = 0; !stopping; i++) {
          try {
            FCAProperties.init(4);
            fcaServer = new FCAServer(SystemProperties.getProperty("SAPSYSTEMNAME")
              + "_" + SystemProperties.getProperty("SAPSYSTEM") + "_"
              + ServiceContext.getServiceContext().getApplicationServiceContext()
              .getClusterContext().getClusterMonitor().getCurrentParticipant()
              .getClusterId() + "_HTTP");
            break;
          } catch (Exception e) {
            if (i < 5) {
              Log.logWarning("ASJ.http.000080", "Error while creating FCAServer.", e, null, null, null);
              try {
                Thread.sleep(1000);
              } catch (InterruptedException e1) {
                // Threads will be left to die
                stopping = true;
              }
            } else {
              Log.logError("ASJ.http.000214", "Error while creating FCAServer.", e, null, null, null);
              // Leaves threads to die
              stopping = true;
            }
          }
        }

        waitingFCAThreads = 0;
        // Frees waiting threads
        recreateFCAServerLock.notifyAll();
        return;
      }

      // Threads waits the last thread to create new FCAServer instance
      waitingFCAThreads++;
      try {
        recreateFCAServerLock.wait();
      } catch (InterruptedException e) {
        // Threads will be left to die
        stopping = true;
      }
    }
  }
  
  public RequestProcessorThread getInstance() {
    return new FCAProcessorThread();
  }

  public String threadGroup() {
    return "HTTP Worker";
  }

  public boolean dedicated() {
    return true;
  }

  class FCAProcessorThread extends RequestProcessorThread {

    public void process() {

        try {
          FCAConnection conn;
          // Do not pass anything else than empty string ("") as
          // first method argument. Required by thread monitoring
          ThreadWrapper.pushTask("", ThreadWrapper.TS_WAITING_FOR_TASK);
          try {
            conn = fcaServer.accept();
            if (LOCATION_HTTP_REQUEST.beDebug()) {
              String msg = "ProcessorThread[" + getThreadNumber() + "] accept connection [" + conn.getRequestPath() + "]";
              LOCATION_HTTP_REQUEST.debugT(msg);
            }
          } catch (FCAException fcae) {
            // The normal way a FCAServer instance to leave the threads
            // waiting on its accept method is to throw an FCAException
            if (stopping) {
              return;
            }
            CATEGORY_HTTP.logThrowableT(Severity.WARNING, LOCATION_HTTP,
              "Unexpected error while accepting HTTP request", fcae);
            // Restart of ICM could cause close of the existing FCAServer
            // instance, in this case it should be recreated
            recreateFCAServer();
            return;
          } finally {
            ThreadWrapper.popTask();
          }
          ThreadWrapper.pushTask("Processing HTTP request",
            ThreadWrapper.TS_PROCESSING);
          monitoring.setThreadsInProcess(clientsRunning.incrementAndGet());
          try {
            if (conn == null) { // in case of returned null for connection an NPE was thrown in chainedRequest() method
                                // see CSN 823402 2007
              Log.logError("ASJ.http.000215",
                "Unexpected NULL returned for FCA connection. The request cannot be processed. " +
                "Check the current state of ICM.", null, null, null);
            } else {
              chainedRequest(this, conn, ServiceContext.getIcmClusterId(), connectionId.incrementAndGet());
            }


          } finally {
            monitoring.setThreadsInProcess(clientsRunning.decrementAndGet());
            try {
              // Thread pool clears the context of each returned thread, but
              // HTTP Provider service doesn't return its worker threads to the
              // pull until the server is running, so the context have to be
              // cleared here before reusing the tread
              com.sap.engine.system.ThreadWrapperExt.clearManagedThreadRelatedData();
            } catch (Exception e) {
              CATEGORY_HTTP.logThrowableT(Severity.WARNING, LOCATION_HTTP,
                "Unexpected error while emptying thread context", e);
            }
          }
        } catch (OutOfMemoryError e) {
          // OutOfMemory should cause restart of the server process,
          // so that it is important that it be re-thrown
          throw e;
        } catch (ThreadDeath td) {
          CATEGORY_HTTP.logThrowableT(Severity.WARNING, LOCATION_HTTP,
            "Unexpected error while processing HTTP request", td);
          // This thread is going to die, but HTTP Provider service have to
          // keep the configured number of request processing threads, so a new
          // one have to be started with the same name
//          FCAProcessorThread fcaThread = new FCAProcessorThread();
//          ServiceContext.getServiceContext().getApplicationServiceContext()
//            .getCoreContext().getThreadSystem().executeInDedicatedThread(fcaThread,
//                Thread.currentThread().getName());
            resourceManager.replaceThread(this);

          // If ThreadDeath is caught by a method, it is important that it be
          // re-thrown so that the thread actually dies
          throw td;
        } catch (Throwable e) {
          CATEGORY_HTTP.logThrowableT(Severity.WARNING, LOCATION_HTTP,
            "Unexpected error while processing HTTP request", e);
        }

    }



    @Override
    protected boolean isStopped() {
      return stopping || Thread.interrupted();
    }

    @Override
    protected boolean consume(FCAConnection conn,String consName) throws FCAException {
      return super.consume(conn, consName);
    }

    @Override
    protected void process(FCAConnection conn) {
      try {
        // Do not pass anything else than empty string ("") as
        // first method argument. Required by thread monitoring	       
        ThreadWrapper.pushTask("Processing HTTP request",
            ThreadWrapper.TS_PROCESSING);
        monitoring.setThreadsInProcess(clientsRunning.incrementAndGet());
        try {
          if (conn == null) { // in case of returned null for connection an NPE was thrown in chainedRequest() method
            // see CSN 823402 2007
            Log.logError("ASJ.http.000390",
                "Unexpected NULL returned for FCA connection. The request cannot be processed. " +
                "Check the current state of ICM.", null, null, null);
          } else {
            chainedRequest(this, conn, ServiceContext.getIcmClusterId(), connectionId.incrementAndGet());
          }


        } finally {
          monitoring.setThreadsInProcess(clientsRunning.decrementAndGet());
          try {
            // Thread pool clears the context of each returned thread, but
            // HTTP Provider service doesn't return its worker threads to the
            // pull until the server is running, so the context have to be
            // cleared here before reusing the tread
            com.sap.engine.system.ThreadWrapperExt.clearManagedThreadRelatedData();
          } catch (Exception e) {
            CATEGORY_HTTP.logThrowableT(Severity.WARNING, LOCATION_HTTP,
                "Unexpected error while emptying thread context", e);
          }
        }
      } catch (OutOfMemoryError e) {
        // OutOfMemory should cause restart of the server process,
        // so that it is important that it be re-thrown
        throw e;
      } catch (ThreadDeath td) {
        CATEGORY_HTTP.logThrowableT(Severity.WARNING, LOCATION_HTTP,
            "Unexpected error while processing HTTP request", td);
        // This thread is going to die, but HTTP Provider service have to
        // keep the configured number of request processing threads, so a new
        // one have to be started with the same name
//      FCAProcessorThread fcaThread = new FCAProcessorThread();
//      ServiceContext.getServiceContext().getApplicationServiceContext()
//      .getCoreContext().getThreadSystem().executeInDedicatedThread(fcaThread,
//      Thread.currentThread().getName());
        resourceManager.replaceThread(this);

        // If ThreadDeath is caught by a method, it is important that it be
        // re-thrown so that the thread actually dies
        throw td;
      } catch (Throwable e) {
        CATEGORY_HTTP.logThrowableT(Severity.WARNING, LOCATION_HTTP,
            "Unexpected error while processing HTTP request", e);
      }			
    }


  }

  void chainedRequest(FCAProcessorThread processorThread, FCAConnection connection, int icm_id, int client_id) {
//    Client client = null;
//    HTTPRequestImpl request =  null;
//    HTTPResponseImpl response = null;
    ClientRequest clientRequest = new ClientRequest();
    //boolean clientInitialized = false;
    long time = System.currentTimeMillis();
    try {
      // report the clientId to the SAPVM for profiling purposes
      ThreadWrapper.setRequestID(String.valueOf(client_id));

      HostChainImpl chain = new HostChainImpl(composer.getFilters(),
          ServiceContext.getServiceContext());
      // In case of HTTP request - lazy initialization of sslAttributes
      try {
        String taskName = connection.getRequestPath();
        String consumerType = getConsumerType(taskName);
        try {
          if (!processorThread.consume(connection, consumerType)) { 
            if (ServiceContext.getServiceContext().getHttpProperties().isUsePostponedRequestQueue()) {
              return;
            } else {
              if (connection.getSessionIdx() > -1) {
                try {
                  ShmWebSession.deactivate(connection.getSessionIdx());
                } catch (Exception e) {
                  Log.logError("ASJ.http.000348", "Unexpected error in processing request.", e, null, null, null);
                }
              }
              // init Client, Request, Response
              initializeClient(connection, icm_id, client_id, clientRequest);
              if (clientRequest.getClient().initialize()) {
                clientRequest.getResponse().sendError(new ErrorData(ResponseCodes.code_service_unavailable,
                    Responses.mess16.replace("{URL}", consumerType), "", false,
                    new SupportabilityData()));//here we do not need user action
              }
              return;
            }
          }
        } catch (Exception ex) {
          if (connection.getSessionIdx() > -1) {
            try {
              ShmWebSession.deactivate(connection.getSessionIdx());
            } catch (Exception e) {
              Log.logError("ASJ.http.000389", "Unexpected error in processing request.", e, null, null, null);
            }
          }
          if (clientRequest.getClient() == null) {
            initializeClient(connection, icm_id, client_id, clientRequest);
          }
          if (clientRequest.getClient().initialize()) {
            clientRequest.getResponse().sendError(new ErrorData(ResponseCodes.code_service_unavailable,
                Responses.mess16.replace("{URL}", consumerType), "", false,
                new SupportabilityData()));//here we do not need user action
          }
          return;

        }

        if (clientRequest.getClient() == null) {
          initializeClient(connection, icm_id, client_id, clientRequest);
        }
        // Pushes the task in the tread. try {...} finally block
        // guarantees that task always will be popped at the end
        // Task name could be seen in SAP MMC -> Threads -> Task
        ThreadWrapper.pushTask(taskName, ThreadWrapper.TS_PROCESSING);
        if (clientRequest.getClient().initialize()) {
          try {
            chain.process(clientRequest.getRequest(), clientRequest.getResponse());
          } finally {
            ThreadWrapper.popTask();
          }
        }
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        Log.logError("ASJ.http.000349", "Unexpected error in processing request.", e, null, null, null);
      }
    } finally {
      int traceResponseTimeAbove = ServiceContext.getServiceContext().getHttpProperties().getTraceResponseTimeAbove();
      if (traceResponseTimeAbove > -1) {
        HashMapObjectLong timeStatistics = new HashMapObjectLong();
        if (clientRequest.getClient() != null) {
          timeStatistics = clientRequest.getClient().getRequestAnalizer().getTimeStatisticsMap();
        } 
        time = System.currentTimeMillis() - time;
        timeStatistics.put("/request", time);
        if (LOCATION_HTTP.beDebug()) {
          LOCATION_HTTP.debugT((new StringBuilder()).append("Processor.chainedRequest(): Put new entry '/request' with time [").append(time)
              .append("] for client [").append(client_id).append("].").toString());
        }

        if (time >= traceResponseTimeAbove) {
          if (LOCATION_HTTP.beDebug()) {
            makeTimeStatistics(timeStatistics, new String(MaskUtils.maskRequestLine(connection.getRequestPath().getBytes())), client_id);
          }
        }
      }

      if (clientRequest.getClient() != null) {
        clientRequest.getClient().finish();
        if (!clientRequest.getClient().getRequestAnalizer().isPreserved()) {
          if (ServiceContext.getServiceContext().getHttpProperties().isUseClientObjectPools()) {
            clientsPool.returnInPool(clientRequest.getClient());
          } else {
            clientRequest.setClient(null);
          }
        }
        clientRequest = null;
      }
    }

  }

  private void initializeClient(FCAConnection connection, int icm_id, int client_id,
      ClientRequest clientRequest) throws IOException {
    // Creates and initializes HTTPRequest instance
    clientRequest.initClientRequest(ServiceContext.getServiceContext().getHttpProperties().isUseClientObjectPools() ?
        (Client)clientsPool.getObject() :  new Client(), new HTTPRequestImpl(), new HTTPResponseImpl());
    clientRequest.getClient().init(connection, connection.getLocalPort(), icm_id, client_id,
        connection.isSecure(), null, releaseManager);

  }
  
  private String getConsumerType(String requestURL) {
    if (ServiceContext.getServiceContext().getHttpProperties().isConsumerTypeIsAlias()) {
      // currently the alias = the string between the first two / /
      if (requestURL == null || requestURL.length() == 0 || requestURL.equals("/")) {
        return "/";
      }
      int firstSlashIndex = requestURL.indexOf("/");
      if (firstSlashIndex == -1) { //no / in the url -> this should never happend 
        return requestURL;  
      } else if (firstSlashIndex == 0) { 
        int secondIndex = requestURL.indexOf("/", firstSlashIndex + 1);
        if (secondIndex != -1) {
          return requestURL.substring(firstSlashIndex, secondIndex);
        } else {
          return requestURL.substring(firstSlashIndex);
        }
      } else if (firstSlashIndex != 0) {
        return requestURL.substring(0, firstSlashIndex);  // the url does not start with / -> return the
      }
    } 
    return requestURL;
  }
  
  
  private void makeTimeStatistics(HashMapObjectLong map, String request, int client_id) {
    StringBuilder result = new StringBuilder(150);

    char[] emptyString = new char[150];
    for (int i = 0; i < 150; i++) {
      emptyString[i] = ' ';
    }

    result.append("\r\n+----------------------------------------------------------------------------------------------------------------------------------------------------+\r\n");
    String str = (new StringBuilder()).append("| Time statistics for client [").append(client_id).append("]").toString();
    result.append(str).append(emptyString, 0, 149 - str.length()).append("|");
    result.append("\r\n+----------+---------+-------------------------------------------------------------------------------------------------------------------------------+\r\n");
    String str1 = "| duration | ratio   |";
    result.append(str1).append(emptyString, 0, 149 - str1.length()).append("|");
    result.append("\r\n+----------+---------+-------------------------------------------------------------------------------------------------------------------------------+\r\n");
    long wholeTime = map.get("/request");
    String str3 = (new StringBuilder()).append("| ").append(wholeTime).append("ms").toString();
    result.append(str3).append(emptyString, 0, 11 - str3.length()).append("| 100%    ");
    String str2 = (new StringBuilder()).append("| request (").append(request).append(")").toString();
    result.append(str2);
    if ((128 - str2.length()) > 0) {
      result.append(emptyString, 0, 128 - str2.length());
    }
    result.append("|");
    result.append("\r\n+----------+---------+-------------------------------------------------------------------------------------------------------------------------------+\r\n");
    map.remove("/request");

    String handleRequest = null;
    String runServlet = null;
    String staticFile = null;
    Enumeration keys = map.keys();
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      if (key.startsWith("/request/invokeWebContainer/handleRequest (")) {
        if (key.endsWith(")")) {
          handleRequest = createLogStats(key, 28, map, wholeTime, 128, 4, emptyString);
        } else {
          runServlet = createLogStats(key, key.lastIndexOf("/") + 1, map, wholeTime, 128, 6, emptyString);
        }
      } else if (key.startsWith("/request/findRequestedFile (")) {
        staticFile = createLogStats(key, 9, map, wholeTime, 128, 4, emptyString);
      }
    }

    if (map.containsKey("/request/invokeWebContainer")) {
      result.append(createLogStats("/request/invokeWebContainer", 9, map, wholeTime, 128, 2, emptyString));
      if (handleRequest != null) {
        result.append(handleRequest);
        if (runServlet != null) result.append(runServlet);
      }
      if (staticFile != null) result.append(staticFile);
      if (map.containsKey("/request/makeAnswer"))
        result.append(createLogStats("/request/makeAnswer", 9, map, wholeTime, 128, 4, emptyString));
    }

    LOCATION_HTTP.debugT(result.toString());
  }//end of makeTimeStatistics(HashMap map, int client_id)

  private String createLogStats(String key, int beginIndex, HashMapObjectLong map, long wholeTime, int logLength, int shift, char[] emptyString) {
    StringBuilder stringBuffer = new StringBuilder();

    long value = map.get(key);

    String str1 = (new StringBuilder()).append("| ").append(value).append("ms").toString();
    stringBuffer.append(str1).append(emptyString, 0, 11 - str1.length());
    String str2 = (new StringBuilder()).append("| ").append(((wholeTime != 0) ? (value * 100) / wholeTime : 100)).append("%").toString();
    stringBuffer.append(str2).append(emptyString, 0, 10 - str2.length());
    String str3 = "| ";
    for (int i = 0; i < shift; i++) {
      str3 += " ";
    }
    str3 += key.substring(beginIndex);
    stringBuffer.append(str3);
    if ((logLength - str3.length()) > 0) {
      stringBuffer.append(emptyString, 0, logLength - str3.length());
    }
    stringBuffer.append("|");
    stringBuffer.append("\r\n+----------+---------+-------------------------------------------------------------------------------------------------------------------------------+\r\n");

    return stringBuffer.toString();
  }


}
