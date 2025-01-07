package com.tssap.dtr.client.lib.protocol.session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IAuthenticator;
import com.tssap.dtr.client.lib.protocol.IRequest;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.ISessionContext;
import com.tssap.dtr.client.lib.protocol.Status;
import com.tssap.dtr.client.lib.protocol.URL;
import com.tssap.dtr.client.lib.protocol.util.Encoder;
import com.tssap.dtr.client.lib.protocol.util.Pair;
import com.tssap.dtr.client.lib.protocol.util.Tokenizer;

/**
 * This class implement the "Digest" authentication scheme
 * as defined in RFC 2617.
 */
public final class DigestAuthenticator implements IAuthenticator {

	/** The session context this authenticator belongs to */
	private ISessionContext context;

	/** The MD5 implementation */
	private final MessageDigest MD5;

	/** Did we have valid setup information? */
	private boolean valid;

	/** Do we authenticate against a proxy? */
	private boolean forProxyAuthentication = false;

	/** The realm setup parameter */
	private String realm;
	/** The nonce parameter */
	private String nonce;
	/** The domain parameter or <code>null</code>*/
	private String domain;
	/** The opaque parameter or <code>null</code> */
	private String opaque;
	/** The stale parameter or <code>null</code> */
	private boolean stale;
	/** The algorithm parameter or <code>null</code> */
	private String algorithm;
	/** The qop parameter or <code>null</code> */
	private String qop;
	/** the nc credentials value */
	private int nc;

	/** set to true for testing (see TestDigestAuthenticator) */
	private boolean test = false;
	
	/** client trace */
	private static Location TRACE = Location.getLocation(DigestAuthenticator.class);
	
	/** Identifier for the authentication scheme */
	public static final String AUTH_SCHEME = "Digest";	
	

	/**
	 * Creates an authenticator for the "Digest" authentication scheme for
	 * the specified context.
	 * @param context  the session context this authenticator belongs to
	 */
	public DigestAuthenticator(ISessionContext context) {
		this(context, false);
	}
	
	/**
	 * Creates an authenticator for the "Digest" authentication scheme for
	 * the specified context.
	 * @param context  the session context this authenticator belongs to
	 */
	public DigestAuthenticator(ISessionContext context, boolean forProxyAuthentication) {
		this.context = context;
		try {
			MD5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			TRACE.catching("DigestAuthenticator(ISessionContext,boolean)", ex);
			throw new UnsupportedOperationException("Platform does not support MD5 algorithm");
		}
		this.forProxyAuthentication = forProxyAuthentication;
	}	

	/**
	 * Creates an authenticator for the "Digest" authentication scheme for
	 * the specified context. If the given response contains a
	 * "WWW-Authenticate" header the authenticator is initialized from that
	 * response.
	 * @param context  the session context this authenticator belongs to
	 * @param response  the response from which to initialize this response
	 */
	public DigestAuthenticator(ISessionContext context, IResponse response) {
		this(context);
		setupCredentials(response);
	}
	
	/**
	 * Creates an authenticator for the "Digest" authentication scheme 
	 * as clone of the given authenticator.
	 * @param context  the authenticator from which to clone
	 */	
	public DigestAuthenticator(ISessionContext context, DigestAuthenticator auth) {
		this(context);
		this.forProxyAuthentication = auth.forProxyAuthentication();		
	}

