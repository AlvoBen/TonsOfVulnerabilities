package com.tssap.dtr.client.lib.protocol;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.impl.URLScanner;
import com.tssap.dtr.client.lib.protocol.requests.http.GetRequest;
import com.tssap.dtr.client.lib.protocol.util.Pair;
import com.tssap.dtr.client.lib.protocol.util.Tokenizer;

/**
 * Represents a Uniform Resource Locator, a pointer to a "resource" on the World Wide Web.
 * A URL can be broken into several parts, like protocol, host, port, path, query string
 * and fragment. Unlike the equivalent class in the JAVA SDK this implementation is
 * intented to parse and analyze URLs strings, but not to connect to a server and retrieve
 * the content of a web resource.
 * Important: One of the major motivations for implementing this class
 * was that java.net.URL does neither support nor recognize a protocol
 * specifier "https"
 */
public class URL {

	private String url;
	private String webLocator;
	private String authority;

	private String protocol;
	private String host;
	private boolean isIPAddress;
	private int port = -1;
	private String path;
	private String query;
	private String fragment;

	private List queryParams;
	private Map queryParamsByName;

	/** trace location*/
	private static Location TRACE = Location.getLocation(URL.class);	

	/**
	 * Creates a new URL instance from the given string. The string
	 * provided must conform to the URL specific subset of RFC2396
	 * ("Uniform Resource Identifiers (URI): Generic Syntax"). This means,
	 * if the string contains for example unicode letters it must be URL
	 * encoded before this constructor is called. Otherwise the URL is
	 * rejected with a MalformedURLException.
	 * @param str  the string used to initialize this URL
	 * @throws MalformedURLException  if the string is not a valid URL
	 */
	public URL(String str) throws MalformedURLException {
		this.url = str;
		try {
			parse(str, false);
		} catch (MalformedURLException ex) {
			throw ex;
		} catch (IOException e) {
			TRACE.catching(e);
			throw new IllegalArgumentException("Reading from string failed (IOException)");
		}
	}

	/**
	 * Create a new URL instance from the given protocol and host.
	 * @param protocol  the protocol 
	 * @param host  the host
	 * 
	 */
	public URL(String protocol, String host) {
		this(protocol, host, -1, null, null, null);
	}

	/**
	 * Create a new URL instance from the given protocol, host and path.
	 * @param protocol  the protocol 
	 * @param host  the host
	 * @param path  the path
	 */
	public URL(String protocol, String host, String path) {
		this(protocol, host, -1, path, null, null);
	}
	
	/**
	 * Create a new URL instance from the given protocol, host and port.
	 * @param protocol  the protocol 
	 * @param host  the host
	 * @param port  the port. A port number of -1 indicates that the
	 * default port number for the given protocol should be used
	 */
	public URL(String protocol, String host, int port) {
		this(protocol, host, port, null, null, null);
	} 	

	/**
	 * Create a new URL instance from the given protocol, host, port and path.
	 * @param protocol  the protocol 
	 * @param host  the host
	 * @param port  the port. A port number of -1 indicates that the
	 * default port number for the given protocol should be used
	 * @param path  the path
	 */
	public URL(String protocol, String host, int port, String path) {
		this(protocol, host, port, path, null, null);
	} 
	
	/**
	 * Create a new URL instance from the given protocol, host, port, path,
	 * query and fragment.
	 * @param protocol  the protocol 
	 * @param host  the host
	 * @param port  the port. A port number of -1 indicates that the
	 * default port number for the given protocol should be used
	 * @param path  the path
	 * @param query  a query string
	 * @param fragment a fragment
	 */
	public URL(String protocol, String host, int port, String path, String query, String fragment) 
	{
		if (protocol != null) {
			this.protocol = protocol;
			this.port = (port<=0 || port>=65535)? Protocol.getDefaultPort(protocol) : port;						 
		}
		if (host != null) {
			this.host = host.trim();
		}		
		if (path != null) {
			this.path = (path.startsWith("/"))? path : "/" + path;
		}
		this.query = query;
		this.fragment = fragment;
	} 	

