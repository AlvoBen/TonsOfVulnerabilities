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

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
//import com.sap.engine.lib.xsl.xpath.ETObject;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;

public final class XSLElement extends XSLContentNode {

  private CharArray name = new CharArray(15);
  private CharArray namespace = new CharArray(30);
  private Vector attributeSets = new Vector();
  private InternalAttributeList attImpl = new InternalAttributeList();
  private Vector attrs = null;
  private boolean isXSLElement = false;
//  private boolean hasLowLevelAttributes = false;
//  private ETObject etname = null;
  private AttributeValueTemplateHandler nameTemplate = new AttributeValueTemplateHandler();
  private AttributeValueTemplateHandler namespaceTemplate = new AttributeValueTemplateHandler();

  public XSLElement(XSLStylesheet owner, XSLNode parent) throws XSLException {
    super(owner, parent);
  }

  public XSLElement(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent, el);
    attrs = new Vector();
//    etname = null;
    String prefix = el.getPrefix();
    String namespaceUriStr = el.getNamespaceURI();
    if (prefix == null) {
      prefix = "";
    }
    if (namespaceUriStr == null) {
      namespaceUriStr = "";
    }

    if (owner.getNamespaceAliases().get(prefix) != null) {
      String prf = (String) owner.getNamespaceAliases().get(prefix);
      name.clear();
      name.append(prf);
      name.append(':');
      name.append(el.getLocalName());
      namespace.set(owner.getNamespaceHandler().get(new CharArray(prf)));
    } else {
      name.set(el.getNodeName());
      name.trim();
      namespace.set(namespaceUriStr);
      namespace.trim();

      if (el.getNamespaceURI() != null && el.getNamespaceURI().equals(XSLStylesheet.sXSLNamespace) && el.getLocalName().equals("element")) {
        name.set(el.getAttribute("name"));
        name.trim();

        if (el.getAttribute("namespace").length() > 0) {
          namespace.set(el.getAttribute("namespace"));
          namespace.trim();
        } else {
          CharArray defaultNS = owner.getNamespaceHandler().getDefault(); 
          if (defaultNS != null) {
            namespace.set(defaultNS);
          } else {
            namespace.clear();
          }
        }

        isXSLElement = true;
        //if (name.charAt(0) == '{' && name.charAt(name.length()-1) == '}') {
        //  etname = owner.etBuilder.process(name.getString().substring(1, name.length()-1));
        // }
      }
    }

    nameTemplate.init(name, owner.etBuilder);
    namespaceTemplate.init(namespace, owner.etBuilder);

    // literal result element
    if (nameTemplate.countTokens() == 1) {
      if (name.indexOf(':') > -1) {
        String sname = name.toString();
        String prf = sname.substring(0, sname.indexOf(':'));

        if (namespaceTemplate.countTokens() == 0) {
          namespaceTemplate.init(owner.getNamespaceHandler().get(new CharArray(prf)), owner.etBuilder);
        }
      }
    }

    //
    if (!isXSLElement) {
      NamedNodeMap elatt = el.getAttributes();
      XSLAttribute xsattr = null;
//      XSLText xstext = null;

      for (int i = 0; i < elatt.getLength(); i++) {
        Attr at = (Attr) elatt.item(i);
        //skip namespace attributes
        //        LogWriter.getSystemLogWriter().println(at.getNamespaceURI());
        //        LogWriter.getSystemLogWriter().println(XSLStylesheet.sXSLNamespace);
        //        if (at.getPrefix().equals("xmlns") || (at.getPrefix().length()==0 && at.getLocalName().equals("xmlns")) ||
        //            at.getNamespaceURI().equals(XMLParser.sXMLNamespace) || at.getNamespaceURI().equals(XSLStylesheet.sXSLNamespace) )
        //          continue;
        //        LogWriter.getSystemLogWriter().println("Adding attribute:" + at.getName() + " ns: " + at.getNamespaceURI());
        xsattr = new XSLAttribute(owner, this, at.getNamespaceURI(), at.getName(), at.getValue());
        xsattr.setProcessByElement();
        //xsattr.append(new XSLText(owner, xsattr, at.getValue()));
        //        LogWriter.getSystemLogWriter().println("XSLElement:adding attr:" + xsattr.getName() + ", =" + at.getName());
        attrs.add(xsattr);
      } 
    }

