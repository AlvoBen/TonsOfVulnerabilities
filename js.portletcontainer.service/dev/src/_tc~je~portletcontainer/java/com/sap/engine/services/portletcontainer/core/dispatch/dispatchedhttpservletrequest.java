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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.portlet.PortletRequest;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.sap.engine.services.portletcontainer.LogContext;
import com.sap.engine.services.portletcontainer.core.PortletRequestImpl;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationContext;

/**
 * DispatchedHttpServletRequest is a modified http servlet request
 * to be used when a portlet dispatches a request to a servlet or a JSP.
 * It overrides some of the http request methods, since the target servlet
 * or JSP of portlet request dispatcher has access to a limited set of
 * methods of the request object.
 * It also implements other methods that have been already overridden by
 * the PortletRequest like getParameters methods to restore their behavior
 * for the included servlet or JSP.
 *
 * @author Vera Buchkova
 * @version 7.10
 */
public class DispatchedHttpServletRequest extends HttpServletRequestWrapper {

  private static final String HTTP_METHOD_GET = "GET";
  private PortletApplicationContext ctx = null;

  /**
   * Extra information sent with the request. For HTTP servlets,
   * parameters are contained in the query string or posted form data.
   */
  private Map mergedParameters = new HashMap();
  private boolean merged = false;

  /**
   * Creates a new DispatchedHttpServletRequest object
   * @param request	the request of the dispatching portlet
   * @param ctx the application context of dispatching portlet
   */
  public DispatchedHttpServletRequest(PortletRequest request, PortletApplicationContext ctx) {
    super((HttpServletRequest) request);
    this.ctx = ctx;
/*
    //PortletRequestImpl portletRequest = (PortletRequestImpl) super.getRequest();
    HttpServletRequest wrapReq = ((PortletRequestImpl)request).getWrappedHttpServletRequest();
    Map parameters = wrapReq.getParameterMap();
    Map renderParameters = request.getParameterMap();
    //merge both with precedence of requestParameters (i.e. parameters)
    for (Iterator it = renderParameters.keySet().iterator(); it.hasNext();) {
      String name = (String) it.next();
      if (!parameters.containsKey(name)) {
        parameters.put(name, renderParameters.get(name));
      } else {
        List all = Arrays.asList((String[]) parameters.get(name));
        all.addAll(Arrays.asList((String[]) renderParameters.get(name)));
        String[] newVals = new String[all.size()];
        newVals = (String[]) all.toArray(newVals);
        parameters.put(name, newVals);
      }
    }
    if (parameters == null) {
      parameters = new HashMap();
    }
*/    
  }

  /*
   * The following methods of the HttpServletRequest must return null: getProtocol,
   * getRemoteAddr, getRemoteHost, getRealPath, and getRequestURL.cxxix
   */

  /**
   * This method must return null. (see spec PLT.16.3.3)
   */
  public String getProtocol() {
    return null;
  }

  /**
   * This method must return null. (see spec PLT.16.3.3)
   */
  public String getRemoteAddr() {
    return null;
  }

  /**
   * This method must return null. (see spec PLT.16.3.3)
   */
  public String getRemoteHost() {
    return null;
  }

  /**
   * This method must return null. (see spec PLT.16.3.3)
   */
  public String getRealPath() {
    return null;
  }

  /**
   * This method must return null. (see spec PLT.16.3.3)
   */
  public String getRealPath(String s) {
     return null;
  }

  /**
   * This method must return null. (see spec PLT.16.3.3)
   */
  public StringBuffer getRequestURL() {
    return null;
  }

  /*
   * The following methods of the HttpServletRequest must do no operations and return
   * null: getCharacterEncoding, setCharacterEncoding, getContentType,
   * getInputStream and getReader.cxxxiii
   * The getContentLength method of the HttpServletRequest must return 0.
   */

