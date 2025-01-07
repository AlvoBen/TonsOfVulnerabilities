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
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;

import com.sap.engine.lib.converter.AbstractConverter;
import com.sap.engine.lib.converter.ConversionContext;
import com.sap.engine.lib.converter.ConversionException;
import com.sap.engine.lib.converter.TransformerWrapper;
import com.sap.engine.lib.converter.util.XercesUtil;

/**
 * J2EE 1.4 converter for EJB container descriptors. Partly copied
 * from com.sap.engine.services.ejb.deploy.Converter
 * 
 * @author d037913
 */
public class EJBConverter extends AbstractConverter {

  private static final ClassLoader MY_LOADER = EJBConverter.class.getClassLoader();

  private static final String EJB_20_TO_21_XSL = XSL_ROOT + "ejb_20_to_21.xsl";
  private static final String EJB_JAR_XSL = XSL_ROOT + "ejbjar.xsl";
  private static final String EJB_J2EE_ENGINE_XSL = XSL_ROOT + "ejbj2eeengine.xsl";
  private static final String PERSISTENT_XSL = XSL_ROOT + "persistent.xsl";

  private TransformerWrapper ejb20To21Transformer;
  private TransformerWrapper ejbJarTransformer;
  private TransformerWrapper ejbJ2eeEngineTransformer;
  private TransformerWrapper persistentTransformer;

  public static final String EJBJAR_FILENAME = "META-INF/ejb-jar.xml";
  public static final String EJBJ2EE_FILENAME = "META-INF/ejb-j2ee-engine.xml";
  public static final String PERSISTENT_FILENAME = "META-INF/persistent.xml";
  public static final String STORAGE_FILENAME = "META-INF/storage.xml";

  private static final String SYSTEM_ID_1_1 = "ejb-jar_1_1.dtd";
  private static final String SYSTEM_ID_2_0 = "ejb-jar_2_0.dtd";

  private static final String dtd = "<!DOCTYPE ejb-jar [" + "\n<!ELEMENT ejb-jar (description?, display-name?, small-icon?, large-icon?, enterprise-beans, assembly-descriptor?)>" +
	  "\n<!ELEMENT description (#PCDATA)>" + "\n<!ELEMENT display-name (#PCDATA)>" + "\n<!ELEMENT small-icon (#PCDATA)>" + "\n<!ELEMENT large-icon (#PCDATA)>" +
	  "\n<!ELEMENT enterprise-beans (session|entity)+ > " + "\n<!ELEMENT session (description?, display-name?, small-icon?, large-icon?, " +
	  "\n ejb-name, home, remote, ejb-class, session-type,session-timeout?, transaction-type, bean-count," + "\n env-entry*, ejb-ref*, security-role-ref*, resource-ref*)> " +
	  "\n<!ELEMENT entity (description?, display-name?, small-icon?, large-icon?," + "\n ejb-name, home, remote, ejb-class, persistence-type, prim-key-class, reentrant, bean-count, cmp-field*, primkey-field?, " +
	  "\n env-entry*, ejb-ref*, security-role-ref*, resource-ref*)> " + "\n<!ELEMENT ejb-name (#PCDATA)>" + "\n<!ELEMENT home (#PCDATA)>" + "\n<!ELEMENT remote (#PCDATA)>" +
	  "\n<!ELEMENT ejb-class (#PCDATA)>" + "\n<!ELEMENT session-type (#PCDATA)>" + "\n<!ELEMENT session-timeout (#PCDATA)>" + "\n<!ELEMENT transaction-type (#PCDATA)>" +
	  "\n<!ELEMENT env-entry (description?, env-entry-name, env-entry-type, env-entry-value?)>" + "\n<!ELEMENT env-entry-name (#PCDATA)>" + "\n<!ELEMENT env-entry-type (#PCDATA)>" +
	  "\n<!ELEMENT env-entry-value (#PCDATA)>" + "\n<!ELEMENT ejb-ref (description?, ejb-ref-name, ejb-ref-type, home, remote, ejb-link?)>" + "\n<!ELEMENT ejb-ref-name (#PCDATA)>" +
	  "\n<!ELEMENT ejb-ref-type (#PCDATA)>" + "\n<!ELEMENT ejb-link (#PCDATA)>" + "\n<!ELEMENT security-role-ref (description?, role-name, role-link?)>" +
	  "\n<!ELEMENT role-name (#PCDATA)>" +	  "\n<!ELEMENT role-link (#PCDATA)>" + "\n<!ELEMENT group-id (#PCDATA)>" + "\n<!ELEMENT user-id (#PCDATA)>" +
	  "\n<!ELEMENT user-name (#PCDATA)>" + "\n<!ELEMENT group-name (#PCDATA)>" +
	  "\n<!ELEMENT resource-ref (description?, res-ref-name, res-type, res-auth, res-link, user-name, password)>" + "\n<!ELEMENT res-ref-name (#PCDATA)>" +
	  "\n<!ELEMENT res-type (#PCDATA)>" + "\n<!ELEMENT res-auth (#PCDATA)>" + "\n<!ELEMENT res-link (#PCDATA)>" + "\n<!ELEMENT password (#PCDATA)>" +
	  "\n<!ELEMENT persistence-type (#PCDATA)>" + "\n<!ELEMENT prim-key-class (#PCDATA)>" + "\n<!ELEMENT reentrant (#PCDATA)>" +
	  "\n<!ELEMENT cmp-field (description?, field-name)>" + "\n<!ELEMENT field-name (#PCDATA)>" + "\n<!ELEMENT primkey-field (#PCDATA)>" +
	  "\n<!ELEMENT assembly-descriptor (security-role*, method-permission*, container-transaction*)>" +
	  "\n<!ELEMENT security-role (description?, role-name, group-id*, user-id*, user-name*, group-name*)>" + "\n<!ELEMENT method-permission (description?, role-name+, method+, pid?)>" +
	  "\n<!ELEMENT method (description?, ejb-name, method-intf?, method-name, method-params?)>" + "\n<!ELEMENT method-intf (#PCDATA)>" +
	  "\n<!ELEMENT method-name (#PCDATA)>" + "\n<!ELEMENT method-params (method-param*)>" + "\n<!ELEMENT method-param (#PCDATA) >" +
	  "\n<!ELEMENT pid (#PCDATA)>" + "\n<!ELEMENT bean-count (#PCDATA)>" + "\n<!ELEMENT container-transaction (description?, method+, trans-attribute)>" +
	  "\n<!ELEMENT trans-attribute (#PCDATA)>" + "\n]>";

