package com.sap.engine.services.httpserver.chain;

import java.io.IOException;


/**
 * Chain is an abstraction that allows request and response to be 
 * passed for processing to the next <code>Filter</code>
 */
public interface Chain {
  /**
   * This method is called to give control to the next <code>Filter<code> 
   * in the chain if any.
   * 
   * <p>Method returns after execution of all filters in the chain.</p>
   * 
   * @param request
   * a <code>Request</code> object that contains the client request
   * 
   * @param response
   * a <code>Response</code> object that contains the response to the client
   * 
   * @throws FilterException
   * if the request could not be processed
   * 
   * @throws java.io.IOException
   * if an input or output error is detected
   */
  public void process(HTTPRequest request, HTTPResponse response)
      throws FilterException, IOException;
  
  /**
   * This method is called to give control to the next <code>Filter<code> 
   * in the chain if any.
   * 
   * <p>In contrast to {@link process(HTTPRequest, HTTPResponse)} method this
   * one returns immediately and chain calls next filter after this method
   * returns.</p>
   * 
   * @param request
   * a <code>Request</code> object that contains the client request
   * 
   * @param response
   * a <code>Response</code> object that contains the response to the client
   */
  public void proceed(HTTPRequest request, HTTPResponse response);
  
  /**
   * This method is called to give control to the next <code>Filter<code> 
   * in the chain if any.
   * 
   * <p>In contrast to {@link process(HTTPRequest, HTTPResponse)} method this
   * one returns immediately and chain calls next filter after this method
   * returns.</p>
   */ 
  public void proceed();
}
