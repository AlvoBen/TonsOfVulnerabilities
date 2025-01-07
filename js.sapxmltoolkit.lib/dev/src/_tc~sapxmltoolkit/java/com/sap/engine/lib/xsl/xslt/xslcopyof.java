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
package com.sap.engine.lib.xsl.xslt;

/**
 *
 <!ELEMENT xsl:copy-of EMPTY>
 <!ATTLIST xsl:copy-of select %expr; #REQUIRED>
 *
 * @author Vladimir Savtchenko   e-mail: vladimir.savchenko@sap.com
 * @version 0.0.1
 *
 *
 * First Edition: 17.01.2001
 *
 */
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.DTM;
import com.sap.engine.lib.xsl.xpath.ETObject;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.IntArrayIterator;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

public final class XSLCopyOf extends XSLNode {

  private static final CharArray xmlns = new CharArray("xmlns");
  private static final CharArray xmlnsColon = new CharArray("xmlns:");

  private ETObject select = null;
  private InternalAttributeList attImpl = new InternalAttributeList();

  //  protected XSLNode clone(XSLStylesheet s, XSLNode p) throws XSLException {
  //    XSLCopyOf n = new XSLCopyOf(s, p);
  //    n.select = select.cloneIt();
  //
  //    getFirst().clone(s, n);
  //    if (getNext() != null) getNext().clone(s, p);
  //    return n;
  //  }
  public XSLCopyOf(XSLStylesheet owner, XSLNode parent) throws XSLException {
    super(owner, parent);
  }

