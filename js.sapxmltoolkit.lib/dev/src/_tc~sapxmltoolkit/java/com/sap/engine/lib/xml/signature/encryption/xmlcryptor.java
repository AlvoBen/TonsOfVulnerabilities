package com.sap.engine.lib.xml.signature.encryption;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

import org.w3c.dom.*;

import com.sap.engine.lib.xml.dom.BinaryTextImpl;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.signature.Configurator;
import com.sap.engine.lib.xml.signature.SignatureContext;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.crypto.CustomCipher;
import com.sap.engine.lib.xml.signature.crypto.Reusable;
import com.sap.engine.lib.xml.signature.crypto.impl.ReusableSecureRandom;
import com.sap.engine.lib.xml.signature.transform.TransformationFactory;
import com.sap.engine.lib.xml.signature.transform.algorithms.Canonicalization;
import com.sap.engine.lib.xml.util.BASE64Decoder;

public abstract class XMLCryptor {

  public TransformationFactory trFact = TransformationFactory.newInstance();
  protected Key key = null;
  protected String algorithmURI = null;
  protected byte[] IV = null;
  protected AlgorithmParameters algParams = null;
  protected String primaryEncodingFormat = "ASN.1";
  protected String prngIdentifier = "SHA1PRNG";
  protected byte[] randomSeed = null;
  protected int ivLength = -1;

  public AlgorithmParameters getAlgorithmParameters() {
    return algParams;
  }

  public void specifyRandomness(String identifier, byte[] seed) {
    this.prngIdentifier = identifier;
    this.randomSeed = seed;
  }

  public void setAlgorithmParameters(AlgorithmParameters algParams) {
    this.algParams = algParams;
  }

  public byte[] getIV() {
    return IV;
  }

  public void setIV(byte[] IV) {
    this.IV = IV;
  }


  /**
   *
   * @param key
   * @deprecated use setEncryptionKey, because this method does not provide means to see if
   * the setKey operation has been successful. 
   */
  public void setKey(Key key){
    this.key = key;
    try {
      this.algorithmURI = Configurator.getCipherURIFromJCE(key.getAlgorithm());
    } catch (SignatureException e) {
//    $JL-EXC$      
    }
  }
  
  public void setEncryptionKey(Key key) throws SignatureException{
    this.key = key;
    this.algorithmURI = Configurator.getCipherURIFromJCE(key.getAlgorithm());
  }  

  public void setIVLength(int ivLength) {
    this.ivLength = ivLength;
  }

  // ----- actual encryption/decryption
  protected String decrypt0(byte[] content) throws IOException, GeneralSecurityException, SignatureException, IllegalArgumentException, IllegalAccessException,
      InvocationTargetException {
    byte[] encryptedWithIV = BASE64Decoder.decode(content);
    Reusable reusable = null;
    try {
      reusable = Reusable.getInstance(algorithmURI);
      CustomCipher ciph = (CustomCipher) reusable;
      int start = ciph.getIVLength();
      IV = new byte[start];
      System.arraycopy(encryptedWithIV, 0, IV, 0, start);
      IvParameterSpec spec = new IvParameterSpec(IV);
      ciph.init(Cipher.DECRYPT_MODE, key, spec);
      return new String(ciph.doFinal(encryptedWithIV, start, encryptedWithIV.length - start)); //$JL-I18N$
    } finally {
      if (reusable != null) {
        reusable.release();
      }
    }
  }

  protected byte[] decrypt1(byte[] content) throws IOException, GeneralSecurityException, SignatureException, IllegalArgumentException, IllegalAccessException,
      InvocationTargetException {
    byte[] encryptedWithIV = BASE64Decoder.decode(content);
    Reusable reusable = null;
    try {
      reusable = Reusable.getInstance(algorithmURI);
      CustomCipher ciph = (CustomCipher) reusable;

      int start = ciph.getIVLength();
      IV = new byte[start];
      System.arraycopy(encryptedWithIV, 0, IV, 0, start);
      
      IvParameterSpec spec = new IvParameterSpec(IV);
      ciph.init(Cipher.DECRYPT_MODE, key, spec);
      return ciph.doFinal(encryptedWithIV, start, encryptedWithIV.length - start);
    } finally {
      if (reusable != null) {
        reusable.release();
      }
    }
  }  
  
