package com.sap.engine.services.rmi_p4.finalizer;

import com.sap.engine.services.rmi_p4.garbagecollector.finalize.FinalizeInformer;
import com.sap.engine.services.rmi_p4.finalizer.ConnectionWrapper;
import com.sap.engine.interfaces.cross.Connection;

/**
 * Copyright: Copyright (c) 2003
 * Company: SAP AG - Sap Labs Bulgaria
 *
 * @author Ivan Atanassov
 * @version 7.1
 */
public class StubEmulator extends Thread {
  private FinalizeInformer informer;
  private Connection connection;
  private long timeout;
  private long frequency;

  public static volatile Integer sleepForeverCounter = 0;
  public static volatile Integer workCounter = 0;

  public StubEmulator(FinalizeInformer informer, Connection connection, long frequency, long timeout) {
    this.informer = informer;
    this.connection = connection;
    this.timeout = timeout;
    this.frequency = (frequency < timeout) ? frequency : timeout;
    this.start();
  }

  public void run() {
    long starTime = System.currentTimeMillis();
    long endTime = starTime + timeout;

    while ((System.currentTimeMillis() < endTime)) {
      if (((ConnectionWrapper) connection).isAlive()) {
        synchronized (workCounter) {
          workCounter++;
        }
      } else {
        synchronized (sleepForeverCounter) {
          sleepForeverCounter++;
        }
      }

      informer.setWork(connection, new byte[0], this.toString());

      try {
        Thread.sleep(frequency);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
