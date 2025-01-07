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
 * The URI parsing and escape encoding exception.
 *
 * @author Nikolai Neichev
 */
public class URIException extends HttpException {

  /**
   * No specified reason code.
   */
  public static final int UNKNOWN = 0;

  /**
   * The URI parsing error.
   */
  public static final int PARSING = 1;

  /**
   * The unsupported character encoding.
   */
  public static final int UNSUPPORTED_ENCODING = 2;

  /**
   * The URI escape encoding and decoding error.
   */
  public static final int ESCAPING = 3;

  /**
   * The DNS punycode encoding or decoding error.
   */
  public static final int PUNYCODE = 4;

  /**
   * The reason code.
   */
  protected int reasonCode;

  /**
   * The reason message.
   */
  protected String reason;

  /**
   * Default constructor.
   */
  public URIException() {
    this(UNKNOWN, null);
  }

  /**
   * The constructor with a reason code argument.
   *
   * @param reasonCode the reason code
   */
  public URIException(int reasonCode) {
    this(reasonCode, null);
  }

  /**
   * The constructor with a reason string and its code arguments.
   *
   * @param reasonCode the reason code
   * @param reason     the reason
   */
  public URIException(int reasonCode, String reason) {
    super(reason); // for backward compatibility of Throwable
    this.reason = reason;
    this.reasonCode = reasonCode;
  }

  /**
   * The constructor with a reason string argument.
   *
   * @param reason the reason
   */
  public URIException(String reason) {
    this(UNKNOWN, reason); // for backward compatibility of Throwable
  }

  /**
   * Get the reason code.
   *
   * @return the reason code
   */
  public int getReasonCode() {
    return reasonCode;
  }

}