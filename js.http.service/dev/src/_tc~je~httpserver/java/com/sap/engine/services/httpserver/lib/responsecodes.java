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

import com.sap.engine.lib.util.ConcurrentHashMapObjectObject;
import com.sap.engine.lib.util.HashMapIntObject;

public class ResponseCodes {
  // ----------------- CODES ------------------
  public static final int code_continue = 100;
  public static final int code_switching_protocols = 101;
  public static final int code_ok = 200;
  public static final int code_created = 201;
  public static final int code_accepted = 202;
  public static final int code_non_authoritative_information = 203;
  public static final int code_no_content = 204;
  public static final int code_reset_content = 205;
  public static final int code_partial_content = 206;
  public static final int code_multiple_choices = 300;
  public static final int code_moved_permanently = 301;
  public static final int code_found = 302;
  public static final int code_see_other = 303;
  public static final int code_not_modified = 304;
  public static final int code_use_proxy = 305;
  public static final int code_bad_request = 400;
  public static final int code_unauthorized = 401;
  public static final int code_payment_required = 402;
  public static final int code_forbidden = 403;
  public static final int code_not_found = 404;
  public static final int code_method_not_allowed = 405;
  public static final int code_not_acceptable = 406;
  public static final int code_proxy_authentication_required = 407;
  public static final int code_request_timeout = 408;
  public static final int code_conflict = 409;
  public static final int code_gone = 410;
  public static final int code_length_required = 411;
  public static final int code_precondition_failed = 412;
  public static final int code_request_entity_too_large = 413;
  public static final int code_request_uri_too_long = 414;
  public static final int code_unsuppored_media_type = 415;
  public static final int code_requested_range_not_satisfiable = 416;
  public static final int code_expectation_failed = 417;
  public static final int code_internal_server_error = 500;
  public static final int code_not_implemented = 501;
  public static final int code_bad_gateway = 502;
  public static final int code_service_unavailable = 503;
  public static final int code_gateway_timeout = 504;
  public static final int code_http_version_not_supported = 505;
  
  //Status Code Extensions to HTTP/1.1 rfc 2518:
  public static final int code_processing = 102;
  public static final int code_multistatus = 207;
  public static final int code_unprocessable_entity = 422;
  public static final int code_locked = 423;
  public static final int code_failed_dependency = 424;
  public static final int code_insufficient_storage = 507;
  

  //easy byte array
  private static final int[] http11Codes = {100, 101,
                                            200, 201, 202, 203, 204, 205, 206,
                                            300, 301, 302, 303, 304, 305,
                                            400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417,
                                            500, 501, 502, 503, 504, 505};
  public static byte[][] status_code_byte = new byte[1000][];
  //init
  static {
    for (int i = 0; i < http11Codes.length; i++) {
      status_code_byte[http11Codes[i]] = (" " + http11Codes[i]).getBytes();
    }
  }

