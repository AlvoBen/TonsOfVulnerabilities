package com.tssap.dtr.client.lib.protocol.templates;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tssap.dtr.client.lib.protocol.IConnectionTemplate;
import com.tssap.dtr.client.lib.protocol.ISessionContext;
import com.tssap.dtr.client.lib.protocol.Protocol;
import com.tssap.dtr.client.lib.protocol.URL;
import com.tssap.dtr.client.lib.protocol.session.SessionContext;
import com.tssap.dtr.client.lib.protocol.ssl.ISecureSocketProvider;
import com.tssap.dtr.client.lib.protocol.util.Tokenizer;

/** 
 * Connection templates may be used to create Connection instances.
 * This implementation of <code>IConnectionTemplate</code> provides the
 * same methods as <code>Connection</code> to define the parameters
 * of a connection. However, <code>ConnectionTemplate</code> never
 * creates sockets and therefore cannot be used to send/receive requests.
 */
public class ConnectionTemplate implements IConnectionTemplate {

	/** The session context with authentication information and cookies */
	protected ISessionContext context;

	/** URL of the remote host */
	protected String host;
	/** Port number used by the remote host */
	protected int port = IConnectionTemplate.DEFAULT_PORT;
	/** Path prefix for request URLs */
	protected String basePath = "/";

	/** URL of a proxy, or null */
	protected String proxy;
	/** Port number used by the proxy */
	protected int proxyPort;
	/** If true then the connection uses the specified proxy */
	protected boolean useProxy = false;
	/** If true (and a proxy selected) the proxy is tunneled with the CONNECT command */
	protected boolean tunnelProxy = false;
	
	private Set excludedHosts;
	private Set excludedDomains;	
	/** If true, the proxy is connected via SOCKS protocol */
	protected boolean useSOCKS = false;
	
	
	/** The protocol to use, either HTTP or HTTPS */
	protected Protocol protocol = Protocol.HTTP;
	/** If true, the protocol is secure, i.e. HTTPS */
	protected boolean secureProtocol = false;
	/** The SSL socket provider to use */
	protected ISecureSocketProvider secureSocketProvider;

	/** The HTTP version to use, eith HTTP/1.0 or HTTP/1.1 */
	protected String version = IConnectionTemplate.DEFAULT_HTTP_VERSION;
	/** If true, the connection is forced to use HTTP/1.0 protocol */
	protected boolean usingHTTP10 = false;

	/** The user agent identifier sent in request headers */
	protected String userAgent = IConnectionTemplate.USER_AGENT;

	/** The timeout applied to read operations from the socket (in milliseconds) */
	protected int readTimeout = IConnectionTemplate.DEFAULT_TIMEOUT;
	/** The timeout applied to opening a socket */
	protected int connectTimeout = IConnectionTemplate.DEFAULT_CONNECT_TIMEOUT;
	/** The expiration timeout for persistent connections. */
	protected int expirationTimeout = IConnectionTemplate.EXPIRATION_TIMEOUT;

	/**
	 * Determines whether and how often request may be repeated if the
	 * first request failed due to an IOError or an Authentication response.
	 * The default is 1.
	 */
	protected int requestRepetitions = IConnectionTemplate.DEFAULT_REPETITIONS;
	/**
	 * Determines whether request should be repeated also for timeouts on
	 * read operations. The default is fals;
	 */
	protected boolean repeatOnTimeout = false;


	/**
	 * If true, the connection automatically follows redirects.
	 * This setting applies to response status codes 302 Moved Temporarily,
	 * 302 Found and 307 Temporary Redirect.
	 * The default is true.
	 */
	protected boolean followRedirects = true;

	/**
	 * If true, the connection also follows redirects that change
	 * host, port or protocol of the connection.
	 * The default is false.
	 */
	protected boolean followForeignRedirects = false;

	/**
	 * If true, the connection also follows redirects to use a
	 * proxy instead of a direct connection.
	 * The default is false.
	 */
	protected boolean followProxyRedirects = false;

