package com.tssap.dtr.client.lib.protocol;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.impl.Response;
import com.tssap.dtr.client.lib.protocol.requests.RequestBase;
import com.tssap.dtr.client.lib.protocol.session.Certificates;
import com.tssap.dtr.client.lib.protocol.session.SessionContext;
import com.tssap.dtr.client.lib.protocol.socks.ISOCKSProvider;
import com.tssap.dtr.client.lib.protocol.socks.SOCKSv5Provider;
import com.tssap.dtr.client.lib.protocol.ssl.IAIKSecureSocketProvider;
import com.tssap.dtr.client.lib.protocol.ssl.ISecureSocketProvider;
import com.tssap.dtr.client.lib.protocol.ssl.JSSESecureSocketProvider;
import com.tssap.dtr.client.lib.protocol.streams.RequestStream;
import com.tssap.dtr.client.lib.protocol.streams.ResponseStream;
import com.tssap.dtr.client.lib.protocol.util.Query;
import com.tssap.dtr.client.lib.protocol.util.Tokenizer;

/**
 * This class prvides HTTP and HTTPS connections to a certain host.
 * <p>In contrast to the URLConnection class that is part of the Java SDK this class
 * provides advanced functionality like communication through proxies, authentication,
 * session management with cookies, HTTPS with client and server certification,
 * persistent connections, true streaming for request and response bodies,
 * chunked data transfer, compression, fine granular handling of timeouts,
 * and automatical request repetitions.</p>
 * <p>This class encapsulated and manages the socket and data streams of
 * an HTTP connection transparently. This means, sockets and data streams
 * are created or destroyed, respectively, on demand when requests are to be
 * transmitted over the connection. If the host supports persistent connection
 * as defined by HTTP/1.1 the socket usually remains open
 * between consecutive requests saving system resources and improving performance.
 * The handling of the socket state then is subject to negotiation between the
 * <code>Connection</code> instance and the host.
 * However, the <code>Connection</code> class provides also
 * the methods <code>open</code> and <code>close</code> that allow to control
 * the state of the socket manually. Directly after creation (or after a call to
 * the <code>close</code> method) a <code>Connection</code> instance is merely a
 * data container holding only the relevant connection parameters.</p>
 * <p>Sending a request and receiving the corresponding response is very simple with
 * the help of the <code>send</code> method.
 */
public final class Connection implements IConnection {

	/** The socket used by this connection */
	private Socket socket;
	/** The input stream wrapper for the socket stream
	 * providing transparent chunking */
	private ResponseStream in;
	/** The output stream wrapper for the socket stream
	 *  providing transparent chunking */
	private RequestStream out;

	/** if true, the socket has been closed explicitly */
	private boolean socketClosed = true;
	/** if true, the socket must be closed and re-opened
	 * before the next request */
	private boolean closeBeforeUse;

	/** The session context with authentication information and cookies */
	private SessionContext context;

	/** URL of the remote host */
	private String host;
	/** Port number used by the remote host */
	private int port = IConnectionTemplate.DEFAULT_PORT;
	/** Path prefix for request URLs */
	private String basePath = "/";

	/** URL of a proxy, or null */
	private String proxy;
	/** Port number used by the proxy */
	private int proxyPort;
	/** If true then the connection uses the specified proxy */
	private boolean useProxy = false;
	/** If true (and a proxy selected) the proxy is tunneled with the CONNECT command */
	private boolean tunnelProxy = false;
	/** If true, the proxy is connected via SOCKS protocol */
	private boolean useSOCKS = false;
	/** The SOCKS protocol provider to use */
	private ISOCKSProvider socksProvider;
	/** Hosts and domains to bypass when connection through socket */
	private Set excludedHosts;
	private Set excludedDomains;

	/** The protocol to use, either HTTP or HTTPS */
	private Protocol protocol = Protocol.HTTP;
	/** If true, the protocol is secure, i.e. HTTPS */
	private boolean secureProtocol = false;
	/** If true, try to upgrade the connection to HTTPS */
	private boolean upgradeProtocol = false;
	/** The SSL socket provider to use */
	private ISecureSocketProvider secureSocketProvider;

	/** The HTTP version to use, eith HTTP/1.0 or HTTP/1.1 */
	private String version = IConnectionTemplate.DEFAULT_HTTP_VERSION;
	/** If true, the connection is forced to use HTTP/1.0 protocol */
	private boolean usingHTTP10 = false;

	/** The user agent identifier sent in request headers */
	private String userAgent = IConnectionTemplate.USER_AGENT;

	/** The timeout applied to read operations from the socket (in milliseconds) */
	private int readTimeout = IConnectionTemplate.DEFAULT_TIMEOUT;
	/** The timeout applied to opening a socket */
	private int connectTimeout = IConnectionTemplate.DEFAULT_CONNECT_TIMEOUT;
	/** The expiration timeout for persistent connections. */
	private int expirationTimeout = IConnectionTemplate.EXPIRATION_TIMEOUT;

	/** The time when the currently opened socket expires, i.e. the time when
	 *  socket was openend + EXPIRATION_TIMEOUT or the last request has been issued +
	 *  EXPIRATION_TIMEOUT */
	private long socketExpirationTime;

	/**
	 * Determines whether and how often request may be repeated if the
	 * first request failed due to an IOError or an Authentication response.
	 * The default is 1.
	 */
	private int requestRepetitions = IConnectionTemplate.DEFAULT_REPETITIONS;
	/**
	 * Determines whether request should be repeated also for timeouts on
	 * read operations. The default is fals;
	 */
	private boolean repeatOnTimeout = false;


	/**
	 * If true, the connection automatically follows redirects.
	 * This setting applies to response status codes 302 Moved Temporarily,
	 * 302 Found and 307 Temporary Redirect.
	 * The default is true.
	 */
	private boolean followRedirects = true;

	/**
	 * If true, the connection also follows permanent redirects.
	 * This settting applies to response status code 301 Moved Permanent.
	 * The default is false.
	 */
	private boolean followPermanentRedirects = false;

	/**
	 * If true, the connection also follows redirects that change
	 * the domain of the connection.
	 * The default is false.
	 */
	private boolean followDomainRedirects = false;

	/**
	 * If true, the connection also follows redirects to use a
	 * proxy instead of a direct connection.
	 * The default is false.
	 */
	private boolean followProxyRedirects = false;

	private boolean followRedirectAllMethods = true;

	/** The maximum number of redirects. */
	private int maxRedirects = 1;


	/** Determines whether the connection should send AcceptEncoding header */
	private boolean compressedResponses = false;
	/** Determines whether the connection should send AcceptEncoding header */
	private boolean compressedRequests = false;
	/** Determines the algorithm used to compress requests. */
	private String compressionAlgorithm = IConnectionTemplate.DEFAULT_COMPRESSION;

	/** Calculate a digest value for response bodies */
	private boolean digestEnabled = false;
	/** Determines the algorithm used to calculate digests. */
	private String digestAlgorithm = IConnectionTemplate.DEFAULT_DIGEST;

	/** The size of the socket's receive buffer */
	private int receiveBufferSize = -1;
	/** The size of the socket's send buffer */
	private int sendBufferSize = -1;
	/** The minimum buffer size */
	private int MIN_BUFFER_SIZE = 256;

	/** trace location*/
	private static Location TRACE = Location.getLocation(Connection.class);

	/** Constants **/
	private static final String CRLF = "\r\n";
	private static final int CONTACT = 0;
	private static final int CONTACT_PROXY = 1;
	private static final int CONTACT_SOCKS =2;
	private static final int CONTACT_PROXY_SSL = 3;
	private static final int CONTACT_HOST = 4;
	private static final int CONTACT_HOST_SSL = 5;


	/**
	 * Creates a connection for the given URL.
	 * @param url   a valid URL with at least a protocol specifier (either "http"
	 * or "https") and a host. Optionally a port number and a base path may be given.<br/>
	 * @throws MalformedURLException  if the URL is not valid.
	 */
	public Connection(String url) throws MalformedURLException {
		parseURL(url);
		if (TRACE.beInfo()) {
			TRACE.infoT("Connection(String)",
						"connection created [url={0}]",
						new Object[]{url}
			);
		}
	}

	/**
	 * Creates a connection for the given URL and opens a session for
	 * the specified context.
	 * @param url a valid URL string with at least a protocol specifier (either "http"
	 * or "https") and a host. Optionally a port number and a base path may be given.
	 * @param context   a session context providing user and session releated information.
	 * @throws MalformedURLException  if the URL is not valid.
	 */
	public Connection(String url, ISessionContext context) throws MalformedURLException {
		this(url);
		setSessionContext(context);
	}

	/**
	 * Creates a connection for the given URL.
	 * @param url a valid URL with at least a protocol specifier (either "http"
	 * or "https") and a host. Optionally a port number and a base path may be given.
	 * @throws MalformedURLException  if the URL is not valid.
	 */
	public Connection(URL url) throws MalformedURLException {
		parseURL(url);
		if (TRACE.beInfo()) {
			TRACE.infoT(
				"Connection(URL)",
				"connection created [url={0}]",
				new Object[]{url.getWebLocator()}
			);
		}
	}

	/**
	 * Creates a connection for the given URL and opens a session for
	 * the specified context.
	 * @param url   a valid URL with at least a protocol specifier (either "http"
	 * or "https") and a host. Optionally a port number and a base path may be given.
	 * @param context   a session context providing user and session releated information.
	 * @throws MalformedURLException  if the URL is not valid.
	 */
	public Connection(URL url, ISessionContext context) throws MalformedURLException {
		this(url);
		setSessionContext(context);
	}

	/**
	 * Creates a connection for the specified host and port.
	 * @param host   a valid host URL or IP address.
	 * @param port   a port number.
	 */
	public Connection(String host, int port) {
		setHost(host);
		setPort(port);
		if (TRACE.beInfo()) {
			TRACE.infoT(
				"Connection(String,int)",
				"connection created [url={0}]",
				new Object[]{getUrl()}
			);
		}
	}

	/**
	 * Creates a connection for the specified host and port and opens a session for
	 * the specified context.
	 * @param host a valid host URL or IP address.
	 * @param port a port number.
	 * @param context a session context providing user and session releated information.
	 */
	public Connection(String host, int port, ISessionContext context) {
		this(host, port);
		setSessionContext(context);
	}

	/**
	 * Creates a new connection from the given template.
	 * All parameters and the session context are copied from the
	 * original template.
	 * @param template the connection template from which to copy.
	 */
	public Connection(IConnectionTemplate template) {
		setHost(template.getHost());
		setPort(template.getPort());
		setProxyHost(template.getProxyHost());
		setProxyPort(template.getProxyPort());
		setUseProxy(template.isUsingProxy());
		setTunnelProxy(template.isTunnelingProxy());
		setProtocol(template.getProtocol());
		setSecureSocketProvider(template.getSecureSocketProvider());
		setFollowRedirects(template.getFollowRedirects());
		setFollowPermanentRedirects(template.getFollowPermanentRedirects());
		setBasePath(template.getBasePath());
		setHTTPVersion(template.getHTTPVersion());
		setUserAgent(template.getUserAgent());
		setSocketReadTimeout(template.getSocketReadTimeout());
		setSocketConnectTimeout(template.getSocketConnectTimeout());
		setSocketExpirationTimeout(template.getSocketExpirationTimeout());
		setRequestRepetitions(template.getRequestRepetitions(), template.getRepeatOnTimeout());
		setReceiveBufferSize(template.getReceiveBufferSize());
		setSendBufferSize(template.getSendBufferSize());
		enableResponseCompression(template.isResponseCompressionEnabled());
		enableRequestCompression(template.isRequestCompressionEnabled());
		setCompressionAlgorithm(template.getCompressionAlgorithm());
		setSessionContext(template.getSessionContext());
		if (TRACE.beInfo()) {
			TRACE.infoT(
				"Connection(IConnectionTemplate)",
				"connection created [url={0}]",
				new Object[]{getUrl()}
			);
		}
	}

