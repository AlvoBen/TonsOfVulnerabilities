/*
 * Copyright (c) 2004 by SAP AG, Walldorf., http://www.sap.com All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG,
 * Walldorf. You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered
 * into with SAP.
 */
package com.sap.engine.lib.converter.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.TransformerConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sap.engine.lib.converter.AbstractConverter;
import com.sap.engine.lib.converter.ConversionContext;
import com.sap.engine.lib.converter.ConversionException;
import com.sap.engine.lib.converter.TransformerWrapper;

/**
 * J2EE 1.4 converter for application descriptors (application.xml ,
 * application-j2ee-engine.xml).
 * 
 * @author d037913
 */
public class ApplicationConverter extends AbstractConverter {

  private static final String APP_13_TO_14_XSL = XSL_ROOT
      + "application_13_to_14.xsl";

  private static final String APP_J2EE_DTD_TO_SCHEMA_XSL = XSL_ROOT
      + "application_j2ee_dtd_to_schema.xsl";

  public static final String APPLICATION_FILENAME = "META-INF/application.xml";
  public static final String APPLICATION_J2EE_FILENAME = "META-INF/application-j2ee-engine.xml";

  protected TransformerWrapper app13To14Transformer;
  protected TransformerWrapper appJ2eeTransformer;

  public ApplicationConverter() {
    super();
    try {
      app13To14Transformer = createTransformer(ApplicationConverter.class
          .getClassLoader().getResourceAsStream(APP_13_TO_14_XSL));
      appJ2eeTransformer = createTransformer(ApplicationConverter.class
          .getClassLoader().getResourceAsStream(APP_J2EE_DTD_TO_SCHEMA_XSL));
    } catch (TransformerConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.sap.appchecker.lib.descriptors.IJ2EEDescriptorConverter#getType()
   */
  public int getType() {
    return APPLICATION;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.sap.appchecker.lib.descriptors.IJ2EEDescriptorConverter#convertToLatestJ2EERelease(java.lang.String,
   *      org.w3c.dom.Document)
   */
  public void convert(ConversionContext context)
      throws ConversionException {
    // parse all streams first
    super.convert(context);
    // then convert if necessary
    Document appDoc = context.getConvertedDocument(APPLICATION_FILENAME);
    if (appDoc != null) {
      convertIfNecessary(appDoc, context);
    }

    appDoc = context.getConvertedDocument(APPLICATION_J2EE_FILENAME);
    if (appDoc != null) {
      convertJ2eeIfNecessary(appDoc, context);
    }
  }

  private void convertIfNecessary(Document appDoc, ConversionContext context)
      throws ConversionException {
    Element root = appDoc.getDocumentElement();
    String version = root.getAttribute("version");
    if ("1.4".equals(version)) {
      // no conversion necessary
      context.setUnconvertedJ2EEVersion(J2EE_1_4);
      return;
    }
    if ("5".equals(version)) {
      // no conversion necessary
      context.setUnconvertedJ2EEVersion(J2EE_1_5);
      return;
    }
    // must be J2EE 1.2 or 1.3 by now
    Document doc = transform(APPLICATION_FILENAME, appDoc,
        app13To14Transformer, context);
    context.setConvertedDocument(APPLICATION_FILENAME, doc);
    context.setUnconvertedJ2EEVersion(J2EE_1_3);
  }

  private void convertJ2eeIfNecessary(Document appDoc, ConversionContext context)
      throws ConversionException {
    Document doc = transform(APPLICATION_J2EE_FILENAME, appDoc,
        appJ2eeTransformer, context);
    context.setConvertedDocument(APPLICATION_J2EE_FILENAME, doc);
    context.setUnconvertedJ2EEVersion(J2EE_1_3);
  }

  protected Document parse(String fileName, InputStream xmlStream)
      throws ConversionException {
    if (xmlStream == null) {
      return null;
    }
    Document doc = null;
    try {
      doc = nonValidatingParser.parse(xmlStream, fileName);
    } finally {
      if (xmlStream != null) {
        try {
          xmlStream.close();
        } catch (IOException e) {
          throwConversionException(e, fileName);
        }
      }
    }
    return doc;
  }

}
