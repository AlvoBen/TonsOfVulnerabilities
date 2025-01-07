package com.sap.security.core.server.jaas;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import com.sap.engine.interfaces.security.auth.AbstractLoginModule;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.engine.lib.security.Principal;
import com.sap.engine.lib.security.http.HttpGetterCallback;
import com.sap.security.api.IUserAccount;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Login module for request header variable based login.
 * 
 * The HeaderVariableLoginModule is used when the authorization is done by a third party
 * Web Access Management (e.g. Siteminder).
 * The user access the J2EE Engine through a Webserver. This Webserver (mostly a filter in it) checks
 * authorization (e.g. username & password) with the WAM. If succeeded, a request header
 * variable is written into the request. HeaderVariableLM checks the user and authenticates this one.
 *
 * The <b>Integrated Windows Authentication</b> works the same way, an IIS check for the authenticated Windows user
 * and adds the request header variable "remote-user" to the request. The Active Server Directory (ADS) of
 * the Windows domain is used as User Data Source.
 * The value of the request header variable contains both the Windows domain name and the logon ID
 * (e.g. “SAP_ALL\D040850”).
 * The HeaderVariableLM cuts off the Windows domain name and the separator and just uses the logon ID for logon.
 * Optional the checking of allowed Windows domains can be enabled for security reasons.
 * 
 * Using <b>multiple windows domains<b> is a special case, where the HeaderVariableLM works together with the
 * UME user data source adapters.
 * In this special case, a user must be identified by the Windows Domain and the logon id. If this is enabled, the
 * whole value is given to the UME user data source adapter.
 * This is a very inconsistent behavior because the logon ID differs in the login and the user data source.
 * E.g. “SAP_ALL\D040850” while login, in user data source it’s “d040850@wdf.sap.corp”.
 * 
 * Login module options:
 * windows_integrated: Enables the Integrated Windows Authentication
 * Header: The name of the request header variable
 * Domain: A comma separated list of allowed Windows domain names 
 *
 * @author D040850 Guenther Wannenmacher
 */
