/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient.utils;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 *  Encodes/decodes data using Base64 format
 *
 * @author Stephan Zlatarev
 */
public class Base64 {

  /**
   *  The character set used is composed by 'A'-'Z','a'-'z','0'-'9','+','/'. This is the standard Base64 encoding.
   */
  public final static byte[] VARIANT_STANDARD = variant('+', '/');

  /**
   *  The character set used is composed by 'A'-'Z','a'-'z','0'-'9','-','_'. This modified Base64 encoding is compatible with URL parsing.
   * See RFC 3548.
   */
  public final static byte[] VARIANT_URL = variant('-', '_');

  /**
   *  The character set used is composed by 'A'-'Z','a'-'z','0'-'9','!','-'. This modified Base64 encoding is compatible with regular expressions.
   */
  public final static byte[] VARIANT_REGEXPS = variant('!', '-');

  /**
   *  The character set used is composed by 'A'-'Z','a'-'z','0'-'9','_',':'. This modified Base64 encoding is compatible with XML documents.
   */
  public final static byte[] VARIANT_XML = variant('_', ':');

  // used constants
  private final static int CHARS_PER_LINE = 64;
  private final static byte MASK = 0x3f;
  private final static int MASK_ONES = 0xff;
  private final static byte PADDING = (byte) '=';

  /**
   *  Decodes the BASE64 input.
   *
   * @param input  the array to be decoded.
	 * @return the decoded array
	 * @throws Exception if any problem occures
   */
  public static byte[] decode(byte[] input) throws Exception {
    return decodeEncodedFile(readEncodedFile(input, VARIANT_STANDARD), VARIANT_STANDARD);
  }

  /**
   *  Decodes the BASE64 input.
   *
   * @param input  the array to be decoded.
   * @param charset  one of Base64.VARIANT_STANDARD, Base64.VARIANT_URL, Base64.VARIANT_XML or Base64.VARIANT_REGEXPS.
	 * @return the decoded array
	 * @throws Exception if any problem accures
   */
  public static byte[] decode(byte[] input, byte[] charset) throws Exception {
    return decodeEncodedFile(readEncodedFile(input, charset), charset);
  }

  /**
   *  Decodes the BASE64 input.
   *
   * @param stream  the array to be decoded.
	 * @return the decoded array
	 * @throws Exception if any problem accures
   */
  public static byte[] decode(InputStream stream) throws Exception {
    return decodeEncodedFile(readEncodedFile(stream, VARIANT_STANDARD), VARIANT_STANDARD);
  }

  /**
   *  Decodes the BASE64 input.
   *
   * @param stream  the array to be decoded.
   * @param charset  one of Base64.VARIANT_STANDARD, Base64.VARIANT_URL, Base64.VARIANT_XML or Base64.VARIANT_REGEXPS.
	 * @return the decoded array
	 * @throws Exception if any problem accures
   */
  public static byte[] decode(InputStream stream, byte[] charset) throws Exception {
    return decodeEncodedFile(readEncodedFile(stream, charset), charset);
  }

