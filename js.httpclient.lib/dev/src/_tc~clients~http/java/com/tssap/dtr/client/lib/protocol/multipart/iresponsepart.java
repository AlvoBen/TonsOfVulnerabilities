package com.tssap.dtr.client.lib.protocol.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;
import com.tssap.dtr.client.lib.protocol.IResponseStream;

/**
 * This interface represents a multipart MIME response part.
 */
public interface IResponsePart {
	
	/**
	 * Returns the content ID of the part.
	 * @return a content ID
	 */
	String getContentID();
	
	/**
	* Returns the content length of the part.
	* @return The content length in bytes.
	 */
	long getContentLength();

	/**
	* Returns the content (MIME) type of the part.
	* @return A MIME content type specifier.
	 */
	String getContentType();

	/**
	 * Returns the names of the headers defined by this part.
	 * @return An enumeration of strings representing header names.
	 */
	public Iterator getHeaderNames();
	
	/**
	 * Returns the specified header.
	 * Note, header names are not case-sensitive.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @return The specified header, of null if the header does not exist.
	 */
	public Header getHeader(String name);
	
	/**
	 * Returns the value of the specified header.
	 * Note, header names are not case-sensitive.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @return The value of the specified header, of null if the header does not exist.
	 */
	public String getHeaderValue(String name);
	
	/**
	 * Returns the value of the specified header, or a
	 * default value if the header is not defined.
	 * Note, header names are not case-sensitive.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @param defaultValue  the value that should be returned if the header is not
	 * defined for this part.
	 * @return The value of the specified header, or <code>defaultValue</code>.
	 */
	public String getHeaderValue(String name, String defaultValue);
	
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
	IResponseStream getContent();	

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
	InputStream getStream();

	/**
	 * Releases the response stream for reuse by other requests.
	 * This method should be called when the read out of the stream
	 * is complete. The method skips any remaining content.
	 * A client <b>must not</b> use or reference the content stream
	 * after this method has been called.
	 * The method <code>getStream</code> always will return null after the stream
	 * has been released.
	 */
	void releaseStream() throws IOException;	
	
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
	
}
