/*
 * Copyright (c) 2002 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.services.httpserver.exceptions;

import com.sap.engine.frame.ServiceException;
import com.sap.localization.LocalizableTextFormatter;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 *
 * @author Violeta Uzunova
 * @version 6.30
 */

public class HttpServiceException extends ServiceException {
  public static String UNABLE_TO_INIT_THE_DEFAULT_HTTP_HOST_CANNOT_START_HTTP_SERVICE_ON_SERVER = "http_0050";
  public static String CONFIGURATION_MANAGER_IS_NOT_AVAILABLE = "http_0051";

  public HttpServiceException(String msg) {
    super(new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), msg));
    //super.setLogSettings(HttpResourceAccessor.category, Severity.ERROR, HttpResourceAccessor.location);
  }

//  /**
//   *Constructs a new HttpServiceException exception.
//   *
//   * @param   msg  message of the exception
//   * @param   parameters  parameters of exception message
//   */
//  public HttpServiceException(String msg, Object [] parameters) {
//    super(new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), msg), linkedException);
//    super.setLogSettings(HttpResourceAccessor.category, Severity.ERROR, HttpResourceAccessor.location);
//  }

  /**
   *Constructs a new HttpServiceException exception.
   *
   * @param   msg  message of the exception
   * @param   linkedException  root cause of this exception
   */
  public HttpServiceException(String msg, Throwable linkedException) {
    super(new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), msg), linkedException);
    //super.setLogSettings(HttpResourceAccessor.category, Severity.ERROR, HttpResourceAccessor.location);
  }

  /**
   *Constructs a new ParseException exception.
   *
   * @param   msg  message of the exception
   * @param   parameters  parameters of exception message
   */
//  public HttpServiceException(String msg, Object [] parameters, Throwable linkedException) {
//    super(msg, parameters, linkedException);
//  }

  private Object writeReplace() {
    StringWriter stringWriter = new StringWriter();
    printStackTrace(new PrintWriter(stringWriter, true));
    return new ServiceException(stringWriter.toString());
  }

}
