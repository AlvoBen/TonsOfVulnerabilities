package com.tssap.dtr.client.lib.protocol.session;

import java.util.Arrays;
import java.util.List;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.URL;
import com.tssap.dtr.client.lib.protocol.util.Pair;
import com.tssap.dtr.client.lib.protocol.util.Tokenizer;

/**
 * This class implements a HTTP session information ("cookie")
 * according to RFC2109 and RFC2965.
 */
public final class Cookie implements Comparable {

	/**
	 * The name of the cookie.
	 * Names that begin with $ are reserved and must not be used by applications.
	 */
	private String name;

	/**
	 * The value of the cookie.
	 * The value is opaque to the user agent and may be anything the
	 * origin server chooses to send.
	 */
	private String value;

	/**
	 * The comment attribute allows an origin server to document how it intends to use the
	 * cookie. The client can inspect the information to decide whether to
	 * initiate or continue a session with this cookie.
	 */
	private String comment;

	/**
	 * The commentURL attribute allows an
	 * origin server to document how it intends to use the cookie. The
	 * client can inspect the information identified by the URL to decide
	 * whether to initiate or continue a session with this cookie.
	 */
	private String commentURL;

	/**
	 * The discard attribute instructs the client to
	 * discard the cookie unconditionally when the client terminates.
	 */
	private boolean discard = false;

	/**
	 * The value of the domain attribute specifies the domain
	 * for which the cookie is valid. Domains must always start with
	 * a leading dot.
	 */
	private String domain;

	/**
	 * The value of the maxAge determines the lifetime of the cookie in seconds.
	 */
	private long maxAge = -1L;

	/**
	 * The value of the path attribute specifies the subset of
	 * URLs on the origin server to which this cookie applies.
	 */
	private String path ="/";

	/**
	 * The portList attribute restricts the port (or ports) to which a cookie
	 * may be returned in a Cookie request header. This list is sorted.
	 */
	private int[] portList;
	private String ports;

	/**
	 * The secure attribute directs the client to use only secure means to
	 * contact the origin server whenever it sends back this cookie, to protect the
	 * confidentially and authenticity of the information in the cookie.
	 * If this attribute true, the cookie is only sent ovet HTTPS connections.
	 */
	private boolean secure = false;

	/**
	 * The value of the Version attribute, a decimal integer,
	 * identifies the version of the state management specification to
	 * which the cookie conforms.
	 */
	private int version = 1;

	/** True if the cookie is valid */
	private boolean valid = false;

	/**
	 * The system time in milliseconds when this cookie expires.
	 *  The default value indicates, that the cookie never expires
	 */
	private long expirationTime = -1L;
	
	/**
	 * The original host that created the cookie
	 */
	private URL originalHost;
	
	/** trace location*/
	private static Location TRACE = Location.getLocation(Cookie.class);
	
	

	/**
	 * Creates a cookie from the given parameters.
	 * @param name  the name of the cookie (required)
	 * @param value  the value of the cookie (required)
	 * @param domain  the domain parameter of the cookie (optional). A valid
	 * domain consists at least of two parts separated by a dot, e.g. ".sap.com".
	 * Cookies for top-level domains like ".com" are not allowed. If domain is
	 * set to null, ".local" is assumed.
	 * @param path  the path value of the cookie (optional). If path is set to null
	 * or an empty string, "/" is assumed. Path should be an absolute path (i.e.
	 * starting with "/").
	 */
	public Cookie(String name, String value, String domain, String path) {
		if (name == null || name.length() ==0) {
			throw new IllegalArgumentException("Cookie name must not be null or an empty string.");
		}		
		if (value == null || value.length() == 0) {
			throw new IllegalArgumentException("Cookie value must not be null or an empty string.");
		}				
		if (version != 1) {
			throw new IllegalArgumentException("Cookie version not supported.");				
		}
		if (domain == null || domain.length()== 0) {
			domain = ".local";
		}
		if (!domain.equals(".local") && domain.indexOf('.', 1) == -1) {
			throw new IllegalArgumentException("Invalid cookie domain.");
		}			
		this.name = name;
		this.value = value;
		this.domain = domain;
		if (path != null && path.length()> 0 ) {			
			this.path = (path.startsWith("/"))? path : "/" + path;
		}
		valid = true;
	}

