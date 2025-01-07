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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.engine.lib.converter.AbstractConverter;
import com.sap.engine.lib.converter.ConversionContext;
import com.sap.engine.lib.converter.ConversionException;
import com.sap.engine.lib.converter.TransformerWrapper;
import com.sap.engine.lib.converter.util.XercesUtil;

/**
 * J2EE 1.4 converter for JCA descriptors.
 *
 * @author d037913
 * @author Peter Matov
 */
public class ConnectorConverter extends AbstractConverter {

  public static final String CONNECTOR_FILENAME = "META-INF/ra.xml";
  public static final String CONNECTOR_J2EE_FILENAME = "META-INF/connector-j2ee-engine.xml";

  public static final byte[] DEFAULT_ADDITIONAL_XML = (
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
      "<connector xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"connector-j2ee-engine.xsd\">\n" +
      "<resourceadapter></resourceadapter>\n" +
      "</connector>").getBytes();

  private static final String RA_XML_XSL_CONVERTOR = XSL_ROOT + "ra.xsl";
  private static final String SAP_XML_XSL_CONVERTOR = XSL_ROOT + "connector-j2ee-engine.xsl";

  private static final String CONNECTOR_MAIN_TAG = "connector";
  private static final String RESOURCE_ADDAPTER_VERSION_TAG = "resourceadapter-version";
  private static final String CONNECTION_FACTORY_INTERFACE_TAG = "connectionfactory-interface";
  private static final String CONNECTOR_OLD_MAIN_TAG = "connector-j2ee-engine";

  protected TransformerWrapper raXmlTransformer;
  protected TransformerWrapper sapXmlTransformer;

