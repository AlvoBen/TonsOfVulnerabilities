package com.sap.engine.frame;

import com.sap.tc.logging.Location;
import com.sap.localization.LocalizableText;

/**
 * Thrown to indicate that service configuration is not valid. If the service is core the
 * system will halt with specific exit code.
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class ServiceConfigurationException extends ServiceException {

  /**
   * Constructs an exception with the specified root cause.
   *
   * @param loc logging location which will be used in case of automatic tracing
   * @param rootCause throwable object which caused this exception
   */
  public ServiceConfigurationException(Location loc, Throwable rootCause) {
    super(loc, rootCause);
  }

  /**
   * Constructs an exception with a localizable text message.
   *
   * @param loc logging location which will be used in case of automatic tracing
   * @param text  - localizable text message
   */
  public ServiceConfigurationException(Location loc, LocalizableText text) {
    super(loc, text);
  }

  /**
   * Constructs an exception with a localizable text message and the specified root cause, which caused this exception.
   *
   * @param loc logging location which will be used in case of automatic tracing
   * @param text  - localizable text message
   * @param rootCause - throwable object, which caused this exception
   */
  public ServiceConfigurationException(Location loc, LocalizableText text, Throwable rootCause) {
    super(loc, text, rootCause);
  }

}