package com.sap.engine.services.iiop.internal.interceptors;

import com.sap.engine.frame.core.thread.ContextObject;

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
 * Date: 2005-1-10
 * Time: 10:30:53
 */
public class SlotTableKey implements ContextObject {
  public SlotTableKey() {
  }

  public ContextObject childValue(ContextObject parent, ContextObject child) {
    if (child != null) {
      return child;
    } else {
      return new SlotTableKey() ;
    }
  }

  public ContextObject getInitialValue() {
    return new SlotTableKey();
  }

  public void empty() {
  }
}
