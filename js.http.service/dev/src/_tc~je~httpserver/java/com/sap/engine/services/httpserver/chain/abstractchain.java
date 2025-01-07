package com.sap.engine.services.httpserver.chain;

import java.io.IOException;
import java.util.Iterator;

import com.sap.engine.services.httpserver.server.Log;

public abstract class AbstractChain implements Chain {
  /**
   * Keeps reference to extended chain
   */
  protected Chain chain;
  
  /**
   * Holds an iterator to filters in the chain
   */
  private Iterator filters;
  
  /**
   * Helper variable that holds temporary the request passed by
   * {@link proceed(HTTPRequest, HTTPResponse)} method
   */
  private HTTPRequest httpRequest;

  /**
   * Helper variable that holds temporary the response passed by
   * {@link proceed(HTTPRequest, HTTPResponse)} method
   */
  private HTTPResponse httpResponse;
  
  /**
   * Helper variable that indicates if call to the next filter is made through
   * {@link proceed(HTTPRequest, HTTPResponse)} or {@link proceed()} method
   */
  private boolean next;

  /**
   * Constructs a <code>Chain</code> that iterates over the passed filters
   * 
   * @param filters
   * an <code>Iterator&lt;Filter&gt;</code> over this chain filters
   */
  public AbstractChain(Iterator<Filter> filters) {
    this.filters = filters;
  }
  
  /**
   * Constructs a <code>Chain</code> that iterates over the passed filters 
   * and then passes the control to the given chain
   * 
   * @param chain
   * the <code>Chain</code> that will be called at the end 
   * 
   * @param filters
   * an <code>Iterator&lt;Filter&gt;</code> over this chain filters
   */
  public AbstractChain(Chain chain, Iterator<Filter> filters) {
    this.chain = chain;
    this.filters = filters;
  }
  
  public void process(HTTPRequest request, HTTPResponse response)
      throws FilterException, IOException {
    if (filters.hasNext()) {
      // TODO: Keep in mind that we have here references to request and
      // response objects. If they are outside an object pool this should
      // not be a problem. What if they are inside object pool?
      httpRequest = request;
      httpResponse = response;
      do {
        next = false;
        Filter filter = (Filter) filters.next();
        
        if (Log.LOCATION_HTTP.beDebug()) {
          Log.LOCATION_HTTP.debugT("Invoke [" + filter.getClass().getName() + "] filter on processing request [" + request.getURLPath() + "]");        
        }
        
        filter.process(httpRequest, httpResponse, this);
      } while (next && filters.hasNext());
      // If control is passed to the next filter but no more filters
      // then control is passed to the extended chain if it is available
      if (next && chain != null) {
        next = false;
        if (Log.LOCATION_HTTP.beDebug()) {
          Log.LOCATION_HTTP.debugT("Invoke the next chain on processing request [" + request.getURLPath() + "]");
        }
        chain.process(request, response);
      }
    } else if (chain != null) {
      if (Log.LOCATION_HTTP.beDebug()) {
        Log.LOCATION_HTTP.debugT("Invoke the next chain on processing request [" + request.getURLPath() + "]");
      }
      chain.process(request, response);
    }
  }

  public void proceed(HTTPRequest request, HTTPResponse response) {
    httpRequest = request;
    httpResponse = response;
    next = true;
  }

  public void proceed() {
    next = true;
  }
}
