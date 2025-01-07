/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.exec;
import com.sap.engine.session.SessionException;

/**
 * This exception is thrown when the count limit is reached
 * @author Nikolai Neichev
 */
public class CounterLimitException extends SessionException {

  public CounterLimitException(String message) {
    super(message);
  }

}
