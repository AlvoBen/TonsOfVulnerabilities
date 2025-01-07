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

import org.w3c.dom.Element;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;

public final class XSLMessage extends XSLContentNode {

  private boolean bTerminate = false;
  private CharArray message = new CharArray();

  public XSLMessage(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent, el);
    if (el.getAttribute("terminate").equals("yes") || el.getAttribute("terminate").equals("true")) {
      bTerminate = true;
    }
  }

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    //    LogWriter.getSystemLogWriter().println("XFMessage ---------------------------");
    owner.getOutputProcessor().startAttributeValueProcessing(message);
    owner.getOutputProcessor().setDisableOutputEscaping(true);
    processFromFirst(xcont, node);
//    if (getFirst() != null) {
//      getFirst().process(xcont, node);
//    }
    owner.getOutputProcessor().setDisableOutputEscaping(false);
    owner.getOutputProcessor().stopAttributeValueProcessing();

    //    LogWriter.getSystemLogWriter().println("XFMessage :" + message);
    if (bTerminate == true) {
      throw new XSLException(message.toString());
    } else {
      owner.sendWarning(message.toString());
    }

//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {

  }

  public final static String[] REQPAR = {};
  public final static String[] OPTPAR = {"terminate"};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

}

