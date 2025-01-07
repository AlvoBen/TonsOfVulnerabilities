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
package com.sap.engine.services.httpserver.lib;

import com.sap.engine.services.httpserver.lib.util.MessageBytes;
import com.sap.engine.services.httpserver.lib.util.ByteArrayUtils;
import com.sap.engine.services.httpserver.lib.util.SortUtils;
import com.sap.engine.services.httpserver.lib.util.Ascii;
import com.sap.engine.services.httpserver.lib.protocol.HeaderNames;
import com.sap.engine.services.httpserver.lib.headers.MimeHeaders;
import com.sap.engine.lib.util.HashMapIntObject;

import java.util.*;

public class ProtocolParser {
  private static final byte[] close = "close".getBytes();
  private static final byte[] keep_alive = "Keep-Alive".getBytes();
  private static final byte[] gzip = "gzip".getBytes();
  private static final byte[] identity = "identity".getBytes();
  private static final byte[] qSeparator = ";q=".getBytes();

  public static String makeAbsolute(String s, MessageBytes aliasName, String scheme, String host, int port) {
    if (!s.startsWith("http://") && !s.startsWith("https://")) {
      s = new String(ParseUtils.separatorsToSlash(s));
      if (s.startsWith(ParseUtils.separator)) {
		if (aliasName != null && aliasName.length() > 0 && !aliasName.equals(ParseUtils.separator)
			&& !s.startsWith(ParseUtils.separator + aliasName + ParseUtils.separator)
			&& !s.startsWith(ParseUtils.separator + aliasName + ";")
			&& !s.startsWith(ParseUtils.separator + aliasName + "?")) {
          s = ParseUtils.separator + aliasName + s;
        }
      } else {
        if (aliasName != null && aliasName.length() > 0 && !s.startsWith(aliasName + ParseUtils.separator)) {
          s = ParseUtils.separator + aliasName + ParseUtils.separator + s;
        } else {
          s = "/" + s;
        }
      }
      s = makeAbsolute(scheme, host, port) + s;
    }
    return s;
  }

  public static String makeAbsolute(String scheme, String host, int port) {
    if ((scheme.equals("http") && port == 80) || (scheme.equals("https") && port == 443)) {
      return scheme + "://" + host;
    } else {
      return scheme + "://" + host + ":" + port;
    }
  }

  public static boolean parseConnectionHeader(byte[] con, MimeHeaders headers) {
    if (con.length < close.length) {
      return false;
    }
    if (ByteArrayUtils.equalsIgnoreCaseAndTrim(con, close)) {
      headers.putHeader(HeaderNames.hop_header_connection_, close);
      return false;
    }
    if (ByteArrayUtils.equalsIgnoreCaseAndTrim(con, keep_alive)) {
      return true;
    }
    boolean foundClose = false;
    int off = 0;
    int len = 0;
    for (int i = 0; i <= con.length; i++) {
      if (i == con.length || con[i] == (byte)',') {
        if (ByteArrayUtils.equalsIgnoreCaseAndTrim(con, off, len, keep_alive)) {
          return true;
        }
        if (!foundClose && ByteArrayUtils.equalsIgnoreCaseAndTrim(con, off, len, close)) {
          foundClose = true;
        }
        off += len + 1;
        len = 0;
      } else {
        len++;
      }
    }
    if (foundClose) {
      headers.putHeader(HeaderNames.hop_header_connection_, close);
    }
    return false;
  }