  public XSLCopyOf(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent);
    select = owner.etBuilder.process(el.getAttribute("select"));
  }

  //private int[] children = new int[50];
  //private int pChildren = 0;
  private QName atname = new QName();

  protected void copyElementNode(DTM dtm, int node) throws XSLException {
    CharArray nodeName = dtm.name[node].rawname;
    CharArray uri = dtm.name[node].uri;
    //owner.stack = dtm.getAttributeNodes(node, owner.stack, owner.pStack);
    owner.stack = dtm.getAttributeAndNSNodes(node, owner.stack, owner.pStack);
    attImpl.clear();

    for (int i = owner.pStack; owner.stack[i] != -1; i++) {
      switch (dtm.nodeType[owner.stack[i]]) {
        case DTM.ATTRIBUTE_NODE: {
          atname.reuse(dtm.name[owner.stack[i]].rawname);
          attImpl.addAttribute(dtm.name[owner.stack[i]].uri, atname.prefix, atname.rawname, atname.localname, dtm.getStringValue(owner.stack[i]));
          break;
        }
        
        case DTM.NAMESPACE_NODE: {
          if (dtm.name[owner.stack[i]].rawname == null || dtm.name[owner.stack[i]].rawname.length() <= 0) {
            atname.reuse(xmlns.copy());
          } else {
            atname.reuse(xmlnsColon.copy().append(dtm.name[owner.stack[i]].rawname.toString()));
          }
          attImpl.addAttribute(dtm.name[owner.stack[i]].uri, atname.prefix, atname.rawname, atname.localname, dtm.getStringValue(owner.stack[i]));
          break;
        }
      } 
    }

    owner.getOutputProcessor().startElement(uri, nodeName, nodeName, attImpl);
    owner.stack = dtm.getChildNodes(node, owner.stack, owner.pStack);
    int count = 0;

    while (owner.stack[owner.pStack + count] != -1) {
      count++;
    }

    int i = owner.pStack;
    owner.pStack += count + 1;

    for (; owner.stack[i] != -1; i++) {
      copyNode(dtm, owner.stack[i]);
    } 

    owner.pStack -= count + 1;
    owner.getOutputProcessor().endElement(uri, nodeName, nodeName);
  }

  protected CharArray tempCarr = new CharArray(20);

  protected void copyTextNode(DTM dtm, int node) throws XSLException {
    tempCarr.set(dtm.getStringValue(node));
    //    LogWriter.getSystemLogWriter().println("XSLCopyOf.copyTextNode(): data=" + tempCarr);
    owner.getOutputProcessor().characters(tempCarr, false);
  }

  protected void copyAttributeNode(DTM dtm, int node) throws XSLException {
    //  public void addAttribute(CharArray uri, CharArray prefix, CharArray qname, CharArray localName, CharArray value) throws XSLException {
    owner.getOutputProcessor().addAttribute(dtm.name[node].uri, dtm.name[node].prefix, dtm.name[node].rawname, dtm.name[node].localname, dtm.getStringValue(node));
  }

  //  private static void serializeDTM (DTM dtm) {
  //    try {
  //      dtm.initializeDOM();
  //      java.io.FileOutputStream out = new java.io.FileOutputStream("d:/xml/examples/xslt/aa.xml");
  //      Node doc = dtm.getDocument();
  //      out.write(doc.toString().getBytes());
  //    } catch (Exception e) {
  //      System.exit(1);
  //    }
  //  
  //  }
  protected void copyCommentNode(DTM dtm, int node) throws XSLException {
    //    LogWriter.getSystemLogWriter().println("XSLCopyOf.copyNode copying comment node:");
    owner.getOutputProcessor().comment(dtm.getStringValue(node));
  }

  protected void copyPINode(DTM dtm, int node) throws XSLException {
    owner.getOutputProcessor().processingInstruction(dtm.name[node].localname, dtm.getStringValue(node));
  }

  protected void copyNode(DTM dtm, int node) throws XSLException {
    //    LogWriter.getSystemLogWriter().println("XSLCopyOf.copyNode copying nodetype:" + (short)dtm.nodeType[node]);
    //    serializeDTM(dtm);
    //    LogWriter.getSystemLogWriter().println(dtm.getDocument());
    switch ((short) dtm.nodeType[node]) {
      case Node.ELEMENT_NODE: {
        copyElementNode(dtm, node);
        break;
      }
      case Node.PROCESSING_INSTRUCTION_NODE: {
        copyPINode(dtm, node);
        break;
      }
      case Node.TEXT_NODE: {
        copyTextNode(dtm, node);
        break;
      }
      case Node.COMMENT_NODE: {
        copyCommentNode(dtm, node);
        break;
      }
      //case Node.DOCUMENT_NODE: copyNode(dtm, dtm.firstChild[node]);
      case Node.DOCUMENT_NODE: {
        int[] documentElements = dtm.getChildNodes(node);

        //        int nodeSibling = dtm.firstChild[node];
        //        LogWriter.getSystemLogWriter().println("XSLCopyOf.copyNode: Document node encountered: nodeSibling=" + nodeSibling + ", nextSibling= " + dtm.nextSibling[nodeSibling]);
        //        
        //        while (nodeSibling != -1) {
        //          LogWriter.getSystemLogWriter().println("XSLCopyOf: coppying " + nodeSibling +" "+ dtm.name[nodeSibling] + " " + (short)dtm.nodeType[nodeSibling]);
        //          copyNode(dtm, nodeSibling);
        //          LogWriter.getSystemLogWriter().println("XSLCopyOf.copyNode: in cycle nodeSibling=" + nodeSibling + ", dtm.nextSibling[nodeSibling]= " + dtm.nextSibling[nodeSibling]);
        //          nodeSibling = dtm.nextSibling[nodeSibling];
        //        }
        //
        //        copyNode(dtm, dtm.firstChild[node]);
        for (int i = 0; i < documentElements.length; i++) {
          copyNode(dtm, documentElements[i]);
        } 

        break;
      }
     
      case Node.ATTRIBUTE_NODE: {
        copyAttributeNode(dtm, node);
        break;
      }
      default: {
        break;
      }
    }
  }

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    xcont.currentNode = this;
//    LogWriter.getSystemLogWriter().println("XSL:CopyOf : " + select.squery); 
//    java.util.Hashtable bindings = varContext.bindings;
//    LogWriter.getSystemLogWriter().println(bindings);
    XObject xo = owner.getXPathProcessor().process(select, xcont, varContext);

    if (xo.getType() == XNodeSet.TYPE) {
      IntArrayIterator iter = ((XNodeSet) xo).iterator();

      //      LogWriter.getSystemLogWriter().println("XSLCopyOf.process iter.hasNext() = " + iter.hasNext());
      while (iter.hasNext()) {
        //        LogWriter.getSystemLogWriter().println("XSLCopyOf.process copying node:");
        int next = iter.next();
        int typeOfNext = xcont.dtm.nodeType[next];
        //if (typeOfNext != DTM.ATTRIBUTE_NODE) {
          copyNode(xcont.dtm, next);
        //}
        //        LogWriter.getSystemLogWriter().println("XSLCopyOf.process node copied ???????????????:");
      }

      iter.close();
    } else {
      owner.getOutputProcessor().characters(xo.toXString().getValue(), true);
    }

    xo.close();
//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLCopyOf");
    //if (getFirst() != null) getFirst().print(ind + "  ");
    if (getNext() != null) {
      getNext().print(ind);
    }
  }

  public final static String[] REQPAR = {"select"};
  public final static String[] OPTPAR = {};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

}

