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
package com.sap.engine.services.portletcontainer.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import javax.portlet.PortletSessionUtil;
import javax.servlet.http.HttpSession;

import com.sap.engine.session.AppSession;
import com.sap.engine.services.portletcontainer.LogContext;
import com.sap.engine.services.portletcontainer.ServiceContext;
import com.sap.engine.services.portletcontainer.container.PortletContainerExtension;
import com.sap.tc.logging.Location;


/**
* The <code>PortletSessionImpl</code> is an implemetation of the <code>PortletSesion</code>
* interface which provides a way to identify a user across more than one request
* and to store transient information about that user.
*
* A PortletSession is created per user client per portlet application.
*
* A portlet can bind an object attribute into a PortletSession by name.
* The PortletSession interface defines two scopes for storing objects:
*
* APPLICATION_SCOPE
* PORTLET_SCOPE
* All objects stored in the session using the APPLICATION_SCOPE must be available
* to all the portlets, servlets and JSPs that belongs to the same portlet
* application and that handles a request identified as being a part of the same
* session. Objects stored in the session using the PORTLET_SCOPE must be available
* to the portlet during requests for the same portlet window that the objects
* where stored from. Attributes stored in the PORTLET_SCOPE are not protected from
* other web components of the portlet application. They are just conveniently
* namespaced.
* The portlet session is based on the HttpSession. Therefore all HttpSession
* listeners do apply to the portlet session and attributes set in the portlet
* session are visible in the HttpSession and vice versa.
*
* @author Diyan Yordanov
* @author Vera Buchkova
*/
public class PortletSessionImpl implements PortletSession, Serializable {
  /*
  If HttpSession gets invalidated, this PortletSessionImpl gets also invalidated.
  The PortletSessionImpl and its corresponding HTTPSession should have same creation time and access times,
  even if the HTTPSession is created separately and before the PortletSessionImpl, and even if it is
  accessed separately. (A from EG) 
  */

  static final long serialVersionUID = 2573457190450434965L;
  
  private static Location currentLocation = Location.getLocation(PortletSessionImpl.class);
  
  public static final String ARGUMENT_NAME_IS_NULL = "portlet_0200";
  public static final String INVALID_PORTLET_WINDOW_ID = "portlet_0201";
  private static final int DEFAULT_SCOPE = PortletSession.PORTLET_SCOPE;
  //Name Prefix in PORTLET_SCOPE: ATTRIBUTE_PREFIX + <unique_id_for_portlet_window>?<ATTRIBUTE_NAME>
  private static final String ATTRIBUTE_PREFIX = "javax.portlet.p.";

  /** HttpSession associated with this PortletSession: it is 1-1 association, but in terms of java objects it is 1-n. */
  private AppSession httpSession;
  /** Used to retrieve the portlet context */
  private String portletApplicationName = null;
  /** 
   * The unique ID for portlet window: it is needed to encode attribute names. 
   * It must be serializable with the httpsession and the portlet session.
   * */
  private String portletWindowId = null;

  /**
   * Creates new portlet session object
   * @param portletWindowId
   * @param httpSession
   */
  public PortletSessionImpl(HttpSession httpSession, String portletApplicationName, String portletWindowId) {
    if (currentLocation.beDebug()) {
      LogContext.getLocation(LogContext.LOCATION_PORTLET_SESSION).traceDebug("PortletSessionImpl constructor, portletApplicationName=[" 
          + portletApplicationName + "], portletWindowId=[" + portletWindowId + "].");
    }
		this.httpSession = (AppSession) httpSession;
		this.portletApplicationName = portletApplicationName;
    this.portletWindowId = portletWindowId;
  }

  /* (non-Javadoc)
   * @see javax.portlet.PortletSession#getAttribute(java.lang.String)
   */
  public Object getAttribute(String name) {
    return getAttribute(name, DEFAULT_SCOPE);
  }

  /* (non-Javadoc)
   * @see javax.portlet.PortletSession#getAttribute(java.lang.String, int)
   */
  public Object getAttribute(String name, int scope) {
    checkAttributeNameForNull(name);
    if (scope == PortletSession.APPLICATION_SCOPE) {
      return ((HttpSession) httpSession).getAttribute(name);
    } else {
      //the default PORTLET SCOPE
      if (portletWindowId == null && currentLocation.beDebug()) {
        LogContext.getLocation(LogContext.LOCATION_PORTLET_SESSION).traceWarning("ASJ.portlet.000058", "PortletSessionImpl.getAttribute(name=[{0}], scope=[{1}]) and portletWindowId is NULL.", new Object[]{name, scope}, null, null);
      }
      return ((HttpSession) httpSession).getAttribute(encodeAttributeName(name, portletWindowId));
    }
  }

  /* (non-Javadoc)
   * @see javax.portlet.PortletSession#getAttributeNames()
   */
  public Enumeration getAttributeNames() {
    return getAttributeNames(DEFAULT_SCOPE);
  }

  /* (non-Javadoc)
   * @see javax.portlet.PortletSession#getAttributeNames(int)
   */
  public Enumeration getAttributeNames(int scope) {
    List attributesNames = new LinkedList();
    String attributePrefix = ATTRIBUTE_PREFIX + portletWindowId;
    
    for (Enumeration all = ((HttpSession) httpSession).getAttributeNames(); all.hasMoreElements(); ){
      String name = (String) all.nextElement();
      if (scope == PortletSession.APPLICATION_SCOPE) {
        if (! name.startsWith(ATTRIBUTE_PREFIX)) {
          attributesNames.add(name);
        }
      } else { //PORTLET_SCOPE
        if (portletWindowId == null && currentLocation.beDebug()) {
          LogContext.getLocation(LogContext.LOCATION_PORTLET_SESSION).tracePath("PortletSessionImpl.getAttributeNames(scope=[" + scope + "]) and portletWindowId is NULL!!!");
        }        
        if (name.startsWith(attributePrefix)) {
          attributesNames.add(PortletSessionUtil.decodeAttributeName(name));
        }
      }
    }
    return Collections.enumeration(attributesNames);
  }

