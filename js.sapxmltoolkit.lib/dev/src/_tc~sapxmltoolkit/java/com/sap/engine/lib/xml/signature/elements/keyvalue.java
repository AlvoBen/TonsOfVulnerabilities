package com.sap.engine.lib.xml.signature.elements;

//import java.security.*;
import java.security.PublicKey;
import java.security.cert.Certificate;

import com.sap.engine.lib.xml.signature.SignatureException;

public abstract class KeyValue extends GenericElement {

  public abstract Certificate getCerificate();

  public abstract void setCertificate(Certificate certificate);

  public abstract void setPublicKey(PublicKey publicKey);

  public abstract void construct() throws SignatureException;

  public KeyValue(String namespaceUri, String qualifiedName, GenericElement parent) throws SignatureException {
    super(namespaceUri, qualifiedName, parent);
  }

}

