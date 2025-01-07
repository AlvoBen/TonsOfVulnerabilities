package com.sap.engine.lib.converter;

import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

/**
 * Threadsafe wrapper for {@link javax.xml.transform.Transformer}.
 */
public class TransformerWrapper {

  private static final Properties STANDARD_OUTPUT_PROPS = new Properties();

  static {
    STANDARD_OUTPUT_PROPS.setProperty(OutputKeys.METHOD, "xml");
    STANDARD_OUTPUT_PROPS.setProperty(OutputKeys.INDENT, "yes");
  }

  private Transformer transformer;

  public TransformerWrapper(Transformer transformer) {
    this.transformer = transformer;
    transformer.setOutputProperties(STANDARD_OUTPUT_PROPS);
  }
  
  public synchronized void transform(Document sourceDoc, Result result,
      String fileName, boolean preserveDocType) throws ConversionException {
    try {
      if (preserveDocType) {
        DocumentType docType = sourceDoc.getDoctype();
        if (docType != null) {
          String sysId = docType.getSystemId();
          if (sysId != null) {
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, sysId);
          }
          String publicId = docType.getPublicId();
          if (publicId != null) {
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicId);
          }
        }
      }
      transform(new DOMSource(sourceDoc), result, fileName);
    } finally {
      transformer.setOutputProperties(STANDARD_OUTPUT_PROPS);
    }
  }

  public synchronized void transform(Source source, Result result,
      String fileName) throws ConversionException {
    try {
      transformer.transform(source, result);
    } catch (TransformerException e) {
      FileNameExceptionPair[] fileExcPairs = new FileNameExceptionPair[] { new FileNameExceptionPair(
          e, fileName, FileNameExceptionPair.SEVERITY_ERROR) };
      throw new ConversionException(fileExcPairs);
    }
  }

}
