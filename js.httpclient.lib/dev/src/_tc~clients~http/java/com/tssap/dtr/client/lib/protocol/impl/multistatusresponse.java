package com.tssap.dtr.client.lib.protocol.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IMultiStatusResponse;
import com.tssap.dtr.client.lib.protocol.IRequest;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;
import com.tssap.dtr.client.lib.protocol.IResponseStream;
import com.tssap.dtr.client.lib.protocol.entities.MultiStatusEntity;
import com.tssap.dtr.client.lib.protocol.entities.ResourceElement;

/**
 */
public class MultiStatusResponse implements IMultiStatusResponse {

	IResponse inner;

	/**
	 * Constructor for MultiStatusResponse.
	 * @param request
	 */
	public MultiStatusResponse(IResponse response) {
		inner = response;
	}

	/**
	 * @see com.tssap.dtr.client.lib.protocol.IMultiStatusResponse#count()
	 */
	public int size() {
		return (inner.isMultiStatus())
			? ((MultiStatusEntity)inner.getEntity()).countResources()
			: 0;
	}

	/**
	 * @see com.tssap.dtr.client.lib.protocol.IMultiStatusResponse#first()
	 */
	public ResourceElement first() {
		return get(0);
	}

	/**
	 * @see com.tssap.dtr.client.lib.protocol.IMultiStatusResponse#getResource(int)
	 */
	public ResourceElement get(int i) {
		return (inner.isMultiStatus())
			? ((MultiStatusEntity)inner.getEntity()).getResource(i)
			: null;
	}

	/**
	 * @see com.tssap.dtr.client.lib.protocol.IMultiStatusResponse#getResources()
	 */
	public Iterator iterator() {
		return (inner.isMultiStatus())
			? ((MultiStatusEntity)inner.getEntity()).getResources()
			: null;
	}

	/**
	 * Checks whether this response is valid.
	 * @return True, if at least a valid status code and the HTTP version is defined.
	 */
	public boolean isValid() {
		return inner.isValid();
	}

	/**
	 * Returns the HTTP version of this response.
	 * @return A string in the format "HTTP/X", where X is either "1.0" or "1.1"
	 */
	public String getHTTPVersion() {
		return inner.getHTTPVersion();
	}

	/**
	 * Checks whether this response uses HTTP version 1.0
	 * @return true if the response line included the version string "HTTP/1.0".
	 */
	public boolean getUsingHTTP10() {
		return inner.getUsingHTTP10();
	}

	/**
	 * Returns the status code of this response.
	 * @return A status code according to RFC2616 (e.g. '200')
	 */
	public int getStatus() {
		return inner.getStatus();
	}

	/**
	 * Returns the status description of this response.
	 * @return A status description according to RFC2616 (e.g. 'OK')
	 */
	public String getStatusDescription() {
		return inner.getStatusDescription();
	}
	
	/**
	 * Retursn the complete status line of the response.
	 * @return the status line of the response, e.g. "HTTP/1.1 200 OK"
	 */
	public String getStatusLine() {
		return inner.getStatusLine();
	}	

	/**
	 * Returns the time in milliseconds the last request-response cycle lasted.
	 * @return The round-trip time in milliseconds.
	 */
	public long getDuration() {
		return inner.getDuration();
	}

	/**
	 * Returns the specified response header.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @return The specified header, of null if the header does not exist.
		 */
	public Header getHeader(String name) {
		return inner.getHeader(name);
	}

	/**
	 * Returns the value of the specified response header.
	 * Note, header names are not case-sensitive.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @return The value of the specified header, of null if the header does not exist.
	 */
	public String getHeaderValue(String name) {
		return inner.getHeaderValue(name);
	}

	/**
	 * Returns the value of the specified response header, or a
	 * default value if the header is not defined.
	 * Note, header names are not case-sensitive.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @param defaultValue - the value that should be returned if the header is not
	 * defined for this response.
	 * @return The value of the specified header, or <code>defaultValue</code>.
	 */
	public String getHeaderValue(String name, String defaultValue) {
		return inner.getHeaderValue(name, defaultValue);
	}

	/**
	 * Returns the names of the headers defined by this request.
	 * @return An enumeration of strings representing header names.
	 */
	public Iterator getHeaderNames() {
		return inner.getHeaderNames();
	}

	/**
	 * Returns the content type of the response body.
	 * @return The value of the "Content-Type" header of this response.
	 */
	public String getContentType() {
		return inner.getContentType();
	}

	/**
	 * Returns the MIME type of the response body.
	 */
	public String getMimeType() {
		return inner.getMimeType();
	}

	/**
	 * Returns the content length of the response body.
	 * @return The value of the "Content-Length" header of this response,
	 * or -1 if the content length is undefined (usually because the
	 * server used "chunked" transfer encoding, see RFC2616).
	 * @return The length of the response body in bytes.
	 */
	public long getContentLength() {
		return inner.getContentLength();
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
		return inner.getDigest();
	}

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
	public IResponseStream getContent() {
		return inner.getContent();
	}

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
	public InputStream getStream() {
		return inner.getStream();
	}

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
	public void releaseStream() throws IOException {
		inner.releaseStream();
	}

	/**
	 * Checks whether this response has a body.
	 * @return True, if the response defines a valid "Content-Type" header,
	 * a content length>0, a "Transfer-Encoding" header, or an "ETag" header.
	 * @return True, if this response has a body.
	 */
	public boolean hasContent() {
		return inner.hasContent();
	}

	/**
	 * Checks whether the content is an XML document.
	 * @return True, if the "Content-Type" header of the response entity is either
	 * "text/xml" or "application/xml".
	 */
	public boolean isContentXML() {
		return inner.isContentXML();
	}

	/**
	 * Checks whether the content is a Multistatus response entity.
	 * @return True, if the getEntityType() returns "Multistatus" and the
	 * response status is 207 MULTISTATUS.
	 */
	public boolean isMultiStatus() {
		return inner.isMultiStatus();
	}

	/**
	 * Returns the response entity.
	 * @return A string specifying the type of the response entity, or null if this
	 * response has no entity.
	 */
	public IResponseEntity getEntity() {
		return inner.getEntity();
	}

	/**
	 * Sets the response entity.
	 * @param entity the response entity.
	 */
	public void setEntity(IResponseEntity entity) {
		inner.setEntity(entity);
	}

	/**
	 * Returns the type of the response entity.
	 * @return A string specifying the type of the response entity, or null if this
	 * response has no entity.
	 */
	public String getEntityType() {
		return inner.getEntityType();
	}

	/**
	 * Returns a reference to the request this response belongs to.
	 * @return Reference to the corresponding request object.
	 */
	public IRequest getRequest() {
		return inner.getRequest();
	}
	/**
	 * Skips the body of this response.
	 * @throws IOException - if an i/o error occurs.
		 */
	public void skipContent() throws IOException {
		inner.skipContent();
	}

}