  /** The structure of the xml document that is generated or read according to J2EE specification. */
  private static final String simpleDTD = "<!DOCTYPE ejb-jar [" + "\n<!ELEMENT ejb-jar (description?, display-name?, small-icon?, large-icon?, enterprise-beans, assembly-descriptor?, ejb-client-jar?)>" +
	  "\n<!ELEMENT description (#PCDATA)>" + "\n<!ELEMENT display-name (#PCDATA)>" + "\n<!ELEMENT small-icon (#PCDATA)>" + "\n<!ELEMENT large-icon (#PCDATA)>" +
	  "\n<!ELEMENT enterprise-beans (session|entity)+ > " + "\n<!ELEMENT session (description?, display-name?, small-icon?, large-icon?, " + "\n ejb-name, home, remote, ejb-class, session-type, transaction-type, " +
	  "\n env-entry*, ejb-ref*, security-role-ref*, resource-ref*)> " + "\n<!ELEMENT entity (description?, display-name?, small-icon?, large-icon?," +
	  "\n ejb-name, home, remote, ejb-class, persistence-type, prim-key-class, reentrant, cmp-field*, primkey-field?, " + "\n env-entry*, ejb-ref*, security-role-ref*, resource-ref*)> " +
	  "\n<!ELEMENT ejb-name (#PCDATA)>" + "\n<!ELEMENT home (#PCDATA)>" + "\n<!ELEMENT remote (#PCDATA)>" + "\n<!ELEMENT ejb-class (#PCDATA)>" +
	  "\n<!ELEMENT session-type (#PCDATA)>" + "\n<!ELEMENT transaction-type (#PCDATA)>" + "\n<!ELEMENT env-entry (description?, env-entry-name, env-entry-type, env-entry-value?)>" +
	  "\n<!ELEMENT env-entry-name (#PCDATA)>" + "\n<!ELEMENT env-entry-type (#PCDATA)>" + "\n<!ELEMENT env-entry-value (#PCDATA)>" +
	  "\n<!ELEMENT ejb-ref (description?, ejb-ref-name, ejb-ref-type, home, remote, ejb-link?)>" + "\n<!ELEMENT ejb-ref-name (#PCDATA)>" + "\n<!ELEMENT ejb-ref-type (#PCDATA)>" +
	  "\n<!ELEMENT ejb-link (#PCDATA)>" + "\n<!ELEMENT security-role-ref (description?, role-name, role-link?)>" + "\n<!ELEMENT role-name (#PCDATA)>" + "\n<!ELEMENT role-link (#PCDATA)>" +
	  "\n<!ELEMENT resource-ref (description?, res-ref-name, res-type, res-auth)>" + "\n<!ELEMENT res-ref-name (#PCDATA)>" + "\n<!ELEMENT res-type (#PCDATA)>" +
	  "\n<!ELEMENT res-auth (#PCDATA)>" + "\n<!ELEMENT persistence-type (#PCDATA)>" + "\n<!ELEMENT prim-key-class (#PCDATA)>" + "\n<!ELEMENT reentrant (#PCDATA)>" +
	  "\n<!ELEMENT cmp-field (description?, field-name)>" + "\n<!ELEMENT field-name (#PCDATA)>" + "\n<!ELEMENT primkey-field (#PCDATA)>" +
	  "\n<!ELEMENT assembly-descriptor (security-role*, method-permission*, container-transaction*)>" + "\n<!ELEMENT security-role (description?, role-name)>" +
	  "\n<!ELEMENT method-permission (description?, role-name+, method+)>" + "\n<!ELEMENT method (description?, ejb-name, method-intf?, method-name, method-params?)>" +
	  "\n<!ELEMENT method-intf (#PCDATA)>" + "\n<!ELEMENT method-name (#PCDATA)>" + "\n<!ELEMENT method-params (method-param*)>" + "\n<!ELEMENT method-param (#PCDATA) >" +
	  "\n<!ELEMENT container-transaction (description?, method+, trans-attribute)>" + "\n<!ELEMENT trans-attribute (#PCDATA)>" + "\n<!ELEMENT ejb-client-jar (#PCDATA)>" + "\n]>";

