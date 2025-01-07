package com.sap.engine.lib.xml.parser;

import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Deprecated
public class DOMParserExt extends DOMParser {

  Document doc = null;

  public DOMParserExt() throws Exception {
    super();
  }

  public Document parse(String systemId) throws IOException, SAXException {
    doc = super.parse(systemId);
    return doc;
  }

  public Document parse(InputSource systemId) throws IOException, SAXException {
    doc = super.parse(systemId);
    return doc;
  }

  public Document parse(InputStream systemId) throws IOException, SAXException {
    doc = super.parse(systemId);
    return doc;
  }

  public Document getDocument() {
    return doc;
  }

}

