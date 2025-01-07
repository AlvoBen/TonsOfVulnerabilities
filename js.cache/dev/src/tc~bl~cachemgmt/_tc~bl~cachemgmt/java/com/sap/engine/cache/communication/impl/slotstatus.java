package com.sap.engine.cache.communication.impl;

import com.sap.engine.cache.communication.NotificationMessage;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Petev, Petio, i024139
 */
public class SlotStatus {

  public static final byte STATUS_UNDEFINED = -1;
  public static final byte STATUS_FREE = 0;
  public static final byte STATUS_IN_BLOCK = 1;

  private HashMap threadToQueue = null;

  public SlotStatus() {
    threadToQueue = new HashMap();
  }

  public synchronized LinkedList getQueue() {
    Thread thread = Thread.currentThread();
    LinkedList queue = (LinkedList) threadToQueue.get(thread);
    return queue;
  }

  public synchronized byte getStatus() {
    Thread thread = Thread.currentThread();
    if (threadToQueue.get(thread) == null) {
      return STATUS_FREE;
    } else {
      return STATUS_IN_BLOCK;
    }
  }

  public synchronized void beginBlock() {
    Thread thread = Thread.currentThread();
    LinkedList queue = (LinkedList) threadToQueue.get(thread);
    if (queue == null) {
      queue = new LinkedList();
      threadToQueue.put(thread, queue);
    }
  }

  public synchronized void endBlock() {
    Thread thread = Thread.currentThread();
    threadToQueue.remove(thread);
  }

  public synchronized void addMessage(NotificationMessage message) {
    Thread thread = Thread.currentThread();
    LinkedList queue = (LinkedList) threadToQueue.get(thread);
    queue.addLast(message);
  }


}
