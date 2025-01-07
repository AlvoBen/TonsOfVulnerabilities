package com.sap.engine.services.httpserver.chain;

import java.io.IOException;


/**
 * Filter is a chunk from the processing chain that works over one 
 * aspect of the request and response
 */
public interface Filter {
  /**
   * Called by the server to indicate to a filter that it is being placed 
   * into service. The servlet container calls this method exactly once
   * after instantiating the filter. The method must complete successfully 
   * before the filter is asked to do any processing work.
   * <p>
   * The chain cannot place the filter into service if the method either
   * <ol>
   * <li>Throws a <code>FilterException</code></li>
   * <li>Does not return within a time period defined by the server</li>
   * </ol>
   * 
   * @param config
   * a <code>FilterConfig</code> object containing the filter's name
   * and initialization parameters
   * 
   * @throws FilterException
   * if filter initialization fails
   */
  public void init(FilterConfig config) throws FilterException;
  
  /**
   * The method is called by the chain each time a request/response pair 
   * is passed through the chain due to a client request for a resource at 
   * the end of the chain. The <code>Chain</code> passed in to this method
   * allows the <code>Filter</code> to pass on the request and response to
   * the next entity in the chain.
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
  public void process(HTTPRequest request, HTTPResponse response, Chain chain)
      throws FilterException, IOException;
  
  /**
   * Called by the server to indicate to a filter that it is being taken out
   * of service. This method is only called once all threads within the 
   * filter's <code>process</code> method have exited or after a timeout period
   * has passed. After the web container calls this method, it will not call
   * the <code>process</code> method again on this instance of the filter.
   * <p>
   * This method gives the filter an opportunity to clean up any resources 
   * that are being held (for example, memory, file handles, threads).
   */
  public void destroy();
}
