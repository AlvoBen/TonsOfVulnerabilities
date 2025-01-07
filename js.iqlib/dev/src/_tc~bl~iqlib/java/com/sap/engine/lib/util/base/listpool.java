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
package com.sap.engine.lib.util.base;

/**
 * List Pool.
 * A very effective object pool. Without resize, waste time and asynchronous
 * new instance creation.<p>
 *
 * This class can be used only with objects that implement com.sap.engine.lib.util.base.NextItem
 * interface or objects that extend com.sap.engine.lib.util.base.NextItemAdapter class.<p>
 *
 * To use this class you have to extend it and implement newInstance() method from
 * com.sap.engine.lib.util.base.ListPoolInstanceCreator interface.<p>
 *
 * WARNING: It is not recommended to call methods from super class BaseLinkedList unless you
 *          take care for synchronization.<p>
 *
 * <b>Note</b>: this class is more effective than com.sap.engine.lib.util.PoolObject.<p>
 *
 *
 * @author Nikola Arnaudov
 * @version 1.00
 */
public abstract class ListPool extends BaseLinkedList implements ListPoolInstanceCreator {

  static final long serialVersionUID = 4609674355706065300L;
  /**
   * Constructs empty pool without size limit.<p>
   *
   */
  public ListPool() {

  }

  /**
   * Constructs pool without size limit and specified initial size.<p>
   *
   * @param  initialSize initial size of the pool.
   */
  public ListPool(int initialSize) {
    this(initialSize, 0);
  }

  /**
   * Constructs pool with size limit and specified initial size.<p>
   *
   * @param   limit max size of pool. If value is not positive then there is no limit.
   * @param  initialSize initial size of the pool.
   */
  public ListPool(int initialSize, int limit) {
    super(limit);
    for (int i = 0; i < initialSize; i++) {
      addFirstItem(newInstance());
    } 
  }

  /**
   * Gets an object from pool. If pool is empty new object is created.<p>
   *
   * @return an object.
   */
  public NextItem getObject() {
    NextItem temp;
    synchronized (this) {
      temp = removeFirstItem();
    }
    return (temp == null) ? newInstance() : temp;
  }

  /**
   * Returns object to pool.<p>
   *
   * @param  item an item for reuse.
   */
  public synchronized void releaseObject(NextItem item) {
    addFirstItem(item);
  }

  /**
   * Releases all objects from pool.<p>
   */
  public synchronized void freeMemory() {
    clear();
  }

  /**
   * Releases some objects from pool.<p>
   *
   * @param  count maximum objects that will remain in pool.<p>
   */
  public synchronized void freeMemory(int count) {
    removeSublist(count);
  }

  /**
   * Sets limit of pool.
   * If you set limit that is lower than pool size, the pool will not be truncated,
   * to do that you must call freeMemory(limit).<p>
   *
   * @param   limit maximum objects that can be stored in pool.
   */
  public synchronized void setLimit(int limit) {
    super.setLimit(limit);
  }

}

