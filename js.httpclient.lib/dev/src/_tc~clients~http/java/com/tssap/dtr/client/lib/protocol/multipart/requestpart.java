package com.tssap.dtr.client.lib.protocol.multipart;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;

/**
 * This class implements a generic multipart MIME request part.
 * Methods are provided to define additional headers
 * and the body of the part.
 */
public class RequestPart implements IRequestPart
{
	private static final String CRLF = "\r\n";
	private Map headers;
	private IRequestEntity entity;
	
	/**
	 * The MIME type of the request entity, e.g. "text/xml". May include
	 * additional parameters like "charset". Corresponds to the
	 * HTTP "Content-Type" header.
	 */
	private String contentType;
	
	/**
	 * The content ID of the part
	 */	
	private String contentID;

	/**
	 * The length of the request body in bytes. Corresponds to the
	 * HTTP "Content-Length" header. If <code>contentLength</code> is -1 the
	 * length of the body is unknown and must be sent in "chunked"
	 * transfer encoding.
	 */
	private long contentLength = -1L;	
	
	/** */
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	
	/** */
	public static final String CONTENT_DESCRIPTOR  = "Content-Descriptor";
	
	/** */
	public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
	
	/** */	
	public static final String CONTENT_ID = "Content-ID";


	/**
	 * Create a new part with the given content ID (see RFC ..) and entity.
	 * @param contentID  a string that identifies the part uniquely within a multipart
	 * request.
	 * @paramentity  an entity that defines the actual content of part
	 */
	public RequestPart(String contentID, IRequestEntity entity) {
		this.entity = entity;
		this.contentID = contentID;
		
		this.contentLength = entity.getContentLength();		
		if (contentLength > 0) {
			setHeader(Header.HTTP.CONTENT_LENGTH, Long.toString(contentLength));
		}

		this.contentType = entity.getContentType();
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		setHeader(Header.HTTP.CONTENT_TYPE, contentType);

		if (entity.getContentMD5() != null) {
			setHeader(Header.HTTP.CONTENT_MD5, entity.getContentMD5());
		}		
		setHeader(CONTENT_ID, contentID);
	}


	/**
	* Returns the content length of the request body.
	* @return The content length in bytes.
	 * @see IRequest#getContentLength()
	 */
	public long getContentLength() {
		return contentLength;
	}

	/**
	* Returns the content (MIME) type of the request body.
	* @return A MIME content type specifier.
	 * @see IRequest#getContentType()
	 */
	public String getContentType() {
		return contentType;
	}
	
	/**
	 * Returns the content ID of this part.
	 */
	public String getContentID() {
		return contentID;		
	}

	/**
	 * Returns the entity of this part.
	 * @return the entity
	 */
	public IRequestEntity getEntity() {
		return entity;
	}

	/**
	 * Returns the names of the headers defined by this part.
	 * @return An enumeration of strings representing header names.
	 */
	public Iterator getHeaderNames() {
		if (headers == null) {
			return null;
		} else {
			return new Iterator() {
				private Iterator iter = headers.keySet().iterator();
				public boolean hasNext() {
					return iter.hasNext();
				}
				public Object next() {
					Header header = (Header) headers.get(iter.next());
					if (header != null) {
						return header.getName();
					}
					return null;
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	/**
	 * Returns the specified header.
	 * Note, header names are not case-sensitive.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @return The specified header, of null if the header does not exist.
	 */
	public Header getHeader(String name) {
		if (headers == null || name == null || name.length() == 0)
			return null;
		return (Header) headers.get(name.toLowerCase());
	}

	/**
	 * Returns the value of the specified request header.
	 * Note, header names are not case-sensitive.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @return The value of the specified header, of null if the header does not exist.
	 */
	public String getHeaderValue(String name) {
		if (headers == null || name == null || name.length() == 0)
			return null;
		Header header = (Header) headers.get(name.toLowerCase());
		return (header != null) ? header.getValue() : null;
	}

	/**
	 * Returns the value of the specified request header, or a
	 * default value if the header is not defined.
	 * Note, header names are not case-sensitive.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @param defaultValue  the value that should be returned if the header is not
	 * defined for this part.
	 * @return The value of the specified header, or <code>defaultValue</code>.
	 */
	public String getHeaderValue(String name, String defaultValue) {
		String value = getHeaderValue(name);
		return (value != null) ? value : defaultValue;
	}

	/**
	 * Defines a new or changes the value of an existing header.
	 * @param header  a header from which name and value are taken to define
	 * or change a header of this request.
	 */
	public void setHeader(Header header) {
		String name = header.getName();
		if (name != null && name.length() > 0) {
			if (headers == null) {
				headers = new HashMap();
			}
			headers.put(name.toLowerCase(), header);
		}
	}

	/**
	 * Defines a new or changes the value of an existing header.
	 * @param header  a header from which name and value are taken to define
	 * or change a header of this request.
	 * @param append if true, the value of the new header is appended to an already defined
	 * header of the same name, instead of replacing the old value. The value is appended
	 * with a comma as separator.
	 */
	public void setHeader(Header header, boolean append) {
		String name = header.getName();
		if (name != null && name.length() > 0) {
			if (!append || headers == null) {
				setHeader(header);
			} else {
				Header existingHeader = (Header) headers.get(name.toLowerCase());
				if (existingHeader == null) {
					setHeader(header);
				} else {
					existingHeader.appendPart(header.getValue(), ", ");
				}
			}
		}
	}

	/**
	 * Defines a new or changes the value of an existing header.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @param value the value of this header.
	 */
	public void setHeader(String name, String value) {
		if (name != null || name.length() > 0) {
			setHeader(new Header(name, value));
		}
	}

	/**
	 * Defines a new or changes the value of an existing header.
	 * @param name the name of a standard HTTP header or a client-defined header.
	 * @param value the value of this header.
	 * @param append if true, the value of the new header is appended to an already defined
	 * header of the same name, instead of replacing the old value. The value is appended
	 * with a comma as separator.
	 */
	public void setHeader(String name, String value, boolean append) {
		if (name != null || name.length() > 0) {
			setHeader(new Header(name, value), append);
		}
	}

	/**
	 * Defines a set of headers from the given array.
	 * @param headerList an array of Header objects.
	 */
	public void setHeaders(Header[] headerList) {
		if (headers == null) {
			headers = new HashMap();
		}
		for (int i = 0; i < headerList.length; ++i) {
			setHeader(headerList[i]);
		}
	}

	/**
	* Removes the header with the specified name. If the header does not
	* exist, nothing happens.
	* @param name the name of a standard HTTP header or a client-defined header.
	 */
	public void removeHeader(String name) {
		if (name != null && name.length() > 0) {
			if (headers != null) {
				headers.remove(name.toLowerCase());
			}
		}
	}

	/**
	 * Removes all headers from the part.
	 */
	public void removeAllHeaders() {
		if (headers != null) {
			headers.clear();
		}
	}
	
	
	void appendHeader(StringBuffer sb) {	
		Iterator iter = getHeaderNames();
		if (iter != null) {
			String name;
			String value;
			while (iter.hasNext()) {
				name = (String) iter.next();
				value = getHeaderValue(name);
				if (value != null && value.length() > 0) {
					sb.append(name).append(": ").append(value).append(CRLF);
				}		
			}
		}		
	}	

}
