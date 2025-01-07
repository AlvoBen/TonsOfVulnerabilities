package com.sap.engine.services.iiop.internal.interceptors;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.CompletionStatus;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.InvalidSlot;
import com.sap.engine.frame.client.ClientFactory;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.services.iiop.CORBA.ORB;
import com.sap.engine.services.iiop.server.CorbaServiceFrame;

import java.util.WeakHashMap;

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
 * Time: 13:20:40
 */
public class PICurrentImpl extends org.omg.CORBA.LocalObject implements Current {
  private static final String SLOT_TABLE_KEY = "slot_table";

  static {
    if (System.getProperty("org.omg.CORBA.ORBSingletonClass", "").equals("com.sap.engine.system.ORBSingletonProxy")) {
      try {
        SlotTableKey key = new SlotTableKey();
        ThreadSystem ts = CorbaServiceFrame.getThreadSystem();
        ts.registerContextObject(SLOT_TABLE_KEY, key);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private transient ORB orb = null;
  private int slotCounter = 0;
  private transient WeakHashMap slotTables = null;

  public PICurrentImpl(ORB orb) {
    slotTables = new WeakHashMap();
    this.orb = orb;
  }

  public void set_slot(int slotID, Any any) throws InvalidSlot {
    if (orb.isInitialized()) {
      SlotTable table = getThreadSlotTable();
      table.set_slot(slotID, any);
    } else {
      throw new BAD_INV_ORDER(10,CompletionStatus.COMPLETED_MAYBE);
    }
  }

  public Any get_slot(int slotID) throws InvalidSlot {
    if (orb.isInitialized()) {
      SlotTable table = getThreadSlotTable();
      return table.get_slot(slotID);
    } else {
      throw new BAD_INV_ORDER(10,CompletionStatus.COMPLETED_MAYBE);
    }
  }

  public synchronized int allocateSlotId() {
    return slotCounter++;
  }

  public int getSlotsCount() {
    return slotCounter;
  }

  public SlotTable getThreadSlotTable() {
    SlotTable threadSlotTable = null;
    SlotTableKey threadSlotTableKey = null;
    try {
      if (orb.isServerORB()) {
        threadSlotTableKey = (SlotTableKey) CorbaServiceFrame.getThreadSystem().getThreadContext().getContextObject(SLOT_TABLE_KEY);
      } else {
        threadSlotTableKey = (SlotTableKey) ClientFactory.getThreadContextFactory().getThreadContext().getContextObject(SLOT_TABLE_KEY);
        if (threadSlotTableKey == null) {
          threadSlotTableKey = new SlotTableKey();
          ClientFactory.getThreadContextFactory().getThreadContext().setContextObject(SLOT_TABLE_KEY, threadSlotTableKey);
        }
      }

      threadSlotTable = (SlotTable)slotTables.get(threadSlotTableKey);
      if (threadSlotTable == null) {
        threadSlotTable = new SlotTable(orb, this);
        slotTables.put(threadSlotTableKey, threadSlotTable);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return threadSlotTable;
  }

  public void finalize() throws Throwable {
    if (orb.isServerORB()) {
      CorbaServiceFrame.getThreadSystem().unregisterContextObject(SLOT_TABLE_KEY);
    }
    super.finalize();                 
  }
}
