package com.tssap.dtr.client.lib.protocol.multipart;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.sap.tc.logging.Location;
import com.tssap.dtr.client.lib.protocol.HTTPException;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.IRequestStream;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;
import com.tssap.dtr.client.lib.protocol.entities.ResponseEntityBase;
import com.tssap.dtr.client.lib.protocol.streams.PartitionInputStream;
import com.tssap.dtr.client.lib.protocol.util.Encoder;
import com.tssap.dtr.client.lib.protocol.util.GUID;
import com.tssap.dtr.client.lib.protocol.util.Pair;


/**
 * This class implements a request/response entity supporting
 * sending and receiving multipart MIME requests.
 */
public class MultiPartEntity extends ResponseEntityBase implements IRequestEntity, IResponseEntity {

	/** 
	 * This multipart subtype is intended for compound objects consisting
	 * of several related body parts, e.g. the combination of an index
	 * and a set of images, or a SOAP request with attachements.
	 */	
	public static final String MULTIPART_RELATED = "multipart/related";
	
	/** 
	 * This multipart subtype is intended for use when the body parts
	 * are independent but need to be bundled in particular order. 
	 */
	public static final String MULTIPART_MIXED = "multipart/mixed";
	
	/** 
	 * This multipart subtype is intended for use when the body parts
	 * are alternative versions of the same information, e.g. two versions
	 * of a text, one in a fancy format, the other as plain text.
	 */
	public static final String MULTIPART_ALTERNATIVE = "multipart/alternative";
	
	
		
	private static final String CRLF = "\r\n";
	private static final byte[] FINIS = "--".getBytes();
		
	/** the boundary delimiter between parts */
	private byte[] boundary;
	private String boundaryStr;
	
	/** the multipart MIME type of this entity */
	private String multipartType;
	
	/** the start parameter in Content-Type header */
	private String start;
	/** the type parameter in Content-Type header */
	private String type;
		
	/** list of IRequestEntity providing the parts */
	private List requestParts;
	
	private Map responseParts;
	private List contentIDs;
	
	/** list of byte[] containing the headers of the parts */ 
	private List headers;
	
	/** if true, the entity supports request repetitions */
	private boolean supportsReset = true;
	
	/** trace location*/
	private static Location TRACE = Location.getLocation(MultiPartEntity.class);	


	/**
	 * Creates a new multipart entity with the given multipart type.
	 * Uses a default boundary delimiter in the form 
	 * "-----PART." + a GUID that is unlikely to occur in any
	 * given body part.
	 * @param multipartType   either of the predefined constants 
	 * <code>MULTIPART_RELATED</code>,
	 * <code>MULTIPART_MIXED</code>, or <code>MULTIPART_ALTERNATIVE</code>. 
	 */
	public MultiPartEntity(String multipartType) {
		super(multipartType);
		this.multipartType = multipartType;
		this.boundaryStr = getBoundary();
		this.boundary = getBoundary(boundaryStr);
		this.contentType = _getContentType();
	}
	
	/**
	 * Creates a new multipart entity with the given multipart type.
	 * Uses a default boundary delimiter in the form 
	 * "-----PART." + a GUID that is unlikely to occur in any
	 * given body part. 
	 * @param multipartType   either of the predefined constants 
	 * <code>MULTIPART_RELATED</code>,
	 * <code>MULTIPART_MIXED</code>, or <code>MULTIPART_ALTERNATIVE</code>.
	 * @param startID  the contentID of the "root" part. The "root" is the
	 * first part to be processed by a client/server. If not present, the
	 * first body part is taken as "root".
	 * @param startType  the MIME media type of the "root" part. This
	 * parameter is mandatory for <code>MULTIPART_RELATED</code> requests.
	 */	
	public MultiPartEntity(String multipartType, String startID, String startType) {
		super(multipartType);
		this.multipartType = multipartType;
		this.boundaryStr = getBoundary();
		this.boundary = getBoundary(boundaryStr);
		this.start = startID;
		this.type = startType;
		this.contentType = _getContentType();	
	}	
	
