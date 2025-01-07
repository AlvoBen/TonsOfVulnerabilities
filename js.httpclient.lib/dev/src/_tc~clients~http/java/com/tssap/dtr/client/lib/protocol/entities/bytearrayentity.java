package com.tssap.dtr.client.lib.protocol.entities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.tssap.dtr.client.lib.protocol.HTTPException;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.IRequestStream;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;
import com.tssap.dtr.client.lib.protocol.IResponseStream;
import com.tssap.dtr.client.lib.protocol.util.Encoder;

/**
 * This class implements an HTTP entity based on a byte array.
 */
public final class ByteArrayEntity extends ResponseEntityBase implements IRequestEntity, IResponseEntity {

	/** The byte array associated with this entity */
	private byte[] buf;
	private int off;
	private int len;

	/**
	 * Creates a new byte array entity for requests with
	 * the specified MIME type (e.g. "text/xml").
	 * @param buf  the byte array that provides the content of this entity.
	 * @param contentType  a MIME type identifier.
	 */
	public ByteArrayEntity(byte[] buf, String contentType) {
		super(contentType);
		this.buf = buf;
		this.off = 0;
		this.len = buf.length;
	}

	/**
	 * Creates a new byte array entity for requests with
	 * the specified MIME type (e.g. "text/xml").
	 * @param buf  the byte array that provides the content of this entity.
	 * @param off  the start offset in array buf.
	 * @param len  the maximum number of bytes to read from buf.
	 * @param contentType a MIME type identifier.
	 */
	public ByteArrayEntity(byte[] buf, int off, int len, String contentType) {
		super(contentType);
		this.buf = buf;
		this.off = off;
		this.len = len;
	}

	/**
	 * Creates a new byte array entity from the specified response.
	 * @param response the response from which this entity is initialized.
	 * @throws HTTPException - if the response is malformed, invalid or incomplete.
	 */
	public ByteArrayEntity(IResponse response) throws HTTPException {
		super(response);
		try {
			read(response.getContent());
		} catch (IOException ex) {
			contentType = null;
			throw new HTTPException("Failed to read response body to buffer.", ex);
		}
	}

	/**
	 * The entity type string for ByteArrayEntity.
	 */
	public static final String ENTITY_TYPE = "ByteArrayEntity";
	
	/**
	 * Checks whether the given response entity is a ByteArrayEntity.
	 * @return true, if the entity is a ByteArrayEntity.
	 */
	public static boolean isByteArrayEntity(IResponseEntity entity) {
		return ENTITY_TYPE.equals(entity.getEntityType());
	}
	
	/**
	 * Returns the given response entity as ByteArrayEntity.
	 * @return the entity casted to ByteArrayEntity, or null
	 * if the entity cannot be converted to ByteArrayEntity
	 */
	public static ByteArrayEntity valueOf(IResponseEntity entity) {
		return (isByteArrayEntity(entity))? ((ByteArrayEntity)entity) : null;		
	}

	/**
	 * Returns the type of this entity.
	 * @return "ByteArrayEntity".
	 * @see IResponseEntity#getEntityType()
	 */
	public String getEntityType() {
		return ENTITY_TYPE;
	}

	/**
	 * Returns the length of that entity in bytes. Corresponds to
	 * the HTTP "Content-Length" entity header.
	 * @return The content length, or -1 if the content length
	 * is undefined.
	 * @see IResponseEntity#getContentLength()
	 */
	public long getContentLength() {
		return buf.length;
	}

	/**
	 * Sets the MD5 hashsum of that entity. Note, this method does not proof
	 * if the MD5 matches the content of the byte array.
	 * @param contentMD5 the MD5 hashsum of that file.
	 */
	public void setContentMD5(String contentMD5) {
		this.contentMD5 = contentMD5;
	}

	/**
	 * Calculates the MD5 hashsum of that entity from the content of the
	 * associated byte array. Note, this method may be very time consuming if
	 * applied to large byte arrays.
	 * @return An MD5 hashsum, or null if the calculation failed.
	 * @throws NoSuchAlgorithmException   if the runtime does not support MD5
	 * message digest.
	 */
	public String calculateMD5() throws NoSuchAlgorithmException {
		if (buf != null) {
			MessageDigest MD5 = MessageDigest.getInstance("MD5");
			MD5.update(buf, off, len);
			byte[] digest = MD5.digest();
			return Encoder.toHexString(digest);
		}
		return null;
	}

	/**
	 * Returns the content of this entity.
	 * @return The content of this entity as byte array, or null
	 * if this entity has no content.
	 */
	public byte[] getContent() {
		return buf;
	}

	/**
	 * Returns the content of this entity.
	 * @param enc  the character encoding to use to convert the byte array to a string.
	 * @return The content of this entity as string, or null if
	 * this entity has no content.
	 */
	public String getContent(String enc) throws UnsupportedEncodingException {
		return (buf != null) ? new String(buf, enc) : null;
	}

	/**
	* Reads the content of this entity from the specified stream.
	* @param source  the stream from which the content is read.
	* @throws IOException  if an i/o error occurs.
	 */
	public void read(IResponseStream source) throws IOException {
		if (source != null) {
			ByteArrayOutputStream dest = new ByteArrayOutputStream();
			int cnt = source.read(dest);
			if (cnt > 0) {
				while (cnt > 0) {
					cnt = source.read(dest);
				}
				buf = dest.toByteArray();
				off = 0;
				len = buf.length;
			}
		}
	}

	/**
	 * Writes the content of this entity to the specified stream.
	 * @param destination - the stream to which the content is written.
	 * @throws IOException - if an i/o error occurs
	 * @see IRequestEntity#write(IRequestStream)
	 */
	public void write(IRequestStream destination) throws IOException {
		if (buf != null) {
			destination.write(buf, off, len);
		}
	}

	/**
	 * Prepares the entity for a repetition of a request.
	 * This method does nothing.
	 * @see IRequestEntity#reset()
	 */
	public void reset() {
		// nothing to do
	}

	/**
	 * Checks whether this entity supports request repetition.
	 * @return always true.
	 */
	public boolean supportsReset() {
		return true;
	}

}
