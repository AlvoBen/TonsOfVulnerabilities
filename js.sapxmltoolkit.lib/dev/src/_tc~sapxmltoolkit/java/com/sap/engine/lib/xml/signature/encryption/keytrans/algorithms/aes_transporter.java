/*
 * Created on 2004-3-31
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.encryption.keytrans.algorithms;

import java.security.Key;
import java.util.Arrays;

import javax.crypto.Cipher;

import com.sap.engine.lib.xml.signature.Configurator;
import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureContext;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.encryption.keytrans.KeyTransporter;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *  
 */
public class AES_Transporter extends KeyTransporter {

  public String algorithmURI = Constants.ALG_ENC_AES128;

  public AES_Transporter(){
    
  }
  
  public AES_Transporter(String algorithm){
    algorithmURI = algorithm;

  }  
  
  /*
  *1.  If N is 1: 
  *  o B=AES(K)enc(0xA6A6A6A6A6A6A6A6|P(1)) 
  *    o C(0)=MSB(B) 
  *    o C(1)=LSB(B) 
  *  If N>1, perform the following steps: 
  *  2.  Initialize variables: 
  *    o Set A to 0xA6A6A6A6A6A6A6A6 
  *    o Fori=1 to N,
  *      R(i)=P(i) 
  *  3.  Calculate intermediate values: 
  *    o Forj=0 to 5, 
  *      ? For i=1 to N,
  *        t= i + j*N
  *        B=AES(K)enc(A|R(i))
  *        A=XOR(t,MSB(B))
  *        R(i)=LSB(B) 
  *  4.  Output the results: 
  *    o Set C(0)=A 
  *    o For i=1 to N,
  *      C(i)=R(i) 
  */
  public byte[] encrypt(Key wrapping, Key wrapped) throws SignatureException {
    
    SignatureException.traceKey("Key for wrapping(AES)", wrapping);
    SignatureException.traceKey("Key to be wrapped(AES)", wrapped);
    
    Cipher cipher = null;
    try {
      cipher = SignatureContext.getCryptographicPool().getCipherFromPool(Configurator.getCipherAlgorithm(Constants.ALG_ENC_AES128/*algorithmURI*/).concat("/ECB/NoPadding"));
      cipher.init(Cipher.ENCRYPT_MODE, wrapping);
      byte[] B = new byte[16];
      byte[] encoded = wrapped.getEncoded();
      byte[] result = new byte[encoded.length + 8];
      // fill in 0xA6
      Arrays.fill(B, 0, 8, (byte) 0xa6);
      // if 6*encoded.length>255 increment, decrement and init are not correct!
      if ((encoded.length % 8 != 0) || (6 * encoded.length > 255)) {
        throw new SignatureException("Not supported wrapping size:" + result.length);
      }
      System.arraycopy(encoded,0,result,8,encoded.length);
      byte t = 0;
      int N = encoded.length / 8;
      for (int j = 0; j <= 5; j++) {
        int startIndex = 8;
        for (int i = 1; i <= N; i++) {
//          increment(t);
          t++;
          System.arraycopy(result, startIndex, B, 8, 8);
          // does not work correctly with BC
          cipher.update(B, 0, 16, B, 0);
//          for (int i1 = 0; i1 < 8; i1++) {
//            B[i1] ^= t[i1];
//          }
          B[7] ^= t;
          System.arraycopy(B, 8, result, startIndex, 8);
          startIndex += 8;
        }
      }

      System.arraycopy(B, 0, result, 0, 8);
      SignatureException.traceByte("Result of encryption(AES)", result);
      return result;
    } catch (SignatureException sig) {
      throw sig;
    } catch (Exception ex) {
      throw new SignatureException(ex);
    } finally {
      SignatureContext.getCryptographicPool().releaseCipher(cipher);
    }
  }

