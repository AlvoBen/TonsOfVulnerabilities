package com.tssap.dtr.client.lib.protocol.requests;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import com.tssap.dtr.client.lib.protocol.HTTPException;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;
import com.tssap.dtr.client.lib.protocol.IResponseParser;
import com.tssap.dtr.client.lib.protocol.Status;
import com.tssap.dtr.client.lib.protocol.entities.ErrorEntity;
import com.tssap.dtr.client.lib.protocol.entities.MultiStatusEntity;
import com.tssap.dtr.client.lib.protocol.entities.SAXResponseEntity;

/**
 * This request class is the base class of most of the DAV and DeltaV request classes
 * defined in this package. XMLRequest provides a SAX parser and implements
 * the interface for response parsers (IResponseParser).
 */
public class XMLRequest extends RequestBase implements IResponseParser {

	public static final String ENCODING = "utf-8";
	
	/** The SAX parser used to parse XML response */
	private XMLReader saxParser;

	/**
	 * Creates a request with specified method and relative path.
	 * @param method the protocol command, e.g. "GET", "CHECKIN" etc.
	 * @param path the URL of a resource to which the request applies.
	 */
	public XMLRequest(String method, String path) {
		super(method, path);
		setDefaultParser(this);
	}

	/**
	 * Creates a request with specified method and relative path and defines
	 * a request entity.
	 * @param method the protocol command, e.g. "GET", "CHECKIN" etc.
	 * @param path the URL of a resource to which the request applies.
	 * @param entity a request entity providing the request body and some
	 * parameters like content length and type.
	 */
	public XMLRequest(String method, String path, IRequestEntity entity) {
		super(method, path, entity);
		setDefaultParser(this);
	}

	/**
	 * Prepares the request object for reuse.
	 * This methods call RequestBase.clear()
	 * but then sets itself again as default parser. The SAX parser used
	 * internally by this object is not removed.
	 */
	public void clear() {
		super.clear();
		setDefaultParser(this);
	}


	/**
	 * Parses the content of the given response.
	 * Instantiates a response entity by calling createResponseEntity,
	 * instantiates a SAX parser and calls its parse method.
	 * If any error occured during this process the method returns null.
		 * @param path the URL of a resource to which the request applies.
	 * @param response  the response object from which the body
	 * is to be retrieved and parsed.
	 * @return A response entity that provides access to the parsed
	 * response body.
	 * @throws HTTPException  if the parsing of the response body failed.
	 * @see IResponseParser#parse(String,IResponse)
	 */
	public IResponseEntity parse(String path, IResponse response) throws HTTPException {
		SAXResponseEntity entity = createResponseEntity(path, response);
		if (entity != null) {
			try {
				XMLReader parser = createXMLReader();
				if (parser != null) {
					parser.setContentHandler(entity);
					InputSource s = new InputSource(response.getStream());
					parser.parse(s);
				}
			} catch (Exception ex) {
				throw new HTTPException("Parsing of the response body failed", ex);
			}
		}
		return (IResponseEntity) entity;
	}

	/**
	 * Factory method for response entity.
	 * Request types derived from this
	 * class should overwrite this method to instantiate specialized
	 * response entities. This method checks whether the status code indicates
	 * an error situation and if there is a XML body. In this case
	 * an <code>ErrorEntity</code> is returned. Otherwise null is returned.
	 * Derived classes
	 * should call this method first to handle such error situations before
	 * applying their own parser.
		 * @param path the URL of a resource to which the request was applied.
	 * @param response  the response object from which the body
	 * is to be retrieved and parsed.
	 * @return A suitable, but uninitialized response entity
	 */
	protected SAXResponseEntity createResponseEntity(String path, IResponse response) {
		SAXResponseEntity entity = null;
		if (response.isContentXML()) {
			int responseStatus = response.getStatus();
			if (responseStatus >= Status.BAD_REQUEST
				&& responseStatus < Status.INTERNAL_SERVER_ERROR) {
				entity = new ErrorEntity(response);
			} else if (responseStatus == Status.MULTI_STATUS) {
				entity = new MultiStatusEntity(path, response);
			}
		}
		return entity;
	}

	/**
	 * Creates a SAX parser instance.
	 */
	private XMLReader createXMLReader() {
		if (saxParser == null) {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
				saxParser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
				saxParser.setFeature("http://xml.org/sax/features/namespaces", true);
			} catch (SAXNotRecognizedException ex) {
				throw new ParserException("VFS WebDAV/DeltaV handler: Selected XML parser does not support namespace handling", ex);
			} catch (SAXNotSupportedException ex) {
				throw new ParserException("VFS WebDAV/DeltaV handler: Selected XML parser does not support namespace handling", ex);
			} catch (SAXException ex) {
				throw new ParserException("VFS WebDAV/DeltaV handler: Unable to find or instantiate XML parser", ex);
			} catch (FactoryConfigurationError ex) { //$JL-EXC$
				throw new ParserException("VFS WebDAV/DeltaV handler: Failed to instantiate XML parser factory", ex);
			} catch (ParserConfigurationException ex) {
				throw new ParserException("VFS WebDAV/DeltaV handler: Unable to find or instantiate XML parser", ex);
			} finally {
				Thread.currentThread().setContextClassLoader(cl);
			}
		}
		return saxParser;
	}
}
