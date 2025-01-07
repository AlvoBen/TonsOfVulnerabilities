package com.tssap.dtr.client.lib.protocol.requests;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;

import com.sap.tc.logging.Location;
import com.sap.util.monitor.jarm.ConfMonitor;
import com.sap.util.monitor.jarm.IMonitor;
import com.sap.util.monitor.jarm.TaskMonitor;
import com.tssap.dtr.client.lib.protocol.HTTPException;
import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IConnection;
import com.tssap.dtr.client.lib.protocol.IRequest;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;
import com.tssap.dtr.client.lib.protocol.IResponseParser;
import com.tssap.dtr.client.lib.protocol.ISessionContext;
import com.tssap.dtr.client.lib.protocol.entities.ByteArrayEntity;
import com.tssap.dtr.client.lib.protocol.impl.ResponseFactory;
import com.tssap.dtr.client.lib.protocol.util.LogUtil;
import com.tssap.dtr.client.lib.protocol.util.Query;
import com.tssap.dtr.client.lib.protocol.util.RequestLog;

/**
 * This class implements a generic HTTP request.
 * <p>Methods are provided to define or retrieve the command,
 * the path, headers, an optional query
 * string and the body of the request.</p>
 * <p>Additionally this class defines a simple framework
 * to initiate the processing of the response body (if any) based
 * on the content type of the response. Many DeltaV requests for example
 * use this mechanism to hand over their response bodies to an
 * XML parser.</p>
 * <p>The <code>perform</code>
 * method should be used instead of <code>Connection.send(IRequest)</code>
 * to execute the request, retrieve the response and initiate
 * the processing of the response body in one step. The result
 * of that processing is a response entity
 * that provides the payload of the response with suitable
 * accessor methods.</p>
 */
public class RequestBase implements IRequest {

	/** The protocol command, e.g. "GET", "PROPFIND" */
	private String method;

	/** The path part of the URL */
	private String path;

	/** The (optional) query string */
	private Query query;

	/** The table of request headers */
	private HashMap headers;

	/** The entity of this request that provides the request body */
	private IRequestEntity requestEntity;

	/**
	 * The MIME type of the request entity, e.g. "text/xml". May include
	 * additional parameters like "charset". Corresponds to the
	 * HTTP "Content-Type" header.
	 */
	private String contentType;

	/**
	 * The length of the request body in bytes. Corresponds to the
	 * HTTP "Content-Length" header. If <code>contentLength</code> is -1 the
	 * length of the body is unknown and must be sent in "chunked"
	 * transfer encoding.
	 */
	private long contentLength = -1L;

	/**
	 * The response to this request if the request already
	 * has been executed with the perform method. Otherwise null.
	 */
	private IResponse response;

	/**
	 * The response entity if the request has been executed with
	 * the perform method. If the response has no body <code>responseEntity</code>
	 * is null.
	 */
	private IResponseEntity responseEntity;
	
	/**
	 * The total duration of the request/response cycle
	 */
	private long duration = 0L;
	
	/**
	 * Determines whether the body (if any) of this request should be traced.
	 */	
	private boolean logRequestEntity = true;
	
	/**
	 * Determines whether the body (if any) of the response following this request
	 * should be traced.
	 */	
	private boolean logResponseEntity = true;
	
	/** The trace location */
	private Location location;

	/**
	 * The table of response parsers. The MIME (content) type of
	 * the response is used as key.
	 */
	private HashMap parsers;

	/**
	 * The default parser for the response if no specialized parser is
	 * available for the response MIME type.
	 */
	private IResponseParser defaultParser;

	/** client trace */
	private static Location TRACE = Location.getLocation(RequestBase.class);
	
	private static Location REQUEST_STACK = 
		Location.getLocation("com.tssap.dtr.client.lib.protocol.requests.STACK");	
	private static Location REQUEST_COUNT_LOG =
	    Location.getLocation( "com.tssap.dtr.client.lib.protocol.requests.COUNT" );

	protected static Location REQUEST_LOG =
		Location.getLocation( "com.tssap.dtr.client.lib.protocol.REQUEST" );	    	    
	protected static Location RESPONSE_LOG =
		Location.getLocation( "com.tssap.dtr.client.lib.protocol.RESPONSE" );
			    
	
	// monitoring and request logging stuff
	private static int requestCount;
	private static PrintStream logStream;
	private IMonitor monitor;
	private String monitorID;
	private String taskID;
	private boolean jarmEnabled = ConfMonitor.getMonitorSwitch(); 

