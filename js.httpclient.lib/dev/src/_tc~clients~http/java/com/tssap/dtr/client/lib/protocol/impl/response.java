package com.tssap.dtr.client.lib.protocol.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.HTTPException;
import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IRequest;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;
import com.tssap.dtr.client.lib.protocol.IResponseStream;
import com.tssap.dtr.client.lib.protocol.Status;
import com.tssap.dtr.client.lib.protocol.entities.MultiStatusEntity;
import com.tssap.dtr.client.lib.protocol.streams.ResponseStream;
import com.tssap.dtr.client.lib.protocol.util.Tokenizer;

/**
 * This class implements a generic HTTP response.
 * <p>Methods are provided to retrieve the status code,
 * status description, headers, content type and content length
 * and the entity of a response.</p>
 * <p>The response entity exposes the payload
 * of the response by specific accessor methods.
 * The creation of the response entity is triggered by
 * the <code>perform</code> method of class @see RequestBase
 * (or any specialized request derived from that class), and the type
 * of entity that actually is instantiated depends
 * on the MIME type of the request body and maybe on the
 * status code of the response.</p>
 * <p>The method <code>getEntityType</code>
 * retrieves the type of the response entity (e.g. "MultiStatusEntity"
 * or "StringEntity"), whereas <code>getEntity</code> returns
 * the entity itself.</p>
 */
public class Response implements IResponse {

	/** the HTTP version reported by the host, either "HTTP/1.0" or "HTTP/1.1" */
	private String version;
	/** true if version is "HTTP/1.0" */
	private boolean usingHTTP10 = false;

	/** the status code, e.g. '200' */
	private int status = -1;
	/** the status description, e.g. 'OK' */
	private String description;
	/** the complete status line, e.g. HTTP/1.1 200 OK */
	private String statusLine;

	/** table of response headers */
	private HashMap headers;

	/** the MIME type of the response body corresponding
	 * to the "Content-Length" header */
	private String contentType;
	
	/** the length of the response body in bytes, or -1 if unknown */
	private long contentLength = -1L;
	/** true if the remote host used chunking to send the body */
	private boolean contentChunked = false;
	/** stream to access the response body */
	private ResponseStream content;

	/** the message digest of the response body */
	private boolean digestEnabled = false;
	private String digestAlgorithm = null;
	private byte[] digest;		

	/** the request that corresponds to this response */
	private IRequest request;
	
	/** true, if the communication runs over a secure channel */
	private boolean secureProtocol;

	/** the response entity, or null if none */
	private IResponseEntity entity;

	/** the duration of the request-response cycle in milliseconds */
	private long duration;
	
	/** indicates whether the connection is expected to be closed after this response by the server */
	private boolean closingConnection;
	

	// Constants for readability
	private static final boolean APPEND_TO_EXISTING_HEADER = true;
	private static final boolean SKIP_WHITESPACE = true;
	private static final boolean REPORT_WHITESPACE = false;
	private static final boolean SKIP_EMPTY_LINES = true;
	private static final boolean REPORT_EMPTY_LINES = false;
	private static final String END_OF_LINE = "\r\n";

	/** client trace */
	private static Location TRACE = Location.getLocation(Response.class);

	/**
	 * Creates a response object with reference to the specified request.
	 * @param request the request to which this response belongs.
	 */
	public Response(IRequest request) {
		this.request = request;
	}

	/**
	 * Creates a response object with reference to the specified request.
	 * @param request the request to which this response belongs.
	 * @param secureProtocol  if true indicates that communication
	 * runs over a secure channel and response bodies should not be
	 * wire traced.
	 */
	public Response(IRequest request, boolean secureProtocol) {
		this.request = request;
		this.secureProtocol = secureProtocol;
	}

	/**
	 * Checks whether this response is valid.
	 * @return True, if at least a status code and the HTTP version is defined,
	 * and the status code is between 200 (OK) and below 600.
	 * @see IResponse#isValid()
	 */
	public boolean isValid() {
		return (status >= Status.OK && status < 600 && version != null);
	}

