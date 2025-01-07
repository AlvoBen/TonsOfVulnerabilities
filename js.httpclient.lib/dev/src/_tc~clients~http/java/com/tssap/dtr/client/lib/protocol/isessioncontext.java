package com.tssap.dtr.client.lib.protocol;

import com.tssap.dtr.client.lib.protocol.session.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * This interface represents HTTP session information,
 * i.e. authentication information and cookies.
 *
 */
public interface ISessionContext {

	/**
	* Returns the user id of this context.
	* @return The user id.
	 */
	String getUser();
	
	/**
	 * Sets the user id for this context.
	 * @param user  the user id for this context.
	 */
	void setUser(String user);

	/**
	* Returns the password for this context.
	* @return The password.
	 */
	String getPassword();
	
	/**
	 * Sets the password for this context.
	 * @param password  the password to set.
	 */
	void setPassword(String password);
	
	/**
	* Returns the proxy user id of this context.
	* @return The user id.
	 */
	String getProxyUser();
	
	/**
	 * Sets the proxy user id for this context.
	 * @param user  the user id for this context.
	 */
	void setProxyUser(String user);	

	/**
	* Returns the proxy password for this context.
	* @return The password.
	 */
	String getProxyPassword();	
	
	/**
	 * Sets the proxy password for this context.
	 * @param password  the password to set.
	 */
	void setProxyPassword(String password);	

	/**
	 * Checks whether this session context sends cookies in requests.
	 * @return True, if the session context sends cookies.
	 * */
	boolean getSendCookies();
	
	/**
	 * Sets whether this session context should send cookies in requests.
	 * By default this parameter is set to true.
	 * @param enable true, if the context should send cookies.
	 */
	void setSendCookies(boolean enable);

	/**
	 * Returns the cookie manager used by this session context.
	 * @return the current or a new cookie manager
	 */
	Cookies cookies();

	/**
	 * Returns the certificate manager used by this session context.
	 * @return  the current or a new default certificate managers
	 */
	Certificates certificates();

	/**
	* Returns a list of cookies matching the given host and path.
	 * @param host  a host qualifier, like "www.sap.com".
	* @param pathPrefix  a URL prefix, like "/public/"
	* @return A list of Cookie instanced matching the requested host and
	* path, or an empty list.
	 */
	List getCookies(String host, String path);

	/**
	 * Sets or changes the value of the specified cookie. If the cookie
	 * does not already exist, it is inserted in the collection of cookies
	 * assigned to this context. Otherwise only the parameters of the cookie
	 * are updated. 
	 * If the domain of the cookie is contained in the rejected domains list
	 * the cookies is not inserted.
	 * If the <code>maxAge</code> parameter of <code>cookie</code>
	 * is set to zero, the cookie is discarded.
	 * @param cookie  the cookie to add or change.
	 */
	void setCookie(Cookie cookie);

	/**
	 * Checks whether the session context sends authentication information
	 * in requests.
	 * @return True, if the session context  sends authentication information.
	 */
	boolean getSendAuthentication();

	/**
	 * Sets whether this session context should send authentication information
	 * in requests. By default this parameter is set to true. Note however, that
	 * authentication information actually is only sent if user, password and
	 * authenticator have been defined.
	 * @param enable  true, if the context should send authentication.
	 */
	void setSendAuthentication(boolean enable);

	/**
	* Returns the authenticator used by this context.
	 */
	IAuthenticator getAuthenticator();
	
	/**
	 * Sets the authenticator used by this context.
	 * @param authenticator  the authenticator to use.
	 */
	void setAuthenticator(IAuthenticator authenticator);
	
	/**
	 * Sets the authenticator used by this context.
	 * @param authScheme  the authentication scheme to use.
	 * @throws NoSuchAlgorithmException - if the authentication scheme is
	 * not supported or the runtime does not support MD5 message digests. 
	 */	
	void setAuthenticator(String authScheme) throws NoSuchAlgorithmException;
	
	/**
	* Returns the proxy authenticator used by this context.
	 */
	IAuthenticator getProxyAuthenticator();	
	
	/**
	 * Sets the proxy authenticator used by this context.
	 * Note, the given authenticator must be configured to serve
	 * proxy authentication requests.
	 * @param authenticator  the authenticator to use.
	 */
	void setProxyAuthenticator(IAuthenticator authenticator);	
	
	/**
	 * Sets the proxy authenticator used by this context.
	 * @param authScheme  the authentication scheme to use.
	 * @throws NoSuchAlgorithmException - if the authentication scheme is
	 * not supported or the runtime does not support MD5 message digests. 
	 */	
	void setProxyAuthenticator(String authScheme) throws NoSuchAlgorithmException;	

	/**
	 * Closes the current session and starts a new one. 
	 * All cookies are removed from the context and the 
	 * authenticator is reset to its initial state.
	 */
	void closeSession();

	/**
	 * Enables server side session trace.
	 * Note, this function is only available for the
	 * SAP NetWeaver HTTP engine.
	 * @param id  the ID of the server side trace, usually "SAP-SAT"
	 * @param level  the intended level of tracing. Currently the values
	 * "low", "medium" and "high" are supported. For details see
	 * the documentation about "Single Activity Tracing" in the NetWeaver
	 * engine.
	 * @since NetWeaver '04 SP8
	 * @deprecated Do not use this method because it will be replaced asap by
	 * the final realization for Server side session trace handling.
	 */
	void enableServerSessionTrace(String id, String level);
}
