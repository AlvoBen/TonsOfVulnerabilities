/*
 * Copyright (c) 2006 by SAP AG, Walldorf. http://www.sap.com All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG,
 * Walldorf. You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered
 * into with SAP.
 * 
 * $Id$
 */

package com.sap.engine.lib.converter;

import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;

import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import com.sap.engine.lib.converter.impl.ConnectorConverter;
import com.sap.engine.lib.converter.impl.MultiResolver;

/**
 * XML dynamic validation abstraction.
 * <p>
 * Provides means for checking XML documents against schema or DTD or checking
 * against schema only.
 * 
 * @author Petar Zhechev
 * @version 1.0, 2006-1-24
 */
public abstract class Validator {
  
  protected MultiResolver resolver;
  
  protected Validator() {}

  /**
   * Validates a XML represented as <code>Source</code>.
   * The validation is performed dynamically against DTD or Schema.
   *
   * @param descriptorName j2ee descriptor file name
   * @see ConnectorConverter.CONNECTOR_FILENAME
   * 
   * @param source
   *          XML document source to be validated, <code>SAXSource</code> is 
   *          not supported
   * @see javax.xml.transform.dom.DOMSource
   * @see javax.xml.transform.stream.StreamSource
   * @throws SAXException
   *           if a parsing error occurs
   *           
   * @throws IOException
   *           if an I/O error occurs
   * @exception IllegalArgumentException of the passed <code>Source</code>
   * is not DOMSource or InputSource           
   */
  public DOMResult validate(Source source, String descriptorName)
      throws ConversionException {
    return validate(source, descriptorName, false);
  }

  /**
   * Validates a XML provided as <code>Source</code>.
   * The validation is performed depending on the client specified forgiving behaviour. 
   * If forgiving is set to true the document will be validated against a schema only, 
   * otherwise the document will be dynamically validated against schema or DTD.
   * 
   * @param source
   *          XML document source to be validated, <code>SAXSource</code> is 
   *          not supported
   * @see javax.xml.transform.dom.DOMSource
   * @see javax.xml.transform.stream.StreamSource
   * 
   * @param descriptorName j2ee descriptor file name
   * @see ConnectorConverter.CONNECTOR_FILENAME
   * 
   * @param forgiving
   *          if set to true the document will be validated against schema only,
   *          otherwise it will be dynamically validated against schema or DTD.
   * 
   * @throws SAXException
   *           if a parsing error occurs
   * @throws IOException
   *           if an I/O error occurs
   * @exception IllegalArgumentException of the passed <code>Source</code>
   * is not DOMSource or InputSource           
   */
  public abstract DOMResult validate(Source source, String descriptorName,
      boolean forgiving) throws ConversionException;

  /**
   * Allows clients to specify entity resolver for retrieving external entities
   * 
   * @param entityResolver
   */
  public abstract void setEntityResolver(EntityResolver entityResolver);
}