  public ConnectorConverter() {
    ClassLoader loader = ConnectorConverter.class.getClassLoader();
    try {
      raXmlTransformer = createTransformer(loader.getResourceAsStream(RA_XML_XSL_CONVERTOR));
      sapXmlTransformer = createTransformer(loader.getResourceAsStream(SAP_XML_XSL_CONVERTOR));
    } catch (TransformerConfigurationException exc) {
      throw new RuntimeException(exc);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.sap.appchecker.lib.descriptors.IJ2EEDescriptorConverter#getType()
   */
  public int getType() {
    return CONNECTOR;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.sap.appchecker.lib.descriptors.IJ2EEDescriptorConverter#convertToLatestJ2EERelease(java.lang.String,
   *      org.w3c.dom.Document)
   */
  public void convert(ConversionContext context)
      throws ConversionException {
    // reset state
    init(context);
    parseDescriptors(context);
    //then convert if necessary
    Document raDoc = context.getConvertedDocument(CONNECTOR_FILENAME);
    if (raDoc == null) {
      throwConversionException(new IllegalStateException("Could not read connector deployment descriptor file '" + CONNECTOR_FILENAME + "'. Probably it is missing."), CONNECTOR_FILENAME);
    }
    Document sapDoc = context.getConvertedDocument(CONNECTOR_J2EE_FILENAME);
    boolean isRaJ2EE14 = convertRaIfNecessary(raDoc, context);
    if (sapDoc != null) {
      convertSapIfNecessary(raDoc, isRaJ2EE14, sapDoc, context);
    }
    if (context.isXmlValidating()) {
      // validate 
      raDoc = context.getConvertedDocument(CONNECTOR_FILENAME) != null 
            ? context.getConvertedDocument(CONNECTOR_FILENAME) : raDoc;
      validateConnector(raDoc);
    }
  }

  private void validateConnector(Document raDoc) throws ConversionException {
    // we need to re-parse in order to get validation
    reParse(CONNECTOR_FILENAME, raDoc);
  }

  protected void parseDescriptors(ConversionContext ctx) throws ConversionException {
    String[] keys = ctx.getAllInputStreamFileNames();
    for (int i = 0; i < keys.length; i++) {
      try {
        InputStream is = ctx.getInputStream(keys[i]);
        byte buffer[] = null;
        if (is != null) {
          buffer = readInputStream(is);
          ctx.setInputStream(keys[i], new ByteArrayInputStream(buffer));
        }
        Document doc = parse(keys[i], buffer != null ? new ByteArrayInputStream(buffer) : null, ctx);
        if (doc != null) {
          ctx.setConvertedDocument(keys[i], doc);
        }
      } catch (IOException e) {
        throwConversionException(e,keys[i]);
      }
    }
  }

  private static final int COPY_BUFFER_SIZE = 1024;

  private byte[] readInputStream(InputStream source) throws IOException {
    ByteArrayOutputStream storage = new ByteArrayOutputStream();
    transferBytes(source, storage);
    return storage.toByteArray();
  }

  private void transferBytes(InputStream source, OutputStream destination)
      throws IOException {
    byte[] buffer = new byte[COPY_BUFFER_SIZE];
    int read;
    try {
      while ((read = source.read(buffer)) != -1) {
        destination.write(buffer, 0, read);
      }
    } finally {
      source.close();
    }
  }

  protected Document parse(String fileName, InputStream xmlStream,
      ConversionContext context) throws ConversionException {
    if (xmlStream == null) {
      return null;
    }
    Document doc = null;
    try {
      doc = AbstractConverter.nonValidatingParser.parse(xmlStream, fileName);
    } finally {
      if (xmlStream != null) {
        try {
          xmlStream.close();
        } catch (IOException e) {
          throwConversionException(e, fileName);
        }
      }
    }
    
    if (doc != null) {
      XercesUtil.trimWhiteSpaces(doc.getDocumentElement());
    }
    
    return doc;
  }


  private boolean convertRaIfNecessary(Document raDoc, ConversionContext context) throws ConversionException {

    boolean isRaJ2EE14 = isRaJ2EE14(raDoc);
    if (isRaJ2EE14) {
      //no conversion necessary
      return isRaJ2EE14;
    }

    try {
      transform(CONNECTOR_FILENAME, raDoc, context, raXmlTransformer);
    } catch (Exception exc) {
      throwConversionException(exc, CONNECTOR_FILENAME);
    }
    return isRaJ2EE14;
  }

  private void convertSapIfNecessary(Document raDoc, boolean isRaJ2EE14, Document sapDoc, ConversionContext context) throws ConversionException {

    boolean isSapJ2EE14 = isSapJ2EE14(sapDoc);

    if (isRaJ2EE14 != isSapJ2EE14) {
      throwConversionException( new IllegalStateException("Versions of standard and additional xmls do not match."), CONNECTOR_J2EE_FILENAME);
    }

    if (isSapJ2EE14) {
      //no conversion necessary
      return;
    }

    NodeList nodeList = raDoc.getElementsByTagName(CONNECTION_FACTORY_INTERFACE_TAG);
    if (nodeList == null || nodeList.getLength() == 0) {
      throwConversionException(new IllegalStateException("Cannot find element \'"+CONNECTION_FACTORY_INTERFACE_TAG+"\'"), CONNECTOR_J2EE_FILENAME);
    }

    Node connFactoryInterface = nodeList.item(0);

    nodeList = sapDoc.getElementsByTagName(CONNECTOR_OLD_MAIN_TAG);

    if (nodeList == null || nodeList.getLength() == 0) {
      throwConversionException(new IllegalStateException("Cannot find element \'"+CONNECTOR_OLD_MAIN_TAG+"\'"), CONNECTOR_J2EE_FILENAME);
    }

    nodeList.item(0).appendChild(sapDoc.importNode(connFactoryInterface, true));

    try {
      transform(CONNECTOR_J2EE_FILENAME, sapDoc, context, sapXmlTransformer);
    } catch (Exception exc) {
      throwConversionException(exc, CONNECTOR_J2EE_FILENAME);
    }
  }

  private boolean isRaJ2EE14(Document raXmlDom) throws ConversionException {

    NodeList nodeList = raXmlDom.getElementsByTagNameNS("*", CONNECTOR_MAIN_TAG);
    if (nodeList == null || nodeList.getLength() == 0) {
      throwConversionException(new IllegalStateException("Cannot find root element \'"+CONNECTOR_MAIN_TAG+"\'"), CONNECTOR_FILENAME);
    }

    Node mainTag = nodeList.item(0);
    NodeList mainList = mainTag.getChildNodes();

    for(int i = 0; i < mainList.getLength(); i++) {
      if (mainList.item(i).getNodeType() == Node.ELEMENT_NODE) {
        if (mainList.item(i).getLocalName().equals(RESOURCE_ADDAPTER_VERSION_TAG)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isSapJ2EE14(Document sapXmlDom) {
    NodeList nodeList = sapXmlDom.getElementsByTagNameNS("*", CONNECTOR_MAIN_TAG);
    if (nodeList == null || nodeList.getLength() == 0) {
      return false;
    }
    return true;
  }

  private void transform(String fileName, Document source, ConversionContext context, TransformerWrapper transformer) throws ConversionException {
    ByteArrayOutputStream destination = new ByteArrayOutputStream();
    transformer.transform(new DOMSource(source), new StreamResult(destination),fileName);
    byte buffer[] = destination.toByteArray();
    context.setInputStream(fileName, new ByteArrayInputStream(buffer));
    context.setConvertedDocument(fileName, parse(fileName, new ByteArrayInputStream(buffer), context));
  }

}
