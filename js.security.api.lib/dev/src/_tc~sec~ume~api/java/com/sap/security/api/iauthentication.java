package com.sap.security.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  This interface retrieves the user information of currently logged-in user.
 *  Please see also {@link com.sap.security.api.logon.ILogonAuthentication}
 *
 */

public interface IAuthentication extends IConfigurable
{
	/**
	 * Checks whether the user is currently logged in and returns an 
	 * implementation of IUser
	 * <p>
	 * If the user is not yet logged in, a respective logon page is written as
	 * ServletResponse. In that case (i.e. <code>null</code> is returned) the calling
	 * servlet can simply end their doGet or doPost method with return.
	 * <p>Example:
	 * <pre>
	 * IUser uid = forceLoggedInUser(req,res);
	 * if (uid == null) return;
	 * </pre>
	 *
	 * @param	req		HttpServletRequest
	 * @param	resp	HttpServletResponse
	 * @return	The currently logged in IUser object or <code>null</code> otherwise.
	 */
	public IUser forceLoggedInUser(HttpServletRequest req, HttpServletResponse resp);

    /**
     * Checks whether the user is currently logged in and returns an 
     * implementation of IUser.
     * If no user is currently logged in, the default guest user is returned (defined in property
     * ume.login.guest_user.uniqueids).
     *
     * @return  The currently logged in IUser object or the default guest user otherwise.
     */
    public IUser getLoggedInUser();

    /**
	 *  Returns the authenticated user. If no user is found in session, the method performs login 
	 *  with the credentials supplied in the request. 
	 *  @param req as HttpServletRequest
	 *  @param resp as HttpServletResponse
	 *  @return the logged in user or null
	 */
	public IUser getLoggedInUser(HttpServletRequest req, HttpServletResponse resp);
  
    /**
     * Loggs off the current user. Performs logout for the authentication stack 
     * configured for the application this method is being called within. Also 
     * invalidates all the http sessions associated with the current JSESSIONID.
     * 
     * @param	req		HttpServletRequest
     * @param	resp	HttpServletResponse
     */
	public void logout(HttpServletRequest req,HttpServletResponse resp);
  
    /**
     * Does the same as method logout. After that redirects to a logoff page 
     * which acknowledges that a logoff has taken place. The logoff page 
     * contains a button to log on again which points to the URL given by the 
     * caller of this method. If the given URL is null, then the logoff page 
     * does not contain a button to log on again.
     * <p>Example:
     * <pre>
     * forceLogoffUser(req, res, returnURL);
     * </pre>
     * 
     * @param	req		HttpServletRequest
     * @param	resp	HttpServletResponse
     * @param url   URL to use to log on again.
     */
	public void forceLogoffUser(HttpServletRequest req, HttpServletResponse resp, String url);
}
