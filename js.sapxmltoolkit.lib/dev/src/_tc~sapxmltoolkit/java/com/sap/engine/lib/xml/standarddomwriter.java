/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.xml;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * StandardDOMWriter is the basic class for printing a DOM structure in a xml file.
 *
 * @deprecated Use JAXP
 */
@Deprecated
public class StandardDOMWriter {
  protected Transformer transformer;
  private boolean canonical;

  // constructors
  public StandardDOMWriter() {
    TransformerFactory factory = TransformerFactory.newInstance();
    try {
      transformer = factory.newTransformer();
    } catch (TransformerConfigurationException tce) {
      throw new RuntimeException(thr2str(tce));
    }
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
  }

  public StandardDOMWriter(boolean canonical) {
    this();
    if (canonical) {
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    }
    this.canonical = canonical;
  }
  
  private static String thr2str(Throwable thr) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    thr.printStackTrace(pw);
    pw.flush();
    String msg = sw.toString();
    pw.close();
    return msg;
  }

  /**
   * Writes out the xml document
   *
   * @param doc the document to be written
   * @param desct the destination file  path
   * @param dtd  The Document Type Definition;
   *              it must conform to the XML specification, because
   *              the method <code>print</code> inserts this DTD
   *              without validating;
   * 
   * @deprecated Use JAXP
   */
  public void write(Document doc, String dest, String dtd) throws IOException {
    OutputStream out = new FileOutputStream(dest);
    write(doc, out, dtd);
    out.close();
  }
  
  /**
   * 
   * @param doc
   * @param stream
   * @param _dtd
   * @throws IOException
   * @deprecated Use JAXP
   */
  public void write(Document doc, OutputStream stream, String _dtd) throws IOException {
    write(doc, stream, _dtd, "UTF-8");
  }

  /**
   * Writes out the xml document
   *
   * @param doc the document to be written
   * @param out the output stream
   * @param dtd  the Document Type Definition;
   *              it must conform to the XML specification, because
   *              the method <code>print</code> inserts this DTD
   *              without validating;
   * @deprecated Use JAXP
   */
  public void write(Document doc, FileWriter out, String dtd) throws IOException {
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    if (canonical || dtd == null || dtd.trim().length() == 0) {
      try {
        transformer.transform(new DOMSource(doc), new StreamResult(out));
      } catch (TransformerException te) {
        throw new IOException(thr2str(te));
      }
    } else {
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      try {
        if (!canonical) {
          out.write("<?xml version='1.0' encoding='UTF-8'?>\n");  
          out.write(dtd);
          out.write("\n");
        }
        transformer.transform(new DOMSource(doc), new StreamResult(out));
      } catch (TransformerException te) {
        throw new IOException(thr2str(te));
      } finally {
        if (!canonical) {
          transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
      }
    }
  }
  
  /**
   * 
   * @param doc
   * @param out
   * @param dtd
   * @param encoding
   * @throws IOException
   * @deprecated Use JAXP
   */
  public void write(Document doc, OutputStream out, String dtd, String encoding) throws IOException {
    transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
    if (canonical || dtd == null || dtd.trim().length() == 0) {
      try {
        transformer.transform(new DOMSource(doc), new StreamResult(out));
      } catch (TransformerException te) {
        throw new IOException(thr2str(te));
      }
    } else {
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      try {
        if (!canonical) {
          out.write(("<?xml version='1.0' encoding='" + encoding + "'?>\n").getBytes()); //$JL-I18N$ 
          out.write(dtd.getBytes()); //$JL-I18N$
          out.write("\n".getBytes()); //$JL-I18N$
        }
        transformer.transform(new DOMSource(doc), new StreamResult(out));
      } catch (TransformerException te) {
        throw new IOException(thr2str(te));
      } finally {
        if (!canonical) {
          transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
      }
    }
  }

  /**
   * Writes out the xml document
   *
   * @param doc   the document to be written
   * @param dest  the destination file path 
   * @param _dtd  the Document Type Definition;
   *              it must conform to the XML specification, because
   *              the method <code>print</code> inserts this DTD
   *              without validating;
   * @param enc   the encoding 
   * @deprecated Use JAXP
   */
  public void write(Document doc, String dest, String _dtd, String enc) throws IOException {
    OutputStream out = new FileOutputStream(dest);
    write(doc, out, _dtd, enc);
    out.close();
  }
}

