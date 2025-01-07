package com.sap.engine.services.iiop.client.pool;

import com.sap.engine.services.iiop.logging.LoggerConfigurator;

/**
 * This class represents a thread pool.
 *
 * @author Nikolai Neichev
 * @version 4.0
 */
public class ThreadPool {
    // pointer to the first available thread in the pool
  private WorkerNode first = null;
    // pointer to the last available thread in the pool
  private WorkerNode last = null;

  /**
   * Constructor
   * @param initSize The initial pool size
   */
  public ThreadPool(int initSize) {
    first = new WorkerNode(this);
    last = first;
    for (int i = 1; i < initSize; i++) {
      returnInPool(new WorkerNode(this));
    }
  }

  /**
   * Starts a new work. Uses threads from pool, if not available, takes action according to the block value setting
   * @param runnable The runnable to be executed
   * @param block If true waits until there is available thread.
   */
  public synchronized void startWork(Runnable runnable, boolean block) {
    WorkerNode worker = getFromPool(block);
    worker.setWork(runnable);
  }

  /**
   * Starts a new work. Uses threads from pool, if not available, starts a raw thread
   * @param runnable The runnable to be executed
   */
  public synchronized void startWork_Raw(Runnable runnable) {
    WorkerNode worker = getFromPool(false);
    if (worker != null) {
      worker.setWork(runnable);
    } else {
      (new Thread(runnable)).start();
    }
  }

  /**
   * Gets the first available worker from pool
   * @param block if true - blocks until there is an available worker
   * @return an available worker
   */
  public synchronized WorkerNode getFromPool(boolean block) {
    WorkerNode availableWorker = first;
    if (first != null) {
      first = first.getNext();
      if (first == null) {
        last = null;
      }
      return availableWorker;
    } else if (block) {
      while (true) {
          try {
            this.wait();
          } catch (InterruptedException ie) {
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("ThreadPool.getFromPool(boolean)", LoggerConfigurator.exceptionTrace(ie));
            }
          }
          availableWorker = getFromPool(false);
          if (availableWorker != null) {
            return availableWorker;
          }
      }
    } else {
      return availableWorker;
    }
  }

  /**
   * Return a worker in pool
   * @param worker the returned worker
   */
  public synchronized void returnInPool(WorkerNode worker) {
    if (last != null) {
      last.setNext(worker);
      last = worker;
    } else {
      first = worker;
      last = worker;
    }
    this.notifyAll();
  }

  public void finish() {
    WorkerNode worker = first;
    while (worker != null) {
      synchronized (worker) {
        worker.finish();
        worker.setWork(null);
        if (worker.equals(last)) {
          return;
        }
        worker = worker.getNext();
      }
    }
  }

}