	/**
	 * Create a cookie from a Set-Cookie header value with reference to
	 * a certain request host, path and port.
	 * @param cookieHeader  a string following the syntax for "Set-Cookie" or
	 * "Set-Cookie2" headers.
	 * @param requestUrl  the absolute URL of the request (including host, port
	 * and path)
	 * @param lazy  if true, the method does not check, whether the domain of
	 * the cookie matches the domain of the host. 
	 * Note, using lazy is not recommended by RFC2965 since it allows a malicious
	 * server to track a connection across host boundaries. However, some 
	 * single-sign-on scenarios are based on such a behavior.
	 */
	public Cookie(String cookieHeader, URL requestUrl, boolean lazy) 
	{
		initialize(cookieHeader);
		
		String requestDomain = getDomainOf(requestUrl.getHost());
		if (domain == null) {
			domain = requestDomain;
		}
		if (path == null) {
			path = requestUrl.getPath();
		} else {
			path = (path.startsWith("/"))? path : "/" + path;			
		}				
		originalHost = new URL(requestUrl.getProtocol(), requestUrl.getHost(),
				requestUrl.getPort(), "/");
		valid = checkValidity(requestDomain, requestUrl.getPort(), lazy);
	}
	
	/**
	 * Creates a cookie as clone of another. All parameters are copied from
	 * the original cookie.
	 * @param cookie  the cookie to clone
	 */
	public Cookie(Cookie cookie) {
		name = cookie.getName();
		value = cookie.getValue();
		comment = cookie.getComment();
		commentURL = cookie.getCommentURL();
		discard = cookie.getDiscard();
		domain = cookie.getDomain();
		maxAge = cookie.getMaxAge();
		path = cookie.getPath();		
		ports = cookie.getPorts();
		int[] plist = cookie.getPortList();
		portList = new int[plist.length];
		if (plist.length > 0) {
			System.arraycopy(plist, 0, portList, 0, plist.length);
		}
		secure = cookie.requiresSecurity();
		version = cookie.getVersion();
		valid = cookie.isValid();
		expirationTime = cookie.getExpires();		
	}

	/**
	 * Returns the name of the cookie.
	 * Names that begin with $ are reserved and must not be used by applications.
	 * This attributes is mandatory for cookies.
	 * @return The name of the cookie.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the value of the cookie.
	 * The value is opaque to the user agent and may be anything the
	 * origin server chooses to send.
	 * This attributes is mandatory for cookies.
	 * @return The value of the cookie.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * The comment attribute allows an origin server to document how it intends to use the
	 * cookie. The client can inspect the information to decide whether to
	 * initiate or continue a session with this cookie.
	 * This attributes is optional.
	 * @return The comment of the cookie, or null.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * The commentURL attribute allows an
	 * origin server to document how it intends to use the cookie. The
	 * client can inspect the information identified by the URL to decide
	 * whether to initiate or continue a session with this cookie.
	 * This attributes is optional.
	 * @return The commentURL of the cookie, or null.
	 */
	public String getCommentURL() {
		return commentURL;
	}

	/**
	 * The discard attribute instructs the client to
	 * discard the cookie unconditionally when the client terminates.
	 * @return True, if the cookie should be discarded. Otherwise the client
	 * may decide whether or not to store the cookie for another session.
	 */
	public boolean getDiscard() {
		return discard;
	}

	/**
	 * Returns the domain for which the cookie is valid. Domains must always start with
	 * a leading dot, for example ".sap.com".
	 * This attributes is mandatory for cookies.
	 * @return The domain to which this cookie applies.
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * Returns the lifetime of the cookie in seconds.
	 * @return The lifetime of the cookie.
	 */
	public long getMaxAge() {
		return maxAge;
	}

