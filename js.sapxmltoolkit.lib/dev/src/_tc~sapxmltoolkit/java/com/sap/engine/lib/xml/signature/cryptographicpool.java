/*
 * Created on 2004-2-17
 * 
 * @author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature;

import java.security.*;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *  
 */

public class CryptographicPool {

  public int MAX_SIZE       = 20;
  int        signatureSize  = 0;
  Hashtable  signatures     = new Hashtable(4);

  int        cipherSize     = 0;
  Hashtable  ciphers        = new Hashtable(4);

  int        messageSize    = 0;
  Hashtable  messageDigests = new Hashtable(4);

  // pooling methods

  // signature pooling
  public Signature getSignatureFromPool(String algorithmURI) throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
    String algorithm = Configurator.getSignatureAlgorithm(algorithmURI);//(String)
                                                                        // signatureAlgorithms.get(algorithmURI);
    if (algorithm != null) {
      synchronized (signatures) {
        Vector v = (Vector) signatures.get(algorithm);
        int i = v != null ? v.size() : 0;
        if (i > 0) {
          signatureSize--;
          return (Signature) v.remove(i - 1);
        }
      }
    }
    return Signature.getInstance(algorithm, Configurator.getProviderName());
  }

  public void releaseSignature(Signature signature) {
    if (signature == null)
      return;
    String alg = signature.getAlgorithm();
    synchronized (signatures) {
      if (signatureSize < MAX_SIZE) {
        Vector v = (Vector) signatures.get(alg);
        if (v == null) {
          v = new Vector(4);
          signatures.put(alg, v);
        }
        v.addElement(signature);
        signatureSize++;
      }
    }
  }

  //cipher pooling

  private String constructCipherRequest(String algorithmURI, String cryptMode, String cryptPadding) throws SignatureException {
    String algorithm = Configurator.getCipherAlgorithm(algorithmURI);//(String)
                                                                     // cipherAlgorithms.get(algorithmURI);
    if ((algorithm == null) || (cryptMode == null) || (cryptPadding == null)) {
      throw new SignatureException("Unknown algorithm URI: " + algorithmURI + ".");
    }
    if ("CBC".equals(cryptMode) && "NoPadding".equals(cryptPadding)) {
      return algorithm.concat("/CBC/NoPadding");
    } else {
      //algorithmURI + '/' +
      int al = algorithm.length();
      int cl = cryptMode.length() + al;
      char[] buffer = new char[cl + cryptPadding.length() + 2];
      algorithm.getChars(0, al, buffer, 0);
      buffer[al] = '/';
      cryptMode.getChars(0, cryptMode.length(), buffer, al + 1);
      buffer[cl + 1] = '/';
      cryptPadding.getChars(0, cryptPadding.length(), buffer, cl + 2);
      return new String(buffer);
      //      return (algorithm + "/" + cryptMode + "/" + cryptPadding);
    }
  }

  public Cipher generateCipherInstance(String algorithmURI, String cryptMode, String cryptPadding) throws SignatureException, GeneralSecurityException {
    return getCipherFromPool(constructCipherRequest(algorithmURI, cryptMode, cryptPadding));
  }

  public Cipher getCipherFromPool(String algorithm) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
    if (algorithm != null) {
      synchronized (ciphers) {
        Vector v = (Vector) ciphers.get(algorithm);
        int i = v != null ? v.size() : 0;
        if (i > 0) {
          cipherSize--;
          return (Cipher) v.remove(i - 1);
        }
      }
    }
    return Cipher.getInstance(algorithm, Configurator.getProviderName());
  }

  public void releaseCipher(Cipher cipher) {
    if (cipher == null)
      return;
    String alg = cipher.getAlgorithm();
    synchronized (ciphers) {
      if (cipherSize < MAX_SIZE) {
        Vector v = (Vector) ciphers.get(alg);
        if (v == null) {
          v = new Vector(4);
          ciphers.put(alg, v);
        }
        v.addElement(cipher);
        cipherSize++;
      }
    }
  }

  // message digest pooling

  public MessageDigest getMessageDigestFromPool(String algorithmURI) throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
    String algorithm = Configurator.getDigestAlgorithm(algorithmURI);//(String)
                                                                     // digestAlgorithms.get(algorithmURI);
    if (algorithm != null) {
      synchronized (messageDigests) {
        Vector v = (Vector) messageDigests.get(algorithm);
        int i = v != null ? v.size() : 0;
        if (i > 0) {
          messageSize--;
          return (MessageDigest) v.remove(i - 1);
        }
      }
    }
    return MessageDigest.getInstance(algorithm, Configurator.getProviderName());
  }

  public void releaseMessageDigest(MessageDigest messageDigest) {
    if (messageDigest == null)
      return;
    String alg = messageDigest.getAlgorithm();
    synchronized (messageDigests) {
      if (messageSize < MAX_SIZE) {
        Vector v = (Vector) messageDigests.get(alg);
        if (v == null) {
          v = new Vector(4);
          messageDigests.put(alg, v);
        }
        v.addElement(messageDigest);
        messageSize++;
      }
    }
  }

}
