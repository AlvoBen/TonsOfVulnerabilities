/*
 * Created on 2004-4-6
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.encryption.keytrans.algorithms;

//import iaik.security.rsa.RSAPublicKey;

import java.security.Key;
import java.security.MessageDigest;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;

import javax.crypto.Cipher;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.crypto.CustomCipher;
import com.sap.engine.lib.xml.signature.crypto.Reusable;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.encryption.XMLCryptor;
import com.sap.engine.lib.xml.signature.encryption.keytrans.KeyTransporter;
import com.sap.engine.lib.xml.util.BASE64Decoder;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *  
 */
public class RSA_OAEP_Transporter extends KeyTransporter {

  GenericElement parent       = null;
  String         digestMethod = Constants.DIGEST_SHA1;
  byte[]         octets2      = new byte[0];

  public RSA_OAEP_Transporter(GenericElement parent) throws SignatureException {
    this.parent = parent;
    GenericElement par = parent.getDirectChild(Constants.ENCRYPTION_SPEC_NS, "EncryptionMethod");
    GenericElement el = par.getDirectChild(Constants.SIGNATURE_SPEC_NS, "DigestMethod");
    if (el != null) {
      digestMethod = el.getAttribute("Algorithm", null, null);
    }
    GenericElement param2 = par.getDirectChild(null, "OAEPparams");
    if (param2 != null) {
      String s = XMLCryptor.gatherText(param2.getDomRepresentation());
      octets2 = BASE64Decoder.decode(s.getBytes()); //$JL-I18N$
    }
  }
  
