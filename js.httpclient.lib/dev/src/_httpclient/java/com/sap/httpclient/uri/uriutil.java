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

import com.sap.httpclient.exception.URIException;
import com.sap.httpclient.exception.URLDecodeException;

import java.util.BitSet;

/**
 * URI escape and character encoding and decoding utility.
 *
 * @author Nikolai Neichev
 */
public class URIUtil {

  protected static final BitSet empty = new BitSet(1);

  /**
   * Get the query of an URI.
   *
   * @param uri a string regarded an URI
   * @return the query string; <code>null</code> if empty or undefined
   */
  public static String getQuery(String uri) {
    if (uri == null || uri.length() == 0) {
      return null;
    }
    // consider of net_path
    int at = uri.indexOf("//");
    int from = uri.indexOf("/", ( at >= 0 ? uri.lastIndexOf("/", (at - 1) >= 0 ? 0 : at + 2) : 0 ) );
    // the authority part of URI ignored
    int to = uri.length();
    // reuse the at and from variables to consider the query
    at = uri.indexOf("?", from);
    if (at >= 0) {
      from = at + 1;
    } else {
      return null;
    }
    // check the fragment
    if (uri.lastIndexOf("#") > from) {
      to = uri.lastIndexOf("#");
    }
    // get the path and query.
    return (from < 0 || from == to) ? null : uri.substring(from, to);
  }

  /**
   * Get the path of an URI.
   *
   * @param uri a string regarded an URI
   * @return the path string
   */
  public static String getPath(String uri) {
    if (uri == null) {
      return null;
    }
    // consider of net_path
    int at = uri.indexOf("//");
    int from = uri.indexOf("/", ( at >= 0 ? (uri.lastIndexOf("/", at - 1) >= 0 ? 0 : at + 2) : 0 ) );
    // the authority part of URI ignored
    int to = uri.length();
    // check the query
    if (uri.indexOf('?', from) != -1) {
      to = uri.indexOf('?', from);
    }
    // check the fragment
    if (uri.lastIndexOf("#") > from && uri.lastIndexOf("#") < to) {
      to = uri.lastIndexOf("#");
    }
    // get only the path.
    return (from < 0) ? (at >= 0 ? "/" : uri) : uri.substring(from, to);
  }

  /**
   * Escape and encode a string regarded as within the query component URI with the default net charset.
   *
   * @param unescaped an unescaped string
   * @return the escaped string
   */
  public static String encodeWithinQuery(String unescaped) {
    return encodeWithinQuery(unescaped, URI.getDefaultProtocolCharset());
  }

  /**
   * Escape and encode a string regarded as within the query component of an URI with a specified charset.
   *
   * @param unescaped an unescaped string
   * @param charset   the charset
   * @return the escaped string
   */
  public static String encodeWithinQuery(String unescaped, String charset) {
    return encode(unescaped, URI.allowed_within_query, charset);
  }

  /**
   * Escape and encode a string regarded as the query component of an URI with a specified charset.
   *
   * @param unescaped an unescaped string
   * @param charset   the charset
   * @return the escaped string
   */
  public static String encodeQuery(String unescaped, String charset) {
    return encode(unescaped, URI.allowed_query, charset);
  }

  /**
   * Escape and encode a specified string with allowed characters not to be escaped
   *
   * @param unescaped a string
   * @param allowed   allowed characters not to be escaped
   * @return the escaped string
   */
  public static String encode(String unescaped, BitSet allowed) {
    return encode(unescaped, allowed, URI.getDefaultProtocolCharset());
  }

  /**
   * Escape and encode a specified string with allowed characters not to be escaped and a specified charset.
   *
   * @param unescaped a string
   * @param allowed   allowed characters not to be escaped
   * @param charset   the charset
   * @return the escaped string
   */
  public static String encode(String unescaped, BitSet allowed, String charset) {
    byte[] rawdata = URLCodec.encodeUrl(allowed, EncodingUtil.getBytes(unescaped, charset));
    return EncodingUtil.getASCIIString(rawdata);
  }

  /**
   * Unescape and decode a specified string regarded as an escaped string with the default charset.
   *
   * @param escaped a string
   * @return the unescaped string
   * @throws URIException if the string cannot be decoded (invalid)
   */
  public static String decode(String escaped) throws URIException {
    try {
      byte[] rawdata = URLCodec.decodeUrl(EncodingUtil.getASCIIBytes(escaped));
      return EncodingUtil.getString(rawdata, URI.getDefaultProtocolCharset());
    } catch (URLDecodeException e) {
      throw new URIException(e.getMessage());
    }
  }

  /**
   * Unescape and decode a specified string regarded as an escaped string with the specified charset.
   *
   * @param escaped a string
   * @param charSet the encoding charset
   * @return the unescaped string
   * @throws URIException if the string cannot be decoded (invalid)
   */
  public static String decode(String escaped, String charSet) throws URIException {
    try {
      byte[] rawdata = URLCodec.decodeUrl(EncodingUtil.getASCIIBytes(escaped));
      return EncodingUtil.getString(rawdata, charSet);
    } catch (URLDecodeException e) {
      throw new URIException(e.getMessage());
    }
  }

}