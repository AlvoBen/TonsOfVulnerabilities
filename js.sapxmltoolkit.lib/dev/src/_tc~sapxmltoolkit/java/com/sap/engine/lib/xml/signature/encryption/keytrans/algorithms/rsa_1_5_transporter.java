package com.sap.engine.lib.xml.signature.encryption.keytrans.algorithms;

import java.security.Key;

import javax.crypto.Cipher;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureContext;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.crypto.CustomCipher;
import com.sap.engine.lib.xml.signature.crypto.Reusable;
import com.sap.engine.lib.xml.signature.encryption.keytrans.KeyTransporter;


// rfc2437 obsoleted by rfc3447
// introduced fallback mechanism if there is need to interoperate with older versions
// of rsa 1.5
public class RSA_1_5_Transporter extends KeyTransporter {
  
  static boolean rsa15fallback = Boolean.getBoolean("com.sap.xml.security.rsa15.fallback");
  public byte[] encrypt(Key wrapping, Key wrapped) throws SignatureException{
    
    SignatureException.traceKey("Key for wrapping(RSA 1.5)", wrapping);
    SignatureException.traceKey("Key to be wrapped(RSA 1.5)", wrapped);

//    try {
//      byte[] encoded = wrapped.getEncoded();
//      cipher = SignatureContext.getCryptographicPool().generateCipherInstance(Constants.KEY_ENC_RSA_1_5, "ECB", "PKCS1Padding");
//      cipher.init(Cipher.ENCRYPT_MODE, wrapping);
//      byte[] encrypted = cipher.doFinal(encoded, 0, encoded.length);
//      SignatureException.traceByte("Result of encryption(RSA 1.5)", encrypted);
//      return encrypted;
//    } catch (Exception other){
//      throw new SignatureException("Error in unwrapping key with RSA_1_5",other);
//    } finally {
//      releaseCipher();
//    }
    Reusable reusable = null;
    try {
      byte[] encoded = wrapped.getEncoded();
      reusable = Reusable.getInstance(Constants.KEY_ENC_RSA_1_5);
      CustomCipher cipher = (CustomCipher) reusable;
      cipher.init(Cipher.ENCRYPT_MODE, wrapping);
      byte[] encrypted = cipher.doFinal(encoded, 0, encoded.length);
      SignatureException.traceByte("Result of encryption(RSA 1.5)", encrypted);
      return encrypted;
    } catch (Exception other) {
      throw new SignatureException("Error in unwrapping key with RSA_1_5", other);
    } finally {
      if (reusable != null) {
        reusable.release();
      }
    }
  }
  
  public byte[] decrypt(Key wrapping, byte[] wrapped) throws SignatureException {
    
    SignatureException.traceKey("Key for unwrapping(RSA 1.5)", wrapping);
    SignatureException.traceByte("Wrapped bytes (RSA 1.5)", wrapped);
    
//    try {
//      cipher = SignatureContext.getCryptographicPool().generateCipherInstance(Constants.KEY_ENC_RSA_1_5, "ECB", "PKCS1Padding");
//      cipher.init(Cipher.DECRYPT_MODE, wrapping);
//      byte[] decrypted = cipher.doFinal(wrapped, 0, wrapped.length);
//      SignatureException.traceByte("Result of decryption(RSA 1.5)", decrypted);
//      return decrypted;
//    } catch (Exception other) {
//      if (rsa15fallback){
//        return decryptFallBack(wrapping, wrapped);
//      }
//      throw new SignatureException("Error in unwrapping key with RSA_1_5", other);
//    } finally {
//      releaseCipher();
//    }
    Reusable reusable = null;
    try {
      reusable = Reusable.getInstance(Constants.KEY_ENC_RSA_1_5);
      CustomCipher cipher = (CustomCipher) reusable;
      cipher.init(Cipher.DECRYPT_MODE, wrapping);
      byte[] decrypted = cipher.doFinal(wrapped, 0, wrapped.length);
      SignatureException.traceByte("Result of decryption(RSA 1.5)", decrypted);
      return decrypted;
    } catch (Exception other) {
      if (rsa15fallback) {
        return decryptFallBack(wrapping, wrapped);
      }
      throw new SignatureException("Error in unwrapping key with RSA_1_5", other);
    } finally {
      if (reusable != null) {
        reusable.release();
      }
    }

  }
  
  public byte[] decryptFallBack(Key wrapping, byte[] wrapped) throws SignatureException 
  {
//    mode = Cipher.DECRYPT_MODE;
//    cryptMode = "ECB";
//    key = wrapping;
//    createCipher(Constants.KEY_ENC_RSA_1_5);
    Reusable reusable = null;
    try {
      reusable = Reusable.getInstance(Constants.KEY_ENC_RSA_OAEP);
      CustomCipher cipher = (CustomCipher) reusable;
      cipher.init(Cipher.DECRYPT_MODE, wrapping);
      byte[] block = cipher.doFinal(wrapped, 0, wrapped.length);
//      if (block.length < cipher.getBlockSize() - HEADER_LENGTH - 1) {
//         throw new SignatureException("Invalid block size of wrapped key!");
//      }
      int start = 0;
      // skiping the leading zeros (due to change in the specification)
      while(block[start]==0){
        start++;
      }
      if (block[start++] != 0x02) {
         throw new SignatureException("Invalid encrypted key!", new Object[]{wrapping, wrapped});
      }
      // skipping pseudo-randomly generated nonzero octets
      while (block[start++] != 0) {
      }
      byte[] result = new byte[block.length - start];
      System.arraycopy(block, start, result, 0, result.length);
      return result;
    } catch (SignatureException e) {
      throw e;
    } catch (Exception e) {
      throw new SignatureException("Error while decrypting",new Object[]{wrapping, wrapped},e);      
    } finally{
      //releaseCipher();
      if (reusable!=null){
        reusable.release();
      }
    }

  }
 
}