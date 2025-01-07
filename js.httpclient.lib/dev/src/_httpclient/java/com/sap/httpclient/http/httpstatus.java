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
package com.sap.httpclient.http;

import com.sap.httpclient.HttpState;
import com.sap.tc.logging.Location;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.PropertyResourceBundle;

/**
 * Container of the HTTP status codes.
 * All status codes defined in RFC1945 (HTTP/1.0, RFC2616 (HTTP/1.1), and RFC2518 (WebDAV) are supported.
 *
 * @author Nikolai Neichev
 */
public class HttpStatus {

  /**
   * Reason phrases table.
   */
  private static final String[][] REASON_PHRASES = new String[][]{
    new String[0],
    new String[3],
    new String[8],
    new String[8],
    new String[25],
    new String[8]
  };

  /**
   * Get the reason phrase corresponding to a particular status code.
   *
   * @param statusCode the numeric status code
   * @return the reason phrase associated with the specified status code, or null if not recognized.
   */
  public static String getReasonPhrase(int statusCode) {
    if (statusCode < 0) {
      throw new IllegalArgumentException("status code is negative :" + statusCode);
    }
    int majorCode = statusCode / 100;
    int minorCode = statusCode - majorCode * 100;
    if ( (majorCode < 1) ||
         (majorCode > (REASON_PHRASES.length - 1) ) ||
         (minorCode < 0) ||
         (minorCode > (REASON_PHRASES[majorCode].length - 1)) ) {
      return null; // code not found
    }
    return REASON_PHRASES[majorCode][minorCode];
  }

  /**
   * Store the specified reason phrase, by the status code.
   *
   * @param statusCode   The status code
   * @param reasonPhrase The reason phrase for this status code
   */
  private static void addStatusCodeMap(int statusCode, String reasonPhrase) {
    int majorCode = statusCode / 100;
    REASON_PHRASES[majorCode][statusCode - majorCode * 100] = reasonPhrase;
  }

  // Tha Status Codes constants *******************************************************************

  // --- 1xx Informational ---

  /**
   * <tt>100 Continue</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_CONTINUE = 100;
  /**
   * <tt>101 Switching Protocols</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_SWITCHING_PROTOCOLS = 101;
  /**
   * <tt>102 Processing</tt> (WebDAV - RFC 2518)
   */
  public static final int SC_PROCESSING = 102;

  // --- 2xx Success ---

  /**
   * <tt>200 OK</tt> (HTTP/1.0 - RFC 1945)
   */
  public static final int SC_OK = 200;
  /**
   * <tt>201 Created</tt> (HTTP/1.0 - RFC 1945)
   */
  public static final int SC_CREATED = 201;
  /**
   * <tt>202 Accepted</tt> (HTTP/1.0 - RFC 1945)
   */
  public static final int SC_ACCEPTED = 202;
  /**
   * <tt>203 Non Authoritative Information</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_NON_AUTHORITATIVE_INFORMATION = 203;
  /**
   * <tt>204 No Content</tt> (HTTP/1.0 - RFC 1945)
   */
  public static final int SC_NO_CONTENT = 204;
  /**
   * <tt>205 Reset Content</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_RESET_CONTENT = 205;
  /**
   * <tt>206 Partial Content</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_PARTIAL_CONTENT = 206;
  /**
   * <tt>207 Multi-Status</tt> (WebDAV - RFC 2518) or <tt>207 Partial Update
   * OK</tt> (HTTP/1.1 - draft-ietf-http-v11-spec-rev-01?)
   */
  public static final int SC_MULTI_STATUS = 207;

  // --- 3xx Redirection ---

