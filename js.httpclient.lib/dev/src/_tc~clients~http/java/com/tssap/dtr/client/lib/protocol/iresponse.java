package com.tssap.dtr.client.lib.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * This interface represents a HTTP responses.
 * <p>Methods are provided to retrieve the status code,
 * status description, headers, content type and content length
 * and the entity of a response.</p>
 */
public interface IResponse {

	/**
	 * Checks whether this response is valid.
	 * @return True, if at least a valid status code and the HTTP version is defined.
	 */
	boolean isValid();

	/**
	 * Returns the HTTP version of this response.
	 * @return A string in the format "HTTP/X", where X is either "1.0" or "1.1"
	 */
	String getHTTPVersion();

	/**
	 * Checks whether this response uses HTTP version 1.0
	 * @return true if the response line included the version string "HTTP/1.0".
	 */
	boolean getUsingHTTP10();

	/**
	 * Returns the status code of this response.
	 * @return A status code according to RFC2616 (e.g. '200')
	 */
	int getStatus();

	/**
	 * Returns the status description of this response.
	 * @return A status description according to RFC2616 (e.g. 'OK')
	 */
	String getStatusDescription();
	
	/**
	 * Returns the complete status line of the response.
	 * @return the status line of the response, e.g. "HTTP/1.1 200 OK"
	 */	
	String getStatusLine();

	/**
	 * Returns the time in milliseconds the last request-response cycle lasted.
	 * @return The round-trip time in milliseconds.
	 */
	long getDuration();

	/**
	 * Returns the specified response header.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @return The specified header, of null if the header does not exist.
		 */
	Header getHeader(String name);

	/**
	 * Returns the value of the specified response header.
	 * Note, header names are not case-sensitive.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @return The value of the specified header, of null if the header does not exist.
	 */
	String getHeaderValue(String name);

	/**
	 * Returns the value of the specified response header, or a
	 * default value if the header is not defined.
	 * Note, header names are not case-sensitive.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @param defaultValue - the value that should be returned if the header is not
	 * defined for this response.
	 * @return The value of the specified header, or <code>defaultValue</code>.
	 */
	String getHeaderValue(String name, String defaultValue);

	/**
	 * Returns the names of the headers defined by this request.
	 * @return An enumeration of strings representing header names.
	 */
	Iterator getHeaderNames();

	/**
	 * Returns the content type of the response body. Corresponds to
	 * the HTTP "Content-Type" entity header. May include additional
	 * parameters like "charset" according to RFC2616.
	 * The default value is "application/octet-stream".
	 * @return A MIME type specifier (eventually with additional parameters),
	 * or null if no "Content-Type" header was defined in the response.
	 */
	String getContentType();
	
	/**
	 * Returns the MIME type of the response body
	 * This is the same as the content type but without additional parameters.
	 * The default value is "application/octet-stream".
	 * @return A MIME type specifier
	 * or null if no "Content-Type" header was defined in the response.
	 */
	String getMimeType();	

	/**
	 * Returns the content length of the response body.
	 * @return The value of the "Content-Length" header of this response,
	 * or -1 if the content length is undefined (usually because the
	 * server used "chunked" transfer encoding, see RFC2616).
	 * @return The length of the response body in bytes.
	 */
	long getContentLength();

	/**
	 * Returns the content of the response.
	 * After the response has sucessfully been
	 * initialized the stream "points" to the first byte of the response body.
	 * Note, the stream returned by this method is a shared resource and managed
	 * by a @see Connection object. A client <b>must not</b> store permanent references to
	 * this stream and properly release it after the content has been
	 * read out (see Response#releaseStream).
	 * @return Reference to the input stream of this response, or null if the
	 * stream already has been released.
	 */
	IResponseStream getContent();

	/**
	 * Returns the content of the response.
	 * After the response has sucessfully been
	 * initialized the stream "points" to the first byte of the response body.
	 * Note, the stream returned by this method is a shared resource and managed
	 * by a @see Connection object. A client <b>must not</b> store permanent references to
	 * this stream and properly release it after the content has been
	 * read out (see Response#releaseStream).
	 * @return Reference to the input stream of this response, or null if the
	 * stream already has been released.
	 */
	InputStream getStream();

	/**
	 * Releases the response stream for reuse by other requests.
	 * This method should be called when the read out of the stream
	 * is complete. The method skips any remaining content.
	 * If the response included trailing headers they are added
	 * to the response's header collection.
	 * Note, a server does not send trailers unless the request explicitly
	 * included a TE header field indicating that "trailers" are acceptable
	 * in the response (see RFC2616).
	 * A client <b>must not</b> use or reference the content stream
	 * of a Response object anymore after this method has been called.
	 * The method <code>getStream</code> always will return null after the stream
	 * has been released.
	 */
	void releaseStream() throws IOException;

	/**
	 * Checks whether this response has a body.
	 * @return True, if the response defines a valid "Content-Type" header,
	 * a content length>0, a "Transfer-Encoding" header, or an "ETag" header.
	 * @return True, if this response has a body.
	 */
	boolean hasContent();
	

	/**
	 * Returns the message digest of the response body.
	 * In order to compare the result of this method with for eaxmple a "Content-MD5"
	 * response header it may be necessary to convert it to a "hexadecimal-encoded" string
	 * first (compare @see Encoder.toHexString(byte[])).
	 * Note, the digest is reset when the digest recording is switched on or off
	 * for the underlying input stream.
	 * @return The digest of the response body.
	 */
	byte[] getDigest();	

	/**
	 * Checks whether the content is an XML document.
	 * @return True, if the "Content-Type" header of the response entity is either
	 * "text/xml" or "application/xml".
	 */
	boolean isContentXML();

	/**
	 * Checks whether the content is a Multistatus response entity.
	 * @return True, if the getEntityType() returns "Multistatus" and the
	 * response status is 207 MULTISTATUS.
	 */
	boolean isMultiStatus();

	/**
	 * Returns the response entity.
	 * @return A string specifying the type of the response entity, or null if this
	 * response has no entity.
	 */
	IResponseEntity getEntity();

	/**
	 * Sets the response entity.
	 * @param entity the response entity.
	 */
	void setEntity(IResponseEntity entity);

	/**
	 * Returns the type of the response entity.
	 * @return A string specifying the type of the response entity, or null if this
	 * response has no entity.
	 */
	String getEntityType();

	/**
	 * Returns a reference to the request this response belongs to.
	 * @return Reference to the corresponding request object.
	 */
	IRequest getRequest();

	/**
	 * Skips the body of this response.
	 * @throws IOException - if an i/o error occurs.
	 */
	void skipContent() throws IOException;

}
