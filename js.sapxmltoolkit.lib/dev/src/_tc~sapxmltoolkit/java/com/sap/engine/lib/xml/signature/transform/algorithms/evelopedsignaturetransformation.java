/**
 * Copyright (c) 2009 by SAP Labs Bulgaria, url: http://www.sap.com All rights reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG, Walldorf. You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered into with SAP.
 * 
 * Created on Feb 24, 2009 by I028512
 * 
 */

package com.sap.engine.lib.xml.signature.transform.algorithms;

import java.util.HashMap;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.engine.lib.xml.dom.DOM;
import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.Data;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.transform.Transformation;

public class EvelopedSignatureTransformation extends Transformation {
  Element currentElement;

  public EvelopedSignatureTransformation( Object[] additionalArgs) {
    super(additionalArgs);
    if ((additionalArgs != null) && (additionalArgs.length > 0)) {
      this.currentElement = (Element) additionalArgs[0];
    }
  }

  @Override
  public Transformation defineFrom(GenericElement el, HashMap hashmap) throws SignatureException {
    this.currentElement = (Element) el.getDomRepresentation();
    return this;
  }

  @Override
  public void transform(Data data) throws SignatureException {
    String referencedUri = null;
    if ((currentElement != null) && "Transform".equals(this.currentElement.getLocalName())) {
      Node transforms = this.currentElement.getParentNode();
      if (transforms != null) {
        Element reference = (Element) transforms.getParentNode();
        if (reference != null) {
          referencedUri = reference.getAttribute("URI");
        }
      }
    }
    Node n = data.getNode();
    if (n instanceof Document){
      n = ((Document) n).getDocumentElement();
    }
    if (n != null) {
      if (referencedUri == null) {
        NamedNodeMap list = n.getAttributes();
        for (int i = 0; i < list.getLength(); i++) {
          Attr at = (Attr) list.item(i);
          if ("ID".equalsIgnoreCase(at.getLocalName())) {
            referencedUri = "#".concat(at.getValue());
            break;
          }
        }
      }
      if (referencedUri == null){
        //sign enveloping element
        referencedUri = "";
      }
      Node reference = DOM.getElementByAttribute(n, "URI", null, referencedUri);
      if (reference != null) {
        Node signedInfo = reference.getParentNode();
        if (signedInfo != null) {
          Node signature = signedInfo.getParentNode();
          if (signature != null) {
            Node signatureParent = signature.getParentNode();
            if (signatureParent != null) {

              NodeList list = signatureParent.getChildNodes();
              for (int i = 0; i < list.getLength(); i++) {
                Node item = list.item(i);
                if (item instanceof Element) {
                  Element el = (Element) item;
                  if (Constants.SIGNATURE_SPEC_NS.equals(el.getNamespaceURI()) && "Signature".equals(el.getLocalName())) {
                    signatureParent.removeChild(el);
                  }
                }
              }
              data.setNode(n);
            }
          }
        }
      }
    }
  }

}
