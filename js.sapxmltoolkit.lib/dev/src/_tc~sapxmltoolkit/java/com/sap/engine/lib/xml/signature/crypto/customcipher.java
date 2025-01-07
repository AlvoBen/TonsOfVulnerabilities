/*
 * Created on 2005-4-11 @author Alexander Alexandrov,
 * e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 */
public interface CustomCipher {
  public static final int ENCRYPT_MODE = 1;
  public static final int DECRYPT_MODE = 2;
  public static final int WRAP_MODE = 3;
  public static final int UNWRAP_MODE = 4;
  public static final int PUBLIC_KEY = 1;
  public static final int PRIVATE_KEY = 2;
  public static final int SECRET_KEY = 3;


//used
  public int getOutputSize(int i1) throws IllegalStateException;
  
  public int getIVLength();

 //used
  public void init(int i1, Key key)  throws InvalidKeyException;
  //used
  public void init(int i1, Key key, AlgorithmParameterSpec algorithmparameterspec) throws InvalidKeyException, InvalidAlgorithmParameterException;

//used
  public byte[] update(byte abyte0[], int i1, int j1) throws IllegalStateException;
//used
  public int update(byte abyte0[], int i1, int j1, byte abyte1[], int k1) throws IllegalStateException, ShortBufferException;
//used
  public byte[] doFinal() throws IllegalStateException, IllegalBlockSizeException, BadPaddingException;
  
  public int doFinal(byte output[], int offset) throws IllegalStateException, IllegalBlockSizeException, BadPaddingException, ShortBufferException;
//used
  public byte[] doFinal(byte abyte0[], int i1, int j1) throws IllegalStateException, IllegalBlockSizeException, BadPaddingException;
//used
  public int doFinal(byte abyte0[], int i1, int j1, byte abyte1[]) throws IllegalStateException, ShortBufferException, IllegalBlockSizeException,
      BadPaddingException;
  public int doFinal(byte abyte0[], int i1, int j1, byte abyte1[], int start) throws IllegalStateException, ShortBufferException, IllegalBlockSizeException,
  BadPaddingException;
//  public byte[] wrap(Key key) throws IllegalStateException, IllegalBlockSizeException, InvalidKeyException;
//
//  public Key unwrap(byte abyte0[], String s, int i1) throws IllegalStateException, InvalidKeyException, NoSuchAlgorithmException;
}
