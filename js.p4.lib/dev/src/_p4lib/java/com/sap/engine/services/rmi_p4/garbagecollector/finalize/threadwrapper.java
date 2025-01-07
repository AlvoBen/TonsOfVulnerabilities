package com.sap.engine.services.rmi_p4.garbagecollector.finalize;

import com.sap.engine.services.rmi_p4.exception.P4Logger;

/**
 * Copyright: Copyright (c) 2003
 * Company: SAP AG - Sap Labs Bulgaria
 *
 * @author Ivan Atanassov
 * @version 7.1
 */
class ThreadWrapper extends Thread {
  boolean isRunning = true;

  public ThreadWrapper(Runnable target) {
    super(target);
  }

  public boolean isRunning() {
    return isRunning;
  }

  public void stopIt() {
    this.isRunning = false;

    try {
      this.interrupt();
    } catch (Throwable e) {
      e.printStackTrace();
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT(this.getClass() + ".close() \r\n " + P4Logger.exceptionTrace(e));
      }
    }

    try {
      this.stop();
    } catch (Throwable e) {
      e.printStackTrace();
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT(this.getClass() + ".close() \r\n " + P4Logger.exceptionTrace(e));
      }
    }
  }
}