  //------------------ STRINGS ------------------
  // 101
  public static final String switching_protocols = "Switching Protocols";
  // 100
  public static final String continue_ = "Continue";
  // 200
  public static final String ok = "OK";
  // 201
  public static final String created = "Created";
  // 202
  public static final String accepted = "Accepted";
  // 203
  public static final String non_authoritative_information = "Non Authoritative Information";
  // 204
  public static final String no_content = "No Content";
  // 205
  public static final String reset_content = "Reset Content";
  // 206
  public static final String partial_content = "Partial Content";
  // 300
  public static final String multiple_choices = "Multiple Choices";
  // 301
  public static final String moved_permanently = "Moved Permanently";
  // 302
  public static final String found = "Found";
  // 303
  public static final String see_other = "See Other";
  // 304
  public static final String not_modified = "Not Modified";
  // 305
  public static final String use_proxy = "Use Proxy";
  // 307
  public static final String temporary_redirect = "Temporary Redirect";
  // 400
  public static final String bad_request = "Bad Request";
  // 401
  public static final String unauthorized = "Unauthorized";
  // 402
  public static final String payment_required = "Payment Required";
  // 403
  public static final String forbidden = "Forbidden";
  // 404
  public static final String not_found = "Not Found";
  // 405
  public static final String method_not_allowed = "Method Not Allowed";
  // 406
  public static final String not_acceptable = "Not Acceptable";
  // 407
  public static final String proxy_authentication_required = "Proxy Authentication Required";
  // 408
  public static final String request_timeout = "Request Timeout";
  // 409
  public static final String conflict = "Conflict";
  // 410
  public static final String gone = "Gone";
  // 411
  public static final String length_required = "Length Required";
  // 412
  public static final String precondition_failed = "Precondition Failed";
  // 413
  public static final String request_entity_too_large = "Request Entity Too Large";
  // 414
  public static final String request_uri_too_long = "Request URI Too Long";
  // 415
  public static final String unsuppored_media_type = "Unsupported Media Type";
  // 416
  public static final String requested_range_not_satisfiable = "Requested Range Not Satisfiable";
  // 417
  public static final String expectation_failed = "Expectation Failed";
  // 500
  public static final String internal_server_error = "Internal Server Error";
  // 501
  public static final String not_implemented = "Not Implemented";
  // 502
  public static final String bad_gateway = "Bad Gateway";
  // 503
  public static final String service_unavailable = "Service Unavailable";
  // 504
  public static final String gateway_timeout = "Gateway Timeout";
  // 505
  public static final String http_version_not_supported = "HTTP Version Not Supported";
  //default
  public static final String unknown = "Unknown";
  
  //Status Descriptions for Status Code Extensions to HTTP/1.1 rfc 2518:
  //102
  public static final String processing = "Processing";
  //207
  public static final String multistatus = "Multistatus";
  //422
  public static final String unprocessable_entity = "Unprocessable Entity";
  //423
  public static final String locked = "Locked";
  //424
  public static final String failed_dependency = "Failed Dependency";
  //507
  public static final String insufficient_storage = "Insufficient Storage";
  

  //------------------- BYTES ------------------
  // 101
  public static final byte[] _switching_protocols = switching_protocols.getBytes();
  // 100
  public static final byte[] _continue_ = continue_.getBytes();
  // 200
  public static final byte[] _ok = ok.getBytes();
  // 201
  public static final byte[] _created = created.getBytes();
  // 202
  public static final byte[] _accepted = accepted.getBytes();
  // 203
  public static final byte[] _non_authoritative_information = non_authoritative_information.getBytes();
  // 204
  public static final byte[] _no_content = no_content.getBytes();
  // 205
  public static final byte[] _reset_content = reset_content.getBytes();
  // 206
  public static final byte[] _partial_content = partial_content.getBytes();
  // 300
  public static final byte[] _multiple_choices = multiple_choices.getBytes();
  // 301
  public static final byte[] _moved_permanently = moved_permanently.getBytes();
  // 302
  public static final byte[] _found = found.getBytes();
  // 303
  public static final byte[] _see_other = see_other.getBytes();
  // 304
  public static final byte[] _not_modified = not_modified.getBytes();
  // 305
  public static final byte[] _use_proxy = use_proxy.getBytes();
  // 307
  public static final byte[] _temporary_redirect = temporary_redirect.getBytes();
  // 400
  public static final byte[] _bad_request = bad_request.getBytes();
  // 401
  public static final byte[] _unauthorized = unauthorized.getBytes();
  // 402
  public static final byte[] _payment_required = payment_required.getBytes();
  // 403
  public static final byte[] _forbidden = forbidden.getBytes();
  // 404
  public static final byte[] _not_found = not_found.getBytes();
  // 405
  public static final byte[] _method_not_allowed = method_not_allowed.getBytes();
  // 406
  public static final byte[] _not_acceptable = not_acceptable.getBytes();
  // 407
  public static final byte[] _proxy_authentication_required = proxy_authentication_required.getBytes();
  // 408
  public static final byte[] _request_timeout = request_timeout.getBytes();
  // 409
  public static final byte[] _conflict = conflict.getBytes();
  // 410
  public static final byte[] _gone = gone.getBytes();
  // 411
  public static final byte[] _length_required = length_required.getBytes();
  // 412
  public static final byte[] _precondition_failed = precondition_failed.getBytes();
  // 413
  public static final byte[] _request_entity_too_large = request_entity_too_large.getBytes();
  // 414
  public static final byte[] _request_uri_too_long = request_uri_too_long.getBytes();
  // 415
  public static final byte[] _unsuppored_media_type = unsuppored_media_type.getBytes();
  // 416
  public static final byte[] _requested_range_not_satisfiable = requested_range_not_satisfiable.getBytes();
  // 417
  public static final byte[] _expectation_failed = expectation_failed.getBytes();
  // 500
  public static final byte[] _internal_server_error = internal_server_error.getBytes();
  // 501
  public static final byte[] _not_implemented = not_implemented.getBytes();
  // 502
  public static final byte[] _bad_gateway = bad_gateway.getBytes();
  // 503
  public static final byte[] _service_unavailable = service_unavailable.getBytes();
  // 504
  public static final byte[] _gateway_timeout = gateway_timeout.getBytes();
  // 505
  public static final byte[] _http_version_not_supported = http_version_not_supported.getBytes();
  //default
  public static final byte[] _unknown = unknown.getBytes();

