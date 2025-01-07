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

import com.sap.httpclient.NameValuePair;
import com.sap.httpclient.utils.Base64;
import com.sap.httpclient.exception.HttpClientError;
import com.sap.httpclient.exception.URLEncodeException;
import com.sap.tc.logging.Location;

import java.io.UnsupportedEncodingException;

/**
 * Used for encoding and decoding
 *
 * @author Nikolai Neichev
 */
public class EncodingUtil {

  /**
   * Default content encoding chatset
   */
  private static final String DEFAULT_CHARSET = "ISO-8859-1";

  /**
   * Log object for this class.
   */
  private static final Location LOG = Location.getLocation(EncodingUtil.class);

  /**
   * Processes url encoding over the specified name/value pair, using the specified charset.
   *
   * @param pairs   the values to be encoded
   * @param charset the character set to be used
   * @return the url encoded pairs as String
   */
  public static String getURLEncodedString(NameValuePair[] pairs, String charset) {
    try {
      return encodeURL(pairs, charset);
    } catch (URLEncodeException e) {
      LOG.errorT("Encoding not supported: " + charset);
      try {
        return encodeURL(pairs, DEFAULT_CHARSET);
      } catch (URLEncodeException fatal) { // $JL-EXC$
        throw new HttpClientError("Default encoding not supported: " + DEFAULT_CHARSET);
      }
    }
  }

  /**
   * Processes the url encoding.
	 * @param pairs the name value pairs to encode
	 * @param charset the charset to use
	 * @return the encoded URL
	 * @throws com.sap.httpclient.exception.URLEncodeException if an exception occures
	 */
  private static String encodeURL(NameValuePair[] pairs, String charset) throws URLEncodeException {
    StringBuilder buf = new StringBuilder();
    URLCodec codec = new URLCodec(charset);
    for (int i = 0; i < pairs.length; i++) {
      NameValuePair pair = pairs[i];
      if (pair.getName() != null) {
        if (i > 0) {
          buf.append("&");
        }
        buf.append(codec.encode(pair.getName()));
        buf.append("=");
        if (pair.getValue() != null) {
          buf.append(codec.encode(pair.getValue()));
        }
      }
    }
    return buf.toString();
  }

  /**
   * Converts the byte sub array to a string, using the specified charset.
   *
   * @param data    the byte array to be encoded
   * @param offset  the index of the first byte to encode
   * @param length  the number of bytes to encode
   * @param charset the desired character encoding
   * @return The result of the conversion.
   */
  public static String getString(final byte[] data, int offset, int length, String charset) {
    if (data == null) {
      throw new IllegalArgumentException("Parameter is null");
    }
    if (charset == null || charset.length() == 0) {
      throw new IllegalArgumentException("charset is null or empty");
    }
    try {
      return new String(data, offset, length, charset);
    } catch (UnsupportedEncodingException e) { // $JL-EXC$
      if (LOG.beWarning()) {
        LOG.warningT("Unsupported encoding: " + charset + ". Will use system encoding");
      }
      return new String(data, offset, length);
    }
  }

  /**
   * Converts the byte array to a string, using the specified charset.
   *
   * @param data    the byte array to be encoded
   * @param charset the desired character encoding
   * @return The result of the conversion.
   */
  public static String getString(final byte[] data, String charset) {
    return getString(data, 0, data.length, charset);
  }

  /**
   * Converts the specified string to a byte array, using the specified charset
   *
   * @param data    the string to be encoded
   * @param charset the desired character encoding
   * @return The resulting byte array.
   */
  public static byte[] getBytes(final String data, String charset) {
    if (data == null) {
      throw new IllegalArgumentException("data is null");
    }
    if (charset == null || charset.length() == 0) {
      throw new IllegalArgumentException("charset is null or empty");
    }
    try {
      return data.getBytes(charset);
    } catch (UnsupportedEncodingException e) {
      if (LOG.beWarning()) {
        LOG.warningT("Unsupported encoding: " + charset + ". System encoding used.");
      }
      return data.getBytes();
    }
  }

  /**
   * Converts the specified string to byte array of ASCII characters.
   *
   * @param data the string to be encoded
   * @return The string as a byte array.
   */
  public static byte[] getASCIIBytes(final String data) {
    if (data == null) {
      throw new IllegalArgumentException("Parameter is null");
    }
    try {
      return data.getBytes("US-ASCII");
    } catch (UnsupportedEncodingException e) {
      throw new HttpClientError("HttpClient requires ASCII support");
    }
  }

  /**
   * Converts the byte sub array of ASCII characters to a string.
   *
   * @param data   the byte array to be encoded
   * @param offset the index of the first byte to encode
   * @param length the number of bytes to encode
   * @return The string representation of the byte array
   */
  public static String getASCIIString(final byte[] data, int offset, int length) {
    if (data == null) {
      throw new IllegalArgumentException("Parameter is null");
    }
    try {
      return new String(data, offset, length, "US-ASCII");
    } catch (UnsupportedEncodingException e) {
      throw new HttpClientError("HttpClient requires ASCII support");
    }
  }

  /**
   * Converts the byte array of ASCII characters to a string.
   *
   * @param data the byte array to be encoded
   * @return The string representation of the byte array
   */
  public static String getASCIIString(final byte[] data) {
    return getASCIIString(data, 0, data.length);
  }

  /**
   * Converts the byte array of ASCII characters to a string. This method is
   * to be used when decoding content of HTTP elements (such as response
   * headers)
   *
   * @param data the byte array to be encoded
   * @return The string representation of the byte array
   * @since 3.0
   */
  public static String getAsciiString(final byte[] data) {
    return getASCIIString(data, 0, data.length);
  }

  /**
   * Encodes the specified bytes to String, using Base64 encoding
   *
   * @param data the byte array
   * @return the Base64 encoded String
   */
  public static String getBase64EncodedString(final byte[] data) {
    if (data == null) {
      throw new IllegalArgumentException("Parameter is null");
    }
    try {
      String result = new String(Base64.encode(data));

      // remove the '\n' simbols, added by the Base64.encode()  TODO - make it better
      int nl = result.indexOf('\n');
      while (nl > -1) {
        result = result.substring(0, nl) + result.substring((nl + 1), result.length());
        nl = result.indexOf('\n');
      }
      return result;
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e.getCause());
    }
  }

  /**
   * Encodes the specified text in XML compatible format by replacing the unsafe characters
   * with predefined XML entities.
   * @param text the specified text
   * @return the XML safe text
   */
	public static String encodeToXml(String text) {
		if (text == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
    for (char ch : text.toCharArray()) {
      if (ch == '&') {
        builder.append("&amp;");
      } else if (ch == '\'') {
        builder.append("&apos;");
      } else if (ch == '"') {
        builder.append("&quot;");
      } else if (ch == '<') {
        builder.append("&lt;");
      } else if (ch == '>') {
        builder.append("&gt;");
      } else {
        builder.append(ch);
      }
    }
		return builder.toString();
	}

  /**
   * This class should not be instantiated.
   */
  private EncodingUtil() {
  }

}