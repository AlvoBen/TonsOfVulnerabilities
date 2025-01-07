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

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * HostStoreException is thrown when writing the host configuration 
 * into the DB throws ConfigurationException  
 * 
 * @author Violeta Uzunova (I024174) 
 */
public class HostStoreException extends BaseException {
	/**
   * Cannot add the aliases of application [{0}].
   */
  public static final String CANNOT_ADD_ALL_APP_ALIASES = "http_interfaces_0013";
  /**
   * Cannot add application alias [{0}].
   */
  public static final String CANNOT_ADD_APP_ALIAS = "http_interfaces_0002";
  /**
   * Cannot remove application alias [{0}].
   */
  public static final String CANNOT_REMOVE_APP_ALIAS = "http_interfaces_0003";
  /**
   * Cannot add http alias [{0}] with value [{1}].
   */
  public static final String CANNOT_ADD_HTTP_ALIAS = "http_interfaces_0004";
  /**
   * Cannot remove http alias [{0}].
   */
  public static final String CANNOT_REMOVE_HTTP_ALIAS = "http_interfaces_0005";

  /**
   * Constructs a new HostStoreException exception.
   *
   * @param   s  message of the exception
   * @param   args  arguments of the exception
   * @param   t  root exception
   */
  public HostStoreException(String s, Object[] args, Throwable t) {
		super(HttpInterfacesResourceAccessor.location, new LocalizableTextFormatter(HttpInterfacesResourceAccessor.getResourceAccessor(), s, args), t);
		//super.setLogSettings(HttpInterfacesResourceAccessor.category, Severity.ERROR, HttpInterfacesResourceAccessor.location);
  }

  /**
   * Constructs a new HostStoreException exception.
   *
   * @param   s  message of the exception   
   * @param   t  root exception
   */
  public HostStoreException(String s, Throwable t) {
		super(HttpInterfacesResourceAccessor.location, new LocalizableTextFormatter(HttpInterfacesResourceAccessor.getResourceAccessor(), s), t);
		//super.setLogSettings(HttpInterfacesResourceAccessor.category, Severity.ERROR, HttpInterfacesResourceAccessor.location);
  }

  /**
   * Constructs a new HostStoreException exception.
   *
   * @param   s  message of the exception
   * @param   args  arguments of the exception   
   */  
  public HostStoreException(String s, Object[] args) {
		super(HttpInterfacesResourceAccessor.location, new LocalizableTextFormatter(HttpInterfacesResourceAccessor.getResourceAccessor(), s, args));
		//super.setLogSettings(HttpInterfacesResourceAccessor.category, Severity.ERROR, HttpInterfacesResourceAccessor.location);
  }

  /**
   * Constructs a new HostStoreException exception.
   *
   * @param   s  message of the exception
   */  
  public HostStoreException(String s) {
		super(HttpInterfacesResourceAccessor.location, new LocalizableTextFormatter(HttpInterfacesResourceAccessor.getResourceAccessor(), s));
		//super.setLogSettings(HttpInterfacesResourceAccessor.category, Severity.ERROR, HttpInterfacesResourceAccessor.location);
  }
}
