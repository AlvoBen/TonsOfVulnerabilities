/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.frame;

import com.sap.localization.LocalizableText;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.exception.BaseException;

/**
 * This is root exception for all exceptions thrown by services.
 *
 * @author Jasen Minov
 * @author Dimitar Kostadinov
 * @version 710
 */
public class ServiceException extends BaseException {

  static final long serialVersionUID = 4039924138753865664L;  

  /**
   * @deprecated - don't use this constant!
   */
  public static final String SERVICE_NOT_STARTED = "kernel_6000";  // 0=service

  /**
   * @deprecated - not used!
   */
  private Throwable linkedException;

  /**
   * Constructs an exception with the specified root cause.
   *
   * @param loc logging location which will be used in case of automatic tracing
   * @param rootCause throwable object which caused this exception
   */
  public ServiceException(Location loc, Throwable rootCause) {
    super(loc, rootCause);
  }

  /**
   * Constructs an exception with a localizable text message.
   *
   * @param loc logging location which will be used in case of automatic tracing
   * @param text  - localizable text message
   */
  public ServiceException(Location loc, LocalizableText text) {
    super(loc, text);
  }

  /**
   * Constructs an exception with a localizable text message and the specified root cause, which caused this exception.
   *
   * @param loc logging location which will be used in case of automatic tracing
   * @param text  - localizable text message
   * @param rootCause - throwable object, which caused this exception
   */
  public ServiceException(Location loc, LocalizableText text, Throwable rootCause) {
    super(loc, text, rootCause);
  }

  /**
   * Constructs an exception with the specified root cause.
   *
   * @param rootCause throwable object which caused this exception
   * @deprecated - use <code>ServiceException(Location loc, Throwable rootCause)</code>
   */
  public ServiceException(Throwable rootCause) {
    super(rootCause);
    linkedException = rootCause;
  }

  /**
   * Constructs an exception with a localizable text message.
   *
   * @param text  - localizable text message
   * @deprecated - use <code>ServiceException(Location loc, LocalizableText text)</code>
   */
  public ServiceException(LocalizableText text) {
    super(text);
  }

  /**
   * Constructs an exception with a localizable text message and the specified root cause, which caused this exception.
   *
   * @param text  - localizable text message
   * @param rootCause - throwable object, which caused this exception
   * @deprecated - use <code>ServiceException(Location loc, LocalizableText text, Throwable rootCause)</code>
   */
  public ServiceException(LocalizableText text, Throwable rootCause) {
    super(text, rootCause);
    linkedException = rootCause;
  }

  /**
   * Constructs an exception with a localizable text message and the specified root cause, which caused this exception.
   *
   * @param category - logging category
   * @param severity - logging severity
   * @param location - logging location
   * @param text  - localizable text message
   * @param rootCause - throwable object, which caused this exception
   * @deprecated - use <code>ServiceException(Location loc, LocalizableText text, Throwable rootCause)</code>
   */
  public ServiceException(Category category, int severity, Location location, LocalizableText text, Throwable rootCause) {
    super(category, severity, location, text, rootCause);
    linkedException = rootCause;
  }

  /**
   * Constructs an exception with a localizable text message and the specified root cause, which caused this exception.
   *
   * @param resourceId - the resource id of the localizable text
   * @param parameters - the parameters of the localizable text
   * @param rootCause - throwable object, which caused this exception
   * @deprecated - use <code>ServiceException(Location loc, LocalizableText text, Throwable rootCause)</code>
   */
  public ServiceException(String resourceId, Object[] parameters, Exception rootCause) {
    super(new LocalizableTextFormatter(ServiceResourceAccessor.getInstance(), resourceId, parameters), rootCause);
    linkedException = rootCause;
  }

  /**
   * Constructors a new ServiceException with detailed message.
   *
   * @param  message  Detail message of the exception.
   * @deprecated - use <code>ServiceException(Location loc, LocalizableText text, Throwable rootCause)</code>
   */
  public ServiceException(String message) {
    super(new LocalizableTextFormatter(ServiceResourceAccessor.getInstance(), message));
  }

  /**
   * Constructors a new ServiceException and links the real exception to it.
   *
   * @param  message  Detail message of the exception.
   * @param  linkedException  This is the real exception that has appeared during service work.
   * @deprecated - use <code>ServiceException(Location loc, LocalizableText text, Throwable rootCause)</code>
   */
  public ServiceException(String message, Throwable linkedException) {
    super(new LocalizableTextFormatter(ServiceResourceAccessor.getInstance(), message), linkedException);
    this.linkedException = linkedException;
  }

  /**
   * Returns the linked exception.
   *
   * @return the exception.
   * @see com.sap.exception.BaseException#getCause()
   * @deprecated - use getCause() method
   */
  public final Throwable getLinkedException() {
    return linkedException;
  }

  /**
   * Sets a linked exception.
   *
   * @param  linkedException  This is the real exception that has appeared during service work.
   * @deprecated
   */
  public final void setLinkedException(Throwable linkedException) {
    this.linkedException = linkedException;
  }

}

