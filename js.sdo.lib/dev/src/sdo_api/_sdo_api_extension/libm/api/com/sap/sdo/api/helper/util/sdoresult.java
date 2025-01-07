package com.sap.sdo.api.helper.util;

import javax.xml.transform.Result;

import commonj.sdo.helper.XMLDocument;

/**
 * With this interface frameworks are able to create an SDO data graph as a
 * javax.xml.transform.Result.
 * The implementation of this class may extend javax.xml.transform.sax.SAXResult
 * or another well supported Result-implementation.
 * @see commonj.sdo.helper.XMLHelper#createSDOResult(Object)
 */
public interface SDOResult extends Result {

    /**
     * Returns the result XMLDocument after the SDOResult is completed.
     * @return The result XMLDocument.
     */
    XMLDocument getDocument();
    
    /**
     * Returns implementation specific options that were passed to the 
     * factory method.
     * @return The options.
     * @see commonj.sdo.helper.XMLHelper#createSDOResult(Object)
     */
    Object getOptions();

}
