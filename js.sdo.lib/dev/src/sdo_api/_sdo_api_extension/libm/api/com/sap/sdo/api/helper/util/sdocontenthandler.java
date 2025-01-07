package com.sap.sdo.api.helper.util;

import org.xml.sax.ContentHandler;

import commonj.sdo.helper.XMLDocument;

/**
 * The SDOContentHandler is a ContentHandler that receives SAX events and
 * creates an SDO data graph.
 * @see commonj.sdo.helper.XMLHelper#createContentHandler(Object)
 */
public interface SDOContentHandler extends ContentHandler {

    /**
     * Returns the result XMLDocument.
     * @return The XMLDocument.
     */
    XMLDocument getDocument();
}
