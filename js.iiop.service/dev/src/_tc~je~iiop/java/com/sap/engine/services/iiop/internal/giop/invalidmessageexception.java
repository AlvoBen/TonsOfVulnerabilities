/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.internal.giop;

/**
 * An Exception thrown to indicate invalid GIOP message header.
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public class InvalidMessageException extends RuntimeException {

  private String reason;

  public InvalidMessageException(String s) {
    super(s);
    reason = s;
  }

  public String getMessage() {
    return reason;
  }

}

