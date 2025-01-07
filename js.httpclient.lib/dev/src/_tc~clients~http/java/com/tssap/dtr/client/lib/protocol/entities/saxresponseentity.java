package com.tssap.dtr.client.lib.protocol.entities;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.tssap.dtr.client.lib.protocol.IResponseEntity;

/**
 * This abstract response entity is the base class of entities like MultiStatusEntity
 * defined in this package. It provides rudimentary implementations for the interfaces
 * IResponseEntity and ContentHandler.
 */
public abstract class SAXResponseEntity	extends ResponseEntityBase implements IResponseEntity, ContentHandler {

	/** string buffer for the characters method */
	private StringBuffer buf = new StringBuffer(64);

	public SAXResponseEntity() {
		super("text/xml");
	}


	// Empty implementation of interface ContentHandler. Requests derived from
	// this class usually should overwrite startElement and endElement.
	/**
	 * Receive a Locator object for document events.
	 *
	 * <p>By default, do nothing.  Application writers may override this
	 * method in a subclass if they wish to store the locator for use
	 * with other document events.</p>
	 *
	 * @param locator A locator for all SAX document events.
	 * @see org.xml.sax.ContentHandler#setDocumentLocator
	 * @see org.xml.sax.Locator
	 */
	public void setDocumentLocator(Locator locator) {
	}

	/**
	 * Receive notification of the beginning of the document.
	 *
	 * <p>By default, do nothing.  Application writers may override this
	 * method in a subclass to take specific actions at the beginning
	 * of a document (such as allocating the root node of a tree or
	 * creating an output file).</p>
	 *
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly
	 *            wrapping another exception.
	 * @see org.xml.sax.ContentHandler#startDocument
	 */
	public void startDocument() throws SAXException {
	}

	/**
	 * Receive notification of the end of the document.
	 *
	 * <p>By default, do nothing.  Application writers may override this
	 * method in a subclass to take specific actions at the end
	 * of a document (such as finalising a tree or closing an output
	 * file).</p>
	 *
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly
	 *            wrapping another exception.
	 * @see org.xml.sax.ContentHandler#endDocument
	 */
	public void endDocument() throws SAXException {
	}

