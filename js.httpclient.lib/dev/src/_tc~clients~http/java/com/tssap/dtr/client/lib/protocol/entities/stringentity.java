package com.tssap.dtr.client.lib.protocol.entities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.IRequestStream;
import com.tssap.dtr.client.lib.protocol.util.Encoder;

/**
 * This class implements a request entity based on a string buffer.
 */
public final class StringEntity implements IRequestEntity {

	/** The string buffer holding the content of this entity */
	private StringBuffer content;

	/** IResponseEntity state */
	private long contentLength = -1L;
	private String contentType;
	private String contentMD5;
	private String contentCharset;
	
	/** trace location*/
	private static Location TRACE = Location.getLocation(StringEntity.class);	

	/**
	 * Creates a new string entity with specified MIME type, like "text/xml",
	 * and character encoding.
	 * <p>The encoding is appended to the content type as a "charset" parameter
	 * according to RFC2616.</p>
		 * <p>Every implementation of the Java platform is required to support the
		 * following character encodings:<br/>
		 * <blockquote><table width="80%">
		 * <tr><td valign=top><tt>US-ASCII</tt></td>
	 * <td>Seven-bit ASCII, a.k.a. <tt>ISO646-US</tt>,
	 *     a.k.a. the Basic Latin block of the Unicode character set</td></tr>
		 * <tr><td valign=top><tt>ISO-8859-1&nbsp;&nbsp;</tt></td>
	 * <td>ISO Latin Alphabet No. 1, a.k.a. <tt>ISO-LATIN-1</tt></td></tr>
		 * <tr><td valign=top><tt>UTF-8</tt></td>
	 * <td>Eight-bit Unicode Transformation Format</td></tr>
		 * <tr><td valign=top><tt>UTF-16BE</tt></td>
	 * <td>Sixteen-bit Unicode Transformation Format,
	 *     big-endian byte&nbsp;order</td></tr>
		 * <tr><td valign=top><tt>UTF-16LE</tt></td>
	 * <td>Sixteen-bit Unicode Transformation Format,
	 *     little-endian byte&nbsp;order</td></tr>
		 * <tr><td valign=top><tt>UTF-16</tt></td>
	 * <td>Sixteen-bit Unicode Transformation Format,
	 *     byte&nbsp;order specified by a mandatory initial byte-order mark
		 * (either order accepted on input, big-endian used on output)</td></tr>
		 *</table></blockquote></p>
		 *
	 * @param contentType a MIME type specifier.
	 * @param contentCharset the character encoding used by this entity (e.g. "UTF-8").
	 */
	public StringEntity(String contentType, String contentCharset) {
		this.contentType = contentType;
		if ( contentCharset!=null ) {
			this.contentType  += "; charset=\"" + contentCharset.toLowerCase() + "\"";
		}
		this.contentCharset = contentCharset;
	}

	/**
	 * Creates a new string entity with specified MIME type, like "text/xml",
	 * character encoding and initial content. The encoding is appended
	 * to the content type as a "charset" parameter according to RFC2616.
	 * @param contentType a MIME type specifier.
	 * @param contentCharset the character encoding used by this entity (e.g. "UTF-8").
	 * @param content the content of this entity.
	 */
	public StringEntity(String contentType, String contentCharset, String content) {
		this(contentType, contentCharset);
		this.content = new StringBuffer(content);
	}

//	/**
//	 * Creates a new string entity from the specified response.
//	 * @param response the response from which this entity is initialized.
//	 * @throws HTTPException - if the response is malformed, invalid or incomplete.
//	 */
	/*public StringEntity(IResponse response) throws HTTPException {
		initialize(response);
	try {
	  read(response.getContent());
	} catch (IOException ex) {
	  content.setLength(0);
	  contentType = null;
	  throw new HTTPException(ex);
	}
	}*/

	/**
	 * Initializes the entity from the specified response. The method
	 * tries to retrieve the "Content-Type", "Content-MD5",
	 * "ETag" and "Last-Modified" headers and the content type's "charset" parameter
	 * from the response.
	 * @param response the response from which this entity is initialized.
	 */
	/*public void initialize(IResponse response) {
	  contentType = response.getContentType();
	  if (contentType!=null) {
	    int n = contentType.indexOf("charset=\"");
	    int m = contentType.indexOf("\"", n+9);
	    if (n != -1) {
	      contentCharset = contentType.substring(n+9,m);
	    }
	  }
			contentMD5 = response.getHeaderValue("Content-MD5");
			entityTag = response.getHeaderValue("ETag");
			lastModified = response.getHeaderValue("Last-Modfied");
	}*/

	/**
	* Returns the type of this entity.
	* @return "StringEntity".
	 * @see com.tssap.dtr.client.lib.protocol.IResponseEntity#getEntityType()
	 */
	public String getEntityType() {
		return "StringEntity";
	}

	/**
	* Returns the length of that entity in bytes according to the
	* selected character encoding. Corresponds to
	* the HTTP "Content-Length" entity header.
	* @return The content length, or -1 if the content length
	* is undefined or the selected character encoding not supported.
	 * @see com.tssap.dtr.client.lib.protocol.IResponseEntity#getContentLength()
	 */
	public long getContentLength() {
		if (content != null) {
			try {
				contentLength = Encoder.getBytes(content, contentCharset).length;
			} catch (UnsupportedEncodingException e) {
				TRACE.catching("getContentLength()", e);
				contentLength = -1L;
			}
		}
		return contentLength;
	}

