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
 * Thrown when there is a failure condition during the encoding process.  This
 * exception is thrown when an Encoder encounters a encoding specific exception
 * such as invalid data, inability to calculate a checksum, characters outside of the
 * expected range.
 *
 * @author Nikolai Neichev
 */
public class URLEncodeException extends Exception {

  /**
   * Creates a new instance of this exception with an useful message.
   *
   * @param pMessage a useful message relating to the encoder specific error.
   */
  public URLEncodeException(String pMessage) {
    super(pMessage);
  }
}