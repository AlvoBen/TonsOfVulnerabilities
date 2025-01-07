package com.tssap.dtr.client.lib.protocol.session;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IAuthenticator;
import com.tssap.dtr.client.lib.protocol.IConnection;
import com.tssap.dtr.client.lib.protocol.IRequest;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.ISessionContext;
import com.tssap.dtr.client.lib.protocol.URL;
import com.tssap.dtr.client.lib.protocol.util.Pair;
import com.tssap.dtr.client.lib.protocol.util.Query;

/**
 * This class manages HTTP session information ("cookies") and
 * provides authentication functionality.
 *
 */
public final class SessionContext implements ISessionContext {

	/** the user id */
	private String user;
	/** the user's password */
	private String password;
	/** the authenticator used for authentication */
	private IAuthenticator authenticator;
	
	/** the proxy user id */
	private String proxyUser;
	/** the proxy user's password */
	private String proxyPassword;
	/** the proxy authenticator used for authentication */
	private IAuthenticator proxyAuthenticator;	

	/** Manager for cookies */
	private Cookies cookies;
	/** Manager for certificates */	
	private Certificates certificates;

	/** true, if the session context should apply cookies to requests */
	private boolean sendCookies = true;
	/** true, if the session context should apply authentication to requests */
	private boolean sendAuthentication = true;


	/** true, if the session context should apply session queries to requests */ 
	private boolean sendSessionQueries = true;
	/** Manager for the session query */
	private Query sessionQuery = new Query();	
	/** list of session query parameters to be send after closeSession */
	private List logoutSessionIDs = new ArrayList();
	/** true, if the session perform a "logout" task */
	private boolean performLogout = false;
	
	private static Location TRACE = Location.getLocation(SessionContext.class);
	

	/**
	 * Creates a new empty session context.
	 */
	public SessionContext() {
	}

	/**
	 * Creates a new session context for the specified user and password.
	 * Note, this constructor does not create an authenticator. Thus, a client
	 * may assign an authenticator explicitly (setAuthenticator), or let the
	 * setupCredential method create a suitable authenticator when the first response
	 * with a 401 (Unauthorized) status is received.
	 * @param user - the user id for this context.
	 * @param password - the password for this context.
	 * @see ISessionContext#setupCredentials(IResponse)
	 */
	public SessionContext(String user, String password) {
		this();
		setUser(user);
		setPassword(password);
	}

	/**
	 * Creates a new session context for the specified user and password
	 * and create an authenticator matching the specified scheme. Currently
	 * only "Basic" and "Digest" are supported.
	 * @param user - the user id for this context.
	 * @param password - the password for this context.
	 * @param authScheme - the authentication scheme used for this context,
	 * either "Basic", "Digest" or "SSO2".
	 * @throws NoSuchAlgorithmException - if the authentication scheme is
	 * not supported or the runtime does not support MD5 message digests.
	 */
	public SessionContext(String user, String password, String authScheme) 
	throws NoSuchAlgorithmException 
	{
		this();
		setUser(user);
		setPassword(password);
		setAuthenticator(authScheme);
	}

	/**
	 * Creates a new session context for the specified user,  password
	 * and authenticator.
	 * @param user - the user id for this context.
	 * @param password - the password for this context.
	 * @param authenticator - the authenticator used for this context.
	 */
	public SessionContext(String user, String password, IAuthenticator authenticator) {
		this();
		setUser(user);
		setPassword(password);
		setAuthenticator(authenticator);
	}

	/**
	 * Creates a new session context suitable for SSL communication
	 * @param certificates  a pre-initialized certificate manager
	 */
	public SessionContext(Certificates certificates) {
		this();
		if (certificates != null) {
			certificates = new Certificates(certificates); 
		}			
	}

	/**
	 * Creates a new session context as clone of the given context.
	 * All parameters and cookies are copied from the original context.
	 * @param context  the context to clone
	 */
	public SessionContext(ISessionContext context) {
		this(context.getUser(), context.getPassword());
		setSendCookies(context.getSendCookies());
		setSendAuthentication(context.getSendAuthentication());
		IAuthenticator auth = context.getAuthenticator();
		if (auth != null) {
			setAuthenticator(createAuthenticator(this, auth));
		}		
		cookies().setCookies(context.cookies().getCookies());
		certificates = new Certificates(context.certificates());
		// OK: ADDED 2005-03-16
		setProxyUser(context.getProxyUser());
		setProxyPassword(context.getProxyPassword());
		auth = context.getProxyAuthenticator();
		if (auth != null) {
			setProxyAuthenticator(createAuthenticator(this, auth));
		}		
	}

