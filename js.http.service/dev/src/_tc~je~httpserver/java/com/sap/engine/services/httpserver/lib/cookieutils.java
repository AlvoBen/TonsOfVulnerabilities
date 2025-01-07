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

/*
 *
 * @author Galin Galchev
 * @version 4.0
 */
import com.sap.engine.lib.util.ArrayObject;
import com.sap.engine.services.httpserver.lib.util.Ascii;
import com.sap.engine.services.httpserver.lib.util.ByteArrayUtils;

import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Util that returns Http header representing some Cookie object.
 *
 */
public class CookieUtils {
  public static final String sHttpOnly = "; HttpOnly";
  public static final String sSecure = "; Secure";
  
  private static final byte[] comment = "; Comment=".getBytes();
  private static final byte[] domain = "; Domain=".getBytes();
  private static final byte[] expires = "; Expires=".getBytes();
  private static final byte[] maxAge = "; Max-Age=".getBytes();
  private static final byte[] discard = "; Discard".getBytes();
  private static final byte[] path = "; Path=".getBytes();
  private static final byte[] secure = "; Secure".getBytes();
  private static final byte[] versionS = "; Version=1".getBytes();
  private static final byte[] httpOnly = sHttpOnly.getBytes();
  private static final byte[] s_c = "Set-Cookie: ".getBytes();
  private static final byte[] rn = "\r\n".getBytes();

  private static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
  private static final Locale locale = Locale.US;
  private static final SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", locale);
  static {
    simpledateformat.setTimeZone(GMT_ZONE);
  }

  /**
   * Returns the value of Http header which represents this Cookie object.
   *
   * @param   cookie  some cookie object
   * @return     Http header value representing this Cookie
   */
  public static byte[] getCookieHeaderValue(HttpCookie cookie) {
    int version = cookie.getVersion();

    if (cookie.getValue() == null) {
      cookie.setValue("null");
    }

    byte[] retValue = Ascii.getBytes(cookie.getName());
    byte temp[] = new byte[1];
    temp[0] = (byte) '=';
    retValue = ByteArrayUtils.append(retValue, temp);
    retValue = ByteArrayUtils.append(retValue, Ascii.getBytes(cookie.getValue()));

    if (version == 1) {
      retValue = ByteArrayUtils.append(retValue, versionS);
      if (cookie.getComment() != null) {
        retValue = ByteArrayUtils.append(retValue, comment);
        retValue = ByteArrayUtils.append(retValue, Ascii.getBytes(cookie.getComment()));
      }
    }

    if (cookie.getDomain() != null) {
      retValue = ByteArrayUtils.append(retValue, domain);
      retValue = ByteArrayUtils.append(retValue, Ascii.getBytes(cookie.getDomain()));
    }

    if (cookie.getMaxAge() >= 0) {
      // According to the RFC2109 for setting Max-Age vs. Expires cookie attribute: 
      //      if (Version == 0) => Expires = <Date>
      //      if (Version == 1) => Max-Age = <delta-seconds>
      //
      // But IE does not take into accunt Max-Age cookie attributes, that 
      // is why SAP J2EE Engine sets both attributes - Expires and Max-Age 
      retValue = ByteArrayUtils.append(retValue, expires);
      temp = getCookieExpiry(cookie.getMaxAge());
      retValue = ByteArrayUtils.append(retValue, temp);
      
      if (cookie.getVersion() == 1) {
        retValue = ByteArrayUtils.append(retValue, maxAge);
        temp = (String.valueOf(cookie.getMaxAge())).getBytes();
        retValue = ByteArrayUtils.append(retValue, temp);
      }
    }

    if (cookie.getPath() != null) {
      retValue = ByteArrayUtils.append(retValue, path);
      retValue = ByteArrayUtils.append(retValue, Ascii.getBytes(cookie.getPath()));
    }
    if (cookie.getSecure()) {
      retValue = ByteArrayUtils.append(retValue, secure);
    }
    if (cookie.isHttpOnly()) {
      retValue = ByteArrayUtils.append(retValue, httpOnly);      
    }
    
    return retValue;
  }
  

  /**
   * Returns Http header representing this Cookie object.
   *
   * @param   cookie  some cookie object
   * @return     Http header representing this Cookie
   */
  public static byte[] getCookieHeader(HttpCookie cookie) {
    byte[] retValue = getCookieHeaderValue(cookie);
    return ByteArrayUtils.append(s_c, retValue);
  }

  public static byte[] getAllCookieHeaders(ArrayObject cookies) {
    if (cookies != null) {
      Object[] enumeration = cookies.toArray();
      byte[] cookieHeaders = new byte[0];

      for (int i = 0; enumeration != null && i < enumeration.length; i++) {
        byte[] oneHeader = getCookieHeader((HttpCookie)enumeration[i]);
        cookieHeaders = ByteArrayUtils.append(cookieHeaders, oneHeader);
        cookieHeaders = ByteArrayUtils.append(cookieHeaders, rn);
      }

      return cookieHeaders;
    }

    return null;
  }

  private static byte[] getCookieExpiry(long l) {
    long time = 0;
    if (l != 0) {
      time = System.currentTimeMillis() + l * 1000L;
    }
    if (time == 0) {
      return simpledateformat.format(new Date(10000)).getBytes();
    } else {
      return simpledateformat.format(new Date(time)).getBytes();
    }
  }
}