	/**
	* Returns the MIME type of that entity. Corresponds to
	* the HTTP "Content-Type" entity header. May include additional
	* parameters like "charset" according to RFC2616.
	* @return A MIME type specifier (eventually with additional parameters),
	* or null if no "Content-Type" header was defined in the response.
	 * @see com.tssap.dtr.client.lib.protocol.IResponseEntity#getContentType()
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	* Returns the character encoding used by this entity. Corresponds to
	* the "charset" parameter of the HTTP "Content-Type" entity header.
	* @return The character encoding used by this entity, or null
	* if the encoding is unknown.
	 * @see com.tssap.dtr.client.lib.protocol.IResponseEntity#getContentType()
	 */
	public String getContentCharset() {
		return contentCharset;
	}

	/**
	* Returns the unique id of that entity. Corresponds to the HTTP "ETag" entity header.
	* @return An entity tag, or null if no "ETag" header was defined
	* in the response.
	 * @see com.tssap.dtr.client.lib.protocol.IResponseEntity#getEntityTag()
	 */
	/*public String getEntityTag() {
		return entityTag;
	}*/

	/**
	* Returns the date of last modification of that entity. Corresponds to
	* the HTTP "Last-Modified" entity header.
	* @return The time of last modification, or null if no "Last-Modified" header was defined
	* in the response.
	 * @see com.tssap.dtr.client.lib.protocol.IResponseEntity#getLastModfied()
	 */
	/*public String getLastModfied() {
		return lastModified;
	}*/

	/**
	* Returns the MD5 hashsum of that entity. Corresponds to
	* the HTTP "Content-MD5" entity header.
	* @return An MD5 hashsum, or null if the hashsum has not yet been
	* defined.
	 * @see com.tssap.dtr.client.lib.protocol.IResponseEntity#getContentMD5()
	 */
	public String getContentMD5() {
		return contentMD5;
	}

	/**
	* Sets the MD5 hashsum of that entity.
	* Note, this method does not proof
	* if the MD5 matches the content of the string.
	* @param contentMD5 the MD5 hashsum of that file.
	 */
	public void setContentMD5(String contentMD5) {
		this.contentMD5 = contentMD5;
	}

	/**
	* Calculates the MD5 hashsum of that entity from the content of the
	* associated string. Note, this method may be very time consuming if
	* applied to large string.
	* @return An MD5 hashsum, or null if the calculation failed.
	* @throws NoSuchAlgorithmException   if the runtime does not support MD5
	* message digest.
	* @throws UnsupportedEncodingException  if the given character encoding is
	* not supported by the platform.
	 */
	public String calculateMD5(String enc) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		if (content != null) {
			MessageDigest MD5 = MessageDigest.getInstance("MD5");
			MD5.update(Encoder.getBytes(content, enc));
			byte[] digest = MD5.digest();
			return Encoder.toHexString(digest);
		}
		return null;
	}

	/**
	 * Returns the content of this entity.
	 * @return The content of this entity as string.
	 */
	public String getContent() {
		return content.toString();
	}

	/**
	 * Sets the content of this entity.
	 * @param content the new content of this entity as string.
	 */
	public void setContent(String content) {
		if (this.content == null) {
			this.content = new StringBuffer(content);
		} else {
			this.content.setLength(0);
			this.content.append(content);
		}
	}

	/**
	 * Returns the length of the internal string buffer. Note, the value returned
	 * by this method may differ from <code>getContentLength</code> depending on the selected
	 * character encoding.
	 * @return The number of characters hosted by this string entity, or -1 of
	 * no content yet has been defined.
	 */
	public int length() {
		return (content != null) ? content.length() : -1;
	}

	/**
	* Reads the content of this entity from the specified stream.
	* @param source - the stream from which the content is read.
	* @throws IOException - if an i/o error occurs.
	 */
	/*public void read(IResponseStream source) throws IOException {
	if (source!=null) {
	  if (content==null) {
	    content = new StringBuffer();
	  } else {
	    content.setLength(0);
	  }
	  int cnt = source.readContent(content, contentCharset);
	  while (cnt>0) {
	    cnt = source.readContent(content, contentCharset);
	  }
	}
	}*/

	/**
	* Writes the content of this entity to the specified stream.
	* The specified encoding is used to transform the string into a
	* byte array.
	* @param destination the stream to which the content is written.
	* @throws IOException if no content has yet been defined or an i/o error occurs.
	 * @see IRequestEntity#write(IRequestStream)
	 */
	public void write(IRequestStream destination) throws IOException {
		if (content != null) {
			destination.write(Encoder.getBytes(content, contentCharset));
		}
	}

	/**
	 * Appends the specified string to the content of the entity and returns
	 * a reference to the internal StringBuffer that can be used for further append
	 * operations.
	 * @param s the string to append to this entity.
	 * @return Reference to the internal string buffer for further append operations.
	 */
	public StringBuffer append(String s) {
		if (content == null) {
			content = new StringBuffer(s);
		} else {
			content.append(s);
		}
		return content;
	}

	/**
	 * Prepares the entity for a repetition of a request. This method
	 * does nothing.
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

	/**
	 * Indicates whether some other StringEnity is "equal to" this one.
	 * The method compares the string contents of the given StringEntity
	 * with the content of this one.
	 * @param obj the object to compare with.
	 * @return True, if the string contents of the given StringEntity matches
	 * the content of this entity.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof StringEntity) {
			String a = content.toString();
			String b = ((StringEntity) obj).content.toString();
			return a.equals(b);
		} else if (obj instanceof String) {
			String a = content.toString();
			return a.equals(obj);
		} else {
			return false;
		}
	}
	
	

	public int hashCode() {
		return (content!=null)? content.toString().hashCode() : super.hashCode();			
	}

}
