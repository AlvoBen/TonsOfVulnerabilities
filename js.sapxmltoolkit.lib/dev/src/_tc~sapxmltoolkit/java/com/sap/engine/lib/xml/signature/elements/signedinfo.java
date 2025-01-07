package com.sap.engine.lib.xml.signature.elements;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureException;

public class SignedInfo extends GenericElement {

  public SignedInfo(GenericElement parent) throws SignatureException {
    super(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "SignedInfo", parent);
  }

  public SignedInfo(String namespaceUri, String qualifiedName, GenericElement parent) throws SignatureException {
    super(namespaceUri, qualifiedName, parent);
  }

}