  public RSA_OAEP_Transporter() throws SignatureException{
  }
  
  

/*
 * 1. Apply the EME-OAEP encoding operation (Section 9.1.1.2) to the message M
 * and the encoding parameters P to produce an encoded message EM of length k-1
 * octets:
 * 
 * EM = EME-OAEP-ENCODE (M, P, k-1)
 * 
 * If the encoding operation outputs "message too long," then output "message
 * too long" and stop.
 *  2. Convert the encoded message EM to an integer message representative m: m =
 * OS2IP (EM)
 *  3. Apply the RSAEP encryption primitive (Section 5.1.1) to the public key
 * (n, e) and the message representative m to produce an integer ciphertext
 * representative c:
 * 
 * c = RSAEP ((n, e), m)
 *  4. Convert the ciphertext representative c to a ciphertext C of length k
 * octets: C = I2OSP (c, k)
 *  5. Output the ciphertext C.
 */
  public byte[] encrypt(Key wrapping, Key wrapped) throws SignatureException {
    
    SignatureException.traceKey("Key for wrapping(RSA OAEP)", wrapping);
    SignatureException.traceKey("Key to be wrapped(RSA OAEP)", wrapped);
  
    Reusable reusable = null;
    try {
      RSAKey temp = (RSAKey) wrapping;
      int k = (temp.getModulus().bitLength()+7)/8;
      byte [] encoded = eme_oaep_encode(wrapped.getEncoded(),k-1);
      reusable = Reusable.getInstance(Constants.KEY_ENC_RSA_OAEP);
      CustomCipher cipher = (CustomCipher) reusable;//SignatureContext.getCryptographicPool().generateCipherInstance(Constants.KEY_ENC_RSA_1_5, "ECB", "NoPadding");
      cipher.init(Cipher.ENCRYPT_MODE, wrapping);
      byte[] encrypted = cipher.doFinal(encoded, 0, encoded.length);
      if (encrypted.length<k){
        byte[] t = new byte[k];
        System.arraycopy(encrypted,0,t,k-encrypted.length,encrypted.length);
        encrypted = t;
      }
      SignatureException.traceByte("Result of encryption(RSA OAEP)", encrypted);
      return encrypted;
    } catch (SignatureException ex){
      throw ex;
    } catch (Exception other){
      throw new SignatureException("Error in unwrapping key with RSA_OAEP_MGF1",other);
    } finally {
      if (reusable != null){
        reusable.release();
      }
    }
  }

/*
 * 1. If the length of the ciphertext C is not k octets, output "decryption
 * error" and stop.
 *  2. Convert the ciphertext C to an integer ciphertext representative c: c =
 * OS2IP (C).
 *  3. Apply the RSADP decryption primitive (Section 5.1.2) to the private key
 * K and the ciphertext representative c to produce an integer message
 * representative m:
 * 
 * m = RSADP (K, c)
 * 
 * If RSADP outputs "ciphertext out of range," then output "decryption error"
 * and stop.
 *  4. Convert the message representative m to an encoded message EM of length
 * k-1 octets: EM = I2OSP (m, k-1)
 * 
 * If I2OSP outputs "integer too large," then output "decryption error" and
 * stop.
 *  5. Apply the EME-OAEP decoding operation to the encoded message EM and the
 * encoding parameters P to recover a message M:
 * 
 * M = EME-OAEP-DECODE (EM, P)
 * 
 * If the decoding operation outputs "decoding error," then output "decryption
 * error" and stop.
 *  6. Output the message M.
 *  
 */
  public byte[] decrypt(Key wrapping, byte[] wrapped) throws SignatureException {
    
    SignatureException.traceKey("Key for unwrapping(RSA OAEP)", wrapping);
    SignatureException.traceByte("Wrapped bytes (RSA OAEP)", wrapped);
  
    Reusable reusable = null;
    try {
      RSAPrivateKey temp = (RSAPrivateKey) wrapping;
      int k = (temp.getModulus().bitLength()+7)/8;      
      reusable = Reusable.getInstance(Constants.KEY_ENC_RSA_OAEP);
      CustomCipher cipher = (CustomCipher) reusable;// = SignatureContext.getCryptographicPool().generateCipherInstance(Constants.KEY_ENC_RSA_1_5, "ECB", "NoPadding");
      cipher.init(Cipher.DECRYPT_MODE, wrapping);
      byte[] decrypted = cipher.doFinal(wrapped, 0, wrapped.length);
      if (decrypted.length<k-1){
        byte[] t = new byte[k-1];
        System.arraycopy(decrypted,0,t,k-1-decrypted.length,decrypted.length);
        decrypted = t;
      } else if (decrypted.length>k-1){
        byte[] t = new byte[k-1];
        System.arraycopy(decrypted,decrypted.length-k+1,t,0,k-1);
        decrypted = t;        
      }
      byte[] ret = eme_oaep_decode(decrypted); 
      SignatureException.traceByte("Result of decryption(RSA OAEP)", ret);
      return ret;
    } catch (SignatureException ex){
      throw ex;
    } catch (Exception other){
      throw new SignatureException("Error in unwrapping key with RSA_OAEP_MGF1",other);
    } finally {
      if (reusable != null){
        reusable.release();
      }
    }
    
  }

  /*
   * 1.If l > 2^32(hLen), output "mask too long" and stop.
   * 2.Let T be the empty octet string.
   * 3.For counter from 0 to \lceil{l / hLen}\rceil-1, do the following:
   * a.Convert counter to an octet string C of length 4 with the primitive
   * I2OSP: C = I2OSP (counter, 4)
   * b.Concatenate the hash of the seed Z and C to the octet string T: T = T ||
   * Hash (Z || C)
   * 4.Output the leading l octets of T as the octet string mask.
   *  
   */

