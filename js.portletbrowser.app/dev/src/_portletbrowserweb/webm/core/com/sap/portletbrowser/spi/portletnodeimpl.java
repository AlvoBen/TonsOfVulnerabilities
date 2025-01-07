/*
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.portletbrowser.spi;

import static com.sap.portletbrowser.LogContext.PORTLETBROWSER_LOCATION;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.WindowState;
import javax.servlet.http.Cookie;

import com.sap.engine.services.portletcontainer.spi.CachingService;
import com.sap.engine.services.portletcontainer.spi.PortalContextExt;
import com.sap.engine.services.portletcontainer.spi.PortletNode;
import com.sap.engine.services.portletcontainer.spi.PortletPreferencesService;
import com.sap.engine.services.portletcontainer.spi.PortletURLProviderService;
import com.sap.portletbrowser.PortalContextImpl;
import com.sap.security.api.IUser;

/**
 * @author Nikolai Dokovski
 * @version 1.0
 * @TODO add description ...
 */
public class PortletNodeImpl implements PortletNode {

  private String objectID;

  private String name;

  private String portletAppName;
  
  private String pageID;
  
  private String windowId;

  private String resourceId;

  private String title;
  
  private String cachability;

  private IUser iuser;

  private WindowState windowState;

  private PortletMode portletMode;

  private CachingService cachingService;

  private CoordinationServiceImpl coordinationService;

  private PortletURLProviderService portletURLProviderService;

  private PortletPreferencesService portletPreferencesService;

  private Set<Cookie> cookies = new HashSet<Cookie>();
  
  private Map<String, String[]>  privateRenderParameters;

  private Map<String, String[]> requestParameters;

  

  public PortletNodeImpl(PortletPreferences preferences, String name,
      String portletAppName, String iNodeID, IUser iuser, String pageid, 
      String servletPath, CoordinationManager manager) {
    this.windowState = WindowState.NORMAL;
    this.portletMode = PortletMode.VIEW;
    this.objectID = iNodeID;
    this.name = name;
    this.title = name;
    this.portletAppName = portletAppName;
    this.iuser = iuser;
    this.pageID = pageid;
    if (PORTLETBROWSER_LOCATION.beDebug()) {
      PORTLETBROWSER_LOCATION.debugT(
        "-------[IPortletNodeImpl] constructor");
    }

    this.cachingService = new CachingServiceImpl();
    this.portletURLProviderService = new PortletURLProviderServiceImpl(this,
      pageid, servletPath);
    this.portletPreferencesService = new PortletPreferencesServiceImpl(
      preferences);

    CoordinationServiceImpl coordinationServiceCached = manager.getService(
      name, portletAppName);

    if (coordinationServiceCached != null) {
      this.coordinationService = coordinationServiceCached;
    } else {
      this.coordinationService = new CoordinationServiceImpl(manager, name,
        portletAppName);
      manager.subscribe(this);
    }

  }

  public String getPortalPageID() {
    return pageID;
  }

  public String getPortletApplicationName() {
    return portletAppName;
  }

  public String getPortletName() {
    return name;
  }

  public WindowState getWindowState() {
    if (PORTLETBROWSER_LOCATION.beDebug()) {
      PORTLETBROWSER_LOCATION.debugT(
        "-------[" + name + "][getWindowState] windowState="
        + windowState);
    }
    return windowState;
  }

  public void setWindowState(WindowState windowState) {
    this.windowState = windowState;
    if (PORTLETBROWSER_LOCATION.beDebug()) {
      PORTLETBROWSER_LOCATION.debugT(
        "-------[" + name + "][setWindowState][1] windowState="
        + windowState);
    }
  }

  public PortletMode getPortletMode() {
    if (PORTLETBROWSER_LOCATION.beDebug()) {
      PORTLETBROWSER_LOCATION.debugT(
        "-------[" + name + "][getPortletMode] portletMode="
        + portletMode);
    }
    return portletMode;
  }

  public void setPortletMode(PortletMode portletMode) {
    if (PORTLETBROWSER_LOCATION.beDebug()) {
      PORTLETBROWSER_LOCATION.debugT(
        "-------[" + name + "][setPortletMode] portletMode="
        + portletMode);
    }
    this.portletMode = portletMode;
  }

