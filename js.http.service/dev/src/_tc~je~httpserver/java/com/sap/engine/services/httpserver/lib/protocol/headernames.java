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
package com.sap.engine.services.httpserver.lib.protocol;

public interface HeaderNames {
  //Strings
  //Request
  public static final String request_header_accept = "Accept";
  public static final String request_header_accpet_charset = "Accept-Charset";
  public static final String request_header_accpet_encoding = "Accept-Encoding";
  public static final String request_header_accpet_lenguage = "Accept-Language";
  public static final String request_header_authorization = "Authorization";
  public static final String request_header_expect = "Expect";
  public static final String request_header_from = "From";
  public static final String request_header_host = "Host";
  public static final String request_header_if_match = "If-Match";
  public static final String request_header_if_modified_since = "If-Modified-Since";
  public static final String request_header_if_none_match = "If-None-Match";
  public static final String request_header_if_range = "If-Range";
  public static final String request_header_if_unmodified_since = "If-Unmodified-Since";
  public static final String request_header_max_forwards = "Max-Forwards";
  public static final String request_header_proxy_authorization = "Proxy-Authorization";
  public static final String request_header_range = "Range";
  public static final String request_header_referer = "Referer";
  public static final String request_header_te = "TE";
  public static final String request_header_user_agent = "User-Agent";
  public static final String request_header_cookie = "Cookie";

  //Response
  public static final String response_header_accept_ranges = "Accept-Ranges";
  public static final String response_header_age = "Age";
  public static final String response_header_etag = "ETag";
  public static final String response_header_location = "Location";
  public static final String response_header_proxy_authenticate = "Proxy-Authenticate";
  public static final String response_header_retry_after = "Retry-After";
  public static final String response_header_server = "Server";
  public static final String response_header_vary = "Vary";
  public static final String response_header_www_authenticate = "WWW-Authenticate";
  public static final String response_header_authentication_info = "Authentication-Info";
  public static final String response_header_x_powered_by = "X-Powered-By";
  public static final String response_header_set_cookie = "Set-Cookie";

  //Entity
  public static final String entity_header_allow = "Allow";
  public static final String entity_header_content_encoding = "Content-Encoding";
  public static final String entity_header_content_language = "Content-Language";
  public static final String entity_header_content_length = "Content-Length";
  public static final String entity_header_content_location = "Content-Location";
  public static final String entity_header_content_md5 = "Content-MD5";
  public static final String entity_header_content_range = "Content-Range";
  public static final String entity_header_content_type = "Content-Type";
  public static final String entity_header_expires = "Expires";
  public static final String entity_header_last_modified = "Last-Modified";
  public static final String entity_header_cache_control = "Cache-Control";
  public static final String entity_header_pragma = "Pragma";
  public static final String entity_header_date = "Date";

  //Hop-by-hop
  public static final String hop_header_connection = "Connection";
  public static final String hop_header_keep_alive = "Keep-Alive";
  public static final String hop_header_proxy_authenticate = "Proxy-Authenticate";
  public static final String hop_header_proxy_connection = "Proxy-Authorization";
  public static final String hop_header_te = "TE";
  public static final String hop_header_trailers = "Trailers";
  public static final String hop_header_transfer_encoding = "Transfer-Encoding";
  public static final String hop_header_upgrade = "Upgrade";

  //other
  public static final String other_header_public = "Public";

  //sap propriatory
  public static final String propriatory_sap_isc_etag = "sap-isc-etag";
  public static final String propriatory_sap_cache_control = "sap-cache-control";
  public static final String propriatory_sap_request_id = "sap-request-id";

