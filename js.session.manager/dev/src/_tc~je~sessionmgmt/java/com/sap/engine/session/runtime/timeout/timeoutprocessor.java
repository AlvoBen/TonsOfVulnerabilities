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
import com.sap.engine.lib.util.WaitQueue;
import com.sap.engine.system.ThreadWrapper;
import com.sap.tc.logging.Location;
import com.sap.jvm.monitor.vm.ThreadMemoryInfo;
import com.sap.jvm.monitor.vm.ThreadTimeInfo;
import com.sap.jvm.monitor.vm.VmInfo;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * This class is used for execution of session timeouts
 * Also watches if the invalidation call ends in the specified <code>invalidateWaitTime</code>
 *
 * @author Nikolai Neichev
 */
public class TimeoutProcessor extends Thread {

  public static final Location loc = Location.getLocation("com.sap.engine.session.runtime.timeout", Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);
  static Timer timer = new Timer("Session WatchDog executor timer", true);
	 // public - used in timeouter
  public static int INVALIDATION_TIMEOUTERS = 2;
  public static int INVALIDATION_TIMEOUT = 5 * 60 * 1000; // 5 min

    // public - used in Manager
  public final WaitQueue invalidatorQueue = new WaitQueue();
  private ArrayList<TimeouterHolder> threadsToInterrupt = new ArrayList<TimeouterHolder>();
  private NonSynchQueue nonSynchQueue = new NonSynchQueue();
  private boolean end;

  /**
   * Constructor
   */
  public TimeoutProcessor() {
    this.setName("Session Invalidate Timeouter");
    for (int i = 0; i < INVALIDATION_TIMEOUTERS; i++) {
      TimeouterHolder holder = new TimeouterHolder();
      holder.setTimeouter(startNewTimeouter());
      threadsToInterrupt.add(holder);
    }
    nonSynchQueue.setDaemon(true);
    nonSynchQueue.start();
    ThreadWrapper.setState(ThreadWrapper.TS_WAITING_FOR_TASK); // initial state
  }

  /**
   * Puts the task in the queue for scheduling in the timer
   *
   * @param watchDog watchDog to be executed after the specified timeout
   */
  public void schedule(ListEntry watchDog) {
    nonSynchQueue.put(watchDog);
  }

  /**
   * Schedules the task in the timer
   *
   * @param task   task to be scheduled.
   * @param delay  delay in milliseconds before task is to be executed.
   * @param period time in milliseconds between successive task executions.
   * @see java.util.Timer  schedule(TimerTask task, long delay, long period)
   */
  public synchronized void timerSchedule(TimerTask task, long delay, long period) {
    timer.schedule(task, delay, period);
  }

  /**
   * Returns the invalidate wait time
   * @return the wait time
   */
  public long getInvalidateWaitTime() {
    return INVALIDATION_TIMEOUT;
  }

  /**
   * Enables session timeout of the given session watchdog
   * (not synchronized, because only the session timer thread invokes it)
   * @param toRun the runnable to execute
   */
  public void executeSessionInvalidator(Runnable toRun) {
    invalidatorQueue.enqueue(toRun);
  }

  /**
   * Starts a timeouter thread
   *
   * @return the new timeouter
   */
  Timeouter startNewTimeouter() {
    Timeouter t = new Timeouter(this);
    t.setContextClassLoader(this.getClass().getClassLoader());  // avoid loader leak
    t.setDaemon(true);
    t.start();
    return t;
  }

  public void run() {
    Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader()); // avoid loader leak
    while (!end) {
      try {
        /* update info in MMC */
        updateMMC(ThreadWrapper.TS_PROCESSING, "processing Timeouters");

        /* spoil current VM picture */
        if (loc.beDebug()) {
          spoilVmInfo();
        }
        long waitTime = processTimeouters();

          /* update info in MMC */
        updateMMC(ThreadWrapper.TS_WAITING_FOR_TASK, null);

        /* log Vm Infos */
        if (loc.beDebug()) {
          logVmInfo();
        }
        synchronized (this) {
          wait(waitTime);
        }
        timer.purge();
      } catch (InterruptedException ie) {
        // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
        // Please do not remove this comment!
      }
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
   * log information about VM picture for the current Thread - for debug
   */
  private void logVmInfo() {
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
    loc.debugT(getName() + "| MemoryConsumption:" + memoryConsumption + " bytes| Delta CPU Time:" + cpuTime + "| full CPU Time:" + currentCPU);
  }

  private void updateMMC(int state, String task) {
    ThreadWrapper.setState(state);
    ThreadWrapper.setTaskName(task);
  }

  private long processTimeouters() {
    long minWait = INVALIDATION_TIMEOUT;
    for (TimeouterHolder aTimeouterHolder : threadsToInterrupt) {
      long currWait = aTimeouterHolder.getTimeouter().getLeftTime();  // !!! - never returns 0 :)
      if (currWait == -2) { // expired
        if (loc.beError()) {
          loc.errorT("Interrupting session invalidation(delayed more than " + INVALIDATION_TIMEOUT + " ms)");
        }
        aTimeouterHolder.getTimeouter().terminate();
        aTimeouterHolder.setTimeouter(startNewTimeouter());
      } else if ((currWait != -1) && (currWait < minWait)) {
        minWait = currWait;
      }
    }
    return minWait;
  }

  public synchronized void kill() {
    nonSynchQueue.kill();
    end = true;
    notify();
    /* update info in MMC */
    updateMMC(ThreadWrapper.TS_NONE, null);
  }

}