	/**
	 * Creates a request with specified protocol method for the root path "/".
	 * @param method the protocol command, e.g. "GET", "CHECKIN" etc.
	 */
	public RequestBase(String method) {
		setMethod(method);
		setPath("/");
	}

	/**
	 * Creates a request with specified protocol method and relative path.
	 * @param method the protocol command, e.g. "GET", "CHECKIN" etc.
	 * @param path the URL of a resource to which the request applies.
	 */
	public RequestBase(String method, String path) {
		setMethod(method);
		setPath(path);
	}

	/**
	 * Creates a request with specified protocol method, relative path and entity.
	 * @param method the protocol command, e.g. "GET", "CHECKIN" etc.
	 * @param path the URL of a resource to which the request applies.
	 * @param entity a request entity providing the request body and some
	 * parameters like content length and type.
	 */
	public RequestBase(String method, String path, IRequestEntity entity) {
		this(method, path);
		setRequestEntity(entity);
	}

	/**
	 * Sets the request object to an initial state ready for reuse.
	 * This method removes all headers and response parsers, releases any
	 * previously retrieved response and response entity and sets the simply
	 * attributes (like method, path etc.) to their appropriate
	 * initial values.
	 */
	public void clear() {
		method = null;
		path = null;
		query = null;
		requestEntity = null;
		contentType = null;
		contentLength = -1L;
		response = null;
		responseEntity = null;
		if (parsers != null) {
			parsers.clear();
		}
		defaultParser = null;
		removeAllHeaders();
	}

	/**
	* Returns the protocol method of this request.
	* Note, since HTTP is an extensible protocol any method identifier could
	* be returned by this method, not only one of the standard HTTP methods
	* like 'GET' or 'PUT'.
	* @return The protocol method, e.g. 'GET', 'POST' or 'PROPFIND'.
	 * @see IRequest#getMethod()
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Sets the protocol method of this request.
	 * Note, since HTTP is an extensible protocol any method identifier
	 * may be specified here. The <code>method</code> parameter is not
	 * limited to standard HTTP methods like "GET" or "PUT" and it is
	 * in the responsibility of the caller to determine whether a remote
	 * host supports a certain command (e.g. with the OPTIONS command).
	 * If a server does not support a command it usually responds with
	 * "405 Method Not Allowed" status code.
	 * @param method the protocol method, e.g. 'GET', 'POST' or 'PROPFIND'.
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	* Returns the path of this request.
	* @return The path relative to either the root ("/")
	* of the remote server or a predefined base path
	* (@see IConnection#getBasePath()).
	* @see IRequest#getPath()
	 */
	public String getPath() {
		return path;
	}

	/**
	* Sets the path of this request.
	* @param path the path of this request relative to either the root ("/") collection
	* or a predefined base path (@see IConnection#getBasePath()).
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * Returns the optional query of this request.
	 * @return the query
	 */
	public Query getQuery() {
		return query;
	}	
	
	/**
	* Sets a query for this request. This method
	* uses the Query.toString() method to calculate 
	* the query string.
	* @param query the query. 
	 */
	public void setQuery(Query query) {
		this.query = query;
	}	

	/**
	* Returns the query string of this request in URL-encoded format.
	* The query string is appended to the URL (after a "?") to provide
	* additional parameters for a request like search parameters or session identifiers.
	* @return The query string, or null if no such string is used.
	 * @see IRequest#getQueryString()
	 */
	public String getQueryString() {
		return (query != null)? query.toString() : null;
	}

	/**
	* Sets a query string for this request. Note, the query string
	* must apply to RFC2626 and must be URL-encoded (@see Encoder#encodeQuery)
	* @param query a query string according to RFC2616.
	 */
	public void setQueryString(String query) {
		this.query = new Query(query);
	}