public class HeaderVariableLoginModule extends AbstractLoginModule
{
    private final static String     WINDOWS_INTEGRATED      = "windows_integrated";
    private final static String     HEADER                  = "Header";
    private final static String     ALLOWED_DOMAINS         = "domain";
    private final static Location   LOCATION                = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_LOCATION + ".HeaderVariableLoginModule");

    private     String              _header_name;
    private     String              _user_name;
    private     boolean             _b_succeeded            = false;
    private     boolean             _b_windows_integrated   = false;
    private     boolean             _b_windows_integrated_md= false;
    private     HashSet             _allowed_domains        = new HashSet ();

    protected   UMEAdapter          _ume_adapter ;
    protected   Map                 _shared_state;
    protected   Map                 _options;
    protected   CallbackHandler     _callback_handler;
    protected   Subject             _subject;

    static      int                 SEVERITY;
    

    static {
      SEVERITY = Severity.INFO;
    }
    /**
     * Initializes with the login module options
     * <ul>
	 * <li>windows_integrated: Enables the Multi Domnain ADS feature, i.e. the Windows Domain name and logon ID is taken.
	 * Can also be set by UME property "ume.ldap.access.multidomain.enabled"</li>
	 * <li>Header: The name of the request header variable. If no value is set, the UME property "ume.logon.header" is taken
	 * (inherited from previous releases). Default is "REMOTE_USER"</li>
	 * <li>Domain: A comma separated list of allowed Windows domain names</li>
	 * </ul>
	 * 
     * @see com.sap.engine.interfaces.security.auth.AbstractLoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
     */
    public void initialize(
        Subject subject,
        CallbackHandler handler,
        Map sharedState,
        Map options)
    {
      	final String METHOD = "initialize()";
        LOCATION.entering (METHOD, new Object [] { subject, handler, sharedState, options });
        try {
            Properties props = new Properties ();
            props.putAll(options);

            _options = options;
            _shared_state = sharedState;
            _callback_handler = handler;
            _subject          = subject;

            _ume_adapter = new UMEAdapter (sharedState, props);
            _header_name = (String) options.get (HEADER);

            // check header name from properties if not set in options
            if (_header_name == null) {
                _header_name = UMFactory.getProperties().get("ume.logon.header", "REMOTE_USER");
            }

            // Read information about
            // Windows authentication
            if (options.get (WINDOWS_INTEGRATED)!=null &&
                    "true".equalsIgnoreCase((String)options.get(WINDOWS_INTEGRATED))) {
                _b_windows_integrated = true;
                String windowsdomain = (String) options.get (ALLOWED_DOMAINS);

                if (windowsdomain != null) {
                	// windows domains is a comma separated list
                    StringTokenizer st = new StringTokenizer (windowsdomain, ",");
                    while (st.hasMoreTokens()) {
                        // trim it, and put to lower because there is no difference on Windows
                        _allowed_domains.add (st.nextToken().trim());
                    }
                }
            }

            //  Windows Integrated with multi domains
            if (UMFactory.getProperties().getBoolean("ume.ldap.access.multidomain.enabled", false)) {
                _b_windows_integrated_md = true;
            }
        }
        finally {
            LOCATION.exiting(METHOD);
        }
    }

    /* (non-Javadoc)
     * @see com.sap.engine.interfaces.security.auth.AbstractLoginModule#login()
     */
    public boolean login() throws LoginException
    {
        HttpGetterCallback  hgc = new HttpGetterCallback ();
        Exception           onthewayexc = null;

        // read the header variable
        hgc.setName(_header_name);
        hgc.setType (HttpGetterCallback.HEADER);
        try {
            _callback_handler.handle (new Callback [] { hgc });
        }
        catch (IOException e) {
            onthewayexc = e;
        }
        catch (UnsupportedCallbackException e) {
            onthewayexc = e;
        }

        if (onthewayexc!=null) {
            LOCATION.traceThrowableT(Severity.WARNING, onthewayexc.getLocalizedMessage(), onthewayexc);
            throw new LoginException (onthewayexc.toString ());
        }

        _user_name = (String) hgc.getValue ();
        LOCATION.debugT ("login", "header {0} has value {1}", new Object [] { _header_name, _user_name } );

        if (_user_name!=null) {

          /* windows integrated authentication
           * header comes like "DOMAIN\\username" (with one backslash)
           */
            if (_b_windows_integrated) {
                if (_b_windows_integrated_md) {
					/*
					 * multi domain ads uses the whole "domain\\user" string for search by 
					 * getUserAccountByLogonID(), but actually, "user@domain.corp" is the logon ID
					 */ 
					_user_name = SAPLogonTicketHelper.removeEndingSpaces (_user_name);
                } else {
                    // extract domain and username 
                    String [] ud = parseUserAndDomain (_user_name);
                    // check for the allowed domains
                    if (_allowed_domains.size()>0) {
                        if (ud[0]==null || !_allowed_domains.contains(ud[0])) {
                            LOCATION.errorT ("login", "Domain " + ud [0] + " isn't part of allowed domains");
                            throw new LoginException ("Domain " + ud [0] + " isn't part of allowed domains");
                        }
                    }
                    _user_name = SAPLogonTicketHelper.removeEndingSpaces (ud[1]);
                }
            }

          IUserAccount userAccount = null;
          try {
            userAccount = UMFactory.getUserAccountFactory().getUserAccountByLogonId(_user_name);
          } catch (UMException e) {
            throwUserLoginException(e, LoginExceptionDetails.WRONG_USERNAME_PASSWORD_COMBINATION);
          }

          /* as in Multi domain ADS the username given via header variable is different that the logon id of the
           * user account, it's reset again for the login context 
           */
          if (userAccount != null) {
            _user_name = userAccount.getLogonUid();
            LOCATION.debugT("login", "LogonID of user is " + _user_name);
          }

            _shared_state.put (AbstractLoginModule.NAME, _user_name);

            refreshUserInfo(_user_name);

            _b_succeeded=true;
        }
        LOCATION.debugT ("login", "login exits with {0}", new Object[] { _b_succeeded?Boolean.TRUE:Boolean.FALSE });
        return _b_succeeded;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.interfaces.security.auth.AbstractLoginModule#commit()
     */
    public boolean commit() throws LoginException
    {
        if (!_b_succeeded)
            return false;

        Principal p = new Principal (_user_name);
        p.setAuthenticationMethod(Principal.AUTH_METHOD_HEADER_VARIABLE);
        _shared_state.put (AbstractLoginModule.PRINCIPAL, p);

        _subject.getPrincipals().add(p);

        return true;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.interfaces.security.auth.AbstractLoginModule#abort()
     */
    public boolean abort() throws LoginException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.sap.engine.interfaces.security.auth.AbstractLoginModule#logout()
     */
    public boolean logout() throws LoginException
    {
        // TODO Auto-generated method stub
        return false;
    }

    private String[] parseUserAndDomain (String remote_user)
    {
        StringTokenizer st = new StringTokenizer (remote_user, "\\");

        // there is always one token
        String d = st.nextToken ();

        if (st.hasMoreTokens ()) {
            String t = st.nextToken ();
            return new String [] { d, t };
        }
        else {
            return new String [] { null, d };
        }
    }
}
