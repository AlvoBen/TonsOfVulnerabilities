package com.sap.engine.lib.xml.signature.encryption.keytrans;

import java.security.Key;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.encryption.XMLCryptor;
import com.sap.engine.lib.xml.signature.encryption.keytrans.algorithms.AES_Transporter;
import com.sap.engine.lib.xml.signature.encryption.keytrans.algorithms.RSA_1_5_Transporter;
import com.sap.engine.lib.xml.signature.encryption.keytrans.algorithms.RSA_OAEP_Transporter;
import com.sap.engine.lib.xml.signature.encryption.keytrans.algorithms.TDES_Transporter;

public abstract class KeyTransporter extends XMLCryptor {

  public abstract byte[] encrypt(Key wrapping, Key wrapped) throws SignatureException;
  public abstract byte[] decrypt(Key wrapping, byte[] wrapped) throws SignatureException;
// TODO: put in cryptographic pool!
  public static KeyTransporter getInstance(String uri) throws SignatureException {
    // change!!!
    if (Constants.KEY_ENC_RSA_1_5.equals(uri)) {
      return new RSA_1_5_Transporter();
    }
    if ("http://www.w3.org/2001/04/xmlenc#kw-tripledes".equals(uri)){
      // TODO: use static
      return new TDES_Transporter();
    } 
    
    if (uri.startsWith("http://www.w3.org/2001/04/xmlenc#kw-aes")){
      return new AES_Transporter(uri);
    }

    if (Constants.KEY_ENC_RSA_OAEP.equals(uri)){
      return new RSA_OAEP_Transporter();
    }


    throw new SignatureException("Unrecognized key transport algorithm uri: " + uri, new Object[]{uri});
  }
  
  public static KeyTransporter getInstance(String uri, GenericElement parent) throws SignatureException {
    // change!!!
    if (Constants.KEY_ENC_RSA_1_5.equals(uri)) {
      return new RSA_1_5_Transporter();
    }
    if ("http://www.w3.org/2001/04/xmlenc#kw-tripledes".equals(uri)){
      // TODO: use static
      return new TDES_Transporter();
    } 
    
    if (uri.startsWith("http://www.w3.org/2001/04/xmlenc#kw-aes")){
      return new AES_Transporter(uri);
    }
    
    if (Constants.KEY_ENC_RSA_OAEP.equals(uri)){
      return new RSA_OAEP_Transporter(parent);
    }

    //if (uri.equals(Constants.KEY_ENC_RSA_OAEP)) {
    //  return new RSA_OAEP_Transporter();
    //}

    throw new SignatureException("Unrecognized key transport algorithm uri: " + uri, new Object[]{uri});
  }
}