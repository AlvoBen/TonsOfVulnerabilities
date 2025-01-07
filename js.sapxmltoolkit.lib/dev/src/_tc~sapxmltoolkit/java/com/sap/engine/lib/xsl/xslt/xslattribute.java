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
 <xsl:attribute
 name = { qname }
 namespace = { uri-reference }>
 <!-- Content: template -->
 </xsl:attribute>
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
//import com.sap.engine.lib.xml.parser.pool.CharArrayPool;
//import com.sap.engine.lib.xsl.xpath.ETObject;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;

public final class XSLAttribute extends XSLContentNode {

  //private CharArray name = new CharArray();
  //private CharArray namespace = new CharArray();
  private CharArray value = new CharArray(20);
  //private CharArray instName = new CharArray();
  
  private boolean processed = false;
  private boolean bInsideValue = false;
  private boolean bProcessByElement = false;
  
  private QName instQName = new QName();

  private AttributeValueTemplateHandler nameTemplate = new AttributeValueTemplateHandler();
  private AttributeValueTemplateHandler valueTemplate = new AttributeValueTemplateHandler();
  private AttributeValueTemplateHandler namespaceTemplate = new AttributeValueTemplateHandler();

  //  public XSLNode clone(XSLStylesheet s, XSLNode p) throws XSLException {
  //    XSLAttribute n = new XSLAttribute(s, p);
  //    n.name = name.copy();
  //    n.etname = (etname!=null)?etname.cloneIt():null;
  //    n.etvalue = (etvalue!=null)?etvalue.cloneIt():null;
  //    n.namespace = namespace;
  //    n.value = value.copy();
  //    n.isXSLElement = isXSLElement;
  //    bInsideValue = bInsideValue;
  //
  //    getFirst().clone(s, n);
  //    if (getNext() != null) getNext().clone(s, p);
  //    return n;
  //
  //  }
  public XSLAttribute(XSLStylesheet owner, XSLNode parent) throws XSLException {
    super(owner, parent);
  }

  public XSLAttribute(XSLStylesheet owner, XSLNode parent, Element el) throws XSLException {
    super(owner, parent, el);
    //boolean bEvalSection = false;
    //nameTokens.clear();
    //valueTokens.clear();

    instQName.rawname.set(el.getNodeName());
    instQName.rawname.trim();
    instQName.uri = new CharArray();
    instQName.uri.set(el.getNamespaceURI());
    instQName.uri.trim();

    if (el.getNamespaceURI() != null && el.getNamespaceURI().equals(XSLStylesheet.sXSLNamespace) && el.getLocalName() != null && el.getLocalName().equals("attribute")) {
      instQName.rawname.set(el.getAttribute("name"));
      instQName.rawname.trim();

      if (el.getAttribute("namespace").length() > 0) {
        instQName.uri.set(el.getAttribute("namespace"));
        instQName.uri.trim();
      } else {
        instQName.uri.clear();
      }

      //      isXSLElement=true;
      nameTemplate.init(instQName.rawname, owner.etBuilder);
      namespaceTemplate.init(instQName.uri, owner.etBuilder);
      //instQName.initFromRawname();

      //      LogWriter.getSystemLogWriter().println(nameTemplate.countTokens() + " " + name.indexOf(':'));
      if (nameTemplate.countTokens() == 1) {
        if (instQName.rawname.indexOf(':') > -1) {
          String sname = instQName.rawname.toString();
          String prf = sname.substring(0, sname.indexOf(':'));

          //          LogWriter.getSystemLogWriter().println(owner.getNamespaceHandler().get(prf));
          if (namespaceTemplate.countTokens() == 0) {
            namespaceTemplate.init(owner.getNamespaceHandler().get(new CharArray(prf)), owner.etBuilder);
          }
        }
      }

      //      CharArray buf = new CharArray(30);
      //      for (int i=0; i<name.length(); i++) {
      //        if (name.charAt(i) == '{') {
      //          if (buf.length() > 0) {
      //            nameTokens.add(buf.copy());
      //            buf.clear();
      //          }
      //          bEvalSection = true;
      //        } else if (name.charAt(i) == '}' && bEvalSection) {
      //          nameTokens.add(owner.etBuilder.process(buf.copy()));
      //          buf.clear();
      //          bEvalSection = false;
      //        } else {
      //          buf.append(name.charAt(i));
      //        }
      //      }
      //      if (buf.length() > 0) nameTokens.add(buf.copy());
      //      //      if (name.charAt(0) == '{' && name.charAt(name.length()-1) == '}') {
      //      //        etname = owner.etBuilder.process(name.getString().substring(1, name.length()-1));
      //      //      }
      //      //
      //    } else {
      //      nameTokens.add(name);
      //    }
    }

    //instName.set(name);
  }

