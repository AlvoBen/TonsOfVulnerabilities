package com.tssap.dtr.client.lib.protocol;

import java.io.IOException;
import java.util.Iterator;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.util.Query;

/**
 * This interface represents a generic HTTP request.
 * <p>Methods are provided to determine the HTTP command (or any
 * extension command), the path, the headers, an optional query
 * string and the body of the request.</p>
 */
public interface IRequest {

	/**
	* Returns the protocol method of this request.
	* Note, since HTTP is an extensible protocol any method identifier could
	* be returned by this method, not only one of the standard HTTP methods
	* like 'GET' or 'PUT'.
	* @return The protocol method, e.g. 'GET', 'POST' or 'PROPFIND'.
	 */
	String getMethod();

	/**
	* Returns the path of this request.
	* @return The path relative to either the root ("/")
	* of the remote server or a predefined base path
	* (@see IConnection#getBasePath()).
	 */
	String getPath();

	/**
	* Sets the path of this request.
	* @param path the path of this request relative to either the root ("/") collection
	* or a predefined base path (@see IConnection#getBasePath()).
	 */
	void setPath(String path);

	/**
	* Returns the query string of this request.
	* The query string is appended to the URL (after a "?") to provide
	* additional parameters for a request like search parameters or session identifiers.
	* @return The query string, or null if no such string is used.
	 */
	String getQueryString();

	/**
	* Sets a query string for this request. Note, the query string
	* must apply to RFC2626 and must be URL-encoded (@see Encoder#encodeQuery)
	* @param query a query string according to RFC2616.
	 */
	void setQueryString(String query);

	/**
	 * Returns the optional query of this request.
	 * @return the query
	 */
	Query getQuery();

	/**
	* Sets a query for this request.
	* @param query the query. 
	 */
	void setQuery(Query query);

	/**
	* Returns the specified request header.
	* Note, header names are not case-sensitive.
	* @param name the name of a standard HTTP header or a client-defined header.
	* @return The specified header, of null if the header does not exist.
	 */
	Header getHeader(String name);

	/**
	* Returns the value of the specified request header.
	* Note, header names are not case-sensitive.
	* @param name the name of a standard HTTP header or a client-defined header.
	* @return The value of the specified header, of null if the header does not exist.
	 */
	String getHeaderValue(String name);

	/**
	* Returns the value of the specified request header, or a
	* default value if the header is not defined.
	* Note, header names are not case-sensitive.
	* @param name the name of a standard HTTP header or a client-defined header.
	* @param defaultValue the value that should be returned if the header is not
	* defined for this request.
	* @return The value of the specified header, or <code>defaultValue</code>.
	 */
	String getHeaderValue(String name, String defaultValue);

	/**
	* Returns the names of the headers defined by this request.
	* @return An enumeration of strings representing header names.
	 */
	Iterator getHeaderNames();

	/**
	* Defines a new or changes the value of an existing header.
	* @param header  a header from which name and value are taken to define
	* or change a header of this request.
	 */
	void setHeader(Header header);

	/**
	 * Defines a new or changes the value of an existing header.
	 * @param header  a header from which name and value are taken to define
	 * or change a header of this request.
	 * @param append if true, the value of the new header is appended to an already defined
	 * header of the same name, instead of replacing the old value.
	 */
	void setHeader(Header header, boolean append);

	/**
	* Defines a new or changes the value of an existing header.
	* @param name the name of a standard HTTP header or a client-defined header.
	* @param value the value of this header.
	 */
	void setHeader(String name, String value);

	/**
	* Defines a new or changes the value of an existing header.
	* @param name the name of a standard HTTP header or a client-defined header.
	* @param value the value of this header.
	* @param append if true, the value of the new header is appended to an already defined
	* header of the same name, instead of replacing the old value. The value is appended
	* with a comma as separator.
	 * @see IRequest#setHeader(String, String)
	 */
	void setHeader(String name, String value, boolean append);

	/**
	 * Defines a set of headers from the given array.
	 * @param headerList an array of Header objects.
	 */
	void setHeaders(Header[] headerList);

	/**
	* Removes the header with the specified name. If the header does not
	* exist, nothing happens.
	* @param name the name of a standard HTTP header or a client-defined header.
	 */
	void removeHeader(String name);
	
	/**
	 * Removes all headers from the request.
	 */
	void removeAllHeaders();