  /**
   * <tt>300 Mutliple Choices</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_MULTIPLE_CHOICES = 300;
  /**
   * <tt>301 Moved Permanently</tt> (HTTP/1.0 - RFC 1945)
   */
  public static final int SC_MOVED_PERMANENTLY = 301;
  /**
   * <tt>302 Moved Temporarily</tt> (Sometimes <tt>Found</tt>) (HTTP/1.0 - RFC 1945)
   */
  public static final int SC_MOVED_TEMPORARILY = 302;
  /**
   * <tt>303 See Other</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_SEE_OTHER = 303;
  /**
   * <tt>304 Not Modified</tt> (HTTP/1.0 - RFC 1945)
   */
  public static final int SC_NOT_MODIFIED = 304;
  /**
   * <tt>305 Use Proxy</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_USE_PROXY = 305;
  /**
   * <tt>307 Temporary Redirect</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_TEMPORARY_REDIRECT = 307;

  // --- 4xx Client Error ---

  /**
   * <tt>400 Bad Request</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_BAD_REQUEST = 400;
  /**
   * <tt>401 Unauthorized</tt> (HTTP/1.0 - RFC 1945)
   */
  public static final int SC_UNAUTHORIZED = 401;
  /**
   * <tt>402 Payment Required</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_PAYMENT_REQUIRED = 402;
  /**
   * <tt>403 Forbidden</tt> (HTTP/1.0 - RFC 1945)
   */
  public static final int SC_FORBIDDEN = 403;
  /**
   * <tt>404 Not Found</tt> (HTTP/1.0 - RFC 1945)
   */
  public static final int SC_NOT_FOUND = 404;
  /**
   * <tt>405 Method Not Allowed</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_METHOD_NOT_ALLOWED = 405;
  /**
   * <tt>406 Not Acceptable</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_NOT_ACCEPTABLE = 406;
  /**
   * <tt>407 Proxy Authentication Required</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_PROXY_AUTHENTICATION_REQUIRED = 407;
  /**
   * <tt>408 Request Timeout</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_REQUEST_TIMEOUT = 408;
  /**
   * <tt>409 Conflict</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_CONFLICT = 409;
  /**
   * <tt>410 Gone</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_GONE = 410;
  /**
   * <tt>411 Length Required</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_LENGTH_REQUIRED = 411;
  /**
   * <tt>412 Precondition Failed</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_PRECONDITION_FAILED = 412;
  /**
   * <tt>413 Request Entity Too Large</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_REQUEST_ENTITY_TOO_LARGE = 413;
  /**
   * <tt>414 Request-URI Too Long</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_REQUEST_URI_TOO_LONG = 414;
  /**
   * <tt>415 Unsupported Media Type</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_UNSUPPORTED_MEDIA_TYPE = 415;
  /**
   * <tt>416 Requested Range Not Satisfiable</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
  /**
   * <tt>417 Expectation Failed</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_EXPECTATION_FAILED = 417;

  /**
   * <tt>418 Unprocessable Entity</tt> (WebDAV drafts)
   * <tt>418 Reauthentication Required</tt> (HTTP/1.1 drafts)
   */
//   public static final int SC_UNPROCESSABLE_ENTITY = 418;  // not used

  /**
   * <tt>419 Insufficient Space on Resource</tt> (WebDAV - draft-ietf-webdav-net-05)
   * <tt>419 Proxy Reauthentication Required</tt> (HTTP/1.1 drafts)
   */
  public static final int SC_INSUFFICIENT_SPACE_ON_RESOURCE = 419;

  /**
   * <tt>420 Method Failure</tt> (WebDAV - draft-ietf-webdav-net-05?)
   */
  public static final int SC_METHOD_FAILURE = 420;
  /**
   * <tt>422 Unprocessable Entity</tt> (WebDAV - RFC 2518)
   */
  public static final int SC_UNPROCESSABLE_ENTITY = 422;
  /**
   * <tt>423 Locked</tt> (WebDAV - RFC 2518)
   */
  public static final int SC_LOCKED = 423;
  /**
   * <tt>424 Failed Dependency</tt> (WebDAV - RFC 2518)
   */
  public static final int SC_FAILED_DEPENDENCY = 424;

  // --- 5xx Server Error ---

  /**
   * <tt>500 Server Error</tt> (HTTP/1.0 - RFC 1945)
   */
  public static final int SC_INTERNAL_SERVER_ERROR = 500;
  /**
   * <tt>501 Not Implemented</tt> (HTTP/1.0 - RFC 1945)
   */
  public static final int SC_NOT_IMPLEMENTED = 501;
  /**
   * <tt>502 Bad Gateway</tt> (HTTP/1.0 - RFC 1945)
   */
  public static final int SC_BAD_GATEWAY = 502;
  /**
   * <tt>503 Service Unavailable</tt> (HTTP/1.0 - RFC 1945)
   */
  public static final int SC_SERVICE_UNAVAILABLE = 503;
  /**
   * <tt>504 Gateway Timeout</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_GATEWAY_TIMEOUT = 504;
  /**
   * <tt>505 HTTP Version Not Supported</tt> (HTTP/1.1 - RFC 2616)
   */
  public static final int SC_HTTP_VERSION_NOT_SUPPORTED = 505;

  /**
   * <tt>507 Insufficient Storage</tt> (WebDAV - RFC 2518)
   */
  public static final int SC_INSUFFICIENT_STORAGE = 507;

  private static final Location LOG = Location.getLocation(HttpState.class);