	/**
	 * Returns the status code of this response.
	 * @return A status code according to RFC2616 (e.g. '200')
	 * @see IResponse#getStatus()
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Returns the status description of this response.
	 * @return A status description according to RFC2616 (e.g. 'OK')
	 * @see IResponse#getStatusDescription()
	 */
	public String getStatusDescription() {
		return description;
	}
	
	/**
	 * Retursn the complete status line of the response.
	 * @return the status line of the response, e.g. "HTTP/1.1 200 OK"
	 */
	public String getStatusLine() {
		return statusLine;
	}

	/**
	 * Returns the time in milliseconds the last request-response cycle lasted.
	 * @return The round-trip time in milliseconds.
	 * @see IResponse#getDuration()
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Returns the specified response header.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @return The specified header, of null if the header does not exist.
	 * @see IResponse#getHeader(String)
	 */
	public Header getHeader(String name) {
		if (headers == null || name == null || name.length() == 0)
			return null;
		return (Header) headers.get(name.toLowerCase());
	}

	/**
	 * Returns the value of the specified response header.
	 * Note, header names are not case-sensitive.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @return The value of the specified header, of null if the header does not exist.
	 * @see IRequest#getHeaderValue(String)
	 */
	public String getHeaderValue(String name) {
		if (headers == null || name == null || name.length() == 0)
			return null;
		Header header = (Header) headers.get(name.toLowerCase());
		return (header != null) ? header.getValue() : null;
	}