	protected boolean followRedirectAllMethods = false;

	/** The maximum number of redirects. */
	protected int maxRedirects = 1;

	/**
	 * If true, the connection also follows permanent redirects.
	 * This settting applies to response status code 301 Moved Permanent.
	 * The default is false.
	 */
	protected boolean followPermanentRedirects = false;

	/** Determines whether the connection should send AcceptEncoding header */
	protected boolean compressedResponses = false;
	/** Determines whether the connection should send AcceptEncoding header */
	protected boolean compressedRequests = false;
	/** Determines the algorithm used to compress requests. */
	protected String compressionAlgorithm = IConnectionTemplate.DEFAULT_COMPRESSION;

	/** Calculate a digest value for response bodies */
	protected boolean digestEnabled = false;
	/** Determines the algorithm used to calculate digests. */
	protected String digestAlgorithm = IConnectionTemplate.DEFAULT_DIGEST;

	/** The size of the socket's receive buffer */
	protected int receiveBufferSize = -1;
	/** The size of the socket's send buffer */
	protected int sendBufferSize = -1;


	/**
	 * Creates a connection template with default setting.
	 */
	public ConnectionTemplate() {
	}
	
	/**
	 * Creates a new connection from the given template.
	 * All parameters and the session context are copied from the
	 * original template.
	 * @param template the connection template from which to copy.
	 */
	public ConnectionTemplate(IConnectionTemplate template) {
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
	}	

	/**
	 * Creates a connection template for the given URL.
	 * @param url a valid URL with at least a protocol specifier (either "http"
	 * or "https") and a host. Optionally a port number and a base path may be given.
	 * @throws MalformedURLException  if the URL is not valid.
	 */
	public ConnectionTemplate(String url) throws MalformedURLException {
		parseURL(url);
	}

	/**
	 * Creates a connection template for the given URL and session context.
	 * @param url a valid URL string with at least a protocol specifier (either "http"
	 * or "https") and a host. Optionally a port number and a base path may be given.
	 * @param context a session context providing user and session releated information.
	 * @throws MalformedURLException  if the URL is not valid.
	 */
	public ConnectionTemplate(String url, ISessionContext context) throws MalformedURLException {
		this(url);
		setSessionContext(context);
	}

	/**
	 * Creates a connection template for the given URL.
	 * @param url a valid URL with at least a protocol specifier (either "http"
	 * or "https") and a host. Optionally a port number and a base path may be given.
	 * @throws MalformedURLException  if the URL is not valid.
	 */
	public ConnectionTemplate(URL url) throws MalformedURLException {
		parseURL(url);
	}

	/**
	 * Creates a connection for the given URL template and session context.
	 * @param url a valid URL with at least a protocol specifier (either "http"
	 * or "https") and a host. Optionally a port number and a base path may be given.
	 * @param context a session context providing user and session releated information.
	 * @throws MalformedURLException  if the URL is not valid.
	 */
	public ConnectionTemplate(URL url, ISessionContext context) throws MalformedURLException {
		this(url);
		setSessionContext(context);
	}

	/**
	 * Creates a connection template for the given host and port.
	 * @param host a valid host URL or IP address.
	 * @param port a port number.
	 */
	public ConnectionTemplate(String host, int port) {
		setHost(host);
		setPort(port);
	}

