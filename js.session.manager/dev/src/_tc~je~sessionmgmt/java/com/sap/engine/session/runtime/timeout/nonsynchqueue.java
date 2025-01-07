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
import com.sap.engine.system.ThreadWrapper;
import com.sap.tc.logging.Location;
import com.sap.jvm.monitor.vm.ThreadMemoryInfo;
import com.sap.jvm.monitor.vm.ThreadTimeInfo;
import com.sap.jvm.monitor.vm.VmInfo;

/**
 * List implementation without synchronization between put and get.
 *
 * @author Nikolai Neichev
 */
public class NonSynchQueue extends Thread {

  public static final Location loc = Location.getLocation("com.sap.engine.session.runtime.timeout", Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);
  private boolean end;
  private ListEntry headPut;
  private ListEntry tailPut;

  public NonSynchQueue() {
    this.setName("Session NotSynchronisedQueue");
    headPut = new ListHead();
    ThreadWrapper.setState(ThreadWrapper.TS_WAITING_FOR_TASK); // initial state
  }

  /**
   * Check if the list is empty
   * @return TRUE if empty
   */
  public boolean isEmpty() {
    return (headPut.getNext() == null);
  }

  /**
   * Adds an item in the list(sorted by execution time).
   * @param item the item
   */
  public synchronized void put(ListEntry item) {
    if (isEmpty()) { // no items, so add as first and notify
      headPut.setNext(item); // item is firs
      tailPut = item;
      notify();
    } else if (headPut.getNext().getExpirationTime() > item.getExpirationTime()) { // put as first and notify
      item.setNext(headPut.getNext()); // item next is the first
      headPut.setNext(item); // the first is item
      notify();
    } else { // put as last element
      tailPut.setNext(item); // last's next is item
      tailPut = item; // last is item
    }
  }

  /**
   * When the time to schedule the first in the list(it's the first to execute also),
   * the entire list is detached from the head and out of the synchronisation
   * scheduled to the timer
   */
  public void run() {
    Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader()); // avoid loader leak
    while (!end) {
      try {
        ListEntry firstExecute;
        ListEntry lastExecute;
        synchronized (this) {
          if (!isEmpty()) {
            long waitTime = (headPut.getNext().getExpirationTime() - System.currentTimeMillis());
            if (waitTime > 0) { // if waitTime = 0, we continue
              this.wait(waitTime);
            }
          } else { // list is empty, wait until notify
            this.wait(0);
          }
          if (System.currentTimeMillis() < headPut.getNext().getExpirationTime()) {
            // it's notified by another put and has to recalculate the waitTime
            continue;
          } else { // the wait time has expired, copy the list
            firstExecute = headPut.getNext(); // first to execute is the first
            lastExecute = tailPut; // last to execute is the last
            headPut.setNext(null); // list is empty
            tailPut = null;
          }
        } // end of synchronised block
        // schedule in timer
        ListEntry temp = firstExecute;

        /* spoil current VM picture */
        if (loc.beDebug()) {
          spoilVmInfo();
        }

        for (; ;) {
          long delay = temp.getExpirationTime() - System.currentTimeMillis();
          if (delay < 0) {
            delay = 0;
          }
          /* update info in MMC */
          updateMMC(ThreadWrapper.TS_PROCESSING, "Scheduling");
          
          if (!temp.isCanceled()) {
            try {
              TimeoutProcessor.timer.schedule(temp, delay, temp.getMaxInactiveInterval());
            } catch (IllegalStateException ise) {
              if (loc.beInfo()) {
                loc.infoT("Scheduling ot watchdog: " + temp + " failed, because it's already scheduled or cancelled");
              }
            }
          }

          /* update info in MMC */
          updateMMC(ThreadWrapper.TS_WAITING_FOR_TASK, null);

          if (temp == lastExecute) {  // we've reached the end
            temp.setNext(null);
            break;
          } else {
            ListEntry detachNext = temp;
            temp = temp.getNext();
            detachNext.setNext(null);
          }
        } // end of for loop

        /* log Vm Infos */
        if (loc.beDebug()) {
          logVmInfo();
        }
      } catch (InterruptedException e) {
        // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
        // Please do not remove this comment!
      } catch (Throwable thr) {
        String stackTrace = "";
        StackTraceElement[] elem = thr.getStackTrace();
        for (StackTraceElement stackTraceElement : elem) {
          stackTrace += "\r\n" + stackTraceElement.toString();
        }
        if (loc.beError()) {
          loc.errorT(stackTrace);
        }
      }
    } // while
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

  // use only for instantiating list heads and a tails
  private class ListHead extends ListEntry {

    public void run() {
      // nothing to do
    }

  }

  public synchronized void kill() {
    end = true;
    notify();

    /* update info in MMC */
    updateMMC(ThreadWrapper.TS_NONE, null);
  }

}