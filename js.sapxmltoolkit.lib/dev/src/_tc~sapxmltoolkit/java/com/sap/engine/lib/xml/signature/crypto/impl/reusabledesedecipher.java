/*
 * Created on 2005-4-14
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */

package com.sap.engine.lib.xml.signature.crypto.impl;

import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.*;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.crypto.CustomCipher;
import com.sap.engine.lib.xml.signature.crypto.Reusable;
import com.sap.engine.lib.xml.signature.crypto.ReusableConfigurator;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public class ReusableDESedeCipher extends Reusable implements CustomCipher {

  public final static String DES_EDE_URI = Constants.ALG_ENC_TRIPLEDES;

  public static ReusablePool pool = new ReusablePool(ReusablePool.MAX_SIZE);

  protected Cipher internalCipher = null;

  private ReusableDESedeCipher() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
    internalCipher = Cipher.getInstance("DESede/CBC/ISO10126Padding", ReusableConfigurator.getProvider());
  }

  public int getOutputSize(int i1) throws IllegalStateException {
    return internalCipher.getOutputSize(i1);
  }

  public void init(int i1, Key key) throws InvalidKeyException {
    try {
      internalCipher.init(i1, key);
    } catch (InvalidKeyException ike) {
      throw new InvalidKeyException(Reusable.LIMITED_CRYPTO_POLICY_MESSAGE, ike);
    }
  }

  public void init(int i1, Key key, AlgorithmParameterSpec algorithmparameterspec) throws InvalidKeyException, InvalidAlgorithmParameterException {
    try {
      internalCipher.init(i1, key, algorithmparameterspec);
    } catch (InvalidKeyException ike) {
      throw new InvalidKeyException(Reusable.LIMITED_CRYPTO_POLICY_MESSAGE, ike);
    }
  }

  public byte[] update(byte[] abyte0, int i1, int j1) throws IllegalStateException {
    return internalCipher.update(abyte0, i1, j1);
  }

  public int update(byte[] abyte0, int i1, int j1, byte[] abyte1, int k1) throws IllegalStateException, ShortBufferException {
    return internalCipher.update(abyte0, i1, j1, abyte1, k1);
  }

  public byte[] doFinal() throws IllegalStateException, IllegalBlockSizeException, BadPaddingException {
    return internalCipher.doFinal();
  }

  public byte[] doFinal(byte[] abyte0, int i1, int j1) throws IllegalStateException, IllegalBlockSizeException, BadPaddingException {
    return internalCipher.doFinal(abyte0, i1, j1);
  }

  public int doFinal(byte[] abyte0, int i1, int j1, byte[] abyte1) throws IllegalStateException, ShortBufferException, IllegalBlockSizeException, BadPaddingException {
    return internalCipher.doFinal(abyte0, i1, j1, abyte1);
  }

  public int doFinal(byte[] abyte0, int i1, int j1, byte[] abyte1, int start) throws IllegalStateException, ShortBufferException, IllegalBlockSizeException, BadPaddingException {
    return internalCipher.doFinal(abyte0, i1, j1, abyte1, start);
  }

  public static Reusable newInstance() throws Exception {
    Reusable ret = pool.newInstance();
    if (ret == null) {
      ret = new ReusableDESedeCipher();
    }
    return ret;
  }

  public void release() {
    pool.release(this);
  }

  public int getIVLength() {
    return 8;
  }

  public Object getInternal() {
    return internalCipher;
  }

  public int doFinal(byte[] output, int offset) throws IllegalStateException, IllegalBlockSizeException, BadPaddingException, ShortBufferException {
    return internalCipher.doFinal(output, offset);
  }

}