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
 * Implemetation of abstract class PoolObject.
 * Throws an exception  if new instance cannot be created.<p>
 *
 * @author Nikola Arnaudov
 * @version 1.00
 */
public class PoolObjectWithCreatorExc extends PoolObjectExc {

  protected PoolInstanceCreatorExc creator;

  /**
   * Constructs empty pool without size limit. Capacity increment is 0 i.e. the capacity
   * of the pool is doubled each time it needs to grow.<p>
   *
   * @param   creator a PoolInstanceCreator for new instances.
   */
  public PoolObjectWithCreatorExc(PoolInstanceCreatorExc creator) {
    super();
    this.creator = creator;
  }

  /**
   * Constructs pool without size limit and specified initial size. Capacity increment is 0 i.e.
   * the capacity of the pool is doubled each time it needs to grow.<p>
   *
   * @param   creator a PoolInstanceCreator for new instances.
   * @param   initialSize  initial size of the pool.
   * @exception   Exception  if new instance cannot be created.
   */
  public PoolObjectWithCreatorExc(PoolInstanceCreatorExc creator, int initialSize) throws Exception {
    this(creator, initialSize, 0, 0);
  }

  /**
   * Constructs pool without size limit and specified initial size and capacity increment.
   *
   * @param   creator a PoolInstanceCreator for new instances.
   * @param   initialSize  initial size of the pool.
   * @param   capacityIncrement   the amount by which the capacity is increased when the pool overflows.
   * @exception   Exception  if new instance cannot be created.
   */
  public PoolObjectWithCreatorExc(PoolInstanceCreatorExc creator, int initialSize, int capacityIncrement) throws Exception {
    this(creator, initialSize, capacityIncrement, 0);
  }

  /**
   * Constructs pool with size limit and specified initial size and capacity increment.<p>
   *
   * @param   creator a PoolInstanceCreator for new instances.
   * @param   initialSize  initial size of the pool.
   * @param   capacityIncrement   the amount by which the capacity is increased when the pool overflows.
   * @param   limit  maximum size of the pool; if the value is not positive then there is no limit.
   * @exception   Exception  if new instance cannot be created.
   */
  public PoolObjectWithCreatorExc(PoolInstanceCreatorExc creator, int initialSize, int capacityIncrement, int limit) throws Exception {
    if (initialSize <= 0) {
      throw new IllegalArgumentException("Initial size must be positive!");
    }

    this.creator = creator;
    this.capacityIncrement = capacityIncrement;
    pool = new Object[initialSize];

    for (int i = 0; i < initialSize; i++) {
      pool[i] = newInstance();
    } 

    elementCount = initialSize;
    setLimit(limit);
  }

  /**
   * Creates a new object, that is an instance of PoolInstanceCreator.<p>
   *
   * @return   the new object.
   * @exception   Exception  if new instance cannot be created.
   */
  public Object newInstance() throws Exception {
    return creator.newInstance();
  }

}

