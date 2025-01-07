/*
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.servlets_jsp.webcontainer_api.exceptions;

/**
 * The Exception is thrown in the following cases:
 * - when a configuration cannot be open for reading;
 * - when the WCE runtime changes action failed in its start, commit or rollback phase;
 * - when it is not possible to return the WCE web module sub configuration
 *   opened for write access, for example if the underlying object has been invalidated;
 * - when it is not possible to return the WCE providers' web modules names,
 *   for example if the underlying object has been invalidated.
 *
 * @author Vera Buchkova
 * @author Violeta Georgieva
 * @version 7.10
 */
public class WCEConfigurationException extends Exception {

  /**
   * Constructs an empty exception.
   */
	public WCEConfigurationException() {
		super();
	}

  /**
   * Constructs an exception with a specified message.
   *
   * @param message a message for this exception to be set.
   */
	public WCEConfigurationException(String message) {
		super(message);
	}

  /**
   * Constructs an exception with a specified message and nested throwable.
   *
   * @param message a message for this exception to be set.
   * @param cause the nested throwable.
   */
	public WCEConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

  /**
   * Constructs an exception with a specified nested throwable.
   *
   * @param cause the nested throwable.
   */
	public WCEConfigurationException(Throwable cause) {
		super(cause);
	}

}
