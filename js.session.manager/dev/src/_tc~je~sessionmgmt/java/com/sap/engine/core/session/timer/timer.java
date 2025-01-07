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


import java.util.Date;

/**
 * Author: Georgi-S
 * Date: 2005-4-5
 */
public abstract class Timer {
  private static java.util.Timer timer = new java.util.Timer("Session mangement task", true);

  public static void schedule(AbstractTimerTask task, long delay) {
    timer.schedule(task, delay);
  }

  public static void schedule(AbstractTimerTask task, Date time) {
    timer.schedule(task, time);
  }

  public static void schedule(AbstractTimerTask task, long delay, long period) {
    timer.schedule(task, delay, period);
  }

  public static void schedule(AbstractTimerTask task, Date firstTime, long period) {
    timer.schedule(task, firstTime, period);
  }

  public static void scheduleAtFixedRate(AbstractTimerTask task, long delay, long period) {
    timer.scheduleAtFixedRate(task, delay, period);
  }

  public static void scheduleAtFixedRate(AbstractTimerTask task, Date firstTime, long period) {
    timer.scheduleAtFixedRate(task, firstTime, period);
  }

  static void cancel() {
    timer.cancel();
  }

}
