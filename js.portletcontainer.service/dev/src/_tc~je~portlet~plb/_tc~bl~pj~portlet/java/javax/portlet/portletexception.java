/**
  * Copyright 2006 IBM Corporation.
  */

package javax.portlet;



/**
 * The <CODE>PortletException</CODE> class defines a general exception
 * that a portlet can throw when it is unable to perform its operation
 * successfully.
 */

public class PortletException extends java.lang.Exception
{

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new portlet exception.
   */

  public PortletException ()
  {
    super();
  }

  /**
   * Constructs a new portlet exception with the given text. The
   * portlet container may use the text write it to a log.
   *
   * @param   text
   *          the exception text
   */

  public PortletException (String text)
  {
    super (text);
  }

  /**
   * Constructs a new portlet exception when the portlet needs to do
   * the following:
   * <ul>
   * <li>throw an exception 
   * <li>include the "root cause" exception
   * <li>include a description message
   * </ul>
   *
   * @param   text
   *          the exception text
   * @param   cause
   *          the root cause
   */
  
  public PortletException (String text, Throwable cause)
  {
    super(text, cause);
  }

  /**
   * Constructs a new portlet exception when the portlet needs to throw an
   * exception. The exception's message is based on the localized message
   * of the underlying exception.
   *
   * @param   cause
   *          the root cause
   */

  public PortletException (Throwable cause)
  {
    super(cause);
  }


}
