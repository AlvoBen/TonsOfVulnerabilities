package com.sap.engine.services.httpserver.chain;

import com.sap.engine.services.httpserver.interfaces.ErrorData;
import com.sap.engine.services.httpserver.server.ResponseImpl;

/**
 * An abstract representation of returned response that gives access to
 * all most frequently used parts of the response
 * 
 * IMPORTANT: Add new useful methods with care
 */
public interface HTTPResponse {
  /**
   * Provides access to the old response.
   * 
   * <p>Will be removed when this interface becomes complete</p>
   * 
   * @return
   * the <code>ResponseImpl</code> object for this request
   * 
   * @deprecated
   */
  public ResponseImpl getRawResponse();
  
  /**
   * Gets the status code for this response.
   * 
   * @return
   * The status code for the response.
   */
  public int getStatusCode();
  
  /**
   * Returns the length, in bytes, of the response body and made available by
   * the output stream, or -1 if the length is not known.
   * 
   * @return
   * an <code>int</code> containing the length of the response body or -1 if
   * the length is not known
   */
  public int getContentLength();
  
  /**
   * Sends an error response to the client using the specified status. 
   * By default the server creates the response to look like an HTML-formatted
   * error page containing the specified message and details, setting the
   * content type to "text/html", leaving cookies and other headers unmodified.
   * Based on <code>ErrorData.isHtmlAllowed()</code> constructs the error page with html allowed or not.
   * 
   * <p>If the response has already been committed, this method throws an
   * IllegalStateException. After using this method, the response should be
   * considered to be committed and should not be written to.</p>
   * 
   * @param errorData the error data. 
   * @see For more info see <code>com.sap.engine.services.httpserver.interfaces.ErrorData</code>
   * @throws IllegalStateException if the response was committed.
   */
  public void sendError(ErrorData errorData);
  
  /**
   * Sends a temporary redirect response to the client using the specified
   * redirect location. If the location is relative without a leading '/' it is
   * interpreted as relative to the current request URI. If the location is
   * relative with a leading '/' it is interpreted as relative to the server
   * root.
   * 
   * <p>If the response has already been committed, this method throws an
   * IllegalStateException. After using this method, the response should be
   * considered to be committed and should not be written to.</p>
   * 
   * @param location
   * the redirect location
   * 
   * @throws IllegalStateException
   * If the response was committed
   */
  public void sendRedirect(String location);
}