	/**
	 * Returns the time in milliseconds when this cookie expires.
	 * The default value indicates, that the cookie never expires.
	 * @return The time when this cookie expires, or -1 if the cookie never
	 * expires.
	 */
	public long getExpires() {
		return expirationTime;
	}

	/**
	 * Returns the URL namespace on the origin server to which this
	 * cookie applies. For example a cookie with <code>getPath()=="/public"</code>
	 * would be applied to requests for resources like "/public" and
	 * "/public/myapp", but not to "/etc".
	 * @return The URL namespace of the cookie.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returns a list of ports on the origin server to which the cookie
	 * may be returned. This list is sorted.
	 * @return A list of port number, or null.
	 */
	public int[] getPortList() {
		return portList;
	}

	/**
	 * Returns a list of ports on the origin server to which the cookie
	 * may be returned in string format.
	 * @return A comma-separated list of port numbers.
	 */
	public String getPorts() {
		return ports;
	}

	/**
	 * Returns whether the client should use secure means to
	 * contact the origin server whenever it sends back this cookie, to protect the
	 * confidentially and authenticity of the information in the cookie.
	 * Such cookies are only sent if the connection uses the "https" protocol.
	 * @return If true, the cookie requires a secure connection to be sent.
	 */
	public boolean requiresSecurity() {
		return secure;
	}

	/**
	 * Returns the version of the state management specification to
	 * which the cookie conforms.
	 * @return Always 1.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Checks whether the cookie contains valid information. Only valid cookies+
	 * are sent with requests.
	 * @return True, if the cookie is valid.
	 */
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * Returns the URL of the original host that created the cookie
	 * @return a host URL
	 */
	public URL getOriginalHost() {
		return originalHost; 
	}

	/**
	 * Checks whether the cookie applies to the given host.
	 * @return True, if the cookie matches the host.
	 */
	public boolean matchesHost(String host) {
		return domain.equals(getDomainOf(host));
	}

	/**
	 * Checks whether the cookie applies to the given resource URL.
	 * @return True, if the cookie matches the resource URL.
	 */
	public boolean matchesPath(String requestPath) {
		return (path == null || path != null && requestPath.startsWith(path));
	}

	/**
	 * Checks whether the cookie applies to the given port number.
	 * @return True, if the cookie matches the port.
	 */
	public boolean matchesPort(int port) {
		return (portList == null || (port > 0 && Arrays.binarySearch(portList, port) >= 0));
	}

	/**
	 * Checks whether the cookie matches a given host and path prefix.
	 * If this method returns true for the cookie it would be sent in a
	 * request to the corresponding server for a resource in the given
	 * path domain.
	 * @param host  a host, for example "repo.sap.com"
	 * @param path  an URL, for example the URL of a request for a certain resource
	 * @return True, if the cookie matches the given parameters.
	 */
	public boolean matches(String host, String path) {
		return matches(host, path, -1);
	}

	/**
	 * Checks whether the cookie matches a given host, path prefix, and port.
	 * If this method returns true for the cookie it would be sent in a
	 * request to the corresponding server for a resource in the given
	 * path domain.
	 * @param host  a host, for example "repo.sap.com"
	 * @param path  an URL, for example the URL of a request for a certain resource
	 * @param port  a port number
	 * @return True, if the cookie matches the given parameters.
	 */
	public boolean matches(String host, String path, int port) {
		return (matchesHost(host) && matchesPath(path) && matchesPort(port));
	}

	/**
	 * Checks whether the cookie matches the given URL.
	 * If this method returns true for the cookie it would be sent in a
	 * request to the corresponding URL.
	 * @param url  the URL to check
	 * @return True, if the cookie matches the URL.
	 */
	public boolean matches(URL url) {
		return matches(url.getHost(), url.getPath(), url.getPort());
	}

