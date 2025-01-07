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
package com.sap.engine.frame.container.deploy.zdm;

import com.sap.engine.frame.container.deploy.ComponentDeploymentException;
import com.sap.tc.logging.Location;
import com.sap.localization.LocalizableText;

/**
 * Can be thrown only if the update process cannot start and the state  
 * of the instance before invoking the method and after throwing the 
 * exception is the same
 * 
 * @author Dimitar Kostadinov
 * @version 1.00
 * @since 7.10
 */
public class RollingException extends ComponentDeploymentException {

  private static final long serialVersionUID = -3293009788636297002L;

  /**
   * Constructs an exception with the specified root cause.
   *
   * @param loc logging location which will be used in case of automatic tracing
   * @param rootCause throwable object which caused this exception
   */
  public RollingException(Location loc, Throwable rootCause) {
    super(loc, rootCause);
  }

  /**
   * Constructs an exception with a localizable text message.
   *
   * @param loc logging location which will be used in case of automatic tracing
   * @param text  - localizable text message
   */
  public RollingException(Location loc, LocalizableText text) {
    super(loc, text);
  }

  /**
   * Constructs an exception with a localizable text message and the specified root cause, which caused this exception.
   *
   * @param loc logging location which will be used in case of automatic tracing
   * @param text  - localizable text message
   * @param rootCause - throwable object, which caused this exception
   */
  public RollingException(Location loc, LocalizableText text, Throwable rootCause) {
    super(loc, text, rootCause);
  }

}