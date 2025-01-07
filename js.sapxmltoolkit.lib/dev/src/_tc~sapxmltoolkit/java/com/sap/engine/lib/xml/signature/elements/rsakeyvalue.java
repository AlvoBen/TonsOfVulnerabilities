package com.sap.engine.lib.xml.signature.elements;

//import java.security.*;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPublicKey;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureContext;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.util.BASE64Encoder;

public class RSAKeyValue extends KeyValue {

  private BigInteger modulus = null;
  private BigInteger exponent = null;
  private Certificate certificate = null;
  private RSAPublicKey publicKey = null;

  public RSAKeyValue(String namespaceUri, String qualifiedName, GenericElement parent) throws SignatureException {
    super(namespaceUri, qualifiedName, parent);
  }

  public Certificate getCerificate() {
    return certificate;
  }

  public void setPublicKey(PublicKey publicKey) {
    this.publicKey = (RSAPublicKey) publicKey;
  }

  public void setCertificate(Certificate certificate) {
    this.certificate = certificate;
  }

  public void construct() throws SignatureException {
    if (publicKey == null && certificate != null) {
      try {
        publicKey = (RSAPublicKey) certificate.getPublicKey();
      } catch (ClassCastException e) {
        throw new SignatureException("DSA key expected but " + certificate.getPublicKey().getAlgorithm() + " key received", new java.lang.Object[]{certificate},e);
      }
    }

    modulus = publicKey.getModulus();
    exponent = publicKey.getPublicExponent();
    GenericElement keyValue = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "RSAKeyValue", this);
    GenericElement valueModulus = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "Modulus", keyValue);
    valueModulus.appendTextChild(new String(BASE64Encoder.encode(SignatureContext.getBytes(modulus)))); //$JL-I18N$
    GenericElement valueExponent = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "Exponent", keyValue);
    valueExponent.appendTextChild(new String(BASE64Encoder.encode(SignatureContext.getBytes(exponent)))); //$JL-I18N$
  }

}

