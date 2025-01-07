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
package com.sap.engine.services.jndi.implclient;

import com.sap.engine.services.jndi.implclient.RemoteObjectReferenceFactory;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.P4RemoteObject;
import com.sap.engine.services.rmi_p4.RemoteObjectInfo;
import com.sap.engine.interfaces.cross.ObjectReference;

import java.rmi.Remote;

/**
 * Copyright: Copyright (c) 2003
 * Company: SAP AG - Sap Labs Bulgaria
 *
 * @author Mladen Droshev
 * @version 6.30
 */

public class P4ReferenceFactory implements RemoteObjectReferenceFactory {

  public static final String P4_SERVICE_NAME = "p4";

  protected static P4ObjectBroker broker = null;

  public P4ReferenceFactory() {
  }

  public ObjectReference getObjectReference(Remote remote) {
    try {
      P4RemoteObject p4Object = P4ObjectBroker.init().loadObject(remote);
      RemoteObjectInfo info = p4Object.getObjectInfo();
      if (broker == null) {
        broker = P4ObjectBroker.init();
      }
      broker.addLink(info.key);
      return (ObjectReference) info;
    } catch (Exception e) {
      // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
      // Please do not remove this comment!
      // nothing to do
      return null;
    }
  }

  public String protocolName() {
    return P4_SERVICE_NAME;
  }

  protected static P4ObjectBroker getBroker() {
    if (broker == null) {
      broker = P4ObjectBroker.init();
    }

    return broker;
  }


}