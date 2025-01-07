/*
 * Created on 2005-7-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.xml.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author aleksandar-a
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class BASE64Encoder extends FilterOutputStream {
  
  protected static boolean skipNewLines = Boolean.getBoolean("com.sap.xml.security.skipCL");
  /*
   * First byte table
   */
  static byte[] b1 = new byte[256];

  /*
   * First and second byte table
   */
  static byte[][] b2 = new byte[256][256];

  /*
   * Second and third byte table
   */
  static byte[][] b3 = new byte[256][256];

  /*
   * Third byte table
   */
  static byte[] b4 = new byte[256];

  static {
    /*
     * One can easily change the alphabet
     */
    char pem_array[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
    for (int i = 0; i < 256; i++) {
      b1[i] = (byte) pem_array[i >>> 2 & 0x3f];
      b4[i] = (byte) pem_array[i & 0x3f];
      for (int j = 0; j < 256; j++) {
        b2[i][j] = (byte) pem_array[(i << 4 & 0x30) + (j >>> 4 & 0xf)];
        b3[i][j] = (byte) pem_array[(i << 2 & 0x3c) + (j >>> 6 & 0x3)];
      }
    }
  }

  private int bufsize;

  private int count;

  private int bytesPerLine;

  private int byte0;

  private int byte1;

  private int byte2;

  public BASE64Encoder(OutputStream outputstream, int i) {
    super(outputstream);
    bytesPerLine = i / 4;
  }

  public BASE64Encoder(OutputStream outputstream) {
    this(outputstream, 80);
  }

  public void write(byte input[], int start, int length) throws IOException {
    // j is end byte!
    length += start;
    switch (bufsize) {
    case 1: {
      if (start < length) {
        byte1 = input[start++] & 0xff;
      } else {
        bufsize = 1;
        return;
      }
      // executed 2 times if it is 1!!!
    }
    case 2: {
      if (start < length) {
        byte2 = input[start++] & 0xff;
      } else {
        bufsize = 2;
        return;
      }
    }
    // flushes buffer
    case 3: {
      bufsize = 0;
      if (count == bytesPerLine) {
        out.write(13);
        out.write(10);
        count = 0;
      }
      out.write(b1[byte0]);
      out.write(b2[byte0][byte1]);
      out.write(b3[byte1][byte2]);
      out.write(b4[byte2]);
      count++;
    }
    default:
    }
    // writes directly 3X bytes -> 4X output
    int end = length - (length - start) % 3;
    for (; start < end;) {
      if (count == bytesPerLine) {
        out.write(13);
        out.write(10);
        count = 0;
      }
      byte0 = input[start++] & 0xff;
      byte1 = input[start++] & 0xff;
      byte2 = input[start++] & 0xff;
      out.write(b1[byte0]);
      out.write(b2[byte0][byte1]);
      out.write(b3[byte1][byte2]);
      out.write(b4[byte2]);
      count++;
    }
    switch (length - start) {
    case 2: {
      byte0 = input[start++] & 0xff;
      byte1 = input[start++] & 0xff;
      bufsize = 2;
      break;
    }
    case 1: {
      byte0 = input[start++] & 0xff;
      bufsize = 1;
      break;
    }
    default:
    }
  }

  public void write(byte input[]) throws IOException {
    write(input, 0, input.length);
  }

  public void write(int i) throws IOException {
    switch (bufsize) {
    case 1: {
      byte1 = i;
      bufsize = 2;
      break;
    }
    case 2: {
      byte2 = i;
      bufsize = 3;
      break;
    }
    case 3: {
      if (count == bytesPerLine) {
        out.write(13);
        out.write(10);
        count = 0;
      }
      out.write(b1[byte0]);
      out.write(b2[byte0][byte1]);
      out.write(b3[byte1][byte2]);
      out.write(b4[byte2]);
      count++;
    }
    // after 3 they are 0 - if 0 only write one
    case 0: {
      byte0 = i;
      bufsize = 1;
    }
    default:
    }
  }

  public void flush() throws IOException {
    // do nothing - write buffer only when ready
    out.flush();
  }

  public void close() throws IOException {
    if (count == bytesPerLine) {
      out.write(13);
      out.write(10);
    }

    switch (bufsize) {
    case 1: {
      out.write(b1[byte0]);
      out.write(b2[byte0][0]);
      out.write(61);
      out.write(61);
      break;
    }
    case 2: {
      out.write(b1[byte0]);
      out.write(b2[byte0][byte1]);
      out.write(b3[byte1][0]);
      out.write(61);
      break;
    }
    case 3: {
      out.write(b1[byte0]);
      out.write(b2[byte0][byte1]);
      out.write(b3[byte1][byte2]);
      out.write(b4[byte2]);
      break;
    }
    default:
    }

    out.close();
  }

  /**
   * Encodes a byte array into base64 encoded byte array without adding new
   * lines and white spaces
   * 
   * @param input
   * @return
   */
  public static byte[] encodeN(byte input[]) {
    if (input.length == 0)
      return input;
    int byte0, byte1, byte2;
    byte output[] = new byte[((input.length + 2) / 3) * 4];
    int i = 0;
    int j = 0;
    int k = input.length;
    for (; k > 2; k -= 3) {
      byte0 = input[i++] & 0xff;
      byte1 = input[i++] & 0xff;
      byte2 = input[i++] & 0xff;
      output[j++] = b1[byte0];
      output[j++] = b2[byte0][byte1];
      output[j++] = b3[byte1][byte2];
      output[j++] = b4[byte2];
    }
    if (k == 1) {
      byte0 = input[i++] & 0xff;
      output[j++] = b1[byte0];
      output[j++] = b2[byte0][0];
      output[j++] = 61;
      output[j++] = 61;
    } else if (k == 2) {
      byte0 = input[i++] & 0xff;
      byte1 = input[i++] & 0xff;
      output[j++] = b1[byte0];
      output[j++] = b2[byte0][byte1];
      output[j++] = b3[byte1][0];
      output[j++] = 61;
    }
    return output;
  }

  /**
   * Encodes a byte array into base64 encoded byte array, while setting new
   * lines after every line_length symbols
   * 
   * @param input
   * @param line_length
   * @return
   */
  public static byte[] encode(byte input[], int line_length) {

    if (input.length == 0)
      return input;

    int byte0, byte1, byte2;
    int i = ((input.length +2) / 3) * 4;
    byte output[] = new byte[i + ((line_length == 0) ? 0 : i / line_length * 2)];
//    byte output[] = new byte[i];
    i = 0;
    int j = 0;
    line_length /= 4;
    int index = 0;
    int k = input.length;
    for (; k > 2; k -= 3) {
      byte0 = input[i++] & 0xff;
      byte1 = input[i++] & 0xff;
      byte2 = input[i++] & 0xff;
      output[j++] = b1[byte0];
      output[j++] = b2[byte0][byte1];
      output[j++] = b3[byte1][byte2];
      output[j++] = b4[byte2];

      index++;
      if (index == line_length) {
        output[j++] = 13;
        output[j++] = 10;
        index = 0;
      }
    }
    if (k == 1) {
      byte0 = input[i++] & 0xff;
      output[j++] = b1[byte0];
      output[j++] = b2[byte0][0];
      output[j++] = 61;
      output[j++] = 61;
      index++;
      if (index == line_length) {
        output[j++] = 13;
        output[j++] = 10;
        index = 0;
      }
    } else if (k == 2) {
      byte0 = input[i++] & 0xff;
      byte1 = input[i++] & 0xff;
      output[j++] = b1[byte0];
      output[j++] = b2[byte0][byte1];
      output[j++] = b3[byte1][0];
      output[j++] = 61;
      index++;
      if (index == line_length) {
        output[j++] = 13;
        output[j++] = 10;
        index = 0;
      }
    }
    return output;
  }

  /**
   * Encodes a byte array into base64 encoded byte array, new line after 80
   * symbols
   * 
   * @param input
   * @return
   */
  public static byte[] encode(byte input[]) {
    return skipNewLines?encodeN(input):encode(input, 80);
  }

  public static boolean isSkipNewLines() {
    return skipNewLines;
  }

  public static void setSkipNewLines(boolean skipNewLines) {
    BASE64Encoder.skipNewLines = skipNewLines;
  }

  // public static void main(String[] args) {
  // byte b = (byte) 129;
  // int i = b & 0xff;
  // LogWriter.getSystemLogWriter().println(i);
  //
  // String original =
  // "AAAAAAAAdasdafasfhdakscxjnzxkfhkwesthwjrgwhjgdcbhjAAAAAAAAAAAAAAAAA";
  // byte[] ex = original.getBytes();
  // int len = 8;
  // String transformed = new String(encodeN(ex));
  // LogWriter.getSystemLogWriter().println(transformed);
  // byte[] back = BASE64Decoder.decodeN(transformed.getBytes());
  // LogWriter.getSystemLogWriter().println(Arrays.equals(back, ex));
  // }
  
  
//  public static void main(String[] abs){
//   byte[] b = new byte[]{ 60, 63, 120, 109, 108, 32, 118, 101, 114, 115, 105, 111, 110, 61, 34, 49, 46, 48, 34, 32, 101, 110, 99, 111, 100, 105, 110, 103, 61, 34, 85, 84, 70, 45, 56, 34, 63, 62, 10, 60, 110, 115, 49, 58, 118, 97, 108, 117, 101, 32, 120, 109, 108, 110, 115, 58, 110, 115, 49, 61, 39, 104, 116, 116, 112, 58, 47, 47, 119, 119, 119, 46, 115, 97, 112, 46, 99, 111, 109, 47, 115, 99, 104, 101, 109, 97, 47, 110, 119, 97, 47, 100, 97, 116, 97, 39, 62, 10, 32, 32, 60, 110, 115, 49, 58, 115, 116, 114, 105, 110, 103, 62, 68, 111, 99, 117, 109, 101, 110, 116, 32, 83, 101, 114, 118, 105, 99, 101, 115, 32, 76, 105, 99, 101, 110, 115, 101, 32, 83, 117, 112, 112, 111, 114, 116, 32, 83, 101, 114, 118, 105, 99, 101, 60, 47, 110, 115, 49, 58, 115, 116, 114, 105, 110, 103, 62, 10, 60, 47, 110, 115, 49, 58, 118, 97, 108, 117, 101, 62};
//   byte[] res = encode(b);
//   LogWriter.getSystemLogWriter().print(Arrays.toString(res));
//  }
}
