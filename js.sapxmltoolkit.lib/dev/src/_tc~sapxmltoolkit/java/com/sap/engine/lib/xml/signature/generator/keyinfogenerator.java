package com.sap.engine.lib.xml.signature.generator;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PublicKey;

import org.w3c.dom.Node;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.elements.KeyInfo;
import com.sap.engine.lib.xml.signature.elements.RetrievalMethod;
import com.sap.engine.lib.xml.signature.transform.Transformation;

public class KeyInfoGenerator {

  private KeyInfo ki = null;
  //private GenericElement refList = null;

  public void init(Node parent) throws SignatureException {
    if (parent != null) {
      ki = new KeyInfo(parent);
    } else {
      ki = new KeyInfo();
      ki.initDOM();
    }
  }

  public void setKeyName(String keyName) throws SignatureException {
    GenericElement name = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "KeyName", ki);
    name.appendTextChild(keyName);
  }
  
  public void setCertificate(java.security.cert.Certificate cert) throws SignatureException {
    setCertificates(new java.security.cert.Certificate[]{cert});
  }
  
  public void setCertificates(java.security.cert.Certificate[] certs) throws SignatureException {
    try {
      ki.setCertificates(certs);
      ki.addCertificateInfo();
    } catch (GeneralSecurityException e) {
      throw new SignatureException("Error while setting certificates",new Object[]{certs}, e);
    }
  }

  
  
  
  public void addKeyValue(Key key) throws SignatureException {
    if (key instanceof PublicKey) {
      ki.setPublicKey((PublicKey) key);
      ki.addKeyValue(key.getAlgorithm());
    }
  }
  
  public void setRetrievalMethod(String uri, String type, Transformation[] tr) throws SignatureException {
    RetrievalMethod meth = new RetrievalMethod(uri);
    //RetrievalMethod meth = new RetrievalMethod(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "RetrievalMethod", ki);

    meth.setTransforms(tr);
    meth.construct(ki);
    if (type != null) {
      meth.setAttribute("type", type);
    }

  }
  
  public KeyInfo getKeyInfo() throws SignatureException {
    return ki;
  }
}

