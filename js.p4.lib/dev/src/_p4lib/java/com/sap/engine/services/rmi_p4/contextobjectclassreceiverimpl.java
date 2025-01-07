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
package com.sap.engine.services.rmi_p4;

import com.sap.engine.frame.core.thread.ClientThreadContext;
import com.sap.engine.frame.core.thread.ContextObject;

import java.rmi.RemoteException;

public class ContextObjectClassReceiverImpl extends P4RemoteObject implements ContextObjectClassReceiver {

  public ContextObjectClassReceiverImpl() throws RemoteException {
  }

  public Class getClassByName(String name) throws RemoteException {
    P4ObjectBroker p4 = P4ObjectBroker.getBroker();
    ClientThreadContext ctc = p4.getCTC();
    ContextObject co = null;
    if (ctc != null) {
      co = ctc.getContextObject(name);
    }
    if (co != null) {
      return co.getClass();
    } else {
      return null;
    }
  }
}
