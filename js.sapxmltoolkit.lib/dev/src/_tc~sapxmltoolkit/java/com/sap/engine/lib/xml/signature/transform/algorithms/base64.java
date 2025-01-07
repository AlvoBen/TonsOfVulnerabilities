package com.sap.engine.lib.xml.signature.transform.algorithms;

import java.util.HashMap;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.Data;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.transform.Transformation;
import com.sap.engine.lib.xml.util.BASE64Decoder;

public class Base64 extends Transformation {

  public Base64(Object[] args) {
    super(args);
    this.uri = Constants.TR_BASE64_DECODE;
  }
  
  public static byte[] decode(byte[] input) throws SignatureException {
    return BASE64Decoder.decode(input);
  }

  public void transform(Data d) throws SignatureException {
    d.setOctets(BASE64Decoder.decode(d.getOctets()));
  }

  public Transformation defineFrom(GenericElement el, HashMap $dataHashmap) throws SignatureException {
    throw new SignatureException("defineFrom not implemented for standard transformation: Base64!", new java.lang.Object[]{el, $dataHashmap});
  }
}