	/**
	 * Returns the complete URL of the connection including the basePath,
	 * i.e. protocol://host:port/basePath.
	 * @see IConnectionTemplate#getUrl()
	 */
	public String getUrl() {
		return getProtocol() + "://" + getHost() + ":" + getPort() + getBasePath();
	}

	/**
	 * Calculates the URL of a resource with a given relative or absolute path.
	 * If a base path has been set and path does not itself start with
	 * "/", the method returns the concatenation of base path and path.
	 * Host, port and protocol are taken from the connection.
	 * @param path   any valid relative or absolute path according to RFC2616
	 * @return The absolute URL of the resource
	 */
	public URL getUrl(String path) {
		return new URL(protocol.toString(), host, port, getAbsolutePath(path));
	}

	/**
	 * Sets the URL of the connection.
	 * @param url  an URL in string form, i.e. protocol://host:port/basePath.
	 * The URL must at least provide a protocol (either "http" or "https") and
	 * a host.
	 */
	public void setUrl(String url) throws MalformedURLException {
		parseURL(url);
	}

	/**
	 * Sets the URL of the connection.
	 * @param url  an URL that at least provides a protocol (either "http" or
	 * "https") and a host.
	 */
	public void setUrl(URL url) throws MalformedURLException {
		parseURL(url);
	}

	/**
	 * Returns the host of this connection.
	 * @return The name of the host.
	 * @see IConnectionTemplate#getHost()
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Returns the host domain. The domain of a host is
	 * the part right to (but including) the first dot.
	 * @return the domain of the host, or ".local" if the host belongs
	 * to the local domain.
	 */
	public String getDomain() {
		String domain = ".local";
		int n = host.indexOf('.');
		if (n > 0) {
			domain = host.substring(n);
		}
		return domain;
	}

	/**
	 * Sets the host for this connection.
	 * The new setting will become
	 * effective for the next request executed by the send method.
	 * @param host the URL or IP address of a remote host.
	 */
	public void setHost(String host) {
		this.host = host;
		closeBeforeUse = true;
	}

	/**
	 * Returns the port number of this connection.
	 * @return A port number. e.g. '80'.
	 * @see IConnectionTemplate#getPort()
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port number for this connection.
	 * <p>The new setting will become
	 * effective for the next request executed by the send method.
	 * Note, if the connection is open it will be
	 * closed and reopended automatically before the next request is
	 * issued.</p>
	 * @param port a valid port number, usually set to '80' (default),
	 * '1080' or '8080' for HTTP.
	 * connections.
	 */
	public void setPort(int port) {
		this.port = port;
		closeBeforeUse = true;
	}

	/**
	 * Returns the default port for the given protocol.
	 * @return either "80" for HTTP, or "443" for "HTTPS"
	 */
	public static int getDefaultPort(Protocol protocol) {
		return (protocol == Protocol.HTTP) ? DEFAULT_PORT : DEFAULT_SSL_PORT;
	}

	/**
	 * Returns the proxy of this connection.
	 * @return The URL of the proxy.
	 * @see IConnectionTemplate#getProxyHost()
	 */
	public String getProxyUrl() {
		String url = getProxyHost();
		if (getProxyPort() > 0 ) {
			url += ":" + getProxyPort();
		}
		return url;
	}

	/**
	 * Sets the URL of the proxy used by this connection.
	 * @param url  a valid URL.
	 */
	public void setProxyUrl(String url) throws MalformedURLException {
		parseProxyURL(url);
	}

	/**
	 * Sets the URL of the proxy used by this connection.
	 * @param url  a valid URL.
	 * @param useProxy  if true, the usage of the given proxy is enabled
	 */
	public void setProxyUrl(String url, boolean useProxy) throws MalformedURLException {
		setProxyUrl(url);
		setUseProxy(useProxy);
	}

	/**
	 * Sets the URL of the proxy used by this connection.
	 * @param url  a valid URL.
	 */
	public void setProxyUrl(URL url) throws MalformedURLException {
		parseProxyURL(url);
		closeBeforeUse = true;
	}

	/**
	 * Sets the URL of the proxy used by this connection.
	 * @param url  a valid URL.
	 * @param useProxy  if true, the usage of the given proxy is enabled
	 */
	public void setProxyUrl(URL url, boolean useProxy) throws MalformedURLException {
		setProxyUrl(url);
		setUseProxy(useProxy);
	}

	/**
	 * Returns the proxy host of this connection.
	 * @return The URL of the proxy.
	 * @see IConnectionTemplate#getProxyHost()
	 */
	public String getProxyHost() {
		return proxy;
	}

	/**
	 * Returns the port used by the proxy of this connection.
	 * @return The port used by the proxy.
	 * @see IConnectionTemplate#getProxyPort()
	 */
	public int getProxyPort() {
		return proxyPort;
	}

	/**
	 * Sets a proxy host for this connection.
	 * The new setting will become
	 * effective for the next request executed by the send method.
	 * @param proxy the host URL or IP address of the proxy.
	 */
	public void setProxyHost(String proxy) {
		this.proxy = proxy;
		closeBeforeUse = true;
	}