	/**
	 * Returns the value of the specified response header, or a
	 * default value if the header is not defined.
	 * Note, header names are not case-sensitive.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @param defaultValue - the value that should be returned if the header is not
	 * defined for this response.
	 * @return The value of the specified header, or <code>defaultValue</code>.
	 * @see IRequest#getHeaderValue(String,String)
	 */
	public String getHeaderValue(String name, String defaultValue) {
		String value = getHeaderValue(name);
		return (value != null) ? value : defaultValue;
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
	 * Returns the HTTP version of this response.
	 * @return A string in the format "HTTP/X", where X is either "1.0" or "1.1"
	 * @see IResponse#getHTTPVersion()
	 */
	public String getHTTPVersion() {
		return version;
	}

	/**
	 * Checks whether this response uses HTTP version 1.0
	 * @return True if the response line included the version string "HTTP/1.0".
	 * @see IResponse#getUsingHTTP10()
	 */
	public boolean getUsingHTTP10() {
		return usingHTTP10;
	}

	/**
	 * Returns the content type of the response body.
	 * @return The value of the "Content-Type" header of this response.
	 * @see IResponse#getContentType()
	 */
	public String getContentType() {
		return contentType;
	}
	
	/**
	 * Returns the MIME type of the response body.
	 * @return The MIME type of the response body, or null if the response
	 * has no body.
	 * @see IResponse#getMType()
	 */
	public String getMimeType() {
		return (entity != null)? entity.getMimeType() : null;
	}	

	/**
	 * Returns the content length of the response body.
	 * @return The value of the "Content-Length" header of this response,
	 * or -1 if the content length is undefined (usually because the
	 * server used "chunked" transfer encoding, see RFC2616).
	 * @return The length of the response body in bytes.
	 * @see IResponse#getContentLength()
	 */
	public long getContentLength() {
		return contentLength;
	}
	
	/**
	 * Returns the message digest of the response body.
	 * In order to compare the result of this method with for eaxmple a "Content-MD5"
	 * response header it may be necessary to convert it to a "hexadecimal-encoded" string
	 * first (compare @see Encoder.toHexString(byte[])).
	 * Note, the digest is reset when the digest recording is switched on or off
	 * for the underlying input stream.
	 * @return The digest of the response body.
	 * @see IResponse#getContentDigest()
	 */		
	public byte[] getDigest() {
		return digest;
	}

	/**
	 * Returns the content stream.
	 * After the response has sucessfully been
	 * initialized the stream "points" to the first byte of the response body.
	 * Note, the stream returned by this method is a shared resource and managed
	 * by a @see Connection object. A client <b>must not</b> store permanent references to
	 * this stream and properly release it after the content has been
	 * read out (see Response#releaseStream).
	 * @return Reference to the input stream of this response, or null if there
	 * was no response body or the stream already has been released.
	 * @see IResponse#getStream()
	 */
	public IResponseStream getContent() {
		return (IResponseStream) content;
	}

	/**
	 * Returns the content stream.
	 * After the response has sucessfully been
	 * initialized the stream "points" to the first byte of the response body.
	 * Note, the stream returned by this method is a shared resource and managed
	 * by a @see Connection object. A client <b>must not</b> store permanent references to
	 * this stream and properly release it after the content has been
	 * read out (see Response#releaseStream).
	 * @return Reference to the input stream of this response, or null if there
	 * was no response body or the stream already has been released.
	 * @see IResponse#getStream()
	 */
	public InputStream getStream() {
		return (InputStream) content;
	}

	/**
	 * Checks whether this response has content.
	 * @return True, if the response contains a "Content-Length" greater zero,
	 * a "Transfer-Encoding" header, or the length of
	 * the content is defined implicitly (e.g. by closing the connection)
	 * @return True, if this response has a body.
	 * @see IResponse#hasContent()
	 */
	public boolean hasContent() {
		return (content != null  && (contentLength != 0 || contentChunked));
	}

	/**
	 * Checks whether the content is an XML document.
	 * @return True, if the "Content-Type" header of the response entity is either
	 * "text/xml" or "application/xml".
	 * @see IResponse#isContentXML()
	 */
	public boolean isContentXML() {
		return (
			hasContent()
			&& contentType != null
			&& (contentType.startsWith("text/xml") || contentType.startsWith("application/xml")));
	}

	/**
	 * Returns a reference to the request this response belongs to.
	 * @return Reference to the corresponding request object.
	 * @see IResponse#getRequest()
	 */
	public IRequest getRequest() {
		return request;
	}

	/**
	 * Returns the entity of this response.
	 * @return The response entity, or null if this response has no body.
	 * @see IResponse#getEntity()
	 */
	public IResponseEntity getEntity() {
		return entity;
	}

	/**
	 * Sets the response entity.
	 * @param entity the response entity.
	 */
	public void setEntity(IResponseEntity entity) {
		this.entity = entity;
	}

	/**
	 * Returns the type of the response entity.
	 * @return A string specifying the type of the response entity, or null if this
	 * response has no entity.
	 * @see IResponse#getEntityType()
	 */
	public String getEntityType() {
		return (entity != null) ? entity.getEntityType() : null;
	}

	/**
	 * Checks whether the content is a Multistatus response entity.
	 * @return True, if the getEntityType() returns "Multistatus" and the
	 * response status is 207 MULTISTATUS.
	 */
	public boolean isMultiStatus() {
		return (status == Status.MULTI_STATUS) &&  MultiStatusEntity.isMultiStatusEntity(entity);
	}

	/**
	 * Skips the body of this response.
	 * @throws IOException - if an i/o error occurs.
	 * @see IResponse#skipContent()
	 */
	public void skipContent() throws IOException {
		if (content != null) {
			content.skipContent();
		}
	}

	/**
	 * Releases the response stream for reuse by other requests.
	 * This method should be called when the read out of the stream
	 * is complete.
	 * The method skips any remaining content.
	 * If the response included trailing headers they are added
	 * to the response's header collection.
	 * Note, a server does not send trailers unless the request explicitly
	 * included a TE header field indicating that "trailers" are acceptable
	 * in the response (see RFC2616).
	 * A client <b>must not</b> use or reference the content stream
	 * of a Response object anymore after this method has been called.
	 * The methods <code>getStream</code> and <code>getContent</code>
	 * always will return null after the stream has been released.
	 * @see IResponse#releaseStream()
	 */
	public void releaseStream() throws IOException {
		if (content != null) {
			content.release();
			readTrailers();
			content = null;
		}
	}
	
	/**
	 * Returns whether the connection should expect that the server
	 * closes the connection after sending this response.
	 * @return true if either the response containes a "Connection: close"
	 * header or the content length is determined implicitly by the server
	 * closing the connection.
	 */
	public boolean closingConnection() {
		return closingConnection;
	}
	

	/**
	 * Enables the calculation of a message digest for the body of
	 * this response (if any). Must be called before <code>initialize()</code>.
	 * @param algorithm  the identification of a hash algorithm, e.g.
	 * "MD5" or "SHA". Note, most platforms at least provide an
	 * implementation of the "MD5" algorithm.
	 */	
	public void enableDigest(String algorithm) {
		this.digestEnabled = true;
		this.digestAlgorithm = algorithm;
	}

	/**
	 * Initializes this response object from the source stream.
	 * @param source the input stream from which to read the response.
	 * @throws IOException - if an I/O error occurs
	 * @throws HTTPException - if the response is malformed, invalid or incomplete.
	 */
	public void initialize(ResponseStream source) throws IOException, HTTPException 
	{
		// Assert that we have a source stream
		if (source == null) {
			IOException ex = new IOException("invalid response stream");
			TRACE.throwing("initialize(ResponseStream)", ex);
			throw ex;				
		}
		TRACE.debugT("initialize(ResponseStream)", " ");

		// Read and parse the status line
		String line = null;
		do {
			line = source.readLine(SKIP_EMPTY_LINES, SKIP_WHITESPACE);
			
			// If unexpected EOF reached or first non-empty line
			// does not start with "HTTP/", response is invalid
			if (line.length() == 0) {
				throw new SocketException("Socket output stream shutdown by peer.");							
			} else if (!line.startsWith("HTTP/")) {
				throw new HTTPException("Invalid begin of response [expected: 'HTTP', found: '" + line + "']");
			}
			if (TRACE.beDebug()) {
				TRACE.debugT("initialize(ResponseStream)", line);
			}
			statusLine = line;

			// Parse the status line (version, status, description)
			Tokenizer tokenizer = new Tokenizer(line);
			version = tokenizer.nextToken();
			if (version.equals("HTTP/1.1")) {
				usingHTTP10 = false;
			} else if (version.equals("HTTP/1.0")) {
				usingHTTP10 = true;
				closingConnection = true;
			} else {
				throw new HTTPException("Invalid or unsupported HTTP version [version=" + version + "]");
			}
			try {
				status = Integer.parseInt(tokenizer.nextToken());
			} catch (NumberFormatException e) {
				TRACE.catching("initialize(ResponseStream)", e);
				throw new HTTPException("Invalid HTTP status code [not a number]");
			}
			description = tokenizer.lastToken();
			
			// Read headers until either EOF, an empty line or an
			// IOException occurs. According to RFC2616 multiple headers with
			// the same name are combined into a comma-separated list, and lines starting
			// with whitespace are appended to the previous header.
			Header currentHeader = null;
			line = source.readLine(REPORT_EMPTY_LINES, REPORT_WHITESPACE);
			while (line != null && !line.equals(END_OF_LINE)) {
				if (Character.isWhitespace(line.charAt(0)) && currentHeader != null) {
					currentHeader.appendPart(line.trim(), " ");
				} else {
					currentHeader = new Header(line);
					setHeader(currentHeader, APPEND_TO_EXISTING_HEADER);
				}
				TRACE.debugT("initialize(ResponseStream)", line);
				line = source.readLine(REPORT_EMPTY_LINES, REPORT_WHITESPACE);
			}
			TRACE.debugT("initialize(ResponseStream)", " ");

			// Repeat if the response was 100 (Continue) or 102 (Processing).
			// The former indicates that the server accepted a legnthy request part by part and acknowlegded
			// the receipt. These responses can safely be ignored, since at this point we have completed the
			// request. The latter indicates that the server still is performing the request operations and
			// we have to way a little bit This avoids unnecessary timeouts.
		}
		while (status == Status.CONTINUE || status == Status.PROCESSING);
		
		// check whether server tries to switching protocol and return
		// to connection for upgrade of protocol in this case
		if (status == Status.SWITCHING_PROTOCOLS) {
			return;
		}		
	
		// According to RFC2616 contentLength must be ignored if chunked encoding is used.
		// We simply remove the Content-Length header in this case.
		String transferEncodingHeader = getHeaderValue(Header.HTTP.TRANSFER_ENCODING);
		contentChunked = (transferEncodingHeader != null && transferEncodingHeader.indexOf("chunked") >= 0);
		if (contentChunked) {
			removeHeader(Header.HTTP.CONTENT_LENGTH);
		}

		// Read and interpret the Content-Length and Content-Type headers
		String length = getHeaderValue(Header.HTTP.CONTENT_LENGTH);
		try {
			if (length != null) {
				contentLength = Long.parseLong(length);
			} else {
				contentLength = -1L;
			}
		} catch (NumberFormatException e) {
			TRACE.catching("initialize(ResponseStream)", e);			
			throw new HTTPException("Malformed Content-Length header [not a number]");
		}
		contentType = getHeaderValue(Header.HTTP.CONTENT_TYPE);
		
		// Determine whether we have a "Connection: close" header
		String connectionHeader = getHeaderValue(Header.HTTP.CONNECTION);
		if (connectionHeader != null) {
			connectionHeader = connectionHeader.toLowerCase();
			if (usingHTTP10) {
				closingConnection = connectionHeader.indexOf("keep-alive") < 0;	
			} else {
				closingConnection = connectionHeader.indexOf("close") >= 0;
			}			
		}		

		// Although "HEAD" request may report "Content-Length" header, it actually 
		// should never have a body!
		// The same holds for 204 (No Content) and 304 (Not Modified) responses.
		// In order to ensure consistency of the input stream it is released immediatelly,
		// thus skipping any pending content.
		if (status == Status.NO_CONTENT || 
			status == Status.NOT_MODIFIED ||
			"HEAD".equalsIgnoreCase(request.getMethod())) 
		{
			content = source;
			releaseStream();
			return;
		} 
		// If we have no Content-Length header we have to apply several checks to
		// determine if we have a body and probably how long this body is.		
		else if (contentLength == -1L) 
		{						
			if (contentType == null) {
				// The content type is unknown too. Check if response defines a transfer encoding.
				if (contentChunked) {
					// We have a "Transfere-Encoding: chunked" header. Thus let's assume we have a body.
					// Then it is safe to define a default content type.
					// If the response status is 207 MULTISTATUS the content type is assumed
					// to be 'test/xml'. Otherwise we treat the body as binary data.
					contentType = (status == Status.MULTI_STATUS) ? "text/xml" : "application/octet-stream";
					TRACE.infoT(
						"initialize(ResponseStream)",
						"missing content type [assuming {0}]",
						new Object[]{contentType}
					);
					setHeader(Header.HTTP.CONTENT_TYPE, contentType);
				} else {
					// We have neither content length nor chunking nor content type.
					// Either the response actually has no content or the server must
					// indicate the end of the body by closing the connection.
				}
			} else {
				// We have a content type! If the content is chunked everything is fine. 
				// Otherwise: HTTP allows to indicate the end of the body by closing the
				// connection. However, if either
				// 1) the response uses HTTP/1.0 and a "Connection: Keep-Alive" header is present, or
				// 2) the response uses HTTP/1.1 but no "Connection: Close" header is present
				// the response is invalid. However, we handle this gracefully and only
				// log a warning and mark the connection to be closed before next use.
				if (usingHTTP10) {
					TRACE.infoT(
						"initialize(ResponseStream)",
						"missing content length for HTTP/1.0 Keep-Alive connection",
						new Object[]{contentType}
					);		
					closingConnection = true;				
				} 
				else if (!contentChunked) {
					if (!closingConnection) {
						TRACE.infoT(
							"initialize(ResponseStream)",
							"missing content length for non-chunking HTTP/1.1 persistent connection",
							new Object[]{contentType}
						);	
						closingConnection = true;					
					}
				}
			}
		}

		// Prepare the stream for content reading. Apply a read limit and switch
		// to chunked mode if necessary. If contentLength was explicitly given 
		// as zero in the response, there is no body.
		if (contentLength != 0) {
			content = source;

			if (contentChunked) {		
				content.enableChunking(contentChunked);
			} else if (contentLength > 0) {
				content.enableLimit(contentLength);
			}

			String contentEncoding = getHeaderValue(Header.HTTP.CONTENT_ENCODING);
			if (contentEncoding != null) {
				content.setCompressionAlgorithm(contentEncoding.toLowerCase());
				content.enableCompression();
			}

			if (digestEnabled) {
				content.setDigestAlgorithm(digestAlgorithm);
				content.enableDigest();
			}

			if (request.logResponseEntity()  &&  !secureProtocol) {
				content.enableWireTrace(request.getLocation());
			}
		}
	}

	/**
	 * Sets the time in milliseconds the last request-response cycle lasted.
	 * @param duration  the round-trip time in milliseconds.
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

	/**
	 * Defines a new or changes the value of an existing header.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @param value the value of this header.
	 */
	private void setHeader(String name, String value) {
		if (name != null && name.length() > 0) {
			if (headers == null) {
				headers = new HashMap();
			}
			headers.put(name.toLowerCase(), new Header(name, value));
		}
	}

	/**
	 * Defines a new or changes the value of an existing header.
	 * @param header another header from which the name and the value
	 * are copied.
	 */
	private void setHeader(Header header) {
		String name = header.getName();
		if (name != null && name.length() > 0) {
			if (headers == null) {
				headers = new HashMap();
			}
			headers.put(name.toLowerCase(), header);
		}
	}

	/**
	 * Defines a new or changes the value of an existing header.
	 * @param header another header from which the name and the value
	 * @param append if true, the value of the new header is appended to an already defined
	 * header of the same name, instead of replacing the old value.
	 */
	private void setHeader(Header header, boolean append) {
		String name = header.getName();
		if (name != null && name.length() > 0) {
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
	}

	/**
	 * Removes the header with the specified name. If the header does not
	 * exist, nothing happens.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 */
	private void removeHeader(String name) {
		if (name != null && name.length() > 0) {
			if (headers != null) {
				headers.remove(name.toLowerCase());
			}
		}
	}

	/**
	 * Read trailing entity headers if content was chunked.
	 * Otherwise the method does nothing.
	 */
	private void readTrailers() throws IOException {
		if (contentChunked) {					
			String line = content.readLine(REPORT_EMPTY_LINES, SKIP_WHITESPACE);									
			if (line != null) {
				boolean skipTrailers = false;
				boolean refreshEntity = false;			
				  
				String header = request.getHeaderValue(Header.HTTP.TE);
				if (header == null  ||  header.indexOf("trailers") < 0) {
					skipTrailers = true;
					TRACE.debugT("readTrailers()", "Ignoring unexpected trailers");
				}				
							
				header = request.getHeaderValue(Header.HTTP.TRAILER);
				while (line != null  &&  !line.equals(END_OF_LINE)) {
					if (!skipTrailers) {
						Header trailer = new Header(line);
						String name = trailer.getName();
						if (!Header.HTTP.CONTENT_LENGTH.equalsIgnoreCase(name)  &&
							!Header.HTTP.TRAILER.equalsIgnoreCase(name)  &&
							!Header.HTTP.TRANSFER_ENCODING.equalsIgnoreCase(name)) 
						{					
							if (header.indexOf(name) < 0) {
								TRACE.infoT("readTrailers()", "Accepting unexpected trailer: {0}",
									new Object[]{name});	
							}							
							setHeader(trailer, true);
							refreshEntity = true;
						}
					}					
					line = content.readLine(REPORT_EMPTY_LINES, SKIP_WHITESPACE);
				}
				
				if (refreshEntity && entity != null) {
					entity.initialize(this);
				}
			}
		}
	}


}