  private static final String EJB_VERSION_UNKNOWN = "unknown";
  private static final String EJB_VERSION_INCORRECT_1_1 = "incorrect11";
  private static final String EJB_VERSION_1_X = "1.x";
  private static final String EJB_VERSION_2_0 = "2.0";
  private static final String EJB_VERSION_2_1 = "2.1";


  public EJBConverter() {
    init();
  }
  
  public EJBConverter(EntityResolver er) {
  	super(er);
  	init();
  }

  private void init() {
  	try {
        ejb20To21Transformer = createTransformer(MY_LOADER.getResourceAsStream(EJB_20_TO_21_XSL));
        ejbJarTransformer = createTransformer(MY_LOADER.getResourceAsStream(EJB_JAR_XSL));
        ejbJ2eeEngineTransformer = createTransformer(MY_LOADER.getResourceAsStream(EJB_J2EE_ENGINE_XSL));
        persistentTransformer = createTransformer(MY_LOADER.getResourceAsStream(PERSISTENT_XSL));
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
    return EJB;
  }

  /**
   * This method overwrites the one from the AbstractConverter, in order to keep the white spaces in the parsed document 
   */
  protected Document parse(String fileName, InputStream xmlStream, ConversionContext context) throws ConversionException {
	  Document doc = parseWithWhiteSpaces(fileName, xmlStream, context);
	  return doc;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sap.appchecker.lib.descriptors.IJ2EEDescriptorConverter#getTransformer(java.lang.String,
   *      org.w3c.dom.Document)
   */
  public void convert(ConversionContext context) throws ConversionException {
    super.convert(context);
    Document ejbJarDoc = context.getConvertedDocument(EJBJAR_FILENAME);
    Document storageDoc = context.getConvertedDocument(STORAGE_FILENAME);
    try {
    	convertIfNecessary(ejbJarDoc, storageDoc, context);
    } catch (UnknownVersionException uve) {
    	throwConversionException( uve,EJBJAR_FILENAME);
    }
  }

  public void convertStopOnUnknownVer(ConversionContext context) throws ConversionException, UnknownVersionException {
  	super.convert(context);
  	Document ejbJarDoc = context.getConvertedDocument(EJBJAR_FILENAME);
    Document storageDoc = context.getConvertedDocument(STORAGE_FILENAME);
    convertIfNecessary(ejbJarDoc, storageDoc, context);
  }
  
  private void convertIfNecessary(Document ejbJarDoc, Document storageDoc, ConversionContext context) throws UnknownVersionException, ConversionException {
    String version = getEjbJarVersion(ejbJarDoc);
      if (EJB_VERSION_INCORRECT_1_1.equals(version)) {
        Document convertedEjbJarDoc = transform(EJBJAR_FILENAME, ejbJarDoc, ejbJarTransformer, context);
        Document convertedEjbJ2eeEngineDoc = transform(EJBJ2EE_FILENAME, ejbJarDoc, ejbJ2eeEngineTransformer, context);
        context.setConvertedDocument(EJBJ2EE_FILENAME, convertedEjbJ2eeEngineDoc);
        convert11(convertedEjbJarDoc, storageDoc, context);
      } else if (EJB_VERSION_1_X.equals(version)) {
        convert11(ejbJarDoc, storageDoc, context);
      } else if (EJB_VERSION_2_0.equals(version)) {
        Document convertedEjbJarDoc = transform(EJBJAR_FILENAME, ejbJarDoc, ejb20To21Transformer, context);
        context.setConvertedDocument(EJBJAR_FILENAME, convertedEjbJarDoc);
        context.setUnconvertedJ2EEVersion(J2EE_1_3);
      } else if (EJB_VERSION_2_1.equals(version)) {
        // no transformation necessary
        context.setUnconvertedJ2EEVersion(J2EE_1_4);
      } else {
      	throw new UnknownVersionException("Version of ejb-jar descriptor could not be recognized. Probably this is a descriptor from ejb 3.0 module or newer");
      }
  }

  private static String getEjbJarVersion(Document doc) {
    Element root = doc.getDocumentElement();
    String namespace = root.getNamespaceURI();

    if (namespace == null) {
      DocumentType docType = doc.getDoctype();
      if (docType == null) {
        return EJB_VERSION_UNKNOWN;
      }

      String sysId = docType.getSystemId();

      if (sysId == null || dtd.equals(sysId) || simpleDTD.equals(sysId)) {
        return EJB_VERSION_INCORRECT_1_1;
      } else if (sysId.indexOf(SYSTEM_ID_1_1) != -1) {
        return EJB_VERSION_1_X;
      } else if (sysId.indexOf(SYSTEM_ID_2_0) != -1) {
        return EJB_VERSION_2_0;
      } else {
        return EJB_VERSION_UNKNOWN;
      }
    } else {
    	return "2.1".equals(root.getAttribute("version")) ? EJB_VERSION_2_1 : EJB_VERSION_UNKNOWN;	
    }
  }

  private void convert11(Document ejbJarDoc, Document storageDoc, ConversionContext context) throws ConversionException {
    if (storageDoc != null) {
      Document convertedPersistentDoc = transform(PERSISTENT_FILENAME, storageDoc, persistentTransformer, context);
      context.setConvertedDocument(PERSISTENT_FILENAME, convertedPersistentDoc);
    }

    convertXml_11_to_20(ejbJarDoc);
    Document convertedEjbJarDoc = transform(EJBJAR_FILENAME, ejbJarDoc, ejb20To21Transformer, context);
    context.setConvertedDocument(EJBJAR_FILENAME, convertedEjbJarDoc);
    context.setUnconvertedJ2EEVersion(J2EE_1_2);
  }

  private static void convertXml_11_to_20(Document doc) {
    NodeList reentrantList = doc.getElementsByTagName("reentrant");
    for (int i = 0; i < reentrantList.getLength(); i++) {
      Node reentrant = reentrantList.item(i);
      Element entity = (Element) reentrant.getParentNode();
      Node persistenceType = entity.getElementsByTagName("persistence-type")
          .item(0);
      String persistence = getNodeText(persistenceType);
      if (persistence.equals("Container")) {
        Element cmpVersion = doc.createElement("cmp-version");
        Text textNode = doc.createTextNode("1.x");
        cmpVersion.appendChild(textNode);
        Node nextSibling = reentrant.getNextSibling();
        entity.insertBefore(cmpVersion, nextSibling);
      }
    }
  }

  private static String getNodeText(Node n) {
    String data = "";
    NodeList nodeList = n.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.getNodeType() == Node.TEXT_NODE) {
        data = ((Text) node).getData();
        break;
      } else if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
        data = node.getNodeValue();
        break;
      }
    }
    return data;
  }

}