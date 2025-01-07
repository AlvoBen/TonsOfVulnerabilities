/*
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.xml.parser.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Locale;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Non-optimized XML Comparator for ATS Tests
 * 
 * Copyright (c) 2004, SAP-AG
 * @author Alexander Zubev
 * @version 1.1, 2004-10-12
 */
public class XMLComparator {
  private static Vector getVectForAttribs(NamedNodeMap attribs) {
    Vector vect = new Vector();
    if (attribs != null) {
      int len = attribs.getLength();
      for (int i = 0; i < len; i++) {
        Attr attr = (Attr) attribs.item(i);
        String name = attr.getName();
        String prefix = attr.getPrefix();
        if (!"xmlns".equals(prefix) && !"xmlns".equals(name)) {
          vect.addElement(name);
        }
      }
    }
    return vect;
  }

  protected static boolean compareAttributes(NamedNodeMap newAttribs, NamedNodeMap oldAttribs) {
    Vector newAttribsVect = getVectForAttribs(newAttribs);
    Vector oldAttribsVect = getVectForAttribs(oldAttribs);
    
    int newLen = newAttribsVect.size();
    int oldLen = oldAttribsVect.size();
    if (newLen != oldLen) {
      return false;    
    }
    
    for (int i = 0; i < newLen; i++) {
      String name = newAttribsVect.elementAt(i).toString();
      
      Attr newAttr = (Attr) newAttribs.getNamedItem(name);
      String newValue = newAttr.getValue();
      
      Attr oldAttr = (Attr) oldAttribs.getNamedItem(name);
      if (oldAttr == null) {
        return false;
      }
      String oldValue = oldAttr.getValue();
      if (!newValue.equals(oldValue)) {
        return false; 
      }
    } 
    
    return true;
  }

  protected static boolean compareNodeLists(NodeList newList, NodeList oldList) {
    if (newList == null || oldList == null) {
      return newList == null && oldList == null;
    }
    
    int newLen = newList.getLength();
    int oldLen = oldList.getLength();
    if (newLen != oldLen) {
      return false;
    }
    
    for (int i = 0; i < newLen; i++) {
      if (!compareNodes(newList.item(i), oldList.item(i))) {
        return false;
      }
    }
    
    return true;
  }
  
//  public static boolean compareDocuments(String newXMLStr, String oldXMLStr) {
//    
//  }
  
  public static boolean compareNodes(Node newNode, Node oldNode) {
    if (newNode == null || oldNode == null) {
      return newNode == null && oldNode == null;
    }
    newNode.normalize();
    oldNode.normalize();

    short newType = newNode.getNodeType();
    short oldType = oldNode.getNodeType();
    if (newType != oldType) {
      return false;
    }
    
    String newLocalName = newNode.getLocalName();
    String oldLocalName = oldNode.getLocalName();
    if (newLocalName == null) {
      if (oldLocalName != null) {
        return false;
      }
    } else if (!newLocalName.equals(oldLocalName)) {
      return false; 
    }
    
    String newNamespace = newNode.getNamespaceURI();
    String oldNamespace = oldNode.getNamespaceURI();
    if (newNamespace == null) {
      if (oldNamespace != null) {
        return false;
      }
    } else if (!newNamespace.equals(oldNamespace)) {
      return false;
    }
    
    NamedNodeMap newAttribs = newNode.getAttributes();
    NamedNodeMap oldAttribs = oldNode.getAttributes();
    if (!compareAttributes(newAttribs, oldAttribs)) {
      return false;
    }
    
    String newValue = newNode.getNodeValue();
    String oldValue = oldNode.getNodeValue();
    if (newValue == null) {
      if (oldValue != null) {
        return false;
      }
    } else if (oldValue == null) {
      return false;
    } else if (!newValue.trim().equals(oldValue.trim())) {
      return false;
    }
    
    NodeList newList = newNode.getChildNodes();
    NodeList oldList = oldNode.getChildNodes();
    if (!compareNodeLists(newList, oldList)) {
      return false;
    }
        
    return true;
  }

