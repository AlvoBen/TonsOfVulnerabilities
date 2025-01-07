/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.exceptions;

import com.sap.exception.BaseException;
import com.sap.localization.LocalizableTextFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This exception is thrown when initialization or modification of some virtual host fails.
 *
 * @author Maria Jurova
 * @version 4.0
 */
public class IllegalHostArgumentsException extends BaseException {

  public static String HOST_WITH_NAME_ALREADY_EXISTS = "http_0060";
  public static String ILLEGAL_TO_REMOVE_DEFAULT_VIRTUAL_HOST = "http_0061";

  /**
   * Constructs a new IllegalHostArgumentsException exception.
   *
   * @param   s  message of the exception
   */
  public IllegalHostArgumentsException(String s, Object[] args, Throwable t) {
		super(HttpResourceAccessor.location, new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), s, args), t);
		//super.setLogSettings(HttpResourceAccessor.category, Severity.ERROR, HttpResourceAccessor.location);
  }

  public IllegalHostArgumentsException(String s, Throwable t) {
		super(HttpResourceAccessor.location, new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), s), t);
		//super.setLogSettings(HttpResourceAccessor.category, Severity.ERROR, HttpResourceAccessor.location);
  }

  public IllegalHostArgumentsException(String s, Object[] args) {
		super(HttpResourceAccessor.location, new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), s, args));
		//super.setLogSettings(HttpResourceAccessor.category, Severity.ERROR, HttpResourceAccessor.location);
  }


  public IllegalHostArgumentsException(String s) {
		super(HttpResourceAccessor.location, new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), s));
		//super.setLogSettings(HttpResourceAccessor.category, Severity.ERROR, HttpResourceAccessor.location);
  }

  private Object writeReplace() {
    StringWriter stringWriter = new StringWriter();
    printStackTrace(new PrintWriter(stringWriter,true));
    return new BaseException(HttpResourceAccessor.location, HttpResourceAccessor.getResourceAccessor(), stringWriter.toString());
  }

}


