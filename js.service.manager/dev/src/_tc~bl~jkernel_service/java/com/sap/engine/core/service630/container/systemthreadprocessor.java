package com.sap.engine.core.service630.container;

import com.sap.engine.frame.*;
import com.sap.engine.core.thread.ThreadManager;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * This class is used to invoke particular method in system thread. Currently these are start and stop service operations.
 */
class SystemThreadProcessor implements Runnable {

  //memory container
  private MemoryContainer memoryContainer;
  //target service
  private ServiceWrapper serviceWrapper;
  //if true invoke start otherwise stop
  private boolean isStart;

  //keep the exception if the operation fails
  private ServiceException serviceException = null;
  //system thread finished flag
  private boolean finish = false;

  private static final Location location = Location.getLocation(SystemThreadProcessor.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  SystemThreadProcessor(MemoryContainer memoryContainer, ServiceWrapper serviceWrapper, boolean isStart) {
    this.memoryContainer = memoryContainer;
    this.serviceWrapper = serviceWrapper;
    this.isStart = isStart;
    if (location.beDebug()) {
      location.debugT("SystemThreadProcessor created for " + (isStart ? "start" : "stop") + " service " + serviceWrapper.getComponentName());
    }
  }

  /**
   * Process start/stop service instantly in system thread.
   *
   * @throws ServiceException if any errors occur
   */
  void process() throws ServiceException {
    if (location.beDebug()) {
      location.debugT("Method process() invoked for " + (isStart ? "start" : "stop") + " service " + serviceWrapper.getComponentName());
    }
    ThreadManager system = (ThreadManager) Framework.getManager(Names.THREAD_MANAGER);
    synchronized (this) {
      //start thread instantly
      system.startThread(this, null, (isStart) ? "Services Start Processor" : "Services Stop Processor", true);
      //wait for finish
      while (!finish) {
        try {
          this.wait();
        } catch (InterruptedException e) {
          throw new ServiceException(location, e);
        }
      }
    }
    if (serviceException != null) {
      if (location.beDebug()) {
        location.traceThrowableT(Severity.DEBUG, "Error processing " + (isStart ? "start" : "stop") + " service " + serviceWrapper.getComponentName(), serviceException);
      }
      throw serviceException;
    }
  }

  /**
   * Check whether the current thread is system or application
   *
   * @return true if the thread is system and false otherwise
   */
  static boolean isSystem () {
    boolean result = ((ThreadManager) Framework.getManager(Names.APPLICATION_THREAD_MANAGER)).getThreadContext().isSystem();
    if (location.beDebug()) {
      location.debugT("SystemThreadProcessor.isSystem() = " + result);
    }
    return result;
  }

  /**
   * Process start/stop service
   */
  public void run() {
    try {
      if (isStart) {
        memoryContainer.startServiceRuntime(serviceWrapper, null, false, false);
      } else {
        memoryContainer.stopServiceRuntime(serviceWrapper, null, false);
      }
    } catch (ServiceException e) {
      //$JL-EXC$
      serviceException = e;
    } catch (Throwable t) {
      //$JL-EXC$
      serviceException = new ServiceException(location, t);
      //rethrow if OOM or ThreadDeath
      if (t instanceof OutOfMemoryError) {
        location.throwing(t);
        throw (OutOfMemoryError) t;
      } else if (t instanceof ThreadDeath) {
        location.throwing(t);
        throw (ThreadDeath) t;
      }
    } finally {
      synchronized (this) {
        finish = true;
        this.notify();
      }
    }
  }

}