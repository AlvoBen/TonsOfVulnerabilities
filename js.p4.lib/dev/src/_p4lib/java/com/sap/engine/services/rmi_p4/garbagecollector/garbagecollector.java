package com.sap.engine.services.rmi_p4.garbagecollector;

import com.sap.engine.services.rmi_p4.ObjectManager;

/**
 * @author Georgy Stanev
 * @version 7.0
 */
public class GarbageCollector implements Runnable {

  private boolean isRunning = true;
  protected ObjectManager manager;

  public GarbageCollector(ObjectManager _manager) {
    this.manager = _manager;
  }

  public void stop() {
    isRunning = false;   //may be must be  manager.collect();
  }

  public void run() {
    while (isRunning) {
      manager.collect();
      synchronized (this) {
        try {
          wait(60000);
        } catch (InterruptedException intr) {
          //$JL-EXC$ process must be stoped with isRuning=false. Interupt is not important itself.
          continue;
        }
      }
    }
  }

}

