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
 *  Used by <code>IDFactory</code> to manipulate an <code>IDFactoryItem</code>
 *  list.
 *  @author Vladimir Velinov
 *  @version 4.0
 */
public class IDFactoryUnit {

  /**  array of <code>IDFactoryItem</code> entries, with list like logical organization */
  private IDFactoryItem[] list = null;
  /**  points the next unit, null if this is the last one */
  private IDFactoryUnit nextUnit = null;
  /**  number of used item in this unit  */
  private int count = 0;
  /**  points the first available item in the unit, value of <code>IDFactroyItem.LAST_ITEM</code>
   if no entries are available  */
  private int firstAvail = 0;
  /**  unit ID in the <code>IDFactory</code> list , the first unit has ID=0  */
  private int ID = 0;

  /**
   *  Creates new list with length of <code>size</code> and <code>ID</code>.
   *  Initalizes all items' pointers.
   *  @param size the size of the list
   *  @param ID unit ID in <code>IDFactory</code> list; ID = last_unit.ID + 1
   */
  protected IDFactoryUnit(int size, int ID) {
    list = new IDFactoryItem[size];

    for (int i = 0; i < list.length; i++) { //initialize the list
      list[i] = new IDFactoryItem(i + 1);
    } 

    //indicates that's the last free item in the list
    list[list.length - 1].setNext(IDFactoryItem.LAST_ITEM);
    this.ID = ID;
  }

  /**
   *  Returns <code>IDFactoryItem</code> object with given ID.
   *  @param index the ID of requested item
   *  @return requested object
   *  @exception IndexOutOfBoundsException if <code>index</code> < 0;if <code>index</code> > <code>list.length</code>
   */
  protected IDFactoryItem get(int index) throws IndexOutOfBoundsException {
    if (index < 0) { //negative index
      String messageWithId = "ID019113: IDFactoryUnit::get() - requested index is negative ";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("IDFactoryUnit.get(int)", messageWithId);
      }
      throw new IndexOutOfBoundsException(messageWithId);
    }

    if (index > list.length) { //the ID is out of bounds
      String messageWithId = "ID019114: IDFactoryUnit::get() - requested index is out of bounds ";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("IDFactoryUnit.get(int)", messageWithId);
      }
      throw new IndexOutOfBoundsException(messageWithId);
    }

    return list[index];
  }

  /**
   *  Returns index of first available item in unit's list.
   *  @return <code>IDFactoryItem.LAST_ITEM</code> if there are no available
   *  items for the unit
   */
  protected int getFirstAvail() {
    return firstAvail;
  }

  public synchronized int getNextAvailableItemID() {
    if (firstAvail == IDFactoryItem.USED_ITEM) {
      String messageWithId = "ID019110: IDFactoryUnit::getNextAvailableItemID - all items have been gotten ";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IDFactoryUnit.getNextAvailableItemID()", messageWithId);
      }
      throw new IllegalArgumentException(messageWithId);
    }

    int id = firstAvail;
    IDFactoryItem item = list[firstAvail];
    firstAvail = item.getNext();

    item.setNext(IDFactoryItem.USED_ITEM);
    incCount();

    return id;
  }

  public synchronized void releaseItemID(int id) {
    IDFactoryItem item = list[id];

    if (item.getNext() != IDFactoryItem.USED_ITEM) {
      String messageWithId = "ID019110: IDFactoryUnit::releaseItemID - item is already disposed ";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IDFactoryUnit.releaseItemID(int)", messageWithId);
      }
      throw new IllegalArgumentException(messageWithId);
    }

    item.setMessage(null);
    item.setNext(firstAvail);
    decCount();

    firstAvail = id;
  }


  /**
   *  Increments by one the number of used items.
   *  @exception IndexOutOfBoundsException if <code>count</code> grows above <code>list.length</code>
   */
  protected void incCount() {
    if (count == list.length) {
      String messageWithId = "ID019115: Count overflow";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("IDFactoryUnit.incCount()", messageWithId);
      }
      throw new IndexOutOfBoundsException(messageWithId);
    }

    count++;
  }

  /**
   *  Decrease by one the number of used items.
   *  @exception IndexOutOfBoundsException if <code>count</code> = 0 before call was made
   */
  protected void decCount() {
    if (count == 0) {
      String messageWithId = "ID019116: Count is zero already";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("IDFactoryUnit.decCount()", messageWithId);
      }
      throw new IndexOutOfBoundsException(messageWithId);
    }
    count--;
  }

  /**
   *  Number of used items.
   *  @return number of used items in this unit, 0 indicates that the unit is empty
   */
  protected int getCount() {
    return count;
  }

  /**
   *  Unit <code>ID</code> in the <code>IDFactory</code> list. The <code>ID</code>
   *  is set by the unit's constructor.
   *  @return first unit has <code>ID</code>=0
   */
  protected int getID() {
    return ID;
  }

  /**
   *  Returns reference to the next unit.
   *  @return null if this is the last unit
   */
  protected IDFactoryUnit getNextUnit() {
    return nextUnit;
  }

  /**
   *  Sets reference to the next unit.
   *  @param unit null if this is the last unit
   */
  protected void setNextUnit(IDFactoryUnit unit) {
    nextUnit = unit;
  }

  protected IDFactoryUnit resize(IDFactoryUnit old, int newSize) {
    //    IDFactoryUnit newUnit = new IDFactoryUnit( old.getID() );  //create with ID and empty list
    //    
    //    int copySize = old.list.length;    //get the smaller length
    //    if ( newSize < old.list.length){
    //      copySize = newSize;
    //    }
    //      
    //    newUnit.list = new IDFactoryItem[ newSize ];
    //    System.arraycopy( old.list, 0, newUnit.list, 0, copySize );
    //    
    //    //in case the old unit is larger than the new one
    //    if ( old.list.size > newSize ){
    //      IDFactoryUnit newUnit1 = new IDFactoryUnit( old.getID() );  //create new unit with empty list
    //      newUnit1.list = new IDFactoryItem[ newSize ];
    //      
    //      int index = copySize;
    //      do{
    //        int copySize = 
    //        
    //        if ( old.list.length - index > newSize ){
    //          System.arraycopy( old.list, index, newUnit1.list, 0, newSize );
    //        } else {
    //          System.arraycopy( old.list, index, newUnit1.list, 0, old.list.length - newSize );    //the rest of the array
    //        }
    //        in
    //      } while( index - newSize > newSize );
    //      
    //      newUnit.setNext( newUnit );    //add the new unit to the previous one
    //    } //if old > new
    //    
    return null;
  }

}

