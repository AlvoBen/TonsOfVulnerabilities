package com.sap.engine.lib.util.concurrent;

import java.lang.reflect.InvocationTargetException;

public class Slot extends SemaphoreControlledChannel {

  /**
   * Create a buffer with the given capacity, using
   * the supplied Semaphore class for semaphores.
   * @exception NoSuchMethodException If class does not have constructor
   * that intializes permits
   * @exception SecurityException if constructor information
   * not accessible
   * @exception InstantiationException if semaphore class is abstract
   * @exception IllegalAccessException if constructor cannot be called
   * @exception InvocationTargetException if semaphore constructor throws an
   * exception
   */
  public Slot(Class semaphoreClass) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException {
    super(1, semaphoreClass);
  }

  /**
   * Create a new Slot using default Semaphore implementations
   */
  public Slot() {
    super(1);
  }

  /** The slot  */
  protected Object itemHolder = null;

  /** Set the item in preparation for a take  */
  protected synchronized void insert(Object x) {
    itemHolder = x;
  }

  /** Take item known to exist  */
  protected synchronized Object extract() {
    Object x = itemHolder;
    itemHolder = null;
    return x;
  }

  public synchronized Object peek() {
    return itemHolder;
  }

}

