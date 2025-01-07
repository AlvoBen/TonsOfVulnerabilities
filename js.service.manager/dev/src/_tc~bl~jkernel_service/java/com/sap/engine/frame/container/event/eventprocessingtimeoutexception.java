/*
 * Copyright (c) 2009 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.frame.container.event;

import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.tc.logging.Location;
import com.sap.localization.LocalizableText;

/**
 * @author Petar Petrov (i030687)
 * @version 7.20
 *          Date: 2009-3-31
 */
public class EventProcessingTimeoutException extends ServiceRuntimeException {

  // constants //

  // static fields //

  // fields //

  // public static methods //

  // constructors //

  // TODO add event info:
  // * event name
  // * timeout (ms/sec)
  // * how many processors left
  // * component name (with the before event)
  // * maybe the event object itself
  // Also add getters for these
  public EventProcessingTimeoutException(Location loc, LocalizableText text) {
    super(loc, text);
  }

  // public methods //

  // package methods //

  // protected methods //

  // private static methods //

  // private methods //

  // inner classes //

}