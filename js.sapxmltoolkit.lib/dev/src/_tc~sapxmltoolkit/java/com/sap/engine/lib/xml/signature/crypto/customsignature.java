/*
 * Created on 2005-4-11 @author Alexander Alexandrov,
 * e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SignatureException;



/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 * 
 * Standart signatures along with HMAC and Kerberos
 * 
 */
public interface CustomSignature {
  public void update(byte[] bytes) throws SignatureException;

  public void initSign(Key signKey) throws SignatureException, InvalidKeyException;

  public void initVerify(Key verifyKey) throws SignatureException, InvalidKeyException;

  public byte[] sign() throws SignatureException;

  public boolean verify(byte[] signature) throws SignatureException;
}
