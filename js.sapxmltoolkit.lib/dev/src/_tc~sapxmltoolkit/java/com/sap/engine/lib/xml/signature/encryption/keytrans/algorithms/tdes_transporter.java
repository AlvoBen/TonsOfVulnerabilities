/*
 * Created on 2004-3-31
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.encryption.keytrans.algorithms;

import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureContext;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.encryption.keytrans.KeyTransporter;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *  
 */
public class TDES_Transporter extends KeyTransporter {

  private final static byte[] IV = new byte[]{(byte) 0x4a, (byte) 0xdd, (byte) 0xa2, (byte) 0x2c, (byte) 0x79, (byte) 0xe8, (byte) 0x21, (byte) 0x05};

  /*
   * 1. Represent the key being wrapped as an octet sequence. If it is a
   * TRIPLEDES key, this is 24 octets (192 bits) with odd parity bit as the
   * bottom bit of each octet. 
   * 2. Compute the CMS key checksum (section 5.6.1) call this CKS. 
   * 3. Let WKCKS = WK || CKS, where || is concatenation. 
   * 4. Generate 8 random octets [RANDOM] and call this IV. 
   * 5. Encrypt WKCKS in CBC mode using KEK as the key and IV as the initialization vector. Call the
   * results TEMP1. 
   * 6. Let TEMP2 = IV || TEMP1. 
   * 7. Reverse the order of the octets in TEMP2 and call the result TEMP3. 
   * 8. Encrypt TEMP3 in CBC mode using the KEK and an initialization vector of 0x4adda22c79e82105. The
   * resulting cipher text is the desired result. It is 40 octets long if a 168
   * bit key is being wrapped.
   */
  public byte[] encrypt(Key wrapping, Key wrapped) throws SignatureException {
    SignatureException.traceKey("Key for wrapping(TDES)", wrapping);
    SignatureException.traceKey("Key to be wrapped(TDES)", wrapped);

    Cipher cipher = null;
    MessageDigest digest = null;
    try {
      cipher = SignatureContext.getCryptographicPool().generateCipherInstance(Constants.ALG_ENC_TRIPLEDES, "CBC", "NoPadding");
      byte[] key = wrapped.getEncoded();
      digest = SignatureContext.getCryptographicPool().getMessageDigestFromPool(Constants.DIGEST_SHA1);
      byte[] digBytes = digest.digest(key);
      byte[] checkSumKey = new byte[key.length+8];
      System.arraycopy(key,0,checkSumKey,0,key.length);
      System.arraycopy(digBytes,0,checkSumKey,key.length,8);
// generate 8 random bytes
      byte[] iv = new byte[8];
      randomize(iv);
      IvParameterSpec pSpec = new IvParameterSpec(iv);
      cipher.init(Cipher.ENCRYPT_MODE, wrapping, pSpec);
// TODO: see if iv is added here!      
      byte[] temp1= cipher.doFinal(checkSumKey);
      byte[] temp2 = new byte[temp1.length+8];
      System.arraycopy(iv,0,temp2,0,8);
      System.arraycopy(temp1,0,temp2,8,temp1.length);
      byte temp = 0;
      for (int i = 0; i < temp2.length / 2; i++) {
        temp = temp2[i];
        temp2[i] = temp2[temp2.length - i - 1];
        temp2[temp2.length - i - 1] = temp;
      }      
      
      pSpec = new IvParameterSpec(IV);
      cipher.init(Cipher.ENCRYPT_MODE, wrapping, pSpec);
      byte[] encrypted = cipher.doFinal(temp2);; 
      SignatureException.traceByte("Result of encryption(TDES)", encrypted);
      return encrypted;
    } catch (SignatureException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new SignatureException("Error uwrapping key", ex);
    } finally {
      SignatureContext.getCryptographicPool().releaseCipher(cipher);
      SignatureContext.getCryptographicPool().releaseMessageDigest(digest);
    }
  }

/*
 *  1. Check if the length of the cipher text is reasonable given the key type.
 * It must be 40 bytes for a 168 bit key and either 32, 40, or 48 bytes for a
 * 128, 192, or 256 bit key. If the length is not supported or inconsistent
 * with the algorithm for which the key is intended, return error. 
 * 2. Decrypt the cipher text with TRIPLEDES in CBC mode using the KEK and an
 * initialization vector (IV) of 0x4adda22c79e82105. Call the output TEMP3. 
 * 3. Reverse the order of the octets in TEMP3 and call the result TEMP2. 
 * 4. Decompose TEMP2 into IV, the first 8 octets, and TEMP1, the remaining
 * octets. 
 * 5. Decrypt TEMP1 using TRIPLEDES in CBC mode using the KEK and the
 * IV found in the previous step. Call the result WKCKS. 
 * 6. Decompose WKCKS. CKS is the last 8 octets and WK, the wrapped key, are those octets before
 * the CKS. 
 * 7. Calculate a CMS key checksum (section 5.6.1) over the WK and compare with the CKS extracted in the above step. If they are not equal,
 * return error. 
 * 8. WK is the wrapped key, now extracted for use in data decryption.
 */
  public byte[] decrypt(Key wrapping, byte[] wrapped) throws SignatureException {

    SignatureException.traceKey("Key for unwrapping(TDES)", wrapping);
    SignatureException.traceByte("Wrapped bytes (TDES)", wrapped);

    //TODO: checkLength;
    Cipher cipher = null;
    MessageDigest digest = null;
    try {
      cipher = SignatureContext.getCryptographicPool().generateCipherInstance(Constants.ALG_ENC_TRIPLEDES, "CBC", "NoPadding");
      IvParameterSpec pSpec = new IvParameterSpec(IV);
      cipher.init(Cipher.DECRYPT_MODE, wrapping, pSpec);
      byte[] decrypted = cipher.doFinal(wrapped);
      //padding!!!
      int originalSize = decrypted.length;// - decrypted[decrypted.length - 1];
      byte temp = 0;
      // reverse octets
      for (int i = 0; i < originalSize / 2; i++) {
        temp = decrypted[i];
        decrypted[i] = decrypted[originalSize - i - 1];
        decrypted[originalSize - i - 1] = temp;
      }
      // IV length 8
      byte[] iv = new byte[8];
      System.arraycopy(decrypted, 0, iv, 0, 8);
      pSpec = new IvParameterSpec(iv);

      cipher.init(Cipher.DECRYPT_MODE, wrapping, pSpec);
      decrypted = cipher.doFinal(decrypted, 8, originalSize - 8);
      // padding
      originalSize = decrypted.length;// - decrypted[decrypted.length - 1];

      // last 8 octets are check sum
      byte[] ret = new byte[originalSize - 8];
      System.arraycopy(decrypted, 0, ret, 0, ret.length);
      // check digest
      digest = SignatureContext.getCryptographicPool().getMessageDigestFromPool(Constants.DIGEST_SHA1);
      byte[] digBytes = digest.digest(ret);
      for (int i = 0; i < 8; i++) {
        if (digBytes[i] != decrypted[ret.length + i]) {
          throw new SignatureException("Key check sum error", new Object[]{ret, digBytes, decrypted});
        }
      }
      SignatureException.traceByte("Result of decryption(TDES)", ret);
      return ret;
    } catch (SignatureException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new SignatureException("Error uwrapping key", ex);
    } finally {
      SignatureContext.getCryptographicPool().releaseCipher(cipher);
      SignatureContext.getCryptographicPool().releaseMessageDigest(digest);
    }
  }

}