  //Bytes Descriptions for Status Code Extensions to HTTP/1.1 rfc 2518:
  //102
  public static final byte[] _processing = processing.getBytes();
  //207
  public static final byte[] _multistatus = multistatus.getBytes();
  //422
  public static final byte[] _unprocessable_entity = unprocessable_entity.getBytes();
  //423
  public static final byte[] _locked = locked.getBytes();
  //424
  public static final byte[] _failed_dependency = failed_dependency.getBytes();
  //507
  public static final byte[] _insufficient_storage = insufficient_storage.getBytes();
  
  
  private static ConcurrentHashMapObjectObject clientCodeMessages = new ConcurrentHashMapObjectObject();

  public static final void setClientMessages(String webAlias, HashMapIntObject codeToMessage) {
    clientCodeMessages.put(webAlias, codeToMessage);
  }

  public static final String reason(int i, String webalias) {
    HashMapIntObject codeToMessage = (HashMapIntObject)clientCodeMessages.get(webalias);
    if (codeToMessage != null) {
      String res = (String)codeToMessage.get(i);
      if (res != null) {
        return res;
      }
    }
    switch (i) {
      case 101: {
        return switching_protocols;
      }
      case 100: {
        return continue_;
      }
      case 200: {
        return ok;
      }
      case 201: {
        return created;
      }
      case 202: {
        return accepted;
      }
      case 203: {
        return non_authoritative_information;
      }
      case 204: {
        return no_content;
      }
      case 205: {
        return reset_content;
      }
      case 206: {
        return partial_content;
      }
      case 300: {
        return multiple_choices;
      }
      case 301: {
        return moved_permanently;
      }
      case 302: {
        return found;
      }
      case 303: {
        return see_other;
      }
      case 304: {
        return not_modified;
      }
      case 305: {
        return use_proxy;
      }
      case 307: {
        return temporary_redirect;
      }
      case 400: {
        return bad_request;
      }
      case 401: {
        return unauthorized;
      }
      case 402: {
        return payment_required;
      }
      case 403: {
        return forbidden;
      }
      case 404: {
        return not_found;
      }
      case 405: {
        return method_not_allowed;
      }
      case 406: {
        return not_acceptable;
      }
      case 407: {
        return proxy_authentication_required;
      }
      case 408: {
        return request_timeout;
      }
      case 409: {
        return conflict;
      }
      case 410: {
        return gone;
      }
      case 411: {
        return length_required;
      }
      case 412: {
        return precondition_failed;
      }
      case 413: {
        return request_entity_too_large;
      }
      case 414: {
        return request_uri_too_long;
      }
      case 415: {
        return unsuppored_media_type;
      }
      case 416: {
        return requested_range_not_satisfiable;
      }
      case 417: {
        return expectation_failed;
      }
      case 500: {
        return internal_server_error;
      }
      case 501: {
        return not_implemented;
      }
      case 502: {
        return bad_gateway;
      }
      case 503: {
        return service_unavailable;
      }
      case 504: {
        return gateway_timeout;
      }
      case 505: {
        return http_version_not_supported;
      }
      //Status Code Extensions to HTTP/1.1 rfc 2518:
      case 102: {
        return processing;
      }
      case 207: {
        return multistatus;
      }
      case 422: {
        return unprocessable_entity;
      }
      case 423: {
        return locked;
      }
      case 424: {
        return failed_dependency;
      }
      case 507: {
        return insufficient_storage;
      }
      default: {
        return unknown;
      }
    }
  }