	/**
	 * Creates a connection template for the specified host, port and session context.
	 * @param host a valid host URL or IP address.
	 * @param port a port number.
	 * @param context a session context providing user and session releated information.
	 */
	public ConnectionTemplate(String host, int port, ISessionContext context) {
		this(host, port);
		setSessionContext(context);
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
	 * Returns the host.
	 * @return The URL of a host.
	 * @see IConnection#getHost()
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Sets the host.
	 * @param host the URL or IP address of a remote host.
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Returns the port number.
	 * @return A port number. e.g. '80'.
	 * @see IConnection#getPort()
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port number.
	 * @param port a valid port number, usually set to '80' (default),
	 * '1080' or '8080' for HTTP.
	 * connections.
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Returns the URL of the proxy.
	 * @return The URL of the proxy.
	 * @see IConnection#getProxyHost()
	 */
	public String getProxyUrl() {
		String url = getProxyHost();
		if (getProxyPort() >0 ) {
			url += ":" + getProxyPort();
		}
		return url;
	}

	/**
	 * Sets the URL of the proxy.
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
	 * Sets the URL of the proxy.
	 * @param url  a valid URL.
	 */
	public void setProxyUrl(URL url) throws MalformedURLException {
		parseProxyURL(url);
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
	 * Returns the proxy host.
	 * @return The URL of the proxy.
	 * @see IConnection#getProxyHost()
	 */
	public String getProxyHost() {
		return proxy;
	}

	/**
	 * Returns the proxy port.
	 * @return The port used by the proxy.
	 * @see IConnection#getProxyPort()
	 */
	public int getProxyPort() {
		return proxyPort;
	}

	/**
	 * Sets a proxy host.
	 * @param proxy the host URL or IP address of the proxy.
	 */
	public void setProxyHost(String proxy) {
		this.proxy = proxy;
	}

	/**
	 * Sets the proxy port.
	 * @param proxyPort a valid port number.
	 */
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * Determines whetherto use a proxy.
	 * @param useProxy if true, the connection uses the specified proxy.
	 */
	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}
	
	public void setUseProxy(boolean useProxy, String exclusions) {
		this.useProxy = useProxy;
		List parts = Tokenizer.partsOf(exclusions, ",;");
		for (int i=0; i < parts.size(); ++i) {
			String part = (String)parts.get(i);
			if (part.startsWith(".")) {
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
	}	

	/**
	 * Checks whether a proxy is used.
	 * @return True if the connection uses a proxy.
	 * @see IConnection#isUsingProxy()
	 */
	public boolean isUsingProxy() {
		return useProxy;
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
	 * Determines whether the proxy should
	 * be switched to tunneling mode (with the help of the CONNECT
	 * @param tunnelProxy  if true, the connection tries to tunnel the
	 * selected proxy.
	 */
	public void setTunnelProxy(boolean tunnelProxy) {
		this.tunnelProxy = tunnelProxy;
	}
	
	/**
	 * Checks whether the proxy is tunneled.
	 * @return True, if the connection tunnels a proxy.
	 * @see IConnection#isTunnelingProxy
	 */
	public boolean isTunnelingProxy() {
		return tunnelProxy;
	}

	/**
	 * Returns the HTTP version used for requests.
	 * @return Either "HTTP/1.0" or "HTTP/1.1".
	 * @see IConnection#getHTTPVersion()
	 */
	public String getHTTPVersion() {
		return version;
	}

	/**
	 * Sets the HTTP version used for requests.
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
			throw new IllegalArgumentException("HTTP version " + version + "not supported");
		}
	}

	/**
	 * Checks whether to use HTTP/1.0 for requests.
	 * @return True, if this connection uses HTTP/1.0.
	 * @see IConnection#usingHTTP10()
	 */
	public boolean usingHTTP10() {
		return usingHTTP10;
	}

	/**
	 * Returns the session context. If no session context
	 * has been set so far, an initial session context is created.
	 * @return A reference to the current session context.
	 */
	public ISessionContext getSessionContext() {
		if (context == null) {
			context = new SessionContext();
		}
		return context;
	}

	/**
	 * Assigns a new session context.
	 * @param context the new session context to use with this connection.
	 */
	public void setSessionContext(ISessionContext ctx) {
		this.context = ctx;
	}
	
	/**
	 * Returns the protocol.
	 * @return Either Protocol.HTTP or Protocol.HTTPS.
	 * @see IConnection#getProtocol()
	 */
	public Protocol getProtocol() {
		return protocol;
	}

	/**
	 * Sets the protocol.
	 * @param protocol  either Protocol.HTTP or Protocol.HTTPS.
	 */
	public void setProtocol(Protocol protocol) {
		if (protocol.equals(Protocol.HTTPS)) {
			secureProtocol = true;
		} else if (protocol.equals(Protocol.HTTP)) {
			secureProtocol = false;
		} 
		if (equalsDefaultPort(port)) {
			port = getDefaultPort(protocol);
		}
		this.protocol = protocol;		
	}

	/**
	 * Checks whether the protocol is secure.
	 * @return True if the connection uses "https".
	 * @see IConnection#isSecureProtocol()
	 */
	public boolean isSecureProtocol() {
		return secureProtocol;
	}

	/**
	 * Sets the secure socket provider for this connection.
	 * @param provider  a provider of SSL sockets.
	 */
	public void setSecureSocketProvider(ISecureSocketProvider provider) {
		this.secureSocketProvider = provider;			
	}
	
	/**
	 * Returns the secure socket provider used by this connection.
	 * @return  the selected provider of SSL sockets, or null.
	 */	
	public ISecureSocketProvider getSecureSocketProvider() {
		return secureSocketProvider;
	}

	/**
	 * Returns the base path of the URL.
	 * <p>The base path is used to resolve relative paths in URLs (that do
	 * not start with a "/").</p>
	 * @return A path prefix.
	 * @see IConnection#getBasePath()
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * Sets the base path.
	 * <p>The base path is used to resolve relative paths in URLs (that do
	 * not start with a "/").
	 * @param path a path fragment.
	 */
	public void setBasePath(String path) {
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

	/**
	 * Calculates the absolute path of a given path.
	 * <p>If a base path has been
	 * set and path does not itself start with "/", the method returns
	 * the concatenation of base path and path.</p>
	 * @param path any valid relative path according to RFC2616
	 * @return The absolute path (starts always with '/').
	 */
	public String getAbsolutePath(String path) {
		if (path==null) {
			return "/";
		} else if (!path.startsWith("/") && basePath != null) {
			return basePath + path;
		} else {
			return path;
		}
	}

	/**
	 * Returns the timeout intervall to wait for server responses.
	 * <p>This timeout is applied to blocking read operation.</p>
	 * @return The timeout in milliseconds.
	 * @see IConnection#getSocketReadTimeout()
	 * @see Socket#getSoTimeout()
	 */
	public int getSocketReadTimeout() {
		return readTimeout;
	}

	/**
	 * Sets the timeout intervall to wait for server responses.
	 * <p>This timeout is applied to blocking read operation.</p>
	 * @param timeout the timeout in milliseconds. Setting the timeout to
	 * zero defines an infinite timeout blocking read operations.
	 * @see Socket#setSoTimeout
	 */
	public void setSocketReadTimeout(int timeout) {
		readTimeout = timeout;
	}

	/**
	 * Returns the timeout intervall to wait for connecting to the host.
	 * @return The timeout in milliseconds.
	 */
	public int getSocketConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * Sets the timeout intervall to wait for connecting to the host.
//	 * @return The timeout in milliseconds.
	 */
	public void setSocketConnectTimeout(int timeout) {
		connectTimeout = timeout;
	}

	/**
	 * Returns the timeout intervall after which the connection assumes
	 * that an open but unused socket has expired on the server.
	 * @return The timeout in milliseconds.
	 */
	public int getSocketExpirationTimeout() {
		return expirationTimeout;
	}

	/**
	 * Set the timeout intervall after which the connection assumes
	 * that an open but unused socket has expired on the server.
//	 * @return timeout  the timeout in milliseconds. The default is 10 seconds.
	 */
	public void setSocketExpirationTimeout(int timeout) {
		expirationTimeout = timeout;
	}

	/**
	 * Returns how often the connection tries to repeat a request if an IOException
	 * occured (for example because the connection to the remost host is broken)
	 * or the server responded with an authorization failure.
	 * @return The number of allowed request repetitions.
	 */
	public int getRequestRepetitions() {
		return requestRepetitions;
	}

	/**
	 * Returns whether request repetition is applied to blocking read timeouts.
	 * @return true, if read timeouts lead to request repetition.
	 */
	public boolean getRepeatOnTimeout() {
		return repeatOnTimeout;
	}

	/**
	 * Sets how often the connection tries to repeat a request if an IOException
	 * occured (for example because the connection to the remost host is broken)
	 * or the server responded with an authorization failure. By default the
	 * repetition counter is set to one. Note, according to the HTTP specification
	 * a request in general should not be repeated more than once.
	 * @param repetitions  the number of allowed request repetitions.
	 * @param repeatOnTimeout  if true, the repetition mechanism is applied to
	 * blocking read timeouts, too. The default is false.
	 * The default is one.
	 */
	public void setRequestRepetitions(int repetitions, boolean repeatOnTimeout) {
		requestRepetitions = repetitions;
		this.repeatOnTimeout = repeatOnTimeout;
	}

	/**
	 * Return the size of the receive buffer of the underlying socket.
	 * @return The size of the receive buffer, or -1 if the size is unknown
	 * or not set.
	 */
	public int getReceiveBufferSize() {
		return receiveBufferSize;
	}

	/**
	 * Sets the size of the receive buffer of the underlying socket.
	 * <p>Increasing buffer size can increase the performance of network
	 * I/O for high-volume connection, while decreasing it can help reduce
	 * the backlog of incoming data.
	 * Note the given size is only a hint to the operation
	 * system to adjust the buffer size of its network I/O.</p>
	 * @param size   the size to which to set the receive buffer size.
	 * This value must be greater than 0.
	 */
	public void setReceiveBufferSize(int size) {
		this.receiveBufferSize = size;
	}

	/**
	 * Returns the size of the send buffer of the underlying socket.
	 * @return The size of the send buffer, or -1 if the size is unknown
	 * or not set.
	 */
	public int getSendBufferSize() {
		return sendBufferSize;
	}

	/**
	 * Sets the size of the send buffer of the underlying socket.
	 * <p>Increasing buffer size can increase the performance of network
	 * I/O for high-volume connection, while decreasing it can help reduce
	 * the backlog of incoming data.</p>
	 * <p>Note the given size is only a hint to the operation
	 * system to adjust the buffer size of its network I/O.</p>
	 * @param size   the size to which to set the receive buffer size.
	 * This value must be greater than 0.
	 */
	public void setSendBufferSize(int size) {
		this.sendBufferSize = size;
	}



	/** 
	 * Enables or disables the usage of compression of message bodies for
	 * communication between client and server.
	 * Enables or disables the sending of compressed request bodies and
	 * the sending of "AcceptEncoding" headers.
	 * Compression may considerably reduce 
	 * the bandwith needs of a connection. 
	 * Unless a compression algorithm has been selected explicitly, 
	 * the connection uses "gzip" encoding to compress requests bodies, 
	 * but allows the server to choose freely between gzip" and "deflate" 
	 * encoding for responses.
	 * Note, servers are not required to support
	 * compression and therefore may ignore "Accept-Encoding" headers
	 * or respond with a 415 (Unsupported Media Type) status
	 * code on compressed request bodies.
	 * Clients enabling compression should be aware of this situation.
	 * @param enable  if true, compression is used by the connection.
	 */
	public void enableCompression(boolean enable) {
		this.compressedRequests = enable;
		this.compressedResponses = enable;
	}
	
	/**
	 * Determines whether the connection uses compression.
	 * @return true, if either the compression of request or response
	 * bodies (or both) is enabled.
	 */
	public boolean isCompressionEnabled() {
		return compressedRequests || compressedResponses; 
	}
	
	/**
	 * Enables or disables the sending of compressed request bodies. Currently the
	 * compression formats "gzip" and "deflate" are supported that most
	 * servers understand. If no other format is selected "gzip" is used.
	 * Compressing may considerably reduce the bandwith needs
	 * of a connection. Note, if a server is not able to understand the selected
	 * compression format, it will respond with a 415 (Unsupported Media Type) status
	 * code. Clients enabling request compression should be aware of this situation.
	 * @param enable  if true, request bodies are compressed using the selected
	 * compression algorithm (@see Connection.setCompressionAlgorithm).
	 */
	public void enableRequestCompression(boolean enable) {
		this.compressedRequests = enable;
	}	
		
	/**
	 * Checks whether the connection sends compressed request bodies. 
	 * Currently the compression formats "gzip" and "deflate" are 
	 * supported that most servers understand.
	 * @return true, if the connection sends compressed request bodies.
	 */
	public boolean isRequestCompressionEnabled() {
		return compressedRequests;
	}	
	
	/**
	 * Enabled or disables the sending of "Accept-Encoding" headers. If the sending
	 * is enabled the server is allowed to compress responses. Currently the
	 * compression formats "gzip" and "deflate" are supported that most
	 * servers understand. Compression may considerably reduce the bandwith needs
	 * of a connection. However, servers are not required to support
	 * compression and therefore may ignore "Accept-Encoding" headers.
	 * @param enable  if true, the server may compress responses, otherwise
	 * compression is switched off.
	 */
	public void enableResponseCompression(boolean enable) {
		this.compressedResponses = enable;
	}
	
	/**
	 * Checks whether the connection sends "Accept-Encoding" headers to allow
	 * servers to compress responses. Currently the compression formats
	 * "gzip" and "deflate" are supported that most servers understand.
	 * @return true, if the connection sends "Accept-Encoding" header.
	 */
	public boolean isResponseCompressionEnabled() {
		return compressedResponses;
	}	
		
	/**
	 * Returns the compression algorithm currently selected.
	 * @return either "gzip" or "deflate".
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
	 * Returns the content encodings supported by this stream.
	 * @return A comma separated list of supported encodings.
	 */
	static public String[] getSupportedCompressionAlgorithms() {
		return new String[] {"gzip", "deflate"};
	}	
	
	
	/**
	 * Enables or disables the calculation of a hash value for
	 * response bodies. Such a "message digest" may be used to
	 * check the validity of a response body received from a server.
	 * The digest value may for example be checked against a "Content-MD5"
	 * header in the response if available.
	 * @see Connection#setDigestAlgorithm(String)
//	 * @see IResponse#getContentDigest()
	 * @param enable  if true, the calculation of a message digest
	 * is switched on for the next request.
	 */
	public void enableResponseDigest(boolean enable) {
		this.digestEnabled = enable;
	}
	
	/**
	 * Checks whether the calculation of hash values for response
	 * bodies is enabled.
	 * @return true, if the calculation of hashes is enabled.
	 */
	public boolean isResponseDigestEnabled() {
		return digestEnabled;	
	}			
	
	/**
	 * Returns the hash algorithm used to calculate message digests
	 * for response bodies.
	 * @return the name of a hash algorithm like "MD5" or "SHA".
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
	 * redirect responses (status code 3xx).
	 * <p>Note: Redirects are not yet implemented.</p>
	 * @return True, if the connection follows redirects automatically.
	 * @see IConnection#getFollowRedirects()
	 */
	public boolean getFollowRedirects() {
		return followRedirects;
	}

	/**
	 * Sets whether the connection should automatically follow
	 * redirect responses (status code 3xx).
	 * <p>Note: Redirects are not yet implemented.</p>
	 * @param followRedirects if true the connection tries to follow redirects automatically (default).
	 */
	public void setFollowRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
	}
	
	/**
	 * Sets whether the connection should also automatically follow permanent
	 * redirect responses (status code "301 Move Permanently").
	 * Note, enabling permanent redirects enables also temporary redirects. 
	 * Disabling temporary redirects also disables permanent redirects.
	 * The new setting will become
	 * effective for the next request executed by the send method.
	 * issued.
	 * @param followPermanentRedirects  if true, the connection tries to
	 * follow also permanent redirects.
	 */
	public void setFollowPermanentRedirects(boolean followPermanentRedirects) {
		if (followPermanentRedirects) {
			this.followRedirects = true;
		}
		this.followPermanentRedirects = followPermanentRedirects;
	}	

	/**
	 * Checks whether the connection automatically follows
	 * permanent redirect responses (status code "301 Move Permanently").
	 * @return True, if the connection follows permanent redirects automatically.
	 * @see IConnectionTemplate#getFollowPermanentRedirects()
	 */
	public boolean getFollowPermanentRedirects() {
		return followPermanentRedirects;
	}		

	/**
	 * Returns the value of the "User-Agent" header sent with each request.
	 * @return The string sent in the "User-Agent" header.
	 * @see IConnection#getUserAgent()
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * Sets the value of the "User-Agent" header sent with each request.
	 * @param userAgent a string specifying the user agent.
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	/**
	 * Returns a string representation of this connection template
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("host<").append(getUrl()).append(">");
		if (context != null) {
			s.append(" ,").append(context.toString());
		}		
		if (proxy != null) {
			s.append(", proxy<").append(getProxyUrl()).append(">");
			s.append("<state:").append(isUsingProxy()? "on" : "off").append(">");
			s.append(",<connect:").append(isTunnelingProxy()? "tunnel" : "direct").append(">");
		}		
		s.append(", timeout<read:").append(getSocketReadTimeout()).append(">");
		s.append("<connect:").append(getSocketConnectTimeout()).append(">");		
		s.append("<expires:").append(getSocketExpirationTimeout()).append(">");				
		s.append(", repeat<").append(getRequestRepetitions()).append(">");			
		s.append("<on-timeout:").append(getRepeatOnTimeout()? "on" : "off").append(">");		
		s.append(", compress<requests:").append(isRequestCompressionEnabled()? "on" : "off").append(">");
		s.append("<responses:").append(isResponseCompressionEnabled()? "on" : "off").append(">");
		s.append("<").append(getCompressionAlgorithm()).append(">");		
		s.append(", buffer-sizes<send:").append(getSendBufferSize()).append(">");
		s.append("<receive:=").append(getReceiveBufferSize()).append(">");	
		s.append(", digest<").append(isResponseDigestEnabled()? "on" : "off").append(">");
		s.append("<").append(getDigestAlgorithm()).append(">");	
		s.append(", version<").append(getHTTPVersion()).append(">");
		s.append(", user-agent<").append(getUserAgent()).append(">");
		s.append(", redirects<").append(getFollowRedirects()? "follow" : "ignore").append(">");
		
		return s.toString();
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
		if ("http".equals(protocol)) {
			setProtocol(Protocol.HTTP);
		} else if ("https".equals(protocol)) {
			setProtocol(Protocol.HTTPS);
		} else {
			throw new MalformedURLException("URI schema \"" + protocol + ":\" not suppported");
		}
		String host = url.getHost();
		if (host != null && host.length() > 0) {
			setHost(host);
		} else {
			throw new MalformedURLException("URL must have a host part");
		}
		int port = url.getPort();
		if (port > 0 && !equalsDefaultPort(port)) {
			setPort(port);
		} else {
			setPort(getDefaultPort(this.protocol));
		}
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
			throw new MalformedURLException("Proxy URL must have a host part");
		}
		int proxyPort = url.getPort();
		if (proxyPort > 0 && !equalsDefaultPort(proxyPort)) {
			setProxyPort(proxyPort);
		} else {
			setProxyPort(getDefaultPort(protocol));
		}		
	}

	/**
	 * Checks whether the given port is the default port for the used protocol
	 */
	private boolean equalsDefaultPort(int port) {
		return ((protocol == Protocol.HTTP && port == DEFAULT_PORT) || port == DEFAULT_SSL_PORT);
	}

	/**
	 * Returns the default port for the current protocol
	 */
	private int getDefaultPort(Protocol protocol) {
		return (protocol == Protocol.HTTP) ? DEFAULT_PORT : DEFAULT_SSL_PORT;
	}
	

}
