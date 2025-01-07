package com.tssap.dtr.client.lib.protocol.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
 * This class implements an HTTP entity based on files.
 */
public final class FileEntity extends ResponseEntityBase implements IRequestEntity, IResponseEntity {

	/** The abstract file specification associated with this entity */
	private File file;

	/**
	 * Creates a new file entity for a given File object
	 * and specified MIME type (e.g. "text/xml").
	 * @param file the file associated with this entity.
	 * @param contentType  a MIME type identifier.
	 */
	public FileEntity(File file, String contentType) {
		super(contentType);
		this.file = file;
	}

	/**
	 * Creates a new file entity from the specified response.
	 * Reads the content of this entity from the response stream.
	 * @param file  the file associated with this entity.
	 * @param response  the response object that porvides the new content of the file.
	 * @throws HTTPException - if the response is malformed, invalid or incomplete.
	 */
	public FileEntity(File file, IResponse response) throws HTTPException {
		super(response);
		this.file = file;
		try {
			read(response.getContent());
		} catch (IOException ex) {
			contentType = null;
			throw new HTTPException("Failed to read response body to file.", ex);
		}
	}


	/**
	 * The entity type string for FileEntity.
	 */
	public static final String ENTITY_TYPE = "FileEntity";
	
	/**
	 * Checks whether the given response entity is a FileEntity.
	 * @return true, if the entity is a FileEntity.
	 */
	public static boolean isFileEntity(IResponseEntity entity) {
		return ENTITY_TYPE.equals(entity.getEntityType());
	}
	
	/**
	 * Returns the given response entity as FileEntity.
	 * @return the entity casted to FileEntity, or null
	 * if the entity cannot be converted to FileEntity
	 */
	public static FileEntity valueOf(IResponseEntity entity) {
		return (isFileEntity(entity))? ((FileEntity)entity) : null;		
	}

	/**
	 * Returns the type of this entity.
	 * @return "FileEntity".
	 * @see IResponseEntity#getEntityType()
	 */
	public String getEntityType() {
		return ENTITY_TYPE;
	}
	

	/**
	* Returns the length of the file in bytes.
	* @return The length of the file.
	 * @see IRequestEntity#getContentLength()
	 */
	public long getContentLength() {
		return file.length();
	}

	/**
	* Sets the MD5 hashsum of that entity. Note, this method does not proof
	* if the MD5 matches the content of the file.
	* @param contentMD5 the MD5 hashsum of that file.
	 */
	public void setContentMD5(String contentMD5) {
		this.contentMD5 = contentMD5;
	}

	/**
	* Calculates the MD5 hashsum of that entity from the content of the
	* associated file. Note, this method may be very time consuming if
	* applied to large files.
	* @return An MD5 hashsum, or null if the calculation failed.
	* @throws NoSuchAlgorithmException   if the runtime does not support MD5
	* message digest.
	* @throws IOException if an I/O error occurs while reading the file.
	 */
	public String calculateMD5() throws NoSuchAlgorithmException, IOException {
		if (file != null) {
			MessageDigest MD5 = MessageDigest.getInstance("MD5");
			FileInputStream in = new FileInputStream(file);
			byte[] buf = new byte[2048];
			int count = -1;
			while ((count = in.read(buf)) != -1) {
				MD5.update(buf, 0, count);
			}
			byte[] digest = MD5.digest();
			return Encoder.toHexString(digest);
		}
		return null;
	}

	/**
	* Reads the content of this entity from the specified stream.
	* @param source  the input stream from which the content is read.
	* @throws IOException - if an i/o error occurs
	*/
	public void read(IResponseStream source) throws IOException {
		if (file != null && source != null) {
			FileOutputStream destination = new FileOutputStream(file);
			int cnt = source.read(destination);
			while (cnt > 0) {
				cnt = source.read(destination);
			}
			destination.close();
		}
	}

	/**
	 * Writes the content of this entity to the specified stream.
	 * This method creates a FileInputStream for the specified File object
	 * and copies the content of the file directly to the destination stream.
	 * The method may throw an IOException if the file does not exist,
	 * is not readable or access is denied.
	 * @param destination the stream to which the content is written.
	 * @throws IOException - if an i/o error occurs
	 * @see IRequestEntity#write(IRequestStream)
		*/
	public void write(IRequestStream destination) throws IOException {
		if (file != null) {
			FileInputStream source = new FileInputStream(file);
			destination.write(source);
			source.close();
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