	/**
	 * Creates a new multipart entity with the given multipart type.
	 * Uses the given boundary delimiter. Note, make sure that this
	 * delimiter does not occur inside one of the body parts, or
	 * at least that this is very unlikely.
	 * @param multipartType   either of the predefined constants 
	 * <code>MULTIPART_RELATED</code>,
	 * <code>MULTIPART_MIXED</code>, or <code>MULTIPART_ALTERNATIVE</code>.
	 * @param boundary  the delimiter to be inserted between consecutive body parts.
	 */	
	public MultiPartEntity(String multipartType, String boundary) {
		super(multipartType);
		this.multipartType = multipartType;
		this.boundary = getBoundary(boundary);	
		this.boundaryStr = boundary;
		this.contentType = _getContentType();
	}	
	
	/**
	 * Creates a new multipart entity with the given multipart type.
	 * Uses the given boundary delimiter. Note, make sure that this
	 * delimiter does not occur inside one of the body parts, or
	 * at least that this is very unlikely.
	 * @param multipartType   either of the predefined constants 
	 * <code>MULTIPART_RELATED</code>,
	 * <code>MULTIPART_MIXED</code>, or <code>MULTIPART_ALTERNATIVE</code>.
	 * @param boundary  the delimiter to be inserted between consecutive body parts.
	 * @param startID  the contentID of the "root" part. The "root" is the
	 * first part to be processed by a client/server. If not present, the
	 * first body part is taken as "root".
	 * @param startType  the MIME media type of the "root" part. This
	 * parameter is mandatory for <code>MULTIPART_RELATED</code> requests. 
	 */		
	public MultiPartEntity(String multipartType, String boundary, String startID, String startType) {
		super(multipartType);
		this.multipartType = multipartType;
		this.boundary = getBoundary(boundary);	
		this.boundaryStr = boundary;
		this.start = startID;
		this.type = startType;
		this.contentType = _getContentType();		
	}
	
	
	/**
	 * Creates a new multipart entity from the specified response.
	 * Reads the content of this entity from the response stream.
	 * @param listener  the part listener that is able to parse the individual parts of
	 * the multipart response
	 * @param response  the response object that porvides the new body of the response
	 * @throws HTTPException - if the response is malformed, invalid or incomplete.
	 */
	public MultiPartEntity(IResponse response, IPartParser parser) throws HTTPException 
	{
		super(response);
		
		for (int i=0; i<contentTypeParams.size(); ++i) {
			Pair param = (Pair)contentTypeParams.get(i);
			String pname = param.getName();
			if ("boundary".equalsIgnoreCase(pname)) {
				boundaryStr = param.getValue();
				boundary = getBoundary(boundaryStr);			
			} else if ("start".equalsIgnoreCase(pname)) {
				start = param.getValue();
			} else if ("type".equalsIgnoreCase(pname)) {
				type = param.getValue();
			}  
		}			
		
		try {			
			PartitionInputStream in = new PartitionInputStream(response.getContent());
			
			// skip anything before the first occurence of boundary
			in.beginPart(boundary);
			in.skipContent();
			in.endPart(false);
			
			// read the parts
			responseParts = new HashMap();
			contentIDs = new ArrayList();
			ResponsePart part = new ResponsePart(boundary);
			
			while (part.initialize(in)) {
				long contentLength = part.getContentLength();
				if (contentLength > 0) {
					in.beginPart(boundary, contentLength);
				} else if (!parser.isSelfDelimiting(part)) {
					in.beginPart(boundary);
				}
								
				IResponseEntity entity = parser.parse(part);
				part.setEntity(entity);
				part.releaseStream();
				
				String contentID = part.getContentID();
				responseParts.put(contentID, part);
				contentIDs.add(contentID);
				
				part = new ResponsePart(boundary);
			}
			
			// skip anything after the final boundary
			in.skipContent();

		} catch (IOException ex) {
			contentType = null;
			throw new HTTPException("Unable to parse multipart response.", ex);
		}
	}
		

	/**
	 * The entity type string for MultiPartEntity.
	 */
	public static final String ENTITY_TYPE = "MultiPartEntity";
	
