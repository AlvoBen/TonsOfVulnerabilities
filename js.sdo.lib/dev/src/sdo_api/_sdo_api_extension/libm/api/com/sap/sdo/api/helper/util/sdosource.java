package com.sap.sdo.api.helper.util;

import javax.xml.transform.Source;

import commonj.sdo.helper.XMLDocument;

/**
 * This interface represents an SDO data graph as javax.xml.transform.Source.
 * The implementation of this class may extend javax.xml.transform.sax.SAXSource
 * or another well supported Source-implementation.
 * @see commonj.sdo.helper.XMLHelper#createSDOSource(XMLDocument, Object)
 */
public interface SDOSource extends Source {

    /**
     * Returns the source XMLDocument.
     * @return The source XMLDocument.
     */
    XMLDocument getDocument();
    
    /**
     * Returns implementation specific options that were passed to the 
     * factory method.
     * @return The options.
     * @see commonj.sdo.helper.XMLHelper#createSDOSource(XMLDocument, Object)
     */
    Object getOptions();

}
