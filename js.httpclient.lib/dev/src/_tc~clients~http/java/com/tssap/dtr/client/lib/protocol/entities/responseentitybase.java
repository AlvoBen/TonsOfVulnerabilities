package com.tssap.dtr.client.lib.protocol.entities;

import java.util.List;

import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;
import com.tssap.dtr.client.lib.protocol.util.Pair;
import com.tssap.dtr.client.lib.protocol.util.Tokenizer;

/**
 * Basic response entity that extracts the entity information,
 * but neither reads nor parses the response body.
 */
public class ResponseEntityBase implements IResponseEntity {

	/** IResponseEntity state */
	protected long contentLength = -1L;
	protected String contentMD5;
	protected String contentType;
	protected String mimeType;
	protected String contentCharset;
	protected List contentTypeParams;
	protected String lastModified;
	protected String entityTag;

	/**
	 * Creates a new entity with the given content type.
	 * @param contentType  a MIME content type
	 */
	public ResponseEntityBase(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Creates a new entity from the specified response.
	 * @param response  the response to evaluate.
	 */
	public ResponseEntityBase(IResponse response) {
		initialize(response);
	}

	/**
	* Returns the type of this entity.
	* @return "StreamEntity"
	 * @see com.tssap.dtr.client.lib.protocol.IRequestEntity#getEntityType()
	 */
	public String getEntityType() {
		return "ResponseEntityBase";
	}

	/**
	* Returns the character encoding used by this entity. Corresponds to
	* the "charset" parameter of the HTTP "Content-Type" entity header.
	* @return The character encoding used by this entity. Default is "UTF-8".
	 * @see IResponseEntity#getContentType()
	 */
	public String getContentCharset() {
		return contentCharset;
	}

	/**
	* Returns the content length of this entity in bytes.
	* @return The length of the entity, or -1 if the length
	* is unknown.
	 * @see com.tssap.dtr.client.lib.protocol.IRequestEntity#getContentLength()
	 */
	public long getContentLength() {
		return contentLength;
	}

	/**
	* Returns the MD5 hashsum of that entity. Corresponds to
	* the HTTP "Content-MD5" entity header.
	* @return An MD5 hashsum, or null if no "Content-MD5" header was defined
	* in the response.
	 * @see IResponseEntity#getContentMD5()
	 */
	public String getContentMD5() {
		return contentMD5;
	}

	/**
	* Returns the content type of that entity. Corresponds to
	* the HTTP "Content-Type" entity header. May include additional
	* parameters like "charset" according to RFC2616.
	* The default value is "application/octet-stream".
	* @return A MIME type specifier (eventually with additional parameters),
	* or null if no "Content-Type" header was defined in the response.
	* @see IResponseEntity#getContentType()
	*/
	public String getContentType() {
		return (contentType != null) ? contentType : "application/octet-stream";
	}
	
	/**
	* Returns a list of content type parameters.
	* The Content-Type header may list additional 
	* parameters like "charset" according to RFC2616.
	* @return A list of Pair instances, or null if the header
	* contained no parameters.
	* @see IResponseEntity#getContentTypeParams()
	*/	
	public List getContentTypeParams() {
		return contentTypeParams;
	}

	/**
	* Returns the MIME type of that entity.
	* This is the same as the content type but without additional parameters.
	* The default value is "application/octet-stream".
	* @return A MIME type specifier
	* or null if no "Content-Type" header was defined in the response.
	* @see IResponseEntity#getMimeType()
	*/
	public String getMimeType() {
		return (mimeType != null) ? mimeType : "application/octet-stream";
	}

	/**
	* Returns the unique id of that entity. Corresponds to the HTTP "ETag" entity header.
	* @return An entity tag, or null if no "ETag" header was defined
	* in the response.
	 * @see IResponseEntity#getEntityTag()
	 */
	public String getEntityTag() {
		return entityTag;
	}

	/**
	 * Returns the date of last modification of that entity. Corresponds to
	 * the HTTP "Last-Modified" entity header.
	 * @return The time of last modification, or null if no "Last-Modified" header was defined
	 * in the response.
	 * @see IResponseEntity#getLastModfied()
	 */
	public String getLastModfied() {
		return lastModified;
	}

	/**
	 * Initializes the entity from the specified response. The method
	 * tries to retrieve the "Content-Type", "Content-MD5",
	 * "ETag" and "Last-Modified" headers and the content type's "charset" parameter
	 * from the response.
	 * @param response the response from which this entity is initialized.
	 */
	public void initialize(IResponse response) {
		contentType = response.getContentType();
		if (contentType != null) {
			int n = contentType.indexOf(';');
			if (n != -1) {
				contentTypeParams = Tokenizer.partsOf(contentType, ";", n, '=');
				for (int i = 0; i < contentTypeParams.size(); ++i) {
					Pair param = (Pair)contentTypeParams.get(i);
					if ("charset".equalsIgnoreCase(param.getName())) {
						contentCharset = param.getValue();
					}
				}
			}
		}
		contentLength = response.getContentLength();
		contentMD5 = response.getHeaderValue("Content-MD5");
		entityTag = response.getHeaderValue("ETag");
		lastModified = response.getHeaderValue("Last-Modfied");
	}
}
