package com.sap.engine.lib.xml.util;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;

import javax.xml.transform.OutputKeys;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.sap.engine.lib.xml.dom.DOMImplementationImpl;
import com.sap.engine.lib.xsl.xslt.output.DocHandlerSerializer;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      August 2001
 */
public final class DOMSerializer {

  private final Properties p = new Properties();
  private DocHandlerSerializer h = new DocHandlerSerializer();
  private DOMToDocHandler d = new DOMToDocHandler();

  public DOMSerializer() {
    p.setProperty(OutputKeys.METHOD, "xml");
    p.setProperty(OutputKeys.VERSION, "1.0");
    p.setProperty(OutputKeys.ENCODING, "UTF-8");
    p.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    p.setProperty(OutputKeys.STANDALONE, "");
    p.setProperty(OutputKeys.DOCTYPE_PUBLIC, "");
    p.setProperty(OutputKeys.DOCTYPE_SYSTEM, "");
    p.setProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "");
    p.setProperty(OutputKeys.INDENT, "yes");
    p.setProperty(OutputKeys.MEDIA_TYPE, "");
    h.setOutputProperties(p);
  }

  public void write(Node node, Writer w) throws Exception {
    h.setCloseOnEnd(false);
    h.setWriter(w);
    serialize(node);
  }

  public void write(Document doc, OutputStream out) throws Exception {
    write((Node) doc, out);
  }

  public void write(Node node, OutputStream out) throws Exception {
    h.setCloseOnEnd(false);
    h.setOutputStream(out);
    serialize(node);
  }

  public void write(Node node, String systemId) throws Exception {
    h.setCloseOnEnd(true);
    //h.setWriter(new FileWriter(systemId));
    h.setOutputStream(new FileOutputStream(systemId));
    serialize(node);
  }

  private void serialize(Node node) throws Exception {
    if (node.getNodeType() == Node.DOCUMENT_NODE) {
      d.process(node, h);
    } else {
      h.startDocument();
      d.process(node, h);
      h.endDocument();
    }
  }

  public void setOutputProperty(String name, String value) {
    p.setProperty(name, value);
    h.setOutputProperties(p);
  }

//  public static void main(String[] args) throws Exception {
//    DOMImplementation di = new DOMImplementationImpl();
//    Document d = di.createDocument(null, "", null);
//    Element a = d.createElementNS("http://www.u.com", "u:a");
//    Element b = d.createElementNS("http://www.v.com", "v:a");
//    //d.appendChild(a);
//    a.appendChild(b);
//    DOMSerializer serializer = new DOMSerializer();
//    serializer.write(a, System.out); //$JL-SYS_OUT_ERR$
//  }

}

