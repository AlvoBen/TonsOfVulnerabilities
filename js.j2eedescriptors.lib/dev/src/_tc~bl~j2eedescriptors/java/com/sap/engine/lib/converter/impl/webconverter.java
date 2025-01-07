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

import com.sap.engine.lib.converter.AbstractConverter;
import com.sap.engine.lib.converter.ConversionContext;
import com.sap.engine.lib.converter.ConversionException;
import com.sap.engine.lib.converter.TransformerWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.transform.TransformerConfigurationException;

/**
 * J2EE 1.4 converter for web container descriptors
 * (web.xml, web-j2ee-egine.xml).
 * Some of the functionality was taken from
 * com.sap.engine.services.servlets_jsp.server.deploy.descriptor.converter.WebConverter
 *
 * @author d037913
 * @author Violeta Georgieva
 */
public class WebConverter extends AbstractConverter {

  public static final String WEB_FILENAME = "WEB-INF/web.xml";
  public static final String WEBJ2EE_FILENAME = "WEB-INF/web-j2ee-engine.xml";
  public static final String WEBJ2EE_FILENAME_2_2 = "WEB-INF/web-j2ee-engine.xml_2_2";

  public static final String WEB_APP_2_4_XSL_SIMPLE = XSL_ROOT + "web-app_2_4-simple.xsl";
  public static final String WEB_J2EE_ENGINE_2_3_TO_2_4_XSL_SIMPLE = XSL_ROOT + "web-j2ee-engine_2_3_to_2_4-simple.xsl";
  public static final String WEB_J2EE_ENGINE_2_2_TO_2_4_XSL_SIMPLE = XSL_ROOT + "web-j2ee-engine_2_2_to_2_4-simple.xsl";
  public static final String WEB_APP_2_4_XSL = XSL_ROOT + "web-app_2_4.xsl";
  public static final String WEB_J2EE_ENGINE_2_4_TO_2_4_XSL = XSL_ROOT + "web-j2ee-engine_2_4_to_2_4.xsl";//this is only for migration from MS1/2 to MS3
  public static final String WEB_J2EE_ENGINE_2_3_TO_2_4_XSL = XSL_ROOT + "web-j2ee-engine_2_3_to_2_4.xsl";
  public static final String WEB_J2EE_ENGINE_2_2_TO_2_4_XSL = XSL_ROOT + "web-j2ee-engine_2_2_to_2_4.xsl";

  public static final String SYSTEM_ID_2_2 = "web-app_2_2.dtd";
  public static final String SYSTEM_ID_2_3 = "web-app_2_3.dtd";
  public static final String SYSTEM_ID_2_3_ADD = "web-j2ee-engine.dtd";
  public static final String SYSTEM_ID_2_4_ADD = "web-j2ee-engine.xsd";

  protected TransformerWrapper web24TransformerSimple;
  protected TransformerWrapper webJ2ee23To24TransformerSimple;
  protected TransformerWrapper webJ2ee22To24TransformerSimple;
  protected TransformerWrapper web24Transformer;
  protected TransformerWrapper webJ2ee24To24Transformer;
  protected TransformerWrapper webJ2ee23To24Transformer;
  protected TransformerWrapper webJ2ee22To24Transformer;

  public WebConverter() {
    ClassLoader loader = WebConverter.class.getClassLoader();
    try {
      web24TransformerSimple = createTransformer(loader.getResourceAsStream(WEB_APP_2_4_XSL_SIMPLE));
      webJ2ee23To24TransformerSimple = createTransformer(loader.getResourceAsStream(WEB_J2EE_ENGINE_2_3_TO_2_4_XSL_SIMPLE));
      webJ2ee22To24TransformerSimple = createTransformer(loader.getResourceAsStream(WEB_J2EE_ENGINE_2_2_TO_2_4_XSL_SIMPLE));
      web24Transformer = createTransformer(loader.getResourceAsStream(WEB_APP_2_4_XSL));
      webJ2ee24To24Transformer = createTransformer(loader.getResourceAsStream(WEB_J2EE_ENGINE_2_4_TO_2_4_XSL));//this is only for migration from MS1/2 to MS3
      webJ2ee23To24Transformer = createTransformer(loader.getResourceAsStream(WEB_J2EE_ENGINE_2_3_TO_2_4_XSL));
      webJ2ee22To24Transformer = createTransformer(loader.getResourceAsStream(WEB_J2EE_ENGINE_2_2_TO_2_4_XSL));
    } catch (TransformerConfigurationException e) {
      throw new RuntimeException(e);
    }
  }//end of constructor

