/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.portletcontainer.core;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.sap.engine.services.portletcontainer.LogContext;
import com.sap.engine.services.portletcontainer.api.IPortletNode;
import com.sap.tc.logging.Location;

/**
 *
 * @author Diyan Yordanov
 * @version 7.10
 */
public class RenderResponseImpl extends PortletResponseImpl implements RenderResponse {

  private Location currentLocation = Location.getLocation(getClass());
  private boolean getWriter = false;
  private boolean getPortletOutputStream = false;
  private ServletOutputStream wrappedWriter = null;
  private IPortletNode portletNode = null;
  /**
   * Shows whether the setContentType method has been invoked.
   */
  private boolean contentTypeSet = false;
  
  private String currentContentType = null;
    
  private static String CHARSET = "charset=";
  
  /**
   * @param servletRequest
   * @param servletResponse
   */
  public RenderResponseImpl(IPortletNode portletNode, PortletRequest portletRequest, HttpServletResponse servletResponse) {
    super(portletRequest, servletResponse);
    this.portletNode = portletNode;
  }

  /**
   * Returns the MIME type that can be used to contribute markup to the render 
   * response.
   * <p>
   * If no content type was set previously using the {@link #setContentType} method
   * this method retuns <code>null</code>.
   * @return the MIME type of the response, or <code>null</code> if no content 
   * type is set.
   */
  public String getContentType() {
    return currentContentType;
  }

  /**
   * Creates a portlet URL targeting the portlet. If no portlet mode, window state 
   * or security modifier is set in the PortletURL the current values are preserved. 
   * If a request is triggered by the PortletURL, it results in a render request.
   * <p>
   * The returned URL can be further extended by adding portlet-specific parameters 
   * and portlet modes and window states. 
   * <p>
   * The created URL will per default not contain any parameters of the current 
   * render request.
   * @return a portlet render URL.
   */
  public PortletURL createRenderURL() {
    return new PortletURLImpl(portletNode, getPortletRequest(), PortletURLImpl.RENDER);
  }

  /**
   * Creates a portlet URL targeting the portlet. If no portlet mode, window state
   * or security modifier is set in the PortletURL the current values are preserved. 
   * If a request is triggered by the PortletURL, it results in an action request.
   * <p>
   * The returned URL can be further extended by adding portlet-specific parameters 
   * and portlet modes and window states. 
   * <p>
   * The created URL will per default not contain any parameters of the current 
   * render request.
   * @return a portlet action URL.
   */
  public PortletURL createActionURL() {
    return new PortletURLImpl(portletNode, getPortletRequest(), PortletURLImpl.ACTION);
  }

  /**
   * The value returned by this method should be prefixed or appended to 
   * elements, such as JavaScript variables or function names, to ensure 
   * they are unique in the context of the portal page.
   * @return   the namespace (com.sap.engine.portletcontainer.)
   */
  public String getNamespace() {
    // TODO clarify this later
    //method must return a valid identifier as defined in the 3.8 Identifier
    //Section of the Java Language Specification Second Edition.
    StringBuffer buffer = new StringBuffer(50);
    String portletId = portletNode.getContextName();
    if (portletId.indexOf('-') != -1) {
      portletId.replace('-', '_');
    }
    buffer.append("com_sap_engine_portletcontainer_");
    buffer.append(portletId);
    buffer.append("_");
    return buffer.toString();
  }

