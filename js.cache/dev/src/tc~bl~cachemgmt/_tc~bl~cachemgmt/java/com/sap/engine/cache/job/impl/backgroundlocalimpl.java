package com.sap.engine.cache.job.impl;

import com.sap.engine.cache.job.Background;
import com.sap.engine.cache.job.Task;
import com.sap.engine.cache.util.dump.LogUtil;

import java.util.HashMap;

/**
 * @author Petev, Petio, i024139
 */
public class BackgroundLocalImpl implements Background, Runnable {

  // Totally stupid implementation! Prototype only! Todo!

  private static Thread backgroundThread = null;

  private static HashMap tasks = null;

  public BackgroundLocalImpl() {
    if (backgroundThread == null) {
      tasks = new HashMap();
      backgroundThread = new Thread(this);
      backgroundThread.setDaemon(true);
      backgroundThread.start();
    }
  }

  public void registerTask(final Task task) {
    Runnable runnable = new Runnable() {
      public void run() {
        do {
          synchronized (this) {
            try {
              this.wait(task.getInterval());
            } catch (InterruptedException e) {
              LogUtil.logTInfo(e);
              break;
            }
          }
          task.run();
        } while (task.repeatable());
      }
    };
    Thread localThread = new Thread(runnable);
    localThread.setDaemon(true);
    tasks.put(task, localThread);
    ((Thread) tasks.get(task)).start();
  }

  public void unregisterTask(Task task) {
    Thread toStop = (Thread) tasks.remove(task);
    toStop.interrupt();
  }

  public void run() {
    // As stated above, the implementation is totally stupid, so we do nothing here
  }

}
