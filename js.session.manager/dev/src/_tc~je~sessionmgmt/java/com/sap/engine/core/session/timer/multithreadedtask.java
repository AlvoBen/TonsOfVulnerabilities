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

import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.core.thread.ThreadManager;

/**
 * Author: georgi-s
 * Date: 2005-3-24
 */
public abstract class MultiThreadedTask extends AbstractTimerTask {

  private static ThreadManager thrManager = (ThreadManager) Framework.getManager(Names.APPLICATION_THREAD_MANAGER);

  ThreadAction action;


  protected MultiThreadedTask() {
    this.action = new ThreadAction();
  }

  protected abstract boolean fire();

  protected abstract void action();

  public final void task() {
    if (fire()) {
      thrManager.startThread(action);
    }
  }

  private class ThreadAction implements Runnable {

    public void run() {
      action();
    }

  }

}