	/**
	 * Closes the current session and starts a new one. 
	 * All cookies are removed from the context and the 
	 * authenticator is reset to its initial state.
	 * @see ISessionContext#closeSession()
	 */
	public void closeSession() {
		cookies().removeAll();	
		if (authenticator != null) {	
			authenticator.reset();
		}		
		if (logoutSessionIDs != null) {
			for (Iterator it = logoutSessionIDs.iterator(); it.hasNext();) {
				Pair next = (Pair)it.next();	
				sessionQuery.appendQueryParameter(next);
			}
			performLogout = true;
		}
	}

	/**
	 * Returns the user id of this context.
	 * @return The user id.
	 * @see ISessionContext#getUser()
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Sets the user id for this context.
	 * @param user  the user id for this context.
	 */
	public void setUser(String user) {
		this.user = user;
	}
	
	/**
	 * Returns the proxy user id of this context.
	 * @return The user id.
	 * @see ISessionContext#getProxyUser()
	 */
	public String getProxyUser() {
		return proxyUser;
	}

	/**
	 * Sets the proxy user id for this context.
	 * @param user  the user id for this context.
	 */
	public void setProxyUser(String user) {
		this.proxyUser = user;
	}	

	/**
	 * Returns the password for this context.
	 * @return The password.
	 * @see ISessionContext#getPassword()
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password for this context.
	 * @param password  the password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Returns the proxy password for this context.
	 * @return The password.
	 * @see ISessionContext#getProxyPassword()
	 */
	public String getProxyPassword() {
		return proxyPassword;
	}

	/**
	 * Sets the proxy password for this context.
	 * @param password  the password to set.
	 */
	public void setProxyPassword(String password) {
		this.proxyPassword = password;
	}	

	/**
	 * Checks whether this session context sends cookies in requests.
	 * @return True, if the session context sends cookies.
	 * @see ISessionContext#getSendCookies()
	 * */
	public boolean getSendCookies() {
		return sendCookies;
	}

	/**
	 * Sets whether this session context should send cookies in requests.
	 * By default this parameter is set to true.
	 * @param enable true, if the context should send cookies.
	 * @see ISessionContext#setSendCookies(boolean)
	 */
	public void setSendCookies(boolean enable) {
		this.sendCookies = enable;
	}

	/**
	 * Returns the cookie manager used by this session context.
	 * @return the current or a new default cookie manager
	 * @see ISessionContext#cookies()
	 */
	public Cookies cookies() {
		if (cookies == null) {
			cookies = new Cookies();
		}
		return cookies;
	}
	
	/**
	 * Returns the certificate manager used by this session context.
	 * @return  the current or a new default certificate managers
	 * @see ISessionContext#certificates()
	 */
	public Certificates certificates() {
		if (certificates == null) {
			certificates = new Certificates();
		}
		return certificates;
	}


	/**
	 * Returns a list of cookies matching the given host and path.
	 * @param host  a host qualifier, like "www.sap.com".
	 * @param pathPrefix  a URL prefix, like "/public/"
	 * @return A list of Cookie instanced matching the requested host and
	 * path, or an empty list.
	 * @see ISessionContext#getCookies(String,String)
	 */
	public List getCookies(String host, String path) {
		return cookies().getCookies(host, path);
	}

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
	 * @see ISessionContext#setCookie(Cookie)
	 */
	public void setCookie(Cookie cookie) {
		cookies().setCookie(cookie);
	}

	/**
	 * Checks whether the session context sends authentication information
	 * in requests.
	 * @return True, if the session context sends authentication information.
	 * @see ISessionContext#getSendAuthentication()
	 * */
	public boolean getSendAuthentication() {
		return sendAuthentication;
	}

	/**
	 * Sets whether this session context should send authentication information
	 * in requests. By default this parameter is set to true. Note however, that
	 * authentication information actually is only sent if user, password and
	 * authenticator have been defined.
	 * @param enable  true, if the context should send authentication.
	 * @see ISessionContext#setSendAuthentication(boolean)
	 */
	public void setSendAuthentication(boolean enable) {
		this.sendAuthentication = enable;
	}

	/**
	 * Returns the authenticator used by this context.
	 * @see ISessionContext#getAuthenticator()
	 */
	public IAuthenticator getAuthenticator() {
		return authenticator;
	}

	/**
	 * Sets the authenticator used by this context.
	 * @param authenticator  the authenticator to use.
	 */
	public void setAuthenticator(IAuthenticator authenticator) {
		TRACE.infoT(
			"setupCredentials(IResponse)", 
			"authentication scheme changed [new scheme={0}]",
			new Object[]{authenticator.getAuthenticationScheme()}
		);		
		this.authenticator = authenticator;
	}
	