	/**
	 * Creates a new URL instance from the given <code>baseUrl</code>
	 * and superimposes it with the given string.
	 * <p>For example:
	 * <code>
	 * String s = "/servlet/public/~login#anchor";
     * URL base = new URL("http", "www.sap.com");
     * URL url = new URL(base, s);
	 * </code>
	 * could be used to combine a web locator with a path.
	 * @param baseUrl  a valid URL 
	 * @param s  the string to superimpose
	 */
	public URL(URL baseUrl, String s) throws MalformedURLException {
		this.protocol = baseUrl.getProtocol();
		this.host = baseUrl.getHost();
		this.port = baseUrl.getPort();
		this.path = baseUrl.getPath();
		this.query = baseUrl.getQuery();
		this.fragment = baseUrl.getFragment();
		try {
			parse(s, true);			
		} catch (MalformedURLException ex) {
			throw ex;		
		} catch (IOException e) { // should never happen
			TRACE.catching(e);
			throw new IllegalStateException("Reading from string failed");
		}
	}
	
	
	/**
	 * Creates a new URL instance as combination of
	 * two URL fragments. Could be used to combine a web locator with 
	 * a path, or attach a query to an existing URL. The <code>right<code> string
	 * overlays the <code>left</code> string to form a valid URL. For example
	 * the combination of "http://sap.com/base" and "/another_base/index.html"
	 * would be "http://sap.com/another_base/index.html" whereas "another_base/index.html"
	 * would result in "http://sap.com/base/another_base/index.html".
	 * Note, both strings must be valid parts of an URL, and the result iself must be
	 * a valid URL.
	 * @param left  the left part of the URL
	 * @param right  the right part of the URL
	 */
	public static URL combine(String left, String right) throws MalformedURLException {
		URL baseUrl = new URL(left);
		return new URL(baseUrl, right);
	}	

	/**
	 * Validates the URL
	 * @throws MalformedURLException if the URL is not valid
	 */
	public void validate() throws MalformedURLException {
		try {
			parse(toString(), false);
		} catch (MalformedURLException ex) {
			throw ex;			
		} catch (IOException e) { // should never happen
			TRACE.catching(e);
			throw new IllegalStateException("Reading from string failed");
		}		
	}

	/**
	 * Returns the protocol part of the URL.
	 * @return either "http" or "https", or null if the URL consists only
	 * of an absolute path
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * Returns the host part of the URL.
	 * @return the host, or null if the URL consists only of an absolute path.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Returns the port number of the URL.
	 * @return the port number, or -1 if the port number is unknown. If a valid
	 * protocol is set for the URL the default port for that protocol is returned.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns the domain of the host. The domain of a host is
	 * the part right to (but including) the first dot.
	 * @return the domain of the host, or ".local" if the host belongs
	 * to the local domain or no host has been set at all.
	 */
	public String getDomain() {
		String domain = ".local";
		if (host != null) {	
			int n = host.indexOf('.');
			if (n > 0) {
				domain = host.substring(n);
			}
		}
		return domain;
	}
	
	/**
	 * Checks whether the host of this URL has the form of
	 * an IP address. Note, IP addresses and ordinary host names
	 * can only be distinuished by the first character of their top-level
	 * domain: if the top-level domain start with a digit, it is an
	 * IP address, otherwise it is a host name.
	 * @return true, if the host has the form of an IP address
	 */
	public boolean isIPAddress() {
		return isIPAddress;
	}
	
	/**
	 * Returns the IP address of the host.
	 * @throws UnknownHostException  if no IP address for the host could be found.
	 */
	public InetAddress getIPAddress() throws UnknownHostException {
		if (host == null) {
			throw new UnknownHostException("No host defined for this URL");
		}
		return InetAddress.getByName(host);
	}
	
	/**
	 * Returns the authority part of the URL (including host and port)
	 * @return the authority, or null if the URL consists only of an
	 * absolute path
	 */
	public String getAuthority() {
		if (authority == null && host != null) {
			StringBuffer s = new StringBuffer();
			s.append(host);
			if (port > 0  &&  port != Protocol.getDefaultPort(protocol)) {
				s.append(":").append(port);
			}
			authority = s.toString();						
		}
		return authority;
	}

	/**
	 * Returns the web locator part of the URL (including protocol, host and
	 * port.
	 * @return the web locator, or null if the URL consists only of an
	 * absolute path
	 */
	public String getWebLocator() {
		if (webLocator == null && protocol != null) {
			StringBuffer s = new StringBuffer();
			s.append(protocol).append("://");
			s.append(getAuthority());
			webLocator = s.toString();
		}
		return webLocator;
	}

	/**
	 * Returns the path part of the URL (or "/" if no path has been specified).
	 * @return the path of the URL (in absolute form, i.e. with a leading "/"),
	 * but without query and fragment parts 
	 */
	public String getPath() {
		return (path != null)? path : "/";
	}
	
