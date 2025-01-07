package com.tssap.dtr.client.lib.protocol;

/**
 * This interface represent a generic authentication scheme
 * for HTTP requests.
 */
public interface IAuthenticator {

	/**
	 * Returns the identifier for this authentication scheme.
	 * @return A string identifying the authentication scheme.
	 */
	String getAuthenticationScheme();

	/**
	 * Returns the session context to which this authenticator is assigned.
	 * @return The session context to which this authenticator is assigned.
	 */
	ISessionContext getSessionContext();

	/**
	 * Calculates the "Authorization" header for the given request according
	 * to the currently defined context and parameters extracted from the last
	 * "Unauthorized" response.
	 * @param uri  the absolute path of the resource to which the request is applied
	 * @param request  the request for which credentials are to be supplied
	 */
	void applyCredentials(URL uri, IRequest request);

	/**
	* Extracts headers from the response that are relevant for
	* authentication (i.e. "WWW-Authenticate" and "Proxy-Authenticate").
	* @param response  the response from which to extract authenticate headers.
	 */
	void setupCredentials(IResponse response);
	
	/**
	 * Resets the authenticator to its initial state.
	 * Any authentication information from previous request/response cycles
	 * is droped.
	 */
	void reset();
	
	
	/**
	 * Determines whether this authenticator is used for
	 * proxy authentication.
	 * @return true, if the authenticator is used for proxy
	 * authentication.
	 */
	boolean forProxyAuthentication();
}