	/**
	 * Sets the authenticator used by this context.
	 * @param authScheme  the authentication scheme to use.
	 * @throws NoSuchAlgorithmException - if the authentication scheme is
	 * not supported or the runtime does not support MD5 message digests. 
	 */	
	public void setAuthenticator(String authScheme) throws NoSuchAlgorithmException {
		this.authenticator = createAuthenticator(this, authScheme, false);
		TRACE.infoT(
			"setupCredentials(IResponse)", 
			"authentication scheme changed [new scheme={0}]",
			new Object[]{authenticator.getAuthenticationScheme()}
		);			
	}
	
	
	/**
	 * Returns the proxy authenticator used by this context.
	 * @see ISessionContext#getAuthenticator()
	 */
	public IAuthenticator getProxyAuthenticator() {
		return proxyAuthenticator;
	}	
	
	/**
	 * Sets the proxy authenticator used by this context.
	 * Note, the given authenticator must be configured to serve
	 * proxy authentication requests.
	 * @param authenticator  the authenticator to use.
	 */
	public void setProxyAuthenticator(IAuthenticator authenticator) {
		TRACE.infoT(
			"setupCredentials(IResponse)", 
			"proxy authentication scheme changed [new scheme={0}]",
			new Object[]{proxyAuthenticator.getAuthenticationScheme()}
		);		
		this.proxyAuthenticator = authenticator;
	}
	
	/**
	 * Sets the proxy authenticator used by this context.
	 * @param authScheme  the authentication scheme to use.
	 * @throws NoSuchAlgorithmException - if the authentication scheme is
	 * not supported or the runtime does not support MD5 message digests. 
	 */	
	public void setProxyAuthenticator(String authScheme) throws NoSuchAlgorithmException {
		this.proxyAuthenticator = createAuthenticator(this, authScheme, true);
		TRACE.infoT(
			"setupCredentials(IResponse)", 
			"proxy authentication scheme changed [new scheme={0}]",
			new Object[]{proxyAuthenticator.getAuthenticationScheme()}
		);			
	}	
	
	
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
	 */
	public void enableServerSessionTrace(String id, String level) {
		sessionQuery.appendQueryParameter(id, "scope-session,level-" + level.toLowerCase());
		logoutSessionIDs.add(new Pair(id, "scope-none", '='));		
	}
	

	
	/**
	 * Creates an authenticator matching the given scheme
	 */
	public static IAuthenticator createAuthenticator(ISessionContext context, String authScheme, boolean forProxy) 
	throws NoSuchAlgorithmException {
		String scheme = authScheme.toLowerCase();
		if (scheme.startsWith("digest")) {
			return new DigestAuthenticator(context, forProxy);
		} else if (scheme.startsWith("basic")) {
			return new BasicAuthenticator(context, forProxy);
		} else if (scheme.startsWith("sso2")) {
			return new SSO2Authenticator(context);
		} else {
			throw new NoSuchAlgorithmException("Unknown authentication scheme [" + authScheme + "]");
		}	
	}	
	
	/**
	 * Creates a new authenticator that is a clone of the given authenticator.
	 * Note: This method attaches the new authenticator to the same session context
	 * as <code>auth</code>. This in general is not advisable 
	 * @deprecated  use <code>createAuthenticator(ISessionContext, IAuthenticator)</code> 
	 * instead.
	 */	
	public static IAuthenticator createAuthenticator(IAuthenticator auth) {
		String scheme = auth.getAuthenticationScheme().toLowerCase();
		ISessionContext ctx = auth.getSessionContext();
		if (scheme.startsWith("digest")) {
			return new DigestAuthenticator(ctx, (DigestAuthenticator)auth);
		} else if (scheme.startsWith("basic")) {
			return new BasicAuthenticator(ctx, (BasicAuthenticator)auth);
		} else if (scheme.startsWith("sso2")) {
			return new SSO2Authenticator(ctx, (SSO2Authenticator)auth);
		} 
		return auth;
	}			
	
	/**
	 * Creates a new authenticator for the given session context
	 * that is a clone of the given authenticator 
	 */	
	public static IAuthenticator createAuthenticator(ISessionContext context, IAuthenticator auth) {
		String scheme = auth.getAuthenticationScheme().toLowerCase();
		if (scheme.startsWith("digest")) {
			return new DigestAuthenticator(context, (DigestAuthenticator)auth);
		} else if (scheme.startsWith("basic")) {
			return new BasicAuthenticator(context, (BasicAuthenticator)auth);
		} else if (scheme.startsWith("sso2")) {
			return new SSO2Authenticator(context, (SSO2Authenticator)auth);
		} 
		return auth;
	}		
	
	

	/**
	 * Assigns a cookie header to the given request
	 * if there are cookies matching the given the connection.
	 * @param request  the request to which a cookie should be applied
	 * @param connection  the connection that selects which cookies are to be sent
	 * @see ISessionContext#applyCookies(IRequest,IConnection)
	 */
	public void applyCookies(IRequest request, IConnection connection) {
		if (sendCookies) {
			cookies().applyCookies(request, connection);
		}
	}

