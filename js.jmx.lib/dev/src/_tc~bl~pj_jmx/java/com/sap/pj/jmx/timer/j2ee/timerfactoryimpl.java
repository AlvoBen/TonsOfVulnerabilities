/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.pj.jmx.timer.j2ee;

import com.sap.pj.jmx.timer.Timer;
import com.sap.pj.jmx.timer.TimerFactory;

/**
 *
 * @author    d025700
 */
public class TimerFactoryImpl implements TimerFactory {

  private static Timer timer;

  public static synchronized void init(Timer timer) {
    if (TimerFactoryImpl.timer == null) {
      TimerFactoryImpl.timer = timer;
    } else {
      // ignore multiple initialization
    }
  }

  /**
   * @see com.sap.pj.jmx.timer.TimerFactory#getTimer()
   */
  public Timer getTimer() {
    return timer;
  }

}
