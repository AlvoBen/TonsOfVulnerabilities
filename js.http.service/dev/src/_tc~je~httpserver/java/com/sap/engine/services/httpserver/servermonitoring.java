/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver;

import java.rmi.RemoteException;

/**
 * This interface gives statistics about the requests processed by the http service.
 * It gives details about the count of the processed requests and responses and the time needed for processing them.
 *
 * @author    Maria Jurova
 * @version   6.30
 */
public interface ServerMonitoring {
  /**
   * Returns the number of requests received by the http service.
   *
   * @return      The number of requests received by the http service
   * @throws RemoteException  If some exception occurs in remote communication
   */
  public long getAllRequestsCount() throws RemoteException;

  /**
   * Returns the number of responses returned by the http service.
   *
   * @return      The number of responses returned by the http service
   * @throws RemoteException  If some exception occurs in remote communication
   */
  public long getAllResponsesCount() throws RemoteException;

  /**
   * Returns the total time needed by the http service to process all client requests.
   *
   * @return      The total http service response time
   * @throws RemoteException  If some exception occurs in remote communication
   */
  public long getTotalResponseTime() throws RemoteException;

  /**
   * Returns all http method names that the http service has received requests with.
   *
   * @return    All http method names processed by the http service
   * @throws RemoteException  If some exception occurs in remote communication
   */
  public String[] getMethodNames() throws RemoteException;

  /**
   * Returns the number of http requests received by the http service with a specified http method name.
   *
   * @param methodName    Http method name (GET, POST etc.)
   * @return      The number of http requests with the specified method name eceived by the http service
   * @throws RemoteException  If some exception occurs in remote communication
   */
  public long getRequestsCount(String methodName) throws RemoteException;

  /**
   * Returns all response codes returned by the http service.
   *
   * @return      An array with all response codes returned by the http service
   * @throws RemoteException  If some exception occurs in remote communication
   */
  public int[] getResponseCodes() throws RemoteException;

  /**
   * Returns the number of responses with specified response code returned by the http service.
   *
   * @param responseCode    Http response code
   * @return      The number of responses with the specified response code
   * @throws RemoteException  If some exception occurs in remote communication
   */
  public long getResponsesCount(int responseCode) throws RemoteException;

  /**
   * Returns the number of http responses generated from cached entries.
   *
   * @return    The number of http responses generated from cach
   * @throws RemoteException  If some exception occurs in remote communication
   * 
   * @deprecated
   * Server cache is now part of the ICM therefore server cann't provide
   * information about number of responses returned by cache
   */
  @Deprecated
  public long getResponsesFromCacheCount() throws RemoteException;

  /**
   * Gets the number of threads used currently in request processing
   * 
   * @return
   * The number of active threads
   */
  public int getActiveThreadsCount();
  
  /**
   * Gets the number of all configured for request processing threads
   * 
   * @return
   * The total number of threads 
   */
  public int getThreadPoolSize();
  
  /**
   * Gets the rate of used to configured HTTP threads
   * 
   * @return
   * The ratio of used to configured HTTP threads
   */
  public int getThreadsInProcessRate();
}
