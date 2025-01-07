package com.sap.engine.services.httpserver.chain;

/**
 * Defines a general exception a <code>Filter</code> can throw when 
 * it encounters difficulty
 */
public class FilterException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = -8736135016616099397L;
  
  /**
   * Holds root cause <code>Throwable</code>
   */
  private Throwable rootCause;
  
  /**
  * Constructs a new <code>FilterException</code>.
  */
  public FilterException() {
    super();
  }
  
  /**
  * Constructs a new <code>FilterException</code> with the specified message.
  * The message can be written to the server log and/or displayed for the user.
  *
  * @param message 
  * a <code>java.lang.String</code> specifying the text of the exception message
  */
  public FilterException(String message) {
    super(message);
  }
  
  /**
  * Constructs a new <code>FilterException</code> when the <code>Filter</code>
  * needs to throw an exception and include a message about the "root cause"
  * exception that interfered with its normal operation, including a
  * description message.
  *
  * @param message
  * a <code>java.lang.String</code> containing the text of 
  * the exception message
  *
  * @param rootCause
  * the <code>java.lang.Throwable</code> exception that interfered with the
  * normal operation, making this <code>FilterException</code> necessary
  *
  */
  public FilterException(String message, Throwable rootCause) {
    super(message);
    this.rootCause = rootCause;
  }
  
  /**
  * Constructs a new <code>FilterException</code> when the <code>Filter</code>
  * needs to throw an exception and include a message about the "root cause"
  * exception that interfered with its normal operation. The exception's
  * message is based on the localized message of the underlying exception.
  * <p>
  * This method calls the <code>getLocalizedMessage</code> method
  * on the <code>Throwable</code> to get a localized exception message. 
  * When subclassing <code>FilterException</code>, this method can be 
  * overridden to create an exception message designed for a specific locale.
  *
  * @param rootCause 
  * the <code>java.lang.Throwable</code> exception that interfered with the 
  * normal operation, making this <code>FilterException</code> necessary
  */
  public FilterException(Throwable rootCause) {
    super(rootCause.getLocalizedMessage());
    this.rootCause = rootCause;
  }
  
  /**
  * Returns the exception that caused this <code>FilterException</code>
  *
  * @return 
  * the <code>java.lang.Throwable</code> that caused this 
  * <code>FilterException</code>
  */
  public Throwable getRootCause() {
    return rootCause;
  } 
}
