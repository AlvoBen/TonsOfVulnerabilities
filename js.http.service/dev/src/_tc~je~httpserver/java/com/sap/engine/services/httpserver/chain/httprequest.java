package com.sap.engine.services.httpserver.chain;

import com.sap.engine.services.httpserver.interfaces.HttpParameters;
import com.sap.engine.services.httpserver.server.Client;

/**
 * An abstract representation of incoming request. Gives access to all
 * most frequently used parts of the request
 * 
 * TODO: Add new useful methods with care
 */
public interface HTTPRequest {
  /**
   * Temporary available. Returns reference to <code>HttpParameters</code>
   * object for use with all runtime logic.
   * <p>
   * Will be removed later
   * 
   * @return
   * an <code>HttpParameters</code> object
   * 
   * @deprecated
   */
  public HttpParameters getHTTPParameters();
  
  /**
   * Temporary available. Returns reference to <code>Client</code>
   * object for use with all runtime logic.
   * <p>
   * Will be removed later
   * 
   * @return
   * an <code>Client</code> object
   * 
   * @deprecated
   */
  public Client getClient();
  
  /**
   * Gets the name of the HTTP method with which this request was made,
   * for example, GET, POST, or PUT
   * 
   * @return
   * a <code>String</code> specifying the name of the method with which 
   * this request was made
   */
  public String getMethod();
  
  /**
   * Gets an unique identifier of the request
   * 
   * @return
   * an <code>int</code> that uniquely identifies the request
   */
  public int getID();
  
  /**
   * Returns the path part from the request URL according RFC 2396
   * <p>
   * Path is decoded and normalized
   * 
   * @return
   * an <code>String</code> with path part from the request URL
   */
  public String getURLPath();
}
