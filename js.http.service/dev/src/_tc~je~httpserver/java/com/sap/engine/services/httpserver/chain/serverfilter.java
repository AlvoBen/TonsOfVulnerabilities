package com.sap.engine.services.httpserver.chain;

import java.io.IOException;

/**
 * Every filter that requires to have access to <code>ServerScope</code>
 * should extend this class
 */
public abstract class ServerFilter implements Filter {
  public void process(HTTPRequest request, HTTPResponse response, Chain chain) 
      throws FilterException, IOException {
    process(request, response, (ServerChain)chain);
  }
  
  /**
   * This method makes the right cast of the passed <code>Chain</code> object
   * and gives access only to available surrounding scopes
   * 
   * @param request
   * a <code>HttpRequest</code> object that contains the client request
   * 
   * @param response
   * a <code>HttpResponse</code> object that contains the response to the client
   * 
   * @param chain
   * a <code>ApplicationChain</code> object that gives access to available 
   * surrounding scopes and allows request and response to be passed to 
   * the next <code>Filter</code>
   * 
   * @throws FilterException
   * if the request could not be processed
   * 
   * @throws java.io.IOException
   * if an input or output error is detected
   */
  public abstract void process(HTTPRequest request, HTTPResponse response,
      ServerChain chain) throws FilterException, IOException;
}
