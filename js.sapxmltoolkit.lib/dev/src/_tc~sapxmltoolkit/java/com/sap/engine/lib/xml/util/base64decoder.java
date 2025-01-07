/*
 * Created on 2005-7-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.xml.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author aleksandar-a
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class BASE64Decoder extends FilterInputStream {
  // if there are 3 arrays with 64x64 elements will it be better (and use
  // pem_convert_array)
  /*
   * table for first and second byte
   */
  private static final byte[][] b1 = new byte[256][256];

  /*
   * table for second and third byte
   */

  private static final byte[][] b2 = new byte[256][256];

  /*
   * table for third and 4th byte
   */

  private static final byte[][] b3 = new byte[256][256];

  private static final byte pem_convert_array[];

  private static final boolean[] notInAlpha;
  static {
    // alphabet - easy to change
    char pem_array[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/', '=' };
    pem_convert_array = new byte[256];
    for (int i = 0; i < 255; i++)
      pem_convert_array[i] = -1;

    for (int j = 0; j < pem_array.length; j++)
      pem_convert_array[pem_array[j]] = (byte) j;

    notInAlpha = new boolean[256];
    Arrays.fill(notInAlpha, true);
    int i;
    for (i = '0'; i <= '9'; i++) {
      notInAlpha[i] = false;
    }
    for (i = 'A'; i <= 'Z'; i++) {
      notInAlpha[i] = false;
    }
    for (i = 'a'; i <= 'z'; i++) {
      notInAlpha[i] = false;
    }
    notInAlpha['+'] = false;
    notInAlpha['/'] = false;
    notInAlpha['='] = false;
    for (int j = 0; j < 64; j++) {
      for (int k = 0; k < 64; k++) {
        b1[pem_array[j]][pem_array[k]] = (byte) (j << 2 & 0xfc | k >>> 4 & 0x3);
        b2[pem_array[j]][pem_array[k]] = (byte) (j << 4 & 0xf0 | k >>> 2 & 0xf);
        b3[pem_array[j]][pem_array[k]] = (byte) (j << 6 & 0xc0 | k & 0x3f);
      }
    }
  }

  private byte out1, out2, out3;

  private int in1, in2;

  private int bufsize;

  private byte byte0;

  public BASE64Decoder(InputStream inputstream) {
    super(inputstream);
  }

  public int read(byte abyte0[], int start, int j) throws IOException {
    j += start;
    int i = start;
    switch (bufsize) {
    case -1: {
      return -1;
    }
    case 1: {
      if (i < j) {
        abyte0[i++] = out1;
        bufsize = 0;
        break;
      }
      // len = 0 - do not read
      return 0;
    }
    case 2: {
      if (i < j) {
        abyte0[i++] = out1;
      } else {
        return 0;
      }
      if (i < j) {
        abyte0[i++] = out2;
        bufsize = 0;
        break;
      } else {
        out1 = out2;
        bufsize = 1;
        return 1;
      }
    }
    case 3: {
      if (i < j) {
        abyte0[i++] = out1;
      } else {
        return 0;
      }
      if (i < j) {
        abyte0[i++] = out2;
      } else {
        out1 = out2;
        out2 = out1;
        bufsize = 2;
        return 1;
      }
      if (i < j) {
        abyte0[i++] = out3;
        bufsize = 0;
        break;
      } else {
        out1 = out3;
        bufsize = 1;
        return 2;
      }
    }
    default:
    }

    int end = j - (j - i) % 3;
    // read 4x - writes 3x!!
    while (i < end) {
      // reads bytes with alpha checks - ignores symbols!
      do {
        in1 = in.read();
        if (in1 == -1) {
          bufsize = -1;
          return i - start;
        }
      } while (notInAlpha[in1]);
      do {
        in2 = in.read();
      } while (notInAlpha[in2]);

      if (in2 == 61) {
        bufsize = -1;
        return i - start;
      }
      abyte0[i++] = b1[in1][in2];
      do {
        in1 = in.read();
      } while (notInAlpha[in1]);
      if (in1 == 61) {
        bufsize = -1;
        return i - start;
      }
      abyte0[i++] = b2[in2][in1];
      do {
        in2 = in.read();
      } while (notInAlpha[in2]);
      if (in2 == 61) {
        bufsize = -1;
        return i - start;
      }
      abyte0[i++] = b3[in1][in2];
    }

    switch (j - i) {
    case 1: {
      do {
        in1 = in.read();
        if (in1 == -1) {
          bufsize = -1;
          return i - start;
        }
      } while (notInAlpha[in1]);
      do {
        in2 = in.read();
      } while (notInAlpha[in2]);

      if (in2 == 61) {
        bufsize = -1;
        return i - start;
      }
      abyte0[i++] = b1[in1][in2];
      do {
        in1 = in.read();
      } while (notInAlpha[in1]);
      if (in1 == 61) {
        bufsize = -1;
        return i - start;
      }
      out1 = b2[in2][in1];
      do {
        in2 = in.read();
      } while (notInAlpha[in2]);
      if (in2 == 61) {
        bufsize = 1;
        return i - start;
      }
      out2 = b3[in1][in2];
      bufsize = 2;
      break;
    }
    case 2: {
      do {
        in1 = in.read();
        if (in1 == -1) {
          bufsize = -1;
          return i - start;
        }
      } while (notInAlpha[in1]);
      do {
        in2 = in.read();
      } while (notInAlpha[in2]);

      if (in2 == 61) {
        bufsize = -1;
        return i - start;
      }
      abyte0[i++] = b1[in1][in2];
      do {
        in1 = in.read();
      } while (notInAlpha[in1]);
      if (in1 == 61) {
        bufsize = -1;
        return i - start;
      }
      abyte0[i++] = b2[in2][in1];
      do {
        in2 = in.read();
      } while (notInAlpha[in2]);
      if (in2 == 61) {
        bufsize = -1;
        return i - start;
      }
      out1 = b3[in1][in2];
      bufsize = 1;
    }
    default:
    }
    return i - start;
  }

  public int read() throws IOException {
    switch (bufsize) {
    case 0: {
      bufsize = read4();
      return read();
    }
    case 1: {
      bufsize = 0;
      return out1;
    }
    case 2: {
      bufsize = 1;
      byte0 = out1;
      out1 = out2;
      return byte0;
    }
    case 3: {
      bufsize = 2;
      byte0 = out1;
      out1 = out2;
      out2 = out3;
      return byte0;
    }
    }
    return -1;
  }

  public boolean markSupported() {
    return false;
  }

  public int available() throws IOException {
    return (bufsize == -1) ? 0 : 1;
  }

  private int read4() throws IOException {
    do {
      in1 = in.read();
      // this may happen only here - if last read byte was '=', followed by '='
      if ((in1 == -1)||(in1 == 61)) {
        return -1;
      }
    } while (notInAlpha[in1]);
    do {
      in2 = in.read();
    } while (notInAlpha[in2]);

    if (in2 == 61) {
      return -1;
    }
    out1 = b1[in1][in2];
    do {
      in1 = in.read();
    } while (notInAlpha[in1]);
    if (in1 == 61) {
      return 1;
    }
    out2 = b2[in2][in1];
    do {
      in2 = in.read();
    } while (notInAlpha[in2]);
    if (in2 == 61) {
      return 2;
    }
    out3 = b3[in1][in2];
    return 3;
  }

  /**
   * Decodes a base64 encoded byte array, taking into account the white spaces and symbols which are not present in the alphabet
   * @param base64Encoded
   * @return
   */
  public static byte[] decode(byte base64Encoded[]) {
    int filtered = 0; // Filtered bytes
    int length = base64Encoded.length;
    for (int k = 0; k < length; k++) {
      if (notInAlpha[base64Encoded[k]])
        filtered++;
    }
    int i = ((length - filtered) / 4) * 3;
    int lastindex1 = length - 1;
    while (notInAlpha[base64Encoded[lastindex1]]) {
      lastindex1--;
    }
    int lastindex2 = lastindex1 - 1;
    while (notInAlpha[base64Encoded[lastindex2]]) {
      lastindex2--;
    }

    if (base64Encoded[lastindex1] == 61) {
      i--;
      if (base64Encoded[lastindex2] == 61) {
        i--;
      }
    }

    byte decoded[] = new byte[i];
    byte byte0, byte1;
    int k = 0;
    int l = 0;
    int j = i - 3;
    for (; l <= j;) {
      while (notInAlpha[base64Encoded[k]]) {
        k++;
      }
      byte0 = base64Encoded[k++];
      while (notInAlpha[base64Encoded[k]]) {
        k++;
      }
      byte1 = base64Encoded[k++];
      decoded[l++] = b1[byte0][byte1];//(byte) (byte0 << 2 & 0xfc | byte1 >>> 4 & 0x3);
      while (notInAlpha[base64Encoded[k]]) {
        k++;
      }
      byte0 = base64Encoded[k++];
      decoded[l++] = b2[byte1][byte0];//(byte) (byte0 << 4 & 0xf0 | byte1 >>> 2 & 0xf);
      while (notInAlpha[base64Encoded[k]]) {
        k++;
      }
      byte1 = base64Encoded[k++];
      decoded[l++] = b3[byte0][byte1];//(byte) (byte0 << 6 & 0xc0 | byte1 & 0x3f);
    }
    if (l < i) {
      while (notInAlpha[base64Encoded[k]]) {
        k++;
      }
      byte0 = base64Encoded[k++];
      while (notInAlpha[base64Encoded[k]]) {
        k++;
      }
      byte1 = base64Encoded[k++];
      decoded[l++] = b1[byte0][byte1];//(byte) (byte0 << 2 & 0xfc | byte1 >>> 4 & 0x3);
      while (notInAlpha[base64Encoded[k]]) {
        k++;
      }
      if (base64Encoded[k] == 61)
        return decoded;
      //byte0 = byte1;
      byte0 = base64Encoded[k++];
      decoded[l++] = b2[byte1][byte0];//(byte) (byte0 << 4 & 0xf0 | byte1 >>> 2 & 0xf);
      while (notInAlpha[base64Encoded[k]]) {
        k++;
      }
      if (base64Encoded[k] == 61)
        return decoded;
    }

    return decoded;
  }

  /**
   * Decodes base64 encoded byte array which does not contain white spaces. Be cautious when using it.
   * @param base64encoded
   * @return
   */

  public static byte[] decodeN(byte base64encoded[]) {
    int length = base64encoded.length;
    int i = length / 4 * 3;
    if (base64encoded[--length] == 61) {
      i--;
      if (base64encoded[--length] == 61) {
        i--;
      }
    }

    byte decoded[] = new byte[i];
    int k = 0;
    int l = 0;
    int j = i - 3;
    byte byte0, byte1;
    for (; l <= j;) {
      byte0 = base64encoded[k++];
      byte1 = base64encoded[k++];
      decoded[l++] = b1[byte0][byte1];//(byte) (byte0 << 2 & 0xfc | byte1 >>> 4 & 0x3);
      byte0 = base64encoded[k++];
      decoded[l++] = b2[byte1][byte0];//(byte) (byte0 << 4 & 0xf0 | byte1 >>> 2 & 0xf);
      byte1 = base64encoded[k++];
      decoded[l++] = b3[byte0][byte1];//(byte) (byte0 << 6 & 0xc0 | byte1 & 0x3f);
    }

    if (l < i) {
      byte0 = base64encoded[k++];
      byte1 = base64encoded[k++];
      decoded[l++] = b1[byte0][byte1];//(byte) (byte0 << 2 & 0xfc | byte1 >>> 4 & 0x3);
      if (base64encoded[k] == 61)
        return decoded;
      //byte0 = byte1;
      byte0 = base64encoded[k++];
      decoded[l++] = b2[byte1][byte0];//(byte) (byte0 << 4 & 0xf0 | byte1 >>> 2 & 0xf);
      if (base64encoded[k] == 61)
        return decoded;
    }

    return decoded;
  }

}
