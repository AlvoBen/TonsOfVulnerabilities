package com.sap.security.api.logon;

import javax.servlet.http.*;

import com.sap.security.api.IUser;

/**
 *  Extension from {@link com.sap.security.api.IAuthentication} that provides more powerful authentication services.
 */
public interface ILogonAuthentication extends com.sap.security.api.IAuthentication
{
    /**
     *  Function that is intended for programmatic logon. Internally, this function is
     *  called by 
     *  {@link com.sap.security.api.IAuthentication#forceLoggedInUser(HttpServletRequest,HttpServletResponse )}.
     *  The function follows
     *  the specific syntax: The parameter <code>authscheme</code> determines which
     *  logon variant will be used, e.g. which authentication mechanism will be used.
     *  On successful return (i.e. if no exception is being thrown) 
     *  a {@link javax.security.auth.Subject} is returned. In order to get an
     *  {@link IUser} object from this subject, call 
     *  {@link javax.security.auth.Subject#getPrincipals()} and
     *  iterate through the returned Set of principals.<p>
     *  For error situations please see the below list of possible exceptions.
     *
     *  @param req HttpServletRequest
     *  @param resp HttpServletResponse
     *  @param authscheme The name of the authscheme to perform logon.
     *  @return	The subject or <code>null</code> otherwise.
     *  @throws javax.security.auth.login.LoginException if the logon fails. The message of the exception is the
     *  key for the error message.<br>Possible keys are<ul>
     *  <li>SecurityPolicy.USER_AUTH_FAILED: general logon failure. Logon id or password wrong, logon id not existent etc.</li>
     *  <li>SecurityPolicy.ACCOUNT_LOCKED_LOGON: the user account is locked due to logon failures.</li>
     *  <li>SecurityPolicy.CERT_AUTH_FAILED: the client certificate is not mapped to a user.</li>
     *  <li>SecurityPolicy.ACCOUNT_LOCKED_ADMIN: the user account is locked by administrator and can't logon.</li>
     *  <li>SecurityPolicy.SAPSTAR_ACTIVATED: the super user SAP* is activated and therefore no other user can logon.</li>
     *  <li>SecurityPolicy.PASSWORD_EXPIRED: the user's password has expired.</li>
     *  </ul> 
     */
    public javax.security.auth.Subject logon (HttpServletRequest req, HttpServletResponse resp, String authscheme)
        throws javax.security.auth.login.LoginException;

    /**
     *  Checks if the current logged in user has satisfied the autscheme with name <code>
     *  authscheme</code>. 
     *  @param user object returned by {@link com.sap.security.api.IAuthentication#getLoggedInUser}.
     *  @param authscheme authscheme to be satisfied
     *  @return <code>true</code> if yes, <code>false</code> if not.
     */
    public boolean isAuthSchemeSufficient (IUser user, String authscheme);

    /**
     * Returns the LogonFrontend for the auth scheme.
     * @param authSchemeName name of the auth scheme
     * @return the logon frontend or <code>null</code>
     */
    public ILogonFrontend getLogonFrontend (String authSchemeName);

    /**
     * Returns the names of teh available auth schemes.
     * @return the names of auth schemes
     */
    public IAuthScheme[] getAuthSchemes();
    
    /**
     *  Check if the current user is already authenticated, i.e. if the <code>IUser</code>
     *  object is result of an authentication process.
     *  @deprecated 
     *  @param  user user to check.
     *  @return <code>true</code> if authenticated, otherwise false.
	 */
    public boolean isAuthenticated(IUser user);
}