	/**
	 * Sets the port used by the proxy of this connection.
	 * The new setting will become
	 * effective for the next request executed by the send method.
	 * @param proxyPort a valid port number.
	 */
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
		closeBeforeUse = true;
	}

	/**
	 * Enables or disables the usage of proxies.
	 * The new setting will become effective for the next request
	 * executed by the send method.<br/>
	 * @param useProxy if true, the connection uses the specified proxy.
	 */
	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
		closeBeforeUse = true;
	}

	/**
	 * Enables or disables the usage of proxies, but not for certain
	 * domains or hosts.
	 * @param useProxy  if true, the connection uses the specified proxy.
	 * @param exclusions  a list (separated with ',', ';' or '|') of
	 * domains (like ".example.org" or ".local") or dedicated
	 * hosts (like "www.example.org", "localhost" or "127.0.0.0").
	 * @see network property <code>http.nonProxyHosts</code>
	 */
	public void setUseProxy(boolean useProxy, String exclusions) {
		this.useProxy = useProxy;
		List parts = Tokenizer.partsOf(exclusions, ",;|");
		for (int i=0; i < parts.size(); ++i) {
			String part = (String)parts.get(i);
			if (part.startsWith(".") || part.startsWith("*.")) {
				if (excludedDomains == null) {
					excludedDomains = new HashSet();
				}
				excludedDomains.add(part);
			} else {
				if (excludedHosts == null) {
					excludedHosts = new HashSet();
				}
				excludedHosts.add(part);
			}
		}
		closeBeforeUse = true;
	}

	/**
	 * Determines whether the proxy used by this connection should
	 * be switched to tunneling mode.
	 * Note, proxy tunneling is by default switched on for HTTPS
	 * connections. You may switch tunneling off with this method.
	 * For plain HTTP protocol tunneling is not supported
	 * by all servers. Usually tunneling is limited to certain ports
	 * (like the default SSL port 443).
	 * The new setting will become
	 * effective for the next request executed by the send method.
	 * @param tunnelProxy  if true, the connection tries to tunnel the
	 * selected proxy.
	 */
	public void setTunnelProxy(boolean tunnelProxy) {
		this.tunnelProxy = tunnelProxy;
		closeBeforeUse = true;
	}

	/**
	 * Determines whether this connection should use the SOCKS
	 * protocol to connect to the proxy. Currently, only
	 * the SOCKS protocol version 5 with user/password
	 * authenticarion is supported.<br/>
	 * The new setting will become effective for the next request
	 * executed by the send method.
	 * @param useSOCKS  if true, the connection through the proxy is
	 * established using the SOCKS v5 protocol.
	 */
	public void setUseSOCKS(boolean useSOCKS) {
		this.useSOCKS = useSOCKS;
		closeBeforeUse = true;
	}

	/**
	 * Checks whether this connection uses a proxy.
	 * @return True if the connection uses a proxy.
	 * @see IConnectionTemplate#isUsingProxy()
	 */
	public boolean isUsingProxy() {
		return useProxy;
	}

	/**
	 * Checks whether this connection tunnels a proxy.
	 * @return True, if the connection tunnels a proxy.
	 * @see IConnectionTemplate#isTunnelingProxy
	 */
	public boolean isTunnelingProxy() {
		return tunnelProxy;
	}
	/**
	 * Checks whether this connection uses SOCKS
	 * protocol to connect to the proxy.
	 * @return True if the connection uses SOCKS
	 */
	public boolean isUsingSOCKS() {
		return useSOCKS;
	}


	/**
	 * Returns the HTTP version used for request sent over this connection.
	 * @return Either "HTTP/1.0" or "HTTP/1.1". The default is "HTTP/1.1".
	 * @see IConnectionTemplate#getHTTPVersion()
	 */
	public String getHTTPVersion() {
		return version;
	}

	/**
	 * Sets the HTTP version used for request sent over this connection.
	 * The new setting will become
	*  effective for the next request executed by the send method.
	 * @param version either "HTTP/1.0" or "HTTP/1.1".
	 * @throws IllegalArgumentException  if the specified version is not supported.
	 */
	public void setHTTPVersion(String version) {
		if ("HTTP/1.0".equalsIgnoreCase(version)) {
			usingHTTP10 = true;
			this.version = version;
		} else if ("HTTP/1.1".equalsIgnoreCase(version)) {
			this.version = version;
		} else {
			throw new IllegalArgumentException("HTTP version '" + version + "' not supported");
		}
	}

	/**
	 * Checks whether this connection uses HTTP/1.0.
	 * @return True, if this connection uses HTTP/1.0.
	 * @see IConnectionTemplate#usingHTTP10()
	 */
	public boolean usingHTTP10() {
		return usingHTTP10;
	}

	/**
	 * Returns the session context used by this connection.
	 * The session context is responsible for maintaining session cookies and care for
	 * authentication.
	 * @return A reference to the current session context.
	 */
	public ISessionContext getSessionContext() {
		return context;
	}

	/**
	 * Assigns a new session context to this connection.
	 * Note, the given context will be cloned with all parameters and cookies
	 * and then assigned to this connection.<br/>
	 * The new setting will become
	 * effective for the next request executed by the send method.
	 * @param context the new session context to use with this connection.
	 */
	public void setSessionContext(ISessionContext ctx) {
		if (ctx != null) {
			this.context = new SessionContext(ctx);
			if (TRACE.beInfo()) {
				TRACE.infoT(
					"setSessionContext(ISessionContext context)",
					"session context defined [user={0},auth={1}]",
					new Object[] {(context != null) ? context.toString() : "none" });
			}
		} else {
			TRACE.infoT(
				"setSessionContext(ISessionContext context)",
				"removing session context [[{0}]",
				new Object[] {(context != null) ? context.toString() : "none" });
			context = null;
		}
	}

	/**
	 * Returns the protocol used by this connection.
	 * @return Either Protocol.HTTP or Protocol.HTTPS.
	 * @see IConnectionTemplate#getProtocol()
	 */
	public Protocol getProtocol() {
		return protocol;
	}

	/**
	 * Sets the protocol used by this connection.
	 * Note, this method switches proxy tunneling on
	 * for protocol HTTPS, and off for protocol HTTP.
	 * See <code>setTunnelProxy</code> for details.
	 * The new setting will become
	 * effective for the next request executed by the send method.
	 * @param protocol  either <code>Protocol.HTTP</code>
	 * or <code>Protocol.HTTPS</code>.
	 */
	public void setProtocol(Protocol protocol) {
		if (protocol.equals(Protocol.HTTPS)) {
			secureProtocol = true;
			tunnelProxy = true;
		} else if (protocol.equals(Protocol.HTTP)) {
			secureProtocol = false;
			tunnelProxy = false;
		}
		if (equalsDefaultPort(port)) {
			port = getDefaultPort(protocol);
		}

		this.protocol = protocol;
		closeBeforeUse = true;
	}


	/**
	 * Checks whether this connection uses "https" as protocol.
	 * @return True if the connection uses "https".
	 * @see IConnectionTemplate#isSecureProtocol()
	 */
	public boolean isSecureProtocol() {
		return secureProtocol;
	}


	/**
	 * Sets the secure socket provider for this connection.
	 * If the connection is open while calling this method it will be
	 * closed and reopended in SSL mode using the new provider
	 * automatically before the next request is processed.
	 * @param provider  a provider for SSL sockets.
	 */
	public void setSecureSocketProvider(ISecureSocketProvider provider) {
		secureSocketProvider = provider;
		closeBeforeUse = true;
	}

	/**
	 * Returns the secure socket provider used by this connection.
	 * @return  the selected provider for SSL sockets, or null.
	 */
	public ISecureSocketProvider getSecureSocketProvider() {
		return secureSocketProvider;
	}


	/**
	 * Returns the base path used by this connection.
	 * The base path is used to resolve relative paths in request URLs (that do
	 * not start with a "/").
	 * Base paths always start with a leading "/" and end with an trailing "/".
	 * In terms of WebDAV, base path denotes always a folder. If a request is send
	 * over this connection and the request URI is a relative path according to
	 * RFC2616, the base path is concatenated with that path to form an absolute
	 * request URL.<br/>
	 * If no base path has been defined explicitly this method always returns
	 * the root path, i.e. "/".
	 * @return An absolute URL according to RFC2616.
	 * @see IConnectionTemplate#getBasePath()
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * Sets the base path used by this connection.
	 * The base path is used to resolve relative paths in request URLs (that do
	 * not start with a "/").<br/>
	 * The new setting will become
	 * effective for the next request executed by the send method.
	 * @param path   an absolute URL according to RFC2616. If <code>path</code>
	 * equals <code>null</code> or the empty string, base path is set to "/".
	 */
	public void setBasePath(String path) {
		if (path==null  ||  path.length()==0) {
			basePath = "/";
		} else {
			StringBuffer buf = new StringBuffer();
			if (!path.startsWith("/")) {
				buf.append('/');
			}
			buf.append(path);
			if (!path.endsWith("/")) {
				buf.append('/');
			}
			basePath = buf.toString();
		}
	}

	/**
	 * Calculates the absolute URI of a given path.
	 * If <code>path</code> already is an absolute URI (starting with "/")
	 * it is returned unmodified. If <code>path<code> is a relative path,
	 * the method returns the concatenation of base path and path.
	 * If <code>path</code> is <code>null</code> or the empty string, the
	 * method returns the base path of the connection ("/" by default).
	 * @param path   any valid relative or absolute path according to RFC2616
	 * @return The absolute path (always starting with '/').
	 */
	public String getAbsolutePath(String path) {
		if (path==null || path.length()==0) {
			return (basePath != null)? basePath : "/";
		} else if (!path.startsWith("/")) {
			return (basePath != null)? basePath + path : "/" + path;
		} else {
			return path;
		}
	}

	/**
	 * Returns the timeout intervall this connection waits for server responses.
	 * This timeout is applied to blocking read operation.
	 * @return The timeout in milliseconds.
	 * @see IConnectionTemplate#getSocketReadTimeout()
	 * @see java.net.Socket#getSoTimeout()
	 */
	public int getSocketReadTimeout() {
		return readTimeout;
	}

	/**
	 * Sets the timeout intervall this connection waits for server responses.
	 * This timeout is applied to blocking read operation.<br/>
	 * The new setting will become effective for the next request
	 * executed by the <code>send</code> method.
	 * @param timeout the timeout in milliseconds. Setting the timeout to
	 * zero (or a negative value) defines an infinite timeout for blocking read
	 * operations.
	 * @see java.lang.Socket#setSoTimeout(int)
	 */
	public void setSocketReadTimeout(int timeout) {
		readTimeout = (timeout>0)? timeout : 0;
		if (!socketClosed) {
			try {
				socket.setSoTimeout(readTimeout);
			} catch (SocketException e) {
				TRACE.catching("setSocketReadTimeout(int)", e);
				closeBeforeUse = true;
			}
		}
	}

	/**
	 * Returns the timeout intervall applied when the connection tries to open
	 * a socket to a remote server.
	 * @return The timeout in milliseconds.
	 * @see IConnectionTemplate#getSocketConnectTimeout()
	 */
	public int getSocketConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * Sets the timeout intervall applied when the connection tries to open
	 * a socket to a remote server.<br/>
	 * Note, due to limitations of the J2SE platform before version 1.4
	 * it is impossible to control the socket connect timeout precisely. The
	 * minimal possible timeout is platform dependent (usually a few seconds)
	 * and cannot be reduced further by this method. If <code>timeout</code>
	 * is larger than the default timeout of the platform, <code>open()</code>
	 * repeats the connect trials up to five times with an each doubled sleeping
	 * period inbetween.<br/>
	 * The new setting will become effective the next time a new socket
	 * has to be opened by the connection.
	 * @param  timeout  the timeout in milliseconds. Setting the value to zero
	 * or a negative number resets the timeout to the default timeout of the
	 * platform.
	 */
	public void setSocketConnectTimeout(int timeout) {
		connectTimeout = (timeout>=10)? timeout : 10;
	}

	/**
	 * Returns the timeout intervall after which the connection assumes
	 * that an open but unused socket has expired on the server. An expired socket
	 * then is closed and reopened when the next request is executed over this
	 * connection.
	 * @return The timeout in milliseconds.
	 * @see IConnectionTemplate#getSocketExpirationTimeout()
	 */
	public int getSocketExpirationTimeout() {
		return expirationTimeout;
	}

	/**
	 * Set the timeout intervall after which the connection assumes
	 * that an open but unused socket has expired on the server. An expired socket
	 * then is closed and reopened in advance when the next request is
	 * executed over this connection. This reduces unnecessary error situations
	 * in the network.<br/>
	 * The new setting will become effective the next time a request is
	 * sent or a socket is opened.
	 * @param  timeout  the timeout in milliseconds.  Setting the timeout to
	 * zero (or a negative value) defines an infinite timeout, i.e. an idle connection
	 * will expire only on behalf of a server-side timeout.
	 */
	public void setSocketExpirationTimeout(int timeout) {
		expirationTimeout = (timeout>0)? timeout : 0;
	}

	/**
	 * Returns how often the connection tries to repeat a request if an IOException
	 * occured (for example because the connection to the remost host is broken)
	 * or the server responded with an authorization failure.
	 * @return The number of allowed request repetitions.
	 * @see IConnectionTemplate#getRequestRepetitions()
	 */
	public int getRequestRepetitions() {
		return requestRepetitions;
	}

	/**
	 * Returns whether request repetition is applied to blocking read timeouts.
	 * @return true, if read timeouts lead to request repetition.
	 * @see IConnectionTemplate#getRepeatOnTimeout()
	 */
	public boolean getRepeatOnTimeout() {
		return repeatOnTimeout;
	}

	/**
	 * Sets how often the connection should try to repeat a request if an
	 * <code>IOException</code> occured (for example because the connection
	 * to the remost host got lost), or the server responded with an
	 * authorization failure. By default the repetition counter is set to one.
	 * <br/>
	 * Note, according to the
	 * HTTP specification a request in general should not be repeated
	 * more than once and only if it is "idempotent". This means, a request
	 * should not be repeated if it has side effects on the server.
	 * @param repetitions  the number of allowed request repetitions (default: 1)
	 * @param repeatOnTimeout  if true, the repetition mechanism is applied to
	 * blocking read timeouts, too (default: false).
	 */
	public void setRequestRepetitions(int repetitions, boolean repeatOnTimeout) {
		requestRepetitions = repetitions;
		this.repeatOnTimeout = repeatOnTimeout;
	}

	/**
	 * Return the size of the receive buffer of the underlying socket.
	 * If this method is executed while the socket of the connection is open
	 * it will return the actual size of the buffer read directly from the
	 * socket. Otherwise it will return the preset value defined by
	 * the <code>setReceiveBufferSize</code> method.
	 * @return The size of the receive buffer, or -1 if the platform's default
	 * buffer size is used.

	 * @see IConnectionTemplate#getReceiveBufferSize()
	 */
	public int getReceiveBufferSize() {
		int result = receiveBufferSize;
		if (socket != null) {
			try {
				result = socket.getReceiveBufferSize();
			} catch (SocketException ex) {
				TRACE.catching("getReceiveBufferSize()", ex);	
			}
		}
		return result;
	}

	/**
	 * Sets the size of the receive buffer of the underlying socket.
	 * <p>Increasing buffer size can increase the performance of network
	 * I/O for high-volume connection, while decreasing it can help reduce
	 * the backlog of incoming data.
	 * Note the given size is only a hint to the operation
	 * system to adjust the buffer size of its network I/O.</p>
	 * <p>The new setting will become effective for the next request
	 * executed by the <code>send</code> method. </p>
	 * @param size   the size to which to set the receive buffer size.
	 * Any negative value resets the buffer size to the platform's default
	 * buffer size. The minimum buffer size is 256 bytes.
	 */
	public void setReceiveBufferSize(int size) {
		if (size >= 0) {
			receiveBufferSize = (size > MIN_BUFFER_SIZE)? size : MIN_BUFFER_SIZE;
			if (!socketClosed) {
				try {
					socket.setReceiveBufferSize(receiveBufferSize);
				} catch (SocketException e) {
					TRACE.catching("setReceiveBufferSize(int)", e);
					closeBeforeUse = true;
				}
			}
		} else {
			receiveBufferSize = DEFAULT_RECEIVE_BUFFER_SIZE;
			closeBeforeUse = true;
		}
	}

	/**
	 * Returns the size of the send buffer of the underlying socket.
	 * If this method is executed while the socket of the connection is open
	 * it will return the actual size of the buffer read directly from the
	 * socket. Otherwise it will return the preset value defined by
	 * the <code>setSendBufferSize</code> method.
	 * @return The size of the send buffer, or -1 if the platform's default
	 * buffer size is used.
	 * @see IConnectionTemplate#getSendBufferSize()
	 */
	public int getSendBufferSize() {
		int result = sendBufferSize;
		if (socket != null) {
			try {
				result = socket.getSendBufferSize();
			} catch (SocketException ex) {
				TRACE.catching("getSendBufferSize()", ex);	
			}
		}
		return result;
	}

	/**
	 * Sets the size of the send buffer of the underlying socket.
	 * <p>Increasing buffer size can increase the performance of network
	 * I/O for high-volume connection, while decreasing it can help reduce
	 * the backlog of incoming data.</p>
	 * <p>Note the given size is only a hint to the operation
	 * system to adjust the buffer size of its network I/O.</p>
	 * <p>The new setting will become effective for the next request
	 * executed by the <code>send</code> method. </p>
	 * @param size   the size to which to set the send buffer size.
	 * Any negative value resets the buffer size to the platform's default
	 * buffer size. The minimum buffer size is 256 bytes.
	 */
	public void setSendBufferSize(int size) {
		if (size >= 0) {
			sendBufferSize = (size > MIN_BUFFER_SIZE)? size : MIN_BUFFER_SIZE;
			if (!socketClosed) {
				try {
					socket.setSendBufferSize(sendBufferSize);
				} catch (SocketException e) {
					TRACE.catching("setSendBufferSize(int)", e);
					closeBeforeUse = true;
				}
			}
		} else {
			sendBufferSize = DEFAULT_SEND_BUFFER_SIZE;
			closeBeforeUse = true;
		}
	}



	/**
	 * Enables or disables the usage of compression of message bodies for
	 * communication between client and server. Currently the
	 * compression formats "gzip" and "deflate" are supported that most
	 * servers understand. If no other format is selected "gzip" is used.
	 * Compression may considerably reduce
	 * the bandwith needs of a connection.
	 * Unless a compression algorithm has been selected explicitly,
	 * the connection uses "gzip" encoding to compress requests bodies,
	 * but allows the server to choose freely between gzip" and "deflate"
	 * encoding for responses.<br/>
	 * If compression is enabled the connection sends "AcceptEncoding" headers
	 * with each request indicating that the client wishes and is capable of
	 * acception compressed response bodies. Furthemore, request bodies are
	 * compressed, too.<br/>
	 * Note, servers are not required to support
	 * compression and therefore may ignore "Accept-Encoding" headers
	 * or respond with a 415 (Unsupported Media Type) status on compressed request
	 * bodies. Clients enabling
	 * compression should be aware of this situation.
	 * @param enable  if true, compression is used by the connection.
	 * @see Connection#setCompressionAlgorithm(String)
	 */
	public void enableCompression(boolean enable) {
		this.compressedRequests = enable;
		this.compressedResponses = enable;
	}

	/**
	 * Determines whether the connection uses compression.
	 * @return true, if either the compression of request or response
	 * bodies (or both) is enabled.
	 * @see IConnectionTemplate#isCompressionEnabled()
	 */
	public boolean isCompressionEnabled() {
		return compressedRequests || compressedResponses;
	}

	/**
	 * Enables or disables the sending of compressed request bodies. Currently the
	 * compression formats "gzip" and "deflate" are supported that most
	 * servers understand. If no other format is selected "gzip" is used.
	 * Compressing may considerably reduce the bandwith needs
	 * of a connection.<br/>
	 * Note, if a server is not able to understand the selected
	 * compression format, it will respond with a 415 (Unsupported Media Type) status
	 * code. Clients enabling request compression should be aware of this situation.
	 * @param enable  if true, request bodies are compressed using the selected
	 * compression algorithm.
	 * @see Connection#setCompressionAlgorithm(String)
	 */
	public void enableRequestCompression(boolean enable) {
		this.compressedRequests = enable;
	}

	/**
	 * Checks whether the connection sends compressed request bodies.
	 * @return true, if the connection sends compressed request bodies.
	 * @see IConnectionTemplate#isRequestCompressionEnabled()
	 */
	public boolean isRequestCompressionEnabled() {
		return compressedRequests;
	}

	/**
	 * Enabled or disables the sending of "Accept-Encoding" headers. If sending
	 * is enabled the server is allowed to compress responses. Currently the
	 * compression formats "gzip" and "deflate" are supported that most
	 * servers understand. Compression may considerably reduce the bandwith needs
	 * of a connection.<br/>
	 * However, servers are not required to support
	 * compression and therefore may ignore "Accept-Encoding" headers.
	 * @param enable  if true, the server may compress responses, otherwise
	 * compression is switched off.
	 * @see Connection#setCompressionAlgorithm(String)
	 */
	public void enableResponseCompression(boolean enable) {
		this.compressedResponses = enable;
	}

	/**
	 * Checks whether the connection sends "Accept-Encoding" headers to allow
	 * servers to compress responses.
	 * @return true, if the connection sends "Accept-Encoding" header.
	 * @see IConnectionTemplate#isResponseCompressionEnabled()
	 */
	public boolean isResponseCompressionEnabled() {
		return compressedResponses;
	}

	/**
	 * Returns the compression algorithm currently selected.
	 * @return either "gzip" or "deflate".
	 * @see IConnectionTemplate#getCompressionAlgorithm()
	 */
	public String getCompressionAlgorithm() {
		return compressionAlgorithm;
	}

	/**
	 * Determines the compression algorithm used for communication
	 * with the server. By default, the connection uses "gzip" encoding
	 * to compress requests bodies, but allows the server to choose freely
	 * between gzip" and "deflate" encoding for responses.
	 * @param algorithm  either "gzip" or "deflate".
	 */
	public void setCompressionAlgorithm(String algorithm) {
		this.compressionAlgorithm = algorithm;
	}

	/**
	 * Returns the compression methods supported by this connection.
	 * @return A comma separated list of supported encodings.
	 */
	public static String[] getSupportedCompressionAlgorithms() {
		return new String[] {"gzip", "deflate"};
	}


	/**
	 * Enables or disables the calculation of a hash value for
	 * response bodies. Such a "message digest" may be used to
	 * check the validity of a response body received from a server.
	 * The digest value may for example be checked against a "Content-MD5"
	 * header in the response if available.
	 * @param enable  if true, the calculation of a message digest
	 * is switched on for the next request.
	 * @see Connection#setDigestAlgorithm(String)
	 */
	public void enableResponseDigest(boolean enable) {
		this.digestEnabled = enable;
	}

	/**
	 * Checks whether the calculation of hash values for response
	 * bodies is enabled.
	 * @return true, if the calculation of hashes is enabled.
	 * @see IConnectionTemplate#isResponseDigestEnabled()
	 */
	public boolean isResponseDigestEnabled() {
		return digestEnabled;
	}

	/**
	 * Returns the hash algorithm used to calculate message digests
	 * for response bodies.
	 * @return the name of a hash algorithm like "MD5" or "SHA".
	 * @see IConnectionTemplate#getDigestAlgorithm()
	 */
	public String getDigestAlgorithm() {
		return digestAlgorithm;
	}

	/**
	 * Sets the hash algorithm used to calculate message digests
	 * for response bodies. The default is "MD5".
	 * @param algorithm  the identification of a hash algorithm, e.g.
	 * "MD5" or "SHA". Note, most platforms at least provide an
	 * implementation of the "MD5" algorithm.
	 */
	public void setDigestAlgorithm(String algorithm) {
		this.digestAlgorithm = algorithm;
	}


	/**
	 * Checks whether the connection automatically follows
	 * redirect responses (status codes 3xx).
	 * @return True, if the connection follows redirects automatically.
	 * @see IConnectionTemplate#getFollowRedirects()
	 */
	public boolean getFollowRedirects() {
		return followRedirects;
	}

	/**
	 * Checks whether the connection automatically follows
	 * permanent redirect responses (status code "301 Move Permanently").
	 * @return True, if the connection follows permanent redirects automatically.
	 * @see IConnectionTemplate#getFollowPermanentRedirects()
	 */
	public boolean getFollowPermanentRedirects() {
		return followRedirects;
	}

	/**
	 * Sets whether the connection should automatically follow redirect.
	 * This method switched redirects generally on or off. By default redirection
	 * is switched on, but only for temporary redirects (status "302 Moved Temporarily"),
	 * and only those poitinting to the same host.
	 * All other redirect operations are switched of by default.<br/>
	 * The new setting will become
	 * effective for the next request executed by the send method.
	 * @param followRedirects  if true, the connection tries to follow
	 * redirects automatically.
	 */
	public void setFollowRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
	}

	/**
	 * Sets whether the connection should follow permanent
	 * redirect responses (status code "301 Move Permanently").
	 * By default, permanent redirects are switched off to give
	 * a cient for example the chance to adjust its "bookmarks".<br/>
	 * The new setting will become
	 * effective for the next request executed by the send method.
	 * issued.
	 * @param followPermanentRedirects  if true, the connection tries to
	 * follow also permanent redirects.
	 */
	public void setFollowPermanentRedirects(boolean followPermanentRedirects) {
		this.followPermanentRedirects = followPermanentRedirects;
	}

	/**
	 * Sets the redirect policy for this connection.
	 * This method allows to fine tune the redirection mechanism.
	 * <code>followPermanentRedirects</code>: follow permanent redirects (status "301 Moved Permanently").
	 * By default, this is switched off.<br/>
	 * <code>followDomainRedirects</code>: follow redirects to hosts in a different domain
	 * (applies to temporary and permanent redirects).<br/>
	 * <code>followProxyRedirects</code>: follow a server advisory to use a certain proxy for
	 * further requests (status "305 Use Proxy"). By default, this is switched off.<br/>
	 * <code>redirectAllMethods</code>: Allows to redirect all requests, not only "GET" and
	 * "HEAD" requests as defined in RFC2616. By default, this is switched off.<br/>
	 * <code>maxRedirects</code>: The maximum number of redirect "hops". By default, this
	 * number is set to one.<br/>
	 * Note, all of the mentioned redirect options may impose a certain security risk on a
	 * client. For example, automatic redirects to another domain may potentially be very dangerous
	 * since you do not know in advance whether you trust the target host or not.<br/>
	 * The new setting will become
	 * effective for the next request executed by the send method.
	 * issued.
	 * @param followPermanentRedirects  if true, the connection tries to
	 * follow also permanent redirects.
	 */
	public void setFollowRedirects(
		boolean followPermanentRedirects,
		boolean followProxyRedirects,
		boolean followDomainRedirects,
		boolean redirectAllMethods,
		int maxRedirects)
	{
		this.followPermanentRedirects = followPermanentRedirects;
		this.followProxyRedirects = followProxyRedirects;
		this.followDomainRedirects = followDomainRedirects;
		this.followRedirectAllMethods = redirectAllMethods;
		this.maxRedirects = maxRedirects;
	}

	/**
	 * Returns the value of the "User-Agent" header sent with each request.
	 * @return The string sent in the "User-Agent" header, i.e. "SAP HTTP CLIENT/6.40"
	 * by default.
	 * @see IConnectionTemplate#getUserAgent()
	 */
	public String getUserAgent() {
		if (userAgent == null) {
			userAgent = IConnectionTemplate.USER_AGENT;
			//TODO: prepare a more comprehensive user agent
		}
		return userAgent;
	}

	/**
	 * Sets the value of the "User-Agent" header sent with each request.
	 * The new setting will become
	 * effective for the next request executed by the send method.
	 * @param userAgent a string specifying the user agent.
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 * Opens the connection.
	 * If the connection was marked to be closed before
	 * next use, the socket is destroyed. If the socket is invalid or
	 * has been destroyed before (either exlicitly by remote server or client,
	 * or implicitly by a timeout), a new socket is created and opened.<br/>
	 * If a proxy has been defined for this connection, the socket is opened to
	 * the proxy. If the proxy supports the SOCKS protocol and the usage of
	 * SOCKS has been switched on, the SOCKS server is contacted to get a valid
	 * socket to the destination host. If proxy tunneling is enabled, a tunnel
	 * through the proxy is established by sending a CONNECT command. Note, proxy
	 * tunneling for plain HTTP protocol may not be supported by all host, or may
	 * be limited to certain ports (like the default SSL port 443).<br/>
	 * If the selected protocol is "https" a SSL socket is created instead of
	 * an "ordinary" socket.
	 * @throws IOException  if an i/o error occurs, e.g. because the host is unknown or invalid,
	 * or a communication error occured while creating the socket or input/output streams.
	 * @throws ConnectException if the host did not respond within
	 * the defined timeout interval.
	 * @see Connection#setSocketConnectTimeout(int).
	 * @see Connection#setSocketExpirationTimeout(int).
	 */
	public void open() throws IOException {
		if (!socketClosed) {
			if (closeBeforeUse) {
				TRACE.debugT("open()", "socket [close before use]");
				close();
			} else if (hasExpired()) {
				TRACE.debugT("open()", "socket [expired]");
				close();
			}
		}
		openSocket();
	}
	
	/**
	 * Closes the connection and releases the underlying socket and streams.
	 */
	public void close() {
		closeSocket();
	}

	/**
	 * Checks whether this connection has expired, i.e. not been used for
	 * a period of time longer than set with <code>setSocketExpirationTimeout</code>
	 * @return true, if the connection has expired (but has not yet been closed)
	 */
	public boolean hasExpired() {
		return !socketClosed  &&  socketExpirationTime > 0  &&  
			System.currentTimeMillis() > socketExpirationTime;
	}

	/**
	 *  Called by the garbage collector on an object when garbage collection determines
	 *  that there are no more references to the object. Closes the socket
	 *  associated with this connection.
	 */
	protected void finalize() throws Throwable { //$JL-FINALIZE$
		close();
		super.finalize();
	}

	/**
	 * Sends the specified request and retrieves the corresponding response.
	 * By default, the socket will remain open after completion of this method.
	 * @param request  the request to send.
	 * @return The response retrieved.
	 * @throws IOException  if a communication error occurs.
	 * @throws ConnectException if the connection tried to open a new socket
	 * to the selected host, but the host did not respond within the defined timeout interval.
	 * @throws SocketException  if the socket became invalid during the
	 * request-response cycle.
	 * @throws InterruptedIOException  if the connection timed out before a valid
	 * response was received.
	 * @throws HTTPException  if the response is malformed, invalid or incomplete.
	 * @see IConnection#send(IRequest)
	 * @see IConnection#send(IRequest, boolean)
	 */
	public IResponse send(IRequest request) throws IOException, HTTPException {
		return sendInternal(request, false, null);
	}

	/**
	 * Sends the specified request and retrieves the corresponding response.
	 * If the socket of the connection has not yet been created, or has been
	 * closed after a previous request either by the client or the remote
	 * host, or is invalid for some other reason, a new socket is created and opened.<br/>
	 * If the <code>sendCloseConnection</code> parameter is set to <code>true</code> a
	 * "Connection: close" header is sent to the remote host and the socket of
	 * the connection is destroyed after the response has been received. Otherwise
	 * the socket remains open ("persistent" connection).<br/>
	 * If the remote host requires authentication and a valid authenticator and
	 * authentication parameters have been set in the session context, the request
	 * is repeated automatically based on the challenge returned by the host.<br/>
	 * Cookies are assigned to the request depending on the domain of the
	 * request URL.
	 *
	 * @param request  the request to send.
	 * @param sendCloseConnection  a "Connection: close" header is send to the server
	 * and the socket is destroyed after completition of the request.
	 * @return The response retrieved.
	 * @throws IOException  if a communication error occurs.
	 * @throws ConnectException   if the connection tried to open a new socket
	 * to the selected host, but the host did not respond within the defined timeout interval.
	 * @throws SocketException  if the socket becomes invalid during the
	 * request-response cycle.
	 * @throws InterruptedIOException  if the connection timed out before a valid
	 * response was received.
	 * @throws HTTPException  if the response is malformed, invalid or incomplete.
	 * @see IConnection#send(IRequest)
	 * @see IConnection#send(IRequest, boolean)
	 * @see Connection#setSessionContext(ISessionContext)
	 * @see ISessionContext#setSendCookies)
	 */
	public IResponse send(IRequest request, boolean sendCloseConnection)
	throws IOException, HTTPException
	{
		return sendInternal(request, sendCloseConnection, null);
	}

	/**
	 * Manually redirects a previously performed request.
	 * @param response  the response with a redirect status.
	 * @return  the response to the redirected request, or <code>response</code>
	 * if the redirect was not possible or not allowed.
	 * @throws IOException  if an i/o error occured
	 * @throws HTTPException  if the redirected response was not valid.
	 */
	public IResponse redirect(IResponse response) throws IOException, HTTPException
	{
		return redirect(response, false);
	}

	/**
	 * Manually redirects a previously performed request.
	 * @param response  the response with a redirect status.
	 * @param sendCloseConnection   a "Connection: close" header is send to the server
	 * and the socket is destroyed after completition of the request.
	 * @return  the response to the redirected request, or <code>response</code>
	 * if the redirect was not possible or not allowed.
	 * @throws IOException  if an i/o error occured
	 * @throws HTTPException  if the redirected response was not valid.
	 */
	public IResponse redirect(IResponse response, boolean sendCloseConnection)
	throws IOException, HTTPException
	{
		IResponse redirectedResponse = response;
		int status = response.getStatus();
		if (status >= Status.MULTIPLE_CHOICES && status <= Status.TEMPORARY_REDIRECT) {
			IRequest request = response.getRequest();
			IRequestEntity entity = request.getRequestEntity();
			if (entity != null) {
				if ( entity.supportsReset() ) {
					entity.reset();
				} else {
					TRACE.infoT(
						"redirect(IRequest,int,String,Set,boolean)",
						"failed to redirect request: cannot reset entity"
					);
					return response;
				}
			}
			String location = response.getHeaderValue(Header.HTTP.LOCATION);
			if (location == null) {
				throw new HTTPException("Redirect failed: response contains no \"Location\" header.");
			}
			if (!URL.isValidURL(location)) {
				throw new HTTPException("Redirect failed: \"Location\" header is invalid.");
			}
			IResponse rsp = redirectInternal(request, response.getStatus(),
					new URL(location), new HashSet(), sendCloseConnection);

			redirectedResponse = (rsp != null)? rsp : response;
		}
		return redirectedResponse;
	}

	/**
	 * @param request  the request to send.
	 * @param sendCloseConnection  a "Connection: close" header is send to the server
	 * @param locations  an optional set of previously visited redirect locations.
	 */
	private Response sendInternal(IRequest request, boolean sendCloseConnection, Set locations)
	throws IOException, HTTPException
	{
		// ensure that the socket is open and valid (otherwise reopen a socket)
		open();
		// skip pending content from a previous response (if any)
		in.release();
		
		// send the request, receive and evaluate the response
		// if an IOException occurs (e.g. the connection is aborted by host or the
		// input stream brakes) try to repeat the request with a fresh socket.
		Response response = null;
		boolean acceptResponse = false;
		long duration = System.currentTimeMillis();
		int lastStatus = -1;
		
		int repeatCount = 0;
		IOException pendingException = null;

		while (!acceptResponse && repeatCount <= requestRepetitions) {
			try {
				if (TRACE.beDebug()) {
					TRACE.debugT("sendInternal(IRequest,boolean,Set)", "sending... [connID={0},socketID={1}]",
						new Object[]{Integer.toHexString(hashCode()), Integer.toHexString(socket.hashCode())});			
				}
				
				// OK: This bloody story with try-finally
				// is here just to guarantee that we call
				// the monitors notification even if some
				// bad things happen on the underlying
				// sockets, i.e. if an exception
				// was thrown during send
				try
				{				
					// OK: <1.12.2004> ADDED
					// Monitors the request
					// Currently it is a DSR passport, also
					// decorating the request with header
					// field "SAP-PASSPORT"

					beforeSend(request);
									
					sendRequest(request, sendCloseConnection);
					
					if (TRACE.beDebug()) {
						TRACE.debugT("sendInternal(IRequest,boolean,Set)", "receiving... [connID={0},socketID={1}]",
							new Object[]{Integer.toHexString(hashCode()), Integer.toHexString(socket.hashCode())});			
					}	
					response = getResponse(request);
				}
				finally
				{
					// OK: <1.12.2004> ADDED
					// Informs monitors that the request-response
					// roundtrip is over 
					
					afterReceive(response);
				}				
				// check if response is acceptable
				int status = response.getStatus();
				if (response.isValid()) {
					acceptResponse = true;

					// handle redirects
			 		if (followRedirects  &&
			 				status >= Status.MULTIPLE_CHOICES  &&
			 				status <= Status.TEMPORARY_REDIRECT)
			 		{
						String locationHeader = response.getHeaderValue(Header.HTTP.LOCATION);
						if (locationHeader != null) {
							URL location = null;
							if (URL.isValidURL(locationHeader)) {
								location = new URL(locationHeader);
							} else {
								// locationHeader is not a valid URL -> we assume it is a path and we append it to the URL of the request
								location = getUrl(locationHeader);
							}
							Response redirectedResponse = null;
							switch (status) {
								case Status.MULTIPLE_CHOICES:
									break;
								case Status.MOVED_PERMANENTLY:
									if (followPermanentRedirects) {
										redirectedResponse =
											redirectInternal(request, status, location, locations, sendCloseConnection);
									}
									break;
								case Status.FOUND:
								case Status.TEMPORARY_REDIRECT:
									redirectedResponse =
										redirectInternal(request, status, location, locations, sendCloseConnection);
									break;
								case Status.SEE_OTHER:
									break;
								case Status.NOT_MODIFIED:
									break;
								case Status.USE_PROXY:
									if (followProxyRedirects) {
										redirectedResponse =
										redirectInternal(request, status, location, locations, sendCloseConnection);
									}
									break;
								default:
									throw new HTTPException("invalid redirect response [" 
										+ response.getStatusLine() + "]");
							}
							if (redirectedResponse != null) {
								response = redirectedResponse;
							}
						} else {
							throw new HTTPException("missing location header in redirect response [" 
								+ response.getStatusLine() + "]");
						}
			 		} 
			 		else if (status == Status.UNAUTHORIZED || status == Status.PROXY_AUTHENTICATION_REQUIRED) {
						if (status != lastStatus && acceptAuthentication(status))
						{
							lastStatus = status;
							--repeatCount;  // repeat once more
							acceptResponse = false;
							if (TRACE.beInfo()) {
								TRACE.infoT(
									"sendInternal(IRequest,boolean,Set)",
									"{0} requests authentication [{1}]",
									new Object[]{
										(status==Status.UNAUTHORIZED)? "host" : "proxy",
										response.getStatusLine()
									}
								);
							}
							// hack for buggy INQMY servers (esp. on Linux):
							// sometimes InterruptedIOException or SocketException
							// occurs when repeating a request with body after an 401 response.
							// To be safe, re-open the socket.
							closeBeforeUse = true;
						}
			 		} 
			 		else if (status == Status.UPGRADE_REQUIRED) {
						if (status != lastStatus) {
							upgradeProtocol = true;
							lastStatus = status;
							--repeatCount;  // repeat once more
							acceptResponse = false;
							if (TRACE.beInfo()) {
								TRACE.infoT(
									"sendInternal(IRequest,boolean,Set)",
									"protocol UPGRADE received [{0}]",
									new Object[]{response.getStatusLine()}
								);
							}
						}
					} 
					else if (status == Status.BAD_REQUEST || status == Status.INTERNAL_SERVER_ERROR) 
					{
						closeBeforeUse = true;
						if (status != lastStatus) {
							lastStatus = status;
							acceptResponse = false;
							//$JL-SEVERITY_TEST$
							TRACE.warningT(
								"sendInternal(IRequest,boolean,Set)",
								"Bad Request or Internal Server error caught [" + response.getStatusLine() + "]"
							);
						}
					}
				} 
				else 
				{
					throw new HTTPException("invalid HTTP response ["
						+ response.getStatusLine() + "]");
				}
			} 
			catch (InterruptedIOException ex) {//$JL-EXC$	
				TRACE.infoT(
					"sendInternal(IRequest,boolean,Set)", "Read operation timed out [{0}][connID={0},socketID={1}]",
					new Object[]{ex.getMessage(), Integer.toHexString(hashCode()), Integer.toHexString(socket.hashCode())}
				);								
				closeBeforeUse = true;
				acceptResponse = !repeatOnTimeout;
				pendingException = ex;
			} 
			catch (SocketException ex) {
				TRACE.catching("sendInternal(IRequest,boolean,Set)", ex);
				TRACE.infoT(
					"sendInternal(IRequest,boolean,Set)", 
					"Connection reset or shut down by peer [connID={0},socketID={1}]",
					new Object[]{Integer.toHexString(hashCode()), Integer.toHexString(socket.hashCode())}
				);				
				closeBeforeUse = true;
				pendingException = ex;														
			} 
			catch (IOException ex) {
				TRACE.catching("sendInternal(IRequest,boolean,Set)", ex);
				TRACE.infoT(
					"sendInternal(IRequest,boolean,Set)", 
					"Connection broken due to I/O error [connID={0},socketID={1}]",
					new Object[]{Integer.toHexString(hashCode()), Integer.toHexString(socket.hashCode())}
				);								
				closeBeforeUse = true;
				pendingException = ex;
			}			

			// if response is not accetable prepare request repetition
			if (!acceptResponse) {
				++repeatCount;
				if (repeatCount <= requestRepetitions) {

					// reset request entity
					IRequestEntity entity = request.getRequestEntity();
					if (entity != null) {
						if (entity.supportsReset()) {
							entity.reset();
						} else {
							TRACE.infoT(
								"sendInternal(IRequest,boolean,Set)",
								"Request cannot be repeated. Request entities of type {0} do not support this.",
								new Object[]{entity.getEntityType()}								
							);
							break;
						}
					}

					// clean up response stream
					if (response != null) {
						response.releaseStream();
					}

					// ensure that socket is still open and valid (otherwise open a new one)
					open();

					pendingException = null;
					TRACE.infoT("sendInternal(IRequest,boolean,Set)", "repeating request");
				}
			}
		}

		// if request repetition failed finally with an exception, re-throw this exception,
		// mark the socket as invalid
		if (pendingException != null) {
			closeBeforeUse = true;
			throw pendingException;
		}

		// if we have no valid response: throw an exception, mark the socket as invalid
		if (!response.isValid()) {
			closeBeforeUse = true;
			throw new HTTPException("Cannot handle response. The response code is invalid" +				"or unknown [" + response.getStatusLine() + "]");
		}

		// reset the expiration timeout
		socketExpirationTime = System.currentTimeMillis() + EXPIRATION_TIMEOUT;

		// determine the elapsed time for the request-response cycle
		duration = System.currentTimeMillis() - duration;
		response.setDuration(duration);

		return response;
	}


	/**
	 * Redirects the request to the given location.
	 * Checks for cyclic redirects and limits the length of a redirect chain
	 * to MAX_REDIRECTS. Opens a new temporary connection if the given location
	 * points to a foreign host or port or requests a different protocol, and
	 * <code>followForeignRedirects</code> is true. The current connection is
	 * used as template for the temporary connection. Thus, all settings of the
	 * current connection (like proxy settings, timeouts etc.) are also applied
	 * to the temporary connection. If the request provides a non-repeatable entity
	 * the redirect fails.
	 * @param request  the request to be redirected
	 * @param location  the destination URL of the redirect
	 * @param locations  a set of previously visited redirect locations, or null.
	 * @return a valid response from the given redirect location, or null if either
	 * the request entity is not repeatable, or location requires a different
	 * host, port or protocol of the connection while <code>followForeignRedirects</code>
	 * forbidds this.
	 * @throws HTTPException  if the redirected request failed due to an HTTPException,
	 * the redirect chain was too long (more than MAX_REDIRECTS redirects), or a
	 * cyclic redirect occured.
	 * @throws IOException  if the redirected request failed due to an IOException.
	 */
 	private Response redirectInternal(IRequest request, int status, URL location, Set locations, boolean sendCloseConnection)
 	throws IOException, HTTPException
 	{
 		// switching protocols is now allowed thru a redirect
		if (!protocol.toString().equalsIgnoreCase(location.getProtocol())) {
			//$JL-SEVERITY_TEST$
			TRACE.warningT(
				"redirect(IRequest,int,URL,Set,boolean)",
				"server tries to switch protocols to \"{0}\"",
				new Object[]{location.getProtocol()}
			);
			return null;
		}

 		// check whether redirects are acceptable for this request method
 		String method = request.getMethod();
 		if (!followRedirectAllMethods) {
 			if(!"GET".equals(method) && !"HEAD".equals(method)) {
				//$JL-SEVERITY_TEST$
 				TRACE.warningT(
 					"redirectInternal(IRequest,int,URL,Set,boolean)",
					"forbidden to redirect {0} requests",
					new Object[]{method}
				);
				return null;
 			}
 		}

 		// check for cyclic dependencies
 		if (!followProxyRedirects) {
	 		if (locations == null) {
	 			locations = new HashSet();
	 		} else if (locations.size() > maxRedirects) {
				//$JL-SEVERITY_TEST$
				TRACE.warningT(
					"redirectInternal(IRequest,int,URL,Set,boolean)",
					"failed to redirect request: too many redirects");
				throw new HTTPException("failed to redirect request: too many redirects");
	 		} else if (locations.contains(location)) {
				TRACE.warningT(
					"redirectInternal(IRequest,int,URL,Set,boolean)",
					"failed to redirect request: cyclic redirect found");
				throw new HTTPException("failed to redirect request: cyclic redirect found");
	 		}
	 		locations.add(location.toString());
 		}

		// reset request entity
		IRequestEntity entity = request.getRequestEntity();
		if (entity != null) {
			if ( entity.supportsReset() ) {
				entity.reset();
			} else {
				//$JL-SEVERITY_TEST$
				TRACE.warningT(
					"redirectInternal(IRequest,int,String,Set,boolean)",
					"failed to redirect request: cannot reset entity"
				);
				return null;
			}
		}

		// handle redirect to proxy: set the proxy params of this connection and
		// repeat the original request
		if (status == Status.USE_PROXY) {
			if (followProxyRedirects) {
				setProxyUrl(location, true);
				return sendInternal(request, sendCloseConnection, locations);
			} else {
				//$JL-SEVERITY_TEST$
				TRACE.warningT(
					"redirectInternal(IRequest,int,String,Set,boolean)",
					"forbidden to redirect to proxy \"{0}\"",
					new Object[]{location.getWebLocator()}
				);
				return null;
			}
		}

 		// if redirect changes host or port of the connection: open a new connection
 		// and redirect the request to the new URI
 		Connection conn = this;
		if (!host.equals(location.getHost()) || port != location.getPort())
		{
			// check whether we leave the current domain
			if (!URL.getDomainOf(host).equals(location.getDomain())) {
				if (!followDomainRedirects) {
					//$JL-SEVERITY_TEST$
					TRACE.warningT(
						"redirect(IRequest,int,String,Set,boolean)",
						"forbidden to redirect to foreign host \"{0}\"",
						new Object[]{location.getWebLocator()}
					);
					return null;
				}
			}
			// use this connection as template
			conn = new Connection(this);
			conn.setUrl(location);
			// start with a new session, esp. do not send passwords to foreign host
			conn.setSessionContext(null);
			request.setPath(location.getResource());
			return conn.sendInternal(request, true, locations);
		}

		// otherwise: use the current connection and only rewrite the request URI
		request.setPath(location.getResource());
		return sendInternal(request, sendCloseConnection, locations);
 	}

	/**
	 * Opens a socket and input/output streams for this connection.
	 * @throws IOException  if an i/o error occurs, i.e. the host is unknown,
	 * the socket or streams could not be created, or the host did not respond within
	 * the predefined timeout interval.
	 * @see Connection#getSocketReadTimeout()
	 * @see Connection#getSocketConnectTimeout()
	 * @see Connection#getSocketExpirationTimeout()
	 */
	private void openSocket() throws IOException
	{
		if (streamsAvailable()) {
			return;
		}

		// determine whether to use host or proxy host as destination
		String _host = host;
		int _port = port;
		if (useProxy && proxy != null  && !bypassProxy()) {
			_host = proxy;
			_port = (useProxy && proxyPort != 0) ? proxyPort : getDefaultPort(protocol);
			if (TRACE.beInfo()) {
				TRACE.infoT(
					"openSocket()",
					"using proxy [host={0}:{1}]",
					new Object[]{_host, Integer.toString(_port)}
				);
			}
		}

		// try to open a socket
		long sleep = 0;
		long elapsedTime = 0;
		long time = 0;
		int state = CONTACT;
		String connectExceptionMsg = null;
        String alias = ( context != null) ? context.certificates().getClientAlias() : null;

		while (socketClosed  &&  elapsedTime < connectTimeout)
		{
			try
			{
				time = System.currentTimeMillis();
				if (useProxy)
				{
					// connect the proxy
					state = CONTACT_PROXY;
					socket = new Socket(_host, _port);

					// if SOCKS is enabled, try to get a socket for the target host
					// from the SOCKS (=proxy) server
					// TODO: is it possible to combine SOCKS with SSL?
					if (useSOCKS) {
						if (socksProvider == null) {
							socksProvider = new SOCKSv5Provider();
						}
						state = CONTACT_SOCKS;
						socket = socksProvider.getSocket(socket, host, port, context);
					} else {
						// if tunneling is enabled issue a CONNECT request
						if (tunnelProxy) {
							openProxyTunnel();
						}

						// if SSL is enabled try to wrap the existing socket with an SSL socket
						// retrieved from the SSL provider
						if (secureProtocol) {
							if (secureSocketProvider == null) {
								createSecureSocketProvider();
							}
							state = CONTACT_PROXY_SSL;
							socket = secureSocketProvider.createSocket(socket, host, port, alias);
						}
					}
				} else {
					if (secureProtocol) {
						// if SSL is enabled retrieve an SSL socket from the SSL provider
						if (secureSocketProvider == null) {
							createSecureSocketProvider();
						}
						state = CONTACT_HOST_SSL;
					  	socket = secureSocketProvider.createSocket(_host, _port, alias);
					} else {
						// this the "ordinary" case without proxy, SOCKS, SSL and all that stuff
						state = CONTACT_HOST;
						socket = new Socket(_host, _port);
					}
				}

				// retrieve the socket streams and set socket params
				prepareSocket();

				// calculate when this socket expires (each request resets this)
				socketExpirationTime = System.currentTimeMillis() + EXPIRATION_TIMEOUT;

				elapsedTime += (System.currentTimeMillis() - time);
				closeBeforeUse = false;
				socketClosed = false;
			}
			catch (ConnectException e)
			{
				closeSocket();
				TRACE.catching("openSocket()", e);	
				TRACE.infoT("openSocket()", "Waiting for connection");				
				connectExceptionMsg = "The host is down or unavailable.";

				time = System.currentTimeMillis() - time;
				elapsedTime += time;

				if (connectTimeout < elapsedTime) {
					break;
				}

				if (sleep == 0) {
					sleep = (connectTimeout - 5*time) / 31;  // 5 repetitions
					TRACE.debugT("openSocket()",
						"Average connect timeout for this platform: {0}ms",
						new Object[]{Long.toString(time)});
				}

				// doubled sleep for each roundtrip = wait connectTimeout in total
				if (sleep > 0) {
					try {
						Thread.sleep(sleep);
					} catch (InterruptedException ex2) {
						TRACE.catching("openSocket()", ex2);
					}
					elapsedTime += sleep;
					sleep *= 2;
				}
			}
			catch (UnknownHostException e) {
				closeSocket();
				TRACE.catching("openSocket()", e);
				connectExceptionMsg = "The IP address of the host could not be resolved. " +
					"Maybe the URL is misspelled or the host does no longer exist.";
				elapsedTime += (System.currentTimeMillis() - time);
				break;
			}
			catch (NoRouteToHostException e) {
				closeSocket();
				TRACE.catching("openSocket()", e);
				connectExceptionMsg = "Could not find a route to the remote host. " +
					"Maybe the host is protected by a firewall or an intermediate server is down.";
				elapsedTime += (System.currentTimeMillis() - time);
				break;
			}
			catch (IOException e)
			{
				closeSocket();
				TRACE.catching("openSocket()", e);
				connectExceptionMsg = e.getMessage();
				elapsedTime += (System.currentTimeMillis() - time);
				break;
			}
		}

		if (socketClosed) {
			StringBuffer stateMsg = new StringBuffer("Unable to open ");
			switch (state) {
				case CONTACT_PROXY:
					stateMsg.append("connection to proxy \"").append(_host);
					stateMsg.append(":").append(_port).append(". ");
					break;
				case CONTACT_SOCKS:
					stateMsg.append("connection to SOCKS server \"").append(_host).append(":");
					stateMsg.append(_port).append("\". ");
					break;
				case CONTACT_PROXY_SSL:
					stateMsg.append("SSL connection to host \"");
					stateMsg.append(host).append(":").append(port);
					stateMsg.append(" via proxy ");
					stateMsg.append(_host).append(":").append(_port).append("\". ");
					break;
				case CONTACT_HOST_SSL:
					stateMsg.append("SSL connection to host \"");
					stateMsg.append(host).append(":").append(port).append("\". ");
					break;
				case CONTACT_HOST:
					stateMsg.append("connection to host \"");
					stateMsg.append(host).append(":").append(port).append("\". ");
					break;
				default:
					stateMsg.append("connection.");
			}
			if (connectExceptionMsg != null) {
				stateMsg.append(connectExceptionMsg).append(".");
			}

			TRACE.debugT(
				"openSocket()",
				"opening socket [failed][{0}][host={1}:{2}][protocol={3}][connID={4}][waited {5}ms]",
				new Object[]{
					stateMsg.toString(),
					_host, Integer.toString(_port), 
					protocol.toString(),
					Integer.toHexString(hashCode()),
					Long.toString(elapsedTime) 
				});

			throw new IOException(stateMsg.toString());
		}

		if (TRACE.beDebug()) {
			TRACE.debugT(
				"openSocket()",
				"opening socket [succeeded][host={0}:{1},ip={2}][localhost={6}:{7},ip={8}][protocol={3}][connID={4},socketID={5}]",
				new Object[]{
					_host, Integer.toString(_port), socket.getInetAddress().getHostAddress(),
					protocol.toString(),
					Integer.toHexString(hashCode()), Integer.toHexString(socket.hashCode()),
					socket.getLocalAddress().getHostName(),
					Integer.toString(socket.getLocalPort()), 
					socket.getLocalAddress().getHostAddress() 
				});
		}

	}

	/**
	 * Retrieve the socket streams and set some socket params (like
	 * timeouts)
	 * @throws IOException
	 */
	private void prepareSocket() throws IOException {
		// running the socket in TCP_NODELAY mode improves performance
		// of requests tremendeously if the request data is written in
		// larger pieces to the socket's output stream
		socket.setTcpNoDelay(true);

		// set the read timeout
		socket.setSoTimeout(readTimeout);

		// setting the buffer sizes of the socket may improve performance
		// for high-speed connections on certain platforms
		if (sendBufferSize > 0) {
			socket.setSendBufferSize(sendBufferSize);
		}
		if (receiveBufferSize > 0) {
			socket.setReceiveBufferSize(receiveBufferSize);
		}

		// retrieve the socket streams
		if (TRACE.beDebug()  &&  secureProtocol) {
			long time = System.currentTimeMillis();
			in = new ResponseStream(socket);
			out = new RequestStream(socket);
			TRACE.debugT("prepareSocket()", "SSL handshake [succeeded][time=" +
				(System.currentTimeMillis()-time) + "ms]");
		} else {
			in = new ResponseStream(socket);
			out = new RequestStream(socket);
		}
	}

	/**
	 * Issues a CONNECT request to a proxy to enable tunneling
	 */
	private void openProxyTunnel() throws IOException {
		IRequest connectRequest = new RequestBase("CONNECT");
		IResponse connectResponse = null;
		try {
			prepareSocket();
			sendRequest(connectRequest, false);
			connectResponse = getResponse(connectRequest);
		} catch (Exception e) {
			TRACE.catching("openProxyTunnel()", e);
			throw new IOException("opening proxy tunnel [failed][" + e.getMessage() +"]");
		}

		if (!connectResponse.isValid()) {
			throw new IOException("opening proxy tunnel [failed][invalid CONNECT response]["
				+ connectResponse.getStatusLine() + "]");
		}

		// any status code in the range 2xx indicates success
		int status = connectResponse.getStatus();
		if (status < Status.SUCCESS_MIN || status > Status.SUCCESS_MAX) {
			throw new IOException("opening proxy tunnel [failed]["
				+ connectResponse.getStatusLine() + "]");
		}

		if (TRACE.beDebug()) {
			TRACE.debugT("openProxyTunnel()", "opening proxy tunnel [succeded]");
		}
	}

	/**
	 * Checks whether the host or domain of this connection
	 * is in the proxy exclusion list.
	 * @return true, if proxy should be bypassed for this host
	 * or domain.
	 */
	private boolean bypassProxy() {
		boolean result = false;
		if (excludedHosts != null) {
			result = excludedDomains.contains(host);
		}
		if (excludedDomains != null) {
			result |= excludedDomains.contains(getDomain());
		}
		return result;
	}

	/**
	 * Creates a SSL socket provider if no explicit provider
	 * has been specified. Searches the classpath for the IAIK and JSSE
	 * SSL libraries (in this sequence), and - if sucessful - instantiates a
	 * appropriate provider.<br/>
	 * If a session context has been defined and that context specifies, that either client
	 * or server authentication (or both) should be applied, the necessary certificates
	 * are taken from the session context. If this is not possible, or the underlying
	 * socket provider does not support certificates (JSSE) the method fails
	 * with an <code>IOException</code>.<br/>
	 * If authentication is not required by the session context, a default socket
	 * provider supporting standard SSL encryption but without authentication is created.
	 * @throws  IOException  if no valid provider could be instantiated.
	 */
	private void createSecureSocketProvider() throws IOException {
		try {
			Class.forName("iaik.security.ssl.SSLSocket"); // check if we have IAIK libs in classpath
			if (context == null) {
				context = new SessionContext();
			}

			Certificates certs = context.certificates();
			if (certs.usingEngineKeyStores()) {
				String clientCerts = null;
				String serverCerts = null;
				if (certs.authenticateMe()) {
					clientCerts = certs.getClientCertStore();
				}
				if (certs.authenticateThem()) {
					serverCerts= certs.getServerCertStore();
				}
				secureSocketProvider = new IAIKSecureSocketProvider(serverCerts, clientCerts);
			} else {
				KeyStore clientCerts = null;
				KeyStore serverCerts = null;
				String pwd = null;

				if (certs.authenticateMe()) {
					try {
						clientCerts = certs.getClientCertificates();
					} catch (KeyStoreException e) {
						throw new IOException("failed to import client certificates ["
							+ e.getMessage() + "]");
					}
					pwd = certs.getClientStoreKeyPassword();
					if (clientCerts==null  ||  pwd==null) {
						throw new IOException("failed to retrieve SSL socket " +
							"[no client certificates provided]");
					}
				}
				if (certs.authenticateThem()) {
					try {
						serverCerts = certs.getServerCertificates();
					} catch (KeyStoreException e) {
						throw new IOException("failed to import server certificates ["
							+ e.getMessage() + "]");
					}
					if (serverCerts == null) {
						serverCerts = Certificates.loadDefaultKeystore();
						if (serverCerts==null) {
							throw new IOException("failed to retrieve SSL socket " +
								"[no server certificates provided]");
						}
					}
				}
				secureSocketProvider = new IAIKSecureSocketProvider(serverCerts, clientCerts, pwd);
			}
			TRACE.infoT("using SSL socket provider [IAIK][authMe={0},authThem={1}]",
				new Object[]{ (certs.authenticateMe()? "on" : "off"),
						  (certs.authenticateThem()? "on" : "off")  });
		}
		catch (ClassNotFoundException t) { //$JL-EXC$		
			try {
				Class.forName("javax.net.ssl.SSLSocketFactory");
				if (context != null) {
					Certificates certs = context.certificates();
					if (certs.authenticateMe() || certs.authenticateThem()) {
						throw new IOException("failed to retrieve SSL socket " +
							"[SSL provider does not support certificates]");
					}
				}
				secureSocketProvider = JSSESecureSocketProvider.getDefault();
				TRACE.infoT("using default SSL socket provider [JSSE][no authentication]");
			} catch (ClassNotFoundException tt) {
				throw new IOException("failed to retrieve SSL socket [no SSL provider]");
			}
		}
	}

	/**
	 * Checks if the socket is still open and valid by requesting
	 * the socket's input and output streams.
	 * @return False, if the socket is no longer valid.
	 */
	private boolean streamsAvailable() {
		boolean result = false;
		if (!socketClosed) {
			try {
				socket.getInputStream();
				socket.getOutputStream();
				result = true;
			} catch (IOException ex) {
				TRACE.catching("streamsAvailable()", ex);				
			}
		}
		return result;
	}

	/**
	 * Closes the socket and the associated input/output streams.
	 */
	private void closeSocket() {
		if (!socketClosed) {
			if (in != null) {
				in.shutdown();
				in = null;
			}
			if (out != null) {
				out.shutdown();
				out = null;
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException ex) {
					TRACE.catching("closeSocket()", ex);
				} finally {
					socket = null;
				}
			}
			socketClosed = true;
			closeBeforeUse = false;
			TRACE.debugT("closeSocket()", "socket [closed]");
			TRACE.debugT("closeSocket()", " ");
		}
	}


	/**
	  * Prepares and sends the request.
	  * Creates the request line from the given protocol method, basePath, path
	  * and protocol version. Creates the required "Host" header.
	  * Determines whether this connection is to be closed after this request,
	  * and whether "chunked" encoding is to be applied to the body.
	  * Applies cookies and credentials headers and finally appends all user-defined headers.
	  * Calls IRequestEntity.write if a request entity is defined. At the
	  * end flushes the output stream. If HTTP/1.0 is used for the request the output stream
	  * is closed if the request entity defined no content length.
	  * @param request  the request to send.
	  * @param sendCloseConnection  if true a "Connection: close" header is sent to the server
	  * and the connection is closed.
	  * IOException  if an i/o error occurs while writing to the underlying output stream
	  */
	private void sendRequest(IRequest request, boolean sendCloseConnection) throws IOException {
		// check if at least command parameter is defined in request
		String method = request.getMethod();
		if (method==null || method.length()==0) {
			throw new IllegalArgumentException("HTTP command in request missing");
		}

		IRequestEntity entity = request.getRequestEntity();

		// apply Content-Length and Transfer-Encoding headers
		long contentLength = request.getContentLength();
		boolean enableChunks = false;
		boolean closeOutStreamAfterSend = false;

		// apply Content-Length and Transfer-Encoding headers
		if (entity != null) {
			if (usingHTTP10) {
				closeOutStreamAfterSend = (contentLength < 0) ? true : false;
			}

			if (contentLength < 0) {
				request.setHeader(Header.HTTP.TRANSFER_ENCODING, "chunked");
				request.removeHeader(Header.HTTP.CONTENT_LENGTH);
				enableChunks = true;
			} else {
				request.removeHeader(Header.HTTP.TRANSFER_ENCODING);
				request.setHeader(Header.HTTP.CONTENT_LENGTH, Long.toString(contentLength));
			}
		} else {
			request.setHeader(Header.HTTP.CONTENT_LENGTH, "0");
		}

		// apply Content-Type header
		// use "application/octet-stream" as fallback if content type is unknown
		String contentType = request.getHeaderValue(Header.HTTP.CONTENT_TYPE);
		if (entity != null && contentType == null) {
			contentType = "application/octet-stream";
			request.setHeader(Header.HTTP.CONTENT_TYPE, contentType);
		}

		// apply Connection header
		if ( sendCloseConnection ||
			 usingHTTP10 ||
			 "close".equals(request.getHeaderValue(Header.HTTP.CONNECTION)))
		{
			closeBeforeUse = true;
			request.setHeader(Header.HTTP.CONNECTION, "close");
		}

		// apply UPGRADE header
		if (upgradeProtocol) {
			request.setHeader(Header.HTTP.UPGRADE, "TLS/1.0");
			request.setHeader(Header.HTTP.CONNECTION, "upgrade", true);
		}

		// apply cookies, credentials and session bases query params
		if (context != null) {
			context.applyCookies(request, this);
			context.applyCredentials(getUrl(request.getPath()), request);
			context.applyQueryParameters(request);
		}

		// apply User-Agent header
		if (userAgent != null && request.getHeader(Header.HTTP.USER_AGENT) == null) {
			request.setHeader(Header.HTTP.USER_AGENT, userAgent);
		}

		// apply Accept-Encoding header
		if (compressedResponses) {
			request.setHeader(Header.HTTP.ACCEPT_ENCODING, "gzip, deflate");
		}

		// send the request line (including Host line)
		// if request already contains a Host header, remove it!
		request.removeHeader(Header.HTTP.HOST);
		sendRequestLine(request);

		// send other HTTP headers
		sendHeader(request);

		// entity writes itself to the output stream
		if (entity != null) {
			if (request.logRequestEntity() && !secureProtocol) {
				out.enableWireTrace(request.getLocation());
			}
			if (enableChunks) {
				out.enableChunking(true);
				entity.write(out);
				out.enableChunking(false);
			} else {
				out.enableLimit(contentLength);
				entity.write(out);
				out.disableLimit();
			}
			out.disableWireTrace();
		}

		// flush buffer of request stream.
		out.flush();

		// if the request uses HTTP/1.0 and Content-Length is unknown we need
		// to shutdown the socket output stream to indicate that the request is finished.
		if (usingHTTP10 && closeOutStreamAfterSend) {
			out.shutdown();
		}
	}

	/**
	 * Assembles the first line of the request from protocol method, basePath, path, query
	 * and protocol version. If a proxy is used the URL is created with full net locator
	 * (including protocol, host and port).
	 * @param request  the request to send.
	 */
	private void sendRequestLine(IRequest request) throws IOException {
		StringBuffer sb = new StringBuffer();

		// append HTTP command
		String method = request.getMethod();
		sb.append(method).append(" ");

		// append authority part of the URL
		if ("CONNECT".equals(method)) {
			sb.append(host).append(':').append(port);
		} else {
			String path = request.getPath();
			if("*".equals(path)) {
				if ("OPTIONS".equals(method)) {
					sb.append('*');
				} else {
					throw new IllegalArgumentException("Invalid request path: '*' " +
						"is only allowed in OPTIONS requests.");
				}
			} else {
				if (useProxy && !tunnelProxy) {
					sb.append(protocol).append("://");
					sb.append(host);
					if (!equalsDefaultPort(port)) {
						sb.append(':').append(port);
					}
				}
				sb.append(getAbsolutePath(path));

				// append a query to the URL (if any)
				Query query = request.getQuery();
				if (query != null  &&  !query.isEmpty()) {
					sb.append('?').append(query.toString());
				}
			}
		}

		// append HTTP version
		sb.append(' ').append(version);

		if (TRACE.beDebug()) {
			TRACE.debugT("sendRequestLine(IRequest)", " ");
			TRACE.debugT("sendRequestLine(IRequest)", sb.toString());
		}

		// append "Host" header
		sb.append(CRLF);
		sb.append(Header.HTTP.HOST).append(": ").append(host);
		if (!equalsDefaultPort(port)) {
			sb.append(':').append(port);
		}
		sb.append(CRLF);

		if (TRACE.beDebug()) {
			TRACE.debugT(
				"sendRequestLine(IRequest)",
				"Host: {0}{1}",
				new Object[] {host, (!equalsDefaultPort(port))? (":" + Integer.toString(port)) : " "}
			);
		}

		out.write(sb.toString(), "ISO-8859-1");
	}


	/**
	 * Appends the header fields to the given string buffer. The order
	 * of the headers is unspecified.
	 * @param request  the request to send.
	 */
	private void sendHeader(IRequest request) throws IOException {
		StringBuffer sb = new StringBuffer();

		Iterator iter = request.getHeaderNames();
		if (iter != null) {
			while (iter.hasNext()) {
				String name = (String)iter.next();
				Header header = request.getHeader(name);
				sb.append(header.toString());
				sb.append(CRLF);

				if (TRACE.beDebug()) {
					if (name.equalsIgnoreCase(Header.HTTP.AUTHORIZATION) &&	header.getValue().startsWith("Basic")) {
						TRACE.debugT("sendHeader(IRequest)", "Authorization: Basic ********");
					} else {
						TRACE.debugT("sendHeader(IRequest)", "{0}", new Object[]{header.toString()});
					}
				}
			}
		}

		//append additional empty line (separates header from body)
		sb.append(CRLF);
		TRACE.debugT("sendHeader(IRequest)", " ");

		out.write(sb.toString(), "ISO-8859-1");
	}

	/**
	  * Reads and parses a response.
	  * Calls IResponse.initialize to read and parse the response.
	  * If a context is set, headers for cookies and credentials are
	  * extracted from the response.
	  * @param request  the request to send.
	  * @return The response retrieved.
	  * @throws IOException  if an i/o error occurs.
	  * @throws java.io.InterruptedIOException  if the connection timed out before a valid
	  * response was received.
	  * @throws HTTPException  if the response is malformed, invalid or incomplete.
	  */
	private Response getResponse(IRequest request) throws IOException, HTTPException {

		// parse the response
		Response response = new Response(request, secureProtocol);
		if (digestEnabled) {
			response.enableDigest(digestAlgorithm);
		}
		response.initialize(in);

		// check if server wants to switch protocol
		if (response.getStatus() == Status.SWITCHING_PROTOCOLS)	{
			response = upgrade(response);
		}

		// check if server wants to close connection
		closeBeforeUse |= response.closingConnection();

		// extract cookies and authentication information
		if (context == null) {
			context = new SessionContext();
		}
		context.setupCookies(response, this);
		context.setupCredentials(response);

		return response;
	}


	/**
	 * Checks whether the connection is able to serve an authentication
	 * requests.
	 * @param status  the status of the authenticate response
	 * @return true, if we have enough information and are allowed to authenticate
	 */
	private boolean acceptAuthentication(int status) {
		if (context == null || !context.getSendAuthentication()) {
			return false;
		} 
		if (status == Status.UNAUTHORIZED) {
			return (context.getUser() != null  && context.getPassword() != null );
		} else if (status == Status.PROXY_AUTHENTICATION_REQUIRED) {
			return (context.getProxyUser() != null  && context.getProxyPassword() != null );
		}
		return false;
	}


	/**
	 * Tries to upgrade the connection to the given protocol.
	 * @param response  the UPGRADE response
	 * @return the upgraded response
	 * @throws HTTPException  if the UPGRADE is not possible
	 */
	private Response upgrade(Response response) throws IOException, HTTPException
	{
		// UPGRADE is only allowed, if we received a 406 before
		if (!upgradeProtocol) {
			throw new HTTPException("Unexpected \"101 Switching Protocols\" received.");
		}

		// Check, if the response contains an UPGRADE header
		String upgradeHeader = response.getHeaderValue(Header.HTTP.UPGRADE);
		if (upgradeHeader != null) {
			TRACE.infoT("getResponse()", "Server wants to switch " +
				"protocols [to={0}]",	new Object[]{upgradeHeader});
		} else {
			throw new HTTPException("Missing UPGRADE header in \"101 Switching " +
				"Protocols\" response");
		}

		// Checks if destination protocol is TLS/1.0
		if (!upgradeHeader.startsWith("TLS/1.0")) {
			throw new HTTPException("UPGRADE protocol not supported [" + upgradeHeader + "]");
		}

		// UPGRADE is not allowed if we are already running on HTTPS
		if (secureProtocol) {
			throw new HTTPException("UPGRADE protocol denied. Already using secure protocol.");
		}

		// create an SSL provider if necessary
		if (secureSocketProvider == null) {
			createSecureSocketProvider();
		}

		// wrap the existing socket with an SSL socket
		socket = secureSocketProvider.createSocket(socket, host, port);
		prepareSocket();
		upgradeProtocol = false;
		secureProtocol = true;
		TRACE.infoT("upgradeSocket(String)", "Switching to protocol TLS/1.0 [succeeded]");

		// read the response again
		response.initialize(in);

		return response;
	}


	/**
	 * Parses the specified URL and extracts protocol, host, port and
	 * basePath.
	 * @param s   a valid URL with at least a protocol specifier (either "http"
	 * or "https") and a host.
	 * @throws MalformedURLException  if the URL is not valid.
	 */
	private void parseURL(String s) throws MalformedURLException {
		URL url = new URL(s);
		parseURL(url);
	}

	/**
	 * Extracts protocol, host, port and basePath from the given URL
	 * @param url   a valid URL with at least a protocol specifier (either "http"
	 * or "https") and a host.
	 * @throws MalformedURLException  if the URL is not valid.
	 */
	private void parseURL(URL url) throws MalformedURLException {
		String protocol = url.getProtocol();
		if ("http".equalsIgnoreCase(protocol)) {
			setProtocol(Protocol.HTTP);
		} else if ("https".equalsIgnoreCase(protocol)) {
			setProtocol(Protocol.HTTPS);
		} else {
			MalformedURLException ex = new MalformedURLException(
				"URI schema \"" + protocol + ":\" not suppported"
			);
			throw ex;
		}
		String host = url.getHost();
		if (host != null && host.length() > 0) {
			setHost(host);
		} else {
			MalformedURLException ex = new MalformedURLException("URL must have a host part");
			throw ex;
		}
		setPort(url.getPort());
		setBasePath(url.getPath());
	}

	/**
	 * Parses the specified URL and extracts proxy host and port.
	 * @param s   a valid URL.
	 * @throws MalformedURLException  if the URL is not valid.
	 */
	private void parseProxyURL(String s) throws MalformedURLException {
		URL url = new URL(s);
		parseProxyURL(url);
	}

	/**
	 * Extracts proxy host and port from the given URL
	 * @param url   a valid URL.
	 * @throws MalformedURLException  if the URL is not valid.
	 */
	private void parseProxyURL(URL url) throws MalformedURLException {
		String proxyHost = url.getHost();
		if (proxyHost != null && proxyHost.length() > 0) {
			setProxyHost(proxyHost);
		} else {
			MalformedURLException ex = new MalformedURLException("Proxy URL must have a host part");
			throw ex;
		}
		setProxyPort(url.getPort());
	}

	/**
	 * Checks whether the given port is the default port for the used protocol
	 */
	private boolean equalsDefaultPort(int port) {
		return ((protocol == Protocol.HTTP && port == DEFAULT_PORT) || port == DEFAULT_SSL_PORT);
	}

	// OK: <1.12.2004> ADDED
	// Actually this is for DSR Passport / IDE checks
	// but slightly extended for the case if other
	// monitors appear in the future
	
	private static Set monitors = new HashSet();

	public static void addMonitor(IConnectionMonitor monitor, Object auth)
	{
		monitors.add(monitor);
	}

	public static void removeMonitor(IConnectionMonitor monitor)
	{
		monitors.remove(monitor);
	}

	private synchronized void beforeSend(IRequest request)
	{
		Iterator id = monitors.iterator();
		while (id.hasNext())
		{
			IConnectionMonitor d = (IConnectionMonitor)id.next();
			try
			{
				d.beforeSend(this, request);
			}
			catch (Throwable t)
			{
				TRACE.warningT("beforeSend notification failed for protocol monitor " + d);
			}
		}
	}
	
	private synchronized void afterReceive(IResponse response)
	{
		Iterator id = monitors.iterator();
		while (id.hasNext())
		{
			IConnectionMonitor d = (IConnectionMonitor)id.next();
			try
			{
				d.afterReceive(this, response);
			}
			catch (Throwable t)
			{
				TRACE.warningT("afterReceive notification failed for protocol monitor " + d);
			}
		}
	}
					
}
