package com.tssap.dtr.client.lib.protocol.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.HTTPException;
import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;
import com.tssap.dtr.client.lib.protocol.IResponseStream;
import com.tssap.dtr.client.lib.protocol.streams.PartitionInputStream;

/**
 * This class implements a generic multipart MIME response part.
 * Methods are provided to retrieve the headers and the body of the part. 
 */
public class ResponsePart implements IResponsePart {

	private String contentID;
	private String contentType;
	private long contentLength = -1L;
	private HashMap headers;
	
	private byte[] boundary;
	private String boundaryStr;
	private PartitionInputStream content;
	
	/** the response entity, or null if none */
	private IResponseEntity entity;	
	
	// Constants for readability
	private static final boolean APPEND_TO_EXISTING_HEADER = true;
	private static final boolean SKIP_WHITESPACE = true;
	private static final boolean REPORT_WHITESPACE = false;
	private static final boolean SKIP_EMPTY_LINES = true;
	private static final boolean REPORT_EMPTY_LINES = false;
	private static final String END_OF_LINE = "\r\n";
	private static final boolean FINAL_BOUNDARY_FOUND = false;

	/** client trace */
	private static Location TRACE = Location.getLocation(ResponsePart.class);
	
		
	/**
	 * Creates a ResponsePart with the given boundary delimiter.
	 * @param boundary  the boundary delimiter that separates
	 * consecutive parts.
	 */
	public ResponsePart(byte[] boundary) {
		this.boundary = boundary;
		boundaryStr = new String(boundary);	
	}

	/**
	 * Returns the content of the response part.
	 * After the part has sucessfully been
	 * initialized the stream "points" to the first byte of the part's body.
	 * Note, the stream returned by this method is a shared resource and managed
	 * by a @see Connection object. A client <b>must not</b> store permanent references to
	 * this stream and properly release it after the content has been
	 * read out.
	 * @return Reference to the input stream of this part's body, or null if the
	 * stream already has been released.
	 */
	public IResponseStream getContent() {
		return content;
	}

	/**
	 * Returns the content ID of the part.
	 * @return a content ID
	 */
	public String getContentID() {
		return contentID;
	}

	/**
	* Returns the content length of the part.
	* @return The content length in bytes.
	 */
	public long getContentLength() {
		return contentLength;
	}

	/**
	* Returns the content (MIME) type of the part.
	* @return A MIME content type specifier.
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Returns the specified header.
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
	 * Returns the value of the specified header.
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
	 * Returns the value of the specified header, or a
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
	 * Returns the names of the headers defined by this part.
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
	 * Returns the content of the response part.
	 * After the part has sucessfully been
	 * initialized the stream "points" to the first byte of the part's body.
	 * Note, the stream returned by this method is a shared resource and managed
	 * by a @see Connection object. A client <b>must not</b> store permanent references to
	 * this stream and properly release it after the content has been
	 * read out.
	 * @return Reference to the input stream of this part's body, or null if the
	 * stream already has been released.
	 */
	public InputStream getStream() {
		return content;
	}

	/**
	 * Releases the response stream for reuse by other requests.
	 * This method should be called when the read out of the stream
	 * is complete. The method skips any remaining content.
	 * A client <b>must not</b> use or reference the content stream
	 * after this method has been called.
	 * The method <code>getStream</code> always will return null after the stream
	 * has been released.
	 */
	public void releaseStream() throws IOException {
		if (content != null) {
			content.endPart(false);
		}
	}
	
	/**
	 * Returns the entity of this part
	 * @return The response entity, or null if this response has no body.
	 * @see IResponse#getEntity()
	 */
	public IResponseEntity getEntity() {
		return entity;
	}

	/**
	 * Sets the part's entity.
	 * @param entity the response entity.
	 */
	public void setEntity(IResponseEntity entity) {
		this.entity = entity;
	}	
	
	/**
	 * Initializes this response part from the source stream.
	 * @param source the input stream from which to read the part.
	 * @return true, if a part has been found; false, if the final boundary
	 * delimiter has been found instead of a part.
	 * @throws IOException - if an I/O error occurs
	 * @throws HTTPException - if the part is malformed, invalid or incomplete.
	 */
	public boolean initialize(PartitionInputStream source) throws IOException, HTTPException 
	{
		String line = null;
		line = source.readLine(SKIP_EMPTY_LINES, SKIP_WHITESPACE);
		if (line.startsWith(boundaryStr)) {
			if (line.endsWith("--")) {
				return FINAL_BOUNDARY_FOUND;
			}
		} else {
			throw new HTTPException("Invalid multipart body: boundary " + boundary + " expected");			
		}		
		
		Header currentHeader = null;
		line = source.readLine(SKIP_EMPTY_LINES, SKIP_WHITESPACE);		
		while (line != null && !line.equals(END_OF_LINE)) {
			if (Character.isWhitespace(line.charAt(0)) && currentHeader != null) {
				currentHeader.appendPart(line.trim(), " ");
			} else {
				currentHeader = new Header(line);
				setHeader(currentHeader, APPEND_TO_EXISTING_HEADER);
			}
			TRACE.debugT("initialize(ResponsePart)", line);
			line = source.readLine(REPORT_EMPTY_LINES, REPORT_WHITESPACE);
		}
		TRACE.debugT("initialize(ResponsePart)", " ");
		
		// Evaluate some Content-XXX headers
		String length = getHeaderValue(Header.HTTP.CONTENT_LENGTH);
		try {
			if (length != null) {
				contentLength = Long.parseLong(length);
			} else {
				contentLength = -1L;
			}
		} catch (NumberFormatException e) {
			// if we have a malformed Content-Length, rely on boundary
			TRACE.catching("initialize(PartitionInputStream)", e);
			contentLength = -1L;  
		}
		contentType = getHeaderValue(Header.HTTP.CONTENT_TYPE);		
		contentID = getHeaderValue(Header.HTTP.CONTENT_ID);		
		content = source;					
		return true;
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

}
