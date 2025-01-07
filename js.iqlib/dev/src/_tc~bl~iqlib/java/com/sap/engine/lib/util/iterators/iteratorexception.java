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
package com.sap.engine.lib.util.iterators;

/**
 * IteratorException is a runtime exception that is thrown when a iterator
 * is asked to perform an inappropriate operation.<p>
 *
 * @author Nikola Arnaudov, Andrei Gatev
 * @version 1.0
 */
public class IteratorException extends RuntimeException {
  static final long serialVersionUID = -1648739756351377934L;
  /**
   * Constructs a IteratorException with the specified detail message.
   * A detail message is a String that describes this particular exception.<p>
   *
   * @param message the detail message.
   */
  public IteratorException(String message) {
    super(message);
  }

}

