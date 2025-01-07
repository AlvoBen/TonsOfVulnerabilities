/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.runtime.timeout;

import com.sap.engine.core.Names;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.SimpleLogger;
import com.sap.engine.system.ThreadWrapper;
import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.jvm.monitor.vm.VmInfo;
import com.sap.jvm.monitor.vm.ThreadMemoryInfo;
import com.sap.jvm.monitor.vm.ThreadTimeInfo;

/**
 * Used as a wrapper for usefull info and to be pooled
 *
 * @author Nikolai Neichev
 */
public class Timeouter extends Thread {

  public static final Location loc = Location.getLocation("com.sap.engine.session.runtime.timeout", Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  private TimeoutProcessor processor;
  private long interruptTime = -1;
  private boolean end;

  public Timeouter(TimeoutProcessor processor) {
    this.setName("Session Invalidator " + this.getName());
    this.processor = processor;
    ThreadWrapper.setState(ThreadWrapper.TS_WAITING_FOR_TASK); // initial state
  }

  /**
   * Sets the interrupt wait time
   * @param interruptTime the time to wait before interrupt
   */
  public synchronized void setInterruptTimeStamp(long interruptTime) {
    this.interruptTime = interruptTime;
  }

  /**
   * Gets the interrupt wait time
   * @return interruptTime the time to wait before interrupt
   */
  public synchronized long getInterruptTimeStamp() {
    return interruptTime;
  }

  /**
   * Gets the left wait time
   * @return interruptTime time left for sleep, -1 if no time to wait
   */
  public synchronized long getLeftTime() {
    if (interruptTime == -1) { // waiting on dequeue
      return -1;
    } else { // running invalidator
      long waitTimeLeft = interruptTime - System.currentTimeMillis();
      if (waitTimeLeft > 0) { // has some time to wait
        return waitTimeLeft;
      } else { // expired
        return -2;
      }
    }
  }

  /**
   * Check if the thread has to stop
   * @return TRUE if it has to stop, FALSE if not
   */
  public synchronized boolean isEnd() {
    return end;
  }

  /**
   * Setter
   * @param end the end value
   */
  synchronized void setEnd(boolean end) {
    this.end = end;
  }

	// sleep; after that, if there is something in the queue - take it and invalidate all; again sleep;

	public void run() {
    Runnable runn;
		int noSleepIterations = 0;
		int sleepTime = 1000; // a default sleep time 1 sec
		while (!isEnd()) {
      setInterruptTimeStamp(-1);
				// we have to pay respect to the new requests, otherwise the performace is going down...
			Thread.yield();
			try {
				if (noSleepIterations == 0) { // take a little break
          int currentQueueSize = processor.invalidatorQueue.size();
          // all for invalidation separated between the timeouters...
          if (currentQueueSize > 0) {
            noSleepIterations = currentQueueSize / TimeoutProcessor.INVALIDATION_TIMEOUTERS;
          }
          Thread.sleep(sleepTime*TimeoutProcessor.INVALIDATION_TIMEOUTERS); // 1 second sleep per thread
				} else {
					noSleepIterations--;
				}

				runn = (Runnable) processor.invalidatorQueue.dequeue();

				if (isEnd()) { // return the runnable, because it's not executed
					if (runn != null) { // runn = null if thread is interrupted in dequeue
						processor.invalidatorQueue.enqueue(runn);
					}
					return;
				}
				setInterruptTimeStamp(System.currentTimeMillis() + processor.getInvalidateWaitTime());
        updateMMC(ThreadWrapper.TS_PROCESSING, "running for " + ((RuntimeSessionModel.SessionInvalidator) runn).info());
        /* spoil current VM picture */
        if (loc.beDebug()) {
          spoilVmInfo();
        }

        runn.run();

        /* log Vm Infos */
        if (loc.beDebug()) {
          logVmInfo(((RuntimeSessionModel.SessionInvalidator) runn).info());
        }
      } catch (Exception e) {
        if (loc.beWarning()) {
          loc.traceThrowableT(Severity.WARNING, "", e);
        }
      }
      updateMMC(ThreadWrapper.TS_WAITING_FOR_TASK, null);
    }
  }

  /* values for VM Info */
  private long memoryConsumption = 0;
  private long cpuTime = 0;

  /**
   * save current VM state - for debug
   */
  private void spoilVmInfo() {
    ThreadMemoryInfo memInfo = VmInfo.getThreadMemoryInfo(this);
    if (memInfo != null) {
      memoryConsumption = memInfo.getMemoryConsumption();
    }
    ThreadTimeInfo timeInfo = VmInfo.getThreadTimeInfo(this);
    if (timeInfo != null) {
      cpuTime = timeInfo.getCpuTime();
    }
  }


  /**
   * Log information about VM picture for the current Thread - for debug
   * @param info the info 
   */
  private void logVmInfo(String info) {
    ThreadMemoryInfo memInfo = VmInfo.getThreadMemoryInfo(this);
    if (memInfo != null) {
      memoryConsumption = memInfo.getMemoryConsumption() - memoryConsumption;
    }
    ThreadTimeInfo timeInfo = VmInfo.getThreadTimeInfo(this);
    long currentCPU = 0;
    if (timeInfo != null) {
      currentCPU = timeInfo.getCpuTime();
      cpuTime = currentCPU - cpuTime;
    }
    loc.debugT(getName() + " |" + info + "| MemoryConsumption:" + memoryConsumption + " bytes| Delta CPU Time:" + cpuTime + "| full CPU Time:" + currentCPU);
  }

  private void updateMMC(int state, String task) {
    ThreadWrapper.setState(state);
    ThreadWrapper.setTaskName(task);
  }

  /**
   * Called by the processor to interrupt this thread
   */
  synchronized void terminate() {
    SimpleLogger.log(Severity.INFO, Category.SYS_SERVER, loc, "ASJ.ses.rt0003", 
	"Sessoin invalidation took more than " + TimeoutProcessor.INVALIDATION_TIMEOUT 
	+ " milliseconds. Terminating hanged " + this.getName());
    setEnd(true);
    this.interrupt();
    /* update info in MMC */
    updateMMC(ThreadWrapper.TS_NONE, null);
    this.stop();
  }

}