  static byte[] testIV = new byte[]{ (byte) 0xa6,(byte) 0xa6,(byte) 0xa6,(byte) 0xa6,
                                    (byte) 0xa6,(byte) 0xa6,(byte) 0xa6,(byte) 0xa6,
                                    (byte) 0xa6,(byte) 0xa6,(byte) 0xa6,(byte) 0xa6,
                                    (byte) 0xa6,(byte) 0xa6,(byte) 0xa6,(byte) 0xa6};
  
  /*
   * 
   *      1. If N is 1: 
   *        o B=AES(K)dec(C(0)|C(1)) 
   *        o P(1)=LSB(B) 
   *        o If MSB(B) is 0xA6A6A6A6A6A6A6A6, return success. Otherwise, return an integrity check failure error. 
   *      If N>1, perform the following steps: 
   *      2.  Initialize the variables: 
   *        o A=C(0) 
   *        o For i=1 to N,
   *          R(i)=C(i) 
   *      3.  Calculate intermediate values: 
   *        o For j=5 to 0, 
   *          ? For i=N to 1,
   *            t= i + j*N
   *            B=AES(K)dec(XOR(t,A)|R(i))
   *            A=MSB(B)
   *            R(i)=LSB(B) 
   *      4.  Output the results: 
   *        o For i=1 to N,
   *          P(i)=R(i) 
   *        o If A is 0xA6A6A6A6A6A6A6A6, return success. Otherwise, return an integrity check failure error. 
   */

  public byte[] decrypt(Key wrapping, byte[] wrapped) throws SignatureException {
    
    SignatureException.traceKey("Key for unwrapping(AES)", wrapping);
    SignatureException.traceByte("Wrapped bytes (AES)", wrapped);
    
    Cipher cipher = null;
    try {
      
      cipher = SignatureContext.getCryptographicPool().getCipherFromPool(Configurator.getCipherAlgorithm(Constants.ALG_ENC_AES128/*algorithmURI*/).concat("/ECB/NoPadding"));//generateCipherInstance(algorithmURI, "CBC", "NoPadding");
      cipher.init(Cipher.DECRYPT_MODE, wrapping);
      

      byte[] B = new byte[16];
      byte[] result = new byte[wrapped.length - 8];

      if ((result.length % 8 != 0) || (6 * result.length > 255)) {
        throw new SignatureException("Not supported wrapping size:" + result.length);
      }
      
      int N = result.length / 8;
      byte t = (byte) (6*N);
      
      //      int t = 6*N;
      // 128 bit keys - N=16 6*16=96
      // for AES_256 - N=32 6*32=192 - change decrement may be!
      System.arraycopy(wrapped, 0, B, 0, 8);
//      init(t, 6 * N);
      for (int j = 5; j >= 0; j--) {
        int startIndex = 8 * N;
        for (int i = N; i > 0; i--) {
//          for (int i1 = 0; i1 < 8; i1++) {
//            B[i1] ^= t[i1];
//          }
          B[7]^=t;
          System.arraycopy(wrapped, startIndex, B, 8, 8);
//          Test1.dumpByte(B);
          cipher.update(B, 0, 16, B, 0);
//          B = cipher.update(B, 0, 16);
//          Test1.dumpByte(B);
          System.arraycopy(B, 8, wrapped, startIndex, 8);
          startIndex -= 8;
          t--;
        }
      }

      for (int i = 0; i < 8; i++) {
        if (B[i] != (byte) 0xa6) {
          throw new SignatureException("Unable to unwrap key with algorithm " + algorithmURI, new Object[]{wrapping.getEncoded(), wrapped});
        }
      }
      System.arraycopy(wrapped, 8, result, 0, result.length);
      SignatureException.traceByte("Result of decryption(AES)", result);
      return result;
    } catch (SignatureException sig) {
      throw sig;
    } catch (Exception ex) {
      throw new SignatureException(ex);
    } finally {
      SignatureContext.getCryptographicPool().releaseCipher(cipher);
    }

  }

//  protected void init(byte[] b, int i) {
//    b[7] = (byte) i;
//  }
//
//  protected void decrement(byte[] b) {
//    b[7]--;
//  }
//
//  protected void increment(byte[] b) {
//    b[7]++;
//  }

}