  /**
   *  Decodes the BASE64 input.
   *
   * @param input  the array to be decoded.
	 * @param charset the charset to use
	 * @return the decoded array
	 * @throws Exception if any problem accures
   */
  private static byte[] decodeEncodedFile(byte[] input, byte[] charset) throws Exception {
    byte[] result;
    int i;
    int len;
    int atomLen;
    int padding = 0;
    len = input.length;
    atomLen = len / 4;

    if (charset == VARIANT_STANDARD) {
      if ((input.length % 4) != 0) {
        throw new Exception("Input array size must be multiple by 4.");
      }

      if (input[len - 1] == PADDING) {
        padding++; // 1 or 2 chars
      }

      if (input[len - 2] == PADDING) {
        padding++; // 1 char, otherwise - 2
      }

      if (input[len - 3] == PADDING) {
        throw new Exception("Invalid input data.");
      }
    } else {
      if ((input.length % 4) == 3) {
        padding = 1;
      }
      if ((input.length % 4) == 2) {
        padding = 2;
      }
      if ((input.length % 4) == 1) {
        padding = 2;
      }
      if ((input.length % 4) != 0) {
        len = ((input.length / 4) + 1) * 4;
      }
    }

    if (padding != 0) {
      result = new byte[(atomLen - ((charset == VARIANT_STANDARD) ? 1 : 0)) * 3 + (padding == 2 ? 1 : 2)];
    } else {
      result = new byte[atomLen * 3];
    }

    for (i = 0; i < (atomLen - ((charset == VARIANT_STANDARD) ? 1 : 0)); i++) {
      System.arraycopy(decodeAtom(input, i * 4, charset), 0, result, i * 3, 3);
    } 

    // if there are more symbols
    if (padding != 0) {
      int tmp = 0;
      tmp |= getIndex(input[len - 4], charset);

      if (padding == 2) {
        tmp <<= 2;
        tmp |= (getIndex(input[len - 3], charset) >>> 4);
      } else {
        /* 1 more byte do decode*/
        tmp <<= 6;
        tmp |= getIndex(input[len - 3], charset);
        tmp <<= 4;
        tmp |= (getIndex(input[len - 2], charset) >>> 2);
        result[result.length - 2] = (byte) ((tmp >> 8) & MASK_ONES);
      }

      result[result.length - 1] = (byte) (tmp & MASK_ONES);
    } else {
      System.arraycopy(decodeAtom(input, (atomLen - 1) * 4, charset), 0, result, (atomLen - 1) * 3, 3);
    }

    return result;
  }

  /**
   *  Encodes the input to BASE64
   *
   * @param input  the array to be encoded.
	 * @return the encoded array
	 * @throws Exception if any problem accures
   */
  public static byte[] encode(byte[] input) throws Exception {
    return encode(input, VARIANT_STANDARD);
  }

  /**
   *  Encodes the input to BASE64
   *
   * @param input  the array to be encoded.
   * @param charset  one of Base64.VARIANT_STANDARD, Base64.VARIANT_URL, Base64.VARIANT_XML or Base64.VARIANT_REGEXPS.
	 * @return the encoded array
	 * @throws Exception if any problem accures
   */
  public static byte[] encode(byte[] input, byte[] charset) throws Exception {
    int atomLen = input.length / 3;
    int blocks;
    int i;
    int off = (input.length / 3) * 3;
    int left = input.length - off;
    byte[] result;
    byte[] ret;
    byte[] atom = new byte[4];

    if (left == 0) {
      result = new byte[atomLen * 4];
    } else if (charset == VARIANT_STANDARD) {
      result = new byte[atomLen * 4 + 4];
    } else {
      result = new byte[atomLen * 4 + ((left == 1) ? 2 : 3)];
    }

    for (i = 0; i < atomLen; i++) {
      encodeAtom(input, i * 3, charset, atom);
      System.arraycopy(atom, 0, result, i * 4, 4);
    } 

    // if there are left some bytes
    if (left != 0) {
      if (left == 1) {
        result[i * 4] = charset[(input[off] >>> 2) & MASK];
        result[i * 4 + 1] = charset[(input[off] << 4) & 0x30];
        if (charset == VARIANT_STANDARD) {
          result[i * 4 + 2] = PADDING;
        }
      } else {
        result[i * 4] = charset[(input[off] >>> 2) & MASK];
        result[i * 4 + 1] = charset[((input[off] << 4) & 0x30) + ((input[off + 1] >>> 4) & 0x0f)];
        result[i * 4 + 2] = charset[((input[off + 1]) & 0x0f) << 2];
      }

      if (charset == VARIANT_STANDARD) {
        result[i * 4 + 3] = PADDING;
      }
    }

    if (charset == VARIANT_STANDARD) {
      blocks = result.length / CHARS_PER_LINE;
      left = result.length % CHARS_PER_LINE;
      ret = new byte[result.length + ((left == 0) ? blocks - 1 : blocks)];

      for (i = 0, off = 0; i < blocks; i++) {
        System.arraycopy(result, i * CHARS_PER_LINE, ret, off, CHARS_PER_LINE);
        off += CHARS_PER_LINE;

        if (!((i == (blocks - 1)) && (left == 0))) {
          ret[off++] = (byte) 10;
        }
      } 

      System.arraycopy(result, i * CHARS_PER_LINE, ret, off, result.length % CHARS_PER_LINE);
    } else {
      ret = result;
    }

    return ret;
  }