	/**
	 * Checks whether the given response entity is a MultiPartEntity.
	 * @return true, if the entity is a MultiPartEntity.
	 */
	public static boolean isMultiPartEntity(IResponseEntity entity) {
		return ENTITY_TYPE.equals(entity.getEntityType());
	}
	
	/**
	 * Returns the given response entity as MultiPartEntity.
	 * @return the entity casted to MultiPartEntity, or null
	 * if the entity cannot be converted to MultiPartEntity
	 */
	public static MultiPartEntity valueOf(IResponseEntity entity) {
		return (isMultiPartEntity(entity))? ((MultiPartEntity)entity) : null;		
	}

	/**
	 * Returns the type of this entity.
	 * @return "MultiPartEntity".
	 * @see IResponseEntity#getEntityType()
	 */
	public String getEntityType() {
		return ENTITY_TYPE;
	}


	/**
	 * Returns the MIME multipart type of that entity. Corresponds to
	 * the HTTP "Content-Type" entity header. Includes additional
	 * parameters indicating the boundary and the content ID and 
	 * content type of the first part.
	 * @return A MIME multipart type specifier according to RFC2046
	 */
	public String getContentType() {
		if (contentType == null) {
			contentType = _getContentType();
		}
		return contentType;
	}
	
	
	/**
	 * Attaches the given request entity as part to this entity.
	 * @param contentID   the content ID for the part
	 * @param entity   the entity representing the part's content
	 */
	public void addRequestPart(String contentID, IRequestEntity entity) {
		addRequestPart(new RequestPart(contentID, entity));			
	}

	/**
	 * Attaches the given part to this entity.
	 * @param part   the part to add
	 */
	public void addRequestPart(IRequestPart part) {	
		if (requestParts == null) {
			requestParts = new ArrayList();
			headers = new ArrayList();
			contentIDs = new ArrayList();
			contentLength = boundary.length + FINIS.length; // final boundary
		}
		requestParts.add(part);		
		contentIDs.add(part.getContentID());		
		
		byte[] header = getPartHeader(part);		
		headers.add(header);
				
		long partContentLength = part.getContentLength();				
		if (contentLength >= 0) {
			if (partContentLength < 0) {
				contentLength = -1L;
			} else {
				// format of a part: boundary CRLF header CRLF body
				contentLength += boundary.length + CRLF.length() + header.length + CRLF.length() + partContentLength;
			}					
		}
		
		supportsReset &= part.getEntity().supportsReset();
	}
	
	/**
	 * Attaches the given parts to this entity.
	 * @param parts   a list of parts to add
	 */
	public void addRequestParts(IRequestPart[] parts) {
		for (int i=0; i<parts.length; ++i) {
			addRequestPart(parts[i]);	
		}		
	}		
	
	
	/**
	 * Returns the number of parts found in the response body.
	 * @return the number of parts.
	 */
	public int countResponseParts() {
		return contentIDs.size();
	}
	
	/**
	 * Returns the list of content IDs in the order
	 * they were found in the response.
	 * @return a list of content ID strings.
	 */
	public List getContentIDs() {
		return contentIDs;
	}
	
	/**
	 * Returns the response part with the given sequence number.
	 * @param index  the position of a part in the response body
	 * (starting with 0)
	 * @return  the corresponding part, or null if no such part
	 * exists.
	 */
	public IResponsePart getResponsePart(int index) {
		IResponsePart result = null;
		if (index < contentIDs.size() ) {
			String contentID = (String)contentIDs.get(index);
			result = (IResponsePart)responseParts.get(contentID);
		}
		return result;
	}	
	
	/**
	 * Returns the response part matching the given content ID.
	 * @param contentID  the content ID to search for
	 * @return  the matching part, or null if no such part
	 * exists.
	 */
	public IResponsePart getResponsePart(String contentID) {
		return (IResponsePart)responseParts.get(contentID);
	}
	