  /* (non-Javadoc)
   * @see javax.portlet.PortletSession#getCreationTime()
   */
  public long getCreationTime() {    
    return ((HttpSession) httpSession).getCreationTime();
  }

  /* (non-Javadoc)
   * @see javax.portlet.PortletSession#getId()
   */
  public String getId() {
    //TODO: should it be different from the http session's id?
    return ((HttpSession) httpSession).getId();
  }

  /* (non-Javadoc)
   * @see javax.portlet.PortletSession#getLastAccessedTime()
   */
  public long getLastAccessedTime() {
    return ((HttpSession) httpSession).getLastAccessedTime();
  }

  /* (non-Javadoc)
   * @see javax.portlet.PortletSession#getMaxInactiveInterval()
   */
  public int getMaxInactiveInterval() {
    //TODO: what if httpsession is accessed more recently than the portlet session?
    return ((HttpSession) httpSession).getMaxInactiveInterval();
  }

  /* (non-Javadoc)
   * @see javax.portlet.PortletSession#invalidate()
   */
  public void invalidate() {
    //TODO: Invalidation of httpSession should automatically invalidate the portlet session
    ((HttpSession) httpSession).invalidate();
  }

  /* (non-Javadoc)
   * @see javax.portlet.PortletSession#isNew()
   */
  public boolean isNew() {
    return ((HttpSession) httpSession).isNew();
  }

  /* (non-Javadoc)
   * @see javax.portlet.PortletSession#removeAttribute(java.lang.String)
   */
  public void removeAttribute(String name) {
    removeAttribute(name, DEFAULT_SCOPE);
  }

  /* (non-Javadoc)
   * @see javax.portlet.PortletSession#removeAttribute(java.lang.String, int)
   */
  public void removeAttribute(String name, int scope) {
    checkAttributeNameForNull(name);
    if (scope == PortletSession.APPLICATION_SCOPE) {
      ((HttpSession) httpSession).removeAttribute(name);
    } else {
      //DEFAULT PORTLET SCOPE
      if (portletWindowId == null && currentLocation.beDebug()) {
        LogContext.getLocation(LogContext.LOCATION_PORTLET_SESSION).traceWarning("ASJ.portlet.000059", "PortletSessionImpl.removeAttribute(name=[{0}], scope=[{1}]) and portletWindowId is NULL!!!", new Object[]{name, scope}, null, null);
      }
      ((HttpSession) httpSession).removeAttribute(encodeAttributeName(name, portletWindowId));
    }
  }

  /* (non-Javadoc)
   * @see javax.portlet.PortletSession#setAttribute(java.lang.String, java.lang.Object)
   */
  public void setAttribute(String name, Object value) {
    setAttribute(name, value, DEFAULT_SCOPE);
  }

  /* (non-Javadoc)
   * @see javax.portlet.PortletSession#setAttribute(java.lang.String, java.lang.Object, int)
   */
  public void setAttribute(String name, Object value, int scope) {
    checkAttributeNameForNull(name);
    if (scope == PortletSession.APPLICATION_SCOPE) {
      ((HttpSession) httpSession).setAttribute(name, value);
    } else {
      //DEFAULT PORTLET SCOPE
      if (portletWindowId == null && currentLocation.beDebug()) {
        LogContext.getLocation(LogContext.LOCATION_PORTLET_SESSION).traceWarning("ASJ.portlet.000060", "PortletSessionImpl.setAttribute(name=[{0}], value=[{1}], scope=[{2}]) and portletWindowId is NULL!!!", new Object[]{name, value, scope}, null, null);        
      }
      ((HttpSession) httpSession).setAttribute(encodeAttributeName(name, portletWindowId), value);
    }
  }

  /* (non-Javadoc)
   * @see javax.portlet.PortletSession#setMaxInactiveInterval(int)
   */
  public void setMaxInactiveInterval(int interval) {
    ((HttpSession) httpSession).setMaxInactiveInterval(interval);
  }

  /* (non-Javadoc)
   * @see javax.portlet.PortletSession#getPortletContext()
   */
  public PortletContext getPortletContext() {
    PortletContainerExtension ext = ServiceContext.getServiceContext().getPortletContainerExtension();
    PortletContext portletContext = ext.getPortletApplicationContext(portletApplicationName).getPortletContext();
    return portletContext;
  }
  
  //private methods

  /**
   * Encodes the attribute name for PORTLET_SCOPE
   * @param attributeName	unencoded attribute name
   * @return	encoded attribute name
   */
  private String encodeAttributeName(String attributeName, String portletWindowId) {
    //ATTRIBUTE_PREFIX + <unique_id_for_portlet_window>?<ATTRIBUTE_NAME>
    StringBuffer result = new StringBuffer(ATTRIBUTE_PREFIX);
    result.append(portletWindowId);
    result.append("?");
    result.append(attributeName);
    return result.toString();
  }

  /**
   * If parameter is null, throw an IllegalArgumentException
   * @param name
   */
  private void checkAttributeNameForNull(String name) {
    if (name == null) {
      throw new IllegalArgumentException(ARGUMENT_NAME_IS_NULL);
    }
  }
}