	/**
	* Returns the names of the headers defined by this request.
	* @return An enumeration of strings representing header names.
	 * @see IRequest#getHeaderNames()
	 */
	public Iterator getHeaderNames() {
		if (headers == null) {
			return null;
		} else {
			return new Iterator() {
				private Iterator iter = headers.keySet().iterator();
				public boolean hasNext() {
					return iter.hasNext();
				}
				public Object next() {
					Header header = (Header) headers.get(iter.next());
					if (header != null) {
						return header.getName();
					}
					return null;
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	/**
	 * Returns the specified request header.
	 * Note, header names are not case-sensitive.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @return The specified header, of null if the header does not exist.
	 * @see IRequest#getHeader(String)
	 */
	public Header getHeader(String name) {
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException("Name of header must not be null or empty.");
		}
		if (headers == null) {
			return null;			
		}
		return (Header) headers.get(name.toLowerCase());
	}

	/**
	 * Returns the value of the specified request header.
	 * Note, header names are not case-sensitive.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @return The value of the specified header, of null if the header does not exist.
	 * @see IRequest#getHeaderValue(String)
	 */
	public String getHeaderValue(String name) {
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException("Name of header must not be null or empty.");
		}
		if (headers == null) {
			return null;			
		}
		Header header = (Header) headers.get(name.toLowerCase());
		return (header != null) ? header.getValue() : null;
	}

	/**
	 * Returns the value of the specified request header, or a
	 * default value if the header is not defined.
	 * Note, header names are not case-sensitive.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @param defaultValue  the value that should be returned if the header is not
	 * defined for this request.
	 * @return The value of the specified header, or <code>defaultValue</code>.
	 * @see IRequest#getHeaderValue(String,String)
	 */
	public String getHeaderValue(String name, String defaultValue) {
		String value = getHeaderValue(name);
		return (value != null) ? value : defaultValue;
	}

	/**
	 * Defines a new or changes the value of an existing header.
	 * @param header  a header from which name and value are taken to define
	 * or change a header of this request.
	 */
	public void setHeader(Header header) {
		String name = header.getName();
		if (headers == null) {
			headers = new HashMap();
		}
		headers.put(name.toLowerCase(), header);
	}

	/**
	 * Defines a new or changes the value of an existing header.
	 * @param header  a header from which name and value are taken to define
	 * or change a header of this request.
	 * @param append if true, the value of the new header is appended to an already defined
	 * header of the same name, instead of replacing the old value. The value is appended
	 * with a comma as separator.
	 */
	public void setHeader(Header header, boolean append) {
		String name = header.getName();
		if (!append || headers == null) {
			setHeader(header);
		} else {			
			Header existingHeader = (Header) headers.get(name.toLowerCase());
			if (existingHeader == null) {
				setHeader(header);
			} else {
				existingHeader.appendPart(header.getValue(), ", ");
			}
		}
	}

	/**
	 * Defines a new or changes the value of an existing header.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @param value the value of this header.
	 * @see IRequest#setHeader(String, String)
	 */
	public void setHeader(String name, String value) {
		setHeader(new Header(name, value));
	}

	/**
	 * Defines a new or changes the value of an existing header.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @param value the value of this header.
	 * @param append if true, the value of the new header is appended to an already defined
	 * header of the same name, instead of replacing the old value. The value is appended
	 * with a comma as separator.
	 * @see IRequest#setHeader(String, String)
	 */
	public void setHeader(String name, String value, boolean append) {
		setHeader(new Header(name, value), append);
	}

	/**
	 * Defines a set of headers from the given array.
	 * @param headerList an array of Header objects.
	 */
	public void setHeaders(Header[] headerList) {
		if (headers == null) {
			headers = new HashMap();
		}
		for (int i = 0; i < headerList.length; ++i) {
			setHeader(headerList[i]);
		}
	}

	/**
	 * Removes the header with the specified name. If the header does not
	 * exist, nothing happens.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @see IRequest#removeHeader(String)
	 */
	public void removeHeader(String name) {
		if (name != null && name.length() > 0) {
			if (headers != null) {
				headers.remove(name.toLowerCase());
			}
		} else {
			throw new IllegalArgumentException("Header name must not be null or empty.");
		}
	}

	/**
	 * Removes all headers from the request.
	 */
	public void removeAllHeaders() {
		if (headers != null) {
			headers.clear();
		}
	}

	/**
	 * Returns the request entity. If no request entity has been set explicitly
	 * the methods call prepareRequestEntity to create an entity.
	 * @return The request entity, or null if the request has no request body.
	 * @see IRequest#getRequestEntity()
	 */
	public IRequestEntity getRequestEntity() {
		if (requestEntity == null) {
			prepareRequestEntity();
		}
		return requestEntity;
	}

	/**
	 * Sets the request entity.
	 * Checks whether the entity defines content
	 * length and content type and creates the corresponding HTTP headers.
	 * @param entity the request entity.
	 */
	public void setRequestEntity(IRequestEntity entity) {
		if (entity != null) {
			requestEntity = entity;

			contentLength = entity.getContentLength();
			if (contentLength > 0) {
				setHeader(Header.HTTP.CONTENT_LENGTH, Long.toString(contentLength));
			}

			contentType = entity.getContentType();
			if (contentType == null) {
				contentType = "application/octet-stream";
			}
			setHeader(Header.HTTP.CONTENT_TYPE, contentType);

			if (entity.getContentMD5() != null) {
				setHeader(Header.HTTP.CONTENT_MD5, entity.getContentMD5());
			}
		}
	}

	/**
	 * Returns the content length of the request body.
	 * @return The content length in bytes.
	 * @see IRequest#getContentLength()
	 */
	public long getContentLength() {
		return contentLength;
	}

	/**
	 * Returns the content (MIME) type of the request body.
	 * @return A MIME content type specifier.
	 * @see IRequest#getContentType()
	 */
	public String getContentType() {
		return contentType;
	}

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
	public IResponse perform(IConnection connection, boolean sendCloseConnection)
	throws IOException, HTTPException 
	{
		if (connection == null) {
			IOException ex = new IOException("invalid connection");
			TRACE.throwing("perform(IConnection,boolean)", ex);
			throw ex;
		}

		if (TRACE.beInfo()) {
			TRACE.infoT("perform(IConnection,boolean)", "");			
			TRACE.infoT("perform(IConnection,boolean)", "<<<BEGIN OF REQUEST>>>");
			TRACE.infoT(
				"perform(IConnection,boolean)", 
				"{0} {1}", 
				new Object[]{getMethod(), getPath()}
			);
		}		
		if (REQUEST_STACK.beDebug()) {
			LogUtil.debugLogException(REQUEST_STACK, new RequestLog());
		}
			
		++requestCount;
		long startTime = System.currentTimeMillis();
		
		if (jarmEnabled) {			
			String user = getUser(connection);									
			taskID = "NW:DI:HTTP:PERFORM";
			monitorID = "NW:DI:HTTP:" + method.toUpperCase();
			 			
			monitor = TaskMonitor.getRequestMonitor(user, taskID);
			monitor.startComponent(monitorID);			
		}
					
		try {
			response = connection.send(this, sendCloseConnection);
			if (logStream != null) {
				logResponse(logStream, connection);
			}
						
			String responseType = response.getContentType();
			IResponseParser parser = null;
			if (parsers != null && responseType != null) {
				parser = getResponseParser(responseType);
			}
			if (parser == null && defaultParser != null) {
				parser = defaultParser;
			}
	
			try {
				if (parser != null) {
					responseEntity = parser.parse(connection.getAbsolutePath(path), response);
				} else {
					responseEntity = new ByteArrayEntity(response);
				}

				if (responseEntity != null) {
					response.setEntity(responseEntity);					
					response = ResponseFactory.createResponse(responseEntity.getEntityType(), response);
				}
				response.releaseStream();												
			} 
			catch (InterruptedIOException e) {
				LogUtil.debugLogException(TRACE, e);
				sendCloseConnection = true;
				responseEntity = null;
				TRACE.infoT("perform(IConnection,boolean)", 
					"Parsing of the response entity has been interrupted.");
			}			
			finally 
			{				
				duration = System.currentTimeMillis() - startTime;
				if (jarmEnabled) {
					monitor.endComponent(monitorID);
					monitor.setDescription(
						getRequestName() + "  "
						+ connection.getUrl() + connection.getAbsolutePath(path)
						+ " : " + response.getStatusLine()
						+ " [req #" + requestCount + "]"
						+ "[" + ((response.getDuration()<10)? "<10" : Long.toString(response.getDuration())) + "ms]"						
						+ "[" + Thread.currentThread().getName() + "]");
					monitor.endRequest(taskID);  
				}				
				if (TRACE.beInfo()) {
					TRACE.infoT(
						"perform(IConnection,boolean)",
						"{0} {1} [completed in {2}ms]",
						new Object[]{
							Integer.toString(response.getStatus()),
							response.getStatusDescription(),
							(( (duration<10)? "<10" : Long.toString(duration)))
						}
					);
				}				
			}			
		} catch (IOException e) {
			sendCloseConnection = true;
			LogUtil.logException(TRACE, e);
			throw e;
		} catch (HTTPException e) {
			sendCloseConnection = true;
			LogUtil.logException(TRACE, e);
			throw e;			
		} finally {
			if (logStream != null) {
				logRequest(logStream, connection);
			}			
			TRACE.infoT("perform(IConnection,boolean)", "<<<END OF REQUEST>>>");			
			TRACE.infoT("perform(IConnection,boolean)", "");							
		
			// if caller indicated that connection should be closed after request-
			// response cycle is complete, or an exception occured during read out
			// of the stream, we now close the connection
			if (sendCloseConnection) {
				connection.close();
			}
		}
		return response;
	}

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
	public IResponse perform(IConnection connection) throws IOException, HTTPException {
		return perform(connection, false);
	}

	/**
	 * Returns the response object.
	 * @return An implementation of IResponse that provides access to the
	 * status information, headers and the entity of the response.
	 */
	public IResponse getResponse() {
		return response;
	}

	/**
	 * Returns the response entity. If this method returns null after
	 * <code>perform</code> has been executed this indicates that the response has
	 * no body.
	 * @return The response entity, or null.
	 */
	public IResponseEntity getResponseEntity() {
		return responseEntity;
	}

	/**
	 * Checks whether the body (if any) of this request should be traced
	 * to the common or any given specific location.
	 */
	public boolean logRequestEntity() {
		return logRequestEntity;	
	}
	
	/**
	 * Checks whether the body (if any) of the response following this request
	 * should be traced to the common or any given specific location.
	 */
	public boolean logResponseEntity() {
		return logResponseEntity;	
	}

	/**
	 * Enables or disables the logging of request and response entities for
	 * this request.
	 * @param enable   if true, logging for entities is enabled, otherwise disabled
	 */
	public void enableLogging(boolean enable) {
		this.logRequestEntity = enable;		
		this.logResponseEntity = enable;
	}

	/**
	 * Enables or disables the logging of the request entity for
	 * this request.
	 * @param enable   if true, logging is enabled, otherwise disabled
	 */
	public void enableRequestEntityLog(boolean enable) {
		this.logRequestEntity = enable;
	} 

	/**
	 * Enables or disables the logging of the response entity for
	 * this request.
	 * @param enable   if true, logging is enabled, otherwise disabled
	 */
	public void enableResponseEntityLog(boolean enable) {
		this.logResponseEntity = enable;
	} 
	
	/**
	 * Returns the trace location for logging of request and response entities.
	 * @return a trace location, or null. In the latter case, traces should be
	 * written to the common trace location.
	 */
	public Location getLocation() {
		return location;	
	}

	/**
	 * Sets the trace location for logging of request and response entities.
	 * @param location   a valid trace location.
	 */
	public void setLocation(Location location) {
		this.location = location;	
	}

	
	

	/**
	 * Returns a response parser for the specified content (MIME) type.
	 * Note, parameters like "charset" that may be present in
	 * <code>contentType</code> are ignored.
	 * @param contentType a valid MIME content type specifier.
	 * @return A response parser suitable for the given content type,
	 * or null.
	 */
	public IResponseParser getResponseParser(String contentType) {
		if (parsers == null)
			return null;
		return (IResponseParser) parsers.get(getParserKey(contentType));
	}

	/**
	 * Sets a response parser for the specified content (MIME) type.
	 * Note, parameters like "charset" that may be present in
	 *  <code>contentType</code> are ignored.
	 * @param contentType a valid MIME content type specifier.
	 * @param parser a response parser suitable for the given content type.
	 */
	public void setResponseParser(String contentType, IResponseParser parser) {
		if (parsers == null) {
			parsers = new HashMap();
		}
		parsers.put(getParserKey(contentType), parser);
	}

	/**
	 * Removes the response parser for the specified content (MIME) type.
	 * If no such parser exists, nothing happend.
	 * @param contentType a valid MIME content type specifier.
	 */
	public void removeResponseParser(String contentType) {
		if (parsers != null) {
			parsers.remove(getParserKey(contentType));
		}
	}

	/**
	 * Returns the default response parser.
	 * @return A response parser suitable for content (MIME) types that
	 * need no special treatment, or null.
	 */
	public IResponseParser getDefaultParser() {
		return defaultParser;
	}

	/**
	 * Sets the default response parser.
	 * @param parser a response parser suitable for content (MIME) types that
	 * need no special treatment.
	 */
	public void setDefaultParser(IResponseParser parser) {
		defaultParser = parser;
	}

	/**
	 * Prepares the request entity before the request is performed.
	 * <p>This method is called by @see Connection#send(IRequest) and should be overwritten
	 * by derived classes that define request bodies.
	 * For example many DeltaV requests (e.g. @see PropfindRequest#prepareRequestEntity())
	 * overwrite this method to create their XML bodies. </p>
	 * <p>The implementation here always returns
	 * null to indicate that no request entity has been prepared.</p>
	 * @return A request entity, e.g. a StringEntity or FileEntity, or null.
	 */
	protected IRequestEntity prepareRequestEntity() {
		return null;
	}

	/**
	 * Helper function to extract the MIME type from "Content-Type" headers.
	 * @param type the ContentType header
	 * @return ContentType without trailing paramaters.
	 */
	private String getParserKey(String type) {
		int n = type.indexOf(';');
		String s = (n > 0) ? type.substring(0, n).trim() : type;
		return s.toLowerCase();
	}


	/**
	 * Sets an optional PrintStream for logging of request and response details.
	 * @param logger the PrintStream to use, or <code>null</code> to disable.
	 */
	public static void setLogStream(PrintStream log) {
		logStream = log;
	}		
	
	/**
	 * 
	 * @param log
	 * @param conn
	 */
	protected void logRequest(PrintStream log, IConnection conn) 
	{
		String requestName = getRequestName();
				
		if (REQUEST_LOG.beDebug()) {
			String url = conn.getProtocol().toString() + "://" + conn.getHost() + ":" + conn.getPort() + path;
			String status = response != null ?  " : "  + String.valueOf(response.getStatus()) : "";
			logStream.println("HTTP Request#" + requestCount
				+ " [" + Thread.currentThread().getName() + "]:  "
				+ requestName + "  "
				+ url
				+ "  (" + ((duration<10)? "<10" : Long.toString(duration)) + "ms) : "
				+ status);

		} else if (REQUEST_COUNT_LOG.beDebug()) {
			String url = conn.getProtocol().toString() + "://" + conn.getHost() + ":" + conn.getPort() + path;
			String status = response != null ?  " : "  + String.valueOf(response.getStatus()) : "";
			logStream.println("HTTP Request#" + requestCount
				+ " [" + Thread.currentThread().getName() + "]:  "
				+ requestName + "  " + url
				+ "  (" + ((duration<10)? "<10" : Long.toString(duration)) + "ms)"
				+ status);
		}			
	}
	
	/**
	 * 
	 * @param log
	 * @param conn
	 */
	protected void logResponse(PrintStream log, IConnection conn) {					
		if (RESPONSE_LOG.beDebug()) {
			String traceHeader = response.getHeaderValue("DTRPerformanceStatistics");
			if (traceHeader != null  &&  traceHeader.length()>0) {
				logStream.println("HTTP Request#" + (requestCount-1)
								  + " [" + Thread.currentThread().getName() + "]:  " + traceHeader);						
			}
		}				
	}	
	
	private String getUser(IConnection conn) {
		String user = null;
		ISessionContext ctx = conn.getSessionContext();
		if (ctx != null) {
			user = ctx.getUser();
		}
		return user;
	}
	
	private String getRequestName() {
		String requestName = getClass().getName();
		int lastDot = requestName.lastIndexOf('.');
		if (lastDot > 0) {
			int penultimateDot = requestName.lastIndexOf('.', lastDot - 1);
			if (penultimateDot > 0) {
				requestName = requestName.substring(penultimateDot + 1);
			}
		}
		return requestName;
	}

}
