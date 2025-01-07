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
package com.sap.engine.services.httpserver.exceptions;

import com.sap.exception.BaseException;
import com.sap.localization.LocalizableTextFormatter;

import java.io.StringWriter;
import java.io.PrintWriter;

public class ParseException extends BaseException {
  public static final String HEADER_NOT_FOUND = "http_0070";

  /**
   * Constructs a new Parse exception.
   *
   * @param   s  message of the exception
   */
  public ParseException(String s, Object[] args, Throwable t) {
		super(HttpResourceAccessor.location, new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), s, args), t);
		//super.setLogSettings(HttpResourceAccessor.category, Severity.ERROR, HttpResourceAccessor.location);
  }

  public ParseException(String s, Throwable t) {
		super(HttpResourceAccessor.location, new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), s), t);
		//super.setLogSettings(HttpResourceAccessor.category, Severity.ERROR, HttpResourceAccessor.location);
  }

  public ParseException(String s, Object[] args) {
		super(HttpResourceAccessor.location, new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), s, args));
		//super.setLogSettings(HttpResourceAccessor.category, Severity.ERROR, HttpResourceAccessor.location);
  }


  public ParseException(String s) {
		super(HttpResourceAccessor.location, new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), s));
		//super.setLogSettings(HttpResourceAccessor.category, Severity.ERROR, HttpResourceAccessor.location);
  }

  private Object writeReplace() {
    StringWriter stringWriter = new StringWriter();
    printStackTrace(new PrintWriter(stringWriter, true));
    return new BaseException(HttpResourceAccessor.location, HttpResourceAccessor.getResourceAccessor(), stringWriter.toString());
  }

}