  protected byte[] toEncrypt;
  
  protected void encrypt0() throws GeneralSecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
  Reusable reusable = null;
  try {
    reusable = Reusable.getInstance(algorithmURI);
    CustomCipher ciph = (CustomCipher) reusable;
    IV = new byte[ciph.getIVLength()];
    randomize(IV);
    IvParameterSpec spec = new IvParameterSpec(IV);
    ciph.init(Cipher.ENCRYPT_MODE, key, spec);
    // here is this length + 1 -> this is valid only in 640, because of the padding
    byte[] total = new byte[IV.length + ciph.getOutputSize(toEncrypt.length)];
    System.arraycopy(IV, 0, total, 0, IV.length);
    ciph.doFinal(toEncrypt, 0, toEncrypt.length, total, IV.length);
    toEncrypt = total;
  } finally {
    if (reusable!=null){
      reusable.release();
    }
  }
  }
  
  
  protected byte[] encrypt0(byte[] toEncrypt) throws GeneralSecurityException, SignatureException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    Reusable reusable = null;
    try {
      reusable = Reusable.getInstance(algorithmURI);
      CustomCipher ciph = (CustomCipher) reusable;
      IV = new byte[ciph.getIVLength()];
      randomize(IV);
      IvParameterSpec spec = new IvParameterSpec(IV);
      ciph.init(Cipher.ENCRYPT_MODE, key, spec);
      byte[] total = new byte[IV.length + ciph.getOutputSize(toEncrypt.length +1)];
      System.arraycopy(IV, 0, total, 0, IV.length);
      ciph.doFinal(toEncrypt, 0, toEncrypt.length, total, IV.length);
      return total;
    } finally {
      if (reusable!=null){
        reusable.release();
      }
    }
  }

  protected byte[] encrypt0(Element el, boolean contentOnly) throws SignatureException, IllegalArgumentException, GeneralSecurityException, IllegalAccessException, InvocationTargetException {
    if (el instanceof BinaryTextImpl){
      toEncrypt = ((BinaryTextImpl) el).getBinaryData();
      //REMOVE
      ((BinaryTextImpl) el).setBinaryData(null);
    } else {
      if (contentOnly) {
        NodeList children = el.getChildNodes();
        int ln = children.getLength();
        if ((ln==1)&&(children.item(0) instanceof BinaryTextImpl)){
          toEncrypt = ((BinaryTextImpl) children.item(0)).getBinaryData();
          //REMOVE
          ((BinaryTextImpl) children.item(0)).setBinaryData(null);
        } else {
          DocumentFragment df = el.getOwnerDocument().createDocumentFragment();
  
          for (int i = 0; i < ln; i++) {
            df.appendChild(children.item(i).cloneNode(true));
          }
          toEncrypt = Canonicalization.canonicalize(df, true);
        }
      } else {
        toEncrypt = Canonicalization.canonicalize(el, true);
      }
    }
    encrypt0();
    byte[] temp = toEncrypt;
    toEncrypt = null;
    return temp;
  }

  protected static byte[] append(byte[] arg1, byte[] arg2) {
    int resLength = arg1.length + arg2.length;
    byte[] result = new byte[resLength];
    System.arraycopy(arg1, 0, result, 0, arg1.length);
    System.arraycopy(arg2, 0, result, arg1.length, arg2.length);
    return result;
  }

  protected int getEncryptedNoIV(byte[] encryptedWithIV) throws SignatureException {
    int wholeLength = encryptedWithIV.length;
    ivLength = getLength();
    IV = new byte[ivLength];
    System.arraycopy(encryptedWithIV, 0, IV, 0, ivLength);
    return ivLength;
  }

  protected int getLength() throws SignatureException {
    if (this.ivLength > 0) {
      return this.ivLength;
    }

    return Configurator.getIVLength(algorithmURI);
  }

  // ------- dom utilities
  static CharArray temp = new CharArray(1000,1000);
  
  public synchronized static String gatherText(Node n) {
    temp.clear();
    NodeList nl = n.getChildNodes();

    for (int i = 0; i < nl.getLength(); i++) {
      Node next = nl.item(i);

      if (next.getNodeType() == Node.TEXT_NODE) {
        temp.append(next.getNodeValue());
      }
    }

    return temp.toString();
  }
  
  public static byte[] gatherBytes(Node n) {
    try {
      NodeList nl = n.getChildNodes();
      ByteArrayOutputStream out =
        SignatureContext.getByteArrayOutputStreamPool().getInstance();
      int len = nl.getLength(); 
      if ( len >0){
        Node nn = nl.item(0);
        if (nn instanceof BinaryTextImpl) {
          if (len==1){
            return ((BinaryTextImpl) nn).getBinaryData();
          }
          out.write(((BinaryTextImpl) nn).getBinaryData());
          
        } else if ((nn.getNodeType() == Node.TEXT_NODE)||(nn.getNodeType() == Node.CDATA_SECTION_NODE)) {
          if (len==1){
            return nn.getNodeValue().getBytes(); //$JL-I18N$
          }
          out.write(nn.getNodeValue().getBytes()); //$JL-I18N$

        }
      } else {
        return new byte[0];
      }
      for (int i = 1; i < len; i++) {
        Node next = nl.item(i);
        if (next instanceof BinaryTextImpl) {
          out.write(((BinaryTextImpl) next).getBinaryData());
        } else if ((next.getNodeType() == Node.TEXT_NODE)||(next.getNodeType() == Node.CDATA_SECTION_NODE)) {
          out.write(next.getNodeValue().getBytes()); //$JL-I18N$
        }
      }
      return out.toByteArray();
    } catch (IOException ex) {
      //if this happens!
      return null;
    }
  }

  protected static void deleteContent(Element e) {
    NodeList nl = e.getChildNodes();

    for (int i = 0; i < nl.getLength(); i++) {
      Node next = nl.item(i);

      if (next.getNodeType() == Node.TEXT_NODE) {
        e.removeChild(next);
      }
    }
  }

  protected String getAlgOnly(String algWithModePad) {
    return algWithModePad.substring(0, algWithModePad.indexOf("/"));
  }

  protected void randomize(byte[] input) throws GeneralSecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    Reusable reusable = null;
    try {
      reusable = Reusable.getInstance(ReusableSecureRandom.SHA1_PRNG_URI);
      SecureRandom rand = (SecureRandom) reusable.getInternal();

      if (randomSeed != null) {
        rand.setSeed(randomSeed);
      }

      rand.nextBytes(input);
    } finally {
      if (reusable != null) {
        reusable.release();
      }
    }
  }

  /**
   * This method does multistep encryption - in such way there is no need to allocate large buffer.
   * @param cipher initialized cipher to be used for encryption/decryption
   * @param encoded data to be encrypted/decrypted
   * @param start start of the data to be encrypted/decrypted
   * @param length length of bytes to be encrypted/decrypted
   * @param decoded byte array where the result of the current operation will be written
   * @param offset offset of the begining of the storage for the result
   * @param max_size max_size buffer to be used.
   * @return new offset in the resulting byte array
   * @throws IllegalStateException
   * @throws IllegalBlockSizeException
   * @throws ShortBufferException
   * @throws BadPaddingException
   */
  public static int decryptBuffer(Cipher cipher, byte[] encoded, int start, int length, byte[] decoded, int offset, int max_size) throws IllegalStateException,
      IllegalBlockSizeException, ShortBufferException, BadPaddingException {
    int toRead = max_size < length ? max_size : length;
    int read;
    do {
      read = cipher.update(encoded, start, toRead, decoded, offset);
      length -= toRead;
      start += toRead;
      offset += read;
      toRead = max_size < length ? max_size : length;
    } while (toRead > 0);
    read = cipher.doFinal(decoded, offset);
    offset += read;
    return offset;
  }
}