  /**
   * Acceptable encodigs are "identity" and "gzip" only,
   * otherwise sends error 406 Not Acceptable.
   *
   * @return true if gzip is assumed , false - otherwise.
   */
  public static boolean parseGzipAcceptEncodingHeader(byte[] encoding) {
    if (encoding == null || encoding.length == 0) {
      return false;
    }
    int gzipQvalue = 0;
    int identityQvalue = 1000;
    int starQvalue = 1000;
    boolean identityFound = false;
    boolean gzipFound = false;
    boolean startFound = false;
    int sep;
    int off = 0;
    int len = 0;
    for (int i = 0; i <= encoding.length; i++) {
      if (i == encoding.length || encoding[i] == (byte)',') {
        sep = ByteArrayUtils.indexOf(encoding, off, len, qSeparator);
        if (sep == -1) {
          while (encoding[off] == (byte)' ') {
            off++;
            len--;
          }
          if (ByteArrayUtils.equalsIgnoreCase(encoding, off, len, gzip)) {
            gzipFound = true;
            gzipQvalue = 1000;
          } else if (ByteArrayUtils.equalsIgnoreCase(encoding, off, len, identity)) {
            identityFound = true;
            identityQvalue = 1000;
          } else if (encoding[off] == (byte)'*') {
            startFound = true;
            starQvalue = 1000;
          }
        } else {
          while (encoding[off] == (byte)' ') {
            off++;
            len--;
            sep--;
          }
          if (ByteArrayUtils.equalsIgnoreCase(encoding, off, sep, gzip)) {
            gzipFound = true;
            gzipQvalue = parseQValue(encoding, off + sep + 3, len - sep - 3);
          } else if (ByteArrayUtils.equalsIgnoreCase(encoding, off, sep, identity)) {
            identityFound = true;
            identityQvalue = parseQValue(encoding, off + sep + 3, len - sep - 3);
          } else if (encoding[off] == (byte)'*') {
            startFound = true;
            starQvalue = parseQValue(encoding, off + sep + 3, len - sep - 3);
          }
        }
        off += len + 1;
        len = 0;
      } else {
        len++;
      }
    }
    if (startFound) {
      if (!identityFound) {
        identityQvalue = starQvalue;
      }
      if (!gzipFound) {
        gzipQvalue = starQvalue;
      }
    }
    if (gzipQvalue == 0 && identityQvalue == 0) {
      return false;
    } else if (gzipQvalue >= identityQvalue) {
      return true;
    }
    return false;
  }

  public static Vector getLocalesVector(byte[] acceptLanguage) {
    if (acceptLanguage == null || acceptLanguage.length == 0) {
      return null;
    }
    HashMapIntObject languages = ProtocolParser.processAcceptLanguage(acceptLanguage);
    int[] sortedQs = SortUtils.sort(languages.getAllKeys());
    return extractLocales(languages, sortedQs);
  }

  public static boolean isValidURL(String s) {
    if (s.indexOf('>') != -1) {
      return false;
    }
    if (s.indexOf('<') != -1) {
      return false;
    }
    if (s.indexOf('|') != -1) {
      return false;
    }
    return true;
  }

  // ------------------------ private ------------------------

  private static HashMapIntObject processAcceptLanguage(byte[] acceptLanguage) {
    int off = 0;
    int len = 0;
    HashMapIntObject languages = new HashMapIntObject(2);
    for (int i = 0; i <= acceptLanguage.length; i++) {
      if (i == acceptLanguage.length || acceptLanguage[i] == (byte)',') {
        int sep = ByteArrayUtils.indexOf(acceptLanguage, off, len, qSeparator);
        int qValue = 1000;
        String language = null;
        if (sep == -1) {
          if (len - off == 1 && acceptLanguage[off] == '*') {
            off += len + 1;
            len = 0;
            continue;
          }
          language = new String(acceptLanguage, off, len);
        } else {
          if (sep == 1 && acceptLanguage[off] == '*') {
            off += len + 1;
            len = 0;
            continue;
          }
          qValue = parseQValue(acceptLanguage, off + sep + 3, len - sep - 3);
          language = new String(acceptLanguage, off, sep);
        }
        Vector languagesWithSameQ;
        if (languages.containsKey(qValue)) {
          languagesWithSameQ = (Vector) languages.get(qValue);
        } else {
          languagesWithSameQ = new Vector();
        }
        languagesWithSameQ.addElement(language);
        languages.put(qValue, languagesWithSameQ);
        off += len + 1;
        len = 0;
      } else if (acceptLanguage[i] == (byte)' ') {
        if (len == 0) {
          off++;
        } else {
          len++;
        }
      } else {
        len++;
      }
    }
    return languages;
  }

  private static int parseQValue(byte[] q, int off, int len) {
    if (q == null || len == 0) {
      return 1000;
    }
    if (q[off] == (byte)'0') {
      if (len > 1) {
        return Ascii.asciiArrToInt(q, off + 2, len - 2);
      } else {
        return 0;
      }
    } else if (q[off] == (byte)'1') {
      return 1000;
    } else {
      //unsupported value
      return 0000;
    }
  }

  private static Vector extractLocales(HashMapIntObject languages, int[] sortedQs) {
    Vector localesSorted = new Vector();
    for (int i = 0; i < sortedQs.length; i++) {
      Vector languagesWithSameQ = (Vector) languages.get(sortedQs[i]);
      Enumeration le = languagesWithSameQ.elements();
      while (le.hasMoreElements()) {
        String language = (String) le.nextElement();
        String country = "";
        int countryIndex = language.indexOf("-");
        if (countryIndex > -1) {
          country = language.substring(countryIndex + 1).trim();
          language = language.substring(0, countryIndex).trim();
        }
        localesSorted.addElement(new Locale(language, country));
      }
    }
    return localesSorted;
  }
}
