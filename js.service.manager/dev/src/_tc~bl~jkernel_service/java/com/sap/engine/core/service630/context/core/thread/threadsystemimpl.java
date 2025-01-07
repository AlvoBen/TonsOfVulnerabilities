/*
 * Copyright (c) 2002 by SAP Labs Sofia AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.core.service630.context.core.thread;

import com.sap.engine.core.thread.ThreadManager;
import com.sap.engine.core.thread.ThreadContextImpl;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.frame.core.thread.ContextObject;
import com.sap.engine.frame.core.thread.Task;
import com.sap.engine.frame.core.thread.execution.Executor;
import com.sap.engine.core.thread.execution.ExecutorFactory;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;

/**
 * @see com.sap.engine.frame.core.thread.ThreadSystem
 *
 * @author Dimitar Kostadinov, Krasimir Semerdzhiev, Elitsa Pancheva  
 * @version 710
 */
public class ThreadSystemImpl implements ThreadSystem {

  private ThreadManager systemThreadManager;
  private ThreadManager applicationThreadManager;
  
  public ThreadSystemImpl() {
    this.systemThreadManager = (ThreadManager) Framework.getManager(Names.THREAD_MANAGER);
    this.applicationThreadManager = (ThreadManager) Framework.getManager(Names.APPLICATION_THREAD_MANAGER);
  }
  
  public void startCleanThread(Runnable thread, boolean instantly) {
    if (getThreadContext() == null) {
      systemThreadManager.startCleanThread(thread, instantly);
    } else {
      applicationThreadManager.startCleanThread(thread, instantly);
    }
  }

  public void startCleanThread(Task task, boolean instantly) {
    if (getThreadContext() == null) {
      systemThreadManager.startCleanThread(task, instantly);
    } else {
      applicationThreadManager.startCleanThread(task, instantly);
    }
  }

  public void startCleanThread(Runnable thread, boolean system, boolean instantly) {
    if (system) {
      systemThreadManager.startCleanThread(thread, instantly);
    } else {
      applicationThreadManager.startCleanThread(thread, instantly);
    }
  }

  public void startCleanThread(Task task, boolean system, boolean instantly) {
    if (system) {
      systemThreadManager.startCleanThread(task, instantly);
    } else {
      applicationThreadManager.startCleanThread(task, instantly);
    }
  }

  public void startThread(Runnable thread, boolean system) {
    if (system) {
      systemThreadManager.startThread(thread);
    } else {
      applicationThreadManager.startThread(thread);
    }
  }

  public void startThread(Runnable thread, String taskName, String threadName, boolean system) {
    if (system) {
      systemThreadManager.startThread(thread, taskName, threadName);
    } else {
      applicationThreadManager.startThread(thread, taskName, threadName);
    }
  }

  public void startThread(Runnable thread, boolean system, boolean instantly) {
    if (system) {
      systemThreadManager.startThread(thread, instantly);
    } else {
      applicationThreadManager.startThread(thread, instantly);
    }
  }

  public void startThread(Runnable thread, String taskName, String threadName, boolean system, boolean instantly) {
    if (system) {
      systemThreadManager.startThread(thread, taskName, threadName, instantly);
    } else {
      applicationThreadManager.startThread(thread, taskName, threadName, instantly);
    }
  }

  public void startTask(Task task, boolean instantly) {
    applicationThreadManager.startThread(task, instantly);
  }

  public void startThread(Task task, String taskName, String threadName, boolean instantly) {
    applicationThreadManager.startThread(task, taskName, threadName, instantly);
  }

  public void startTask(Task task, long timeout) {
    applicationThreadManager.startThread(task, timeout);
  }

  public void startThread(Task task, String taskName, String threadName, long timeout) {
    applicationThreadManager.startThread(task, taskName, threadName, timeout);
  }

  public ThreadContext getThreadContext() {
    ThreadContextImpl tci = (ThreadContextImpl) applicationThreadManager.getThreadContext();
    if (tci.isSystem()) {
      return null;
    } else {
      return tci;
    }
  }

  /**
   * Register object that is connected to a thread. Depends from which thread
   * you access it, the object has different instance. You can access the
   * object by name. Returns -1 if working on client side and value >= 0 if
   * working on cluster node.
   *
   * @param  name    Name of the ContextObject
   * @param  object  ContextObject to be registered
   * @return ID of the registered object
   */
  public int registerContextObject(String name, ContextObject object) {
    return applicationThreadManager.registerContextObject(name, object);
  }

  public int getContextObjectId(String name) {
    return applicationThreadManager.getContextObjectId(name);
  }

  public void unregisterContextObject(String name) {
    applicationThreadManager.unregisterContextObject(name);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.frame.core.thread.ThreadSystem#createExecutor(java.lang.String, int, int)
   */
  public Executor createExecutor(String name, int maxConcurrency, int maxQueueSize) {
    return ExecutorFactory.getInstance().createExecutor(name, maxConcurrency, maxQueueSize);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.frame.core.thread.ThreadSystem#createExecutor(java.lang.String, int, int, byte)
   */
  public Executor createExecutor(String name, int maxConcurrency, int maxQueueSize, byte rejectionPolicy) {
    return ExecutorFactory.getInstance().createExecutor(name, maxConcurrency, maxQueueSize, rejectionPolicy);
  }
  
  /* (non-Javadoc)
   * @see com.sap.engine.frame.core.thread.ThreadSystem#createCleanThreadExecutor(java.lang.String, int, int, byte)
   */
  public Executor createCleanThreadExecutor(String name, int maxConcurrency, int maxQueueSize, byte rejectionPolicy) {
  	return ExecutorFactory.getInstance().createCleanThreadExecutor(name, maxConcurrency, maxQueueSize, rejectionPolicy);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.frame.core.thread.ThreadSystem#destroyExecutor(com.sap.engine.frame.core.thread.execution.Executor)
   */
  public void destroyExecutor(Executor instance) {
    ExecutorFactory.getInstance().destroyExecutor(instance);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.frame.core.thread.ThreadSystem#executeInDedicatedThread(java.lang.Runnable, java.lang.String)
   */
  public void executeInDedicatedThread(Runnable runnable, String threadName) {
    ExecutorFactory.getInstance().executeInDedicatedThread(runnable, threadName);
  }

  /* (non-Javadoc)
   * @see com.sap.engine.frame.core.thread.ThreadSystem#executeInDedicatedThread(java.lang.Runnable, java.lang.String, java.lang.String)
   */
  public void executeInDedicatedThread(Runnable runnable, String taskDescription, String threadName) {
    ExecutorFactory.getInstance().executeInDedicatedThread(runnable, taskDescription, threadName);
  }

}