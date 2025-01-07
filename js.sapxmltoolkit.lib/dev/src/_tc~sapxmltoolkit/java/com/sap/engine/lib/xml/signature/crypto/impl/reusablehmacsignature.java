/*
 * Created on 2005-4-14 @author Alexander Alexandrov,
 * e-mail:aleksandar.aleksandrov@sap.com
 */

package com.sap.engine.lib.xml.signature.crypto.impl;

import java.security.*;
import java.util.Arrays;

import javax.crypto.Mac;

import com.sap.engine.lib.xml.signature.crypto.CustomSignature;
import com.sap.engine.lib.xml.signature.crypto.Reusable;
import com.sap.engine.lib.xml.signature.crypto.ReusableConfigurator;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 */
public class ReusableHMACSignature extends Reusable implements CustomSignature {
  public final static String HMAC_SHA1_URI = "http://www.w3.org/2000/09/xmldsig#hmac-sha1";

  public static ReusablePool pool = new ReusablePool(ReusablePool.MAX_SIZE);

  protected Mac internalMac = null;

  private ReusableHMACSignature() throws NoSuchAlgorithmException, NoSuchProviderException {
    internalMac = Mac.getInstance("HmacSHA1", ReusableConfigurator.getProvider());
  }

  public void update(byte[] bytes) throws SignatureException {
    internalMac.update(bytes);
  }

  public void initSign(Key signKey) throws SignatureException, InvalidKeyException {
    try {
      internalMac.init(signKey);
    } catch (InvalidKeyException ike) {
      throw new InvalidKeyException(Reusable.LIMITED_CRYPTO_POLICY_MESSAGE, ike);
    }
  }

  public void initVerify(Key verifyKey) throws SignatureException, InvalidKeyException {
    try {
      internalMac.init(verifyKey);
    } catch (InvalidKeyException ike) {
      throw new InvalidKeyException(Reusable.LIMITED_CRYPTO_POLICY_MESSAGE, ike);
    }

  }

  public byte[] sign() throws SignatureException {
    return internalMac.doFinal();
  }

  public boolean verify(byte[] signature) throws SignatureException {
    byte[] res = sign();
    return Arrays.equals(res, signature);
  }

  public static Reusable newInstance() throws Exception {
    Reusable ret = pool.newInstance();
    if (ret == null) {
      ret = new ReusableHMACSignature();
    }
    return ret;
  }

  public void release() {
    pool.release(this);
  }

  public Object getInternal() {
    return internalMac;
  }
}
