/*
 * Created on 2004.9.15
 *
 */
package com.sap.engine.cache.job.impl;

import java.util.HashMap;

import com.sap.engine.cache.job.Background;
import com.sap.engine.cache.job.Task;
import com.sap.engine.cache.util.dump.LogUtil;

/**
 * @author petio-p
 *
 */
public class BackgroundExactImpl implements Background, Runnable {

  private final static String THREAD_NAME = "Background Jobs Internal Thread";

  // holds Task to TaskWrapper mapping
  private HashMap map;
  
  // holds TaskWrapper-s sorted according their nextCallTime
  private PriorityQueue queue;

  // background jobs work thread
  private Thread internalThread;
  
  // background integrity watcher instance
  private IntegrityWatcher integrityWatcher;

  // timeout manager work thread priority
  private int threadPriority = Thread.NORM_PRIORITY;
  private boolean work;

  // operation lock
  private Object lock;
  private Thread thread = null;

  // constructor
  public BackgroundExactImpl() {
    lock = new Object();
    map = new HashMap();
    queue = new PriorityQueue();
    integrityWatcher = new IntegrityWatcher(queue);
    work = true;
    thread = new Thread(this);
    thread.setDaemon(true);
    thread.start();
  }

  // stop timeout manager
  void stop() {
    synchronized(lock) {
      work = false;
      if (internalThread != null) {
        internalThread.interrupt();
      }
      integrityWatcher.stop();
      internalThread = null;
      integrityWatcher = null;
      map = null;
      queue = null;
    }
  }

  public void registerTask(Task task) {
    if (task == null) return;
    TaskWrapper wrapper;
    synchronized (lock) {
      if (map.containsKey(task)) throw new IllegalArgumentException("Task already registered");
      wrapper = new TaskWrapper(task, this);
      wrapper.nextCallTime = integrityWatcher.getCurrentTimeMillis() + wrapper.getInterval();
      wrapper.queuePosition = -1;
      map.put(task, wrapper);
      if (queue.add(wrapper)) {
        lock.notify();
      }
    }
  }
  
  public void unregisterTask(Task task) {
    if (task == null) return;
    TaskWrapper wrapper;
    synchronized (lock) {
      wrapper = (TaskWrapper) map.remove(task);
      if (wrapper != null) {
        if (wrapper.queuePosition >= 0) {
          if (queue.remove(wrapper)) {
            lock.notify();
          }
        } else {
          wrapper.queuePosition = -2;
        }
      }
    }
  }

  // main logic
  public void run() {
    internalThread = Thread.currentThread();
    int priority = internalThread.getPriority();
    String name = internalThread.getName();
    try {
      internalThread.setName(THREAD_NAME);
      internalThread.setPriority(threadPriority);
      TaskWrapper node = null;
      long delayTime = 0;
      while (work) {
        try {
          // wait while queue is empty
          synchronized (lock) {
            while (queue.isEmpty()) {
              lock.wait();
            }
          }
          long currentTime = integrityWatcher.getCurrentTimeMillis();
          // process all nodes with time <= current time
          while ((node = queue.getFirst()) != null && (delayTime = node.nextCallTime - currentTime) <= 0) {
            // call timeout() in thread
            synchronized (lock) {
              queue.removeFirst();
            }
            node.run();
          }
          if (node != null) {
            delayTime = (delayTime + 1) / 60000;
            delayTime = delayTime * 60000;
            if (delayTime == 0) {
              delayTime = 60000;
            }
            synchronized (lock) {
              lock.wait(delayTime);
            }
          }
        } catch (InterruptedException iException) {
          LogUtil.logTInfo(iException);
        }
      }
    } finally {
      internalThread.setPriority(priority);
      internalThread.setName(name);
    }
  }

  // this method is call from work thread when waitForTimeoutEvent=true
  final void processNode(TaskWrapper node) {
    synchronized (lock) {
      // notify lock if node is added as queue head
      if (processTimeoutNode(node)) {
        lock.notify();
      }
    }
  }

  private final boolean processTimeoutNode(TaskWrapper node) {
    boolean result = false;
    if (node.queuePosition <= -2) {
      return false;
    }
    if (node.repeatable()) {
      if (map.containsKey(node.aggregate)) {
        node.nextCallTime = integrityWatcher.getCurrentTimeMillis() + node.getInterval();
        result = queue.add(node);
      }
    } else {
      // automatic unregistration
      map.remove(node.aggregate);
    }
    return result;
  }

}
