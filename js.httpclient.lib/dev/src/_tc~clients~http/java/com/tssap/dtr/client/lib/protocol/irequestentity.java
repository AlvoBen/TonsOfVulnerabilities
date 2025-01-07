package com.tssap.dtr.client.lib.protocol;

import java.io.IOException;

/**
 * This interface represents the "enity" of a HTTP request.
 * <p>According to HTTP the entity of a request encloses (besides
 * the actual request body, i.e. the payload of the request)
 * a set of header entries, e.g. the "Content-Length",
 * "Content-Size" and "Content-MD5" headers, that are directly
 * related to properties of the body.</p>
 */
public interface IRequestEntity {

	/**
	 * Returns the type of this entity.
	 * @return A string describing the type of this entity.
	 */
	String getEntityType();

	/**
	 * Returns the length of that entity in bytes. Corresponds to
	 * the HTTP "Content-Length" entity header.
	 * @return The content length of the entity, or -1 if the
	 * length is unknown.
	 */
	long getContentLength();

	/**
	 * Returns the MIME type of that entity. Corresponds to
	 * the HTTP "Content-Type" entity header. May include additional
	 * parameters like "charset" according to RFC2616.
	 * @return The content type of the entity.
	 */
	String getContentType();

	/**
	 * Returns the MD5 hashsum of that entity. Corresponds to
	 * the HTTP "Content-MD5" entity header. This hashum is used
	 * for example to calculate authentication information if Digest
	 * authentication is used (compare the 'auth-int' value for the
	 * qop parameter according to RFC 2617).
	 * @return The MD5 hashsum of the entity's content, or null if
	 * the hashum is unknown.
	 */
	String getContentMD5();

	/**
	 * Writes the content of this entity to the specified stream.
	 * Note, an implementation of this method <b>must not</b> close the
	 * destination stream after the write is complete.
	 * @param destination the stream to which the content is written.
	 * @throws IOException  if an i/o error occurs.
	 */
	void write(IRequestStream destination) throws IOException;

	/**
	 * Prepares the entity for a repetition of a request.
	 */
	void reset() throws IOException;

	/**
	 * Checks whether this entity supports request repetition.
	 * @return true, if request repetitions are supported.
	 */
	boolean supportsReset();
}