  /**
   * This method sets the title of the portlet.
   * <p>
   * The value can be a text String.
   * @param  title    portlet title as text String or resource URI.
   */
  public void setTitle(String title) {
    if (isCommitted()) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000061", "The setTitle [{0}] method has no effect. It must be called before the output of the portlet has been commited.", new Object[]{title}, null, null);
      return;
    }
    portletNode.setPortletTitle(title);
  }

  /**
   * Sets the MIME type for the render response. The portlet must set the content 
   * type before calling {@link #getWriter} or {@link #getPortletOutputStream}.
   * <p>
   * Calling <code>setContentType</code> after <code>getWriter</code>
   * or <code>getOutputStream</code> does not change the content type.
   * @param   type  the content MIME type.
   * @throws  IllegalArgumentException if the given type is not in the list returned
   * by <code>PortletRequest.getResponseContentTypes</code>
   */
  public void setContentType(String type) {
    // throw IllegalArgumentException if content type does not match
    if (isValidContentType(type)) {
      if (getWriter || getPortletOutputStream) {
        // log message that method will be ignored
        LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000062", "The setContentType method has no effect on the Content Type![{0}].", new Object[]{contentTypeSet}, null, null);
      } else {
        //set content type and ignore any charcter encoding
        //TODO use ByteArrayUtils
        int charsetLocation = type.indexOf(CHARSET);
        if (charsetLocation > -1) {
          currentContentType = type.substring(0,charsetLocation).trim();
        } else {
          currentContentType = type.trim();
        }
        //If the portlet has set a content type, the getContentType method must return it.
        //That's why the type is cached in currentContentType
        this.getWrappedHttpServletResponse().setContentType(currentContentType);
        contentTypeSet = true;
      }
    } else {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000063", 
    		  "The specified content type set [{0}] does not match any of the content Types returned by the getResponseContentType().", new Object[]{type}, null, null);
      throw new IllegalArgumentException("The specified content type set [" + type +
          "] does not match any of the content Types returned by the getResponseContentType().");
    }
  }

  /**
   * Returns the name of the charset used for the MIME body sent in this response.
   * @return		a <code>String</code> specifying the	name of the charset, for
   * example, <code>ISO-8859-1</code>.
   */
  public String getCharacterEncoding() {
    return this.getWrappedHttpServletResponse().getCharacterEncoding();
  }

  /**
   * Returns a PrintWriter object that can send character text to the portal.
   * <p>
   * Before calling this method the content type of the render response must be 
   * set using the {@link #setContentType} method.
   * <p>
   * Either this method or {@link #getPortletOutputStream} may be called to 
   * write the body, not both.
   * @return a <code>PrintWriter</code> object that	can return character data to 
   * the portal.
   * @exception IOException if an input or output exception occurred.
   * @exception IllegalStateException if the <code>getPortletOutputStream</code> 
   * method has been called on this response, or if no content type was set using 
   * the <code>setContentType</code> method.
   *
   * @see #setContentType
   * @see #getPortletOutputStream
   */
  public PrintWriter getWriter() throws IOException {
    if (!contentTypeSet) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000065", "getWriter() cannot be called before setContentType.", null, null);
      throw new IllegalStateException("No Content Type has been set.");
    }
    if (getPortletOutputStream) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000066", "getWriter() can't be used after getOutputStream() was invoked", null, null);
      throw new IllegalStateException("getWriter() can't be used after getOutputStream() was invoked");
    }

    PrintWriter printWriter = this.getWrappedHttpServletResponse().getWriter();
    getWriter = true;
    return printWriter;
  }

  /**
   * Returns the locale assigned to the response.
   * @return  Locale of this response.
   */
  public Locale getLocale() {
    return this.getWrappedHttpServletResponse().getLocale();
  }

  /**
   * Sets the preferred buffer size for the body of the response. 
   * Portlet Container currently does not support buffering. Because of that  
   * this method throws an <code>IllegalStateException</code>.
   * @param size 	the preferred buffer size.
   * @throws IllegalStateException the portlet container does not support buffering.
   */
  public void setBufferSize(int size) {
    LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000067", "Portlet container does not support buffering.", null, null);
    throw new IllegalStateException("portlet container does not support buffering");
  }

  /**
   * Returns the actual buffer size used for the response.  As no buffering
   * is used, this method returns 0.
   * @return	0, no buffering is used.
   */
  public int getBufferSize() {
    return 0; // no buffering is used
  }

  /**
   * Forces any content in the buffer to be written to the client.  A call
   * to this method automatically commits the response.
   * @exception IOException  if an error occured when writing the output.
   */
  public void flushBuffer() throws IOException {
    this.getWrappedHttpServletResponse().flushBuffer();
  }

  /**
   * Clears the content of the underlying buffer in the response without
   * clearing properties set. If the response has been committed, 
   * this method throws an <code>IllegalStateException</code>.
   * @exception  IllegalStateException 	if this method is called after
   * response is comitted.
   */
  public void resetBuffer() {
    this.getWrappedHttpServletResponse().resetBuffer();
  }

  /**
   * Returns a boolean indicating if the response has been committed.
   * @return a boolean indicating if the response has been committed.
   */
  public boolean isCommitted() {
    return this.getWrappedHttpServletResponse().isCommitted();
  }

  /**
   * Clears any data that exists in the buffer as well as the properties set.
   * If the response has been committed, this method throws an 
   * <code>IllegalStateException</code>.
   * @exception IllegalStateException  if the response has already been committed.
   */
  public void reset() {
    this.getWrappedHttpServletResponse().reset();
  }

  /**
   * Returns a <code>OutputStream</code> suitable for writing binary data in 
   * the response. The portlet container does not encode the binary data.  
   * <p>
   * Before calling this method the content type of the render response must be 
   * set using the {@link #setContentType} method.
   * <p>
   * Calling <code>flush()</code> on the OutputStream commits the response.
   * <p>
   * Either this method or {@link #getWriter} may be called to write the body, 
   * not both.
   * @return	a <code>OutputStream</code> for writing binary data.	
   * @exception IllegalStateException if the <code>getWriter</code> method
   * has been called on this response, or if no content type was set using the
   * <code>setContentType</code> method.
   * @exception IOException if an input or output exception occurred.
   */
  public OutputStream getPortletOutputStream() throws IOException {
    if (!contentTypeSet) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000068", "getPortletOutputStream() cannot be called before setContentType.", null, null);
      throw new IllegalStateException("No Content Type set.");
    }
    
    if (getWriter) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000069", "getPortletOutputStream() can't be used after getWriter() was invoked", null, null);
      throw new IllegalStateException("getPortletOutputStream() can't be used after getWriter() was invoked");
    }

    //OutputStream outputStream = this.getWrappedHttpServletResponse().getOutputStream();
    wrappedWriter = new PortletOutputStream(this.getWrappedHttpServletResponse().getWriter());
    getPortletOutputStream = true;
    //return outputStream;
    return wrappedWriter;
  }

  public String encodeURL(String path) {
    if (path.indexOf("://") == -1 && !path.startsWith("/")) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000070", "Only absolute URLs or full path URIs are allowed [{0}].", new Object[]{path}, null, null);
      throw new IllegalArgumentException("Only absolute URLs or full path URIs are allowed [" + path + "].");
    }
    //return portletNode.convertPortletURLToString(new PortletURLImpl(portletNode, getPortletRequest(), PortletURLImpl.RENDER));
    // TODO The encodeURL method may include the session ID 
    // and other portal/portlet-container specific information into the URL.
    return super.encodeURL(path);
  }
  
  public void addProperty(String key, String value) {
    super.addProperty(key, value);
    //  These parameters will be used in all subsequent render requests until a new client request targets the portlet.
    portletNode.addProperty(key, value);
  }
  
  public void setProperty(String key, String value) {
    super.setProperty(key, value);
    //  These parameters will be used in all subsequent render requests until a new client request targets the portlet.
    portletNode.setProperty(key, value);
  }

  // TODO private methods
  
  /**
   * @param type
   * @return
   */
  private boolean isValidContentType(String type) {
    // TODO Check if type is a valid content type set for this response.
    //checks and removes character encoding
    int charsetLocation = type.indexOf(CHARSET);
    if (charsetLocation > -1) {
      type = type.substring(0,charsetLocation).trim();
      if (type.endsWith(";")) {
        type = type.substring(0, type.length() - 1);
      }
    } else {
      type = type.trim();
    }
    //content type is not valid
    //if the content type set does not match (including wildcard matching) 
    //any of the content types returned by the getResponseContentType method of
    //the PortleRequest object.
    Enumeration contentTypes = getPortletRequest().getResponseContentTypes();
    while (contentTypes.hasMoreElements()){
      String supportedType = (String)contentTypes.nextElement();
      if (supportedType.equals(type)) {
        return true;
      } else if (supportedType.indexOf("*") >= 0) {
        //If the portlet has defined '*' or '* / *' as supported content types, 
        //these may also be valid return values.
        
        // the supported type contains a wildcard
        int index = supportedType.indexOf("/");
        String supportedPrefix = supportedType.substring(0, index);
        String supportedSuffix = supportedType.substring(index + 1, supportedType.length());
         
        index = type.indexOf("/");
        String typePrefix = type.substring(0, index);
        String typeSuffix = type.substring(index + 1, type.length());
                          
        if (supportedPrefix.equals("*") || supportedPrefix.equals(typePrefix)) {
          // the prefixes match
          if (supportedSuffix.equals("*") || supportedSuffix.equals(typeSuffix)) {
            // the suffixes match
            return true;
          }
        }                
      }
    }
    return false;
  }


}
