package com.sap.engine.services.httpserver.server.dsr;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP_SESSION_SIZE;

import java.util.Enumeration;
import java.util.HashMap;

import com.sap.engine.services.httpserver.interfaces.DSRHttpRequestStatistics;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.RequestImpl;
import com.sap.engine.services.httpserver.server.ResponseImpl;
import com.sap.engine.services.httpserver.server.sessionsize.SessionSizeManager;

public class DSRHttpRequestStatisticsImpl implements DSRHttpRequestStatistics {
  
  private ResponseImpl response;
  private RequestImpl request;  
  
  public void init(RequestImpl request, ResponseImpl response) {
    this.response = response;
    this.request = request;
  }

  /**
   * Returns IP of the client from the request
   * @return  byte[] representation of the client's IP
   */
  public byte[] getClientIp() {
    return request.getClientIP();    
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

  public int getRequestLength() {  //tva naistina li my triabva 
    return -1;
  }

  /**
   * Returns the whole request line ([http method] [resource] [protocol version]) for example:
   *  GET /test.html HTTP1.1
   * @return  byte[] representation of the request line
   */
  public byte[] getRequestLine() {    
    return request.getRequestLine().toByteArray();
  }

  public int getResponseCode() {    
    return response.getResponseCode();
  }

  public HashMap getResponseHeaders() {
    HashMap result = new HashMap();
    Enumeration en = response.getHeaders().names();
    while (en.hasMoreElements()) {
      String name = (String)en.nextElement();
      result.put(name, response.getHeaders().getHeaders(name));
    }
    return result;
  }

  public int getResponseLength() {    
    return response.getResponseLength(); 
  }

  public byte[] getResponsePhrase() {    
    return response.getResponsePhrase();
  }

  /**
   * Returns the type of the requested resource 
   * 
   * @return true if the response is static resource, otherwiese false.
   */  
  public boolean isStatic() { 
    return response.isStaticResourceServed();
  }
  
  /**
   * Returns the approximate size of the session (in bytes) remaining at the end of 
   * the http request processing. The number is accumulating the sizes of the http 
   * sessions touched by this requests. 
   *    
   * @return   >0     the size of the session in kilobytes
   *            0     if there is no session in the request
   *           -1     in case of error or unserializable session
   */
  public long getApproximateSessionSize() {    
    long result = SessionSizeManager.getSessionSize(request.getClientId());
    //TODO trace it could be removed 
    Log.traceInfo(LOCATION_HTTP_SESSION_SIZE, "[DSR] Session size request [" +
        request.getClientId() + "] is [" + result +"] bytes", null);    
    return result;
  }
}
