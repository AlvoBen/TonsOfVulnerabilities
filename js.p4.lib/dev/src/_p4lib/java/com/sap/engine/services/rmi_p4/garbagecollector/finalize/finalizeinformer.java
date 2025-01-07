package com.sap.engine.services.rmi_p4.garbagecollector.finalize;

import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.interfaces.cross.Connection;

import java.util.Vector;


/**
 * Copyright: Copyright (c) 2003
 * Company: SAP AG - Sap Labs Bulgaria
 *
 * @author Ivan Atanassov
 * @version 7.1
 */
public class FinalizeInformer implements Runnable {

  //cyclic list
  private FinalizeMessage header;  //first not empty node
  private FinalizeMessage tail;    //first empty
  private FinalizeMessanger[] messangersPool;
  private Thread timeoutThread;

  private int queueSize;
  private int waitTimeout;

  private boolean isClosed = false;

  public FinalizeInformer(int initialCacheSize, int threadsSize, int waitTimeout) {  //real size is queueSize + 1
    this.header = new FinalizeMessage();
    FinalizeMessage temp = header;
    for(int i = 0; i < initialCacheSize; i++) {
      temp.next = new FinalizeMessage();
      temp.next.previous = temp;
      temp = temp.next;
    }
    temp.next = this.header;
    this.header.previous = temp;
    this.tail = header;

    this.queueSize = initialCacheSize + 1;
    this.waitTimeout = waitTimeout;

    this.messangersPool = new FinalizeMessanger[threadsSize];
    for (int i = 0; i < messangersPool.length; i++) {
      this.messangersPool[i] = new FinalizeMessanger(this, waitTimeout);
    }

    this.timeoutThread = new Thread(this);
    this.timeoutThread.setDaemon(true);
    this.timeoutThread.start();
  }

  public synchronized void setWork(Connection connection, byte[] message, String className) {
    if (isClosed) {
      return;
    }
    boolean toNotify  = header.isEmpty();

    tail.setMessage(connection, message, className);
    tail = tail.next;

    if (!tail.isEmpty()) {  //insert new blank node
      FinalizeMessage temp = new FinalizeMessage();
      temp.next = tail;
      temp.previous = tail.previous;
      tail.previous.next = temp;
      tail.previous = temp;

      tail = temp;
      queueSize++;
    }

    if (toNotify) {
      this.notifyAll();
    }
  }

  void doWork(FinalizeMessanger messanger) {
    Connection connection = null;
    byte[] message = null;
    String className = null;
    synchronized (this) {
      try {
        while (header.isEmpty() && (!isClosed) && ((ThreadWrapper) Thread.currentThread()).isRunning()) {
          messanger.isWaitingMessage = true;
          try {
            this.wait(0);
          } catch (InterruptedException e) {
            //$JL-EXC$
          }
        }
      } finally {
        if (!isClosed) {
          connection = header.getConnection();
          message = header.getMessage();
          className = header.getClassName();

          header = header.next;
          messanger.isWaitingMessage = false;
        }
      }
    }

    if (isClosed) {
      return;
    }

    try {
      messanger.setTimeoutStartTime(System.currentTimeMillis());

      connection.sendRequest(message, message.length, null);
      if (P4Logger.getLocation().bePath()) {
        P4Logger.getLocation().pathT("garbagecollector.finalize.FinalizeInformer.p4_finalize()", "Finalyzed " + className +". Inform message has been sent");
      }
    } catch (Exception ex) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("garbagecollector.finalize.FinalizeInformer.p4_finalize()", "Cannot send inform message about finalization of " + className + " \r\n" + P4Logger.exceptionTrace(ex));
      }
    } catch (ThreadDeath tr) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("garbagecollector.finalize.FinalizeInformer.p4_finalize()", "Cannot send inform message about finalization of " + className + ". Timeout expired. Maybe destination is hanged");
      }
      throw tr;
    }
  }

  public void run() {
    while (!isClosed) {
      for (int i = 0; i < messangersPool.length; i++) {
        if (messangersPool[i].isExpiredTimeout()) {
          synchronized(this) {       //TODO e.. li sym go kakvo da e
            if (messangersPool[i].isExpiredTimeout() && !messangersPool[i].isWaitingMessage) {  //TODO e.. li sym go kakvo da e
              messangersPool[i].startInNewThread();
            }
          }
        }
      }

      try {
        Thread.sleep(waitTimeout);
      } catch (InterruptedException e) {
        //$JL-EXC$
      }
    }
  }

  public synchronized boolean isEmptyFinalizeQueue() {
    return header.isEmpty();
  }

 public synchronized int queueSize() {
    return queueSize;
  }

  public synchronized boolean areFreeMessagers() {
    for (FinalizeMessanger messanger : messangersPool) {
      if (!messanger.isWaitingMessage) {
        return false;
      }
    }
    return true;
  }

  public void close() {
    isClosed = true;
    for (int i = 0; i < messangersPool.length; i++) {
      messangersPool[i].stop();
    }
    synchronized(this) {
      this.notifyAll();
    }
    try {
      Thread.sleep(3000);   //wait threads to finish alone
    } catch (InterruptedException e) {
      //$JL-EXC$
    }
    for (int i = 0; i < messangersPool.length; i++) {
      messangersPool[i].stop();
      messangersPool[i] = null;
    }
    timeoutThread.interrupt();
    timeoutThread = null;
    messangersPool = null;
    header = null;
    tail = null;
  }

  public synchronized String printCheck() {
    StringBuffer resultString = new StringBuffer("[FinalizeInformer] > Task queue has size                    : ").append(queueSize);
    resultString.append("\n[FinalizeInformer] > Is the task queue empty?               : ").append(isEmptyFinalizeQueue());
    resultString.append("\n[FinalizeInformer] > Are finalize messangers free of tasks? : ").append(areFreeMessagers());
    FinalizeMessage temp = header;
    Vector<FinalizeMessage> vector = new Vector<FinalizeMessage>();
    for (int i = 0; i < queueSize; i++) {
      if (!temp.isEmpty()) {
        resultString.append("\n[FinalizeInformer] >  element ").append(temp.toString()).append(" is not empty on position : ").append(i);
      }
      if (vector.contains(temp)) {
        resultString.append("\n[FinalizeInformer] >  element makes chain in queue on position : ").append(i);
      }

      if (temp == tail) {
        resultString.append("\n[FinalizeInformer] >  tail element founds on position          : ").append(i);
      }

      vector.add(temp);
      temp = temp.next;
    }

    if ((temp != header) || (temp.next != header)) {
      resultString.append("\n[FinalizeInformer] >  It is not a Cycle");
    }

    return resultString.toString();
  }

}