  //
  //  Encodes 3 bytes chunk from the specified offset in the input array
  //
  private static void encodeAtom(byte[] input, int offset, byte[] charset, byte[] result) throws Exception {
    if ((offset < 0) || (input.length <= offset)) {
      throw new Exception(" Invalid offset: " + offset);
    }

    int temp = input[offset] & MASK_ONES;
    temp <<= 8;
    temp |= input[offset + 1] & MASK_ONES;
    temp <<= 8;
    temp |= input[offset + 2] & MASK_ONES;
    result[0] = charset[(temp >> 18) & MASK];
    result[1] = charset[(temp >> 12) & MASK];
    result[2] = charset[(temp >> 6) & MASK];
    result[3] = charset[temp & MASK];
  }

  //
  //  Decodes 4 bytes chunk from the specified offset in the input array
  //
  private static byte[] decodeAtom(byte[] input, int offset, byte[] charset) throws Exception {
    if ((offset < 0) || (input.length <= offset)) {
      throw new Exception("decodeAtom exception: invalid offset " + offset);
    }

    int tmp = 0;
    byte[] result = new byte[3];

    for (int i = 0; i < 4; i++) {
      tmp <<= 6;
      tmp |= (MASK_ONES & getIndex(input[i + offset], charset));
    } 

    result[0] = (byte) ((tmp >> 16) & MASK_ONES);
    result[1] = (byte) ((tmp >> 8) & MASK_ONES);
    result[2] = (byte) (tmp & MASK_ONES);
    return result;
  }

  //
  // Returns the index of the character in the codes
  //   'A' - 0
  //   'a' - 26
  //   '0' - 52
  //   '+' - 62
  //   '/' - 63
  //   unknown - -1
   private static int getRawIndex(byte b, byte[] charset) {
     int result = ((int) b - (int) 'A');
     if (result >= 0 && result < 26) {
       return result;
     }
     result = ((int) b - (int) 'a');
     if (result >= 0 && result < 26) {
       return result + 26;
     }
     result = ((int) b - (int) '0');
     if (result >= 0 && result < 10) {
       return result + 52;
     }
     if (b == charset[62]) {
       return 62;
     }
     if (b == charset[63]) {
       return 63;
     }
     return -1;
   }

   private static int getIndex(byte b, byte[] charset) throws Exception {
     int result = getRawIndex(b, charset);
     if (b < 0) {
       throw new Exception(" Unknown symbol '" + b + "'.");
     }
     return result;
   }

   private static boolean isBase64(byte b, byte[] charset) {
     return (getRawIndex(b, charset) >= 0);
   }

  /**
   *  Returns if the data in the InputStream is Base64 formatted with the give starting string
   *
   * @param input  the input stream;
   * @param start  the string that the data should start with
   *
   * @return true if this is Base64 with the given starting.
	 * @throws Exception if any problem occures
   */
  @SuppressWarnings({"ResultOfMethodCallIgnored"})
	public static boolean isBase64(InputStream input, String start) throws Exception {
    byte[] temp;

    if (!input.markSupported()) {
      // convert input to InputStream that supports mark
      temp = new byte[input.available()];
      input.read(temp);
      input = new ByteArrayInputStream(temp);
    }

    if (input.available() <= 10) {
      throw new Exception("Unknown encoding.");
    }

    temp = new byte[10];
    input.mark(10);
    input.read(temp);
    input.reset();
    return start.startsWith(new String(temp));
  }