  //Byte arrays
  //Request
  public static final byte[] request_header_accept_ = "Accept".getBytes();
  public static final byte[] request_header_accpet_charset_ = "Accept-Charset".getBytes();
  public static final byte[] request_header_accpet_encoding_ = "Accept-Encoding".getBytes();
  public static final byte[] request_header_accpet_lenguage_ = "Accept-Language".getBytes();
  public static final byte[] request_header_authorization_ = "Authorization".getBytes();
  public static final byte[] request_header_expect_ = "Expect".getBytes();
  public static final byte[] request_header_from_ = "From".getBytes();
  public static final byte[] request_header_host_ = "Host".getBytes();
  public static final byte[] request_header_if_match_ = "If-Match".getBytes();
  public static final byte[] request_header_if_modified_since_ = "If-Modified-Since".getBytes();
  public static final byte[] request_header_if_none_match_ = "If-None-Match".getBytes();
  public static final byte[] request_header_if_range_ = "If-Range".getBytes();
  public static final byte[] request_header_if_unmodified_since_ = "If-Unmodified-Since".getBytes();
  public static final byte[] request_header_max_forwards_ = "Max-Forwards".getBytes();
  public static final byte[] request_header_proxy_authorization_ = "Proxy-Authorization".getBytes();
  public static final byte[] request_header_range_ = "Range".getBytes();
  public static final byte[] request_header_referer_ = "Referer".getBytes();
  public static final byte[] request_header_te_ = "TE".getBytes();
  public static final byte[] request_header_user_agent_ = "User-Agent".getBytes();
  public static final byte[] request_header_cookie_ = "Cookie".getBytes();

  //Response
  public static final byte[] response_header_accept_ranges_ = "Accept-Ranges".getBytes();
  public static final byte[] response_header_age_ = "Age".getBytes();
  public static final byte[] response_header_etag_ = "ETag".getBytes();
  public static final byte[] response_header_location_ = "Location".getBytes();
  public static final byte[] response_header_proxy_authenticate_ = "Proxy-Authenticate".getBytes();
  public static final byte[] response_header_retry_after_ = "Retry-After".getBytes();
  public static final byte[] response_header_server_ = "Server".getBytes();
  public static final byte[] response_header_vary_ = "Vary".getBytes();
  public static final byte[] response_header_www_authenticate_ = "WWW-Authenticate".getBytes();
  public static final byte[] response_header_authentication_info_ = "Authentication-Info".getBytes();
  public static final byte[] response_header_x_powered_by_ = "X-Powered-By".getBytes();

  //Entity
  public static final byte[] entity_header_allow_ = "Allow".getBytes();
  public static final byte[] entity_header_content_encoding_ = "Content-Encoding".getBytes();
  public static final byte[] entity_header_content_language_ = "Content-Language".getBytes();
  public static final byte[] entity_header_content_length_ = "Content-Length".getBytes();
  public static final byte[] entity_header_content_location_ = "Content-Location".getBytes();
  public static final byte[] entity_header_content_md5_ = "Content-MD5".getBytes();
  public static final byte[] entity_header_content_range_ = "Content-Range".getBytes();
  public static final byte[] entity_header_content_type_ = "Content-Type".getBytes();
  public static final byte[] entity_header_expires_ = "Expires".getBytes();
  public static final byte[] entity_header_last_modified_ = "Last-Modified".getBytes();
  public static final byte[] entity_header_cache_control_ ="Cache-Control".getBytes();
  public static final byte[] entity_header_pragma_ = "Pragma".getBytes();
  public static final byte[] entity_header_date_ = "Date".getBytes();

  //Hop-by-hop
  public static final byte[] hop_header_connection_ = "Connection".getBytes();
  public static final byte[] hop_header_keep_alive_ = "Keep-Alive".getBytes();
  public static final byte[] hop_header_proxy_authenticate_ = "Proxy-Authenticate".getBytes();
  public static final byte[] hop_header_proxy_connection_ = "Proxy-Authorization".getBytes();
  public static final byte[] hop_header_te_ = "TE".getBytes();
  public static final byte[] hop_header_trailers_ = "Trailers".getBytes();
  public static final byte[] hop_header_transfer_encoding_ = "Transfer-Encoding".getBytes();
  public static final byte[] hop_header_upgrade_ = "Upgrade".getBytes();

  //other
  public static final byte[] other_header_public_ = other_header_public.getBytes();

  //sap propriatory
  public static final byte[] propriatory_sap_isc_etag_ = propriatory_sap_isc_etag.getBytes();
  public static final byte[] propriatory_sap_cache_control_ = propriatory_sap_cache_control.getBytes();
  public static final byte[] propriatory_sap_request_id_ = propriatory_sap_request_id.getBytes();
}
