package com.tssap.dtr.client.lib.protocol.session;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IAuthenticator;
import com.tssap.dtr.client.lib.protocol.IRequest;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.ISessionContext;
import com.tssap.dtr.client.lib.protocol.Status;
import com.tssap.dtr.client.lib.protocol.URL;
import com.tssap.dtr.client.lib.protocol.util.Encoder;

/**
 * This class implement the "Basic" authentication scheme
 * as defined in RFC 2617.
 */
public final class BasicAuthenticator implements IAuthenticator {

	/** The session context this authenticator belongs to */
	private ISessionContext context;
	
	/** Do we authenticate against a proxy? */
	private boolean forProxyAuthentication = false;	
	
	/** client trace */
	private static Location TRACE = Location.getLocation(BasicAuthenticator.class);
	
	/** Identifier for the authentication scheme */
	public static final String AUTH_SCHEME = "Basic";

	/**
	 * Creates an authenticator for the "Basic" authentication scheme for
	* the specified context.
	* @param context  the session context this authenticator belongs to
	 */
	public BasicAuthenticator(ISessionContext context) {
		this.context = context;
	}
	
	/**
	 * Creates an authenticator for the "Basic" authentication scheme for
	* the specified context.
	* @param context  the session context this authenticator belongs to
	 */
	public BasicAuthenticator(ISessionContext context, boolean forProxyAuthentication) {
		this.context = context;
		this.forProxyAuthentication = forProxyAuthentication;
	}	
	
	/**
	 * Creates an authenticator for the "Basic" authentication scheme 
	 * as clone of the given authenticator.
	 * @param context  the authenticator from which to clone
	 */
	public BasicAuthenticator(ISessionContext context, BasicAuthenticator auth) {
		this(context);
		this.forProxyAuthentication = auth.forProxyAuthentication();
	}		

	/**
	 * Returns the identifier for this authentication scheme.
	 * @return "Basic".
	 * @see IAuthenticator#getAuthenticationScheme()
	 */
	public String getAuthenticationScheme() {
		return AUTH_SCHEME;
	}

	/**
	 * Returns the session context to which this authenticator is assigned.
	 * @return The session context to which this authenticator is assigned.
	 */
	public ISessionContext getSessionContext() {
		return context;
	}

	/**
	 * Calculates the "Authorization" header for the given request according
	 * to the currently defined context and parameters extracted from the last
	 * "Unauthorized" response.
	 * Note, no header is applied if user or password of the given context
	 * evaluates to null.
	 * @param uri  the absolute URL of the resource to which the request is applied
	 * @param request  the request for which credentials are to be supplied
	 * @see IAuthenticator#applyCredentials(URL,IRequest)
	 */
	public void applyCredentials(URL uri, IRequest request) {		
		String user = (forProxyAuthentication)? context.getProxyUser(): context.getUser();
		String password = (forProxyAuthentication)? context.getProxyPassword() : context.getPassword();
		String header = (forProxyAuthentication)? Header.HTTP.PROXY_AUTHORIZATION : Header.HTTP.AUTHORIZATION;
		
		if (user == null || password == null) {
			//$JL-SEVERITY_TEST$
			TRACE.warningT(
				"applyCredentials(String,IRequest)",
				"no credentials applied [user or password undefined]");			
			return;
		}
		
		request.setHeader(
			header,
			"Basic " + Encoder.encodeBase64(user + ":" + password));
	}
	


	/**
	* Extracts headers from the response that are relevant for
	* authentication (i.e. "WWW-Authenticate" and "Proxy-Authenticate").
	* @param response  the response from which to extract authenticate headers.
	 * @see IAuthenticator#setupCredentials(IResponse)
	 */
	public void setupCredentials(IResponse response) {
		if (response.getStatus() == Status.PROXY_AUTHENTICATION_REQUIRED) {			
			forProxyAuthentication = true;
		}
	}

	/**
	 * Resets the authenticator to its initial state.
	 * Any authentication information from previous request/response cycles
	 * is droped.
	 */
	public void reset() {
		// nothing to do
	}

	/**
	 * Determines whether this authenticator is used for
	 * proxy authentication.
	 * @return true, if the authenticator is used for proxy
	 * authentication.
	 */
	public boolean forProxyAuthentication() {
		return forProxyAuthentication;
	}

}
