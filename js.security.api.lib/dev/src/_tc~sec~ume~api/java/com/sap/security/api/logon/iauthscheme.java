package com.sap.security.api.logon;

import javax.security.auth.login.*;

/** Interface that represents the authentication scheme object.<br><hr>
 *
 *  @see ILogonFrontend
 */
public interface IAuthScheme
{
    /** Gets the name of the authscheme.
     *  @return name of the authentication scheme
     *
     */
    public String           getName ();

    /**
     *  Gets the priority of an authscheme. Allowed are all numerical values.
     *  It is recommended that the default authentication scheme has priority 0.
     *  @return priority of the authentication scheme.
     */
    public int              getPriority ();

    /**
     *  Gets the authentication modules in the order of their priority.
     *  @deprecated Use getAuthenticationTemplate instead to get the name of
     *              the authentication template which contains the login
     *              modules that are processed for logon.
     *  @return array of authentication module entries.
     */
    public AppConfigurationEntry []   getModules ();
    
    /**
     * @return returns the authentication template that corresponds
     *         to this authscheme.
     */
    public String getAuthenticationTemplate ();

    /**
     * Gets the logon frontend information that comes with this authscheme
     * @return a reference to a ILogonFrontend object
     */
    public ILogonFrontend   getLogonFrontend ();
}
