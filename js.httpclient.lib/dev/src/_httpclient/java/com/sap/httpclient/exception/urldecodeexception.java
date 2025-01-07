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
 * Thrown when a Decoder has encountered a failure condition during a decode.
 *
 * @author Nikolai Neichev
 */
public class URLDecodeException extends Exception {

  /**
   * Creates a URLDecodeException
   *
   * @param pMessage A message with meaning to a human
   */
  public URLDecodeException(String pMessage) {
    super(pMessage);
  }

}