    String useAttributeSets = el.getAttribute("use-attribute-sets");
    if (useAttributeSets != null && useAttributeSets.length() > 0) {
      StringTokenizer tok = new StringTokenizer(useAttributeSets, "\040\012\015\011");

      while (tok.hasMoreElements()) {
        String setName = tok.nextToken();
        XSLAttributeSet set = owner.getAttributeSet(setName);
        if (set == null) {
          throw new XSLException("Cannot find attribute set with name: " + setName + ", xsl: "+ owner.getBaseURI());
        } else {
          attributeSets.add(set);
        }
      }
    }

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

    // removed due to problems when initializing attributes, and they contain variable referneces
    // to variables defined in the same element, but before the attribute
    // the processing model, allows that the attributes are dynamically added to the tree
    //initAttributes();
  }

//  public void initAttributes() throws XSLException {
//    XSLNode nd = getFirst(), ntemp = null;
//
//    while (nd != null) {
//      if (nd instanceof XSLAttribute) {
//        if (nd.getPrev() != null) {
//          nd.getPrev().setNext(nd.getNext());
//        }
//
//        ntemp = nd.getNext();
//
//        if (nd.getNext() != null) {
//          nd.getNext().setPrev(nd.getPrev());
//        }
//
//        nd.setPrev(null);
//        nd.setNext(null);
//        //nd.setParent(this);
//        ((XSLAttribute) nd).setProcessByElement();
//        //        attrs.add(nd);
//        int b;
//
//        if ((b = findXSLAttribute(attrs, (XSLAttribute) nd)) > -1) {
//          //          LogWriter.getSystemLogWriter().println("XSLElement:setting attr:" + ((XSLAttribute)nd).getName());
//          attrs.set(b, nd);
//        } else {
//          //          LogWriter.getSystemLogWriter().println("XSLElement:addingt attr:" + ((XSLAttribute)nd).getName());
//          attrs.add(nd);
//        }
//
//        nd = ntemp;
//      } else {
//        nd = nd.getNext();
//      }
//    }
//  }

  //private CharArray procname = new CharArray(15);
  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    owner.getOutputProcessor().startElement0();
    CharArray instName = owner.crPoolXSLElement.getObject().reuse();
    CharArray instPrefName = owner.crPoolXSLElement.getObject().reuse();
    nameTemplate.processTo(instName, owner.getXPathProcessor(), xcont, varContext);
    namespaceTemplate.processTo(namespace, owner.getXPathProcessor(), xcont, varContext);
    //    if (isXSLElement == true && etname != null) {
    //      XObject xo = owner.getXPathProcessor().process(etname, xcont, varContext);
    //      procname.set(xo.toXString().getValue());
    //    } else {
    //      procname.set(name);
    //    }
    XSLAttribute xsatt = null;
    attImpl.clear();

    for (int i = 0; i < attrs.size(); i++) {
      xsatt = (XSLAttribute) attrs.get(i);
      xsatt.processStart();
      xsatt.process(xcont, node);
      xsatt.processEnd();
      QName q = xsatt.getQName();
      //      LogWriter.getSystemLogWriter().println("XSLElement.process - processing attributes: q=" + q + ", value=" + xsatt.getValue());
      attImpl.addAttribute(q.uri, q.prefix, q.rawname, q.localname, xsatt.getValue());
    } 

    //    if (owner.getNamespaceAliaces().get(namespace) != null) {
    //    LogWriter.getSystemLogWriter().println("Element: bforeStart  instname = " + instName + " " + instName.hashCode() + " in xsl:element: " + hashCode());

    instPrefName.substring(instName, instName.indexOf(':')+1 );
    owner.getOutputProcessor().startElement(namespace, instPrefName, instName, attImpl);
    
    processFromFirst(xcont, node);
//    if (getFirst() != null) {
//      getFirst().process(xcont, node);
//    }
    //    LogWriter.getSystemLogWriter().println("Element: before end instname = " + instName + " " + instName.hashCode()+ " in xsl:element: " + hashCode());
    owner.getOutputProcessor().endElement(namespace, instPrefName, instName);
    owner.crPoolXSLElement.releaseObject(instPrefName);
    owner.crPoolXSLElement.releaseObject(instName);
//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLElement: name = " + name);
    XSLAttribute xsatt = null;

    for (int i = 0; i < attrs.size(); i++) {
      xsatt = (XSLAttribute) attrs.get(i);
      xsatt.print(ind + "  ");
      //owner.handleText(" " + xsatt.getName() + "=" + xsatt.getValue());
    } 

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

  public final static String[] REQPAR = {"name"};
  public final static String[] OPTPAR = {"namespace", "use-attribute-sets"};

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

