package com.sap.engine.lib.xml.signature.elements;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureContext;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.util.BASE64Encoder;

public class DSAKeyValue extends KeyValue {

  private BigInteger p = null;
  private BigInteger q = null;
  private BigInteger g = null;
  private BigInteger y = null;
//  private BigInteger j = null;
//  private BigInteger seed = null;
//  private BigInteger pgenCounter = null;
  private Certificate certificate = null;
  private DSAPublicKey publicKey = null;
  private DSAParams params = null;

  public DSAKeyValue(String namespaceUri, String qualifiedName, GenericElement parent) throws SignatureException {
    super(namespaceUri, qualifiedName, parent);
  }

  public Certificate getCerificate() {
    return certificate;
  }

  public void setCertificate(Certificate certificate) {
    this.certificate = certificate;
  }

  public void setPublicKey(PublicKey publicKey) {
    this.publicKey = (DSAPublicKey) publicKey;
  }

  public void construct() throws SignatureException {
    if (publicKey == null && certificate != null) {
      try {
        publicKey = (DSAPublicKey) certificate.getPublicKey();
      } catch (ClassCastException e) {
        throw new SignatureException("DSA key expected but " + certificate.getPublicKey().getAlgorithm() + " key received", new java.lang.Object[]{certificate}, e );
      }
    }

    y = publicKey.getY();
    params = publicKey.getParams();
    p = params.getP();
    q = params.getQ();
    g = params.getG();
    GenericElement keyValue = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "DSAKeyValue", this);
    GenericElement valueP = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "P", keyValue);
    //valueP.appendTextChild(new String(BASE64Encoder.encode(toByteArrOmitSign(p))));
    //valueP.appendTextChild(new String(BASE64Encoder.encode(getBytes(p, p.bitLength()))));
    valueP.appendTextChild(new String(BASE64Encoder.encode(SignatureContext.getBytes(p)))); //$JL-I18N$
    GenericElement valueQ = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "Q", keyValue);
    //valueQ.appendTextChild(new String(BASE64Encoder.encode(toByteArrOmitSign(q))));
    //valueQ.appendTextChild(new String(BASE64Encoder.encode(getBytes(q, q.bitLength()))));
    valueQ.appendTextChild(new String(BASE64Encoder.encode(SignatureContext.getBytes(q)))); //$JL-I18N$
    GenericElement valueG = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "G", keyValue);
    //valueG.appendTextChild(new String(BASE64Encoder.encode(toByteArrOmitSign(g))));
    //valueG.appendTextChild(new String(BASE64Encoder.encode(getBytes(g, g.bitLength()))));
    valueG.appendTextChild(new String(BASE64Encoder.encode(SignatureContext.getBytes(g)))); //$JL-I18N$
    GenericElement valueY = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "Y", keyValue);
    //valueY.appendTextChild(new String(BASE64Encoder.encode(toByteArrOmitSign(y))));
    //valueY.appendTextChild(new String(BASE64Encoder.encode(getBytes(y, y.bitLength()))));
    valueY.appendTextChild(new String(BASE64Encoder.encode(SignatureContext.getBytes(y)))); //$JL-I18N$
  }

}

