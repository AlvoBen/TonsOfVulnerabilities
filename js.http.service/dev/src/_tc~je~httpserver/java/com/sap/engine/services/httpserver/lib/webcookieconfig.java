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
package com.sap.engine.services.httpserver.lib;

public class WebCookieConfig {
  public static final int COOKIE_TYPE_OTHER = 1;
  public static final int COOKIE_TYPE_SESSION = 2;
  public static final int COOKIE_TYPE_APPLICATION = 3;
  
  public static final byte NONE = 1;
  public static final byte SERVER = 2;
  public static final byte APPLICATION = 4;
  public static final byte OTHER = 8;

  private static final String slash = "/";

  private String aliasName = null;

  public int getCookieType() {
    return cookieType;
  }

  private int cookieType = COOKIE_TYPE_SESSION;
  private byte pathType = SERVER;
  private String path = null;
  private byte domainType = NONE;
  private String domain = null;
  private int maxAge = -1;

  public WebCookieConfig(String aliasName, int cookieType) {
    this.aliasName = aliasName;
	this.cookieType = cookieType;
    init();
  }

  private void init() {
    pathType = SERVER;
    path = slash;
    if (cookieType == COOKIE_TYPE_SESSION) {
      domainType = NONE;// just hack SERVER;
    } else if (cookieType == COOKIE_TYPE_APPLICATION) {
      domainType = NONE;
    } else {
      domainType = SERVER;
    }
    domain = null;
    maxAge = -1;
  }

  public void setPath(byte type, String path) {
    this.pathType = type;
    this.path = path;
    if (type == SERVER) {
      this.path = slash;
    } else if (type == APPLICATION) {
      if (aliasName == null || aliasName.length() == 0) {
        this.path = slash;
      } else {
        this.path = slash + aliasName + slash;
      }
    }
  }

  public void setDomain(byte type, String domain) {
    this.domainType = type;
    this.domain = domain;
  }

  public void setMaxAgen(int maxAge) {
    this.maxAge = maxAge;
  }

  public byte getPathType() {
    return pathType;
  }

  public String getPath() {
    return path;
  }

  public byte getDomainType() {
    return domainType;
  }

  public String getDomain() {
    return domain;
  }

  public int getMaxAge() {
    return maxAge;
  }
}
