/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.core.session.timer;

import com.sap.engine.core.Names;
import com.sap.engine.session.trace.Trace;
import com.sap.engine.system.ThreadWrapper;
import com.sap.jvm.monitor.vm.ThreadMemoryInfo;
import com.sap.jvm.monitor.vm.VmInfo;
import com.sap.jvm.monitor.vm.ThreadTimeInfo;
import com.sap.tc.logging.Location;

import java.util.TimerTask;

/**
 * Author: georgi-s
 * Date: 2005-3-24
 */
public abstract class AbstractTimerTask extends TimerTask {
  public static final String THREAD_NAME = "Session Management Timeout Thread";
  private final static Location loc = Location.getLocation("com.sap.engine.core.session.timer", Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  public abstract void task();

  public abstract String taskDescription();

  public final void run() {
    //TODO add thread task (subtask)
    /* update info in MMC */
    updateMMC(ThreadWrapper.TS_PROCESSING, taskDescription());

    /* spoil current VM picture */
    if(loc.beDebug()){
      spoilVmInfo();
    }

    try {
      task();
    } catch (Exception e) {
      Trace.logException(e);
    }

    /* log Vm Infos */
    if(loc.beDebug()){
      logVmInfo("");
    }
    /* update info in MMC */
    updateMMC(ThreadWrapper.TS_WAITING_FOR_TASK, null);
  }

  protected void updateMMC(int state, String task) {
    ThreadWrapper.setState(state);
    ThreadWrapper.setTaskName(task);
  }

  /* values for VM Info */
  private long memoryConsumption = 0;
  private long cpuTime = 0;

  /**
   * save current VM state - for debug
   */
  private void spoilVmInfo() {
    ThreadMemoryInfo memInfo = VmInfo.getThreadMemoryInfo(Thread.currentThread());
    if (memInfo != null) {
      memoryConsumption = memInfo.getMemoryConsumption();
    }
    ThreadTimeInfo timeInfo = VmInfo.getThreadTimeInfo(Thread.currentThread());
    if (timeInfo != null) {
      cpuTime = timeInfo.getCpuTime();
    }
  }


  /**
   * log information about VM picture for the current Thread - for debug
   * @param info the info to be loged
   */
  private void logVmInfo(String info) {
    ThreadMemoryInfo memInfo = VmInfo.getThreadMemoryInfo(Thread.currentThread());
    if (memInfo != null) {
      memoryConsumption = memInfo.getMemoryConsumption() - memoryConsumption;
    }
    ThreadTimeInfo timeInfo = VmInfo.getThreadTimeInfo(Thread.currentThread());
    long currentCPU = 0;
    if (timeInfo != null) {
      currentCPU = timeInfo.getCpuTime();
      cpuTime = currentCPU - cpuTime;
    }
    loc.debugT(taskDescription() + " |" + info + "| MemoryConsumption:" + memoryConsumption + " bytes| Delta CPU Time:" + cpuTime + "| full CPU Time:" + currentCPU);
  }

}