	/**
	* Returns the request entity.
	* @return The request entity, or null if the request has no request body.
	 */
	IRequestEntity getRequestEntity();

	/**
	* Sets the request entity.
	* Checks whether the entity defines content
	* length and content type and creates the corresponding HTTP headers.
	* @param entity the request entity.
	 */
	void setRequestEntity(IRequestEntity entity);

	/**
	 * Returns the response object.
	 * @return An implementation of IResponse that provides access to the
	 * status information, headers and the entity of the response.
	  */
	IResponse getResponse();

	/**
	 * Returns the response entity. If this method returns null after
	 * <code>perform</code> has been executed this indicates that the response has
	 * no body.
	 * @return The response entity, or null.
	 */
	IResponseEntity getResponseEntity();

	/**
	* Returns the content length of the request body.
	* @return The content length in bytes.
	 */
	long getContentLength();

	/**
	* Returns the content (MIME) type of the request body.
	* @return A MIME content type specifier.
	 */
	String getContentType();

	/**
	 * Executes the request.
	 * If the parameter sendCloseConnection is set to true, a "Connection: Close"
	 * header is send to the server indicating that the client wants to close
	 * the connection after this request.
	 *
	 * @param	connection   the connection that should be used to perform this request.
	 * @param  sendCloseConnection  if true a "Connection: Close" header is sent with
	 * this request.
	 * @return A response object that collects the status information and headers of the response
	 * and provides stream access to the body of the response.
	 * @throws InterruptedIOException  if the server didn't respond in the
	 * predefined timeout intervall, or the connection broke.
	 * @throws SocketException  if the socket for the connection cannot be
	 * opened. Usually this indicates that the server is down.
	 * @throws IOException  if an I/O error occurs while reading the response.
	 * @throws HTTPException  if the response is malformed, invalid, incomplete,
	 * or parsing failed.
	 */
	IResponse perform(IConnection connection, boolean sendCloseConnection) throws IOException, HTTPException;

	/**
	 * Executes the request.
	 * First, calls the prepareRequest method that derived classes may
	 * overwrite to create or modify the request entity before it is
	 * send. Second, calls Connection.send to execute
	 * the request and retrieve the response.
	 * <p>
	 * If a valid response was
	 * received the table of response parsers is searched for a parser
	 * that matches the MIME (content) type of the response.
	 * If no specialized parser is available, but a default parser defined, the
	 * default parser is used.
	 * <p>
	 * If any parser is available, IResponseParser.parse is called to read and
	 * parse the response body. The parser must return an implementation of IResponseEntity
	 * that provides access to the entity attributes and probably to the content.
	 * If no parser is available a StringEntity is created and the response body read
	 * into this entity.
	 * <p>
	 * The perform method does not change the state (i.e. opened or closed) of the
	 * connection. By default the connection remains open after a request/response
	 * cyle unless either the server or the clients sends a "Connection:close" header
	 * or the connection is timed out by the server. A client should explicitly call
	 * Connection.release() to indicate that a connection could be closed (or reused
	 * for example by a ConnectionPool).
	 *
	 * @param	connection   the connection that should be used to perform this request.
	 * @return A response object that collects the status information and headers of the response
	 * and provides stream access to the body of the response.
	 * @throws InterruptedIOException  if the server didn't respond in the
	 * predefined timeout intervall, or the connection broke.
	 * @throws SocketException  if the socket for the connection cannot be
	 * opened. Usually this indicates that the server is down.
	 * @throws IOException  if another I/O error occured while reading the response.
	 * @throws HTTPException  if the response is malformed, invalid, incomplete,
	 * or parsing failed.
	 */
	IResponse perform(IConnection connection) throws IOException, HTTPException;

	/**
	 * Prepares the request object for reuse.
	 */
	void clear();
	
	/**
	 * Checks whether the body (if any) of this request should be traced
	 * to the common or any given specific location. Note, entity traces always
	 * are logged with category DEBUG.
	 */
	boolean logRequestEntity();
	
	/**
	 * Checks whether the body (if any) of the response following this request
	 * should be traced to the common or any given specific location. Note, entity 
	 * traces always are logged with category DEBUG.
	 */
	boolean logResponseEntity();
	
	/**
	 * Returns the trace location for logging of request and response entities.
	 * @return a trace location, or null. In the latter case, traces should be
	 * written to the common trace location.
	 */
	Location getLocation();	
}
