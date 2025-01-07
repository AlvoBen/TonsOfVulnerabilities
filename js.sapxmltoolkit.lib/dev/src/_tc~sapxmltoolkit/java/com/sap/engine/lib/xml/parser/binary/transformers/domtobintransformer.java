/*
 * Copyright (c) 2006 by SAP Labs Bulgaria.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.lib.xml.parser.binary.transformers;

import com.sap.engine.lib.jaxp.DocumentBuilderFactoryImpl;
import com.sap.engine.lib.xml.parser.binary.BinaryXmlWriter;
import com.sap.engine.lib.xml.parser.binary.exceptions.BinaryXmlException;
import com.sap.engine.lib.xml.util.NestedException;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * DOM to BXML transformer
 * 
 * @author Vladimir Videlov
 * @version 7.10
 */
public class DomToBinTransformer {
  public static PrintStream out;

  public static void main(String[] args) throws NestedException {
    new DomToBinTransformer().transform(args[0], args[1]);
  }

  public void transform(String path, String target) throws NestedException {
    BinaryXmlWriter writer = null;

    try {
      writer = new BinaryXmlWriter(new FileOutputStream(target));

      DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(path);

      Element root = doc.getDocumentElement();
      browseElement(writer, root);
    } catch (Exception ex) {
      throw new NestedException(ex);
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  public void browseElement(BinaryXmlWriter writer, Node node) throws BinaryXmlException, IOException {
    String localName = node.getLocalName();
    String ns = node.getNamespaceURI();
    String prefix = node.getPrefix();

    short type = node.getNodeType();

    if (type == Node.ELEMENT_NODE || type == Node.DOCUMENT_NODE) {
      if (ns != null && !ns.equals("") && prefix != null && !prefix.equals(""))
        writer.writeStartElement(localName, ns);
      else
        writer.writeStartElement(localName);
    } else if (type == Node.TEXT_NODE) {
      writer.writeText(node.getNodeValue());
      return;
    } else if (type == Node.COMMENT_NODE) {
      writer.writeComment(node.getNodeValue());
      return;
    } else if (type == Node.PROCESSING_INSTRUCTION_NODE) {
      writer.writeProcessingInstruction(node.getNodeName(), node.getNodeValue());
    }

    NamedNodeMap attributes = node.getAttributes();

    if (attributes != null) {
      for (int i = 0; i < attributes.getLength(); i++) {
        Node attribute = attributes.item(i);
  
        localName = attribute.getLocalName();
        ns = attribute.getNamespaceURI();
        String value = attribute.getNodeValue();

        //if (!attribute.getPrefix().equals("xmlns"))
        if (ns != null && !ns.equals("")) {
          writer.writeAttribute(localName, ns, value);
        }
        else {
          writer.writeAttribute(localName, value);
        }
      }
    }

    NodeList childs = node.getChildNodes();

    if (childs != null) {
    for (int i = 0; i < childs.getLength(); i++) {
      Node child = childs.item(i);
      browseElement(writer, child);
    }
    }

    writer.writeEndElement();

    /***
    Node sibling = node.getNextSibling();

    if (sibling != null) {
      browseElement(writer, sibling);
    }
    ***/
  }
}
