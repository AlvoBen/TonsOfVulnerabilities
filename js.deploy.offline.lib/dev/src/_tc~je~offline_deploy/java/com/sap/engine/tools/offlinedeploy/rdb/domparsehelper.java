package com.sap.engine.tools.offlinedeploy.rdb;

import org.w3c.dom.Document;
import org.xml.sax.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Offline Deploy parse helper provides resolving and error handling.
 * If parsed XML document is not valid according corresponding DTD
 * warning is reported on deploy result.
 *
 * @author Dimitar Kostadinov
 * @version 710
 * @see org.xml.sax.ErrorHandler
 * @see org.xml.sax.EntityResolver
 */
public class DOMParseHelper implements EntityResolver, ErrorHandler {

  //document builder instance;
  private DocumentBuilder docBuilder;

  //the name of the current document to be parsed
  private String currentDocument;
  //the name of the current zip archive
  private String currentZip;

  /**
   * Creates a DOM parse helper instance.
   */
  DOMParseHelper() throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(true);
    docBuilder = factory.newDocumentBuilder();
    docBuilder.setEntityResolver(this);
    docBuilder.setErrorHandler(this);
  }


  /**
   * Parses XML file from zip archive.
   *
   * @param zipEntry - representing the xml file in the zip archive
   * @param zipFile - the zip archive
   * @return document object model
   * @exception IOException If any IO errors occur.
   * @exception SAXException If any parse errors occur.
   * @see javax.xml.parsers.DocumentBuilder#parse(java.io.InputStream)
   */
  public synchronized Document parse(ZipEntry zipEntry, ZipFile zipFile) throws SAXException, IOException {
    InputStream inputStream = zipFile.getInputStream(zipEntry);
    try {
      currentDocument = zipEntry.getName();
      currentZip = zipFile.getName();
      return docBuilder.parse(inputStream);
    } finally {
      currentDocument = null;
      currentZip = null;
      inputStream.close();
    }
  }

  /**
   * (non-Javadoc)
   * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
   */
  public void warning(SAXParseException exception) throws SAXException {
  }

  /**
   * The parsing document violates the validity constraint. This is reported as
   * warning in the deploy result.
   *
   * @see org.xml.sax.SAXParseException
   */
  public void error(SAXParseException exception) throws SAXException {
    if (Boolean.getBoolean("xmlvalidate")) {
      Utils.warning(exception, "ASJ.dpl_off.000023", "Can not validate xml [" + currentDocument + "] from [" + currentZip + "].");
    }
  }

  /**
   * (non-Javadoc)
   * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
   */
  public void fatalError(SAXParseException exception) throws SAXException {
    throw exception;
  }

  /**
   * (non-Javadoc)
   * @see org.xml.sax.EntityResolver#resolveEntity(String, String)
   */
  public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
    int index = systemId.lastIndexOf('/');
    if (index != -1) {
      systemId = systemId.substring(index + 1);
    }
    InputStream in = this.getClass().getClassLoader().getResourceAsStream("dtd/" + systemId);
    InputSource inputSource = new InputSource(in);
    inputSource.setSystemId(systemId);
    return inputSource;
  }

}