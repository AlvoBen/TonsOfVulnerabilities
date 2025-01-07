package com.sap.engine.lib.jaxp;

import javax.xml.transform.Result;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.TransformerHandler;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sap.engine.lib.xml.parser.NestedSAXParseException;
import com.sap.engine.lib.xml.util.SAXToDOMHandler;

/**
 * A TransformerHandler
 * listens for SAX ContentHandler parse events and transforms
 * them to a Result.
 */
public class TransformerHandlerImpl extends SAXToDOMHandler implements TransformerHandler { // extends ContentHandler, LexicalHandler, DTDHandler {

//  private Templates temaplate = null;
  private Result result = null;
  private String systemId = null;
  private Transformer transformer = null;

  protected TransformerHandlerImpl() throws TransformerConfigurationException {
    transformer = TransformerFactory.newInstance().newTransformer();
  }

  protected TransformerHandlerImpl(Templates templates) throws TransformerConfigurationException {
    transformer = templates.newTransformer();
  }

  /**
   * Enables the user of the TransformerHandler to set the
   * to set the Result for the transformation.
   *
   * @param result A Result instance, should not be null.
   *
   * @throws IllegalArgumentException if result is invalid for some reason.
   */
  public void setResult(Result result) throws IllegalArgumentException {
    if (result == null) {
      throw new IllegalArgumentException("Result is invalid: null");
    }
    this.result = result;
  }

  /**
   * Set the base ID (URI or system ID) from where relative
   * URLs will be resolved.
   * @param systemID Base URI for the source tree.
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

  /**
   * Get the Transformer associated with this handler, which
   * is needed in order to set parameters and output properties.
   */
  public Transformer getTransformer() {
    return transformer;
  }

  public void endDocument() throws SAXException {
    super.endDocument();
    try {
      if (result == null) {
        throw new SAXException("Cannot process transformation: result is null");
      }

      transformer.transform(new DOMSource((Document) getRoot(), systemId), result);
    } catch (TransformerException e) {
      throw new NestedSAXParseException("Could not process transformation", e);
    }
  }

}