  public XSLAttribute(XSLStylesheet owner, XSLNode parent, Object uri, Object name, String value) throws XSLException {
    super(owner, parent, null);

    if (name instanceof CharArray) {
      instQName.rawname.set((CharArray) name);
    } else if (name instanceof String) {
      instQName.rawname.set((String) name);
    } else {
      throw new IllegalArgumentException("name must be either CharArray or String");
    }

    instQName.uri = new CharArray();
    
    if (uri instanceof CharArray) {
      this.instQName.uri.set((CharArray) uri);
    } else if (uri instanceof String) {
      this.instQName.uri.set((String) uri);
    } else if (uri == null) {
      this.instQName.uri.clear();
    } else {
      throw new IllegalArgumentException("uri must be either CharArray or String");
    }

    //nameTokens.clear();
    //valueTokens.clear();
    nameTemplate.init(instQName.rawname, owner.etBuilder);
    namespaceTemplate.init(instQName.uri, owner.etBuilder);
    //nameTokens.add(this.name);
    //instName.set(this.name);
    bInsideValue = true;
    //boolean bEvalSection = false;
    valueTemplate.init(new CharArray(value), owner.etBuilder);
    //    CharArray buf = new CharArray(30);
    //    for (int i=0; i<value.length(); i++) {
    //      if (value.charAt(i) == '{') {
    //        if (buf.length() > 0) {
    //          valueTokens.add(buf.copy());
    //          buf.clear();
    //        }
    //        bEvalSection = true;
    //      } else if (value.charAt(i) == '}' && bEvalSection) {
    //        valueTokens.add(owner.etBuilder.process(buf.copy()));
    //        buf.clear();
    //        bEvalSection = false;
    //      } else {
    //        buf.append(value.charAt(i));
    //      }
    //    }
    //    if (buf.length() > 0) valueTokens.add(buf.copy());
    //    if (value.length() > 0 && value.charAt(0) == '{' && value.charAt(value.length()-1) == '}') {
    //      etvalue = owner.etBuilder.process(value.substring(1, value.length()-1));
    //    } else {
    //      this.value.set(value);
    //    }
  }

  public CharArray getName() {
    return instQName.rawname;
  }

  //  private CharArray instPrefix = new CharArray();
  //  private CharArray instLocalName = new CharArray();
  //  public CharArray getPrefix() {
  //    int x = instName.indexOf(':');
  //    if (x==-1) {
  //      instPrefix.clear();
  //    } else {
  //      instPrefix.substring(instName, 0, x);
  //    }
  //    return instPrefix();
  //  }
  //
  //  public CharArray getLocalName() {
  //    int x = instName.indexOf(':');
  //    if (x==-1) {
  //      instPrefix.clear();
  //    } else {
  //      instPrefix.substring(instName, 0, x);
  //    }
  //    return instPrefix();
  //  }
  public QName getQName() {
    return instQName;
  }

  public CharArray getValue() throws XSLException {
    return value;
  }

  public CharArray getNamespaceURI() {
    return instQName.uri;
  }

  public void setProcessByElement() {
    bProcessByElement = true;
  }

  public void clearProcessByElement() {
    bProcessByElement = false;
  }

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    nameTemplate.processTo(instQName.rawname, owner.getXPathProcessor(), xcont, varContext);
    namespaceTemplate.processTo(instQName.uri, owner.getXPathProcessor(), xcont, varContext);
    instQName.initFromRawname();
    //instQName.uri = namespace;

