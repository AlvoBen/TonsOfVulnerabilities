/*
 * Copyright (c) 2002 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.services.jndi.cache;

/*
 * @author Elitsa Pancheva
 * @version 6.30
 */

public class ReplicationCounter {
  private int value;

  protected ReplicationCounter(int value) {
    this.value = value;
  }

  public synchronized void inc() {
    value++;
    this.notifyAll();
  }

  public int getValue() {
    return value;
  }
}
