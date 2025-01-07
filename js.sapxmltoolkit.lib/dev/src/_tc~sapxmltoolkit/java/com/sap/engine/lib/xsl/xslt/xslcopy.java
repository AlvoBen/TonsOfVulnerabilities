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
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Element;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.DTM;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;

public final class XSLCopy extends XSLContentNode {

  private Vector attributeSets = new Vector();
  private Vector attrs = new Vector();
  private InternalAttributeList intatt = new InternalAttributeList();

  //  protected XSLNode clone(XSLStylesheet s, XSLNode p) throws XSLException {
  //    XSLCopy n = new XSLCopy(s, p);
  //    copyXSLNodeVector(n.attributeSets, attributeSets, s, p);
  //    copyXSLNodeVector(n.attrs, attrs, s, p);
  //
  //    getFirst().clone(s, n);
  //    if (getNext() != null) getNext().clone(s, p);
  //    return n;
  //
  //  }
  //
  public XSLCopy(XSLStylesheet owner, XSLNode parent) throws XSLException {
    super(owner, parent);
  }

  public XSLCopy(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent, el);
    if (el.getAttribute("use-attribute-sets") != null) {
      StringTokenizer tok = new StringTokenizer(el.getAttribute("use-attribute-sets"), "\040\012\015\011");

      while (tok.hasMoreElements()) {
        attributeSets.add(owner.getAttributeSet(tok.nextToken()));
      }
    }

    for (int i = 0; i < attributeSets.size(); i++) {
      XSLAttributeSet xslattrset = (XSLAttributeSet) attributeSets.get(i);
      Vector v = xslattrset.getAttributes();

      for (int j = 0; j < v.size(); j++) {
        XSLAttribute xslattr = (XSLAttribute) v.get(j);
        //        LogWriter.getSystemLogWriter().println("XSLCopy:adding attribute in init:"+  xslattr.getName());
        xslattr.setProcessByElement();
        int b;

        if ((b = findXSLAttribute(attrs, xslattr)) > -1) {
          attrs.set(b, xslattr);
        } else {
          attrs.add(xslattr);
        }
      } 
    } 
  }

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    //    LogWriter.getSystemLogWriter().println("XSLCopy name = " +  xcont.dtm.name[xcont.node] + ", #=" + xcont.node);
    switch (xcont.dtm.nodeType[xcont.node]) {
      case DTM.ELEMENT_NODE: {
        {
          CharArray nodeName = xcont.dtm.name[xcont.node].rawname;
          CharArray uri = xcont.dtm.name[xcont.node].uri;
          //LogWriter.getSystemLogWriter().println("XSLCopy type = " + xcont.dtm.nodeType[xcont.node]);
          XSLAttribute xsatt = null;
          intatt.clear();

          for (int i = 0; i < attrs.size(); i++) {
            xsatt = (XSLAttribute) attrs.get(i);
            xsatt.processStart();
            xsatt.process(xcont, node);
            xsatt.processEnd();
            //          LogWriter.getSystemLogWriter().println("XSLCopy:adding attribute:"+  xsatt.getName());
            intatt.addAttribute(xsatt.getNamespaceURI(), EMPTY_CARR, xsatt.getName(), xsatt.getName(), xsatt.getValue());
          } 

          owner.getOutputProcessor().startElement(uri, nodeName, nodeName, intatt);
          processFromFirst(xcont, node);
//          if (getFirst() != null) {
//            getFirst().process(xcont, node);
//          }
          owner.getOutputProcessor().endElement(uri, nodeName, nodeName);
        }

        break;
      }
      case DTM.TEXT_NODE: {
        {
          //LogWriter.getSystemLogWriter().println("--------------Copying text: -" + xcont.dtm.getStringValue(xcont.node] + "-");
          owner.getOutputProcessor().characters(xcont.dtm.getStringValue(xcont.node), false);
        }

        break;
      }
      case DTM.PROCESSING_INSTRUCTION_NODE: {
        {
          owner.getOutputProcessor().processingInstruction(xcont.dtm.name[xcont.node].rawname, xcont.dtm.getStringValue(xcont.node));
        }

        break;
      }
      case DTM.COMMENT_NODE: {
        {
          owner.getOutputProcessor().comment(xcont.dtm.getStringValue(xcont.node));
        }

        break;
      }
      case DTM.ATTRIBUTE_NODE: {
        {
          owner.getOutputProcessor().addAttribute(xcont.dtm.name[xcont.node].uri, xcont.dtm.name[xcont.node].prefix, xcont.dtm.name[xcont.node].rawname, xcont.dtm.name[xcont.node].localname, xcont.dtm.getStringValue(xcont.node));
        }

        break;
      }
      case DTM.DOCUMENT_NODE: {
        {
          processFromFirst(xcont, node);
//          if (getFirst() != null) {
//            getFirst().process(xcont, node);
//          }
        }

        break;
      }
      default: {
        break;
      }
    }

//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLCopy"); //$JL-SYS_OUT_ERR$
    if (getFirst() != null) {
      getFirst().print(ind + "  ");
    }
    if (getNext() != null) {
      getNext().print(ind);
    }
  }

  public Vector getAttributes() {
    return attrs;
  }

  public final static String[] REQPAR = {};
  public final static String[] OPTPAR = {"use-attribute-sets"};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

  public XSLNode changeOwner(XSLStylesheet newOwner) {
    super.changeOwner(newOwner);

    if (attrs != null) {
      for (int i = 0; i < attrs.size(); i++) {
        ((XSLNode) attrs.get(i)).changeOwner(newOwner);
      } 
    }

    return this;
  }

}