    //    LogWriter.getSystemLogWriter().println("XSLAttribute: name = " + instName + ", namespace =" + namespace);
    //instName.clear();
    //    for (int i=0; i < nameTokens.size(); i++) {
    //      if (nameTokens.get(i) instanceof CharArray) {
    //        instName.append((CharArray)nameTokens.get(i));
    //      } else {
    //        XObject xo = owner.getXPathProcessor().process((ETObject)nameTokens.get(i), xcont, varContext);
    //        instName.append(xo.toXString().getValue());
    //      }
    //    }
    //    if (etname != null) {
    //    }
    //value.clear();
    if (!bInsideValue) {
      //      CharArray carr = pool.getObject().reuse();
      value.clear();
      owner.getOutputProcessor().startAttributeValueProcessing(value);
      owner.getOutputProcessor().setDisableOutputEscaping(true);
      processFromFirst(xcont, node);
//      if (getFirst() != null) {
//        getFirst().process(xcont, node);
//      }
      owner.getOutputProcessor().setDisableOutputEscaping(false);
      owner.getOutputProcessor().stopAttributeValueProcessing();
      //LogWriter.getSystemLogWriter().println("XSLAttribute.process. value= " + value);
      //valueTemplate.init(carr, owner.etBuilder);
      //      if ((carr.length() != 0) &&
      //                carr.charAt(0) == '{' && carr.charAt(carr.length()-1) == '}') {
      //        CharArray vvv = pool.getObject().reuse();
      //        vvv.set(carr, 1, carr.length()-1);
      //        XObject xo = owner.getXPathProcessor().process(owner.etBuilder.process(vvv.getString()), xcont, varContext);
      //        value.set(xo.toXString().getValue());
      //        pool.releaseObject(vvv);
      //      LogWriter.getSystemLogWriter().println("Processed Attribute is: " + carr);
      //} else {
      //value.set(carr);
      //pool.releaseObject(carr);
      // }
    } else {
      //valueTemplate.init(value);
      owner.getOutputProcessor().setDisableOutputEscaping(true);
      valueTemplate.processTo(value, owner.getXPathProcessor(), xcont, varContext);
      owner.getOutputProcessor().setDisableOutputEscaping(false);
      //LogWriter.getSystemLogWriter().println("XSLAttribute.process. value= " + value);
    }

    //LogWriter.getSystemLogWriter().println("Processed Attribute is (afger valuetemplate): " + value);
    //    if (bInsideValue) {
    //      for (int i=0; i < valueTokens.size(); i++) {
    //        if (valueTokens.get(i) instanceof CharArray) {
    //          value.append((CharArray)valueTokens.get(i));
    //        } else {
    //          XObject xo = owner.getXPathProcessor().process((ETObject)valueTokens.get(i), xcont, varContext);
    //          value.append(xo.toXString().getValue());
    //        }
    //      }
    //      
    //      //      if (etvalue != null) {
    //      //        XObject xo = owner.getXPathProcessor().process(etvalue, xcont, varContext);
    //      //        value.set(xo.toXString().getValue());
    //      //      }
    //    } else {
    //      CharArray carr = pool.getObject().reuse();
    //      owner.getOutputProcessor().startAttributeValueProcessing(carr);
    //      if (getFirst() != null) getFirst().process(xcont, node);
    //      owner.getOutputProcessor().stopAttributeValueProcessing();
    //      if ((carr.length() != 0) &&
    //                carr.charAt(0) == '{' && carr.charAt(carr.length()-1) == '}') {
    //        CharArray vvv = pool.getObject().reuse();
    //        vvv.set(carr, 1, carr.length()-1);
    //        XObject xo = owner.getXPathProcessor().process(owner.etBuilder.process(vvv.getString()), xcont, varContext);
    //        value.set(xo.toXString().getValue());
    //        pool.releaseObject(vvv);
    //      } else {
    //        value.set(carr);
    //      }
    //      pool.releaseObject(carr);
    //    }
    //LogWriter.getSystemLogWriter().print("</" + name + ">");
    //if (getNext() != null) getNext().process(xcont, node);
    if (bProcessByElement == false) {
      owner.getOutputProcessor().addAttribute(instQName.uri, instQName.getPrefix(), instQName.rawname, instQName.getlocalname(), value);
//      if (getNext() != null) {
//        getNext().process(xcont, node);
//      }
    } else {
      setProcessNext(false);
    }

    processed = true;
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLAttribute: name = " + instQName.rawname);
    if (getFirst() != null) {
      getFirst().print(ind + "  ");
    }
    //if (getNext() != null) getNext().print(ind);
  }

  public final static String[] REQPAR = {"name"};
  public final static String[] OPTPAR = {"namespace"};

  public String[] getRequiredParams() {
    return REQPAR;
  }

  public String[] getOptionalParams() {
    return OPTPAR;
  }

}

