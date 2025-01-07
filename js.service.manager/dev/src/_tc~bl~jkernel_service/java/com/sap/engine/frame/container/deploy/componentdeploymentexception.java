/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.frame.container.deploy;

import com.sap.tc.logging.Location;
import com.sap.localization.LocalizableText;
import com.sap.engine.frame.ServiceException;

/**
 * Defines an exception that is thrown in cases of errors during server
 * components managements. This includes deployment, remove and information
 * retrieving methods.
 *
 * @author Monika Kovachka
 * @version 710
 */
public class ComponentDeploymentException extends ServiceException {

  static final long serialVersionUID = -2329345532708743440L;

  /**
   * Constructors a new ComponentDeploymentException with detailed message.
   *
   * @param  message  Detail message of the exception.
   * @deprecated - use ComponentDeploymentException(Location loc, LocalizableText text)
   */
  public ComponentDeploymentException(String message) {
    super(message);
  }

  /**
   * Constructs an exception with the specified root cause.
   *
   * @param loc logging location which will be used in case of automatic tracing
   * @param rootCause throwable object which caused this exception
   */
  public ComponentDeploymentException(Location loc, Throwable rootCause) {
    super(loc, rootCause);
  }

  /**
   * Constructs an exception with a localizable text message.
   *
   * @param loc logging location which will be used in case of automatic tracing
   * @param text  - localizable text message
   */
  public ComponentDeploymentException(Location loc, LocalizableText text) {
    super(loc, text);
  }

  /**
   * Constructs an exception with a localizable text message and the specified root cause, which caused this exception.
   *
   * @param loc logging location which will be used in case of automatic tracing
   * @param text  - localizable text message
   * @param rootCause - throwable object, which caused this exception
   */
  public ComponentDeploymentException(Location loc, LocalizableText text, Throwable rootCause) {
    super(loc, text, rootCause);
  }

}

