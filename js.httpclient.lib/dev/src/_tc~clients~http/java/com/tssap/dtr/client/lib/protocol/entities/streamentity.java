package com.tssap.dtr.client.lib.protocol.entities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tssap.dtr.client.lib.protocol.HTTPException;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.IRequestStream;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;
import com.tssap.dtr.client.lib.protocol.IResponseStream;

/**
 * This class implements an HTTP entity based on streams.
 */
public final class StreamEntity
	extends ResponseEntityBase
	implements IRequestEntity, IResponseEntity {

	/** The input stream associated with this entity */
	private InputStream in;
	/** The output stream associated with this entity */
	private OutputStream out;

	/**
	 * Creates a new stream entity for requests with
	 * the specified MIME type (e.g. "text/xml").
	 * @param in the input stream associated with this entity.
	 * @param contentType a MIME type identifier.
	 */
	public StreamEntity(InputStream in, String contentType) {
		super(contentType);
		this.in = in;
	}

	/**
	 * Creates a new stream response entity from the specified response.
	 * Reads the content of this entity from the response stream.
	 * @param out the stream to which the content of the response is written.
	 * @param response the response from which this entity is initialized.
	 * @throws HTTPException - if the response is malformed, invalid or incomplete.
	 */
	public StreamEntity(OutputStream out, IResponse response) throws HTTPException {
		super(response);
		this.out = out;
		try {
			read(response.getContent());
		} catch (IOException ex) {
			contentType = null;
			throw new HTTPException("Failed to read response body to stream.", ex);
		}
	}

	/**
	 * The entity type string for StreamEntity.
	 */
	public static final String ENTITY_TYPE = "StreamEntity";

	/**
	 * Checks whether the given response entity is a StreamEntity.
	 * @return true, if the entity is a StreamEntity.
	 */
	public static boolean isStreamEntity(IResponseEntity entity) {
		return ENTITY_TYPE.equals(entity.getEntityType());
	}

	/**
	 * Returns the given response entity as StreamEntity.
	 * @return the entity casted to StreamEntity, or null
	 * if the entity cannot be converted to StreamEntity
	 */
	public static StreamEntity valueOf(IResponseEntity entity) {
		return (isStreamEntity(entity)) ? ((StreamEntity)entity) : null;
	}

	/**
	 * Returns the type of this entity.
	 * @return "StreamEntity".
	 * @see IResponseEntity#getEntityType()
	 */
	public String getEntityType() {
		return ENTITY_TYPE;
	}

	/**
	* Sets the length of this entity in bytes.
	* @param contentLength the length of the entity.
	 * @see IRequestEntity#getContentLength()
	 */
	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	/**
	* Sets the MD5 hashsum of that entity. Note, this method does not proof
	* if the MD5 matches the content of the stream entity.
	* @param contentMD5 the MD5 hashsum of that stream entity.
	 */
	public void setContentMD5(String contentMD5) {
		this.contentMD5 = contentMD5;
	}

	/**
	* Reads the content of this entity from the specified stream.
	* Note, the destination stream assigned to this entity is left open and may
	* be used for further write operations.
	* @param source the input stream from which the content is read.
	* @throws IOException - if an i/o error occurs
	*/
	public void read(IResponseStream source) throws IOException {
		if (out != null && source != null) {
			int cnt = source.read(out);
			while (cnt > 0) {
				cnt = source.read(out);
			}
		}
	}

	/**
	 * Writes the content of this entity to the specified stream.
	 * Note, the source stream assigned to this entity is left open and may
	 * be used for further read operations.
	 * @param destination - the stream to which the content is written.
	 * @throws IOException - if an i/o error occurs
	 * @see IRequestEntity#write(IRequestStream)
		*/
	public void write(IRequestStream destination) throws IOException {
		if (in != null) {
			destination.write(in);
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
	 * @return always false.
	 */
	public boolean supportsReset() {
		return false;
	}

}