  private static boolean domCompare(File newXML, File oldXML) throws Throwable {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(false);    
    //factory.setAttribute("http://inqmy.org/sax/features/read-dtd", Boolean.FALSE);
    DocumentBuilder builder = factory.newDocumentBuilder();    
    Document newDoc = builder.parse(newXML);
    Document oldDoc = builder.parse(oldXML);
    
    return compareNodes(newDoc.getDocumentElement(), oldDoc.getDocumentElement());
  }

  private static boolean domCompare(InputStream newXML, InputStream oldXML) throws Throwable {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(false);    
    //factory.setAttribute("http://inqmy.org/sax/features/read-dtd", Boolean.FALSE);
    DocumentBuilder builder = factory.newDocumentBuilder();    
    Document newDoc = builder.parse(newXML, "nexXML");
    Document oldDoc = builder.parse(oldXML, "oldXML");
    
    return compareNodes(newDoc.getDocumentElement(), oldDoc.getDocumentElement());
  }
  
  protected static String getStringForXML(File xml) throws Exception {
    FileInputStream in = new FileInputStream(xml);
    try {
      return getStringForXML(in);
    } finally {
      in.close();
    }
  }

  protected static String getStringForXML(InputStream xml) throws Exception { 
    StringBuffer buf = new StringBuffer();
    LineNumberReader reader = new LineNumberReader(new InputStreamReader(xml)); //$JL-I18N$
    String line;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (line.length() > 0) {
        buf.append(line + ((line.endsWith(">")) ? "" : " ") );
      }
    }
    String str = buf.toString();
    int index = str.indexOf("<?xml");
    if (index == 0) { //XML starts with <?xml
      int index2 = str.indexOf("?>");
      String xmlDecl = str.substring(0, index2 + 2);
      str = xmlDecl.toLowerCase(Locale.ENGLISH) + str.substring(index2 + 2);
    }
    return str;
  }
  
  public static boolean compareXMLs(File newXML, File oldXML) throws Throwable {
    String newStr = getStringForXML(newXML);
    String oldStr = getStringForXML(oldXML);
    
    if (newStr.equals(oldStr)) {
      return true;
    } else {
      return domCompare(newXML, oldXML);
    }
  }

  private static byte[] getBytes(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] b = new byte[1024];
    int len;
    while ((len = in.read(b, 0, 1024)) != -1) {
      out.write(b, 0, len);
    }
    byte[] bytes = out.toByteArray();
    out.close();
    return bytes;
  }

  public static boolean compareXMLs(InputStream newXML, InputStream oldXML) throws Throwable {
    byte[] newBytes = getBytes(newXML);
    byte[] oldBytes = getBytes(oldXML);
    
    if (newBytes.length == oldBytes.length) {
      boolean theSame = true;
      for (int i = 0; i < newBytes.length; i++) {
        if (newBytes[i] != oldBytes[i]) {
          theSame = false;
          break;
        }
      }
      if (theSame) {
        return true;
      }
    }

    ByteArrayInputStream newIn = new ByteArrayInputStream(newBytes);
    ByteArrayInputStream oldIn = new ByteArrayInputStream(oldBytes);
    try {
      return domCompare(newIn, oldIn);
    } finally {
      newIn.close();
      oldIn.close();
    }
  }
  
  public static boolean compareXMLs(String newXMLStr, InputStream oldXMLStr) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(false);
    //factory.setAttribute("http://inqmy.org/sax/features/read-dtd", Boolean.FALSE);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document newDoc;
    ByteArrayInputStream newXMLStream = new ByteArrayInputStream(newXMLStr.getBytes()); //$JL-I18N$
    try {    
      newDoc = builder.parse(newXMLStream, newXMLStr);
    } finally {
      newXMLStream.close();
    }
    
    Document oldDoc = builder.parse(oldXMLStr, "oldXML");
    
    return compareNodes(newDoc.getDocumentElement(), oldDoc.getDocumentElement());
  }
}
