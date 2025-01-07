/*
 * Copyright (c) 2000-2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.interfaces.client;

import com.sap.engine.services.httpserver.interfaces.ErrorData;
import com.sap.engine.services.httpserver.lib.headers.MimeHeaders;

import java.io.IOException;

public interface Response {
  public void setResponseCode(int code);

  public void sendResponse(int responseCode) throws IOException;

  public void sendResponse(byte[] response, int offset, int length) throws IOException;

  public void flush() throws IOException;

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
   * @throws IOException
   */
  public void sendError(ErrorData errorData) throws IOException;

  public void sendApplicationStopped(byte[] body) throws IOException;

  public void setSchemeHttps();

  public MimeHeaders getHeaders();

  public void setPersistentConnection(boolean isPersistent);

  public boolean isPersistentConnection();
}