	/**
	 * Extracts the domain part of the given host URL. For example, the domain
	 * of the host URL "www.sap.com:1080" is ".sap.com".
	 * @param host  a host URL, e.g. "repo.sap.com:1080". Note, the host
	 * must contain at least one internal dot. Otherwise ".local" is returned.
	 * @return The domain part of the given host URL, including a leading dot,
	 * or ".local", if the host does not contain inner dots.
	 */
	public static String getDomainOf(String hostURL) {
		return URL.getDomainOf(hostURL);
	}

	/**
	 * Extracts the port from a given host URL.
	 * @param host  a host URL, e.g. "www.sap.com:1080"
	 * @return The port of the given host URL, or -1 if no port
	 * was found.
	 */
	public static int getPortOf(String hostURL) {
		int hostPort = -1;
		int m = hostURL.lastIndexOf(':');
		if (m > 0) {
			try {
				hostPort = Integer.parseInt(hostURL.substring(m + 1));
			} catch (NumberFormatException ex) {
				TRACE.catching("getPortOf(String)", ex);
			}
		}
		return hostPort;
	}

	/**
	 * Returns a string representation of the cookie. The returned value
	 * can directly be used to set a "Cookie" request header.
	 * @return The string representation of the cookie.
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("$Version=").append(version).append(';');
		s.append(name).append('=').append(value);
		if (path != null) {
			s.append(";$Path=").append(path);
		}
		if (!".local".equals(domain)) {
			s.append(";$Domain=").append(domain);
		}
		if (ports != null) {
			s.append(";$Port=\"").append(ports).append("\"");
		}
		return s.toString();
	}
	
	/**
	 * Indicates whether some cookie is equal to this one.
	 * Cookies are considered to be equal if they share the same name,
	 * domain, path and port list.
	 * @param obj  the cookie with which to compare
	 * @return true if the cookies are equal, false otherwise.
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof Cookie))
			return false;
		Cookie c = (Cookie) obj;
		if (!name.equals(c.name))
			return false;
		if (!domain.equals(c.domain))
			return false;
		if (path != null && c.path != null && !path.equals(c.path))
			return false;
		if (portList != null && c.portList != null && !Arrays.equals(portList, c.portList))
			return false;
		return true;
	}
	
	/**
	 * Returns a hash code value for the Cookie.
	 * The <code>name</code>, <code>domain</code>, <code>path</code> 
	 * and <code>portList</code> attributes
	 * are taken into account for hash calculation.
	 * @return  a hash code value for this Cookie.
	 */	
	public int hashCode() {
		int result = 17;
		result = (name != null)? 37*result + name.hashCode() : result;
		result = (domain != null)? 37*result + domain.hashCode() : result;
		result = (path != null)? 37*result + path.hashCode() : result;
		if (portList != null)  {
			for (int i=0; i<portList.length; ++i) {
				result = 37*result + portList[i];
			}
		}
		return result;		
	}

	/**
	 * Compares two cookies.
	 * If the domain attributes differ, the method
	 * returns <code>this.domain.compareTo(obj.domain)</code>.
	 * Otherwise if both cookies define path attributes and both pathes
	 * share a common path prefix then the longer path precedes the shorter.
	 * Otherwise the names of the cookies determine the result of the
	 * comparision. Note: this method produces natural ordering if Cookies that is
	 * inconsistent with equals. This means, if this method returns 0 this
	 * does <b>not</b> mean that <code>this.equals(obj)</code>.
	 * @param obj the cookie to be compared.
	 * @return a negative integer, zero, or a positive integer as this cookie is
	 * less than, equal to, or greater than the specified cookie.
	 */
	public int compareTo(Object obj) {
		if (!(obj instanceof Cookie)) {
			throw new ClassCastException();
		}
		Cookie c = (Cookie) obj;
		if (!domain.equals(c.domain)) {
			return domain.compareTo(c.domain);
		}
		if (path != null && c.path != null) {
			int comp = path.compareTo(c.path);
			if (comp < 0 && c.path.startsWith(path) || comp > 0 && path.startsWith(c.path)) {
				return -comp;
			}
		}
		if (!name.equals(c.name)) {
			return name.compareTo(c.name);
		}
		return 0;
	}

