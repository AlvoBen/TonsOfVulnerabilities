﻿/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.iiop.PortableServer;

import org.omg.CORBA.Object;
import org.omg.PortableServer.Servant;


public class RETAINServantHolder extends ServantHolder {

  Servant servant;

  public RETAINServantHolder(POAImpl poa, byte[] oid, String[] ids) {
    super(poa, oid, ids);
  }

  public Servant locateServant() throws Exception {
    if (servant == null) {
      synchronized (this) {
        servant = super.locateServant();
      }
    }
    return servant;
  }


  public synchronized Object getObject() {
    return this;
  }

}