	/**
	 * Receive notification of the start of a Namespace mapping.
	 *
	 * <p>By default, do nothing.  Application writers may override this
	 * method in a subclass to take specific actions at the start of
	 * each Namespace prefix scope (such as storing the prefix mapping).</p>
	 *
	 * @param prefix The Namespace prefix being declared.
	 * @param uri The Namespace URI mapped to the prefix.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly
	 *            wrapping another exception.
	 * @see org.xml.sax.ContentHandler#startPrefixMapping
	 */
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
	}

	/**
	 * Receive notification of the end of a Namespace mapping.
	 *
	 * <p>By default, do nothing.  Application writers may override this
	 * method in a subclass to take specific actions at the end of
	 * each prefix mapping.</p>
	 *
	 * @param prefix The Namespace prefix being declared.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly
	 *            wrapping another exception.
	 * @see org.xml.sax.ContentHandler#endPrefixMapping
	 */
	public void endPrefixMapping(String prefix) throws SAXException {
	}

	/**
	 * Receive notification of ignorable whitespace in element content.
	 *
	 * <p>By default, do nothing.  Application writers may override this
	 * method to take specific actions for each chunk of ignorable
	 * whitespace (such as adding data to a node or buffer, or printing
	 * it to a file).</p>
	 *
	 * @param ch The whitespace characters.
	 * @param start The start position in the character array.
	 * @param length The number of characters to use from the
	 *               character array.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly
	 *            wrapping another exception.
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace
	 */
	public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
	}

	/**
	 * Receive notification of a processing instruction.
	 *
	 * <p>By default, do nothing.  Application writers may override this
	 * method in a subclass to take specific actions for each
	 * processing instruction, such as setting status variables or
	 * invoking other methods.</p>
	 *
	 * @param target The processing instruction target.
	 * @param data The processing instruction data, or null if
	 *             none is supplied.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly
	 *            wrapping another exception.
	 * @see org.xml.sax.ContentHandler#processingInstruction
	 */
	public void processingInstruction(String target, String data) throws SAXException {
	}

	/**
	 * Receive notification of a skipped entity.
	 *
	 * <p>By default, do nothing.  Application writers may override this
	 * method in a subclass to take specific actions for each
	 * processing instruction, such as setting status variables or
	 * invoking other methods.</p>
	 *
	 * @param name The name of the skipped entity.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly
	 *            wrapping another exception.
	 * @see org.xml.sax.ContentHandler#processingInstruction
	 */

	public void skippedEntity(String name) throws SAXException {
	}

	/**
	 * Receive notification of the start of an element.
	 *
	 * <p>By default, do nothing.  Application writers may override this
	 * method in a subclass to take specific actions at the start of
	 * each element (such as allocating a new tree node or writing
	 * output to a file).</p>
	 *
	 * @param uri The Namespace URI, or the empty string if the
	 *        element has no Namespace URI or if Namespace
	 *        processing is not being performed.
	 * @param localName The local name (without prefix), or the
	 *        empty string if Namespace processing is not being
	 *        performed.
	 * @param qName The qualified name (with prefix), or the
	 *        empty string if qualified names are not available.
	 * @param attributes The attributes attached to the element.  If
	 *        there are no attributes, it shall be an empty
	 *        Attributes object.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly
	 *            wrapping another exception.
	 * @see org.xml.sax.ContentHandler#startElement
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes)
		throws SAXException {
	}

	/**
	 * Receive notification of the end of an element.
	 *
	 * <p>By default, do nothing.  Application writers may override this
	 * method in a subclass to take specific actions at the end of
	 * each element (such as finalising a tree node or writing
	 * output to a file).</p>
	 *
	 * @param uri The Namespace URI, or the empty string if the
	 *        element has no Namespace URI or if Namespace
	 *        processing is not being performed.
	 * @param localName The local name (without prefix), or the
	 *        empty string if Namespace processing is not being
	 *        performed.
	 * @param qName The qualified name (with prefix), or the
	 *        empty string if qualified names are not available.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly
	 *            wrapping another exception.
	 * @see org.xml.sax.ContentHandler#endElement
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException {
	}

	/**
	 * Receive notification of character data inside an element.
	 *
	 * <p>By default, do nothing.  Application writers may override this
	 * method to take specific actions for each chunk of character data
	 * (such as adding the data to a node or buffer, or printing it to
	 * a file).</p>
	 * <p>Since SAX splits consecutive lines of
	 * character input into multiple events,
	* this method collects the parts.</p>
	*
	 * @param chars The characters.
	 * @param start The start position in the character array.
	 * @param length The number of characters to use from the
	 *               character array.
	 * @exception org.xml.sax.SAXException Any SAX exception, possibly
	 *            wrapping another exception.
	 * @see org.xml.sax.ContentHandler#characters
	 */
	public void characters(char[] chars, int start, int length) {
		buf.append(chars, start, length);
	}

	/**
	 * Returns a previously recorded string value of a tag.
	 * The string buffer is cleared by this method.
	 * @return A tag's value, or null if the tag was empty.
	 */
	protected String getStringValue() {
		if (buf.length() > 0) {
			String s = buf.toString().trim();
			buf.setLength(0);
			int start = 0;
			int end = s.length();
			// OK: changed 3.10.2004 due to
			// internal CSN 3138272
			// Probably stripping down quote marks
			// might be done by the concrete protocols
			// if they cannot deal with it?
// OK <24.11.2004>: REMOVED
// Removed, since it is a show-stopper from CBS point of view
//			if (s.startsWith("\"") && s.endsWith("\"")) {
//				start++;
//				end--;
//			}
			return s.substring(start, end);
		} else {
			return null;
		}
	}

	/**
	 * Clears the internal buffer used to assemble
	 * string elements reported by the SAX parser.
	 */
	protected void clearBuffer() {
		buf.setLength(0);
	}

}