	/**
	 * Returns the resource part of the URL including path, query and fragment
	 * parts.
	 * @return the path of the URL (in absolute form, i.e. with a leading "/"),
	 * including query and fragment parts 
	 */
	public String getResource() {
		StringBuffer s = new StringBuffer();
		s.append(getPath());
		if (query != null) {
			s.append("?").append(query);
		}
		if (fragment != null) {
			s.append("#").append(fragment);
		}
		return s.toString();
	}	

	/**
	 * Returns the optional query string of the URL (the URL part after "?").
	 * @return the query string (without leading "?"), or null
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Returns the query parameter with the given identifier (if the query
	 * string was given as a list of name-value pairs).
	 */
	public String getQueryParameter(String name) {
		if (queryParamsByName == null && query != null) {
			queryParamsByName = new HashMap();
			Iterator iter = getQueryParameters();
			while (iter.hasNext()) {
				Pair item = (Pair) iter.next();
				queryParamsByName.put(item.getName(), item.getValue());
			}
		}
		return (queryParamsByName != null) ? (String) queryParamsByName.get(name) : null;
	}

	/**
	 * Returns an iterator over the query parameters of the URL (if the query
	 * string was given as a list of name-value pairs).
	 * @return an Iterator over Pair instances, or null if there are
	 * no parameters.
	 */
	public Iterator getQueryParameters() {
		if (queryParams == null && query != null) {
			queryParams = Tokenizer.partsOf(query, "&", 0, '=');
		}
		return (queryParams != null) ? queryParams.iterator() : null;
	}

	/**
	 * Returns the optional fragment identifier (sometimes also called "anchor").
	 * @return the fragment string (without leading "#"), or null
	 */
	public String getFragment() {
		return fragment;
	}
	
	
	/**
	 * Creates a HTTP connection from this URL.
	 * @return a connection, or null if this URL does not
	 * define a valid web locator (host or protocol undefined).
	 */
	public Connection openConnection() {
		Connection conn = null;
		if (protocol != null  &&  host != null) {
			try {
				conn = new Connection(new URL(getProtocol(), getHost(), getPort()));
			} catch (MalformedURLException e) {
				TRACE.catching(e);
			}
		}		 
		return conn;
	}
	
	/**
	 * Opens a connection to this URL and returns a response stream 
	 * for reading from that connection. Issues a GET request to the path
	 * of the URL (including query and fragment indentifier).
	 * @return a response stream, or null if the request did not result
	 * in a <code>200 OK</code> response.
	 */
	public IResponseStream openStream() throws IOException, HTTPException {
		Connection conn = openConnection();
		GetRequest req = new GetRequest(getResource());
		IResponse resp = conn.send(req, true);
		return (resp.getStatus() == Status.OK)? resp.getContent() : null;
	}	

	/**
	 * Extracts the domain part of the given host URL. For example, the domain
	 * of the host URL "www.sap.com" is ".sap.com". For host URLs that do not contain 
	 * domain separators (".") like "localhost", the string ".local" is returned
	 * to indicate, that the host belongs to the local domain.
	 * @param host  a host identifier, web locator or an URL containing a host.
	 * @return The domain part of the given host URL (including a leading dot),
	 * or ".local", if the given host belongs to the local domain.
	 */
	public static String getDomainOf(String host) {
		String result = null;
		int n = host.indexOf('.');
		int m = host.lastIndexOf(':');
		if (n < 0 || host.indexOf('.', n + 1) < 0) {
			result = ".local";
		} else {
			result = (m < 0) ? host.substring(n) : host.substring(n, m);
		}
		return result;
	}


	
	/**
	 * Checks whether the given string represents a valid URL
	 * @param str  the string to check
	 * @return true, if the string represents a valid URL
	 */
	public static boolean isValidURL(String s) {
		try {
			new URL(s);
		} 
		catch (MalformedURLException e) { //$JL-EXC$
			return false;
		} 
		return true;
	}

	/**
	 * Returns the string representation of this URL.
	 * @return the URL as string in canconical format
	 */
	public String toString() {
		if (url == null) {
			StringBuffer s = new StringBuffer();
			String locator = getWebLocator();
			if (locator != null) {
				s.append(locator);
			}
			if (path != null) {
				s.append(getPath());
			}
			if (query != null) {
				s.append("?").append(getQuery());
			}
			if (fragment != null) {
				s.append("#").append(getFragment());
			}
			url = s.toString();
		}
		return url;
	}
	

