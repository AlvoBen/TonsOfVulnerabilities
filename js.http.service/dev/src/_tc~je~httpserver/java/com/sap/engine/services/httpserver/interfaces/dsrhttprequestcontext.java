package com.sap.engine.services.httpserver.interfaces;

import java.util.HashMap;

/**
 * This interface is used by DSR service to retrieve runtime information about the current http request and response.
 * It is important to use methods related to the response after the response is generated.
 *
 * @author Maria Jurova, Diyan Yordanov, Violeta Uzunova
 * @version 6.30
 */
public interface DSRHttpRequestContext {
  
  /**
   * Returns the name of the host as specified in the http request.
   *
   * @return      the name of the host as specified in the http request
   */
  public String getHost();

  /**
   * Returns a port as specified in the http request.
   *
   * @return      a port as specified in the http request
   */
  public int getPort();
  
  /**
   * Returns the name of the scheme used in the http request.
   *
   * @return      the name of the scheme used in the http request
   */
  public String getScheme();

  /**
   * Returns the part of the request�s URL from the protocol name up to the query
   * string in the first line of the HTTP request.
   * @return      the full URL from the request line of the http request
   */
  public String getFullURL(); //-> from alias with request paraam
  
  /**
   * Returns the part of the requestï¿½s URL from the protocol name up to the query
   * string in the first line of the HTTP request.
   * @return      the full URL from the request line of the http request
   */
  public String getURL(); // -> from alias without request param 
 
  /**
   * Returns all headers from the http request. The keys in the hashtable are Strings representing
   * header names. The values are String arrays representing all values of headers with the given
   * as key name.
   *
   * @return      all headers from the http request as name/values pairs
   */
  public HashMap getRequestHeaders();

  /**
   * Return the value of the headers <CODE>name</CODE>. If such header does not exist, returns null.
   * 
   * @param   name    the name of the header 
   * @return          the value of the header
   */
  public String[] getRequestHeader(String name);
}