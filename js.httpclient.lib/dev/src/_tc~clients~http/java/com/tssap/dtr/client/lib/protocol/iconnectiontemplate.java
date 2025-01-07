package com.tssap.dtr.client.lib.protocol;

import com.tssap.dtr.client.lib.protocol.ssl.ISecureSocketProvider;

/**
 * This interface represents connection templates.
 * Connection templates may be used to clone <code>Connection</code> instances. 
 * They can be viewed as "passiv" connections,
 * i.e. they have not the capability of sending and receiving requests, but define the same properties
 * and methods. Templates are especially useful in conjunction with
 * connection pools. 
 * @see ITemplateProvider
 * @see IConnectionPool
 */
public interface IConnectionTemplate {

	/** The default port used for ordinary HTTP connections. 
	 *  The HTTPS default port number is 80. */
	static final int DEFAULT_PORT = 80;

	/** The default port used for HTTPS connections. 
	 *  The HTTPS default port number is 433. */
	static final int DEFAULT_SSL_PORT = 443;

	/** The default user agent identifier sent in request headers. 
	 *  The value of this String contant is "SAP HTTP CLIENT/6.40". */
	static final String USER_AGENT = "SAP HTTP CLIENT/6.40";

	/** Identifier for HTTP version 1.0. The value of this string constant is "HTTP/1.0" .*/	
	static final String HTTP_VERSION_1_0 = "HTTP/1.0";
	
	/** Identifier for HTTP version 1.1. The value of this string constant is "HTTP/1.1". */	
	static final String HTTP_VERSION_1_1 = "HTTP/1.1";

	/** The default HTTP version used to send requests. Equals <code>HTTP_VERSION_1_1</code>. */
	static final String DEFAULT_HTTP_VERSION = HTTP_VERSION_1_1;

	/** The default timeout for blocking read operations in milliseconds. 
	 *  The value of this constant corresponds to a timeout of 300 seconds. */
	static final int DEFAULT_TIMEOUT = 300000;
	
	/** The default timeout for opening sockets in milliseconds. 
	 *  The value of this constant corresponds to a timeout of 10 seconds. */	
	static final int DEFAULT_CONNECT_TIMEOUT = 10000;
	
	/** The default timeout intervall in milliseconds after which the connection
	 *  assumes that an open but unused socket has expired on the server.  
	 *  The value of this constant corresponds to a timeout of 10 seconds. */
	static final int EXPIRATION_TIMEOUT = 10000;	

	/** Determines how often the connection by default tries to repeat
	 *  a request if an exception is thrown while a request is pending. 
	 *  The value of this constant is 1. */
	static final int DEFAULT_REPETITIONS = 1;
	
	/** The default size of the socket's send buffer. 
	 *  The value of this constant corresponds to the default buffer size of the
	 *  underlying operating system. */
	static final int DEFAULT_SEND_BUFFER_SIZE = -1;

	/** The default size of the socket's receive buffer. 
	 *  The value of this constant corresponds to the default buffer size of the
	 *  underlying operating system. */
	static final int DEFAULT_RECEIVE_BUFFER_SIZE = -1;
	
	/** The default message digest algorithm.
	 *  The value of this constant equals "MD5". */
	static final String DEFAULT_DIGEST = "MD5";

	/** The default compression algorithm.
	 *  The value of this constant equals "gzip". */
	static final String DEFAULT_COMPRESSION = "gzip";



	/**
	 * Returns the complete URL of the connection including the basePath,
	 * i.e. protocol://host:port/basePath.
	 */
	String getUrl();
	
	/**
	 * Calculates the URL of a resource with a given relative or absolute path.
	 * If a base path has been set and path does not itself start with 
	 * "/", the method returns the concatenation of base path and path.
	 * Host, port and protocol are taken from the connection.
	 * @param path   any valid relative or absolute path according to RFC2616
	 * @return The absolute URL of the resource
	 */	
	URL getUrl(String path);	

	/**
	 * Returns the host of this connection.
	 * @return The name of the host.
	 */
	String getHost();

	/**
	 * Returns the port number of this connection.
	 * @return A port number. e.g. '80'.
	 */
	int getPort();

	/**
	 * Returns the proxy of this connection.
	 * @return The URL of the proxy.
	 */
	String getProxyUrl();

	/**
	 * Returns the proxy host of this connection.
	 * @return The URL of the proxy.
	 */
	String getProxyHost();

	/**
	 * Returns the port used by the proxy of this connection.
	 * @return The port used by the proxy.
	 */
	int getProxyPort();

	/**
	 * Checks whether this connection uses a proxy.
	 * @return True if the connection uses a proxy.
	 */
	boolean isUsingProxy();

	/**
	 * Checks whether this connection uses SOCKS
	 * protocol to connect to the proxy. 
	 * @return True if the connection uses SOCKS
	 */
	boolean isUsingSOCKS();

	/**
	 * Checks whether this connection tunnels a proxy.
	 * @return True, if the connection tunnels a proxy.
	 */
	boolean isTunnelingProxy();

