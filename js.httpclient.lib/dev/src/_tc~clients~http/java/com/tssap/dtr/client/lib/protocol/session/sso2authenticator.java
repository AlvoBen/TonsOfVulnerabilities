package com.tssap.dtr.client.lib.protocol.session;

import java.util.List;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IAuthenticator;
import com.tssap.dtr.client.lib.protocol.IRequest;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.ISessionContext;
import com.tssap.dtr.client.lib.protocol.Status;
import com.tssap.dtr.client.lib.protocol.URL;

/**
 * This class implement an "SSO2" authentication scheme, a propriatary
 * single-sign-on scheme supported by the SAP J2EE engine.
 */
public class SSO2Authenticator implements IAuthenticator {
	
	/** The session context this authenticator belongs to */
	private ISessionContext context;
	
	/** The session ticket */
	private String ticket;	
	private URL originalHost;
	
	/** The "background" authenticator for login */
	private IAuthenticator login;
	
	/** Do we authenticate against a proxy? */
	private boolean forProxyAuthentication = false;		
		
	public static final String SSO_COOKIE_NAME = "MYSAPSSO2";
	
	/** Identifier for the authentication scheme */
	public static final String AUTH_SCHEME = "SSO2";		

	private static Location TRACE = 
		Location.getLocation(SSO2Authenticator.class);

	/**
	 * Creates an SSO2 ticket authenticator.
	 * @param context  the session context this authenticator belongs to
	 */
	public SSO2Authenticator(ISessionContext context) {
		this.context = context;
	}
	
	/**
	 * Creates an SSO2 ticket authenticator.
	 * @param context  the session context this authenticator belongs to
	 * @param ticket  a valid SSO2 ticket
	 */
	public SSO2Authenticator(ISessionContext context, String ticket) {
		this.context = context;
		this.ticket = ticket;
	}	
	
	/**
	 * Creates an SSO2 ticket authenticator backed by the given standard
	 * authenticator. If no valid ticket is available the login authenticator
	 * is used to authenticate by user/password.
	 * @param context  the session context this authenticator belongs to
	 * @param login  another authenticator used for user/password authentication
	 */
	public SSO2Authenticator(ISessionContext context, IAuthenticator login) {
		this.context = context;
		this.login = login;
	}	
	
	/**
	 * Creates an SSO2 ticket authenticator backed by the given standard
	 * authenticator.
	 * @param context  the session context this authenticator belongs to
	 * @param ticket  a valid SSO2 ticket
	 * @param login  another authenticator used for user/password authentication
	 */
	public SSO2Authenticator(ISessionContext context, String ticket, IAuthenticator login) {
		this.context = context;
		this.login = login;
		this.ticket = ticket;
	}		

	/**
	 * Creates a clone of the given authenticator.
	 * Copies login ticket, login authenticator and original host from
	 * the given authenticator 
	 * @param auth the SSO2 authenticator to clone.
	 */
	public SSO2Authenticator(ISessionContext context, SSO2Authenticator auth) {
		this(context);
		forProxyAuthentication = auth.forProxyAuthentication();
		IAuthenticator loginAuthenticator = ((SSO2Authenticator)auth).login;
		if (loginAuthenticator != null) {
			login = SessionContext.createAuthenticator(context, loginAuthenticator); 
		}
		ticket = ((SSO2Authenticator)auth).ticket;
		originalHost = ((SSO2Authenticator)auth).originalHost;
	}	

	/**
	 * Returns the identifier for this authentication scheme.
	 * @return "SSO2"
	 */
	public String getAuthenticationScheme() {
		return AUTH_SCHEME;
	}

	/**
	 * Returns the authenticator used for initial login.
	 * @return the login authenticator
	 */
	public IAuthenticator getLoginAuthenticator() {
		return login;
	}
	
	/**
	 * Sets the authenticator used for initial login.
	 */
	public void setLoginAuthenticator(IAuthenticator login) {
		this.login = login;
	}	
	
	/**
	 * Sets the authenticator used for initial login.
	 */
	public void setLoginAuthenticator(String loginScheme) {
		this.login = createAuthenticator(context, loginScheme);
	}		

	/**
	 * Returns the session context to which this authenticator is assigned.
	 * @return The session context to which this authenticator is assigned.
	 */
	public ISessionContext getSessionContext() {
		return context;
	}	
	
	
	/**
	 * Returns the single-sign-on ticket as string.
	 * @return a valid SSO2 ticket or null
	 */
	public String getTicket() {
		return ticket;
	}
	
