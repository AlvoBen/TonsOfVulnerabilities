/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.util;

import com.sap.engine.lib.util.base.ListPool;
import com.sap.engine.lib.util.base.NextItem;

/**
 * This is an extension of the <code>ListPool</code> class. This extension 
 * limits the total number of objects that can be allocated from the pool at 
 * a certain moment. 
 * 
 * <p>The limit of the total number of objects that can be allocated at a 
 * certain moment is determined by the <code>useLimit</code> argument in the 
 * constructor. </p>
 * 
 * <p>If the total number of allocated objects is eqaul or greater than  
 * <code>useLimit</code>, then the <code>getObject()</code> method blocks until 
 * an allocated object is released back in the pool. There is a 
 * <code>getObject(timeout)</code> method, which blocks only for a certain 
 * period of time. </p>
 * 
 * <p>This implementation is thread safe. </p> 
 * 
 * @see com.sap.engine.lib.util.base.ListPool
 * 
 * @author Kaloyan Raev
 * @version 6.30
 */
public abstract class LimitedWaitListPool extends ListPool {
  
  static final long serialVersionUID = 3248869433667146545L;
  protected int inUse;
  protected int useLimit;
  
  /**
   * Constructs empty pool without size limit and use limit.
   */
  public LimitedWaitListPool() {
    this(0, 0, 0);
  }
  
  /**
   * Constructs empty pool without size limit, but with specified use limit.
   * 
   * @param useLimit use limit of the pool. If the value is non positive - 
   *        there is no limit. 
   */
  public LimitedWaitListPool(int useLimit) {
    this(0, 0, useLimit);
  }
  
  /**
   * Constructs pool without size limit, but with specified initial size and
   * use limit.
   * 
   * @param initialSize initial size of the pool. If the value is 
   *                    non positive - the pool is created empty. 
   * @param useLimit    use limit of the pool. If the value is non positive - 
   *                    there is no limit. 
   */
  public LimitedWaitListPool(int initialSize, int useLimit) {
    this(initialSize, 0, useLimit);
  }
  
  /**
   * Constructs pool with specified initial size, size limit and use limit.
   * 
   * @param initialSize initial size of the pool. If the value is 
   *                    non positive - the pool is created empty. 
   * @param limit       size limit of the pool. If the value is non positive - 
   *                    there is no limit. 
   * @param useLimit    use limit of the pool. If the value is non positive - 
   *                    there is no limit. 
   */
  public LimitedWaitListPool(int initialSize, int limit, int useLimit) {
    super(initialSize, limit);
    this.useLimit = useLimit;
    inUse = 0;
  }
  
  /**
   * Gets an object from pool. 
   * 
   * <p>If pool is empty new object is created. </p>
   * 
   * <p>If the use limit is reached, then this method blocks until an already 
   * allocated object is released back to the pool or the calling thread is 
   * interrupted. </p>
   *
   * @return an object or <code>null</code> if the calling thread is 
   *         interrupted.
   */
  public synchronized NextItem getObject() {
    NextItem result = null;
    
    while ((result = removeFirstItem()) == null) {
      if (useLimit <= 0 || inUse < useLimit) {
        result = newInstance();
        break;
      } else {
        try {
          wait();
        } catch (InterruptedException e) {
          return null;
        }
      }
    }
    
    inUse++;
    
    return result;
  }
  
  /**
   * Gets an object from pool. 
   * 
   * <p>If pool is empty new object is created. </p>
   * 
   * <p>If the use limit is reached, then this method blocks until an already 
   * allocated object is released back to the pool or the calling thread is 
   * interrupted, or the specified timeout expires
   * expires. </p>
   *
   * @param timeout the maximum time (in milliseconds) the method to wait to 
   *                allocate an object. 
   *
   * @return an object or <code>null</code> if the calling thread is 
   *         interrupted or the specified timeout expires.
   */
  public synchronized Object getObject(long timeout) {
    Object result;

    long lastTime = System.currentTimeMillis();
    while ((result = removeFirstItem()) == null && 
           (timeout -= (System.currentTimeMillis() - lastTime)) > 0) {
      if (useLimit <= 0 || inUse < useLimit) {
        result = newInstance();
        break;
      } else {
        lastTime = System.currentTimeMillis();
        try {
          wait(timeout);
        } catch (InterruptedException e) {
          return null;
        }
      }
    }
    
    if (result != null) {
      inUse++;
    }

    return result;
  }
  
  /**
   * Returns object to pool.
   * 
   * <p>If there are any threads waiting on the <code>getObject()</code> 
   * methods, they are notified. </p>
   *
   * @param item an item for reuse.
   */
  public synchronized void releaseObject(NextItem item) {
    addFirstItem(item);
    inUse--;
    if (inUse == useLimit - 1) {
      notifyAll();
    }
  }
  
  /**
   * Returns the total number of objects currently allocated from this pool. 
   * 
   * @return the allocated objects count. 
   */
  public synchronized int itemsInUseCount() {
    return inUse;
  }
  
  /**
   * Returns <code>String</code> representation of this pool. 
   * 
   * <p>The returned <code>String</code> contains the values of the internal 
   * members of the pool. </p>
   * 
   * @return a <code>String</code>, representing the pool. 
   */
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    
    int _count;
    int _limit;
    int _inUse;
    int _useLimit;
    
    synchronized (this) {
      _count = count;
      _limit = limit;
      _inUse = inUse;
      _useLimit = useLimit;
    }
    
    buffer.append(getClass().getName());
    buffer.append("@");
    buffer.append(Integer.toHexString(hashCode()));
    buffer.append("[count=");
    buffer.append(_count);
    buffer.append(",limit=");
    buffer.append(_limit);
    buffer.append(",inUse=");
    buffer.append(_inUse);
    buffer.append(",useLimit=");
    buffer.append(_useLimit);
    buffer.append("]");
    
    return buffer.toString();
  }
  
}