  //
  //  Reads the available data from the given input stream ignoring
  // unrecognized symbols
  //
  private static byte[] readEncodedFile(byte[] input, byte[] charset) {
    try {
      return readEncodedFile(new ByteArrayInputStream(input), charset);
    } catch (IOException ioe) {
      return null;
    }
  }

  //
  //  Reads the available data from the given input stream ignoring
  // unrecognized symbols
  //
  private static byte[] readEncodedFile(InputStream input, byte[] charset) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int skip_state = 0;
    byte a = 0x00;

    if (input.available() > 0) {
      a = (byte) input.read();

      if ((charset != VARIANT_STANDARD) || (a != '-')) {
        // this file does not contain header
        int count = input.available();
        baos.write(a);

        for (int i = 0; i < count; i++) {
          a = (byte) input.read();

          if (isBase64(a, charset) || (a == PADDING)
//              || ((charset == VARIANT_STANDARD) && (a == PADDING))
              ) {
            baos.write(a);
          }
        }

        return baos.toByteArray();
      }
    }

    while (input.available() > 0) {
      switch (skip_state) {
        case 0: {
          // this is the begining of the file. we expect '-' sequence
          if (a == '-') {
            skip_state++;
          }

          break;
        }
        case 1: {
          // this is the first '-' sequence in header.
          if (a != '-') {
            skip_state++;
          }

          break;
        }
        case 2: {
          // this is the header name. skip until '-' sequence
          if (a == '-') {
            skip_state++;
          }

          break;
        }
        case 3: {
          // check if this is a '-' sequence, or just one '-'
          if (a == '-') {
            skip_state++;
          } else {
            skip_state--;
          }

          break;
        }
        case 4: {
          // this is the second '-' sequence in header.
          if (a != '-') {
            skip_state++;
          }

          break;
        }
        case 5: {
          // this is the content part
          if (isBase64(a, charset) || (a == PADDING)) {
            baos.write(a);
          } else if (a == '-') {
            skip_state++;
          }

          break;
        }
        case 6: {
          // check if this is a '-' sequence, or just one '-'
          if (a == '-') {
            skip_state++;
          } else {
            skip_state--;
          }

          break;
        }
        case 7: {
          // this is the first '-' sequence in header closure.
          if (a != '-') {
            skip_state++;
          }

          break;
        }
        case 8: {
          // this is the header name. skip until '-' sequence
          if (a == '-') {
            skip_state++;
          }

          break;
        }
        case 9: {
          // check if this is a '-' sequence, or just one '-'
          if (a == '-') {
            skip_state++;
          } else {
            skip_state--;
          }

          break;
        }
        case 10: {
          // this is the second '-' sequence in header closure.
          if (a != '-') {
            skip_state++;
          }

          break;
        }
        case 11: {
          // file is over everything's ok.
          break;
        }
        default: {
          // what's going on??
        }
      }

      a = (byte) input.read();
    }

    return baos.toByteArray();
  }

  private static byte[] variant(char plus, char dash) {
    byte[] result = charArrToByteArr(new char[] {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'});
    result[62] = charToAscii(plus);
    result[63] = charToAscii(dash);
    return result;
  }
  /**
   *  Returns the converted ASCII version of the given character table
   *
   * @param array  the character table
	 * @return the byte array
   */
  static byte[] charArrToByteArr(char[] array) {
    byte[] result = new byte[array.length];

    for (int i = 0; i < array.length; i++) {
      result[i] = charToAscii(array[i]);
    } 

    return result;
  }

  /**
   * Char to ASCII
   *
   * @param   c the char to convert
   * @return char from ASCII
   */
  private static byte charToAscii(char c) {
    String temp = String.valueOf(c);
    try {
      byte[] arr = temp.getBytes("ISO-8859-1");
      return arr[0];
    } catch (java.io.UnsupportedEncodingException e) {
      throw new RuntimeException(e.getMessage(), e.getCause());
    }
  }

}