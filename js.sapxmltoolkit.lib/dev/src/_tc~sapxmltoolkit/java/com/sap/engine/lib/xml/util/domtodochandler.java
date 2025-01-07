package com.sap.engine.lib.xml.util;

//import javax.xml.transform.TransformerException;

import org.w3c.dom.*;

import com.sap.engine.lib.xml.dom.BinaryTextImpl;
import com.sap.engine.lib.xml.parser.*;
//import com.sap.engine.lib.xml.dom.*;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.DTMFactory;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      August 2001
 */
public final class DOMToDocHandler {

  private NamespaceAppender h;
  private CharArray ca0 = new CharArray();
  private CharArray ca1 = new CharArray();
  private CharArray ca2 = new CharArray();
  private CharArray ca3 = new CharArray();
  private CharArray ca4 = new CharArray();
  private CharArray ca5 = new CharArray();
  private CharArray ca6 = new CharArray();
  private NamespaceAppender nsa = new NamespaceAppender();

  //  private static char[] semicolon = {';'};
  //  private static char[] amp = {'&'};
  public void process(Node x, DocHandler h) throws Exception {
    if (!(h instanceof NamespaceAppender)) {
      this.h = nsa.init(h);
    } else {
      this.h = (NamespaceAppender) h;
    }

    if (x.getNodeType() != Node.DOCUMENT_NODE) {
      this.h.startDocument();
      this.h.setNodeToHandler(x);
      process(x);
      this.h.endDocument();
    } else {
//      if (((Document) x).getDocumentElement() == null) {
//        throw new TransformerException("Invalid Document object passed! The document has no root element!");
//      }
      process(x);
    }
  }

