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

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;

public final class XSLComment extends XSLContentNode {

  //private Vector useAttributeSets = new Vector();
  //private Vector attrs = null;
  //private boolean isXSLElement = false;
  //  protected XSLNode clone(XSLStylesheet s, XSLNode p) throws XSLException {
  //    XSLComment n = new XSLComment(s, p, null);
  //    
  //    getFirst().clone(s, n);
  //    if (getNext() != null) getNext().clone(s, p);
  //    return n;
  //    
  //  }
  public XSLComment(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent, el);
    //attrs = new Vector();
    //name = el.getNodeName();
    //if (name.equals("xsl:element")) {
    //  name = el.getAttribute("name");
    //  isXSLElement=true;
    // }
    //initAttributes();
  }

  //  public void initAttributes() {  
  //    XSLNode nd = getFirst(), ntemp = null;
  //    while (nd != null) {
  //      if (nd instanceof XSLAttribute) {
  //        if (nd.getPrev() != null) {
  //          nd.getPrev().setNext(nd.getNext());
  //        }
  //        ntemp = nd.getNext();
  //        if (nd.getNext() != null) {
  //          nd.getNext().setPrev(nd.getPrev());
  //        }
  //        nd.setPrev(null);
  //        nd.setNext(null);
  //        attrs.add(nd);
  //        nd = ntemp;
  //      } else {
  //        nd = nd.getNext();
  //      }
  //    }
  //  }
  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    CharArray ca = new CharArray();
    owner.getOutputProcessor().startAttributeValueProcessing(ca);
    owner.getOutputProcessor().setDisableOutputEscaping(true);
    processFromFirst(xcont, node);
//    if (getFirst() != null) {
//      getFirst().process(xcont, node);
//    }
    owner.getOutputProcessor().setDisableOutputEscaping(false);
    owner.getOutputProcessor().stopAttributeValueProcessing();
    //    LogWriter.getSystemLogWriter().println("XSLComment:" + ca);
    owner.getOutputProcessor().comment(ca);
//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLComment:"); //$JL-SYS_OUT_ERR$
    if (getFirst() != null) {
      getFirst().print(ind + "  ");
    }
    if (getNext() != null) {
      getNext().print(ind);
    }
  }

  public final static String[] REQPAR = {};
  public final static String[] OPTPAR = {};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

}

