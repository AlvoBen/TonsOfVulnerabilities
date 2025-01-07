/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.pj.jmx.timer;

/**
 * A number generator used by the JMX timer to create sequence numbers from timer notifications.
 *
 * @author d025700
 */
public final class NumberGenerator {
  private long count = 0;

  /**
   * Returns the next sequence number
   *
   * @return a sequence number.
   */
  public synchronized long getNextNumber() {
    return ++count;
  }
}