  /**
   * This method must do no operations. (see spec PLT.16.3.3)
   * @param enc	character encoding
   */
  public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
    traceNoOperations("setCharacterEncoding", " enc = [" + enc + "]");
	}

  /**
   * This method must always return 0. (see spec PLT.16.3.3)
   */
  public int getContentLength() {
    return 0;
  }

  /**
   * This method must return null. (see spec PLT.16.3.3)
   */
  public String getCharacterEncoding() {
    return null;
  }

  /**
   * This method must return null. (see spec PLT.16.3.3)
   */
  public String getContentType() {
    return null;
  }

  /**
   * This method must return null. (see spec PLT.16.3.3)
   */
  public ServletInputStream getInputStream() throws IOException {
    return null;
  }

  /**
   * This method must return null. (see spec PLT.16.3.3)
   */
  public BufferedReader getReader() throws IOException {
    //Must return null (PLT.16.3.3)
    return null;
  }

  /*
   * The following methods of the HttpServletRequest must be based on the properties
   * provided by the getProperties method of the PortletRequest interface: getHeader,
   * getHeaders, getHeaderNames, getCookies, getDateHeader and 30
   * getIntHeader.cxxxv.
   */

  public String getHeader(String name) {
    return getWrappedPortletRequest().getProperty(name);
  }

  public Enumeration getHeaders(String name) {
    return getWrappedPortletRequest().getProperties(name);
  }

  public Enumeration getHeaderNames() {
    return getWrappedPortletRequest().getPropertyNames();
  }

  public Cookie[] getCookies() {
    //TODO: based on the getProperties method
    return super.getCookies();
  }

  public long getDateHeader(String name) {
    //TODO: based on the getProperties method
    return super.getDateHeader(name);
  }

  public int getIntHeader(String name) {
    //TODO: based on the getProperties method
    return super.getIntHeader(name);
  }

  /*
   * The following methods of the HttpServletRequest must provide the functionality
   * defined by the Servlet Specification 2.3: getRequestDispatcher, getMethod,
   * isUserInRole, getSession, isRequestedSessionIdFromCookie,
   * isRequestedSessionIdFromURL and isRequestedSessionIdFromUrl.
   */

  public String getMethod() {
    //Must always return "GET" (PLT.16.3.3)
    return HTTP_METHOD_GET;
  }

    public String getPathTranslated() {
       return null;
    }

    public String getServletPath() {
        String result = (String) super.getAttribute("javax.servlet.include.servlet_path");
        if (result != null){
            return result;
        }
        result = super.getServletPath();
        return result;
    }

    public String getRequestURI() {
        String result = (String) super.getAttribute("javax.servlet.include.request_uri");
        if (result != null){
            return result;
        }
        result = super.getRequestURI();
        return result;
    }

    public String getQueryString() {
        String result = (String) super.getAttribute("javax.servlet.include.query_string");
        if (result != null){
            return result;
        }
        result = super.getQueryString();
        return result;
    }

    public String getPathInfo() {
        String result = (String) super.getAttribute("javax.servlet.include.path_info");
        if (result != null){
            return result;
        }
        result = super.getPathInfo();
        return result;
    }

    /**
   * Trace a message for the given method
   * @param method	the method that logs the message
   * @param paramMessage	message containing parameter values
   */
  private void traceNoOperations(String method, String paramMessage) {
    //Do no operations (PLT.16.3.3)
    LogContext.getLocation(LogContext.LOCATION_REQUESTS).trace(
        "Ignoring call of HttpServletRequest." + method + " on " +
        "dispatched request in appication [" +
        ctx.getPortletModuleName() + "]: " + paramMessage);
  }

  private PortletRequest getWrappedPortletRequest() {
		return (PortletRequest)super.getRequest();
	}

  /*
   * Parameters specified in the query string used to create the PortletRequestDispatcher
   * must be aggregated with the portlet render parameters and take precedence over other
   * portlet render parameters of the same name passed to the included servlet or JSP
   */

  /* (non-Javadoc)
   * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
   */
  public String getParameter(String name) {
    String[] values = getParameterValues(name);
    String result = null;
    if (values != null && values.length > 0) {
      result = values[0];
    }
    return result;
  }

  /* (non-Javadoc)
   * @see javax.servlet.ServletRequest#getParameterMap()
   */
  public Map getParameterMap() {
    mergeParameters();
    Map result = new HashMap(mergedParameters.size());
    for (Iterator it = mergedParameters.keySet().iterator(); it.hasNext();) {
      String name = (String) it.next();
      String[] values = (String[]) mergedParameters.get(name);
      String[] newValue = new String[values.length];
      System.arraycopy(values, 0, newValue, 0, values.length);
      result.put(name, newValue);
    }
    return result;
  }

  /* (non-Javadoc)
   * @see javax.servlet.ServletRequest#getParameterNames()
   */
  public Enumeration getParameterNames() {
    mergeParameters();
    Enumeration result = Collections.enumeration(mergedParameters.keySet());
    return result;
  }

  /* (non-Javadoc)
   * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
   */
  public String[] getParameterValues(String name) {
    mergeParameters();
    if (!mergedParameters.containsKey(name)) {
      return null;
    }    
    String[] values = (String[]) mergedParameters.get(name);
    String[] result = new String[values.length];
    System.arraycopy(values, 0, result, 0, values.length);
    return result;
  }
  
  /**
   * Merge the request parameters of the dispatched http servlet request
   * and the render parameters of the portlet request with precedence of 
   * the http servlet request parameters
   * @param request
   */
  private void mergeParameters() {
    PortletRequest request = (PortletRequest) super.getRequest();
    synchronized (this) {
      if (merged) {
        mergedParameters.clear();
        merged = false;
      }  
	    HttpServletRequest wrapReq = ((PortletRequestImpl)request).getWrappedHttpServletRequest();
	    Map requestParameters = wrapReq.getParameterMap();
	    Map renderParameters = request.getParameterMap();
	    Set names = new HashSet();
	    for (Iterator it = requestParameters.keySet().iterator(); it.hasNext();) {
	      String name = (String)it.next();
	      names.add(name);
	    }
	    for (Iterator it = renderParameters.keySet().iterator(); it.hasNext();) {
	      String name = (String)it.next();
	      names.add(name);
	    }
	    
	    //merge both with precedence of requestParameters (i.e. parameters)
	    for (Iterator it = names.iterator(); it.hasNext();) {
	      String name = (String) it.next();
	      Vector mergedValues = new Vector();
	      String[] values = (String[]) requestParameters.get(name);
	      if (values != null) {
	        for (int i = 0; i < values.length; i++) {
	          if (!name.startsWith(PortletRequestImpl.EXTRA_PARAMETER_PREFIX) && !mergedValues.contains(values[i])) {
	            mergedValues.add(values[i]);
	          }
	        }
	      }
	      values = (String[]) renderParameters.get(name);
	      if (values != null) {
	        for (int i = 0; i < values.length; i++) {
	          if (!mergedValues.contains(values[i])) {
	            mergedValues.add(values[i]);
	          }
	        }
	      }
	      if (!mergedValues.isEmpty()) {
	        mergedParameters.put(name, mergedValues.toArray(new String[0]));
	      }	      
	    }
	    merged = true;
    }
  }
}
