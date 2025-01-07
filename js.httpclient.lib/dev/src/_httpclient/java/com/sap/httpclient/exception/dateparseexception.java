/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient.exception;

/**
 * An exception to indicate an error parsing a date string.
 *
 * @author Nikolai Neichev
 */
public class DateParseException extends Exception {

  /**
   *
   */
  public DateParseException() {
    super();
  }

  /**
   * @param message the exception message
   */
  public DateParseException(String message) {
    super(message);
  }

}