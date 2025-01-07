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

package com.sap.engine.lib.converter.impl;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sap.engine.lib.converter.AbstractConverter;
import com.sap.engine.lib.converter.ConversionException;
import com.sap.engine.lib.converter.ParserWrapper;
import com.sap.engine.lib.converter.Validator;
import com.sap.engine.lib.converter.util.XercesUtil;

/**
 * Validator implementation on top of SAPXMLToolkit API
 * 
 * @author Petar Zhechev
 * @version 1.0, 2006-2-28 
 */
public class XMLToolkitValidator extends Validator {

  protected ParserWrapper validatingParser;
  protected ParserWrapper nonValidatingParser;
  protected ParserWrapper schemaValidatingParser;
  
  public XMLToolkitValidator() {
    try {
      resolver = new MultiResolver();
      validatingParser = new ParserWrapper(XercesUtil.getDocumentBuilder(XercesUtil.VALIDATING));
      validatingParser.setEntityResolver(resolver);
      schemaValidatingParser = new ParserWrapper(XercesUtil.getDocumentBuilder(XercesUtil.SCHEMA_VALIDATING));
      schemaValidatingParser.setEntityResolver(resolver);
      nonValidatingParser = new ParserWrapper(XercesUtil.getDocumentBuilder(XercesUtil.NON_VALIDATING));      
    } catch (ParserConfigurationException e) {
      AbstractConverter.LOCATION.catching(e);
      throw new RuntimeException(e);
    }
  }
    
  /* (non-Javadoc)
   * @see com.sap.engine.lib.converter.Validator#validate(javax.xml.transform.Source, java.lang.String, boolean)
   */
  public DOMResult validate(Source source, String descriptorName, boolean forgiving) throws ConversionException {
    StreamSource inputSource = null;    
    if (source instanceof DOMSource) {
      try {
        inputSource = XercesUtil.serialize((DOMSource) source);
      } catch (SAXException e) {
        AbstractConverter.throwConversionException(e, descriptorName);
      }
    } else if (source instanceof StreamSource) {
      inputSource = (StreamSource) source;      
    } else {
      throw new IllegalArgumentException(source.getClass().getName());
    }
    InputSource is = XercesUtil.toInputSource(inputSource.getInputStream()); 
    Document doc = forgiving ? schemaValidatingParser.parse(is, descriptorName) 
                             : validatingParser.parse(is, descriptorName);
    return new DOMResult(doc);
  }
  
  /* (non-Javadoc)
   * @see com.sap.engine.lib.converter.Validator#setEntityResolver(org.xml.sax.EntityResolver)
   */
  public void setEntityResolver(EntityResolver entityResolver) {
    if (entityResolver instanceof MultiResolver) {
      resolver = (MultiResolver) entityResolver;
      validatingParser.setEntityResolver(resolver);
      schemaValidatingParser.setEntityResolver(resolver);
    } else {
      resolver.addResolver(entityResolver);
    }
  }  
}