  protected static byte[] mgf1(byte[] seed, int offset, int seedLength, int outputLength) throws SignatureException {
    // l>2^32 - mask too long
    byte[] b = new byte[seedLength];
    System.arraycopy(seed,offset,b,0,seedLength);
    //step 1
    if (outputLength < 0) {
      throw new SignatureException("Illegal mask length");
    }
    Reusable reusable = null;
    try {
      reusable = Reusable.getInstance(Constants.DIGEST_SHA1);
      MessageDigest digest = (MessageDigest) reusable.getInternal();//SignatureContext.getCryptographicPool().getMessageDigestFromPool(Constants.DIGEST_SHA1);
      // initialization!!!
      byte[] t = new byte[outputLength];
      int index = 0;
      byte[] buffer = new byte[seedLength + 4];
      System.arraycopy(seed, offset, buffer, 0, seedLength);

      int copy;
      // step 3 & 4
      while (index < outputLength) {
        byte[] temp = digest.digest(buffer);
        copy = temp.length < (outputLength - index) ? temp.length : outputLength - index;
        System.arraycopy(temp, 0, t, index, copy);
        index += copy;
        inc(buffer);
      }
      return t;
    } catch (Exception ex1) {
      throw new SignatureException("Error in mask generation function", ex1);
    } finally {
      if (reusable != null){
        reusable.release();
      }
    }

  }

  protected static void inc(byte[] array) {
    int len = array.length - 1;
    array[len]++;
    while (array[len] == 0) {
      array[--len]++;
    }
  }

