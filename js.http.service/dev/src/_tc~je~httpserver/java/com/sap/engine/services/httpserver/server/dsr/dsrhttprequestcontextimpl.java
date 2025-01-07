package com.sap.engine.services.httpserver.server.dsr;

import java.util.Enumeration;
import java.util.HashMap;

import com.sap.engine.services.httpserver.interfaces.DSRHttpRequestContext;
import com.sap.engine.services.httpserver.interfaces.client.Request;

public class DSRHttpRequestContextImpl implements DSRHttpRequestContext {

  private Request request = null;

  public void init(Request request) {
    this.request = request;
  }
  
  /**
   * Returns the name of the host as specified in the http request.
   *
   * @return      the name of the host as specified in the http request
   */
  public String getHost() {
    return request.getHost();
  }

  /**
   * Returns a port as specified in the http request.
   *
   * @return      a port as specified in the http request
   */
  public int getPort() {
    return request.getPort();
  }
  
  /**
   * Returns the name of the scheme used in the http request.
   *
   * @return      the name of the scheme used in the http request
   */
  public String getScheme() {
    return request.getScheme();
  }

  /**
   * Returns the part of the request�s URL from the protocol name up to the query
   * string in the first line of the HTTP request.
   * @return      the full URL from the request line of the http request
   */
  public String getFullURL() { //-> from alias with request paraam
    return request.getRequestLine().getFullUrl().toString();
  }
  
  /**
   * Returns the part of the requestï¿½s URL from the protocol name up to the query
   * string in the first line of the HTTP request.
   * @return      the full URL from the request line of the http request
   */
  public String getURL() { // -> from alias without request param
    return request.getRequestLine().getUrlDecoded().toString();
  }
 
  /**
   * Returns all headers from the http request. The keys in the hashtable are Strings representing
   * header names. The values are String arrays representing all values of headers with the given
   * as key name.
   *
   * @return      all headers from the http request as name/values pairs
   */
  public HashMap getRequestHeaders() {
    HashMap result = new HashMap();
    Enumeration en = request.getHeaders().names();
    while (en.hasMoreElements()) {
      String name = (String)en.nextElement();
      result.put(name, request.getHeaders().getHeaders(name));
    }
    return result;
  }
  
  /**
   * Return the value of the headers <CODE>name</CODE>. If such header does not exist, returns null.
   * 
   * @param   name    the name of the header 
   * @return          the value of the header
   */
  public String[] getRequestHeader(String name) {
    if (name == null) {
      return null;
    }
    return request.getHeaders().getHeaders(name);
  }
}
