/*
 * Copyright (c) 2004-2006 by SAP AG, Walldorf., http://www.sap.com All rights
 * reserved.
 *
 * This software is the confidential and proprietary information of SAP AG,
 * Walldorf. You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered
 * into with SAP.
 */
package com.sap.engine.lib.converter.impl;

import com.sap.engine.lib.converter.AbstractConverter;
import com.sap.engine.lib.converter.ConversionContext;
import com.sap.engine.lib.converter.ConversionException;
import com.sap.engine.lib.converter.TransformerWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerConfigurationException;

/**
 * @author Violeta Georgieva
 * @version 7.10
 */
public class TldConverter extends AbstractConverter {

  public static final String TLD_FILENAME = ".tld";
  public static final String WEB_JSP_TAG_LIBRARY_TO_2_0_XSL = XSL_ROOT + "web-jsptaglibrary_2_0.xsl";

  protected TransformerWrapper tldTransformer;

  public TldConverter() {
    ClassLoader loader = TldConverter.class.getClassLoader();
    try {
      tldTransformer = createTransformer(loader.getResourceAsStream(WEB_JSP_TAG_LIBRARY_TO_2_0_XSL));
    } catch (TransformerConfigurationException e) {
      throw new RuntimeException(e);
    }
  }//end of constructor

  /*
   * @see com.sap.appchecker.lib.descriptors.IJ2EEDescriptorConverter#getType()
   */
  public int getType() {
    return TLD;
  }//end of getType()

  /*
   * @see com.sap.appchecker.lib.descriptors.IJ2EEDescriptorConverter#getTransformer(java.lang.String, org.w3c.dom.Document)
   */
  public void convert(ConversionContext context) throws ConversionException {
    // parse all descriptors first
    super.convert(context);

    Document tldDoc = context.getConvertedDocument(TLD_FILENAME);

    // now do the actual conversion (if necessary)
    if (tldDoc != null) {
      convertTldIfNeeded(tldDoc, context);
    }

  }//end of convert(ConversionContext context)

  public void convert(Document tldDoc, ConversionContext context) throws ConversionException {
    init(context);
    convertTldIfNeeded(tldDoc, context);
  }//end of convert(Document tldDoc, ConversionContext context)

  public void convertTldIfNeeded(Document tldDoc, ConversionContext context) throws ConversionException {
    // If it's the new version then don't convert
    Element root = tldDoc.getDocumentElement();
    String version = root.getAttribute("version");

    if (version != null) {
      if ("2.0".equals(version)) {
        // no conversion necessary
        context.setUnconvertedJ2EEVersion(J2EE_1_4);
        return;
      } else if ("2.1".equals(version)) {
        // no conversion necessary
        context.setUnconvertedJ2EEVersion(J2EE_1_5);
        return;
      }
    }

    context.setUnconvertedJ2EEVersion(J2EE_1_3);

    context.setConvertedDocument(TLD_FILENAME, transform(TLD_FILENAME, tldDoc, tldTransformer, context));
  }//end of convertTldIfNeeded(Document webDoc, ConversionContext context)

}//end of class
