package com.sap.engine.lib.xml.signature.elements;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureException;

public class XMLSignature extends GenericElement {

  public static final String localName = "Signature";

  //  public XMLSignature() throws com.sap.engine.lib.xml.signature.SignatureException {
  //    super(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "Signature");  
  //    initDOM();
  //  }
  public XMLSignature(Node parent) throws com.sap.engine.lib.xml.signature.SignatureException {
    super(((parent != null) ? parent.getOwnerDocument() : null), Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + XMLSignature.localName, null);
    if (parent != null) {
      parent.appendChild(this.domRepresentation);
    } else {
      getOwner().appendChild(domRepresentation);
    }
  }

  public XMLSignature(Node parent, Node nextSibling, boolean replace) throws com.sap.engine.lib.xml.signature.SignatureException {
    super(((parent != null) ? parent.getOwnerDocument() : null), Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + XMLSignature.localName, null);
    if (replace) {
      parent.replaceChild(this.domRepresentation, nextSibling);
    } else {
      parent.insertBefore(this.domRepresentation, nextSibling);
    }
  }

  public XMLSignature(Element n, boolean unused) throws SignatureException {
    super(n, null);
  }

  public String getSignatureValue() throws SignatureException {
    GenericElement sValue = getDirectChildIgnoreCase(Constants.SIGNATURE_SPEC_NS, "SignatureValue");
    return sValue.getNodeValue();
  }

}

