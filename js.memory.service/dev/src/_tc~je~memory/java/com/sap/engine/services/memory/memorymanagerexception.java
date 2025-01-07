/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.memory;

/**
 * This exception is called from the MemoryManager.
 * @author Doichin Tsvetkov
 * @version 4.0
 */
public class MemoryManagerException extends RuntimeException {

  /**
   * Constructor.
   * @param message
   */
  public MemoryManagerException(String message) {
    super(message);
  }

}