	/**
	 * Extracts cookie headers from the specified response.
	 * @param response  a response with "Cookie" headers.
	 * @param connection  the connection from which cookies were
	 * retrieved. This parameter ist used to check the validity of the
	 * received cookies.
	 * @see ISessionContext#setupCookies(IResponse,IConnection)
	 */
	public void setupCookies(IResponse response, IConnection connection) {
		cookies().setupCookies(response, connection);
	}	
	

	/**
	 * Calls IAuthenticator.applyCredentials to calculate the "Authentication"
	 * header for the specified request if an authenticator is assigned to this context.
	 * @param uri  the absolute URL of the resource to which the request is applied
	 * @param request  the request for which credentials are to be supplied.
	 * @see ISessionContext#applyCredentials(URL,IRequest)
	 */
	public void applyCredentials(URL uri, IRequest request) {
		if (sendAuthentication) {
			if (authenticator != null) {
				authenticator.applyCredentials(uri, request);
			}
			if (proxyAuthenticator != null) {
				proxyAuthenticator.applyCredentials(uri, request);
			}			
		}	
	}

	/**
	 * Allows the authenticator of this context to extract headers from
	 * the response that are relevant for authentication (i.e.
	 * "WWW-Authenticate" and "Proxy-Authenticate"). If no authenticator
	 * is defined yet, the method parses the headers by itself to find out,
	 * which authentication scheme the server supports, and creates a
	 * suitable authenticator. If the server supports "Digest" authentication
	 * a DigestAuthenticator is created, otherwise a BasicAuthenticator
	 * is used (servers always must support "Basic" authentication).
	 * @param response  the response from which to extract authenticate headers.
	 * @see ISessionContext#setupCredentials(IResponse)
	 */
	public void setupCredentials(IResponse response) {
		if (authenticator == null) {				
			String authScheme = response.getHeaderValue(Header.HTTP.WWW_AUTHENTICATE);
			if (authScheme != null) {
				try {
					setAuthenticator(authScheme);
				} catch (NoSuchAlgorithmException e){
					TRACE.catching("setupCredentials(IResponse)", e);
					TRACE.infoT(
						"setupCredentials(IResponse)",
						"Ignoring credentials due to unknown or invalid authentication scheme [{0}]",
						new Object[]{authScheme}
					);
				}
			}
		}
		if (authenticator != null) {
			authenticator.setupCredentials(response);
		}
				
		if (proxyAuthenticator == null) {
			String authScheme = response.getHeaderValue(Header.HTTP.PROXY_AUTHENTICATE);
			if (authScheme != null) {
				try {
					setProxyAuthenticator(authScheme);
				} catch (NoSuchAlgorithmException e){
					TRACE.catching("setupCredentials(IResponse)", e);
					TRACE.infoT(
						"setupCredentials(IResponse)",
						"Ignoring proxy credentials due to unknown or invalid proxy authentication scheme [{0}]",
						new Object[]{authScheme}
					);
				}
			}
		}	
		if (proxyAuthenticator != null) {
			proxyAuthenticator.setupCredentials(response);
		}							
	}
	
	
	/**
	 * Applies query parameters to the request.
	 * @param request the request for which a query is to be supplied. 
	 */
	public void applyQueryParameters(IRequest request) {
		if (sendSessionQueries) {		 
			Query query = request.getQuery();
			if (query == null) {
				query = new Query();
			}
			query.appendQuery(sessionQuery);						
			request.setQuery(query);

			if (performLogout) {
				for (Iterator it = logoutSessionIDs.iterator(); it.hasNext();) {			
					sessionQuery.removeQueryParameter(((Pair)it.next()).getName());
				}
				logoutSessionIDs.clear();
				performLogout = false;
			}			
		}
	}

	
	/**
	 * Returns a string representation of this session context.
	 * @return a string representation of the session context including
	 * the currently selected user (if any)
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("user<").append((user!=null)? user : "unknown user").append(">");
		s.append(", auth<");
		s.append((authenticator!=null)? authenticator.getAuthenticationScheme() : "none");
		s.append(">");
		s.append(", cookies<").append(sendCookies? "allowed" : "blocked").append(">");
		if (sendCookies) {
			s.append("<privacy:").append(cookies().getPrivacy().toString()).append(">");
			Iterator blocked = cookies().rejectedDomains().iterator();
			if (blocked.hasNext()) {
				s.append("<blocked-sites:");
				while (blocked.hasNext()) {
					s.append(" \"").append((String)blocked.next()).append("\"");
				}
				s.append(">");
			}
		}
		return s.toString();
	}
	
}
