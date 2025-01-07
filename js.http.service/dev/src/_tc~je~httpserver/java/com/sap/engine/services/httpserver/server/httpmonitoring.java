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
package com.sap.engine.services.httpserver.server;

import com.sap.engine.services.httpserver.ServerMonitoring;
import com.sap.engine.lib.util.ConcurrentHashMapObjectLong;
import com.sap.engine.lib.util.ConcurrentHashMapIntLong;

import java.util.NoSuchElementException;

public class HttpMonitoring implements ServerMonitoring {
  private long allResponsesCount = 0;
  private long allResponseTime = 0;
  private long allRequestsCount = 0;
  
  /**
   * Holds the number of used for requests processing HTTP threads
   */
  private int activeThreads;
  
  /**
   * Holds the number of configured for requests processing HTTP threads
   */
  private int maximumThreads;

  private ConcurrentHashMapObjectLong requestMethodToRequestsCount = new ConcurrentHashMapObjectLong();
  private ConcurrentHashMapIntLong responseCodeToResponsesCount = new ConcurrentHashMapIntLong();

  public long getAllRequestsCount() {
    return allRequestsCount;
  }

  public long getAllResponsesCount() {
    return allResponsesCount;
  }

  public long getTotalResponseTime() {
    return allResponseTime;
  }

  public String[] getMethodNames() {
    synchronized (requestMethodToRequestsCount) {
      int methodsCount = requestMethodToRequestsCount.size();
      if (methodsCount == 0) {
        return new String[0];
      } else if (methodsCount == 1) {
        return new String[]{(String)requestMethodToRequestsCount.getAllKeys()[0]};
      } else {
        Object[] keys = requestMethodToRequestsCount.getAllKeys();
        String[] methodNames = new String[keys.length];
        System.arraycopy(keys, 0, methodNames, 0, keys.length);
        return methodNames;
      }
    }
  }

  public synchronized long getRequestsCount(String methodName) {
    try {
      return requestMethodToRequestsCount.get(methodName);
    } catch (NoSuchElementException e) {
      return 0;
    }
  }

  public synchronized int[] getResponseCodes() {
    synchronized (responseCodeToResponsesCount) {
      int responseCodesCount = responseCodeToResponsesCount.size();
      if (responseCodesCount == 0) {
        return new int[0];
      } else if (responseCodesCount == 1) {
        return new int[]{responseCodeToResponsesCount.getAllKeys()[0]};
      } else {
        return responseCodeToResponsesCount.getAllKeys();
      }
    }
  }

  public synchronized long getResponsesCount(int responseCode) {
    synchronized (responseCodeToResponsesCount) {
      try {
        return responseCodeToResponsesCount.get(responseCode);
      } catch (NoSuchElementException e) {
        //ok - no such element
        return 0;
      }
    }
  }

  public long getResponsesFromCacheCount() {
    return 0;
  }

  // ------------------------ PROTECTED ------------------------

  public void newRequest(String requestMethod) {
    allRequestsCount++;
    synchronized (requestMethodToRequestsCount) {
      if (requestMethodToRequestsCount.containsKey(requestMethod)) {
        long count = requestMethodToRequestsCount.get(requestMethod);
        requestMethodToRequestsCount.put(requestMethod, count + 1);
      } else {
        requestMethodToRequestsCount.put(requestMethod, 1);
      }
    }
  }

  public void addResponseTime(long responseTime, int responseCode) {
    allResponsesCount++;
    allResponseTime += responseTime;
    if (allResponseTime < 0) {
      allResponsesCount = 1;
      allResponseTime = responseTime;
    }
    synchronized (responseCodeToResponsesCount) {
      if (responseCodeToResponsesCount.containsKey(responseCode)) {
        long count = 0;
        try {
          count = responseCodeToResponsesCount.get(responseCode);
        } catch (NoSuchElementException e) {
          //ok - no such element
          count = 0;
        }
        responseCodeToResponsesCount.put(responseCode, count + 1);
      } else {
        responseCodeToResponsesCount.put(responseCode, 1);
      }
    }
  }

  /**
   * Sets the number of used for requests processing HTTP threads
   * 
   * @param threads
   * The number of used threads
   */
  void setThreadsInProcess(int threads) {
    activeThreads = threads;
  }
  
  /**
   * Gets the number of threads used currently in request processing
   * 
   * @return
   * The number of active threads
   */
  public int getActiveThreadsCount() {
    return activeThreads;
  }
  
  /**
   * Sets the number of configured for requests processing HTTP threads
   * 
   * @param threads
   * The number of configured threads
   */
  void setThreadsInPool(int threads) {
    maximumThreads = threads;
  }
  
  /**
   * Gets the number of all configured for request processing threads
   * 
   * @return
   * The total number of threads 
   */
  public int getThreadPoolSize() {
    return maximumThreads;
  }
  
  /**
   * Gets the rate of used to configured HTTP threads
   * 
   * @return
   * The ratio of used to configured HTTP threads
   */  
  public int getThreadsInProcessRate() {
    return (activeThreads/maximumThreads)*100;
  }
}
