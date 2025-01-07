/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient.uri;

import com.sap.httpclient.exception.URLDecodeException;
import com.sap.httpclient.exception.URLEncodeException;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;

/**
 * Used for URL encoding
 *
 * @author Nikolai Neichev
 */
public class URLCodec {

  /**
   * BitSet of www-form-url safe characters.
   */
  protected static final BitSet WWW_FORM_URL = new BitSet(256);

  /**
   * ASCII - ISO646-US, also known as the Basic Latin block of the Unicode character set. / 7-bit
   */
  protected static final String US_ASCII = "US-ASCII";

  /**
   * Unicode Transformation Format. / 8-bit
   */
  protected static final String UTF8 = "UTF-8";

  /**
   * The default charset used for string decoding and encoding.
   */
  protected String charset = UTF8;

  protected static byte ESCAPE_CHAR = '%';

  // Static initializer for www_form_url
  static {
    // alpha characters
    for (int i = 'a'; i <= 'z'; i++) {
      WWW_FORM_URL.set(i);
    }
    for (int i = 'A'; i <= 'Z'; i++) {
      WWW_FORM_URL.set(i);
    }
    // numeric characters
    for (int i = '0'; i <= '9'; i++) {
      WWW_FORM_URL.set(i);
    }
    // special chars
    WWW_FORM_URL.set('-');
    WWW_FORM_URL.set('_');
    WWW_FORM_URL.set('.');
    WWW_FORM_URL.set('*');
    // blank to be replaced with +
    WWW_FORM_URL.set(' ');
  }

  /**
   * Default constructor.
   */
  public URLCodec() {
    super();
  }

  /**
   * Constructor which allows for the selection of a default charset
   *
   * @param charset the default string charset to use.
   */
  public URLCodec(String charset) {
    super();
    this.charset = charset;
  }

  /**
   * Encodes an array of bytes into an array of URL safe 7-bit characters.
   *
   * @param urlsafe bitset of characters deemed URL safe
   * @param bytes   array of bytes to convert to URL safe characters
   * @return array of bytes containing URL safe characters
   */
  public static byte[] encodeUrl(BitSet urlsafe, byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    if (urlsafe == null) {
      urlsafe = WWW_FORM_URL;
    }
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    for (int b : bytes) {
      if (b < 0) {
        b = 256 + b;
      }
      if (urlsafe.get(b)) { // is safe
        if (b == ' ') {
          b = '+';
        }
        buffer.write(b);
      } else { // is not safe character, replace with %HexHex
        buffer.write('%');
        char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
        char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
        buffer.write(hex1);
        buffer.write(hex2);
      }
    }
    return buffer.toByteArray();
  }

  /**
   * Decodes an array of URL safe 7-bit characters into an array of original bytes.
   *
   * @param bytes array of URL safe characters
   * @return array of original bytes
   * @throws URLDecodeException if URL decoding fails
   */
  public static byte[] decodeUrl(byte[] bytes) throws URLDecodeException {
    if (bytes == null) {
      return null;
    }
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    for (int i = 0; i < bytes.length; i++) {
      int b = bytes[i];
      if (b == '+') {
        buffer.write(' ');
      } else if (b == '%') {
        try {
          int u = Character.digit((char) bytes[++i], 16);
          int l = Character.digit((char) bytes[++i], 16);
          if (u == -1 || l == -1) {
            throw new URLDecodeException("Invalid URL encoding");
          }
          buffer.write((char) ((u << 4) + l));
        } catch (ArrayIndexOutOfBoundsException e) {
          throw new URLDecodeException("Invalid URL encoding");
        }
      } else {
        buffer.write(b);
      }
    }
    return buffer.toByteArray();
  }

  /**
   * Encodes an array of bytes into an array of URL safe 7-bit characters.
   *
   * @param bytes array of bytes to convert to URL safe characters
   * @return array of bytes containing URL safe characters
   */
  public byte[] encode(byte[] bytes) {
    return encodeUrl(WWW_FORM_URL, bytes);
  }

  /**
   * Decodes an array of URL safe 7-bit characters into an array of original bytes.
   *
   * @param bytes array of URL safe characters
   * @return array of original bytes
   * @throws URLDecodeException if URL decoding fails
   */
  public byte[] decode(byte[] bytes) throws URLDecodeException {
    return decodeUrl(bytes);
  }

  /**
   * Encodes a string into its URL safe form using the specified string charset.
   *
   * @param pString string to convert to a URL safe form
   * @param charset the charset for pString
   * @return URL safe string
   * @throws UnsupportedEncodingException if charset is not supported
   */
  public String encode(String pString, String charset) throws UnsupportedEncodingException {
    if (pString == null) {
      return null;
    }
    return new String(encode(pString.getBytes(charset)), US_ASCII);
  }

  /**
   * Encodes a string into its URL safe form using the default string charset.
   *
   * @param pString string to convert to a URL safe form
   * @return URL safe string
   * @throws URLEncodeException if URL encoding fails
   */
  public String encode(String pString) throws URLEncodeException {
    if (pString == null) {
      return null;
    }
    try {
      return encode(pString, getDefaultCharset());
    } catch (UnsupportedEncodingException e) {
      throw new URLEncodeException(e.getMessage());
    }
  }

  /**
   * Decodes a URL safe string into its original form using the specified charset.
   *
   * @param str URL safe string to convert into its original form
   * @param charset the original string charset
   * @return original string
   * @throws URLDecodeException if URL decoding fails
   * @throws UnsupportedEncodingException if charset is not supported
   */
  public String decode(String str, String charset) throws URLDecodeException, UnsupportedEncodingException {
    if (str == null) {
      return null;
    }
    return new String(decode(str.getBytes(US_ASCII)), charset);
  }

  /**
   * Decodes a URL safe string into its original form using the default string charset.
   *
   * @param str URL safe string to convert into its original form
   * @return original string
   * @throws URLDecodeException if URL decoding fails
   */
  public String decode(String str) throws URLDecodeException {
    if (str == null) {
      return null;
    }
    try {
      return decode(str, getDefaultCharset());
    } catch (UnsupportedEncodingException e) {
      throw new URLDecodeException(e.getMessage());
    }
  }

  /**
   * Encodes an object into its URL safe form.
   *
   * @param obj string to convert to a URL safe form
   * @return URL safe object
   * @throws URLEncodeException if URL encoding fails
   */
  public Object encode(Object obj) throws URLEncodeException {
    if (obj == null) {
      return null;
    } else if (obj instanceof byte[]) {
      return encode((byte[]) obj);
    } else if (obj instanceof String) {
      return encode((String) obj);
    } else {
      throw new URLEncodeException("Object : " + obj.getClass().getName() + " cannot be URL encoded");
    }
  }

  /**
   * Decodes a URL safe object into its original form.
   *
   * @param obj URL safe object to convert into its original form
   * @return original object
   * @throws URLDecodeException if URL decoding fails
   */
  public Object decode(Object obj) throws URLDecodeException {
    if (obj == null) {
      return null;
    } else if (obj instanceof byte[]) {
      return decode((byte[]) obj);
    } else if (obj instanceof String) {
      return decode((String) obj);
    } else {
      throw new URLDecodeException("Object : " + obj.getClass().getName() + " cannot be URL decoded");
    }
  }

  /**
   * The default charset used for string decoding and encoding.
   *
   * @return the default string charset.
   */
  public String getDefaultCharset() {
    return this.charset;
  }

}