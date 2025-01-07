package com.sap.security.api.logon;

//Interface that provides an abstract definition of a reference to a kind of logon page.
//During an authentication process, a common requirement for an authentication API is to
//perform redirects to custom or own logon pages. "Redirect" can mean an http redirect,
//a forward action within a servlet or even the launching of a Java iView within the
// Enterprise portal.<p>
// This interface provides the necessary functions to provide information about such a
// redirect.
// The {@link #getTarget()} method provides information about the resource that performs
// the authentication. The actual type of this resource depends on the type of the redirect.
// The different type are:
// <table>
//   <tr><td>{@link #TARGET_FORWARD}</td><td>The resource is a JSP or a servlet.
//                                          Examples: /logon.jsp, /base/servlet/LogonServlet</td></tr>
//   <tr><td>{@link #TARGET_REDIRECT}</td><td>The resource is an http application. </td></tr>
//   <tr><td>{@link #TARGET_JAVAIVIEW}</td><td>The resource is a portal Java iView.</td></tr>
// </table>
//
// The call {@link #getTarget()} returns a String that points to the resource.

/**
 *  Interface that represents an object that's used for visualization of
 *  logon user interaction
 */
public interface ILogonFrontend
{
    /** This value signals that a getRequestDispatcher(<resource>).forward()
     *  call is to be performed.
     */
    public static final int TARGET_FORWARD   = 0 ;

    /** This value signals that an http redirect to the
     *  resource is to be performed
     */
    public static final int TARGET_REDIRECT  = 1 ;

    /** This value signals that the page performing
     *  the authentication is a Portal Java iView.
     */
    public static final int TARGET_JAVAIVIEW = 2 ;

    /** Gets the target of logon frondend.
     *  @return target pointing to the logon resource. Can be of different types,
     *          in case we're running in the Enterprise Portal this will be an
     *          <i>IPortalComponentContext</i>.
     */
    public Object getTarget ();

    /** Gets the type of the logon frontend. See remarks at the beginning.
     *  @return type of frontend.
     */
    public int    getType   ();

}
