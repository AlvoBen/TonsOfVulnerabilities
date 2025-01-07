package com.sap.engine.services.iiop.client.pool;

import com.sap.engine.services.iiop.logging.LoggerConfigurator;

/**
 * This class represents a thread from the thread pool
 *
 * @author Nikolai Neichev
 * @version 4.0*
 */
public class WorkerNode implements Runnable {

    // the node's work
  Runnable work = null;
    // the thread pool
  ThreadPool thePool = null;
    // the next available worker, null if no such
  WorkerNode nextAvailable = null;

  boolean finish = false;

  /**
   * Constructor.
   * @param thePool The thread pool
   */
  public WorkerNode(ThreadPool thePool) {
    this.thePool = thePool;
    Thread thisThread = new Thread(this);
    thisThread.setDaemon(true);
    thisThread.start();
  }

  /**
   * Setter method
   * @param next - next available worker
   */
  public void setNext(WorkerNode next) {
    this.nextAvailable = next;
  }

  /**
   * Getter method
   * @return next available worker
   */
  public WorkerNode getNext() {
    return nextAvailable;
  }

  /**
   * Sets work to the node.
   * @param work the work
   */
  public void setWork(Runnable work) {
    this.work = work;
    synchronized (this) {
      this.notify();
    }
  }

  /**
   * run() implementation
   */
  public synchronized void run() {
    while (true) {
      try {
        this.wait();
        if (finish) {
          return;
        }
      } catch (InterruptedException ie) {
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("WorkerNode.run()", LoggerConfigurator.exceptionTrace(ie));
            }
      }
      work.run();
      thePool.returnInPool(this);
    }
  }

  public void finish() {
    finish = true;
  }

}
