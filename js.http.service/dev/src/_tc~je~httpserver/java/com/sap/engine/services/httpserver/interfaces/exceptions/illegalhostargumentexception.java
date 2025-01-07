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

import com.sap.localization.LocalizableTextFormatter;
import com.sap.exception.BaseException;

/**
 * The exception is thrown on attempt to add an aliases which already exists 
 * 
 * @author Violeta Uzunova(I024174)
 *
 */
public class IllegalHostArgumentException extends BaseException {
  public static String CANNOT_ADD_HTTP_ALIAS_ON_HOST_BECAUSE_APPLICATION_ALIAS_ALREADY_EXSITS = "http_interfaces_0001";

  /**
   * Constructs a new IllegalHostArgumentsException exception.
   *
   * @param   s  message of the exception
   * @param   args  arguments of the message 
   * @param   t     nested exception
   */
  public IllegalHostArgumentException(String s, Object[] args, Throwable t) {
    super(HttpInterfacesResourceAccessor.location, new LocalizableTextFormatter(HttpInterfacesResourceAccessor.getResourceAccessor(), s, args), t);
    //super.setLogSettings(HttpInterfacesResourceAccessor.category, Severity.ERROR, HttpInterfacesResourceAccessor.location);
  }
  
  /**
   * Constructs a new IllegalHostArgumentsException exception.
   *
   * @param   s  message of the exception
   * @param   t     nested exception
   */
  public IllegalHostArgumentException(String s, Throwable t) {
    super(HttpInterfacesResourceAccessor.location, new LocalizableTextFormatter(HttpInterfacesResourceAccessor.getResourceAccessor(), s), t);
    //super.setLogSettings(HttpInterfacesResourceAccessor.category, Severity.ERROR, HttpInterfacesResourceAccessor.location);
  }

  /**
   * Constructs a new IllegalHostArgumentsException exception.
   *
   * @param   s  message of the exception
   * @param   args  arguments of the message   
   */
  public IllegalHostArgumentException(String s, Object[] args) {
    super(HttpInterfacesResourceAccessor.location, new LocalizableTextFormatter(HttpInterfacesResourceAccessor.getResourceAccessor(), s, args));
    //super.setLogSettings(HttpInterfacesResourceAccessor.category, Severity.ERROR, HttpInterfacesResourceAccessor.location);
  }

  /**
   * Constructs a new IllegalHostArgumentsException exception.
   *
   * @param   s  message of the exception
   */
  public IllegalHostArgumentException(String s) {
    super(HttpInterfacesResourceAccessor.location, new LocalizableTextFormatter(HttpInterfacesResourceAccessor.getResourceAccessor(), s));
    //super.setLogSettings(HttpInterfacesResourceAccessor.category, Severity.ERROR, HttpInterfacesResourceAccessor.location);
  }
}