	/**
	 * Sets the single-sign-on ticket
	 * @param a valid SSO2 ticket
	 */
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}	
	
	/**
	 * Returns the domain of the original host that created the
	 * ticket. 
	 * @return a domain string.
	 */
	public String getOriginalDomain() {
		return originalHost.getDomain();
	}	
	
	
	/**
	 * Applies the necessary authentication information to the given request according
	 * to the currently defined context and single-sign-on ticket. If no valid ticket
	 * has been received so far and a login authenticator was defined, the applyCredentials
	 * method of that authenticator is called.
	 * @param path  the absolute path of the resource to which the request is applied
	 * @param request  the request for which credentials are to be supplied
	 */
	public void applyCredentials(URL uri, IRequest request) {
		if (ticket != null) {
			Cookie sso2cookie = new Cookie(SSO_COOKIE_NAME, ticket, uri.getDomain(), uri.getPath());
			request.setHeader(Header.HTTP.COOKIE, sso2cookie.toString(), true);			
		} else if (login != null) {
			login.applyCredentials(uri, request);
	 	}
	}

	/**
	 * Extracts headers from the response that are relevant for
	 * authentication, i.e. the "SSO2" cookie in this case. If no valid single-sign-on
	 * ticket is found and a login authenticator was defined, the <code>setupCredentials</code>
	 * method of this authenticator is called. If more than one SSO2 cookie is
	 * found, all of them are ignored and again  the <code>setupCredentials</code> of
	 * the login authenticator is called.
	 * @param response  the response from which to extract authenticate headers.
	 */
	public void setupCredentials(IResponse response) {
		if (response.getStatus() == Status.PROXY_AUTHENTICATION_REQUIRED) {			
			forProxyAuthentication = true;
		}		
		List matches = context.cookies().searchCookiesByName(SSO_COOKIE_NAME);
		if (matches.size() == 1) {
			Cookie sso2Cookie = (Cookie)matches.get(0);
		 	ticket = sso2Cookie.getValue();
		 	originalHost = sso2Cookie.getOriginalHost();
			TRACE.infoT(
				"setupCredentials(IResponse)",
				"Received Single Sign On ({0}) ticket for  domain \"{1}\". " +				"Switching to SSO2 authentication.", 
				new Object[]{SSO_COOKIE_NAME, sso2Cookie.getDomain()});			 	
			context.cookies().removeCookie(sso2Cookie);		 	
		} else {
			// OK: ADDED 2005-02-22
			// Problem: if SSO ticket is not valid anymore
			// (something like "SSO cookie expires"),
			// HTTP authentication should be applied next time.
			// If ticket is not reset, it will be used again with no
			// success
			if (ticket != null
				&& (response.getStatus() == Status.UNAUTHORIZED
					|| response.getStatus() == Status.PROXY_AUTHENTICATION_REQUIRED))
			{
				TRACE.infoT(
					"setupCredentials(IResponse)",
					"Single Sign On ticket is not valid anymore. " +
					"A new one will be requested."); 
				ticket = null;
			}
			if (login == null) {
				String auth = null;
				if (response.getStatus() == Status.UNAUTHORIZED) {
					auth = response.getHeaderValue(Header.HTTP.WWW_AUTHENTICATE);
				} else if (response.getStatus() == Status.PROXY_AUTHENTICATION_REQUIRED) {
					auth = response.getHeaderValue(Header.HTTP.PROXY_AUTHENTICATE);
				}
				if (auth != null) {
					login = createAuthenticator(context, auth);
				}
		 	}
		 	if (login != null) {
				login.setupCredentials(response);
		 	}				
		}
	}

	
	
	/**
	 * Resets the authenticator to its initial state.
	 * Any authentication information from previous request/response cycles
	 * is droped.
	 */	
	public void reset() {
		ticket = null;
		originalHost = null;
		if (login != null) {
			login.reset();
		}
	}	

	/**
	 * Determines whether this authenticator is used for
	 * proxy authentication.
	 * @return true, if the authenticator is used for proxy
	 * authentication.
	 */
	public boolean forProxyAuthentication() {
		return false;
	}
	
	private IAuthenticator createAuthenticator(ISessionContext context, String authScheme) 
	{
		String scheme = authScheme.toLowerCase();
		if (scheme.startsWith("digest")) {
			return new DigestAuthenticator(context);
		} else if (scheme.startsWith("basic")) {
			return new BasicAuthenticator(context);
		} 
		return null;
	}

}