  public String getContextName() {
    if (PORTLETBROWSER_LOCATION.beDebug()) {
      PORTLETBROWSER_LOCATION.debugT(
        "-------[" + name + "][getContextName] objectID="
        + objectID);
    }
    return objectID;
  }

  public void setProperty(String key, String value) {
    if (PORTLETBROWSER_LOCATION.beDebug()) {
      PORTLETBROWSER_LOCATION.debugT(
        "-------[" + name + "][setProperty] not implemented");
    }
  }

  public void addProperty(String key, String value) {
    if (PORTLETBROWSER_LOCATION.beDebug()) {
      PORTLETBROWSER_LOCATION.debugT(
        "-------[" + name + "][addProperty] not implemented");
    }
  }

  public boolean storePortletPreferences(PortletPreferences pps)
      throws IOException {
    if (PORTLETBROWSER_LOCATION.beDebug()) {
      PORTLETBROWSER_LOCATION.debugT(
        "-------[" + name
        + "][storePortletPreferences] not implemented");
    }
    return false;
  }

  public PortalContextExt getPortalContext() {
    return PortalContextImpl.getInstance();
  }

  public IUser getUser() {
    return iuser;
  }

  public void setPortletTitle(String s) {
    title = s;
  }
  public boolean isPortletModeAllowed(PortletMode arg0) {
    // TODO supports all modes
    return true;
  }

  public boolean isWindowStateAllowed(WindowState arg0) {
    // TODO supports all modes
    return true;
  }

  public void setExpirationCache(Integer expirationValue) {
    if (PORTLETBROWSER_LOCATION.beDebug()) {
      PORTLETBROWSER_LOCATION.debugT(
        "-------This operation is not supported: Expiration Cache is provided by the portal! expirationValue=["
        + expirationValue.toString() + "]");
    }
  }

  public void addCookie(Cookie cookie) {
    this.cookies.add(cookie);
  }

  public CachingService getCachingService() {
    return cachingService;
  }

  public Cookie[] getCookies() {
    return (Cookie[])cookies.toArray();
  }

  public CoordinationServiceImpl getCoordinationService() {
    return coordinationService;
  }

  public PortletPreferencesService getPortletPreferencesService() {
    return portletPreferencesService;
  }

  public PortletURLProviderService getPortletURLProviderService() {
    return portletURLProviderService;
  }

  public void setNextPossiblePortletModes(Collection<PortletMode> portletModes) {
    // TODO Auto-generated method stub

  }

  /**
   * Returns the portlet window ID. The portlet window ID is 
   * unique for this portlet window and is constant for the lifetime
   * of the portlet window.
   * <p>
   * This ID is the same that is used by the portlet container for
   * scoping the portlet-scope session attributes.
   * 
   * @since 2.0
   * @return  the portlet window ID
   */
  public String getWindowID() {
    return windowId;
  }

  /**
   * Returns the resource ID set on the ResourceURL or <code>null</code>
   * if no resource ID was set on the URL.
   * 
   * @since 2.0
   * @return  the resource ID set on the ResourceURL,or <code>null</code>
   *          if no resource ID was set on the URL. 
   */
  public String getResourceID() {
    return resourceId;
  }

  /**
   * Returns the cache level of this resource request.
   * <p>
   * Possible return values are: 
   * <code>ResourceURL.FULL, ResourceURL.PORTLET</code> 
   * or <code>ResourceURL.PAGE</code>.
   * 
   * @since 2.0
   * @return  the cache level of this resource request.
   */
  public String getCacheability() {
    return cachability;
  }

  public void setWindowID(String windowId) {
    this.windowId = windowId;
  }

  public void setResourceID(String resourceId) {
    this.resourceId = resourceId;
  }

  public void setCachability(String cachability) {
    this.cachability = cachability;
  }
  
  public String getTitle() {
    return title;
  }

  public Map<String, String[]> getPrivateRenderParameters() {
    return (this.privateRenderParameters != null) ? privateRenderParameters : Collections.EMPTY_MAP;
  }

  public void setPrivateRenderParameters(Map<String, String[]> parameters) {
    this.privateRenderParameters = parameters;
  }  

  public Map<String, String[]> getRequestParameters() {
    return (this.requestParameters != null) ? requestParameters : Collections.EMPTY_MAP;
  }

  public void setRequestParameters(Map<String, String[]> parameters) {
    this.requestParameters = parameters;
  }
}
