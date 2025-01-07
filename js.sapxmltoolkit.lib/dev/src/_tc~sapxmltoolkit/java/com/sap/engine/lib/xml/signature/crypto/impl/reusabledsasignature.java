/*
 * Created on 2005-4-14 @author Alexander Alexandrov,
 * e-mail:aleksandar.aleksandrov@sap.com
 */

package com.sap.engine.lib.xml.signature.crypto.impl;

import java.security.*;

import com.sap.engine.lib.xml.signature.crypto.CustomSignature;
import com.sap.engine.lib.xml.signature.crypto.Reusable;
import com.sap.engine.lib.xml.signature.crypto.ReusableConfigurator;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 */
public class ReusableDSASignature extends Reusable implements CustomSignature {
  public final static String DSA_SHA1_URI = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";

  public static ReusablePool pool = new ReusablePool(ReusablePool.MAX_SIZE);

  protected Signature internalSignature = null;

  private ReusableDSASignature() throws NoSuchAlgorithmException, NoSuchProviderException {
    internalSignature = Signature.getInstance("SHA1withDSA", ReusableConfigurator.getProvider());
  }

  public void update(byte[] bytes) throws SignatureException {
    internalSignature.update(bytes);
  }

  public void initSign(Key signKey) throws SignatureException, InvalidKeyException {
    try {
      internalSignature.initSign((PrivateKey) signKey);
    } catch (InvalidKeyException ike) {
      throw new InvalidKeyException(Reusable.LIMITED_CRYPTO_POLICY_MESSAGE, ike);
    }

  }

  public void initVerify(Key verifyKey) throws SignatureException, InvalidKeyException {
    try {
      internalSignature.initVerify((PublicKey) verifyKey);
    } catch (InvalidKeyException ike) {
      throw new InvalidKeyException(Reusable.LIMITED_CRYPTO_POLICY_MESSAGE, ike);
    }

  }

  public byte[] sign() throws SignatureException {
    return internalSignature.sign();
  }

  public boolean verify(byte[] signature) throws SignatureException {
    return internalSignature.verify(signature);
  }

  public static Reusable newInstance() throws Exception {
    Reusable ret = pool.newInstance();
    if (ret == null) {
      ret = new ReusableDSASignature();
    }
    return ret;
  }

  public void release() {
    pool.release(this);
  }

  public Object getInternal() {
    return internalSignature;
  }
}
