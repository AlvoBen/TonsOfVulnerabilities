package com.sap.engine.lib.util.concurrent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class SemaphoreControlledChannel implements BoundedChannel {

  protected final Semaphore putGuardSync;
  protected final Semaphore takeGuardSync;
  protected int capacityNumber;

  /**
   * Create a channel with the given capacity and default
   * semaphore implementation
   * @exception IllegalArgumentException if capacity less or equal to zero
   */
  public SemaphoreControlledChannel(int capacity) throws IllegalArgumentException {
    if (capacity <= 0) {
      throw new IllegalArgumentException();
    }
    capacityNumber = capacity;
    putGuardSync = new Semaphore(capacity);
    takeGuardSync = new Semaphore(0);
  }

  /**
   * Create a channel with the given capacity and
   * semaphore implementations instantiated from the supplied class
   * @exception IllegalArgumentException if capacity less or equal to zero.
   * @exception NoSuchMethodException If class does not have constructor
   * that intializes permits
   * @exception SecurityException if constructor information
   * not accessible
   * @exception InstantiationException if semaphore class is abstract
   * @exception IllegalAccessException if constructor cannot be called
   * @exception InvocationTargetException if semaphore constructor throws an
   * exception
   */
  public SemaphoreControlledChannel(int capacity, Class semaphoreClass) throws IllegalArgumentException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException {
    if (capacity <= 0) {
      throw new IllegalArgumentException();
    }
    capacityNumber = capacity;
    Class[] intarg = {Integer.TYPE};
    Constructor ctor = semaphoreClass.getDeclaredConstructor(intarg);
    Integer[] cap = {new Integer(capacity)};
    putGuardSync = (Semaphore) (ctor.newInstance(cap));
    Integer[] zero = {new Integer(0)};
    takeGuardSync = (Semaphore) (ctor.newInstance(zero));
  }

  public int capacity() {
    return capacityNumber;
  }

  /**
   * Return the number of elements in the buffer.
   * This is only a snapshot value, that may change
   * immediately after returning.
   */
  public int size() {
    return (int) (takeGuardSync.permits());
  }

  /**
   * Internal mechanics of put.
   */
  protected abstract void insert(Object x);

  /**
   * Internal mechanics of take.
   */
  protected abstract Object extract();

  public void put(Object x) throws InterruptedException {
    if (x == null) {
      throw new IllegalArgumentException();
    }
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    putGuardSync.acquire();
    try {
      insert(x);
      takeGuardSync.release();
    } catch (ClassCastException ex) {
      putGuardSync.release();
      throw ex;
    }
  }

  public boolean offer(Object x, long msecs) throws InterruptedException {
    if (x == null) {
      throw new IllegalArgumentException();
    }
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }

    if (!putGuardSync.attempt(msecs)) {
      return false;
    } else {
      try {
        insert(x);
        takeGuardSync.release();
        return true;
      } catch (ClassCastException ex) {
        putGuardSync.release();
        throw ex;
      }
    }
  }

  public Object take() throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    takeGuardSync.acquire();
    try {
      Object x = extract();
      putGuardSync.release();
      return x;
    } catch (ClassCastException ex) {
      takeGuardSync.release();
      throw ex;
    }
  }

  public Object poll(long msecs) throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }

    if (!takeGuardSync.attempt(msecs)) {
      return null;
    } else {
      try {
        Object x = extract();
        putGuardSync.release();
        return x;
      } catch (ClassCastException ex) {
        takeGuardSync.release();
        throw ex;
      }
    }
  }

}

