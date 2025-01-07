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
import com.sap.engine.lib.xml.parser.binary.BinaryXmlReader;
import com.sap.engine.lib.xml.parser.binary.common.EntryTypes;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.util.NestedException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * BXML to DOM transformer
 * 
 * @author Vladimir Videlov
 * @version 7.10
 */
public class BinToDomTransformer {
  public static PrintStream out;

  public static void main(String[] args) throws NestedException {
    new BinToDomTransformer().transform(args[0], args[1]);
  }

  public boolean transform(String path, String target) throws NestedException {
    boolean result = true;

    BinaryXmlReader reader = null;
    FileOutputStream outStream = null;

    try {
      reader = new BinaryXmlReader(new FileInputStream(path));

      DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.newDocument();

      Node root = doc.createDocumentFragment();
      doc.appendChild(root);
      readElement(reader, doc, root);

      // Obtaining an instance of the factory
      TransformerFactory transformFactory = TransformerFactory.newInstance();
      Transformer transformer = transformFactory.newTransformer();
      //transformer.setOutputProperty(OutputKeys.INDENT, "yes");

      // dom -> stream
      outStream = new FileOutputStream(target);
      transformer.transform(new DOMSource(root), new StreamResult(outStream));
    } catch (Exception ex) {
      throw new NestedException(ex);
    } finally {
      if (reader != null) {
        reader.close();
      }

      if (outStream != null) {
        try {
          outStream.close();
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    }

    return result;
  }

  public void readElement(BinaryXmlReader reader, Document doc, Node node) throws Exception {
      while (reader.read()) {
        short type = reader.getNodeType();

        if (type == Node.ELEMENT_NODE || type == Node.DOCUMENT_NODE) {
          if (reader.isStartElement) {
            Node child = doc.createElement(reader.getQName());
            node.appendChild(child);
            readElement(reader, doc, child);
          }
          else {
            return;
          }
        } else if (type == Node.TEXT_NODE) {
          Node text = doc.createTextNode(getTextValue(reader));
          node.appendChild(text);
        } else if (type == Node.COMMENT_NODE) {
          Node comment = doc.createComment(getTextValue(reader));
          node.appendChild(comment);
        } else if (type == Node.ATTRIBUTE_NODE) {
          Node attribute = doc.createAttribute(reader.getQName());
          attribute.setNodeValue(getTextValue(reader));
          node.getAttributes().setNamedItem(attribute);
        } else if (type == Node.PROCESSING_INSTRUCTION_NODE) {
          Node pi = doc.createProcessingInstruction(reader.getLocalName(), getTextValue(reader));
          node.appendChild(pi);
        }
      }
    }

  public static String getTextValue(BinaryXmlReader reader) throws UnsupportedEncodingException {
    String result = null;

    switch (reader.getValueType()) {
      case EntryTypes.CharArray: {
        result = reader.getTextValue();
        break;
      }
      case EntryTypes.Binary: {
        result = new String((byte[]) reader.getValue(), "UTF-8");
        break;
      }
      case EntryTypes.Ref: {
        break;
      }
    }

    if (result != null) {
      result = result.replace('\r', ' ');
    }

    return result;
  }

  public static CharArray getTextValue0(BinaryXmlReader reader) throws UnsupportedEncodingException {
    CharArray result = null;

    switch (reader.getValueType()) {
      case EntryTypes.CharArray: {
        result = reader.getTextValue0();
        break;
      }
      case EntryTypes.Binary: {
        result = new CharArray(new String((byte[]) reader.getValue(), "UTF-8"));
        break;
      }
      case EntryTypes.Ref: {
        break;
      }
    }

    if (result != null) {
      result = result.replace('\r', ' ');
    }

    return result;
  }
}
