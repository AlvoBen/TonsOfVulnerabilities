/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
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

/**
 * ShmException is thrown on problems with shared memory when creating
 * application or registring/activating web aliases.
 * 
 * @date 2006-3-22
 * @author vera-b
 */
public class HttpShmException extends BaseException {
  
  /**
   * Cannot register application in shared memory either when creating new ShmApplication or when activating its web aliases.
   */
  public static final String CANNOT_REGISTER_APP_IN_SHM = "http_interfaces_0007";
  /**
   * Cannot stop/delete application from shared memory.
   */
  public static final String CANNOT_UNREGISTER_APP_IN_SHM = "http_interfaces_0008";
  /**
   * Cannot register/activate a single web alias of the application in Shared Memory.
   */
  public static final String CANNOT_REGISTER_ALIAS_IN_SHM = "http_interfaces_0009";
  /**
   * Detailed exception message for 
   * Cannot register application in shared memory either when creating new ShmApplication or when activating its web aliases.
   */
  public static final String CANNOT_REGISTER_APP_IN_SHM_DETAILED = "http_interfaces_0010";
  /**
   * Detailed exception message for 
   * Cannot stop/delete application from shared memory.
   */
  public static final String CANNOT_UNREGISTER_APP_IN_SHM_DETAILED = "http_interfaces_0011";
  /**
   * Detailed exception message for 
   * Cannot register/activate a single web alias of the application in Shared Memory.
   */
  public static final String CANNOT_REGISTER_ALIAS_IN_SHM_DETAILED = "http_interfaces_0012";
  
  /**
   * Constructs a new HttpShmException exception.
   *
   * @param   s     message of the exception
   * @param   args  arguments of the message 
   * @param   t     nested exception
   * 
   */
  public HttpShmException(String s, Object[] args, Throwable t) {
    super(HttpInterfacesResourceAccessor.location, 
        new LocalizableTextFormatter(HttpInterfacesResourceAccessor.getResourceAccessor(), s, args), t);
    //super.setLogSettings(HttpInterfacesResourceAccessor.category, Severity.ERROR, HttpInterfacesResourceAccessor.location);
  }
}
