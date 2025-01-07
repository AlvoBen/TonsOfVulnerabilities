/**
 * Copyright (c) 2008 by SAP Labs Bulgaria,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * 
 * Created on Aug 15, 2008 by I032049
 *   
 */

package com.sap.engine.interfaces.security.auth;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.lib.xml.util.BASE64Decoder;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Helper class to be used by callback handler
 * 
 * @author NW F SIM Runtime BG
 * @version 7.1
 */
class CallbackHandlerHelper {

  protected static final Location LOCATION = Location.getLocation(AuthenticationTraces.CALLBACK_HANDLER_LOCATION);
  
  /**
   * The default character encoding used by web container.
   */
  private static final String DEFAULT_CHARACTER_ENCODING = "ISO-8859-1";

  /**
   * The request attribute that contains the original context path stored on an forwarded dispatcher request.
   */
  private static final String FORWARD_CONTEXT_PATH_REQUEST_ATRIBUTE = "javax.servlet.forward.context_path";

  /**
   * The request attribute that contains the original query string stored on an forwarded dispatcher request.
   */
  private static final String FORWARD_QUERY_STRING_REQUEST_ATRIBUTE = "javax.servlet.forward.query_string";

  /**
   * The request attribute that contains the original path stored on an forwarded dispatcher request.
   */
  private static final String FORWARD_URI_REQUEST_ATRIBUTE = "javax.servlet.forward.request_uri";

  /**
   * Decodes header authorization data using the character encoding of the request
   * or the default character encoding "ISO-8859-1".
   * @param request HttpServletRequest object
   * @param encodedAuthorizationData encoded header data 
   * @return a String containing the decoded authorization data or null if data cannot be decoded
   */
  static String decodeAuthorizationData(HttpServletRequest request, String encodedAuthorizationData) {
    try {
      String charsetName = request.getCharacterEncoding();

      if (charsetName == null) {
        charsetName = DEFAULT_CHARACTER_ENCODING;
      }

      byte[] decodedBytes = BASE64Decoder.decode(encodedAuthorizationData.getBytes());
      //$JL-I18N$

      return new String(decodedBytes, charsetName);
      //$JL-I18N$
      //This jlin test does not catch the case when the charset string is provided as a variable 

    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.WARNING, "Cannot decode authorization header.", e);
      return null;
    }
  }

  /**
   * Returns the value of an authentication page property for a given security context
   * It can be for the login page, error page, password change page, etc. or policy domain of the application.
   * 
   * @param context SecurityContext object
   * @param propName a String value of the authentication property page name
   * @return a String value of the authentication property
   */
  static String getAuthenticationContextProperty(SecurityContext context, String propName) {
    if (context == null || propName == null) {
      return null;
    }

    String propValue = context.getAuthenticationContext().getProperty(propName);

    if (propValue != null && !propValue.startsWith("/")) {
      propValue = "/" + propValue;
    }

    return propValue;
  }
  
  /**
   * Constructs the original URL from the request. 
   * @param request HttpServletRequest object
   * @return a String value containing the current URL, in case of forward the original URL is returned 
   */
  static String getOriginalUrlFromRequest(HttpServletRequest request) throws MalformedURLException {
    String currentUrl = request.getRequestURL().toString();
    String queryString = request.getQueryString();

    // if forwarded get original (parent) url
    String parentUrlRelative = (String) request.getAttribute(FORWARD_URI_REQUEST_ATRIBUTE);
    if (parentUrlRelative != null && parentUrlRelative.length() != 0) {
      URL requestUrl = new URL(currentUrl);
      currentUrl = requestUrl.getProtocol() + "://" + requestUrl.getHost() + ":" + requestUrl.getPort() + parentUrlRelative;
      queryString = (String) request.getAttribute(FORWARD_QUERY_STRING_REQUEST_ATRIBUTE);
    }

    if (queryString != null) {
      currentUrl += "?" + queryString;
    }
    return currentUrl;
  }

  /**
   * Returns the original context path of the request
   * @param request HttpServletRequest object
   * @return a String value containing the context path, in case of forward the original context path is returned
   */
  static String getOriginalContextPath(HttpServletRequest request) {
    String contextPath = (String) request.getAttribute(FORWARD_CONTEXT_PATH_REQUEST_ATRIBUTE);
    if (contextPath == null || contextPath.length() == 0) {
      contextPath = request.getContextPath();
    }
    return contextPath;
  }

  /**
   * Returns the original URI value of the request  
   * @param request HttpServletRequest object
   * @return a String value containing the URI of the request, in case of forward the original URI is returned
   */
  static String getOriginalURI(HttpServletRequest request) {
    String uri = (String) request.getAttribute(FORWARD_URI_REQUEST_ATRIBUTE);
    if (uri == null || uri.length() == 0) {
      uri = request.getRequestURI();
    }
    return uri;
  }
  
  /**
   * Returns the relative URL extracted from an absolute URL string
   * @param url a String value containing the absolute URL 
   * @return a String value containing the relative URL
   */
  static String getRelativeURL(String url) {
    if (url == null) {
      return null;
    }

    int pos = url.indexOf("/", 8); // Length of "https://" string is 8

    if (pos > -1) {
      url = url.substring(pos);
    }

    return url;
  }
}
