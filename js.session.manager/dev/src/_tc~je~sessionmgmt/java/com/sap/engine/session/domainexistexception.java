/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.session;

/**
 * @author georgi-s 
 */
public class DomainExistException extends CreateException {

  public DomainExistException() {
  }

  public DomainExistException(String message) {
    super(message);
  }

  public DomainExistException(String message, Throwable cause) {
    super(message, cause);
  }

  public DomainExistException(Throwable cause) {
    super(cause);
  }

}