	/**
	 * Returns the identifier for this authentication scheme.
	 * @return "Digest".
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
	 * Calculates the "Authorization" or "Proxy-Authorization" header for the given
	 * request according to the currently defined context and authentication parameters
	 * extracted from the last "Unauthorized" response.
	 * Note, no header is applied if user or password of the given context
	 * evaluates to null.
	 * @param uri  the absolute URL of the resource to which the request is applied
	 * @param request  the request for which credentials are to be supplied
	 * @see IAuthenticator#applyCredentials(URL,IRequest)
	 */
	public void applyCredentials(URL uri, IRequest request) {
		String user = (forProxyAuthentication)? context.getProxyUser(): context.getUser();
		String password = (forProxyAuthentication)? context.getProxyPassword() : context.getProxyUser();
		String header = (forProxyAuthentication)? Header.HTTP.PROXY_AUTHORIZATION : Header.HTTP.AUTHORIZATION;
					
		if (user == null || password == null) {
			//$JL-SEVERITY_TEST$
			TRACE.warningT(
				"applyCredentials(String,IRequest)",
				"no credentials applied [user or password undefined]");			
			return;
		}					
					
		// if we have no valid authentication params, nothing to do...
		if (!valid) {
			//$JL-SEVERITY_TEST$
			TRACE.warningT(
				"applyCredentials(String,IRequest)",
				"no credentials applied [invalid authentication params]");
			return;
		}
	

		// if the server has sent a domain param, check in advance if
		// the path of the request belongs to the protection space spanned by
		// these URIs. Otherwise we do not have the right user/password.
		String path = uri.getPath();
		if (domain != null) {
			boolean match = false;
			List uris = Tokenizer.partsOf(domain, ",");
			for (int i = 0; i < uris.size(); ++i) {
				if (path.startsWith((String) uris.get(i))) {
					match = true;
					break;
				}
			}
			if (!match) {
				//$JL-SEVERITY_TEST$				
				TRACE.warningT(
					"applyCredentials(String,IRequest)",
					"no credentials applied [no user/password for requested domain]");				
				return;
			}
		}

		/* Calculate the Digest header
		 *  The naming of the variables is choosen similar to RFC2617.
		 *  Note, for testing, cnonce needs to have a fixed value (taken from
		 *  the test example in RFC2617). Otherwise wie use the current system
		 *  time.
		 */
		String nc = incrementNC();
		String cnonce = (test) ? "0a4f113b" : generateClientNonce();
		final byte[] COLON = ":".getBytes();

		// calculate MD5(A1) = session key
		MD5.reset();
		MD5.update(user.getBytes());
		MD5.update(COLON);
		MD5.update(realm.getBytes());
		MD5.update(COLON);
		MD5.update(password.getBytes());
		byte[] hashA1 = MD5.digest();
		if (algorithm.equalsIgnoreCase("MD5-SESS")) {
			MD5.reset();
			MD5.update(hashA1);
			MD5.update(COLON);
			MD5.update(nonce.getBytes());
			MD5.update(COLON);
			MD5.update(cnonce.getBytes());
			hashA1 = MD5.digest();
		}
		byte[] sessionKey = Encoder.encodeHex(hashA1);

		// hash A2
		MD5.reset();
		MD5.update(request.getMethod().getBytes());
		MD5.update(COLON);
		MD5.update(path.getBytes());
		if (qop.equals("auth-int")) {
			String contentMD5 = request.getRequestEntity().getContentMD5();
			if (contentMD5 != null) {
				MD5.update(COLON);
				MD5.update(contentMD5.getBytes());
			} else {
				qop = "auth";
			}
		}
		byte[] hashA2 = MD5.digest();
		byte[] hashA2hex = Encoder.encodeHex(hashA2);

		// calculate response parameter
		MD5.reset();
		MD5.update(sessionKey);
		MD5.update(COLON);
		MD5.update(nonce.getBytes());
		MD5.update(COLON);
		if (qop != null) {
			MD5.update(nc.getBytes());
			MD5.update(COLON);
			MD5.update(cnonce.getBytes());
			MD5.update(COLON);
			MD5.update(qop.getBytes());
			MD5.update(COLON);
		}
		MD5.update(hashA2hex);
		byte[] responseHash = MD5.digest();
		String response = Encoder.toHexString(responseHash);

		// generate the Digest
		StringBuffer buf = new StringBuffer(100);
		buf.append("Digest username=\"").append(user);
		buf.append("\", realm=\"").append(realm);
		buf.append("\", nonce=\"").append(nonce);
		buf.append("\", uri=\"").append(path).append('\"');
		if (qop != null) {
			buf.append(", qop=").append(qop).append(", nc=").append(nc);
			buf.append(", cnonce=\"").append(cnonce);
		}
		buf.append("\", response=\"").append(response).append("\"");
		if (opaque != null) {
			buf.append(", opaque=\"").append(opaque).append("\"");
		}

		// set the authorization header
		request.setHeader(header, buf.toString());
	}

