package com.sap.engine.frame.container;

import com.sap.engine.frame.ServiceException;
import com.sap.localization.LocalizableText;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Category;

/**
 * Defines an exception that is thrown if not loaded service is starting.
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class ServiceNotLoadedException extends ServiceException {

  static final long serialVersionUID = 4385430556056064408L;
  
  /**
   * Constructs an exception with a localizable text message.
   *
   * @param loc logging location which will be used in case of automatic tracing
   * @param text  - localizable text message
   */
  public ServiceNotLoadedException(Location loc, LocalizableText text) {
    super(loc, text);
  }

  /**
   * Constructs an exception with a localizable text message and the specified root cause, which caused this exception.
   *
   * @param loc logging location which will be used in case of automatic tracing
   * @param text  - localizable text message
   * @param rootCause - throwable object, which caused this exception
   */
  public ServiceNotLoadedException(Location loc, LocalizableText text, Throwable rootCause) {
    super(loc, text, rootCause);
  }

  /**
   * Constructs an exception with the specified root cause.
   *
   * @param rootCause throwable object which caused this exception
   * @deprecated - use ServiceNotLoadedException(Location loc, LocalizableText text)
   */
  public ServiceNotLoadedException(Throwable rootCause) {
    super(rootCause);
  }

  /**
   * Constructs an exception with a localizable text message.
   *
   * @param text  - localizable text message
   * @deprecated - use ServiceNotLoadedException(Location loc, LocalizableText text)
   */
  public ServiceNotLoadedException(LocalizableText text) {
    super(text);
  }

  /**
   * Constructs an exception with a localizable text message and the specified root cause, which caused this exception.
   *
   * @param text  - localizable text message
   * @param rootCause - throwable object, which caused this exception
   * @deprecated - use ServiceNotLoadedException(Location loc, LocalizableText text)
   */
  public ServiceNotLoadedException(LocalizableText text, Throwable rootCause) {
    super(text, rootCause);
  }

  /**
   * Constructs an exception with a localizable text message and the specified root cause, which caused this exception.
   *
   * @param category - logging category
   * @param severity - logging severity
   * @param location - logging location
   * @param text  - localizable text message
   * @param rootCause - throwable object, which caused this exception
   * @deprecated - use ServiceNotLoadedException(Location loc, LocalizableText text)
   */
  public ServiceNotLoadedException(Category category, int severity, Location location, LocalizableText text, Throwable rootCause) {
    super(category, severity, location, text, rootCause);
  }

}