  /*
   * @see com.sap.appchecker.lib.descriptors.IJ2EEDescriptorConverter#getType()
   */
  public int getType() {
    return WEB;
  }//end of getType()

  /*
   * @see com.sap.appchecker.lib.descriptors.IJ2EEDescriptorConverter#getTransformer(java.lang.String, org.w3c.dom.Document)
   */
  public void convert(ConversionContext context) throws ConversionException {
    // parse all descriptors first
    super.convert(context);

    Document webDoc = context.getConvertedDocument(WEB_FILENAME);
    Document webJ2EEDoc = context.getConvertedDocument(WEBJ2EE_FILENAME);

    // now do the actual conversion (if necessary)

    if (webDoc != null) {
      convertWebIfNeeded(webDoc, context);
    } else {
      throwConversionException(new IllegalStateException("Cannot find [web.xml] file."), WEB_FILENAME);
    }

    if (webJ2EEDoc != null) {
      convertWebJ2EEIfNeeded(webJ2EEDoc, context);
    } else {
      Document doc = context.getConvertedDocument(WEBJ2EE_FILENAME_2_2);
      if (doc != null) {
        context.setConvertedDocument(WEBJ2EE_FILENAME, doc);
      }
    }

  }//end of convert(ConversionContext context)

  protected void convertWebJ2EEIfNeeded(Document webJ2EEDoc, ConversionContext context) throws ConversionException {
    String version = getWebAppVersion(webJ2EEDoc);

    if ("unknown".equals(version)) {
      throwConversionException(new IllegalStateException("Unknown descriptor version: [" + version + "]."), WEBJ2EE_FILENAME);
    }

    String webVersion = context.getUnconvertedJ2EEVersion();
    if (webVersion != null) {
      if ((webVersion.equals(J2EE_1_4) && !version.equals("2.4")) || (version.equals("2.4") && !(webVersion.equals(J2EE_1_4) || webVersion.equals(J2EE_1_5)))) {
        throwConversionException(new IllegalStateException("Cannot mix the versions of web deployment descriptors."), WEBJ2EE_FILENAME);
      }
    }

    String mode = (String) context.getAttribute("mode");

    if (version.equals("2.4")) {
      context.setUnconvertedJ2EEVersion(J2EE_1_4);
      if (mode == null || mode.equals("simple")) {
        // no conversion necessary.
        return;
      }
    } else {
      context.setUnconvertedJ2EEVersion(J2EE_1_3);

      Document doc = context.getConvertedDocument(WEBJ2EE_FILENAME_2_2);
      if (doc != null) {
        //This case means that there is web.xml 2.2 compatible and web-j2ee-engine.xml
        //We must merge tags in web.xml, that are additional to it, with tags in web-j2ee-engine.xml
        webJ2EEDoc = mergeDocuments(doc, webJ2EEDoc,
          new String[][]{{"resource-ref", "res-ref-name"}, {"response-status", "code"}, {"max-sessions", "max-sessions"}, {"cookie", "type"}});
      }

      Element root = webJ2EEDoc.getDocumentElement();
      createSubElement(webJ2EEDoc, root, "2.3", "spec-version");
    }

    if (mode == null || mode.equals("simple")) {
      context.setConvertedDocument(WEBJ2EE_FILENAME, transform(WEBJ2EE_FILENAME, webJ2EEDoc, webJ2ee23To24TransformerSimple, context));
    } else if (mode.equals("extended")) {
      if ("2.4".equals(version)) {
        context.setConvertedDocument(WEBJ2EE_FILENAME, transform(WEBJ2EE_FILENAME, webJ2EEDoc, webJ2ee24To24Transformer, context));
      } else {
        context.setConvertedDocument(WEBJ2EE_FILENAME, transform(WEBJ2EE_FILENAME, webJ2EEDoc, webJ2ee23To24Transformer, context));
      }
    }
  }//end of convertWebJ2EEIfNeeded(Document webJ2EEDoc, ConversionContext context)