	/**
	 * Returns an iterator over all response parts in the
	 * order they were found in the response body.
	 * @return an iterator of IResponsePart
	 */
	public Iterator getResponseParts() {
		return new Iterator() {
			private int i = 0;
			public boolean hasNext() {
				return i < contentIDs.size();
			}
			public Object next() {
				if (i < contentIDs.size()) {
					String contentID = (String)contentIDs.get(i);
					++i; 		
					return responseParts.get(contentID);
				}
				throw new NoSuchElementException(); 
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	
	/**
	 * Writes the content of this entity to the specified stream.
	 * @param destination - the stream to which the content is written.
	 * @throws IOException - if an i/o error occurs
	 * @see IRequestEntity#write(IRequestStream)
	 */	
	public void write(IRequestStream destination) throws IOException {
		if (requestParts != null  &&  requestParts.size() > 0) {
			int i = 0;
			Iterator iter = requestParts.iterator();
			while (iter.hasNext()) {
				destination.write(boundary);
				destination.write(CRLF.getBytes());
				IRequestPart part = (IRequestPart)iter.next();				
				destination.write( (byte[])headers.get(i) );
				destination.write(CRLF.getBytes());								
				part.getEntity().write(destination);	
				++i;			
			}
			destination.write(boundary);
			destination.write(FINIS);
		}		
	}
	
	/**
	 * Prepares the entity for a repetition of a request.
	 * This method calls the corresponding method of each
	 * attached part.
	 * @see IRequestEntity#reset()
	 */	
	public void reset() throws IOException {
		if (!supportsReset) {
			throw new IOException("Entity does not support reset.");
		}
		if (requestParts != null  &&  requestParts.size() > 0) 
		{			
			Iterator iter = requestParts.iterator();
			while (iter.hasNext()) {
				IRequestPart part = (IRequestPart)iter.next();
				part.getEntity().reset();
			}
		}		
		
	}

	/**
	 * Checks whether this entity supports request repetition.
	 * This method returns true only if all parts support request repetition.
	 * @return true, if the entity supports request repetition.
	 */
	public boolean supportsReset() {
		return supportsReset;
	}


	/**
	 * Returns the header of the given part in a format usable
	 * for writing to the socket.
	 * @param part  the part
	 * @return a byte array containg the header
	 */
	private byte[] getPartHeader(IRequestPart part) {
		StringBuffer sb = new StringBuffer();
		Iterator iter = part.getHeaderNames();
		if (headers != null) {
			while (iter.hasNext()) {
				String name = (String) iter.next();
				String value = part.getHeaderValue(name);
				if (value != null && value.length() > 0) {
					sb.append(name).append(": ").append(value).append(CRLF);
				}		
			}
		}		

		try {
			return Encoder.getBytes(sb.toString(), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Java Runtime does not support ISO-8859-1 encoding");
		}
	}

	/**
	 * Converts the given boundary to a byte array, or creates a default one.
	 * If <code>boundary = null</code> the method creates a boundary in
	 * the form <code>"-----PART." + getGUID()</code>.
	 * @param boundary  the boundary to convert, or null.
	 * @return  the boundary
	 */
	private byte[] getBoundary(String boundary) {
		try {
			if (boundary == null) {
				return Encoder.getBytes(getBoundary(), "ISO-8859-1");
			} else {
				return Encoder.getBytes(boundary, "ISO-8859-1");
			}
		}  catch (UnsupportedEncodingException e) {
			TRACE.catching("getBoundary(String)", e);
			return new byte[]{ 0x2D,0x2D,0x2D,0x2D,0x2D,0x50,0x41,0x52,0x54};
		}
	}
	
	private String getBoundary() {
		return "-----PART." + getGUID();
	}	
	
	/**
	 * Returns a GUID.
	 */
	private String getGUID() {
		return new GUID().toString();
	}
	
	
	private String _getContentType() {
		StringBuffer sb = new StringBuffer(multipartType);

		sb.append("; boundary=").append(boundaryStr);
		if (start != null) {
			sb.append("; start=\"").append(start).append("\"");
		}
		if (type != null) {
			sb.append("; type=\"").append(type).append("\"");
		}
//		if (startInfo != null) {
//			sb.append("; start-info=\"").append(startInfo).append("\"");				
//		}
		return sb.toString();		
	}
	
		

}