  public static final byte[] reasonBytes(int i, String webalias) {
    HashMapIntObject codeToMessage = (HashMapIntObject)clientCodeMessages.get(webalias);
    if (codeToMessage != null) {
      String res = (String)codeToMessage.get(i);
      if (res != null) {
        return res.getBytes();
      }
    }
    switch (i) {
      case 101: {
        return _switching_protocols;
      }
      case 100: {
        return _continue_;
      }
      case 200: {
        return _ok;
      }
      case 201: {
        return _created;
      }
      case 202: {
        return _accepted;
      }
      case 203: {
        return _non_authoritative_information;
      }
      case 204: {
        return _no_content;
      }
      case 205: {
        return _reset_content;
      }
      case 206: {
        return _partial_content;
      }
      case 300: {
        return _multiple_choices;
      }
      case 301: {
        return _moved_permanently;
      }
      case 302: {
        return _found;
      }
      case 303: {
        return _see_other;
      }
      case 304: {
        return _not_modified;
      }
      case 305: {
        return _use_proxy;
      }
      case 307: {
        return _temporary_redirect;
      }
      case 400: {
        return _bad_request;
      }
      case 401: {
        return _unauthorized;
      }
      case 402: {
        return _payment_required;
      }
      case 403: {
        return _forbidden;
      }
      case 404: {
        return _not_found;
      }
      case 405: {
        return _method_not_allowed;
      }
      case 406: {
        return _not_acceptable;
      }
      case 407: {
        return _proxy_authentication_required;
      }
      case 408: {
        return _request_timeout;
      }
      case 409: {
        return _conflict;
      }
      case 410: {
        return _gone;
      }
      case 411: {
        return _length_required;
      }
      case 412: {
        return _precondition_failed;
      }
      case 413: {
        return _request_entity_too_large;
      }
      case 414: {
        return _request_uri_too_long;
      }
      case 415: {
        return _unsuppored_media_type;
      }
      case 416: {
        return _requested_range_not_satisfiable;
      }
      case 417: {
        return _expectation_failed;
      }
      case 500: {
        return _internal_server_error;
      }
      case 501: {
        return _not_implemented;
      }
      case 502: {
        return _bad_gateway;
      }
      case 503: {
        return _service_unavailable;
      }
      case 504: {
        return _gateway_timeout;
      }
      case 505: {
        return _http_version_not_supported;
      }
      //Status Code Extensions to HTTP/1.1 rfc 2518:
      case 102: {
        return _processing;
      }
      case 207: {
        return _multistatus;
      }
      case 422: {
        return _unprocessable_entity;
      }
      case 423: {
        return _locked;
      }
      case 424: {
        return _failed_dependency;
      }
      case 507: {
        return _insufficient_storage;
      }
      default: {
        return _unknown;
      }
    }
  }
}
