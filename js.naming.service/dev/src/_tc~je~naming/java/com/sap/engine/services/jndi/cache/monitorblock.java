/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.cache;

import com.sap.engine.services.jndi.JNDIFrame;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public final class MonitorBlock {

	private final static Location LOG_LOCATION = Location.getLocation(MonitorBlock.class);

  private int access;
  private int wannaBeChangers;
  private boolean change;

  public MonitorBlock() {
    access = 0;
    change = false;
    wannaBeChangers = 0;
  }

  public synchronized void tryToChange() {
    wannaBeChangers++;
    while ((access > 0) || (change)) {
      try {
        wait();
      } catch (InterruptedException ie) {
        LOG_LOCATION.traceThrowableT(Severity.PATH, "", ie);
      }
    }
    change = true;
    wannaBeChangers--;
  }

  public synchronized void tryToAccess() {
    while (change || wannaBeChangers > 0) {
      try {
        wait();
      } catch (InterruptedException ie) {
        LOG_LOCATION.traceThrowableT(Severity.PATH, "", ie);
      }
    }
    access++;
  }

  public synchronized void endChange() {
    change = false;
    notifyAll();
  }

  public synchronized void endAccess() {
    access--;
    notifyAll();
  }

}