  protected void convertWebIfNeeded(Document webDoc, ConversionContext context) throws ConversionException {
    String version = getWebAppVersion(webDoc);

    if ("unknown".equals(version)) {
      throwConversionException(new IllegalStateException("Unknown descriptor version: [" + version + "]."), WEB_FILENAME);
    }

    if ("2.4".equals(version)) {
      // no conversion necessary
      context.setUnconvertedJ2EEVersion(J2EE_1_4);
      return;
    } else if ("2.5".equals(version)) {
      // no conversion necessary
      context.setUnconvertedJ2EEVersion(J2EE_1_5);
      return;
    } else if ("2.3".equals(version)) {
      context.setUnconvertedJ2EEVersion(J2EE_1_3);
    } else if ("2.2".equals(version)) {
      context.setUnconvertedJ2EEVersion(J2EE_1_2);

      Element root = webDoc.getDocumentElement();
      createSubElement(webDoc, root, "2.2", "spec-version");
    }

    String mode = (String) context.getAttribute("mode");

    if (mode == null || mode.equals("simple")) {
      context.setConvertedDocument(WEB_FILENAME, transform(WEB_FILENAME, webDoc, web24TransformerSimple, context));
      if ("2.2".equals(version)) {
        context.setConvertedDocument(WEBJ2EE_FILENAME_2_2, transform(WEBJ2EE_FILENAME, webDoc, webJ2ee22To24TransformerSimple, context));
      }
    } else if (mode.equals("extended")) {
      context.setConvertedDocument(WEB_FILENAME, transform(WEB_FILENAME, webDoc, web24Transformer, context));
      if ("2.2".equals(version)) {
        context.setConvertedDocument(WEBJ2EE_FILENAME_2_2, transform(WEBJ2EE_FILENAME, webDoc, webJ2ee22To24Transformer, context));
      }
    }
  }//end of convertWebIfNeeded(Document webDoc, ConversionContext context)

  protected static String getWebAppVersion(Document document) {
    String version = "unknown";
    DocumentType docType = document.getDoctype();
    if (docType != null) {
      String systemID = docType.getSystemId();
      if (systemID != null) {
        if (systemID.endsWith(SYSTEM_ID_2_2)) {
          version = "2.2";
        } else if (systemID.endsWith(SYSTEM_ID_2_3) || systemID.endsWith(SYSTEM_ID_2_3_ADD)) {
          version = "2.3";
        } else {
          version = "2.2";
        }
      } else {
        version = "2.2";
      }
    } else {
      Element root = document.getDocumentElement();
      String vers = root.getAttribute("version");
      if (vers != null) {
        version = vers;
      }
      if ((root.getAttribute("xsi:noNamespaceSchemaLocation").equals(SYSTEM_ID_2_4_ADD))) {
        version = "2.4";
      }
    }
    return version;
  }//end of getWebAppVersion(Document document)

  protected static Element createSubElement(Document document, Element element, String value, String elementName) {
    if (value == null) {
      value = new String("");
    }
    Element subElement = document.createElement(elementName);
    Text textNode = document.createTextNode(value);
    subElement.appendChild(textNode);
    element.insertBefore(subElement, element.getFirstChild());
    return subElement;
  }//end of createSubElement(Document document, Element element, String value, String elementName)