	/**
	 * Extracts the cookie parameters from a given "Set-Cookie" or
	 * "Set-Cookie2" header value.
	 * @param setCookieHeader  a valid cookie header from an HTTP response.
	 */
	private void initialize(String setCookieHeader) {
		if (setCookieHeader != null) {
			List parts = Tokenizer.partsOf(setCookieHeader, ";", 0, '=');
			if (parts.size() > 0) {
				Pair p = (Pair) parts.get(0);
				name = p.getName();
				value = p.getValue();

				for (int i = 1; i < parts.size(); ++i) {
					p = (Pair) parts.get(i);
					String s = p.getName().toLowerCase();
					if (version == -1 && s.equals("version")) {
						try {
							version = Integer.parseInt(p.getValue());
						} catch (NumberFormatException ex) {
							TRACE.catching("initialize(String)", ex);
							version = -1;
						}
					} else if (s.equals("domain")) {
						domain = p.getValue();
						if (!domain.startsWith(".")) {
							domain = "." + p.getValue();
						}
					} else if (s.equals("discard")) {
						discard = true;
					} else if (path == null && s.equals("path")) {
						path = p.getValue();
					} else if (comment == null && s.equals("comment")) {
						comment = p.getValue();
					} else if (commentURL == null && s.equals("commenturl")) {
						commentURL = p.getValue();
					} else if (maxAge == -1L && s.equals("max-age")) {
						try {
							maxAge = Long.parseLong(p.getValue());
						} catch (NumberFormatException ex) {
							TRACE.catching("initialize(String)", ex);
							maxAge = -1L;
						}
						if (maxAge != -1L) {
							expirationTime = System.currentTimeMillis() + maxAge * 1000;
						}
					} else if (portList == null && s.equals("port")) {
						ports = p.getValue();
						List plist = Tokenizer.partsOf(ports, ",");
						portList = new int[plist.size()];
						try {
							for (int j = 0; j < portList.length; ++j) {
								portList[j] = Integer.parseInt((String) plist.get(j));
							}
							Arrays.sort(portList);
						} catch (NumberFormatException ex) {
							TRACE.catching("initialize(String)", ex);
							portList = null;
						}
					} else if (s.equals("secure")) {
						secure = true;
					}
				}
			}
		}
	}

	/**
	 * Check the validity of the cookie.
	 * Checks that name, value and domain are defined. Checks that the domain
	 * is well-formed and matches the request domain. Checks that the request
	 * port is in the port list (if a port list is defined).
	 * @param hostDomain  the domain of the host
	 * @param hostPort  the port of the host
	 * @param lazy  if true, the method does not check, whether the domain of
	 * the cookie matches the domain of the host
	 * @return true if the cookie is valid.
	 */
	private boolean checkValidity(String hostDomain, int hostPort, boolean lazy) {
		if (name == null || value == null || name.length() ==0 ||
			value.length() == 0 || version <= 0 || domain == null || domain.length() == 0) 
		{
			return false;
		}
		
		// if an explicitly specified domain contains no "." the cookie is rejected
		// according to RFC2956.
		if (!domain.equals(".local") && domain.indexOf('.', 1) == -1) {
			return false;
		}
		
		// if the domain of the request host does not  match the domain of the cookie
		// the cookie is rejected according to RFC2956 (if we do not operate in lazy mode)
		// furthermore if an explicit port list is specified, but the port of the request host
		// is not in that list, the cookie is rejected according to RFC2956.
		if (!lazy) {			
			if (!hostDomain.equals(domain)) {
				return false;
			}
			if (portList != null) {
				if (hostPort > 0 && Arrays.binarySearch(portList, hostPort) < 0) {
					return false;
				}
			}			
		}
		return true;
	}

}
