/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.internal.util;


import com.sap.engine.services.iiop.logging.LoggerConfigurator;

/**
 *  Produces reusable IDs. Every ID is represented by <code>IDFactoryItem</code>.
 *  The maximum number of IDs that can be produced is calculated by the formula
 *  <code>maxIDs = offset + (maxUnits * unitSize)</code>. First ID value is <code>offset</code>
 *  and the last is <code>maxIDs-1</code>. The list is resized upwards
 *  when the number of used IDs in the last unit has reached the
 *  <code>increase_threshold</code> value, unless the maximum number of allowed units
 *  is reached. If so the requested process sleeps until an ID is released. The size
 *  of the list is decreased if the last unit has no items used and the prior unit usage
 *  is below <code>decrease_threshold </code> value.
 *
 *  @author Vladimir Velinov
 *  @ ver 4.0
 */
public class IDFactory {

  public static final int DEFAULT_LIST_SIZE = 500; //number of items in single unit
  public static final int DEFAULT_MAX_UNITS = 3; //max number of units allowed
  private static final IDFactoryItem nullItem = new IDFactoryItem(-1);
  private IDFactoryUnit list = null; //references first unit
  private IDFactoryUnit notifyAvailUnit = null;
  private int maxUnits = 0; //maximum number of units allowed
  private int unitSize = 0; //number of IDFactoryItem in single unit
  private int count = 0; //number of used items(IDs), itrs sum of all units' count
  private int offset = 0; //value of the first ID
  /**
   *  to create new unit
   *  1. the <code>count</code> field of the last unit has to be above
   *     <code>increase_threshold</code>
   *  2. the number of units is less than maxUnits
   *  3. units prior to the last are full
   */
  private int increase_threshold = 0;
  /**
   *  to delete last unit
   * 1. the last unit has to be empty
   * 2. the previous unit <code>count</code> field has to be below
   *   <code>decrease_threshold</code>
   */
  private int decrease_threshold = 0;
  /**
   *  used to organize ID request queue, if max number of units reached
   * requestID() makes the requesting thread to wait, while
   * releaseID() notifies and releases one of the waiting threads
   */
//  private Object requestQueue = new Object();

  /**
   *  Default constructor, initializes the factory with <code>DEFAULT_LIST_SIZE</code>
   *  and <code>DEFAULT_MAX_UNITS</code>.
   */
  public IDFactory() {
    this(DEFAULT_LIST_SIZE, DEFAULT_MAX_UNITS, 0);
  }

  /**
   *  Created new <code>IDFactory</code> with user defined unit's parameters.
   *
   *  @param size unit's list size
   *  @param offset the value of the first ID returned
   *  @param max_units maximim number of units allowed when resizing upwards
   */
  public IDFactory(int size, int max_units, int offset) {
    this.unitSize = size;
    this.maxUnits = max_units;
    this.offset = offset;
    list = new IDFactoryUnit(size, 0);
    increase_threshold = (int) (unitSize * 0.75);
    decrease_threshold = (int) (unitSize * 0.25);
  }

  public void resize(int newSize, int newMaxUnits) throws IllegalArgumentException {
    if (newSize <= 0 || newMaxUnits <= 0) {
      String messageWithId = "ID019105: IDFactory::resize() - new value is negative ";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IDFactory.resize(int, int)", messageWithId + " (" + newSize + "/" + newMaxUnits + ") is negative.");
      }
      throw new IllegalArgumentException(messageWithId);
    }

