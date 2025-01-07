﻿/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.runtime;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public class LockException extends RuntimeException {
  public LockException() {
  }

  public LockException(String message) {
    super(message);
  }

  public LockException(String message, Throwable cause) {
    super(message, cause);
  }

  public LockException(Throwable cause) {
    super(cause);
  }
}
