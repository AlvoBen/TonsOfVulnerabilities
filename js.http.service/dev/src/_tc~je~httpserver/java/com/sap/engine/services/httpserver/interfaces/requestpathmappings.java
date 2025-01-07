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
package com.sap.engine.services.httpserver.interfaces;

import com.sap.engine.services.httpserver.lib.util.MessageBytes;

public class RequestPathMappings {
  private MessageBytes aliasName = null;
  private String aliasValue = null;
  private String servletPath = null;
  private String pathInfo = null;
  private MessageBytes servletName = null;
  private byte[] realPath = null;
  private String[] filterChain = null;
  private String zoneName = null;
	private boolean zoneExactAlias = false;

  public void init() {
    aliasName = null;
    zoneName = null;
		zoneExactAlias = false;
    aliasValue = null;
    servletPath = null;
    pathInfo = null;
    realPath = null;
    filterChain = null;
    servletName = null;
  }

  public void setAlias(MessageBytes aliasName, String aliasValue, boolean zoneExactAlias) {
    this.aliasName = aliasName;
    this.aliasValue = aliasValue;
    this.zoneExactAlias = zoneExactAlias;
    if (zoneExactAlias) {
      //todo - encoding!!!
      zoneName = aliasName.toString();
    }
  }

  public void setZoneName(String zoneName, boolean zoneExactAlias) {
    this.zoneName = zoneName;
		this.zoneExactAlias = zoneExactAlias;
  }

  public void setServletPath(String servletPath) {
    this.servletPath = servletPath;
  }

  public void setPathInfo(String pathInfo) {
    this.pathInfo = pathInfo;
  }

  public void setRealPath(byte[] realPath) {
    this.realPath = realPath;
  }

  public void setServletName(MessageBytes servletName) {
    this.servletName = servletName;
  }

  public void setFilterChain(String[] filterChain) {
    this.filterChain = filterChain;
  }

  public MessageBytes getAliasName() {
    return aliasName;
  }

  public String getAliasValue() {
    return aliasValue;
  }

  public String getZoneName() {
    return zoneName;
  }
  
  public boolean isZoneExactAlias() {
    return zoneExactAlias;
  }

  public String getServletPath() {
    return servletPath;
  }

  public String getPathInfo() {
    return pathInfo;
  }

  public MessageBytes getServletName() {
    return servletName;
  }

  public byte[] getRealPath() {
    return realPath;
  }

  public String[] getFilterChain() {
    return filterChain;
  }
}