  /** Set up status code to "reason phrase" map. */
  static {
    String HTTP_PREFIX = "http_";
    PropertyResourceBundle bundle = null;
    String reasons = "com/sap/httpclient/HttpReasonPhrases.properties";
    try {
      bundle = new PropertyResourceBundle(new FileInputStream(reasons));
    } catch (IOException ioe) {
      LOG.warningT(reasons + "not found, will use English phrases");
    }
    if (bundle != null) {
      addStatusCodeMap(SC_OK, bundle.getString(HTTP_PREFIX + SC_OK)); // 200
      addStatusCodeMap(SC_CREATED, bundle.getString(HTTP_PREFIX + SC_CREATED));  // 201
      addStatusCodeMap(SC_ACCEPTED, bundle.getString(HTTP_PREFIX + SC_ACCEPTED));  // 202
      addStatusCodeMap(SC_NO_CONTENT, bundle.getString(HTTP_PREFIX + SC_NO_CONTENT));  // 204
      addStatusCodeMap(SC_MOVED_PERMANENTLY, bundle.getString(HTTP_PREFIX + SC_MOVED_PERMANENTLY));  // 301
      addStatusCodeMap(SC_MOVED_TEMPORARILY, bundle.getString(HTTP_PREFIX + SC_MOVED_TEMPORARILY));  // 302
      addStatusCodeMap(SC_NOT_MODIFIED, bundle.getString(HTTP_PREFIX + SC_NOT_MODIFIED));  // 304
      addStatusCodeMap(SC_BAD_REQUEST, bundle.getString(HTTP_PREFIX + SC_BAD_REQUEST));  // 400
      addStatusCodeMap(SC_UNAUTHORIZED, bundle.getString(HTTP_PREFIX + SC_UNAUTHORIZED));  // 401
      addStatusCodeMap(SC_FORBIDDEN, bundle.getString(HTTP_PREFIX + SC_FORBIDDEN));  // 403
      addStatusCodeMap(SC_NOT_FOUND, bundle.getString(HTTP_PREFIX + SC_NOT_FOUND));  //404
      addStatusCodeMap(SC_INTERNAL_SERVER_ERROR, bundle.getString(HTTP_PREFIX + SC_INTERNAL_SERVER_ERROR));  // 500
      addStatusCodeMap(SC_NOT_IMPLEMENTED, bundle.getString(HTTP_PREFIX + SC_NOT_IMPLEMENTED));  // 501
      addStatusCodeMap(SC_BAD_GATEWAY, bundle.getString(HTTP_PREFIX + SC_BAD_GATEWAY));  // 502
      addStatusCodeMap(SC_SERVICE_UNAVAILABLE, bundle.getString(HTTP_PREFIX + SC_SERVICE_UNAVAILABLE));  // 503

      // HTTP 1.1 Server status codes -- see RFC 2048
      addStatusCodeMap(SC_CONTINUE, bundle.getString(HTTP_PREFIX + SC_CONTINUE));  // 100
      addStatusCodeMap(SC_SWITCHING_PROTOCOLS, bundle.getString(HTTP_PREFIX + SC_SWITCHING_PROTOCOLS));  // 101
      addStatusCodeMap(SC_NON_AUTHORITATIVE_INFORMATION, bundle.getString(HTTP_PREFIX + SC_NON_AUTHORITATIVE_INFORMATION));  // 203
      addStatusCodeMap(SC_RESET_CONTENT, bundle.getString(HTTP_PREFIX + SC_RESET_CONTENT));  // 205
      addStatusCodeMap(SC_PARTIAL_CONTENT, bundle.getString(HTTP_PREFIX + SC_PARTIAL_CONTENT));  // 206
      addStatusCodeMap(SC_MULTIPLE_CHOICES, bundle.getString(HTTP_PREFIX + SC_MULTIPLE_CHOICES));  // 300
      addStatusCodeMap(SC_SEE_OTHER, bundle.getString(HTTP_PREFIX + SC_SEE_OTHER));  // 303
      addStatusCodeMap(SC_USE_PROXY, bundle.getString(HTTP_PREFIX + SC_USE_PROXY));  // 305
      addStatusCodeMap(SC_TEMPORARY_REDIRECT, bundle.getString(HTTP_PREFIX + SC_TEMPORARY_REDIRECT));  // 307
      addStatusCodeMap(SC_PAYMENT_REQUIRED, bundle.getString(HTTP_PREFIX + SC_PAYMENT_REQUIRED));  // 402
      addStatusCodeMap(SC_METHOD_NOT_ALLOWED, bundle.getString(HTTP_PREFIX + SC_METHOD_NOT_ALLOWED));  // 405
      addStatusCodeMap(SC_NOT_ACCEPTABLE, bundle.getString(HTTP_PREFIX + SC_NOT_ACCEPTABLE));  // 406
      addStatusCodeMap(SC_PROXY_AUTHENTICATION_REQUIRED, bundle.getString(HTTP_PREFIX + SC_PROXY_AUTHENTICATION_REQUIRED));  // 407
      addStatusCodeMap(SC_REQUEST_TIMEOUT, bundle.getString(HTTP_PREFIX + SC_REQUEST_TIMEOUT));  // 408
      addStatusCodeMap(SC_CONFLICT, bundle.getString(HTTP_PREFIX + SC_CONFLICT));  // 409
      addStatusCodeMap(SC_GONE, bundle.getString(HTTP_PREFIX + SC_GONE));  // 410
      addStatusCodeMap(SC_LENGTH_REQUIRED, bundle.getString(HTTP_PREFIX + SC_LENGTH_REQUIRED));  // 411
      addStatusCodeMap(SC_PRECONDITION_FAILED, bundle.getString(HTTP_PREFIX + SC_PRECONDITION_FAILED));  // 412
      addStatusCodeMap(SC_REQUEST_ENTITY_TOO_LARGE, bundle.getString(HTTP_PREFIX + SC_REQUEST_ENTITY_TOO_LARGE));  // 413
      addStatusCodeMap(SC_REQUEST_URI_TOO_LONG, bundle.getString(HTTP_PREFIX + SC_REQUEST_URI_TOO_LONG));  // 414
      addStatusCodeMap(SC_UNSUPPORTED_MEDIA_TYPE, bundle.getString(HTTP_PREFIX + SC_UNSUPPORTED_MEDIA_TYPE));  // 415
      addStatusCodeMap(SC_REQUESTED_RANGE_NOT_SATISFIABLE, bundle.getString(HTTP_PREFIX + SC_REQUESTED_RANGE_NOT_SATISFIABLE));  // 416
      addStatusCodeMap(SC_EXPECTATION_FAILED, bundle.getString(HTTP_PREFIX + SC_EXPECTATION_FAILED));  // 417
      addStatusCodeMap(SC_GATEWAY_TIMEOUT, bundle.getString(HTTP_PREFIX + SC_GATEWAY_TIMEOUT));  // 504
      addStatusCodeMap(SC_HTTP_VERSION_NOT_SUPPORTED, bundle.getString(HTTP_PREFIX + SC_HTTP_VERSION_NOT_SUPPORTED));  // 505

      // WebDAV Server-specific status codes
      addStatusCodeMap(SC_PROCESSING, bundle.getString(HTTP_PREFIX + SC_PROCESSING));  // 102
      addStatusCodeMap(SC_MULTI_STATUS, bundle.getString(HTTP_PREFIX + SC_MULTI_STATUS));  // 207
      addStatusCodeMap(SC_INSUFFICIENT_SPACE_ON_RESOURCE, bundle.getString(HTTP_PREFIX + SC_INSUFFICIENT_SPACE_ON_RESOURCE));  // 419
      addStatusCodeMap(SC_METHOD_FAILURE, bundle.getString(HTTP_PREFIX + SC_METHOD_FAILURE));  // 420
      addStatusCodeMap(SC_UNPROCESSABLE_ENTITY, bundle.getString(HTTP_PREFIX + SC_UNPROCESSABLE_ENTITY));  // 422
      addStatusCodeMap(SC_LOCKED, bundle.getString(HTTP_PREFIX + SC_LOCKED));  // 423
      addStatusCodeMap(SC_FAILED_DEPENDENCY, bundle.getString(HTTP_PREFIX + SC_FAILED_DEPENDENCY));  // 424
      addStatusCodeMap(SC_INSUFFICIENT_STORAGE, bundle.getString(HTTP_PREFIX + SC_INSUFFICIENT_STORAGE));  // 507
    } else { // defaults are english phrases
      // HTTP 1.0 Server status codes -- see RFC 1945
      addStatusCodeMap(SC_OK, "OK");  // 200
      addStatusCodeMap(SC_CREATED, "Created");  // 201
      addStatusCodeMap(SC_ACCEPTED, "Accepted");  // 202
      addStatusCodeMap(SC_NO_CONTENT, "No Content");  // 204
      addStatusCodeMap(SC_MOVED_PERMANENTLY, "Moved Permanently");  // 301
      addStatusCodeMap(SC_MOVED_TEMPORARILY, "Moved Temporarily");  // 302
      addStatusCodeMap(SC_NOT_MODIFIED, "Not Modified");  // 304
      addStatusCodeMap(SC_BAD_REQUEST, "Bad Request");  // 400
      addStatusCodeMap(SC_UNAUTHORIZED, "Unauthorized");  // 401
      addStatusCodeMap(SC_FORBIDDEN, "Forbidden");  // 403
      addStatusCodeMap(SC_NOT_FOUND, "Not Found");  //404
      addStatusCodeMap(SC_INTERNAL_SERVER_ERROR, "Internal Server Error");  // 500
      addStatusCodeMap(SC_NOT_IMPLEMENTED, "Not Implemented");  // 501
      addStatusCodeMap(SC_BAD_GATEWAY, "Bad Gateway");  // 502
      addStatusCodeMap(SC_SERVICE_UNAVAILABLE, "Service Unavailable");  // 503

      // HTTP 1.1 Server status codes -- see RFC 2048
      addStatusCodeMap(SC_CONTINUE, "Continue");  // 100
      addStatusCodeMap(SC_SWITCHING_PROTOCOLS, "Switching Protocols");  // 101
      addStatusCodeMap(SC_NON_AUTHORITATIVE_INFORMATION, "Non Authoritative Information");  // 203
      addStatusCodeMap(SC_RESET_CONTENT, "Reset Content");  // 205
      addStatusCodeMap(SC_PARTIAL_CONTENT, "Partial Content");  // 206
      addStatusCodeMap(SC_MULTIPLE_CHOICES, "Multiple Choices");  // 300
      addStatusCodeMap(SC_SEE_OTHER, "See Other");  // 303
      addStatusCodeMap(SC_USE_PROXY, "Use Proxy");  // 305
      addStatusCodeMap(SC_TEMPORARY_REDIRECT, "Temporary Redirect");  // 307
      addStatusCodeMap(SC_PAYMENT_REQUIRED, "Payment Required");  // 402
      addStatusCodeMap(SC_METHOD_NOT_ALLOWED, "Method Not Allowed");  // 405
      addStatusCodeMap(SC_NOT_ACCEPTABLE, "Not Acceptable");  // 406
      addStatusCodeMap(SC_PROXY_AUTHENTICATION_REQUIRED, "Proxy Authentication Required");  // 407
      addStatusCodeMap(SC_REQUEST_TIMEOUT, "Request Timeout");  // 408
      addStatusCodeMap(SC_CONFLICT, "Conflict");  // 409
      addStatusCodeMap(SC_GONE, "Gone");  // 410
      addStatusCodeMap(SC_LENGTH_REQUIRED, "Length Required");  // 411
      addStatusCodeMap(SC_PRECONDITION_FAILED, "Precondition Failed");  // 412
      addStatusCodeMap(SC_REQUEST_ENTITY_TOO_LARGE, "Request Entity Too Large");  // 413
      addStatusCodeMap(SC_REQUEST_URI_TOO_LONG, "Request-URI Too Long");  // 414
      addStatusCodeMap(SC_UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type");  // 415
      addStatusCodeMap(SC_REQUESTED_RANGE_NOT_SATISFIABLE, "Requested Range Not Satisfiable");  // 416
      addStatusCodeMap(SC_EXPECTATION_FAILED, "Expectation Failed");  // 417
      addStatusCodeMap(SC_GATEWAY_TIMEOUT, "Gateway Timeout");  // 504
      addStatusCodeMap(SC_HTTP_VERSION_NOT_SUPPORTED, "Http Version Not Supported");  // 505

      // WebDAV Server-specific status codes
      addStatusCodeMap(SC_PROCESSING, "Processing");  // 102
      addStatusCodeMap(SC_MULTI_STATUS, "Multi-Status");  // 207
      addStatusCodeMap(SC_INSUFFICIENT_SPACE_ON_RESOURCE, "Insufficient Space On Resource");  // 419
      addStatusCodeMap(SC_METHOD_FAILURE, "Method Failure");  // 420
      addStatusCodeMap(SC_UNPROCESSABLE_ENTITY, "Unprocessable Entity");  // 422
      addStatusCodeMap(SC_LOCKED, "Locked");  // 423
      addStatusCodeMap(SC_FAILED_DEPENDENCY, "Failed Dependency");  // 424
      addStatusCodeMap(SC_INSUFFICIENT_STORAGE, "Insufficient Storage");  // 507
    }
  }

}