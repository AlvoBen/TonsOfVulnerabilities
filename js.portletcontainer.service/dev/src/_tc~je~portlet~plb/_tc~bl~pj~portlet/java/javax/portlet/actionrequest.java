/**
  * Copyright 2006 IBM Corporation.
  */

package javax.portlet;


/**
 * The <CODE>ActionRequest</CODE> represents the request sent to the portlet
 * to handle an action.<br>
 * It extends the ClientDataRequest interface and provides action request
 * information to portlets.
 * <p>
 * The portlet container creates an <CODE>ActionRequest</CODE> object and
 * passes it as argument to the portlet's <CODE>processAction</CODE> method.
 * 
 * @see ClientDataRequest
 */
public interface ActionRequest extends ClientDataRequest
{
	/**
	 * Predefined action name for usage with the
	 * <code>@ProcessAction</code> annotation.
	 * 
	 * @since 2.0
	 */
	public static final String ACTION_NAME = "javax.portlet.action";
}
