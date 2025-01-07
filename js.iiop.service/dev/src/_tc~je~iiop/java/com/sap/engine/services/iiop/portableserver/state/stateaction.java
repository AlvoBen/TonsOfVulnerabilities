/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.iiop.PortableServer.state;

import com.sap.engine.services.iiop.internal.giop.IncomingRequest;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.TRANSIENT;



public interface StateAction {

  public int value();
  public void preinvoke(IncomingRequest request) throws TRANSIENT,OBJ_ADAPTER;
  public void postinvoke(IncomingRequest request) throws TRANSIENT, OBJ_ADAPTER;

}