  private void process(Node node) throws Exception {
    String uri = null;
    String localName = null;
    String name = null;
    boolean isEmpty = false;
    int t = node.getNodeType();

    // Before processing child nodes
    switch (t) {
      case Node.ELEMENT_NODE: {
        uri = node.getNamespaceURI();
        localName = node.getLocalName();
        name = node.getNodeName();
        ca1.set(localName);
        ca2.set(name);
        ca3.set(uri);
        h.startElementStart(ca3, ca1, ca2);
        h.setNodeToHandler(node);
        NamedNodeMap map = node.getAttributes();

        for (int i = 0; i < map.getLength(); i++) {
          Attr a = (Attr) map.item(i);
          ca3.set(a.getPrefix());
          ca4.set(a.getLocalName());
          ca5.set(a.getNodeName());
          ca6.set(a.getNodeValue());
          ca0.set(a.getNamespaceURI());
          h.addAttribute(ca0, ca3, ca4, ca5, "CDATA", ca6);
          h.setNodeToHandler(a);
        } 

        isEmpty = (node.getFirstChild() == null);
        h.startElementEnd(isEmpty);
        break;
      }
      case Node.ATTRIBUTE_NODE: {
        // Shouldn't happen
        break;
      }
      case Node.TEXT_NODE: // Falls through
      case Node.CDATA_SECTION_NODE: // CData sections are reported as text
      {
        ca0.clear();
        ca0.set(((CharacterData) node).getData());

        if (t == Node.CDATA_SECTION_NODE) {
          h.onCDSect(ca0);
          //ca0.insert(0, "<![CDATA[");
          //ca0.append("]]>");
        } else {
          h.charData(ca0, t == Node.CDATA_SECTION_NODE);
        }
        h.setNodeToHandler(node);

        return;
      }
      case BinaryTextImpl.TYPE:
      {
        h.onCustomEvent(BinaryTextImpl.TYPE, node);
        return;
      }      
      case Node.ENTITY_REFERENCE_NODE: {
        String referenceName = ((EntityReference) node).getNodeName();
        ca0.clear();
        ca0.append('&');
        ca0.append(referenceName);
        ca0.append(';');
        ca1.set(referenceName);
        h.onStartContentEntity(ca1, false);
        h.charData(ca0, true);
        h.onEndContentEntity(ca1);
        h.setNodeToHandler(node);
        return;
      }
      case Node.ENTITY_NODE: {
        //xxx
        break;
      }
      case Node.PROCESSING_INSTRUCTION_NODE: {
        ProcessingInstruction pi = (ProcessingInstruction) node;
        ca0.set(pi.getTarget());
        ca1.set(pi.getData());
        h.onPI(ca0, ca1);
        h.setNodeToHandler(node);
        return;
      }
      case Node.COMMENT_NODE: {
        Comment comment = (Comment) node;
        ca0.set(comment.getData());
        h.onComment(ca0);
        h.setNodeToHandler(node);
        return;
      }
      case Node.DOCUMENT_NODE: { //$JL-SWITCH$
        h.startDocument();
        h.setNodeToHandler(node);
      }
      case Node.DOCUMENT_FRAGMENT_NODE: {
        break;
      }
      case Node.DOCUMENT_TYPE_NODE: {
        DocumentType x = (DocumentType) node;

        if (x.getEntities().getLength() > 0) {
          ca0.set(x.getName());
          ca1.set(x.getPublicId());
          ca2.set(x.getSystemId());
          h.startDTD(ca0, ca1, ca2);
          h.setNodeToHandler(node);
          NamedNodeMap nm = x.getEntities();

          for (int m = 0; m < nm.getLength(); m++) {
            Entity ent = (Entity) nm.item(m);
            ca0.set(ent.getNodeName());
            ca1.set(ent.getNodeValue());
            //          LogWriter.getSystemLogWriter().println("DOMToDocHandler.process: creating entity: name=" + ent.getNodeName() + ", value=" + ent.getNodeValue());
            com.sap.engine.lib.xml.parser.helpers.Entity myent = new com.sap.engine.lib.xml.parser.helpers.Entity(ca0, ca1, false, new CharArray(ent.getPublicId()), new CharArray(ent.getSystemId()), new CharArray(ent.getNotationName()), null, null);
            h.onDTDEntity(myent);
            h.setNodeToHandler(ent);
          } 

          h.endDTD();
        }

        return;
      }
      //break;
      case Node.NOTATION_NODE: {
        return;
      }
      default: {
        break;
      }
    }

    // Processing child nodes
    for (Node c = node.getFirstChild(); c != null; c = c.getNextSibling()) {
      //      LogWriter.getSystemLogWriter().println("DOMToDOCHand;er.process. node to process is: 1. " + c.getClass());
      process(c);
      //      LogWriter.getSystemLogWriter().println("DOMToDOCHand;er.process. node to process is: 2. " + c.getClass());
    } 

    // After processing child nodes
    switch (t) {
      case Node.ELEMENT_NODE: {
        ca1.set(localName);
        ca2.set(name);
        ca3.set(uri);
        h.endElement(ca3, ca1, ca2, isEmpty);
        break;
      }
      case Node.ATTRIBUTE_NODE: {
        // Shouldn't happen
        break;
      }
      case Node.TEXT_NODE: // Falls through
      case Node.CDATA_SECTION_NODE: {
        // Shouldn't happen
        break;
      }
      case Node.ENTITY_REFERENCE_NODE: {
        //xxx
        break;
      }
      case Node.ENTITY_NODE: {
        //xxx
        break;
      }
      case Node.PROCESSING_INSTRUCTION_NODE: {
        // Shouldn't happen
        break;
      }
      case Node.COMMENT_NODE: {
        // Shouldn't happen
        break;
      }
      case Node.DOCUMENT_NODE: {
        h.endDocument();
        break;
      }
      case Node.DOCUMENT_TYPE_NODE: {
        //xxx
        break;
      }
      case Node.DOCUMENT_FRAGMENT_NODE: {
        //xxx
        break;
      }
      case Node.NOTATION_NODE: {
        //xxx
        break;
      }
      default: {
        break;
      }
    }
  }

}