  /**
   * Merge source and target document.
   * When there is an element in source that is presented in target,
   * then the element in target document wins.
   *
   * @param source
   * @param target
   * @param mergeList the first element is the tag name,
   *                  the second element is key tag name in this tag
   * @return resulted document after merging
   */
  private Document mergeDocuments(Document source, Document target, String[][] mergeList) {
    Element root = (target.getDocumentElement());

    NodeList sourceList = null;
    NodeList targetList = null;

    for (int j = 0; j < mergeList.length; j++) {
      String nodeName = mergeList[j][0];
      String keyNode = mergeList[j][1];

      sourceList = source.getElementsByTagName(nodeName);
      targetList = target.getElementsByTagName(nodeName);
      if (sourceList != null && sourceList.getLength() > 0) {
        if ((targetList != null && targetList.getLength() == 0) || targetList == null) {
          for (int i = 0; i < sourceList.getLength(); i++) {
            if (sourceList.item(i).getNodeType() == Node.ELEMENT_NODE) {
              if (!nodeName.equals("cookie")) {
                if (!nodeName.equals(keyNode)) {
                  root.appendChild(createElement(sourceList.item(i), nodeName, target));
                } else {
                  createSubElement(target, root, sourceList.item(i).getFirstChild().getNodeValue(), nodeName);
                }
              } else {
                Element elem = target.createElement("cookie-config");
                elem.appendChild(createElement(sourceList.item(i), nodeName, target));
                root.appendChild(elem);
              }
            }
          }
        } else if (!nodeName.equals(keyNode)) {
          for (int i = 0; i < sourceList.getLength(); i++) {
            if (sourceList.item(i).getNodeType() == Node.ELEMENT_NODE) {
              if (!isContainNode(targetList, sourceList.item(i), keyNode)) {
                if (!nodeName.equals("cookie")) {
                  root.appendChild(createElement(sourceList.item(i), nodeName, target));
                } else {
                  Element elem = (Element) (target.getElementsByTagName("cookie-config")).item(0);
                  elem.appendChild(createElement(sourceList.item(i), nodeName, target));
                }
              }
            }
          }
        }
      }
    }

    return target;
  }//end of mergeDocuments(Document first, Document second)

  private Element createElement(Node node, String keyNode, Document mainDocument) {
    Element resultElement = mainDocument.createElement(keyNode);
    NodeList childList = node.getChildNodes();
    Node idNode = null;
    String tagName;
    String tagValue;
    Element nextElement;
    Text textNode;
    for (int i = 0; childList != null && i < childList.getLength(); i++) {
      idNode = childList.item(i);
      if (idNode.getNodeType() == Node.ELEMENT_NODE) {
        tagName = ((Element) idNode).getTagName();
        tagValue = idNode.getFirstChild().getNodeValue();
        nextElement = mainDocument.createElement(tagName);
        textNode = mainDocument.createTextNode(tagValue);
        nextElement.appendChild(textNode);
        resultElement.appendChild(nextElement);
      }
    }
    return resultElement;
  }//end of createElement(Node node, String keyNode, Document mainDocument)

  private boolean isContainNode(NodeList targetNodeList, Node item, String tagName) {
    boolean result = false;
    NodeList childList = ((Element) item).getElementsByTagName(tagName);
    Node idNode = childList.item(0);
    String tagValue = idNode.getFirstChild().getNodeValue();
    String itemTagValue;
    for (int i = 0; i < targetNodeList.getLength(); i++) {
      childList = ((Element) targetNodeList.item(i)).getElementsByTagName(tagName);
      idNode = childList.item(0);
      itemTagValue = idNode.getFirstChild().getNodeValue();
      if (tagValue.equals(itemTagValue)) {
        return true;
      }
    }
    return result;
  }//end of isContainNode(NodeList targetNodeList, Node item, String tagName)

}//end of class