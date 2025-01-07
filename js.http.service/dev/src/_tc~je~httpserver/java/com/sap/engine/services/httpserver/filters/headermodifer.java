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
package com.sap.engine.services.httpserver.filters;

import java.io.IOException;

import com.sap.engine.services.httpserver.chain.FilterConfig;
import com.sap.engine.services.httpserver.chain.FilterException;
import com.sap.engine.services.httpserver.chain.HTTPRequest;
import com.sap.engine.services.httpserver.chain.HTTPResponse;
import com.sap.engine.services.httpserver.chain.ServerChain;
import com.sap.engine.services.httpserver.chain.ServerFilter;
import static com.sap.engine.services.httpserver.lib.protocol.HeaderNames.request_header_if_match;
import static com.sap.engine.services.httpserver.lib.protocol.HeaderNames.request_header_if_match_;
import static com.sap.engine.services.httpserver.lib.protocol.HeaderNames.request_header_if_none_match;
import static com.sap.engine.services.httpserver.lib.protocol.HeaderNames.request_header_if_none_match_;
import static com.sap.engine.services.httpserver.lib.protocol.HeaderNames.request_header_if_range;
import static com.sap.engine.services.httpserver.lib.protocol.HeaderNames.request_header_if_range_;

/**
 * This filter is responsible to modify request and response http headers.
 * 
 * Modifies the following header:
 * 		- removes -gzip suffix from the each entity-tag of the If-Match, 
 * 			If-None-Match, If-Range headers (request headers)
 * 
 * @author Violeta Uzunova (I024174)
 */
public class HeaderModifer extends ServerFilter {

	/**
   * This method is invoked to prepare the object for processing the request
   */
	public void init(FilterConfig config) throws FilterException {
		
	}

	
	/**
   * Modifies the http request and response headers
   *  
   * @param request
   * a <code>Request</code> object that contains the client request
   * 
   * @param response
   * a <code>Response</code> object that contains the response to the client
   * 
   * @param chain
   * a <code>Chain</code> object that gives access to surrounding scopes and 
   * allows request and response to be passed to the next <code>Filter</code>
   * 
   * @throws FilterException 
   * if the request could not be processed
   * 
   * @throws java.io.IOException
   * if an input or output error is detected
   */
	public void process(HTTPRequest request, HTTPResponse response, ServerChain chain) throws FilterException, IOException {
		changeETagHeadres(request);
		chain.proceed(request, response);
	}

	/**
   * The method is called to finilize its work and clean up used resources 
   */
	public void destroy() {
		
	}
	
  /**
   * Removes the -gzip suffix from the each entity-tag of the If-Match, If-None-Match, If-Range headers
   * For more details see CSN I-3889270 2006   
   * 
   * @param request
   * a <code>Request</code> object that contains the client request
   */
  private void changeETagHeadres(HTTPRequest request) {
    //If-Match = ( "*" | 1#entity-tag )
    // ex. If-Match: "xyzzy"
    //     If-Match: "xyzzy", "r2d2xxxx", "c3piozzzz"
    //     If-Match: *
    // entity-tag = [ weak ] opaque-tag
    //     weak       = "W/"
    //     opaque-tag = quoted-string  	
    String headerValue = request.getClient().getRequest().getHeaders().getHeader(request_header_if_match);
    if (headerValue != null) {      
    	request.getClient().getRequest().getHeaders().putHeader(request_header_if_match_, removeGzipExtention(headerValue).getBytes());
    }
    
    //If-None-Match = ( "*" | 1#entity-tag )
    // ex. If-None-Match: "xyzzy"
    //     If-None-Match: W/"xyzzy"
    //     If-None-Match: "xyzzy", "r2d2xxxx", "c3piozzzz"
    //     If-None-Match: W/"xyzzy", W/"r2d2xxxx", W/"c3piozzzz"
    //     If-None-Match: *
    // entity-tag = [ weak ] opaque-tag
    //     weak       = "W/"
    //     opaque-tag = quoted-string    
    headerValue = request.getClient().getRequest().getHeaders().getHeader(request_header_if_none_match);
    if (headerValue != null) {
    	request.getClient().getRequest().getHeaders().putHeader(request_header_if_none_match_, removeGzipExtention(headerValue).getBytes());
    }    
    
    //If-Range - ( entity-tag | HTTP-date ) together with Range header
    headerValue = request.getClient().getRequest().getHeaders().getHeader(request_header_if_range);
    if (headerValue != null) {
    	request.getClient().getRequest().getHeaders().putHeader(request_header_if_range_, removeGzipExtention(headerValue).getBytes());
    }
    
    //If-Modified-Since - time; ex.: If-Modified-Since: Sat, 29 Oct 1994 19:43:31 GMT
    //If-Unmodified-Since - time; ex. If-Unmodified-Since: Sat, 29 Oct 1994 19:43:31 GMT
  }
  
  private String removeGzipExtention(String headerValue) {
    if (headerValue.equals("*")) {
      return headerValue;
    }
    
    boolean firstToken = true;    
    StringBuffer headerValueBuffer = new StringBuffer("");  
    String token = null;
    
    int startIndex = 0;
    int endIndex = headerValue.indexOf(",");
    
    do {
      if (endIndex == -1) {
        endIndex = headerValue.length();
      }
      token = headerValue.substring(startIndex, endIndex).trim();      
      if (token.endsWith("-gzip\"")) {                
        if (!firstToken) {
          headerValueBuffer.append(", ");
        } else {
          firstToken = false;
        }
        headerValueBuffer.append(token.substring(0, token.length() - 6)); //"-gzip\"".length() = 6
        headerValueBuffer.append("\"");          
      } else if (token.endsWith("-gzip")) {         
        if (!firstToken) {
          headerValueBuffer.append(", ");
        } else {
          firstToken = false;
        }
        headerValueBuffer.append(token.substring(0, token.length() - 5)); //"-gzip".length() = 5          
      } else {
        if (!firstToken) {
          headerValueBuffer.append(", ");
        } else {
          firstToken = false;
        }
        headerValueBuffer.append(token);
      }
      
      // prepare for the next loop;
      startIndex = endIndex + 1;
      endIndex = headerValue.indexOf(",", startIndex);
    } while (startIndex < headerValue.length());    
    return headerValueBuffer.toString();
  }
}
