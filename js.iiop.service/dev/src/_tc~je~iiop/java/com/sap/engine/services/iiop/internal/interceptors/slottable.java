package com.sap.engine.services.iiop.internal.interceptors;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.PortableInterceptor.InvalidSlot;

import java.util.HashMap;

/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 *
 * User: Pavel Bonev
 * Date: 2005-1-3
 * Time: 13:20:07
 */

public class SlotTable {
  private HashMap slots = null;
  private ORB orb;
  private PICurrentImpl piCurrent = null;

  public SlotTable(ORB orb, PICurrentImpl piCurrent) {
    this.orb = orb;
    this.piCurrent = piCurrent;
    this.slots = new HashMap();
  }

  public SlotTable(ORB orb, PICurrentImpl piCurrent, HashMap slots) {
    this.orb = orb;
    this.piCurrent = piCurrent;
    this.slots = (HashMap)(slots.clone());
  }

  public SlotTable copy() {
    return new SlotTable(orb, piCurrent, slots);
  }

  public Any get_slot(int slotID) throws InvalidSlot {
    if ((slotID >= 0) && (slotID < piCurrent.getSlotsCount())) {
      Integer key = new Integer(slotID);
      Any any = (Any) slots.get(key);
      if (any == null) {
        any = orb.create_any();
        slots.put(key, any);
      }

      return any;
    } else {
      throw new InvalidSlot("Invalid slot ID: " + slotID);
    }
  }

  public void set_slot(int slotID, Any any) throws InvalidSlot {
    if ((slotID >= 0) && (slotID < piCurrent.getSlotsCount())) {
      slots.put(new Integer(slotID),any);
    } else {
      throw new InvalidSlot("Invalid slot ID: " + slotID);
    }
  }

  protected PICurrentImpl getPICurrent() {
    return piCurrent;
  }

  protected void setPICurrent(PICurrentImpl pic) {
    this.piCurrent = pic;
  }

  protected void setORB(ORB orb) {
    this.orb = orb;
  }

  protected ORB getORB() {
    return orb;
  }
}
