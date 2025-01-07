package com.tssap.dtr.client.lib.protocol;

import java.io.IOException;

/**
 * This interface represents a HTTP or HTTPS connection to a certain host.
 * It adds methods to <code>IConnectionTemplate</code> for sending of
 * requests and to open/close the connection. Furthermore it allows
 * to define some critical connection parameters like timeouts and
 * number of request repetitions that often have to be changed on
 * a per-request basis. 
 */
public interface IConnection extends IConnectionTemplate {

	/**
	 * Sets the timeout intervall this connection waits for server responses.
	 * This timeout is applied to blocking read operation.<br/>
	 * The new setting will become effective for the next request
	 * executed by the <code>send</code> method. Note, if the connection is 
	 * open it will be closed and reopended automatically before the 
	 * next request is issued.
	 * @param timeout the timeout in milliseconds. Setting the timeout to
	 * zero (or a negative value) defines an infinite timeout for blocking read 
	 * operations.
	 */
	void setSocketReadTimeout(int timeout);
	
	/**
	 * Opens the connection.	/**
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
	void setRequestRepetitions(int repetitions, boolean repeatOnTimeout);	


	/**
	 * <p>If the connection was marked to be closed before
	 * next use, the socket is destroyed. If the socket is invalid or
	 * has been destroyed before (either exlicitly by remote server or client,
	 * or implicitly by a timeout), a new socket is created and opened.</p>
	 * @throws IOException - if an i/o error occurs, i.e. the host is unknown,
	 * the socket or streams could not be created, or the host did not respond within
	 * the predefined timeout interval (@see Connection#setSocketTimeout(int)).
	 */
	void open() throws IOException;

	/**
	* Closes the connection and releases the underlying socket and streams.
	 */
	void close();

	/**
	* <p>Sends the specified request and retrieves the corresponding response.</p>
	* <p>By default, the socket will remain open after completion of this method.</p>
	* @param request the request to send.
	* @return The response retrieved.
	* @throws java.io.IOException - if an i/o error occurs.
	* @throws java.net.SocketException - if the socket became invalid during the
	* request-response cycle.
	* @throws java.io.InterruptedIOException - if the connection timed out before a valid
	* response was received.
	* @throws HTTPException - if the response is malformed, invalid or incomplete.
	 */
	IResponse send(IRequest request) throws IOException, HTTPException;

	/**
	* <p>Sends the specified request and retrieves the corresponding response.</p>
	* <p>If the socket of the connection has not yet been created, has been
	* closed after a previous request either by the client or the remote
	* host, or is invalid for some other reason, a new socket and associated data
	* streams are created and opened.</p>
	* <p>If the <code>sendCloseConnection</code> parameter is true a
	* "Connection: close" header
	* is sent to the remote host and the socket of the connection is destroyed after
	* the response has been received. Otherwise the socket remains open
	* (persistent connection).</p>
	* <p>If the remote host requests authentication and a valid authenticator and
	* authentication parameters (user, password) have been set in the session
	* context (@see Connection#setSessionContext(ISessionContext)), the request
	* is repeated automatically based on the challenge returned by the host.</p>
	* <p>Cookies are assigned to the request depending on the domain of the
	* request URL if a session context has been defined and the sending of cookies
	* is switched on (default, @see ISessionContext#setSendCookies).</p>
	*
	* @param request  the request to send.
	* @param sendCloseConnection  a "Connection: close" header is send to the server
	* and the socket is destroyed after completition of the request.
	* @return The response retrieved.
	* @throws java.io.IOException - if an i/o error occurs.
	* @throws java.net.SocketException - if the socket became invalid during the request-response cycle.
	* @throws java.io.InterruptedIOException - if the connection timed out before a valid
	* response was received.
	* @throws HTTPException - if the response is malformed, invalid or incomplete.
	 */
	IResponse send(IRequest request, boolean sendCloseConnection) throws IOException, HTTPException;

	/**
	 * Manually redirects a previously performed request.
	 * @param response  the response with a redirect status.
	 * @return  the response to the redirected request, or null if the redirect
	 * was not possible
	 * @throws IOException  if an i/o error occured
	 * @throws HTTPException  if the redirected response was not valid.
	 */
	IResponse redirect(IResponse response) throws IOException, HTTPException;	

	/**
	 * Manually redirects a previously performed request.
	 * @param response  the response with a redirect status.
	 * @param sendCloseConnection   a "Connection: close" header is send to the server
	 * and the socket is destroyed after completition of the request.
	 * @return  the response to the redirected request, or null if the redirect
	 * was not possible
	 * @throws IOException  if an i/o error occured
	 * @throws HTTPException  if the redirected response was not valid.
	 */
	IResponse redirect(IResponse response, boolean sendCloseConnection) throws IOException, HTTPException;
}
