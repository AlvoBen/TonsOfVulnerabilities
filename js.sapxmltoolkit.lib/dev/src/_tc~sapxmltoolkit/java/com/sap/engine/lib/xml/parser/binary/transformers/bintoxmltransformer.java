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

import com.sap.engine.lib.xml.parser.tokenizer.XMLBinaryTokenReader;
import com.sap.engine.lib.xml.parser.tokenizer.XMLTokenReader;
import com.sap.engine.lib.xml.parser.tokenizer.XMLTokenWriterImpl;
import com.sap.engine.lib.xml.util.NestedException;
import org.xml.sax.Attributes;

import java.io.*;

/**
 * BXML to XML transformer
 * 
 * @author Vladimir Videlov
 * @version 7.10
 */
public class BinToXmlTransformer {
  public static PrintStream out;

  public static void main(String[] args) throws NestedException {
    new BinToDomTransformer().transform(args[0], args[1]);
  }

  public boolean transform(String path, String target) throws NestedException {
    boolean result = false;

    XMLBinaryTokenReader reader;
    XMLTokenWriterImpl writer;

    String localName;
    String namespace;

    int state;

    InputStream input = null;
    OutputStream output = null;

    try {
      reader = new XMLBinaryTokenReader();
      writer = new XMLTokenWriterImpl();

      input = new FileInputStream(path);
      output = new FileOutputStream(target);

      reader.init(input);
      reader.begin();
      reader.moveToNextElementStart();

      writer.init(output, "UTF-8");
      writer.writeInitial();

      while ((state = reader.getState()) != XMLTokenReader.EOF) {
        switch (state) {
          case XMLTokenReader.STARTELEMENT: {
            localName = reader.getLocalName();
            namespace = reader.getURI();

            writer.enter(namespace, localName);
            Attributes atr = reader.getAttributes();

            int length = atr.getLength();

            for (int j = 0; j < length; j++) {
              String qname = atr.getQName(j);

              if (qname.startsWith("xmlns:")) {
                writer.setPrefixForNamespace(qname.substring(6), atr.getValue(j));
              } else {
                if (qname.equals("xmlns")) {
                  writer.writeAttribute(null, qname, atr.getValue(j));
                } else {
                  writer.writeAttribute(atr.getURI(j), atr.getLocalName(j), atr.getValue(j));
                }
              }
            }
            break;
          }
          case XMLTokenReader.CHARS: {
            writer.writeContent(reader.getValue());
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
        }

        reader.next();
      }

      reader.end();
      writer.close();

      result = true;
    } catch (Exception ex) {
      ex.printStackTrace();
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
