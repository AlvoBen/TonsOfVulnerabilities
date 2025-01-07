/*
 * Copyright (c) 2004 by SAP AG, Walldorf., http://www.sap.com All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG,
 * Walldorf. You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered
 * into with SAP.
 */
package com.sap.engine.lib.converter;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import com.sap.engine.lib.converter.util.XercesUtil;
import com.sap.engine.lib.xml.StandardDOMParser;
import com.sap.tc.logging.Location;


/**
 * Abstract base class that should be used for implementing
 * 
 * @see com.sap.engine.lib.converter.IJ2EEDescriptorConverter
 * 
 * @author d037913
 */
public abstract class AbstractConverter implements IJ2EEDescriptorConverter {

  /** Trace location which should be used by the converter implementations */
  public static final Location LOCATION = Location
      .getLocation("com.sap.engine.lib.converter");

  /**
   * constants for setting the J2EE version
   * {@link ConversionContext#setUnconvertedJ2EEVersion(String)}
   */
  public static final String J2EE_1_2 = "1.2";
  public static final String J2EE_1_3 = "1.3";
  public static final String J2EE_1_4 = "1.4";
  public static final String J2EE_1_5 = "1.5";

  private static TransformerFactory transformerFactory = null;

  /* root dir of J2EE 1.4 transformer .xsl's */
  protected static final String XSL_ROOT = "com/sap/engine/lib/converter/xsl/";
  
  protected static ParserWrapper nonValidatingParser;
  
  {
    try {
      nonValidatingParser = new ParserWrapper(XercesUtil.getDocumentBuilder(XercesUtil.NON_VALIDATING));
    } catch (ParserConfigurationException e) {
      LOCATION.catching(e);
      throw new RuntimeException(e);
    }
  }
  
  private static final Validator validator = (Validator) XercesUtil.createObject(
      "com.sap.engine.lib.converter.impl.JAXPValidator",
      "com.sap.engine.lib.converter.impl.XMLToolkitValidator");
  
  protected TransformerFactory getTransformerFactory() {
    return transformerFactory;
  }

  protected AbstractConverter() {
    this(new CombinedEntityResolver());
  }

  protected AbstractConverter(EntityResolver entityResolver) {
    synchronized (validator) {
      validator.setEntityResolver(entityResolver);
    }      
  }

  /**
   * Default implementation that resolves substitution variables in the
   * InputStreams if necessary, parses all InputStreams and puts the unconverted
   * Documents into the context.
   */
  public void convert(ConversionContext context) throws ConversionException {
    init(context);
    parseDescriptors(context);
  }

  protected TransformerWrapper createTransformer(InputStream xslStream)
    throws TransformerConfigurationException {
    return new TransformerWrapper(XercesUtil.getTransformer(xslStream));
  }

  protected Document transform(String fileName, Document source,
      TransformerWrapper transformer, ConversionContext context)
      throws ConversionException {
    DOMResult domResult = new DOMResult();
    transformer.transform(new DOMSource(source), domResult, fileName);
    Document doc = (Document) domResult.getNode();
    if (context.isXmlValidating()) {
      // we have to re-parse in order to get schema validation!
      return reParse(fileName, doc);
    }
    return doc;    
  }

  /*
   * serialize the (converted) Document and parse it again with
   * schema-validation turned on. This needs to be done in order to ensure that
   * the XMLMarshaller can create a J2EE descriptor object from this Document
   * without XMLUnMarshalExceptions.
   */
  protected Document reParse(String fileName, Document document) throws ConversionException {
    InputStream inStream = null;
    Document doc = null;
    try {
      DOMResult domResult = validator.validate(new DOMSource(document), fileName, true); 
      doc = (Document) domResult.getNode();
    } finally {
      if (inStream != null) {
        try {
          inStream.close();
        } catch (IOException e) {
          throwConversionException(e, fileName);
        }
      }
    }
    return doc;
  }

  protected Document parseWithWhiteSpaces(String fileName, InputStream xmlStream, ConversionContext context) throws ConversionException {
	    Document doc = null;
	    try {
	      if (context.isXmlValidating()) {
	        DOMResult domResult;
	        domResult = validator.validate(XercesUtil.toStreamSource(xmlStream), fileName, isForgiving(context));
	        doc = (Document) domResult.getNode();
	      } else {
	        doc = nonValidatingParser.parse(XercesUtil.toInputSource(xmlStream), fileName);
	      }
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
  
  protected Document parse(String fileName, InputStream xmlStream, ConversionContext context) throws ConversionException {
    Document doc = parseWithWhiteSpaces(fileName, xmlStream, context);
    if (doc != null) {
      XercesUtil.trimWhiteSpaces(doc.getDocumentElement());
    }
    return doc;
  }

  protected void resolveSubstVars(ConversionContext context)
      throws ConversionException {
    ISubstVarResolver resolver = context.getSubstVarResolver();
    if (resolver == null) {
      LOCATION
          .debugT("no subst var resolver provided, variables will not be resolved.");
      // no resolver provided
      return;
    }
    String[] keys = context.getAllInputStreamFileNames();
    for (int i = 0; i < keys.length; i++) {
      InputStream inStream = null;
      try {
        inStream = context.getInputStream(keys[i]);
        if (inStream != null) {
          synchronized (resolver) {
            context.setInputStream(keys[i], resolver
                .substituteParamStream(inStream));
          }
        }
      } catch (DescriptorParseException e) {
        throwConversionException(e, keys[i]);
      } finally {
        if (inStream != null) {
          try {
            inStream.close();
          } catch (IOException e) {
            throwConversionException(e, keys[i]);
          }
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.sap.engine.lib.converter.IJ2EEDescriptorConverter#convert(com.sap.engine.lib.converter.ConversionContext)
   */
  protected void init(ConversionContext context) throws ConversionException {
    resolveSubstVars(context);
  }

  protected void parseDescriptors(ConversionContext ctx)
      throws ConversionException {
    String[] keys = ctx.getAllInputStreamFileNames();
    for (int i = 0; i < keys.length; i++) {
      // try {
      InputStream inStream = ctx.getInputStream(keys[i]);
      if (inStream != null) {
        Document doc = parse(keys[i], inStream, ctx);
        if (doc != null) {
          ctx.setConvertedDocument(keys[i], doc);
        }
      }
    }
  }

  /**
   * Need to add prefix to system id for backwards compatibility with xml
   * toolkit entity resolver behavior.
   * 
   * @see com.sap.engine.lib.xml.StandardEntityResolver
   * @see com.sap.engine.lib.processor.SchemaEntityResolver
   */
  protected InputSource wrapStream(InputStream inStream) {
    InputSource inSource = new InputSource(inStream);
    inSource.setSystemId(StandardDOMParser.SAP_DTD_PREFIX);
    return inSource;
  }


  protected static boolean isForgiving(ConversionContext ctx) {
    Boolean forgivingBoolean = (Boolean) ctx
        .getAttribute(ConversionContext.FORGIVING_ATTR);
    return forgivingBoolean == null ? false : forgivingBoolean.booleanValue();
  }
  
  public static void throwConversionException(Throwable t, String fileName)
      throws ConversionException {
    throw new ConversionException(
        new FileNameExceptionPair[] { new FileNameExceptionPair(t, fileName,
            FileNameExceptionPair.SEVERITY_ERROR) });
  }
}