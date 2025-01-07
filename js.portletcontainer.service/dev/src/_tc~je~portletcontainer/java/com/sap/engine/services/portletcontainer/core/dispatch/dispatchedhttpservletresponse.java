/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.portletcontainer.core.dispatch;

import java.io.IOException;
import java.util.Locale;

import javax.portlet.PortletResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.sap.engine.services.portletcontainer.LogContext;
import com.sap.engine.services.portletcontainer.core.RenderResponseImpl;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationContext;

/**
 * DispatchedHttpServletResponse
 * 
 * The target servlet or JSP of portlet request dispatcher 
 * has access to a limited set of methods of the response object.
 * 
 * @author vera-b
 * @version 7.10
 */
public class DispatchedHttpServletResponse extends HttpServletResponseWrapper {
  
  private PortletApplicationContext ctx = null;
  private PortletResponse response = null;

  /**
   * @param response
   */
  public DispatchedHttpServletResponse(PortletResponse response, PortletApplicationContext ctx) {
    super((HttpServletResponse)response);
    this.ctx = ctx;
    this.response = response;
  }
  
  /*
   * The following methods of the HttpServletResponse must return null:
   * encodeRedirectURL and encodeRedirectUrl.cxxxviii
   */

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServletResponse#encodeRedirectUrl(java.lang.String)
   */
  public String encodeRedirectUrl(String arg0) {
    //Must return null (PLT.16.3.3)
    return null;
  }
  
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
   */
  public String encodeRedirectURL(String arg0) {
    //Must return null (PLT.16.3.3)
    return null;
  }  
  
  /*
   * The following methods of the
	 * HttpServletResponse must be equivalent to the methods of the RenderResponse of
	 * similar name: getCharacterEncoding, setBufferSize, flushBuffer,
	 * resetBuffer, reset, getBufferSize, isCommitted, getOutputStream,
	 * getWriter, encodeURL and encodeUrl.cxxxix
   */

  
  /*
   * The following methods of the HttpServletResponse must perform no operations:
   * setContentType, setContentLength, setLocale, addCookie, sendError,
   * sendRedirect, setDateHeader, addDateHeader, setHeader, addHeader, 5
   * setIntHeader, addIntHeader and setStatus.cxl 
   * The containsHeader method of the HttpServletResponse must return false.
   */

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie)
   */
  public void addCookie(Cookie cookie) {
    traceNoOperations("addCookie", " cookie = [" + cookie + "]");
  }  
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
   */
  public void addDateHeader(String name, long date) {
    traceNoOperations("addDateHeader", " name = [" + name + "], date = [" + date + "]");
  }
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
   */
  public void addHeader(String name, String value) {
    traceNoOperations("addHeader", " name = [" + name + "], value = [" + value + "]");
  }
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String, int)
   */
  public void addIntHeader(String name, int value) {
    traceNoOperations("addIntHeader", " name = [" + name + "], value = [" + value + "]");
  }
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
   */
  public boolean containsHeader(String arg0) {
    //Must return false (PLT.16.3.3)
    return false;
  }
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServletResponse#sendError(int, java.lang.String)
   */
  public void sendError(int sc, String msg) throws IOException {
    traceNoOperations("sendError", " status code = [" + sc + "], msg = [" + msg + "]");
  }
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServletResponse#sendError(int)
   */
  public void sendError(int sc) throws IOException {
    traceNoOperations("sendError", " status code = [" + sc + "]");
  }
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
   */
  public void sendRedirect(String location) throws IOException {
    traceNoOperations("sendRedirect", " location = [" + location + "]");
  }
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
   */
  public void setDateHeader(String name, long date) {
    traceNoOperations("setDateHeader", " name = [" + name + "], date = [" + date + "]");
  }
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
   */
  public void setHeader(String name, String value) {
    traceNoOperations("setHeader", " name = [" + name + "], value = [" + value + "]");
  }
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
   */
  public void setIntHeader(String name, int value) {
    traceNoOperations("setIntHeader", " name = [" + name + "], value = [" + value + "]");
  }
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServletResponse#setStatus(int, java.lang.String)
   */
  public void setStatus(int sc) {
    traceNoOperations("setStatus", " status code = [" + sc + "]");
  }
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServletResponse#setStatus(int, java.lang.String)
   */
  public void setStatus(int sc, String sm) {
    traceNoOperations("setStatus", " status code = [" + sc + "], sm = [" + sm + "]");
  }
  /* (non-Javadoc)
   * @see javax.servlet.ServletResponse#setContentLength(int)
   */
  public void setContentLength(int len) {
    traceNoOperations("setContentLength", " len = [" + len + "]");
  }
  /* (non-Javadoc)
   * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
   */
  public void setContentType(String type) {
    traceNoOperations("setContentType", " type = [" + type + "]");
  }
  /* (non-Javadoc)
   * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
   */
  public void setLocale(Locale loc) {
    traceNoOperations("setLocale", " loc = [" + loc + "]");
  }
  
  /*
   * The getLocale method of the HttpServletResponse must be based on the getLocale
   * method of the RenderResponse.cxli
   */

  /**
   * Trace a message for the given method
   * @param method	the method that logs the message
   * @param paramMessage	message containing parameter values
   */
  private void traceNoOperations(String method, String paramMessage) {
    //Do no operations (PLT.16.3.3)
    LogContext.getLocation(LogContext.LOCATION_REQUESTS).trace(
        "Ignoring call of HttpServletResponse." + method + " on " +
        "dispatched response in appication [" + 
        ctx.getPortletModuleName() + "]: " + paramMessage);
  }
  
  /**
   * Gets the portlet response output stream.
   * @return	the output stream from the wrapped portlet response
   * @throws	java.io.IOException - if an input or output exception occurred
   * @throws	java.lang.IllegalStateException - if the getWriter method has been called 
   * on this response, or if no content type was set using the setContentType method
   */
  public ServletOutputStream getOutputStream() throws IOException {
    ServletOutputStream result = null;
    result = (ServletOutputStream)(((RenderResponseImpl) response).getPortletOutputStream());
    return result;
  }  
}
