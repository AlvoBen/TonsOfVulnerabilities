/*
 * Copyright (c) 2005 by SAP AG, Walldorf., http://www.sap.com All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG,
 * Walldorf. You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered
 * into with SAP.
 * 
 * $Id: //engine/js.j2eedescriptors.lib/dev/src/_tc~bl~j2eedescriptors/java/com/sap/engine/lib/converter/ConversionContext.java#1 $
 */
package com.sap.engine.lib.converter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

/**
 * This class is used as an input/output parameter passed in when converting
 * J2EE descriptors.
 * 
 * @see com.sap.engine.lib.converter.IJ2EEDescriptorConverter#convert(ConversionContext).
 * @author d037913
 */
public class ConversionContext {

  /**
   * java.lang.Boolean conversion attribute for "forgiving" conversion, i.e.
   * don't do initial DTD validation when parsing J2EE 1.3 descriptors before
   * converting (but still do in-memory schema-validation on converted
   * org.w3c.Document if validation is switched on).
   */
  public static final String FORGIVING_ATTR = "conversion.forgiving";

  private Map inStreamMap = new HashMap(4);
  private Map convertedNotSubstVarDomMap = new HashMap(4);
  private Map convertedDocMap = new HashMap(4);
  private Map attributeMap = new HashMap();
  private String unconvertedJ2eeVersion = "unknown";//default is unknown
  private ISubstVarResolver resolver;
  private boolean xmlValidate = true;

  /**
   * Equivalent to
   * {@link #ConversionContext(<code>null</code>, <code>true</code>).
   *  
   */
  public ConversionContext() {
    this(null, true);
  }

  /**
   * Constructs a conversion context.
   * 
   * @param substVarResolver
   *          Substitution variable resolver (may be <code>null</code>).
   * @param xmlValidating
   *          whether to DTD/XSD validate XML InputStreams.
   */
  public ConversionContext(ISubstVarResolver substVarResolver,
      boolean xmlValidating) {
    this.resolver = substVarResolver;
    this.xmlValidate = xmlValidating;
  }

  /**
   * Constructs a conversion context.
   * 
   * @param substVarResolver
   *          Substitution variable resolver (may be <code>null</code>).
   * @param xmlValidating
   *          whether to DTD/XSD validate XML InputStreams.
   * @param ignoreNullInStreams
   *          whether to ignore <code>null</code> InputStreams.
   * @deprecated ignoreNullInStreams is no longer supported. Use
   *             {@link   public ConversionContext(ISubstVarResolver,boolean) }
   *             instead.
   */
  public ConversionContext(ISubstVarResolver substVarResolver,
      boolean xmlValidating, boolean ignoreNullInStreams) {
    this.resolver = substVarResolver;
    this.xmlValidate = xmlValidating;
  }

  /**
   * Set an InputStream for XML parsing.
   * 
   * @param fileName
   *          relative fileName, e.g. "META-INF/application.xml".
   * @param inStream
   *          XML input stream. Will be closed on successful conversion.
   */
  public void setInputStream(String fileName, InputStream inStream) {
    this.inStreamMap.put(fileName, inStream);
  }

  /**
   * Get an InputStream for XML parsing.
   * 
   * @param fileName
   *          relative fileName, e.g. "META-INF/application.xml".
   * @return inStream XML input stream. Should be closed on successful
   *         conversion.
   */
  public InputStream getInputStream(String fileName) {
    return (InputStream) inStreamMap.get(fileName);
  }

  /**
   * Set the J2EE version of the unconverted descriptors detected during
   * conversion.
   * 
   * @param version
   * @see AbstractConverter#J2EE_1_2 etc. constants.
   */
  public void setUnconvertedJ2EEVersion(String version) {
    this.unconvertedJ2eeVersion = version;
  }

  /**
   * Get the J2EE version of the unconverted descriptors detected during
   * conversion.
   * 
   * @see AbstractConverter#J2EE_1_2 etc. constants.
   */
  public String getUnconvertedJ2EEVersion() {
    return unconvertedJ2eeVersion;
  }

  /**
   * Set the converted document that complies to the latest J2EE release.
   * 
   * @param fileName
   *          relative fileName, e.g. "META-INF/application.xml"
   * @param doc
   *          XML document of the descriptor.
   */
  public void setConvertedDocument(String fileName, Document doc) {
    this.convertedDocMap.put(fileName, doc);
  }

  /**
   * Get the converted document that complies to the latest J2EE release.
   * 
   * @param fileName
   *          relative fileName, e.g. "META-INF/application.xml"
   * @return XML document of the descriptor.
   */
  public Document getConvertedDocument(String fileName) {
    return (Document) convertedDocMap.get(fileName);
  }

  /**
   * Set the converted document that complies to the latest J2EE release. The
   * substitution variables are not replaced in the document.
   * 
   * @param fileName
   *          relative fileName, e.g. "META-INF/application.xml"
   * @param doc
   *          XML document of the descriptor.
   */
  public void setConvertedNotSubstitutedDocument(String fileName, Document doc) {
    this.convertedNotSubstVarDomMap.put(fileName, doc);
  }

  /**
   * Get the converted document that complies to the latest J2EE release. The
   * substitution variables are not replaced in the document.
   * 
   * @param fileName
   *          relative fileName, e.g. "META-INF/application.xml"
   * @return XML document of the descriptor.
   */
  public Document getConvertedNotSubstitutedDocument(String fileName) {
    return (Document) convertedNotSubstVarDomMap.get(fileName);
  }

  /**
   * Set a generic attribute for conversion.
   */
  public void setAttribute(String key, Object value) {
    this.attributeMap.put(key, value);
  }

  /**
   * Get a generic attribute for conversion.
   */
  public Object getAttribute(String key) {
    return this.attributeMap.get(key);
  }

  /**
   * Get an array of all available InputStream fileNames used as keys.
   * 
   * @see ConversionContext#getInputStream(String)
   */
  public String[] getAllInputStreamFileNames() {
    return (String[]) inStreamMap.keySet().toArray(new String[0]);
  }

  /**
   * Get an array of all available converted Document fileNames used as keys.
   * 
   * @see ConversionContext#getConvertedDocument(String)
   */
  public String[] getAllConvertedDocFileNames() {
    return (String[]) convertedDocMap.keySet().toArray(new String[0]);
  }

  /**
   * Get the substitution variable resolver used for replacing variables in the
   * InputStreams before parsing.
   *  
   */
  public ISubstVarResolver getSubstVarResolver() {
    return resolver;
  }

  /**
   * @return <code>true</code> if InputStreams should be DTD/XSD validated
   *         during parsing.
   */
  public boolean isXmlValidating() {
    return xmlValidate;
  }

  /**
   * @param validate
   *          <code>true</code> if InputStreams should be DTD/XSD validated
   *          during parsing.
   */
  public void setXmlValidating(boolean validate) {
    this.xmlValidate = validate;
  }

}