	/**
	 * Returns the HTTP version used for request sent over this connection.
	 * @return Either "HTTP/1.0" or "HTTP/1.1". The default is "HTTP/1.1".
	 */
	Protocol getProtocol();

	/**
	 * Checks whether this connection uses "https" as protocol.
	 * @return True if the connection uses "https".
	 */
	boolean isSecureProtocol();

	/**
	 * Returns the secure socket provider used by this connection.
	 * @return  the selected provider for SSL sockets, or null.
	 */
	ISecureSocketProvider getSecureSocketProvider();

	/**
	 * Returns the HTTP version used for request sent over this connection.
	 * @return Either "HTTP/1.0" or "HTTP/1.1". The default is "HTTP/1.1".s
	 */
	String getHTTPVersion();

	/**
	 * Checks whether this connection uses HTTP/1.0.
	 * @return True, if this connection uses HTTP/1.0.
	 */
	boolean usingHTTP10();

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
	 */
	String getBasePath();

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
	String getAbsolutePath(String path);

	/**
	 * Returns the timeout intervall this connection waits for server responses.
	 * This timeout is applied to blocking read operation.
	 * @return The timeout in milliseconds.
	 * @see java.net.Socket#getSoTimeout()
	 */
	int getSocketReadTimeout();

	/**
	 * Returns the timeout intervall applied when the connection tries to open
	 * a socket to a remote server.
	 * @return The timeout in milliseconds.
	 */
	int getSocketConnectTimeout();

	/**
	 * Returns the timeout intervall after which the connection assumes
	 * that an open but unused socket has expired on the server. An expired socket
	 * then is closed and reopened when the next request is executed over this
	 * connection.
	 * @return The timeout in milliseconds.
	 */
	int getSocketExpirationTimeout();

	/**
	 * Returns how often the connection tries to repeat a request if an IOException
	 * occured (for example because the connection to the remost host is broken)
	 * or the server responded with an authorization failure.
	 * @return The number of allowed request repetitions.
	 */
	int getRequestRepetitions();

	/**
	 * Returns whether request repetition is applied to blocking read timeouts.
	 * @return true, if read timeouts lead to request repetition.
	 */
	boolean getRepeatOnTimeout();

	/**
	 * Return the size of the receive buffer of the underlying socket.
	 * If this method is executed while the socket of the connection is open
	 * it will return the actual size of the buffer read directly from the
	 * socket. Otherwise it will return the preset value defined by
	 * the <code>setReceiveBufferSize</code> method.
	 * @return The size of the receive buffer, or -1 if the size is unknown
	 * or not set.
	 */
	int getReceiveBufferSize();

	/**
	 * Returns the size of the send buffer of the underlying socket.
	 * If this method is executed while the socket of the connection is open
	 * it will return the actual size of the buffer read directly from the
	 * socket. Otherwise it will return the preset value defined by
	 * the <code>setSendBufferSize</code> method.
	 * @return The size of the send buffer, or -1 if the size is unknown
	 * or not set.
	 */
	int getSendBufferSize();

	/**
	 * Determines whether the connection uses compression.
	 * @return true, if either the compression of request or response
	 * bodies (or both) is enabled.
	 */
	boolean isCompressionEnabled();

	/**
	 * Returns the compression algorithm currently selected.
	 * @return either "gzip" or "deflate".
	 */
	String getCompressionAlgorithm();

	/**
	 * Checks whether the connection sends compressed request bodies.
	 * Currently the compression formats "gzip" and "deflate" are
	 * supported that most servers understand.
	 * @return true, if the connection sends compressed request bodies.
	 */
	boolean isRequestCompressionEnabled();


	/**
	 * Checks whether the connection sends AcceptEncoding headers to allow
	 * servers to compress responses. Currently the compression formats
	 * "gzip" and "deflate" are supported that most servers understand.
	 * @return true, if the connection sends AcceptEncoding header.
	 */
	boolean isResponseCompressionEnabled();

	/**
	 * Checks whether the calculation of hash values for response
	 * bodies is enabled.
	 * @return true, if the calculation of hashes is enabled.
	 */
	boolean isResponseDigestEnabled();

	/**
	 * Returns the hash algorithm used to calculate message digests
	 * for response bodies.
	 * @return the name of a hash algorithm like "MD5" or "SHA".
	 */
	String getDigestAlgorithm();

	/**
	 * Checks whether the connection automatically follows
	 * redirect responses (status codes 3xx).
	 * @return True, if the connection follows redirects automatically.
	 */
	boolean getFollowRedirects();

	/**
	 * Checks whether the connection automatically follows
	 * permanent redirect responses (status code "301 Move Permanently").
	 * @return True, if the connection follows permanent redirects automatically.
	 */
	boolean getFollowPermanentRedirects();

	/**
	 * Returns the value of the "User-Agent" header sent with each request.
	 * @return The string sent in the "User-Agent" header, i.e. "SAP HTTP CLIENT/6.40"
	 * by default.
	 */
	String getUserAgent();

	/**
	 * Returns the session context used by this connection.
	 * The session context is responsible for maintaining session cookies and care for
	 * authentication.
	 * @return A reference to the current session context.
	 */
	ISessionContext getSessionContext();

}
