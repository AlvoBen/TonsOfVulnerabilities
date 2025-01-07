/*
 * Created on 2005-4-21
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.crypto;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public class CustomCipherOutputStream extends FilterOutputStream {

  public static final int BUFFER_SIZE = 512;
  private CustomCipher customCipher = null;
  private OutputStream outputStream = null;
  private byte inBuffer[] = null;
  private byte outBuffer[] = new byte[BUFFER_SIZE];

  public CustomCipherOutputStream(OutputStream outputstream, CustomCipher cipher) {
    super(outputstream);
    inBuffer = new byte[1];
    outBuffer = new byte[BUFFER_SIZE];
    outputStream = outputstream;
    customCipher = cipher;
  }

  public void write(int i) throws java.io.IOException {
    inBuffer[0] = (byte) i;
    write(inBuffer, 0, 1);
  }

  public void write(byte abyte0[]) throws java.io.IOException {
    write(abyte0, 0, abyte0.length);
  }

  public void write(byte abyte0[], int start, int length) throws java.io.IOException {
    int processed, pro;
    while (length > 0){
      pro = Math.min(length, BUFFER_SIZE);
      while (customCipher.getOutputSize(pro) > BUFFER_SIZE){
        pro--;
      }
      try {
        processed = customCipher.update(abyte0, start, pro, outBuffer, 0);
      } catch (Exception e) {
        throw new IOException(e.getMessage());
      }
      outputStream.write(outBuffer, 0, processed);
      start += pro;
      length -= pro;
    }
//    outBuffer = customCipher.update(abyte0, start, length);
//    if (outBuffer != null) {
//      outputStream.write(outBuffer);
//      outBuffer = null;
//    }
  }

  public void flush() throws java.io.IOException {
//    if (outBuffer != null) {
//      outputStream.write(outBuffer);
//      outBuffer = null;
//    }
    outputStream.flush();
  }

  public void close() throws java.io.IOException {
    try {
      int res = customCipher.doFinal(outBuffer, 0);
      outputStream.write(outBuffer, 0, res);
    } catch (Exception ex){ 
      throw new IOException(ex.getMessage());
    }
    flush();
    super.out.close();
  }
}
