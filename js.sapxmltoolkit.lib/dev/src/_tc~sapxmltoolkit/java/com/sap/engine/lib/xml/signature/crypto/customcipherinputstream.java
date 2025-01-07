/*
 * Created on 2005-4-21 @author Alexander Alexandrov,
 * e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.crypto;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

import com.sap.engine.lib.xml.signature.SignatureException;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 */
public class CustomCipherInputStream extends FilterInputStream {
  public static final int BUFFER_SIZE = 512;
  private CustomCipher customCipher = null;
  private InputStream inputStream = null;
  private byte inputBuffer[] = null;
  private boolean endReached = false;
  private byte outputBuffer[] = null;
  private int index = 0;
  private int end = 0;

  private int decrypt() throws IOException {
    if (endReached) {
      return -1;
    }
    int i = inputStream.read(inputBuffer);
    if (i == -1) {
      endReached = true;
      try {
        end = customCipher.doFinal(outputBuffer, 0);
      } catch (IllegalBlockSizeException illegalblocksizeexception) {
        outputBuffer = null;
      } catch (BadPaddingException badpaddingexception) {
        outputBuffer = null;
      } catch (IllegalStateException e) {
        outputBuffer = null;
      } catch (ShortBufferException e) {
        outputBuffer = null;
      }
      if (outputBuffer == null) {
        return -1;
      }
      index = 0;
      //end = outputBuffer.length;
      SignatureException.traceByteAsString("Decrypted last portion of bytes", outputBuffer);
      return end;

    }
    try {
      end = customCipher.update(inputBuffer, 0, i, outputBuffer, 0);
    } catch (IllegalStateException illegalstateexception) {
      outputBuffer = null;
    } catch (ShortBufferException e) {
      outputBuffer = null;
    }
    index = 0;
    if (outputBuffer == null) {
      end = 0;
    } else {
      SignatureException.traceByteAsString("Decrypted portion of bytes", outputBuffer);
      //end = outputBuffer.length;
    }
    return end;
  }

  public CustomCipherInputStream(InputStream inputstream, CustomCipher cipher) {
    super(inputstream);
    inputBuffer = new byte[BUFFER_SIZE];
    endReached = false;
    index = 0;
    end = 0;
    inputStream = inputstream;
    customCipher = cipher;
    outputBuffer = new byte[BUFFER_SIZE];
  }

  public int read() throws IOException {
    if (index >= end) {
      int i;
      for (i = 0; i == 0; i = decrypt()) {
      }
      if (i == -1) {
        return -1;
      }
    }
    return outputBuffer[index++] & 0xff;
  }

  public int read(byte abyte0[]) throws IOException {
    return read(abyte0, 0, abyte0.length);
  }

  public int read(byte abyte0[], int start, int length) throws IOException {
    if (index >= end) {
      int k;
      for (k = 0; k == 0; k = decrypt()) {
      }
      if (k == -1) {
        return -1;
      }
    }
    if (length <= 0) {
      return 0;
    }
    int l = end - index;
    if (length < l) {
      l = length;
    }
    if (abyte0 != null) {
      System.arraycopy(outputBuffer, index, abyte0, start, l);
    }
    index = index + l;
    return l;
  }

  public long skip(long l) throws IOException {
    int i = end - index;
    if (l > i) {
      l = i;
    }
    if (l < 0L) {
      return 0L;
    }
    index += ((int) (l));
    return l;

  }

  public int available() throws IOException {
    return end - index;
  }

  public void close() throws IOException {
    inputStream.close();
    try {
      customCipher.doFinal();
    } catch (BadPaddingException badpaddingexception) {
      // $JL-EXC$
    } catch (IllegalBlockSizeException illegalblocksizeexception) {
      // $JL-EXC$
    }
    index = 0;
    end = 0;
  }

  public boolean markSupported() {
    return false;
  }
}
