package com.sap.engine.lib.jaxp;

import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.dom.*;
import org.xml.sax.*;
import org.xml.sax.ext.*;
import org.w3c.dom.*;

import com.sap.engine.lib.xml.parser.NestedSAXParseException;
import com.sap.engine.lib.xml.util.SAXToDOMHandler;

/**
 * A SAX ContentHandler that may be used to process SAX
 * parse events (parsing transformation instructions) into a Templates object.
 *
 * <p>Note that TemplatesHandler does not need to implement LexicalHandler.</p>
 */
public class TemplatesHandlerImpl extends SAXToDOMHandler implements TemplatesHandler {

  private Templates template = null;
  private String systemId = null;

  protected TemplatesHandlerImpl() {

  }

  /**
   * When a TemplatesHandler object is used as a ContentHandler
   * for the parsing of transformation instructions, it creates a Templates object,
   * which the caller can get once the SAX events have been completed.
   *
   * @return The Templates object that was created during
   * the SAX event process, or null if no Templates object has
   * been created.
   *
   */
  public Templates getTemplates() {
    return template;
  }

  /**
   * Set the base ID (URI or system ID) for the Templates object
   * created by this builder.  This must be set in order to
   * resolve relative URIs in the stylesheet.  This must be
   * called before the startDocument event.
   *
   * @param baseID Base URI for this stylesheet.
   */
  public void setSystemId(String systemId) {
    this.systemId = systemId;
  }

  /**
   * Get the base ID (URI or system ID) from where relative
   * URLs will be resolved.
   * @return The systemID that was set with {@link #setSystemId}.
   */
  public String getSystemId() {
    return systemId;
  }

  //    public void setDocumentLocator (Locator locator) {
  //      sax2dom.setDocumentLocator(locator);
  //    }
  //
  //
  //    public void startDocument () throws SAXException {
  //      sax2dom.startDocument();
  //    }
  public void endDocument() throws SAXException {
    super.endDocument();
    try {
      template = TransformerFactory.newInstance().newTemplates(new DOMSource((Document) getRoot(), systemId));
    } catch (TransformerConfigurationException e) {
      throw new NestedSAXParseException("Could not create Templates from SAX Events", e);
    }
  }

  //    public void startPrefixMapping (String prefix, String uri) throws SAXException {
  //      sax2dom.startPrefixMapping(prefix, uri);
  //    }
  //
  //
  //    public void endPrefixMapping (String prefix) throws SAXException {
  //      sax2dom.endPrefixMapping(prefix);
  //    }
  //
  //
  //    public void startElement (String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
  //      sax2dom.startElement(namespaceURI, localName, qName, atts);
  //    }
  //
  //
  //    public void endElement (String namespaceURI, String localName, String qName) throws SAXException {
  //      sax2dom.endElement(namespaceURI, localName, qName);
  //    }
  //
  //
  //    public void characters (char ch[], int start, int length) throws SAXException {
  //      sax2dom.characters(ch, start, length);
  //    }
  //
  //
  //    public void ignorableWhitespace (char ch[], int start, int length) throws SAXException {
  //      sax2dom.ignorableWhitespace(ch, start, length);
  //    }
  //
  //
  //    public void processingInstruction (String target, String data) throws SAXException {
  //      sax2dom.processingInstruction(target, data);
  //    }
  //
  //
  //    public void skippedEntity (String name) throws SAXException {
  //      sax2dom.skippedEntity(name);
  //    }

}