	/**
	 * Extracts headers from the response that are relevant for
	 * authentication (i.e. "WWW-Authenticate" and "Proxy-Authenticate").
	 * @param response the response from which to extract authentication headers.
	 * @see IAuthenticator#setupCredentials(IResponse)
	 */
	public void setupCredentials(IResponse response) {
		String auth = null;
		if (response.getStatus() == Status.UNAUTHORIZED) {
			auth = response.getHeaderValue(Header.HTTP.WWW_AUTHENTICATE);
		} else if (response.getStatus() == Status.PROXY_AUTHENTICATION_REQUIRED) {
			auth = response.getHeaderValue(Header.HTTP.PROXY_AUTHENTICATE);
			forProxyAuthentication = true;
		}
		if (auth != null && auth.startsWith("Digest")) {
			reset();
			List parts = Tokenizer.partsOf(auth, ",", "Digest".length(), '=');
			Pair p = null;
			for (int i = 0; i < parts.size(); ++i) {
				p = (Pair) parts.get(i);
				String s = p.getName().toLowerCase();
				if (s.equals("realm")) {
					realm = p.getValue();
				} else if (s.equals("qop")) {
					qop = p.getValue();
				} else if (s.equals("nonce")) {
					nonce = p.getValue();
				} else if (s.equals("opaque")) {
					opaque = p.getValue();
				} else if (s.equals("domain")) {
					domain = p.getValue();
				} else if (s.equals("stale")) {
					stale = p.getValue().equalsIgnoreCase("true");
				} else if (s.equals("algorithm")) {
					algorithm = p.getValue();
				}
			}
			// The realm and nonce params are mandatory
			if (realm == null || nonce == null) {
				//$JL-SEVERITY_TEST$
				TRACE.warningT(
					"setupCredentials(IResponse)",
					"request for authentication is invalid [missing realm or nonce value in response]");
				return;
			}
			// According to RFC2617 we must ignore all algorithms that we do not
			// recognize or support. Currently "MD5" and "MD5-SESS" are supported.
			if (algorithm != null && !algorithm.equalsIgnoreCase("MD5") && !algorithm.equalsIgnoreCase("MD5-SESS")) {
				//$JL-SEVERITY_TEST$				
				TRACE.warningT(
					"setupCredentials(IResponse)",
					"request for authentication is invalid [unsupported algorithm '{0}']",
					new Object[]{algorithm});
				return;
			}

			// If a qop param is present is must be "auth" or "auth-int". Other
			// "quality of protection" methods are not supported
			if (qop != null) {
				if (qop.indexOf("auth") == -1 && qop.indexOf("auth-int") == -1) {
					//$JL-SEVERITY_TEST$
					TRACE.warningT(
						"setupCredentials(IResponse)",
						"request for authentication is invalid [unsupported qop '{0}']",
						new Object[]{qop}
					);		
					return;
				}
				if (qop.indexOf("auth") != -1) {
					qop = "auth";
				}
			}

			// The domain param must be ignored according to RFC2617 if we authenticate
			// against a proxy. The protection space in this case is always the entire proxy.
			if (forProxyAuthentication) {
				domain = null;
			}
			// We have valid authentication params!
			valid = true;
		}
	}
	
	/**
	 * Resets the authenticator to its initial state.
	 * Any authentication information from previous request/response cycles
	 * is droped.
	 */
	public void reset()
	{
		valid = false;
		realm = null;
		nonce = null;
		domain = null;
		opaque = null;
		stale = false;
		algorithm = "MD5";
		qop = "auth";
		nc = 0;
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
	

	/**
	 * Generates a client nonce value.
	 * @return The current system time in milliseconds.
	 */
	private String generateClientNonce() {
		return String.valueOf(System.currentTimeMillis());
	}

	/**
	 * Increments the nc value for the next request.
	 * @return A new nc value in hex encoding.
	 */
	private String incrementNC() {
		return Encoder.toHexString(++nc);
	}

}
