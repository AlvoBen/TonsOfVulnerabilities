/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.lib.util;

/*
 * MessageBytes is used to present String as byte[]. There is many methods,
 * same as java.lang.String methods.
 *
 * @author Galin Galchev
 * @version 4.0
 */

import com.sap.engine.services.httpserver.lib.exceptions.HttpIllegalArgumentException;
import com.sap.engine.services.httpserver.server.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class MessageBytes extends Ascii {
  private byte[] bytes = null;
  private int offset = -1;
  private int length = -1;
  private static final String UTF8 = "UTF-8";

  /**
   * Default constructor
   *
   */
  public MessageBytes() {
    bytes = new byte[0];
    offset = 0;
    length = 0;
  }

  /**
   * Constructs new MessageBytes object with given byte[]
   *
   * @param   byteArray  byte[]
   */
  public MessageBytes(byte[] byteArray) {
    if (byteArray == null) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"MessageBytes", "" + byteArray});
    }
    setBytes(byteArray, 0, byteArray.length);
  }

  /**
   * Constructs new MessageBytes object with given byte[] , offset and length
   *
   * @param   byteArray byte[]
   * @param   i  offset of given byte[] to get
   * @param   j  number of bytes to get
   */
  public MessageBytes(byte[] byteArray, int i, int j) {
    if (byteArray == null) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"MessageBytes", "" + byteArray});
    }
    setBytes(byteArray, i, j);
  }

  /**
   * Resets MessageBytes
   *
   */
  public void reset() {
    offset = 0;
    length = 0;
  }

  /**
   * Sets MessageBytes object with given byte[] , offset and length
   *
   * @param   byteArray byte[]
   * @param   i  offset of given byte[] to get
   * @param   j  number of bytes to get
   */
  public void setBytes(byte[] byteArray, int i, int j) {
    if (byteArray == null) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"setBytes", "" + byteArray});
    }
    if (i < 0 || j < 0 || i + j > byteArray.length) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"setBytes", "" + i, "" + j});
    }
    bytes = byteArray;
    offset = i;
    length = j;
  }

  public void setBytes(byte[] byteArray) {
    setBytes(byteArray, 0, byteArray.length);
  }

  public void incOffset(int inc) {
    if (inc < 0 || inc > length) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"setLength", "" + length});
    }
    offset += inc;
    length -= inc;
  }

  public void setLength(int length) {
    if (length < 0 || length > this.length) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"setLength", "" + length});
    }
    this.length = length;
  }

  /**
   * Returns byte[] presentation of MessageBytes
   *
   * @return  byte[] presentation of MessageBytes
   */
  public byte[] getBytes() {
    byte[] res = new byte[length];
    System.arraycopy(bytes, offset, res, 0, length);
    return res;
  }

  /**
   *
   *
   * @param   toByteArr
   * @param   off
   * @return
   */
  public int getBytes(byte toByteArr[], int off) {
    if (toByteArr == null || toByteArr.length < off) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"getBytes", "" + toByteArr + ", " + off});
    }
    if (toByteArr.length - off < length) {
      System.arraycopy(bytes, offset, toByteArr, off, toByteArr.length - off);
      return toByteArr.length - off;
    } else {
      System.arraycopy(bytes, offset, toByteArr, off, length);
      return length;
    }
  }

  /**
   * Returns byte[] presentation of area of MessageBytes ,which is
   * determinated with given offset and length
   *
   * @param   off  offset
   * @param   len  number of bytes to get
   * @return  byte[] presentation of this area of MessageBytes
   */
  public byte[] getBytes(int off, int len) {
    if (off < 0 || len < 0 || off + len > length) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"getBytes", "" + off + ", " + len});
    }
    byte[] res = new byte[len];
    System.arraycopy(bytes, offset + off, res, 0, len);
    return res;
  }

  /**
   * Returns byte[] presentation of area of MessageBytes ,which is
   * determinated with given offset till end
   *
   * @param   off  offset
   * @return  byte[] presentation of this area of MessageBytes
   *

   */
  public byte[] getBytes(int off) {
    if (off < 0 || off > length) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"getBytes", "" + off});
    }
    byte[] res = new byte[length - off];
    System.arraycopy(bytes, offset + off, res, 0, res.length);
    return res;
  }

  /**
   * Appends given byte[] at the end
   *
   * @param   newBytes  byte[] to append
   */
  public void appendAfter(byte[] newBytes) {
    if (newBytes == null) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"appendAfter", "" + newBytes});
    }
    byte[] newByteArr = new byte[length + newBytes.length];
    System.arraycopy(bytes, offset, newByteArr, 0, length);
    System.arraycopy(newBytes, 0, newByteArr, length, newBytes.length);
    bytes = newByteArr;
    offset = 0;
    length = newByteArr.length;
  }

  /**
   * Appends given byte[] at the begin
   *
   * @param   newBytes  byte[] to append
   */
  public void appendBefore(byte[] newBytes) {
    if (newBytes == null) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"appendBefore", "" + newBytes});
    }
    byte[] newByteArr = new byte[length + newBytes.length];
    System.arraycopy(newBytes, 0, newByteArr, 0, newBytes.length);
    System.arraycopy(bytes, offset, newByteArr, newBytes.length, length);
    bytes = newByteArr;
    offset = 0;
    length = newByteArr.length;
  }

  public void deleteByteAt(int off) {
    if (off < 0 || off > length) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"deleteByteAt", "" + off});
    }
    for (int i = offset + off + 1; i < length; i++) {
      bytes[i - 1] = bytes[i];
    }
    length--;
  }

  /**
   * Appends first byte at the begin and second to the end
   *
   * @param   b1  byte to append at the begin
   * @param   b2  byte to append at the end
   * @return  byte[] presentation of new MessageBytes
   */
  public byte[] addByteAtBeginAndEnd(byte b1, byte b2) {
    byte[] newByteArr = new byte[length + 2];
    newByteArr[0] = b1;
    System.arraycopy(bytes, offset, newByteArr, 1, length);
    newByteArr[length + 1] = b2;
    return newByteArr;
  }

  /**
   * String presentation of this MessageBytes
   *
   * @return String presentation of this MessageBytes
   */
  public String toString() {
    char[] res = new char[length];
    for (int i = 0; i < length; i++) {
      res[i] = (char) (bytes[offset + i] & 0x00ff);
    }
    return new String(res);
  }

  public String toStringUTF8() {
    try {
      return new String(bytes, offset, length, UTF8);
    } catch (UnsupportedEncodingException e) {
      char[] res = new char[length];
      for (int i = 0; i < length; i++) {
        res[i] = (char)(bytes[offset + i] & 0x00ff);
      }
      return new String(res);
    }
  }

  public String toStringWithoutException(String encoding) {
    try {
      return new String(bytes, offset, length, encoding);
    } catch (UnsupportedEncodingException e) {
      char[] res = new char[length];
      for (int i = 0; i < length; i++) {
        res[i] = (char)(bytes[offset + i] & 0x00ff);
      }
      return new String(res);
    }
  }

  public String toString(String encoding) throws UnsupportedEncodingException {
    return new String(bytes, offset, length, encoding);
  }

  public String toString(int off) {
    if (off < 0 || off > length) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"toString", "" + off});
    }
    char[] res = new char[length - off];
    for (int i = 0; i < length - off; i++) {
      res[i] = (char) (bytes[offset + off + i] & 0x00ff);
    }
    return new String(res);
  }

  public String toStringUTF8(int off) {
    if (off < 0 || off > length) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"toStringUTF8", "" + off});
    }
    try {
      return new String(bytes, offset + off, length - off, UTF8);
    } catch (UnsupportedEncodingException e) {
      char[] res = new char[length - off];
      for (int i = 0; i < length - off; i++) {
        res[i] = (char) (bytes[offset + off + i] & 0x00ff);
      }
      return new String(res);
    }
  }

  /**
   * int presentation of this MessageBytes
   *
   * @return  int presentation of this MessageBytes
   * @exception   NumberFormatException
   */
  public int toInteger() throws NumberFormatException {
    return Ascii.asciiArrToInt(bytes, offset, length);
  }

  /**
   * Compare this MessageBytes to given String, case insensitive
   *
   * @param   s  String to compare
   * @return  true if equals, false if not
   */
  public boolean equalsIgnoreCase(String s) {
    if (s == null || length != s.length()) {
      return false;
    }
    for (int k = 0; k < length; k++) {
      if (Ascii.toLower(bytes[offset + k]) != Ascii.toLower((byte) s.charAt(k))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Compare this MessageBytes to given byte[]
   *
   * @param   byteArr  byte[] to compare
   * @return  true if equals, false if not
   */
  public boolean equals(byte byteArr[]) {
    if (byteArr == null || length != byteArr.length) {
      return false;
    }
    for (int k = 0; k < length; k++) {
      if (bytes[offset + k] != byteArr[k]) {
        return false;
      }
    }
    return true;
  }

  /**
   * Compare this MessageBytes to area of given byte[], case insensitive
   *
   * @param   byteArr  byte[] to compare
   * @param   off  offset
   * @param   len  length
   * @return  true if equals, false if not
   */
  public boolean equalsIgnoreCase(byte byteArr[], int off, int len) {
    if (off < 0 || len < 0 || byteArr == null || off + len > byteArr.length) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"equalsIgnoreCase", "" + byteArr + ", " + off + ", " + len});
    }
    if (length != len) {
      return false;
    }
    for (int k = 0; k < length; k++) {
      if (Ascii.toLower(bytes[offset + k]) != Ascii.toLower(byteArr[off + k])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Tests if this MessageBytes starts with given String
   *
   * @param   s  String to test
   * @return  true if starts with given String, false if not
   */
  public boolean startsWith(String s) {
    if (s == null) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"startsWith", "" + s});
    }
    int sLen = s.length();
    if (sLen > length) {
      return false;
    }
    for (int k = 0; k < sLen; k++) {
      if (bytes[offset + k] != s.charAt(k)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Tests if this MessageBytes starts with given MessageBytes
   *
   * @param   s  MessageBytes to test
   * @return  true if starts with given MessageBytes, false if not
   */
  public boolean startsWith(MessageBytes s) {
    if (s == null) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"startsWith", "" + s});
    }
    int sLen = s.length();
    if (sLen > length) {
      return false;
    }
    for (int k = 0; k < sLen; k++) {
      if (bytes[offset + k] != s.charAt(k)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Tests if this MessageBytes starts with given String, case insensitive
   *
   * @param   s  String to test
   * @return  true if starts with given String, false if not
   */
  public boolean startsWithIgnoreCase(String s) {
    if (s == null) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"startsWithIgnoreCase", "" + s});
    }
    int sLen = s.length();
    if (sLen > length) {
      return false;
    }
    for (int k = 0; k < sLen; k++) {
      if (Ascii.toLower(bytes[offset + k]) != Ascii.toLower(s.charAt(k))) {
        return false;
      }
    }
    return true;
  }

  public boolean startsWithIgnoreCase(byte[] s) {
    if (s == null) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"startsWithIgnoreCase", "" + s});
    }
    if (s.length > length) {
      return false;
    }
    for (int k = 0; k < s.length; k++) {
      if (Ascii.toLower(bytes[offset + k]) != Ascii.toLower(s[k])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns the index within this MessageBytes
   * of the first occurrence of the specified String.
   *
   * @param   s  String to search
   * @return  the index within this MessageBytes  of the first occurrence of the String
   */
  public int indexOf(String s) {
    if (s == null) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"indexOf", "" + s});
    }
    for (int k = offset; k < length + offset; k++) {
      if (s.charAt(0) == bytes[k]) {
        int j = 1;
        for (; j < s.length() && (j + k) < length; j++) {
          if (s.charAt(j) != bytes[k + j]) {
            break;
          }
        }
        if (j == s.length()) {
          return k - offset;
        }
      }
    }
    return -1;
  }

  public int indexOf(String s, int fromIndex) {
    if (s == null || fromIndex > length) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"indexOf", "" + s + ", " + fromIndex});
    }
    for (int k = fromIndex + offset; k < length + offset; k++) {
      if (s.charAt(0) == bytes[k]) {
        int j = 1;
        for (; j < s.length() && (j + k) < length; j++) {
          if (s.charAt(j) != bytes[k + j]) {
            break;
          }
        }
        if (j == s.length()) {
          return k - offset;
        }
      }
    }
    return -1;
  }

  /**
   * Returns the index within this MessageBytes
   * of the first occurrence of the specified byte[].
   *
   * @param   sbyte  byte[] to search
   * @return  the index within this MessageBytes  of the first occurrence of the byte[]
   */
  public int indexOf(byte[] sbyte) {
    if (sbyte == null) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"indexOf", "" + sbyte});
    }
    for (int k = offset; k < length + offset; k++) {
      if (sbyte[0] == bytes[k]) {
        int j = 1;
        for (; j < sbyte.length && (j + k) < length; j++) {
          if (sbyte[j] != bytes[k + j]) {
            break;
          }
        }
        if (j == sbyte.length) {
          return k - offset;
        }
      }
    }
    return -1;
  }

  /**
   * Returns the index within this MessageBytes
   * of the first occurrence of the specified char.
   *
   * @param   ch  char to search
   * @return  the index within this MessageBytes  of the first occurrence of the char
   */
  public int indexOf(char ch) {
    for (int k = offset; k < length + offset; k++) {
      if (bytes[k] == ch) {
        return k - offset;
      }
    }
    return -1;
  }

  /**
   * Returns the index within this MessageBytes
   * of the first occurrence of the specified char after given position.
   *
   * @param   ch  char to search
   * @param   off  begining position
   * @return  the index within this MessageBytes  of the first occurrence of the char
   */
  public int indexOf(char ch, int off) {
    if (off > length) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"indexOf", "" + ch + ", " + off});
    }
    for (int k = off + offset; k < length + offset; k++) {
      if (bytes[k] == ch) {
        return k - offset;
      }
    }
    return -1;
  }

  /**
   * Returns the index within this MessageBytes
   * of the last occurrence of the specified char
   *
   * @param   ch  char to search
   * @return  the index within this MessageBytes  of the first occurrence of the char
   */
  public int lastIndexOf(char ch) {
    for (int k = length + offset - 1; k >= offset; k--) {
      if (bytes[k] == ch) {
        return k - offset;
      }
    }
    return -1;
  }

  /**
   * Returns the index within this MessageBytes
   * of the last occurrence of the specified byte.
   *
   * @param   b  byte to search
   * @return  the index within this MessageBytes  of the last occurrence of the byte
   */
  public int lastIndexOf(byte b) {
    for (int k = length + offset - 1; k >= offset; k--) {
      if (bytes[k] == b) {
        return k - offset;
      }
    }
    return -1;
  }

  /**
   * Returns the character at the specified index
   *
   * @param   i  index
   * @return  the character at the specified index
   */
  public char charAt(int i) {
    if (i >= length) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"charAt", "" + i});
    }
    return (char) bytes[offset + i];
  }

  /**
   * Writes this MessageBytes to given OutputStream
   *
   * @param   outputstream  OutputStream write to
   * @exception   IOException
   */
  public void write(OutputStream outputstream) throws IOException {
    outputstream.write(bytes, offset, length);
  }

  /**
   * Returns length of byte[] representing this MessageBytes
   *
   * @return length of byte[] representing this MessageBytes
   */
  public int length() {
    return length;
  }

  /**
   * Replaces all occurrences of first byte with second
   *
   * @param   first byte to replace
   * @param   second the new byte
   */
  public void replace(byte first, byte second) {
    for (int i = offset; i < length + offset; i++) {
      if (bytes[i] == first) {
        bytes[i] = second;
      }
    }
  }

  public void replace(byte first, byte second, int off, int len) {
    if (off < 0 || len < 0 || off + len > length) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"replace", "" + first + ", " + second + ", " + off + ", " + len});
    }
    for (int i = offset; i < len + offset; i++) {
      if (bytes[i] == first) {
        bytes[i] = second;
      }
    }
  }

  /**
   * Tests if this MessageBytes ends with given String
   *
   * @param   s  String to test
   * @return  true if ends with given String, false if not
   */
  public boolean endsWith(String s) {
    if (s == null) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"endsWith", "" + s});
    }
    int sLen = s.length();
    if (sLen > length) {
      return false;
    }
    int endOffset = offset + length - sLen;
    for (int k = 0; k < sLen; k++) {
      if (bytes[endOffset + k] != s.charAt(k)) {
        return false;
      }
    }
    return true;
  }

  public boolean endsWith(byte[] s) {
    if (s == null) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"endsWith", "" + s});
    }
    int sLen = s.length;
    if (sLen > length) {
      return false;
    }
    int endOffset = offset + length - sLen;
    for (int k = 0; k < sLen; k++) {
      if (bytes[endOffset + k] != s[k]) {
        return false;
      }
    }
    return true;
  }

  /**
   * Tests if this MessageBytes ends with given byte
   *
   * @param   b  byte to test
   * @return  true if ends with given byte, false if not
   */
  public boolean endsWith(byte b) {
    if (length == 0) {
      return false;
    }
    return bytes[offset + length - 1] == b;
  }

  /**
   * Tests if this MessageBytes ends with given byte array
   *
   * @param   s  String to test
   * @return  true if ends with given String, false if not
   */
  public boolean endsWithIgnoreCase(byte[] s) {
    if (s == null) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MESSAGEBYTES_ARGUMENTS,
              new Object[]{"endsWithIgnoreCase", "" + s});
    }
    int sLen = s.length;
    if (sLen > length) {
      return false;
    }
    int endOffset = offset + length - sLen;
    for (int k = 0; k < sLen; k++) {
      if (Ascii.toLower(bytes[endOffset + k]) != Ascii.toLower(s[k])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Overwrites standart method defined java.lang.Object.
   * This method is used when CacheQueueItemKey object is
   * inserted or searched in standart java.util.Hashtable
   * object
   *
   * @return hash code for this object
   */
  public int hashCode() {
    int hash = 0;
    if (bytes != null) {
      for (int i = offset; i < offset + length; i++) {
        hash += (hash << 13) + (bytes[i] & 0xff);
      }
    }
    return hash;
  }

  /**
   * Compare this MessageBytes to another
   *
   * @param   s  MessageBytes to compare
   * @return  true if equals, false if not
   */
  public boolean equals(MessageBytes s) {
    if (s == null || length != s.length()) {
      return false;
    }
    for (int k = 0; k < length; k++) {
      if (bytes[offset + k] != s.charAt(k)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Compare this MessageBytes to given String
   *
   * @param   s  String to compare
   * @return  true if equals, false if not
   */
  public boolean equals(String s) {
    if (s == null || length != s.length()) {
      return false;
    }
    for (int k = 0; k < length; k++) {
      if (bytes[offset + k] != s.charAt(k)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Overwrites standart method defined java.lang.Object.
   * This method is used when CacheQueueItemKey object is
   * inserted or searched in standart java.util.Hashtable
   * object
   *
   * @param   obj  the object to witch this object is
   *          compared
   * @return true if this object value is equal to the
   *         value of the spacified object
   */
  public boolean equals(Object obj) {
    if (obj instanceof MessageBytes) {
      return equals((MessageBytes) obj);
    } else if (obj instanceof String) {
      return equals((String) obj);
    } else {
      return false;
    }
  }
 
  /**
   * Replaces all occurences of CR AND LF with spaces. 
   * If combination of bytes forms "linear white space" it is not touched.
   * This method should be used only for header values.
   * HTTP 1.1:
    *    CRLF = CR LF 
    *    LWS = [CRLF] 1*( SP | HT )
   */
  public void filterValueForCRLF(){
    boolean found = false;
    for (int i = offset; i < length + offset; i++) {
      byte currByte = bytes[i];
      switch (currByte) {
        case 13: //CR
          if ((i + 2 < length + offset) &&                       // if we have at least 2 chars more
            (bytes[i + 1] == 10) &&                            //and next char is LF
            (bytes[i + 2] == 32 || bytes[i + 2] == 9)              //and next char is space or tab
            ) {                          // then this is linear white space
            continue;
          } else {
            bytes[i] = 32;              //replace with space
            found = true;
          }
          break;
        case 10: //LF
          if ((i != 0) &&                                       // if we have some characters before
            (i + 1 < length + offset && i - 1 >= offset) &&      // and at least 1 more
            (bytes[i - 1] == 13) &&                            // and the previous one was CR
            (bytes[i + 1] == 32 || bytes[i + 1] == 9)            // and next is space or tab
            ) {                            // then this is linear white space
            continue;
          } else {
            bytes[i] = 32;              //replace with space
            found = true;
          }
          break;
        default:
          continue;
      }
    }
   
    if ( found ) {
       Log.logWarning("ASJ.http.000002", 
         "CR or LF found in header for value [{0}].", new Object[]{ new String(bytes) }, null, null, null);
    }
  }
}

