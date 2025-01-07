package com.sap.engine.core.cache.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sap.engine.cache.core.impl.PluggableFramework;

/**
 * @author Petev, Petio, i024139
 */
class PluggableContainer implements EntityResolver {

  private static String DTD_PREFIX = "com/sap/engine/cache/spi/";

  private ClassLoader kernelLoader;

  private DocumentBuilder domParser;

  private List pluggableList;
  private Map holders;

  protected static ClassLoader pluggableLoader = null;

  protected void init() {
    kernelLoader = this.getClass().getClassLoader();
    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    domFactory.setValidating(false);
    try {
      domParser = domFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      CacheManagerImpl.traceT(e);
    }
    domParser.setEntityResolver(this);
  }

  protected void prepareList() {
    pluggableList = new ArrayList();
    pluggableList.add(kernelLoader.getResourceAsStream(DTD_PREFIX + "policy/impl/simpleLRU-policy.xml"));
    pluggableList.add(kernelLoader.getResourceAsStream(DTD_PREFIX + "storage/impl/hashMap-storage.xml"));
  }

  protected void createDocumentList() {
    Object[] array = pluggableList.toArray();
    pluggableList.clear();
    for (int i = 0; i < array.length; i++) {
      if (array[i] != null) {
        InputStream input = (InputStream )array[i];
        try {
          Document document = domParser.parse(input);
          pluggableList.add(document);
        } catch (SAXException e) {
          CacheManagerImpl.traceT(e);
        } catch (IOException e) {
          CacheManagerImpl.traceT(e);
        } finally {
          try {
            input.close();
          } catch (IOException e) {
            CacheManagerImpl.traceT(e);
          }
        }
      }
    }
  }

  public void processDocumentList() {
    holders = new HashMap();

    // process each document in a separate method
    Iterator docIterator = pluggableList.iterator();
    while (docIterator.hasNext()) {
      Document doc = (Document) docIterator.next();
      PluggableHolder holder = processSingleDocument(doc);
      initPluggable(holder);
    }

  }

  private PluggableHolder processSingleDocument(Document doc) {
    Element rootElement = doc.getDocumentElement();
    // Get the type of the pluggable
    String type = rootElement.getAttribute("type");
    int pluggableType;
    if ("eviction-policy".equals(type)) {
      pluggableType = PluggableHolder.TYPE_EVICTION_POLICY;
    } else if ("storage-plugin".equals(type)) {
      pluggableType = PluggableHolder.TYPE_STORAGE_PLUGIN;
    } else {
      // todo - log - wrong type
      return null;
    }

    // Get the class name of the pluggable
    NodeList list = rootElement.getElementsByTagName("base-class");
    if (list.getLength() > 1) {
      // todo log - too many base classes
      return null;
    }
    Node baseClass = list.item(0).getFirstChild();
    String className = baseClass.getNodeValue();
    if (className == null || "".equals(className)) {
      // todo log - no class name defined
    }

    // Get the name of the pluggable
    list = rootElement.getElementsByTagName("name");
    if (list.getLength() > 1) {
      // todo log - too many names
      return null;
    }
    Node baseName = list.item(0).getFirstChild();
    String name = baseName.getNodeValue();
    if (name == null || "".equals(name)) {
      // todo log - no name
    }

    // Get the name of the pluggable
    String fileName = null;
    list = rootElement.getElementsByTagName("name");
    if (list.getLength() > 1) {
      // todo log - too many names
      return null;
    }
    if (list.getLength() == 1) {
      Node propsFile = list.item(0).getFirstChild();
      fileName = propsFile.getNodeValue();
      if (fileName == null || "".equals(name)) {
        // todo log - no file name
      }
    }

    return new PluggableHolder(pluggableType, name, className, null); // todo - load properties
  }

  public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
    systemId = systemId.substring(systemId.lastIndexOf('/') + 1);
    InputStream dtdStream = kernelLoader.getResourceAsStream(DTD_PREFIX + systemId);
    InputSource src = new InputSource(dtdStream);
    src.setSystemId(systemId);
    return src;
  }

  public void shutdownPluggable(String name) {
  }

  public void initPluggable(PluggableHolder holder) {
    if (holder != null) {
      if (holder.init()) {
        holders.put(holder.getName(), holder);
        PluggableFramework.putPluggable(holder.getName(), holder.getInstance());
      }
    }
  }


}
