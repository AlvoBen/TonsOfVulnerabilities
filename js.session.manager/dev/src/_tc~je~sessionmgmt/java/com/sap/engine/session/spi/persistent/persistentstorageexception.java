/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.spi.persistent;

import java.io.IOException;

/**
 * Author: georgi-s
 * Date: Jun 20, 2004
 */
public class PersistentStorageException extends IOException {

  public PersistentStorageException() {
  }

  public PersistentStorageException(String message) {
    super(message);
  }

  public PersistentStorageException(String message, Throwable cause) {
    super(message);
    initCause(cause);
  }

  public PersistentStorageException(Throwable cause) {
    super();
    initCause(cause);
  }

}
