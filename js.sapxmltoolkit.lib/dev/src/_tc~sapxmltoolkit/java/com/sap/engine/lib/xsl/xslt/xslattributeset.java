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
//import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.dom.TextImpl;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;

public final class XSLAttributeSet extends XSLNode {

  private String name = null;
  protected Vector attributeSets = new Vector();
  protected Vector attrs = new Vector();

  //  protected XSLNode clone(XSLStylesheet s, XSLNode p) throws XSLException {
  //    XSLAttributeSet n = new XSLAttributeSet(s);
  //    n.name = name;
  //    copyXSLNodeVector(n.attributeSets, attributeSets, s, p);
  //    copyXSLNodeVector(n.attrs, attrs, s, p);
  //
  //    getFirst().clone(s, n);
  //    if (getNext() != null) getNext().clone(s, p);
  //    return n;
  //
  //  }
  public XSLAttributeSet(XSLStylesheet owner) throws XSLException {
    super(owner, null);
  }

  public XSLAttributeSet(XSLStylesheet owner, Element el) throws XSLException {
    super(owner, null);
    name = el.getAttribute("name");

    if (el.getAttribute("use-attribute-sets") != null) {
      StringTokenizer tok = new StringTokenizer(el.getAttribute("use-attribute-sets"), "\040\012\015\011");

      while (tok.hasMoreElements()) {
        attributeSets.add(owner.getAttributeSet(tok.nextToken()));
      }
    }

//    Hashtable hashattr = new Hashtable();

    for (int i = 0; i < attributeSets.size(); i++) {
      XSLAttributeSet xslattrset = (XSLAttributeSet) attributeSets.get(i);
      Vector v = xslattrset.getAttributes();

      for (int j = 0; j < v.size(); j++) {
        XSLAttribute xslattr = (XSLAttribute) v.get(j);
        xslattr.setProcessByElement();
        int b;

        if ((b = findXSLAttribute(attrs, xslattr)) > -1) {
          attrs.set(b, xslattr);
        } else {
          attrs.add(xslattr);
        }
      } 
    } 

    NodeList nl = el.getChildNodes();

    for (int i = 0; i < nl.getLength(); i++) {
      if (nl.item(i).getNodeType() == Node.TEXT_NODE && ((TextImpl) nl.item(i)).isWhiteSpace() == false) {
        throw new XSLException("Text nodes are not allowed in Attribute-set definition");
      } else if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
        if (nl.item(i).getNamespaceURI() == null || !nl.item(i).getNamespaceURI().equals(XSLStylesheet.sXSLNamespace) || nl.item(i).getLocalName() == null || !nl.item(i).getLocalName().equals("attribute")) {
          throw new XSLException("Only \'attribute\' nodes from the XSL-namespace are allowed in attribute-set definition");
        }
      } else if (nl.item(i).getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }

      XSLAttribute xslattr = new XSLAttribute(owner, this, (Element) nl.item(i));
      int b;

      if ((b = findXSLAttribute(attrs, xslattr)) > -1) {
        attrs.set(b, xslattr);
      } else {
        attrs.add(xslattr);
      }
    } 
  }

  public Vector getAttributes() {
    return attrs;
  }

  public String getName() {
    return name;
  }

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    processFromFirst(xcont, node);
    //getFirst().process(xcont, node);

    for (int i = 0; i < attributeSets.size(); i++) {
      XSLAttributeSet attribSet = owner.getAttributeSet((String) attributeSets.get(i));
      attribSet.processStart(); 
      attribSet.process(xcont, node);
      attribSet.processEnd();
    } 
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLAttributeSet  name = " + name);
    if (getFirst() != null) {
      getFirst().print(ind + "  ");
    }
    if (getNext() != null) {
      getNext().print(ind);
    }
  }

  public final static String[] REQPAR = {"name"};
  public final static String[] OPTPAR = {"use-attribute-sets"};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

}

