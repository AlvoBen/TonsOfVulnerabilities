package com.tssap.dtr.client.lib.protocol.multipart;

import java.util.Iterator;

import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;

/**
 * This interface represents a multipart MIME request part.
 */
public interface IRequestPart {

	/**
	 * Returns the content length of the part's body.
	 * @return The content length in bytes.
	 * @see IRequest#getContentLength()
	 */
	long getContentLength();

	/**
	 * Returns the content (MIME) type of the part's body.
	 * @return A MIME content type specifier.
	 * @see IRequest#getContentType()
	 */
	String getContentType();
	
	/**
	 * Returns the content ID of this part.
	 */
	String getContentID();	

	/**
	 * Returns the entity of this part.
	 * @return the entity
	 */
	IRequestEntity getEntity();
	
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
									

}
