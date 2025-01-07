package com.sap.engine.services.httpserver.interfaces;

import java.util.HashMap;

public interface DSRHttpRequestStatistics {
  /**
   * Returns all headers from the http request. The keys in the hashtable are Strings representing
   * header names. The values are String arrays representing all values of headers with the given
   * as key name.
   *
   * @return      all headers from the http request as name/values pairs
   */
  public HashMap getRequestHeaders();   // tva e za optimizacia za da ne go pazi i simo 

  /**
   * Returns IP of the client from the request
   * @return  byte[] representation of the client's IP
   */
  public byte[] getClientIp();
  
  /**
   * Returns the whole request line ([http method] [resource] [protocol version]) for example:
   *  GET /test.html HTTP1.1
   * @return  byte[] representation of the request line
   */
  public byte[] getRequestLine();

  /**
   * Returns all headers from the http response. The keys in the hashtable are Strings representing
   * header names. The values are String arrays representing all values of headers with the given
   * as key name.
   * @return      all headers from the http response as name/values pairs.
   */
  public HashMap getResponseHeaders();

  /**
   * The code of returned response.
   * @return integer representing the response code
   */
  public int getResponseCode();

  /**
   * Returns the description of the response code sent to the client.
   * @return byte[] representation of the response description
   */
  public byte[] getResponsePhrase();

  /**
   * The length of the request body.
   * @return  integer repesenting the body length
   */
  public int getRequestLength();
  
  /**
   * The length of the response body.
   * @return  integer rpesenting the body length
   */
  public int getResponseLength();  
  
  /**
   * Returns the type of the requested resource 
   * 
   * @return true if the response is static resource, otherwiese false.
   */  
  public boolean isStatic(); 
  
  /**
   * Returns the approximate size of the session (in bytes) remaining at the end of 
   * the http request processing. The number is accumulating the sizes of the http 
   * sessions touched by this requests. 
   *    
   * @return   >0     the size of the session in kilobytes
   *            0     if there is no session in the request
   *           -1     in case of error or unserializable session
   */
  public long getApproximateSessionSize();

}