	/**
	 * Compares two URLs. 
	 * Two URL objects are equal if they have the same protocol and reference 
	 * the same host, the same port number on the host, and the same 
	 * path and anchor on the host.
	 * @param obj  the URL to compare against.
	 * @return true if the objects are the same; false otherwise.
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof URL)) {
			return false;
		}
		URL u = (URL)obj;
		return ((getProtocol() != null)? getProtocol().equals(u.getProtocol()) : u.getProtocol()==null)
			&& ((getHost() != null)? getHost().equals(u.getHost()) : u.getHost()==null)
			&& (getPort() == u.getPort())
			&& ((getPath() != null)? getPath().equals(u.getPath()) : u.getPath()==null)
			&& ((getFragment()!= null)? getFragment().equals(u.getFragment()) : u.getFragment()==null);				
	}

	/**
	 * Creates an integer suitable for hash table indexing.
	 * @return a hash code for this URL.
	 */
	public int hashCode() {
		int result = 17;
		result = (getProtocol() != null)? 37*result + getProtocol().hashCode() : result;
		result = (getHost() != null)? 37*result + getHost().hashCode() : result;
		result = (getPort() != -1)? 37*result + getPort() : result;
		result = (getPath() != null)? 37*result + getPath().hashCode() : result;
		result = (getFragment() != null)? 37*result + getFragment().hashCode() : result;
		return result;		
	}
	

	/**
	 * Parses the given URL string into its constituents.
	 */
	private void parse(String str, boolean relative) throws MalformedURLException, IOException 
	{
		int token = URLScanner.EOS;
		URLScanner scanner = new URLScanner(new StringReader(str));
		if (relative) {
			scanner.yybegin(URLScanner.YYINITIAL_RESOURCE);
		}
		while ((token = scanner.next_token()) != URLScanner.EOS) {
			switch (token) {
				case URLScanner.SYM_PROTOCOL :
					protocol = scanner.yytext();
					break;
				case URLScanner.SYM_HOST_IP:
					host = scanner.yytext();
					isIPAddress = true;
					break;
				case URLScanner.SYM_HOST_NAME :
					host = scanner.yytext();
					isIPAddress = false;
					break;
				case URLScanner.SYM_PORT :
					String portStr = scanner.yytext();
					try {
						port = Integer.parseInt(portStr);
					} catch (NumberFormatException ex) {
						throw new MalformedURLException("Invalid port [not a number]");
					}
					break;
				case URLScanner.SYM_PATH :
					path = "/" + scanner.yytext();
					break;
				case URLScanner.SYM_RELPATH : 
					if (path==null) {
						path ="/";
					} else if (!path.endsWith("/")) {
						path += "/";	
					}
					path += scanner.yytext();		
					break;
				case URLScanner.SYM_QUERY :
					query = scanner.yytext();
					break;
				case URLScanner.SYM_FRAGMENT :
					fragment = scanner.yytext();
					break;
				default:
					break;					
			}
		}
		
		if (protocol != null) {
			if (!Protocol.isValidProtocol(protocol)) {
				throw new MalformedURLException("Unknown protocol [" + protocol + "]");
			}
			if (port < 0) {
				port = Protocol.getDefaultPort(protocol);
			} else if (port==0 || port > 65535) {
				throw new MalformedURLException("Invalid port '" + port + 
					"' [outside allowed range 1..65535]");
			}				
		}			
		
		if (protocol != null && host == null) {
			throw new MalformedURLException("URL with a protocol specifier must have a host");
		}
		if (host != null && protocol == null) {
			throw new MalformedURLException("URL with a host must have a protocol specifier");
		}
		if (host == null && path == null) {
			throw new MalformedURLException("URL must at least have a host or a path part");
		}
		
		if (isIPAddress()) {
			List segments = Tokenizer.partsOf(host, ".");
			int sz = segments.size();
			if (sz!=4  && sz!=6) {
				throw new MalformedURLException("URL is not a valid IPv4 or IPv6 address " +
					"[expected to find 4 or 6 segments, but found " + sz + "]");
			}
			for (int i=0; i<sz; ++i) {
				try {
					int n = Integer.parseInt((String)segments.get(i));
					if (n>255) {
						throw new MalformedURLException("URL is not a valid IP address " +							"[segement " + i+1 + " outside allowed range 0..255]");
					}
				} catch (NumberFormatException e) {
					TRACE.catching(e);
				}
			} 
		}							
	}
	


}