    if (count > newSize * newMaxUnits) {
      String messageWithId = "ID019106: IDFactory::resize() - the requested size is too small ";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IDFactory.resize(int, int)", messageWithId);
      }
      throw new IllegalArgumentException(messageWithId);
    }

    list = list.resize(list, newSize);
  }

  /**
   *  Produces ID. if no IDs available then the requesting thread
   *  will sleep, until an ID is released by <code>disposeID()</code>.
   *  The greatest value returned will be <code>(offset + (maxUnits * unitSize)) - 1</code>.
   *
   *@return requested ID; the calling process will sleep if no ID are available
   */
  public synchronized int requestID() {
    IDFactoryUnit lastUnit = list;
    IDFactoryUnit loopUnit = list;
    IDFactoryUnit availUnit = null;

    //go through all the units and remember the position of the first  free item
    do {
      if (lastUnit.getFirstAvail() >= 0 && availUnit == null) {
        availUnit = lastUnit;
      }

      loopUnit = loopUnit.getNextUnit();

      if (loopUnit != null) {
        lastUnit = loopUnit;
      }
    } while (loopUnit != null);

    if (((lastUnit.getCount() > increase_threshold) || (availUnit == null)) &&
        (lastUnit.getID() < maxUnits - 1)) {
      lastUnit.setNextUnit(new IDFactoryUnit(unitSize, lastUnit.getID() + 1));
      lastUnit = lastUnit.getNextUnit();

      //all previous units are used, change pointer to the new one
      if (availUnit == null) {
        availUnit = lastUnit;
      }
    }

    while (availUnit == null) {
      //if max number of units reached, ID request will wait until an ID becomes available
      try {
        this.wait();
        availUnit = notifyAvailUnit;

        if (availUnit.getFirstAvail() < 0) {
          // this if clause is because a case:
          // some thread has been faster and got a free key (entered into this method)
          // before waiting one had been notified
          availUnit = null;
        }
      } catch (Exception e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("IDFactory.requestID()", LoggerConfigurator.exceptionTrace(e));
        }
      }
    }

    int id = availUnit.getNextAvailableItemID();
    count++;

    int _id = offset + id + (unitSize * availUnit.getID()); //calculate the real ID
    return _id;
  }

  /**
   *  Returns unit specified by given <code>id</code>.
   *  @param  id that is hold in the requested unit
   *  @exception IndexOutOfBoundsException if <code>id<code> is negative; if <code>id<code>
   *  exceeds the number of created units or the number of allowed units
   */
  private IDFactoryUnit getUnit(int id) throws IndexOutOfBoundsException {
    if (id < 0) { //negative index
      String messageWithId = "ID019107: IDFactory::getUnit() - requested ID is negative ";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IDFactory.getUnit(int)", messageWithId);
      }
      throw new IndexOutOfBoundsException(messageWithId);
    }

    int currUnit = (id - offset) / unitSize;

    if (id > 0 && (currUnit > maxUnits)) { //the ID is over the allowed maximum number of units
      String messageWithId = "ID019108: IDFactory::getUnit() - requested ID is out of bounds ";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IDFactory.getUnit(int)", messageWithId);
      }
      throw new IndexOutOfBoundsException(messageWithId);
    }

    IDFactoryUnit unit = list;

    while (unit.getNextUnit() != null && currUnit > 0) {
      unit = unit.getNextUnit();
      currUnit--;
    }

    if (currUnit > 0) {
      String messageWithId = "ID019109: IDFactory::getUnit() - unit does not exist ";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IDFactory.getUnit(int)", messageWithId);
      }
      throw new IndexOutOfBoundsException(messageWithId);
    }

    return unit;
  }

  /**
   *  Gets the item's ID in respect to the beginning of  it's unit.
   *
   *  @param id the real ID
   *  @return the id index respective to the beginning of the unit
   */
  private int getItem(int id) {
    return (id - offset) % unitSize;
  }

  /**
   * Disposes given <code>id</code> by placing it on the top of the free item's list.
   * If unit list has to be decreased it's checked if teh last unit has no
   *  used items and the prior unit usage has dropped below the
   *  <code>decrease_threshold</code> level.
   *
   *@param id the item that has to be marked as available
   *@exception IllegalArgumentException if id has already been released or
   *  available
   */
  public synchronized void disposeID(int id) throws IllegalArgumentException {
    if (id == -1) {
      return;
    }

    IDFactoryUnit unit = getUnit(id);
    int itemID = getItem(id);

    unit.releaseItemID(itemID);
    count--;

    //check if the last unit has to be removed
    IDFactoryUnit prev = list;
    IDFactoryUnit next = list.getNextUnit();

    while (next != null) {
      prev = next;
      next = next.getNextUnit();
    }

    //if last unit is empty and the previuos unit is below decrease_threshold value
    //delete the last unit
    if ((prev.getCount() < decrease_threshold) && (next != null) && (next.getCount() == 0)) {
      prev.setNextUnit(null); //delete the last unit
    }

    //if there are threads waiting for IDs, notify them that there is one free item
    notifyAvailUnit = unit;
    this.notify();
  }


  public IDFactoryItem get(int id) {
    if (id == -1) {
      return IDFactory.nullItem;
    }
    IDFactoryUnit unit = getUnit(id);
    IDFactoryItem item = unit.get(getItem(id));

    if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beInfo()) {
      LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).infoT("IDFactory.get(int)", "ID: "+id);
    }

    if (item.getNext() != IDFactoryItem.USED_ITEM) {
      String messageWithId = "ID019112: IDFactory::get - item was not requested before this operation ";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IDFactory.get(int)", messageWithId);
      }
      throw new IllegalArgumentException(messageWithId);
    }

    return item;
  }

  //TODO not used
  public void disposeAll() {
    for (int i = 0; i < unitSize * maxUnits; i++) {
      try {
        disposeID(i);
      } catch (Exception e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IDFactory.disposeAll()", LoggerConfigurator.exceptionTrace(e));
        }
      }
    }
  }

}

