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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.portlet.ActionRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.engine.services.portletcontainer.api.IPortletNode;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationContext;

/**
 * The <code>ActionRequestImpl</code> class is an implementation of the <code>
 * ActionRequest</code> interface that represents the request sent to the portlet
 * to handle an action. ActionRequest extends the PortletRequest interface to
 * provide action request information to portlets.
 * The portlet container creates an ActionRequest object and passes it as argument
 * to the portlet's processAction method.
 *
 * @author Diyan Yordanov
 * @version 7.10
 */
public class ActionRequestImpl extends PortletRequestImpl implements ActionRequest {

  private boolean getInputStream = false;
  private boolean getReader = false;
  
  /**
   * Creates new <code>ActionRequestImp</code> object. 
   * @param servletRequest the servlet request object that contains the request 
   * the client has made.
   * @param servletResponse the servlet response object that contains the response 
   * the portlet sends to the client.
   * @param portletNode the <code>IPortletNode</code> object that specified 
   * the requested portlet.
   * @param portletApplicationContext the <code>PortletApplicationContext</code> 
   * object that is used by the portlet to communicate with the Portlet Container.
   */
  public ActionRequestImpl(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
      IPortletNode portletNode, PortletApplicationContext portletApplicationContext) {
    super(servletRequest, servletResponse, portletNode, portletApplicationContext, IPortletNode.ACTION_METHOD);
  }

  /**
   * Retrieves the body of the HTTP request from client to portal as binary data
   * using an <code>InputStream</code>. Either this method or getReader() may be
   * called to read the body, but not both.
   * @return an input stream containing the body of the request.
   * @throws IllegalStateException if getReader was already called, or it is a 
   * HTTP POST data of type application/x-www-form-urlencoded.
   * @throws IOException if an input or output exception occurred.
   */
  public InputStream getPortletInputStream() throws IOException {
    HttpServletRequest servletRequest = (HttpServletRequest) super.getRequest();

    getInputStream = true;
    if (servletRequest.getMethod().equals("POST")) {
      String contentType=servletRequest.getContentType();
      if (contentType==null||contentType.equals("application/x-www-form-urlencoded")) {
        //PLT.11.1.1 Request Parameters
        //The parameters the request object returns must be "x-www-form-urlencoded" decoded.
        throw new IllegalStateException("User request HTTP POST data is of type application/x-www-form-urlencoded. " +
            "This data has been already processed by the portal/portlet-container and is available as request parameters.");
      }
    }
    return servletRequest.getInputStream();
  }
  
  /**
   * Overrides the name of the character encoding used in the body of this
   * request. This method must be called prior to reading input 
   * using {@link #getReader} or {@link #getPortletInputStream}.
   * <p>
   * This method only sets the character set for the Reader that the
   * {@link #getReader} method returns.
   * @param	enc	a <code>String</code> containing the name of the chararacter encoding.
   * @exception	UnsupportedEncodingException if this is not a valid encoding.
   * @exception	IllegalStateException if this method is called after reading request 
   * parameters or reading input using <code>getReader()</code>
   */

  public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
    if (getReader) {
//      traceWarning("setCharacterEncoding", "Encoding not set. Post data parsed: " +
//        "isPostDataParsed() = [" + isPostDataParsed() + "], getReader = [" + getReader +
//        "], getInputStream = [" + getInputStream + "]", false);
      throw new IllegalStateException("setCharacterEncoding [" + enc + "] method is called " +
          "after reading request parameters or reading input using getReader()");
    } 
    
    super.setCharacterEncoding(enc);
    
  }
  
  /**
   * Retrieves the body of the HTTP request from the client to the portal
   * as character data using a <code>BufferedReader</code>.  The reader translates 
   * the character data according to the character encoding used on the body.
   * Either this method or {@link #getPortletInputStream} may be called to read the
   * body, not both.
   * <p>
   * For HTTP POST data of type application/x-www-form-urlencoded this method 
   * throws an <code>IllegalStateException</code> as this data has been already 
   * processed by the portal/portlet-container and is available as request parameters.
   * @return	a <code>BufferedReader</code>	containing the body of the request.	
   * @exception UnsupportedEncodingException if the character set encoding used 
   * is	not supported and the text cannot be decoded
   * @exception  IllegalStateException if {@link #getPortletInputStream} method
   * has been called on this request,  it is a HTTP POST data of type 
   * application/x-www-form-urlencoded.
   * @exception  IOException if an input or output exception occurred.
   *
   * @see #getPortletInputStream
   */
  
  public BufferedReader getReader() throws UnsupportedEncodingException, IOException {
    getReader = true;
    return super.getReader();
  }
   
  /**
   * Returns the name of the character encoding used in the body of this request.
   * This method returns <code>null</code> if the request does not specify a 
   * character encoding.
   * @return		a <code>String</code> containing the name of the chararacter 
   * encoding, or <code>null</code> if the request does not specify a character 
   * encoding.
   */
  public String getCharacterEncoding() {
    return super.getCharacterEncoding();
  }

  /**
   * Returns the MIME type of the body of the request, 
   * or null if the type is not known.
   * @return		a <code>String</code> containing the name	of the MIME type of 
   * the request, or null if the type is not known.
   */
  public String getContentType() {
    return super.getContentType();
  }

  /**
   * Returns the length, in bytes, of the request body which is made available 
   * by the input stream, or -1 if the length is not known. 
   * @return		an integer containing the length of the	request body or -1 if 
   * the length is not known.
   */
  public int getContentLength() {
    return super.getContentLength();
  }
}