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

import org.omg.CORBA.portable.ApplicationException;
import com.sap.engine.services.iiop.internal.giop.*;
import com.sap.engine.interfaces.cross.CrossCall;

/**
 *  The lowest level of <code>IDFactory</code> structure. List of
 *  <code>IDFactoryItem</code> represents all IDs that factory produces.
 *  @author Vladimir Velinov
 *  @version 4.0
 */
public class IDFactoryItem implements CrossCall {

  /**  if given item is used by a process  */
  protected static final int USED_ITEM = -2;
  /**  if it's the last item in the unit's list  */
  protected static final int LAST_ITEM = -1;
  /**  holds GIOP Message associated with this ID*/
  protected IncomingReply msg = null;
  /**
   *  Points to next available item in the list.
   * <ul>
   * <li><code>USED_ITEM</code> - a process has already taken this value
   * <li><code>LAST_ITEM</code> - last item in the list
   * <li> other value - next item in list
   *</ul>
   */
  private int nextAvail;

  /**
   *  Constructs the item and sets it's pointer to the next one in the list.
   *  @param next points to next available item in the unit list; if the item
   *  is occupied by a process  the <code>USED_ITEM</code> value is used;
   *  if it's the last one in the list then it's value equals to <code>LAST_ITEM</code>
   */
  protected IDFactoryItem(int next) {
    nextAvail = next;
  }

  /**
   *  Sets the next available item in the list.
   *  @param next sets the item's pointer to the next item
   */
  protected void setNext(int next) {
    nextAvail = next;
  }

  /**
   *  Returns the next available item in the list.
   *  @return gets the next available item
   */
  protected int getNext() {
    return nextAvail;
  }

  /**
   *  Sets the GIOP message associated with this ID.
   *  @param newMsg GIOP message
   */
  public void setMessage(IncomingReply newMsg) {
    msg = newMsg;
  }

  /**
   *  Returns the GIOP message associated with this ID.
   *  @return GIOP message
   */
  public IncomingReply getMessage() throws ApplicationException {
    return msg;
  }

  // CrossCall impl
  public void fail() {
    synchronized (this) {
      this.notify();
    }
  }

}

