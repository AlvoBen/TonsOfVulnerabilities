/*
 * Copyright (c) 2000-2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.interfaces;

public class ErrorData implements Cloneable {
  /**
   * It is very important to specify errorByCode.
   * Based on this custom error handling will be based on error code or exception.
   */
  private boolean errorByCode = false;
  private int errorCode = -1;
  private String message = "";
  private String additionalMessage = "";
  private Throwable exception = null;
  private SupportabilityData supportabilityData = null;
  private boolean htmlAllowed = false;

  public ErrorData(int errorCode, String message, String additionalMessage, boolean htmlAllowed, SupportabilityData supportabilityData) {
    errorByCode = true;
    this.errorCode = errorCode;
    this.message = message == null ? "" : message;
    this.additionalMessage = additionalMessage == null ? "" : additionalMessage;
    this.htmlAllowed = htmlAllowed;
    this.supportabilityData = supportabilityData == null ? new SupportabilityData() : supportabilityData;
  }

  public ErrorData(Throwable exception, boolean htmlAllowed, SupportabilityData supportabilityData) {
    errorByCode = false;
    this.exception = exception;
    this.htmlAllowed = htmlAllowed;
    this.supportabilityData = supportabilityData == null ? new SupportabilityData() : supportabilityData;
  }
  
  public Object clone() {
    if (errorByCode) {
      return new ErrorData(errorCode, message, additionalMessage, htmlAllowed, supportabilityData);
    } else {
      return new ErrorData(exception, htmlAllowed, supportabilityData);
    }
  }

  public boolean isErrorByCode() {
    return errorByCode;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public String getMessage() {
    return message;
  }

  public String getAdditionalMessage() {
    return additionalMessage;
  }

  public Throwable getException() {
    return exception;
  }

  public SupportabilityData getSupportabilityData() {
    return supportabilityData;
  }

  public boolean isHtmlAllowed() {
    return htmlAllowed;
  }
  
  public void setErrorByCode(boolean errorByCode) {
    this.errorByCode = errorByCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setAdditionalMessage(String additionalMessage) {
    this.additionalMessage = additionalMessage;
  }

  public void setException(Throwable exception) {
    this.exception = exception;
  }

  public void setSupportabilityData(SupportabilityData supportabilityData) {
    this.supportabilityData = supportabilityData;
  }

  public void setHtmlAllowed(boolean htmlAllowed) {
    this.htmlAllowed = htmlAllowed;
  }

}
