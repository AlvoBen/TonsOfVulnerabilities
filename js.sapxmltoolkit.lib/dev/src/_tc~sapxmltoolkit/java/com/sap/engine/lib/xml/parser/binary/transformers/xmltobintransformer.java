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

import com.sap.engine.lib.xml.parser.XMLParser;
import com.sap.engine.lib.xml.parser.binary.BinaryXmlWriter;
import com.sap.engine.lib.xml.parser.tokenizer.XMLTokenReader;
import com.sap.engine.lib.xml.parser.tokenizer.XMLTokenReaderImpl;
import com.sap.engine.lib.xml.parser.tokenizer.XMLBinaryTokenWriter;
import com.sap.engine.lib.xml.util.NestedException;
import org.xml.sax.Attributes;

import java.io.*;

/**
 * XML to BXML transformer
 * 
 * @author Vladimir Videlov
 * @version 7.10
 */
public class XmlToBinTransformer {
  public static PrintStream out;

  public static void main(String[] args) throws NestedException {
    new XmlToBinTransformer().transform(args[0], args[1]);
  }

public boolean transform(String path, String target) throws NestedException {
    boolean result = false;

    XMLBinaryTokenWriter writer;
    XMLTokenReaderImpl reader;

    InputStream input = null;
    OutputStream output = null;

    String localName;
    String namespace;

    try {
      input = new FileInputStream(path);
      output = new FileOutputStream(target);

      writer = new XMLBinaryTokenWriter(output);
      reader = new XMLTokenReaderImpl();

      reader.init(input);
      reader.begin();
      reader.moveToNextElementStart();

      while (reader.getState() != XMLTokenReader.EOF) {
        switch (reader.getState()) {
          case XMLTokenReader.STARTELEMENT: {
            localName = reader.getLocalName();
            namespace = reader.getURI();

            writer.enter(namespace, localName);
            Attributes atr = reader.getAttributes();

            int length = atr.getLength();

            for (int j = 0; j < length; j++) {
              String qname = atr.getQName(j);

              if (qname.startsWith("xmlns:")) {
                writer.writeAttribute(XMLParser.sXMLNSNamespace, qname.substring(6), atr.getValue(j));
              } else {
                if (qname.equals("xmlns")) {
                  writer.writeAttribute(XMLParser.sXMLNSNamespace, qname, atr.getValue(j));
                } else {
                  writer.writeAttribute(atr.getURI(j), atr.getLocalName(j), atr.getValue(j));
                }
              }
            }
            break;
          }
          case XMLTokenReader.CHARS: {
            if (reader.getValue().trim().length() > 0) {
              writer.writeContent(reader.getValue().trim());
            }
            break;
          }
          case XMLTokenReader.ENDELEMENT: {
            writer.leave();
            break;
          }
          case XMLTokenReader.COMMENT: {
            writer.writeComment(reader.getValue());
            break;
          }
          case XMLTokenReader.PI: {
            writer.writeContent("<?" + reader.getLocalName() + " " + reader.getValue() + " ?>");
            break;
          }
        }

        reader.next();
      }

      reader.end();

      writer.flush();
      writer.close();

      result = true;
    } catch (Exception ex) {
      throw new NestedException(ex);
    } finally {
      try {
        if (input != null) {
          input.close();
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }

      try {
        if (output != null) {
          output.close();
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }

    return result;
  }
}