  /*
   * 1. If the length of P is greater than the input limitation for the hash
   * function (2^61-1 octets for SHA-1) then output "parameter string too long"
   * and stop. 
   * 2. If ||EM|| < 2hLen+1, then output "decoding error" and stop. 
   * 3. Let maskedSeed be the first hLen octets of EM and let maskedDB be the
   * remaining ||EM|| - hLen octets. 
   * 4. Let seedMask = MGF(maskedDB, hLen). 
   * 5. Let seed = maskedSeed \xor seedMask. 
   * 6. Let dbMask = MGF(seed, ||EM|| - hLen). 
   * 7. Let DB = maskedDB \xor dbMask. 
   * 8. Let pHash = Hash(P), an octet string of length hLen. 
   * 9. Separate DB into an octet string pHash' consisting of the first hLen octets of DB, a (possibly empty) octet string
   * PS consisting of consecutive zero octets following pHash', and a message M
   * as:
   *  DB = pHash' || PS || 01 || M
   * If there is no 01 octet to separate PS from M, output "decoding error" and
   * stop.
   * 10. If pHash' does not equal pHash, output "decoding error" and stop.
   * 11. Output M. 
   */
  protected byte[] eme_oaep_decode(byte[] encodedMessage) throws SignatureException {
    Reusable reusable = null;
    try {
      reusable = Reusable.getInstance(digestMethod);
      MessageDigest digest = (MessageDigest) reusable.getInternal();//digest = SignatureContext.getCryptographicPool().getMessageDigestFromPool(digestMethod);
      int k = digest.getDigestLength();
      //step 2
      if (encodedMessage.length < 2 * k + 1) {
        throw new SignatureException("EME-OAEP_DECODE error: insufficient length of encoded message: " + encodedMessage.length + " must be at least"
            + (2 * k + 1));
      }
      //step 4
      byte[] maskedSeed = mgf1(encodedMessage, k, encodedMessage.length - k, k);
      //step 5
      for (int i = 0; i < maskedSeed.length; i++) {
        maskedSeed[i] ^= encodedMessage[i];
      }
      //step 6
      byte[] dbMask = mgf1(maskedSeed, 0, maskedSeed.length, encodedMessage.length - k);
      //step 7
      for (int i = 0; i < dbMask.length; i++) {
        dbMask[i] ^= encodedMessage[i + k];
      }
      //step 8
      byte[] pHash = digest.digest(octets2);
      //step 10
      int index = 0;
      for (; index < k; index++) {
        if (pHash[index] != dbMask[index]) {
          throw new SignatureException("EME-OAEP_DECODE error - pHash does not equal pHash'");
        }
      }
      while (dbMask[index++] == 0) {
        ;
      }
      //step 9
      if (dbMask[index-1] != 1) {
        throw new SignatureException("EME-OAEP_DECODE error - no 01 separator befor message'");
      }
      byte[] decoded = new byte[dbMask.length - index];
      System.arraycopy(dbMask, index, decoded, 0, decoded.length);
      return decoded;
    } catch (SignatureException sig) {
      throw sig;
    } catch (Exception ex) {
      throw new SignatureException("Error in EME-OAEP-DECODE", new Object[]{encodedMessage, octets2, digestMethod}, ex);
    } finally {
      if (reusable!= null){
        reusable.release();
      }
    }
  }
  
  

/*
 * 1. If the length of P is greater than the input limitation for the hash
 * function (2^61-1 octets for SHA-1) then output "parameter string too long"
 * and stop.
 *  2. If ||M|| > emLen-2hLen-1 then output "message too long" and stop.
 *  3. Generate an octet string PS consisting of emLen-||M||-2hLen-1 zero
 * octets. The length of PS may be 0.
 *  4. Let pHash = Hash(P), an octet string of length hLen.
 *  5. Concatenate pHash, PS, the message M, and other padding to form a data
 * block DB as: DB = pHash || PS || 01 || M
 *  6. Generate a random octet string seed of length hLen.
 *  7. Let dbMask = MGF(seed, emLen-hLen).
 *  8. Let maskedDB = DB \xor dbMask.
 *  9. Let seedMask = MGF(maskedDB, hLen).
 * 
 * 10. Let maskedSeed = seed \xor seedMask.
 * 
 * 11. Let EM = maskedSeed || maskedDB.
 * 
 * 12. Output EM.
 */
  protected byte[] eme_oaep_encode(byte[] message, int emLen) throws SignatureException{
    Reusable reusable = null;
    try {
      reusable = Reusable.getInstance(digestMethod);
      MessageDigest digest = (MessageDigest) reusable.getInternal();//SignatureContext.getCryptographicPool().getMessageDigestFromPool(Constants.DIGEST_SHA1);
      int k = digest.getDigestLength();
      //step 2
      if (message.length >emLen- 2 * k - 1) {
        throw new SignatureException("EME-OAEP_ENCODE error: message too long: " + message.length + " must be at most"
            + (emLen- 2 * k - 1));
      }
      //step 3
      int PS = emLen - message.length-2*k-1;
      //step 4      
      byte[] pHash = digest.digest(octets2);
      //step 5
      byte[] db = new byte[k+PS+1+message.length];
      System.arraycopy(pHash,0,db,0,k);
      db[k+PS]=1;
      System.arraycopy(message,0,db,k+PS+1,message.length);
      byte[] seed = new byte[k];
      //step 6
      randomize(seed);
      //step 7
      byte[] dbSeed = mgf1(seed, 0, k, emLen - k);
      //step 8
      for (int i = 0; i < dbSeed.length; i++) {
        dbSeed[i] ^= db[i];
      }
      //step 9
      byte[] maskedSeed = mgf1(dbSeed, 0 , dbSeed.length, k);
      //step 10
      for (int i = 0; i < k; i++) {
        maskedSeed[i] ^= seed[i];
      }
      //step 11
      byte[]  ret = new byte[emLen];
      System.arraycopy(maskedSeed,0, ret,0,k);
      System.arraycopy(dbSeed,0, ret, k, dbSeed.length);
      return ret;
    } catch (SignatureException sig) {
      throw sig;
    } catch (Exception ex) {
      throw new SignatureException("Error in EME-OAEP-DECODE", new Object[]{message, octets2, digestMethod}, ex);
    } finally {
      if (reusable != null){
        reusable.release();
      }
    }
    
    
  }

  /**
   * @return Returns the digestMethod.
   */
  public String getDigestMethod() {
    return digestMethod;
  }
  /**
   * @param digestMethod The digestMethod to set.
   */
  public void setDigestMethod(String digestMethod) {
    this.digestMethod = digestMethod;
  }
  /**
   * @return Returns the octets2.
   */
  public byte[] getOctets2() {
    return octets2;
  }
  /**
   * @param octets2 The octets2 to set.
   */
  public void setOctets2(byte[] octets2) {
    this.octets2 = octets2;
  }
}
