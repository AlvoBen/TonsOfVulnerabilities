/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.util;

/**
 * A standart object pool. Pooled objects are stored in Object array.
 * There is no pool that uses Class.newInstance() method because this
 * method is slower.Throws an exception  if new instance cannot be created.<p>
 *
 * <b>Note</b>: If it is possible it is better to use com.sap.engine.lib.util.base.ListPool.<p>
 *
 *
 * @author Nikola Arnaudov
 * @version 1.0
 */
public abstract class PoolObjectExc implements PoolInstanceCreatorExc {

  /**
   * The amount by which the capacity of the pool is automatically incremented when its
   * size becomes greater than its capacity. If the capacity increment is 0, the capacity
   * of the pool is doubled each time it needs to grow.<p>
   */
  protected int capacityIncrement;
  /**
   * Upper limit of list size.<p>
   */
  protected int limit;
  /**
   * Number ot objects stored in pool.<p>
   */
  protected int elementCount;
  /**
   * The array buffer into which the components of the poole are stored.<p>
   */
  protected Object[] pool;

  /**
   * Constructs empty pool without size limit. And capacity increment is 0 i.e. the capacity
   * of the pool is doubled each time it needs to grow.<p>
   *
   */
  public PoolObjectExc() {
    pool = new Object[1];
    setLimit(0);
  }

  /**
   * Constructs pool without size limit and specified initial size. Capacity increment is 0 i.e.
   * the capacity of the pool is doubled each time it needs to grow.<p>
   *
   * @param   initialSize  initial size of the pool.
   * @exception   Exception  if new instance cannot be created.
   */
  public PoolObjectExc(int initialSize) throws Exception {
    this(initialSize, 0, 0);
  }

  /**
   * Constructs pool without size limit and specified initial size and capacity increment.<p>
   *
   * @param   initialSize  initial size of pool.
   * @param   capacityIncrement   the amount by which the capacity is increased when the pool overflows.
   * @exception   Exception  if new instance cannot be created.
   */
  public PoolObjectExc(int initialSize, int capacityIncrement) throws Exception {
    this(initialSize, capacityIncrement, 0);
  }

  /**
   * Constructs pool with size limit and specified initial size and capacity increment.<p>
   *
   * @param   initialSize  initial size of pool.
   * @param   capacityIncrement   the amount by which the capacity is increased when the pool overflows.
   * @param   limit  maximum size of the pool; if the value is not positive then there is no limit.
   * @exception   Exception  if new instance cannot be created.
   */
  public PoolObjectExc(int initialSize, int capacityIncrement, int limit) throws Exception {
    if (initialSize <= 0) {
      throw new IllegalArgumentException("Initial size must be positive!");
    }

    this.capacityIncrement = capacityIncrement;
    pool = new Object[initialSize];

    for (int i = 0; i < initialSize; i++) {
      pool[i] = newInstance();
    } 

    elementCount = initialSize;
    setLimit(limit);
  }

  /**
   * Sets the size limit of the pool.
   * If the limit is smaller than current size of the pool, the pool is not truncated.
   * Just you can not add more elements.<p>
   *
   * @param   lim  the new size limit.
   */
  public synchronized void setLimit(int lim) {
    limit = lim <= 0 ? Integer.MAX_VALUE : lim;

    if (limit < pool.length) {
      Object[] oldPool = pool;
      pool = new Object[limit];

      if (elementCount > limit) {
        elementCount = limit;
      }

      System.arraycopy(oldPool, 0, pool, 0, elementCount);
    }
  }

  /**
   * Gets object from pool; if pool is empty new object is created.<p>
   *
   * @return an object from this pool.
   * @exception   Exception  if new instance cannot be created.
   */
  public Object getObject() throws Exception {
    Object temp = null;
    synchronized (this) {
      if (elementCount != 0) {
        temp = pool[--elementCount];
        pool[elementCount] = null;
      }
    }
    return (temp == null) ? newInstance() : temp;
  }

  /**
   * Returns object to pool.<p>
   *
   * @param   item  the objcet for reuse.
   */
  public synchronized void releaseObject(Object item) {
    if (elementCount < limit) {
      if (elementCount == pool.length) {
        int newCapacity = (capacityIncrement > 0) ? (pool.length + capacityIncrement) : (pool.length << 1);

        if (newCapacity > limit) {
          newCapacity = limit;
        }

        Object[] oldPool = pool;
        pool = new Object[newCapacity];
        System.arraycopy(oldPool, 0, pool, 0, elementCount);
      }

      pool[elementCount++] = item;
    }
  }

  /**
   * Releases all objects from pool.<p>
   *
   */
  public synchronized void freeMemory() {
    pool = new Object[1];
    elementCount = 0;
  }

  /**
   * Releases some objects from pool.<p>
   *
   * @param   count  maximum objects that will remain in the pool.
   */
  public synchronized void freeMemory(int count) {
    if (count <= 0) {
      count = 1;
    }

    if (pool.length > count) {
      Object[] oldPool = pool;
      pool = new Object[count];

      if (elementCount > count) {
        elementCount = count;
      }

      System.arraycopy(oldPool, 0, pool, 0, elementCount);
    }
  }

  /**
   * Returns a string representation of the pool.<p>
   *
   * @return     a string representation of the pool.
   */
  public synchronized String toString() {
    return "Pool: Objects = " + elementCount + " Capacity = " + pool.length + " Limit = " + limit + " Capacity increment = " + capacityIncrement;
  }

}

