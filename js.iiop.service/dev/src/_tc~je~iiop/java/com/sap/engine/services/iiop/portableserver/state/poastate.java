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
 
 
package com.sap.engine.services.iiop.PortableServer.state;

import org.omg.PortableServer.POAManagerPackage.State;


public class POAState {

  private ActiveState activState = new ActiveState();
  private DiscardingState discardState = new DiscardingState();
  private InactiveState inactiveState = new InactiveState();
  private HoldingState holdingState = new HoldingState();

  public StateAction getCurrentState(int value) {
    switch (value) {
      case State._ACTIVE:
        return activState;
      case State._DISCARDING:
        return discardState;
      case State._HOLDING:
        return holdingState; 
      case State._INACTIVE:
        return inactiveState;
    }
    return activState;
  }



}
