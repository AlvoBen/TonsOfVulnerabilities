package com.tssap.dtr.client.lib.protocol;

import java.util.List;

/**
 * This interface represents the "enity" of a HTTP response.
 * <p>According to HTTP the entity of a request encloses (besides
 * the actual request body, i.e. the payload of the request)
 * a set of header entries, e.g. the "Content-Length",
 * "Content-Size" and "Content-MD5" headers, that are directly
 * related to properties of the body.</p>
 * <p>The methods of this interface only provide access to the most
 * important entity header fields of a HTTP response.
 * The implementors of this interface usually add
 * some own methods to allow access to the "payload" of a response.</p>
 */
public interface IResponseEntity {

	/**
	 * Returns the type of this entity.
	 * @return A string idenifying the type of an entity.
	 */
	String getEntityType();

	/**
	 * Returns the length of that entity in bytes. Corresponds to
	 * the HTTP "Content-Length" entity header.
	 */
	long getContentLength();

	/**
	 * Returns the content type of that entity. Corresponds to
	 * the HTTP "Content-Type" entity header. May include additional
	 * parameters like "charset" according to RFC2616.
	 * The default value is "application/octet-stream".
	 * @return A MIME type specifier (eventually with additional parameters),
	 * or null if no "Content-Type" header was defined in the response.
	 * @see IResponseEntity#getContentType()
	 */
	String getContentType();

	/**
	 * Returns a list of content type parameters.
	 * The Content-Type header may list additional 
	 * parameters like "charset" according to RFC2616.
	 * @return A list of Pair instances, or null if the header
	 * contained no parameters.
	 * @see IResponseEntity#getContentTypeParams()
	 */
	List getContentTypeParams();

	/**
	 * Returns the MIME type of that entity.
	 * This is the same as the content type but without additional parameters.
	 * The default value is "application/octet-stream".
	 * @return A MIME type specifier
	 * or null if no "Content-Type" header was defined in the response.
	 * @see IResponseEntity#getMimeType()
	 */
	String getMimeType();

	/**
	 * Returns the MD5 hashsum of that entity. Corresponds to
	 * the HTTP "Content-MD5" entity header. This hashum is used
	 * for example to calculate authentication information if Digest
	 * authentication is used (compare the 'auth-int' value for the
	 * qop parameter according to RFC 2617).
	 * @return The MD5 hashsum of the entity's content, or null if
	 * the hashum could not be calculated.
	 */
	String getContentMD5();

	/**
	 * Returns the unique id of that entity.
	 * Corresponds to the HTTP "ETag" entity header.
	 * @return The value of the "ETag" header of the response.
	 */
	String getEntityTag();

	/**
	 * Returns the date of last modification of that entity. Corresponds to
	 * the HTTP "Last-Modified" entity header.
	 * @return The value of the "Last-Modified" header of the response.
	 */
	String getLastModfied();

	/**
	 * Returns the character encoding used by this entity. Corresponds to
	 * the "charset" parameter of the HTTP "Content-Type" entity header.
	 * @return The character encoding used by this entity.
	 */
	String getContentCharset();

	/**
	 * Initializes the entity from the specified response.
	 * The method tries to retrieve the "Content-Type", "Content-MD5",
	 * "ETag" and "Last-Modified" headers and the content type's "charset" parameter
	 * from the response. If "charset" is not specified, "UTF-8" is assumed.
	 * @param response the response from which this entity is initialized.
	 */
	void initialize(IResponse response);
}
