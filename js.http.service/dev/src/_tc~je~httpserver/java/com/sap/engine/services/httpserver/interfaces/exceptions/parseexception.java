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
package com.sap.engine.services.httpserver.interfaces.exceptions;

import com.sap.exception.BaseException;
import com.sap.localization.LocalizableTextFormatter;

import java.io.StringWriter;
import java.io.PrintWriter;

public class ParseException extends BaseException {
  public static final String INCORRECT_REQUEST_LINE = "http_interfaces_0006";
  /**
   * Constructs a new Parse exception.
   *
   * @param   s  message of the exception
   */
  public ParseException(String s, Object[] args, Throwable t) {
		super(HttpInterfacesResourceAccessor.location, new LocalizableTextFormatter(HttpInterfacesResourceAccessor.getResourceAccessor(), s, args), t);
		//super.setLogSettings(HttpInterfacesResourceAccessor.category, Severity.ERROR, HttpInterfacesResourceAccessor.location);
  }

  public ParseException(String s, Throwable t) {
		super(HttpInterfacesResourceAccessor.location, new LocalizableTextFormatter(HttpInterfacesResourceAccessor.getResourceAccessor(), s), t);
		//super.setLogSettings(HttpInterfacesResourceAccessor.category, Severity.ERROR, HttpInterfacesResourceAccessor.location);
  }

  public ParseException(String s, Object[] args) {
		super(HttpInterfacesResourceAccessor.location, new LocalizableTextFormatter(HttpInterfacesResourceAccessor.getResourceAccessor(), s, args));
		//super.setLogSettings(HttpInterfacesResourceAccessor.category, Severity.ERROR, HttpInterfacesResourceAccessor.location);
  }


  public ParseException(String s) {
		super(HttpInterfacesResourceAccessor.location, new LocalizableTextFormatter(HttpInterfacesResourceAccessor.getResourceAccessor(), s));
		//super.setLogSettings(HttpInterfacesResourceAccessor.category, Severity.ERROR, HttpInterfacesResourceAccessor.location);
  }

  private Object writeReplace() {
    StringWriter stringWriter = new StringWriter();
    printStackTrace(new PrintWriter(stringWriter, true));
    return new BaseException(HttpInterfacesResourceAccessor.location, HttpInterfacesResourceAccessor.getResourceAccessor(), stringWriter.toString());
  }

}

